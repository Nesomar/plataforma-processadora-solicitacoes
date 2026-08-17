## 1. Esqueleto Spring/WebFlux (`fullstack-dev-skills:spring-boot-engineer`)

- [x] 1.1 Trocar `spring-boot-starter-webmvc` por `spring-boot-starter-webflux` em `backend/build.gradle.kts`; adicionar `kotlinx-coroutines-reactor` e `kotlinx-coroutines-jdk8`.
- [x] 1.2 Confirmar no jar os pacotes reativos equivalentes do Boot 4.1 (`@WebMvcTest`→reativo, autoconfig do OAuth2 Resource Server) antes de importar — mesmo processo já documentado no `CLAUDE.md`.
- [x] 1.3 Migrar `config/SecurityConfig.kt` de `HttpSecurity`/`SecurityFilterChain` pra `ServerHttpSecurity`/`SecurityWebFilterChain`.
- [x] 1.4 Migrar `config/AwsClientConfig.kt`: `DynamoDbClient`→`DynamoDbAsyncClient` (+ `DynamoDbEnhancedAsyncClient`), `S3Client`→`S3AsyncClient`, `SqsClient`→`SqsAsyncClient`.
- [x] 1.5 Confirmar que o projeto builda (mesmo com use cases ainda bloqueantes) antes de seguir pra migração capability por capability.

## 2. Capability `client-profile` (Perfil)

- [x] 2.1 `port/output/PerfilRepository.kt` e `adapter/output/dynamodb/PerfilDynamoDbRepository.kt` (+ `DynamoDbRepository.kt` base) viram `suspend fun`, usando `DynamoDbEnhancedAsyncClient` e `.await()`.
- [x] 2.2 `port/input/PerfilUseCase.kt` e `application/PerfilService.kt` viram `suspend fun`.
- [x] 2.3 `adapter/input/web/PerfilController.kt` vira `suspend fun` nos handlers.
- [x] 2.4 Testes da capability rodando verde antes de seguir. Nota: base `DynamoDbRepository` migrada em 2.1 quebra compilação de `Anexo`/`Solicitacao`/`Credencial` (todos estendem a mesma base) até suas próprias fases migrarem — "verde" isolado por capability não é possível; validado no checkpoint final (7.1).

## 3. Capability `attachments` (Anexo)

- [x] 3.1 `port/output/AnexoRepository.kt`, `ArquivoStorage.kt`, `EventoAnexoPublisher.kt` e seus adapters (`AnexoDynamoDbRepository`, `S3ArquivoStorage`, `SqsEventoAnexoPublisher`) viram `suspend fun`, usando `S3AsyncClient`/`SqsAsyncClient`.
- [x] 3.2 `port/input/AnexoUseCase.kt` e `application/AnexoService.kt` viram `suspend fun`.
- [x] 3.3 `adapter/input/web/AnexoController.kt` vira `suspend fun` nos handlers. Nota adicional: `MultipartFile` (servlet) não existe em WebFlux — trocado por `FilePart` (`@RequestPart`), bytes lidos via `DataBufferUtils.join(...).awaitSingle()`.
- [x] 3.4 Testes da capability rodando verde antes de seguir (mesma ressalva da 2.4 — compilação isolada por capability não é possível dado a base compartilhada).

## 4. Capability `service-requests` (Solicitação)

- [x] 4.1 `port/output/SolicitacaoRepository.kt` e `adapter/output/dynamodb/SolicitacaoDynamoDbRepository.kt` viram `suspend fun`.
- [x] 4.2 `port/input/SolicitacaoUseCase.kt` e `application/SolicitacaoService.kt` viram `suspend fun`.
- [x] 4.3 `adapter/input/web/SolicitacaoController.kt` vira `suspend fun` nos handlers.
- [x] 4.4 Testes da capability rodando verde antes de seguir (mesma ressalva das fases anteriores).

## 5. `MeController` e fechamento da migração de framework

- [x] 5.1 `adapter/input/web/MeController.kt` vira `suspend fun`; `MeControllerTest.kt` migra de `@WebMvcTest` pro equivalente reativo (task 1.2). Nota: `AuthControllerTest.kt` (mesmo padrão `@WebMvcTest`/`MockMvc`, não listado no tasks.md original) também migrado — `@WebFluxTest` + `WebTestClient` + `SecurityMockServerConfigurers.mockJwt()`.
- [x] 5.2 `adapter/input/web/WebExceptionHandler.kt`: `@ExceptionHandler` funciona igual em WebFlux, sem mudança nos handlers de domínio. `MaxUploadSizeExceededException` (servlet-only) trocado por `DataBufferLimitException` (reativo); `application.yml` migrado de `spring.servlet.multipart.max-file-size` pra `spring.webflux.multipart.max-in-memory-size`/`max-disk-usage-per-part`.
- [x] 5.3 `cd backend && ./gradlew test --no-daemon --console=plain` — suite inteira passando. Dois bugs adicionais achados e corrigidos nesta etapa: (1) `securityWebFilterChain` chamava `http.build()` duas vezes (`ServerHttpSecurityDsl.invoke` já builda internamente — igual ao `HttpSecurity` servlet, mas lá `invoke` NÃO builda, então o padrão antigo não é transferível 1:1); (2) `corsConfigurationSource()`/`UrlBasedCorsConfigurationSource` estavam com os tipos servlet (`org.springframework.web.cors.*`) — WebFlux exige os tipos reativos (`org.springframework.web.cors.reactive.*`), senão falha em runtime com `NoClassDefFoundError: jakarta/servlet/ServletRequest`. Também trocado `JwtDecoder`→`ReactiveJwtDecoder`/`NimbusReactiveJwtDecoder` (o resource server reativo procura esse tipo de bean, não o servlet).

## 6. Revisão de idiomaticidade Kotlin (`fullstack-dev-skills:kotlin-specialist`)

- [x] 6.1 Revisar toda a cadeia hexagonal migrada — uso correto de `suspend`/`.await()`, oportunidades reais de `Flow<T>` em vez de `List<T>` materializada (caso a caso, ver Open Questions do `design.md`).
- [x] 6.2 Nenhum ajuste necessário: queries do domínio são pequenas (perfil único, poucas solicitações/anexos por cliente — confirmado no design.md), `List<T>` materializada via `.asFlow().toList()` é suficiente; não há ponto real de streaming. `.await()`/`suspend` usados de forma consistente em toda a cadeia (ports → services → controllers).

## 7. Verificação final

- [x] 7.1 `cd backend && ./gradlew clean test --no-daemon --console=plain` — suite completa, sem cache residual.
- [x] 7.2 Rodar local via docker-compose + ministack, testar manualmente os 4 fluxos (login, onboarding, anexo, solicitação) ponta a ponta. Bug adicional (pré-existente, não introduzido por esta change) achado e corrigido: `S3AsyncClient`/`S3Client` nunca setavam `forcePathStyle`, então upload de anexo contra o ministack falhava com `UnknownHostException` (addressing virtual-hosted-style tentando resolver `<bucket>.<host>` como DNS). Corrigido em `AwsClientConfig.kt` — só ativa quando `endpointOverride` está setado (dev local), nunca em produção.
- [x] 7.3 Confirmar `/actuator/health` retorna `UP`. Confirmado (200, `{"status":"UP"}`).
- [x] 7.4 Confirmar contrato de API inalterado (specs/backend-performance/spec.md) — mesmos status codes e payloads dos testes existentes. Confirmado manualmente (mesmos status/shapes de signup 201, login 200, onboarding 204/200, anexo 201, solicitação 201/200) e pela suíte de testes automatizados intacta.
- [x] 7.5 Code review desta etapa antes do commit (convenção do projeto). 5 achados, todos corrigidos: (1) BCrypt (`PasswordEncoder.encode`/`matches`) bloqueava a event-loop do Netty — envolvido em `withContext(Dispatchers.IO)` em `AuthService.kt`; (2) upload de anexo vazio (0 bytes) derrubava com `NoSuchElementException` não tratada — `AnexoController.bytes()` trocado pra `awaitSingleOrNull() ?: ByteArray(0)`; (3) `spring.webflux.multipart` não tem equivalente a `max-request-size` agregado do servlet — mitigado com `max-parts: 4`; (4) `contentType` via `MediaType.toString()` (normalizado) podia divergir do header cru — trocado pra `headers().getFirst(HttpHeaders.CONTENT_TYPE)`; (5) branch morto `catch (CompletionException)` em `DynamoDbRepository.saveIfNotExists` (kotlinx-coroutines `.await()` já desembrulha) — removido. Revalidado com `./gradlew clean test` (verde) e reteste manual do fluxo de anexo via docker-compose (upload normal, arquivo vazio, content-type com parâmetro — todos com o comportamento esperado, nenhum 500).
