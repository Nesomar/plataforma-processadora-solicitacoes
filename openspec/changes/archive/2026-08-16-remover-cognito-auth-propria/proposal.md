## Why

Testar localmente depende do ministack emular o Cognito, e o pool ID/client ID são recriados a
cada `docker compose up --build`/`terraform apply`. Isso já causou pelo menos três quebras reais
nesta branch (`.env.local` desatualizado, mismatch de `iss` entre `localhost` e `ministack`,
credenciais AWS ausentes) — cadeia de fragilidade que só existe por causa da emulação do Cognito,
não do domínio do produto. Remover o Cognito e emitir/validar o JWT no próprio backend elimina essa
classe inteira de problema, tanto em dev local quanto em produção (não depende mais de um serviço
AWS gerenciado só para autenticação de um app com regras de negócio simples).

## What Changes

- **BREAKING**: login deixa de emitir JWT do Cognito; o backend passa a emitir seu próprio JWT
  (assinado com segredo HS256 gerado localmente), com `sub` = `clienteId` (mesmo contrato que os
  controllers já consomem hoje via `Jwt.clienteId()` — não muda nada nos controllers existentes).
- **BREAKING**: cadastro (`signUp`) e confirmação por e-mail (`confirmSignUp`) do Cognito são
  substituídos por `POST /api/auth/signup` no backend, que já ativa a conta na hora — sem etapa de
  confirmação por código de e-mail (essa etapa dependia do Cognito/SES; sem ela, elimina-se
  complexidade e a UI de "confirmar cadastro" inteira).
- Credenciais (email + hash de senha via BCrypt) passam a ser armazenadas no DynamoDB, indexadas
  por email (novo item `EMAIL#{email}` / `SK=CREDENTIAL`), e não mais no Cognito.
- `SecurityConfig.kt` passa a validar o JWT próprio (chave HS256 compartilhada, sem depender de
  `issuer-uri`/`jwk-set-uri` apontando pra um provedor externo).
- Frontend: `auth/cognito.ts` (baseado em `amazon-cognito-identity-js`) é substituído por um
  cliente HTTP simples que chama os endpoints REST do próprio backend; a dependência
  `amazon-cognito-identity-js` é removida do `package.json`; a página/rota de confirmação de
  cadastro (`ConfirmSignUpPage.tsx`) é removida.
- Infra: módulo Terraform `modules/cognito` é removido por completo; `infra/terraform/outputs.tf`,
  `docker-init-apply.sh` e o `application.yml`/env vars do backend deixam de referenciar Cognito
  (issuer, JWKS, pool ID, client ID). `docker-compose` local não precisa mais que o ministack
  emule Cognito.
- Setup de dev local simplifica: não há mais IDs gerados dinamicamente pelo Terraform que o
  frontend precise sincronizar manualmente após cada rebuild — o segredo JWT é uma env var estável.

## Capabilities

### New Capabilities
(nenhuma — o cadastro/login continuam fazendo parte de `client-auth`, só muda quem emite/valida o token)

### Modified Capabilities
- `client-auth`: os requisitos passam a descrever autenticação própria do backend (signup, login,
  emissão e validação de JWT com segredo próprio) no lugar de Cognito + API Gateway Authorizer.
  Requisito de "API Gateway valida o token via Cognito Authorizer" é removido — validação passa a
  ser só no backend (ECS), já que não existe mais dependência do Cognito Authorizer.

## Impact

- **Backend**: novo `domain/Credencial`, `port/input/SignupUseCase` + `LoginUseCase`,
  `application/AuthService`, `port/output/CredencialRepository`,
  `adapter/output/dynamodb/CredencialItem` + repositório, novo `adapter/input/web/AuthController`
  (`/api/auth/signup`, `/api/auth/login`). `SecurityConfig.kt` troca `oauth2ResourceServer { jwt {} }`
  configurado via issuer/JWKS por um `JwtDecoder`/`JwtEncoder` com chave HS256 própria.
  `application.yml` perde `COGNITO_ISSUER_URI`/`COGNITO_JWK_SET_URI`, ganha `JWT_SIGNING_SECRET`.
- **Frontend**: `auth/cognito.ts` reescrito (ou renomeado) para chamar o backend via `httpClient`;
  `SignupPage.tsx`/`LoginPage.tsx` ajustam chamadas; `ConfirmSignUpPage.tsx` e sua rota são
  removidos; dependência `amazon-cognito-identity-js` sai do `package.json`.
- **Infra**: `infra/terraform/modules/cognito/` removido; `infra/terraform/outputs.tf` perde os
  outputs de Cognito; `docker-init-apply.sh` não provisiona mais pool/client; qualquer referência a
  Cognito Authorizer no módulo `api_gateway` (se existir) é removida/ajustada para JWT genérico.
- **Specs**: `openspec/specs/client-auth/spec.md` reescrita. `client-profile`, `attachments`,
  `service-requests` não citam Cognito diretamente (usam apenas "o JWT"/"o cliente autenticado") —
  sem mudança de requisito, só continuam válidas com o novo emissor de token.
- **Dependências removidas**: `amazon-cognito-identity-js` (frontend). Nenhuma lib nova
  estritamente necessária no backend além do que Spring Security já oferece
  (`NimbusJwtDecoder`/`NimbusJwtEncoder`, `BCryptPasswordEncoder`).
