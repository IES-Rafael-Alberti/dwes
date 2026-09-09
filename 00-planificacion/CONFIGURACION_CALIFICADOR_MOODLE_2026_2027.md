# Configuración del calificador Moodle — DWES 2026/2027

Esta es la estructura operativa del calificador para DWES. No es un CSV para
importar: Moodle importa desde CSV notas de alumnado, no categorías,
ponderaciones ni la configuración de actividades. La configuración se crea una
vez en un curso plantilla y ese curso se reutiliza mediante copia de seguridad
sin usuarios o importación de curso.

## Criterio de cálculo

- Crear cada categoría con agregación **Media ponderada de calificaciones**.
- Calificar todas las evidencias sobre 100 puntos para que el peso sea legible.
- Las ponderaciones de las tablas son internas a cada UD y suman 100 %.
- Ocultar el total general del curso o identificarlo como **informativo**: la
  calificación final oficial se obtiene por RA/CE con los pesos de la
  programación didáctica, no sumando unidades.
- Usar el texto de la columna `ID` como número de identificación del ítem. Sirve
  para localizar después evidencias y RA/CE.
- Una tarea o cuestionario evaluable crea su ítem de calificación
  automáticamente. No crear además un ítem manual duplicado.
- Crear tareas de seguimiento, propuestas y práctica formativa sin calificación
  o con el ítem oculto/excluido del total de su categoría.

## Árbol de categorías

```text
DWES 2026/2027 (total informativo, oculto)
├── UD1 — HTTP y entorno
│   └── UD1 — Cuestionarios Moodle
├── UD2a — API REST Spring Boot
│   └── UD2a — Cuestionarios Moodle
├── UD3 — MVC Spring Boot
│   └── UD3 — Cuestionarios Moodle
├── UD4 — PHP 8.4
│   └── UD4 — Cuestionarios Moodle
├── UD5 — Laravel 12
│   └── UD5 — Cuestionarios Moodle
├── UD6 — Aplicaciones híbridas
│   └── UD6 — Cuestionarios Moodle
├── UD7 — Proyecto integrador
└── Acreditación individual (sin peso en el total)
```

`Acreditación individual` contiene el hito 1 para conservar la evidencia y su
rúbrica sin convertirlo en una sexta ponderación de UD2a. El hito 2 se integra
en la defensa individual de UD7.

## Ítems evaluables

| Categoría | Actividad Moodle / ítem | Tipo | Peso | ID | RA/CE principal |
|---|---|---|---:|---|---|
| UD1 | Práctica cooperativa HTTP + Hello Server | Tarea | 65 % | `ud1-practica-http` | RA1.a-g |
| UD1 / Cuestionarios | Quiz HTTP | Cuestionario | 50 % de cuestionarios | `ud1-quiz-http` | RA1.a-d |
| UD1 / Cuestionarios | Quiz entorno y TDD | Cuestionario | 50 % de cuestionarios | `ud1-quiz-entorno-tdd` | RA1.e-g |
| UD1 | Total de cuestionarios | Categoría | 35 % | `ud1-cuestionarios` | RA1.a-g |
| UD2a | Mini Spring Boot Tasks | Tarea | 15 % | `ud2a-mini-tasks` | RA1.g, RA5, RA6, RA7 |
| UD2a | Book Catalog API | Tarea | 20 % | `ud2a-book-catalog` | RA5, RA7 |
| UD2a | Gestión de eventos | Tarea | 25 % | `ud2a-gestion-eventos` | RA5, RA6, RA7 |
| UD2a / Cuestionarios | Spring/TDD, REST, persistencia, seguridad y contratos | Cinco cuestionarios | 20 % cada uno | `ud2a-quiz-*` | RA5-RA7 |
| UD2a | Total de cuestionarios | Categoría | 25 % | `ud2a-cuestionarios` | RA5-RA7 |
| UD2a | Defensa individual | Tarea sin entrega o ítem manual | 15 % | `ud2a-defensa` | RA1.g, RA5-RA7 |
| UD3 | Productos MVC incremental | Tarea | 60 % | `ud3-productos-mvc` | RA4-RA6, RA8 |
| UD3 / Cuestionarios | MVC/Thymeleaf y sesión/seguridad | Dos cuestionarios | 50 % cada uno | `ud3-quiz-*` | RA4, RA5, RA8 |
| UD3 | Total de cuestionarios | Categoría | 25 % | `ud3-cuestionarios` | RA4, RA5, RA8 |
| UD3 | Defensa individual | Tarea sin entrega o ítem manual | 15 % | `ud3-defensa` | RA4-RA6, RA8 |
| UD4 | GTask PHP 8.4 OOP | Tarea | 60 % | `ud4-gtask` | RA5, RA6 |
| UD4 / Cuestionarios | PHP seguro y PDO/OOP | Dos cuestionarios | 50 % cada uno | `ud4-quiz-*` | RA2-RA6 |
| UD4 | Total de cuestionarios | Categoría | 25 % | `ud4-cuestionarios` | RA2-RA6 |
| UD4 | Defensa individual | Tarea sin entrega o ítem manual | 15 % | `ud4-defensa` | RA3-RA6 |
| UD5 | API Laravel 12 de recetas | Tarea | 60 % | `ud5-laravel-api` | RA2, RA4-RA7 |
| UD5 / Cuestionarios | Laravel API y Eloquent/seguridad | Dos cuestionarios | 50 % cada uno | `ud5-quiz-*` | RA4-RA7 |
| UD5 | Total de cuestionarios | Categoría | 25 % | `ud5-cuestionarios` | RA4-RA7 |
| UD5 | Defensa individual | Tarea sin entrega o ítem manual | 15 % | `ud5-defensa` | RA2, RA4-RA7 |
| UD6 | Proyecto de transferencia P2B | Tarea | 60 % | `ud6-p2b` | RA9.a-h |
| UD6 / Cuestionarios | Integración y seguridad/resiliencia | Dos cuestionarios | 50 % cada uno | `ud6-quiz-*` | RA9.a-h |
| UD6 | Total de cuestionarios | Categoría | 25 % | `ud6-cuestionarios` | RA9.a-h |
| UD6 | Defensa individual | Tarea sin entrega o ítem manual | 15 % | `ud6-defensa` | RA9.a-h |
| UD7 | Proyecto final de DWES | Tarea | 70 % | `ud7-proyecto-final` | RA1-RA9 aplicables |
| UD7 | Defensa individual y modificación dirigida | Tarea sin entrega o ítem manual | 30 % | `ud7-defensa-hito2` | RA1, RA5-RA7; RA8/RA9 si aplican |
| Acreditación individual | Hito individual 1 — servidor mínimo verificable | Tarea | Excluido | `hito-individual-1` | RA1, RA5, RA7 |

## Elementos sin nota independiente

- UD2a Battleship: code-along, no crear tarea calificable.
- UD4 Notes procedural: práctica preparatoria; puede existir como tarea sin nota.
- UD6 P2A: preparación guiada de P2B; puede existir como tarea sin nota.
- UD7 propuesta, seguimiento y diseño: requisitos de proceso integrados en el
  proyecto final, no tres calificaciones adicionales.

## Orden de montaje en Moodle

1. Crear el curso plantilla `DWES 2026/2027 — plantilla`, sin alumnado.
2. Crear las ocho categorías del árbol y seleccionar media ponderada.
3. Crear primero las tareas y cuestionarios de UD1 y UD2a; al hacerlo aparecen
   sus ítems automáticamente en el calificador.
4. Mover cada ítem a su categoría, introducir el ID y aplicar exactamente los
   pesos de la tabla. Crear las subcategorías de cuestionarios antes de mover los
   quiz correspondientes.
5. Crear los ítems sin entrega para las defensas y el hito 1; excluir el hito 1
   del total de categoría y de curso.
6. Crear las actividades de UD3-UD7 cuando se publiquen, manteniendo su ID y
   peso previstos.
7. Comprobar con una cuenta de prueba que las categorías suman 100 % y realizar
   una copia de seguridad del curso sin usuarios.

No usar la importación CSV de notas para este montaje. Cuando Moodle Centros
2026/2027 esté disponible, la primera restauración de prueba confirmará qué
opciones de copia conserva la instalación.

La cobertura y acreditación por resultados de aprendizaje se controla aparte en
`MATRIZ_SEGUIMIENTO_RA_CE_2026_2027.md` y su plantilla CSV; no se deduce del
total numérico del calificador.
