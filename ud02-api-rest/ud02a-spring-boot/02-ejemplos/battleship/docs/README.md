# Battleship API — ampliaciones por sesión

Estas sesiones amplían el [recorrido incremental canónico](../../../01-documentacion/08-battleship-caso-practico.md). Empieza allí: fija los checkpoints y separa el núcleo comprobado de las extensiones de seguridad y producción.

El proyecto de referencia actual usa Spring Boot 4.0.5 y Java 25. Algunas sesiones muestran el camino hacia el resultado final y no describen necesariamente un commit intermedio conservado en el repositorio.

## Organización de las sesiones

Horario semanal: **2h + 2h + 3h** (tres días a la semana).

Dentro de cada sesión:
- **Code-along**: el profe explica teoría y escribe código en vivo con los alumnos (Battleship)
- **Tarea individual**: los últimos 30 min (sesiones de 2h) o 60 min (sesión de 3h) trabajan en el ejercicio correspondiente

Los alumnos avanzan en paralelo con su proyecto (book-catalog, mini-tasks, gestion-eventos) aplicando los mismos conceptos que ven en Battleship.

## Sesiones

| # | Code-along (Battleship) | Tarea individual | Tiempo estimado |
|---|------------------------|------------------|-----------------|
| 01 | Introducción, proyecto SB4, H2, primera ejecución (`01-introduccion-y-setup.md`) | Book-catalog entrega 1 | 2h |
| 02 | TDD, primer test, entidad Game (`02-tdd-primer-test.md`) | Mini-tasks v1 | 2h |
| 03 | Controladores REST, endpoints básicos (`03-controladores-rest.md`) | Mini-tasks v2 | 3h |
| 04 | Capa de servicios, DTOs, lógica de negocio (`04-capa-servicios-dtos.md`) | Book-catalog entrega 4 | 2h |
| 04b | Reglas de negocio: soft delete, estados, máquina de estados (`04b-reglas-de-negocio.md`) | Book-catalog reglas de negocio | 3h |
| 05 | Manejo de errores, @ControllerAdvice (`05-manejo-errores.md`) | Book-catalog entregas 5 y 6 | 2h |
| 06 | Test slicing, @WebMvcTest, @DataJpaTest (`06-test-slicing.md`) | Mini-tasks tests | 2h |
| 07 | Seguridad JWT, RBAC, CORS, rate limiting (`07-seguridad-jwt.md`) | Gestion-eventos seguridad | 3h |
| 08 | Perfiles, paginación, OpenAPI, actuator, logging (`08-produccion-perfiles.md`) | Despliegue completo | 3h |
| 09 | Docker, Tomcat tuning, alternativas (`09-docker-tomcat.md`) | Dockerizar proyecto | 2h |

Total: ~24h de clase distribuidas en 3-4 semanas.

> **Nota**: Cada sesión asume que leíste el documento de teoría correspondiente de `01-documentacion/`.

## Material complementario

- [Persistencia avanzada](persistencia-avanzada/README.md) — MongoDB, JSON Postgres, Spring Data variantes (si el tiempo lo permite; si no, como material de estudio autónomo)
