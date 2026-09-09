# Guía para replicar la evaluación, Moodle y seguimiento RA/CE

Proceso aplicado en DWES 2026/2027 para organizar la evaluación de un grupo
numeroso. Debe adaptarse a los RA, CE, instrumentos y normativa de cada módulo;
no copiar ponderaciones ni evidencias sin revisarlas.

## Objetivo

Preparar cuatro artefactos coherentes antes de abrir el curso en Moodle:

1. Ponderaciones de instrumentos por unidad, cerradas y documentadas.
2. Estructura de categorías, actividades y pesos del calificador Moodle.
3. Matriz de distribución de todos los CE entre unidades y evidencias.
4. Hoja de seguimiento individual para decidir la acreditación de cada RA.

## Orden de trabajo

1. Inventariar todas las prácticas, proyectos, cuestionarios, defensas y
   actividades históricas.
2. Identificar qué actividades son evaluables, formativas, ejemplos o material
   retirado. Evitar dos notas distintas por la misma evidencia.
3. Asociar cada actividad evaluable a RA/CE concretos y definir una evidencia
   individual verificable cuando la actividad sea cooperativa.
4. Cerrar la ponderación interna de cada unidad. Debe sumar 100 % y separar, si
   procede, práctica/proyecto, cuestionarios y defensa individual.
5. Crear un documento de configuración Moodle con categorías, subcategorías,
   nombres, ID de ítem, tipo de actividad, peso, máximo y RA/CE principal.
6. Crear una matriz central con una fila por CE: unidad primaria, refuerzos,
   ítem Moodle, evidencia individual y regla de cierre.
7. Crear una hoja de cálculo docente con una fila por CE y estados de
   acreditación; duplicarla por estudiante o usarla como base de una tabla por
   alumno.
8. Revisar que los totales, referencias RA/CE y rúbricas no se contradigan;
   ejecutar las validaciones de documentación del repositorio.

## Moodle: límite y solución operativa

El CSV nativo de Moodle importa calificaciones de alumnado. No configura de
forma completa categorías, agregaciones, ponderaciones ni actividades.

La solución reutilizable es:

1. Crear un curso plantilla sin alumnado.
2. Montar el calificador y las actividades de cada unidad una vez.
3. Realizar una copia de seguridad sin usuarios.
4. Restaurar o importar ese curso como punto de partida para cada grupo o curso.
5. Probar primero una restauración en un curso de ensayo, porque la instalación
   de Moodle puede limitar opciones de copia.

Si el centro habilita servicios web con permisos suficientes, puede estudiarse
una automatización por API. No sustituye la comprobación docente de categorías,
pesos y cálculos.

## Diseño recomendado del calificador

- Una categoría superior por unidad y, si hay varios cuestionarios, una
  subcategoría de cuestionarios.
- Agregación mediante media ponderada dentro de cada unidad.
- Todos los ítems sobre 100 puntos para leer los pesos sin conversión adicional.
- Las tareas y cuestionarios evaluables crean su propio ítem de calificación;
  no añadir un ítem manual duplicado.
- Usar tareas sin entrega o ítems manuales solo para defensas y evidencias que
  no tienen actividad calificable propia.
- Actividades formativas, propuestas, seguimiento o ejemplos: sin nota o
  excluidos del total.
- Si la nota oficial es criterial por RA/CE, ocultar o marcar como informativo el
  total numérico del curso. No usar una suma de unidades como calificación final
  si contradice la programación.

## Seguimiento de RA/CE

Estados mínimos por CE:

| Estado | Significado |
|---|---|
| `NE` | Sin evidencia individual evaluada. |
| `PE` | Evidencia entregada o parcialmente revisada. |
| `AC` | Acreditado: evidencia individual suficiente y nota igual o superior a 5. |
| `NR` | No acreditado: precisa refuerzo o recuperación. |

Un RA queda cerrado cuando todos sus CE tienen evidencia suficiente y el cálculo
establecido en la programación confirma su superación. Si la programación usa
la media de CE, registrar también esa media. Una entrega cooperativa no acredita
por sí sola a cada integrante: hay que verificar contribución, comprensión y
defensa individual.

## Entregables por módulo

Usar nombres equivalentes a estos, adaptados al módulo:

```text
00-planificacion/CONFIGURACION_CALIFICADOR_MOODLE_<CURSO>.md
00-planificacion/MATRIZ_SEGUIMIENTO_RA_CE_<CURSO>.md
00-planificacion/plantilla_seguimiento_ra_ce.csv
```

En DWES, estos tres ficheros son la referencia de ejemplo. Antes de replicarlos
en otro módulo hay que contrastar los CE oficiales, la programación vigente y
las actividades realmente impartidas.
