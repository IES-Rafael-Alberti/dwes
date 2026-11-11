# Battleship: asociaciones JPA y persistencia

Este recorrido continúa la construcción de **`bshipAlong`**, el proyecto de
aula. No sustituye al proyecto de referencia `battleship`: parte del estado
construido en clase tras el cierre anterior.

## 1. Punto de partida y objetivo

Antes de empezar, abre `02-ejemplos/bshipAlong/` y ejecuta desde su raíz:

```bash
./mvnw test
```

El punto de partida consolidado tiene **43 pruebas verdes**. Si el test antiguo
`missingGameReturns404` exige un cuerpo vacío y también existe
`missingGameHasErrorPayload`, conserva este último y retira solo el caso antiguo
duplicado: el contrato actual devuelve un cuerpo de error JSON. Vuelve a ejecutar
las pruebas antes de continuar.

Al finalizar habrá **45 pruebas verdes**, asociaciones persistidas y conservación
del histórico al cancelar. No se cambian las migraciones ni los DTOs. Las
restricciones de unicidad y la actualización mediante PUT/PATCH se trabajan
en un tramo posterior.

## 2. Decisión de modelo

Se sustituyen los ids numéricos por asociaciones en los hijos:

```text
antes:  Ship.gameId  ->  Long          Attack.gameId -> Long
después: Ship.game   ->  Game          Attack.game   -> Game
```

Se añade **solo** `Game.ships` como colección navegable desde el padre. No se
añade `Game.attacks`: el servicio ya consulta ataques por repositorio y no hay
una necesidad de navegación que justifique otra colección. Se puede añadir más
adelante si un caso de uso lo pide.

No se cambia la tabla: `ship.game_id` y `attack.game_id` siguen siendo las mismas
columnas y las mismas FK de V3. Cambia el mapeo Java, no el esquema.

## 3. Entidades

### 3.1. Entidades hijas: `Ship` y `Attack`

Abrir `domain/Ship.java` y sustituir el campo `Long gameId` por la asociación.
El archivo completo queda así:

```java
package es.daw2a.bshipalong.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Ship {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Muchos barcos pueden apuntar a la misma partida.
    // La FK sigue siendo game_id; no se guarda una copia de Game en cada fila.
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

`Attack` recibe el mismo cambio. Archivo completo:

```java
package es.daw2a.bshipalong.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Attack {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // El ataque también pertenece a una partida. Se conserva la columna game_id.
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

No dejar a la vez `Long gameId` y `Game game` escribiendo la misma columna. El
DTO puede seguir teniendo `gameId`; eso no obliga a conservar el campo en la
entidad.

### 3.2. `Game`: colección y helper

Sustituye `domain/Game.java` por este archivo completo antes de adaptar el
servicio: incorpora la colección y el helper que se utilizarán más adelante.

```java
package es.daw2a.bshipalong.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "game")
@Getter @Setter
public class Game {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "board_size", nullable = false)
    private int boardSize;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private GameStatus status;
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    @Column(nullable = false)
    private boolean active = true;
    @Column(name = "cancelled_at")
    private LocalDateTime cancelledAt;

    // Lado inverso: mappedBy nombra el campo Java Ship.game, no la columna game_id.
    // La lista se inicializa para que una partida nueva pueda recibir barcos.
    @OneToMany(mappedBy = "game")
    private List<Ship> ships = new ArrayList<>();

    public Game() { this(10); }
    public Game(int boardSize) {
        this.boardSize = boardSize;
        this.status = GameStatus.PENDING;
    }

    // Coordina los dos lados en memoria. No hace INSERT ni valida el tablero.
    public void addShip(Ship ship) {
        if (ship == null) {
            throw new IllegalArgumentException("Ship is required");
        }
        if (ship.getGame() != null && ship.getGame() != this) {
            throw new IllegalStateException("Ship already belongs to another game");
        }
        if (!ships.contains(ship)) {
            ships.add(ship);
        }
        ship.setGame(this);
    }

    public void cancel() {
        if (status == GameStatus.WON) {
            throw new IllegalStateException("Cannot cancel finished game");
        }
        status = GameStatus.CANCELLED;
        active = false;
        cancelledAt = LocalDateTime.now();
    }

    @PrePersist
    void onCreate() {
        if (createdAt == null) createdAt = LocalDateTime.now();
    }
}
```

Decisiones de este modelo:

- **No** se añade `@OneToMany` a ataques en este tramo.
- **No** se usa `CascadeType.ALL`.
- **No** se usa `orphanRemoval`.
- **No** se sustituye la lista por una nueva desde un setter sin pensar.
- Si más adelante se usa `@Builder` en `Game`, la lista necesita
  `@Builder.Default`; hoy `Game` no usa builder.

## 4. Repositorios: cambiar el nombre derivado

Al desaparecer el campo `gameId`, usamos `Game_Id` para expresar claramente
el recorrido por la asociación:

```java
public interface ShipRepository extends JpaRepository<Ship, Long> {
    List<Ship> findByGame_Id(Long gameId);
}
```

```java
public interface AttackRepository extends JpaRepository<Attack, Long> {
    List<Attack> findByGame_Id(Long gameId);
    boolean existsByGame_IdAndXAndY(Long gameId, int x, int y);
}
```

`Game_Id` significa «el `id` de la propiedad `game`». No es una columna nueva.
El separador hace explícito el recorrido `game.id`; Spring Data también puede
interpretar algunos nombres sin separador. Hay que actualizar tanto las
declaraciones como sus llamadas para que coincidan y compilen.

## 5. Servicio: construir con la asociación

El archivo `Game.java` del apartado 3.2 debe estar aplicado antes de este paso:
el servicio necesita su método `addShip`.

En `GameService.placeShip`, sustituir la construcción del barco:

```java
Ship ship = Ship.builder()
        .game(game)              // antes: .gameId(id)
        .shipName(dto.shipName())
        .length(dto.length())
        .startX(dto.startX())
        .startY(dto.startY())
        .isHorizontal(dto.isHorizontal())
        .sunk(false)
        .build();
game.addShip(ship);              // mantiene los dos lados en memoria
shipRepository.save(ship);       // el helper no hace INSERT por sí solo
```

### 5.1. ¿Qué se guarda y qué ocurre con el estado?

`game.addShip(ship)` coordina las dos referencias en memoria: añade el barco a
`game.ships` y establece `ship.game`. No envía por sí mismo un `INSERT` ni
valida la posición. Como en este modelo no hemos configurado cascada de
persistencia, el barco nuevo se guarda explícitamente con `shipRepository.save(ship)`.

La partida es distinta: `findGame(id)` la recupera dentro de la transacción de
`GameService`, de modo que es una entidad **gestionada** por el contexto de
persistencia. JPA detecta los cambios realizados sobre ella (*dirty checking*)
y los sincroniza con la base de datos; normalmente lo hace al hacer `flush`,
como parte de la confirmación de la transacción. Un `save` no es un commit
independiente, y una transacción que finalmente se revierte no confirma esos
cambios.

En concreto, el servicio conserva este bloque que ya existía:

```java
if (game.getStatus() == GameStatus.PENDING) {
    game.setStatus(GameStatus.IN_PROGRESS);
    repository.save(game);
}
```

Para esa partida ya gestionada, `repository.save(game)` es redundante en este
contexto transaccional; lo mantenemos en este paso para no mezclar cambios. No
hay que eliminar la transición: el primer barco sigue haciendo que la partida
pase de `PENDING` a `IN_PROGRESS`. Tampoco se eliminan las validaciones ni el
rechazo de estados terminales que ya realiza `placeShip`.

**Posible refactor posterior, no aplicado aquí:** estudiar que la entidad asuma
la transición en un método de dominio. Eso exigiría decidir sus invariantes y
comprobarlas con pruebas; el `addShip` actual solo coordina las asociaciones y
no cambia el estado de la partida.

### 5.2. Ataques y consultas

En `GameService.attack`:

```java
attackRepository.save(Attack.builder()
        .game(game)              // antes: .gameId(id)
        .x(dto.x())
        .y(dto.y())
        .hit(hit)
        .build());
```

Y cambiar todas las llamadas de consulta:

```java
shipRepository.findByGame_Id(id)
attackRepository.findByGame_Id(game.getId())
attackRepository.existsByGame_IdAndXAndY(id, dto.x(), dto.y())
```

Afecta a `attack`, `checkSunkShips`, `validatePlacement`, `validateNoOverlap`
y `toResponse`. No cambiar la lógica de las reglas: solo cómo se consulta y cómo
se construye la referencia.

## 6. Control intermedio

En `GameRulesTest.placesShipOnGame`, cambia también la llamada existente
`ships.findByGameId(id)` por `ships.findByGame_Id(id)`. La comprobación adicional
del apartado 7.2 se incorpora después. Si no se adapta ahora el nombre, ni siquiera
compilan las pruebas al ejecutar Maven.

```bash
./mvnw -Dtest=GameRulesTest,GameServiceTest test
```

Debe compilar y pasar. Si falla, revisar primero:

- que no queda ningún `gameId` escribible en `Ship`/`Attack`;
- que los repositorios usan `Game_Id`;
- que el builder del servicio usa `.game(game)`.

## 7. Pruebas de persistencia

### 7.1. Persistencia real: dos pruebas nuevas

Crear `src/test/java/es/daw2a/bshipalong/domain/GamePersistenceTest.java`:

```java
package es.daw2a.bshipalong.domain;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

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
}
```

Por qué estas dos pruebas y no cinco:

- la primera demuestra que la relación se ha escrito y se reconstruye desde la BD
  después de `flush` + `clear`; no se queda solo en la instancia en memoria;
- la segunda demuestra que cancelar conserva partida, barcos y ataques;
- no se demuestra aquí el rechazo de una FK inexistente ni una restricción de
  unicidad; esas comprobaciones corresponden al siguiente tramo.

`flush` envía los cambios pendientes; `clear` separa las entidades. `clear` no
borra filas y no debe usarse antes de sincronizar.

### 7.2. Actualizar una aserción existente

En `GameRulesTest.placesShipOnGame`, después de comprobar el DTO, comprobar la
relación persistida:

```java
var stored = ships.findByGame_Id(id);
assertThat(stored).hasSize(1);
assertThat(stored.getFirst().getGame().getId()).isEqualTo(id);
```

No hace falta reescribir el resto de pruebas del juego.

## 8. Carga, DTO y ciclos

El servicio sigue construyendo `GameResponseDTO` dentro de métodos
`@Transactional` o `@Transactional(readOnly = true)`. Eso permite leer lo que el
DTO necesita sin que el controlador recorra entidades fuera del contexto.

- `@ManyToOne(fetch = LAZY)` no promete una sola consulta.
- `open-in-view: false` ya está configurado; no se activa para tapar cargas
  tardías.
- El listado de partidas consulta barcos y ataques por cada partida. Es un
  patrón que conviene observar; **no se arregla hoy** con `EAGER` ni con un
  `fetch join` improvisado. El bloque B trabaja consultas y paginación.
- El controlador devuelve DTOs, no entidades. Por eso no aparece el bucle
  `Game → ships → Ship.game → Game`. No se añaden `@JsonIgnore`,
  `@JsonManagedReference` ni `@JsonIdentityInfo` como parche: el DTO ya expresa
  dónde termina el recorrido.

## 9. Control final

```bash
./mvnw test
```

Resultado esperado:

```text
Tests run: 45, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

Las dos pruebas de `GamePersistenceTest` forman parte de este recorrido y llevan
la salida esperada a 45 pruebas. No reducir el número
borrando pruebas existentes.

## 10. Qué se observa en SQL

Con `show-sql: true` ya activo, la prueba y los flujos muestran que la asociación
sigue usando la misma columna:

```text
insert into ship (game_id,is_horizontal,length,ship_name,start_x,start_y,sunk,id) ...
select ... from ship s1_0 where s1_0.game_id=?
insert into attack (created_at,game_id,hit,x,y,id) ...
select ... from attack a1_0 where a1_0.game_id=?
```

No aparece una tabla nueva ni una columna duplicada. Flyway valida las tres
migraciones existentes y Hibernate arranca con `ddl-auto: validate`.

## 11. Errores previsibles y respuesta breve

| Síntoma | Causa probable | Corrección |
|---|---|---|
| Falla una consulta derivada o no compila una llamada | El nombre del repositorio y sus usos no coinciden, o el recorrido no es válido | Revisar la propiedad `game.id` y usar `findByGame_Id` de forma coherente |
| Sigue habiendo dos columnas o un mapeo raro | Se conservó `Long gameId` junto a `Game game` | Dejar solo la asociación |
| El barco se guarda sin partida | Se olvidó `game.addShip` o `ship.setGame` | Helper y builder con `.game(game)` |
| La lista está a `null` | No se inicializó `ships` | `new ArrayList<>()` |
| Se borran barcos al cancelar | `orphanRemoval` o cascada de borrado | Quitarlos; cancelar cambia estado |
| Bucle en JSON | Se devuelve una entidad en el controlador | Devolver el DTO existente |
| `LazyInitializationException` | Se lee la asociación fuera del servicio | Preparar el DTO dentro de la transacción |

No cambiar `V1`, `V2` ni `V3`. No añadir una migración en este recorrido. No cambiar
el contrato HTTP ni los DTOs.
