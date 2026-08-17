# UD7 — Proyecto integrador de DWES

UD7 cierra el módulo de Desarrollo Web en Entorno Servidor con un proyecto que demuestra competencia propia de servidor. No es el Proyecto Final de Grado ni sustituye sus requisitos.

## Ruta rápida

1. Tras terminar Spring Boot, presentar un tema y esperar su aprobación docente.
2. Construir y evidenciar un servidor propio con uno de los stacks oficiales.
3. Mantener la trazabilidad en el repositorio y preparar la defensa con una demostración real.

## Alcance y prerrequisito

El proyecto parte de los contenidos de Spring Boot. El tema se propone y aprueba después de ese bloque; Laravel puede seguir en curso en ese momento. Por ello, la elección inicial no puede depender de que Laravel esté ya terminado: quien lo elija deberá asumir responsablemente el baseline que se haya impartido y las orientaciones acordadas.

El dominio debe ser no trivial en el servidor: debe contener reglas, estados, restricciones o procesos que no se reduzcan a pantallas conectadas a operaciones CRUD planas.

## Stacks oficiales

| Permitido | Condición |
|---|---|
| Spring Boot 4 con Java 25 | Ruta oficial Java del módulo. |
| Laravel 12 con PHP 8.4 | Ruta oficial PHP del módulo. |

**Express y Node.js no se aceptan como stack servidor para la evidencia de DWES.** Un proyecto que los use como backend no satisface esta unidad, aunque pueda usar otras tecnologías en su interfaz.

## Límites de coordinación

La coordinación con Cliente, Diseño de Interfaces Web y Despliegue de Aplicaciones Web **solo sincroniza calendario y ritmo**. No crea entregables compartidos ni un ámbito de evaluación compartido: DWES evalúa sus propias evidencias de servidor.

- reutilizar un cliente de otro módulo es opcional; solo aporta evidencia a DWES cuando prueba un flujo completo y visible a través del servidor propio permitido del estudiante: acción visible → petición → endpoint → servicio/regla → persistencia → respuesta o error, incluida la gestión de respuestas y errores;
- el trabajo exclusivamente de frontend, diseño visual o despliegue no acredita por sí mismo el servidor;
- cada integrante debe poder explicar y defender su aportación de servidor, también en trabajos de equipo.

## Modelo temporal

Los hitos son relativos al avance efectivo del curso, no fechas rígidas. El proyecto debe estar casi terminado a finales de enero, sujeto al calendario escolar. La secuencia y los puntos de control están en la [guía de hitos](01-documentacion/02-seguimiento-hitos.md).

## Mapa de artefactos

| Documento | Uso |
|---|---|
| [Enunciado canónico](01-documentacion/01-proyecto-final-dwes.md) | Qué debe entregar y demostrar el alumnado. |
| [Seguimiento por hitos](01-documentacion/02-seguimiento-hitos.md) | Cómo mostrar avance, recibir feedback y reducir riesgos. |
| [Propuesta y aprobación](03-ejercicios/propuesta-proyecto/README.md) | Plantilla para delimitar y aprobar el tema antes de consolidar el desarrollo. |
| [Seguimiento de hitos](03-ejercicios/seguimiento-proyecto/README.md) | Plantilla reutilizable de evidencias, trazabilidad, autoría y feedback. |
| [Preparación de defensa](03-ejercicios/seguimiento-proyecto/plantilla-defensa.md) | Guion para demostrar y explicar el servidor con evidencia real. |
| [Evidencias RA/CE](04-proyectos/proyecto-final-dwes/ra-ce-evidencias.md) | Qué evidencias pueden relacionarse con la rúbrica común. |
| [Rúbrica y checklist](04-proyectos/proyecto-final-dwes/rubrica-y-checklist-dwes.md) | Criterios observables para revisión y autoevaluación. |
| [Inventario de reforma](INVENTARIO_REFORMA.md) | Estado editorial de UD7 y fases restantes. |

## Estado editorial

P0, P1 y P2 están completadas: la unidad dispone de contrato y seguimiento públicos; los instrumentos privados de revisión y defensa se conservan localmente, fuera de Git y MkDocs. Está preparada para su cierre editorial y para la ejecución en clase; la aplicación real, el seguimiento docente y cualquier coordinación externa continúan siendo operaciones pendientes. P3 sigue siendo opcional y específica de cada proyecto.

## Verificación de la unidad

- [ ] El tema está aprobado y delimita claramente el servidor propio.
- [ ] El repositorio accesible al docente contiene trazabilidad, sin secretos ni archivos `.env` versionados.
- [ ] Hay una puesta en marcha local reproducible y pruebas de las reglas relevantes.
- [ ] La integración cliente-servidor, si existe, muestra el recorrido completo y los errores relevantes.
- [ ] La defensa permite recorrer el servidor y realizar o razonar un cambio pequeño dirigido.

La evaluación se apoya en `00-planificacion/rubrica_comun_DWES_por_RA_CE.md` y en los instrumentos específicos de esta unidad; no se publican soluciones, respuestas GIFT ni baremos numéricos en este material.
