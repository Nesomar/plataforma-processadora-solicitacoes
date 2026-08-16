## 1. Backend — domínio e portas

- [x] 1.1 Criar `domain/Credencial` (email, passwordHash, clienteId, criadoEm) + exceptions
      relevantes (ex: `EmailJaCadastradoException`, `CredenciaisInvalidasException`)
- [x] 1.2 Criar `port/input/SignupUseCase` e `port/input/LoginUseCase`
- [x] 1.3 Criar `port/output/CredencialRepository` (buscarPorEmail, salvar com condição de
      unicidade)

## 2. Backend — persistência

- [x] 2.1 Criar `adapter/output/dynamodb/CredencialItem` (`@DynamoDbBean`, PK=`EMAIL#{email}`,
      SK=`CREDENTIAL`, mapeamento explícito de atributos como já feito nos outros `*Item`)
- [x] 2.2 Implementar `CredencialRepository` no DynamoDB Enhanced Client, com `PutItem` condicional
      (`attribute_not_exists(PK)`) para garantir e-mail único no signup

## 3. Backend — aplicação e emissão/validação de JWT

- [x] 3.1 Implementar `application/AuthService` (`SignupUseCase` + `LoginUseCase`), usando
      `BCryptPasswordEncoder` para hash/verificação de senha
- [x] 3.2 Adicionar bean de `JwtEncoder` (HS256, `NimbusJwtEncoder` + `ImmutableSecret`) — gera
      token com `sub=clienteId`, `iat`, `exp` (12h)
- [x] 3.3 Trocar `SecurityConfig.kt`: remover `issuer-uri`/`jwk-set-uri`, configurar `JwtDecoder`
      via `NimbusJwtDecoder.withSecretKey(secretKey)` com o mesmo segredo do encoder
- [x] 3.4 Adicionar `JWT_SIGNING_SECRET` em `application.yml` (substituindo
      `COGNITO_ISSUER_URI`/`COGNITO_JWK_SET_URI`)

## 4. Backend — endpoints

- [x] 4.1 Criar `adapter/input/web/AuthController` com `POST /api/auth/signup` e
      `POST /api/auth/login`, liberados em `SecurityConfig` (`permitAll`) já que precedem a
      autenticação
- [x] 4.2 Tratar erros no `WebExceptionHandler` (email duplicado → 409, credenciais inválidas →
      401)
- [x] 4.3 Testes de unidade/integração: `AuthService`, `AuthController`, `CredencialRepository`
      (contra ministack local)

## 5. Frontend

- [x] 5.1 Criar `auth/authApi.ts` (ou reescrever `cognito.ts`) chamando
      `POST /api/auth/signup` e `POST /api/auth/login` via `httpClient`
- [x] 5.2 Atualizar `SignupPage.tsx` e `LoginPage.tsx` para usar o novo client
- [x] 5.3 Remover `ConfirmSignUpPage.tsx` e sua rota em `App.tsx`
- [x] 5.4 Remover dependência `amazon-cognito-identity-js` do `package.json`
- [x] 5.5 Remover `VITE_COGNITO_*` de `frontend/.env.local`/`.env.example`
- [x] 5.6 Rodar `npm test` + `npm run build`

## 6. Infra

- [x] 6.1 Remover `infra/terraform/modules/cognito/`
- [x] 6.2 Remover outputs de Cognito em `infra/terraform/outputs.tf`
- [x] 6.3 Ajustar `docker-init-apply.sh` para não provisionar mais pool/client Cognito
- [x] 6.4 Revisar módulo `api_gateway` (se referenciar Cognito Authorizer, remover/ajustar)
- [x] 6.5 Adicionar `JWT_SIGNING_SECRET` fixo ao ambiente de dev local (docker-compose/`.env`)

## 7. Specs e limpeza

- [x] 7.1 Rodar `openspec archive remover-cognito-auth-propria` (ou equivalente) após validar
      implementação, sincronizando `openspec/specs/client-auth/spec.md`
- [x] 7.2 Atualizar `CLAUDE.md` (seção "ID token, não access token" e qualquer menção a Cognito)
      para refletir o novo mecanismo de auth
- [x] 7.3 Atualizar `docs/architecture.md` e `docs/decisions/` se citarem Cognito diretamente

## 8. Validação end-to-end

- [x] 8.1 `docker compose up --build` do zero (sem `.env.local` pré-existente) e confirmar que
      signup → login → dashboard funciona sem nenhum ajuste manual de IDs/segredos
- [x] 8.2 Repetir `docker compose down && docker compose up --build` e confirmar que nada quebra
      (idempotência — o problema original que motivou esta mudança)
