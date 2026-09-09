# Session State — Curso MongoDB + PostgreSQL JSON

> Archivo de estado para que agentes en otro ordenador puedan continuar.
> Creado: 2026-06-17

## Contexto del curso

- **Módulo**: DWES (Desarrollo Web en Entorno Servidor)
- **Profesor**: solicita materiales para bloque inicial de BD
- **Alumnos**: vienen de Kotlin, ven MERN en otro módulo, usan MongoDB sin diseño
- **Metodología**: flipped classroom (teoría → demo → ejercicio autónomo → día de dudas)

## Estructura del proyecto

```
00-unidad-0-previos/
└── docs/
    ├── README.md                          ← Guía docente, cronograma, evaluación
    │
    ├── diseno-mongo/                      ← Curso MongoDB (5 sesiones + extras)
    │   ├── 01-modelado-acceso.md         ← Metodología 3 fases, cardinalidad 1:F/N/S
    │   ├── 02-validacion-indices.md       ← $jsonSchema, índices, ESR rule, warn mode
    │   ├── 03-seguridad-operativa.md     ← Roles, auth, cifrado, backup
    │   ├── 04-migraciones-versionado.md   ← migrate-mongo, idempotencia, rollback
    │   ├── 05-antipatrones.md            ← 9 anti-patrones + tabla detección
    │   ├── cheatsheet-mongo.md           ← Hoja de referencia rápida
    │   ├── ejercicios/
    │   │   ├── 01-caso-blog.md            ← Ejercicio + rúbrica
    │   │   ├── 02-caso-ecommerce.md       ← Ejercicio + rúbrica
    │   │   └── 03-caso-refactor.md        ← Refactor SQL→Mongo + rúbrica
    │   ├── soluciones/                    ← Solucionarios de referencia
    │   │   ├── 01-caso-blog-resuelto.md
    │   │   ├── 02-caso-ecommerce-resuelto.md
    │   │   └── 03-caso-refactor-resuelto.md
    │   ├── scripts/                       ← Ejecutables con mongosh
    │   │   ├── demo-blog.js
    │   │   ├── validate-schema.js
    │   │   ├── setup-security.js
    │   │   └── migrate-example.js
    │   └── diseno-grupal/
    │       ├── enunciado.md               ← Trabajo grupal + rúbrica + autoevaluación
    │       └── plantilla-design-doc.md    ← Plantilla para entregable
    │
    └── postgres-json/                    ← Curso PostgreSQL JSONB (3 sesiones)
        ├── README.md
        ├── 01-jsonb-vs-relacional.md
        ├── 02-operaciones-avanzadas.md
        ├── 03-comparativa.md
        └── ejercicios/
            ├── 01-modelo-hibrido.md
            └── 02-benchmark.md
```

## Contenido de cada sesión

### MongoDB

| Sesión | Archivo | Temas clave | Tiene ejercicio |
|--------|---------|-------------|-----------------|
| 1 | `01-modelado-acceso.md` | Metodología 3 fases, card. 1:F/1:N/1:S, embed vs reference, patrones | ✅ E-commerce |
| 2 | `02-validacion-indices.md` | `$jsonSchema`, índices (TTL/partial/text), ESR rule, `explain()`, `validationAction: warn` | ✅ Script completo |
| 3 | `03-seguridad-operativa.md` | Roles, mínimo privilegio, IP binding, cifrado en reposo, backup | ✅ 3 usuarios + dump |
| 4 | `04-migraciones-versionado.md` | `migrate-mongo`, idempotencia, `schemaVersion`, rollback | ✅ 3 migraciones |
| 5 | `05-antipatrones.md` | 9 anti-patrones en tabla, comandos de detección | ✅ Refactor |

### PostgreSQL JSONB

| Sesión | Archivo | Temas clave |
|--------|---------|-------------|
| 1 | `01-jsonb-vs-relacional.md` | json vs jsonb, GIN indexes, columnas generadas, modelo híbrido |
| 2 | `02-operaciones-avanzadas.md` | jsonb_set, triggers validación, JsonPath, vistas materializadas, EAV |
| 3 | `03-comparativa.md` | MongoDB vs PG JSONB, benchmark, caso híbrido, regla decisión |

## Decisiones tomadas

1. **Metodología flipped classroom**: teoría → demo → ejercicio autónomo → día de dudas
2. **Evaluación**: ejercicios (30% Mongo individ, 30% Mongo grupal, 20% PG, 20% participación)
3. **Idioma**: español neutral/profesional para todo el contenido del curso
4. **Cardinalidad**: adoptada la notación 1:F/1:N/1:S (few/many/squillions) sobre "pequeño/grande"
5. **Patrones**: categorizados por Representación / Frecuencia de acceso / Agrupación
6. **Autoevaluación**: los grupos se autoevalúan; desviarse de la nota del profesor penaliza

## Pendientes / Ideas para continuar

- [ ] Revisar que los scripts `.js` funcionan correctamente con `mongosh` (no se probaron)
- [ ] Probar los ejercicios de PostgreSQL con una BD real
- [ ] Traducir a ejercicios de código los patrones: atributo, subconjunto, bucket, calculado, atípico
- [ ] Crear versión de los materiales adaptada a .NET Core Web API (si se confirma el cambio)
- [ ] Hacer demo rápida de FastAPI/Django con MongoDB (según el plan)
- [ ] Añadir ejercicios de `$lookup` y aggregation pipeline
- [ ] Añadir nota sobre tiempos de `ObjectId` vs `UUID` vs `string` como `_id`

## Comandos rápidos

```bash
# Ver estructura completa
find 00-unidad-0-previos/docs -type f | sort

# Contar líneas totales
wc -l 00-unidad-0-previos/docs/**/*.md 00-unidad-0-previos/docs/**/**/*.md 00-unidad-0-previos/docs/**/**/*.js

# Ejecutar un script de ejemplo
mongosh < 00-unidad-0-previos/docs/diseno-mongo/scripts/demo-blog.js
```
