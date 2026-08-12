---
name: Portal do Cliente
description: Portal bancário de onboarding e solicitações — Swiss minimalista, prova sempre visível.
colors:
  azul-acao: "#2563eb"
  azul-acao-hover: "#1d4ed8"
  laranja-atencao: "#ea580c"
  fundo: "#f8fafc"
  papel: "#ffffff"
  tinta: "#1e293b"
  linha: "#e2e8f0"
  erro: "#b91c1c"
  erro-bg: "#fef2f2"
  muted-bg: "#e9eff8"
  muted-tinta: "#475569"
typography:
  headline:
    fontFamily: "Lexend, system-ui, 'Segoe UI', Roboto, sans-serif"
    fontSize: "28px"
    fontWeight: 600
    lineHeight: 1.2
    letterSpacing: "-0.3px"
  title:
    fontFamily: "Lexend, system-ui, 'Segoe UI', Roboto, sans-serif"
    fontSize: "20px"
    fontWeight: 600
    lineHeight: 1.2
    letterSpacing: "-0.2px"
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
    letterSpacing: "normal"
rounded:
  container: "2px"
  control: "8px"
  chip: "6px"
spacing:
  sm: "8px"
  md: "16px"
  lg: "20px"
  xl: "32px"
components:
  button-primary:
    backgroundColor: "{colors.azul-acao}"
    textColor: "#ffffff"
    rounded: "{rounded.control}"
    padding: "11px 20px"
  button-primary-hover:
    backgroundColor: "{colors.azul-acao-hover}"
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
---

# Design System: Portal do Cliente

## Overview

**Creative North Star: "O Comprovante"**

O portal existe pra conduzir um processo burocrático sério (pedir crédito, entregar
documentos, CPF, renda) em linguagem de gente, sem nunca fingir que não é sério. A
metáfora que organiza o sistema inteiro é o comprovante bancário: número de protocolo
sempre visível, valores alinhados, nada escondido atrás de um clique. Estrutura Swiss —
grid disciplinado, alto contraste, zero sombra/gradiente — carrega essa seriedade; a cor
é usada com parcimônia (azul só onde é de fato interativo, laranja só onde há pendência
real do cliente) porque confiança vem de clareza, não de selo.

Rejeitado explicitamente: visual de internet banking datado (denso, cinza, ícones anos
2010); estética cripto/fintech hyped (dark mode dramático, neon); gradiente-roxo genérico
de interface gerada por IA.

**Key Characteristics:**
- Estrutura em 3 capítulos (Entrada / Dossiê / Painel), cada um com sua própria largura de shell
- Número de protocolo em mono, persistente desde a lista até o detalhe
- Zero sombra — profundidade vem de borda de 1px e contraste de fundo, nunca de blur
- Motion mínimo: só transição de etapa no wizard e confirmação inline de ação

## Colors

Paleta restrita (Restrained): neutros dominam, azul-ação é o único acento com permissão
de aparecer em elementos interativos, laranja é reservado e raro.

### Primary
- **Azul-ação** (`#2563eb`, hover `#1d4ed8`): todo elemento interativo primário — botões
  `.button--primary`, foco de input, links. Nunca usado como cor de fundo de região grande.

### Secondary
- **Laranja-atenção** (`#ea580c`): reservado só pra ação pendente real do cliente (ainda
  não usado nas telas desta fase — entra quando houver estado de "ação necessária"
  explícito, ex. documento rejeitado). Não decorativo.

### Neutral
- **Tinta** (`#1e293b`): texto principal, títulos.
- **Fundo** (`#f8fafc`): fundo de página, atrás dos cards.
- **Papel** (`#ffffff`): superfície de card, sem sombra.
- **Linha** (`#e2e8f0`): toda borda — cards, inputs, divisores de lista/tabela.
- **Muted-tinta** (`#475569`): texto secundário/rótulo (ajustado de slate-500 pra
  slate-600 nesta implementação — o tom mais claro falhava contraste AA sobre
  `muted-bg`; ver Named Rule abaixo).
- **Muted-bg** (`#e9eff8`): fundo de chip (protocolo, status).
- **Erro** (`#b91c1c`): texto/borda de alerta (ajustado de red-600 pra red-700 nesta
  implementação pelo mesmo motivo de contraste).
- **Erro-bg** (`#fef2f2`): fundo de alerta.

### Named Rules
**The Proof-Chip Rule.** Todo identificador que prova que algo está sendo processado
(protocolo, status) vive num chip mono com fundo `muted-bg` — nunca só texto solto, nunca
escondido atrás de navegação extra.

**The AA-Is-The-Floor Rule.** Qualquer tom que passar como "muted" ou "de alerta" precisa
bater 4.5:1 contra o fundo real onde ele aparece (não contra branco puro). `#64748b` e
`#dc2626` (as sugestões originais do brief) falhavam contra `muted-bg`/`erro-bg` nesta
implementação; foram escurecidos um degrau. Verifique de novo se `muted-bg`/`erro-bg`
mudarem de valor.

## Typography

**Display Font:** Lexend (fallback: system-ui, Segoe UI, Roboto, sans-serif)
**Body Font:** Source Sans 3 (fallback: system-ui, Segoe UI, Roboto, sans-serif)
**Label/Mono Font:** IBM Plex Mono

**Character:** Lexend é geométrica e comedida — usada só em `h1`/`h2`, nunca em
parágrafo. Source Sans 3 carrega todo o corpo, label e botão — pareamento desenhado pra
legibilidade/acessibilidade (finance/enterprise). IBM Plex Mono existe só pra números que
provam algo: protocolo, CPF, data, renda, status — como um extrato bancário, nunca pra
texto corrido.

### Hierarchy
- **Headline** (600, 28px, 1.2): `h1` — título de página/tela.
- **Title** (600, 20px, 1.2): `h2` — título de seção/etapa dentro de um card.
- **Body** (400, 16px, 1.5): texto de formulário, parágrafo, item de lista, `.topbar__brand`.
- **Emphasis** (600, 15px, 1.4): `.button`, `.detalhe-grid__row` — texto que pede peso sem
  subir de tamanho.
- **Label** (500, 14px): `.field__label`, `.alert`, `.confirm`, `.entrada__footer`.
- **Caption** (400, 13px): `.field__hint` — texto de apoio abaixo do campo.
- **Micro** (500, 12px): `.progress-step__label`, texto do chip `.status-badge`.
- **Mono/Data** (400–500, 12–15px, `font-variant-numeric: tabular-nums`): `.mono`,
  `.protocolo`, `.detalhe-grid__value.mono`, `.input--mono` — qualquer valor numérico ou
  código que precisa alinhar verticalmente. Reusa os tamanhos acima (13px pro chip de
  protocolo, 15px pro valor de dinheiro no detalhe) trocando só a família pra IBM Plex Mono.

### Named Rules
**The Never-Running-Text Rule.** IBM Plex Mono nunca aparece em frase corrida ou label —
só em valor isolado (protocolo, CPF, data, dinheiro, status).

## Layout

Três shells de largura fixa, um por capítulo da experiência:
- `.shell--entrada` (380px, centralizado vertical e horizontalmente): login, cadastro,
  confirmação — coluna única, sem distração.
- `.shell--dossie` (560px): onboarding — form dentro de um card, com `.progress-rail`
  acima mostrando as etapas pelo nome real, não número.
- `.shell--painel` (800px): dashboard e detalhe de solicitação — mais denso, listas e
  grids de campo/valor.

Padding lateral cai de 20px pra 16px abaixo de 600px; nenhum shell tem largura mínima
que force scroll horizontal em mobile.

## Elevation & Depth

Sistema inteiramente flat. Nenhum `box-shadow` em nenhum componente — profundidade vem só
de borda de 1px (`--linha`) e do contraste entre `--papel` e `--fundo`. Decisão do brief
(Swiss/Minimalism), não uma omissão.

### Named Rules
**The No-Shadow Rule.** Nenhum componente novo introduz `box-shadow`. Se precisar
separar uma superfície, use borda de 1px ou mudança de fundo, nunca sombra.

## Shapes

Raio em duas camadas, desvio deliberado do "zero-radius" Swiss padrão: 2px em
containers/cards (`.card`, `.shell` — disciplina), 6–8px em controles interativos
(`.input`, `.button`, chips — alvo de toque confortável). Bordas sempre 1px sólida em
`--linha`, nunca dupla ou tracejada.

## Components

### Buttons
- **Shape:** 8px de raio.
- **Primary** (`.button--primary`): fundo `--azul-acao`, texto branco, `11px 20px`.
- **Hover/Focus:** primary escurece pra `--azul-acao-hover` no hover; todo elemento
  focável ganha `outline: 2px solid var(--foco)` com 2px de offset no `:focus-visible`
  (nunca só troca de cor de borda).
- **Secondary** (`.button--secondary`): transparente, borda 1px `--linha`, texto
  `--tinta`.
- **Link** (`.button--link`): sem fundo, texto `--azul-acao` sublinhado — usado só pra
  ação secundária de navegação (ex. "Criar conta" no rodapé do login).

### Cards / Containers
- **Corner Style:** 2px.
- **Background:** `--papel` sobre `--fundo`.
- **Shadow Strategy:** nenhuma — ver Elevation & Depth.
- **Border:** 1px sólida `--linha`.
- **Internal Padding:** 32px (24px abaixo de 600px).

### Inputs / Fields
- **Style:** borda 1px `--linha`, fundo `--papel`, raio 8px, `10px 12px` de padding.
  `.input--mono` troca a fonte pra IBM Plex Mono (usado em CPF, renda).
- **Focus:** borda muda pra `--azul-acao` + halo `box-shadow: 0 0 0 3px rgb(37 99 235 /
  15%)`.
- **Error:** mensagem em `.alert` (`role="alert"`) logo abaixo do form — nunca só borda
  vermelha no input isolado.
- **Label:** sempre `<label htmlFor>` associado ao `id` do input — nunca placeholder-only.

### Navigation
Sem navegação persistente lateral/superior complexa nesta fase — só `.topbar` simples
(marca + chip de protocolo quando aplicável) nas telas do capítulo Painel. Entrada e
Dossiê não têm topbar (foco total no card único).

### Progress Rail (signature component)
Trilha de progresso do onboarding (`.progress-rail`): uma barra fina por etapa, rotulada
com o nome real da etapa (Dados pessoais / Endereço / Renda / Anexos) — nunca números
01/02/03, porque a ordem aqui carrega informação real de gate do backend. Etapa ativa e
concluída ganham `--azul-acao` na barra; label ativo fica em `--tinta` e negrito, as
demais em `--muted-tinta`.

## Do's and Don'ts

### Do:
- **Do** usar `--azul-acao` só em elementos interativos (botão, foco, link) — nunca como
  fundo de região grande.
- **Do** rotular etapas de fluxo pelo nome real da tarefa, nunca por número de ordem.
- **Do** usar IBM Plex Mono só em protocolo/CPF/data/renda/status.
- **Do** conferir contraste contra o fundo real do componente (não contra branco) antes
  de reusar `--muted-tinta` ou `--erro` num novo contexto.

### Don't:
- **Don't** adicionar `box-shadow` a card, botão ou input.
- **Don't** usar `--laranja-atencao` decorativamente — só quando existe uma pendência
  real e nomeada do cliente.
- **Don't** esconder o número de protocolo atrás de navegação extra — ele é a prova, tem
  que estar no primeiro olhar da tela de lista e de detalhe.
- **Don't** usar mono pra texto corrido ou label.
