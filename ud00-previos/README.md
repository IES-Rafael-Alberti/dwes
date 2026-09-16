# UD0 — Previos

Índice de trabajo para las sesiones iniciales de DWES. Este bloque reúne cuatro
líneas de preparación: Java moderno para alumnado con base en Kotlin, MongoDB
básico, diseño de datos en MongoDB y comparación con PostgreSQL JSON/JSONB.

## Java y Kotlin

- [Java para programadores Kotlin — documentación completa](01-documentacion/Java/java-para-programadores-kotlin.md)
- [PDF de Java para programadores Kotlin](01-documentacion/Java/java-para-programadores-kotlin.pdf)
- [Guion completo de Java 17-25 para alumnado de Kotlin](01-documentacion/Java/guion_de_clase_java_hasta_25_para_estudiantes_de_kotlin.md)
- [Selección de ejercicios Java 25](01-documentacion/Java/seleccion-ejercicios-java25.md)
- [Presentación Java frente a Kotlin — excepciones](01-documentacion/Java/java_vs_kotlin_excepciones_presentacion.pdf)
- [Material ampliado Java frente a Kotlin — excepciones](01-documentacion/Java/java_vs_kotlin_excepciones_extendido.pdf)
- [Proyecto guiado GeoNotes](03-ejercicios/01-geonotes/README.md)

## MongoDB básico

- [Índice del bloque MongoDB básico](01-documentacion/mongodb-basico/README.md)
- [Conceptos básicos](01-documentacion/mongodb-basico/01-conceptos-basicos.md)
- [Comandos de la shell](01-documentacion/mongodb-basico/02-comandos-shell.md)
- [Comandos con PyMongo](01-documentacion/mongodb-basico/03-comandos-pymongo.md)

Este material recupera la iniciación histórica de SBD. El antiguo guion de
instalación no se incorpora porque sus comandos dependen de versiones antiguas;
la instalación se documentará según el entorno que se vaya a usar en el curso.

## Diseño de datos en MongoDB

- [Índice del bloque MongoDB](01-documentacion/diseno-mongo/README.md)
- [Guía completa: cómo diseñar un esquema MongoDB](01-documentacion/diseno-mongo/00-proceso-diseno.md)
- [Modelado por acceso: primera sesión](01-documentacion/diseno-mongo/01-modelado-acceso.md)
- [Validación e índices](01-documentacion/diseno-mongo/02-validacion-indices.md)
- [Seguridad operativa](01-documentacion/diseno-mongo/03-seguridad-operativa.md)
- [Migraciones y versionado](01-documentacion/diseno-mongo/04-migraciones-versionado.md)
- [Antipatrones](01-documentacion/diseno-mongo/05-antipatrones.md)
- [Proyecto grupal de diseño MongoDB](04-proyectos/01-diseno-mongo-grupal/README.md)

La idea esencial para explicar en clase es que MongoDB no ofrece integridad
referencial como una base de datos relacional. Por eso primero se diseñan los
documentos, sus identificadores y sus referencias; después se fuerzan en el
código las reglas de existencia, cardinalidad, autorización y borrado, con
validadores, índices y pruebas.

## PostgreSQL JSON/JSONB

- [Índice del bloque PostgreSQL JSON/JSONB](01-documentacion/postgres-json/README.md)
- [JSONB frente al modelo relacional](01-documentacion/postgres-json/01-jsonb-vs-relacional.md)
- [Operaciones avanzadas](01-documentacion/postgres-json/02-operaciones-avanzadas.md)
- [Comparativa MongoDB y PostgreSQL JSONB](01-documentacion/postgres-json/03-comparativa.md)

## Diseño relacional y Spring Boot

- [Índice del recordatorio SQL/JPA](01-documentacion/sql-relacional/README.md)
- [Diseño de una base de datos relacional](01-documentacion/sql-relacional/01-diseno-relacional.md)
- [De tablas SQL a entidades JPA](01-documentacion/sql-relacional/02-sql-a-jpa.md)

## Secuencia rápida para empezar

1. Java/Kotlin: setup y equivalencias básicas.
2. MongoDB básico: documentos, colecciones, CRUD y consultas.
3. MongoDB: diseñar primero el acceso y decidir qué se embebe y qué se referencia.
4. MongoDB: identificar qué debe garantizar la base de datos y qué debe garantizar el código.
5. PostgreSQL JSONB: comparar qué problemas resuelve una base relacional con integridad referencial.
