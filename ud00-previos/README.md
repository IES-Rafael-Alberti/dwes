# UD0 — Previos

Índice de trabajo para las sesiones iniciales de DWES. Este bloque reúne tres
líneas de preparación: Java moderno para alumnado con base en Kotlin, diseño de
datos en MongoDB y comparación con PostgreSQL JSON/JSONB.

## Java y Kotlin

- [Java para programadores Kotlin — documentación completa](01-documentacion/java-para-programadores-kotlin.md)
- [PDF de Java para programadores Kotlin](01-documentacion/java-para-programadores-kotlin.pdf)
- [Guion completo de Java 17-25 para alumnado de Kotlin](01-documentacion/guion_de_clase_java_hasta_25_para_estudiantes_de_kotlin.md)
- [Presentación Java frente a Kotlin — excepciones](01-documentacion/java_vs_kotlin_excepciones_presentacion.pdf)
- [Material ampliado Java frente a Kotlin — excepciones](01-documentacion/java_vs_kotlin_excepciones_extendido.pdf)
- [Proyecto guiado GeoNotes](03-ejercicios/01-geonotes/README.md)

## Diseño de datos en MongoDB

- [Índice del bloque MongoDB](docs/diseno-mongo/README.md)
- [Modelado por acceso: primera sesión](docs/diseno-mongo/01-modelado-acceso.md)
- [Validación e índices](docs/diseno-mongo/02-validacion-indices.md)
- [Seguridad operativa](docs/diseno-mongo/03-seguridad-operativa.md)
- [Migraciones y versionado](docs/diseno-mongo/04-migraciones-versionado.md)
- [Antipatrones](docs/diseno-mongo/05-antipatrones.md)
- [Proyecto grupal de diseño MongoDB](04-proyectos/01-diseno-mongo-grupal/README.md)

La idea esencial para explicar en clase es que MongoDB no ofrece integridad
referencial como una base de datos relacional. Por eso primero se diseñan los
documentos, sus identificadores y sus referencias; después se fuerzan en el
código las reglas de existencia, cardinalidad, autorización y borrado, con
validadores, índices y pruebas.

## PostgreSQL JSON/JSONB

- [Índice del bloque PostgreSQL JSON/JSONB](docs/postgres-json/README.md)
- [JSONB frente al modelo relacional](docs/postgres-json/01-jsonb-vs-relacional.md)
- [Operaciones avanzadas](docs/postgres-json/02-operaciones-avanzadas.md)
- [Comparativa MongoDB y PostgreSQL JSONB](docs/postgres-json/03-comparativa.md)

## Secuencia rápida para empezar

1. Java/Kotlin: setup y equivalencias básicas.
2. MongoDB: diseñar primero el acceso y decidir qué se embebe y qué se referencia.
3. MongoDB: identificar qué debe garantizar la base de datos y qué debe garantizar el código.
4. PostgreSQL JSONB: comparar qué problemas resuelve una base relacional con integridad referencial.
