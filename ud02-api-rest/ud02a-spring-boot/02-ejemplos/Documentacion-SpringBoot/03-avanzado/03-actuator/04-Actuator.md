# Guía: Spring Boot Actuator

## Qué es
Actuator expone endpoints para monitorizar y gestionar la aplicación (salud, métricas, info, logs). Útil para dev y para observabilidad en producción.

## Dependencia
- Maven:
  ```xml
  <dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
  </dependency>
  ```
- Gradle:
  ```kotlin
  implementation("org.springframework.boot:spring-boot-starter-actuator")
  ```

## Endpoints principales
- `/actuator/health`: estado de la app (por defecto “UP”). Puedes ver detalles si los habilitas.
- `/actuator/info`: info personalizada de la app (rellena `application.properties` o `info.*`).
- `/actuator/metrics`: métricas disponibles; `/actuator/metrics/{name}` para detalle.
- Otros: `/actuator/env`, `/actuator/loggers`, `/actuator/threaddump`, etc. (requiere habilitarlos).

## Configuración básica
En `application.properties`:
```properties
# Exponer endpoints deseados
management.endpoints.web.exposure.include=health,info,metrics
# (para pruebas locales puedes usar "*", en prod mejor restringir)

# Detalle de health
management.endpoint.health.show-details=when_authorized
# Info personalizada
info.app.name=API Gestion de Eventos
info.app.version=1.0.0
```

## Seguridad
- Si usas Spring Security, los endpoints de Actuator se protegen como cualquier ruta. Permite explícitamente los que quieras públicos:
  ```java
  http
    .authorizeHttpRequests(auth -> auth
      .requestMatchers("/actuator/health", "/actuator/info").permitAll()
      .requestMatchers("/actuator/**").hasRole("ADMIN")
      .anyRequest().authenticated());
  ```
- No expongas `env`, `beans`, etc. públicamente. En producción, limita por rol o red (IP allowlist/reverse proxy).

## Swagger/OpenAPI
Por defecto Actuator no aparece en Swagger. Si quieres documentarlo, inclúyelo en `springdoc` (opcional). Lo habitual es dejarlo fuera del contrato público.

## Buenas prácticas
- Exponer solo lo necesario en producción; `health` público, resto autenticado.
- Añadir un rol/admin para observar métricas/loggers.
- Si añades Prometheus/Micrometer, expón `/actuator/prometheus` y configuralo como endpoint permitido.
- No mezcles datos sensibles en `info.*`.

## Prometheus (qué es y ejemplo)
- **Qué es**: Prometheus es un sistema de monitorización de series temporales. Hace “scrape” de endpoints de métricas (formato de texto) y almacena/consulta estadísticas (latencias, contadores, memoria, etc.), habitualmente visualizadas con Grafana.
- **Por qué usarlo**: te da visibilidad de rendimiento, errores y consumo de recursos; permite alertar si suben los tiempos de respuesta o bajan los ratios de éxito.
- **Cómo exponer métricas** (Spring Boot + Micrometer):
  1. Dependencia adicional:
     - Maven:
       ```xml
       <dependency>
         <groupId>io.micrometer</groupId>
         <artifactId>micrometer-registry-prometheus</artifactId>
       </dependency>
       ```
     - Gradle:
       ```kotlin
       implementation("io.micrometer:micrometer-registry-prometheus")
       ```
  2. Configura Actuator para exponer el endpoint:
     ```properties
     management.endpoints.web.exposure.include=health,info,metrics,prometheus
     ```
  3. Permite el acceso a `/actuator/prometheus` en seguridad (normalmente restringido a admin o a la red de monitorización):
     ```java
     http.authorizeHttpRequests(auth -> auth
       .requestMatchers("/actuator/prometheus").hasRole("ADMIN")
       // resto...
     );
     ```
  4. En Prometheus, añade un job de scrape apuntando a `http://host:puerto/actuator/prometheus`.
  5. Métricas personalizadas: con Micrometer puedes registrar contadores/timers en tu código y aparecerán en el endpoint.***
