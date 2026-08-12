## Why

O frontend hoje só tem tela de login (`LoginPage.tsx`), sem nenhum cliente conseguir se cadastrar sozinho — só existe se alguém criar o usuário direto no Cognito. Além disso, nenhuma tela tem estilo de verdade: `index.css` só define tokens soltos (cores, fontes) e os formulários são HTML puro sem classe nenhuma. É a base do produto (onboarding começa por aqui) e está com a pior primeira impressão possível.

## What Changes

- Nova capability de autocadastro: cliente cria a própria conta no Cognito (o user pool já permite self sign-up — não tem `admin_create_user_config` restringindo) e confirma por código enviado ao email.
- Duas telas novas: `SignupPage` (formulário de cadastro) e `ConfirmSignUpPage` (input do código de confirmação), com rotas `/cadastro` e `/cadastro/confirmar`.
- `auth/cognito.ts` ganha `signUp()` e `confirmSignUp()`, seguindo o mesmo padrão do `login()` existente.
- Redesign visual completo: `index.css` deixa de ser só tokens soltos e vira um design system real (classes de formulário, input, botão, cards, estados de erro), aplicado em todas as telas existentes (`LoginPage`, `DashboardPage`, `OnboardingWizard` e seus steps, `SolicitacaoDetailPage`) e nas duas novas.
- Processo de design documentado e seguido à risca: `ui-ux-pro-max` (pesquisa de estilo/paleta/tipografia) → `frontend-design` (ponto de vista distintivo em cima da pesquisa) → `impeccable` (execução do redesign, craft real verificado em browser) → `react-expert` (revisão/implementação dos componentes React novos e alterados). Sequência e papel de cada skill documentados em `design.md`.

## Capabilities

### New Capabilities
- `client-registration`: autocadastro do cliente no Cognito (signup + confirmação de email) antes do primeiro login.

### Modified Capabilities
(nenhuma — login existente não muda de comportamento, só de forma visual)

## Impact

- Frontend: `frontend/src/auth/cognito.ts` (novas funções), `frontend/src/App.tsx` (novas rotas), `frontend/src/pages/SignupPage.tsx` e `ConfirmSignUpPage.tsx` (novos), `frontend/src/index.css` (design system), todas as páginas existentes (restyle, sem mudança de lógica).
- Nenhuma mudança de infraestrutura: o Cognito User Pool (`infra/terraform/modules/cognito`) já suporta self sign-up como está hoje.
- Nenhuma mudança de backend: cadastro é feito direto no Cognito pelo frontend, como o login já é.
