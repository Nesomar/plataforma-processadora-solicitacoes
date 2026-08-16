FROM eclipse-temurin:25-jdk AS build
WORKDIR /app
COPY gradlew settings.gradle.kts build.gradle.kts ./
COPY gradle ./gradle
RUN ./gradlew --no-daemon dependencies || true
COPY src ./src
RUN ./gradlew --no-daemon bootJar

FROM eclipse-temurin:25-jre
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar
EXPOSE 8080
# lê /env/backend.env (gerado pelo terraform-init, montado via volume) no start do container —
# não depende do mecanismo `env_file:` do compose, que resolve o arquivo antes dele existir na
# primeira subida (ver docker-compose.yml)
ENTRYPOINT ["sh", "-c", "set -a; [ -f /env/backend.env ] && . /env/backend.env; set +a; exec java -jar app.jar"]
