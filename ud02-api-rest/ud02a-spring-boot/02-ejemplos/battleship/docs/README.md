# Battleship API — ampliaciones por sesión

## Recorrido de aula 2026/2027: construir desde cero

El code-along se construye en **`02-ejemplos/bshipAlong/`**, con **Spring Boot
4.1.1 y Java 25**. El proyecto terminado `battleship/` permanece como referencia
funcional (Boot 4.0.5); no se abre como punto de partida del code-along.

- Sesión 2: realizada en clase; entidad mínima `Game`, test `PENDING` y tabla `games`.
- [Sesión 3](01-construccion/03-controladores-rest.md): adaptada al proyecto de clase y reproducida
  paso a paso en una copia, incluidos los cinco ciclos rojo/verde y la integración.
- [Sesión 4](01-construccion/04-capa-servicios-dtos.md): servicio y DTOs desde el checkpoint 3,
  realizada en clase y comprobada con doce pruebas finales.
- [Tramo 4b](01-construccion/04b-reglas-de-negocio.md): construcción completa del núcleo de reglas,
  modelo, migraciones y endpoints; preparación verde y ciclos red/green/blue,
  cierre de 36 pruebas. Se reparte en los bloques
  que requiera el ritmo de clase, no se presupone terminado en la sesión 4.
- [Sesión 5](01-construccion/05-manejo-errores.md): advice y payload de errores desde 04b,
  reproducida con los rojos previos y 43 pruebas finales. Cada handler local se
  retira por separado para mantener verde la suite. Empezar cuando se cierre
  04b, no por haber llegado a una hora determinada.
- [Asociaciones JPA en bshipAlong](01-construccion/06-asociaciones-jpa-bshipalong.md):
  recorrido del guion 10 desde el cierre de errores, con asociaciones, servicio y
  pruebas de persistencia. Pendiente de reconstrucción desde este documento.
- [Integridad y actualización de barcos en bshipAlong](01-construccion/07-integridad-y-actualizacion-bshipalong.md):
  migración V4, restricciones y edición mediante PUT/PATCH.
- Otros tramos posteriores: adaptación progresiva según el avance real del aula.

**Tareas:** Mini Tasks es **individual**; después Book Catalog y Gestión de
eventos son **en grupo**. Se trabaja una tarea cada vez y su avance se indica al
final de cada sesión. Battleship es el code-along, no otra entrega.

## Material de referencia anterior

Estas sesiones amplían el [recorrido incremental canónico](../../../01-documentacion/02-api-rest/08-battleship-caso-practico.md). Empieza allí: fija los checkpoints y separa el núcleo comprobado de las extensiones de seguridad y producción.

Las guías están agrupadas en `01-construccion/`, `02-pruebas/`, `03-seguridad/`
y `04-operacion/`. La segunda parte del curso se estudia desde el
[bloque A de persistencia](../../../01-documentacion/04-persistencia/bloque-a/01-entidades-relaciones-y-ciclo-de-vida.md).

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
- Se alternan ciclos de **60 minutos**: **hasta 20 min de explicación y
  code-along de Battleship + 30 min de trabajo en la tarea activa + 10 min de
  control** de pruebas, avances y bloqueos.
- Las sesiones de **2 h tienen dos ciclos**; las de **3 h, tres**. El arranque y
  el cierre están incluidos. Si la explicación termina antes, se amplía el
  trabajo autónomo; no se recorta este para añadir otra demo.
- Mini Tasks se realiza individualmente; Book Catalog y Gestión de eventos,
  en grupo.

El alumnado aplica los conceptos de Battleship en la tarea activa: primero Mini
Tasks, luego Book Catalog y finalmente Gestión de eventos. La tabla siguiente
es una previsión temática; salvo donde se indique, no certifica que los tramos
posteriores a la 5 estén adaptados ni fija que se abra una tarea antes de cerrar
la anterior.

## Sesiones

| # | Code-along (Battleship) | Tarea (Mini Tasks individual; restantes en grupo) | Tiempo estimado |
|---|------------------------|------------------|-----------------|
| 01 | [Introducción y setup](01-construccion/01-introduccion-y-setup.md) | Mini Tasks: preparar el repositorio propio desde cero | 2h |
| 02 | [Primer test y entidad](01-construccion/02-tdd-primer-test.md) | Mini-tasks v1 | 2h |
| 03 | [Controladores REST](01-construccion/03-controladores-rest.md) | Mini-tasks v2 | 3h |
| 04 | [Servicio y DTOs](01-construccion/04-capa-servicios-dtos.md) | Mini Tasks individual: normalizar título con TDD | 3h |
| 04b | [Modelo y reglas](01-construccion/04b-reglas-de-negocio.md) | Mini Tasks individual: completar tarea de forma idempotente | Varios bloques |
| 05 | [Manejo de errores](01-construccion/05-manejo-errores.md) | Cierre de Mini Tasks; avance de Book Catalog según el punto del grupo | Según avance |
| 06 | [Test slicing](02-pruebas/06-test-slicing.md), referencia pendiente de adaptación | Consulta sobre los tests de la práctica activa | Consulta |
| 07 | [Seguridad JWT](03-seguridad/07-seguridad-jwt.md), referencia del recorrido anterior | Gestión de eventos según alcance publicado | Replanificado |
| 08 | [Producción y perfiles](04-operacion/08-produccion-perfiles.md), referencia anterior | Conceptos redistribuidos en los nuevos bloques | Replanificado |
| 09 | [Docker y Tomcat](04-operacion/09-docker-tomcat.md) | Consulta según necesidad | Consulta |
| 10 | [Trazabilidad SDD del contrato](04-operacion/10-sdd-openapi.md) | Gestión de eventos: contrato y conformidad | Lectura |

La duración global se ajusta a los checkpoints realmente alcanzados. La tabla
orienta la secuencia; no obliga a comprimir 4b ni a quitar tiempo de la tarea activa.

> **Nota**: Cada sesión asume que leíste el documento de teoría correspondiente de `01-documentacion/`.

## Material complementario

- [Persistencia avanzada](persistencia-avanzada/README.md) — MongoDB, JSON Postgres, Spring Data variantes (si el tiempo lo permite; si no, como material de estudio autónomo)
