# Proyecto final de DWES — Enunciado canónico

El proyecto final de **DWES** demuestra que puedes diseñar, implementar, probar y explicar un servidor propio. Es el proyecto de cierre del módulo; **no es el Proyecto Final de Grado** y no incorpora requisitos ajenos a DWES.

## Qué hacer primero

1. Después de terminar Spring Boot, presenta una propuesta de tema, problema, usuarios, reglas de servidor y stack previsto.
2. Espera la aprobación docente antes de consolidar el desarrollo.
3. Mantén el trabajo trazable en un repositorio y contrasta cada hito con el docente.

La elección de Laravel es posible, pero Laravel puede no estar concluido cuando se aprueba el tema. En ese caso, la propuesta debe ser viable con el baseline ya impartido y puede concretarse con el feedback posterior; no se aplaza la responsabilidad de definir un dominio servidor no trivial.

## Alcance del proyecto

Elige un dominio con decisiones y reglas del lado servidor: estados, permisos, reservas, cálculos, límites, transiciones, consistencia o procesos verificables. Incluye al menos **tres reglas, estados o procesos de negocio independientes y comprobables en servidor**. La calidad y la pertinencia para el dominio importan más que el número: un catálogo, formulario o CRUD no cuentan por sí solos como regla de negocio.

Son ejemplos adecuados una reserva de recursos con conflictos de disponibilidad, una gestión de incidencias con estados y permisos, un préstamo de material con sanciones o una planificación de turnos con restricciones. Evita proponer una tienda en línea si solo contiene catálogo, carrito y pedidos: suele reducirse a CRUD y solo será viable si incorpora reglas de servidor sustanciales y verificables más allá de esas operaciones.

El trabajo es individual por defecto. Puedes trabajar en pareja si el docente lo autoriza. Un equipo de tres personas solo se autoriza cuando el dominio aprobado tiene tres responsabilidades de servidor claramente separables; no se aceptan grupos de más de tres personas. El alumnado puede proponer el grupo, pero el docente aprueba a la vez el tema, el reparto inicial de responsabilidades de servidor y el acceso al repositorio.

En cada hito, documenta la responsabilidad, una incidencia/commit o evidencia equivalente y aquello que cada persona puede explicar y modificar. La defensa y la trazabilidad deben permitir atribuir autoría real: nadie acredita trabajo de servidor que no pueda explicar, localizar y modificar. La evaluación es individual; no hay una nota común automática.

## Uso responsable de IA

La [declaración común de uso de IA](../../plantillas/plantilla-declaracion-uso-ia.md) es obligatoria: marca **No** si no has usado IA. Si la usaste, registra la herramienta y el propósito, qué resultado incorporaste, modificaste o rechazaste, y cómo lo verificaste. La verificación debe aportar evidencia: pruebas, documentación oficial, fuentes contrastadas, comprobación manual o revisión. En la defensa debes poder explicar esas decisiones; la IA es un asistente supervisado, no una delegación de autoría.

No incluyas secretos, datos personales ni datos privados del proyecto en los prompts.

## Stack, persistencia y límites tecnológicos

El servidor DWES debe construirse con una de estas rutas:

- **Spring Boot 4 con Java 25**.
- **Laravel 12 con PHP 8.4**.

**PostgreSQL es obligatorio por defecto**. MySQL puede aceptarse como alternativa. MongoDB es un plus opcional, normalmente propio del módulo de stack MERN, y no un requisito de DWES; solo puede aprobarse si el dominio lo justifica. Su diseño debe ser explícito y justificar cómo la aplicación mantiene consistencia e integridad sin las restricciones relacionales habituales. En cualquier otro caso se exige una base de datos relacional.

**Node.js, Express y cualquier framework de servidor no visto en clase están estrictamente prohibidos como evidencia DWES.** Tampoco se acepta sustituir el servidor por un frontend, una maqueta visual o una configuración de despliegue.

## Diseño y requisitos de servidor DWES

Antes de implementar de forma sostenida, documenta el diseño y **por qué** tomas cada elección significativa. Para un modelo relacional incluye un diagrama entidad-relación, entidades, relaciones, restricciones y su vínculo con las reglas. Define una política explícita de ciclo de vida y retención: no apliques `ON DELETE CASCADE` por inercia; justifica conservación, archivo, anonimización, baja lógica o borrado de históricos como antiguos clientes o trabajadores. Si el dominio incluye existencias, modela y prueba disponibilidad, reserva o ajuste, validación, concurrencia/integridad y auditoría o ciclo de vida. Explica cómo se plasma en entidades, repositorios y migraciones de Spring Boot, o en modelos y migraciones de Laravel. Actualiza el diseño cuando la implementación se aparte materialmente de él y registra el motivo.

El proyecto debe aportar evidencia adecuada al dominio de:

- arquitectura con responsabilidades separadas: entrada HTTP o MVC, reglas/casos de uso y acceso a datos no se mezclan sin justificación;
- en **Spring Boot**, configuración, entidades/modelos, controladores, servicios/casos de uso, repositorios, DTO/formularios cuando hagan falta, validación, errores, seguridad cuando aplique y **migraciones obligatorias** según lo visto en el curso;
- en **Laravel**, una separación idiomática de responsabilidades; los controladores no concentran reglas de negocio o persistencia sin justificación;
- modelo de datos y cambios reproducibles mediante migraciones o mecanismo equivalente del stack;
- validación en servidor y respuestas/errores consistentes para datos inválidos, ausencia de recursos y conflictos de reglas cuando proceda;
- autenticación y autorización cuando el dominio tenga identidades, datos o acciones que proteger; la solución debe corresponder al riesgo real, no añadirse como adorno;
- pruebas automatizadas de reglas, casos de error y flujos relevantes;
- documentación técnica y runbook de puesta en marcha local reproducible;
- contrato OpenAPI cuando se exponga una API; debe describir los flujos, entradas, respuestas y seguridad relevantes;
- un servidor que se pueda ejecutar localmente siguiendo la documentación, sin intervención manual del autor ni secretos compartidos.

La aplicación puede ser API, MVC o combinar ambos estilos de forma justificada. Se valora el comportamiento observable y la separación de responsabilidades, no el nombre de un framework o patrón usado sin evidencia.

## Integración con cliente

Se puede reutilizar un cliente desarrollado en otros módulos. En DWES solo se considera evidencia la integración real con el servidor propio. Debe poder mostrarse un flujo visible que llegue a una petición, endpoint, lógica de negocio/servicio, persistencia y respuesta o error controlado.

El cliente no sustituye el código, las pruebas ni la explicación del servidor. Del mismo modo, un despliegue correcto no sustituye la puesta en marcha local reproducible requerida en DWES.

## Repositorio y trazabilidad

Usa GitHub, GitLab, Bitbucket u otro alojamiento Git equivalente accesible para el docente. El repositorio puede ser privado. Debe conservar:

- commits que expliquen el avance y permitan revisar responsabilidades;
- incidencias, tablero o mecanismo equivalente para decisiones, tareas y seguimiento;
- etiquetas o releases periódicos asociados a los hitos;
- documentación de cómo ejecutar, probar y demostrar el proyecto.
- la [declaración de uso de IA](../../plantillas/plantilla-declaracion-uso-ia.md), obligatoria y marcada explícitamente con **No** si no hubo uso.

No subas contraseñas, tokens, claves, datos privados ni archivos `.env` con valores reales. Incluye una plantilla segura y documenta las variables necesarias sin revelar sus valores.

CI/CD y despliegue pueden aportar valor como integración opcional, pero no son obligatorios ni se califican en DWES: su evaluación obligatoria corresponde al módulo de Despliegue y a su profesorado.

## Entrega y defensa

La entrega final incluye el repositorio y las evidencias necesarias para revisarlo. En la defensa se realizará un recorrido vivo del servidor: dominio, flujo relevante, datos, reglas, pruebas y decisiones. También podrás recibir un cambio pequeño dirigido o una petición de razonarlo y localizar sus capas afectadas. El objetivo es demostrar comprensión y autoría, no memorizar una presentación.

Consulta la [guía de seguimiento](02-seguimiento-hitos.md), la [matriz de evidencias](../04-proyectos/proyecto-final-dwes/ra-ce-evidencias.md) y la [rúbrica/checklist](../04-proyectos/proyecto-final-dwes/rubrica-y-checklist-dwes.md) antes de cada revisión.
