# Catálogo Cultural Híbrido — P1A

Ejemplo ejecutable de aplicación híbrida para UD6 del módulo DWES.
Establece la base local (modelo, repositorio, ingesta idempotente, fixture Wikidata)
sobre la que se añadirá la integración remota con Open Library en P1B.

## Propósito

Demostrar la construcción de un repositorio derivado normalizado a partir de
una fuente de datos local (fixture JSON derivado de Wikidata, CC0).

## Comandos

| Acción | Comando |
|--------|---------|
| Compilar | `mvn compile` |
| Ejecutar pruebas | `mvn test` |
| Empaquetar | `mvn package` |

Requiere Java 25+ y Maven 3.6+.

## Arquitectura (P1A)

```
Application
  └── IngestionService        ← idempotent upsert loop
        ├── CulturalItemRepository  ← Spring Data JPA
        └── WikidataCulturalRecord  ← DTO from fixture JSON
```

- **Modelo**: `CulturalItem` (JPA entity) con identidad compuesta `(source, externalId)`.
- **Source**: enumerado con `WIKIDATA` y `OPEN_LIBRARY` (P1B).
- **Repositorio**: `CulturalItemRepository` con lookup por `findBySourceAndExternalId`.
- **Ingesta**: `IngestionService.ingestFixture()` lee el fixture del classpath, mapea
  cada registro y ejecuta upsert dentro de una transacción.
- **Colecciones**: `creators` y `subjects` se serializan como texto delimitado con `||`
  mediante `StringListConverter` (JPA `AttributeConverter`). Alternativa: usar
  `@ElementCollection` con tabla separada. Se optó por `AttributeConverter` por su
  simplicidad docente, a costa de renunciar a consultas JPQL sobre elementos individuales.

## Dependencias (P1A)

Se usa `spring-boot-starter-webclient` (Boot 4.0.5) en lugar de
`spring-boot-starter-webflux`: el starter focalizado de Boot 4 aporta WebClient y
Jackson 3 (`tools.jackson.*`) para la integración remota de P1B sin arrancar un
servidor reactivo (solo reactor-netty como cliente HTTP). El resto son JPA, H2 y
pruebas.

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

Total: 18 tests (3 unitarios, 5 repositorio, 10 integración). Todas offline (H2
embebida, fixture en classpath).

## Límites P1A

- **Sin Open Library**: la integración remota se implementa en P1B.
- **Sin controlador web**: no hay endpoints REST.
- **Sin JWT, OpenAPI, frontend**: fuera del alcance de UD6.
- **Sin Spring AI, RAG, vectores, MCP, agentes**: excluidos explícitamente.

## Siguiente paso: P1B

Añadir:
- Cliente HTTP con `WebClient` para Open Library Search API
- Mapeo de respuesta JSON de Open Library al modelo `CulturalItem`
- Integración dual: ingesta desde API remota + fixture local
- Pruebas offline con doble del proveedor HTTP
