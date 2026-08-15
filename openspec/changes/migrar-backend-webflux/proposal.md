## Why

O backend hoje é 100% bloqueante: Spring WebMVC (Tomcat, thread-per-request) chamando AWS SDK síncrono (DynamoDB Enhanced, S3, SQS) em toda a cadeia hexagonal. Cada requisição segura uma thread da plataforma inteira enquanto espera I/O de rede (Dynamo/S3/SQS), o que limita a capacidade de resposta sob carga concorrente. Migrar pra WebFlux (Netty + clients AWS assíncronos) remove esse bloqueio ponta a ponta.

Alternativa mais barata (virtual threads, nativa no Java 25 + Boot 4.1, zero mudança de código) foi considerada e descartada nesta exploração — decisão explícita do usuário por WebFlux completo, registrada em `design.md`.

## What Changes

- **BREAKING** (interno, não de API pública): troca de `spring-boot-starter-webmvc` por `spring-boot-starter-webflux`. Servidor embarcado passa de Tomcat pra Netty.
- Troca dos clients AWS SDK síncronos por assíncronos: `DynamoDbClient`→`DynamoDbAsyncClient` (+ `DynamoDbEnhancedAsyncClient`), `S3Client`→`S3AsyncClient`, `SqsClient`→`SqsAsyncClient`.
- Toda a cadeia hexagonal passa a usar `suspend fun` (Kotlin coroutines) em vez de funções bloqueantes: `port/output/*Repository`, `port/output/ArquivoStorage`, `port/output/EventoAnexoPublisher`, `port/input/*UseCase`, `application/*Service`, `adapter/output/dynamodb/*`, `adapter/output/s3/S3ArquivoStorage`, `adapter/output/sqs/SqsEventoAnexoPublisher`.
- Controllers (`PerfilController`, `AnexoController`, `SolicitacaoController`, `MeController`) viram `suspend fun`, suportado nativamente pelo Spring WebFlux com Kotlin.
- `SecurityConfig`: `HttpSecurity`/`SecurityFilterChain` (servlet) → `ServerHttpSecurity`/`SecurityWebFilterChain` (reativo).
- Nova dependência: `org.jetbrains.kotlinx:kotlinx-coroutines-reactor` (bridge entre `suspend fun` e o runtime Reactor do WebFlux) e `kotlinx-coroutines-jdk8` (bridge entre `CompletableFuture` dos clients AWS async e `suspend`, via `.await()`).
- Processo de refatoração documentado e seguido à risca com as skills `fullstack-dev-skills:spring-boot-engineer` (WebFlux, Security reativo) e `fullstack-dev-skills:kotlin-specialist` (coroutines/suspend idiomático) — sequência e papel de cada uma em `design.md`.
- Testes: `@WebMvcTest` (usado em `MeControllerTest`) migra pro equivalente reativo do Boot 4.1 (nome exato do pacote a confirmar na implementação — mesmo tipo de gotcha já documentado no `CLAUDE.md` pra outros pacotes movidos no Boot 4.1).

## Capabilities

### New Capabilities
- `backend-performance`: requisito não-funcional de que o backend processa requisições sem bloquear threads da plataforma esperando I/O externo (Dynamo/S3/SQS).

### Modified Capabilities
(nenhuma — client-auth, client-profile, attachments, service-requests mantêm o mesmo comportamento observável; só a forma de execução interna muda)

## Impact

- Todo `backend/src/main/kotlin/com/portalcliente/backend/` é tocado: config (`SecurityConfig`, `AwsClientConfig`), todos os `port/`, `application/`, `adapter/input/web/`, `adapter/output/*`.
- `backend/build.gradle.kts`: troca de starter web, novas dependências de coroutines.
- Testes existentes (`MeControllerTest`, `BackendApplicationTests`, e os demais slice tests) precisam da variante reativa das anotações de teste.
- Nenhuma mudança de infraestrutura Terraform, nenhuma mudança de contrato de API (mesmos endpoints, mesmos payloads, mesmos códigos de status).
