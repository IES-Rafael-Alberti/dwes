# UD6 — Aplicaciones Híbridas

## Propósito

Desarrollar aplicaciones web que integren información y código de terceros (APIs públicas, datasets versionados, librerías externas), los transformen en un repositorio propio normalizado y operen esa integración de forma segura, resiliente y verificable.

RA9 es el único RA sin cobertura previa en UD1–UD5. No repite CRUD, JWT, Swagger, MVC ni frontend.

## Alcance realista (post-Navidad)

UD6 se imparte después de Navidad, con calendario reducido por prácticas en empresa. El alcance prioriza un recorrido único ejecutable sobre una colección de tecnologías.

| Fase | Contenido | Estado |
|------|-----------|--------|
| **P0** | Contrato, publicación, fuentes | **Completado** |
| **P1** | Ejemplo ejecutable con Spring Boot | **Completado** — P1A/P1B/P1C: modelo e ingesta idempotente, cliente Open Library, caché oficial `spring-boot-starter-cache` + Caffeine (máx. 100, TTL 24 h, clave normalizada), control de tasa 1 req/s y observabilidad SLF4J. **55 pruebas offline del núcleo superadas**; los reintentos son una ampliación opcional y no bloquean P1 |
| **P2A** | Práctica incremental y seguridad transversal | **Completado** — entregables RA9.a-h, análisis acotado con Tablesaw y auditoría de consumo de terceros |
| **P2B** | Proyecto de transferencia evaluable, trazabilidad RA9 y rúbrica; banco GIFT privado preparado para Moodle | **Completado** |
| **P3** | Ampliación opcional (una llamada de chat) | **Completado** — Spring AI 2.0.0 + Ollama, desactivado por defecto, 21 pruebas offline; 76 pruebas totales |

**UD6 queda cerrada** en sus materiales técnicos y documentales. Solo permanece
pendiente el paso operativo privado de importar el banco GIFT en Moodle.

## Prerrequisitos

- Java 25, Spring Boot 4, Maven
- Cliente HTTP, mapeo DTO, persistencia JPA, pruebas con dobles (UD2a)
- Contenedores Docker para bases de datos (UD2a opcional, UD3)

## RA9 — Mapa de evidencias

> **9. Desarrolla aplicaciones web híbridas seleccionando y utilizando tecnologías, frameworks servidor y repositorios heterogéneos de información.**

La tabla siguiente resume la evidencia de aprendizaje de **P2A**. Sus nombres de
prueba pertenecen a la [práctica guiada](03-ejercicios/practica-integracion/README.md#trazabilidad-minima-de-ra9)
y no constituyen el mapa canónico del proyecto independiente P2B.

| CE | Título | Evidencia guiada de P2A |
|----|--------|--------------------|
| a | Reutilización de código e información existente | Decisión y coste evitado en `docs/fuentes.md` |
| b | Identificación de librerías y tecnologías | Comparativa y selección en `docs/fuentes.md`, incluida la alternativa a Tablesaw |
| c | Recuperación y procesamiento | Adaptadores demostrados por `LocalDatasetTest` y `ExternalProviderClientTest` |
| d | Creación de repositorios propios | Contrato e ingesta demostrados por `NormalizedContractTest` e `IdempotentIngestionTest` |
| e | Uso de librerías para funcionalidades específicas | Dependencias usadas y comportamiento en los tests de cliente, caché/throttle y análisis |
| f | Programación con código de terceros | Contrato, licencia y atribución en el informe, verificados por tests de fuente |
| g | Análisis e inteligencia de datos | Tablesaw sobre datos normalizados, comparación e interpretación en `docs/informe-final.md` y `RepositoryAnalysisTest`; el chat opcional no basta |
| h | Pruebas, depuración y documentación | Nueve tests escritos por el alumnado, diagnóstico y comandos offline registrados en `docs/informe-final.md` |

### Evidencia independiente de P2B

P2B define [su propia tabla de pruebas y reproducibilidad](04-proyectos/proyecto-integracion-hibrida/README.md#8-pruebas-reproducibilidad-y-documentacion):
el corte mínimo cubre siete comportamientos —carga y procedencia del dataset,
invariantes del modelo neutral, contrato y mapeo del adaptador externo, identidad
con idempotencia y rollback básico, análisis BI reproducible, un fallo controlado
representativo y aplicable, y una política determinista de protección del
proveedor—. Los dos refinamientos de nivel superior son la matriz de fallos
aplicable y la concurrencia/conflicto con resultado transaccional observable.
Los nombres de clase son orientativos y pueden adaptarse justificadamente.

La trazabilidad se evalúa con la [matriz oficial de P2B](04-proyectos/proyecto-integracion-hibrida/ra-ce-evidencias.md)
y su [rúbrica criterial](04-proyectos/proyecto-integracion-hibrida/rubrica-ra9.md),
sin trasladar automáticamente las nueve clases de P2A.

## Secuencia canónica

1. Introducción al problema de integración y procedencia de fuentes
2. Elección de fuentes: API externa pública + dataset versionado local
3. Cliente HTTP resiliente con WebClient
4. Ingesta, mapeo y normalización
5. Persistencia idempotente del repositorio derivado
6. Análisis acotado de cobertura con una librería tabular
7. Pruebas offline con dobles del proveedor
8. Documentación de arquitectura, fuentes y limitaciones

## Material canónico para el alumnado

- [Práctica incremental de integración](03-ejercicios/practica-integracion/README.md): recorrido evaluable offline por checkpoints, sin solución pública.
- [Proyecto evaluable de integración híbrida](04-proyectos/proyecto-integracion-hibrida/README.md): transferencia independiente a fuentes y dominio aprobados, con entregables, hitos, aceptación y defensa.
- [Evidencias RA9 del proyecto](04-proyectos/proyecto-integracion-hibrida/ra-ce-evidencias.md) y [rúbrica específica](04-proyectos/proyecto-integracion-hibrida/rubrica-ra9.md): trazabilidad y niveles observables sin ponderaciones inventadas.
- [Seguridad al consumir APIs y datos de terceros](06-seguridad/README.md): modelo de amenazas, controles y lista de auditoría enlazada desde la práctica.
- [Catálogo Cultural Híbrido](02-ejemplos/catalogo-cultural-hibrido/README.md): ejemplo P1 de referencia, no plantilla para copiar.
- [Integración opcional de chat con Spring AI](01-documentacion/03-integracion-chat-spring-ai.md): P3, una llamada estructurada sobre datos normalizados, sin RAG ni descarga automática.

El banco GIFT privado de 14 preguntas está preparado localmente para Moodle. No se
versiona ni se enlaza públicamente por la política de evaluación. Su importación
en Moodle continúa como paso operativo docente mientras no se haya realizado.

## Concepto conductor: Catálogo Cultural Híbrido

Aplicación Spring Boot que integra:

- **Fuente externa**: Open Library Search API para búsquedas humanas de bajo volumen
- **Fuente local**: snapshot versionado de datos estructurados de Wikidata bajo licencia CC0
- **Repositorio derivado**: normalización, fusión e idempotencia
- **Análisis**: consulta agregada que demuestra procedencia, transformación y valor añadido

No se implementa frontend completo, autenticación JWT como contenido nuevo, OpenAPI como novedad ni arquitectura por capas como aprendizaje.

## Alcance del ejemplo y del proyecto

### Comportamiento del ejemplo canónico P1

Estos requisitos describen el comportamiento implementado por el Catálogo
Cultural Híbrido de P1. Son referencia técnica, no requisitos universales que
P2B deba copiar con otras fuentes.

| Requisito | Detalle |
|-----------|---------|
| Cliente HTTP | URI segura, timeouts, errores controlados |
| Ingesta dual | API externa + dataset versionado en formato diferente |
| Mapeo y persistencia | Modelo propio, repositorio derivado, idempotencia |
| Procedencia | Licencia, instante de actualización, política de refresco |
| Resiliencia | Caché y control de tasa; reintentos seguros como ampliación opcional |
| Seguridad | API keys protegidas, validación de datos externos no confiables |
| Pruebas offline | Dobles del proveedor, sin dependencia de Internet |
| Análisis/BI | `tablesaw-core` sobre datos normalizados, comparación razonada, informe y test determinista |
| Casos de error | Timeout, respuesta inválida, límite de cuota, proveedor no disponible |
| Documentación | Arranque, arquitectura, fuentes, decisiones, limitaciones |

### Corte obligatorio de P2B

P2B exige offline al menos **un fallo controlado representativo y aplicable** a la
fuente aprobada. La matriz separada de timeout, `429`, `5xx` y datos malformados
aporta evidencia de nivel superior únicamente para los casos contemplados por su
contrato. Del mismo modo, caché y control de tasa son obligatorios solo cuando los
términos, la cuota o la carga prevista los justifican; en otro caso se documenta
y prueba de forma determinista una política equivalente de protección del
proveedor. El [enunciado de P2B](04-proyectos/proyecto-integracion-hibrida/README.md#corte-minimo-obligatorio)
define el corte completo y sus refinamientos.

### Opcional (P3)

| Ampliación | Condición |
|------------|-----------|
| Spring AI | P3 completado como enriquecimiento opcional; no satisface RA9.g por sí solo; una llamada, sin RAG, vectores ni MCP |
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
| Pruebas | Dependencias, plugins y transitivos resueltos; los comandos exactos se ejecutan una vez con red y después `mvn -o test` queda verde en el ejemplo y la entrega |
| Offline | Ninguna prueba depende de Internet real |

## Histórico de fases

| Fase | Fecha | Logros |
|------|-------|--------|
| P0 | 2026-07 | README, contrato, archivo de chat, planificación, rúbrica |
| P1A | 2026-07 | Modelo, repositorio JPA, ingesta idempotente, fixture Wikidata verificado, 18 pruebas offline, `spring-boot-starter-webclient` para WebClient en P1B |
| P1B | 2026-07 | Cliente Open Library (`/search.json`) con propiedades tipadas, mapeo a `CulturalRecord` y fallos controlados; orquestación `CatalogSearchService` sobre la ingesta idempotente; 39 pruebas offline con WireMock 3 |
| P1C | 2026-07 | Integración oficial `spring-boot-starter-cache` + Caffeine (`SearchCachingConfig`, caché predeclarada, máx. 100, expire-after-write 24 h, clave = consulta normalizada + límite), control de tasa conservador 1 req/s y observabilidad SLF4J; 55 pruebas offline superadas. Reintentos seguros opcionales |
| P2A | 2026-07 | Práctica guiada incremental con nueve checkpoints y evidencia observable RA9.a-h; Tablesaw 0.44.4 aplicado a cobertura del repositorio con test offline; política SSRF por entorno y auditoría de secretos, datos no confiables, resiliencia, cuotas, privacidad, licencias y dobles |
| P2B | 2026-07 | Proyecto independiente de transferencia con aprobación previa de fuentes, matriz literal RA9.a-h, rúbrica criterial, aceptación offline y defensa. Banco GIFT privado de 14 preguntas preparado localmente para Moodle, no versionado; no se publica solución |
| P3 | 2026-07 | Spring AI 2.0.0 con starter Ollama focalizado: puerto/adaptador condicional, prompt acotado para datos no confiables, salida estructurada e IDs validados. Desactivado por defecto, sin auto-pull; 21 pruebas nuevas, 76 totales offline |
