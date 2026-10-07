---
title: "UD01 — Un entorno que se pueda reproducir"
subtitle: "10 · Java 25, Spring Boot 4 y Maven Wrapper"
author: "DWES · 2026/2027"
lang: es
aspectratio: 169
---

## Qué versión usa este módulo · 3 min

- Nuestra referencia de clase es **JDK 25** con **Spring Boot 4**.
- Spring Boot puede ejecutarse con un Java mínimo inferior; **el proyecto
  adopta Java 25** para no mezclar versiones en los ejercicios.
- Maven Wrapper fija Maven para el proyecto; **no instala ni fija el JDK**.

*Base: [10 — Entorno reproducible](../10-entorno-java25-spring-boot4.md).*

## Tres comprobaciones antes del IDE · 4 min

```bash
java -version
javac -version
git --version
```

Si `java` y `javac` muestran versiones diferentes, revisar `JAVA_HOME` y
`PATH`. En IntelliJ o VS Code, seleccionar también el **SDK 25** al importar
el proyecto como Maven.

*Base: [10 — «Comprobación inicial» e «IDE»](../10-entorno-java25-spring-boot4.md).*

## Maven Wrapper: misma herramienta para todos · 4 min

Desde `02-ejemplos/hello-server/`:

```bash
./mvnw --version
./mvnw test
```

En Windows: `mvnw.cmd --version` y `mvnw.cmd test`.

**Pregunta:** si el IDE muestra pruebas verdes, pero `./mvnw test` falla,
¿cuál reproducirá un compañero en otro equipo?

*Base: [10 — «Maven Wrapper»](../10-entorno-java25-spring-boot4.md).*

## Dos terminales para observar un servidor · 5 min

Terminal 1, con el proyecto abierto:

```bash
./mvnw spring-boot:run
```

Terminal 2, cuando haya arrancado:

```bash
curl -i http://localhost:8080/
curl -i http://localhost:8080/api/hello
curl -i http://localhost:8080/health
```

Detener con `Ctrl+C`. No se instala Spring Boot globalmente.

*Base: [10 — «Arranque»](../10-entorno-java25-spring-boot4.md).*

## Cuando algo no arranca · 4 min

| Síntoma | Primera comprobación |
|---|---|
| Versión de clase incompatible | `java -version` y SDK del IDE |
| Maven usa otro Java | `./mvnw --version` y `JAVA_HOME` |
| Puerto 8080 ocupado | Identificar el proceso que lo usa |
| Falla el wrapper | Permiso de ejecución de `mvnw` |

No solucionarlo matando procesos desconocidos ni cambiando versiones al azar.

*Base: [10 — «Diagnóstico»](../10-entorno-java25-spring-boot4.md).*

## Evidencia mínima · 2 min

Para explicar que funciona bastan:

1. Versiones de Java y Maven Wrapper.
2. Resultado de `./mvnw test`.
3. Estado y `Content-Type` de `/`, `/api/hello` y `/health`.

Evitar rutas personales completas, variables de entorno y tokens.

*Base: [10 — «Evidencia»](../10-entorno-java25-spring-boot4.md).*
