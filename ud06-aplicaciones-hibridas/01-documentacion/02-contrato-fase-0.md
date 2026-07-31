# Contrato Fase 0 — Fuentes, modelo e integración

Contrato canónico de UD6 (RA9) que define las fuentes, el modelo normalizado, los contratos de integración y los límites de uso antes de implementar el ejemplo ejecutable (P1).

## Fuentes seleccionadas

| Fuente | Tipo | Formato | Licencia | Límite de uso |
|--------|------|---------|----------|---------------|
| Open Library Search API | Remota (REST) | JSON | Internet Archive: sin copyright nuevo declarado; derechos de contribuciones y jurisdicciones variables | 1 req/s (3 req/s con identificación); solo consulta humana ocasional |
| Wikidata (subconjunto educativo) | Versionada en repo | CSV o JSON | CC0 (dominio público) | Sin límite técnico; el fixture se genera en P1 con URLs de origen |

### Open Library — Restricciones operativas

- **Documentación oficial**: <https://openlibrary.org/developers/api>
- **Licencia**: <https://openlibrary.org/developers/licensing>
- Solo **consulta humana ocasional**, nunca como backend de alto tráfico.
- Obligatorio incluir cabecera `User-Agent` con identificación y medio de contacto.
- **Cachear respuestas** obligatorio; no repetir la misma consulta en cada ejecución.
- Prohibido el **rastreo masivo** (*bulk harvesting*).
- **No redistribuir contenido de libros**. Almacenar únicamente metadatos y procedencia; atribuir y enlazar a la fuente.

### Wikidata — Condiciones de uso

- <https://www.wikidata.org/wiki/Wikidata:Copyright>
- El subconjunto se genera en P1 como fixture educativo versionado (CSV/JSON).
- Cada registro incluye la URL del ítem origen y la fecha de recuperación.
- Licencia CC0: no requiere atribución, pero se documenta la procedencia por transparencia docente.

## Modelo normalizado

| Campo | Tipo | Origen | Obligatorio |
|-------|------|--------|-------------|
| `source` | enumerado | `OPEN_LIBRARY` o `WIKIDATA` | Sí |
| `externalId` | texto | Identificador en la fuente | Sí |
| `title` | texto | Título de la obra | Sí |
| `creators` | lista de texto | Autores/creadores | No |
| `year` | entero | Año de publicación | No |
| `subjects` | lista de texto | Categorías o materias | No |
| `sourceUrl` | texto | URL canónica en la fuente original | Sí |
| `license` | texto | Licencia declarada por la fuente | Sí |
| `retrievedAt` | instante (ISO 8601) | Fecha/hora de la recuperación o importación | Sí |

### Regla de identidad e idempotencia

La clave compuesta `(source, externalId)` es la identidad única del repositorio derivado. Una misma clave no puede aparecer duplicada.

La operación de importación es **idempotente**: ejecutarla múltiples veces sobre los mismos datos origen produce exactamente el mismo estado en el repositorio derivado. Si un registro ya existe, se actualizan sus metadatos (título, creadores, etc.) y el instante de importación; no se crea un duplicado.

## Contratos de integración

### Importación desde Open Library

- **Endpoint**: `https://openlibrary.org/search.json`
- **Parámetros de consulta**: `q` (término de búsqueda), `limit` (máx. 100), `fields` (lista de campos solicitados).
- **Campos solicitados**: `key`, `title`, `author_name`, `first_publish_year`, `subject`, `cover_edition_key`.
- **Cache**: las respuestas se almacenan localmente con TTL configurable (por defecto 24 h). No repetir la misma petición en el mismo TTL.
- **User-Agent**: `DWES-UD6/1.0 (docencia; contacto: <email-del-docente>)`.
- **Fallo de red/timeout**: error controlado sin reintento automático y sin estado inconsistente.
- **Política de reintentos de P1**: el camino núcleo no realiza reintentos automáticos. Los reintentos con backoff + jitter quedan como experimento futuro opcional de resiliencia.

### Importación desde Wikidata (fixture local)

- **Formato**: CSV con cabeceras o JSON array (decidido en P1).
- **Estructura**: cada registro contiene `wikidataId`, `label`, `description`, `aliases`, `instanceOf`, `sourceUrl` y `retrievedAt`.
- **Localización**: recurso classpath cargado como `dataset/authors-wikidata.{csv,json}`.
- **No depende de red**: el fixture se lee del classpath; las pruebas nunca llaman a la API de Wikidata.

### Consulta al repositorio derivado

- Filtros: por fuente, por texto parcial en título, por creador, por año, por categoría.
- Paginación: con página y tamaño de página.
- Los resultados incluyen siempre los campos `source`, `externalId`, `title`, `sourceUrl`, `license` y `retrievedAt`.

## Semántica de fallos

| Condición | Comportamiento |
|-----------|----------------|
| Open Library devuelve `429 Too Many Requests` | Devolver error controlado e informativo, sin reintento automático ni dato parcial |
| Open Library devuelve `5xx` | Devolver error controlado e informativo, sin reintento automático |
| Timeout de conexión/lectura | Capturar como error controlado; no persistir estado inconsistente |
| Respuesta malformada (JSON inválido) | Error controlado; no persistir datos corruptos |
| Fixture local ausente o malformado | Error en arranque (fail-fast); detectable en pruebas |
| Caché expirada sin conexión | Usar caché existente con advertencia; no fallar silenciosamente |

P1 se considera completo con esta política: caché, control de tasa y errores controlados protegen el camino núcleo. Los reintentos limitados con backoff + jitter no son obligatorios; se reservan como experimento futuro opcional y deberán demostrar que no amplifican respuestas `429` ni fallos `5xx`.

## Seam de pruebas offline

- **Open Library**: el cliente HTTP se diseña contra una interfaz; la implementación real se sustituye por un doble (WireMock o mock manual) en pruebas. Ninguna prueba depende de conexión real a Internet.
- **Wikidata**: el fixture es un recurso classpath. Las pruebas lo leen del sistema de archivos local sin red.
- **Repositorio**: se prueba con una base de datos embebida (H2) en lugar del motor de producción.

## Atribución y límites de seguridad

- Toda respuesta mostrada al usuario debe incluir: nombre de la fuente, URL canónica del registro original y licencia.
- El código fuente documenta la procedencia de cada fuente y las condiciones de uso aplicables.
- El contenido de libros (texto, portadas, fragmentos) **no se almacena ni redistribuye**.
- Las API keys se inyectan como variables de entorno o propiedades externalizadas; nunca se versionan en el repositorio.
- El servicio valida y sanitiza datos externos antes de persistirlos o mostrarlos (SSRF, campos contaminados, tipos inesperados).

## Dependencia de IA

La aplicación núcleo (P1–P2) **funciona completamente sin IA**. Solo en P3, y como ampliación opcional, se permite una llamada a un chat model vía Spring AI. Quedan excluidos RAG, vector stores, MCP y agentes.
