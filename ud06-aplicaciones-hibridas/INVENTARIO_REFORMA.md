# Inventario de reforma de UD6

## Diagnóstico

UD6 está infradotada: solo contiene `01-documentacion/UD6-AplicacionesHibridas.md`. No hay índice, ejemplo ejecutable, práctica, proyecto evaluable, seguridad específica ni mapa de evidencias para RA9.

**Estado:** P0 completado (contrato, fuentes, planificación). Pendiente P1 (ejemplo ejecutable).

El documento existente es una conversación sin depurar. Enumera tecnologías y propone EcoViajes, pero el código es incompleto, no tiene pruebas y confunde el criterio `g` de análisis de datos con el criterio `h` de pruebas y documentación. No puede ser la fuente canónica de la unidad.

## Decisión

UD6 se mantiene y se amplía porque RA9 tiene un objeto propio: **integrar información y código de terceros, transformarlos en un repositorio propio y operar esa integración de forma segura, resiliente y verificable**.

La unidad tendrá un único proyecto conductor con Spring Boot. Debe consumir una API externa y un conjunto de datos versionado, normalizar ambas fuentes, registrar su procedencia, persistir un repositorio derivado y exponer una consulta o análisis agregado.

Spring AI (llamada a un chat model) puede aparecer como ampliación opcional para el criterio `g`. Quedan excluidos RAG, vector stores, MCP y cualquier infraestructura de agentes. La aplicación debe funcionar sin IA. FastAPI se evalúa en P3 si el equipo docente lo considera útil.

## Alineación con RA9

| CE | Evidencia mínima prevista |
|---|---|
| a | Justificación de qué código e información se reutilizan y qué coste evitan. |
| b | Comparación y selección razonada de cliente HTTP, librerías de mapeo, persistencia y resiliencia. |
| c | Recuperación y procesamiento real de una API y un repositorio de datos existente. |
| d | Construcción idempotente de un repositorio propio normalizado, con procedencia y fecha de actualización. |
| e | Uso justificado de librerías para incorporar cliente HTTP, mapeo, resiliencia o análisis. |
| f | Servicio web construido sobre información o código de terceros, respetando contrato, licencia y atribución. |
| g | Análisis reproducible de los datos integrados; IA externa solo como ampliación opcional. |
| h | Pruebas con proveedor simulado, diagnóstico de fallos y documentación reproducible. |

## Material existente y destino

| Material | Diagnóstico | Destino |
|---|---|---|
| `01-documentacion/UD6-AplicacionesHibridas.md` | Chat sin depurar, CE incompletos y código no ejecutable | Extraer únicamente ideas válidas; retirar como apunte canónico |
| EcoViajes | Propuesta demasiado amplia: repite CRUD, JWT, Swagger y frontend | No implementar tal cual; conservar solo la idea de integrar fuentes externas |
| Ejercicios MongoDB/PostgreSQL JSON de Unidad 0 | Patrones útiles de transformación, idempotencia y análisis | Referenciar como prerrequisitos, no duplicar |
| Battleship y Laravel 12 | Patrones ya cubiertos de API, seguridad, pruebas y documentación | Reutilizar convenciones; no repetir contenidos |

## Alcance

### Obligatorio

- Cliente HTTP con URI segura, timeouts y errores controlados.
- Ingesta de una API externa y un dataset versionado en formato diferente.
- Mapeo a un modelo propio y persistencia idempotente del repositorio derivado.
- Procedencia, licencia, instante de actualización y política de refresco.
- Caché o degradación controlada; reintentos solo cuando sean seguros y limitados.
- Protección de API keys y validación de datos externos no confiables.
- Pruebas sin depender de Internet mediante dobles del proveedor.
- Casos de timeout, respuesta inválida, límite de cuota y proveedor no disponible.
- Documentación de arranque, arquitectura, fuentes, decisiones y limitaciones.

### Fuera de alcance

- Repetir CRUD, autenticación JWT, OpenAPI o arquitectura por capas como contenido nuevo.
- Implementar el mismo proyecto en Spring Boot y Laravel.
- Node/Express, ya cubierto por otro módulo.
- Frontend completo o aplicación móvil híbrida.
- Entrenamiento de modelos o una unidad completa de Spring AI/FastAPI.

## Plan por prioridad

### P0 - recorrido y contrato — completado

- [x] Crear el README de unidad con propósito, prerrequisitos, RA/CE, secuencia y fuera de alcance.
- [x] Archivar el chat heredado como fuente histórica rastreable y crear documento canónico introductorio.
- [x] Actualizar planificación, rúbrica RA9 y contrato de unidad.
- [x] Elegir fuentes externas estables, con licencia y condiciones de uso compatibles con el aula.
- [x] Definir el proyecto conductor y sus contratos antes de implementar.

### P1 - ejemplo ejecutable

**P1A — Fundación local** (completado)

- [x] Crear proyecto Maven con Spring Boot 4.0.5 y Java 25.
- [x] Modelo `CulturalItem` con identidad `(source, externalId)` y `@UniqueConstraint`.
- [x] Spring Data JPA repository con `findBySourceAndExternalId`.
- [x] IngestionService con upsert idempotente y control de errores.
- [x] Fixture Wikidata CC0 con 5 registros verificados vía API (QIDs canónicos: incluye Las Meninas Q208758 y Hamlet Q41567, sin QIDs sintéticos).
- [x] H2 para ejecución local y pruebas.
- [x] 18 tests: 3 unitarios (CulturalItem), 5 repositorio, 10 integración (parseo, idempotencia, actualización, upsert mixto, batch multi-fuente, lista vacía, error, rollback real).
- [x] Dependencia `spring-boot-starter-webclient` en lugar de `spring-boot-starter-webflux`: starter focalizado de Boot 4 que provee Jackson 3 (paquete `tools.jackson.*`) y WebClient para P1B sin arrancar un servidor reactivo.
- [x] Documentación del proyecto, dataset y arquitectura.

**P1B — Integración remota** (completado)

- [x] DTO normalizado: `WikidataCulturalRecord` renombrado a `CulturalRecord` (source-neutral) sin referencias obsoletas.
- [x] Cliente mínimo con `WebClient` sobre `spring-boot-starter-webclient`: `/search.json`, `q` validada no vacía, `limit` acotado y `fields` mínimos.
- [x] Propiedades tipadas `catalogo.open-library.*` (Boot 4 `@ConfigurationProperties`): base URL, User-Agent identificado, timeout y `max-results` (defecto 10, máx. 10 classroom-safe), validación fail-fast y sin secretos.
- [x] Mapeo a `CulturalRecord`: clave de obra normalizada, URL canónica de Open Library, `OPEN_LIBRARY`, instante de recuperación y nota de licencia/atribución.
- [x] Fallos explícitos sin catch-all y sin reintento automático: timeout, 429, 5xx y JSON malformado (jerarquía `OpenLibraryClientException`).
- [x] Orquestación `CatalogSearchService` sobre la ingesta idempotente existente (llamada remota antes de abrir transacción).
- [x] 39 pruebas offline con WireMock 3 (`wiremock-standalone`, JUnit 5, puerto dinámico): ruta/query/fields/limit, User-Agent, mapeo, resultado vacío, 429, 5xx, JSON malformado, timeout con delay fijo y re-importación idempotente.
- [x] Documentación actualizada del proyecto, la unidad y el inventario, con política de uso de Open Library (1 req/s, 3 req/s identificado, cacheo/atribución, sin rastreo masivo).

**P1C — Resiliencia, caché y observabilidad** (pendiente)

- [ ] Caché de respuestas con TTL (contrato: 24 h por defecto) respetando la política de cacheo de Open Library.
- [ ] Control de tasa acorde a 1 req/s (3 req/s identificado).
- [ ] Reintentos seguros y limitados (backoff + jitter) solo para 429/5xx, sin thundering herd.
- [ ] Observabilidad de fallos y métricas del proveedor.

### P2 - práctica y evaluación

- [ ] Crear una práctica incremental y un proyecto evaluable sin solución pública.
- [ ] Mapear cada evidencia a RA9.a-h y crear rúbrica específica.
- [ ] Añadir seguridad transversal: secretos, SSRF, cuotas, licencias y datos externos no confiables.
- [ ] Preparar pruebas, documentación y cuestionario docente en sus ubicaciones correspondientes.

### P3 - ampliación opcional

- [ ] Evaluar una integración mínima con Spring AI (chat model) o FastAPI solo si añade análisis útil no cubierto por el núcleo.
- **Decisión IA**: solo llamada a un chat model vía Spring AI. Quedan excluidos RAG, vector stores, MCP y agentes. La aplicación debe funcionar sin IA.

## Criterio de cierre

- Existe un recorrido único y ejecutable, no una colección de tecnologías.
- RA9.a-h tienen evidencias observables y no se agrupan en una demostración superficial.
- Las pruebas no requieren acceso real a servicios de terceros.
- El alumnado puede explicar procedencia, licencia, transformación y comportamiento ante fallos.
- La IA es opcional y no sustituye los fundamentos de integración de datos y servicios.
