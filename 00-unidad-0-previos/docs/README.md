# DWES — Materiales de base de datos

Bloque inicial de diseño de bases de datos NoSQL y uso avanzado de PostgreSQL JSON/JSONB.
Destinado a alumnos de Desarrollo Web en Entorno Servidor (2º DAW/ASIR).

## Estructura

```
docs/
├── diseno-mongo/           # Curso MongoDB: 5 sesiones + trabajo grupal
│   ├── README.md
│   ├── 01-modelado-acceso.md
│   ├── 02-validacion-indices.md
│   ├── 03-seguridad-operativa.md
│   ├── 04-migraciones-versionado.md
│   ├── 05-antipatrones.md
│   ├── ejercicios/
│   │   ├── 01-caso-blog.md
│   │   ├── 02-caso-ecommerce.md
│   │   └── 03-caso-refactor.md
│   └── diseno-grupal/
│       ├── enunciado.md
│       └── plantilla-design-doc.md
│
└── postgres-json/          # Curso PostgreSQL JSONB: 3 sesiones
    ├── README.md
    ├── 01-jsonb-vs-relacional.md
    ├── 02-operaciones-avanzadas.md
    ├── 03-comparativa.md
    └── ejercicios/
        ├── 01-modelo-hibrido.md
        └── 02-benchmark.md
```

## Cronograma recomendado (3-4 semanas)

El bloque se imparte en **paralelo con la introducción a Java y las primeras semanas de backend**,
siguiendo la metodología de sesiones intercaladas:

| Semana | Lunes | Martes | Miércoles | Jueves | Viernes |
|--------|-------|--------|-----------|--------|---------|
| 1 | Mongo 1: Modelado | Backend | Mongo 2: Validación | Backend | **Dudas Mongo 1-2** |
| 2 | Mongo 3: Seguridad | Backend | Mongo 4: Migraciones | Postgres 1: JSONB | **Dudas Mongo 3-4 + PG 1** |
| 3 | Mongo 5: Anti-patrones | Backend | Postgres 2: Avanzado | Postgres 3: Comparativa | **Dudas PG 2-3** |
| 4 (buffer) | Entrega trabajo grupal | Presentaciones | Retroalimentación | — | — |

### Sesiones de "Backend"
En esas franjas, el profesor continúa con el temario habitual de backend (Java — Spring Boot / Laravel / .NET).

Las sesiones de "Dudas" son abiertas: los alumnos traen los ejercicios hechos y se resuelven casos concretos.

## Metodología docente

Cada sesión sigue la misma estructura:

1. **Teoría** (30-45 min) — Conceptos clave con ejemplos reales
2. **Demo guiada** (20-30 min) — El profesor codifica/ejecuta en vivo
3. **Ejercicio autónomo** — Los alumnos trabajan por su cuenta; el profesor avanza con backend
4. **Día de dudas** — 1-2 sesiones de resolución colectiva

Los ejercicios autónomos se **entregan por escrito** (documento markdown o script SQL)
y se evalúan con la rúbrica incluida en cada enunciado.

## Evaluación

| Componente | Peso | Descripción |
|------------|------|-------------|
| Ejercicios MongoDB (3) | 30% | Blog, e-commerce, refactor — individuales |
| Trabajo grupal MongoDB | 30% | Proyecto en grupo de 3-4 con design doc y defensa |
| Ejercicios PostgreSQL (2) | 20% | Modelo híbrido + benchmark |
| Participación en dudas | 20% | Presentación de soluciones, preguntas, discusión |

## Requisitos técnicos

- **MongoDB 7+** — `mongod`, `mongosh`, `migrate-mongo` (Node.js)
- **PostgreSQL 15+** — con tipo `jsonb`, GIN indexes, `jsonb_path_query`
- Drivers de conexión desde el lenguaje que se use en backend (Java MongoDB Driver, pgvector, etc.)

## Material complementario

- [MongoDB University — Data Modeling](https://university.mongodb.com/courses/M320/about)
- [PostgreSQL JSONB Docs](https://www.postgresql.org/docs/current/datatype-json.html)
- [M320 Data Modeling](https://learn.mongodb.com/courses/data-modeling) — gratis, ~2h
- [Aitor Medrano — Modelado de datos documentales](https://aitor-medrano.github.io/iabd/sa/modelado.html) — base metodológica usada en este curso, con 3 fases, patrones, anti-patrones, validación y ejercicios
