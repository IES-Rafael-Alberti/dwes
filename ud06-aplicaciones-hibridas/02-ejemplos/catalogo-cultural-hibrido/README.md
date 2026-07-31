# Catálogo Cultural Híbrido — P1C

Ejemplo ejecutable de aplicación híbrida para UD6 del módulo DWES.
Integra dos fuentes distintas en un repositorio derivado normalizado e idempotente:

- **Remote**: Open Library Search API (`/search.json`) vía `WebClient`.
- **Local**: fixture JSON versionado derivado de Wikidata (CC0).

El modelo, el repositorio y la ingesta idempotente provienen de P1A y se mantienen
sin reescrituras; P1B añadió el cliente HTTP, el mapeo y la orquestación; P1C añade
por encima caché Caffeine con Spring Cache, control de tasa conservador (1 req/s) y
observabilidad SLF4J, sin reintentos automáticos.

## Propósito

Demostrar la construcción de un repositorio derivado normalizado a partir de dos
fuentes heterogéneas (API remota + dataset local), respetando contrato, licencia,
atribución y límites de uso del proveedor (cacheo obligatorio, ~1 req/s), con pruebas
100% offline y deterministas.

## Comandos

| Acción | Comando |
|--------|---------|
| Compilar | `mvn compile` |
| Ejecutar pruebas | `mvn test` |
| Empaquetar | `mvn package` |

Requiere Java 25+ y Maven 3.6+.

Para trabajar sin red no basta con descargar las dependencias directas: Maven
necesita también plugins y todos los artefactos transitivos. Docente y estudiante
deben ejecutar una vez con red estos comandos exactos —o ejecutar primero
`mvn dependency:go-offline` y después los mismos comandos—. Tras desconectar, se
repiten como `mvn -o compile`, `mvn -o test` y `mvn -o package`.

## Arquitectura (P1C)

```
Application
  ├── CatalogSearchService     ← orquesta búsqueda remota + ingesta (sin tx)
  │     └── CachedOpenLibraryClient  ← @Cacheable (Spring Cache sobre Caffeine)
  │           ├── OpenLibrarySearchKeyGenerator  ← clave = consulta normalizada + límite
  │           ├── RequestThrottle (OpenLibraryRequestThrottle)  ← 1 req/s conservador
  │           ├── OpenLibraryClient  ← adapter WebClient → /search.json (boundary bloqueante)
  │           │     ├── OpenLibraryProperties   ← @ConfigurationProperties catalogo.open-library.*
  │           │     └── OpenLibrarySearchResponse + OpenLibraryClientException
  │           └── SLF4J: arranque, éxito (duración/resultados), categoría de fallo
  │                 └── SearchCachingConfig   ← @Configuration @EnableCaching
  │                       └── Boot CaffeineCacheManager (máx. 100, TTL 24 h)
  └── IngestionService   ← upsert idempotente en transacción propia
        ├── CulturalItemRepository  ← Spring Data JPA
        └── CulturalRecord          ← DTO normalizado (ambas fuentes)
```

- **Modelo**: `CulturalItem` (JPA entity) con identidad compuesta `(source, externalId)`.
- **Source**: enumerado con `WIKIDATA` y `OPEN_LIBRARY`.
- **DTO normalizado**: `CulturalRecord` sirve para el fixture de Wikidata y para el
  mapeo de resultados remotos.
- **Cliente**: `OpenLibraryClient` (WebClient + Reactor Netty) consulta `/search.json`
  con `q`, `limit` y `fields` mínimos. No se toca en P1C.
- **Caché**: `CachedOpenLibraryClient` cachea las respuestas mapeadas vía
  `@Cacheable` (Spring Cache sobre Caffeine); los aciertos saltan proveedor **y**
  throttle.
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

- **1 req/s** por defecto para clientes no identificados (P1C aplica este límite).
- **3 req/s** cuando la aplicación se identifica con `User-Agent`.
- **Cachear** respuestas es obligatorio; P1C lo implementa con TTL 24 h.
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
amplifica la carga del proveedor (thundering herd). Los reintentos seguros (backoff +
jitter) quedan diferidos como frontera P2.

### Boundary bloqueante

`OpenLibraryClient` hace `block()` sobre el pipeline WebClient en su límite, de forma
deliberada y documentada: los servicios superiores y la transacción JPA nunca ven tipos
reactivos. Coste asumido: un hilo bloqueado por petición, aceptable para el volumen
bajo y humano que Open Library permite.

## Caché Caffeine (P1C)

- **Configuración dedicada**: `SearchCachingConfig` (`@Configuration @EnableCaching`)
  es el único lugar donde se habilita el cacheo. Spring Boot configura el gestor.
- **Nombre predeclarado**: `openLibrarySearchResults` como constante; nunca se crean
  cachés bajo demanda. `spring.cache.cache-names` lo declara al arrancar.
- **Acotada**: máx. `100` entradas y `expire-after-write 24 h` (contrato de cacheo de
  Open Library), configurados mediante `spring.cache.caffeine.spec`.
- **Clave normalizada**: `OpenLibrarySearchKeyGenerator` genera la clave a partir de la
  consulta **recortada y en minúsculas** más el **límite** acotado; así
  `"  DON QUIXOTE "` y `"don quixote"` comparten entrada y no duplican llamadas.
- **Nunca se cachean fallos ni valores nulos o inválidos**: una excepción del proveedor
  se propaga sin escribir en caché; `@Cacheable(unless = "#result == null")` evita
  valores nulos, y Caffeine no puede almacenarlos.

**Integración oficial**: `spring-boot-starter-cache` aporta la integración de Spring
Cache y, al detectar `caffeine`, Spring Boot 4.0.5 autoconfigura el
`CaffeineCacheManager` oficial. Ambas dependencias usan las versiones gestionadas por
Boot; el proyecto no mantiene adaptadores propios ni declara un `CacheManager` manual.

## Control de tasa (P1C)

- `RequestThrottle` es la interfaz; `OpenLibraryRequestThrottle` (@Component) aplica un
  **mínimo conservador de 1 segundo** entre peticiones al proveedor.
- Se adquiere **antes** de cada llamada no cacheada; un **acierto de caché no consume**
  ranura del throttle.
- **Seams de prueba**: el `Clock` y la espera (`ThrottleWait`) se inyectan. En
  producción se usa el reloj del sistema y `Thread.sleep`; en pruebas se usa un reloj
  falso mutable y una espera que registra duraciones, sin dormir nunca.

## Observabilidad (P1C)

Logging SLF4J con nivel por defecto `INFO`, privacidad por defecto:

| Evento | Nivel | Contenido |
|--------|-------|-----------|
| Inicio de búsqueda | `INFO` | Aviso sin la consulta |
| Consulta cruda | `DEBUG` | `query='{}' limit={}` — **nunca** a nivel `INFO` |
| Éxito | `INFO` | Duración en ms y número de resultados |
| Fallo controlado | `WARN` | Categoría estable (`rate_limit`, `server_error`, `timeout`, `malformed_response`, `unreachable`) y duración, sin la consulta |

La categoría se deriva de la jerarquía `OpenLibraryClientException` en
`CachedOpenLibraryClient.failureCategory`.

## Dependencias (P1C)

Se usa `spring-boot-starter-webclient` (Boot 4.0.5) en lugar de
`spring-boot-starter-webflux`: aporta WebClient y Jackson 3 (`tools.jackson.*`) sin
arrancar un servidor reactivo. Para pruebas, WireMock 3 (`wiremock-standalone`,
Jetty 11 embebido) simula el proveedor en un puerto dinámico. P1C añade
`spring-boot-starter-cache` y `caffeine`, sin versiones explícitas: Boot 4.0.5 gestiona
ambas y autoconfigura `CaffeineCacheManager`.

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
| `CatalogSearchServiceTest` | Integración Spring (WireMock) | Orquestación remota, re-importación idempotente, sin estado parcial en fallo (caché limpiada entre pruebas) |
| `CachedOpenLibraryClientTest` | Integración Spring (WireMock) | Acierto de caché, normalización de consulta, límites distintos, fallos no cacheados, el acierto salta proveedor y throttle, clave normalizada, categorías de fallo |
| `OpenLibraryRequestThrottleTest` | Unitario (reloj falso) | Espaciado 1 req/s secuencial y concurrente con tiempo falso, incluido oversleep del scheduler; interrupción rápida que conserva el flag y libera el monitor |
| `SearchCachingConfigTest` | Integración Spring | Caché predeclarada con el nombre esperado; política Caffeine con máx. 100 y expire-after-write 24 h |

Total: 55 tests (13 WireMock cliente, 5 propiedades, 3 orquestación, 10 ingesta,
5 repositorio, 3 modelo, 7 caché/throttle/observabilidad del cliente, 7 espaciado/concurrencia/oversleep/interrupción del
throttle, 2 configuración de caché). **Todas offline y deterministas**: H2 embebida,
fixture en classpath, WireMock en puerto dinámico y relojes falsos inyectados; ninguna
prueba contacta con Open Library real ni espera un segundo real por el throttle.

## Límites P1C / frontera P2

- **Sin reintentos automáticos**: son una ampliación opcional (backoff + jitter), no
  bloquean P1; la caché y el control de tasa ya protegen al proveedor.
- **Sin controlador web**: no hay endpoints REST.
- **Sin JWT, OpenAPI, frontend**: fuera del alcance de UD6.
- **Sin Spring AI, RAG, vectores, MCP, agentes**: excluidos explícitamente.
- **Sin Spring Actuator ni métricas de proveedor**: la observabilidad se limita al
  logging SLF4J de este ejemplo.

## Siguiente paso: P2B

P2A ya está completado con la
[práctica incremental](../../03-ejercicios/practica-integracion/README.md) y la
[seguridad transversal](../../06-seguridad/README.md). Solo queda pendiente P2B:
el proyecto evaluable, la rúbrica RA9.a-h y el cuestionario docente.
