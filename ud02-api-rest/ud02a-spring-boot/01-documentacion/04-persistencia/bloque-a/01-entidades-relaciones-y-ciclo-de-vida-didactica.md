# Relaciones entre entidades — versión didáctica

**Para estudiar por primera vez y volver a consultar cuando haga falta.**

Vamos a partir de preguntas sencillas: ¿a qué partida pertenece este barco?,
¿puede una cuenta tener dos perfiles?, ¿cómo guardamos que dos usuarios tienen
el mismo rol? Después veremos cómo escribir esas ideas con JPA y cómo devolver
los datos por HTTP sin dar vueltas infinitas entre los objetos.

Esta versión explica los mismos temas principales que la
[versión de referencia](01-entidades-relaciones-y-ciclo-de-vida.md), con un recorrido
más pausado. La referencia conserva detalles adicionales de implementación.
Los ejemplos sirven para estudiar; su incorporación al proyecto de clase se hará
por bloques, conservando lo que ya funciona.

## 1. Empecemos por algo conocido: una partida tiene barcos

Imagina estas filas en la base de datos:

**Tabla de partidas, `game`:**

| id | board_size | status |
|---|---|---|
| 7 | 8 | IN_PROGRESS |
| 8 | 10 | PENDING |

**Tabla de barcos, `ship`:**

| id | ship_name | game_id |
|---|---|---|
| 12 | A | 7 |
| 13 | B | 7 |

Los barcos A y B pertenecen a la partida 7. Lo sabemos porque su columna
`game_id` contiene el número 7.

Esa columna es una **clave foránea**, o **FK**: un dato que apunta a la clave de
otra tabla. La base de datos puede comprobar que la partida indicada existe.

Hasta ahora, en Java podíamos guardar ese vínculo como un número:

```java
private Long gameId;
```

Pero también podemos decir que el barco tiene una referencia a **un objeto
partida**:

```java
private Game game;
```

Entonces se puede escribir `ship.getGame()` y trabajar con esa partida. A esta
referencia entre entidades la llamamos **asociación**.

La columna SQL puede seguir siendo `game_id`. No estamos metiendo un objeto
Java completo dentro de una columna: Hibernate utiliza el id de la partida para
guardar el vínculo.

### Tres cosas que no conviene mezclar

- **En la BD:** tablas, filas, columnas y claves.
- **En Java:** objetos, campos, referencias y listas.
- **En la respuesta HTTP:** los datos que decidimos enviar al cliente, normalmente JSON.

Tener una referencia en Java no nos obliga a enviar todos sus datos al cliente.
Volveremos sobre ello al explicar los bucles al generar JSON.

## 2. ¿Qué es una entidad?

Una entidad es una clase cuyos objetos representan información que guardamos
con identidad propia. `Game` representa una partida; `Ship`, un barco.

```java
@Entity
@Table(name = "game")
public class Game {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
}
```

En este fragmento solo hemos dejado lo necesario para reconocer:

- `@Entity`: esta clase participa en la persistencia JPA.
- `@Table`: su tabla se llama `game`.
- `@Id`: este campo identifica la fila.
- `@GeneratedValue`: el id se genera con la estrategia indicada.

En una clase completa también estarán sus demás campos, métodos y un constructor
sin argumentos que JPA pueda utilizar. JPA es el conjunto de reglas y anotaciones
para trabajar con estos objetos persistentes; **Hibernate es la herramienta que
las interpreta en nuestro proyecto**. Spring Boot prepara buena parte de la
configuración que permite utilizarlas.

Dos partidas con el mismo tamaño y estado no son necesariamente la misma
partida. Por eso necesitamos identidad y no solo comparar todos sus datos.

## 3. Muchos barcos, una partida: `@ManyToOne`

Miremos desde **un barco**:

> Este barco pertenece a una partida. Otros barcos pueden pertenecer a esa misma partida.

Eso se escribe así en `Ship`:

```java
@ManyToOne(fetch = FetchType.LAZY, optional = false)
@JoinColumn(name = "game_id", nullable = false)
private Game game;
```

Leámoslo por partes:

1. **`ManyToOne`: muchos a uno.** Muchos barcos pueden señalar la misma partida.
2. **`JoinColumn`: dónde se guarda la referencia.** Usaremos la columna `game_id`.
3. **`optional=false`: el barco debe tener partida** en este modelo Java.
4. **`nullable=false`: queremos que la columna no admita nulos.** La migración
   de la BD debe contener también esa condición.
5. **`LAZY`: los datos de la partida pueden cargarse cuando se necesiten.**
   No hace falta estudiar todavía cómo se realiza esa carga por dentro.

Al utilizar esta referencia se sustituye el antiguo campo `Long gameId`.
No se dejan los dos campos intentando escribir la misma columna.

### ¿Qué ocurre al guardar?

Si un barco tiene como partida el objeto que representa la partida 7,
su fila se guarda con `game_id=7`. No se guarda una copia de la partida en cada barco.

Las reglas del tablero siguen siendo necesarias. Esta anotación no comprueba
que el barco quepa ni que no se cruce con otro.

## 4. Una partida, varios barcos: `@OneToMany`

Ahora miremos desde **la partida**:

> Quiero consultar los barcos que pertenecen a esta partida.

Podemos añadir a `Game`:

```java
@OneToMany(mappedBy = "game")
private List<Ship> ships = new ArrayList<>();
```

`OneToMany` significa **uno a muchos**. La lista empieza vacía porque una partida
nueva todavía puede no tener barcos.

Lo importante es leer bien `mappedBy`:

> «Esta lista corresponde a los barcos cuyo campo Java **game** apunta a esta partida».

Por eso se escribe `mappedBy="game"`, y no `mappedBy="game_id"`.

| Nombre | Dónde existe |
|---|---|
| `game` | Campo de la clase Java `Ship` |
| `game_id` | Columna de la tabla SQL `ship` |

### 4.1. Dos caminos en Java, una sola relación en la BD

```text
partida.getShips()       → barcos de la partida
barco.getGame()          → partida del barco
```

Podemos ir en los dos sentidos. Se llama relación **bidireccional**.
En SQL seguimos utilizando la misma FK de antes; no hacen falta dos columnas
ni dos relaciones independientes.

Si solo necesitáramos `barco.getGame()`, podríamos no añadir la lista de vuelta.
Se llamaría relación **unidireccional**. No es obligatorio poder recorrerla por ambos lados.

### 4.2. ¿Qué lado decide el valor de la FK?

La columna `game_id` está en la tabla del barco. Por eso, el campo `Ship.game`
es el que indica qué referencia se escribe en esa columna.

En JPA se dice que ese es el **lado propietario**. No significa «la clase más
importante» ni «donde hay que poner todas las reglas». Solo nos indica quién
controla cómo se guarda este vínculo.

La lista `Game.ships`, con `mappedBy`, es el otro lado. Sirve para consultar y
trabajar con los barcos desde la partida.

### 4.3. Mantener los dos lados de acuerdo

Si escribimos únicamente:

```java
game.getShips().add(ship);
```

la partida ve el barco, pero ¿hemos rellenado el campo `ship.game`?

Y si hacemos solo `ship.setGame(game)`, la referencia del barco puede estar bien
mientras una lista que ya teníamos en memoria sigue sin incluirlo.

Un pequeño método en `Game` puede realizar las dos operaciones:

<!-- metodo-didactico: Game.addShip -->
```java
public void addShip(Ship ship) {
    ships.add(ship);
    ship.setGame(this);
}
```

`this` es la partida sobre la que hemos llamado a `addShip`.

```java
game.addShip(ship);
```

Este método es la versión mínima para entender la idea. La versión de referencia
añade comprobaciones para no recibir un barco nulo, repetir el mismo objeto o
cambiarle de partida sin querer. **Ninguna de las dos versiones guarda una fila
por el mero hecho de modificar las referencias**: la persistencia se coordina aparte.

## 5. Una cuenta, un perfil: `@OneToOne`

Ahora cambiamos de ejemplo. Tenemos la cuenta de un jugador y, por separado,
su perfil público: nombre visible, descripción y, más adelante, avatar.

Decidimos que:

- Una cuenta puede tener **cero o un perfil**.
- Cada perfil pertenece a **una sola cuenta**.

Esto es **uno a uno**. Puede tener sentido separar los datos de cuenta de los
de presentación, pero no es obligatorio hacerlo en todas las aplicaciones.

### 5.1. Cómo lo ve la BD

**`app_user`:**

| id | username |
|---|---|
| 7 | ana |
| 8 | luis |

**`user_profile`:**

| id | user_id | display_name |
|---|---|---|
| 20 | 7 | Ana |

Ana tiene perfil y Luis todavía no. Si intentamos añadir otro perfil con
`user_id=7`, la BD debe rechazarlo.

Para conseguirlo, `user_id` tiene **dos condiciones distintas**:

1. Es una FK: la cuenta debe existir.
2. Es único: esa cuenta no puede aparecer en otro perfil.

**La FK sola no impide tener varios perfiles para Ana. Hace falta la unicidad.**

### 5.2. Cómo lo escribimos en las entidades

En `PlayerProfile`:

```java
@OneToOne(fetch = FetchType.LAZY, optional = false)
@JoinColumn(name = "user_id", nullable = false, unique = true)
private PlayerAccount user;
```

En `PlayerAccount`, si queremos consultar el perfil desde la cuenta:

```java
@OneToOne(mappedBy = "user", fetch = FetchType.LAZY)
private PlayerProfile profile;
```

Otra vez, **la FK la escribe el lado que tiene `JoinColumn`**: aquí el perfil.
El `mappedBy="user"` apunta al campo Java que acabamos de ver en `PlayerProfile`.

`optional=false` en el perfil significa «no permitimos un perfil sin cuenta».
No significa «todas las cuentas están obligadas a tener perfil».

### 5.3. Preparar ambos objetos

Un método como `account.attachProfile(profile)` establece la referencia en los
dos lados. Debe decidir qué hacer si la cuenta ya tenía otro perfil o si ese
perfil pertenecía a otra cuenta.

No basta con asignar un perfil nuevo sobre el anterior y esperar que se borre
automáticamente. Si queremos sustituirlo, hace falta definir y comprobar esa operación.

### 5.4. Otra posibilidad: usar el mismo id

En vez de un id de perfil independiente, el perfil puede utilizar el id de su
cuenta como clave primaria. Entonces la columna es a la vez PK y FK:

```text
cuenta id=7  ←  perfil user_id=7
```

`@MapsId` sirve para expresar esa elección:

```java
@Id
@Column(name = "user_id")
private Long id;

@MapsId
@OneToOne
@JoinColumn(name = "user_id")
private PlayerAccount user;
```

El id se obtiene de la cuenta; no se genera otro id distinto para el perfil.
Es una alternativa al diseño anterior, no una obligación añadida. Las clases
completas y sus detalles están en la versión de referencia.

## 6. Usuarios y roles: `@ManyToMany`

Un usuario puede tener varios roles. Un mismo rol puede estar asignado a varios usuarios.

Por ejemplo:

- Ana tiene PLAYER y ADMIN.
- Luis tiene PLAYER.

Esto es **muchos a muchos**. No queremos guardar otro rol PLAYER distinto por
cada usuario: es el mismo rol, compartido mediante varias asignaciones.

### 6.1. Hace falta una tabla entre las dos

**Usuarios:**

| id | username |
|---|---|
| 7 | ana |
| 8 | luis |

**Roles:**

| id | name |
|---|---|
| 1 | PLAYER |
| 2 | ADMIN |

**Asignaciones, `user_role`:**

| user_id | role_id |
|---|---|
| 7 | 1 |
| 7 | 2 |
| 8 | 1 |

Cada fila de `user_role` dice «este usuario tiene este rol». Tiene dos FK:
una a usuarios y otra a roles. La pareja no debe repetirse.

No estamos añadiendo una tercera entidad Java necesariamente: con `ManyToMany`,
JPA puede gestionar esa tabla a partir de las dos colecciones.

### 6.2. Las dos colecciones

En `PlayerAccount` elegimos declarar cómo se guarda la tabla intermedia:

```java
@ManyToMany
@JoinTable(name = "user_role",
    joinColumns = @JoinColumn(name = "user_id"),
    inverseJoinColumns = @JoinColumn(name = "role_id"))
private Set<Role> roles = new HashSet<>();
```

En `Role`:

```java
@ManyToMany(mappedBy = "roles")
private Set<PlayerAccount> users = new HashSet<>();
```

Leamos los nombres sin saltarnos pasos:

- `JoinTable`: la tabla que contiene las asignaciones se llama `user_role`.
- `joinColumns`: la columna que apunta al usuario, donde hemos escrito esta anotación.
- `inverseJoinColumns`: la columna que apunta al rol.
- `mappedBy="roles"`: el otro lado está explicado por el campo `roles` del usuario.

En una relación N:M no hay un padre y un hijo naturales. **Hemos elegido al usuario
como propietario del mapeo** al poner allí `JoinTable`. Podríamos diseñarlo desde
el otro lado, pero no declarar ambos como propietarios independientes.

### 6.3. Asignar y quitar no es borrar el rol

Un método `ana.addRole(player)` añade el rol a la colección de Ana y añade a Ana
a la colección de usuarios de ese rol. El método de quitar hace la operación inversa.

Si Ana deja de tener PLAYER, se elimina esta fila:

```text
user_role: (7, 1)
```

No se elimina el rol PLAYER de la tabla `role`, ni la asignación `(8,1)` de Luis.
Esta es la razón para no poner `CascadeType.ALL` por costumbre: incluye borrado
y podría intentar eliminar objetos que siguen siendo compartidos.

Un `Set` es una colección que evita repeticiones según la igualdad de sus
elementos. Ayuda en Java, pero **la BD debe proteger también la pareja de ids**,
por ejemplo con una clave primaria compuesta. No son la misma comprobación.

### 6.4. ¿Y si queremos guardar cuándo se asignó el rol?

La relación empieza a tener datos propios:

```text
usuario 7  —  rol 1  —  concedido el 2026-11-05
```

Ahora interesa una entidad como `RoleAssignment`, con referencias al usuario y
al rol y un campo para la fecha:

```java
@ManyToOne
private PlayerAccount user;

@ManyToOne
private Role role;

private LocalDateTime grantedAt;
```

No lleva un `ManyToMany` dentro: cada asignación pertenece a un usuario y a un rol.
Resolvemos la relación con **dos muchos-a-uno**. Si se necesita, se añaden listas
de asignaciones desde usuario y rol.

Esta entidad sustituye el modo de gestionar ese vínculo. No debemos mantener
dos mecanismos independientes escribiendo la misma relación sin coordinación.

## 7. Guardar objetos y propagar operaciones

Tener referencias bien colocadas no equivale a guardar los objetos nuevos.

Podemos hacerlo expresamente en el servicio. En este fragmento, `em` es un
`EntityManager`: el componente de JPA al que podemos
pedir que guarde, consulte o gestione entidades.

```java
em.persist(account);
em.persist(profile);
```

O decidir que ciertas operaciones realizadas sobre uno se propaguen al otro.
Eso se llama **cascada**.

| Si configuramos… | Estamos diciendo… |
|---|---|
| `PERSIST` | Al persistir este objeto, persiste también los nuevos objetos relacionados por aquí |
| `MERGE` | Propaga la operación de combinar estado con el contexto de persistencia |
| `REMOVE` | Al eliminar este objeto, elimina también los relacionados por aquí |
| `ALL` | Propaga todas esas operaciones y otras, incluido el borrado |

No se elige ALL porque sea «más completo». Se eligen las operaciones que
corresponden a la vida de esos datos.

### 7.1. ¿Qué significa `orphanRemoval`?

Imagina un detalle que solo tiene sentido mientras pertenece a su objeto principal.
Con `orphanRemoval=true`, quitarlo de ese vínculo puede hacer que se elimine su fila.

No significa «arreglar todas las referencias» ni «evitar bucles». Es una decisión
de **borrado de datos**.

En Battleship cancelar conserva la partida, sus barcos y sus ataques. Por tanto,
no vamos a vaciar una colección con eliminación de huérfanos para representar
esa cancelación. Cancelar cambia estado, actividad y fecha; no borra el historial.

## 8. Cuándo se guardan los cambios

Una **transacción** reúne operaciones que deben confirmarse juntas o deshacerse
si algo falla. Por ejemplo, no queremos dar una operación por terminada si solo
se ha guardado la mitad de lo que necesitaba. Sus límites se suelen definir en
el servicio con `@Transactional`.

Hay una diferencia importante entre estos dos objetos:

1. Un objeto que acabamos de crear con `new` y todavía no hemos persistido.
2. Una entidad que Hibernate ha recuperado y está siguiendo dentro de una operación.

Se dice que la segunda está **gestionada**. Hibernate puede recordar sus datos,
detectar que hemos cambiado algo y preparar la actualización de la fila.
Ese seguimiento se llama **dirty checking**.

```java
game.cancel();
```

Si la partida está gestionada en una transacción, sus cambios pueden acabar en
un UPDATE sin que cada setter llame a la BD ni tengamos que añadir un `save` a
cada línea de código.

### Tres palabras que aparecerán en los tests

| Palabra / método | Explicación |
|---|---|
| `flush()` | Envía a la BD los cambios pendientes; puede aparecer aquí un error de restricción |
| commit | Confirma la transacción; antes puede haber un flush |
| rollback | Deshace los cambios transaccionales de esa operación |
| `clear()` | Hace que el contexto deje de seguir esas instancias; no vacía las tablas |

**Flush y clear no son lo mismo.** Para comprobar que algo se ha guardado, un
test puede sincronizar primero, separar los objetos con clear y recuperarlos de
nuevo desde la BD.

Un objeto separado conserva sus datos Java, pero cambiarlo ya no significa que
ese contexto vaya a guardar automáticamente el cambio. La referencia explica
los estados de las entidades y `merge` con más detalle.

## 9. Cargar datos cuando se necesitan: `LAZY` y `EAGER`

Podemos pedir una partida sin necesitar todavía todos sus barcos. Con carga
**LAZY**, Hibernate puede esperar a que usemos esa información para consultarla.
Con **EAGER**, la asociación debe estar disponible como parte de esa recuperación,
aunque no necesariamente mediante una única consulta SQL.

```java
Game game = gameRepository.findById(id).orElseThrow();
int numberOfShips = game.getShips().size();
```

La segunda línea puede necesitar una consulta adicional.

### 9.1. ¿Por qué a veces da error al acceder a una lista?

Si Hibernate ya no tiene disponible el contexto que necesitaba para cargarla,
el acceso a una relación no inicializada puede fallar. El mensaje puede hablar
de `LazyInitializationException`.

No significa que la lista esté mal declarada. Hay que revisar **dónde y cuándo
se están preparando los datos**.

En nuestra API, el servicio puede preparar la respuesta dentro de su transacción:

```java
@Transactional(readOnly = true)
public GameDetailDTO getDetail(Long id) {
    Game game = findGame(id);
    return toDetailDTO(game);
}
```

`toDetailDTO` lee allí lo que necesitará el cliente. El controlador recibe datos
ya preparados, sin tener que seguir recorriendo entidades por sorpresa.

`readOnly=true` indica que el caso se plantea como lectura y puede permitir
optimizaciones; no es un permiso de seguridad. `@Transactional` tampoco actúa
por escribirlo en cualquier objeto creado con `new`: en el uso de Spring debe
intervenir su infraestructura.

### 9.2. ¿Por qué no ponemos todo EAGER?

Porque podríamos cargar muchos datos que el cliente no utiliza. Y cambiar a EAGER
no garantiza que todas las consultas se conviertan en una sola.

Si pedimos una lista de diez partidas y luego se lanza otra consulta para los
barcos de cada una, podemos acabar con una consulta inicial más diez adicionales.
Este patrón se conoce como **N+1**.

Primero decidimos qué datos necesita esa pantalla o endpoint. A veces basta un
resumen; otras veces hay que pedir expresamente datos relacionados. En el bloque B
veremos consultas y paginación para aplicar esa decisión.

En un 1:1 opcional visto desde la cuenta, Hibernate puede necesitar averiguar si
existe perfil. Es otro motivo para mirar el SQL real y no prometer «ninguna
consulta adicional» solo porque una anotación diga LAZY.

## 10. El bucle al convertir entidades a JSON

**Serializar** significa convertir el objeto Java en una representación como JSON.
**Deserializar** es el camino de vuelta: leer JSON y crear/rellenar objetos Java.

Supongamos que tenemos los dos lados de la relación:

```text
la partida conoce sus barcos
cada barco conoce su partida
```

Si el conversor intenta escribir todos los datos de todos los objetos, puede
ocurrir esto:

```text
Escribo la partida 7
  Escribo sus barcos
    Escribo el barco 12
      Escribo su partida 7
        Escribo sus barcos
          ...
```

No está fallando la FK. Tampoco sobra necesariamente la relación de vuelta.
**Nos falta decidir dónde terminamos al escribir el JSON.** El conversor puede
acabar dando un error de profundidad o de recursión en lugar de una respuesta útil.

Para ver el problema hay que preparar objetos que realmente se señalen en ambos
sentidos. Si `ship.game` está a null, no hemos reproducido ese recorrido.

## 11. Primera solución: decidir qué datos enviamos con un DTO

Un **DTO** es un tipo que contiene los datos elegidos para una entrada o salida.
No necesita tener todas las referencias de una entidad.

Para consultar una partida, decidimos enviar:

```json
{
  "id": 7,
  "status": "IN_PROGRESS",
  "ships": [{"id": 12, "shipName": "A"}]
}
```

El resumen del barco no lleva otra partida completa. El recorrido termina ahí.

Para consultar un barco podemos elegir otra respuesta:

```json
{"id":12,"shipName":"A","gameId":7}
```

La partida se representa solo con su id. No hemos borrado ninguna referencia de
las entidades ni ninguna relación de la BD.

### Lo mismo para usuarios y roles

- Al consultar un usuario, devolvemos sus datos y una lista de roles resumidos.
- Al consultar un rol, podemos devolver sus datos y usuarios resumidos.
- El resumen del rol no vuelve a contener todos los usuarios, ni al revés.

Hay que preparar esos DTOs con ese límite. Si el método que convierte usuarios
llama al que convierte roles completos, y ese vuelve a convertir usuarios
completos, **hemos trasladado el bucle al código del conversor**.

Esta suele ser la primera opción de nuestra API: decidir una respuesta para cada
necesidad. También debemos cuidar las consultas del mapper: un DTO no hace que
la BD trabaje menos por arte de magia.

## 12. Otras soluciones: las anotaciones de Jackson

Jackson es la biblioteca que utilizamos para convertir muchos de estos objetos
a JSON y leerlos de vuelta. Tiene herramientas para controlar las referencias.
Vamos a compararlas por separado; **no hay que acumularlas todas sobre el mismo campo**.

### 12.1. `@JsonIgnore`: no escribas esa propiedad

Si omitimos la referencia a la partida dentro del barco:

```java
@JsonIgnore
private Game game;
```

al escribir la partida aparecerán sus barcos, pero esos barcos no volverán a
escribir la partida. En este ejemplo reducido a ids:

```json
{"id":7,"ships":[{"id":12}]}
```

Si escribimos el barco por separado, la propiedad `game` también se omite:

```json
{"id":12}
```

**No se transforma automáticamente en `gameId`. Se omite.**

Por defecto, esa propiedad también se ignora al leer JSON. Si leemos una partida
con barcos, no debemos esperar que ignore cree por su cuenta la referencia de
cada barco a su padre. Se pueden configurar otras reglas de lectura, pero deben
ser una elección expresa. Ninguna de ellas busca una entidad en un repositorio
simplemente porque el cliente haya enviado un id.

### 12.2. Managed/back: escribir los hijos sin volver al padre

Podemos marcar la lista que se escribe y la referencia que no debe escribirse:

```java
// En Game
@JsonManagedReference("game-ships")
private List<Ship> ships;

// En Ship
@JsonBackReference("game-ships")
private Game game;
```

El JSON de la partida vuelve a ser finito:

```json
{"id":7,"ships":[{"id":12}]}
```

Lo nuevo aparece al **leerlo de vuelta**. Cuando Jackson crea la partida y sus
barcos, esta pareja le permite colocar en cada barco la referencia al padre que
lo contiene. Así, el objeto partida creado y el `game` de su barco pueden ser
la misma instancia Java.

Eso no ha guardado nada en SQL. Solo ha reconstruido referencias en memoria.

#### Dos detalles que evitan muchos errores

1. Las dos anotaciones de una pareja tienen el **mismo nombre**.
2. Las parejas distintas tienen **nombres distintos**. Si hay barcos y ataques,
   podemos usar `"game-ships"` y `"game-attacks"`.

Además, back se utiliza sobre una **referencia a un objeto**, como `Game game`.
No se puede poner sobre una colección de usuarios para resolver directamente
un N:M de usuarios y roles. Allí los dos lados son colecciones y hace falta
otra representación, por ejemplo DTOs o identidad JSON.

Estos nombres de Jackson no deciden quién escribe la FK. Eso sigue siendo una
cuestión del mapeo JPA, con JoinColumn/JoinTable y mappedBy.

### 12.3. `@JsonIdentityInfo`: si el objeto se repite, utiliza su id

Otra opción es escribir un objeto completo la primera vez y, cuando vuelva a
aparecer, representarlo por su identificador.

```java
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class,
                  property = "id", scope = Game.class)
```

Aplicando la configuración correspondiente a los tipos del ejemplo, podemos obtener:

```json
{"id":7,"ships":[{"id":12,"game":7}]}
```

El 7 del barco significa «la partida que ya hemos representado con id 7».
El objeto no se vuelve a desplegar entero.

Cada tipo puede tener su propio ámbito de identidad, indicado con `scope`.
Así no se mezclan el id 7 de una partida y el id 7 de un barco. También necesitamos
ids que sirvan como referencias: un id todavía nulo no identifica un objeto
persistente que podamos dar por resuelto.

Al leer el documento, Jackson puede reconstruir referencias a los objetos
definidos en él. **No interpreta `game:7` como una orden para consultar la BD.**
Si ese objeto no está definido ni hay una resolución preparada, el número solo
no basta para reconstruirlo.

Con `@JsonIdentityReference(alwaysAsId=true)` se puede pedir que una referencia
se escriba siempre como id. Eso tampoco configura una búsqueda de repositorio.

### 12.4. Qué elegir

| Necesitamos… | Primera opción que estudiar |
|---|---|
| Una respuesta clara para cada endpoint | DTOs con los datos elegidos |
| Que una propiedad no forme parte de ese JSON | JsonIgnore, revisando también qué pasa al leer |
| Padre con hijos y una vuelta al padre que se reconstruye al leer | Pareja managed/back |
| Referencias entre objetos identificados dentro de un documento | JsonIdentityInfo |

No arreglamos un recorrido infinito aumentando el límite de profundidad del
conversor. Eso solo permite dar más vueltas antes del fallo.

En el entorno actual, el mapper de Jackson 3 utiliza paquetes `tools.jackson.databind`,
pero estas anotaciones mantienen `com.fasterxml.jackson.annotation`. No son un
error de importación por el hecho de empezar de forma distinta.

## 13. Otros bucles que no son de JSON

### 13.1. `toString`, `equals` y `hashCode`

Si Lombok genera un `toString` que incluye todos los campos, puede intentar
imprimir la partida, luego los barcos, luego su partida… El problema puede
aparecer al mirar un log, aunque no estemos devolviendo ninguna respuesta HTTP.

Excluir una relación de `toString` puede evitar ese recorrido. También podemos
elegir explícitamente imprimir solo id y estado.

`equals` decide si dos objetos se consideran iguales. `hashCode` produce un valor
que ayuda a localizar objetos en estructuras como `HashSet` o `HashMap`; debe
ser coherente con esa igualdad. Si el cálculo cambia mientras el objeto está
guardado en un conjunto, podemos tener problemas para encontrarlo o eliminarlo.

Al decidirlos para entidades, incluir listas y referencias de vuelta puede
recorrer el mismo ciclo. Usar un id generado sin pensar tampoco basta: antes
de guardar, varios objetos nuevos pueden tener el id a null.

**Excluir un campo de Jackson no lo excluye de los métodos que genera Lombok.**
Y excluirlo de Lombok no cambia automáticamente el JSON.

### 13.2. Una relación que necesita otra consulta

Que falle el acceso a una lista LAZY fuera de su contexto es un problema distinto.
Puede ocurrir aunque el JSON no tenga ningún ciclo.

La pregunta en ese caso es: «¿hemos preparado los datos mientras podíamos
cargarlos?». No: «¿qué anotación de Jackson pongo para que desaparezca el error?».

## 14. Cómo comprobar que hemos entendido la solución

No basta con que deje de salir una excepción. Hay que comprobar el resultado:

- Guardar la relación y recuperar los objetos de la BD para ver que sigue allí.
- En 1:1, intentar guardar dos perfiles para la misma cuenta y comprobar el rechazo.
- En N:M, quitar un rol a Ana y comprobar que el rol y la asignación de Luis siguen ahí.
- Convertir a JSON desde los dos extremos: partida y barco, o usuario y rol.
- Si usamos managed/back o identidad, leer el JSON y comprobar qué referencias
  se reconstruyen realmente.
- Si usamos DTOs, comprobar sus campos y que el conversor termina donde habíamos decidido.

Estos son criterios para las pruebas y para revisar lo que ya hacemos, no una
colección de nuevas prácticas independientes.

## 15. Resumen para volver a consultar

| Término que verás | Idea que hay detrás |
|---|---|
| ManyToOne | Muchos objetos pueden apuntar al mismo objeto |
| OneToMany | Desde uno puedo consultar sus varios objetos relacionados |
| OneToOne | La relación no permite varios elementos del otro tipo para el mismo vínculo |
| ManyToMany | Ambos lados pueden estar relacionados con varios elementos |
| Propietario | Lado que declara cómo se escribe el vínculo |
| mappedBy | Nombre del campo Java que ya declara ese vínculo en el otro lado |
| JoinColumn | Columna utilizada para una FK |
| JoinTable | Tabla que guarda las parejas de una relación |
| Helper | Método que reúne pasos que deben hacerse juntos, como actualizar ambos lados |
| Cascada | Propagar ciertas operaciones a objetos relacionados |
| orphanRemoval | Eliminar el hijo cuando se retira del vínculo según ese ciclo de vida |
| Entidad gestionada | Objeto cuyos cambios Hibernate está siguiendo |
| Dirty checking | Detectar cambios en esas entidades |
| Flush | Enviar los cambios pendientes a la BD |
| DTO | Datos que hemos elegido para una entrada o salida |
| Serializar / deserializar | Convertir a JSON / leer JSON y reconstruir objetos |

### Preguntas para el repaso

1. Si `mappedBy` dice `game`, ¿busca una columna o un campo Java?
2. ¿Por qué una FK no impide que Ana tenga dos perfiles si no añadimos unicidad?
3. ¿Dónde se guarda que Ana y Luis tienen PLAYER?
4. ¿Qué fila quitamos cuando Ana deja de tenerlo?
5. ¿Qué cambia si necesitamos la fecha de esa asignación?
6. ¿Qué diferencia hay entre quitar un elemento de una colección y borrar su fila?
7. ¿Un DTO evita el bucle si sus dos conversores se llaman entre sí sin terminar?
8. ¿Qué información se pierde al poner JsonIgnore sobre la vuelta al padre?
9. ¿Qué reconstruye managed/back al leer y qué no hace con la BD?
10. ¿Por qué las dos colecciones de un N:M no se resuelven colocando back en una de ellas?

## Para continuar

- [Versión de referencia: clases completas, esquema y alternativas](01-entidades-relaciones-y-ciclo-de-vida.md).
- [Parte 2: integridad, migraciones y pruebas](02-integridad-migraciones-y-pruebas.md).
- [Presentación del bloque A](../../transparencias/bloque-a-modelo-relacional-e-integridad.md).
- [Diccionarios de testing](../../03-pruebas/diccionarios/README.md).
- [Lombok y código generado](../../05-referencias/anexo-lombok.md).

Las fuentes técnicas y las precisiones de implementación están enlazadas en la
versión de referencia. Podemos empezar por esta explicación y acudir a aquella
cuando necesitemos profundizar en una decisión concreta.
