# Plano de testes — Café na Mão

Última atualização: 09/11/2026

## 1. Objetivo

Definir a estratégia, o ambiente, os critérios e os casos de teste do estado atual
do Café na Mão, garantindo rastreabilidade entre requisitos, casos de teste e
verificações executadas.

## 2. Escopo

### 2.1 Itens testáveis

- Catálogo de bebidas e seus estados (carregando, vazio, erro e sucesso).
- Painel de detalhes e validação de quantidade.
- Cálculo de total parcial e total do carrinho em centavos.
- Inclusão, soma, alteração e remoção de itens.
- Persistência do carrinho e do histórico em `localStorage`.
- Conclusão de pedido, bloqueios de validação e pagamento simulado.
- Histórico, alteração de status e limpeza com confirmação.
- Página Sobre, navegação, acessibilidade básica e responsividade.
- Domínio do backend Java: preço, quantidade, item, pedido, catálogo e status.
- API REST de bebidas e pedidos: contratos, validação, erros HTTP e serialização.
- Persistência em H2 com migrações Flyway e catálogo inicial semeado.
- Integração do menu e dos pedidos com a API, incluindo fallback local.
- CORS para consumo por páginas abertas em `file://`.
- Dockerfile, Docker Compose, coleção Postman/Newman e workflow de CI.

### 2.2 Itens não testáveis nesta fase

- Execução local do Docker Compose: Docker Desktop/WSL não estão instalados.
- Execução do workflow no GitHub Actions: o repositório ainda não tem commits/push.
- PostgreSQL em produção: o projeto usa H2 em arquivo.
- Pagamento real: o comportamento é explicitamente simulado.
- Sincronização entre dispositivos e contas de usuário: não existem.
- Integração com máquina física de café: fora do escopo acadêmico.

## 3. Estratégia

| Nível | Ferramenta | Abrangência |
|---|---|---|
| Unitário do front-end | `node:test` + `node:assert/strict` | Regras de dados, quantidade, carrinho, histórico, pagamento e integração HTTP. |
| Unitário e de contexto do backend | JUnit 5 + Spring Boot Test | Domínio, catálogo, persistência e inicialização do contexto Spring. |
| Integração da API | `@SpringBootTest` + MockMvc + H2 em memória | Endpoints, validação, códigos HTTP, erros `ProblemDetail`, serialização, persistência e CORS. |
| Integração do front-end | Edge headless via CDP | Catálogo da API, criação de pedido, status, recusa da API e fallback offline. |
| Interface | Verificação manual no navegador | Renderização, estados, foco, teclado, mensagens e responsividade. |
| Estático | Inspeção de arquivos | IDs, links internos, `aria-*`, ausência de `href="#"` e de estilo inline. |

### 3.1 Técnicas de teste adotadas

- **Partição de equivalência:** agrupa entradas que devem produzir o mesmo
  comportamento, como preço válido, quantidade válida e carrinho vazio.
- **Análise de valor-limite:** exercita os limites 1 e 10 de quantidade, além dos
  valores imediatamente inválidos 0 e 11.
- **Tabela de decisão:** cobre combinações de condições, como carrinho vazio,
  pagamento ausente, pagamento em dinheiro e cartão fictício.
- **Transição de estados:** verifica as mudanças de status do pedido e a abertura
  e o fechamento do painel de detalhes.
- **Teste de persistência:** confere gravação, leitura, ordenação e descarte de
  dados inválidos no armazenamento local e no banco H2.
- **Inspeção e teste funcional:** valida navegação, renderização, conteúdo da
  página Sobre e configuração visual.
- **Teste de contrato HTTP:** verifica método, caminho, corpo JSON, cabeçalho
  `Location` e código de status de cada endpoint.
- **Teste de integração no navegador:** usa Edge headless via CDP para executar
  o fluxo real por `file://` contra a API.

Os testes automatizados usam o padrão Arrange-Act-Assert e cobrem casos
positivos, negativos e de limite. Os testes de integração da API usam MockMvc
para exercitar o contexto Spring completo, sem abrir uma porta de rede.

## 4. Ambiente

| Item | Valor |
|---|---|
| Sistema | Windows |
| Node.js | v26.13.0 (executor nativo `node --test`) |
| Java | 25 instalado, compilação com target 21 |
| Spring Boot | 4.1.2 |
| Build do backend | Maven Wrapper 3.3.4 (Maven 3.10.0) |
| Navegadores | Microsoft Edge e Google Chrome (versões atuais) |
| Larguras verificadas | 390 px, 768 px e 1366 px |
| Execução do front-end | Páginas abertas por `file://`, sem servidor local |
| Dependências do front-end | Nenhuma dependência npm |
| API REST | `http://localhost:8080/api`, executada com `.\mvnw.cmd spring-boot:run` |
| Banco de desenvolvimento | H2 em arquivo em `./data/cafe-na-mao`, versionado por Flyway |
| Testes da API | MockMvc com H2 em memória, sem porta de rede |
| Contrato da API | Newman 6.2.1 via `npx`, contra o JAR real na porta 18080 |
| Integração do front-end | Edge headless via CDP, página aberta por `file://` |
| Docker | Não instalado na máquina local; arquivos validados por parser YAML |
| CI | Workflow do GitHub Actions criado; ainda não executado por falta de commit/push |

## 5. Critérios de entrada, saída, suspensão e retomada

- **Entrada:** código das Etapas 1 a 10 disponível; `npm test`, `.\mvnw.cmd test`,
  `.\mvnw.cmd verify` e `.\scripts\test-api.ps1` executáveis; navegador com
  JavaScript habilitado.
- **Saída:** todos os testes automatizados aprovados; coleção Newman sem falhas;
  casos manuais executados sem falhas bloqueantes; JaCoCo gerado; documentação
  atualizada.
- **Suspensão:** falha de ambiente (Node ou Java ausente, navegador indisponível)
  ou defeito bloqueante que impeça o fluxo principal.
- **Retomada:** após corrigir a causa da suspensão e repetir os testes afetados.

## 6. Riscos e mitigação

| Risco | Impacto | Mitigação |
|---|---|---|
| `localStorage` bloqueado no modo `file://` | Perda do carrinho entre páginas | Fallback silencioso e teste com armazenamento indisponível. |
| Ausência de testes de DOM automatizados | Regressões visuais não detectadas por `npm test` | Verificação manual em três larguras e checklist de interface. |
| Divergência entre documentação e código | Rastreabilidade incorreta | Revisão final dos IDs e execução dos testes antes de concluir. |
| Dados corrompidos no armazenamento | Histórico ou carrinho inválidos | Validação e descarte de registros inválidos já implementados e testados. |
| Uso de ponto flutuante para dinheiro | Erros de arredondamento | `BigDecimal` com duas casas no backend e centavos inteiros no front-end. |
| Docker ainda não instalado | Critério de execução via Docker Compose não comprovado | Instalar Docker Desktop/WSL em uma máquina compatível e repetir os testes. |
| CI sem execução no GitHub | Workflow pode falhar no primeiro uso | Manter validação YAML e executar após commit/push autorizado. |
| Repositórios em memória | Dados desaparecem ao reiniciar a API | Substituídos por H2 em arquivo com Flyway; os testes usam H2 em memória e `PersistenciaTest` cobre gravação, leitura e ordenação. |
| API indisponível com a página aberta por `file://` | Menu ou pedido não carregam | Fallback para `js/dados.js` e `localStorage`, com aviso visual e teste headless do fluxo offline. |
| API recusar um pedido válido no front-end | Pedido perdido ou duplicado | Erros HTTP são diferenciados de falha de rede; a recusa mantém o carrinho e não cria pedido local. |
| Divergência entre entidades JPA e schema | Falha na inicialização | `ddl-auto=validate` e migração Flyway versionada. |
| Contrato da API divergir do front-end | Integração quebrada | Preços sempre em centavos, DTOs separados do domínio, `tests/api.test.js` e testes de contrato com MockMvc. |

## 7. Casos de teste

### 7.1 Catálogo e navegação

| ID | Requisito | Técnica | Pré-condições | Passos | Dados | Resultado esperado |
|---|---|---|---|---|---|---|
| CT01 | RF02 | Partição de equivalência (válida) | `menu.html` acessível e JavaScript habilitado. | Abrir `menu.html` e aguardar o carregamento. | Catálogo padrão. | Seis cards com nome, categoria, descrição e preço; preços formatados em reais; nenhum erro no console. |
| CT02 | RF03 | Teste de estado | `menu.html` acessível. | Abrir `menu.html` e observar a tela antes de 450 ms. | Carregamento simulado. | Três esqueletos visíveis, `aria-busy="true"` e anúncio "Carregando bebidas.". |
| CT03 | RF03 | Partição de equivalência (vazia) | `menu.html` acessível. | Abrir `menu.html?estado=vazio`. | Estado vazio. | Mensagem "Nenhuma bebida disponível no momento." e link "Voltar ao início". |
| CT04 | RF03 | Teste de estado + recuperação | `menu.html` acessível. | Abrir `menu.html?estado=erro` e clicar em "Tentar novamente". | Estado de erro. | Mensagem de falha e botão "Tentar novamente"; após clicar, o catálogo normal é carregado. |

### 7.2 Detalhes e quantidade

| ID | Requisito | Técnica | Pré-condições | Passos | Dados | Resultado esperado |
|---|---|---|---|---|---|---|
| CT05 | RF04 | Funcional (caixa-preta) | `menu.html` carregado. | Clicar em "Ver detalhes" do Cappuccino. | Cappuccino, R$ 7,50. | Painel modal abre com categoria, nome, descrição e preço; foco inicial no campo de quantidade. |
| CT06 | RF04, RNF01 | Transição de estado + acessibilidade | Painel de detalhes aberto. | Pressionar `Esc`; repetir abrindo e usando o botão "Fechar". | Teclado e mouse. | Painel fecha e o foco retorna ao botão "Ver detalhes" de origem. |
| CT07 | RF05 | Análise de valor-limite (1 e 10) | Painel de detalhes aberto. | Informar quantidade 1 e depois 10. | Limites 1 e 10. | Ambos os valores são aceitos, sem mensagem de erro, e o total parcial é atualizado. |
| CT08 | RF05 | Valor-limite inferior inválido (0) | Painel de detalhes aberto. | Informar quantidade 0. | Abaixo do mínimo. | Mensagem "A quantidade mínima é 1." e total parcial não calculado. |
| CT09 | RF05 | Valor-limite superior inválido (11) | Painel de detalhes aberto. | Informar quantidade 11. | Acima do máximo. | Mensagem "A quantidade máxima é 10." e total parcial não calculado. |
| CT10 | RF05 | Partição de equivalência (inválidos) | Painel de detalhes aberto. | Informar `2,5` e depois um texto não numérico. | Valor fracionário e não numérico. | Mensagem "Informe uma quantidade inteira." nos dois casos. |
| CT11 | RF06, RNF03 | Partição de equivalência + cálculo | Painel de detalhes aberto. | Selecionar quantidade 3 para o Cappuccino. | 750 centavos × 3. | Total parcial exibe `R$ 22,50`. |

### 7.3 Carrinho

| ID | Requisito | Técnica | Pré-condições | Passos | Dados | Resultado esperado |
|---|---|---|---|---|---|---|
| CT12 | RF07 | Funcional (caixa-preta) | Painel de detalhes aberto. | Selecionar quantidade 3 de Espresso Curto e clicar em "Adicionar ao pedido". | Espresso Curto, R$ 4,00. | Painel fecha e o resumo mostra `3 itens · R$ 12,00`. |
| CT13 | RF07 | Valor-limite (10) + repetição | Carrinho com Espresso Curto. | Adicionar Espresso Curto novamente e testar o limite de 10. | Soma de repetidos. | A quantidade é somada em uma única linha; ao ultrapassar 10, a adição é bloqueada com mensagem. |
| CT14 | RF08 | Funcional (caixa-preta) | Carrinho com item em `pedidos.html`. | Alterar a quantidade do item para 5. | Item de R$ 4,00. | Subtotal e total geral são recalculados imediatamente. |
| CT15 | RF08 | Funcional (caixa-preta) | Carrinho com mais de um item. | Clicar em remover em um dos itens. | Remoção. | A linha é removida e o total geral é recalculado. |
| CT16 | RF09, RNF06 | Teste de persistência | Carrinho com itens. | Navegar para `pedidos.html` e recarregar a página. | Persistência local. | O carrinho permanece; se o armazenamento estiver bloqueado, o fluxo continua na sessão sem erro visível. |

### 7.4 Conclusão, pagamento e histórico

| ID | Requisito | Técnica | Pré-condições | Passos | Dados | Resultado esperado |
|---|---|---|---|---|---|---|
| CT17 | RF10 | Partição de equivalência (vazia) | Carrinho vazio em `pedidos.html`. | Tentar concluir o pedido. | Carrinho vazio. | A conclusão é bloqueada e a tela informa "Seu carrinho está vazio.". |
| CT18 | RF10, RF11 | Tabela de decisão | Carrinho com itens válidos e nenhum pagamento selecionado. | Clicar em concluir o pedido. | Sem pagamento. | A conclusão é bloqueada com "Escolha uma forma de pagamento para continuar." e foco no grupo de pagamento. |
| CT19 | RF11, RF12 | Tabela de decisão | Carrinho com itens válidos. | Selecionar "Dinheiro" e concluir o pedido. | Pagamento em dinheiro. | Confirmação exibida com número, data, itens, total, pagamento "Dinheiro" e status "Recebido"; carrinho é limpo e o pedido é salvo no histórico. |
| CT20 | RF11, RF12, RNF04 | Tabela de decisão | Carrinho com itens válidos. | Selecionar "Cartão fictício" e concluir o pedido. | Cartão fictício. | Confirmação exibida com pagamento "Cartão fictício"; nenhum campo de número, validade ou código de cartão é solicitado. |
| CT21 | RF12 | Persistência + ordenação | Ao menos um pedido concluído. | Recarregar `pedidos.html` e concluir um segundo pedido. | Histórico local. | O histórico persiste após recarregar e o pedido mais recente aparece primeiro. |
| CT22 | RF13 | Transição de estados | Histórico com um pedido. | Alterar o status para "Em preparo" e depois para "Finalizado"; recarregar. | Status do pedido. | A alteração é aplicada imediatamente, persiste após a recarga e o cartão do histórico reflete o novo status. |
| CT23 | RF14 | Tabela de decisão (cancelar) | Histórico com ao menos um pedido. | Abrir o diálogo de limpeza e cancelar. | Cancelamento. | O diálogo fecha e o histórico permanece intacto. |
| CT24 | RF14 | Tabela de decisão (confirmar) | Histórico com ao menos um pedido e carrinho atual com itens. | Confirmar a limpeza do histórico. | Limpeza. | O histórico é esvaziado, o armazenamento correspondente é removido e o carrinho atual é preservado. |

### 7.5 Interface, acessibilidade e responsividade

| ID | Requisito | Técnica | Pré-condições | Passos | Dados | Resultado esperado |
|---|---|---|---|---|---|---|
| CT25 | RF01, RF15 | Inspeção + funcional | Todas as páginas acessíveis. | Abrir `sobre.html` e navegar pelo cabeçalho. | Navegação principal. | A página descreve proposta, funcionalidades, construção e limitações; o link "Sobre" recebe `aria-current="page"`; todos os links internos funcionam. |
| CT26 | RNF01, RNF02, RNF05 | Limites de viewport + acessibilidade | Navegador disponível. | Abrir `index.html`, `menu.html`, `pedidos.html` e `sobre.html` em 390, 768 e 1366 px; navegar por `Tab`, `Shift+Tab` e `Esc`. | Três larguras e teclado. | Não há rolagem horizontal; foco visível; diálogos fecham com `Esc`; páginas funcionam por `file://`. |

### 7.6 Backend Java — domínio (Etapa 7)

Pré-condição geral: `cd backend` e `.\mvnw.cmd test` executável com Java 21+.
Todos os casos desta seção são automatizados com JUnit 5.

| ID | Requisito | Técnica | Passos | Dados | Resultado esperado |
|---|---|---|---|---|---|
| CT27 | RNF03 | Partição de equivalência + valor-limite | Executar `PrecoTest` e `BebidaTest`. | Preço nulo, negativo, com três casas, `4` e `4.00`. | Preço válido é normalizado para duas casas; inválidos são rejeitados com mensagem. |
| CT28 | RF05 | Análise de valor-limite | Executar `RegraQuantidadeTest` e `ItemPedidoTest`. | Quantidades 0, 1, 10 e 11. | 1 e 10 são aceitos; 0 e 11 são rejeitados com mensagem. |
| CT29 | RF06 | Partição de equivalência + cálculo | Executar `ItemPedidoTest` e `PedidoTest`. | Cappuccino R$ 7,50 × 3; Espresso R$ 4,00 × 3 + Café com Leite R$ 5,50 × 2. | Subtotal `22.50` e total `23.00`, em `BigDecimal` com duas casas. |
| CT30 | RF10, RF12 | Funcional + estado inicial | Executar `PedidoTest`. | Pedido com itens e pagamento em dinheiro. | Pedido recebe número iniciado por `PED-`, data, status inicial "Recebido" e itens imutáveis. |
| CT31 | RF02 | Inspeção + partição de equivalência | Executar `CatalogoBebidasTest`. | Seis bebidas e busca por `cappuccino`, ID inexistente e `null`. | Catálogo lista seis bebidas com IDs únicos e preços positivos; busca retorna a bebida correta ou vazio. |
| CT32 | RF13 | Transição de estados | Executar `PedidoTest`. | Status Recebido → Em preparo → Finalizado; status `null`. | Transições válidas atualizam o status; valor nulo é rejeitado sem alterar o pedido. |

### 7.7 API REST (Etapa 8)

Pré-condição geral: `cd backend` e `.\mvnw.cmd verify` executável com Java 21+.
Todos os casos desta seção são automatizados com `@SpringBootTest` e MockMvc.
Os repositórios são reiniciados antes de cada teste para garantir isolamento.

| ID | Requisito | Técnica | Passos | Dados | Resultado esperado |
|---|---|---|---|---|---|
| CT33 | RF16, RNF08 | Teste de contrato HTTP + partição de equivalência | Executar `GET /api/bebidas` e `GET /api/bebidas/{id}`; repetir com ID inexistente. | Catálogo inicial e `cappuccino`. | Lista com seis bebidas em JSON; consulta retorna nome, categoria e `precoCentavos`; ID inexistente retorna `404` com `ProblemDetail`. |
| CT34 | RF16, RNF08 | Teste de contrato HTTP + conflito | Executar `POST /api/bebidas` sem `id` e com `id` informado; repetir com ID já existente. | Nome "Café Gelado"; `id` "cafe-gelado"; `id` "cappuccino". | Criação retorna `201`, `Location` e o ID gerado; ID duplicado retorna `409`. |
| CT35 | RF16, RNF08 | Partição de equivalência (inválidos) | Executar `POST /api/bebidas` com campos vazios, preço zero, ID fora do padrão e nome sem letras. | `""`, `0`, `"Café Gelado"` e `"!!!"`. | Todas as requisições retornam `400` com `ProblemDetail` e o mapa `erros` quando a falha é de Bean Validation. |
| CT36 | RF16, RNF08 | Teste de contrato HTTP + atualização | Executar `PUT` em bebida existente, `PUT` com ID divergente, `PUT`/`DELETE` em ID inexistente e `DELETE` em bebida existente. | Cappuccino e `cha-de-canela`. | Atualização válida retorna `200`; ID divergente retorna `400`; inexistentes retornam `404`; exclusão retorna `204` e o item deixa de aparecer na listagem. |
| CT37 | RF17, RNF03, RNF08 | Teste de contrato HTTP + cálculo | Executar `POST /api/pedidos` com bebidas, quantidades e as duas formas de pagamento. | Cappuccino 750 × 3 + Espresso 400 × 2; Mocha 850 × 1. | Pedido retorna `201`, `Location`, número `PED-…`, status "Recebido", itens com `subtotalCentavos` e `totalCentavos` 3050 ou 850; cartão fictício não solicita dados reais. |
| CT38 | RF17, RF05, RNF08 | Análise de valor-limite + partição de equivalência | Executar `POST /api/pedidos` com quantidade 0 e 11, lista vazia, pagamento inválido e bebida inexistente. | Limites inválidos, `pix` e `bebida-inexistente`. | Quantidades e lista vazia retornam `400` com `erros`; pagamento inválido retorna `400` com detalhe; bebida inexistente retorna `404`. |
| CT39 | RF17, RNF08 | Teste de contrato HTTP + ordenação | Criar dois pedidos e executar `GET /api/pedidos`; depois `GET /api/pedidos/{numero}` e uma busca inexistente. | Dois pedidos e `PED-INEXISTENTE`. | A lista traz o mais recente primeiro; a busca retorna o pedido correto; número inexistente retorna `404`. |
| CT40 | RF16, RF17, RNF08 | Transição de estados + teste de erro | Executar `PATCH /api/pedidos/{numero}/status` com `em-preparo`, `finalizado`, código inválido, corpo vazio e pedido inexistente; enviar JSON malformado. | Códigos válidos, `pronto`, `{}`, `PED-INEXISTENTE` e `{`. | Transições válidas retornam `200`; status inválido ou ausente retorna `400` com `erros`; pedido inexistente retorna `404`; JSON malformado retorna `400`. |

### 7.8 Docker, Newman e CI (Etapa 9)

Pré-condição geral: projeto completo, Java 21+, Node.js 20+ e, para os casos de
Docker, Docker Desktop com Compose v2 e WSL 2 (ou Docker Engine no Linux).
Os casos CT41, CT42 e CT46 dependem de ambiente que não existe na máquina local
usada nesta etapa.

| ID | Requisito | Técnica | Passos | Dados | Resultado esperado |
|---|---|---|---|---|---|
| CT41 | RNF09 | Inspeção + build em container | Conferir `backend/Dockerfile` e `.dockerignore`; em ambiente com Docker, executar `docker build -t cafe-na-mao-api:local ./backend`. | Java 21, Maven Wrapper e `curl`. | Build em duas etapas, `verify` executado no estágio de build, runtime não privilegiado e healthcheck em `/api/bebidas`. |
| CT42 | RNF09 | Integração com Docker Compose | Executar `docker compose config`, `docker compose up --build`, aguardar o healthcheck e rodar os serviços do profile `testes`. | Serviços `api`, `testes-frontend`, `testes-backend` e `testes-api`. | API sobe em `http://localhost:8080`, o healthcheck fica saudável, os testes passam e o Newman acessa `http://api:8080`. |
| CT43 | RNF10 | Teste de contrato via Newman | Executar `.\scripts\test-api.ps1 -Port 18080`. | 27 requisições e 31 asserções da coleção. | Newman termina com 0 falhas e grava `postman/newman-report.json`; a API é encerrada no final. |
| CT44 | RNF10, RNF09 | Repetição + isolamento de porta | Executar novamente com `-SkipBuild` e conferir a porta e os processos ao final. | JAR já gerado. | Mesmo resultado de Newman; nenhum processo Java fica escutando a porta 18080. |
| CT45 | RNF10 | Inspeção + validação YAML | Validar `../.github/workflows/ci.yml` e `docker-compose.yml`; conferir jobs, artefatos e detecção da pasta do projeto. | Workflow e Compose. | YAML válido; jobs de front-end, backend, Newman e Docker; artefatos JaCoCo e Newman configurados. |
| CT46 | RNF10 | CI ponta a ponta | Após commit/push autorizado, executar o workflow no GitHub Actions. | Repositório com o projeto na raiz ou em `CafeNaMao-main/`. | Todos os jobs verdes e artefatos publicados; o caso só é aprovado após execução real. |

### 7.9 Banco e integração front-end/API (Etapa 10)

Pré-condição geral: `.\mvnw.cmd verify` executável e, para os casos de navegador,
API em `http://localhost:8080` e Edge instalado. Os casos CT47 a CT50 são
automatizados com JUnit/H2; CT51 a CT54 foram executados com Edge headless via
CDP, com a página aberta por `file://`.

| ID | Requisito | Técnica | Passos | Dados | Resultado esperado |
|---|---|---|---|---|---|
| CT47 | RF20, RNF11 | Migração + teste de persistência | Iniciar o contexto de teste, deixar o Flyway aplicar `V1` e consultar o catálogo. | Banco H2 em memória vazio. | Tabelas criadas, seis bebidas semeadas na ordem do front-end e nenhuma duplicação ao reiniciar. |
| CT48 | RF20, RNF11 | CRUD + teste de persistência | Criar, atualizar e remover bebidas pelo serviço e reler do repositório. | `cafe-gelado`, `cappuccino` e `cha-de-canela`. | Nome e preço persistem; a remoção reduz a lista; valores continuam com duas casas. |
| CT49 | RF20, RNF11 | Teste de persistência + transição de estados | Criar dois pedidos, alterar o status e listar. | Cappuccino 2 + Espresso 1; `em-preparo`. | Itens, total 1900, status e ordem do mais recente são preservados. |
| CT50 | RF18, RNF12 | Teste de contrato CORS | Executar `OPTIONS` em `/api/bebidas` e `/api/pedidos` com `Origin: null`. | `GET` e `POST`; cabeçalho `content-type`. | `200`, `Access-Control-Allow-Origin: *` e métodos/cabeçalhos permitidos. |
| CT51 | RF18, RNF12 | Integração no navegador | Criar uma bebida só na API, abrir `menu.html` por `file://` e depois desligar a API e recarregar. | Bebida `bebida-da-api` (999 centavos). | Online: 7 cards e aviso "Catálogo carregado da API."; offline: 6 cards e aviso "Modo local...". |
| CT52 | RF19, RNF12 | Integração no navegador | Com a API no ar, montar o carrinho, escolher dinheiro, concluir e alterar o status para `em-preparo`. | Cappuccino 2 + Espresso 1. | Confirmação "registrado na API"; `GET /api/pedidos/{numero}` retorna 1900 e status `em-preparo`; a tela mostra "Em preparo". |
| CT53 | RF19 | Teste de erro + integração | Com a API no ar, montar o carrinho com `bebida-inexistente`, escolher pagamento e concluir. | `bebida-inexistente`. | A confirmação não aparece; o carrinho permanece; o histórico não recebe pedido; mensagem "A API não aceitou o pedido...". |
| CT54 | RF19, RNF12 | Integração no navegador (offline) | Desligar a API, montar o carrinho, escolher cartão fictício e concluir. | Cappuccino 2 + Espresso 1. | Confirmação "registrado neste navegador"; um pedido no histórico local; nenhum dado real de cartão. |

## 8. Rastreabilidade resumida

| Requisito | Casos |
|---|---|
| RF01 | CT25, CT26 |
| RF02 | CT01, CT31 |
| RF03 | CT02, CT03, CT04 |
| RF04 | CT05, CT06 |
| RF05 | CT07, CT08, CT09, CT10, CT28 |
| RF06 | CT11, CT29 |
| RF07 | CT12, CT13 |
| RF08 | CT14, CT15 |
| RF09 | CT16 |
| RF10 | CT17, CT18, CT30 |
| RF11 | CT18, CT19, CT20 |
| RF12 | CT19, CT20, CT21, CT30 |
| RF13 | CT22, CT32 |
| RF14 | CT23, CT24 |
| RF15 | CT25 |
| RF16 | CT33, CT34, CT35, CT36, CT40 |
| RF17 | CT37, CT38, CT39, CT40 |
| RF18 | CT50, CT51 |
| RF19 | CT52, CT53, CT54 |
| RF20 | CT47, CT48, CT49 |
| RNF01 | CT06, CT26 |
| RNF02 | CT26 |
| RNF03 | CT11, CT27, CT29 |
| RNF04 | CT18, CT19, CT20 |
| RNF05 | CT26 |
| RNF06 | CT16 |
| RNF07 | CT27, CT28, CT29, CT30, CT31, CT32, CT33 a CT40, CT47 a CT50 |
| RNF08 | CT33, CT34, CT35, CT36, CT37, CT38, CT39, CT40 |
| RNF09 | CT41, CT42, CT44 |
| RNF10 | CT43, CT44, CT45, CT46 |
| RNF11 | CT47, CT48, CT49 |
| RNF12 | CT50, CT51, CT54 |

A lista completa de requisitos está em `docs/requisitos.md`.

## 9. Registro de evidências

Para cada execução, registrar:

- data e responsável;
- versão do código ou etapa;
- comandos executados (`npm test`, `.\mvnw.cmd test`, `.\mvnw.cmd verify`,
  `.\scripts\test-api.ps1`, validação YAML e script CDP);
- quantidade de testes aprovados, reprovados e ignorados;
- requisições, asserções e falhas do Newman;
- navegador, sistema e larguras usadas na verificação manual e headless;
- resultado por caso (aprovado, reprovado ou bloqueado);
- evidência (captura de tela, saída do terminal ou observação);
- defeitos encontrados e encaminhamento.

Os resultados mais recentes estão em `docs/relatorio-testes.md`.
