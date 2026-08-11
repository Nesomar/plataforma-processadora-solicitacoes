# CLAUDE.md

Contexto pra sessões futuras do Claude Code neste repo. Visão de produto e arquitetura completa:
[`README.md`](README.md) e [`docs/architecture.md`](docs/architecture.md).

## O que é

Portal de cliente: login (Cognito) → onboarding por etapas retomável → criar/acompanhar
solicitações reaproveitando o perfil. Backend Kotlin/Spring Boot hexagonal, frontend React,
DynamoDB single-table, infra Terraform, dev local via docker-compose + ministack.

## Comandos

```bash
# backend
cd backend && ./gradlew test          # compila + testa
cd backend && ./gradlew bootRun       # roda local (precisa das env vars — ver infra/README.md)

# frontend
cd frontend && npm test               # vitest
cd frontend && npm run build          # typecheck (tsc -b) + vite build
cd frontend && npm run dev            # dev server

# infra local
docker compose up -d ministack        # emulador AWS na porta 4566
```

## Convenções deste projeto

- **Branch + commit por fase.** Cada capability é uma branch `feature/{nome}` (ver `openspec/changes/archive/*/tasks.md`
  pro histórico de fases). Um commit ao final de cada etapa lógica, não por arquivo.
- **Code review ao final de cada etapa**, antes do commit — não só no fim do projeto inteiro.
- **Hexagonal em toda capability nova**: `domain/` (puro Kotlin, sem Spring/AWS) → `port/input/XUseCase`
  (interface) → `application/XService` (implementa o port, injeta `port/output/XRepository`) →
  `adapter/input/web/XController` e `adapter/output/*` (Dynamo/S3/SQS).
- **Pacotes `input`/`output`, nunca `in`/`out`** — são palavras reservadas em Kotlin, colidem se usadas
  como nome de pacote sem crase.
- **ID token, não access token** no `Authorization: Bearer`. O Cognito Authorizer do API Gateway
  confere a claim `aud`, que só existe no ID token (`frontend/src/auth/cognito.ts`).
- **Token em memória no frontend**, não `localStorage` (`frontend/src/auth/tokenStore.ts`) — reduz
  superfície de XSS. Trade-off: perde sessão no F5 (sem refresh-token silencioso implementado).
- **DynamoDB single-table**: `PK=CLIENTE#{id}`, `SK` varia (`PROFILE`, `SOLICITACAO#{id}`, `ANEXO#{id}`).
  Isolamento entre clientes vem da chave, não de checagem de ownership em código.

## Gotchas específicas deste ambiente/stack

- **Gradle trava neste sandbox.** Conexões HTTPS pro Maven Central às vezes ficam mortas
  (`CloseWait`) sem o cliente Java perceber — o build fica preso indefinidamente sem erro.
  - Se travar: mate os processos `java` residentes e rode nesse formato:
    `./gradlew test --no-daemon --console=plain` (sem daemon persistente = sem lock zumbi entre
    tentativas).
  - Depois de matar um processo à força, um build seguinte pode reportar `up-to-date` errado
    (cache corrompido pela morte abrupta) — se suspeitar disso, rode com `clean` uma vez pra
    garantir: `./gradlew clean test --no-daemon`.
- **Spring Boot 4.1 modularizou pacotes que eram monolíticos no Boot 3.** Não assuma o caminho
  antigo — confirme no jar antes de importar algo não usado ainda no projeto. Exemplos já
  descobertos na prática:
  - `@WebMvcTest` → `org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest`
  - `OAuth2ResourceServerAutoConfiguration` → `org.springframework.boot.security.oauth2.server.resource.autoconfigure.OAuth2ResourceServerAutoConfiguration`
  - `@TestConfiguration` → `org.springframework.boot.test.context.TestConfiguration`
  - Jackson é a v3, pacote `tools.jackson.*` (não `com.fasterxml.jackson.*`) — `ObjectMapper` é
    `tools.jackson.databind.ObjectMapper`.
  - Pra confirmar um pacote real: `unzip -l <jar-no-cache-do-gradle> | grep NomeDaClasse`.
- **DynamoDB Enhanced Client (Kotlin)**: `@DynamoDbBean` é
  `software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean` (não
  `.extensions.annotations`). Bean precisa de classe com propriedades `var` e getter público —
  data class Kotlin com defaults funciona.
- **ministack não confirma suporte a NLB/VPC Link** (só ALB é documentado com confiança). Esse
  trecho da infra (`modules/network` + `modules/api_gateway`) nunca foi testado de verdade — ver
  `infra/README.md` e `docs/decisions/0006-sem-deploy-aws-real.md`.
- **`PerfilService` não tem locking otimista** — dois PATCHes concorrentes pro mesmo clienteId
  podem se sobrescrever (read-modify-write simples). Mitigado no front com guarda contra
  double-submit; não resolvido no backend. Comentário `ponytail:` no código marca isso.

## Onde as coisas ficam

```
backend/src/main/kotlin/com/portalcliente/backend/
  domain/                 modelos + regras (Perfil, Anexo, Solicitacao, exceptions)
  port/input/             interfaces de use case (XUseCase)
  port/output/            interfaces de repositório/storage/publisher
  application/            implementação dos use cases (XService)
  adapter/input/web/      controllers REST + exception handler
  adapter/output/dynamodb/  repositórios DynamoDB + *Item (@DynamoDbBean)
  adapter/output/s3/       storage de anexo
  adapter/output/sqs/      publisher de evento de anexo
  config/                  Security, AwsClientConfig (DynamoDB/S3/SQS clients)

frontend/src/
  auth/                   cognito.ts (login), tokenStore.ts, ProtectedRoute.tsx
  api/                    clients por capability (perfilApi, anexosApi, solicitacoesApi) + httpClient (interceptors)
  pages/                  LoginPage, DashboardPage, SolicitacaoDetailPage
  pages/onboarding/       wizard (um form por etapa + OnboardingWizard orquestrando via gate do backend)

infra/terraform/modules/  network, cognito, dynamodb, s3, sqs, api_gateway, ecs

docs/
  architecture.md         diagramas + visão geral
  decisions/              ADRs leves (por que cada escolha não-óbvia foi feita)
```

## Specs

Requisitos formais por capability em `openspec/specs/{client-auth,client-profile,attachments,service-requests}/spec.md`.
Histórico do planejamento/implementação original em `openspec/changes/archive/2026-08-11-portal-cliente-solicitacoes/`.
