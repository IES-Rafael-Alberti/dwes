# Inventario de reforma de UD7

## Diagnóstico inicial

UD7 existía como estructura vacía y una ficha de dieciocho líneas. Presentaba el proyecto como intermodular, pero no definía la evidencia propia de DWES, los stacks aceptados, el seguimiento, la trazabilidad ni instrumentos de revisión. También podía confundirse con un proyecto final de grado.

## Decisión de alcance

UD7 es el proyecto integrador final de **DWES**, no un PFG. El núcleo acredita un servidor propio en Spring Boot 4/Java 25 o Laravel 12/PHP 8.4. Puede coordinar calendario y ritmo con otros módulos, sin entregables ni ámbito de evaluación compartidos y sin que estos sustituyan las evidencias del módulo.

Express/Node.js no es un stack servidor aceptado para esta unidad. No se publicarán soluciones de proyecto, respuestas GIFT, secretos ni baremos inventados.

## Procedencia y adaptación

| Fuente histórica | Alcance heredado | Decisión 2026/27 |
|---|---|---|
| `ProyctoAlumnos/Checklist_DWESv1.2_Unificada.md`, línea 18 y sus referencias de stack | Laravel, Spring Boot, NestJS/Express o equivalentes | Se elimina Express/Nest y se limita el servidor DWES a Spring Boot 4/Java 25 o Laravel 12/PHP 8.4. |
| `ProyctoAlumnos/proyecto_del_alumnado_enunciado_rubrica_checklist.md`, líneas 75–111 y pesos JSON 117–139 | Ponderaciones fijas de backend/modelo y por ítem | Se eliminan los pesos históricos; la rúbrica usa criterios observables sin porcentajes ni puntos inventados. |
| `ProyctoAlumnos/proyecto_del_alumnado_enunciado_rubrica_checklist.md`, líneas 49–61 | Ventana y secuencia temporal fijas de 5–6 semanas y siete hitos | Se adapta a hitos relativos al avance efectivo y al calendario escolar. |
| Índice de UD7 previo a P0, `docs/unidades/ud07.md`, ficha de 18 líneas | Trabajo en grupos de 3–4 | Se elimina el tamaño fijo: el trabajo puede ser individual o en equipo con autorización docente y atribución individual. |

- **Conceptos conservados:** hitos en Git, checklist de servidor, defensa e integración cliente-servidor.
- **Adaptación 2026/27:** todas las restricciones heredadas de la tabla se eliminan o adaptan deliberadamente. El baseline PHP es **PHP 8.4**, establecido de forma canónica en [UD4 — PHP 8.4](../ud04-php/README.md).

## Plan por fases

### P0 — Contrato documental e instrumentos — completado

- [x] Crear índice de unidad, enunciado canónico y seguimiento por hitos relativos.
- [x] Delimitar stacks, prerrequisito tras Spring Boot, elección Laravel y frontera PFG/intermodular.
- [x] Definir matriz práctica RA1–RA9 y rúbrica/checklist sin pesos artificiales.
- [x] Alinear la planificación mínima de curso y los documentos de estado.

### P1 — Plantillas de proyecto y seguimiento del alumnado — completado

- [x] Preparar plantillas sin solución para propuesta y aprobación, responsabilidades, puesta en marcha local, evidencias, defensa y registro reutilizable de hitos.
- [x] Añadir guía interna de triaje y evidencia mínima, excluida de la publicación del alumnado.
- [x] Validar que las plantillas no fuerzan un número artificial de entidades, funcionalidades, integrantes ni tecnologías avanzadas.

### P2 — Materiales docentes de evaluación y defensa — completado

- [x] Preparar localmente, fuera de Git y MkDocs, instrumentos privados para observación, defensa, atribución individual, integración y feedback.
- [x] Mantener las respuestas, correcciones y posibles bancos GIFT fuera del material publicado.

### P3 — Adiciones opcionales por proyecto — no iniciada

P3 solo admite extensiones justificadas por un proyecto concreto —por ejemplo, una integración externa real o una técnica avanzada necesaria para su dominio—. No amplía el alcance común de UD7 ni convierte opciones en requisitos para todo el alumnado.

## Criterio de cierre

UD7 está preparada para cierre editorial: P0, P1 y P2 permiten proponer, seguir, evaluar y defender proyectos reales con evidencia servidor reproducible. La ejecución en clase, el seguimiento docente y cualquier coordinación externa continúan pendientes de su realización; cualquier P3 seguirá siendo opcional y acotada por proyecto.
