# Battleship API — ampliaciones por sesión

Estas sesiones amplían el [recorrido incremental canónico](../../../01-documentacion/08-battleship-caso-practico.md). Empieza allí: fija los checkpoints y separa el núcleo comprobado de las extensiones de seguridad y producción.

El proyecto de referencia actual usa Spring Boot 4.0.5 y Java 25. Algunas sesiones muestran el camino hacia el resultado final y no describen necesariamente un commit intermedio conservado en el repositorio.

## Contrato HTTP canónico y flujo SDD

La fuente de verdad de la API final es
`src/main/resources/static/api-docs/battleship-v1.yaml`. Al ejecutar la
aplicación se publica en `/api-docs/battleship-v1.yaml`, y Swagger UI
(`/swagger-ui.html`) carga **solo** esa URL. La generación de `/api-docs` de
Springdoc está deshabilitada: Springdoc aporta el visor, pero no genera el
contrato ni es una segunda fuente de documentación.

El contrato OpenAPI 3.1 conserva las rutas sin prefijo de versión y fija estas
nueve operaciones:

| Operación | `operationId` | Acceso |
|---|---|---|
| `POST /auth/register` | `registerUser` | público |
| `POST /auth/login` | `loginUser` | público |
| `POST /auth/refresh` | `refreshToken` | público |
| `POST /api/games` | `createGame` | bearer `PLAYER` |
| `GET /api/games` | `listGames` | público |
| `GET /api/games/{id}` | `getGame` | público |
| `POST /api/games/{id}/ships` | `placeShip` | bearer `PLAYER` |
| `POST /api/games/{id}/attacks` | `attackGame` | bearer `PLAYER` |
| `DELETE /api/games/{id}` | `cancelGame` | bearer `ADMIN` |

Los cinco accesos públicos se expresan como overrides `security: []`. Las
otras cuatro operaciones declaran `bearerAuth`; la descripción fija el rol
esperado porque OpenAPI no puede validar claims JWT.

### De requisitos a evidencia

El cambio sigue una estrategia contract-first con SDD y TDD estricto. La
evidencia reproducible actual demuestra que el token de login autentica una
operación `PLAYER`, el parser valida OpenAPI 3.1/v1 y las nueve interacciones
con errores representativos `400`, `401`, `403`, `404` y `409` conforman con
el mismo YAML.

`OpenApiContractTest` comprueba la estructura,
`OpenApiPublicationIntegrationTest` la publicación y
`OpenApiConformanceIntegrationTest` usa el validador Atlassian con MockMvc.
La respuesta `429` está declarada estructuralmente en las nueve operaciones
   y `RateLimitFilterTest` ejecuta el rechazo real del filtro; no se afirma que
   el validador Atlassian fuerce el rate limit durante la matriz de conformidad.
   Esto complementa, no sustituye,
   `SecurityAuthorizationIntegrationTest`: sus casos de roles siguen siendo
   la evidencia autoritativa de autorización.

### Verificación reproducible

Desde la raíz de Battleship:

```bash
./mvnw clean verify
jar tf target/battleship-0.0.1-SNAPSHOT.jar \
  | grep 'BOOT-INF/classes/static/api-docs/battleship-v1.yaml'
jar tf target/battleship-0.0.1-SNAPSHOT.jar \
  | grep -Ei '(^|/)(keys?|secrets?)/|\.(pem|p12|jks|key)$' && exit 1 || true
```

Desde la raíz del repositorio:

```bash
mkdocs build --strict
```

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
| 10 | [Trazabilidad SDD del contrato](10-sdd-openapi.md) | Auditoría y recorrido docente | Lectura |

Total: ~24h de clase distribuidas en 3-4 semanas.

> **Nota**: Cada sesión asume que leíste el documento de teoría correspondiente de `01-documentacion/`.

## Material complementario

- [Persistencia avanzada](persistencia-avanzada/README.md) — MongoDB, JSON Postgres, Spring Data variantes (si el tiempo lo permite; si no, como material de estudio autónomo)
