## 1. Esqueleto Spring/WebFlux (`fullstack-dev-skills:spring-boot-engineer`)

- [ ] 1.1 Trocar `spring-boot-starter-webmvc` por `spring-boot-starter-webflux` em `backend/build.gradle.kts`; adicionar `kotlinx-coroutines-reactor` e `kotlinx-coroutines-jdk8`.
- [ ] 1.2 Confirmar no jar os pacotes reativos equivalentes do Boot 4.1 (`@WebMvcTest`→reativo, autoconfig do OAuth2 Resource Server) antes de importar — mesmo processo já documentado no `CLAUDE.md`.
- [ ] 1.3 Migrar `config/SecurityConfig.kt` de `HttpSecurity`/`SecurityFilterChain` pra `ServerHttpSecurity`/`SecurityWebFilterChain`.
- [ ] 1.4 Migrar `config/AwsClientConfig.kt`: `DynamoDbClient`→`DynamoDbAsyncClient` (+ `DynamoDbEnhancedAsyncClient`), `S3Client`→`S3AsyncClient`, `SqsClient`→`SqsAsyncClient`.
- [ ] 1.5 Confirmar que o projeto builda (mesmo com use cases ainda bloqueantes) antes de seguir pra migração capability por capability.

## 2. Capability `client-profile` (Perfil)

- [ ] 2.1 `port/output/PerfilRepository.kt` e `adapter/output/dynamodb/PerfilDynamoDbRepository.kt` (+ `DynamoDbRepository.kt` base) viram `suspend fun`, usando `DynamoDbEnhancedAsyncClient` e `.await()`.
- [ ] 2.2 `port/input/PerfilUseCase.kt` e `application/PerfilService.kt` viram `suspend fun`.
- [ ] 2.3 `adapter/input/web/PerfilController.kt` vira `suspend fun` nos handlers.
- [ ] 2.4 Testes da capability rodando verde antes de seguir.

## 3. Capability `attachments` (Anexo)

- [ ] 3.1 `port/output/AnexoRepository.kt`, `ArquivoStorage.kt`, `EventoAnexoPublisher.kt` e seus adapters (`AnexoDynamoDbRepository`, `S3ArquivoStorage`, `SqsEventoAnexoPublisher`) viram `suspend fun`, usando `S3AsyncClient`/`SqsAsyncClient`.
- [ ] 3.2 `port/input/AnexoUseCase.kt` e `application/AnexoService.kt` viram `suspend fun`.
- [ ] 3.3 `adapter/input/web/AnexoController.kt` vira `suspend fun` nos handlers.
- [ ] 3.4 Testes da capability rodando verde antes de seguir.

## 4. Capability `service-requests` (Solicitação)

- [ ] 4.1 `port/output/SolicitacaoRepository.kt` e `adapter/output/dynamodb/SolicitacaoDynamoDbRepository.kt` viram `suspend fun`.
- [ ] 4.2 `port/input/SolicitacaoUseCase.kt` e `application/SolicitacaoService.kt` viram `suspend fun`.
- [ ] 4.3 `adapter/input/web/SolicitacaoController.kt` vira `suspend fun` nos handlers.
- [ ] 4.4 Testes da capability rodando verde antes de seguir.

## 5. `MeController` e fechamento da migração de framework

- [ ] 5.1 `adapter/input/web/MeController.kt` vira `suspend fun`; `MeControllerTest.kt` migra de `@WebMvcTest` pro equivalente reativo (task 1.2).
- [ ] 5.2 `adapter/input/web/WebExceptionHandler.kt`: confirmar que `@ExceptionHandler` funciona igual em WebFlux (handlers anotados são suportados nativamente) ou migrar pro equivalente reativo se necessário.
- [ ] 5.3 `cd backend && ./gradlew test --no-daemon --console=plain` — suite inteira passando.

## 6. Revisão de idiomaticidade Kotlin (`fullstack-dev-skills:kotlin-specialist`)

- [ ] 6.1 Revisar toda a cadeia hexagonal migrada — uso correto de `suspend`/`.await()`, oportunidades reais de `Flow<T>` em vez de `List<T>` materializada (caso a caso, ver Open Questions do `design.md`).
- [ ] 6.2 Aplicar os ajustes apontados pela revisão.

## 7. Verificação final

- [ ] 7.1 `cd backend && ./gradlew clean test --no-daemon --console=plain` — suite completa, sem cache residual.
- [ ] 7.2 Rodar local via docker-compose + ministack, testar manualmente os 4 fluxos (login, onboarding, anexo, solicitação) ponta a ponta.
- [ ] 7.3 Confirmar `/actuator/health` retorna `UP`.
- [ ] 7.4 Confirmar contrato de API inalterado (specs/backend-performance/spec.md) — mesmos status codes e payloads dos testes existentes.
- [ ] 7.5 Code review desta etapa antes do commit (convenção do projeto).
