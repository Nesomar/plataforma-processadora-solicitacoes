---
name: Portal do Cliente
description: Portal bancário de onboarding e solicitações — caderneta financeira de abas coloridas, cor por etapa, protocolo carimbado.
colors:
  acao: "#2447c9"
  acao-hover: "#1c39a3"
  tab-dados: "#4338ca"
  tab-dados-tint: "#e3e1fb"
  tab-dados-ink: "#362f9e"
  tab-endereco: "#047857"
  tab-endereco-tint: "#d7f2e6"
  tab-endereco-ink: "#036348"
  tab-renda: "#92400e"
  tab-renda-tint: "#faecd2"
  tab-renda-ink: "#7a3609"
  tab-anexos: "#9f1239"
  tab-anexos-tint: "#fbdfe6"
  tab-anexos-ink: "#870f30"
  papel: "#fffefb"
  fundo: "#f5f1e6"
  tinta: "#201b13"
  linha: "#ddd2b8"
  linha-forte: "#b7a67c"
  muted-tinta: "#6a5d44"
  muted-bg: "#efe8d5"
  erro: "#b91c1c"
  erro-bg: "#fef2f2"
  sucesso: "#15803d"
typography:
  headline:
    fontFamily: "IBM Plex Serif, Georgia, 'Times New Roman', serif"
    fontSize: "28px"
    fontWeight: 700
    lineHeight: 1.2
    letterSpacing: "-0.1px"
  title:
    fontFamily: "IBM Plex Serif, Georgia, 'Times New Roman', serif"
    fontSize: "20px"
    fontWeight: 600
    lineHeight: 1.2
    letterSpacing: "0"
  brand:
    fontFamily: "IBM Plex Serif, Georgia, 'Times New Roman', serif"
    fontSize: "17px"
    fontWeight: 600
    lineHeight: 1.3
    letterSpacing: "0"
  body:
    fontFamily: "Source Sans 3, system-ui, 'Segoe UI', Roboto, sans-serif"
    fontSize: "16px"
    fontWeight: 400
    lineHeight: 1.5
    letterSpacing: "normal"
  emphasis:
    fontFamily: "Source Sans 3, system-ui, 'Segoe UI', Roboto, sans-serif"
    fontSize: "15px"
    fontWeight: 600
    lineHeight: 1.4
    letterSpacing: "normal"
  label:
    fontFamily: "Source Sans 3, system-ui, 'Segoe UI', Roboto, sans-serif"
    fontSize: "14px"
    fontWeight: 500
    lineHeight: 1.4
    letterSpacing: "normal"
  caption:
    fontFamily: "Source Sans 3, system-ui, 'Segoe UI', Roboto, sans-serif"
    fontSize: "13px"
    fontWeight: 400
    lineHeight: 1.35
    letterSpacing: "normal"
  micro:
    fontFamily: "IBM Plex Mono, ui-monospace, Consolas, monospace"
    fontSize: "12px"
    fontWeight: 500
    lineHeight: 1.3
    letterSpacing: "0.03em"
rounded:
  container: "2px"
  control: "8px"
  tab: "4px 4px 2px 2px"
  pill: "999px"
spacing:
  sm: "8px"
  md: "16px"
  lg: "20px"
  xl: "32px"
components:
  button-primary:
    backgroundColor: "{colors.acao}"
    textColor: "#ffffff"
    rounded: "{rounded.control}"
    padding: "11px 20px"
  button-primary-hover:
    backgroundColor: "{colors.acao-hover}"
  button-secondary:
    backgroundColor: "transparent"
    textColor: "{colors.tinta}"
    rounded: "{rounded.control}"
    padding: "11px 20px"
  input:
    backgroundColor: "{colors.papel}"
    textColor: "{colors.tinta}"
    rounded: "{rounded.control}"
    padding: "10px 12px"
  card:
    backgroundColor: "{colors.papel}"
    rounded: "{rounded.container}"
    padding: "32px"
  status-badge:
    backgroundColor: "{colors.muted-bg}"
    textColor: "{colors.muted-tinta}"
    rounded: "{rounded.pill}"
    padding: "3px 10px"
---

# Design System: Portal do Cliente

## Overview

**Creative North Star: "A Caderneta"**

Redesign completo (2026-08-15) — substitui o sistema Swiss-minimalista anterior ("O
Comprovante", arquivado abaixo em histórico) por pedido explícito do usuário por "mais
cores, mais interativo". Mantém o produto (onboarding por etapas retomável, solicitações
rastreáveis) e a seriedade do domínio (CPF, renda, documentos), mas troca a metáfora
organizadora: o portal agora se parece com uma caderneta financeira física — cada etapa
do dossiê e cada seção do sistema tem sua própria tinta, como abas coloridas num livro-
razão ou numa caderneta de poupança. O protocolo de solicitação, que antes vivia só num
chip neutro, agora é carimbado — um retângulo de tinta rotacionado, com anel duplo, que
"bate" na tela com uma animação de carimbo.

Direção sorteada via `concept-seed.mjs --scope direction --mode operate` (seed
`fd9f56be`, candidato 6 da lista própria) e pesada contra 3 challengers do catálogo
(Metro Tiles, Drum Machine Step Row, Cassette J-Card) — venceu em identificação de
audiência (caderneta/talão é cultura financeira brasileira reconhecível) e em clareza de
produto (é a única direção que cobre os 3 capítulos do portal com uma gramática coerente:
aba = etapa, cor = status).

Rejeitado explicitamente (herdado do sistema anterior, ainda vale): visual de internet
banking datado; estética cripto/fintech hyped (dark mode dramático, neon); gradiente-roxo
genérico de interface gerada por IA.

**Key Characteristics:**
- 4 tintas nomeadas, uma por etapa do onboarding — reaproveitadas em status/badges em
  todo o resto do sistema, fechando o círculo entre onboarding e painel.
- Protocolo carimbado — chip com borda dupla, leve rotação, animação de "batida" na
  entrada (`@keyframes carimbo`).
- Papel pautado — textura de linhas horizontais muito sutil (`rgb(150 124 74 / 12%)`) no
  fundo da página inteira, nunca dentro do card (evita colidir com conteúdo).
- Zero sombra segue valendo — profundidade só por borda/contraste/cor, nunca blur.
- Motion como gesto físico, não decoração: cada interação cita um gesto do mundo
  ledger — carimbo bate, aba pulsa ao ficar ativa, lançamento da lista desliza ao passar
  o mouse. Não é uma entrada idêntica repetida — cada componente tem seu próprio gesto.

## Colors

Full palette (4 tintas nomeadas + 1 ação): cada tinta de etapa é uma cor completa (não um
acento raro) — solid/tint/ink por tinta, igual ao sistema anterior fazia só com azul.
Ainda restrito no sentido de que cada cor tem um dono fixo (uma etapa), nunca é escolha
estética solta.

### Ação
- **Acao** (`#2447c9`, hover `#1c39a3`): todo elemento interativo primário — botões
  `.button--primary`, link, foco de input. Deliberadamente próxima da família azul-índigo
  do sistema anterior (continuidade de marca), mas distinta o bastante de `tab-dados` pra
  nunca ser confundida com um chip de etapa.

### Abas por etapa (a assinatura do sistema)
- **Tab-dados** (`#4338ca` / tint `#e3e1fb` / ink `#362f9e`): Dados pessoais.
- **Tab-endereco** (`#047857` / tint `#d7f2e6` / ink `#036348`): Endereço.
- **Tab-renda** (`#92400e` / tint `#faecd2` / ink `#7a3609`): Renda. Reaproveitada hoje
  como cor do único status real do backend (`ABERTA` → "em andamento" lê melhor em âmbar
  que em azul neutro).
- **Tab-anexos** (`#9f1239` / tint `#fbdfe6` / ink `#870f30`): Anexos.

Ordem fixa — nunca reordenar as 4 tintas; `.progress-step:nth-child(n)` no CSS depende da
ordem do array `ETAPAS` em `OnboardingWizard.tsx` bater com a ordem aqui.

### Neutral (papel pautado)
- **Tinta** (`#201b13`): texto principal, títulos.
- **Fundo** (`#f5f1e6`): fundo de página — aveia quente, não cinza frio.
- **Papel** (`#fffefb`): superfície de card, sem sombra.
- **Linha** (`#ddd2b8`): borda padrão — cards, inputs, divisores de lista.
- **Linha-forte** (`#b7a67c`): divisores tracejados do `detalhe-grid` (mais presente que
  `--linha`, textura de "linha de lançamento" de livro-razão).
- **Muted-tinta** (`#6a5d44`): texto secundário/rótulo.
- **Muted-bg** (`#efe8d5`): fundo de chip neutro (status sem tinta própria).
- **Erro** (`#b91c1c`) / **Erro-bg** (`#fef2f2`): mantidos do sistema anterior, valores
  inalterados — deliberadamente não reaproveitam nenhuma tab-color, pra erro nunca se
  confundir com "estou na etapa Anexos" (carmim) num alerta dentro daquele passo.
- **Sucesso** (`#15803d`): `.confirm`, inalterado do sistema anterior.

### Named Rules
**The Tab-Owns-a-Step Rule.** Uma tab-color é always tied a uma etapa nomeada real, nunca
escolhida por preferência estética solta. Ao adicionar uma 5ª etapa, uma 5ª tinta precisa
ser definida com o mesmo trio solid/tint/ink e checada em contraste antes de entrar.

**The AA-Is-The-Floor Rule.** (herdada) Qualquer tom "muted" ou "de alerta" precisa bater
4.5:1 contra o fundo real onde aparece. Todos os pares tab-ink/tab-tint e muted-tinta/papel
|fundo|muted-bg foram verificados nesta implementação (6.15–8.02:1). Reverifique se
qualquer tint/fundo mudar de valor.

**The Stamp-Not-Chip Rule.** `.protocolo` não é mais um chip neutro — é um carimbo (borda
dupla, leve rotação, animação de entrada). Nunca reduzir de volta a texto solto: é a prova
de que a solicitação existe, tem que parecer "batida", não impressa.

## Typography

**Display Font:** IBM Plex Serif (fallback: Georgia, Times New Roman, serif)
**Body Font:** Source Sans 3 (fallback: system-ui, Segoe UI, Roboto, sans-serif)
**Label/Mono Font:** IBM Plex Mono

**Character:** IBM Plex Serif substitui Lexend — família do mesmo desenhista da Plex Mono
já usada em protocolo/CPF, dando ao título peso de registro/livro-razão sem introduzir uma
terceira família não relacionada. Usado só em `h1`/`h2`/`.topbar__brand`, nunca em
parágrafo. Source Sans 3 segue carregando corpo/label/botão — legibilidade AA/AAA em
formulário financeiro não é negociável. IBM Plex Mono segue restrita a valores que provam
algo (protocolo, CPF, data, renda, status) — nunca texto corrido.

### Hierarchy
- **Headline** (700, 28px, 1.2): `h1`.
- **Title** (600, 20px, 1.2): `h2`.
- **Brand** (600, 17px, 1.3): `.topbar__brand` — subiu de 16 pra 17px nesta implementação,
  serif precisa de um pelo mais de tamanho que sans pra manter presença equivalente.
- **Body** (400, 16px, 1.5): formulário, parágrafo, item de lista.
- **Emphasis** (600, 15px, 1.4): `.button`, `.detalhe-grid__row`.
- **Label** (500, 14px): `.field__label`, `.alert`, `.confirm`, `.entrada__footer`.
- **Caption** (400, 13px): `.field__hint`.
- **Micro** (500, 12px, tracking 0.03em): `.progress-step__label`, `.status-badge` — agora
  com `text-transform: uppercase` e tracking (antes era case normal sem tracking).
- **Mono/Data**: `.mono`, `.protocolo`, `.detalhe-grid__value.mono`, `.input--mono`.

### Named Rules
**The Never-Running-Text Rule.** (herdada, inalterada) IBM Plex Mono nunca em frase
corrida ou label — só em valor isolado.

## Layout

Inalterado do sistema anterior — 3 shells de largura fixa por capítulo
(`.shell--entrada` 380px / `.shell--dossie` 560px / `.shell--painel` 800px). O redesign é
inteiramente de superfície (cor, tipo, textura, motion); topologia e composição não
mudaram.

## Elevation & Depth

Ainda flat — nenhum `box-shadow` com blur em nenhum componente. Uma exceção deliberada e
nomeada: `.protocolo` usa `box-shadow` de anel duplo com **zero blur** (`0 0 0 2px`, `0 0
0 1px`) pra simular a borda de um carimbo — isso não é elevação, é um device gráfico (ver
Stamp-Not-Chip Rule); não abre precedente pra sombra com blur em outro componente.

### Named Rules
**The No-Blur-Shadow Rule** (renomeada de "No-Shadow" pra ser precisa: zero-blur rings
são permitidos como device gráfico nomeado — carimbo — nunca como elevação; blur
continua banido em todo componente).

## Shapes

- **Container** (2px): `.card`, `.shell`.
- **Control** (8px): `.input`, `.button`.
- **Tab** (`4px 4px 2px 2px`): topo arredondado, base quase reta — `.progress-step__bar` e
  a aba de lançamento em `.lista-solicitacoes__link::before`. Nova forma nesta
  implementação: simula uma aba de fichário/caderneta saindo da borda.
- **Pill** (999px): `.status-badge` — trocou de 6px pra pílula completa nesta
  implementação, lendo mais como "selo/carimbo de status" que como chip retangular.

## Components

### Buttons
Inalterado na forma (8px raio, `.button--primary`/`--secondary`/`--link`); cor de base
trocou de `--azul-acao` pra `--acao`. Novo: `:active` aplica `translateY(1px)` — feedback
de pressão, ausente no sistema anterior.

### Cards / Containers
- **Corner Style:** 2px, inalterado.
- **Lombada colorida** (`.shell--entrada .card::before`): faixa de 5px no topo do card de
  Entrada, gradiente sólido nas 4 tintas do sistema em blocos de 25% cada — não é um
  gradiente suave (banido — "gradiente genérico de IA"), é 4 blocos de cor sólida lado a
  lado, como a lombada de um fichário revelando as divisórias internas.
- **Aba do card ativo** (`.card[data-etapa="..."]`): no dossiê, o card do passo atual
  ganha `border-top: 3px solid` na tinta daquela etapa — o card "veste" a cor da aba que
  está aberta.

### Inputs / Fields
Inalterado na forma; halo de foco recalculado pra `--acao` (`rgb(36 71 201 / 15%)`).

### Navigation
Inalterado — `.topbar` simples em Painel, sem navegação persistente lateral.

### Progress Rail (signature component)
`.progress-rail`: agora cada barra é mais alta (10px vs 3px antigo), topo arredondado
(forma "tab"), e cada uma das 4 posições fixas tem sua própria tinta (`:nth-child`
amarrado à ordem de `ETAPAS`). Etapa ativa recebe `transform: scaleY(1.3)` — pulso sutil,
não só troca de cor. Label ativo herda a `-ink` da própria tinta, não mais um `--tinta`
genérico.

### Status Badge
Pílula (999px), maiúsculas, tracking. `data-status="ABERTA"` pinta com `tab-renda-tint`/
`-ink`. Hoje só existe 1 valor real no backend (`SolicitacaoStatus.ABERTA`); o atributo
`data-status` já está no markup pra quando o backend adicionar mais valores — só falta
adicionar a regra CSS correspondente, sem precisar tocar em JSX de novo.

### Lançamento de lista (`.lista-solicitacoes__link`)
Novo: uma aba pequena (`::before`, mesma forma do progress-step__bar) à esquerda de cada
linha, colorida por `data-status`. Deliberadamente **não** é um `border-left` — esse
padrão é banido pelo craft floor do Impeccable (accent border genérico em list item); a
forma de aba reaproveita a mesma geometria do progress-rail em vez de inventar um segundo
device visual pro mesmo conceito de "cor = status".

## Motion

Um gesto por componente, cada um citando algo físico do mundo caderneta — não um
"authored moment" único, mas um vocabulário coerente:
- **Carimbo** (`.protocolo`, `@keyframes carimbo`): entra com overshoot de rotação/escala
  e assenta — bounce easing (`cubic-bezier(0.34, 1.56, 0.64, 1)`) é intencional aqui,
  citando o impacto físico de um carimbo de tinta batendo no papel; não usar esse easing
  em nenhum outro componente sem a mesma justificativa literal.
- **Aba ativa** (`.progress-step--active .progress-step__bar`): `scaleY` — puxa como uma
  aba de fichário sendo levantada.
- **Lançamento** (`.lista-solicitacoes__link:hover`): `translateX(2px)` — desliza como
  virar a página.
- **Botão** (`.button:active`): `translateY(1px)` — feedback de pressão física.

`prefers-reduced-motion` zera tudo, herdado e inalterado.

## Do's and Don'ts

### Do:
- **Do** dar a cada nova etapa/seção sua própria tinta nomeada (solid/tint/ink) em vez de
  reusar uma tab-color existente pra outro conceito.
- **Do** manter `data-status`/`data-etapa` como o mecanismo de cor dinâmica — nunca
  hard-code cor de status em classe condicional no JSX.
- **Do** conferir contraste contra o fundo real (não branco) antes de reusar qualquer
  `-ink`/`-tint` num novo contexto.
- **Do** rotular etapas de fluxo pelo nome real, nunca por número — herdado, ainda vale.

### Don't:
- **Don't** usar `border-left`/`border-right` colorido em list item, card ou alert — usar
  a forma de aba (`border-radius: 4px 4px 2px 2px`, elemento próprio) em vez disso.
- **Don't** adicionar `box-shadow` com blur a nenhum componente — o anel do carimbo é a
  única exceção nomeada, e é zero-blur.
- **Don't** usar o bounce easing do carimbo em outro componente sem a mesma justificativa
  de gesto físico literal.
- **Don't** esconder o protocolo atrás de navegação extra — continua sendo a prova,
  primeiro olhar da lista e do detalhe.
- **Don't** usar mono pra texto corrido ou label.

---

## Histórico

**"O Comprovante" (2026-08-11 até 2026-08-15):** sistema Swiss-minimalista anterior —
paleta restrita a um único azul de ação + laranja raro, Lexend em títulos, zero cor fora
de elementos interativos. Substituído por pedido explícito do usuário por um sistema mais
colorido e interativo. Arquivo original preservado no histórico do git
(`git show 32fda2d:DESIGN.md`) para referência.
