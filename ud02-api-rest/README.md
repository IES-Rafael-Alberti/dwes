# UD2 - APIs REST

UD2 enseña a construir APIs REST con Spring Boot 4 y Java 25. La ruta
evaluable es `ud02a-spring-boot`; .NET y GraphQL son referencias opcionales y
no sustituyen ese recorrido.

## Ruta rápida

1. Estudia la [teoría de Spring Boot](ud02a-spring-boot/01-documentacion/README.md).
2. Sigue el [mapa incremental de Battleship](ud02a-spring-boot/01-documentacion/08-battleship-caso-practico.md).
3. Desarrolla cada sesión en el [code-along de Battleship](ud02a-spring-boot/02-ejemplos/battleship/docs/README.md).
4. Aplica los conceptos en [mini-tasks](ud02a-spring-boot/03-ejercicios/01-mini-tasks/), [book-catalog](ud02a-spring-boot/03-ejercicios/02-book-catalog/) y [gestión de eventos](ud02a-spring-boot/03-ejercicios/03-gestion-eventos/).

## Qué se aprende

| Bloque | Evidencia principal |
| --- | --- |
| Diseño REST y capas | Controladores, DTOs, servicios y repositorios en Battleship y ejercicios |
| Persistencia | JPA, H2/PostgreSQL y migraciones Flyway |
| Calidad | TDD, test slicing e integración HTTP |
| Seguridad | JWT, roles, CORS y respuestas de error previsibles |
| Contrato | OpenAPI 3.1 canónico y pruebas de conformidad |

## Evaluación

Las evidencias y criterios de la unidad están reunidos en la
[matriz RA/CE de UD2a](ud02a-spring-boot/ra-ce.md). Cada ejercicio conserva su
propia contribución publicada; Battleship es el proyecto conductor de aula y
la evidencia técnica compartida.

La rúbrica común del módulo fija los niveles de logro. No hay que memorizarla:
para una entrega, comprueba siempre su archivo `ra-ce.md`, sus tests y el
enunciado correspondiente.

## Límites

- [UD2b .NET](ud02b-dotnet/) y `ud02c-graphql/` se conservan como material de consulta opcional.
- La implementación de referencia es Battleship; no hay una segunda ruta obligatoria.
- Las decisiones internas de reforma viven en [INVENTARIO_REFORMA.md](INVENTARIO_REFORMA.md), no forman parte del itinerario del alumnado.
