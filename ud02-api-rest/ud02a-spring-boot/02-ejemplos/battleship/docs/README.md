# Battleship API — ampliaciones por sesión

## Recorrido de aula 2026/2027: construir desde cero

El code-along se construye en **`02-ejemplos/bshipAlong/`**, con **Spring Boot
4.1.1 y Java 25**. El proyecto terminado `battleship/` permanece como referencia
funcional (Boot 4.0.5); no se abre como punto de partida del code-along.

- Sesión 2: realizada en clase; entidad mínima `Game`, test `PENDING` y tabla `games`.
- [Sesión 3](03-controladores-rest.md): adaptada al proyecto de clase y reproducida
  paso a paso en una copia, incluidos los cinco ciclos rojo/verde y la integración.
- [Sesión 4](04-capa-servicios-dtos.md): servicio y DTOs desde el checkpoint 3,
  realizada en clase y comprobada con doce pruebas finales.
- [Tramo 4b](04b-reglas-de-negocio.md): construcción completa del núcleo de reglas,
  modelo, migraciones y endpoints; cierre de 29 pruebas. Se reparte en los bloques
  que requiera el ritmo de clase, no se presupone terminado en la sesión 4.
- [Sesión 5](05-manejo-errores.md): advice y payload de errores desde 04b,
  reproducida con los rojos previos y 37 pruebas finales. Empezar cuando se cierre
  04b, no por haber llegado a una hora determinada.
- Sesiones 6 y siguientes: pendientes de adaptación progresiva desde ese checkpoint.

**Tareas:** Mini Tasks es **individual**; después Book Catalog y Gestión de
eventos son **en grupo**. Se trabaja una tarea cada vez y su avance se indica al
final de cada sesión. Battleship es el code-along, no otra entrega.

## Material de referencia anterior

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

### Caso de estudio opcional: negociación con MessagePack

JSON sigue siendo el contrato canónico y la representación por defecto. Como
ampliación, `GET /api/games/{id}` también responde `application/msgpack` si el
cliente lo pide mediante `Accept`; la estructura semántica es la misma, pero
el cuerpo es binario:

```bash
curl -H 'Accept: application/msgpack' http://localhost:8080/api/games/1 \
  --output game.msgpack
```

El ejemplo solo cubre respuestas de lectura: los cuerpos de petición siguen
siendo JSON, por lo que un `Content-Type` no soportado devuelve `415`; un
`Accept` sin representación disponible devuelve `406`.

| Formato | Ventaja | Coste principal |
|---|---|---|
| JSON | Legible y universal | Más bytes |
| MessagePack | Compacto sin esquema | Conversor MVC y cliente compatible |
| Protobuf | Esquema y contratos fuertes | `.proto` y código generado |
| CBOR | Binario y cercano al ecosistema Jackson | Menor soporte de clientes que JSON |

No es contenido obligatorio de UD2: sirve para estudiar `Accept`,
`Content-Type` y `HttpMessageConverter` sin sustituir el contrato JSON.

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
- **Trabajo de la tarea activa**: últimos 30 min (sesiones de 2h) o 60 min
  (sesiones de 3h). Mini Tasks se realiza individualmente; Book Catalog y
  Gestión de eventos, en grupo.

El alumnado aplica los conceptos de Battleship en la tarea activa: primero Mini
Tasks, luego Book Catalog y finalmente Gestión de eventos. La tabla siguiente
es una previsión temática; no certifica que las sesiones posteriores a la 5 estén
adaptadas ni fija que se abra una tarea antes de cerrar la anterior.

## Sesiones

| # | Code-along (Battleship) | Tarea (Mini Tasks individual; restantes en grupo) | Tiempo estimado |
|---|------------------------|------------------|-----------------|
| 01 | Introducción, proyecto SB4, H2, primera ejecución (`01-introduccion-y-setup.md`) | Mini Tasks: preparar el repositorio propio desde cero | 2h |
| 02 | TDD, primer test, entidad Game (`02-tdd-primer-test.md`) | Mini-tasks v1 | 2h |
| 03 | Controladores REST, endpoints básicos (`03-controladores-rest.md`) | Mini-tasks v2 | 3h |
| 04 | Cerrar integración de 03; servicio y DTOs (`04-capa-servicios-dtos.md`) | Mini Tasks individual: normalizar título con TDD (50 min) | 3h |
| 04b | Modelo, migraciones y reglas completas (`04b-reglas-de-negocio.md`) | Mini Tasks individual: completar tarea de forma idempotente | Varios bloques según ritmo |
| 05 | Manejo de errores, @ControllerAdvice (`05-manejo-errores.md`) | Mini Tasks: respuesta del caso de error | 2h |
| 06 | Test slicing, @WebMvcTest, @DataJpaTest (`06-test-slicing.md`) | Mini-tasks tests | 2h |
| 07 | Seguridad JWT, RBAC, CORS, rate limiting (`07-seguridad-jwt.md`) | Book Catalog: entregas 1–3 | 3h |
| 08 | Perfiles, paginación, OpenAPI, actuator, logging (`08-produccion-perfiles.md`) | Book Catalog: entregas 4–6 | 3h |
| 09 | Docker, Tomcat tuning, alternativas (`09-docker-tomcat.md`) | Gestión de eventos: iniciar su tramo evaluable | 2h |
| 10 | [Trazabilidad SDD del contrato](10-sdd-openapi.md) | Gestión de eventos: contrato y conformidad | Lectura |

La duración global se ajusta a los checkpoints realmente alcanzados. La tabla
orienta la secuencia; no obliga a comprimir 4b ni a quitar tiempo de la tarea activa.

> **Nota**: Cada sesión asume que leíste el documento de teoría correspondiente de `01-documentacion/`.

## Material complementario

- [Persistencia avanzada](persistencia-avanzada/README.md) — MongoDB, JSON Postgres, Spring Data variantes (si el tiempo lo permite; si no, como material de estudio autónomo)
