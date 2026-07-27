# Trazabilidad SDD — contrato OpenAPI versionado

## Propósito y alcance

Este cambio convierte `static/api-docs/battleship-v1.yaml` en la fuente de
verdad de las nueve operaciones HTTP de Battleship. Mantiene las rutas sin
prefijo de versión, usa Swagger UI solo como visor y prueba la conformidad de
la implementación sin sustituir las pruebas específicas de autorización por
roles. También corrige la interoperabilidad de los access tokens emitidos por
login. No introduce generación de código ni normaliza todos los errores.

## Requisitos y escenarios comprobables

- El YAML debe ser OpenAPI 3.1, declarar v1 y enumerar exactamente registro,
  login, refresh y las seis operaciones de partidas.
- Requests, respuestas, validaciones, `Location`, errores y la respuesta
  paginada `PageResponse` con campo `page` deben coincidir con el runtime.
- Registro, login, refresh, listado y consulta son públicos; crear, colocar y
  atacar requieren `PLAYER`, y cancelar requiere `ADMIN`.
- Swagger UI debe cargar únicamente el YAML canónico. `/api-docs` generado
  permanece deshabilitado.
- La matriz MockMvc debe cubrir las nueve operaciones y errores representativos
  `400`, `401`, `403`, `404` y `409`. El `429` se declara en todas las
  operaciones y el rechazo real se prueba de forma focalizada en el filtro.
- Un access token `PLAYER` emitido por login debe autenticar `POST /api/games`.

## Decisiones de diseño

1. **Contrato estático y versionado**: evita que anotaciones Java creen una
   segunda especificación divergente.
2. **Publicación única**: Springdoc conserva Swagger UI, pero deshabilita la
   generación de `/api-docs`.
3. **Dos niveles de evidencia**: parser y Atlassian MockMvc prueban estructura
   e interacciones; `SecurityAuthorizationIntegrationTest` sigue siendo la
   autoridad para claims y roles.
4. **Compatibilidad de tokens**: los roles de access token se normalizan al
   prefijo `ROLE_` sin debilitar la validación del filtro JWT.

## Secuencia de tareas y unidades de trabajo

| Unidad | Foco | Evidencia reproducible actual |
|---|---|---|
| 1. Tokens | Interoperabilidad login → operación `PLAYER` | Login emite un claim normalizado y el token crea una partida |
| 2. Contrato | Recurso, inventario y publicación canónicos | Parser, YAML v1 y Swagger UI con URL única |
| 3. Conformidad | Cuerpos, límites y errores | Nueve operaciones, variantes y regresiones de seguridad verdes |
| 4. Cierre y documentación | CORS y guías alineados con el contrato | CORS limitado por regresión y documentación contrastada con código, YAML y tests |

Las unidades se diseñaron como cortes revisables y apilables. La regresión de
CORS verifica `GET`, `POST`, `DELETE` y `OPTIONS`, y rechaza `PUT` y `QUERY`.
Las correcciones documentales se validan con el build estricto y las puertas
técnicas existentes.

## Evidencia strict-TDD y reproducción

La evidencia reproducible actual está en estos tests versionados:

- `SecurityAuthorizationIntegrationTest`: token de login, matriz de roles y
  lista CORS canónica con rechazo de métodos heredados.
- `OpenApiContractTest`: estructura, inventario, schemas y respuestas.
- `OpenApiPublicationIntegrationTest`: publicación canónica y ausencia del
  documento generado.
- `OpenApiConformanceIntegrationTest`: requests/responses MockMvc frente al
  YAML mediante Atlassian.
- `RateLimitFilterTest`: rama real `429` y payload de error canónico.

La regresión de token emitido por login permanece intencionadamente en
`SecurityAuthorizationIntegrationTest`, no en un
`LoginTokenIntegrationTest` separado. Así recorre el camino real completo
login → token devuelto → solicitud protegida y conserva la matriz de roles que
debe seguir siendo la autoridad de seguridad; separarla duplicaría fixtures y
podría ocultar esa interoperabilidad entre autenticación y autorización.

Base de esta unidad documental: `main@ddf960b`.

```bash
# Desde la raíz de Battleship
./mvnw clean verify

# Desde la raíz del repositorio
mkdocs build --strict
git diff --check
```

En la revisión final del 25 de julio de 2026, la primera orden terminó
con **136 pruebas, 0 fallos y 0 errores** sobre Java 25.0.3; el build estricto
de MkDocs y `git diff --check` también terminaron correctamente.

## Persistencia y auditoría

La guía presente es la **traza versionada de auditoría y enseñanza** que puede
leer el alumnado en el repositorio. Conserva la intención, los requisitos, las
decisiones de diseño, las tareas y la evidencia verificable sin depender de una
herramienta concreta. Los artefactos de trabajo detallados se mantienen en el
backend SDD privado configurado; esta guía es la referencia pública, versionada
y estable para docencia y auditoría.

Los ejemplos OpenAPI representativos más allá de la respuesta textual de
registro continúan pendientes y se mantienen sin marcar en el inventario de
reforma; no se confunden con la trazabilidad SDD ya documentada.
