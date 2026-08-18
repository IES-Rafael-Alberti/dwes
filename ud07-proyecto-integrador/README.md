# UD7 — Proyecto integrador de DWES

UD7 cierra el módulo de Desarrollo Web en Entorno Servidor con un proyecto que demuestra competencia propia de servidor. No es el Proyecto Final de Grado ni sustituye sus requisitos.

## Ruta rápida

1. Tras terminar Spring Boot, presentar un tema y esperar su aprobación docente.
2. Construir y evidenciar un servidor propio con uno de los stacks oficiales.
3. Mantener la trazabilidad en el repositorio y preparar la defensa con una demostración real.

## Alcance y prerrequisito

El proyecto parte de los contenidos de Spring Boot. El tema se propone y aprueba después de ese bloque; Laravel puede seguir en curso en ese momento. Por ello, la elección inicial no puede depender de que Laravel esté ya terminado: quien lo elija deberá asumir responsablemente el baseline que se haya impartido y las orientaciones acordadas.

El dominio debe ser no trivial en el servidor: debe contener reglas, estados, restricciones o procesos que no se reduzcan a pantallas conectadas a operaciones CRUD planas.

Como referencia, son dominios adecuados una reserva de recursos con conflictos de disponibilidad, una gestión de incidencias con estados y permisos, un préstamo de material con sanciones o una planificación de turnos con restricciones. Una tienda en línea suele quedarse en catálogo, carrito y pedidos CRUD; no la propongas salvo que el alcance demuestre reglas de servidor sustanciales y verificables más allá de esas operaciones.

Incluye como mínimo **tres reglas, estados o procesos de negocio independientes y comprobables en servidor**. La calidad y la pertinencia para el dominio importan más que el número: CRUD por sí solo no cuenta como una regla de negocio.

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

## Modalidad de trabajo y autoría

El trabajo es **individual por defecto**. Las parejas requieren aprobación docente. Un equipo de tres personas solo se aprueba cuando el dominio propuesto contiene tres responsabilidades de servidor claramente separables; no se admiten equipos de más de tres personas.

El alumnado puede proponer su grupo, pero el docente aprueba conjuntamente el tema, el reparto inicial de responsabilidades de servidor y el acceso al repositorio. En cada hito, cada persona deja evidencia localizable de su responsabilidad —una incidencia, commit o equivalente— y de lo que puede explicar y modificar. La evaluación es individual: no existe una nota común automática.

## Diseño, datos y arquitectura

Antes de consolidar el desarrollo, documenta el diseño y la razón de cada elección significativa. Para una base de datos relacional incluye un diagrama entidad-relación, relaciones, restricciones e impacto en las reglas. Explica cómo ese diseño se traduce a entidades, repositorios y migraciones en Spring Boot, o a modelos y migraciones en Laravel. Si la implementación se separa materialmente del diseño, actualízalo y deja trazada la decisión.

En Spring Boot se aplican los elementos vistos en el curso que correspondan al proyecto: configuración, entidades/modelos, controladores, servicios o casos de uso, repositorios, DTO/formularios cuando eviten exponer el modelo, validación, errores, seguridad cuando proceda y **migraciones obligatorias**. Laravel puede organizarse de forma idiomática, pero el controlador no debe absorber reglas de negocio o persistencia sin una justificación documentada.

## Persistencia y límites tecnológicos

**PostgreSQL es la opción obligatoria por defecto**; MySQL puede aceptarse como alternativa. MongoDB solo puede aprobarse si el diseño de datos MongoDB se ha impartido efectivamente a ese grupo y el dominio justifica su uso. En cualquier otro caso, la persistencia debe ser relacional.

Solo Spring Boot 4/Java 25 o Laravel 12/PHP 8.4 son rutas de servidor aceptadas. Node.js, Express y cualquier framework de servidor no impartido en clase están estrictamente prohibidos como evidencia DWES. GitHub, GitLab, Bitbucket u otro alojamiento Git equivalente es obligatorio; CI/CD es un plus si aporta valor, no un requisito ni una fuente de peso de calificación.

## Uso responsable de IA

La [declaración común de uso de IA](../plantillas/plantilla-declaracion-uso-ia.md) es obligatoria: marca **No** si no has usado IA. Si la usaste, registra qué herramienta y propósito tuvo, qué aceptaste/cambiaste/rechazaste, cómo lo verificaste y la evidencia (pruebas, documentación oficial, comprobaciones manuales o revisión). Debes poder explicarlo en la defensa; la IA es un asistente supervisado, no una sustitución de autoría.

No incluyas secretos, datos personales ni datos privados del proyecto en los prompts.

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
- [ ] El diseño documenta decisiones y razones, datos, relaciones, restricciones, migraciones y su correspondencia con el código.
- [ ] Hay una puesta en marcha local reproducible y pruebas de las reglas relevantes.
- [ ] La integración cliente-servidor, si existe, muestra el recorrido completo y los errores relevantes.
- [ ] La defensa permite recorrer el servidor y realizar o razonar un cambio pequeño dirigido.
- [ ] La declaración común de IA está incluida: marca **No** si no se usó IA o registra cada uso material, qué se cambió/rechazó y la evidencia de su verificación.

La evaluación se apoya en `00-planificacion/rubrica_comun_DWES_por_RA_CE.md` y en los instrumentos específicos de esta unidad; no se publican soluciones, respuestas GIFT ni baremos numéricos en este material.
