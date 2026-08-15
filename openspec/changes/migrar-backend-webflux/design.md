## Context

Backend atual (`backend/src/main/kotlin/com/portalcliente/backend/`), confirmado lendo o código:
- `build.gradle.kts`: `spring-boot-starter-webmvc` (Tomcat), AWS SDK v2 (`dynamodb-enhanced`, `s3`, `sqs`) — todos os clients configurados em `config/AwsClientConfig.kt` são síncronos (`DynamoDbClient`, `S3Client`, `SqsClient`).
- `adapter/output/dynamodb/DynamoDbRepository.kt` (base de todos os repositórios): `table.getItem(...)`, `table.putItem(...)`, `table.query(...).items().toList()` — tudo bloqueante.
- `adapter/output/s3/S3ArquivoStorage.kt`: `s3Client.putObject(...)` síncrono.
- `application/PerfilService.kt` (e `AnexoService`, `SolicitacaoService` no mesmo padrão): funções diretas, sem `suspend`, sem `Mono`/`Flux`.
- `adapter/input/web/PerfilController.kt` (e demais controllers): retornam `ResponseEntity<T>`/`T` direto, não `Mono<ResponseEntity<T>>`.
- `config/SecurityConfig.kt`: `HttpSecurity` + `SecurityFilterChain` (servlet), JWT via `oauth2ResourceServer { jwt {} }` autoconfigurado.
- Boot 4.1 + Kotlin 2.3 + Java 25 toolchain (já usa Kotlin recente e JDK com virtual threads disponíveis, mas a decisão desta change foi WebFlux completo, não virtual threads — ver "Alternativas rejeitadas").
- CLAUDE.md já documenta que Boot 4.1 modularizou pacotes que eram monolíticos no Boot 3 (`@WebMvcTest`, `OAuth2ResourceServerAutoConfiguration`, Jackson v3 em `tools.jackson.*`) — o mesmo cuidado se aplica aos pacotes reativos equivalentes, que ainda não foram confirmados no jar (ver Open Questions).

## Goals / Non-Goals

**Goals:**
- Backend inteiro (web + AWS I/O) não-bloqueante, ponta a ponta, sem thread de plataforma presa esperando Dynamo/S3/SQS.
- Zero mudança de contrato de API (mesmos endpoints, payloads, status codes — ver `specs/backend-performance/spec.md`).
- Código idiomático Kotlin: `suspend fun` em vez de `Mono`/`Flux` explícitos em toda a camada de aplicação/domínio.

**Non-Goals:**
- Mudar infraestrutura Terraform (ECS/ALB/NLB já servem tanto Tomcat quanto Netty sem mudança).
- Adotar virtual threads (alternativa avaliada e descartada nesta exploração — ver Decisões).
- Mudar o modelo de dados DynamoDB (single-table) ou qualquer regra de domínio.

## Decisions

### D1 — WebFlux completo, não virtual threads
Avaliadas duas rotas pro mesmo problema (thread bloqueada em I/O): WebFlux+clients async (reescrita grande, stack reativa de verdade) vs. `spring.threads.virtual.enabled=true` (Java 25 já suporta, zero mudança de código, resolve o mesmo gargalo de forma muito mais barata). Decisão explícita do usuário: WebFlux completo. Trade-off aceito conscientemente: esforço de reescrita muito maior em troca de um modelo de concorrência reativo "de verdade" (backpressure, composição de streams), não só troca de agendador de thread.

### D2 — Kotlin coroutines (`suspend fun`), não Mono/Flux explícito
Spring WebFlux suporta ambos os estilos nativamente. Coroutines foi escolhido por ser mais idiomático em Kotlin e mais legível (esconde a complexidade de composição reativa atrás de `suspend`/`await`). Ponte necessária:
- `kotlinx-coroutines-reactor`: permite controllers/services `suspend fun` rodarem sobre o runtime Reactor do WebFlux sem código reativo explícito.
- `kotlinx-coroutines-jdk8`: os clients AWS SDK async (`DynamoDbAsyncClient`, `S3AsyncClient`, `SqsAsyncClient`) retornam `CompletableFuture<T>`, não `Mono<T>` — a extensão `.await()` faz a ponte pra `suspend fun`.
**Alternativa rejeitada**: `Mono`/`Flux` explícito em toda a cadeia — mais verboso em Kotlin, rejeitado pelo usuário nesta exploração.

### D3 — troca de clients AWS SDK, mantendo a mesma família (SDK Java v2 async), não `aws-sdk-kotlin`
`DynamoDbEnhancedAsyncClient` existe no mesmo módulo `dynamodb-enhanced` já usado (só troca o builder), assim como `S3AsyncClient`/`SqsAsyncClient` já estão nos módulos `s3`/`sqs` já declarados no `build.gradle.kts`. Não há necessidade de trocar pra `aws-sdk-kotlin` (SDK Kotlin-nativo da AWS, projeto separado) — trocaria toda a API de chamada (builders, exceptions) sem ganho real, já que a ponte coroutines-CompletableFuture (D2) resolve a interop.

### D4 — sequência de skills: `spring-boot-engineer` → `kotlin-specialist`
```
┌────────────────────┐   estrutura Spring/WebFlux    ┌─────────────────────┐
│ spring-boot-engineer│ ──(controllers, Security      │  kotlin-specialist   │
│  (framework)         │   reativo, config, testes)──▶│  (idiomatic Kotlin)  │
└────────────────────┘                                └─────────────────────┘
```
1. **`spring-boot-engineer`** primeiro: monta o esqueleto Spring/WebFlux — troca de starter, `ServerHttpSecurity`/`SecurityWebFilterChain` em `SecurityConfig`, clients async em `AwsClientConfig`, assinatura `suspend fun` nos controllers, adaptação dos testes de slice (`@WebMvcTest`→ equivalente reativo). Essa skill cobre explicitamente WebFlux e Spring Security 6 reativo na própria descrição.
2. **`kotlin-specialist`** em seguida, sobre o resultado do passo 1: revisa/refina o uso de coroutines em toda a cadeia hexagonal (ports, services, adapters) — idiomaticidade de `suspend fun`, uso correto de `.await()` na ponte com os clients AWS async, e se algum ponto deveria usar `Flow` (ex.: streaming de itens do DynamoDB) em vez de `List` materializada.

**Por que essa ordem**: a estrutura Spring (onde entra `suspend`, como o WebFlux invoca controllers Kotlin, como fica a Security reativa) precisa existir antes de refinar o estilo Kotlin por cima dela — não dá pra revisar idiomaticidade de coroutines em código que ainda não decidiu se é WebFlux ou não.

## Risks / Trade-offs

- **Reescrita toca praticamente todo arquivo do backend** (todos os `port/`, `application/`, `adapter/*`) → risco de regressão ampla. Mitigação: migrar capability por capability (client-profile → attachments → service-requests → me), rodando a suíte de testes após cada uma, não tudo de uma vez.
- **Pacotes reativos do Boot 4.1 ainda não confirmados** (equivalente de `@WebMvcTest` pra WebFlux, autoconfig reativa do OAuth2 Resource Server) → mesmo padrão de risco que o projeto já documentou no `CLAUDE.md` pra outros pacotes movidos. Mitigação: confirmar via `unzip -l <jar> | grep NomeDaClasse` antes de importar, como já é convenção do projeto.
- **DynamoDB Enhanced Client async tem API ligeiramente diferente do síncrono** (retorno `SdkPublisher`/`CompletableFuture` em vez de objeto direto) → mitigação: usar `.await()` (item único) e coletar `SdkPublisher` em lista/`Flow` conforme a necessidade real de cada query (a maioria das queries aqui já são pequenas — perfil, poucas solicitações por cliente — não há necessidade real de streaming verdadeiro).
- **`ponytail:` no `PerfilService`** (read-modify-write sem locking otimista) continua existindo depois da migração — WebFlux não resolve nem piora essa race condition, só muda a mecânica de concorrência. Não é escopo desta change.

## Migration Plan

1. `spring-boot-engineer`: trocar starter, `AwsClientConfig` (clients async), `SecurityConfig` (reativo) — projeto ainda não compila nesse meio-tempo, é esperado.
2. Migrar capability por capability (menor primeiro): `client-profile` (Perfil) → `attachments` (Anexo) → `service-requests` (Solicitação) → `client-auth`/`MeController`. Cada uma: port → service → adapter → controller → testes daquela capability rodando verde antes de seguir pra próxima.
3. `kotlin-specialist`: passada de revisão em cima do resultado completo.
4. Rollback: reverter a branch da change inteira — não há dado persistido que mude de formato (DynamoDB, S3, SQS continuam com o mesmo shape), então rollback é só reverter código.

## Open Questions

- Nome exato do pacote reativo equivalente a `@WebMvcTest` e da autoconfig reativa do OAuth2 Resource Server no Boot 4.1 — confirmar no jar durante a implementação (mesmo processo que o projeto já usa pros outros pacotes movidos).
- Se algum ponto de leitura do DynamoDB deveria virar `Flow<T>` em vez de `List<T>` materializada — decisão que fica pra revisão do `kotlin-specialist` (D4, passo 2), caso a caso.
