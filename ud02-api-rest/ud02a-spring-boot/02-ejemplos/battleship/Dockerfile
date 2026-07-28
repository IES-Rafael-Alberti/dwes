# == Etapa 1: compilar y verificar ==
FROM maven:3.9.11-eclipse-temurin-25 AS builder
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn --batch-mode clean verify

# == Etapa 2: ejecutar ==
FROM eclipse-temurin:25-jre
RUN groupadd --system battleship \
    && useradd --system --gid battleship battleship \
    && mkdir --parents /var/log/battleship \
    && chown battleship:battleship /var/log/battleship
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar
USER battleship
EXPOSE 8443
ENTRYPOINT ["java", "-jar", "app.jar"]
