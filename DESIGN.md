---
name: Portal do Cliente
description: Portal bancário de onboarding e solicitações — SaaS financeiro contemporâneo, azul de ação único, sombra suave.
colors:
  primary: "#1d4ed8"
  primary-hover: "#1e40af"
  primary-tint: "#eff6ff"
  bg: "#f8fafc"
  surface: "#ffffff"
  surface-hover: "#f1f5f9"
  border: "#e2e8f0"
  border-strong: "#cbd5e1"
  text: "#0f172a"
  text-muted: "#64748b"
  danger: "#dc2626"
  danger-bg: "#fef2f2"
  success: "#059669"
  pending: "#d97706"
  pending-bg: "#fffbeb"
  pending-ink: "#92400e"
typography:
  headline:
    fontFamily: "IBM Plex Sans, ui-sans-serif, system-ui, sans-serif"
    fontSize: "28px"
    fontWeight: 700
    lineHeight: 1.2
    letterSpacing: "-0.2px"
  title:
    fontFamily: "IBM Plex Sans, ui-sans-serif, system-ui, sans-serif"
    fontSize: "20px"
    fontWeight: 600
    lineHeight: 1.2
    letterSpacing: "-0.1px"
  brand:
    fontFamily: "IBM Plex Sans, ui-sans-serif, system-ui, sans-serif"
    fontSize: "16px"
    fontWeight: 700
    lineHeight: 1.3
    letterSpacing: "0"
  body:
    fontFamily: "IBM Plex Sans, ui-sans-serif, system-ui, sans-serif"
    fontSize: "16px"
    fontWeight: 400
    lineHeight: 1.5
    letterSpacing: "normal"
  emphasis:
    fontFamily: "IBM Plex Sans, ui-sans-serif, system-ui, sans-serif"
    fontSize: "15px"
    fontWeight: 600
    lineHeight: 1.4
    letterSpacing: "normal"
  label:
    fontFamily: "IBM Plex Sans, ui-sans-serif, system-ui, sans-serif"
    fontSize: "14px"
    fontWeight: 500
    lineHeight: 1.4
    letterSpacing: "normal"
  caption:
    fontFamily: "IBM Plex Sans, ui-sans-serif, system-ui, sans-serif"
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
  container: "12px"
  container-sm: "10px"
  control: "8px"
  pill: "999px"
spacing:
  sm: "8px"
  md: "16px"
  lg: "20px"
  xl: "32px"
shadow:
  sm: "0 1px 2px rgb(15 23 42 / 6%)"
  md: "0 1px 2px rgb(15 23 42 / 4%), 0 8px 24px rgb(15 23 42 / 8%)"
components:
  button-primary:
    backgroundColor: "{colors.primary}"
    textColor: "#ffffff"
    rounded: "{rounded.control}"
    padding: "11px 20px"
    shadow: "{shadow.sm}"
  button-primary-hover:
    backgroundColor: "{colors.primary-hover}"
  button-secondary:
    backgroundColor: "{colors.surface}"
    textColor: "{colors.text}"
    rounded: "{rounded.control}"
    padding: "11px 20px"
  input:
    backgroundColor: "{colors.surface}"
    textColor: "{colors.text}"
    rounded: "{rounded.control}"
    padding: "10px 12px"
  card:
    backgroundColor: "{colors.surface}"
    rounded: "{rounded.container}"
    shadow: "{shadow.md}"
    padding: "32px"
  status-badge:
    backgroundColor: "{colors.surface-hover}"
    textColor: "{colors.text-muted}"
    rounded: "{rounded.pill}"
    padding: "3px 10px"
---

# Design System: Portal do Cliente

## Overview

**Creative North Star: "Console Financeiro"**

Pivot (2026-08-16) — substitui a "Caderneta" (papel pautado, serif, uma tinta nomeada por
etapa, zero sombra, protocolo carimbado; arquivada abaixo em histórico) por um sistema SaaS
financeiro contemporâneo, por pedido explícito do usuário por "cara mais moderna". Mantém o
produto (onboarding por etapas retomável, solicitações rastreáveis) mas troca completamente
a linguagem visual: um único azul de ação, superfícies neutras claras, sombra suave (não
mais zero-blur), tipografia sans única (IBM Plex Sans) em vez do trio serif/sans/mono
anterior.

Rejeitado explicitamente (herdado, ainda vale): visual de internet banking datado; estética
cripto/fintech hyped (dark mode dramático, neon, gold+purple); gradiente genérico de
interface gerada por IA; fonte "que toda UI gerada por IA usa" sem personalidade (Inter/Plus
Jakarta Sans isolados foram avaliados e descartados por esse motivo — IBM Plex Sans entrega
o mesmo profissionalismo com mais identidade e reaproveita o Plex Mono que os dados já
usavam).

**Key Characteristics:**
- Um único azul de ação (`--primary`) para todo elemento interativo — sem mais "uma tinta
  por etapa"; a etapa atual do onboarding é comunicada pela trilha de progresso (barra
  cheia = feito/atual, cinza = pendente), não por cor própria de cada card.
- Sombra suave permitida e usada com intenção — `--shadow-md` em cards, `--shadow-sm` em
  botão primário e item de lista em hover. Substitui o "No-Blur-Shadow Rule" da Caderneta.
- Cantos maiores (12px em card, 10px em superfícies menores) — lê como produto SaaS atual,
  não como formulário impresso.
- Fundo neutro claro (`--bg: #f8fafc`), sem textura de papel pautado.
- Status usa cor semântica restrita: âmbar (`--pending`) só pra "em andamento", vermelho só
  pra erro, verde só pra confirmação — nunca decorativo.

## Colors

### Ação
- **Primary** (`#1d4ed8`, hover `#1e40af`, tint `#eff6ff`): todo elemento interativo
  primário — botões, links, foco de input, indicador de progresso ativo/concluído.

### Semânticos (status)
- **Pending** (`#d97706` / bg `#fffbeb` / ink `#92400e`): status "em andamento"
  (`SolicitacaoStatus.ABERTA` hoje é o único valor real do backend).
- **Danger** (`#dc2626` / bg `#fef2f2`): erro de formulário/autenticação.
- **Success** (`#059669`): confirmação de ação (`.confirm`, ex: "Arquivo enviado").

### Neutros
- **Text** (`#0f172a`): texto principal, títulos.
- **Text-muted** (`#64748b`): texto secundário/rótulo.
- **Bg** (`#f8fafc`): fundo de página.
- **Surface** (`#ffffff`): card, topbar.
- **Surface-hover** (`#f1f5f9`): hover de botão secundário, fundo de badge neutro.
- **Border** (`#e2e8f0`): borda padrão — cards, inputs, divisores.
- **Border-strong** (`#cbd5e1`): hover de borda (input, item de lista).

### Named Rules
**The One-Blue Rule.** Não há mais tinta própria por etapa/seção — um único `--primary`
cobre toda ação interativa. Se uma nova seção precisar de identidade visual, usar
peso/tipografia/ícone, não uma cor nova.

**The AA-Is-The-Floor Rule.** (herdada) Qualquer tom "muted" ou "de alerta" precisa bater
4.5:1 contra o fundo real onde aparece.

## Typography

**Font:** IBM Plex Sans (família única — título e corpo), fallback: ui-sans-serif,
system-ui, sans-serif.
**Mono/Data Font:** IBM Plex Mono (herdada da Caderneta, mantida — dado financeiro pede
figura tabular e um SaaS financeiro moderno também usa mono pra números, não é um artefato
exclusivo do sistema anterior).

**Character:** família única em vez do trio serif/sans/mono — decisão deliberada de reduzir
personalidade tipográfica de "livro-razão" pra "produto atual"; IBM Plex Sans foi escolhida
sobre Inter/Plus Jakarta Sans (a escolha SaaS-padrão) por ser mais distintiva e por já ter
o Plex Mono como par nativo pra dado tabular, evitando introduzir uma terceira família.

### Hierarchy
- **Headline** (700, 28px, 1.2): `h1`.
- **Title** (600, 20px, 1.2): `h2`.
- **Brand** (700, 16px, 1.3): `.topbar__brand`.
- **Body** (400, 16px, 1.5): formulário, parágrafo, item de lista.
- **Emphasis** (600, 15px, 1.4): `.button`, `.detalhe-grid__row`.
- **Label** (500, 14px): `.field__label`.
- **Caption** (400, 13px): `.field__hint`.
- **Micro** (500, 12px, tracking 0.03em): `.progress-step__label`, `.status-badge`.
- **Mono/Data**: `.mono`, `.protocolo`, `.detalhe-grid__value.mono`, `.input--mono`.

### Named Rules
**The Never-Running-Text Rule.** (herdada, inalterada) IBM Plex Mono nunca em frase corrida
ou label — só em valor isolado.

## Layout

Inalterado — 3 shells de largura fixa por capítulo (`.shell--entrada` 380px /
`.shell--dossie` 560px / `.shell--painel` 800px). O pivot é inteiramente de superfície (cor,
sombra, forma, tipo); topologia e composição não mudaram.

## Elevation & Depth

Sombra suave permitida — pivot central desta mudança (a Caderneta bania `box-shadow` com
blur por completo). `--shadow-sm` (1px + leve tint) em botão primário e hover de item de
lista; `--shadow-md` (duas camadas, blur maior) em card. Nunca sombra "dura"
(`box-shadow` sem blur) nem múltiplas camadas de neumorfismo.

### Named Rules
**The Soft-Shadow Rule** (substitui a No-Blur-Shadow Rule da Caderneta): toda elevação usa
`--shadow-sm`/`--shadow-md`; não introduzir um terceiro valor de sombra sem necessidade
real de hierarquia adicional.

## Shapes

- **Container** (12px): `.card`.
- **Container-sm** (10px): `.card` em mobile, `.lista-solicitacoes__item`.
- **Control** (8px): `.input`, `.button`.
- **Pill** (999px): `.status-badge`, `.progress-step__bar`.

Forma de "aba" (`4px 4px 2px 2px`, específica da Caderneta) foi removida — trilha de
progresso e indicador de lista agora usam barra/ponto arredondados simples.

## Components

### Buttons
`.button--primary`/`--secondary`/`--link`, 8px de raio. Primário ganha `--shadow-sm`
(pivot: antes era flat sem sombra). Removido o `:active { translateY(1px) }` da Caderneta —
feedback de clique fica só na sombra/cor, mais discreto, mais "produto atual".

### Cards / Containers
- **Corner Style:** 12px (10px em telas pequenas), com `--shadow-md`.
- Removida a "lombada colorida" (faixa de 4 tintas no topo do card de Entrada) e a borda
  colorida por etapa no card do dossiê — eram os dois devices mais específicos da
  identidade Caderneta; a trilha de progresso acima do card já comunica a etapa atual.

### Inputs / Fields
Inalterado na forma; halo de foco recalculado pra `--primary`
(`box-shadow: 0 0 0 3px var(--primary-tint)`).

### Navigation
Inalterado — `.topbar` simples em Painel, sem navegação persistente lateral.

### Progress Rail (signature component)
`.progress-rail`: barra fina (4px, pill), uma única cor (`--primary` pra feito/ativo,
`--border` pra pendente) — trocou a barra alta multicolor (10px, uma tinta por etapa,
`transform: scaleY(1.3)` no ativo) por um traço simples e uniforme. Label ativo usa
`--primary`; label concluído usa `--text`; label pendente usa `--text-muted`.

### Status Badge
Pílula (999px), maiúsculas, tracking. `data-status="ABERTA"` pinta com `--pending-bg`/
`--pending-ink` (era `--tab-renda-tint`/`-ink` na Caderneta — mesmo papel semântico "em
andamento", cor renomeada pro token semântico novo).

### Lançamento de lista (`.lista-solicitacoes__link`)
Indicador de status agora é um ponto (8px, círculo) à esquerda de cada linha, não mais uma
aba retangular — mudança deliberada: a forma de aba era o motivo visual central da
Caderneta (fichário/caderneta), abandonado junto com o resto da metáfora. Item de lista
ganhou borda + raio (10px) e sombra leve no hover, lendo como card clicável em vez de linha
de tabela crua.

## Motion

Reduzida a um vocabulário simples e neutro — a Caderneta tinha um gesto físico citado por
componente (carimbo com bounce, aba puxada, página virando); o pivot pra SaaS moderno troca
isso por transições utilitárias:
- **Entrada** (`.protocolo`, `.confirm`): fade-in simples, sem rotação/overshoot.
- **Passo do wizard** (`.step-enter`): fade + leve subida (`translateY(6px)`), não mais
  slide lateral.
- **Hover de lista**: sombra + borda, sem `translateX`.

`prefers-reduced-motion` zera tudo, herdado e inalterado.

## Do's and Don'ts

### Do:
- **Do** usar `--primary` pra toda ação interativa — não introduzir uma segunda cor de
  marca sem motivo semântico (status) real.
- **Do** manter `data-status` como mecanismo de cor dinâmica de status — nunca hard-code
  cor condicional no JSX.
- **Do** conferir contraste contra o fundo real antes de reusar qualquer token semântico
  num novo contexto.
- **Do** manter `--shadow-sm`/`--shadow-md` como os únicos dois níveis de elevação.

### Don't:
- **Don't** reintroduzir cor por etapa/seção — essa era a identidade da Caderneta,
  deliberadamente abandonada.
- **Don't** usar sombra sem blur ("dura") ou múltiplas camadas tipo neumorfismo — só
  `--shadow-sm`/`--shadow-md`.
- **Don't** usar mono pra texto corrido ou label.
- **Don't** esconder o protocolo atrás de navegação extra — continua sendo a prova,
  primeiro olhar da lista e do detalhe.

---

## Histórico

**"A Caderneta" (2026-08-15 até 2026-08-16):** sistema de caderneta financeira física —
papel pautado, IBM Plex Serif em título, 4 tintas nomeadas (uma por etapa do onboarding),
protocolo carimbado com animação de bounce, zero `box-shadow` com blur. Substituído por
pedido explícito do usuário por uma "cara mais moderna" — pivot pra SaaS financeiro
contemporâneo. Arquivo original preservado no histórico do git
(`git show d6b233e:DESIGN.md`).

**"O Comprovante" (2026-08-11 até 2026-08-15):** sistema Swiss-minimalista anterior —
paleta restrita a um único azul de ação + laranja raro, Lexend em títulos, zero cor fora de
elementos interativos. Arquivo original preservado no histórico do git
(`git show 32fda2d:DESIGN.md`).
