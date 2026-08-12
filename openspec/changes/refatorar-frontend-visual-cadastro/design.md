## Context

Frontend atual (`frontend/src/`):
- `pages/LoginPage.tsx` é a única tela de auth — form HTML puro, sem classe nenhuma.
- `index.css` só define custom properties (`--text`, `--accent`, `--bg`, etc.) e estilo de `#root`/`h1`/`h2`/`code`. Nenhum `.button`, `.input`, `.card` — cada tela herda estilo de navegador puro.
- `auth/cognito.ts` só tem `login()`. Não existe `signUp`/`confirmSignUp`.
- `App.tsx` só roteia `/login` (público) e `/`, `/onboarding`, `/solicitacoes/:id` (atrás de `ProtectedRoute`).
- Cognito User Pool (`infra/terraform/modules/cognito/main.tf`) **não** tem `admin_create_user_config` nem `lambda_config` — self sign-up já é permitido por padrão, sem mudança de infra necessária. `auto_verified_attributes = ["email"]` só significa que o Cognito pode enviar/validar o código por email; a confirmação (`ConfirmSignUp`) ainda é um passo manual do cliente.
- Password policy do pool: mínimo 8 caracteres, maiúscula, minúscula e número obrigatórios, símbolo opcional.

## Goals / Non-Goals

**Goals:**
- Cliente se cadastra sozinho (signup + confirmação por código) sem precisar de intervenção manual no Cognito.
- Todo o frontend (telas existentes + novas) passa a ter um design system real em vez de HTML cru.
- O processo de design é auditável: cada decisão visual rastreável a qual das 4 skills a produziu.

**Non-Goals:**
- Mudança de infraestrutura Cognito (pool já suporta o fluxo como está).
- Mudança de backend (cadastro é Cognito-direto, como o login já é).
- Recuperação de senha ("esqueci minha senha") — fora de escopo desta change.
- Login social / MFA.

## Decisions

### D1 — signup/confirm client-side puro, espelhando `login()`
`signUp(email, password)` e `confirmSignUp(email, code)` entram em `auth/cognito.ts` usando `amazon-cognito-identity-js` (já é dependência), no mesmo estilo do `login()` existente (Promise em volta do callback do SDK). Sem endpoint novo no backend Kotlin.
**Alternativa rejeitada**: endpoint backend proxy pro Cognito — rejeitado por inconsistência (login não passa pelo backend) e por não haver necessidade (SDK cliente já cobre o fluxo).

### D2 — capability nova `client-registration`, separada de `client-auth`
Login não muda de comportamento (só de estilo). Cadastro é comportamento novo. Manter as specs separadas evita modificar requirements do `client-auth` sem necessidade.

### D3 — sequência estrita das 4 skills (o núcleo desta change)
Ordem fixa, cada uma consumindo o output da anterior — não são intercambiáveis nem paralelas:

```
┌─────────────────┐   brief de estilo    ┌──────────────────┐
│  ui-ux-pro-max   │ ──(paleta/tipografia/─▶│  frontend-design  │
│  (pesquisa)      │   UX guidelines)      │  (ponto de vista)  │
└─────────────────┘                       └─────────┬─────────┘
                                                      │ decisões de
                                                      │ direção (sem código)
                                                      ▼
┌──────────────────┐   componentes reais   ┌──────────────────┐
│   react-expert    │◀──(CSS/tokens/telas)──│    impeccable     │
│ (qualidade React) │                       │  (execução real)  │
└──────────────────┘                       └──────────────────┘
```

1. **`ui-ux-pro-max`** roda primeiro, sozinha, sobre a pergunta "que estilo/paleta/tipografia fazem sentido pra um portal de cliente de crédito com onboarding" (product type ~ dashboard/SaaS onboarding). Saída: um brief curto (nome do estilo, paleta, pareamento de fontes, guidelines de UX relevantes — contraste, formulários, empty states). Só pesquisa, zero código.
2. **`frontend-design`** recebe esse brief como insumo/hint e fixa o ponto de vista distintivo: nomeia o assunto (portal de cliente, não um SaaS genérico), decide tipografia de display vs. corpo, estrutura (o que os "capítulos" da experiência — login, cadastro, onboarding, dashboard — comunicam), motion, e um risco estético justificado. Ainda sem código — é a etapa de brainstorm/crítica que a própria skill descreve.
3. **`impeccable`** executa o redesign de verdade (`craft`/`shape`), tratando os passos 1-2 como a direção de marca já commitada — **não** roda o `palette.mjs` de marca nova (regra da própria skill: só roda se não achar tokens commitados; aqui os tokens de `index.css` + o brief das etapas 1-2 contam como identidade já definida). Produz CSS real, classes de componente, e aplica em todas as telas, com verificação em browser (contraste, responsividade, motion reduzido).
4. **`react-expert`** revisa por último os componentes React tocados/criados (`SignupPage`, `ConfirmSignUpPage`, `LoginPage` redesenhada, hooks de formulário) — arquitetura de estado, padrões React 19, performance. Não refaz decisão visual, só garante que a implementação React em cima do que `impeccable` produziu está correta.

**Por que essa ordem e não outra**: `ui-ux-pro-max` e `frontend-design` fazem trabalho parecido (ambas decidem paleta/tipografia) — rodá-las em paralelo geraria decisões conflitantes. Rodando em série, `frontend-design` herda e refina a pesquisa da `ui-ux-pro-max` em vez de competir com ela. `impeccable` só entra depois de a direção estar fechada, porque senão ela mesma tentaria inventar uma paleta do zero. `react-expert` por último porque revisão de código React só faz sentido depois que o código existe.

### D4 — rotas novas públicas
`/cadastro` e `/cadastro/confirmar` entram fora do `ProtectedRoute`, ao lado de `/login`. O email digitado no cadastro é passado pra tela de confirmação via `location.state` (com fallback: se o cliente recarregar a página e perder o state, a tela de confirmação pede o email de novo) — mesmo trade-off já aceito pelo projeto de token em memória sem sobreviver a F5.

## Risks / Trade-offs

- **Sobreposição `ui-ux-pro-max` / `frontend-design`** → mitigado pela ordem estrita (D3): a segunda sempre parte do output da primeira, nunca decide do zero.
- **`impeccable` é uma skill pesada (produz código de produção, itera em browser)** — pode extrapolar escopo e tentar refazer mais do que o pedido → mitigar restringindo o alvo explicitamente (`--target frontend/`) e revisando o diff antes de aceitar.
- **Perda do `location.state` no reload durante a confirmação** → mitigado pedindo o email de novo na tela de confirmação se o state não existir (nunca bloqueia o cliente).
- **Escolha de biblioteca/abordagem de CSS (plain CSS vs. CSS modules vs. algo que o `impeccable` sugerir)** → não travado nesta design; decisão fica para a etapa 3 (impeccable), respeitando "nenhuma dependência nova sem necessidade real".

## Migration Plan

Sem dado existente pra migrar (funcionalidade nova + restyle). Deploy é o build normal do frontend (S3+CloudFront ou docker-compose local). Rollback = reverter o build anterior, sem estado de banco envolvido.

## Open Questions

- Direção visual final (paleta/tipografia) fica em aberto propositalmente — só será decidida ao rodar `ui-ux-pro-max` na fase de implementação, conforme escolhido pelo usuário nesta exploração.
