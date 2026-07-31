# Catálogo Cultural Híbrido — P1B

Ejemplo ejecutable de aplicación híbrida para UD6 del módulo DWES.
Integra dos fuentes distintas en un repositorio derivado normalizado e idempotente:

- **Remote**: Open Library Search API (`/search.json`) vía `WebClient`.
- **Local**: fixture JSON versionado derivado de Wikidata (CC0).

El modelo, el repositorio y la ingesta idempotente provienen de P1A y se mantienen
sin reescrituras: P1B añade el cliente HTTP, el mapeo y la orquestación encima.

## Propósito

Demostrar la construcción de un repositorio derivado normalizado a partir de dos
fuentes heterogéneas (API remota + dataset local), respetando contrato, licencia,
atribución y límites de uso del proveedor, con pruebas 100% offline.

## Comandos

| Acción | Comando |
|--------|---------|
| Compilar | `mvn compile` |
| Ejecutar pruebas | `mvn test` |
| Empaquetar | `mvn package` |

Requiere Java 25+ y Maven 3.6+.

## Arquitectura (P1B)

```
Application
  ├── CatalogSearchService     ← orquesta búsqueda remota + ingesta (sin tx)
  │     ├── OpenLibraryClient  ← adapter WebClient → /search.json (boundary bloqueante)
  │     │     ├── OpenLibraryProperties   ← @ConfigurationProperties catalogo.open-library.*
  │     │     ├── OpenLibrarySearchResponse + OpenLibraryClientException
  │     └── IngestionService   ← upsert idempotente en transacción propia
  │           ├── CulturalItemRepository  ← Spring Data JPA
  │           └── CulturalRecord          ← DTO normalizado (ambas fuentes)
```

- **Modelo**: `CulturalItem` (JPA entity) con identidad compuesta `(source, externalId)`.
- **Source**: enumerado con `WIKIDATA` y `OPEN_LIBRARY`.
- **DTO normalizado**: `CulturalRecord` (P1A: `WikidataCulturalRecord`) sirve para el
  fixture de Wikidata y para el mapeo de resultados remotos.
- **Cliente**: `OpenLibraryClient` (WebClient + Reactor Netty) consulta `/search.json`
  con `q`, `limit` y `fields` mínimos (`key,title,author_name,first_publish_year,subject`).
- **Orquestación**: `CatalogSearchService` llama al cliente **antes** de abrir la
  transacción de persistencia; no mantiene transacciones abiertas a través de I/O de red.
- **Colecciones**: `creators` y `subjects` se serializan con `||` vía `StringListConverter`.

## Configuración Open Library (P1B)

Propiedades `catalogo.open-library.*`, con validación fail-fast en el constructor compacto:

| Propiedad | Defecto | Notas |
|-----------|---------|-------|
| `base-url` | `https://openlibrary.org` | Debe ser URL absoluta http(s) |
| `user-agent` | `DWES-UD6/1.0 (docencia; contacto: <email-del-docente>)` | Obligatorio por contrato; sustituir el email del docente |
| `request-timeout` | `5s` | Tiempo máximo de conexión y de respuesta |
| `max-results` | `10` | Límite acotado y classroom-safe (1..10); el API admite hasta 100 pero aquí se restringe a 10 |

Sin API key (Open Library no requiere ninguna para consultas humanas ocasionales) y sin
secretos: nada sensible se versiona en el repositorio.

### Límites de uso del proveedor

- **1 req/s** por defecto para clientes no identificados.
- **3 req/s** cuando la aplicación se identifica con `User-Agent`.
- **Cachear** respuestas es obligatorio; este ejemplo no implementa caché aún (P1C).
- **Atribución y licencia**: cada registro persiste `sourceUrl` canónica
  (`https://openlibrary.org/works/<work-key>`) y la nota de licencia de Open Library /
  Internet Archive.
- **Prohibido el rastreo masivo** (bulk harvesting) y la redistribución de contenido
  de libros: solo se almacenan metadatos y procedencia.

### Semántica de fallos

| Condición | Comportamiento |
|-----------|----------------|
| Consulta en blanco o `limit` fuera de `1..max-results` | `IllegalArgumentException` antes de la llamada |
| HTTP `429` | `OpenLibraryRateLimitException` (sin reintento automático) |
| HTTP `5xx` | `OpenLibraryServerException` (sin reintento automático) |
| Timeout de conexión/lectura | `OpenLibraryTimeoutException` |
| JSON malformado o cuerpo vacío | `OpenLibraryMalformedResponseException` |
| Proveedor inalcanzable | `OpenLibraryClientException` genérica |

No hay catch-all y **no se reintenta automáticamente**: reintentar a ciegas 429/5xx
amplifica la carga del proveedor (thundering herd). La resiliencia (backoff, jitter,
presupuestos) y la caché con TTL quedan para P1C.

### Boundary bloqueante

`OpenLibraryClient` hace `block()` sobre el pipeline WebClient en su límite, de forma
deliberada y documentada: los servicios superiores y la transacción JPA nunca ven tipos
reactivos. Coste asumido: un hilo bloqueado por petición, aceptable para el volumen
bajo y humano que Open Library permite.

## Dependencias (P1B)

Se usa `spring-boot-starter-webclient` (Boot 4.0.5) en lugar de
`spring-boot-starter-webflux`: aporta WebClient y Jackson 3 (`tools.jackson.*`) sin
arrancar un servidor reactivo. Para pruebas, WireMock 3 (`wiremock-standalone`,
Jetty 11 embebido) simula el proveedor en un puerto dinámico.

Jackson 3 conserva intencionadamente las anotaciones en el paquete
`com.fasterxml.jackson.annotation`; el resto de sus APIs se importa desde `tools.jackson.*`.

## Fixture local

| Fichero | Contenido |
|---------|-----------|
| `src/main/resources/dataset/wikidata-cultural-fixture.json` | 5 registros culturales CC0 |
| `src/test/resources/dataset/malformed-fixture.json` | JSON inválido para pruebas de error |

Ver `src/main/resources/dataset/README.md` para procedencia y licencia.

## Pruebas

| Prueba | Tipo | Verifica |
|--------|------|----------|
| `CulturalItemTest` | Unitario | Creación, setters, campos opcionales |
| `CulturalItemRepositoryTest` | Integración JPA | Lookup, unicidad, fuentes distintas |
| `IngestionServiceTest` | Integración Spring | Parseo, idempotencia, actualización, error controlado |
| `OpenLibraryClientTest` | Unitario (WireMock) | Ruta/query/fields/limit, User-Agent, mapeo, 429, 5xx, JSON malformado, timeout, resultado vacío |
| `OpenLibraryPropertiesTest` | Unitario | Validación fail-fast: base URL scheme-relative rechazada, límite 10 aceptado, 11/0/negativo rechazados, defecto |
| `CatalogSearchServiceTest` | Integración Spring (WireMock) | Orquestación remota, re-importación idempotente, sin estado parcial en fallo |

Total: 39 tests (13 WireMock cliente, 5 propiedades, 3 orquestación, 10 ingesta, 5 repositorio, 3
modelo). **Todas offline**: H2 embebida, fixture en classpath y WireMock en puerto
dinámico; ninguna prueba contacta con Open Library real.

## Límites P1B / frontera P1C

- **Sin caché, sin control de tasa ni observabilidad**: se implementan en P1C.
- **Sin reintentos**: diferidos a la capa de resiliencia de P1C.
- **Sin controlador web**: no hay endpoints REST.
- **Sin JWT, OpenAPI, frontend**: fuera del alcance de UD6.
- **Sin Spring AI, RAG, vectores, MCP, agentes**: excluidos explícitamente.

## Siguiente paso: P1C

- Caché de respuestas con TTL (contrato: 24 h por defecto) respetando la política de
  cacheo de Open Library.
- Reintentos seguros y limitados (backoff + jitter) solo para 429/5xx.
- Control de tasa acorde a 1 req/s (3 req/s identificado).
- Observabilidad de fallos y métricas de proveedor.
