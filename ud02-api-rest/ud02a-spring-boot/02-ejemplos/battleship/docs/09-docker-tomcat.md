# Sesión 9 — Docker, Tomcat tuning y alternativas

## Antes de empezar

Asegurate de tener instalados:

- Docker y Docker Compose
- `mvn` (para compilar local) o confiá en que el multi-stage build lo hace dentro del contenedor

Conceptos previos (sesión 8): perfiles Spring, `application-prod.yml`.

## 1. Docker multi-stage: de JAR a imagen

### 1.1 Dockerfile

```dockerfile
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
```

Señalar:

- **Multi-stage**: la imagen final solo tiene el JRE (120 MB vs 350+ MB con JDK)
- **Usuario no root**: `adduser -S battleship` — seguridad básica
- El JAR se copia sin capas intermedias de Maven

### 1.2 Perfil Docker en application.yml

```yaml
# application-docker.yml
spring:
  datasource:
    url: jdbc:postgresql://db:5432/battleship
    username: battleship
    password: ${DB_PASSWORD}

app:
  security:
    jwt:
      private-key: ${JWT_PRIVATE_KEY}
      public-key: ${JWT_PUBLIC_KEY}

logging:
  level:
    com.example.battleship: INFO
```

### 1.3 docker-compose.yml

```yaml
version: "3.9"

services:
  db:
    image: postgres:16-alpine
    environment:
      POSTGRES_DB: battleship
      POSTGRES_USER: battleship
      POSTGRES_PASSWORD: ${DB_PASSWORD}
    volumes:
      - pgdata:/var/lib/postgresql/data
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U battleship"]
      interval: 5s
      timeout: 3s
      retries: 5

  app:
    build: .
    ports:
      - "8443:8443"
    environment:
      SPRING_PROFILES_ACTIVE: prod,docker
      DB_PASSWORD: ${DB_PASSWORD}
      JWT_PRIVATE_KEY: ${JWT_PRIVATE_KEY}
      JWT_PUBLIC_KEY: ${JWT_PUBLIC_KEY}
      SSL_KEYSTORE_PATH: ${SSL_KEYSTORE_PATH}
      SSL_KEYSTORE_PASSWORD: ${SSL_KEYSTORE_PASSWORD}
    depends_on:
      db:
        condition: service_healthy
    volumes:
      - ./keys:/app/keys:ro

volumes:
  pgdata:
```

Señalar:

- `condition: service_healthy` — la app espera a que PostgreSQL esté listo
- Perfiles combinados: `prod,docker` — Spring mergea ambas configuraciones
- Variables de entorno desde un `.env` (que no se sube al repo)

### 1.4 .env (no subir a git)

```env
DB_PASSWORD=secreto123
JWT_PRIVATE_KEY=file:/app/keys/private.pem
JWT_PUBLIC_KEY=file:/app/keys/public.pem
SSL_KEYSTORE_PATH=file:/app/keys/battleship.p12
SSL_KEYSTORE_PASSWORD=cambiar
```

### 1.5 .dockerignore

```dockerignore
target/
.env
keys/*.pem
keys/*.p12
.git
```

### 1.6 Construir y ejecutar

```bash
# Construir
docker compose build

# Ejecutar
docker compose up -d

# Ver logs
docker compose logs -f app

# Detener
docker compose down -v
```

## 2. Tomcat tuning

Spring Boot usa Tomcat embebido. Por defecto viene con valores conservadores que van bien para desarrollo pero se quedan cortos en producción.

### 2.1 Connection pool (HikariCP)

HikariCP ya viene con Spring Boot. Lo ajustamos en `application-prod.yml`:

```yaml
spring:
  datasource:
    hikari:
      maximum-pool-size: 20
      minimum-idle: 5
      idle-timeout: 300000
      connection-timeout: 20000
      max-lifetime: 1200000
```

| Parámetro | Valor | Por qué |
|-----------|-------|---------|
| `maximum-pool-size` | 20 | Conexiones concurrentes máximas |
| `minimum-idle` | 5 | Mantiene 5 listas siempre |
| `connection-timeout` | 20s | Tiempo máximo esperando conexión |
| `max-lifetime` | 20 min | Renovar conexiones viejas |
| `idle-timeout` | 5 min | Liberar conexiones no usadas |

### 2.2 Server (Tomcat embebido)

```yaml
server:
  tomcat:
    threads:
      max: 200          # Hilos de procesamiento máximos
      min-spare: 10     # Hilos mínimos esperando
    max-connections: 8192
    accept-count: 100
    connection-timeout: 5000
```

| Parámetro | Valor | Por qué |
|-----------|-------|---------|
| `threads.max` | 200 | Requests simultáneas máximas |
| `max-connections` | 8192 | Conexiones TCP aceptadas en cola |
| `accept-count` | 100 | Cola de espera cuando todos los hilos están ocupados |
| `connection-timeout` | 5s | Tiempo máximo para recibir la request |

Señalar: más hilos NO es siempre mejor. Si tenés 200 hilos y todos hacen queries lentas, la CPUs se satura de context switching. Monitorizá con Actuator y ajustá.

### 2.3 Access log de Tomcat

```yaml
server:
  tomcat:
    accesslog:
      enabled: true
      directory: /var/log/battleship
      pattern: "%h %l %u %t \"%r\" %s %b %D"  # %D = tiempo en ms
      rotate: true
      prefix: access
      suffix: .log
```

Esto genera logs como:

```
192.168.1.1 - - [07/Jul/2026:10:15:30 +0000] "QUERY /api/games/1/attacks HTTP/1.1" 200 1245 23
```

El último campo (`%D`) es el tiempo de respuesta en milisegundos. Fundamental para detectar endpoints lentos.

### 2.4 Verificar configuración con Actuator

```bash
curl http://localhost:8080/actuator/health
curl http://localhost:8080/actuator/metrics/tomcat.threads.busy
curl http://localhost:8080/actuator/metrics/hikaricp.connections.active
```

Señalar: si `tomcat.threads.busy` se acerca a `threads.max`, necesitás escalar (más hilos, o más instancias).

## 3. Alternativas a Tomcat

Spring Boot permite cambiar de servidor embebido cambiando una dependencia:

| Servidor | Starter | Perfil |
|----------|---------|--------|
| Tomcat | `spring-boot-starter-webmvc` (por defecto) | Gralista, más extensiones |
| Jetty | `spring-boot-starter-webmvc-jetty` | Más ligero, mejor para WebSocket |
| Undertow | `spring-boot-starter-webmvc-undertow` | Más alto rendimiento I/O |

### Cambiar a Undertow

Excluir Tomcat, incluir Undertow:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-webmvc</artifactId>
    <exclusions>
        <exclusion>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-tomcat</artifactId>
        </exclusion>
    </exclusions>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-webmvc-undertow</artifactId>
</dependency>
```

Configuración de Undertow:

```yaml
server:
  undertow:
    threads:
      io: 4              # Hilos de I/O (CPU cores)
      worker: 200        # Hilos de trabajo
    buffer-size: 16384
    direct-buffers: true
```

Señalar:

- **Undertow** es más rápido en I/O no bloqueante (peticiones largas, streaming)
- **Jetty** es ideal si usás WebSocket o necesitás embedder programático
- **Tomcat** es el más conocido, con más soporte y extensiones (JSF, JSP)
- En la práctica, para APIs REST, cualquiera de los tres funciona bien. La diferencia real está en la configuración, no en el servidor.

## 4. DevOps: CI/CD (visión general)

El profe de despliegue lo verá en detalle, pero el pipeline típico para esta app:

```yaml
# .github/workflows/deploy.yml (visión general)
name: Build and Deploy

on:
  push:
    branches: [main]

jobs:
  test:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - uses: actions/setup-java@v4
        with:
          java-version: 21
          distribution: temurin
      - run: mvn verify

  docker:
    needs: test
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - run: docker build -t battleship .
      - run: docker push registry.example.com/battleship:latest

  deploy:
    needs: docker
    runs-on: ubuntu-latest
    steps:
      - run: ssh server "docker compose pull && docker compose up -d"
```

Conceptos clave que verán en despliegue:

- **GitHub Actions / GitLab CI** — ejecutar tests automáticamente
- **SonarQube** — calidad de código y seguridad
- **Trivy / Snyk** — escaneo de vulnerabilidades en dependencias
- **Health checks** en el orquestador (Kubernetes liveness/readiness probes)

> En clase nos centramos en la app. El CI/CD lo coordina el módulo de despliegue.

## Lo que vimos hoy

| Concepto | Dónde se ve |
|----------|-------------|
| Docker multi-stage | `Dockerfile` con dos etapas |
| Usuario no root | `adduser -S battleship` |
| docker-compose | App + PostgreSQL con healthcheck |
| Perfil docker | `application-docker.yml` |
| HikariCP pool | Conexiones, timeouts, lifetime |
| Tomcat threads | `server.tomcat.threads.*` |
| Access log | Patrón con tiempo de respuesta |
| Jetty / Undertow | Alternativas con perfiles |
| CI/CD overview | Pipeline test → docker → deploy |

## Tarea — Dockerizar tu proyecto

Elige book-catalog, mini-tasks o gestion-eventos:

1. Crea `Dockerfile` multi-stage con usuario no root
2. Crea `docker-compose.yml` con PostgreSQL
3. Crea `application-docker.yml` con las config de conexión
4. Añade `.dockerignore` y `.env` (sin subir)
5. (Opcional) Prueba Undertow cambiando la dependencia
6. (Opcional) Configura access log de Tomcat
