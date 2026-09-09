# Matriz de cobertura y seguimiento individual RA/CE — DWES 2026/2027

Esta matriz es la referencia rápida para saber dónde se trabaja cada criterio de
evaluación (CE) y cuándo hay evidencia suficiente para valorar cada resultado de
aprendizaje (RA). Complementa el calificador Moodle: sus categorías organizan
instrumentos y pesos, pero no sustituyen el registro criterial por RA/CE.

La plantilla editable está en `plantilla_seguimiento_ra_ce.csv`. Se abre con una
hoja de cálculo, se duplica por alumno o alumna y se completa después de cada
evidencia individual revisada.

## Regla de acreditación

- `NE`: sin evidencia individual evaluada; el CE no se considera cerrado.
- `PE`: evidencia entregada o revisada parcialmente; falta calificar o defender.
- `AC`: evidencia individual suficiente, calificación de CE igual o superior a
  5.
- `NR`: no acreditado; requiere refuerzo o recuperación del CE.
- Un RA se marca **cerrado** solo cuando todos sus CE tienen estado `AC` y la
  media aritmética de sus CE es igual o superior a 5, conforme a la programación.
- Una actividad grupal solo aporta `AC` tras comprobar el slice, la trazabilidad
  y la defensa individual de la persona evaluada.
- Para RA1, RA5, RA6 y RA7 debe existir evidencia práctica individual suficiente;
  un cuestionario por sí solo no cierra esos RA.

## Distribución principal

| RA | CE | Unidad de evidencia primaria | Refuerzo o contraste | Evidencia individual de cierre |
|---|---|---|---|---|
| RA1 | a-g | UD1 | UD2a y hito 1 para g | Práctica HTTP + Hello Server, cuestionarios y hito 1 cuando proceda |
| RA2 | a-h | UD4 | UD5 | Cuestionarios PHP y evidencia ejecutable de GTask |
| RA3 | a-g | UD4 | — | Cuestionarios PHP, formularios y código defendido de GTask |
| RA4 | a-f | UD3 | UD4 y UD5 | Productos MVC, cuestionarios y defensa individual |
| RA5 | a-b, g-h | UD2a | UD3-UD5 | Mini Tasks, Book Catalog, Gestión de eventos y hito 1 |
| RA5 | c-f | UD3 | UD4 y UD5 | Productos MVC, cuestionarios y defensa individual |
| RA6 | a-g | UD2a | UD4 y UD5 | Mini Tasks y Gestión de eventos; defensa y pruebas individuales |
| RA7 | a-h | UD2a | UD6 para consumo e integración | Book Catalog, Gestión de eventos, cuestionarios y hito 1 |
| RA8 | a-g | UD3 | UD7 cuando el proyecto incluya MVC | Productos MVC, cuestionarios y defensa individual |
| RA9 | a-h | UD6 | UD7 solo si hay integración real | P2B, cuestionarios y defensa individual |

UD0, UD2b y UD2c son nivelación, ejemplo o comparación: no son la evidencia
primaria de ningún CE. UD7 consolida evidencias pertinentes del proyecto, pero
no obliga a repetir CE que ya estén acreditados ni a introducir integraciones
artificiales.

## Uso con Moodle

1. Mantener esta matriz fuera del cálculo de totales de Moodle.
2. Al corregir una actividad, registrar en la plantilla solo los CE que esa
   actividad demuestra realmente; no marcar todos los CE del RA por defecto.
3. Anotar como referencia el ID del ítem Moodle definido en
   `CONFIGURACION_CALIFICADOR_MOODLE_2026_2027.md` y el enlace al repositorio,
   prueba o defensa que sustenta la decisión.
4. Filtrar la hoja por `RA` y comprobar que no quedan estados `NE`, `PE` o `NR`
   antes de declarar el RA cerrado.
5. Si un CE queda `NR`, planificar una evidencia equivalente individual y volver
   a registrar el resultado; no sustituirlo por una nota común del grupo.

## Límites

La matriz señala la ubicación y la evidencia mínima, no convierte una lista de
archivos en una calificación automática. La rúbrica común y las rúbricas
específicas determinan el nivel de logro; la matriz solo asegura cobertura,
trazabilidad y una decisión de acreditación verificable.
