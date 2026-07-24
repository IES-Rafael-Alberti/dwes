# Sesión 4 — Capa de servicios y DTOs

## Antes de empezar

Lee el documento [`04-capa-servicios.md`](../../../01-documentacion/04-capa-servicios.md). Cubre:

- `@Service` y el rol de la capa de negocio
- DTOs como registros inmutables
- `@Transactional` para operaciones atómicas
- Excepciones personalizadas

## El problema

En la sesión 3, el controlador accedía a `GameRepository` directamente. Esto tiene varios problemas:

- **Mezcla responsabilidades**: el controlador decide qué filtrar, cómo validar, cómo responder
- **Lógica de negocio no testeable**: no podés probar las reglas de Battleship sin MockMvc
- **Código duplicado**: si otro endpoint necesita la misma lógica, la reescribes

La solución: extraer la lógica a un **servicio** y usar **DTOs** para separar la API de las entidades JPA.

## Code-along: refactor a capa de servicios

Partimos del controlador de la sesión 3. Vamos a extraer la lógica paso a paso.

### 1. Antes: el controlador monolítico

```java
@RestController
@RequestMapping("/api/games")
public class GameController {

    private final GameRepository gameRepository;  // ← acceso directo a BD

    @GetMapping("/{id}")
    public ResponseEntity<Game> getGame(@PathVariable Long id) {
        return gameRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    // ...
}
```

La entidad `Game` se expone directamente al cliente. Si cambiás JPA, cambiás la API.

### 2. Crear los DTOs como records

Separamos el contrato público de la implementación interna:

```java
package com.example.battleship.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public record CreateGameDTO(
    @Min(5) @Max(20) int boardSize
) {}
```

```java
package com.example.battleship.dto;

import java.util.List;

public record GameResponseDTO(
    Long id,
    int boardSize,
    String status,
    LocalDateTime createdAt,
    List<ShipDTO> ships,
    List<AttackDTO> attacks
) {
    public record ShipDTO(Long id, String shipName, int length,
                          int startX, int startY, boolean isHorizontal, boolean sunk) {}
    public record AttackDTO(Long id, int x, int y, boolean hit, LocalDateTime createdAt) {}
}
```

Señalar en clase:

- Los records son inmutables, tienen constructor, `equals`, `hashCode`, `toString` gratis
- `@Min`/`@Max` en `CreateGameDTO` para validar el tamaño del tablero (5x5 mínimo, 20x20 máximo)
- `GameResponseDTO` expone solo lo que el cliente necesita, no la entidad JPA

### 3. Crear el servicio

```java
package com.example.battleship.service;

import com.example.battleship.domain.Game;
import com.example.battleship.dto.CreateGameDTO;
import com.example.battleship.dto.GameResponseDTO;
import com.example.battleship.repository.GameRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class GameService {

    private final GameRepository gameRepo;

    public GameService(GameRepository gameRepo) {
        this.gameRepo = gameRepo;
    }

    @Transactional(readOnly = true)
    public List<GameResponseDTO> listGames() {
        return gameRepo.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public GameResponseDTO getGame(Long id) {
        Game game = gameRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Game not found"));
        return toResponse(game);
    }

    public GameResponseDTO createGame(CreateGameDTO dto) {
        Game game = Game.builder()
                .boardSize(dto.boardSize())
                .status("PENDING")
                .build();
        game = gameRepo.save(game);
        return toResponse(game);
    }

    private GameResponseDTO toResponse(Game game) {
        return new GameResponseDTO(
                game.getId(), game.getBoardSize(), game.getStatus(),
                game.getCreatedAt(), List.of(), List.of()
        );
    }
}
```

Señalar:

- `@Service` — estereotipo de Spring, mismo efecto que `@Component` pero semántico
- `@Transactional` — toda la clase opera dentro de una transacción
- `@Transactional(readOnly = true)` — optimización para consultas (Hibernate no necesita flush)
- Constructor injection — sin `@Autowired`
- El servicio lanza `IllegalArgumentException` si no encuentra la partida — el controlador decidirá el código HTTP

### 4. Refactorizar el controlador

```java
@RestController
@RequestMapping("/api/games")
public class GameController {

    private final GameService gameService;  // ← ahora inyecta el servicio

    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<GameResponseDTO> getGame(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(gameService.getGame(id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<GameResponseDTO> createGame(@Valid @RequestBody CreateGameDTO dto) {
        var game = gameService.createGame(dto);
        return ResponseEntity.created(URI.create("/api/games/" + game.id())).body(game);
    }

    @GetMapping
    public List<GameResponseDTO> listGames() {
        return gameService.listGames();
    }
}
```

Señalar:

- El controlador queda **delgado**: solo maneja HTTP y delega al servicio
- El servicio contiene la **lógica de negocio**: validación, transformación, reglas
- `@Valid` activa la validación de `@Min`/`@Max` en el DTO
- El controlador decide el código HTTP; el servicio solo lanza excepciones

### 5. Test del servicio con @SpringBootTest

```java
package com.example.battleship.service;

import com.example.battleship.dto.CreateGameDTO;
import com.example.battleship.dto.GameResponseDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class GameServiceTest {

    @Autowired
    private GameService gameService;

    @Test
    void createAndRetrieveGame() {
        var dto = new CreateGameDTO(10);
        var created = gameService.createGame(dto);
        assertThat(created.id()).isNotNull();
        assertThat(created.boardSize()).isEqualTo(10);
        assertThat(created.status()).isEqualTo("PENDING");

        var retrieved = gameService.getGame(created.id());
        assertThat(retrieved.id()).isEqualTo(created.id());
    }

    @Test
    void listGamesReturnsAll() {
        gameService.createGame(new CreateGameDTO(5));
        gameService.createGame(new CreateGameDTO(8));
        assertThat(gameService.listGames()).hasSize(2);
    }
}
```

> **Nota**: `@SpringBootTest` carga el contexto completo. En sesiones posteriores veremos test slicing con `@DataJpaTest` para tests más rápidos.

### 6. ¿Qué pasa con la lógica de Battleship?

Hoy extrajimos solo CRUD. La lógica real de Battleship (colocar barcos, disparar, detectar hundidos, fin de partida) también vive en `GameService`. El patrón es el mismo:

| Responsabilidad | Antes (sesión 3) | Ahora (sesión 4) |
|---|---|---|
| Recibir petición HTTP | `GameController` | `GameController` (delgado) |
| Validar reglas de negocio | En el controlador, mezclado | `GameService` |
| Crear/colocar barcos | No existía | `GameService.placeShip()` |
| Procesar disparo | No existía | `GameService.attack()` |
| Detectar hundidos | No existía | `GameService.checkSunkShips()` |
| Mapear entidad → DTO | No existía | `GameService.toResponse()` |

En sesiones posteriores iremos añadiendo cada operación con TDD.

## Lo que vimos hoy

| Concepto | Dónde se ve |
|----------|-------------|
| `@Service` | Clase `GameService` |
| `@Transactional` y readOnly | Métodos del servicio |
| Constructor injection | Sin `@Autowired` |
| DTOs como records | `CreateGameDTO`, `GameResponseDTO` |
| `@Valid` + validación | `@Min`/`@Max` en DTOs |
| `@SpringBootTest` | Test de integración del servicio |
| Controlador delgado + servicio gordo | Arquitectura por capas |

## Tarea — Book Catalog, Entrega 4

Sobre tu proyecto **book-catalog** (o continuando las entregas anteriores):

1. Crea un DTO `BookRequestDTO` y `BookResponseDTO` como records
2. Crea un `BookService` con `@Service` y `@Transactional`
3. Refactoriza `BookController` para que use el servicio en lugar de acceder al repositorio directamente
4. Añade validación: `@NotBlank` en `title`, `@NotEmpty` en `isbn`
5. Escribe un test `@SpringBootTest` para el servicio

Detalles en `../../03-ejercicios/02-book-catalog/README.md` (entrega 4: Servicio).

> **Pista**: El servicio debería tener al menos `list()`, `getById(id)`, `create(dto)`. El controlador solo mapea HTTP y delega.
