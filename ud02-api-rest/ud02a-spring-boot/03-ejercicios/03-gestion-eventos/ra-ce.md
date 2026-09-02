# RA/CE — Seguridad JWT en API de Gestión de Eventos

## Resultados de Aprendizaje y Criterios de Evaluación

| RA | CE | Descripción | ¿Cómo se evidencia en esta tarea? |
|----|----|-------------|----------------------------------|
| **RA5** | **g** | Se han identificado y aplicado mecanismos de autenticación y autorización en el desarrollo de aplicaciones web en entorno servidor. | Implementar autenticación JWT (login, register, logout) y autorización por roles (PARTICIPANTE, ORGANIZADOR) usando Spring Security + jjwt. |
| **RA5** | **h** | Se ha probado y documentado el código. | Los tests existentes de la API continúan funcionando tras añadir la capa de seguridad; se añaden tests para los nuevos endpoints de autenticación. |
| **RA6** | **e-g** | Se ha garantizado la integridad y reproducibilidad del acceso a datos. | Versionar el esquema y el cambio propio con Flyway, impedir `ddl-auto` en producción y demostrar la persistencia con pruebas. |
| **RA7** | **g-h** | Se ha documentado y comprobado el servicio web. | Versionar el contrato OpenAPI, declarar JWT y respuestas de error, y contrastar las operaciones del slice con MockMvc. |

## Contribución a la nota

- Peso sobre la evaluación de la unidad 2026/2027: **25%**
- Tipo: Cooperativa por slices, con defensa individual
- Entrega: Repositorio del grupo con API segura, migraciones, contrato y pruebas.
