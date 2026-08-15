## Context

Hoje `client-auth` depende inteiramente do Amazon Cognito: o frontend fala direto com o Cognito
via SDK (`amazon-cognito-identity-js`) pra signup/confirm/login, e o backend valida o JWT resultante
via `oauth2ResourceServer { jwt {} }` do Spring, configurado com `issuer-uri`/`jwk-set-uri` apontando
pro Cognito real (prod) ou pra emulação do ministack (dev local). Em produção, o desenho original
(ver `docs/decisions/0006-sem-deploy-aws-real.md`) previa um API Gateway com Cognito Authorizer na
frente do ECS — nunca testado de verdade (ministack não confirma suporte a NLB/VPC Link).

O ministack recria o user pool e o client a cada `terraform apply`/`docker compose up --build`,
gerando IDs novos toda vez. O frontend lê esses IDs de `frontend/.env.local`, que precisa ser
atualizado manualmente após cada rebuild — e o issuer/JWKS do ID token também já divergiu entre
`localhost` (usado pelo browser) e `ministack` (hostname interno do docker-compose), quebrando a
validação no backend. Essa fragilidade inteira é seguida de perto pelo histórico recente da branch
`feature/refatorar-frontend-visual-cadastro` (3 incidentes distintos na mesma sessão de dev).

Todos os controllers autenticados (`AnexoController`, `MeController`, `PerfilController`,
`SolicitacaoController`) já usam `@AuthenticationPrincipal jwt: Jwt` e extraem `clienteId` via
`jwt.clienteId()` = `jwt.subject`. Esse contrato (claim `sub` = clienteId) é o que precisa ser
preservado — o resto (quem emite, como se valida a assinatura) é livre pra mudar.

## Goals / Non-Goals

**Goals:**
- Login e signup funcionam localmente sem nenhum serviço externo emulado (nem Cognito, nem SES).
- Backend emite e valida seu próprio JWT; controllers existentes não mudam (mesmo contrato `sub`).
- Setup de dev local (`docker compose up --build`) fica idempotente: nenhum ID/segredo muda entre
  subidas, exceto se o operador trocar o `.env` explicitamente.
- Produção usa exatamente o mesmo mecanismo de auth que dev local (sem Cognito real).

**Non-Goals:**
- Não é objetivo implementar refresh token, "esqueci minha senha", MFA ou rate-limiting de login —
  ficam como possíveis mudanças futuras, fora do escopo desta.
- Não é objetivo manter confirmação de cadastro por e-mail (a etapa existia só por causa do
  Cognito/SES). Conta fica ativa no signup.
- Não é objetivo resolver o `PerfilService` sem locking otimista (`ponytail:` no código) — fora de
  escopo, tratado em outra mudança se necessário.

## Decisions

### 1. JWT assinado com segredo simétrico (HS256), não par de chaves RSA
Alternativas consideradas: (a) RSA/RS256 com o backend expondo seu próprio endpoint
`/.well-known/jwks.json`, imitando o padrão OIDC; (b) HS256 com segredo compartilhado via env var.

Escolha: **(b) HS256**. RS256 + JWKS endpoint recria exatamente o problema que motivou essa
mudança — mais uma peça de infraestrutura (endpoint, rotação de chave, cache do JWKS) só pra
resolver um problema que não existe aqui: emissor e validador são o mesmo processo (o próprio
backend). Um segredo HS256 fixo, gerado uma vez e guardado em `JWT_SIGNING_SECRET` (env var local /
secret manager em prod), elimina toda a superfície de sincronização que causava os bugs.

Configuração via `NimbusJwtDecoder.withSecretKey(secretKey)` e `NimbusJwtEncoder` com
`ImmutableSecret<SecurityContext>` — ambos built-in do Spring Security, sem dependência nova.

### 2. Sem etapa de confirmação de e-mail; conta ativa no signup
Cognito exigia confirmar um código enviado por e-mail (via SES) antes do login funcionar. Sem
Cognito, replicar isso exigiria integrar um provedor de e-mail — complexidade desproporcional pro
que o produto precisa agora. Signup passa a criar a credencial e o cliente já pode logar em
seguida. Se verificação de e-mail vier a ser necessária, é uma mudança futura independente.

### 3. Credencial indexada por e-mail num item dedicado, não dentro do perfil
O acesso no login é por e-mail (não por `clienteId`), então precisa de uma chave de partição
diferente da usada pelo resto da single-table (`CLIENTE#{id}`). Novo item:

```
PK = EMAIL#{email (lowercase)}
SK = CREDENTIAL
clienteId: String (UUID gerado no signup)
passwordHash: String (BCrypt)
criadoEm: String (ISO-8601)
```

Signup faz um `PutItem` condicional (`attribute_not_exists(PK)`) pra garantir e-mail único sem
precisar de leitura prévia (evita race condition entre check-then-write). Login faz `GetItem` por
`PK=EMAIL#{email}` e compara a senha com `BCryptPasswordEncoder.matches`.

Alternativa descartada: guardar `passwordHash` dentro do item de perfil (`PK=CLIENTE#{id}`) — não
serve porque login não conhece o `clienteId` antes de autenticar (é o e-mail que chega do form).

### 4. Token sem refresh — expiração fixa, mesma troca já aceita no projeto
`CLAUDE.md` já documenta que o token fica em memória (não `localStorage`) e a sessão se perde no F5
por não haver refresh-token implementado. Mantém-se essa mesma postura: o JWT próprio tem expiração
fixa (proposta: 12h) e não há endpoint de refresh nesta mudança — consistente com o trade-off já
aceito, não uma regressão nova.

### 5. Endpoints REST simples no backend, sem novo bounded context
`POST /api/auth/signup { email, password }` → 201, sem body (ou `{ clienteId }`).
`POST /api/auth/login { email, password }` → 200 `{ token }` em caso de sucesso, 401 caso contrário.
Seguem o padrão hexagonal do projeto: `port/input/SignupUseCase` + `LoginUseCase`,
`application/AuthService`, `port/output/CredencialRepository`,
`adapter/output/dynamodb/CredencialItem`, `adapter/input/web/AuthController`.

## Risks / Trade-offs

- [Risco] Segredo HS256 fixo em texto plano no ambiente → mesmo nível de exposição que qualquer
  outra credencial de app hoje (ex: `backend.env` já carrega credenciais AWS em texto plano pro
  dev local). Mitigação: em prod, `JWT_SIGNING_SECRET` deve vir de um secret manager (fora do
  escopo desta mudança implementar isso, mas o design não impede).
- [Risco] Sem confirmação de e-mail, qualquer e-mail (inclusive inexistente) pode se cadastrar →
  aceitável pro estágio atual do produto (mesmo risco que qualquer form de signup sem verificação);
  não é pior que o comportamento já observado no bug "signup pré-confirmado" mencionado no memory
  da sessão anterior, que effectively já deixava a conta utilizável sem confirmação real em dev.
- [Trade-off] Perde-se a "defesa em profundidade" de dois validadores (API Gateway + backend) que
  o design original prometia — mas essa camada nunca foi testada de verdade (API Gateway com
  Cognito Authorizer não está implantado/validado em lugar nenhum, ver `0006-sem-deploy-aws-real.md`).
  Backend continua sendo o único validador real hoje; a mudança só torna isso explícito na spec.
- [Trade-off] Rotação de segredo HS256 invalida todos os tokens emitidos de uma vez (não há chave
  antiga/nova em paralelo como um JWKS com múltiplas chaves permitiria). Aceitável dado o Non-Goal
  de não implementar refresh token — usuário só precisa logar de novo.

## Migration Plan

1. Implementar backend (domain/use cases/adapter/controller) mantendo Cognito funcionando em
   paralelo (branch isolada, sem deploy).
2. Trocar `SecurityConfig.kt` pro `JwtDecoder`/`JwtEncoder` próprio.
3. Trocar frontend (`auth/cognito.ts` → novo client HTTP; remover `ConfirmSignUpPage.tsx` e rota).
4. Remover `amazon-cognito-identity-js` do `package.json`.
5. Remover `infra/terraform/modules/cognito/`, outputs relacionados, e ajustar
   `docker-init-apply.sh`/`docker-compose` pra não esperar mais Cognito no ministack.
6. Testar fluxo completo (signup → login → dashboard) local via docker-compose antes de mesclar.
7. Não há dado em produção pra migrar (projeto nunca teve deploy AWS real —
   `0006-sem-deploy-aws-real.md` —, então não há usuários/tokens Cognito existentes a considerar).
   Rollback, se necessário, é reverter o commit/branch.

## Open Questions

- `JWT_SIGNING_SECRET` em dev local: gerar um valor fixo versionado em `docker-init-apply.sh`/
  `.env.example`, ou exigir que cada dev gere o próprio? (Proposta: fixo pra dev, já que não é
  segredo de produção real — simplicidade > segurança nesse ambiente descartável.)
- Vale adicionar rate-limiting básico no `/api/auth/login` já nesta mudança, ou fica pra depois?
  (Proposta: fica pra depois — Non-Goal explícito acima.)
