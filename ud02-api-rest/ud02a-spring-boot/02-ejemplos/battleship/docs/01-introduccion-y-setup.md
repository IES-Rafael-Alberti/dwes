# Sesión 1 — Introducción y Setup del proyecto

## Antes de empezar

Lee el documento de teoría [`01-introduccion-spring-boot.md`](../../../01-documentacion/01-introduccion-spring-boot.md). Cubre:

- Qué es Spring Boot y cómo se diferencia de Spring Framework
- Starters y autoconfiguración
- Estructura típica de un proyecto por capas
- Novedades de SB4 (Java 25, records, virtual threads)

## Qué vamos a construir

**Battleship API** — un servicio REST para jugar al Hundir la Flota. Soporta múltiples partidas, tableros con barcos colocados en coordenadas, turnos de disparo y estado de la partida. Lo haremos con TDD desde cero, añadiendo complejidad sesión a sesión.

## Code-along: crear el proyecto

### 1. Generar el esqueleto con Spring Initializr

Usamos [start.spring.io](https://start.spring.io) en clase con estos parámetros:

| Campo | Valor |
|-------|-------|
| Project | Maven |
| Language | Java |
| Spring Boot | 4.0.5 |
| Group | `com.example` |
| Artifact | `battleship` |
| Java | 25 |
| Dependencies | `Spring Web MVC` |

El resto de dependencias (JPA, H2, Validation, Flyway, Lombok y SpringDoc) se
añadirán más adelante, cuando cada sesión las necesite. Así podrás ver cómo se
incorporan dependencias a un proyecto ya iniciado.

### 2. El `pom.xml`

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0
         https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>4.0.5</version>
        <relativePath/>
    </parent>

    <groupId>com.example</groupId>
    <artifactId>battleship</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <name>battleship</name>
    <description>Battleship API — Hundir la Flota con TDD</description>

    <properties>
        <java.version>25</java.version>
    </properties>
    ...
</project>
```

Señalar en clase:

- **spring-boot-starter-parent**: versión de SB como POM padre → hereda gestión de dependencias
- **java.version = 25**: usamos el LTS más reciente
- Cada starter agrupa dependencias consistentes entre sí

### 3. El punto de entrada

```java
package com.example.battleship;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BattleshipApplication {

    public static void main(String[] args) {
        SpringApplication.run(BattleshipApplication.class, args);
    }
}
```

Explicar:

- `@SpringBootApplication` = `@Configuration` + `@EnableAutoConfiguration` + `@ComponentScan`
- `SpringApplication.run(...)` arranca el contexto y el servidor embebido

### 4. `application.yml`

```yaml
spring:
  application:
    name: battleship
  datasource:
    url: jdbc:h2:mem:battleship
    driver-class-name: org.h2.Driver
  h2:
    console:
      enabled: true
  jpa:
    hibernate:
      ddl-auto: validate
    show-sql: true
```

Explicar:

- `spring.h2.console.enabled: true` — consola web en `/h2-console`
- `spring.jpa.hibernate.ddl-auto: validate` — Flyway gestiona el esquema; Hibernate solo valida
- `spring.jpa.show-sql: true` — ver las SQL que genera Hibernate

### 5. Primera ejecución

Usa el Maven Wrapper para reproducir la versión de Maven fijada por el proyecto:

```bash
./mvnw spring-boot:run
```

Verificar:

- El log muestra `Started BattleshipApplication in X.XX seconds`
- La consola H2 en `http://localhost:8080/h2-console` (JDBC URL: `jdbc:h2:mem:battleship`)
- `http://localhost:8080` devuelve **404** (todavía no hay controllers) — esto es normal

Detener con `Ctrl+C`.

> **Maven Wrapper.** Spring Initializr genera `mvnw`, `mvnw.cmd` y la carpeta
> `.mvn/wrapper` por defecto, así que al crear el proyecto ya lo tienes. Los
> plugins de Java de IntelliJ y VS Code también lo usan automáticamente. Si un
> proyecto no lo incluyera, puedes generarlo desde su raíz con:
>
> ```bash
> mvn wrapper:wrapper
> ```
>
> (necesita un Maven instalado en el equipo una única vez). Después, ejecuta
> siempre `./mvnw` para arrancar, probar o empaquetar el proyecto.

## Lo que vimos hoy

| Concepto | Dónde se ve |
|----------|-------------|
| Spring Initializr | Creación del proyecto |
| `@SpringBootApplication` | Clase principal, arranque |
| `application.yml` | Config de base de datos H2, JPA |
| Spring Boot Maven plugin (Wrapper) | `./mvnw spring-boot:run` |
| Estructura de proyecto | Paquetes, recursos, tests |

## Tarea — Book Catalog, Entrega 1

Crea un proyecto nuevo (o clona el template) para **Book Catalog API** siguiendo las mismas instrucciones de creación que vimos en clase.

**Entrega 1**: Un controlador que devuelva texto plano.

```java
@RestController
public class BookController {

    @GetMapping("/books")
    public String hello() {
        return "Hello, Book Catalog!";
    }
}
```

Ver `../../03-ejercicios/02-book-catalog/README.md` para los detalles completos. Es la **entrega 1 de la práctica cooperativa Book Catalog**, que forma parte de la evaluación de UD2a; practica lo mismo que acabamos de ver.
