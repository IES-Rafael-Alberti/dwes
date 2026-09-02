# RA/CE - UD2a API REST con Spring Boot

Esta matriz reúne la cobertura de la ruta canónica de UD2a. No sustituye los
`ra-ce.md` de cada ejercicio ni asigna ponderaciones nuevas. Battleship es el
hilo conductor de las explicaciones teórico-prácticas: no es una entrega ni
una evidencia calificable.

## Evidencias

| RA/CE | Cobertura en UD2a | Actividad o recurso |
| --- | --- | --- |
| RA1.g | Selección y uso razonado de Spring Boot, Maven y perfiles | Mini-tasks |
| RA5.b | Separación Controller -> Service -> Repository y DTOs | Mini-tasks y Book Catalog |
| RA5.g | Patrones de diseño, validación y reglas de negocio | Gestión de eventos |
| RA5.h | TDD, pruebas MVC/JPA/integración y documentación | Los tres ejercicios |
| RA6.a-d | Acceso JPA, entidad, repositorio y operación en servicio | Mini-tasks |
| RA6.e-g | Migraciones, integridad y persistencia reproducible | Gestión de eventos |
| RA7.a-d | Endpoint, DTO y respuestas HTTP básicas | Mini-tasks |
| RA7.a-f | Diseño REST, DTOs, errores y pruebas de operaciones | Book Catalog |
| RA7.g-h | Contrato OpenAPI y verificación documentada | Gestión de eventos |

## Actividades evaluables

| Actividad | Evidencia | RA/CE específico |
| --- | --- | --- |
| [Mini Spring Boot Tasks](03-ejercicios/01-mini-tasks/ra-ce.md) | API básica con JPA/H2, DTO y prueba | RA1.g, RA5.b/h, RA6.a-d, RA7.a-d |
| [Book Catalog](03-ejercicios/02-book-catalog/ra-ce.md) | API REST por capas, errores y pruebas MockMvc | RA5.b/h, RA7.a-f |
| [Gestión de eventos](03-ejercicios/03-gestion-eventos/ra-ce.md) | JWT, Flyway, OpenAPI y pruebas de conformidad | RA5.g/h, RA6.e-g, RA7.g-h |

Battleship se utiliza exclusivamente durante las explicaciones y demostraciones
de aula. No se entrega, no se corrige ni se emplea para calcular la calificación.

## Criterio de calidad

La rúbrica común por RA/CE del módulo define los niveles de logro. Para UD2a,
una evidencia completa debe mostrar:

- contrato HTTP coherente y respuestas de error verificables;
- separación de responsabilidades y DTOs que no expongan el dominio;
- pruebas automatizadas que cubran comportamiento y casos de error;
- persistencia versionada y sin credenciales integradas;
- autenticación y autorización cuando la operación lo requiera.
