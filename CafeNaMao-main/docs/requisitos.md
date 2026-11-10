# Requisitos — Café na Mão

Última atualização: 09/11/2026

Este documento descreve os requisitos do estado atual do projeto, ou seja, o que
está implementado nas Etapas 1 a 10. Ele não antecipa funcionalidades futuras
(autenticação, pagamento real ou execução do CI em ambiente compatível), que
permanecem fora de escopo até autorização explícita.

## 1. Visão do produto

O Café na Mão é uma aplicação acadêmica de pedidos de bebidas para uma máquina de
café. O cliente consulta um catálogo, escolhe a quantidade de cada bebida, monta
um carrinho, informa uma forma de pagamento simulada e acompanha o pedido pelo
histórico. O projeto também demonstra práticas de Garantia de Qualidade de
Software por meio de testes automatizados e verificação manual documentada.

## 2. Atores

| Ator | Descrição |
|---|---|
| Cliente | Consulta o catálogo, monta o carrinho, confirma o pedido e acompanha o histórico. |
| Operador (simulado) | Altera o status dos pedidos no histórico. Não há autenticação nem perfil separado; a mesma pessoa pode executar as duas funções na demonstração. |

## 3. Requisitos funcionais

| ID | Requisito | Critério de aceite | Casos de teste |
|---|---|---|---|
| RF01 | Navegar entre as páginas Início, Menu, Pedidos e Sobre. | Cada página exibe o cabeçalho, a navegação e o rodapé; o link da página atual recebe `aria-current="page"`. | CT25, CT26 |
| RF02 | Exibir o catálogo de bebidas com nome, categoria, descrição e preço. | O catálogo normal mostra as seis bebidas cadastradas, com preços formatados em reais. | CT01 |
| RF03 | Tratar os estados do catálogo: carregando, vazio, erro e sucesso. | O estado de carregamento usa esqueletos e `aria-busy="true"`; o estado vazio e o estado de erro exibem mensagem e ação adequadas. | CT02, CT03, CT04 |
| RF04 | Abrir os detalhes de uma bebida em painel modal acessível. | O painel mostra categoria, nome, descrição e preço; pode ser fechado pelo botão "Fechar" e pela tecla `Esc`, devolvendo o foco ao botão de origem. | CT05, CT06 |
| RF05 | Validar a quantidade de bebidas entre 1 e 10. | Quantidades inteiras de 1 a 10 são aceitas; valores abaixo de 1, acima de 10, fracionários ou não numéricos exibem mensagem de erro. | CT07, CT08, CT09, CT10 |
| RF06 | Calcular o total parcial de uma bebida em centavos. | O total é o preço unitário multiplicado pela quantidade, sempre com inteiros em centavos e sem arredondamento por ponto flutuante. | CT11 |
| RF07 | Adicionar bebidas ao carrinho, somando itens repetidos. | A mesma bebida adicionada novamente soma a quantidade em uma única linha, respeitando o limite de 10 por bebida. | CT12, CT13 |
| RF08 | Alterar a quantidade e remover itens do carrinho. | Alterações e remoções atualizam imediatamente subtotais e total; quantidades inválidas são rejeitadas sem alterar o carrinho. | CT14, CT15 |
| RF09 | Persistir o carrinho no navegador. | O carrinho permanece após navegar entre páginas e recarregar; se o armazenamento estiver indisponível, a aplicação continua funcionando na sessão. | CT16 |
| RF10 | Concluir o pedido somente com carrinho válido. | A conclusão é bloqueada quando o carrinho está vazio ou contém quantidade inválida, com mensagem e foco no campo problemático. | CT17, CT18 |
| RF11 | Oferecer pagamento simulado por dinheiro ou cartão fictício. | A forma de pagamento é obrigatória; nenhum dado real de cartão é solicitado ou armazenado. | CT18, CT19, CT20 |
| RF12 | Registrar e exibir o histórico de pedidos. | Cada pedido confirmado é salvo com número, data, itens, total, pagamento e status; o mais recente aparece primeiro. | CT19, CT20, CT21 |
| RF13 | Alterar o status de um pedido do histórico. | O status alterna entre "Recebido", "Em preparo" e "Finalizado", com persistência imediata. | CT22 |
| RF14 | Limpar o histórico de pedidos com confirmação. | O diálogo de confirmação permite cancelar ou limpar; limpar remove apenas o histórico, sem afetar o carrinho atual. | CT23, CT24 |
| RF15 | Apresentar a página Sobre. | A página descreve a proposta, as funcionalidades, a construção e as limitações atuais, mantendo a navegação ativa. | CT25 |
| RF16 | Expor uma API REST de bebidas. | `GET /api/bebidas` lista o catálogo; `GET /api/bebidas/{id}` busca uma bebida; `POST` cria; `PUT` atualiza; `DELETE` remove. Respostas usam JSON, o preço é exposto em centavos e os códigos HTTP indicam sucesso, conflito ou recurso inexistente. | CT33, CT34, CT35, CT36, CT40 |
| RF17 | Expor uma API REST de pedidos. | `POST /api/pedidos` cria um pedido com itens e forma de pagamento; `GET /api/pedidos` lista do mais recente para o mais antigo; `GET /api/pedidos/{numero}` busca; `PATCH /api/pedidos/{numero}/status` altera o status. | CT37, CT38, CT39, CT40 |
| RF18 | Carregar o catálogo a partir da API. | O menu consulta `GET /api/bebidas`; se a API não responder, usa o catálogo local e informa o modo local. | CT51 |
| RF19 | Criar pedidos e alterar status na API. | Com a API disponível, o pedido é enviado por `POST /api/pedidos` e o status por `PATCH /api/pedidos/{numero}/status`; se a API estiver inacessível, o pedido é salvo localmente. Uma recusa da API mantém o carrinho e mostra a mensagem. | CT52, CT53, CT54 |
| RF20 | Persistir bebidas e pedidos em banco. | O backend usa H2 em arquivo com schema versionado por Flyway; bebidas, pedidos, itens e status sobrevivem ao reinício. O catálogo inicial só é semeado quando a tabela está vazia. | CT47, CT48, CT49 |

## 4. Requisitos não funcionais

| ID | Requisito | Critério de aceite |
|---|---|---|
| RNF01 | Acessibilidade | Foco de teclado visível, navegação por teclado, rótulos associados aos campos, mensagens em regiões `aria-live` e contraste legível. |
| RNF02 | Responsividade | Layout sem rolagem horizontal em larguras de 390 px, 768 px e 1366 px. |
| RNF03 | Integridade monetária | Todos os preços e totais são inteiros em centavos; formatação com `Intl.NumberFormat("pt-BR")`; uso de `float` para dinheiro é proibido. |
| RNF04 | Privacidade e pagamento simulado | Nenhum dado real de cartão é solicitado, trafegado ou armazenado; o caráter simulado é informado na interface. |
| RNF05 | Execução local | As páginas funcionam por `file://`, com scripts clássicos, sem servidor local, build ou dependências npm. |
| RNF06 | Resiliência da persistência | Falhas ou bloqueios de `localStorage` não interrompem o uso da aplicação; o histórico inválido é ignorado e o carrinho é mantido se a gravação falhar. |
| RNF07 | Testabilidade do backend | Projeto Maven com wrapper, testes JUnit 5 (unitários e de integração com MockMvc) e relatório JaCoCo, executáveis com `.\mvnw.cmd test` e `.\mvnw.cmd verify`, sem configuração manual além do Java instalado. |
| RNF08 | Qualidade da API REST | DTOs com Bean Validation, separação entre domínio e contrato HTTP, valores monetários em centavos, códigos HTTP coerentes e erros no formato `ProblemDetail` (RFC 9457), sem expor entidades de domínio diretamente. |
| RNF09 | Reprodutibilidade em containers | `Dockerfile` multi-stage com Java 21, build executando testes e JaCoCo, runtime não privilegiado e healthcheck; `docker-compose.yml` sobe a API e executa testes de front-end, backend e API. |
| RNF10 | Automação de testes de API e CI | Coleção Postman parametrizada por `base_url`, executável por Newman, e workflow de CI com detecção da pasta do projeto e jobs de front-end, backend, API e Docker. |
| RNF11 | Persistência durável e versionada | H2 em arquivo no perfil padrão, H2 em memória nos testes e migrações Flyway; `ddl-auto=validate` impede divergência entre entidades e schema. |
| RNF12 | Integração sem quebrar `file://` | Scripts clássicos, `js/api.js` com `fetch`, CORS liberado para `/api/**` e fallback para `localStorage` quando a API não estiver acessível. |

## 5. Fora de escopo

Os itens abaixo não fazem parte do estado atual e não devem ser tratados como
implementados:

- PostgreSQL em produção (o projeto usa H2 em arquivo);
- autenticação, contas de usuário e sincronização entre dispositivos;
- pagamento real ou integração com meio de pagamento;
- manutenção de máquinas de café;
- execução local do Docker Compose em máquina sem Docker/WSL e primeira execução
  verde do workflow no GitHub Actions.

## 6. Matriz de rastreabilidade

| Requisito | Casos de teste | Cobertura automatizada |
|---|---|---|
| RF01 | CT25, CT26 | Verificação estática de links e IDs; verificação manual no navegador. |
| RF02 | CT01, CT31 | `tests/dados.test.js` e `CatalogoBebidasTest` (JUnit) |
| RF03 | CT02, CT03, CT04 | Verificação manual no navegador. |
| RF04 | CT05, CT06 | Verificação manual no navegador (incluindo teclado). |
| RF05 | CT07, CT08, CT09, CT10, CT28 | `tests/detalhes.test.js`, `tests/carrinho.test.js` e `RegraQuantidadeTest` (JUnit) |
| RF06 | CT11, CT29 | `tests/detalhes.test.js`, `ItemPedidoTest` e `PedidoTest` (JUnit) |
| RF07 | CT12, CT13 | `tests/carrinho.test.js` |
| RF08 | CT14, CT15 | `tests/carrinho.test.js` |
| RF09 | CT16 | `tests/carrinho.test.js` |
| RF10 | CT17, CT30 | `tests/carrinho.test.js` e `PedidoTest` (JUnit); verificação manual no navegador. |
| RF11 | CT18, CT19, CT20 | `tests/historico.test.js`; verificação manual no navegador. |
| RF12 | CT19, CT20, CT21, CT30 | `tests/historico.test.js` e `PedidoTest` (JUnit) |
| RF13 | CT22, CT32 | `tests/historico.test.js` e `PedidoTest` (JUnit); verificação manual no navegador. |
| RF14 | CT23, CT24 | `tests/historico.test.js`; verificação manual no navegador. |
| RF15 | CT25 | Verificação manual no navegador. |
| RF16 | CT33, CT34, CT35, CT36, CT40 | `BebidaControllerTest` e `SlugTest` (MockMvc/JUnit) |
| RF17 | CT37, CT38, CT39, CT40 | `PedidoControllerTest` (MockMvc/JUnit) |
| RF18 | CT51 | `tests/api.test.js`; verificação manual/CDP no navegador. |
| RF19 | CT52, CT53, CT54 | `tests/api.test.js`; verificação manual/CDP no navegador. |
| RF20 | CT47, CT48, CT49 | `PersistenciaTest` (JUnit/H2) |
| RNF01 | CT06, CT26 | Verificação manual no navegador. |
| RNF02 | CT26 | Verificação manual no navegador. |
| RNF03 | CT11, CT27, CT29 | `tests/dados.test.js`, `tests/detalhes.test.js`, `PrecoTest` e `PedidoTest` (JUnit) |
| RNF04 | CT18, CT19, CT20 | Verificação estática e manual. |
| RNF05 | CT26 | Verificação manual abrindo as páginas por `file://`. |
| RNF06 | CT16 | `tests/carrinho.test.js` e `tests/historico.test.js` |
| RNF07 | CT27, CT28, CT29, CT30, CT31, CT32, CT33 a CT40, CT47 a CT50 | 86 testes JUnit 5, incluindo integração com MockMvc e H2; relatório JaCoCo gerado por `.\mvnw.cmd verify`. |
| RNF08 | CT33, CT34, CT35, CT36, CT37, CT38, CT39, CT40 | `BebidaControllerTest`, `PedidoControllerTest` e `ApiExceptionHandler`. |
| RNF09 | CT41, CT42 | `backend/Dockerfile`, `backend/.dockerignore`, `docker-compose.yml` e validação YAML; execução Docker pendente no ambiente local. |
| RNF10 | CT43, CT44, CT45, CT46 | Coleção Postman, `scripts/test-api.ps1`, `.github/workflows/ci.yml` e validação YAML; CI ainda não executado no GitHub Actions. |
| RNF11 | CT47, CT48, CT49 | `PersistenciaTest`, migração Flyway e `application.properties`. |
| RNF12 | CT50, CT51, CT54 | `CorsTest`, `tests/api.test.js` e verificação manual/CDP. |

## 7. Backend, banco e integração — Etapas 7 a 10

A Etapa 7 criou a fundação do backend, a Etapa 8 expôs o domínio por uma API
REST, a Etapa 9 adicionou containers, testes de contrato e CI e a Etapa 10
adicionou persistência com H2/Flyway e a integração do front-end com a API:

- projeto Maven com Spring Boot 4.1.2, Java com target 21 e Maven Wrapper;
- domínio `Bebida`, `ItemPedido`, `Pedido`, `StatusPedido`, `FormaPagamento`,
  `Preco` e `RegraQuantidade`;
- validação de preço com `BigDecimal` e duas casas decimais;
- validação de quantidade entre 1 e 10;
- cálculo de subtotal e total do pedido;
- status inicial "Recebido", número gerado com prefixo `PED-` e troca de status;
- DTOs de requisição e resposta com Bean Validation;
- interfaces `BebidaRepositorio` e `PedidoRepositorio` com adaptadores JPA;
- migração `V1__estrutura_inicial.sql` com as tabelas `bebida`, `pedido` e
  `pedido_item`, chaves estrangeiras e valores `NUMERIC(10,2)`;
- H2 em arquivo (`./data/cafe-na-mao`) no perfil padrão e H2 em memória nos
  testes, com `ddl-auto=validate`;
- catálogo inicial semeado somente quando a tabela `bebida` está vazia;
- itens do pedido com cópia de nome, descrição, categoria e preço da bebida,
  preservando o histórico mesmo se o catálogo mudar;
- endpoints de bebidas e de pedidos;
- preços e totais expostos em centavos (`precoCentavos`, `subtotalCentavos` e
  `totalCentavos`);
- erros HTTP no formato `ProblemDetail`, com `400`, `404` e `409`;
- CORS liberado para `/api/**`, permitindo o consumo por páginas abertas em
  `file://`;
- 86 testes JUnit 5, incluindo testes de integração com `@SpringBootTest`,
  MockMvc, H2 e CORS;
- relatório de cobertura JaCoCo gerado no `verify`;
- `js/api.js` com `fetch`, timeout, validação das respostas e distinção entre
  falha de rede e recusa da API;
- menu e pedidos consumindo a API, com fallback para o catálogo e o histórico
  locais quando a API não está acessível;
- 61 testes Node.js no front-end, incluindo os testes de `js/api.js`;
- `Dockerfile` multi-stage com healthcheck e testes executados no build;
- `docker-compose.yml` com a API, volume para o banco e serviços de teste em
  profile separado;
- coleção Postman com 27 requisições e 31 asserções;
- script `scripts/test-api.ps1` para subir o JAR e executar o Newman;
- workflow de CI com detecção da pasta do projeto e jobs de front-end, backend,
  Newman e Docker Compose.

### 7.1 Endpoints disponíveis

| Método | Caminho | Resultado esperado |
|---|---|---|
| `GET` | `/api/bebidas` | Lista as bebidas do catálogo. |
| `GET` | `/api/bebidas/{id}` | Retorna uma bebida ou `404`. |
| `POST` | `/api/bebidas` | Cria uma bebida; retorna `201` e `Location`, ou `409` se o identificador já existir. |
| `PUT` | `/api/bebidas/{id}` | Atualiza uma bebida; retorna `200` ou `404`. |
| `DELETE` | `/api/bebidas/{id}` | Remove uma bebida; retorna `204` ou `404`. |
| `POST` | `/api/pedidos` | Cria um pedido; retorna `201` e `Location`. |
| `GET` | `/api/pedidos` | Lista os pedidos do mais recente para o mais antigo. |
| `GET` | `/api/pedidos/{numero}` | Retorna um pedido ou `404`. |
| `PATCH` | `/api/pedidos/{numero}/status` | Altera o status; retorna `200`, `400` ou `404`. |

Os códigos de domínio usados na API são `dinheiro`, `cartao-ficticio`,
`recebido`, `em-preparo` e `finalizado`.

### 7.2 Integração front-end/API — Etapa 10

- O menu carrega `js/api.js` e tenta `GET /api/bebidas`. Em caso de sucesso,
  exibe "Catálogo carregado da API."; em caso de falha, usa `js/dados.js` e
  exibe "Modo local: API indisponível...".
- A página de pedidos verifica a API ao carregar. Com a API disponível, envia
  `POST /api/pedidos` e espelha o pedido devolvido no histórico local; a troca de
  status usa `PATCH /api/pedidos/{numero}/status`.
- Se houver falha de rede, o pedido é criado e salvo apenas no navegador. Se a
  API responder com erro (por exemplo, bebida removida), o carrinho é mantido,
  nenhum pedido local é criado e a mensagem da API é exibida.
- A URL da API pode ser alterada com `?api=http://host:porta/api` na página ou
  definindo `window.CAFE_NA_MAO_API_URL` antes de `js/api.js`. O padrão é
  `http://localhost:8080/api`.
- Nenhum dado real de cartão é coletado; o pagamento continua simulado.

O Docker Compose e o workflow de CI foram criados, mas a execução local do Docker
e a primeira execução no GitHub Actions continuam pendentes de ambiente e de
autorização para commit/push.
