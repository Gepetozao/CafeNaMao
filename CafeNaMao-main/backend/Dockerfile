# syntax=docker/dockerfile:1

# Etapa 1: build e testes com Maven + Java 21.
FROM maven:3.9-eclipse-temurin-21 AS build

WORKDIR /workspace

# Copia primeiro os arquivos de build; o código vem depois para preservar camadas.
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN chmod +x mvnw

COPY src/ src/

# `verify` executa os testes JUnit e gera o relatório JaCoCo.
RUN ./mvnw -B verify

# Etapa 2: imagem de execução enxuta, com usuário não privilegiado.
FROM eclipse-temurin:21-jre

WORKDIR /app

# curl é usado pelo healthcheck do Compose/CI.
RUN apt-get update \
    && apt-get install -y --no-install-recommends curl \
    && rm -rf /var/lib/apt/lists/* \
    && groupadd --system cafe \
    && useradd --system --gid cafe --home-dir /app cafe \
    && mkdir -p /app/data \
    && chown -R cafe:cafe /app

COPY --from=build /workspace/target/cafe-na-mao-api-0.1.0-SNAPSHOT.jar /app/app.jar

USER cafe

EXPOSE 8080

HEALTHCHECK --interval=10s --timeout=3s --start-period=30s --retries=6 \
    CMD curl -fsS http://localhost:8080/api/bebidas > /dev/null || exit 1

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
