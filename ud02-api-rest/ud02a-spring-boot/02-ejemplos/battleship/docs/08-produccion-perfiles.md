# Sesión 8 — Producción: perfiles, paginación, OpenAPI, actuator, logging, HTTPS

## Antes de empezar

Consulta los anexos:

- [`anexo-git-maven-spring.md`](../../../01-documentacion/anexo-git-maven-spring.md) — perfiles Maven/Spring
- Doc 08: [`08-battleship-caso-practico.md`](../../../01-documentacion/08-battleship-caso-practico.md) — guion completo

## Visión general

En esta sesión llevamos Battleship a producción:
1. **Perfiles** Spring (dev / prod) con configuraciones diferenciadas
2. **Paginación** con `Pageable` para listas grandes
3. **Filtros dinámicos** con `@RequestParam` + `Specification`
4. **OpenAPI / Swagger** con springdoc
5. **Actuator** para health checks y métricas
6. **Logging** por perfil
7. **HTTPS** configurable con toggle por perfil

## Code-along: de desarrollo a producción

### 1. Perfiles Spring

Creamos configuraciones separadas para desarrollo y producción.

**`application.yml`** — configuración común:

```yaml
spring:
  application:
    name: battleship
  jpa:
    open-in-view: false
    hibernate:
      ddl-auto: validate
    show-sql: false

server:
  servlet:
    application-display-name: ""

app:
  security:
    jwt:
      issuer: battleship-api
      audience: battleship-client
```

**`application-dev.yml`** — desarrollo:

```yaml
spring:
  datasource:
    url: jdbc:h2:mem:battleship
    driver-class-name: org.h2.Driver
  h2:
    console:
      enabled: true
  jpa:
    show-sql: true
    properties:
      hibernate:
        format_sql: true

server:
  port: 8080

app:
  security:
    jwt:
      access-token-expiration: 3600000     # 1 hora en dev
      refresh-token-expiration: 86400000   # 24 horas en dev

logging:
  level:
    com.example.battleship: DEBUG
    org.springframework.web: DEBUG
    org.springframework.security: DEBUG
```

**`application-prod.yml`** — producción:

```yaml
spring:
  datasource:
    url: ${DB_URL}
    username: ${DB_USER}
    password: ${DB_PASSWORD}
    driver-class-name: org.postgresql.Driver

server:
  port: 8443
  ssl:
    enabled: true
    key-store: ${SSL_KEYSTORE_PATH}
    key-store-password: ${SSL_KEYSTORE_PASSWORD}
    key-store-type: PKCS12

app:
  security:
    jwt:
      access-token-expiration: 900000      # 15 min en producción
      refresh-token-expiration: 604800000  # 7 días en producción
      private-key: ${JWT_PRIVATE_KEY}
      public-key: ${JWT_PUBLIC_KEY}

logging:
  level:
    com.example.battleship: INFO
    org.springframework: WARN
  file:
    path: /var/log/battleship
    name: battleship.log
  logback:
    rollingpolicy:
      max-history: 30
      max-file-size: 10MB
```

Señalar:

- Perfil activo: `SPRING_PROFILES_ACTIVE=prod` o `--spring.profiles.active=prod`
- Las variables `${...}` se resuelven de entorno, no se hardcodean
- En dev: H2, TTL largo, SQL visible, log DEBUG
- En prod: PostgreSQL, TTL corto, HTTPS, SQL oculto, log INFO

Ejecutar con perfil:

```bash
# Desarrollo
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# Producción (requiere variables de entorno)
SPRING_PROFILES_ACTIVE=prod DB_URL=... mvn spring-boot:run
```

### 2. Paginación con Pageable

El endpoint `GET /api/games` devuelve todas las partidas. Con cientos o miles, es inviable. Añadimos paginación:

```java
@GetMapping
public Page<GameResponseDTO> listGames(
        @PageableDefault(size = 20, sort = "createdAt") Pageable pageable) {
    return gameService.listGames(pageable);
}
```

En el servicio:

```java
public Page<GameResponseDTO> listGames(Pageable pageable) {
    return gameRepo.findAll(pageable)
            .map(this::toResponse);
}
```

Probar:

```bash
# Primera página, 10 elementos, ordenado por fecha descendente
curl "http://localhost:8080/api/games?page=0&size=10&sort=createdAt,desc"

# Respuesta incluye:
# {
#   "content": [...],
#   "totalElements": 42,
#   "totalPages": 5,
#   "size": 10,
#   "number": 0,
#   "first": true,
#   "last": false
# }
```

Señalar:

- `Pageable` lo resuelve Spring automáticamente desde los query params
- `@PageableDefault` valores por defecto si no se pasan
- `Page<T>` incluye metadatos: total, páginas, siguiente/anterior
- El cliente sabe cuántas páginas hay sin hacer múltiples requests

### 3. Filtros dinámicos con Specifications

Para filtros combinados sin escribir SQL a mano, usamos `Specification` (Spring Data JPA):

```java
package com.example.battleship.repository;

import com.example.battleship.domain.Game;
import org.springframework.data.jpa.domain.Specification;

public class GameSpecifications {

    public static Specification<Game> hasStatus(String status) {
        return (root, query, cb) ->
            status == null ? null : cb.equal(root.get("status"), status);
    }

    public static Specification<Game> boardSizeAtLeast(int minSize) {
        return (root, query, cb) ->
            cb.greaterThanOrEqualTo(root.get("boardSize"), minSize);
    }

    public static Specification<Game> createdAfter(java.time.LocalDateTime date) {
        return (root, query, cb) ->
            date == null ? null : cb.greaterThanOrEqualTo(root.get("createdAt"), date);
    }
}
```

Repositorio debe extender `JpaSpecificationExecutor`:

```java
public interface GameRepository extends JpaRepository<Game, Long>,
                                        JpaSpecificationExecutor<Game> {
}
```

Controlador:

```java
@GetMapping
public Page<GameResponseDTO> listGames(
        @RequestParam(required = false) String status,
        @RequestParam(required = false) @Min(1) Integer minBoardSize,
        @PageableDefault(size = 20, sort = "createdAt") Pageable pageable) {

    var spec = Specification
            .where(GameSpecifications.hasStatus(status))
            .and(GameSpecifications.boardSizeAtLeast(minBoardSize != null ? minBoardSize : 0));

    return gameService.listGames(spec, pageable);
}
```

Servicio:

```java
public Page<GameResponseDTO> listGames(Specification<Game> spec, Pageable pageable) {
    return gameRepo.findAll(spec, pageable)
            .map(this::toResponse);
}
```

Probar:

```bash
curl "http://localhost:8080/api/games?status=IN_PROGRESS&minBoardSize=8&page=0&size=10"
```

Señalar:

- `Specification` evita concatenar SQL o escribir JPQL a mano
- Los filtros son opcionales (`null` si no se pasan)
- Se pueden combinar con `AND`/`OR` encadenando `.and()`, `.or()`
- Spring Data genera la query automáticamente

### 4. OpenAPI / Swagger

Springdoc ya está en el `pom.xml`. Solo falta configurarlo:

```yaml
springdoc:
  api-docs:
    path: /api-docs
  swagger-ui:
    path: /swagger-ui.html
    try-it-out-enabled: true
    display-request-duration: true
```

Personalizar con `@OpenAPIDefinition`:

```java
package com.example.battleship.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;

@OpenAPIDefinition(
    info = @Info(
        title = "Battleship API",
        version = "1.0.0",
        description = "API REST para jugar al Hundir la Flota con TDD",
        contact = @Contact(name = "DWES", email = "dwes@ies.com"),
        license = @License(name = "MIT")
    )
)
public class OpenApiConfig {}
```

Añadir seguridad JWT en Swagger:

```java
package com.example.battleship.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerSecurityConfig {

    @Bean
    public OpenAPI customizeOpenAPI() {
        var schemeName = "bearerAuth";
        return new OpenAPI()
                .addSecurityItem(new SecurityRequirement().addList(schemeName))
                .components(new Components()
                        .addSecuritySchemes(schemeName, new SecurityScheme()
                                .name(schemeName)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")));
    }
}
```

Acceso operativo en la configuración base:

```bash
curl -H "Authorization: Bearer $ACCESS_TOKEN" \
  http://localhost:8080/api-docs
```

`/api-docs` y `/swagger-ui.html` requieren autenticación. La especificación puede descargarse con bearer como en el ejemplo. La UI no se presenta como accesible mediante navegación directa: el navegador no añade la cabecera `Authorization` al cargarla. Si se necesita Swagger UI interactiva en clase, debe habilitarse explícitamente solo en un perfil `dev` y permitirse allí en `SecurityFilterChain`; no se abre en la configuración base o de producción.

Señalar:

- `try-it-out-enabled: true` solo resulta operativo si la UI se habilita en el perfil `dev`
- El esquema bearer describe cómo autenticar llamadas de la API; no hace pública la propia UI
- Alternativa: `@SecurityRequirement` por endpoint si los roles difieren

### 5. Actuator (health y métricas)

Añadir al `pom.xml`:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```

Configurar solo lo necesario (no expongas todo en producción):

```yaml
# application.yml — común
management:
  endpoints:
    web:
      exposure:
        include: health,info

# application-dev.yml — más info en desarrollo
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,env,loggers
  endpoint:
    env:
      show-values: always
    health:
      show-details: always
```

Health checks personalizados:

```java
package com.example.battleship.config;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
public class BattleshipHealthIndicator implements HealthIndicator {

    private final GameRepository gameRepository;

    public BattleshipHealthIndicator(GameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }

    @Override
    public Health health() {
        try {
            long count = gameRepository.count();
            return Health.up()
                    .withDetail("totalGames", count)
                    .build();
        } catch (Exception e) {
            return Health.down()
                    .withDetail("error", e.getMessage())
                    .build();
        }
    }
}
```

Probar:

```bash
curl -H "Authorization: Bearer $ACCESS_TOKEN" \
  http://localhost:8080/actuator/health
# {"status": "UP", "components": {"battleship": {"status": "UP", "details": {"totalGames": 5}}}}
```

### 6. Logging por perfil

Creamos `logback-spring.xml` en `src/main/resources/`:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration>
    <!-- Consola: formato con colores -->
    <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>%d{HH:mm:ss.SSS} %highlight(%-5level) [%thread] %cyan(%logger{36}) - %msg%n</pattern>
        </encoder>
    </appender>

    <!-- Archivo (solo prod): JSON para centralizar logs -->
    <appender name="FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <file>${LOG_PATH:-/tmp}/battleship.log</file>
        <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
            <fileNamePattern>battleship-%d{yyyy-MM-dd}.log</fileNamePattern>
            <maxHistory>30</maxHistory>
        </rollingPolicy>
        <encoder>
            <pattern>{"timestamp":"%d{yyyy-MM-dd'T'HH:mm:ss.SSS}","level":"%level","logger":"%logger","message":"%msg"}%n</pattern>
        </encoder>
    </appender>

    <!-- Perfiles -->
    <springProfile name="dev">
        <root level="DEBUG">
            <appender-ref ref="CONSOLE"/>
        </root>
        <logger name="com.example.battleship" level="DEBUG"/>
    </springProfile>

    <springProfile name="prod">
        <root level="INFO">
            <appender-ref ref="CONSOLE"/>
            <appender-ref ref="FILE"/>
        </root>
        <logger name="com.example.battleship" level="INFO"/>
    </springProfile>
</configuration>
```

Señalar:

- En prod: log JSON para que herramientas como ElasticSearch puedan parsearlo
- En dev: log formateado con colores para leer en consola
- `LOG_PATH` se hereda de `application-prod.yml`
- Rolling policy: archivo nuevo cada día, 30 días de histórico

### 7. HTTPS configurable por perfil

En producción se activa con `application-prod.yml` (ya configurado arriba). Para desarrollo:

Generar certificado autofirmado para pruebas:

```bash
keytool -genkeypair -alias battleship \
  -keyalg RSA -keysize 2048 \
  -storetype PKCS12 -keystore battleship.p12 \
  -validity 365 \
  -dname "CN=Battleship Dev, OU=DWES, O=IES, L=Sevilla, C=ES"
```

Colocar en `src/main/resources/keys/battleship.p12` y actualizar `application-prod.yml`:

```yaml
server:
  port: 8443
  ssl:
    enabled: true
    key-store: classpath:keys/battleship.p12
    key-store-password: ${SSL_PASSWORD}
    key-store-type: PKCS12
```

Toggle HTTPS mediante perfil:

| Perfil | HTTP/HTTPS | Puerto |
|--------|-----------|--------|
| dev | HTTP | 8080 |
| prod | HTTPS | 8443 |

Si en producción se usa un proxy inverso (Nginx, Cloudflare), el SSL termina ahí y la app internamente sigue en HTTP. En ese caso:

```yaml
# application-prod.yml (detrás de proxy)
server:
  port: 8080
  forward-headers-strategy: framework
```

Y el proxy hace HTTPS → HTTP hacia la app.

### 8. Resumen: estructura final del proyecto

```
battleship/
├── pom.xml
├── src/main/resources/
│   ├── application.yml              # Común
│   ├── application-dev.yml          # Desarrollo (H2, TTL largo, DEBUG)
│   ├── application-prod.yml         # Producción (PostgreSQL, HTTPS, INFO)
│   ├── logback-spring.xml           # Logging por perfil
│   ├── keys/
│   │   ├── private.pem              # Clave privada JWT (dev)
│   │   ├── public.pem               # Clave pública JWT (dev)
│   │   └── battleship.p12           # Certificado SSL (solo prod)
│   ├── db/migration/
│   │   ├── V1__create_games_table.sql
│   │   ├── V2__seed_games.sql
│   │   ├── V3__create_users.sql
│   │   └── V4__seed_admin.sql
│   └── static/                      # (opcional)
├── src/main/java/.../
│   ├── config/
│   │   ├── SecurityConfig.java
│   │   ├── OpenApiConfig.java
│   │   ├── SwaggerSecurityConfig.java
│   │   └── ServerHeaderCustomizer.java
│   ├── security/
│   │   ├── JwtService.java
│   │   ├── JwtAuthFilter.java
│   │   ├── RateLimitFilter.java
│   │   ├── SecurityAuditor.java
│   │   └── BattleshipUserDetailsService.java
│   ├── domain/
│   │   ├── Game.java
│   │   ├── Ship.java
│   │   ├── Attack.java
│   │   └── User.java
│   ├── dto/
│   │   ├── CreateGameDTO.java
│   │   ├── GameResponseDTO.java
│   │   ├── AttackDTO.java
│   │   ├── PlaceShipDTO.java
│   │   ├── AttackQueryDTO.java
│   │   ├── ErrorPayload.java
│   │   ├── AuthRequest.java
│   │   ├── TokenResponse.java
│   │   └── TokenRefreshRequest.java
│   ├── repository/
│   │   ├── GameRepository.java
│   │   ├── ShipRepository.java
│   │   ├── AttackRepository.java
│   │   └── UserRepository.java
│   ├── service/
│   │   ├── GameService.java
│   │   └── AuthService.java
│   └── web/
│       ├── GameController.java
│       ├── AuthController.java
│       ├── GlobalExceptionHandler.java
│       └── AttackQueryController.java
```

## Lo que vimos hoy

| Concepto | Dónde se ve |
|----------|-------------|
| Perfiles Spring | `application-dev.yml`, `application-prod.yml` |
| Paginación con `Pageable` | `GET /api/games?page=0&size=10&sort=createdAt,desc` |
| Filtros con `Specification` | `GameSpecifications` + `JpaSpecificationExecutor` |
| OpenAPI / Swagger | `springdoc`, `swagger-ui.html` |
| Actuator | `/actuator/health`, `BattleshipHealthIndicator` |
| Logging por perfil | `logback-spring.xml` |
| HTTPS configurable | Certificado + toggle por perfil |

## Tarea — Despliegue completo

Para el proyecto que elijas (book-catalog, mini-tasks o gestion-eventos):

1. Crea perfiles `dev` y `prod`
2. Añade paginación a los endpoints `GET /api/...`
3. Añade un filtro por campo (usando `@RequestParam` o `Specification`)
4. Configura springdoc y verifica `/api-docs` con autenticación; habilita Swagger UI solo en desarrollo si la necesitas
5. Añade actuator con al menos `health` e `info`
6. Configura logging diferente por perfil

> **Nota**: HTTPS en desarrollo es opcional. Si no tenés certificado, déjalo solo en `application-prod.yml` y ejecuta siempre en `dev` para clase.
