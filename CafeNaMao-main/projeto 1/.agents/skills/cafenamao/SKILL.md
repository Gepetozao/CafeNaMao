---
compatibility: opencode
description: Desenvolver e testar o projeto acadêmico Café na Mão, uma
  aplicação de pedidos de bebidas para máquinas de café, com interface
  neobrutalista, backend Java e foco em qualidade de software.
name: cafe-na-mao
---

# Skill: Desenvolvimento do Café na Mão

## Objetivo do projeto

Construir o **Café na Mão**, uma aplicação acadêmica para clientes
consultarem bebidas, realizarem pedidos e simularem o pagamento. O
projeto também deve demonstrar práticas de Garantia de Qualidade de
Software, com plano de testes, casos de teste, testes automatizados,
testes de API e CI.

Trabalhe em etapas pequenas e verificáveis. O usuário está retomando a
programação e precisa entender o código para conseguir explicá-lo e
defendê-lo em uma apresentação.

## Identidade visual

-   Estilo: **neobrutalista**, com formas geométricas fortes, bordas
    visíveis, sombras duras e hierarquia visual clara.
-   Paleta sugerida: carvão `#252525`, creme `#F5F0E8`, laranja
    `#FF6A00` e cinza claro.
-   Priorize legibilidade, contraste, botões evidentes e espaçamento
    consistente.
-   Use a marca textual "Café na Mão". Se o arquivo da logo fornecido
    pelo usuário estiver disponível no repositório, reutilize-o; não
    invente um caminho de arquivo.
-   A interface deve funcionar em desktop e celular. Em telas estreitas,
    reorganize o menu sem causar rolagem horizontal.
-   Não dependa de imagens externas para a interface principal. Use
    placeholders simples quando necessário.

## Stack e arquitetura recomendadas

-   Frontend inicial: HTML semântico, CSS e JavaScript puro, sem
    framework obrigatório.
-   Backend: Java 21 e Spring Boot com Maven, se o ambiente já estiver
    preparado.
-   Persistência: a versão integrada usa H2 em arquivo com migrações Flyway e
    H2 em memória nos testes. Se PostgreSQL for adotado no futuro, preserve as
    migrações e os contratos da API.
-   Testes Java: JUnit 5 e, quando adequado, Spring Boot Test/MockMvc.
-   API: coleção Postman com variáveis de ambiente, assertions e
    execução pelo Newman.
-   Qualidade: JaCoCo para relatório de cobertura e GitHub Actions para
    build e testes.
-   Execução integrada: Docker Compose, com instruções claras e valores
    de configuração de desenvolvimento sem segredos reais.

Antes de escolher dependências ou alterar a arquitetura, examine os
arquivos existentes. Preserve decisões válidas do projeto e evite
reescritas desnecessárias.

## Funcionalidades mínimas

1.  Página inicial com cabeçalho e navegação.
2.  Catálogo de bebidas com nome, descrição e preço.
3.  Detalhes de uma bebida e seleção de quantidade.
4.  Criação de pedido e exibição de resumo/total.
5.  Estados de interface: carregando, lista vazia, sucesso e erro.
6.  Para o escopo acadêmico, o pagamento pode ser apenas simulado. Nunca
    armazene dados reais de cartão.
7.  Se houver backend, disponibilize endpoints REST coerentes para
    listar, consultar, criar, atualizar e excluir bebidas, além de criar
    e consultar pedidos.
8.  Manutenção de máquinas é uma funcionalidade futura; não a implemente
    antes de concluir e testar o fluxo principal de pedidos, salvo
    pedido explícito do usuário.

## Fluxo obrigatório de trabalho

1.  **Inspecionar antes de editar.** Leia a estrutura do projeto,
    README, configurações, testes e arquivos existentes.
2.  **Planejar em partes pequenas.** Resuma o que pretende fazer e
    identifique arquivos que serão criados ou alterados.
3.  **Não inventar contexto.** Se faltar um requisito essencial, faça
    uma pergunta curta. Se for um detalhe visual não essencial, adote
    uma opção simples e informe a decisão.
4.  **Implementar incrementalmente.** Faça uma funcionalidade por vez;
    não gere o projeto inteiro numa única alteração sem verificar os
    resultados.
5.  **Explicar o código.** Ao concluir cada etapa, descreva em português
    simples o que mudou, onde está e como funciona. Explique conceitos
    novos sem presumir experiência avançada.
6.  **Executar verificações reais.** Rode os testes e o build
    apropriados ao que foi alterado. Não declare testes aprovados se não
    foram executados. Se alguma ferramenta estiver ausente, informe
    exatamente o que faltou e como isso afeta a validação.
7.  **Revisar o diff.** Procure erros, arquivos desnecessários,
    credenciais, configurações frágeis, código duplicado e regressões
    antes de finalizar.
8.  **Encerrar com resumo.** Liste arquivos alterados, funcionalidade
    concluída, comandos executados e resultados, limitações e próximo
    passo recomendado.

## Padrões de implementação

-   Use HTML semântico: `header`, `nav`, `main`, `section`, `form` e
    `footer` quando apropriado.
-   Todo link e botão deve ter uma finalidade real. Não use `href="#"`
    como substituto de navegação funcional.
-   Use nomes claros em português para conceitos de domínio e mantenha
    consistência no projeto.
-   Separe estrutura, estilos e comportamento em arquivos diferentes
    quando fizer sentido.
-   Evite estilos inline, dependências desnecessárias e grandes arquivos
    monolíticos.
-   Valide formulários no frontend para experiência do usuário e no
    backend para segurança e integridade.
-   Trate preços com `BigDecimal` no Java; não use `double` para valores
    monetários.
-   Retorne códigos HTTP coerentes e mensagens de erro compreensíveis.
-   Nunca inclua senhas, tokens, chaves de API ou dados pessoais reais
    no repositório.
-   Não simule integração real com máquina física. Identifique
    claramente qualquer comportamento simulado.

## Acessibilidade e responsividade

-   Mantenha contraste legível, foco de teclado visível e navegação por
    teclado.
-   Associe rótulos aos campos de formulário.
-   Use textos acessíveis para ícones e imagens relevantes.
-   Não comunique sucesso ou erro apenas por cor.
-   Teste visualmente em largura de celular e desktop, evitando
    elementos cortados e rolagem horizontal.
-   Respeite `prefers-reduced-motion` caso adicione animações.

## Requisitos de Garantia de Qualidade

Ajude o projeto a cobrir estes entregáveis, sem afirmar que estão
concluídos antes de existirem:

1.  **Plano de teste:** escopo, estratégia, critérios de entrada/saída,
    riscos e mitigação.
2.  **Casos de teste:** pelo menos 15 casos, com ID, requisito
    relacionado, pré-condições, passos, dados e resultado esperado.
3.  **Testes automatizados:** testes unitários e de integração
    apropriados, padrão Arrange-Act-Assert (AAA), casos positivos,
    negativos e limites.
4.  **Testes de API:** coleção Postman para operações principais, com
    `{{base_url}}` e assertions; documentar execução via Newman.
5.  **CI:** GitHub Actions para build, testes e geração de cobertura. O
    pipeline deve falhar quando um comando de validação falhar.
6.  **Relatório e IA:** documentar resultados, limitações e conclusão;
    no README, declarar de forma honesta como a IA foi utilizada e o que
    foi verificado manualmente.
7.  **Apresentação:** preparar uma demonstração curta do fluxo principal
    e explicar arquitetura, testes e limitações.

## Estratégia de testes

-   Priorize comportamento observável, não testes que apenas repetem a
    implementação.
-   Cubra lista vazia, identificador inexistente, dados obrigatórios
    ausentes, preço inválido, quantidade inválida, pedido válido e
    cálculo do total.
-   Teste limites e mensagens de erro.
-   Para integração, valide a interação entre camadas ou endpoints reais
    de teste.
-   Evite depender de serviços externos para testes automatizados.
-   Faça os testes serem reproduzíveis com comandos documentados e,
    quando exigido pelo projeto, por Docker Compose.
-   Nunca reduza ou remova testes apenas para fazer o pipeline ficar
    verde. Corrija a causa real ou explique o bloqueio.

## Segurança ao usar ferramentas

-   Antes de executar comandos destrutivos, apagar arquivos, alterar
    histórico Git ou sobrescrever trabalho existente, explique o impacto
    e peça confirmação.
-   Não execute comandos remotos ou scripts desconhecidos sem
    inspecionar o conteúdo e explicar o risco.
-   Não exponha arquivos `.env` ou segredos na saída.
-   Não faça commit ou push sem solicitação explícita do usuário.

## Formato das respostas ao usuário

Ao concluir uma tarefa, responda em português brasileiro e inclua: - **O
que foi feito** - **Arquivos alterados** - **Como executar ou testar** -
**Resultado das verificações** - **O que o usuário deve aprender ou
observar** - **Próximo passo pequeno e concreto**

Prefira instruções copiáveis e comandos compatíveis com
Windows/PowerShell quando o usuário estiver no Windows. Não despeje
grandes blocos de código sem explicar onde cada arquivo deve ficar.
