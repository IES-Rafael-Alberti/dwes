# Sesión 3 — Controladores REST y endpoints básicos

## Antes de empezar

Lee el documento [`03-controladores-rest.md`](../../01-documentacion/03-controladores-rest.md). Cubre:

- `@RestController` y `@RequestMapping`
- Verbos HTTP: `@GetMapping`, `@PostMapping`
- `@PathVariable`, `@RequestBody`
- `ResponseEntity` y códigos de estado
- DTOs de entrada/salida

## Code-along: endpoints para crear y obtener partidas

En la sesión anterior creamos la entidad `Game`. Ahora toca exponerla vía REST. Aplicamos TDD: escribimos el test del controlador antes de implementarlo.

### 1. Crear la migración Flyway

Primero necesitamos datos de prueba. Añadimos una migración que inserte una partida inicial:

`src/main/resources/db/migration/V2__seed_games.sql`

```sql
INSERT INTO games (status) VALUES ('PENDING');
```

### 2. Crear el repositorio JPA

Necesitamos acceder a la base de datos. Creamos un repositorio (por ahora lo usaremos directamente desde el controlador; en la sesión 4 extraeremos la capa de servicios).

```java
package com.example.battleship.repository;

import com.example.battleship.domain.Game;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GameRepository extends JpaRepository<Game, Long> {
}
```

Sin implementación — Spring Data JPA genera los métodos automáticamente.

### 3. TDD: test del controlador — RED

Creamos el test con `@WebMvcTest` para cargar solo la capa web, no todo el contexto:

```java
package com.example.battleship.web;

import com.example.battleship.repository.GameRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(GameController.class)
class GameControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private GameRepository gameRepository;

    @Test
    void getGameShouldReturn200() throws Exception {
        var game = new Game();
        game.setId(1L);

        when(gameRepository.findById(1L)).thenReturn(java.util.Optional.of(game));

        mockMvc.perform(get("/api/games/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.status").value("PENDING"));
    }
}
```

Ejecutar:

```bash
mvn test
```

Falla — `GameController` no existe. Rojo.

### 4. Implementar el controlador — GREEN

```java
package com.example.battleship.web;

import com.example.battleship.domain.Game;
import com.example.battleship.repository.GameRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/games")
public class GameController {

    private final GameRepository gameRepository;

    public GameController(GameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Game> getGame(@PathVariable Long id) {
        return gameRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
```

Señalar en clase:

- **Constructor injection**: el controller recibe el repositorio por constructor (no `@Autowired` directo)
- `ResponseEntity.ok()` vs `ResponseEntity.notFound().build()`: control preciso del código HTTP
- `@PathVariable` extrae el `{id}` de la URL

Ejecutar test → pasa. Verde.

### 5. Añadir POST para crear partidas

Aplicamos TDD otra vez. Primero el test:

```java
@Test
void createGameShouldReturn201() throws Exception {
    when(gameRepository.save(any())).thenAnswer(invocation -> {
        var g = invocation.<Game>getArgument(0);
        g.setId(99L);
        return g;
    });

    mockMvc.perform(post("/api/games")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{}"))
            .andExpect(status().isCreated())
            .andExpect(header().string("Location", "/api/games/99"))
            .andExpect(jsonPath("$.id").value(99))
            .andExpect(jsonPath("$.status").value("PENDING"));
}
```

Añadir imports necesarios (`post`, `contentType`, `MediaType`, `header`, `any`).

Luego el endpoint:

```java
@PostMapping
public ResponseEntity<Game> createGame() {
    var game = new Game();
    var saved = gameRepository.save(game);
    var location = URI.create("/api/games/" + saved.getId());
    return ResponseEntity.created(location).body(saved);
}
```

Señalar:

- `ResponseEntity.created(location)` devuelve **201 Created** con `Location` header
- Sin body en la request (creamos partida con valores por defecto → `PENDING`)

### 6. Query params: filtrar por estado (tiempo de refactor)

Añadimos un endpoint que permita filtrar partidas por estado. Primero el test:

```java
@Test
void listGamesShouldFilterByStatus() throws Exception {
    var pending = new Game();
    pending.setId(1L);
    var inProgress = new Game();
    inProgress.setId(2L);
    inProgress.setStatus(GameStatus.IN_PROGRESS);

    when(gameRepository.findAll()).thenReturn(List.of(pending, inProgress));

    mockMvc.perform(get("/api/games?status=PENDING"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(1))
            .andExpect(jsonPath("$[0].status").value("PENDING"));
}
```

Luego el endpoint:

```java
@GetMapping
public ResponseEntity<List<Game>> listGames(
        @RequestParam(required = false) GameStatus status) {

    if (status != null) {
        var filtered = gameRepository.findAll().stream()
                .filter(g -> g.getStatus() == status)
                .toList();
        return ResponseEntity.ok(filtered);
    }
    return ResponseEntity.ok(gameRepository.findAll());
}
```

Señalar:

- `@RequestParam(required = false)` — el parámetro no es obligatorio
- Si no se pasa `?status=...`, devuelve todas las partidas
- La lógica de filtrado vive en el controlador — en la sesión 4 la moveremos al servicio

Probar:

```bash
curl "http://localhost:8080/api/games"
curl "http://localhost:8080/api/games?status=PENDING"
curl "http://localhost:8080/api/games?status=IN_PROGRESS"
```

### 7. Bonus: el método HTTP QUERY

Cuando los filtros se vuelven complejos, `@RequestParam` se queda corto. Con GET no podés enviar body. Con POST podrías, pero POST no es seguro ni idempotente.

Ahí entra **QUERY** ([RFC 10008](https://www.rfc-editor.org/rfc/rfc10008), junio 2026): un método HTTP **safe** e **idempotent** como GET, pero que admite body como POST. Es el candidato perfecto para consultar el historial de ataques de una partida con filtros combinados.

#### Caso real en Battleship: historial de ataques

Hoy, `GET /api/games/{id}` devuelve la partida con todos sus ataques. Si querés filtrar (solo hits, por rango de fechas, paginado), necesitarías algo como:

```
GET /api/games/1/attacks?hit=true&dateFrom=2025-01-01&dateTo=2025-06-01&page=0&size=20&sort=desc
```

Esto es feo, frágil, y no escala a filtros anidados. Con QUERY:

```
QUERY /api/games/1/attacks HTTP/1.1
Content-Type: application/json

{
  "hit": true,
  "dateFrom": "2025-01-01",
  "dateTo": "2025-06-01",
  "page": 0,
  "size": 20,
  "sort": "desc"
}
```

#### Implementación

El problema: Spring MVC no soporta QUERY de serie — `RequestMethod` es un enum sin ese valor. La solución es `@RequestMapping` sin restricción de verbo y validación manual:

```java
@RestController
@RequestMapping("/api/games/{gameId}/attacks")
public class AttackQueryController {

    private final AttackRepository attackRepository;

    public AttackQueryController(AttackRepository attackRepository) {
        this.attackRepository = attackRepository;
    }

    @RequestMapping
    public ResponseEntity<?> queryAttacks(HttpServletRequest request,
                                           @PathVariable Long gameId,
                                           @RequestBody(required = false) AttackQueryDTO query) {
        if (!"QUERY".equals(request.getMethod())) {
            return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).build();
        }

        if (query == null) {
            return ResponseEntity.ok(attackRepository.findByGameId(gameId));
        }

        var all = attackRepository.findByGameId(gameId);
        var result = all.stream()
                .filter(a -> query.hit() == null || a.isHit() == query.hit())
                .filter(a -> query.dateFrom() == null
                        || !a.getCreatedAt().isBefore(query.dateFrom()))
                .filter(a -> query.dateTo() == null
                        || !a.getCreatedAt().isAfter(query.dateTo()))
                .toList();

        return ResponseEntity.ok(result);
    }
}
```

DTO:

```java
package com.example.battleship.dto;

import java.time.LocalDateTime;

public record AttackQueryDTO(
    Boolean hit,
    LocalDateTime dateFrom,
    LocalDateTime dateTo,
    Integer page,
    Integer size,
    String sort
) {}
```

Repositorio necesario (si no existe):

```java
package com.example.battleship.repository;

import com.example.battleship.domain.Attack;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AttackRepository extends JpaRepository<Attack, Long> {
    List<Attack> findByGameId(Long gameId);
}
```

> **¿Por qué funciona `@RequestMapping` sin `method`?** Al no especificar método, el mapping acepta cualquier verbo HTTP (GET, POST, QUERY...). Dentro validamos que sea QUERY y devolvemos 405 en caso contrario.

Probar:

```bash
curl -X QUERY http://localhost:8080/api/games/1/attacks \
  -H "Content-Type: application/json" \
  -d '{"hit": true, "dateFrom": "2025-01-01T00:00:00"}'
```

```bash
# GET a la misma URL — 405 Method Not Allowed
curl -i -X GET http://localhost:8080/api/games/1/attacks
```

Para depurar:

```yaml
logging:
  level:
    org.springframework.web: TRACE
```

#### Consumir QUERY desde JavaScript (Fetch API)

El verbo QUERY se envía como cualquier otro método con `fetch`. Importante: no es un método "simple" (CORS), por lo que el navegador hará una **preflight request** con `OPTIONS`. El servidor debe responder con `Access-Control-Allow-Methods: QUERY`.

```javascript
async function queryAttacks(gameId, filters) {
    const response = await fetch(`/api/games/${gameId}/attacks`, {
        method: 'QUERY',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(filters)
    });

    if (!response.ok) {
        throw new Error(`QUERY failed: ${response.status}`);
    }

    return response.json();
}

// Uso
const attacks = await queryAttacks(1, {
    hit: true,
    dateFrom: '2025-01-01T00:00:00',
    dateTo: '2025-06-01T00:00:00',
    sort: 'desc'
});
```

Si el backend añade CORS (lo veremos en la sesión 7):

```java
@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "QUERY"); // ← incluir QUERY
    }
}
```

> **Nota**: QUERY está registrado en el IANA HTTP Method Registry desde RFC 10008 (2026). Es un estándar reciente; el soporte depende del servidor (Tomcat lo acepta sin configuración adicional, y `fetch()` admite cualquier string como `method`). En clase lo vemos como concepto avanzado; para el proyecto usamos GET + `@RequestParam` por compatibilidad.

### 8. Probar con la aplicación real

```bash
mvn spring-boot:run
```

```bash
# Crear partida
curl -i -X POST http://localhost:8080/api/games

# Obtenerla por id (usar el id que devuelva el POST)
curl http://localhost:8080/api/games/1

# Pedir una que no existe
curl -i http://localhost:8080/api/games/999
```

## Lo que vimos hoy

| Concepto | Dónde se ve |
|----------|-------------|
| `@RestController` + `@RequestMapping` | Clase `GameController` |
| `@GetMapping`, `@PostMapping` | Métodos `getGame`, `createGame` |
| `@PathVariable` | `{id}` en la URL |
| `@RequestParam` | `?status=PENDING` en query string |
| Método HTTP QUERY | Historial de ataques con filtros (`QUERY /api/games/{id}/attacks`) |
| `ResponseEntity` | `ok()`, `created()`, `notFound()` |
| `@WebMvcTest` + `@MockitoBean` | Test del controlador |
| `jsonPath()` | Verificar campos del JSON de respuesta |
| Spring Data JPA repository | `GameRepository` sin implementación |

## Tarea — Mini-tasks v2

Sobre tu proyecto **mini-tasks** de la sesión anterior:

1. Crea un repositorio JPA para `Task`
2. Implementa un `TaskController` con:
   - `GET /api/tasks` — lista todas las tareas
   - `GET /api/tasks?done=true` — filtrar por estado (usando `@RequestParam`)
   - `GET /api/tasks/{id}` — obtener una por id
   - `POST /api/tasks` — crear tarea (body: `{"title": "..."}`)
3. Escribe los tests con `@WebMvcTest` mockeando el repositorio

Detalles en `../../03-ejercicios/01-mini-tasks/`.

> **Pista**: Para `GET /api/tasks` usá `gameRepository.findAll()` y devolvé la lista directamente. Spring Boot la serializa a JSON automáticamente.
