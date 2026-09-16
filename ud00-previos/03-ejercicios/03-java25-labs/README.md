# Laboratorio Java 25

Consulta primero la [hoja de asignacion](HOJA-ASIGNACION.md), que fija el
orden de dificultad y los entregables.

Retos breves basados en fundamentos del libro *Aprende Java con Ejercicios*,
reescritos para JDK 25, IntelliJ, Gradle y JUnit 5. Cada carpeta contiene un
enunciado, un starter y tests que describen el contrato.

Los tests del starter pueden fallar hasta completar el reto. La solución
docente está fuera del material público, en `../../99-profesor/soluciones-java25/`.

| Reto | Tema | Tiempo | Base |
|---|---|---:|---|
| J25-01 | Validación, bucles y estadísticas | 20 min | Capítulos 2-7 |
| J25-03 | `record` y constructor compacto | 25 min | Capítulo 9 |
| J25-04 | Colecciones, `Comparator` y Streams | 30 min | Capítulo 10 |
| J25-05 | `sealed` y `switch` con patrones | 35 min | Capítulos 9 y 14 |
| J25-06 | Ficheros con NIO.2 | 45 min | Capítulo 11 |
| J25-08 | Consulta geografica en GeoNotes | 60 min | Capítulos 9-11 |
| J25-07 | Historial de resultados en Calc25 | 45-60 min | Capítulos 10-11 |

## Uso en IntelliJ

Abrir como proyecto la carpeta raíz `03-java25-labs/`, la que contiene
`settings.gradle` y `build.gradle`. No abrir cada carpeta `J25-*` por separado.
IntelliJ detectará los subproyectos Gradle y permitirá ejecutar los tests de
cada reto desde el panel Gradle.

También se pueden ejecutar desde la terminal:

```bash
./gradlew test
./gradlew :J25-06:test
```

El proyecto usa el toolchain JDK 25 y su propio Gradle Wrapper.

J25-07 se abre aparte desde la carpeta existente de Calc25:
`03-ejercicios/02-calculadora/recursos/calc25/`.
