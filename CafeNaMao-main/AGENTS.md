# AGENTS.md — Café na Mão

Aplicação acadêmica de pedidos de bebidas. Front-end estático (HTML/CSS/JS puro), sem build, sem dependências e sem servidor. O backend Java/Spring usa H2 em arquivo com migrações Flyway, expõe uma API REST e se integra ao front-end, que mantém fallback por `localStorage` para continuar funcionando por `file://`. O Docker Compose, a coleção Postman/Newman e o workflow de CI existem. Docker Desktop/WSL não estão instalados nesta máquina — não declare Docker ou CI como executados sem evidência real.

## Antes de trabalhar

- Leia `progresso.md` (fonte de verdade do estágio atual, decisões e próxima etapa) e `projeto 1/.agents/skills/cafenamao/SKILL.md`.
- Trabalhe em etapas pequenas; implemente **somente** a próxima etapa descrita em `progresso.md` e não avance sem autorização.
- Rode `git status` antes de editar e preserve alterações existentes.
- Não faça commit ou push sem solicitação explícita.
- Responda ao usuário em português brasileiro, explicando o código em termos simples.

## Comandos

```powershell
npm test                              # todos os testes (node --test nativo)
node --test tests/dados.test.js       # um arquivo de teste
node --test --test-name-pattern="preço"   # testes por nome
node --check js/menu.js               # checagem de sintaxe (faça para cada JS alterado)
git diff --check                      # whitespace

cd backend; .\mvnw.cmd verify          # testes JUnit + JaCoCo
.\scripts\test-api.ps1 -Port 18080     # sobe o JAR e executa Newman
docker compose up --build              # API em container (requer Docker)
docker compose --profile testes run --rm testes-api   # Newman no Compose
```

- Não há lint/typecheck/formatter configurados. Use `node --check` + `npm test`.
- Abra as páginas direto no navegador (`start index.html`, `start menu.html`). **Não** introduza servidor local: os scripts são clássicos e precisam funcionar por `file://`.
- Não adicione dependências npm, bundlers ou frameworks sem pedir. Não existe `package-lock.json`.

## Arquitetura e convenções

- `js/dados.js` — catálogo (`bebidas`) + `formatarPreco`; expõe `window.CafeNaMaoDados` e também `module.exports` para os testes Node.
- `js/detalhes.js` — validação de quantidade e cálculo do total parcial.
- `js/api.js` — cliente HTTP da API (`fetch` com timeout), URL configurável, validação das respostas e conversão do pedido para o formato do histórico; expõe `window.CafeNaMaoApi` e `module.exports`.
- `js/carrinho.js` — regras do carrinho (adicionar, alterar, remover, total, pedido) e persistência em `localStorage`; expõe `window.CafeNaMaoCarrinho` e `module.exports`.
- `js/historico.js` — histórico de pedidos, pagamento simulado, status e persistência em `localStorage`; expõe `window.CafeNaMaoHistorico` e `module.exports`.
- `js/menu.js` — renderização e estados do catálogo (`menu.html`); tenta `GET /api/bebidas` e cai para `js/dados.js` quando a API não responde; usa apenas scripts clássicos (IIFE, `"use strict"`), **não** módulos ES.
- `js/pedidos.js` — carrinho visual, pagamento simulado, confirmação, histórico e alteração de status em `pedidos.html`; usa `POST /api/pedidos` e `PATCH /api/pedidos/{numero}/status` quando a API está disponível.
- `tests/dados.test.js`, `tests/detalhes.test.js`, `tests/carrinho.test.js`, `tests/historico.test.js` e `tests/api.test.js` — testes com `node:test` + `node:assert/strict`.
- Páginas: `index.html`, `menu.html`, `pedidos.html` e `sobre.html` prontas para as etapas concluídas.
- `backend/` — Spring Boot 4.1.2, API REST em `/api`, persistência JPA em H2, migrações Flyway, testes JUnit/MockMvc, CORS e JaCoCo.
- `backend/src/main/resources/db/migration/` — migrações Flyway; a `V1` cria `bebida`, `pedido` e `pedido_item`.
- `backend/src/main/java/br/com/cafenamao/persistencia/` — entidades JPA, mapeadores, adaptadores dos repositórios e semeador do catálogo.
- `backend/data/` — banco H2 de desenvolvimento, gerado em tempo de execução; não versionar.
- `postman/` — coleção e ambiente da API; `scripts/test-api.ps1` executa o Newman.
- `docker-compose.yml` e `backend/Dockerfile` — API e testes em containers.
- `../.github/workflows/ci.yml` — CI na raiz do repositório Git (o projeto está na subpasta `CafeNaMao-main`).
- Preços são inteiros em **centavos**; arredondamentos/`float` são proibidos. Formate com `formatarPreco` (`Intl.NumberFormat("pt-BR")`).
- Insira dados de domínio no DOM com `textContent`/`createElement`, nunca interpolando em `innerHTML`.
- Nomes de domínio em português. HTML semântico, sem estilos inline, sem `href="#"`.
- Identidade visual neobrutalista: carvão `#252525`, creme `#F5F0E8`, laranja `#FF6A00`, cinza `#D8D3CA`; bordas grossas e sombras duras. Sem imagens/fontes externas nem arquivo de logo.

## Gotchas

- Estados do catálogo para teste manual: `menu.html?estado=vazio` e `menu.html?estado=erro`; valem só na primeira carga ("Tentar novamente" recarrega normal). Há atraso simulado de 450 ms.
- Acessibilidade é requisito: foco visível, navegação por teclado, `aria-live` para estados, contraste e `prefers-reduced-motion`.
- Persistência local: carrinho em `cafe-na-mao:carrinho:v1`; histórico em `cafe-na-mao:historico:v1` no mesmo esquema.
- Não há testes de DOM automatizados; verificação de UI é manual no navegador. Os fluxos de integração foram conferidos com Edge headless via CDP em scripts temporários não versionados.
- A API persiste em H2 em arquivo (`backend/data/cafe-na-mao`) no perfil padrão e em memória nos testes (`application-test.properties`). `ddl-auto=validate` exige que as entidades batam com o schema Flyway.
- O front-end usa `http://localhost:8080/api` por padrão. A URL pode ser trocada por `?api=http://host:porta/api` ou por `window.CAFE_NA_MAO_API_URL` definida antes de `js/api.js`.
- `CorsConfig` libera `/api/**` com `allowedOrigins("*")` porque `file://` envia `Origin: null`; não há cookies nem credenciais.
- Falha de rede tem `semRede = true` e ativa o modo local; recusa HTTP tem `status` e **não** cria pedido local — mantém o carrinho e mostra a mensagem da API. Preserve essa distinção ao mexer em `js/api.js`/`js/pedidos.js`.
- Pedidos criados na API são espelhados no histórico local; o histórico completo do servidor não é sincronizado automaticamente para o navegador.
- O Newman é executado com `npx --yes newman@6.2.1` para não adicionar dependência ao `package.json`.
- Docker Compose e GitHub Actions existem, mas não foram executados neste ambiente; registre bloqueios honestamente.
- Ao final de cada sessão, atualize `progresso.md` com resultados, pendências e a próxima etapa.
