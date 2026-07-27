# Sesión 5 — Manejo de errores con @ControllerAdvice

## Antes de empezar

Lee el documento [`05-tdd-manejo-errores.md`](../../../01-documentacion/05-tdd-manejo-errores.md). Cubre:

- `@ControllerAdvice` y `@ExceptionHandler`
- Payload de error uniforme
- Códigos HTTP apropiados para cada error

## El problema

En la sesión 4, el controlador usa `try-catch` para capturar excepciones del servicio:

```java
@GetMapping("/{id}")
public ResponseEntity<GameResponseDTO> getGame(@PathVariable Long id) {
    try {
        return ResponseEntity.ok(gameService.getGame(id));
    } catch (IllegalArgumentException e) {
        return ResponseEntity.notFound().build();
    }
}
```

Esto tiene dos problemas:
- **Código repetitivo**: cada método necesita su `try-catch`
- **Respuesta inconsistente**: un error de validación devuelve `Map.of(...)` aquí, otro allá

La solución: `@ControllerAdvice` — un único punto que captura todas las excepciones y devuelve JSON consistente.

## Contrato de errores vigente

La forma canónica es `Error(error,message,timestamp)` y está definida en
`src/main/resources/static/api-docs/battleship-v1.yaml`, no generada desde las
anotaciones Java. El contrato declara, según la operación, `400` para entrada o
validación inválida, `401` para credenciales ausentes o inválidas, `403` para
rol insuficiente, `404` para partida inexistente, `409` para conflicto de
estado, `429` para rate limiting y `500` para un fallo inesperado.

`OpenApiConformanceIntegrationTest` valida respuestas MockMvc reales contra
esas variantes, incluidas muestras de `400`, `404` y `409`. Los tests de
conformidad comprueban la forma HTTP; los tests específicos del handler y de
seguridad siguen comprobando mensajes y ramas concretas.

## Code-along: manejo centralizado de errores

### 1. TDD: test del error handler

Primero probamos que un `GET` a una partida que no existe devuelve el JSON esperado:

```java
@WebMvcTest(GameController.class)
class GameControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private GameService gameService;

    @Test
    void getNonExistentGameReturns404() throws Exception {
        when(gameService.getGame(999L))
                .thenThrow(new IllegalArgumentException("Game not found"));

        mockMvc.perform(get("/api/games/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("Game not found"))
                .andExpect(jsonPath("$.timestamp").isNotEmpty());
    }
}
```

Ejecutar → falla porque no existe el `GlobalExceptionHandler` con ese formato. Rojo.

### 2. Crear el payload de error como record

```java
package com.example.battleship.dto;

import java.time.Instant;

public record ErrorPayload(
    String error,
    String message,
    String timestamp
) {
    public ErrorPayload(String error, String message) {
        this(error, message, Instant.now().toString());
    }
}
```

Señalar: constructor compacto que genera el timestamp automáticamente.

### 3. Crear el GlobalExceptionHandler

```java
package com.example.battleship.web;

import com.example.battleship.dto.ErrorPayload;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorPayload> handleNotFound(IllegalArgumentException ex) {
        var payload = new ErrorPayload("NOT_FOUND", ex.getMessage());
        return ResponseEntity.status(404).body(payload);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorPayload> handleConflict(IllegalStateException ex) {
        var payload = new ErrorPayload("CONFLICT", ex.getMessage());
        return ResponseEntity.status(409).body(payload);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorPayload> handleValidation(MethodArgumentNotValidException ex) {
        String msg = ex.getBindingResult().getAllErrors().get(0).getDefaultMessage();
        var payload = new ErrorPayload("VALIDATION_ERROR", msg);
        return ResponseEntity.badRequest().body(payload);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorPayload> handleGeneric(Exception ex) {
        var payload = new ErrorPayload("INTERNAL_ERROR", "Unexpected error");
        return ResponseEntity.status(500).body(payload);
    }
}
```

Señalar en clase:

- `@ControllerAdvice` — escucha excepciones de **todos** los controladores
- Orden de los handlers: Spring elige el más específico primero
- El handler genérico `Exception.class` es el **seguro de vida** — nunca dejes que Spring devuelva un stack trace
- No exponer `ex.getMessage()` en errores genéricos (seguridad)

### 4. Refactorizar el controlador

Ahora que `@ControllerAdvice` gestiona los errores, el controlador puede delegar sin `try-catch`:

```java
@RestController
@RequestMapping("/api/games")
public class GameController {

    private final GameService gameService;

    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<GameResponseDTO> getGame(@PathVariable Long id) {
        return ResponseEntity.ok(gameService.getGame(id));
        // Si lanza IllegalArgumentException → @ControllerAdvice responde 404
    }

    @PostMapping
    public ResponseEntity<GameResponseDTO> createGame(@Valid @RequestBody CreateGameDTO dto) {
        var game = gameService.createGame(dto);
        return ResponseEntity.created(URI.create("/api/games/" + game.id())).body(game);
        // Si @Valid falla → MethodArgumentNotValidException → 400
    }

    @GetMapping
    public List<GameResponseDTO> listGames() {
        return gameService.listGames();
    }
}
```

Señalar: el controlador ya no tiene `try-catch`. Cada capa hace su trabajo:
- **Controlador**: solo HTTP (verbos, rutas, códigos de estado)
- **Servicio**: lógica de negocio y validación
- **@ControllerAdvice**: formatea los errores

### 5. Probar la consistencia

```bash
# Partida que no existe
curl -i http://localhost:8080/api/games/999
# → 404 { "error": "NOT_FOUND", "message": "Game not found", ... }

# Crear partida con datos inválidos
curl -i -X POST http://localhost:8080/api/games \
  -H "Content-Type: application/json" \
  -d '{"boardSize": 1}'
# → 400 { "error": "VALIDATION_ERROR", "message": "must be at least 5", ... }

# Error interno (lanzar excepción no controlada en el servicio)
# → 500 { "error": "INTERNAL_ERROR", "message": "Unexpected error", ... }
```

## Lo que vimos hoy

| Concepto | Dónde se ve |
|----------|-------------|
| `@ControllerAdvice` | Clase `GlobalExceptionHandler` |
| `@ExceptionHandler` | Métodos para cada tipo de excepción |
| `ErrorPayload` record | DTO unificado de error |
| Códigos HTTP en errores | 400, 404, 409, 500 |
| Controller sin try-catch | `GameController` limpio |
| `MethodArgumentNotValidException` | Errores de `@Valid` |

## Tarea — Book Catalog, Entregas 5 y 6

Sobre tu proyecto **book-catalog**:

**Entrega 5** — Excepciones:
1. Define `BookNotFoundException` que extiende `RuntimeException`
2. Lánzala desde `BookService` cuando no encuentre un libro
3. Añade un handler en `@ControllerAdvice` que devuelva 404

**Entrega 6** — Global handler:
1. Crea un `ErrorPayload` record como hicimos en clase
2. Implementa `@RestControllerAdvice` global
3. Añade handler para `MethodArgumentNotValidException`
4. Añade handler genérico para `Exception`

Detalles en `../../03-ejercicios/02-book-catalog/README.md`.
