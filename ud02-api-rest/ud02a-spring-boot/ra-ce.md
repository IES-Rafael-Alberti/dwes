# RA/CE - UD2a API REST con Spring Boot

Esta matriz reúne las evidencias de la ruta canónica de UD2a. No sustituye los
`ra-ce.md` de cada ejercicio ni asigna ponderaciones nuevas: permite reconocer
qué actividad demuestra cada criterio.

## Evidencias

| RA/CE | Evidencia en UD2a | Actividad o recurso |
| --- | --- | --- |
| RA1.g | Selección y uso razonado de Spring Boot, Maven y perfiles | Teoría, setup de Battleship y mini-tasks |
| RA5.b | Separación Controller -> Service -> Repository y DTOs | Battleship, mini-tasks y book-catalog |
| RA5.g | Patrones de diseño, validación y reglas de negocio | Battleship y gestión de eventos |
| RA5.h | TDD, pruebas MVC/JPA/integración y documentación | Battleship y los tres ejercicios |
| RA6.a-g | Acceso JPA, migraciones Flyway, integridad y pruebas de persistencia | Battleship |
| RA7.a-f | Diseño, implementación y verificación de una API REST | Battleship, book-catalog y gestión de eventos |
| RA7.h | Contrato OpenAPI y documentación de operaciones | Battleship |

## Actividades evaluables

| Actividad | Evidencia | RA/CE específico |
| --- | --- | --- |
| [Mini Spring Boot Tasks](03-ejercicios/01-mini-tasks/ra-ce.md) | API guiada y tests | RA1.g, RA5.b, RA5.h |
| [Book Catalog](03-ejercicios/02-book-catalog/ra-ce.md) | Entregas incrementales por capas | RA5.b, RA5.h |
| [Gestión de eventos](03-ejercicios/03-gestion-eventos/ra-ce.md) | JWT, roles y tests de seguridad | RA5.g, RA5.h |

## Criterio de calidad

La rúbrica común por RA/CE del módulo define los niveles de logro. Para UD2a,
una evidencia completa debe mostrar:

- contrato HTTP coherente y respuestas de error verificables;
- separación de responsabilidades y DTOs que no expongan el dominio;
- pruebas automatizadas que cubran comportamiento y casos de error;
- persistencia versionada y sin credenciales integradas;
- autenticación y autorización cuando la operación lo requiera.
