# Estructura común de unidades — DWES 2026/2027

```
udXX-nombre-breve/
├── README.md                    → Propósito, orden recomendado, materiales principales,
│                                   prácticas asociadas, evaluaciones, pendientes.
├── 01-teoria/                   → Apuntes, documentación fuente, presentaciones.
│                                   Formatos: .md, .Rmd, .org, .tex, .pdf, .html.
├── 02-ejemplos/                 → Demos, ejemplos no evaluables, scripts de apoyo.
│                                   Sin evaluación asociada.
├── 03-practicas/                → Prácticas guiadas, laboratorios, katas.
│                                   TDD desde la primera práctica.
│                                   Más adelante: spec-driven development.
├── 04-evaluacion/               → Enunciados evaluables, rúbricas, plantillas de entrega.
├── 05-recursos/                 → Plantillas de proyecto, docker-compose, configs,
│                                   datasets, requirements, packs reutilizables.
├── 06-seguridad/                → Seguridad específica de la unidad.
│                                   Contenidos, ejemplos y prácticas de seguridad
│                                   integrados en cada bloque temático.
│                                   Especialización en ciberseguridad del centro.
├── 90-archivo/                  → Versiones antiguas, material histórico,
│                                   duplicados conservados como referencia.
└── 99-profesor/                 → Soluciones, correcciones, prompts de IA,
                                    guías de corrección, notas internas.
```

## Criterios de clasificación

| Categoría | Destino |
| --------- | ------- |
| Apuntes, teoría, documentos fuente | `01-teoria/` |
| Ejemplos no evaluables, scripts demo | `02-ejemplos/` |
| Guiones de laboratorio, prácticas de aula, katas con TDD | `03-practicas/` |
| Enunciados evaluables, rúbricas, entregas | `04-evaluacion/` |
| Plantillas, docker-compose, configs, packs | `05-recursos/` |
| Seguridad específica del bloque (OWASP, JWT, CORS, etc.) | `06-seguridad/` |
| Versiones antiguas, históricos | `90-archivo/` |
| Soluciones, prompts, notas de profesor | `99-profesor/` |
| Recursos transversales del módulo | `00-recursos-comunes/` |
| Planificación, normativa, RA/CE, secuencia didáctica | `00-planificacion/` |

## Notas sobre la estructura

- **`06-seguridad/`** es la novedad respecto a la estructura SBD original. Cada unidad debe tener contenidos de seguridad específicos (XSS en la unidad de PHP, JWT en API REST, SQL injection en acceso a BBDD, etc.), no solo una unidad final de "seguridad".
- **`03-practicas/`** asume TDD como método desde la sesión 1. Las prácticas empiezan con test escritos (rojo) y el alumnado implementa hasta que pasan (verde). Más adelante se introduce spec-driven development (contratos OpenAPI, guardrails de propiedades).
- **`04-evaluacion/`** incluye enunciados y rúbricas, no entregas de alumnado. Las entregas se recogen por otro canal (Moodle, repos alumnado).
