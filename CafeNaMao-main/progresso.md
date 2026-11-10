# PROGRESSO — Café na Mão

Última atualização: 09/11/2026

Status atual: **Etapas 1 a 10 concluídas. A Etapa 10 entregou persistência em H2 com migrações Flyway e a integração do front-end com a API, preservando o funcionamento por `file://` com fallback local. Docker e GitHub Actions continuam sem execução real neste ambiente. Próxima etapa: a definir e autorizar.**

Este arquivo registra o estado real do projeto, as decisões tomadas, os testes executados e o próximo passo. Uma nova sessão deve conseguir continuar a partir dele, lendo também `projeto 1/.agents/skills/cafenamao/SKILL.md`.

## Histórico do registro

O registro anterior (commit `c359b67`) foi preservado e ampliado. Ele documentava:

- primeira etapa de desenvolvimento realizada com auxílio do OpenCode;
- front-end inicial com `index.html`, `menu.html`, `pedidos.html`, `sobre.html` e `style.css`;
- projeto versionado no GitHub;
- ferramentas usadas: VS Code, OpenCode, DeepSeek v4.1 Flash, HTML, CSS, JavaScript, Git e GitHub;
- orientação para trabalhar em etapas, explicar em português, testar e não avançar sem autorização.

Essas orientações continuam válidas. O estado detalhado das Etapas 1 a 10 está nas seções seguintes.

## 1. Como retomar o projeto

1. Leia `progresso.md` e a skill `cafe-na-mao`.
2. Inspecione `git status` antes de alterar qualquer arquivo.
3. Execute os testes existentes:

```powershell
npm test
cd backend
.\mvnw.cmd test
```

4. Abra as páginas diretamente no navegador:

```powershell
start index.html
start menu.html
```

5. Implemente somente a próxima etapa descrita na seção 15.
6. Ao final, atualize este arquivo com resultados, pendências e a nova próxima etapa.

Não é necessário servidor local nesta fase. Os scripts usam arquivos JavaScript clássicos, portanto funcionam também por `file://`.

## 2. Visão do projeto

O Café na Mão é uma aplicação acadêmica de pedidos de bebidas para uma máquina de café. O cliente deve consultar bebidas, escolher quantidade, montar um pedido e simular o pagamento. O projeto também precisa demonstrar práticas de Garantia de Qualidade de Software.

Stack atual:

- Frontend estático com HTML semântico, CSS e JavaScript puro.
- Testes unitários com o executor nativo do Node.js.
- Sem framework, sem build e sem dependências externas nesta fase.
- Backend Java/Spring Boot com API REST, persistência JPA em H2 e migrações Flyway, testes JUnit/MockMvc e JaCoCo.
- Dockerfile, Docker Compose, coleção Postman, execução com Newman e workflow de CI (GitHub Actions).
- Front-end integrado à API, com fallback para o catálogo e o histórico locais.
- PostgreSQL e sincronização automática de pedidos offline ainda não implementados.

Identidade visual:

- Neobrutalismo com bordas grossas, sombras duras e blocos de alto contraste.
- Carvão `#252525`, creme `#F5F0E8`, laranja `#FF6A00` e cinza `#D8D3CA`.
- Marca textual “Café na Mão”. Não existe arquivo de logo no repositório.
- Sem imagens ou fontes externas; a ilustração da xícara é feita com CSS.

## 3. Estrutura atual

```text
CaféNaMão/
├── index.html                 Página inicial
├── menu.html                  Catálogo de bebidas e painel de detalhes
├── pedidos.html               Carrinho, pagamento, confirmação e histórico
├── sobre.html                 Página sobre o projeto
├── style.css                  Estilos globais e responsivos
├── js/
│   ├── dados.js               Dados das bebidas e formatação de preço
│   ├── detalhes.js            Validação de quantidade e cálculo do total
│   ├── api.js                 Cliente HTTP, URL configurável e conversão de pedido
│   ├── carrinho.js            Regras do carrinho, total e persistência
│   ├── historico.js           Histórico, pagamento simulado e status
│   ├── menu.js                Renderização, estados e painel de detalhes
│   └── pedidos.js             Carrinho na tela, pagamento e histórico
├── docs/
│   ├── requisitos.md          Requisitos funcionais e não funcionais
│   ├── plano-de-testes.md     Plano e casos CT01 a CT54
│   └── relatorio-testes.md    Resultados, limitações e próximos passos
├── backend/
│   ├── pom.xml                Projeto Maven/Spring Boot 4.1.2
│   ├── mvnw / mvnw.cmd        Maven Wrapper
│   ├── Dockerfile             Build em duas etapas, runtime e healthcheck
│   ├── .dockerignore          Exclui target/ e arquivos locais do contexto
│   ├── data/                  Banco H2 de desenvolvimento (gerado)
│   └── src/
│       ├── main/java/br/com/cafenamao/
│       │   ├── CafeNaMaoApplication.java
│       │   ├── dominio/       Bebida, ItemPedido, Pedido, Preco, status e pagamento
│       │   ├── catalogo/      Catálogo e semeador inicial
│       │   ├── persistencia/  Entidades JPA, mapeadores e adaptadores
│       │   └── api/           DTOs, serviços, controllers, CORS e erros
│       ├── main/resources/db/migration/V1__estrutura_inicial.sql
│       └── test/java/br/com/cafenamao/  Testes JUnit 5, MockMvc e H2
├── tests/
│   ├── dados.test.js          Testes unitários dos dados
│   ├── detalhes.test.js       Testes de quantidade e total
│   ├── carrinho.test.js       Testes do carrinho e criação de pedido
│   ├── historico.test.js      Testes do histórico, pagamento e status
│   └── api.test.js            Testes do cliente HTTP e da conversão de pedido
├── postman/
│   ├── CafeNaMao.postman_collection.json
│   └── CafeNaMao.postman_environment.json
├── scripts/
│   └── test-api.ps1           Sobe o JAR, aguarda a API e executa o Newman
├── docker-compose.yml         API e serviços de teste em containers
├── .gitignore                 Artefatos de build e relatórios locais
├── package.json               Script de testes; sem dependências
├── README.md                  Descrição, execução, API e testes
├── progresso.md               Este registro
└── projeto 1/                 Skills e configurações de agentes
```

O workflow de CI fica na raiz do repositório Git, em
`../.github/workflows/ci.yml`, porque o projeto está dentro da pasta
`CafeNaMao-main`. O workflow detecta automaticamente se o projeto está na raiz
ou nessa subpasta.

## 4. Etapa 1 — Fundação visual e página inicial

Status: concluída.

Implementado:

- `index.html` com `lang="pt-BR"`, viewport mobile, título e descrição.
- Estrutura semântica com `header`, `nav`, `main`, `section` e `footer`.
- Link “Pular para o conteúdo” e navegação com `aria-current="page"`.
- Página inicial com hero, chamada para o menu, seção “Como funciona” e rodapé.
- Ilustração de xícara feita somente com CSS.
- Cabeçalho fixo no desktop e estático em telas menores.
- Foco de teclado visível, áreas de toque com pelo menos 44 px e suporte a `prefers-reduced-motion`.

Arquivos: `index.html`, `style.css`.

## 5. Etapa 2 — Catálogo de bebidas

Status: concluída.

Implementado:

- `menu.html` com cabeçalho, navegação ativa, catálogo e rodapé.
- Seis bebidas simuladas em `js/dados.js`.
- Preços armazenados em centavos e formatados como moeda brasileira.
- Cards com número, categoria, nome, descrição e preço.
- Estado de carregamento com três esqueletos e `aria-busy="true"`.
- Estado de lista vazia com mensagem e link para a página inicial.
- Estado de erro com botão “Tentar novamente”.
- Região `aria-live` para anunciar mudanças de estado.
- Layout responsivo: 1 coluna no celular, 2 no tablet e 3 no desktop.
- Textos vindos dos dados são inseridos com `textContent`, evitando interpretação de HTML.

Bebidas atuais:

| Bebida | Categoria | Preço |
|---|---|---:|
| Espresso Curto | Café | R$ 4,00 |
| Café com Leite | Café | R$ 5,50 |
| Cappuccino | Café | R$ 7,50 |
| Mocha | Café | R$ 8,50 |
| Chocolate Quente | Chocolate | R$ 7,00 |
| Chá de Canela | Chá | R$ 5,00 |

Arquivos: `menu.html`, `js/dados.js`, `js/menu.js`, `style.css`, `tests/dados.test.js`, `package.json`.

### Como testar os estados do catálogo

- Catálogo normal: abrir `menu.html`.
- Lista vazia: abrir `menu.html?estado=vazio`.
- Erro: abrir `menu.html?estado=erro` e clicar em “Tentar novamente”.

Os parâmetros de estado são apenas um recurso de demonstração/teste. Eles valem para a primeira carga; o botão de nova tentativa carrega os dados normais.

## 6. Etapa 3 — Detalhes e quantidade

Status: concluída.

Implementado:

- Botão “Ver detalhes” em cada card, com `aria-haspopup="dialog"` e `aria-controls`.
- Painel modal acessível usando o elemento nativo `<dialog>` em `menu.html`.
- Detalhes com categoria, nome, descrição, preço unitário e seletor de quantidade.
- Quantidade limitada de 1 a 10, com mensagem de erro para valores abaixo do mínimo, acima do máximo, fracionários ou não numéricos.
- Total parcial calculado com inteiros em centavos e exibido com `formatarPreco`.
- Fechamento pelo botão “Fechar” e pela tecla `Esc`, devolvendo o foco ao botão que abriu o painel.
- Painel responsivo e sem rolagem horizontal em 390 px, 768 px e 1366 px.
- Testes unitários em `tests/detalhes.test.js`.

Arquivos: `menu.html`, `js/detalhes.js`, `js/menu.js`, `style.css`, `tests/detalhes.test.js`.

## 7. Etapa 4 — Carrinho e criação de pedido

Status: concluída.

Implementado:

- Botão “Adicionar ao pedido” no painel de detalhes, com a quantidade validada de 1 a 10.
- `js/carrinho.js` como módulo clássico compartilhado pelo navegador e pelos testes Node.
- Carrinho com identificador, nome, preço unitário congelado no momento da adição, quantidade e total geral em centavos.
- Adições repetidas da mesma bebida somam a quantidade em uma única linha do carrinho.
- Alteração de quantidade e remoção de itens em `pedidos.html`, com atualização imediata do subtotal e do total geral.
- Persistência do carrinho em `localStorage` (chave `cafe-na-mao:carrinho:v1`), com fallback silencioso quando o armazenamento não estiver disponível.
- Barra “Seu pedido” no menu com quantidade de itens, total e link para `pedidos.html`.
- Estado de carrinho vazio com link “Escolher bebidas”.
- Bloqueio da conclusão quando o carrinho está vazio, tanto na interface quanto na função `criarPedido`.
- Confirmação simulada com número do pedido, data, itens, total e foco movido para a confirmação.
- Mensagens anunciadas por `aria-live`, rótulos associados aos campos e navegação por teclado preservada.
- Testes unitários em `tests/carrinho.test.js`.

Arquivos: `menu.html`, `pedidos.html`, `style.css`, `js/carrinho.js`, `js/menu.js`, `js/pedidos.js`, `tests/carrinho.test.js`.

## 8. Etapa 5 — Pagamento simulado e histórico de pedidos

Status: concluída.

Implementado:

- Escolha obrigatória entre “Dinheiro” e “Cartão fictício”, sem solicitar ou armazenar dados reais de cartão.
- Bloqueio da conclusão quando nenhuma forma de pagamento foi escolhida ou quando há quantidade inválida, com mensagem e foco no campo problemático.
- `js/historico.js` como módulo clássico compartilhado pelo navegador e pelos testes Node.
- Histórico persistido em `localStorage` na chave `cafe-na-mao:historico:v1`, com fallback silencioso quando o armazenamento não estiver disponível.
- Registro com número, data, itens, total, forma de pagamento e status.
- Histórico exibido em `pedidos.html`, com o pedido mais recente primeiro e estado vazio.
- Alteração de status entre “Recebido”, “Em preparo” e “Finalizado”, com persistência imediata.
- Limpeza do histórico em diálogo de confirmação, sem remover o carrinho atual.
- O carrinho só é limpo depois que o pedido foi salvo no histórico; se a gravação falhar, o carrinho é mantido.
- Pagamento e status aparecem na confirmação do pedido e nos cards do histórico.
- Mensagens por `aria-live`, rótulos associados aos campos e foco devolvido ao título após limpar o histórico.
- Testes unitários em `tests/historico.test.js`.

Arquivos: `pedidos.html`, `style.css`, `js/historico.js`, `js/pedidos.js`, `tests/historico.test.js`.

## 9. Etapa 6 — Documentação e preparação da API

Status: concluída.

Implementado:

- `sobre.html` preenchido com proposta, funcionalidades, construção e limitações, mantendo o cabeçalho, a navegação ativa e o rodapé das demais páginas.
- `README.md` reescrito com descrição, funcionalidades, estrutura, execução local, testes, persistência, acessibilidade, identidade visual e declaração honesta de uso de IA.
- `docs/requisitos.md` com 15 requisitos funcionais (RF01–RF15) e 6 requisitos não funcionais (RNF01–RNF06) na Etapa 6, itens fora de escopo e matriz de rastreabilidade. O documento foi ampliado para RF01–RF17 e RNF01–RNF08 na Etapa 8.
- `docs/plano-de-testes.md` com objetivo, escopo, estratégia, ambiente, critérios de entrada/saída/suspensão/retomada, riscos e casos de teste (ampliados para `CT01` a `CT32` na Etapa 7).
- `docs/relatorio-testes.md` com resumo executivo, resultados por suíte, verificação manual, limitações, cobertura e próximos passos de automação.
- Rastreabilidade entre requisitos, casos de teste e suítes automatizadas revisada.
- Nenhuma funcionalidade nova de tela foi adicionada além da página Sobre; as Etapas 1 a 5 permanecem com o mesmo comportamento.

Arquivos: `sobre.html`, `README.md`, `docs/requisitos.md`, `docs/plano-de-testes.md`, `docs/relatorio-testes.md`, `progresso.md`.

## 10. Etapa 7 — Fundação do backend Java/Spring Boot

Status: concluída.

Implementado:

- Projeto Maven em `backend/` com Spring Boot 4.1.2, Java com target 21 e Maven Wrapper 3.3.4 (Maven 3.10.0).
- Classe principal `CafeNaMaoApplication` com `@SpringBootApplication`.
- Domínio imutável: `Bebida`, `ItemPedido`, `Pedido`, `StatusPedido`, `FormaPagamento`, `Preco`, `RegraQuantidade` e `Validacao`.
- Preços com `BigDecimal` e exatamente duas casas; valores nulos, negativos ou com mais de duas casas são rejeitados.
- Quantidade validada entre 1 e 10, com mensagens em português.
- Subtotal do item e total do pedido calculados em `BigDecimal`.
- Pedido com número gerado no formato `PED-XXXXXXXX`, data, cópia imutável dos itens, pagamento, status inicial "Recebido" e troca de status.
- Catálogo em memória (`CatalogoBebidas`) com as mesmas seis bebidas do front-end e busca por identificador.
- 32 testes JUnit 5, incluindo teste de inicialização do contexto Spring.
- JaCoCo configurado no `verify`, com relatório em `backend/target/site/jacoco/index.html`.
- Ganhos rápidos de documentação: coluna "Técnica" nos casos de teste e seção "Conclusão" no relatório.

Arquivos: `backend/pom.xml`, `backend/mvnw`, `backend/mvnw.cmd`, `backend/.mvn/`, `backend/src/main/java/br/com/cafenamao/`, `backend/src/test/java/br/com/cafenamao/`, `docs/plano-de-testes.md`, `docs/requisitos.md`, `docs/relatorio-testes.md`, `README.md`, `progresso.md`.

## 11. Etapa 8 — API REST, testes de integração e cobertura

Status: concluída.

Implementado:

- DTOs de requisição e resposta com Bean Validation: `BebidaRequest`, `BebidaResponse`, `ItemPedidoRequest`, `ItemPedidoResponse`, `PedidoRequest`, `PedidoResponse` e `StatusRequest`.
- Repositórios em memória `BebidaRepositorio` e `PedidoRepositorio`, com reinicialização controlada para isolamento dos testes.
- Serviços `BebidaService` e `PedidoService`, com regras de conflito, atualização, exclusão e busca.
- Utilitário `Slug` para gerar identificadores como `cafe-gelado` a partir do nome.
- Conversão explícita de valores monetários com `Preco.deCentavos` e `Preco.paraCentavos`.
- Códigos e rótulos em `StatusPedido` e `FormaPagamento`, com `deCodigo` para validar a entrada da API.
- CRUD de bebidas em `/api/bebidas`: listar, buscar, criar, atualizar e excluir.
- Pedidos em `/api/pedidos`: criar, listar do mais recente para o mais antigo, buscar por número e alterar status.
- Respostas com preços e totais em centavos e códigos de domínio em minúsculas.
- Erros no formato `ProblemDetail` para `400`, `404` e `409`, incluindo validação de campos e JSON malformado.
- 45 novos testes JUnit 5 (44 de API/utilitários e 1 de serviço), elevando o total para 77.
- Testes de integração com `@SpringBootTest` e MockMvc, sem porta de rede.
- Cobertura JaCoCo de 99,3% de linhas, 99,6% de instruções e 95,0% de branches.
- Casos `CT33` a `CT40` e requisitos `RF16`, `RF17` e `RNF08` adicionados à documentação.

Arquivos: `backend/src/main/java/br/com/cafenamao/api/`, `backend/src/test/java/br/com/cafenamao/api/`, `backend/src/main/java/br/com/cafenamao/dominio/StatusPedido.java`, `backend/src/main/java/br/com/cafenamao/dominio/FormaPagamento.java`, `backend/src/main/java/br/com/cafenamao/dominio/Preco.java`, `docs/requisitos.md`, `docs/plano-de-testes.md`, `docs/relatorio-testes.md`, `README.md`, `progresso.md`.

## 11.1 Etapa 9 — Docker Compose, Postman/Newman e CI

Status: concluída em 09/11/2026, com ressalva de ambiente.

Entregue:

- `backend/Dockerfile` em duas etapas: build com Maven/Java 21, execução de `verify` com testes e JaCoCo, runtime `eclipse-temurin:21-jre`, usuário não privilegiado e healthcheck em `/api/bebidas`.
- `backend/.dockerignore` para excluir `target/`, logs e arquivos locais do contexto de build.
- `docker-compose.yml` com os serviços `api`, `testes-frontend` (Node 26), `testes-api` (Newman) e `testes-backend` (Maven); os três serviços de teste usam o profile `testes`.
- Coleção `postman/CafeNaMao.postman_collection.json` com 27 requisições e 31 asserções, cobrindo bebidas, pedidos, status, validações, conflito, `404` e JSON malformado.
- Ambiente `postman/CafeNaMao.postman_environment.json` com a variável `base_url`.
- Script `scripts/test-api.ps1`, que gera o JAR, sobe a API em porta isolada, aguarda o healthcheck, executa o Newman e encerra a API no `finally`.
- Workflow `.github/workflows/ci.yml` na raiz do repositório Git, com detecção automática da pasta do projeto e jobs de front-end, backend, Newman e Docker Compose, incluindo publicação de JaCoCo e relatórios.
- `.gitignore` para `backend/target/`, `node_modules/`, relatório local do Newman e arquivos de editor.

Resultados reais:

- `npm test`: **53 testes aprovados, 0 reprovados, 0 ignorados**.
- `.\mvnw.cmd verify`: **77 testes aprovados** e JaCoCo com **99,3% de linhas**, **99,6% de instruções** e **95,0% de branches**.
- Newman contra o JAR real na porta 18080: **27 requisições, 31 asserções, 0 falhas**; relatório em `postman/newman-report.json`.
- O script foi executado duas vezes. Na primeira, um processo Java antigo da Etapa 8 mantinha o JAR bloqueado; o processo foi encerrado. Depois, o script foi corrigido para detectar porta ocupada e encerrar o processo que realmente escuta a porta.

Bloqueios de ambiente:

- Docker Desktop, Docker Compose e WSL não estão instalados nesta máquina. O `Dockerfile` e o `docker-compose.yml` foram criados e validados por parser YAML, mas o build e os serviços Docker não foram executados localmente.
- O workflow de CI foi criado e validado por parser YAML, mas não foi executado no GitHub Actions porque o projeto ainda não foi commitado/enviado e o workflow precisa estar na raiz efetiva do repositório.
- A instalação do Docker Desktop não foi tentada; ela altera o sistema e pode exigir reinicialização.

Arquivos: `backend/Dockerfile`, `backend/.dockerignore`, `docker-compose.yml`, `postman/`, `scripts/test-api.ps1`, `.gitignore`, `../.github/workflows/ci.yml`, `README.md`, `docs/requisitos.md`, `docs/plano-de-testes.md`, `docs/relatorio-testes.md` e este arquivo.

## 11.2 Etapa 10 — Banco de dados e integração front-end/API

Status: concluída em 09/11/2026.

Entregue no backend:

- Entidades JPA `BebidaEntity`, `PedidoEntity` e `PedidoItemEntity`, com mapeadores entre domínio e persistência.
- Adaptadores `BebidaRepositorioJpa` e `PedidoRepositorioJpa` implementando as interfaces de repositório existentes, sem alterar os contratos dos serviços e controllers.
- Itens do pedido guardam cópia de nome, descrição, categoria e preço da bebida, preservando o histórico mesmo se o catálogo mudar.
- Migração `V1__estrutura_inicial.sql` com as tabelas `bebida`, `pedido` e `pedido_item`, chaves estrangeiras e valores `NUMERIC(10,2)`.
- H2 em arquivo (`./data/cafe-na-mao`) no perfil padrão e H2 em memória no perfil de teste; `ddl-auto=validate`.
- `SemeadorCatalogo` grava as seis bebidas iniciais apenas quando a tabela está vazia.
- `spring-boot-starter-flyway` adicionado ao `pom.xml`; o `flyway-database-h2` não existe no Maven Central e não foi usado.
- `CorsConfig` libera `/api/**` para qualquer origem, necessário porque `file://` envia `Origin: null`.
- `PersistenciaTest` com 7 testes e `CorsTest` com 2 testes.

Entregue no front-end:

- `js/api.js` com `fetch`, timeout de 3500 ms, validação das respostas, URL configurável e erros tipados.
- Distinção entre falha de rede (`semRede = true`) e recusa HTTP (`status`): a primeira ativa o modo local; a segunda mantém o carrinho e exibe a mensagem da API.
- `menu.js` consome `GET /api/bebidas`, mostra "Catálogo carregado da API." ou "Modo local..." e mantém fallback para `js/dados.js`.
- `pedidos.js` verifica a disponibilidade da API, cria pedidos com `POST /api/pedidos`, altera status com `PATCH /api/pedidos/{numero}/status` e espelha o pedido no histórico local.
- `menu.html` e `pedidos.html` carregam `js/api.js`; `menu.html` ganhou o aviso `#origem-catalogo` e `style.css` ganhou `.catalogo__origem`.
- `tests/api.test.js` com 8 testes.

Resultados reais:

- `npm test`: **61 testes aprovados, 0 reprovados, 0 ignorados**.
- `.\mvnw.cmd verify`: **86 testes aprovados** e JaCoCo com **97,5% de linhas (434/445)**, **96,5% de instruções (1747/1810)** e **91,0% de branches (71/78)**.
- Newman contra o JAR real na porta 18080: **27 requisições, 31 asserções, 0 falhas**, em 2,5 s.
- Persistência entre reinícios: bebida e pedido criados na API continuaram disponíveis após reiniciar o processo (`PERSISTENCIA OK`).
- Verificação no Edge headless via CDP, com páginas em `file://`:
  - menu online com 7 cards, incluindo a bebida `bebida-da-api` criada só na API;
  - pedido online `PED-96A0A104` criado com total 1900 e status alterado para `em-preparo`;
  - recusa da API manteve o carrinho, não criou pedido local e mostrou a mensagem da API;
  - offline exibiu 6 cards locais e criou pedido local.

Arquivos: `backend/pom.xml`, `backend/src/main/resources/application.properties`, `backend/src/test/resources/application-test.properties`, `backend/src/main/resources/db/migration/V1__estrutura_inicial.sql`, `backend/src/main/java/br/com/cafenamao/persistencia/`, `backend/src/main/java/br/com/cafenamao/api/CorsConfig.java`, `backend/src/test/java/br/com/cafenamao/persistencia/PersistenciaTest.java`, `backend/src/test/java/br/com/cafenamao/api/CorsTest.java`, `js/api.js`, `js/menu.js`, `js/pedidos.js`, `menu.html`, `pedidos.html`, `style.css`, `tests/api.test.js`, `docs/requisitos.md`, `docs/plano-de-testes.md`, `docs/relatorio-testes.md`, `README.md`, `AGENTS.md` e este arquivo.

## 12. Decisões técnicas registradas

1. HTML, CSS e JavaScript puro nesta fase para reduzir complexidade e facilitar a explicação na apresentação.
2. Scripts clássicos com padrão de proteção e namespace global (`window.CafeNaMaoDados`) em vez de módulos ES. Isso permite abrir o projeto por `file://` sem servidor local.
3. O painel de detalhes usa o elemento nativo `<dialog>`, com foco inicial no campo de quantidade e retorno do foco ao botão de origem ao fechar.
4. Preços são inteiros em centavos. A formatação usa `Intl.NumberFormat("pt-BR")` e a validação rejeita valores negativos, fracionários ou não numéricos.
5. O carregamento tem atraso simulado de 450 ms para permitir observar o estado de carregamento sem depender de rede.
6. O catálogo em si não usa `localStorage`; a partir da Etapa 4 o carrinho é salvo em `localStorage` para atravessar as páginas.
7. Os cards são renderizados com criação de elementos DOM e `textContent`, não com interpolação direta de dados em `innerHTML`.
8. O cabeçalho é fixo apenas acima de 860 px. Em telas menores ele fica estático para não consumir grande parte da tela.
9. Testes usam o executor nativo do Node.js, sem dependências externas e sem `package-lock.json`.
10. Não há pagamento real. O rodapé e o texto do catálogo informam que o pagamento é simulado.
11. A quantidade fica limitada de 1 a 10; validação e cálculo do total estão em `js/detalhes.js`, isolados para permitir testes no Node.
12. O total parcial da Etapa 3 não cria carrinho: apenas multiplica o preço unitário pela quantidade, sempre em centavos inteiros.
13. O carrinho guarda uma cópia do nome e do preço unitário no momento da adição, para que o resumo não dependa de mudanças futuras no catálogo.
14. A quantidade máxima de 10 vale por bebida no carrinho. Tentar ultrapassar esse limite somando adições repetidas exibe a mensagem de validação e não altera o item.
15. O carrinho é salvo em `localStorage` para sobreviver à navegação entre `menu.html` e `pedidos.html`; se o navegador bloquear o armazenamento, a página continua funcionando durante a sessão.
16. A conclusão do pedido cria um objeto com número, data, cópia dos itens, total, pagamento e status. O pedido é salvo no histórico antes de o carrinho ser limpo. O pagamento é simulado; quando a API está disponível, o pedido também é criado no servidor.
17. A forma de pagamento é obrigatória e limitada a “dinheiro” ou “cartão fictício”. Nenhum número, validade ou código de cartão é solicitado ou armazenado.
18. Se o histórico não puder ser salvo, a confirmação não avança e o carrinho é mantido, evitando perda silenciosa do pedido.
19. O histórico valida os campos e recalcula o total a partir dos itens; registros corrompidos, com status/pagamento desconhecido ou total divergente são ignorados.
20. O status é alterado manualmente entre “Recebido”, “Em preparo” e “Finalizado”; não há progressão automática.
21. O histórico e o carrinho continuam no `localStorage`; com a API disponível, o pedido também é criado no servidor e espelhado no histórico local. Não há conta de usuário nem sincronização automática entre dispositivos.
22. A página Sobre reutiliza as classes existentes (`.catalogo`, `.passos`, `.chamada`) e não exigiu CSS novo, mantendo a consistência visual e reduzindo manutenção.
23. A declaração de uso de IA ficou somente no `README.md`; a página Sobre trata de proposta, funcionalidades e limitações técnicas.
24. A documentação descreve apenas o que existe em cada etapa. As etapas futuras aparecem explicitamente como pendentes até serem implementadas e verificadas.
25. Os 26 casos de teste foram vinculados aos requisitos e às suítes automatizadas para manter rastreabilidade entre documentação e código.
26. A Etapa 7 adicionou 6 casos de backend (`CT27` a `CT32`), elevando o plano para 32 casos rastreáveis.
27. O backend usa Spring Boot 4.1.2 com Java target 21. O Java 25 instalado compila e executa o projeto com target 21.
28. Preços no backend usam `BigDecimal` com duas casas e arredondamento `UNNECESSARY`; valores com mais de duas casas são rejeitados em vez de arredondados.
29. O catálogo do backend passou a ser persistido em H2 na Etapa 10, mantendo as mesmas regras de domínio e o contrato da API. O seed só ocorre quando a tabela de bebidas está vazia.
30. O front-end continua estático e abrível por `file://`, mas agora consome a API quando ela está disponível e cai para o catálogo/histórico locais quando não está.
31. A API usa caminho base `/api`, com bebidas em `/api/bebidas` e pedidos em `/api/pedidos`.
32. Os DTOs expõem valores em centavos (`precoCentavos`, `subtotalCentavos`, `totalCentavos`) para manter o mesmo contrato do front-end e evitar ponto flutuante.
33. Os repositórios da API passaram a usar JPA/H2 na Etapa 10, com adaptadores que preservam as interfaces existentes. Os testes usam H2 em memória e `ddl-auto=validate`.
34. Erros usam `ProblemDetail` (RFC 9457) com `title`, `status`, `detail` e, na validação, o mapa `erros`.
35. A atualização de bebida não permite trocar o identificador pela URL; um `id` divergente no corpo retorna `400`.
36. A imagem da API usa build em duas etapas e `verify` dentro do Docker, para que os testes JUnit e o JaCoCo façam parte do próprio build.
37. O Compose separa a API dos testes: `api` sobe com healthcheck; `testes-frontend`, `testes-api` e `testes-backend` ficam no profile `testes` para não executar tudo em um `docker compose up` comum.
38. A coleção Postman usa `{{base_url}}` e gera um identificador único por execução, evitando conflito com resíduos de rodadas anteriores.
39. O Newman é executado com `npx --yes newman@6.2.1`, sem adicionar dependência ao `package.json` e sem criar `node_modules` permanente.
40. O script PowerShell verifica se a porta está livre, aguarda o endpoint de bebidas e encerra o processo que realmente escuta a porta, porque o comando `java` do Windows pode criar um processo filho.
41. O workflow de CI detecta se o projeto está na raiz ou em `CafeNaMao-main/`, permitindo que o mesmo arquivo funcione antes e depois de uma eventual reorganização do repositório.
42. A Etapa 10 usou H2 em arquivo no perfil padrão e H2 em memória nos testes. H2 evita exigir Docker/WSL nesta máquina e ainda exercita SQL, transações e migrações reais.
43. O schema é versionado por Flyway (`V1__estrutura_inicial.sql`) e `ddl-auto=validate` impede que entidades e banco divirjam silenciosamente.
44. O `spring-boot-starter-flyway` foi necessário no Spring Boot 4; o artefato `flyway-database-h2` não existe no Maven Central e não foi usado.
45. Os adaptadores JPA implementam as interfaces de repositório já existentes, então serviços e controllers não precisaram conhecer detalhes de persistência.
46. Cada item de pedido persiste uma cópia dos dados da bebida (nome, descrição, categoria e preço), preservando o histórico mesmo se o catálogo mudar depois.
47. O `SemeadorCatalogo` só insere as seis bebidas quando a tabela está vazia, evitando duplicação a cada reinício.
48. `js/api.js` centraliza `fetch`, timeout e validação de respostas. A URL padrão é `http://localhost:8080/api` e pode ser trocada por `?api=...` ou `window.CAFE_NA_MAO_API_URL`.
49. Falha de rede e recusa HTTP são tratadas de forma diferente: falha de rede ativa o modo local; recusa mantém o carrinho, não cria pedido local e exibe a mensagem da API.
50. CORS usa `allowedOrigins("*")` em `/api/**` porque páginas `file://` enviam `Origin: null`; não há cookies nem credenciais, então a liberação ampla é aceitável no escopo acadêmico.
51. O menu mantém atraso mínimo de 450 ms para preservar o estado de carregamento observável, mesmo quando a API responde rápido.
52. O histórico local recebe apenas os pedidos criados/alterados na sessão; não há sincronização automática de todo o histórico do servidor para o navegador.

## 13. Verificações executadas

### Testes automatizados

Comandos:

```powershell
npm test
cd backend
.\mvnw.cmd verify
```

O primeiro executa `node --test` no front-end. O segundo compila o backend, roda os testes JUnit e gera o relatório JaCoCo. Em sessões anteriores, quando o `npm.ps1` estava bloqueado, foi usada a versão `npm.cmd`.

Resultado do front-end: **61 testes aprovados, 0 reprovados, 0 ignorados** (8 novos testes de `js/api.js`).

Resultado do backend: **86 testes aprovados, 0 reprovados, 0 ignorados**, com **97,5% de cobertura de linhas (434/445)**, **96,5% de instruções (1747/1810)** e **91,0% de branches (71/78)**.

Cobertura atual dos testes do front-end:

- quantidade mínima de bebidas;
- unicidade dos identificadores;
- presença de dados obrigatórios;
- preços inteiros e positivos;
- formatação de moeda;
- limite inferior de preço zero;
- separador de milhar;
- rejeição de preço negativo, fracionário e não numérico;
- quantidade válida dentro dos limites de 1 a 10;
- rejeição de quantidade abaixo do mínimo, acima do máximo, fracionária ou não numérica;
- cálculo do total em centavos, inclusive nos limites mínimo e máximo;
- rejeição de quantidade e preço inválidos no cálculo do total;
- total do carrinho vazio igual a zero;
- total do carrinho com bebidas diferentes;
- soma de itens repetidos em uma única linha;
- alteração de quantidade e remoção de itens;
- bloqueio de pedido com carrinho vazio;
- criação de pedido com número, data, cópia dos itens e total;
- rejeição de quantidade inválida e de soma acima do limite;
- persistência do carrinho em armazenamento simulado;
- criação de registro com pagamento e status inicial;
- rejeição de pagamento, status, itens e total inválidos no histórico;
- leitura de histórico vazio, ausente ou com JSON inválido;
- gravação, ordenação do mais recente e substituição de número repetido;
- alteração de status existente e rejeição de status/pedido inválido;
- filtragem de registros corrompidos e números duplicados;
- limpeza do histórico;
- comportamento com armazenamento indisponível;
- rótulos em português para status e pagamento;
- conversão do pedido da API para o formato do histórico;
- rejeição de pedido ou item inválido devolvido pela API;
- URL padrão, URL configurável e remoção da barra final;
- catálogo recebido da API e rejeição de resposta inválida;
- distinção entre falha de rede (`semRede`) e recusa HTTP (`status`).

Também foram executados:

```powershell
node --check js/dados.js
node --check js/detalhes.js
node --check js/api.js
node --check js/carrinho.js
node --check js/historico.js
node --check js/menu.js
node --check js/pedidos.js
node --check tests/dados.test.js
node --check tests/detalhes.test.js
node --check tests/carrinho.test.js
node --check tests/historico.test.js
node --check tests/api.test.js
```

Resultado: sem erros de sintaxe.

Foi executada também uma verificação estática complementar:

- todos os IDs usados por `getElementById` em `js/menu.js` e `js/pedidos.js` existem nas respectivas páginas, inclusive os do pagamento e do histórico;
- não há IDs duplicados em `index.html`, `menu.html`, `pedidos.html` ou `sobre.html`;
- as referências de `aria-labelledby` e `aria-describedby` apontam para IDs existentes;
- `js/historico.js` é carregado depois de `js/carrinho.js` e antes de `js/pedidos.js`;
- não há `href="#"` nem estilos inline;
- todos os links internos apontam para arquivos existentes.

### Verificação no navegador

Executada com Microsoft Edge 156 em modo headless, controlado por CDP, com viewports de 390, 768 e 1366 px. O script auxiliar foi temporário e não foi adicionado ao repositório.

| Cenário | Resultado |
|---|---|
| `menu.html` em 390/768/1366 px | Sem rolagem horizontal |
| Detalhes com quantidade 3 | Total parcial `R$ 12,00`; foco inicial no campo |
| Adicionar ao pedido | Painel fecha e resumo mostra `3 itens · R$ 12,00` |
| Duas bebidas no carrinho | `5 itens · R$ 23,00` |
| Adicionar a mesma bebida novamente | Quantidade somada: `6 itens · R$ 27,00` e 2 linhas no armazenamento |
| `pedidos.html` em 390/768/1366 px | Sem rolagem horizontal; total inicial `R$ 27,00` |
| Alterar quantidade para 5 | Subtotal `R$ 20,00` e total `R$ 31,00` |
| Quantidade 11 | Mensagem “A quantidade máxima é 10.” e total inalterado |
| Remover item | Total recalculado para `R$ 20,00` e uma linha restante |
| Concluir pedido | Confirmação visível, número `PED-…`, total `R$ 20,00` e foco na confirmação |
| Após concluir | Carrinho salvo limpo; recarregar mostra “Seu carrinho está vazio.” |
| Concluir sem pagamento | Bloqueia e mantém o carrinho visível |
| Concluir com quantidade 11 | Bloqueia a conclusão e move o foco para o campo |
| Concluir com pagamento escolhido | Confirmação exibida e pedido salvo no histórico |
| Após recarregar com histórico | Pedido persiste e carrinho fica vazio |
| Alterar status | Persiste no histórico e atualiza o cartão |
| Cancelar limpeza | Diálogo fecha sem apagar o histórico |
| Limpar histórico | Diálogo confirma, remove o storage e exibe histórico vazio |
| Teclado | `Esc` fecha o painel e devolve o foco ao botão de origem |
| Console | Nenhum erro de JavaScript durante o fluxo |
| Confirmação em 390/1366 px | Sem rolagem horizontal |
| Histórico em 390/768/1366 px | Sem rolagem horizontal |

### Verificação da Etapa 6

- `npm test` executado em 09/11/2026: **53 testes aprovados, 0 reprovados, 0 ignorados**.
- Nenhum arquivo JavaScript foi alterado na Etapa 6; a checagem de sintaxe da etapa anterior continua válida.
- `sobre.html` aberto por `file://` em 390, 768 e 1366 px para conferir renderização, navegação ativa e ausência de rolagem horizontal.
- Conferência de que todos os IDs de requisitos e casos de teste citados existem e são consistentes entre `docs/requisitos.md`, `docs/plano-de-testes.md` e `docs/relatorio-testes.md`.
- Conferência de links internos, ausência de `href="#"`, ausência de estilos inline e ausência de IDs duplicados em todas as páginas.
- Observação: a pasta usada nesta sessão não é um repositório Git (`git status` retorna "not a git repository"), portanto não foi possível executar `git diff --check` nem revisar diff pelo Git.

### Verificação da Etapa 7

- `.\mvnw.cmd test` em `backend/`: **32 testes aprovados, 0 reprovados, 0 ignorados**.
- `.\mvnw.cmd verify`: build aprovado e relatório JaCoCo gerado.
- Cobertura JaCoCo: **92,0% de linhas (81/88)**, **93,5% de instruções (405/433)** e **96,7% de branches (29/30)**.
- `npm test` no front-end: **53 testes aprovados**, sem regressão.
- O primeiro teste do catálogo falhou por expectativa incorreta (3 bebidas "Café" em vez de 4); a expectativa foi corrigida e a suíte passou. Nenhum código de produção precisou ser alterado por esse motivo.
- Avisos do Mockito e do agente dinâmico do Java 25 aparecem no log, mas não impedem o build. Não há falha de teste associada.
- O Docker não está instalado nesta máquina; a execução via Docker Compose continua pendente para uma etapa futura.

### Verificação da Etapa 8

- `.\mvnw.cmd verify` em `backend/`: **77 testes aprovados, 0 reprovados, 0 ignorados**.
- Cobertura JaCoCo: **99,3% de linhas (274/276)**, **99,6% de instruções (1243/1248)** e **95,0% de branches (57/60)**.
- Testes de integração com MockMvc cobrindo CRUD de bebidas, criação/listagem/busca de pedidos, alteração de status, validação de campos, `404`, `409`, JSON malformado e `ProblemDetail`.
- `npm test` no front-end: **53 testes aprovados**, sem regressão.
- Os testes de integração reiniciam os repositórios em memória antes de cada caso, evitando dependência de ordem de execução.
- Verificação com o JAR real em `--server.port=18080`: `GET /api/bebidas` retornou 6 bebidas; `POST /api/pedidos` criou `PED-852F838D` com `totalCentavos=2250` e status `recebido`; busca inexistente retornou HTTP `404`.
- Avisos do Mockito e do agente dinâmico do Java 25 aparecem no log, mas não impedem o build.
- O front-end continua sem integração com a API; nenhum JS, CSS ou HTML foi alterado na Etapa 8.
- O Docker não está instalado nesta máquina; a execução via Docker Compose continua pendente para uma etapa futura.

### Verificação da Etapa 9

- `npm test`: **53 testes aprovados, 0 reprovados, 0 ignorados**.
- `.\mvnw.cmd verify` em `backend/`: **77 testes aprovados, 0 reprovados, 0 ignorados**, com JaCoCo em 99,3% de linhas, 99,6% de instruções e 95,0% de branches.
- `powershell -ExecutionPolicy Bypass -File .\scripts\test-api.ps1 -Port 18080`: **27 requisições, 31 asserções, 0 falhas**, em 2,5 s.
- Segunda execução com `-SkipBuild`: mesmo resultado de Newman; o script encerrou o processo lançador e o processo Java que escutava a porta, deixando a porta 18080 livre.
- `npx js-yaml@4` validou a sintaxe de `.github/workflows/ci.yml` e `docker-compose.yml`.
- A validação JSON confirmou 27 requisições na coleção e as variáveis `bebida_existente_id`, `bebida_inexistente_id`, `bebida_teste_id` e `pedido_numero`.
- Docker Compose e GitHub Actions **não foram executados**: não há Docker/WSL instalados e o projeto ainda não foi commitado/enviado ao GitHub.
- O front-end continua sem integração com a API; nenhum JS, CSS ou HTML foi alterado na Etapa 9.

### Verificação da Etapa 10

- `npm test`: **61 testes aprovados, 0 reprovados, 0 ignorados**.
- `.\mvnw.cmd verify` em `backend/`: **86 testes aprovados, 0 reprovados, 0 ignorados**, com JaCoCo em 97,5% de linhas, 96,5% de instruções e 91,0% de branches.
- `PersistenciaTest`: 7 testes cobrindo migração, seed, CRUD, itens e ordenação em H2.
- `CorsTest`: 2 testes de preflight com `Origin: null` para `GET` e `POST`.
- Newman: **27 requisições, 31 asserções, 0 falhas**, em 2,5 s.
- Persistência entre reinícios da API confirmada manualmente: bebida e pedido criados continuaram disponíveis após parar e subir o processo de novo.
- Edge headless/CDP por `file://`:
  - menu online com 7 cards, incluindo `bebida-da-api` criada só na API;
  - pedido online `PED-96A0A104`, total 1900, status alterado para `em-preparo` na API e na tela;
  - recusa da API manteve o carrinho (1 item), não criou pedido local e exibiu a mensagem da API;
  - offline exibiu 6 cards locais e registrou 1 pedido no histórico local.
- A porta 8080 e a porta 18080 ficaram livres ao final das verificações.
- Docker Compose e GitHub Actions continuam **não executados** neste ambiente.

## 14. Pendências e limitações conhecidas

- A verificação automatizada foi feita no Edge headless; uma conferência visual rápida no navegador real ainda é recomendada antes da apresentação.
- O histórico local recebe apenas os pedidos criados/alterados na sessão; não há sincronização automática de todo o histórico do servidor para o navegador.
- Pedidos criados offline ficam só no `localStorage` e não são reenviados automaticamente quando a API volta.
- O status do pedido é alterado manualmente no UI e não evolui automaticamente.
- O carrinho e o histórico dependem de `localStorage`; em navegadores que bloqueiam o armazenamento para `file://`, a navegação entre menu e pedidos pode perder o carrinho.
- O banco de desenvolvimento é H2 em arquivo; não há PostgreSQL nem estratégia de produção definida.
- A coleção Postman/Newman, o Docker Compose e o workflow de CI existem, mas o Docker Compose não foi executado localmente e o CI ainda não teve uma execução verde no GitHub Actions.
- Não há testes automatizados de DOM versionados; a verificação de interface é manual com apoio de scripts temporários no Edge.
- Docker Desktop, Docker Compose e WSL não estão instalados nesta máquina. O `Dockerfile` e o `docker-compose.yml` não puderam ser executados localmente; a validação ficou limitada à sintaxe.
- O repositório Git está na pasta pai, com o projeto dentro de `CafeNaMao-main/`; não há commits. Commits, push e a primeira execução do CI continuam pendentes de autorização.
- O Maven Wrapper baixa Maven e dependências na primeira execução, portanto a primeira rodada exige internet.
- Não existe arquivo de logo; a marca é textual.
- Nenhum dado real de cartão pode ser coletado ou armazenado.
- A documentação precisa ser revisada novamente quando o Docker/CI forem executados de verdade e quando houver uma decisão sobre sincronização offline/PostgreSQL.

## 15. Próxima etapa — a definir

Status: aguardando definição e autorização do usuário. Não iniciar sem pedido explícito.

A Etapa 10 concluiu o escopo de banco e integração. Não há uma Etapa 11 definida.

Candidatos para a próxima etapa, a serem escolhidos pelo usuário:

1. Executar o Docker Compose em uma máquina com Docker Desktop/WSL e corrigir o
   que a execução real revelar.
2. Autorizar commit/push e executar o workflow no GitHub Actions para validar o
   CI de ponta a ponta.
3. Definir sincronização de pedidos criados offline com a API.
4. Avaliar PostgreSQL e uma estratégia de migração para produção.
5. Adicionar testes de DOM versionados sem quebrar `file://` nem adicionar
   dependências sem autorização.
6. Revisar a apresentação final e repetir a verificação manual no navegador.

Critérios para qualquer próxima etapa:

- nenhum item declarado pronto sem execução real;
- testes e documentação atualizados na mesma etapa;
- `file://` preservado;
- commit e push somente com autorização explícita.

## 16. Checklist para a próxima sessão

- [x] Ler a skill e este arquivo.
- [x] Rodar `npm test` antes de começar.
- [x] Confirmar com o usuário o escopo das Etapas 6, 7, 8, 9 e 10.
- [x] Implementar somente as etapas autorizadas.
- [x] Executar testes unitários do front-end e do backend.
- [x] Gerar e conferir o relatório de cobertura.
- [x] Executar a coleção Postman com Newman contra o JAR real.
- [x] Validar a sintaxe do `ci.yml` e do `docker-compose.yml`.
- [x] Executar os fluxos online, de recusa e offline no navegador via CDP.
- [x] Confirmar a persistência entre reinícios da API.
- [x] Revisar a documentação e procurar inconsistências.
- [x] Atualizar `progresso.md` com resultados e nova próxima etapa.
- [ ] Não fazer commit ou push sem solicitação explícita do usuário.
- [ ] Executar Docker Compose em uma máquina com Docker Desktop/WSL.
- [ ] Executar o workflow no GitHub Actions após commit/push autorizado.
- [ ] Definir e autorizar a próxima etapa.
