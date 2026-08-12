# Product

## Register

product

## Platform

web

## Users
Cliente final pedindo crédito ou serviço, preenchendo dados financeiros sensíveis (CPF, renda, endereço, documentos) em várias etapas, geralmente em um único momento de sessão, em qualquer dispositivo (mobile e desktop). Não é um usuário técnico — precisa entender o que está sendo pedido e por quê, e confiar que o processo tem começo, meio e fim rastreável.

## Product Purpose
Portal onde o cliente faz login, se cadastra, completa o onboarding por etapas (retomável a qualquer momento) e cria/acompanha solicitações reaproveitando o perfil já preenchido. Sucesso é o cliente completar o cadastro e a solicitação sem precisar de suporte, e conseguir voltar depois pra ver em que pé está.

## Positioning
Um processo burocrático (pedir crédito/serviço) explicado e conduzido em linguagem de gente, sem fingir que não é sério — cada etapa tem um lugar visível e um número que prova que aquilo está sendo processado de verdade.

## Brand Personality
Claro, humano, sem burocracia. Sério no conteúdo (dinheiro, documentos, CPF), nunca solene no tom. Explica o que vai acontecer antes de pedir o próximo dado; nunca usa jargão de sistema ("payload", "token expirado") na frente do cliente.

## Anti-references
Não pode parecer internet banking datado (denso, cinza, ícones anos 2010, sem hierarquia). Também não pode parecer app cripto/fintech hyped (dark mode dramático, neon, sensação de especulação/trading). E não pode cair no gradiente-roxo genérico de "feito por IA".

## Design Principles
Estrutura antes de decoração — grid disciplinado carrega a seriedade, não sombra/gradiente. Prova sempre visível — status e número de referência da solicitação nunca ficam escondidos atrás de um clique. Confiança por clareza, não por selo — cor de ação (azul) reservada pro que é de fato interativo, laranja só onde há ação pendente real do cliente. Um risco estético por vez — o resto do sistema fica quieto ao redor dele.

## Accessibility & Inclusion
Meta WCAG AAA onde viável (contraste, foco visível, hierarquia tipográfica), AA como piso inegociável. `prefers-reduced-motion` sempre respeitado. Erros de formulário anunciados via `role="alert"`/`aria-live`, nunca só cor. Navegação completa por teclado (formulários multi-etapa não podem prender foco).
