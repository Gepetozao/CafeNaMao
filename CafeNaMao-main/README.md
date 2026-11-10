# Café na Mão

Aplicação acadêmica de pedidos de bebidas para uma máquina de café. O cliente
consulta o catálogo, escolhe a quantidade, monta o carrinho, informa uma forma de
pagamento **simulada** e acompanha o pedido pelo histórico.

O projeto também demonstra práticas de Garantia de Qualidade de Software, com
testes automatizados, plano de testes e relatório de execução.

> **Pagamento simulado.** Nenhum dado real de cartão é solicitado ou armazenado.
> O front-end funciona por `file://`: quando a API está disponível, carrega o
> catálogo, cria pedidos e altera status no servidor; quando não está, cai para o
> catálogo e o histórico locais (`localStorage`). O backend usa H2 com migrações
> Flyway, API REST, Docker Compose, coleção Postman/Newman e workflow de CI.

## Funcionalidades implementadas

- Página inicial com apresentação, navegação e chamada para o menu.
- Catálogo com seis bebidas, categoria, descrição e preço em reais.
- Estados de carregamento, lista vazia, erro e sucesso no catálogo.
- Painel de detalhes acessível, com validação de quantidade de 1 a 10.
- Cálculo de total parcial em centavos, sem arredondamento por ponto flutuante.
- Carrinho com inclusão, soma de itens repetidos, alteração e remoção.
- Resumo do pedido, escolha obrigatória de pagamento simulado e confirmação.
- Histórico de pedidos com número, data, itens, total, pagamento e status.
- Alteração de status entre "Recebido", "Em preparo" e "Finalizado".
- Limpeza do histórico com diálogo de confirmação.
- Página Sobre com proposta, funcionalidades, construção e limitações.
- Layout responsivo e navegação por teclado.
- Backend Java/Spring Boot com domínio de bebidas, pedidos, preço, quantidade e
  status, coberto por testes JUnit 5.
- API REST de bebidas e pedidos, com Bean Validation, respostas `ProblemDetail`
  e testes de integração com MockMvc.
- Persistência em H2 com schema versionado por Flyway e catálogo inicial semeado.
- Integração do menu e dos pedidos com a API, mantendo o fallback local por
  `file://`.
- Dockerfile multi-stage e `docker-compose.yml` para API e testes.
- Coleção Postman com 27 requisições, executável por Newman.
- Workflow de GitHub Actions para front-end, backend, Newman e Docker.

## Estrutura do projeto

```text
CaféNaMão/
├── index.html                 Página inicial
├── menu.html                  Catálogo e painel de detalhes
├── pedidos.html               Carrinho, pagamento, confirmação e histórico
├── sobre.html                 Informações sobre o projeto
├── style.css                  Estilos globais e responsivos
├── js/
│   ├── dados.js               Catálogo e formatação de preço
│   ├── detalhes.js            Validação de quantidade e total parcial
│   ├── api.js                 Cliente HTTP da API e conversão de pedido
│   ├── carrinho.js            Regras do carrinho e persistência
│   ├── historico.js           Histórico, pagamento simulado e status
│   ├── menu.js                Renderização e estados do catálogo
│   └── pedidos.js             Carrinho na tela, pagamento e histórico
├── tests/
│   ├── dados.test.js
│   ├── detalhes.test.js
│   ├── carrinho.test.js
│   ├── historico.test.js
│   └── api.test.js
├── docs/
│   ├── requisitos.md          Requisitos funcionais e não funcionais
│   ├── plano-de-testes.md     Casos CT01 a CT54
│   └── relatorio-testes.md    Resultados e limitações
├── backend/
│   ├── pom.xml                Projeto Maven/Spring Boot
│   ├── mvnw / mvnw.cmd        Maven Wrapper
│   ├── Dockerfile             Build em duas etapas e healthcheck
│   ├── .dockerignore          Exclusões do contexto Docker
│   ├── data/                  Banco H2 de desenvolvimento (gerado)
│   └── src/
│       ├── main/java/br/com/cafenamao/
│       │   ├── CafeNaMaoApplication.java
│       │   ├── dominio/       Bebida, Pedido, ItemPedido, Preco, status e pagamento
│       │   ├── catalogo/      Catálogo e semeador inicial
│       │   ├── persistencia/  Entidades JPA, mapeadores e adaptadores
│       │   └── api/           DTOs, serviços, controllers, CORS e erros
│       └── main/resources/db/migration/V1__estrutura_inicial.sql
│       └── test/java/br/com/cafenamao/  Testes JUnit 5 e MockMvc
├── postman/
│   ├── CafeNaMao.postman_collection.json
│   └── CafeNaMao.postman_environment.json
├── scripts/
│   └── test-api.ps1           Sobe o JAR, aguarda a API e executa o Newman
├── docker-compose.yml         API e serviços de teste em containers
├── .gitignore                 Artefatos de build e relatórios locais
├── package.json               Script de testes; sem dependências
├── progresso.md               Registro do estágio atual
└── projeto 1/                 Skills e configurações de agentes
```

O workflow de CI fica na raiz do repositório Git, em
`../.github/workflows/ci.yml`, porque o projeto está na subpasta
`CafeNaMao-main`. Ele detecta automaticamente a pasta correta.

## Como executar

O front-end é estático e funciona diretamente no navegador, sem servidor local e
sem build.

```powershell
start index.html
start menu.html
start pedidos.html
start sobre.html
```

Também é possível abrir os arquivos `.html` com dois cliques ou arrastá-los para
o navegador.

### Integração com a API

Com a API no ar em `http://localhost:8080`, o menu carrega o catálogo do servidor
e a página de pedidos cria o pedido e altera o status na API. Sem a API, as
páginas continuam funcionando com o catálogo e o histórico locais e mostram um
aviso de modo local.

A URL da API pode ser trocada de duas formas:

- parâmetro na página: `menu.html?api=http://192.168.0.10:8080/api`;
- variável global definida antes de `js/api.js`:
  `window.CAFE_NA_MAO_API_URL = "http://192.168.0.10:8080/api";`.

O padrão é `http://localhost:8080/api`. A API libera CORS para `/api/**`, o que
permite o consumo por páginas abertas em `file://`.

### Estados de demonstração do catálogo

- Catálogo normal: `menu.html`
- Lista vazia: `menu.html?estado=vazio`
- Erro: `menu.html?estado=erro`

Os parâmetros valem apenas para a primeira carga. O botão "Tentar novamente"
recarrega o catálogo normal.

## Backend Java (Etapas 7 a 10)

O backend é um projeto Maven com Spring Boot 4.1.2 e Java com target 21. Não é
necessário instalar Maven: o Maven Wrapper está incluído no repositório.

Requisito: Java 21 ou superior instalado (verificado com Java 25). Na primeira
execução, o wrapper baixa o Maven e as dependências.

```powershell
cd backend
.\mvnw.cmd test
```

Para gerar o relatório de cobertura JaCoCo:

```powershell
.\mvnw.cmd verify
```

O relatório fica em `backend/target/site/jacoco/index.html`.

Para iniciar a API:

```powershell
cd backend
.\mvnw.cmd spring-boot:run
```

A API sobe em `http://localhost:8080`. No perfil padrão, os dados ficam em um
banco H2 em arquivo, em `backend/data/cafe-na-mao`, e sobrevivem ao reinício do
servidor. O schema é criado e versionado pelo Flyway; o catálogo inicial é
semeado apenas quando a tabela de bebidas está vazia.

### Banco de dados e migrações

- `backend/src/main/resources/db/migration/V1__estrutura_inicial.sql` cria as
  tabelas `bebida`, `pedido` e `pedido_item`, com chaves estrangeiras e valores
  `NUMERIC(10,2)`.
- O perfil padrão usa `jdbc:h2:file:./data/cafe-na-mao`.
- O perfil `test` usa H2 em memória, então cada execução de teste começa limpa.
- `ddl-auto=validate` faz a API falhar na inicialização se as entidades e o schema
  divergirem.
- Para recomeçar do zero no desenvolvimento, pare a API e apague a pasta
  `backend/data/`.

### CORS

`CorsConfig` libera `/api/**` para qualquer origem, sem credenciais. Isso é
necessário porque páginas abertas por `file://` enviam `Origin: null`.

### Endpoints

| Método | Caminho | Descrição |
|---|---|---|
| `GET` | `/api/bebidas` | Lista o catálogo. |
| `GET` | `/api/bebidas/{id}` | Busca uma bebida; retorna `404` se não existir. |
| `POST` | `/api/bebidas` | Cria uma bebida; retorna `201` e `Location`. |
| `PUT` | `/api/bebidas/{id}` | Atualiza uma bebida. |
| `DELETE` | `/api/bebidas/{id}` | Remove uma bebida; retorna `204`. |
| `POST` | `/api/pedidos` | Cria um pedido. |
| `GET` | `/api/pedidos` | Lista os pedidos do mais recente para o mais antigo. |
| `GET` | `/api/pedidos/{numero}` | Busca um pedido pelo número. |
| `PATCH` | `/api/pedidos/{numero}/status` | Altera o status do pedido. |

Os valores monetários são expostos em centavos (`precoCentavos`,
`subtotalCentavos` e `totalCentavos`). Os códigos usados são `dinheiro`,
`cartao-ficticio`, `recebido`, `em-preparo` e `finalizado`.

Exemplo de criação de bebida:

```powershell
curl.exe -X POST http://localhost:8080/api/bebidas `
  -H "Content-Type: application/json" `
  -d '{"nome":"Café Gelado","descricao":"Bebida gelada.","categoria":"Café","precoCentavos":950}'
```

Exemplo de criação de pedido:

```powershell
curl.exe -X POST http://localhost:8080/api/pedidos `
  -H "Content-Type: application/json" `
  -d '{"formaPagamento":"dinheiro","itens":[{"bebidaId":"cappuccino","quantidade":3}]}'
```

Erros de validação, conflito e recurso inexistente retornam `ProblemDetail`
(`application/problem+json`) com `title`, `status`, `detail` e, quando aplicável,
o mapa `erros`.

## Docker Compose (Etapa 9)

Pré-requisito: Docker Desktop com Docker Compose v2 e WSL 2 no Windows, ou Docker
Engine no Linux.

A imagem da API usa build em duas etapas: `maven:3.9-eclipse-temurin-21` executa
`./mvnw verify` e gera o JAR; `eclipse-temurin:21-jre` executa a aplicação como
usuário não privilegiado. O healthcheck consulta `/api/bebidas`.

```powershell
# Sobe somente a API em segundo plano, em http://localhost:8080
docker compose up --build -d

# Testes do front-end em um container Node
docker compose --profile testes run --rm testes-frontend

# Testes do backend via Maven
docker compose --profile testes run --rm testes-backend

# Com a API no ar, executa o Newman dentro da rede do Compose
docker compose --profile testes run --rm --no-deps testes-api

# Encerra os containers
docker compose down -v
```

O build da imagem `api` já executa os testes JUnit e o JaCoCo. O relatório do
Newman é gravado em `postman/newman-report.json`.

> **Ambiente atual:** Docker Desktop, Docker Compose e WSL não estão instalados
> na máquina usada nesta etapa. Os arquivos foram criados e validados por parser
> YAML, mas os comandos acima não foram executados localmente.

## Postman/Newman (Etapa 9)

A coleção fica em `postman/CafeNaMao.postman_collection.json` e usa a variável
`base_url`. Ela cobre 27 requisições: catálogo, CRUD de bebidas, pedidos,
alteração de status, validações, conflito, `404` e JSON malformado.

Importe a coleção e o ambiente `postman/CafeNaMao.postman_environment.json` no
Postman, ou execute com Newman:

```powershell
npx --yes newman@6.2.1 run postman/CafeNaMao.postman_collection.json `
  --environment postman/CafeNaMao.postman_environment.json `
  --env-var base_url=http://localhost:8080 `
  --reporters cli,json `
  --reporter-json-export postman/newman-report.json
```

O script `scripts/test-api.ps1` automatiza o ciclo completo: gera o JAR, sobe a
API em uma porta isolada, aguarda o healthcheck, executa o Newman e encerra a API.

```powershell
.\scripts\test-api.ps1 -Port 18080
```

Resultado atual: **27 requisições, 31 asserções, 0 falhas**.

## Integração contínua (Etapa 9)

O workflow fica em `../.github/workflows/ci.yml` na raiz do repositório Git,
porque o projeto está na subpasta `CafeNaMao-main`. Ele detecta automaticamente
se o projeto está na raiz ou nessa subpasta e executa:

- front-end: `npm test` com Node 26;
- backend: `./mvnw verify` com Java 21, publicando JaCoCo e surefire;
- API: gera o JAR, inicia a API, executa Newman e publica o relatório;
- Docker: valida o Compose, faz o build da imagem, roda os testes do front-end e
  do Newman em containers e derruba tudo no final.

O workflow ainda não foi executado no GitHub Actions porque o repositório não tem
commits/push. Não há resultado verde para declarar.

## Como executar os testes

### Front-end

Requisito: Node.js instalado (o projeto foi verificado com a versão 26.13.0).

```powershell
npm test
```

O comando executa o executor nativo do Node.js (`node --test`) e não precisa de
dependências externas nem de `npm install`.

Para executar um arquivo específico:

```powershell
node --test tests/dados.test.js
node --test tests/detalhes.test.js
node --test tests/carrinho.test.js
node --test tests/historico.test.js
node --test tests/api.test.js
```

Para filtrar testes pelo nome:

```powershell
node --test --test-name-pattern="preço"
```

Resultado atual do front-end: **61 testes aprovados, 0 reprovados, 0 ignorados**.

### Backend

```powershell
cd backend
.\mvnw.cmd test
```

Resultado atual do backend: **86 testes aprovados, 0 reprovados, 0 ignorados**.

Total do projeto: **147 testes automatizados aprovados**.

Além disso, a coleção Postman executa **27 requisições com 31 asserções** via
Newman, sem falhas.

## Persistência

- API: banco H2 em arquivo em `backend/data/cafe-na-mao`, versionado por Flyway.
- Carrinho: chave `cafe-na-mao:carrinho:v1` no `localStorage`.
- Histórico: chave `cafe-na-mao:historico:v1` no `localStorage`.

Os dados locais ficam apenas no navegador. Com a API disponível, o pedido é
criado no servidor e também espelhado no histórico local; sem a API, o pedido é
salvo somente no navegador. Não há conta de usuário nem sincronização automática
do histórico do servidor para o navegador. Se o armazenamento for bloqueado, a
aplicação continua funcionando durante a sessão.

## Acessibilidade e responsividade

- HTML semântico e link "Pular para o conteúdo".
- Foco de teclado visível e navegação por teclado.
- Rótulos associados aos campos e mensagens em regiões `aria-live`.
- Diálogos fecham com `Esc` e devolvem o foco ao elemento de origem.
- Layout verificado em 390 px, 768 px e 1366 px, sem rolagem horizontal.
- Suporte a `prefers-reduced-motion`.

## Identidade visual

Estilo neobrutalista com bordas grossas, sombras duras e blocos de alto contraste.

- Carvão `#252525`
- Creme `#F5F0E8`
- Laranja `#FF6A00`
- Cinza `#D8D3CA`

Não há imagens ou fontes externas; a ilustração da xícara é feita em CSS.

## Uso de inteligência artificial

Este projeto foi desenvolvido com apoio de ferramentas de IA para geração e
revisão de código e documentação. Todo o resultado foi lido e ajustado ao longo
do processo. As verificações automatizadas foram executadas localmente com
`npm test`, `.\mvnw.cmd verify` e `.\scripts\test-api.ps1` (Newman), e a interface
foi conferida manualmente no navegador, incluindo navegação por teclado e
larguras de celular, tablet e desktop.

A IA não foi usada como substituta dos testes: nenhum resultado é declarado como
aprovado sem execução real, e as limitações conhecidas estão documentadas neste
README, em `docs/relatorio-testes.md` e em `progresso.md`.

## Documentação

- `progresso.md` — histórico do projeto, decisões técnicas e próxima etapa.
- `docs/requisitos.md` — requisitos funcionais e não funcionais.
- `docs/plano-de-testes.md` — plano e casos `CT01` a `CT54`.
- `docs/relatorio-testes.md` — resultados, limitações e próximos passos.
- `postman/` — coleção e ambiente da API.
- `docker-compose.yml` e `backend/Dockerfile` — execução em containers.

## Limitações e próximos passos

Ainda não existem:

- banco de produção (o desenvolvimento usa H2 em arquivo, não PostgreSQL);
- execução local do Docker Compose: Docker Desktop/WSL não estão instalados;
- execução verde do workflow no GitHub Actions: o repositório ainda não tem
  commits/push;
- autenticação, contas de usuário e sincronização de dados entre dispositivos;
- sincronização automática de pedidos criados offline;
- integração com máquina física de café ou pagamento real.

O front-end continua estático: as páginas abrem por `file://` e usam a API quando
ela está disponível. O projeto está em evolução por etapas, e a próxima etapa deve
ser definida e autorizada antes de ser iniciada.
