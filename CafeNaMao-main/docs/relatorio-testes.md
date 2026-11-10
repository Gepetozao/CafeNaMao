# Relatório de testes — Café na Mão

Última atualização: 09/11/2026

Etapa de referência: Etapas 1 a 10 implementadas. A Etapa 7 criou a fundação do
backend Java, a Etapa 8 expôs o domínio por uma API REST com testes de integração,
a Etapa 9 adicionou Docker Compose, coleção Postman/Newman e workflow de CI e a
Etapa 10 adicionou persistência com H2/Flyway e a integração do front-end com a
API, mantendo o fallback local por `file://`.

## 1. Resumo executivo

| Indicador | Resultado |
|---|---:|
| Testes do front-end (`node:test`) | 61 |
| Testes do backend (JUnit 5) | 86 |
| Total de testes automatizados | 147 |
| Aprovados | 147 |
| Reprovados | 0 |
| Ignorados | 0 |
| Suítes do front-end | 5 arquivos |
| Classes de teste do backend | 15 classes |
| Dependências externas do front-end | 0 |
| Requisições Newman | 27 |
| Asserções Newman | 31 |
| Falhas Newman | 0 |
| Execução do Docker Compose | Não (Docker/WSL ausentes) |
| Execução do workflow no GitHub Actions | Não (sem commit/push) |

Os comandos `npm test`, `.\mvnw.cmd verify` e
`.\scripts\test-api.ps1 -Port 18080` foram executados em 09/11/2026 no ambiente
Windows com Node.js v26.13.0, Java 25 e Newman 6.2.1, e terminaram com todos os
testes e asserções aprovados. Os fluxos online, de recusa da API e offline também
foram exercitados no Edge headless via CDP, com as páginas abertas por `file://`.

## 2. Resultados automatizados

### 2.1 Front-end (`node:test`)

| Suíte | Testes | Foco |
|---|---:|---|
| `tests/dados.test.js` | 10 | Catálogo, unicidade de IDs, dados obrigatórios, preços em centavos e formatação. |
| `tests/detalhes.test.js` | 10 | Validação de quantidade (1 a 10) e cálculo do total parcial. |
| `tests/carrinho.test.js` | 14 | Inclusão, soma, alteração, remoção, total, criação de pedido e persistência. |
| `tests/historico.test.js` | 19 | Registro, validação, persistência, status, limpeza e rótulos. |
| `tests/api.test.js` | 8 | Conversão do pedido da API, URL configurável e distinção entre falha de rede e recusa HTTP. |

### 2.2 Backend (`JUnit 5`)

| Classe de teste | Testes | Foco |
|---|---:|---|
| `BebidaTest` | 6 | Criação e validação dos campos obrigatórios da bebida. |
| `PrecoTest` | 9 | Normalização, rejeição de preço inválido, soma, multiplicação e conversão de/para centavos. |
| `RegraQuantidadeTest` | 3 | Limites 1 e 10, quantidade 0, negativa e 11. |
| `ItemPedidoTest` | 3 | Subtotal do item, bebida nula e quantidade inválida. |
| `PedidoTest` | 10 | Total, status inicial, número, data, itens imutáveis e transições de status. |
| `CatalogoBebidasTest` | 4 | Seis bebidas, IDs únicos, preços e busca por identificador. |
| `StatusPedidoTest` | 3 | Conversão de códigos `recebido`, `em-preparo`, `finalizado`, inválido e nulo. |
| `FormaPagamentoTest` | 3 | Conversão de códigos `dinheiro`, `cartao-ficticio`, inválido e nulo. |
| `SlugTest` | 4 | Geração de identificadores com acentos, espaços, símbolos e entradas inválidas. |
| `BebidaServiceTest` | 1 | Rejeição de preço nulo pela camada de serviço. |
| `BebidaControllerTest` | 14 | CRUD de bebidas, validação, `Location`, `404`, `409` e `ProblemDetail`. |
| `PedidoControllerTest` | 16 | Criação, listagem, busca, status, validação, ordenação e erros HTTP. |
| `PersistenciaTest` | 7 | Migração Flyway, catálogo semeado, CRUD em H2, itens de pedido e ordenação. |
| `CorsTest` | 2 | Preflight de leitura e criação com `Origin: null`. |
| `CafeNaMaoApplicationTests` | 1 | Inicialização do contexto Spring Boot. |

### 2.3 Testes de integração da API

Os testes de integração usam `@SpringBootTest` com `@AutoConfigureMockMvc`, sem
abrir porta de rede, com H2 em memória e o Flyway aplicando o schema. O perfil de
teste usa um banco em memória isolado, garantindo que cada execução comece limpa.

Casos cobertos:

- listagem e busca de bebidas;
- criação de bebida com identificador gerado, identificador informado e conflito;
- validação de campos obrigatórios, preço, formato do identificador e nome sem
  letras;
- atualização, atualização com identificador divergente, exclusão e `404`;
- criação de pedido com as duas formas de pagamento;
- cálculo de subtotal e total em centavos;
- validação de quantidade 0 e 11, lista vazia, pagamento inválido e bebida
  inexistente;
- listagem do pedido mais recente para o mais antigo;
- busca por número e `404`;
- transições de status, status inválido, status ausente e pedido inexistente;
- JSON malformado;
- respostas de erro no formato `ProblemDetail`;
- migração Flyway e catálogo inicial semeado sem duplicação;
- persistência de bebidas, pedidos, itens e status no H2;
- preflight CORS com `Origin: null` para `GET` e `POST`.

### 2.4 Cobertura JaCoCo do backend

Relatório gerado em `backend/target/site/jacoco/index.html` pelo comando
`.\mvnw.cmd verify`:

| Métrica | Cobertura |
|---|---:|
| Linhas | 97,5% (434 de 445) |
| Instruções | 96,5% (1747 de 1810) |
| Branches | 91,0% (71 de 78) |

A cobertura caiu em relação à Etapa 9 porque a Etapa 10 adicionou as entidades,
os mapeadores, os adaptadores JPA, o semeador e a configuração de CORS, que são
classes de infraestrutura com trechos não exercitados diretamente, como o método
`main` de `CafeNaMaoApplication` e ramificações defensivas dos mapeadores.

### 2.5 Cobertura automatizada do front-end

- Quantidade mínima de bebidas e unicidade dos identificadores.
- Presença de dados obrigatórios em todas as bebidas.
- Preços inteiros e positivos em centavos.
- Formatação de moeda brasileira, incluindo zero e separador de milhar.
- Rejeição de preço negativo, fracionário e não numérico.
- Aceitação de quantidades entre 1 e 10.
- Rejeição de quantidade abaixo do mínimo, acima do máximo, fracionária ou não
  numérica.
- Cálculo do total em centavos nos limites mínimo e máximo.
- Carrinho vazio com total zero.
- Total do carrinho com bebidas diferentes e soma de itens repetidos.
- Alteração de quantidade, remoção de itens e remoção de item inexistente.
- Bloqueio de pedido com carrinho vazio.
- Criação de pedido com número, data, cópia dos itens e total.
- Imutabilidade do pedido após alterações no carrinho.
- Persistência do carrinho e comportamento com armazenamento indisponível.
- Criação de registro com pagamento e status inicial.
- Rejeição de pagamento, status, itens e total inválidos no histórico.
- Leitura de histórico vazio, ausente ou com JSON inválido.
- Gravação, ordenação do mais recente e substituição de número repetido.
- Alteração de status existente e rejeição de status ou pedido inválido.
- Filtragem de registros corrompidos e números duplicados.
- Limpeza do histórico e comportamento com armazenamento indisponível.
- Rótulos em português para status e formas de pagamento.
- Conversão do pedido devolvido pela API para o formato do histórico local.
- Rejeição de pedido ou item inválido devolvido pela API.
- URL padrão, URL configurável e remoção da barra final.
- Catálogo recebido da API e rejeição de resposta inválida.
- Distinção entre falha de rede (`semRede`) e recusa HTTP (`status`).

### 2.6 Coleção Postman/Newman (Etapa 9)

A coleção foi executada contra o JAR real em `http://localhost:18080`, com o
script `scripts/test-api.ps1`:

| Indicador | Resultado |
|---|---:|
| Iterações | 1 |
| Requisições | 27 |
| Scripts de teste | 27 |
| Asserções | 31 |
| Falhas | 0 |
| Duração total | 2,5 s |
| Tempo médio de resposta | 6 ms (mín. 2 ms, máx. 62 ms) |

Cobertura da coleção:

- listagem, busca, criação, atualização, conflito e exclusão de bebidas;
- criação e consulta de pedidos em dinheiro e cartão fictício;
- cálculo de subtotais e total em centavos;
- alteração de status para `em-preparo` e `finalizado`;
- validação de campos, quantidade 0 e 11, lista vazia e pagamento inválido;
- `404` para bebida, pedido e status inexistentes;
- JSON malformado e respostas `ProblemDetail`;
- criação e remoção de uma bebida temporária com ID único por execução.

O relatório JSON fica em `postman/newman-report.json`. Com a persistência em H2, os
pedidos criados pela coleção permanecem no banco de desenvolvimento; isso não
afeta as asserções, que consultam os pedidos criados na própria execução. A
bebida temporária continua sendo removida ao final.

### 2.7 Integração front-end/API (Etapa 10)

Os fluxos de navegador foram executados em Edge headless controlado por CDP, com
as páginas abertas por `file://` e a API em `http://localhost:8080`. Os scripts
foram temporários e não fazem parte do repositório.

| Cenário | Resultado |
|---|---|
| Menu online | 7 cards renderizados, incluindo a bebida `bebida-da-api` criada apenas na API; aviso "Catálogo carregado da API.". |
| Menu offline | 6 cards do catálogo local; aviso "Modo local: API indisponível...". |
| Pedido online | Carrinho Cappuccino 2 + Espresso 1, total `R$ 19,00`; confirmação "registrado na API"; `GET /api/pedidos/{numero}` retornou 1900 centavos e status `em-preparo` após a troca. |
| Recusa da API | Bebida `bebida-inexistente`; confirmação não apareceu; carrinho permaneceu com 1 item; histórico local ficou vazio; mensagem com o `detail` da API. |
| Pedido offline | Confirmação "registrado neste navegador"; 1 pedido salvo no histórico local; nenhum dado real de cartão. |
| CORS | Preflight `OPTIONS` com `Origin: null` respondeu `200` e `Access-Control-Allow-Origin: *`. |
| Persistência entre reinícios | Bebida e pedido criados na API continuaram disponíveis após reiniciar o processo (`PERSISTENCIA OK`). |

## 3. Verificação manual de interface

A verificação de interface registrada na Etapa 5 foi executada com Microsoft Edge
em modo headless, controlado por CDP, nas larguras de 390, 768 e 1366 px. O
script auxiliar foi temporário e não foi adicionado ao repositório.

| Cenário | Resultado |
|---|---|
| `menu.html` em 390/768/1366 px | Sem rolagem horizontal. |
| Detalhes com quantidade 3 | Total parcial `R$ 12,00`; foco inicial no campo. |
| Adicionar ao pedido | Painel fecha e resumo mostra `3 itens · R$ 12,00`. |
| Duas bebidas no carrinho | `5 itens · R$ 23,00`. |
| Adicionar a mesma bebida novamente | Quantidade somada: `6 itens · R$ 27,00` e duas linhas no armazenamento. |
| `pedidos.html` em 390/768/1366 px | Sem rolagem horizontal; total inicial `R$ 27,00`. |
| Alterar quantidade para 5 | Subtotal `R$ 20,00` e total `R$ 31,00`. |
| Quantidade 11 | Mensagem "A quantidade máxima é 10." e total inalterado. |
| Remover item | Total recalculado para `R$ 20,00` e uma linha restante. |
| Concluir pedido | Confirmação visível, número `PED-…`, total `R$ 20,00` e foco na confirmação. |
| Após concluir | Carrinho salvo limpo; recarregar mostra "Seu carrinho está vazio.". |
| Concluir sem pagamento | Bloqueia e mantém o carrinho visível. |
| Concluir com quantidade 11 | Bloqueia a conclusão e move o foco para o campo. |
| Concluir com pagamento escolhido | Confirmação exibida e pedido salvo no histórico. |
| Após recarregar com histórico | Pedido persiste e carrinho fica vazio. |
| Alterar status | Persiste no histórico e atualiza o cartão. |
| Cancelar limpeza | Diálogo fecha sem apagar o histórico. |
| Limpar histórico | Diálogo confirma, remove o armazenamento e exibe histórico vazio. |
| Teclado | `Esc` fecha o painel e devolve o foco ao botão de origem. |
| Console | Nenhum erro de JavaScript durante o fluxo. |
| Confirmação em 390/1366 px | Sem rolagem horizontal. |
| Histórico em 390/768/1366 px | Sem rolagem horizontal. |

## 4. Verificações das Etapas 6 a 10

Em 09/11/2026 foram executadas as seguintes verificações:

- `npm test` com 61 testes aprovados e nenhuma falha;
- `.\mvnw.cmd verify` com 86 testes JUnit aprovados e nenhuma falha;
- testes de integração com MockMvc cobrindo os endpoints de bebidas e pedidos;
- `PersistenciaTest` cobrindo a migração Flyway, o catálogo semeado e o CRUD em H2;
- `CorsTest` cobrindo o preflight com `Origin: null`;
- verificação com o JAR real em uma porta isolada: a listagem retornou 6 bebidas,
  um pedido foi criado com total de 2250 centavos e uma busca inexistente
  retornou HTTP 404;
- persistência entre reinícios da API, confirmando que bebidas e pedidos
  permanecem no H2 em arquivo;
- integração do menu e dos pedidos com a API via Edge headless/CDP nos cenários
  online, recusa e offline;
- coleção Postman executada com Newman contra o JAR real: 27 requisições, 31
  asserções e 0 falhas;
- segunda execução do script com `-SkipBuild`, confirmando o mesmo resultado e o
  encerramento da API ao final;
- validação de sintaxe de `../.github/workflows/ci.yml` e `docker-compose.yml`
  com parser YAML;
- conferência de que os IDs de requisitos e casos de teste citados existem e são
  consistentes entre `docs/requisitos.md` e `docs/plano-de-testes.md`;
- conferência de links internos, ausência de `href="#"`, ausência de estilo
  inline e ausência de IDs duplicados;
- abertura de `sobre.html` por `file://` em navegador headless com captura de tela
  nas larguras de 390, 768 e 1366 px, sem rolagem horizontal.

Não foram executados:

- `docker compose build/up` nem os serviços de teste em container, porque Docker
  Desktop, Docker Compose e WSL não estão instalados na máquina;
- o workflow do GitHub Actions, porque o repositório ainda não tem commits/push.

## 5. Defeitos e limitações conhecidas

Nenhum defeito bloqueante foi identificado nas Etapas 1 a 10.

Limitações atuais:

- o front-end cai para `localStorage` quando a API não está acessível, então
  pedidos feitos offline não são sincronizados automaticamente depois;
- o histórico local não é preenchido com todos os pedidos já existentes no
  servidor: apenas os pedidos criados ou alterados durante a sessão são
  espelhados;
- o banco de desenvolvimento é H2 em arquivo; não há PostgreSQL em produção;
- Docker Desktop, Docker Compose e WSL não estão instalados, então o Compose e o
  `Dockerfile` não foram executados localmente;
- o workflow de CI existe, mas ainda não teve uma execução real no GitHub Actions
  porque o repositório não tem commits/push;
- não há testes de DOM automatizados no repositório; a interface depende de
  verificação manual e dos scripts CDP temporários;
- em navegadores que bloqueiam o armazenamento para `file://`, o carrinho pode
  não sobreviver à navegação entre páginas;
- o status é alterado manualmente, sem progressão automática;
- o pagamento é apenas simulado e nenhum dado real de cartão é coletado.

## 6. Cobertura de requisitos e lacunas

| Requisito | Cobertura | Lacuna |
|---|---|---|
| RF01–RF02 | Automatizada e manual | Nenhuma relevante. |
| RF03–RF04 | Manual | Sem teste de DOM automatizado. |
| RF05–RF09 | Automatizada | Sem teste de DOM automatizado para a interface. |
| RF10–RF14 | Automatizada e manual | Front-end integrado à API, com fallback local verificado por CDP. |
| RF15 | Manual | Sem teste de DOM automatizado. |
| RF16–RF17 | Automatizada com MockMvc | Sem PostgreSQL e sem teste de carga. |
| RF18–RF19 | Automatizada (Node) e CDP | Sem teste de DOM automatizado no repositório. |
| RF20 | Automatizada com H2/Flyway | Sem banco de produção nem migração de dados legados. |
| RNF01–RNF02 | Manual | Sem verificação automatizada de acessibilidade ou layout. |
| RNF03–RNF06 | Automatizada e manual | Nenhuma relevante. |
| RNF07 | Automatizada | Nenhuma relevante. |
| RNF08 | Automatizada | Sem teste de carga ou segurança, fora do escopo atual. |
| RNF09 | Inspeção e validação YAML | Docker Compose não executado localmente por ausência de Docker/WSL. |
| RNF10 | Newman executado; CI validado por YAML | Workflow ainda não executado no GitHub Actions. |
| RNF11 | Automatizada com H2/Flyway | Banco de produção não definido. |
| RNF12 | Automatizada (CORS/Node) e CDP | Scripts CDP são temporários e não versionados. |

## 7. Próximos passos de automação

1. Executar `docker compose build` e os serviços do profile `testes` em uma
   máquina com Docker Desktop/WSL, repetindo os casos CT41 e CT42.
2. Autorizar commit/push e executar o workflow no GitHub Actions para validar o
   caso CT46 e publicar os artefatos reais.
3. Definir se haverá sincronização do histórico local com o servidor depois de
   um período offline e, se aprovado, implementá-la com testes.
4. Avaliar testes de DOM versionados, sem quebrar a execução por `file://` nem
   adicionar dependências sem autorização.
5. Repetir a verificação manual antes da apresentação final.

## 8. Conclusão

As Etapas 1 a 10 estão concluídas: o front-end mantém 61 testes aprovados, o
backend soma 86 testes JUnit aprovados (incluindo integração com MockMvc, H2 e
CORS) e o relatório JaCoCo mostra 97,5% de cobertura de linhas, 96,5% de
instruções e 91,0% de branches. A coleção Postman executa 27 requisições e 31
asserções via Newman, sem falhas.

A API REST cobre o catálogo de bebidas, a criação e consulta de pedidos e a
alteração de status, com validação, códigos HTTP coerentes e erros no formato
`ProblemDetail`. Os dados são persistidos em H2 com schema versionado por Flyway.

O front-end continua funcionando por `file://`: quando a API está disponível,
carrega o catálogo, cria pedidos e altera status no servidor; quando não está,
usa o catálogo e o histórico locais e informa o modo local. Os três cenários
(online, recusa e offline) foram exercitados no navegador.

Os artefatos de Docker Compose e CI foram criados e validados por sintaxe, mas
ainda não foram executados em ambiente real: Docker/WSL não estão instalados e o
repositório não tem commits/push. Esses itens não devem ser considerados
aprovados antes de uma execução real.
