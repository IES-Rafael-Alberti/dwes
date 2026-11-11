# Battleship: integridad y actualización de barcos

Este recorrido continúa la construcción de **`bshipAlong`**, el proyecto de
aula. Partimos de [asociaciones JPA en bshipAlong](06-asociaciones-jpa-bshipalong.md):
`Ship.game`, `Attack.game`, `Game.ships` y **45 pruebas verdes**. La aplicación
de aula utiliza H2 en este tramo; no presupongas que ya se haya cambiado a
PostgreSQL.

Antes de empezar, ejecuta `./mvnw test` desde la raíz de `bshipAlong`. Si no
obtienes el punto de partida esperado, consulta el recorrido anterior antes de
añadir nuevas funcionalidades.

Aplicaremos restricciones con Flyway y añadiremos PUT y PATCH para editar un
barco antes del primer ataque. Al terminar, la suite esperada tiene **65 pruebas
verdes**. El apunte [integridad, migraciones y pruebas](../../../../01-documentacion/04-persistencia/bloque-a/02-integridad-migraciones-y-pruebas.md)
explica los conceptos con más detalle.

## 2. Contrato que se fija antes de codificar

### 2.1. PUT

```http
PUT /api/games/{gameId}/ships/{shipId}
Content-Type: application/json

{"shipName":"B","length":3,"startX":2,"startY":2,"isHorizontal":false}
```

- Reemplaza **toda** la configuración editable.
- Todos los campos son obligatorios; si falta uno, 400 de validación.
- El DTO solo contiene los campos editables; `id`, `gameId`, `sunk` y los datos
  de ataques no modifican esos valores. Este ejemplo no comprueba que PUT rechace
  explícitamente propiedades JSON adicionales.
- Respuesta correcta: **200** con `GameResponseDTO` actualizado.
- Barco o partida inexistente: **404**.
- Partida ganada o con ataques: **409**. Una partida cancelada está inactiva
  y `findGame` la oculta: **404**.
- Configuración inválida, solapamiento o nombre repetido: **400**.

### 2.2. PATCH

```http
PATCH /api/games/{gameId}/ships/{shipId}
Content-Type: application/json

{"startY":3}
```

- Solo modifica los campos presentes.
- Campo **ausente**: se conserva el valor actual.
- Campo presente con `null`: **400**, porque los campos editables no admiten nulo.
- Campo presente con valor: se valida el resultado completo antes de escribir.
- Campo desconocido (`id`, `gameId`, `sunk`, `hit`…): **400**.
- Objeto vacío: **400**; no es un PATCH silencioso.
- Respuesta correcta: **200** con `GameResponseDTO`.
- Mismos 404/409 que PUT.

Esta distinción ausente/nulo/valor es la razón de recibir `JsonNode` en el
controlador. Un `record` con campos nullable convertiría ausente y `null` en el
mismo valor Java y perdería el contrato.

## 3. Integridad y migración

### 3.1. Migración V4

Crear `src/main/resources/db/migration/V4__add_game_integrity_constraints.sql`:

```sql
ALTER TABLE ship
    ADD CONSTRAINT uk_ship_game_name UNIQUE (game_id, ship_name);

ALTER TABLE attack
    ADD CONSTRAINT uk_attack_game_point UNIQUE (game_id, x, y);
```

No editar V1, V2 ni V3. Si el aula ya tuviera una V4, usar la siguiente versión
libre; no renombrar una migración ya aplicada.

Antes de aplicarla en una base con datos, comprobar duplicados:

```sql
SELECT game_id, ship_name, COUNT(*) AS repeticiones
FROM ship
GROUP BY game_id, ship_name
HAVING COUNT(*) > 1;

SELECT game_id, x, y, COUNT(*) AS repeticiones
FROM attack
GROUP BY game_id, x, y
HAVING COUNT(*) > 1;
```

Si aparecen filas, hay que decidir qué hacer con esos datos antes de añadir la
restricción. No borrar automáticamente para que la migración «pase».

### 3.2. Coherencia en las entidades

Las restricciones reales viven en Flyway. En JPA se declaran para que el mapeo
describa la misma regla. `Ship` queda:

```java
package es.daw2a.bshipalong.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "ship", uniqueConstraints = {
        @UniqueConstraint(name = "uk_ship_game_name",
                columnNames = {"game_id", "ship_name"})
})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Ship {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "game_id", nullable = false)
    private Game game;
    @Column(name = "ship_name", nullable = false, length = 50)
    private String shipName;
    @Column(nullable = false)
    private int length;
    @Column(name = "start_x", nullable = false)
    private int startX;
    @Column(name = "start_y", nullable = false)
    private int startY;
    @Column(name = "is_horizontal", nullable = false)
    private boolean isHorizontal;
    @Column(nullable = false)
    private boolean sunk;
}
```

`Attack` recibe la restricción de la coordenada:

```java
package es.daw2a.bshipalong.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "attack", uniqueConstraints = {
        @UniqueConstraint(name = "uk_attack_game_point",
                columnNames = {"game_id", "x", "y"})
})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Attack {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "game_id", nullable = false)
    private Game game;
    @Column(nullable = false)
    private int x;
    @Column(nullable = false)
    private int y;
    @Column(nullable = false)
    private boolean hit;
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    void onCreate() {
        if (createdAt == null) createdAt = LocalDateTime.now();
    }
}
```

`columnNames` usa nombres de **columnas** (`game_id`, `ship_name`), no nombres de
atributos Java. Es lo contrario de `mappedBy`, que nombra un campo Java.

Con Flyway y `ddl-auto: validate`, la anotación no crea por sí sola la restricción
en una tabla ya existente: la garantía real es la migración.

### 3.3. Pruebas de restricción contra la BD

Añadir cuatro casos a `GamePersistenceTest`. El archivo completo queda:

```java
package es.daw2a.bshipalong.domain;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class GamePersistenceTest {
    @Autowired EntityManager em;

    private Game game() {
        Game game = new Game(8);
        em.persist(game);
        return game;
    }

    private Ship ship(Game game, String name) {
        Ship ship = new Ship();
        ship.setShipName(name);
        ship.setLength(1);
        ship.setStartX(0);
        ship.setStartY(0);
        ship.setHorizontal(true);
        ship.setSunk(false);
        game.addShip(ship);
        return ship;
    }

    private Attack attack(Game game, int x, int y) {
        Attack attack = new Attack();
        attack.setGame(game);
        attack.setX(x);
        attack.setY(y);
        attack.setHit(false);
        return attack;
    }

    @Test void relationSurvivesFlushAndClear() {
        Game game = game();
        Ship ship = ship(game, "A");
        em.persist(ship);
        em.flush();
        Long gameId = game.getId();
        Long shipId = ship.getId();
        em.clear();

        Ship loaded = em.find(Ship.class, shipId);
        assertThat(loaded.getGame().getId()).isEqualTo(gameId);
        assertThat(em.find(Game.class, gameId).getShips())
                .extracting(Ship::getShipName).containsExactly("A");
    }

    @Test void cancellingKeepsRelatedRows() {
        Game game = game();
        Ship ship = ship(game, "A");
        em.persist(ship);
        Attack attack = attack(game, 0, 0);
        em.persist(attack);
        em.flush();
        Long gameId = game.getId();
        Long shipId = ship.getId();
        Long attackId = attack.getId();

        game.cancel();
        em.flush();
        em.clear();

        assertThat(em.find(Game.class, gameId).isActive()).isFalse();
        assertThat(em.find(Ship.class, shipId)).isNotNull();
        assertThat(em.find(Attack.class, attackId)).isNotNull();
    }

    @Test void repeatedNameInSameGameIsRejected() {
        Game game = game();
        em.persist(ship(game, "A"));
        em.flush();

        assertThatThrownBy(() -> {
            em.persist(ship(game, "A"));
            em.flush();
        }).isInstanceOf(jakarta.persistence.PersistenceException.class);
    }

    @Test void sameNameInDifferentGamesIsAllowed() {
        em.persist(ship(game(), "A"));
        em.persist(ship(game(), "A"));
        em.flush();
        em.clear();

        Long count = em.createQuery(
                "select count(s) from Ship s where s.shipName = :name", Long.class)
                .setParameter("name", "A").getSingleResult();
        assertThat(count).isEqualTo(2L);
    }

    @Test void repeatedPointInSameGameIsRejected() {
        Game game = game();
        em.persist(attack(game, 2, 3));
        em.flush();

        assertThatThrownBy(() -> {
            em.persist(attack(game, 2, 3));
            em.flush();
        }).isInstanceOf(jakarta.persistence.PersistenceException.class);
    }

    @Test void samePointInDifferentGamesIsAllowed() {
        em.persist(attack(game(), 2, 3));
        em.persist(attack(game(), 2, 3));
        em.flush();
        em.clear();

        Long count = em.createQuery(
                "select count(a) from Attack a where a.x = :x and a.y = :y", Long.class)
                .setParameter("x", 2).setParameter("y", 3).getSingleResult();
        assertThat(count).isEqualTo(2L);
    }
}
```

Los avisos de H2 sobre `UK_SHIP_GAME_NAME` y `UK_ATTACK_GAME_POINT` en las
pruebas negativas son esperados: se está comprobando precisamente que la BD
rechaza el duplicado. Un `WARN` de violación de unicidad dentro de
`assertThatThrownBy` no es un test fallido.

### 3.4. Control intermedio

```bash
./mvnw -Dtest=GamePersistenceTest,SchemaEvolutionTest test
```

Debe pasar. `SchemaEvolutionTest` migra desde V1 y comprueba que los datos
anteriores se conservan al llegar a V4.

## 4. Actualización del barco

### 4.1. Excepción y error 404 de barco

Crear `domain/exceptions/ShipNotFoundException.java`:

```java
package es.daw2a.bshipalong.domain.exceptions;

public class ShipNotFoundException extends RuntimeException {
    public ShipNotFoundException(Long id) {
        super("Ship not found: " + id);
    }
}
```

Añadir al `GlobalExceptionHandler`:

```java
import es.daw2a.bshipalong.domain.exceptions.ShipNotFoundException;

@ExceptionHandler(ShipNotFoundException.class)
public ResponseEntity<ErrorPayload> missingShip(ShipNotFoundException ex) {
    return ResponseEntity.status(404).body(new ErrorPayload("NOT_FOUND", ex.getMessage()));
}
```

Así un barco que no existe o que no pertenece a la partida no se confunde con un
error interno ni con un conflicto de estado.

### 4.2. PUT en el controlador

**Antes del siguiente control**, incorpora también `patchShip` y sus helpers
del apartado 4.6. Los métodos del controlador y las pruebas los necesitan para
compilar; Maven compila todas las pruebas aunque ejecutes una sola clase.

Añadir a `GameController`:

```java
import tools.jackson.databind.JsonNode;

@PutMapping("/{id}/ships/{shipId}")
public ResponseEntity<GameResponseDTO> replaceShip(@PathVariable("id") Long id,
                                                   @PathVariable("shipId") Long shipId,
                                                   @Valid @RequestBody PlaceShipDTO dto) {
    return ResponseEntity.ok(service.replaceShip(id, shipId, dto));
}

@PatchMapping("/{id}/ships/{shipId}")
public ResponseEntity<GameResponseDTO> patchShip(@PathVariable("id") Long id,
                                                 @PathVariable("shipId") Long shipId,
                                                 @RequestBody JsonNode patch) {
    return ResponseEntity.ok(service.patchShip(id, shipId, patch));
}
```

`tools.jackson.databind.JsonNode` es Jackson 3, el que usa Spring Boot 4.1. No
copiar un import de Jackson 2 (`com.fasterxml.jackson.databind.JsonNode`).

### 4.3. Servicio: reemplazo completo

Añadir a `GameService`:

```java
import es.daw2a.bshipalong.domain.exceptions.ShipNotFoundException;
import tools.jackson.databind.JsonNode;

public GameResponseDTO replaceShip(Long gameId, Long shipId, PlaceShipDTO dto) {
    Game game = findGame(gameId);
    Ship ship = findShip(gameId, shipId);
    ensureShipEditable(game);
    validateShipConfiguration(game, shipId, dto.shipName(), dto.length(),
            dto.startX(), dto.startY(), dto.isHorizontal());

    ship.setShipName(dto.shipName());
    ship.setLength(dto.length());
    ship.setStartX(dto.startX());
    ship.setStartY(dto.startY());
    ship.setHorizontal(dto.isHorizontal());
    shipRepository.save(ship);
    return toResponse(game);
}
```

La validación se hace **antes** de modificar la entidad. Si algo falla, no se
aplica un cambio parcial.

### 4.4. Validación compartida con exclusión del propio barco

Sustituir `validatePlacement` y `validateNoOverlap` por una única validación que
excluya el barco actual cuando se está editando:

```java
private void validateShipConfiguration(Game game, Long excludeShipId,
                                       String name, int length, int startX,
                                       int startY, boolean horizontal) {
    if (name == null || name.isBlank()) {
        throw new IllegalArgumentException("Ship name is required");
    }
    if (length < 1) {
        throw new IllegalArgumentException("Ship length must be positive");
    }
    if (startX < 0 || startY < 0
            || startX >= game.getBoardSize() || startY >= game.getBoardSize()) {
        throw new IllegalArgumentException("Position out of bounds");
    }
    if (horizontal && (long) startX + length > game.getBoardSize()) {
        throw new IllegalArgumentException("Ship does not fit horizontally");
    }
    if (!horizontal && (long) startY + length > game.getBoardSize()) {
        throw new IllegalArgumentException("Ship does not fit vertically");
    }

    List<Ship> existing = shipRepository.findByGame_Id(game.getId());
    if (existing.stream()
            .filter(ship -> !isCurrent(ship, excludeShipId))
            .anyMatch(ship -> ship.getShipName().equals(name))) {
        throw new IllegalArgumentException("Ship name already used");
    }
    for (Ship other : existing) {
        if (isCurrent(other, excludeShipId)) {
            continue;
        }
        for (int i = 0; i < length; i++) {
            int x = startX + (horizontal ? i : 0);
            int y = startY + (horizontal ? 0 : i);
            if (occupies(other, x, y)) {
                throw new IllegalArgumentException("Ships cannot overlap");
            }
        }
    }
}

private boolean isCurrent(Ship ship, Long excludeShipId) {
    return excludeShipId != null && excludeShipId.equals(ship.getId());
}
```

En `placeShip`, sustituir **ambas** llamadas anteriores a `validatePlacement` y
`validateNoOverlap` por este bloque completo (no conservar llamadas a los métodos
retirados):

```java
validateShipConfiguration(game, null, dto.shipName(), dto.length(),
        dto.startX(), dto.startY(), dto.isHorizontal());
```

Así la creación y la actualización comparten reglas, pero la actualización no
se rechaza a sí misma. Conserva el método `occupies` que ya existía.

### 4.5. Estado editable y pertenencia

```java
private Ship findShip(Long gameId, Long shipId) {
    Ship ship = shipRepository.findById(shipId)
            .orElseThrow(() -> new ShipNotFoundException(shipId));
    if (!ship.getGame().getId().equals(gameId)) {
        throw new ShipNotFoundException(shipId);
    }
    return ship;
}

private void ensureShipEditable(Game game) {
    if (!game.isActive() || game.getStatus() == GameStatus.WON
            || game.getStatus() == GameStatus.CANCELLED) {
        throw new IllegalStateException("Cannot edit ships on a finished or cancelled game");
    }
    if (!attackRepository.findByGame_Id(game.getId()).isEmpty()) {
        throw new IllegalStateException("Cannot edit ships after the first attack");
    }
}
```

No se puede editar un barco de otra partida aunque el id exista. La partida
cancelada ya queda oculta por `findGame` y devuelve 404; una partida ganada
sigue activa y devuelve 409. La edición se bloquea en cuanto hay un ataque,
aunque sea agua.

### 4.6. PATCH: distinguir ausente, nulo y valor

Añade el método y sus helpers a `GameService`, antes de crear las pruebas de
actualización y de ejecutar el siguiente control. No sustituyen a `replaceShip`,
`findShip` ni a la validación compartida de los apartados anteriores:

```java
public GameResponseDTO patchShip(Long gameId, Long shipId, JsonNode patch) {
    if (patch == null || !patch.isObject()) {
        throw new IllegalArgumentException("Patch body must be a JSON object");
    }
    Game game = findGame(gameId);
    Ship ship = findShip(gameId, shipId);
    ensureShipEditable(game);

    String name = ship.getShipName();
    int length = ship.getLength();
    int startX = ship.getStartX();
    int startY = ship.getStartY();
    boolean horizontal = ship.isHorizontal();
    boolean changed = false;

    for (var field : patch.properties()) {
        String key = field.getKey();
        JsonNode value = field.getValue();
        switch (key) {
            case "shipName" -> {
                name = requireText(value, "shipName");
                changed = true;
            }
            case "length" -> {
                length = requireInt(value, "length");
                changed = true;
            }
            case "startX" -> {
                startX = requireInt(value, "startX");
                changed = true;
            }
            case "startY" -> {
                startY = requireInt(value, "startY");
                changed = true;
            }
            case "isHorizontal" -> {
                horizontal = requireBoolean(value, "isHorizontal");
                changed = true;
            }
            default -> throw new IllegalArgumentException("Field not editable: " + key);
        }
    }
    if (!changed) {
        throw new IllegalArgumentException("No editable fields provided");
    }

    validateShipConfiguration(game, shipId, name, length, startX, startY, horizontal);
    ship.setShipName(name);
    ship.setLength(length);
    ship.setStartX(startX);
    ship.setStartY(startY);
    ship.setHorizontal(horizontal);
    shipRepository.save(ship);
    return toResponse(game);
}
```

Helpers de tipo:

```java
private String requireText(JsonNode value, String field) {
    if (value.isNull()) {
        throw new IllegalArgumentException(field + " must not be null");
    }
    if (!value.isTextual()) {
        throw new IllegalArgumentException(field + " must be a string");
    }
    return value.asText();
}

private int requireInt(JsonNode value, String field) {
    if (value.isNull()) {
        throw new IllegalArgumentException(field + " must not be null");
    }
    if (!value.isIntegralNumber()) {
        throw new IllegalArgumentException(field + " must be an integer");
    }
    if (!value.canConvertToInt()) {
        throw new IllegalArgumentException(field + " is outside the integer range");
    }
    return value.asInt();
}

private boolean requireBoolean(JsonNode value, String field) {
    if (value.isNull()) {
        throw new IllegalArgumentException(field + " must not be null");
    }
    if (!value.isBoolean()) {
        throw new IllegalArgumentException(field + " must be a boolean");
    }
    return value.asBoolean();
}
```

Puntos que hay que decir:

- `patch.properties()` solo recorre campos presentes; un campo ausente no aparece.
- Un `null` explícito llega como `NullNode` y se rechaza.
- Un tipo incorrecto (`"length":"tres"`) se rechaza antes de tocar la entidad.
- Un entero fuera del rango de `int` se rechaza antes de convertirlo: Jackson 3
  lanza una excepción de conversión si se llama a `asInt()` con ese valor.
- Un campo desconocido se rechaza; no se ignora en silencio.
- La validación final usa el estado fusionado, no solo el JSON recibido.
- La escritura se hace después de validar; no hay cambios parciales.

### 4.7. Pruebas de servicio de actualización

Crear `src/test/java/es/daw2a/bshipalong/service/ShipUpdateRulesTest.java`:

```java
package es.daw2a.bshipalong.service;

import es.daw2a.bshipalong.domain.Ship;
import es.daw2a.bshipalong.dto.CreateGameDTO;
import es.daw2a.bshipalong.dto.GameResponseDTO;
import es.daw2a.bshipalong.dto.PlaceShipDTO;
import es.daw2a.bshipalong.repository.ShipRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class ShipUpdateRulesTest {
    @Autowired GameService service;
    @Autowired ShipRepository ships;
    @Autowired ObjectMapper mapper;

    private Long game() {
        return service.createGame(new CreateGameDTO(8)).id();
    }

    private Long place(Long gameId, String name, int length, int x, int y, boolean horizontal) {
        service.placeShip(gameId, new PlaceShipDTO(name, length, x, y, horizontal));
        return ships.findByGame_Id(gameId).stream()
                .filter(s -> s.getShipName().equals(name))
                .findFirst().orElseThrow().getId();
    }

    private GameResponseDTO.ShipDTO ship(GameResponseDTO response, Long id) {
        return response.ships().stream()
                .filter(s -> s.id().equals(id))
                .findFirst().orElseThrow();
    }

    private JsonNode json(String text) {
        return mapper.readTree(text);
    }

    @Test void putReplacesAllEditableFields() {
        Long gameId = game();
        Long shipId = place(gameId, "A", 2, 0, 0, true);

        var result = service.replaceShip(gameId, shipId,
                new PlaceShipDTO("B", 3, 2, 2, false));

        var updated = ship(result, shipId);
        assertThat(updated.shipName()).isEqualTo("B");
        assertThat(updated.length()).isEqualTo(3);
        assertThat(updated.startX()).isEqualTo(2);
        assertThat(updated.startY()).isEqualTo(2);
        assertThat(updated.isHorizontal()).isFalse();

        Ship stored = ships.findById(shipId).orElseThrow();
        assertThat(stored.getShipName()).isEqualTo("B");
        assertThat(stored.getLength()).isEqualTo(3);
    }

    @Test void patchChangesOnlyProvidedFields() {
        Long gameId = game();
        Long shipId = place(gameId, "A", 2, 0, 0, true);

        var result = service.patchShip(gameId, shipId, json("{\"shipName\":\"B\"}"));

        var updated = ship(result, shipId);
        assertThat(updated.shipName()).isEqualTo("B");
        assertThat(updated.length()).isEqualTo(2);
        assertThat(updated.startX()).isZero();
        assertThat(updated.startY()).isZero();
        assertThat(updated.isHorizontal()).isTrue();
    }

    @Test void patchIsIdempotent() {
        Long gameId = game();
        Long shipId = place(gameId, "A", 2, 0, 0, true);

        service.patchShip(gameId, shipId, json("{\"shipName\":\"B\"}"));
        var result = service.patchShip(gameId, shipId, json("{\"shipName\":\"B\"}"));

        assertThat(ship(result, shipId).shipName()).isEqualTo("B");
        assertThat(ships.findByGame_Id(gameId)).hasSize(1);
    }

    @Test void rejectsEditAfterFirstAttack() {
        Long gameId = game();
        Long shipId = place(gameId, "A", 2, 0, 0, true);
        place(gameId, "B", 1, 4, 4, false);
        service.attack(gameId, new es.daw2a.bshipalong.dto.AttackDTO(7, 7));

        assertThatThrownBy(() -> service.replaceShip(gameId, shipId,
                new PlaceShipDTO("A", 2, 0, 1, true)))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Cannot edit ships after the first attack");
        assertThatThrownBy(() -> service.patchShip(gameId, shipId,
                json("{\"startY\":1}")))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Cannot edit ships after the first attack");
    }

    @Test void rejectsOverlapButAllowsOwnPosition() {
        Long gameId = game();
        Long shipId = place(gameId, "A", 2, 0, 0, true);
        place(gameId, "B", 2, 4, 4, true);

        service.replaceShip(gameId, shipId, new PlaceShipDTO("A", 2, 0, 0, true));

        assertThatThrownBy(() -> service.replaceShip(gameId, shipId,
                new PlaceShipDTO("A", 2, 4, 4, true)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Ships cannot overlap");
    }

    @Test void rejectsDuplicateNameButAllowsOwnName() {
        Long gameId = game();
        Long shipId = place(gameId, "A", 2, 0, 0, true);
        place(gameId, "B", 2, 4, 4, true);

        service.replaceShip(gameId, shipId, new PlaceShipDTO("A", 2, 0, 0, true));

        assertThatThrownBy(() -> service.replaceShip(gameId, shipId,
                new PlaceShipDTO("B", 2, 0, 0, true)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Ship name already used");
    }

    @Test void patchRejectsNullUnknownAndEmptyFields() {
        Long gameId = game();
        Long shipId = place(gameId, "A", 2, 0, 0, true);

        assertThatThrownBy(() -> service.patchShip(gameId, shipId,
                json("{\"length\":null}")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("length must not be null");
        assertThatThrownBy(() -> service.patchShip(gameId, shipId,
                json("{\"id\":99}")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Field not editable: id");
        assertThatThrownBy(() -> service.patchShip(gameId, shipId, json("{}")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("No editable fields provided");
    }

    @Test void rejectsOutOfRangeIntegerWithoutChangingShip() {
        Long gameId = game();
        Long shipId = place(gameId, "A", 2, 0, 0, true);

        assertThatThrownBy(() -> service.patchShip(gameId, shipId,
                json("{\"shipName\":\"B\",\"startX\":4294967296}")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("startX is outside the integer range");
        Ship stored = ships.findById(shipId).orElseThrow();
        assertThat(stored.getShipName()).isEqualTo("A");
        assertThat(stored.getStartX()).isZero();
    }
}
```

### 4.8. Control intermedio

```bash
./mvnw -Dtest=ShipUpdateRulesTest test
```

Debe pasar con 8 casos.

## 5. Pruebas web y HTTP

Crear `src/test/java/es/daw2a/bshipalong/web/ShipUpdateWebTest.java` para
comprobar el contrato con servicio mock:

```java
package es.daw2a.bshipalong.web;

import es.daw2a.bshipalong.domain.exceptions.ShipNotFoundException;
import es.daw2a.bshipalong.dto.GameResponseDTO;
import es.daw2a.bshipalong.dto.PlaceShipDTO;
import es.daw2a.bshipalong.service.GameService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.JsonNode;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(GameController.class)
class ShipUpdateWebTest {
    @Autowired MockMvc mvc;
    @MockitoBean GameService service;

    private GameResponseDTO game() {
        return new GameResponseDTO(1L, 8, "IN_PROGRESS", null,
                List.of(new GameResponseDTO.ShipDTO(2L, "B", 3, 2, 2, false, false)),
                List.of());
    }

    @Test void putShipReturnsOk() throws Exception {
        var request = new PlaceShipDTO("B", 3, 2, 2, false);
        when(service.replaceShip(1L, 2L, request)).thenReturn(game());

        mvc.perform(put("/api/games/1/ships/2")
                        .contentType("application/json")
                        .content("""
                                {"shipName":"B","length":3,"startX":2,"startY":2,"isHorizontal":false}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ships[0].shipName").value("B"))
                .andExpect(jsonPath("$.ships[0].length").value(3));
    }

    @Test void patchShipReturnsOk() throws Exception {
        when(service.patchShip(eq(1L), eq(2L), any(JsonNode.class))).thenReturn(game());

        mvc.perform(patch("/api/games/1/ships/2")
                        .contentType("application/json")
                        .content("{\"shipName\":\"B\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ships[0].shipName").value("B"));
    }

    @Test void missingShipReturns404() throws Exception {
        when(service.replaceShip(eq(1L), eq(999L), any(PlaceShipDTO.class)))
                .thenThrow(new ShipNotFoundException(999L));

        mvc.perform(put("/api/games/1/ships/999")
                        .contentType("application/json")
                        .content("""
                                {"shipName":"B","length":3,"startX":2,"startY":2,"isHorizontal":false}
                                """))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("NOT_FOUND"));
    }

    @Test void editAfterAttackReturns409() throws Exception {
        when(service.patchShip(eq(1L), eq(2L), any(JsonNode.class)))
                .thenThrow(new IllegalStateException("Cannot edit ships after the first attack"));

        mvc.perform(patch("/api/games/1/ships/2")
                        .contentType("application/json")
                        .content("{\"startY\":1}"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("CONFLICT"));
    }

    @Test void putWithoutRequiredFieldsReturns400() throws Exception {
        mvc.perform(put("/api/games/1/ships/2")
                        .contentType("application/json")
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("VALIDATION_ERROR"));
        verifyNoInteractions(service);
    }
}
```

Crear `src/test/java/es/daw2a/bshipalong/web/ShipUpdateHttpTest.java` para
comprobar el recorrido con componentes reales:

```java
package es.daw2a.bshipalong.web;

import es.daw2a.bshipalong.repository.ShipRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ShipUpdateHttpTest {
    @Autowired MockMvc mvc;
    @Autowired ShipRepository ships;

    private String create() throws Exception {
        return mvc.perform(post("/api/games").contentType("application/json")
                        .content("{\"boardSize\":8}"))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getHeader("Location");
    }

    private void place(String game, String name, int length, int x, int y, boolean horizontal)
            throws Exception {
        mvc.perform(post(game + "/ships").contentType("application/json").content("""
                        {"shipName":"%s","length":%d,"startX":%d,"startY":%d,"isHorizontal":%s}
                        """.formatted(name, length, x, y, horizontal)))
                .andExpect(status().isCreated());
    }

    private Long shipId(String game) {
        Long gameId = Long.parseLong(game.substring(game.lastIndexOf('/') + 1));
        return ships.findByGame_Id(gameId).getFirst().getId();
    }

    @Test void putAndPatchThroughHttp() throws Exception {
        String game = create();
        place(game, "A", 2, 0, 0, true);
        Long shipId = shipId(game);

        mvc.perform(put(game + "/ships/" + shipId).contentType("application/json").content("""
                        {"shipName":"B","length":3,"startX":2,"startY":2,"isHorizontal":false}
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ships[0].shipName").value("B"))
                .andExpect(jsonPath("$.ships[0].startY").value(2));

        mvc.perform(patch(game + "/ships/" + shipId).contentType("application/json")
                        .content("{\"startY\":3}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ships[0].startY").value(3))
                .andExpect(jsonPath("$.ships[0].shipName").value("B"));
    }

    @Test void editAfterAttackReturns409ThroughHttp() throws Exception {
        String game = create();
        place(game, "A", 2, 0, 0, true);
        place(game, "B", 1, 4, 4, false);
        Long shipId = shipId(game);

        mvc.perform(post(game + "/attacks").contentType("application/json")
                        .content("{\"x\":7,\"y\":7}"))
                .andExpect(status().isCreated());

        mvc.perform(put(game + "/ships/" + shipId).contentType("application/json").content("""
                        {"shipName":"A","length":2,"startX":0,"startY":1,"isHorizontal":true}
                        """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("CONFLICT"));
    }

    @Test void outOfRangePatchReturns400AndKeepsShip() throws Exception {
        String game = create();
        place(game, "A", 2, 0, 0, true);
        Long shipId = shipId(game);

        mvc.perform(patch(game + "/ships/" + shipId).contentType("application/json")
                        .content("{\"shipName\":\"B\",\"startX\":4294967296}"))
                .andExpect(status().isBadRequest());
        mvc.perform(get(game)).andExpect(status().isOk())
                .andExpect(jsonPath("$.ships[0].shipName").value("A"))
                .andExpect(jsonPath("$.ships[0].startX").value(0));
    }
}
```

## 6. Control final

```bash
./mvnw test
```

Salida verificada en copia:

```text
Tests run: 65, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

Composición del recuento:

| Bloque | Casos |
|---|---:|
| Base anterior (guion 10) | 45 |
| Restricciones en `GamePersistenceTest` | +4 |
| Actualización en `ShipUpdateRulesTest` | +8 |
| Contrato web en `ShipUpdateWebTest` | +5 |
| Flujo HTTP real en `ShipUpdateHttpTest` | +3 |
| **Total** | **65** |

Si el aula no añade los tres tests HTTP, el mínimo es 62; si tampoco añade el
contrato web, 57. No reducir el número borrando pruebas anteriores.

## 7. Qué se observa en la BD

- Flyway valida **4 migraciones** y aplica V4.
- Hibernate arranca con `ddl-auto: validate`; no crea la restricción por su cuenta.
- En las pruebas negativas aparecen avisos `23505`/`UK_SHIP_GAME_NAME` y
  `UK_ATTACK_GAME_POINT`: son las violaciones esperadas.
- `SchemaEvolutionTest` migra desde V1 y comprueba que las partidas anteriores
  siguen ahí al llegar a V4.
- Las filas existentes no se borran al añadir la restricción si no hay duplicados.

## 8. Errores previsibles y respuesta breve

| Síntoma | Causa probable | Corrección |
|---|---|---|
| La migración V4 falla al aplicarse | Hay duplicados previos | Consultar y decidir antes de añadir la restricción |
| `No property 'gameId'` al arrancar | Repositorio antiguo | Usar `findByGame_Id` |
| PUT devuelve 400 por campos ausentes | Se envió un PATCH parcial a PUT | PUT exige todos los campos |
| PATCH ignora un campo desconocido | Se usó un DTO tipado en vez de `JsonNode` | Recibir `JsonNode` y validar claves |
| PATCH acepta `null` como «no cambio» | Se perdió la distinción ausente/nulo | Rechazar `NullNode` |
| 500 al editar un barco inexistente | Falta `ShipNotFoundException` | Añadir excepción y handler 404 |
| Se puede editar después de atacar | Falta `ensureShipEditable` | Comprobar ataques antes de escribir |
| Se rechaza el propio barco al editar | No se excluye `shipId` | `isCurrent` en nombre y solapamiento |
| El cambio se aplica a medias | Se modifica antes de validar | Validar el estado fusionado y luego escribir |

No cambiar V1–V3. No añadir `ddl-auto=update`. No convertir cualquier violación
de integridad en el mismo mensaje de «nombre repetido» sin comprobar la causa.
