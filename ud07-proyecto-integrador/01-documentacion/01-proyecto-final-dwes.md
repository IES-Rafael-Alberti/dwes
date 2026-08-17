# Proyecto final de DWES — Enunciado canónico

El proyecto final de **DWES** demuestra que puedes diseñar, implementar, probar y explicar un servidor propio. Es el proyecto de cierre del módulo; **no es el Proyecto Final de Grado** y no incorpora requisitos ajenos a DWES.

## Qué hacer primero

1. Después de terminar Spring Boot, presenta una propuesta de tema, problema, usuarios, reglas de servidor y stack previsto.
2. Espera la aprobación docente antes de consolidar el desarrollo.
3. Mantén el trabajo trazable en un repositorio y contrasta cada hito con el docente.

La elección de Laravel es posible, pero Laravel puede no estar concluido cuando se aprueba el tema. En ese caso, la propuesta debe ser viable con el baseline ya impartido y puede concretarse con el feedback posterior; no se aplaza la responsabilidad de definir un dominio servidor no trivial.

## Alcance del proyecto

Elige un dominio con decisiones y reglas del lado servidor: estados, permisos, reservas, cálculos, límites, transiciones, consistencia o procesos verificables. Un catálogo o formulario puede formar parte del proyecto, pero no basta si todo se reduce a CRUD sin reglas.

Puedes trabajar individualmente o en equipo si el docente lo autoriza. En equipo se documentan responsabilidades, aportaciones y decisiones de cada persona. La defensa y la trazabilidad deben permitir atribuir autoría real: nadie acredita trabajo de servidor que no pueda explicar, localizar y modificar.

## Stack permitido

El servidor DWES debe construirse con una de estas rutas:

- **Spring Boot 4 con Java 25**.
- **Laravel 12 con PHP 8.4**.

**Node.js y Express no están aceptados como servidor DWES.** Tampoco se acepta sustituir el servidor por un frontend, una maqueta visual o una configuración de despliegue.

## Requisitos de servidor DWES

El proyecto debe aportar evidencia adecuada al dominio de:

- arquitectura con responsabilidades separadas: entrada HTTP o MVC, reglas/casos de uso y acceso a datos no se mezclan sin justificación;
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

Usa GitHub, GitLab u otro alojamiento Git equivalente accesible para el docente. El repositorio puede ser privado. Debe conservar:

- commits que expliquen el avance y permitan revisar responsabilidades;
- incidencias, tablero o mecanismo equivalente para decisiones, tareas y seguimiento;
- etiquetas o releases periódicos asociados a los hitos;
- documentación de cómo ejecutar, probar y demostrar el proyecto.

No subas contraseñas, tokens, claves, datos privados ni archivos `.env` con valores reales. Incluye una plantilla segura y documenta las variables necesarias sin revelar sus valores.

## Entrega y defensa

La entrega final incluye el repositorio y las evidencias necesarias para revisarlo. En la defensa se realizará un recorrido vivo del servidor: dominio, flujo relevante, datos, reglas, pruebas y decisiones. También podrás recibir un cambio pequeño dirigido o una petición de razonarlo y localizar sus capas afectadas. El objetivo es demostrar comprensión y autoría, no memorizar una presentación.

Consulta la [guía de seguimiento](02-seguimiento-hitos.md), la [matriz de evidencias](../04-proyectos/proyecto-final-dwes/ra-ce-evidencias.md) y la [rúbrica/checklist](../04-proyectos/proyecto-final-dwes/rubrica-y-checklist-dwes.md) antes de cada revisión.
