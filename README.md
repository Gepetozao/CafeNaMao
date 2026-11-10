# Café na Mão

Aplicação acadêmica de pedidos de bebidas para uma máquina de café. O cliente
consulta o catálogo, monta o carrinho, escolhe uma forma de pagamento
**simulada** e acompanha o histórico. O projeto também demonstra práticas de
Garantia de Qualidade de Software.

## Conteúdo do repositório

- `CafeNaMao-main/` — front-end, backend, testes, coleção Postman e documentação.
- `CafeNaMao-main/README.md` — instruções completas de execução.
- `CafeNaMao-main/docs/plano-de-testes.md` — plano e casos `CT01` a `CT54`.
- `CafeNaMao-main/docs/relatorio-testes.md` — resultados, cobertura e limitações.
- `CafeNaMao-main/docs/requisitos.md` — requisitos funcionais e não funcionais.
- `.github/workflows/ci.yml` — pipeline de CI.

## Resultados atuais

- **61** testes de front-end (`node:test`).
- **86** testes de backend (JUnit 5).
- **147** testes automatizados, com 0 falhas.
- Cobertura JaCoCo: **97,5%** de linhas, **96,5%** de instruções e **91,0%** de branches.
- Newman: **27 requisições**, **31 asserções**, **0 falhas**.

## Pagamento simulado

Nenhum dado real de cartão é solicitado ou armazenado.

## Uso de inteligência artificial

Este projeto foi desenvolvido com apoio de ferramentas de IA para geração e
revisão de código e documentação. Todo o resultado foi lido e ajustado ao longo
do processo. As verificações automatizadas foram executadas localmente com
`npm test`, `.\mvnw.cmd verify` e `.\scripts\test-api.ps1` (Newman), e a
interface foi conferida manualmente no navegador, incluindo navegação por
teclado e larguras de celular, tablet e desktop.

A IA não foi usada como substituta dos testes: nenhum resultado é declarado como
aprovado sem execução real, e as limitações conhecidas estão documentadas no
README completo, em `docs/relatorio-testes.md` e em `progresso.md`.

## Limitações conhecidas

- Docker Compose e GitHub Actions ainda não tiveram execução local/verde nesta
  máquina.
- O banco de desenvolvimento é H2 em arquivo; não há PostgreSQL de produção.
- Pedidos feitos offline não são sincronizados automaticamente.
- O pagamento é apenas simulado.

## Como executar

Consulte `CafeNaMao-main/README.md`.
