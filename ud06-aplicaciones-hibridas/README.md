# UD6 — Aplicaciones Híbridas

## Propósito

Desarrollar aplicaciones web que integren información y código de terceros (APIs públicas, datasets versionados, librerías externas), los transformen en un repositorio propio normalizado y operen esa integración de forma segura, resiliente y verificable.

RA9 es el único RA sin cobertura previa en UD1–UD5. No repite CRUD, JWT, Swagger, MVC ni frontend.

## Alcance realista (post-Navidad)

UD6 se imparte después de Navidad, con calendario reducido por prácticas en empresa. El alcance prioriza un recorrido único ejecutable sobre una colección de tecnologías.

| Fase | Contenido | Estado |
|------|-----------|--------|
| **P0** | Contrato, publicación, fuentes | **Completado** |
| **P1** | Ejemplo ejecutable con Spring Boot | **Completado** — P1A/P1B/P1C: modelo e ingesta idempotente, cliente Open Library, caché oficial `spring-boot-starter-cache` + Caffeine (máx. 100, TTL 24 h, clave normalizada), control de tasa 1 req/s y observabilidad SLF4J. **55 pruebas offline superadas**; los reintentos son una ampliación opcional y no bloquean P1 |
| **P2** | Práctica y evaluación | Planificado |
| **P3** | Ampliación opcional (IA) | Planificado |

## Prerrequisitos

- Java 25, Spring Boot 4, Maven
- Cliente HTTP, mapeo DTO, persistencia JPA, pruebas con dobles (UD2a)
- Contenedores Docker para bases de datos (UD2a opcional, UD3)

## RA9 — Mapa de evidencias

| CE | Título | Evidencia prevista |
|----|--------|--------------------|
| a | Reutilización de código e información existente | Justificación de qué código/datos se reutilizan y qué coste evitan |
| b | Identificación de librerías y tecnologías | Comparación y selección razonada de cliente HTTP, mapeo, persistencia, resiliencia |
| c | Recuperación y procesamiento | Ingesta real de una API externa y un dataset versionado |
| d | Creación de repositorios propios | Construcción idempotente con procedencia, licencia y fecha de actualización |
| e | Uso de librerías para funcionalidades específicas | Empleo justificado de librerías para HTTP, mapeo, resiliencia o análisis |
| f | Programación con código de terceros | Servicio que respeta contrato, licencia y atribución de la fuente original |
| g | Análisis e inteligencia de datos | Análisis reproducible; IA externa solo como ampliación opcional (P3) |
| h | Pruebas, depuración y documentación | Pruebas con proveedor simulado, diagnóstico de fallos, documentación reproducible |

## Secuencia canónica

1. Introducción al problema de integración y procedencia de fuentes
2. Elección de fuentes: API externa pública + dataset versionado local
3. Cliente HTTP resiliente con WebClient
4. Ingesta, mapeo y normalización
5. Persistencia idempotente del repositorio derivado
6. Consulta o análisis agregado
7. Pruebas offline con dobles del proveedor
8. Documentación de arquitectura, fuentes y limitaciones

## Concepto conductor: Catálogo Cultural Híbrido

Aplicación Spring Boot que integra:

- **Fuente externa**: Open Library Search API para búsquedas humanas de bajo volumen
- **Fuente local**: snapshot versionado de datos estructurados de Wikidata bajo licencia CC0
- **Repositorio derivado**: normalización, fusión e idempotencia
- **Análisis**: consulta agregada que demuestra procedencia, transformación y valor añadido

No se implementa frontend completo, autenticación JWT como contenido nuevo, OpenAPI como novedad ni arquitectura por capas como aprendizaje.

## Alcance obligatorio vs opcional

### Obligatorio (P1–P2)

| Requisito | Detalle |
|-----------|---------|
| Cliente HTTP | URI segura, timeouts, errores controlados |
| Ingesta dual | API externa + dataset versionado en formato diferente |
| Mapeo y persistencia | Modelo propio, repositorio derivado, idempotencia |
| Procedencia | Licencia, instante de actualización, política de refresco |
| Resiliencia | Caché y control de tasa; reintentos seguros como ampliación opcional |
| Seguridad | API keys protegidas, validación de datos externos no confiables |
| Pruebas offline | Dobles del proveedor, sin dependencia de Internet |
| Casos de error | Timeout, respuesta inválida, límite de cuota, proveedor no disponible |
| Documentación | Arranque, arquitectura, fuentes, decisiones, limitaciones |

### Opcional (P3)

| Ampliación | Condición |
|------------|-----------|
| Spring AI | Llamada a un chat model como análisis adicional; sin RAG, vectores ni MCP |
| FastAPI | Solo si el equipo docente lo considera útil; sin unidad propia |

### Excluido

- Repetir CRUD, JWT, OpenAPI, MVC como contenido nuevo
- Implementación dual Spring Boot + Laravel
- Node/Express (cubierto por otro módulo)
- Frontend completo o app móvil híbrida
- RAG, vectores, MCP, agentes
- Unidad completa de IA

## Verificación y publicación

| Aspecto | Criterio |
|---------|----------|
| Build | `mkdocs build --strict` sin errores |
| Enlaces | Sin rotos en la publicación |
| Archivo | Contenido no publicable en `90-archivo/`, `90-historico/` o `99-profesor/`. El histórico de chat se conserva en `01-documentacion/90-historico/` (rastreado por Git, excluido de MkDocs). |
| Navegación | Generada automáticamente por `hooks/nav_generator.py` |
| Pruebas | `mvn test` verde en el ejemplo ejecutable |
| Offline | Ninguna prueba depende de Internet real |

## Histórico de fases

| Fase | Fecha | Logros |
|------|-------|--------|
| P0 | 2026-07 | README, contrato, archivo de chat, planificación, rúbrica |
| P1A | 2026-07 | Modelo, repositorio JPA, ingesta idempotente, fixture Wikidata verificado, 18 pruebas offline, `spring-boot-starter-webclient` para WebClient en P1B |
| P1B | 2026-07 | Cliente Open Library (`/search.json`) con propiedades tipadas, mapeo a `CulturalRecord` y fallos controlados; orquestación `CatalogSearchService` sobre la ingesta idempotente; 39 pruebas offline con WireMock 3 |
| P1C | 2026-07 | Integración oficial `spring-boot-starter-cache` + Caffeine (`SearchCachingConfig`, caché predeclarada, máx. 100, expire-after-write 24 h, clave = consulta normalizada + límite), control de tasa conservador 1 req/s y observabilidad SLF4J; 55 pruebas offline superadas. Reintentos seguros opcionales |
