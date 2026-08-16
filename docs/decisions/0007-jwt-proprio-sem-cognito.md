# 7. JWT próprio (HS256), sem Cognito

**Status:** aceito — substitui [`0005-auth-authorizer-e-revalidacao.md`](0005-auth-authorizer-e-revalidacao.md)

## Contexto

O Cognito (real ou emulado no ministack) recriava pool/client a cada `terraform apply`/
`docker compose up --build`, gerando IDs novos toda vez. O frontend lia esses IDs de um `.env.local`
que precisava ser atualizado manualmente após cada rebuild, e o issuer/JWKS do ID token divergia
entre `localhost` (browser) e `ministack` (hostname interno do docker-compose) — três incidentes
reais na mesma sessão de dev (`.env.local` desatualizado, mismatch de `iss`, credenciais AWS
ausentes). Toda essa fragilidade existia só por causa da emulação do Cognito, não do domínio do
produto — ver `openspec/changes/archive/*/remover-cognito-auth-propria/proposal.md`.

## Decisão

Backend emite e valida seu próprio JWT (HS256, segredo simétrico via `JWT_SIGNING_SECRET`),
mantendo o mesmo contrato que os controllers já consumem (`sub` = `clienteId`). Cadastro
(`POST /api/auth/signup`) e login (`POST /api/auth/login`) passam a ser endpoints do próprio
backend, com credencial (email + hash BCrypt) persistida no DynamoDB (`PK=EMAIL#{email}`).
Conta fica ativa imediatamente no signup — sem confirmação por e-mail (essa etapa só existia por
causa do Cognito/SES).

## Raciocínio

Emissor e validador são o mesmo processo — RS256 + endpoint JWKS próprio recriaria exatamente o
problema que motivou a mudança (mais infraestrutura pra sincronizar). Um segredo HS256 fixo,
gerado uma vez, elimina toda a superfície de sincronização. `NimbusJwtEncoder`/`NimbusJwtDecoder`
com chave compartilhada são built-in do Spring Security — sem dependência nova.

## Trade-offs aceitos

- Sem "defesa em profundidade" de dois validadores (API Gateway + backend) — mas essa camada nunca
  foi testada de verdade (ver `0006-sem-deploy-aws-real.md`); o backend já era o único validador
  real.
- Rotação de segredo invalida todos os tokens de uma vez (sem chave antiga/nova em paralelo como
  um JWKS multi-chave permitiria) — aceitável, não há refresh token nesta fase.
- Segredo fixo em texto plano no ambiente — mesmo nível de exposição de qualquer outra credencial
  de app hoje; em produção deve vir de secret manager (fora do escopo desta mudança implementar).
