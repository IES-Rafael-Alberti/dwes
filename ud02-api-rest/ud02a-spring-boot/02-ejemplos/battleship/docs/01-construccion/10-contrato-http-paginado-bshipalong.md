# Contrato HTTP paginado de partidas

Esta guía continúa [09: filtros, orden y paginación](09-filtros-orden-y-paginacion-bshipalong.md)
y convierte el listado de partidas en un contrato HTTP estable.

## 1. El contrato antes de tocar el código

Al completar esta guía, `GET /api/games` devolverá un objeto de página, no un array:

```json
{
  "content": [
    {
      "id": 7,
      "boardSize": 10,
      "status": "PENDING",
      "createdAt": "2026-11-19T13:00:00",
      "ships": [],
      "attacks": []
    }
  ],
  "page": 0,
  "size": 20,
  "totalElements": 1,
  "totalPages": 1,
  "first": true,
  "last": true
}
```

Reglas públicas:

| Parámetro | Regla |
|---|---|
| `page` | índice basado en cero; por defecto `0`; no puede ser negativo |
| `size` | por defecto `20`; valores permitidos `1..100` |
| `status` | opcional; valores del enum (`PENDING`, `IN_PROGRESS`, `WON`, `CANCELLED`) |
| orden | siempre `id ASC`; no se ofrece un parámetro HTTP `sort` |
| activos | solo se incluyen partidas con `active=true` |

Una página válida sin resultados responde `200` y conserva la forma:

```json
{"content":[],"page":3,"size":2,"totalElements":3,"totalPages":2,"first":false,"last":true}
```

`page=-1`, `size=0`, `size=101`, un estado desconocido o parámetros no numéricos
responden `400` con `ErrorPayload`:

```json
{
  "error": "BAD_REQUEST",
  "message": "size must be between 1 and 100",
  "timestamp": "2026-11-19T13:00:00Z"
}
```

El contrato es incompatible con los clientes antiguos que deserializaban un array
o una `List<GameResponseDTO>`: deben leer `content` y la metadata de página. No se
expone un `GET sort`; el orden es una decisión del servidor y forma parte del
contrato. La representación completa de cada partida (`GameResponseDTO`, incluidos
`ships` y `attacks`) se mantiene.

## 2. Punto de partida y comprobación opcional

Ejecuta todo desde la raíz de `bshipAlong`:

```bash
./mvnw test
```

Antes de modificar el servicio, ejecuta opcionalmente el test unitario del servicio:

```bash
./mvnw -Dtest=GameServiceTest test
```

Al cambiar de una consulta sin argumentos a otra con `Pageable`, Mockito distingue
ambas llamadas y puede producir `PotentialStubbingProblem`. Al actualizar el test,
el stub debe usar el `Pageable` esperado (o
`any(Pageable.class)`) y devolver un `Page<Game>`; no se debe ocultar el problema
con cambios en producción.

En el punto de partida de clase se conservan las dos sobrecargas
`findByActiveTrue()` y `findByActiveTrue(Sort)`, precisamente para estudiar la
sobrecarga. Si `listGames` llama a `findByActiveTrue(Sort.unsorted())`, pero
`GameServiceTest.listsAndFiltersDtos` configura la versión sin argumentos, la
prueba falla: el mock distingue ambas llamadas. Para comprobar la línea base,
sustituye **solo ese stub** por:

```java
when(repository.findByActiveTrue(org.springframework.data.domain.Sort.unsorted()))
        .thenReturn(java.util.List.of(pending(1L), pending(2L)));
```

No se elimina ninguna sobrecarga. Después, al introducir `Pageable`, el test
se adapta de nuevo en el apartado 6 para aceptar la página que crea el servicio.

## 3. DTO de página

Crea o sustituye exactamente
`src/main/java/es/daw2a/bshipalong/dto/PageResponseDTO.java` por:

```java
package es.daw2a.bshipalong.dto;

import java.util.List;

public record PageResponseDTO<T>(
        List<T> content,
        int page,
        int size,
        long totalElements,
        int totalPages,
        boolean first,
        boolean last) {
}
```

Es un DTO propio: así no se filtra el formato interno de `org.springframework.data`
y se conserva el mismo tipo de elemento que ya usaba el endpoint.

## 4. Servicio: página, filtro y orden fijo

En `src/main/java/es/daw2a/bshipalong/service/GameService.java`, añade estos
imports si no existen:

```java
import es.daw2a.bshipalong.dto.PageResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
```

Sustituye el método `listGames` completo, conservando su anotación
`@Transactional(readOnly = true)`:

```java
@Transactional(readOnly = true)
public PageResponseDTO<GameResponseDTO> listGames(GameStatus status, int page, int size) {
    var pageable = PageRequest.of(page, size, Sort.by("id").ascending());
    Page<Game> games = status == null
            ? repository.findByActiveTrue(pageable)
            : repository.findByActiveTrueAndStatus(status, pageable);
    return new PageResponseDTO<>(
            games.map(this::toResponse).getContent(),
            games.getNumber(),
            games.getSize(),
            games.getTotalElements(),
            games.getTotalPages(),
            games.isFirst(),
            games.isLast());
}
```

La firma del repositorio debe ser paginable. En
`src/main/java/es/daw2a/bshipalong/repository/GameRepository.java`, conserva los
imports existentes. Comprueba que están estas dos firmas de la guía 09; si falta
alguna, añádela y agrega los imports `Page` y `Pageable`:

```java
Page<Game> findByActiveTrue(Pageable pageable);

Page<Game> findByActiveTrueAndStatus(GameStatus status, Pageable pageable);
```

Conserva también las sobrecargas anteriores sin `Pageable`: `GameQueriesTest`
y `PostgresVerificationIT` siguen utilizándolas. No dupliques ninguna firma.

No añadas un parámetro `Sort` al controlador. El servicio construye siempre
`Sort.by("id").ascending()`.

## 5. Controlador y errores HTTP

En `src/main/java/es/daw2a/bshipalong/web/GameController.java`, la firma del
listado debe quedar así (imports: `GameStatus`, `PageResponseDTO` y
`GameResponseDTO`):

```java
@GetMapping
public PageResponseDTO<GameResponseDTO> list(
        @RequestParam(name = "status", required = false) GameStatus status,
        @RequestParam(name = "page", defaultValue = "0") int page,
        @RequestParam(name = "size", defaultValue = "20") int size) {
    if (page < 0) {
        throw new IllegalArgumentException("page must be greater than or equal to 0");
    }
    if (size < 1 || size > 100) {
        throw new IllegalArgumentException("size must be between 1 and 100");
    }
    return service.listGames(status, page, size);
}
```

Conserva `GlobalExceptionHandler`. En el prototipo, `IllegalArgumentException` y
los errores de conversión de parámetros producen `ErrorPayload` con estado `400`;
la conversión de un `status` inexistente se captura como
`MethodArgumentTypeMismatchException`. `ErrorPayload` completo es:

```java
package es.daw2a.bshipalong.dto;

import java.time.Instant;

public record ErrorPayload(String error, String message, String timestamp) {
    public ErrorPayload(String error, String message) {
        this(error, message, Instant.now().toString());
    }
}
```

## 6. Prueba HTTP ejecutable

Para cubrir explícitamente esos casos, crea
`src/test/java/es/daw2a/bshipalong/web/GamePaginationHttpTest.java` con este
contenido completo. Es una prueba adicional independiente:

```java
package es.daw2a.bshipalong.web;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class GamePaginationHttpTest {
    @Autowired MockMvc mvc;

    @Test
    void defaultPageFiltersStatusAndKeepsFullGameResponse() throws Exception {
        create(10);
        mvc.perform(get("/api/games").param("status", "PENDING"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].boardSize").value(10))
                .andExpect(jsonPath("$.content[0].ships").isArray())
                .andExpect(jsonPath("$.content[0].attacks").isArray())
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(20))
                .andExpect(jsonPath("$.first").value(true))
                .andExpect(jsonPath("$.last").value(true));
    }

    @Test
    void pageBoundaryReturnsSecondPage() throws Exception {
        create(8);
        create(9);
        create(10);

        mvc.perform(get("/api/games").param("page", "1").param("size", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.page").value(1))
                .andExpect(jsonPath("$.size").value(2))
                .andExpect(jsonPath("$.totalElements").value(3))
                .andExpect(jsonPath("$.totalPages").value(2))
                .andExpect(jsonPath("$.first").value(false))
                .andExpect(jsonPath("$.last").value(true));
    }

    @Test
    void pagesAreOrderedByIdAscending() throws Exception {
        long firstId = idFrom(create(8));
        long secondId = idFrom(create(9));
        create(10);

        mvc.perform(get("/api/games").param("page", "0").param("size", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(firstId))
                .andExpect(jsonPath("$.content[1].id").value(secondId));
    }

    @Test
    void inactiveGamesAreExcludedWhileStatusFilterIsApplied() throws Exception {
        String location = create(8);
        create(9);
        mvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                        .delete(location))
                .andExpect(status().isNoContent());

        mvc.perform(get("/api/games").param("status", "PENDING"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].boardSize").value(9));
    }

    @Test
    void emptyPageHasStableShape() throws Exception {
        create(8);
        create(9);
        mvc.perform(get("/api/games").param("page", "3").param("size", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(0)))
                .andExpect(jsonPath("$.page").value(3))
                .andExpect(jsonPath("$.size").value(2))
                .andExpect(jsonPath("$.totalElements").value(2))
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.first").value(false))
                .andExpect(jsonPath("$.last").value(true));
    }

    @Test
    void invalidStatusIsBadRequestErrorPayload() throws Exception {
        mvc.perform(get("/api/games").param("status", "NOT_A_STATUS"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.timestamp").isString());
    }

    @Test
    void invalidPageAndSizeAreBadRequest() throws Exception {
        mvc.perform(get("/api/games").param("page", "-1"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("BAD_REQUEST"));
        mvc.perform(get("/api/games").param("size", "0"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("BAD_REQUEST"));
        mvc.perform(get("/api/games").param("size", "101"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("BAD_REQUEST"));
    }

    @Test
    void invalidNumbersAreBadRequestErrorPayload() throws Exception {
        mvc.perform(get("/api/games").param("page", "abc"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.timestamp").isString());
        mvc.perform(get("/api/games").param("size", "xyz"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.timestamp").isString());
    }

    private String create(int boardSize) throws Exception {
        return mvc.perform(post("/api/games")
                        .contentType("application/json")
                        .content("{\"boardSize\":" + boardSize + "}"))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getHeader("Location");
    }

    private long idFrom(String location) {
        return Long.parseLong(location.substring(location.lastIndexOf('/') + 1));
    }
}
```

La clase completa anterior crea sus propios datos en una transacción aislada:
comprueba orden determinista, primera y segunda página, página no inicial vacía,
`size` personalizado, límites, cadenas no numéricas, estado inválido y exclusión
de inactivas combinada con el filtro de estado. La partida cancelada sigue en la
base, pero `active=false`; por eso no basta con filtrar por `status`.

Antes de ejecutar esta clase, verifica que el perfil de pruebas usa una base H2
limpia (sin datos precargados). `@Transactional` revierte los datos creados por
cada test, pero no garantiza que una base ya poblada esté vacía. Si arrancas la
aplicación con `spring-boot:run`, puedes estar usando una base persistente:
compruébalo o usa el perfil de pruebas antes de interpretar los totales.

Los tres cambios de producción (`GameRepository`, `GameService` y
`GameController`) y estos tests forman **una sola unidad de compilación**. Haz
todos los cambios siguientes antes de ejecutar Maven. En
`src/test/java/es/daw2a/bshipalong/service/GameServiceTest.java`, añade:

```java
import org.springframework.data.domain.PageImpl;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
```

Sustituye el método completo `listsAndFiltersDtos` por:

```java
@Test
void listsAndFiltersDtos() {
    when(repository.findByActiveTrue((org.springframework.data.domain.Pageable) any()))
            .thenReturn(new PageImpl<>(java.util.List.of(pending(1L), pending(2L))));
    when(repository.findByActiveTrueAndStatus(eq(GameStatus.PENDING), any()))
            .thenReturn(new PageImpl<>(java.util.List.of(pending(1L), pending(2L))));
    when(repository.findByActiveTrueAndStatus(eq(GameStatus.IN_PROGRESS), any()))
            .thenReturn(new PageImpl<>(java.util.List.of()));

    assertThat(service.listGames(null, 0, 20).content()).hasSize(2);
    assertThat(service.listGames(GameStatus.PENDING, 0, 20).content()).hasSize(2);
    assertThat(service.listGames(GameStatus.IN_PROGRESS, 0, 20).content()).hasSize(0);
}
```

No conserves stubs ni aserciones para `listGames(status)` o para los métodos de
repositorio sin `Pageable`.

En `src/test/java/es/daw2a/bshipalong/web/GameControllerTest.java`, añade:

```java
import es.daw2a.bshipalong.dto.PageResponseDTO;
```

Sustituye `listsGames` y `filtersByStatus` completos por:

```java
@Test
void listsGames() throws Exception {
    when(service.listGames(null, 0, 20)).thenReturn(
            new PageResponseDTO<>(java.util.List.of(pending(1L)),
                    0, 20, 1, 1, true, true));
    mvc.perform(get("/api/games"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content", hasSize(1)));
}

@Test
void filtersByStatus() throws Exception {
    when(service.listGames(GameStatus.IN_PROGRESS, 0, 20)).thenReturn(
            new PageResponseDTO<>(java.util.List.of(),
                    0, 20, 0, 0, true, true));
    mvc.perform(get("/api/games").param("status", "IN_PROGRESS"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content", hasSize(0)));
}
```

Conserva los demás métodos. Toda aserción del array raíz debe pasar a
`$.content`.

En `src/test/java/es/daw2a/bshipalong/web/GameHttpIntegrationTest.java`,
sustituye `createsPersistsAndRetrievesGame` por:

```java
@Test
void createsPersistsAndRetrievesGame() throws Exception {
    String location = mvc.perform(post("/api/games")
                    .contentType("application/json")
                    .content("{\"boardSize\": 10}"))
            .andExpect(status().isCreated())
            .andExpect(header().string("Location", startsWith("/api/games/")))
            .andExpect(jsonPath("$.status").value("PENDING"))
            .andReturn().getResponse().getHeader("Location");
    mvc.perform(get(location))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("PENDING"));
    mvc.perform(get("/api/games").param("status", "PENDING"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content", hasSize(1)))
            .andExpect(jsonPath("$.page").value(0))
            .andExpect(jsonPath("$.size").value(20));
}
```

Esta clase ya tiene todos los imports estáticos necesarios (`hasSize`,
`startsWith`, `post`, `get`, `status` y `jsonPath`).

En `src/test/java/es/daw2a/bshipalong/service/GameRulesTest.java`, dentro de
`cancellationKeepsHistoryAndHidesGame`, sustituye **solo** esta aserción:

```java
assertThat(service.listGames(null)).noneMatch(g -> g.id().equals(id));
```

por la llamada paginada y su contenido:

```java
assertThat(service.listGames(null, 0, 20).content())
        .noneMatch(g -> g.id().equals(id));
```

No agregues una sobrecarga de compatibilidad al servicio solo para hacer compilar
esta prueba: el objetivo es actualizar explícitamente sus consumidores. Esta
aserción comprueba la página solicitada; la prueba HTTP de esta guía también
comprueba la exclusión de inactivas con datos controlados.

Comandos secuenciales:

```bash
./mvnw -Dtest=GameServiceTest test
./mvnw -Dtest=GamePaginationHttpTest test
./mvnw -Dtest=GameHttpIntegrationTest,GameControllerTest test
./mvnw test
```

Ejecuta el primer comando solo después de actualizar todos los llamadores de
`listGames`; no intentes compilar el servicio entre cambios. Servicio,
controlador y tests deben quedar sincronizados antes de cualquier checkpoint
Maven.

## 7. Prueba manual con HTTPie

Con la aplicación arrancada (`./mvnw spring-boot:run`), en otra terminal:

```bash
http GET :8080/api/games
http GET :8080/api/games page==0 size==2
http GET :8080/api/games status==PENDING page==0 size==20
http GET :8080/api/games page==999 size==20
http GET :8080/api/games size==0
http GET :8080/api/games size==101
http GET :8080/api/games status==UNKNOWN
```

Las cuatro primeras peticiones son válidas; las tres últimas deben devolver
`400` y un objeto `ErrorPayload`. `sort=id,desc` no forma parte del contrato:
un cliente no debe enviarlo para cambiar el orden. Los clientes que consumían el
array deben migrar a `response.content`, guardar `page`, `size`, `totalPages` y
`last`, y tratar `content: []` como una respuesta válida, no como un error. Una
`page` mayor que `totalPages - 1` también es válida y devuelve una página vacía
con `200`.

El DTO completo conserva barcos y ataques. En la implementación actual construir
cada elemento consulta esas asociaciones; no es una afirmación universal sobre
coste o consultas de otras implementaciones. Consulta
[11: proyecciones y carga](11-proyecciones-y-carga-bshipalong.md).

## 8. Resultado y límites de esta guía

La clase anterior es código standalone para añadir en una copia del aula. Incluye
datos propios en cada método y `@Transactional`: la transacción revierte los
inserts y cancelaciones al terminar, pero es requisito comprobar que H2 no tiene
datos previos. Hasta ejecutar
los comandos anteriores en la copia del aula, no se debe afirmar que esta
cobertura adicional ha pasado.
