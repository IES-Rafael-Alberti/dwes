# == Etapa 1: compilar ==
FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# == Etapa 2: ejecutar ==
FROM eclipse-temurin:21-jre-alpine
RUN addgroup -S battleship && adduser -S battleship -G battleship
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar
USER battleship
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
