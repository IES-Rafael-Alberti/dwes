# Sesión 4b — Reglas de negocio: más allá del CRUD

> Esta sesión se hace DESPUÉS de la 4 (capa de servicios + DTOs) y ANTES de la 5 (manejo de errores). Sin reglas de negocio sólidas, no hay errores que merezca la pena manejar.

## Antes de empezar

Domina estos conceptos de la sesión 4 antes de entrar aquí:

- `@Service`, `@Transactional`
- Inyección por constructor
- DTOs como records
- `@SpringBootTest`

## ¿Qué es una regla de negocio?

**No es** validar que un campo no esté vacío. Eso es validación de datos, y lo hace `@Valid`.

**Regla de negocio** es una decisión o restricción que viene del dominio del problema, no de la tecnología. Ejemplos reales:

| CRUD (mal) | Regla de negocio (bien) |
|---|---|
| `DELETE /empleados/{id}` → borrar fila | Un empleado que se va **no se borra**. Se desactiva, se archiva, se le asigna un end-date. Pero la fila sigue existiendo porque hay nóminas, contratos, informes que la referencian. |
| `DELETE /articulos/{id}` → borrar fila | Un artículo que se descataloga **sigue en la BD** porque hay pedidos, facturas y devoluciones que lo referencian. Se marca como `INACTIVE`, `DISCONTINUED` o similar. |
| `POST /api/pedidos` → `INSERT INTO pedidos` | Antes de crear un pedido hay que comprobar: ¿el cliente existe y no está moroso? ¿el artículo tiene stock? ¿el carrito no está vacío? |
| `PUT /api/partidas/{id}` → `UPDATE` | No puedes cambiar el estado de una partida de PENDING a WON sin pasar por IN_PROGRESS. Eso es una **máquina de estados**. |

**El CRUD es la infraestructura. Las reglas de negocio son el software.**

## Code-along: reglas de negocio de Battleship

Vamos a implementar las reglas que diferencian Battleship de un mero `INSERT INTO attacks`.

### 1. Máquina de estados del juego

Un `Game` pasa por estados definidos. No todos los saltos son válidos:

```
PENDING ──(colocar barcos)──→ IN_PROGRESS ──(hundir todos)──→ WON
                                  ↑                            |
                                  └──── (no cabe) ────────────-┘
```

La regla: no se puede pasar de PENDING a WON sin tener barcos colocados y disparos hechos. El servicio **no expone un `setStatus()`**.

```java
public enum GameStatus {
    PENDING, IN_PROGRESS, WON
}
```

TDD primero:

```java
@SpringBootTest
class GameServiceBusinessRulesTest {

    @Autowired
    private GameService gameService;

    @Test
    void cannotAttackBeforePlacingShips() {
        var game = gameService.createGame(new CreateGameDTO(10));
        // No colocamos barcos, intentamos atacar directamente
        assertThatThrownBy(() ->
            gameService.attack(game.id(), new AttackDTO(0, 0)))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("No ships placed");
    }
}
```

Rojo. Implementamos:

```java
public GameResponseDTO attack(Long gameId, AttackDTO dto) {
    Game game = gameRepo.findById(gameId)
            .orElseThrow(() -> new IllegalArgumentException("Game not found"));

    // Regla 1: solo se puede atacar si hay barcos colocados
    List<Ship> ships = shipRepo.findByGameId(gameId);
    if (ships.isEmpty()) {
        throw new IllegalStateException("No ships placed. Place ships before attacking.");
    }

    // ... resto de la lógica
}
```

### 2. Soft delete: no borramos, archivamos

En producción **nunca** se hace `DELETE FROM games WHERE id = ?`. Los datos tienen valor histórico. En su lugar:

```java
// En Game.java
@Column(nullable = false)
private boolean active = true;

@Column
private LocalDateTime cancelledAt;
```

```java
public void cancel() {
    if (!"PENDING".equals(this.status)) {
        throw new IllegalStateException("Only pending games can be cancelled");
    }
    this.status = "CANCELLED";
    this.active = false;
    this.cancelledAt = LocalDateTime.now();
}
```

El repositorio solo devuelve juegos activos por defecto:

```java
public interface GameRepository extends JpaRepository<Game, Long> {
    @Query("SELECT g FROM Game g WHERE g.active = true")
    List<Game> findAllActive();
}
```

O mejor, añadimos `@Where` de Hibernate (pero ojo: es Hibernate-specific). O usamos un `@Filter`. O simplemente lo hacemos explícito en el servicio.

La regla de negocio: **un juego cancelado no aparece en las listas, pero sus datos persisten**.

TDD:

```java
@Test
void cancelledGameDoesNotAppearInList() {
    var game = gameService.createGame(new CreateGameDTO(10));
    gameService.cancelGame(game.id());

    assertThat(gameService.listGames())
            .noneMatch(g -> g.id().equals(game.id()));
}
```

### 3. Los barcos no se solapan

Cuando un jugador coloca un barco, hay que verificar que no ocupe el mismo espacio que otro ya colocado:

```java
// En GameService
private void validateNoOverlap(List<Ship> existingShips, PlaceShipDTO newShip) {
    for (Ship existing : existingShips) {
        if (rectanglesOverlap(
                existing.getStartX(), existing.getStartY(),
                existing.isHorizontal() ? existing.getStartX() + existing.getLength() - 1 : existing.getStartX(),
                existing.isHorizontal() ? existing.getStartY() : existing.getStartY() + existing.getLength() - 1,
                newShip.startX(), newShip.startY(),
                newShip.isHorizontal() ? newShip.startX() + newShip.length() - 1 : newShip.startX(),
                newShip.isHorizontal() ? newShip.startY() : newShip.startY() + newShip.length() - 1
        )) {
            throw new IllegalArgumentException("Ships cannot overlap");
        }
    }
}

private boolean rectanglesOverlap(int ax1, int ay1, int ax2, int ay2,
                                   int bx1, int by1, int bx2, int by2) {
    return ax1 <= bx2 && ax2 >= bx1 && ay1 <= by2 && ay2 >= by1;
}
```

### 4. No se puede atacar dos veces la misma coordenada

```java
// En GameService
public GameResponseDTO attack(Long gameId, AttackDTO dto) {
    // ... validaciones previas ...

    // Regla: no repetir coordenadas
    if (attackRepo.existsByGameIdAndXAndY(gameId, dto.x(), dto.y())) {
        throw new IllegalArgumentException("Position already attacked");
    }

    // ... resto ...
}
```

### 5. Detección automática de hundidos

Al disparar, hay que comprobar si ese impacto hunde un barco entero:

```java
private void checkSunkShips(Game game, List<Ship> ships) {
    List<Attack> attacks = attackRepo.findByGameId(game.getId());
    for (Ship ship : ships) {
        if (ship.isSunk()) continue;
        if (allPositionsHit(ship, attacks)) {
            ship.setSunk(true);
            shipRepo.save(ship);
        }
    }
}

private boolean allPositionsHit(Ship ship, List<Attack> attacks) {
    for (int i = 0; i < ship.getLength(); i++) {
        int x = ship.isHorizontal() ? ship.getStartX() + i : ship.getStartX();
        int y = ship.isHorizontal() ? ship.getStartY() : ship.getStartY() + i;
        boolean hit = attacks.stream()
                .anyMatch(a -> a.getX() == x && a.getY() == y && a.isHit());
        if (!hit) return false;
    }
    return true;
}
```

### 6. Fin de partida automático

Cuando todos los barcos están hundidos, la partida termina:

```java
private void checkGameOver(Game game, List<Ship> ships) {
    boolean allSunk = ships.stream().allMatch(Ship::isSunk);
    if (allSunk && !ships.isEmpty()) {
        game.setStatus("WON");
        gameRepo.save(game);
    }
}
```

## La pirámide de responsabilidades

```
              ┌──────────────────────────┐
              │   Controlador (HTTP)     │  ← delgado
              ├──────────────────────────┤
              │  Reglas de negocio       │  ← gordo, esto es el software
              │  - máquina de estados    │
              │  - validaciones de fondo │
              │  - soft deletes          │
              │  - eventos/auditoría     │
              ├──────────────────────────┤
              │  Repositorio (BD)        │  ← Spring Data JPA
              └──────────────────────────┘
```

El servicio es la capa más importante. Si un proyecto solo tiene CRUD, el servicio es una simple tubería del controlador al repositorio — y eso no es software, es una fachada.

## Lo que vimos hoy

| Regla | Implementación |
|-------|---------------|
| Máquina de estados | `GameStatus` — no se puede saltar estados |
| Soft delete | `active + cancelledAt` en entidad, filtro en queries |
| Barcos sin solapar | Validación geométrica de rectángulos |
| Sin ataques repetidos | `existsByGameIdAndXAndY()` |
| Hundimiento automático | `checkSunkShips()` al atacar |
| Fin de partida | `checkGameOver()` cuando todos hundidos |
| No atacar sin barcos | `IllegalStateException` si no hay ships |

## Tarea — Reglas de negocio en book-catalog

En tu proyecto **book-catalog**, implementa estas reglas en `BookService`:

1. **Soft delete**: un libro que se "elimina" se marca como `active = false`. No se borra de la BD.
   - `GET /api/books` solo devuelve activos
   - `GET /api/books?includeInactive=true` devuelve todos

2. **No ISBN duplicados**: no se puede crear un libro con un ISBN que ya existe (activo o inactivo).

3. **Mínimo de autores**: un libro no puede quedarse sin autores al editarlo (si aplica tu modelo).

4. **Estado del libro**: `AVAILABLE`, `BORROWED`, `DAMAGED`. Solo se puede prestar si está `AVAILABLE`. Solo se puede devolver si está `BORROWED`.

Escribe tests TDD para cada regla.

> **Pista**: El soft delete es la regla más sencilla y la que más valor real aporta. Empezá por ahí.
