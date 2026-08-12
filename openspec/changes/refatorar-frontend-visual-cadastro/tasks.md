## 1. Pesquisa de direção visual (`ui-ux-pro-max`)

- [x] 1.1 Rodar `ui-ux-pro-max` sobre o produto (portal de cliente, onboarding, dashboard de solicitações) e produzir um brief curto: estilo, paleta, pareamento de fontes, guidelines de UX relevantes (contraste, formulários, empty/error states).
- [x] 1.2 Registrar o brief no início da sessão de implementação (comentário/anotação, não precisa virar arquivo permanente).

**Brief (`ui-ux-pro-max`):**
- Estilo: Minimalism & Swiss Style (grid, alto contraste, sem sombra/gradiente, WCAG AAA) — categoria recomendada pra "enterprise apps, dashboards, SaaS platforms, professional tools".
- Paleta (SaaS General / trust blue): primary `#2563EB`, accent `#EA580C`, background `#F8FAFC`, foreground `#1E293B`, card `#FFFFFF`, muted `#E9EFF8`/`#64748B`, border `#E2E8F0`, destructive `#DC2626`.
- Tipografia (Corporate Trust): heading **Lexend**, body **Source Sans 3** — pareamento desenhado pra acessibilidade/legibilidade, indicado pra finance/enterprise/accessibility-focused.
- UX guidelines aplicáveis: indicador de progresso no onboarding multi-etapa; erros com `role="alert"`/`aria-live` (não só borda vermelha); empty state com mensagem + ação; submit com loading→sucesso/erro visível; label associado via `for`/wrap (não placeholder-only); estado ativo de navegação visível; erro com caminho de recuperação (retry/help).

## 2. Ponto de vista de design (`frontend-design`)

- [x] 2.1 Rodar `frontend-design` usando o brief da etapa 1 como insumo — fixar tipografia display/corpo, estrutura da experiência (login → cadastro → confirmação → onboarding → dashboard), motion, e um risco estético justificado.
- [x] 2.2 Confirmar que a direção não contradiz o brief da etapa 1 (refinamento, não substituição).

**Ponto de vista (`frontend-design`)** — herda estilo/paleta/tipografia da etapa 1 sem substituir, só refina:

- **Cor** (nomeada, mesmos hex da etapa 1): `--azul-acao: #2563EB` (interativo/foco, nunca fundo grande), `--laranja-atencao: #EA580C` (só onde há ação pendente real — "ação necessária"), `--fundo: #F8FAFC`, `--papel: #FFFFFF` (cards, sem sombra), `--tinta: #1E293B`, `--linha: #E2E8F0`, `--erro: #DC2626`.
- **Tipografia**: Lexend (heading, uso comedido — títulos de seção, não parágrafos) + Source Sans 3 (corpo/label/botão) + terceira função utilitária: **monoespaçada** (IBM Plex Mono) só pra número de protocolo, CPF mascarado, datas, status — como um extrato bancário real, nunca pra texto corrido.
- **Estrutura em 3 capítulos**, não um fluxo contínuo genérico:
  1. **Entrada** (login/cadastro/confirmação) — coluna única centrada, quieta, sem distração.
  2. **Dossiê** (onboarding) — trilha de progresso com os 4 nomes reais das etapas (Dados pessoais, Endereço, Renda, Anexos), não bolinhas 01/02/03 — a ordem aqui carrega informação real (gate do backend já é sequencial).
  3. **Painel** (dashboard/detalhe) — denso, tabular, números alinhados (`font-variant-numeric: tabular-nums`).
- **Raio de borda em duas camadas** (desvio justificado do "zero-radius" padrão Swiss): 2px em containers/cards (disciplina), 6–8px em botões/inputs (alvo de toque confortável — regra `touch-target-size` da etapa 1).
- **Motion**: nenhuma animação decorativa/hero. Só 2 momentos: transição de etapa no wizard (slide direcional, progride = avança visualmente) e confirmação inline de ação (upload ok, gate completo) — nunca toast solto. `prefers-reduced-motion` sempre respeitado (crossfade).
- **Risco estético assumido**: número de **protocolo** visível e persistente desde a criação da conta — aparece no header a partir do cadastro, atravessa onboarding e dashboard, em mono. Foge do padrão SaaS que esconde IDs do usuário; aqui o ID é a prova de que "isso está sendo processado de verdade", como um comprovante.

## 3. Execução do redesign (`impeccable`)

- [x] 3.1 Rodar `impeccable craft`/`shape` com `--target frontend/`, usando as decisões das etapas 1-2 como direção de marca já commitada (não gerar paleta nova do zero).
- [x] 3.2 Evoluir `index.css` de tokens soltos para um design system real: classes de form, input, botão, card, estado de erro/loading.
- [x] 3.3 Aplicar o novo design system em `LoginPage.tsx`, `DashboardPage.tsx`, `OnboardingWizard.tsx` (+ steps), `SolicitacaoDetailPage.tsx`.
- [x] 3.4 Verificar contraste, responsividade e `prefers-reduced-motion` no browser antes de fechar a etapa.

**Execução (`impeccable`):** direção já estava pinada (design.md D3) — sem concept-seed/roll,
sem decision page (brief-pinned direction beats the roll). `index.css` reescrito como design
system real (tokens + `.card`/`.form`/`.input`/`.button`/`.alert`/`.progress-rail`/etc.),
aplicado nas 5 telas + 3 forms do onboarding. Fontes reais (Lexend/Source Sans 3/IBM Plex Mono)
carregadas via Google Fonts em `index.html`, com contrato de direção documentado no comentário
do `<body>`. Verificação: `npm run build` limpo, `detect.mjs` sem findings, contraste calculado
manualmente pra cada par texto/fundo (2 tokens corrigidos por falha AA: `--erro` `#dc2626`→
`#b91c1c`, `--muted-tinta` `#64748b`→`#475569` — ambos falhavam 4.5:1 contra seus fundos
tintados), sem overflow horizontal em 375px, snapshot de acessibilidade confirmando labels
associados. Regra de `prefers-reduced-motion` presente e testável via CSS (não há tooling de
emulação de media query neste conjunto de ferramentas — verificado por leitura de código).
**Substituição registrada**: este harness não tem os subagentes `impeccable-finish-reviewer`/
`impeccable-documenter`; rodei os dois papéis inline a partir de `reference/degraded/*.md`
(sem screenshot — o pipeline de screenshot do Playwright MCP grava num filesystem que as
ferramentas de leitura desta sessão não alcançam; substituído por leitura de código +
snapshot de acessibilidade + matemática de contraste). Disposition: ship. `DESIGN.md` escrito
na raiz do projeto a partir do mundo construído; sidecar `.impeccable/design.json` não gerado
(não usado nesta sessão).

## 4. Cadastro (signup + confirmação)

- [x] 4.1 Adicionar `signUp(email, password)` e `confirmSignUp(email, code)` em `auth/cognito.ts`, espelhando o padrão Promise do `login()` existente.
- [x] 4.2 Criar `pages/SignupPage.tsx` (form email/senha, validação client-side da política de senha do pool antes de chamar o Cognito).
- [x] 4.3 Criar `pages/ConfirmSignUpPage.tsx` (input de código; recebe email via `location.state`, com fallback pra pedir de novo se ausente).
- [x] 4.4 Adicionar rotas públicas `/cadastro` e `/cadastro/confirmar` em `App.tsx`, com link "Criar conta" a partir do `LoginPage`.
- [x] 4.5 Tratar erros de signup (email duplicado, senha fora da política) e de confirmação (código inválido/expirado) com mensagens claras.

## 5. Revisão React (`react-expert`)

- [x] 5.1 Rodar `react-expert` sobre `SignupPage`, `ConfirmSignUpPage` e o `LoginPage` redesenhado — revisar form state, hooks, tratamento de erro, padrões React 19.
- [x] 5.2 Aplicar os ajustes apontados pela revisão.

**Revisão:** nenhum problema material encontrado — `validarSenha`/`SENHA_REGEX` corretamente
hoisted pra fora do componente, narrowing de erro via `err.name` segue a convenção da SDK do
Cognito, labels/`role="alert"`/`role="status"` corretos, `tsc -b` limpo. Nada a ajustar.

## 6. Fechamento

- [x] 6.1 `cd frontend && npm test` — suite existente continua passando.
- [x] 6.2 `cd frontend && npm run build` — typecheck + build sem erros.
- [x] 6.3 Teste manual do fluxo ponta a ponta: cadastro → código de confirmação → login → dashboard (via docker-compose + ministack).
- [x] 6.4 Code review desta etapa antes do commit (convenção do projeto).

**Code review (`/code-review medium`):** 3 findings, todos corrigidos. (1) `SignupPage`
navegava sempre pra `/cadastro/confirmar` mesmo se `signUp()` já devolvesse
`userConfirmed: true` — agora checa o resultado e pula pro `/login` direto nesse caso.
(2) wrapper `.shell.shell--entrada`/`.card` duplicado em 3 arquivos — extraído pra
`pages/EntradaShell.tsx`, reusado por `LoginPage`/`SignupPage`/`ConfirmSignUpPage`. (3)
`signUp`/`confirmSignUp` sem teste (diferente de `login`, que já tinha) — 4 testes novos
adicionados em `cognito.test.ts` espelhando o padrão de mock existente. `npm test` (8/8) e
`npm run build` limpos depois das correções.

**Teste e2e manual (playwright + docker-compose):** cadastro real contra ministack —
`SenhaForte123` (sem símbolo) foi rejeitado pelo emulador com 400/`InvalidPasswordException`
mesmo com `require_symbols=false` no terraform (`infra/terraform/modules/cognito/main.tf:18`);
com símbolo (`SenhaForte123!`) o cadastro passou e navegou pra `/cadastro/confirmar` com o
email correto pré-preenchido via `location.state`. Confirmação: ministack não valida o código
de verdade (aceitou `000000` num usuário já confirmado via `AdminConfirmSignUp` de bypass) —
não dá pra testar ao vivo o cenário de código inválido/expirado contra este emulador; a
cobertura desse caminho ficou na revisão de código da etapa 5 (nomes de exceção reais do
Cognito). Login: autenticação no Cognito funcionou: **mas o redirect pro dashboard falhou por
CORS** — `POST /api/perfil/gate` de `localhost:5173` pra `localhost:8080` é bloqueado
(`No 'Access-Control-Allow-Origin' header`); confirmado que o backend não tem nenhuma config
de CORS (`grep` em `backend/src/main/kotlin` não achou nada) e o `vite.config.ts` não tem proxy
de `/api` — ou seja, isso já quebrava antes desta change (nenhuma mudança aqui tocou
`LoginPage`'s fetch-then-redirect nem o backend) e está fora do escopo desta change (proposal.md:
"Nenhuma mudança de backend"). Reportado ao usuário como gap de ambiente pré-existente, não
corrigido aqui. `.env.local` do frontend atualizado com os IDs reais do ministack desta subida
do docker-compose (nota inline: mudam a cada `down`+`up`).
