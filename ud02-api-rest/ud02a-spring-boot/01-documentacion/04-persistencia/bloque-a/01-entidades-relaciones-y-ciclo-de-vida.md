# Bloque A · Parte 1 — Entidades, relaciones y ciclo de vida

**Versión de referencia técnica · Spring Boot 4, Jakarta Persistence e Hibernate.**

Para una primera lectura, utiliza la
[versión didáctica, con explicaciones paso a paso](01-entidades-relaciones-y-ciclo-de-vida-didactica.md).
Ambas cubren 1:N, 1:1, N:M y referencias circulares. Esta versión conserva mayor
detalle sobre mapeo, estados de persistencia y alternativas de implementación.

Este documento explica cómo pasar de «guardar objetos con un id» a trabajar con
un modelo relacional coherente. El hilo conductor es Battleship: partidas,
barcos y ataques. La [segunda parte](02-integridad-migraciones-y-pruebas.md)
completa el recorrido con restricciones, migraciones y pruebas de persistencia.

Los fragmentos son ejemplos de estudio: **no son una sustitución completa de
las clases del proyecto de aula**. Las reglas de tablero, los estados, la baja
lógica y los contratos HTTP que ya funcionan deben conservarse al evolucionarlo.

## 1. Tres modelos relacionados, pero diferentes

Cuando devolvemos una partida por HTTP intervienen al menos tres representaciones:

| Representación | Qué expresa | Ejemplo |
|---|---|---|
| Modelo relacional | Tablas, columnas, FK y restricciones | `ship.game_id → game.id` |
| Modelo de entidades | Objetos, asociaciones y comportamiento persistente | `Ship.game` referencia un `Game` |
| Contrato HTTP | Datos que necesita el cliente para una operación | `GameResponseDTO` con resumen y barcos |

No tienen por qué copiarse literalmente. La BD puede tener una FK, Java una
referencia a un objeto y el JSON solo el identificador de ese objeto.

Esta separación importa porque cada representación cambia por motivos distintos.
Podemos añadir una asociación a una entidad sin añadir automáticamente nuevos
campos a todas las respuestas de la API.

### 1.1. Lo que ya existe en Battleship

En el checkpoint de 04b, la tabla `ship` tiene una FK a `game`, pero la clase
`Ship` guarda ese vínculo como un número:

```java
@Column(name = "game_id", nullable = false)
private Long gameId;
```

Esto no es incorrecto por definición: una aplicación puede decidir trabajar con
identificadores. Sin embargo, **no declara una asociación JPA**. Hibernate ve un
atributo numérico, no una relación navegable entre `Ship` y `Game`.

Para recuperar la partida, el servicio debe buscarla explícitamente. Para listar
sus barcos, consulta el repositorio de barcos por ese número. El esquema tiene
relación; el modelo Java todavía no la expresa como asociación.

El bloque A estudia la evolución a asociaciones JPA y sus consecuencias. No se
añaden anotaciones únicamente para que el diagrama parezca más completo.

## 2. Qué es una entidad y qué identidad tiene

Una entidad representa información persistente con identidad propia. Dos partidas
pueden tener el mismo estado y tamaño de tablero y seguir siendo partidas distintas.

```java
@Entity
@Table(name = "game")
public class Game {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Resto de atributos y comportamiento de la partida.
}
```

- `@Entity` pertenece a **Jakarta Persistence**, no específicamente a Spring Boot.
- `@Table` permite fijar el nombre de tabla y otra información del mapeo.
- `@Id` identifica el atributo que representa la PK.
- `@GeneratedValue` indica una estrategia de generación del identificador.
- Hibernate es el proveedor que materializa ese mapeo en nuestro entorno.

La entidad necesita un constructor sin argumentos accesible para el proveedor,
habitualmente `protected` o `public`. Para este curso se usan clases convencionales
no finales; los records son adecuados para muchos DTOs, no un reemplazo directo
de estas entidades JPA.

### Identidad Java e identidad persistente

`a == b` compara si dos referencias apuntan al mismo objeto Java. Un id compara
identidad persistente dentro de su tipo de entidad. No son la misma operación.

Dentro de un contexto de persistencia, el proveedor mantiene una instancia
gestionada para una determinada identidad. Fuera de ese contexto podemos tener
dos objetos Java que representen la misma fila.

Antes de persistir, una entidad con id generado puede tener `id == null`. Por
eso, generar `equals` y `hashCode` con todos los campos, o decidir que dos ids
nulos significan igualdad, produce errores. Volveremos sobre ello al tratar Lombok.

## 3. Cardinalidad y dirección no son lo mismo

La **cardinalidad** responde a cuántas entidades pueden estar relacionadas:

| Asociación | Lectura | Caso |
|---|---|---|
| `@ManyToOne` | Muchos hijos pueden pertenecer al mismo padre | Muchos barcos pertenecen a una partida |
| `@OneToMany` | Un padre puede tener varios hijos | Una partida contiene barcos |
| `@OneToOne` | Una entidad se relaciona como máximo con una de la otra clase según el modelo | Usuario y perfil separado |
| `@ManyToMany` | Varios elementos se relacionan con varios del otro lado | Usuarios y roles |

La **dirección de navegación** responde a qué referencias existen en Java:

```text
Unidireccional:   Ship ───────────► Game

Bidireccional:    Ship ───────────► Game
                      ◄───────────
                         ships
```

Una relación bidireccional no implica dos FK. Puede tener una sola FK y dos
formas de navegar por los objetos. Tampoco todo vínculo debe hacerse bidireccional:
añadir una colección tiene coste de mantenimiento y puede facilitar cargas que
la aplicación no necesita.

El documento desarrolla las tres cardinalidades. Su incorporación al proyecto
de clase sigue siendo progresiva: primero partida–barcos y, al preparar usuarios,
cuenta–perfil y cuenta–roles. Leer aquí el modelo de usuarios no significa que el
login o esas tablas ya estén implementados en `bshipAlong`.

## 4. Del barco a la partida: `@ManyToOne`

La tabla hija contiene la FK. Por eso, `Ship` es el lugar natural para declarar:

```java
@ManyToOne(fetch = FetchType.LAZY, optional = false)
@JoinColumn(name = "game_id", nullable = false)
private Game game;
```

### Qué significa cada parte

- `@ManyToOne`: varios barcos pueden referirse al mismo objeto persistente `Game`.
- `fetch = LAZY`: solicitamos carga diferida de la asociación. El proveedor puede
  utilizar proxies u otras técnicas; no debemos diseñar suponiendo un número fijo
  de consultas solo por haber escrito la anotación.
- `optional = false`: en el modelo JPA, esta asociación no es opcional.
- `@JoinColumn(name = "game_id")`: la referencia utiliza esa columna física.
- `nullable = false`: declara la intención de columna no nula en el mapeo. Con
  Flyway, la garantía real debe existir también en el esquema migrado.

Los defaults de JPA no son iguales para todas las asociaciones: `ManyToOne` y
`OneToOne` son `EAGER` por defecto; las colecciones `OneToMany` y `ManyToMany`
son `LAZY`. Conviene expresar una decisión y no depender de esos defaults sin saberlo.

### 4.1. No mapear la misma columna dos veces sin una razón

Al introducir `Game game`, no dejamos además un `Long gameId` ordinario que
escriba la misma columna. Dos atributos escribibles sobre `game_id` crearían un
mapeo conflictivo.

La lectura del id puede hacerse mediante la asociación, dentro del uso adecuado
del contexto. Existen mapeos duplicados de solo lectura con `insertable=false`
y `updatable=false`, pero no los necesitamos como receta inicial.

El DTO puede seguir teniendo un `gameId`: no obliga a que la entidad lo conserve
como otro campo persistente.

## 5. De la partida a sus barcos: `@OneToMany` y `mappedBy`

Si necesitamos navegar desde la partida hacia sus barcos:

```java
@OneToMany(mappedBy = "game")
private List<Ship> ships = new ArrayList<>();
```

**`mappedBy = "game"` nombra el atributo Java `Ship.game`.** No nombra la tabla
`game`, ni la columna `game_id`, ni una variable del servicio.

La asociación se interpreta así:

```text
Objeto Game                    Objeto Ship
  id = 7                         id = 12
  ships ──────────────────────►  game ─────────► Game(id=7)

Tabla game                     Tabla ship
  id = 7                         id = 12, game_id = 7
```

### 5.1. El propietario no es «el más importante»

En una asociación bidireccional 1:N, el lado `ManyToOne` es propietario del
vínculo porque mapea la FK. El lado `OneToMany(mappedBy=...)` es inverso.

Esto no significa que la entidad hija controle el caso de uso ni que sea la raíz
del dominio. **Propietario del mapeo** y **responsable del proceso de negocio** son
ideas distintas.

Si solo añadimos el barco a `game.getShips()` y nunca establecemos `ship.game`,
el lado propietario puede seguir sin FK. Si solo establecemos `ship.game`, la BD
puede quedar bien mientras la colección ya cargada en memoria queda desactualizada.

### 5.2. Sin `mappedBy` puede aparecer otro diseño

Un `@OneToMany` unidireccional sin configuración adicional puede utilizar una
tabla de unión. Añadir a la vez un `ManyToOne` independiente no hace que ambos
mapeos sean automáticamente los dos lados de la misma relación.

No hay que adivinar qué ha ocurrido: revisar el esquema y el SQL permite detectar
una tabla o columna que no formaba parte de nuestro diseño.

## 6. Mantener una relación bidireccional en memoria

Un helper expresa la operación «incorporar este barco a esta partida»:

```java
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
```

Este fragmento supone una colección `ships` inicializada y los accesores de
`Ship`. La comparación de referencias se usa aquí para el ensamblaje de este
grafo en memoria; no define una política universal de igualdad para entidades
recuperadas en contextos distintos.

El helper coordina ambas referencias. **No hace un INSERT por sí solo**, no
valida el tablero y no sustituye una restricción de unicidad.

### 6.1. Inicialización y encapsulación

Una colección nueva debe estar preparada para recibir elementos:

```java
private List<Ship> ships = new ArrayList<>();
```

Si se utiliza Lombok `@Builder`, hay que revisar su interacción con los valores
iniciales —por ejemplo, `@Builder.Default`— o controlar la construcción de otra
forma. Una lista nula no es una representación útil de «todavía no tiene barcos».

En una entidad gestionada, Hibernate puede envolver o sustituir la colección por
una implementación persistente. No conviene reemplazarla indiscriminadamente
por una lista nueva ni exponer operaciones que permitan saltarse los helpers.

### 6.2. No inventar un `remove` sin definir qué significa

Quitar un barco puede significar borrar su fila, desvincularlo o marcarlo como
inactivo. Son decisiones diferentes. Si `game_id` es obligatorio, poner la FK a
null no es una operación persistente válida por sí sola.

Battleship conserva el histórico al cancelar. Por tanto, no copiamos un helper
`removeShip` que ponga `game=null` y asumimos que esa es la cancelación del juego.

## Relaciones uno a uno: `@OneToOne`

### El requisito, la FK y la restricción única

Supongamos que separamos los datos de la cuenta del jugador de sus datos de
presentación. Una cuenta tiene **cero o un perfil**, y un perfil pertenece a
**exactamente una cuenta**. Es una decisión de diseño: si todos los datos se usan
y cambian juntos, una sola tabla también podría ser una solución suficiente.

```text
app_user                          user_profile
id   username                     id   user_id   display_name
7    ana                          20   7         Ana
8    luis
```

La FK `user_profile.user_id` indica a qué cuenta pertenece el perfil.
**La restricción UNIQUE sobre esa FK impide que dos perfiles apunten a la misma
cuenta.** Una FK sin esa unicidad permitiría muchos perfiles por usuario y, por
tanto, no garantizaría el 1:1 en el esquema.

No confundamos las dos condiciones:

- `NOT NULL` en el perfil exige que todo perfil tenga usuario.
- `UNIQUE` exige que ese usuario no tenga otro perfil.
- Ninguna de esas dos condiciones obliga a que todos los usuarios tengan perfil.

### Clases con propietario en el perfil

Las clases siguientes pertenecen a un mismo paquete. Se omite solo su declaración
de paquete para reutilizar el ejemplo de estudio; cada clase pública va en un
archivo propio. No se introduce contraseña porque aquí estudiamos asociaciones,
no un modelo de credenciales listo para producción.

<!-- modelo-java: PlayerAccount.java -->
```java
import jakarta.persistence.*;
import java.util.*;

@Entity
@Table(name = "app_user")
public class PlayerAccount {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @OneToOne(mappedBy = "user", fetch = FetchType.LAZY)
    private PlayerProfile profile;

    protected PlayerAccount() {}
    public PlayerAccount(String username) { this.username = username; }

    public void attachProfile(PlayerProfile profile) {
        Objects.requireNonNull(profile, "Profile is required");
        if (this.profile != null && this.profile != profile) {
            throw new IllegalStateException("Account already has a profile");
        }
        if (profile.getUser() != null && profile.getUser() != this) {
            throw new IllegalStateException("Profile belongs to another account");
        }
        this.profile = profile;
        profile.assignUser(this);
    }

    public Long getId() { return id; }
    public String getUsername() { return username; }
    public PlayerProfile getProfile() { return profile; }

    // Punto de extensión: roles.
}
```

<!-- modelo-java: PlayerProfile.java -->
```java
import jakarta.persistence.*;

@Entity
@Table(name = "user_profile")
public class PlayerProfile {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private PlayerAccount user;

    @Column(name = "display_name", nullable = false)
    private String displayName;

    protected PlayerProfile() {}
    public PlayerProfile(String displayName) { this.displayName = displayName; }

    void assignUser(PlayerAccount user) { this.user = user; }
    public Long getId() { return id; }
    public PlayerAccount getUser() { return user; }
    public String getDisplayName() { return displayName; }
}
```

`PlayerProfile.user` es el propietario porque contiene el mapeo de la FK.
`PlayerAccount.profile` es la navegación inversa: `mappedBy="user"` nombra el
atributo de `PlayerProfile`, no la columna `user_id`.

El helper evita inconsistencias al construir este grafo en memoria. La BD sigue
necesitando la FK y la unicidad para proteger escrituras de otros procesos o
contextos. En un servicio transaccional, sin cascada de persistencia:

```java
var account = new PlayerAccount("ana");
var profile = new PlayerProfile("Ana");
account.attachProfile(profile);
em.persist(account);
em.persist(profile);
```

Con Flyway, la tabla debe incluir la restricción real:

<!-- esquema-sql: user_profile -->
```sql
CREATE TABLE app_user (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE user_profile (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    user_id BIGINT NOT NULL,
    display_name VARCHAR(255) NOT NULL,
    CONSTRAINT fk_profile_user FOREIGN KEY (user_id) REFERENCES app_user(id),
    CONSTRAINT uk_profile_user UNIQUE (user_id)
);
```

### Direcciones, carga y eliminación

También puede ser unidireccional: si nunca necesitamos `account.getProfile()`,
podemos conservar solo `PlayerProfile.user`. No hace falta poner dos referencias
para que SQL considere única la FK.

El lado inverso opcional merece cuidado con la carga diferida. La fila de usuario
no contiene un `profile_id`; Hibernate puede necesitar otra consulta para saber
si hay perfil. Según versión, mapeo y mejora de bytecode, `LAZY` en ese lado no
garantiza que no se consulte nada hasta invocar el getter. No marcar
`optional=false` solo para intentar evitar consultas si el perfil realmente es opcional.

Si se permite borrar la cuenta, hay que decidir si su perfil se elimina, se
archiva o se conserva de otra forma. `orphanRemoval` y cascadas se configuran según
esa política. El helper mostrado **no cambia ni borra un perfil existente**:
una sustitución requiere otro caso de uso y respetar la FK única durante el flush.

### Variante: compartir la clave primaria con `@MapsId`

Otra solución consiste en que el perfil use el id de la cuenta como su propia PK.
No necesita un id generado independiente. Este es un **mapeo alternativo**, no
otra relación que haya que añadir además del perfil anterior:

<!-- modelo-java: SharedProfile.java -->
```java
import jakarta.persistence.*;

@Entity
@Table(name = "shared_profile_example")
public class SharedProfile {
    @Id
    @Column(name = "user_id")
    private Long id;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private PlayerAccount user;

    private String biography;

    protected SharedProfile() {}
    public SharedProfile(PlayerAccount user, String biography) {
        this.user = user;
        this.biography = biography;
    }

    public Long getId() { return id; }
    public PlayerAccount getUser() { return user; }
}
```

En el esquema, `user_id` es a la vez PK y FK. `@MapsId` comunica que el id se
obtiene de la asociación: no se le añade `@GeneratedValue`. La cuenta referenciada
debe existir o persistirse con la coordinación elegida. Tampoco este diseño obliga
a que toda cuenta tenga perfil.

## Relaciones muchos a muchos: `@ManyToMany`

### La relación tiene sus propias filas

Un usuario puede tener varios roles y un rol puede estar asignado a varios usuarios.
No se guarda una lista de ids dentro de una columna del usuario, ni se duplica
la fila del rol por cada usuario:

```text
app_user              role                    user_role
id  username          id  name                user_id  role_id
7   ana               1   PLAYER              7        1
8   luis              2   ADMIN               7        2
                                              8        1
```

`user_role` es una **tabla de unión**. Tiene dos FK y cada fila representa una
asignación. La pareja debe ser única. Se puede usar una PK compuesta para esa
pareja sin que las entidades `PlayerAccount` y `Role` tengan ids compuestos.

### Propietario y lado inverso

Añadimos a `PlayerAccount` los siguientes miembros, utilizando los imports
`jakarta.persistence.*` y `java.util.*` de la clase anterior:

<!-- miembros-java: PlayerAccount.roles -->
```java
@ManyToMany
@JoinTable(name = "user_role",
    joinColumns = @JoinColumn(name = "user_id"),
    inverseJoinColumns = @JoinColumn(name = "role_id"),
    uniqueConstraints = @UniqueConstraint(
        name = "uk_user_role", columnNames = {"user_id", "role_id"}))
private Set<Role> roles = new HashSet<>();

public void addRole(Role role) {
    Objects.requireNonNull(role, "Role is required");
    if (roles.add(role)) {
        role.addUser(this);
    }
}

public void removeRole(Role role) {
    if (roles.remove(role)) {
        role.removeUser(this);
    }
}

public Set<Role> getRoles() { return Collections.unmodifiableSet(roles); }
```

La clase del otro lado:

<!-- modelo-java: Role.java -->
```java
import jakarta.persistence.*;
import java.util.*;

@Entity
@Table(name = "role")
public class Role {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @ManyToMany(mappedBy = "roles")
    private Set<PlayerAccount> users = new HashSet<>();

    protected Role() {}
    public Role(String name) { this.name = name; }

    void addUser(PlayerAccount user) { users.add(user); }
    void removeUser(PlayerAccount user) { users.remove(user); }
    public Long getId() { return id; }
    public String getName() { return name; }
    public Set<PlayerAccount> getUsers() { return Collections.unmodifiableSet(users); }
}
```

- `PlayerAccount.roles` posee el mapeo y declara la tabla de unión.
- `joinColumns` identifica la FK del propietario; `inverseJoinColumns`, la del otro lado.
- `Role.users` usa `mappedBy="roles"`: nombra la colección Java del propietario.
- Si solo modificamos `role.users`, no hemos modificado el lado que gobierna
  la escritura de la tabla de unión.

Estos helpers trabajan con las instancias coordinadas dentro del mismo caso de
uso/contexto. No se utilizan objetos nuevos con ids copiados del JSON para
simular que hemos recuperado entidades gestionadas. La política general de igualdad
de las entidades debe diseñarse por separado.

Esquema correspondiente:

<!-- esquema-sql: user_role -->
```sql
CREATE TABLE role (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE user_role (
    user_id BIGINT NOT NULL REFERENCES app_user(id),
    role_id BIGINT NOT NULL REFERENCES role(id),
    CONSTRAINT pk_user_role PRIMARY KEY (user_id, role_id)
);
```

El `Set` evita determinadas repeticiones en esa colección Java; la PK compuesta
protege todas las escrituras a la tabla. Son responsabilidades diferentes.

### Persistir y retirar asignaciones

Sin cascadas, se persisten usuario y rol y se modifica la asociación dentro de
la transacción. En una modificación posterior se recuperan ambos como entidades
gestionadas y se llama al helper del propietario.

```java
var ana = new PlayerAccount("ana");
var luis = new PlayerAccount("luis");
var player = new Role("PLAYER");
em.persist(ana);
em.persist(luis);
em.persist(player);
ana.addRole(player);
luis.addRole(player);
```

Después, `ana.removeRole(player)` quita una asignación. **No debe eliminar el rol
PLAYER ni quitárselo a Luis.** Por eso no configuramos `CascadeType.REMOVE` ni
`ALL` indiscriminadamente. `@ManyToMany` tampoco ofrece `orphanRemoval` como las
asociaciones 1:1 y 1:N: un rol compartido no se convierte en propiedad exclusiva
de Ana por estar en su colección.

Una relación N:M solo establece vínculos de datos. Ni la anotación ni el nombre
ADMIN activan permisos: la integración con Spring Security se hará aparte.

### Cuando el vínculo necesita datos: entidad de asociación

Si queremos saber **cuándo** o **quién** concedió un rol, ya no basta con una
tabla que solo contenga la pareja de ids. El vínculo pasa a representarse con
una entidad, por ejemplo `RoleAssignment`:

<!-- modelo-java: RoleAssignment.java -->
```java
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "role_assignment_example", uniqueConstraints =
    @UniqueConstraint(name = "uk_assignment_pair", columnNames = {"user_id", "role_id"}))
public class RoleAssignment {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private PlayerAccount user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    @Column(name = "granted_at", nullable = false)
    private LocalDateTime grantedAt;

    protected RoleAssignment() {}
    public RoleAssignment(PlayerAccount user, Role role, LocalDateTime grantedAt) {
        this.user = user;
        this.role = role;
        this.grantedAt = grantedAt;
    }

    public Long getId() { return id; }
    public PlayerAccount getUser() { return user; }
    public Role getRole() { return role; }
    public LocalDateTime getGrantedAt() { return grantedAt; }
}
```

Se resuelve con dos `ManyToOne`; si hace falta navegar en sentido inverso, se
añaden colecciones `OneToMany`. El id generado no sustituye la unicidad de la
pareja si solo se admite una asignación actual por usuario y rol.

Es una **alternativa al mapeo directo**. En una aplicación real se elige cuál
gobierna esa relación; no se mantienen dos colecciones escribibles independientes
sobre las mismas filas. Los nombres terminados en `_example` permiten comparar
alternativas en el material sin confundirlas con las migraciones del aula.

### Qué comprobar en cada cardinalidad

| Caso | Evidencia necesaria |
|---|---|
| 1:1 con FK única | Perfil enlazado tras recuperar desde BD; segundo perfil para la misma cuenta rechazado |
| Cuenta sin perfil opcional | La cuenta se guarda y su perfil puede ser nulo |
| 1:1 con PK compartida | El id del perfil coincide con el de su cuenta |
| N:M | El mismo rol puede pertenecer a dos cuentas y una cuenta a varios roles |
| Retirar un rol | Desaparece solo la fila de unión; el rol y la asignación del otro usuario sobreviven |
| Entidad de asociación | Se conserva el dato del vínculo y se aplica la unicidad que corresponda |

## 7. Persistir no es lo mismo que relacionar

Sin cascada de persistencia, el caso de uso puede guardar explícitamente el hijo:

```java
@Transactional
public void registerShip(Long gameId, String name) {
    Game game = gameRepository.findById(gameId)
            .orElseThrow(() -> new GameNotFoundException(gameId));

    Ship ship = new Ship();
    ship.setShipName(name);
    // Completar y validar el resto de datos obligatorios del barco.
    game.addShip(ship);
    shipRepository.save(ship);
}
```

Es un esquema del proceso, no el reemplazo del `placeShip` real. En el proyecto
se conservan tamaño, posición, orientación, estado y validadores ya existentes.

La partida encontrada dentro de la transacción está gestionada. Si cambiamos un
atributo suyo, Hibernate puede detectar el cambio y sincronizarlo con la BD
durante el flush, sin que cada modificación exija otro `save(game)`. No confundir
esa sincronización con `clear()`, que separa las entidades del contexto.

La persistencia del barco nuevo es otra cuestión: necesita guardado explícito o
una cascada de persistencia elegida deliberadamente.

## 8. Cascadas: propagación de operaciones, no permisos mágicos

Una cascada indica qué operaciones sobre una entidad se propagan a las asociadas.

| Tipo | Idea principal |
|---|---|
| `PERSIST` | Persistir también las nuevas entidades asociadas |
| `MERGE` | Propagar la combinación de estado al contexto gestionado |
| `REMOVE` | Propagar la eliminación de entidades |
| `REFRESH` | Refrescar el estado desde la BD |
| `DETACH` | Separar también las asociadas del contexto |
| `ALL` | Reúne los tipos anteriores; incluye eliminación |

`CascadeType.ALL` no significa «hacer que la relación funcione». Añadirlo por
costumbre puede permitir un borrado no deseado. Además:

- Cascada JPA no es lo mismo que `ON DELETE CASCADE` de una FK SQL.
- Ninguna de las dos sustituye autorización o reglas de negocio.
- Una operación de baja lógica cambia atributos; no es `EntityManager.remove`.
- Una cascada desde un hijo hacia un padre compartido puede tener efectos graves:
  eliminar un barco no debe eliminar su partida, y quitar un rol a un usuario no
  debe eliminar un rol compartido.

Para la evolución inicial de Battleship es razonable mantener guardados
explícitos y **no introducir cascada de eliminación**. Una decisión distinta
necesita una política de ciclo de vida y pruebas que la sostengan.

### 8.1. `orphanRemoval` es otra decisión

`orphanRemoval=true` permite eliminar entidades hijas que dejan de estar vinculadas
al padre dentro de ese ciclo de vida. Puede ser adecuado para un detalle sin
existencia independiente, pero no equivale a «que no queden referencias colgando».

```java
@OneToMany(mappedBy = "game", orphanRemoval = true)
private List<Ship> ships = new ArrayList<>();
```

Con ese mapeo, quitar un barco de la colección gestionada puede acabar borrando
su fila al sincronizar. **No es la configuración elegida para conservar el
histórico de una partida cancelada.**

## 9. Contexto de persistencia y estados de las entidades

El contexto de persistencia mantiene y coordina las entidades gestionadas por
el proveedor. No es lo mismo que una conexión JDBC ni que la propia transacción,
aunque en nuestro uso habitual se relacionan estrechamente.

| Estado | Qué ocurre |
|---|---|
| Nueva/transitoria | El objeto existe en Java, pero todavía no está gestionado ni tiene por qué existir como fila |
| Gestionada (`managed`) | El proveedor sigue sus cambios dentro de un contexto |
| Separada (`detached`) | Conserva sus datos, pero ese contexto ya no sigue automáticamente sus cambios |
| Marcada para eliminación | Se ha solicitado su eliminación; el SQL se materializa según la sincronización |

### 9.1. Dirty checking

Si recuperamos una partida gestionada y cambiamos `active`, el proveedor puede
detectar la modificación y generar el `UPDATE` al sincronizar. No hace falta que
el setter ejecute SQL.

Si modificamos un objeto separado, el cambio no se guarda automáticamente.
`merge` combina su estado y devuelve una instancia gestionada; no convierte por
arte de magia todas las referencias antiguas en objetos gestionados. Por eso no
debemos identificar sin más `save` con «INSERT inmediato».

### 9.2. `flush` no es `commit`

- **Flush:** sincroniza cambios pendientes con la BD; puede ejecutar SQL y revelar
  una restricción incumplida.
- **Commit:** confirma la transacción. Antes puede producirse un flush.
- **Rollback:** deshace los cambios transaccionales; no implica restaurar cada
  objeto Java a sus valores anteriores.

Tras un rollback o error de persistencia no se sigue trabajando como si el mismo
contexto estuviera sano. La segunda parte muestra por qué esto importa al probar
restricciones.

## 10. Carga diferida y límite transaccional

`LAZY` permite diferir una carga hasta que hace falta. No significa «esa relación
no se consulta nunca» ni «su acceso es gratuito».

```java
Game game = gameRepository.findById(id).orElseThrow();
int count = game.getShips().size();
```

La segunda línea puede requerir acceder a la BD. Si la asociación no está
inicializada y ya no existe un contexto capaz de cargarla, puede aparecer una
`LazyInitializationException`.

### 10.1. Un servicio prepara la respuesta dentro de su unidad de trabajo

```java
@Transactional(readOnly = true)
public GameDetailDTO getDetail(Long id) {
    Game game = gameRepository.findById(id)
            .orElseThrow(() -> new GameNotFoundException(id));
    return toDetailDTO(game); // Aquí se leen las asociaciones que necesita el DTO.
}
```

El ejemplo supone un mapper y DTO definidos por la aplicación. El resultado que
sale del servicio contiene datos preparados, no un proxy que el controlador
deba completar después accidentalmente.

`readOnly=true` expresa una intención de lectura y permite optimizaciones; no
es un sistema de autorización ni una garantía universal de que escribir sea imposible.

En el uso habitual de Spring, `@Transactional` actúa mediante un proxy. Una llamada
desde otro método del mismo objeto no atraviesa ese proxy: no debemos esperar
que una autollamada abra la transacción indicada en el método interno.

### 10.2. Open Session in View no sustituye el diseño

Mantener el contexto disponible durante la capa web puede ocultar cargas tardías.
Desactivar `spring.jpa.open-in-view` ayuda a hacer explícito el límite, pero no
arregla por sí solo los mapeos. Hay que preparar en el servicio lo que necesita
la respuesta y comprobarlo con el contexto de prueba adecuado.

## 11. N+1: correcto no siempre significa eficiente

Supongamos que consultamos N partidas y luego recorremos los barcos de cada una.
Según el plan de carga, puede aparecer una consulta inicial y otras N para las
colecciones: el conocido problema **N+1**.

No todo número de consultas mayor que uno es un defecto. El problema es realizar
trabajo repetitivo que crece con los resultados sin haberlo decidido.

Formas de abordarlo, según lo que pida el endpoint:

- No cargar barcos si solo necesitamos id, estado y tamaño de tablero.
- Consultar directamente una proyección o resumen.
- Elegir un plan de carga específico con join de carga o entity graph.
- Usar otras estrategias de carga medidas cuando el caso lo necesite.

**No se resuelve poniendo todas las relaciones en `EAGER`.** Eso puede traer
datos innecesarios y seguir generando varias consultas. El bloque B desarrollará
consultas y paginación; allí también veremos por qué un fetch join de colecciones
no es una receta universal para una lista paginada.

## 12. Referencias circulares: tres problemas que se parecen

Una asociación bidireccional es un ciclo de referencias perfectamente válido en
Java y en el modelo persistente. El problema aparece cuando otra operación recorre
ese ciclo sin un criterio de terminación:

```text
Game(7) → ships → Ship(12) → game → Game(7) → ships → ...
```

**Romper el recorrido para JSON no significa borrar la FK ni eliminar uno de los
lados de la asociación JPA.** Primero se identifica qué operación está recorriendo
el modelo: conversión a JSON, métodos de objeto o carga desde la BD.

### 12.1. Serialización JSON

Si un controlador devuelve directamente una entidad bidireccional, el serializador
puede seguir `game.ships[0].game.ships…` y entrar en recursión o producir un grafo
enorme. También puede activar cargas diferidas o exponer atributos internos.

La respuesta debe diseñarse para el caso de uso:

<!-- dto-java: ShipSummaryDTO.java -->
```java
public record ShipSummaryDTO(Long id, String shipName) {}
```

<!-- dto-java: GameDetailDTO.java -->
```java
public record GameDetailDTO(Long id, String status, List<ShipSummaryDTO> ships) {}
```

Estos son **dos tipos de nivel superior, cada uno en su archivo**. El DTO del barco
no incluye otra partida completa. Un JSON válido y finito podría ser:

```json
{
  "id": 7,
  "status": "IN_PROGRESS",
  "ships": [{"id": 12, "shipName": "A"}]
}
```

Un DTO no acelera por sí solo la consulta: si su mapper navega por todas las
asociaciones de cien entidades, puede seguir provocando N+1. Contrato y plan de
carga son decisiones relacionadas, pero diferentes.

Si la petición parte de un barco, se diseña otra respuesta, sin incluir una partida
que vuelva a incluir todos los barcos:

<!-- dto-java: ShipDetailDTO.java -->
```java
public record ShipDetailDTO(Long id, String shipName, Long gameId) {}
```

```json
{"id":12,"shipName":"A","gameId":7}
```

En N:M se aplica el mismo criterio: usuario con resúmenes de roles y rol con
resúmenes de usuarios, sin que esos resúmenes vuelvan a expandir la relación.

```text
UsuarioDTO → [RolResumenDTO]              RolDTO → [UsuarioResumenDTO]
             id, nombre                              id, nombre visible
```

El mapper debe terminar deliberadamente el recorrido. Llamar recursivamente a
`toUserDTO` y `toRoleDTO` desde ambos lados reproduce el mismo ciclo aunque las
clases se llamen DTO. La entidad no se devuelve «ya convertida» por tener un record
de destino: hay que escoger y copiar los datos que corresponden.

### 12.2. Alternativas de Jackson

| Mecanismo | Qué permite | Qué no soluciona por sí solo |
|---|---|---|
| `@JsonIgnore` | Omitir un atributo de una serialización | Diseñar varios contratos distintos para distintos endpoints |
| `@JsonManagedReference` / `@JsonBackReference` | Controlar un vínculo padre/hijo evitando serializar la vuelta | Resolver toda relación N:M o todos los problemas de carga |
| `@JsonIdentityInfo` | Representar identidades y referencias dentro del JSON | Desacoplar automáticamente API y entidades |

Son herramientas válidas en contextos concretos. En nuestra API, los DTOs permiten
decidir de forma explícita qué se publica sin convertir las entidades en el
contrato de todos los endpoints.

Los siguientes modelos pequeños sirven para comparar **alternativas separadas**.
No son entidades JPA ni se añaden todos a la aplicación. Así se ve qué resuelve
Jackson sin mezclarlo con SQL o proxies de Hibernate. Cada clase pública va en su
archivo; las clases de cada pareja comparten paquete.

#### Omitir la vuelta: `@JsonIgnore`

<!-- json-java: IgnoredGame.java -->
```java
import java.util.*;

public class IgnoredGame {
    public Long id;
    public List<IgnoredShip> ships = new ArrayList<>();
}
```

<!-- json-java: IgnoredShip.java -->
```java
import com.fasterxml.jackson.annotation.JsonIgnore;

public class IgnoredShip {
    public Long id;
    @JsonIgnore
    public IgnoredGame game;
}
```

Al escribir una partida que contiene el barco y al escribir ese barco por separado:

```json
{"id":7,"ships":[{"id":12}]}
```

```json
{"id":12}
```

La vuelta a la partida se omite en ambas respuestas. La referencia Java y la FK
de una eventual entidad siguen existiendo: la anotación actúa sobre JSON.

Por defecto, ignorar una propiedad afecta también a su lectura desde JSON. No
debemos esperar que un JSON con `game` reconstruya esa referencia o consulte una
partida. Se puede utilizar `@JsonProperty` con acceso adecuado o, por ejemplo,
`@JsonIgnoreProperties(value="game", allowSetters=true)` para un caso deliberado
de escritura/lectura asimétrica. Eso no reemplaza la validación ni la recuperación
autorizada de entidades desde el servicio.

#### Pareja padre/hijo: `@JsonManagedReference` y `@JsonBackReference`

<!-- json-java: ManagedGame.java -->
```java
import com.fasterxml.jackson.annotation.JsonManagedReference;
import java.util.*;

public class ManagedGame {
    public Long id;
    @JsonManagedReference("game-ships")
    public List<ManagedShip> ships = new ArrayList<>();
}
```

<!-- json-java: ManagedShip.java -->
```java
import com.fasterxml.jackson.annotation.JsonBackReference;

public class ManagedShip {
    public Long id;
    @JsonBackReference("game-ships")
    public ManagedGame game;
}
```

Al convertir a JSON, `ships` se escribe normalmente y `ship.game` se omite:

```json
{"id":7,"ships":[{"id":12}]}
```

La diferencia importante frente a un simple ignore aparece al leer ese JSON
como `ManagedGame`: Jackson puede asignar al barco deserializado la referencia
al padre que lo contiene. Esto permite comprobar:

```java
ManagedGame restored = mapper.readValue(json, ManagedGame.class);
assertSame(restored, restored.ships.getFirst().game);
```

Ese enlace se ha reconstruido **en memoria**. Jackson no ha comprobado la FK,
consultado un repositorio ni guardado las entidades. Si escribimos el barco
aislado, su `game` también se omite; la pareja no lo transforma automáticamente
en un `gameId` visible.

Limitaciones que hay que conocer:

- Los nombres de managed y back deben coincidir dentro de cada pareja.
- Con varios vínculos distintos se usan nombres distintos: por ejemplo,
  `"game-ships"` y `"game-attacks"`. No reutilizar indiscriminadamente el nombre
  predeterminado para todas las referencias de una clase.
- La propiedad back debe ser una referencia a un objeto, **no una colección,
  un mapa o un array**. Por eso no sirve colocar back en `Role.users` para resolver
  sin más un N:M donde ambos lados son colecciones.
- No habilita por sí sola cargas `LAZY` ni evita los problemas de `equals/hashCode`.

Los términos managed/back de Jackson no indican qué lado es propietario para
JPA. `mappedBy` y la pareja de anotaciones JSON pertenecen a mecanismos diferentes.

#### Representar las repeticiones mediante ids: `@JsonIdentityInfo`

<!-- json-java: IdentityGame.java -->
```java
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import java.util.*;

@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class,
                  property = "id", scope = IdentityGame.class)
public class IdentityGame {
    public Long id;
    public List<IdentityShip> ships = new ArrayList<>();
}
```

<!-- json-java: IdentityShip.java -->
```java
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class,
                  property = "id", scope = IdentityShip.class)
public class IdentityShip {
    public Long id;
    public IdentityGame game;
}
```

La primera aparición se escribe como objeto; una referencia posterior al mismo
objeto puede escribirse como su id:

```json
{"id":7,"ships":[{"id":12,"game":7}]}
```

`scope` separa ámbitos de identidad: el id 7 de una partida no se interpreta como
el id 7 de un barco. Para `PropertyGenerator`, la propiedad debe proporcionar una
identidad útil; objetos nuevos con ids nulos no deben tratarse como un sistema
de referencias persistentes ya resuelto.

Jackson puede reconstruir referencias dentro de un documento que contiene las
identidades necesarias. **Recibir un número no le ordena buscar una fila por su
id en la BD**. Una referencia sin definición o sin resolución configurada puede
no poder leerse. `@JsonIdentityReference(alwaysAsId=true)` permite pedir solo ids
en un caso concreto, pero tampoco añade por sí sola un resolvedor de repositorio.

En N:M esta alternativa puede representar referencias sin repetir infinitamente
ambas colecciones, aunque el cliente pasa a tener que interpretar ese formato de
identidad. Los DTOs suelen proporcionar respuestas más sencillas y específicas
para un endpoint de usuarios o uno de roles.

#### Versiones y límites de estas soluciones

En Boot 4 con Jackson 3, las anotaciones de estos ejemplos siguen siendo
`com.fasterxml.jackson.annotation.*`; el mapper se encuentra en la familia
`tools.jackson.databind`, por ejemplo `tools.jackson.databind.json.JsonMapper`.
No cambiar los imports de las anotaciones solo porque cambie el del mapper.

Subir el máximo de profundidad del serializador puede retrasar el fallo, pero
no decide dónde debe terminar un ciclo. Tampoco `@Transient` de JPA es una receta
de serialización: alteraría el mapeo persistente, que es otra responsabilidad.

| Necesidad | Solución que conviene estudiar primero |
|---|---|
| Respuesta específica, estable y sin campos internos | DTO diseñado por endpoint y mapper finito |
| Omitir siempre un vínculo en ese modelo JSON | `@JsonIgnore`, entendiendo también la deserialización |
| Árbol padre/hijos con vuelta que se reconstruye al leer | Pareja managed/back con referencias compatibles y nombres claros |
| Compartir identidades dentro de un documento JSON | IdentityInfo, con ids y ámbitos definidos |
| Evitar consultas tardías o repetitivas | Transacción y plan de consulta/carga; no otra anotación de JSON |

### 12.3. Lombok y métodos de objeto

`@Data` genera, entre otras cosas, `toString`, `equals` y `hashCode`. Si esos
métodos recorren una relación bidireccional pueden provocar recursión, cargas
inesperadas o un hash que cambia mientras el objeto está en un `HashSet`.

Excluir asociaciones con `@ToString.Exclude` y `@EqualsAndHashCode.Exclude`
puede evitar recorridos concretos, pero **no define por sí solo una política de
igualdad correcta**. En entidades, es preferible elegir conscientemente qué
métodos generamos y cómo tratamos identidad e id todavía no asignado.

Cambiar `List` por `Set` tampoco garantiza unicidad en la BD. Una colección Java
y una restricción SQL protegen cosas diferentes.

Para una representación textual acotada puede utilizarse, por ejemplo:

```java
@ToString(onlyExplicitlyIncluded = true)
class GameLogView {
    @ToString.Include
    private Long id;
    @ToString.Include
    private String status;
    private List<Ship> ships; // No se recorre desde el toString generado.
}
```

Es un ejemplo de selección para Lombok, no otra entidad que haya que incorporar.
Si el campo ya está excluido de `toString`, no por ello queda excluido de Jackson
o de `equals`. Cada mecanismo tiene su configuración. Para una entidad se puede
preferir no generar esos métodos y mantener accesores y operaciones explícitos.

### 12.4. Comprobar dónde termina el recorrido

- Construir los objetos con la vuelta establecida, no una lista de hijos cuyo
  padre sea nulo: ese montaje no reproduce el problema.
- Serializar desde la partida y también desde el barco; comparar qué se omite.
- Con managed/back e identity, deserializar y comprobar la identidad de la referencia
  reconstruida, no solo que el texto «parece correcto».
- En N:M, revisar usuario con roles y rol con usuarios. No colocar back en una
  colección para intentar convertir ambos sentidos en una relación padre/hijo.
- Si se utiliza DTO, comprobar que el mapper no llama recursivamente al mapper
  completo del otro lado y que el JSON contiene solo los campos acordados.
- Probar separadamente recuperación JPA y conversión JSON: así se distingue un
  fallo de carga de un fallo de representación.

## 13. Relación y comportamiento de dominio

Las asociaciones no deben reducir las entidades a una lista de anotaciones.
`Game.cancel()` expresa una transición coherente: cambia estado, actividad y
fecha juntos, y protege las condiciones que correspondan.

El servicio coordina la operación: localiza la partida, aplica la transición y
completa el caso de uso dentro de una transacción. La entidad no tiene que
conocer `ResponseEntity`, códigos HTTP o el repositorio.

Al incorporar relaciones conservamos esa separación. No reemplazamos una regla
de negocio por una cascada, ni trasladamos al controlador el mantenimiento de
las colecciones.

### Decisiones para el histórico del juego

| Acción | Resultado esperado |
|---|---|
| Colocar barco válido | Nuevo barco asociado a la partida correcta |
| Consultar detalle | DTO finito con los datos previstos |
| Cancelar | Partida inactiva con histórico conservado |
| Consultar listado normal | No mostrar partidas inactivas |
| Acceso directo al repositorio para comprobar histórico | Las filas de partida, barcos y ataques siguen existiendo |

## 14. Diccionario de refresco

| Término | Recordatorio |
|---|---|
| FK | Restricción y columna(s) que vinculan filas entre tablas |
| Asociación | Referencia o colección que expresa un vínculo entre entidades |
| Propietario | Lado que gobierna el mapeo del vínculo; en este 1:N, el hijo con la FK |
| `mappedBy` | Nombre del atributo Java propietario visto desde el lado inverso |
| Cascada | Propagación de una operación de persistencia |
| Huérfano | Hijo desvinculado en una asociación con el ciclo de vida correspondiente |
| Contexto de persistencia | Entidades gestionadas y coordinación de su estado |
| Dirty checking | Detección de cambios en entidades gestionadas |
| Flush | Sincronización de cambios pendientes con la BD |
| Carga diferida | Recuperación de datos relacionados cuando se necesitan según el proveedor |
| DTO | Representación diseñada para la entrada o salida de un caso de uso |

## 15. Preguntas para comprobar la comprensión

Estas preguntas sirven de repaso, no constituyen otra entrega de programación:

1. ¿Puede existir una FK SQL aunque el atributo Java sea solo un `Long`?
2. ¿Por qué `mappedBy="game_id"` no es correcto si el atributo se llama `game`?
3. Si ambos lados son navegables, ¿cuántas FK necesita esta relación?
4. ¿Qué puede ocurrir si modificas solo la colección inversa?
5. ¿Por qué `CascadeType.ALL` no es una solución automática?
6. ¿Qué perderíamos si al cancelar se borrasen barcos y ataques?
7. ¿Por qué un DTO evita el ciclo JSON, pero no garantiza una consulta eficiente?
8. ¿Qué diferencia hay entre flush y commit?
9. ¿Por qué un `Set` no sustituye una restricción única?
10. ¿Qué información necesitas para decidir si hay N+1 en una respuesta?
11. ¿Qué restricción distingue el perfil 1:1 de varios perfiles para el mismo usuario?
12. ¿Por qué `optional=false` en el perfil no obliga a cada usuario a tener perfil?
13. ¿Qué fila se elimina al retirar un rol compartido, y cuál debe conservarse?
14. ¿Cuándo necesitas una entidad para la asignación en lugar de un `ManyToMany` directo?
15. ¿Por qué back no se coloca sobre la colección de usuarios de un rol?
16. ¿Qué hace identity con una referencia repetida y qué no hace con la BD?

## 16. Continuidad y referencias

- Continuar con [integridad, migraciones y pruebas](02-integridad-migraciones-y-pruebas.md).
- Para primera lectura: [versión didáctica completa](01-entidades-relaciones-y-ciclo-de-vida-didactica.md).
- Para clase: [presentación del bloque A](../../transparencias/bloque-a-modelo-relacional-e-integridad.md).
- Repaso de [arquitectura en capas](../../01-fundamentos/04a-arquitectura-en-capas.md)
  y [anotaciones](../../05-referencias/anexo-anotaciones-spring-boot.md).
- [Jakarta Persistence: especificación y documentación](https://jakarta.ee/specifications/persistence/).
- [Hibernate ORM User Guide — asociaciones](https://docs.jboss.org/hibernate/orm/current/userguide/html_single/Hibernate_User_Guide.html#associations).
- [Spring Framework — transacciones declarativas](https://docs.spring.io/spring-framework/reference/data-access/transaction/declarative.html).
- [Jackson — managed reference](https://javadoc.io/static/com.fasterxml.jackson.core/jackson-annotations/2.20/com/fasterxml/jackson/annotation/JsonManagedReference.html),
  [back reference](https://javadoc.io/static/com.fasterxml.jackson.core/jackson-annotations/2.20/com/fasterxml/jackson/annotation/JsonBackReference.html)
  e [identidad](https://javadoc.io/static/com.fasterxml.jackson.core/jackson-annotations/2.20/com/fasterxml/jackson/annotation/JsonIdentityInfo.html).

Las referencias oficiales explican el comportamiento general. La versión concreta
que utiliza el proyecto se determina por su `pom.xml`; no se cambia para seguir
un ejemplo de una documentación más reciente.
