---
name: spring-boot4-package-check
description: Confirma o pacote real de uma classe Spring Boot 4.1/Jackson v3 antes de importar. Use quando for importar algo do Spring Boot ainda não usado no projeto (WebMvcTest, OAuth2ResourceServerAutoConfiguration, TestConfiguration, ObjectMapper, etc) — Boot 4.1 remapeou pacotes que eram monolíticos no Boot 3.
---

Spring Boot 4.1 modularizou pacotes que eram monolíticos no Boot 3. Não assuma o caminho antigo.
Exemplos já descobertos neste projeto (ver `CLAUDE.md`):

- `@WebMvcTest` → `org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest`
- `OAuth2ResourceServerAutoConfiguration` → `org.springframework.boot.security.oauth2.server.resource.autoconfigure.OAuth2ResourceServerAutoConfiguration`
- `@TestConfiguration` → `org.springframework.boot.test.context.TestConfiguration`
- Jackson é v3, pacote `tools.jackson.*` (não `com.fasterxml.jackson.*`) — `ObjectMapper` é
  `tools.jackson.databind.ObjectMapper`.

## Procedimento pra confirmar um pacote antes de importar

1. Ache o jar no cache do Gradle:
   ```bash
   find ~/.gradle/caches -iname "spring-boot-*<modulo>*.jar" 2>/dev/null
   # ou, se GRADLE_USER_HOME custom (ver CLAUDE.md, Scoop):
   find "$GRADLE_USER_HOME/caches" -iname "*.jar" 2>/dev/null | grep -i <modulo>
   ```
2. Liste as classes dentro pra achar o pacote real:
   ```bash
   unzip -l <jar-encontrado> | grep NomeDaClasse
   ```
3. Só então escreva o `import` com o pacote confirmado.

Nunca importe um pacote Boot 3 "de memória" neste projeto — confirme no jar primeiro.
