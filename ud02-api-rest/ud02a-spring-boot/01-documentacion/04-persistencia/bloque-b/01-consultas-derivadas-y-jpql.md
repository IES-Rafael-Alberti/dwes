# Consultas con Spring Data JPA: del nombre del método a JPQL

Una API necesita preguntar por **algunos** registros, no cargar siempre todos.
En este apunte aprenderás dos formas de expresar esas preguntas en un repositorio:
consultas derivadas y consultas escritas con JPQL. Primero entenderemos qué se
pide y qué resultado esperamos; después veremos el código y cómo comprobarlo.

**Punto de partida:** conoces una entidad JPA, un repositorio que extiende
`JpaRepository` y las operaciones básicas `save`, `findById` y `findAll`.
No necesitas conocer Specifications, proyecciones ni optimización de consultas.

**Recorrido de clase:** apartados 1–5 y las dos pruebas del 7. Las convenciones
de nombres SQL se explican en el 2.1; el 6 sirve como referencia para construir
nombres de consultas derivadas. El 9 orienta ampliaciones posteriores.

## 1. El problema: `findAll()` no responde a todas las preguntas

En Battleship tenemos partidas con tamaño de tablero, estado y un indicador
`active`. Algunas preguntas habituales son:

- ¿Qué partidas activas están pendientes?
- ¿Qué partidas activas tienen un tablero de tamaño 10 o mayor?

Podríamos obtener `findAll()` y filtrar la lista en Java. Pero eso trae a la
aplicación filas que quizá no necesita. Una consulta de repositorio expresa las
condiciones para que la base de datos seleccione el resultado.

Esto **no crea un endpoint**. El repositorio consulta datos; el servicio decide
cuándo necesita esos datos y el controlador define qué se publica por HTTP.

### Tres piezas que no debemos confundir

| Pieza | Responsabilidad en estos ejemplos |
|---|---|
| Spring Data JPA | Crea la implementación del repositorio y resuelve sus métodos de consulta |
| JPA y su proveedor, Hibernate en el proyecto | Mapean entidades, preparan y ejecutan las consultas contra el motor |
| H2 o PostgreSQL | Ejecutan el SQL sobre las tablas y devuelven los resultados |

No escribimos una implementación de `GameRepository` ni un bucle que recorra la
base de datos. Sí debemos especificar correctamente la pregunta.

## 2. El vocabulario del ejemplo: entidad no es tabla

Usaremos el modelo de `bshipAlong`:

| En Java | Tipo o significado | En la base de datos del proyecto |
|---|---|---|
| `Game` | Entidad que representa una partida | Tabla `game` |
| `id` | `Long`, identificador | Columna `id` |
| `boardSize` | `int`, lado del tablero | Columna `board_size` |
| `status` | Enum `GameStatus` | Columna `status`, almacenada como texto |
| `active` | `boolean`, partida activa/no cancelada | Columna `active` |

Las consultas derivadas y JPQL utilizan **propiedades y entidades Java**.
SQL utiliza **tablas y columnas**. El mapeo JPA conecta los dos mundos: no hay
que cambiar `boardSize` por `board_size` en una consulta JPQL.

### 2.1. Convenciones para nombres de tablas y columnas

Hay tres niveles que conviene separar:

| Nivel | Convención habitual | Ejemplo |
|---|---|---|
| Java | Clases en `UpperCamelCase`; atributos en `lowerCamelCase` | `Game`, `boardSize` |
| Nombre lógico JPA | Se deduce del mapeo o se indica con anotaciones | Nombre de entidad, tabla o columna |
| Nombre físico SQL | Lo determina la estrategia de nombres de Hibernate y cómo lo trata el motor | `game`, `board_size` |

Sin nombres explícitos, JPA toma por defecto el nombre simple de la clase como
nombre de entidad y el nombre de entidad como nombre de tabla; para una columna
básica, toma el nombre del atributo persistente. **Eso no implica que todas las
aplicaciones JPA conviertan automáticamente camelCase a snake_case.**

En Hibernate, la estrategia **implícita** deduce un nombre lógico cuando no lo
hemos indicado; la estrategia **física** transforma ese nombre lógico en el
identificador usado en SQL, tanto si se dedujo como si se indicó explícitamente.

Con la configuración por defecto de Spring Boot con Hibernate que usamos, la
estrategia física convierte camelCase a nombres con guiones bajos y los pasa a
minúsculas. Por ejemplo, una clase hipotética `GameSession` daría `game_session`
y su atributo `createdAt` daría `created_at`. No se añade una `s` para pluralizar:
`Game` no se convierte automáticamente en `games`.

| Nombre Java | Nombre SQL esperado con esa estrategia, sin sobrescrituras |
|---|---|
| `Game` | `game` |
| `GameSession` (ejemplo hipotético) | `game_session` |
| `boardSize` | `board_size` |
| `createdAt` | `created_at` |
| `active` | `active` |

La estrategia se puede cambiar mediante
`spring.jpa.hibernate.naming.implicit-strategy` y
`spring.jpa.hibernate.naming.physical-strategy`. Por eso, al leer otro proyecto,
hay que comprobar sus anotaciones y configuración, no trasladar estas reglas
como si fueran universales de Java o de SQL.

### 2.2. Nombres explícitos: entidad, tabla, columna y clave foránea

Estas anotaciones pertenecen a `jakarta.persistence`:

| Anotación | Qué nombra | Qué no cambia |
|---|---|---|
| `@Entity(name = "Match")` | Nombre de la entidad que se usa en JPQL | Nombre de la clase Java; con `@Table` explícito, tampoco ese nombre de tabla |
| `@Table(name = "game")` | Nombre lógico de la tabla | Nombre de entidad usado por JPQL |
| `@Column(name = "board_size")` | Nombre lógico de una columna básica | Atributo Java `boardSize` |
| `@JoinColumn(name = "game_id")` | Columna de unión, normalmente una clave foránea en el lado propietario | Propiedad Java `game` ni el recorrido `game.id` |

No hay que añadir estas anotaciones para seguir la práctica: sirven para leer
el mapeo existente. En el proyecto, `Game.boardSize` ya declara
`@Column(name = "board_size")`. En la asociación de `Ship` con `Game`,
`@JoinColumn(name = "game_id")` identifica la columna que guarda la referencia
a la partida. Un nombre de clave foránea no se obtiene necesariamente añadiendo
`_id` al nombre de la clase: en un mapeo implícito influyen la propiedad de la
asociación y la columna referenciada.

Los nombres explícitos también pasan por la estrategia física de Hibernate;
no son una garantía universal de conservar mayúsculas tal cual. Para esta
práctica usamos identificadores sencillos en minúsculas y snake_case, sin
depender de nombres entrecomillados ni de diferencias de mayúsculas del motor.

**Ejemplo de contraste, no cambio del modelo:** si a `Game` le pusiéramos
`@Entity(name = "Match")` manteniendo `@Table(name = "game")`, JPQL usaría
`from Match`, SQL seguiría consultando `game` y la consulta derivada seguiría
utilizando las propiedades de la clase `Game`. No haría falta renombrar
`findByBoardSizeGreaterThanEqual`.

En `bshipAlong`, **Flyway crea el esquema y `ddl-auto: validate` lo comprueba**.
Cambiar un atributo o una anotación no renombra una columna de una base existente
ni sustituye una migración. Para comprobar el nombre real, contrastar entidad,
configuración y migraciones; no activar `ddl-auto: update` para ocultar una
discrepancia.

## 3. Consultas derivadas: la pregunta está en el nombre

Una **consulta derivada** es un método cuya consulta Spring Data obtiene del
nombre. No lleva una implementación ni necesita `@Query`.

### 3.1. Un primer ejemplo pequeño

En un repositorio de tareas, suponiendo que la entidad `Task` tiene una propiedad
booleana `done`, la declaración sería:

```java
List<Task> findByDone(boolean done);
```

Se lee «buscar tareas cuyo `done` sea igual al argumento». Con `true`, tareas
completadas; con `false`, tareas no completadas. No es un método de Battleship
que haya que copiar: sirve para aprender la forma `findBy` + propiedad.

### 3.2. Primera pregunta de Battleship

**Archivo del proyecto de aula:**
`src/main/java/es/daw2a/bshipalong/repository/GameRepository.java`.
Dentro de su interfaz, el método es:

```java
List<Game> findByActiveTrueAndStatus(GameStatus status);
```

| Fragmento | Qué pide |
|---|---|
| `find` | Recuperar entidades; aquí se devuelven en una lista |
| `By` | Comenzar las condiciones |
| `ActiveTrue` | Que la propiedad `active` sea `true` |
| `And` | Que también se cumpla la condición siguiente |
| `Status` | Que `status` coincida con el argumento recibido |

Solo hay **un argumento**: `true` ya está fijado en el nombre, pero el estado
lo proporciona quien llama al método:

```java
games.findByActiveTrueAndStatus(GameStatus.PENDING);
```

Este fragmento es una llamada desde un servicio o test que ya dispone del
repositorio `games`; no va dentro de la interfaz.

### 3.3. Predecir antes de ejecutar

Supongamos estos datos de prueba:

| Partida | `active` | `status` | `boardSize` |
|---|---|---|---|
| A | `true` | `PENDING` | 8 |
| B | `true` | `IN_PROGRESS` | 12 |
| C | `false` | `CANCELLED` | 15 |

Al pedir `PENDING`, entra **A**. B no tiene ese estado y C tampoco está activa.
Al pedir `IN_PROGRESS`, entra **B**. Si no hay coincidencias, obtenemos una lista
vacía, no hay que inventar una partida ni lanzar un 404 desde el repositorio.

### 3.4. El nombre no es texto libre

`findByActiveTrueAndStatus` funciona porque existen `active` y `status`.
Si escribimos una propiedad inexistente, Spring Data no puede construir esa
consulta: puede fallar la creación del repositorio al cargar el contexto.

Tampoco basta con inventar un nombre como `findActiveWithMinimumBoardSize` y
esperar que Spring interprete una frase libre. Para ese nombre escribiremos
explícitamente la consulta con `@Query` en el apartado siguiente.

## 4. JPQL: escribir la pregunta sobre las entidades

**JPQL** significa *Java Persistence Query Language*. Es un lenguaje de consulta
de JPA con una sintaxis parecida a SQL, pero sus nombres corresponden al modelo
Java. El proveedor JPA lo traduce al SQL que ejecutará la base de datos.

Para preguntas sencillas, un nombre derivado suele bastar. Cuando la condición
se entiende mejor escrita como una consulta, podemos declararla con `@Query`.
No es que una técnica sea «profesional» y la otra no: elegimos la más legible
para la pregunta que necesitamos resolver.

### 4.1. Segunda pregunta: partidas activas con un tamaño mínimo

**En el mismo `GameRepository.java`**, la declaración es:

```java
@Query("select g from Game g where g.active = true and g.boardSize >= :minSize")
List<Game> findActiveWithMinimumBoardSize(@Param("minSize") int minSize);
```

Lectura por partes:

| Fragmento | Significado |
|---|---|
| `from Game g` | Consultar la entidad `Game` y darle el alias `g` |
| `select g` | Devolver las entidades que cumplan las condiciones |
| `where` | Introducir el filtro |
| `g.active = true` | Exigir que la partida esté activa |
| `and` | Exigir también la condición siguiente |
| `g.boardSize >= :minSize` | Comparar el tamaño con un parámetro recibido |

El alias `g` solo es un nombre corto dentro de la consulta; no es una tabla ni
una variable Java que tengamos que declarar en el servicio.

### 4.2. ¿Cómo entra el valor del parámetro?

`@Param("minSize")` conecta el argumento Java con `:minSize` en JPQL:

```java
games.findActiveWithMinimumBoardSize(10);
```

Con los datos A, B y C del apartado 3.3, solo entra **B**: A tiene tamaño menor
que 10 y C está inactiva, aunque su tamaño sea mayor. El límite es inclusivo:
una partida activa de tamaño exactamente 10 también entraría.

No concatenar el valor del usuario dentro del texto de la consulta. Se mantiene
la consulta fija y se enlaza el argumento como parámetro. El nombre escrito en
`@Param` debe coincidir con el marcador de JPQL; no son dos etiquetas arbitrarias.

### 4.3. Comparación con SQL

JPQL del repositorio:

```java
@Query("select g from Game g where g.active = true and g.boardSize >= :minSize")
```

SQL equivalente de forma conceptual, con un parámetro JDBC `?`:

```sql
SELECT *
FROM game
WHERE active = TRUE AND board_size >= ?;
```

No es una promesa del SQL exacto que imprimirá Hibernate: puede seleccionar
columnas concretas, introducir alias y adaptar la sintaxis al motor.
La distinción que importa aquí es `Game.boardSize` frente a `game.board_size`.

`@Query` usa JPQL por defecto. Con `nativeQuery = true` se pide SQL nativo,
una alternativa que no necesitamos para estas dos preguntas.

### 4.4. Reglas básicas de JPQL y diferencias con SQL

Esta referencia amplía la primera consulta, sin añadir pasos obligatorios al
code-along. Los fragmentos siguientes son **consultas JPQL de ejemplo**, no
métodos que haya que incorporar al repositorio del apartado 5.

#### Estructura y nombres

```sql
select g
from Game g
where g.active = true
order by g.boardSize desc, g.id asc
```

El orden de escritura es `select`, `from`, `where`, `group by`, `having` y
`order by`; se omiten las cláusulas que no hagan falta. Las palabras clave
pueden escribirse en mayúsculas o minúsculas. Los nombres de entidades y
atributos sí deben respetar su nombre y capitalización: `Game` y `boardSize`,
no `game` y `board_size`. Utilizaremos alias explícitos de forma coherente.

#### Qué seleccionamos determina qué recibimos

| Selección | Resultado de cada coincidencia | Retorno de repositorio apropiado en estos ejemplos |
|---|---|---|
| `select g from Game g` | Una entidad `Game` | `List<Game>` |
| `select g.boardSize from Game g` | Un tamaño, no una partida completa | `List<Integer>` |
| `select distinct g.boardSize from Game g` | Tamaños sin repetir | `List<Integer>` |
| `select count(g) from Game g` | Un único recuento | `long` o `Long` |

No escribir `select *`: en JPQL se selecciona la entidad mediante su alias,
atributos concretos o expresiones. `distinct` elimina duplicados del resultado
seleccionado; no modifica los datos almacenados.

#### Condiciones, parámetros y valores nulos

```sql
select g from Game g
where g.active = true
  and (g.status = :status or g.boardSize >= :minSize)
```

Aquí los paréntesis exigen actividad **en ambos casos**. Como en SQL, `and`
tiene prioridad sobre `or`; a diferencia del nombre de una consulta derivada,
en JPQL podemos indicar la agrupación explícitamente.

En un método con esta consulta se enlazarían `@Param("status") GameStatus status`
y `@Param("minSize") int minSize`. El estado se pasa como un enum Java, por
ejemplo `GameStatus.PENDING`, no como el texto de su columna SQL.

Para valores nulos se usa `g.cancelledAt is null` o `is not null`, **no**
`g.cancelledAt = null`. Tampoco debe suponerse que un parámetro `null` desactiva
un filtro: `g.status = :status` no significa «cualquier estado» cuando se recibe
`null`. Los filtros opcionales necesitan una condición diseñada para ello.

#### Relaciones: unir asociaciones del modelo

```sql
select s from Ship s
join s.game g
where g.active = true and g.status = :status
```

Se lee «barcos cuya partida está activa y tiene el estado indicado». `s.game`
es la asociación Java de `Ship` con `Game`; Hibernate conoce su mapeo y genera
la unión SQL correspondiente mediante `game_id`. No escribimos
`join game on s.game_id = game.id` dentro de JPQL.

`join` es una unión interna: exige una asociación coincidente. `left join`
permite conservar también la entidad de la izquierda cuando no hay coincidencia;
un filtro posterior en `where` sobre la parte derecha puede volver a excluirla.
El resultado de este ejemplo son barcos (`Ship`), no partidas, porque se ha
seleccionado `s`. `join` no equivale a `join fetch`: la estrategia de carga de
las asociaciones queda para una ampliación posterior.

#### Orden, agregación y agrupación

`order by g.boardSize desc, g.id asc` fija un orden: mayor tamaño primero y,
en caso de empate, menor id. Sin `order by` no hay un orden garantizado.
JPQL estándar no incorpora la cláusula SQL `limit`; en Spring Data la limitación
y paginación se resuelven mediante sus mecanismos de repositorio, que veremos
más adelante.

```sql
select g.status, count(g)
from Game g
where g.active = true
group by g.status
having count(g) >= :minimumCount
order by g.status
```

`where` filtra partidas **antes** de agrupar; `group by` forma un grupo por
estado; `having` conserva solo los grupos que alcanzan el recuento solicitado.
Por ejemplo, con `minimumCount = 2`, un estado con una sola partida activa
no aparece.

Cada fila contiene dos valores: `GameStatus` y un recuento `Long`, no un `Game`.
Una representación básica sería `List<Object[]>`; para una salida más expresiva
se estudiarán las proyecciones. No devolver `List<Game>` de una selección que
solo contiene estado y cantidad.

**Regla práctica:** reutilizar lo aprendido de SQL para condiciones y agrupación,
pero comprobar siempre **qué entidades/atributos se nombran y qué tipo se selecciona**.
Las funciones específicas del motor y su sintaxis no son automáticamente JPQL;
si hacen falta, habrá que revisar su soporte o justificar una consulta nativa.

## 5. Las dos consultas juntas: archivo completo

Este es el resultado de los ejemplos anteriores, no un tercer cambio.
**Proyecto:** `bshipAlong`. **Archivo:**
`src/main/java/es/daw2a/bshipalong/repository/GameRepository.java`.

```java
package es.daw2a.bshipalong.repository;

import es.daw2a.bshipalong.domain.Game;
import es.daw2a.bshipalong.domain.GameStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface GameRepository extends JpaRepository<Game, Long> {
    List<Game> findByActiveTrueAndStatus(GameStatus status);

    @Query("select g from Game g where g.active = true and g.boardSize >= :minSize")
    List<Game> findActiveWithMinimumBoardSize(@Param("minSize") int minSize);
}
```

Si ya has seguido el documento 08 y están escritos ambos métodos, **no los añadas
otra vez**: comprueba que coinciden. Si tu repositorio tiene otras consultas
necesarias, consérvalas; este archivo describe el checkpoint de aula, no una
instrucción para borrar métodos de otros proyectos.

## 6. Convenciones del lenguaje de consultas derivadas

El nombre del método sigue una **gramática**, no una traducción libre del inglés.
Las palabras clave se escriben con su forma reconocida (`GreaterThanEqual`, no
`BiggerThan`); las propiedades se toman del modelo Java. No hace falta memorizar
el catálogo, pero sí reconocer sus piezas.

### 6.1. Estructura del nombre

```text
operación [modificadores] By condición [And/Or condición...] [OrderBy propiedad Asc/Desc...]
```

Los corchetes indican partes opcionales: no se escriben en el método. Por ejemplo:

```java
List<Game> findTop3ByActiveTrueOrderByBoardSizeDescIdAsc();
```

Se lee «buscar como máximo tres partidas activas, primero por tamaño descendente
y, en caso de empate, por id ascendente». No recibe argumentos: `True`, `Top3` y
el orden están fijados en el nombre. Es una alternativa de consulta para
`GameRepository`, **no un método que haya que añadir al checkpoint**.

| Pieza | Convención y efecto |
|---|---|
| Operación | `find…By`, `read…By`, `get…By`, `query…By`, `search…By` y `stream…By` son prefijos de recuperación; el tipo de retorno debe ser compatible |
| Existencia o cantidad | `exists…By` devuelve un booleano; `count…By`, un recuento |
| Modificadores | `Distinct` pide resultados distintos; `First` o `Top` limita la cantidad, con un número opcional (sin número, uno) |
| Separador | `By` separa la parte que describe la operación de las condiciones |
| Condición | Propiedad Java y, si procede, operador: `BoardSizeGreaterThanEqual` |
| Combinación | `And` exige ambas condiciones; `Or` admite una u otra |
| Orden fijo | `OrderBy` seguido de propiedades y `Asc`/`Desc`; para varias, por ejemplo `BoardSizeDescIdAsc` |

`getByStatus` no significa por sí solo «devuelve 404 si falta»: el prefijo no
define el contrato HTTP. También existen `delete…By` y `remove…By`, pero borran
datos; no son alternativas inocuas a `find…By` y quedan fuera de esta práctica.

### 6.2. Propiedades Java, mayúsculas y argumentos

Con una propiedad `boardSize`, se escribe `BoardSize` en el nombre del método.
No se escribe `Board_size`, aunque esa sea su columna SQL. El nombre de la
variable del parámetro (`minSize`, por ejemplo) no establece el filtro: lo
establecen la propiedad y el operador del **nombre del método**.

```java
List<Game> findByStatusAndBoardSizeGreaterThanEqual(GameStatus status, int minSize);
```

Los argumentos se asocian a las condiciones en orden: el primero a `Status` y
el segundo a `BoardSizeGreaterThanEqual`. En una consulta derivada no se necesita
`@Param` para esa asociación.

| Operador o forma | Significado | Argumentos por condición |
|---|---|---|
| `Status` o `StatusIs` | Igualdad | Uno: `GameStatus` |
| `BoardSizeGreaterThan`, `BoardSizeLessThan` | Mayor o menor, sin incluir el límite | Uno: tamaño |
| `BoardSizeGreaterThanEqual`, `BoardSizeLessThanEqual` | Comparación que incluye el límite | Uno: tamaño |
| `BoardSizeBetween` | Entre dos límites, ambos incluidos | Dos: mínimo y máximo |
| `StatusIn` | Estado incluido en una colección | Uno: colección de estados |
| `CancelledAtIsNull`, `CancelledAtIsNotNull` | Ausencia o presencia de valor | Ninguno |
| `ActiveTrue`, `ActiveFalse` | Booleano fijado en el nombre | Ninguno |
| `StatusNot` | Distinto del estado indicado | Uno: estado |

No confundir «ningún argumento» con «pasar `null`». Para pedir explícitamente
valores nulos, `IsNull` hace la intención visible. Los operadores deben tener
sentido para el tipo de la propiedad: un enum no es una cadena para búsquedas
parciales, aunque se almacene como texto.

### 6.3. Texto: coincidencia parcial y mayúsculas

En un repositorio de `Ship`, cuya propiedad `shipName` es texto, podríamos declarar:

```java
List<Ship> findByShipNameContainingIgnoreCase(String text);
```

Es un ejemplo de sintaxis, no un cambio de archivo. `Containing` busca el texto
en cualquier posición; `StartingWith` lo busca al principio y `EndingWith`, al
final. `IgnoreCase` pide una comparación sin distinguir mayúsculas y minúsculas
para esa propiedad de texto; no cambia el nombre de la columna ni garantiza
ignorar acentos.

`Like` es distinto: recibe un patrón, con `%` para cualquier secuencia y `_`
para un carácter. Para una búsqueda de fragmento ordinaria, `Containing` expresa
mejor la intención que obligar a quien llama a construir ese patrón.

### 6.4. Asociaciones: recorrer propiedades, no claves SQL

Si `Ship` tiene una propiedad `game` y `Game` una propiedad `id`, en
`ShipRepository` se puede escribir:

```java
List<Ship> findByGame_Id(Long gameId);
```

`Game_Id` significa el recorrido Java `ship.game.id`. El guion bajo marca el
límite entre propiedades y permite evitar ambigüedades: **no hace referencia a
la columna `game_id`**. Spring Data puede deducir recorridos por camelCase,
pero intenta resolver primero una propiedad directa; el separador explícito
resulta útil si hay nombres que podrían interpretarse de varias formas.

Por eso conviene usar `lowerCamelCase` sin guiones bajos en los atributos Java
ordinarios y reservar `_` para expresar estos recorridos en las consultas.

### 6.5. Combinaciones y límites del lenguaje

`And` tiene prioridad sobre `Or`. Por ejemplo:

```java
List<Game> findByActiveTrueAndStatusOrBoardSizeGreaterThanEqual(
        GameStatus status, int minSize);
```

Se interpreta como `(active = true AND status = argumento) OR boardSize >= mínimo`.
**Podría devolver una partida inactiva** si cumple el tamaño. No equivale a
`active = true AND (status = argumento OR boardSize >= mínimo)`.
Los nombres no admiten paréntesis para cambiar esa agrupación; si la intención
no queda clara o exige otra agrupación, escribirla con JPQL es más legible.

Otra excepción importante: el método heredado `findById` busca por el atributo
identificador de JPA, aunque este no se llame `id`. En nuestro modelo sí se llama
así, pero no debe generalizarse como una búsqueda de cualquier propiedad llamada
`id`. Es un método reservado del repositorio.

### 6.6. Ejemplos rápidos para `Game`

Estas son alternativas, no métodos obligatorios que haya que añadir al apartado 5.

| Necesidad | Declaración dentro de `GameRepository` |
|---|---|
| Igualdad | `List<Game> findByStatus(GameStatus status);` |
| Comparación inclusiva | `List<Game> findByBoardSizeGreaterThanEqual(int minSize);` |
| Rango inclusivo | `List<Game> findByBoardSizeBetween(int min, int max);` |
| Existencia, sin recuperar una lista | `boolean existsByActiveTrueAndStatus(GameStatus status);` |
| Cantidad de coincidencias | `long countByActiveTrue();` |
| Orden explícito | `List<Game> findByActiveTrueOrderByIdAsc();` |

**No son consultas equivalentes entre sí.** Por ejemplo,
`findByBoardSizeGreaterThanEqual` no filtra por `active`: no omitir una condición
del negocio por haber encontrado un nombre más corto.

### 6.7. Orden y resultado

Una consulta sin `OrderBy` o `order by` **no garantiza el orden** de las filas.
Que hoy salgan por id en una prueba no forma parte del contrato.

`List<Game>` admite cero, una o muchas coincidencias. Si se utiliza un retorno
singular u `Optional<Game>`, debe existir una razón para esperar como máximo una;
`Optional` no convierte varios resultados en «el primero». No cambiar el tipo
de retorno solo para evitar tratar una colección.

## 7. Cómo comprobar que preguntamos bien

### 7.1. Casos que deben discriminar las condiciones

Para la primera consulta, preparar una partida activa `PENDING`, otra activa
`IN_PROGRESS` y otra cancelada. La consulta con `PENDING` debe devolver solo la
primera. Para aislar además el filtro de actividad, se puede preparar en un test
una partida inactiva con el mismo estado solicitado; no representa una transición
nueva del juego, sino un dato controlado para comprobar cada condición.

Para la segunda, probar una activa de tamaño 8, una activa de tamaño 12 y una
cancelada de tamaño 15. Con mínimo 10 debe entrar solo la activa de 12. Una
comprobación adicional útil es el borde: activa de tamaño exactamente 10.

No basta con insertar una partida que coincide y comprobar que aparece: también
necesitamos datos que **no** deberían aparecer, por razones diferentes.

### 7.2. Ejecutar las pruebas del code-along, no inventar otro escenario

El [documento 08, apartados 6–7](../../../02-ejemplos/battleship/docs/01-construccion/08-postgresql-y-consultas-bshipalong.md#6-comprobar-las-consultas-con-h2)
contiene el archivo completo `GameQueriesTest.java`, con imports, datos y dos
pruebas. Se crea en:

```text
src/test/java/es/daw2a/bshipalong/repository/GameQueriesTest.java
```

Desde la raíz de `bshipAlong`:

```bash
./mvnw -Dtest=GameQueriesTest test
./mvnw test
```

Estas pruebas usan repositorio y JPA reales con H2, no un repositorio mock.
`@Transactional` revierte los cambios del test al terminar; `flush()` sincroniza
los cambios pendientes sin confirmar la transacción. No demuestra que los datos
se hayan confirmado y puedan leerse desde otra conexión.

Para PostgreSQL, seguir por separado `PostgresVerificationIT` en el apartado 7
del documento 08. No basta con activar una configuración cualquiera y atribuir
un test H2 a PostgreSQL: hay que comprobar motor, conexión y perfil.

En el checkpoint docente previo hay 65 pruebas; al incorporar los dos casos
del repositorio se esperan 67 en H2, si no hay otros cambios. Esa cifra no
sustituye leer los resultados, ni hay que borrar tests para que coincida.

## 8. Errores frecuentes

| Síntoma o duda | Qué revisar |
|---|---|
| Spring no encuentra una propiedad en un método derivado | Nombre del atributo Java y recorrido de propiedades, no la columna SQL |
| JPQL no encuentra `board_size` | Usar `g.boardSize`; `board_size` es el nombre de columna |
| No reconoce `@Query` o `@Param` | Imports exactos del archivo completo del apartado 5 |
| El parámetro nombrado no se enlaza | Coincidencia entre `:minSize` y `@Param("minSize")` |
| Aparecen partidas canceladas | Comprobar que la consulta incluye `active = true` |
| Un test falla por el orden | Pedir orden si es parte del contrato o no depender de él en la aserción |
| «No se ha escrito la implementación del método» | La genera Spring Data; comprobar declaración y contexto, no crear un bucle manual |
| «La consulta ya está disponible por HTTP» | Un método de repositorio no crea una ruta ni cambia el listado del controlador |

## 9. Después de lo básico: material de consulta

No necesitamos estas ampliaciones para continuar con las dos consultas:

- Navegación por asociaciones y `JOIN`: consultar entidades relacionadas.
- SQL nativo: expresar una necesidad específica del motor cuando esté justificada.
- Specifications: combinar filtros que pueden estar presentes o no.
- Paginación y proyecciones: decidir cuántos resultados traer y qué datos devolver.

Hay un [apunte previo de consultas y alternativas](../../../02-ejemplos/Documentacion-SpringBoot/02-intermedio/06-consultas-jpa/20-ConsultasSpringBootDataJPA.md)
con ejemplos de otros modelos. Es material de consulta del curso anterior;
sus apartados avanzados no son pasos obligatorios de este recorrido ni se han
revalidado aquí sus APIs. Para el punto actual, este documento y el code-along
08 son la entrada principal. Para más adelante, véase también
[Proyecciones JPA](../10-proyecciones-jpa.md).

Referencias oficiales:

- [Métodos de consulta de Spring Data JPA](https://docs.spring.io/spring-data/jpa/reference/jpa/query-methods.html).
- [Palabras clave de los repositorios de Spring Data](https://docs.spring.io/spring-data/commons/reference/repositories/query-keywords-reference.html).
- [Estructura de métodos, propiedades y métodos reservados](https://docs.spring.io/spring-data/jpa/reference/repositories/query-methods-details.html).
- [Estrategias de nombres de Hibernate en Spring Boot](https://docs.spring.io/spring-boot/how-to/data-access.html#howto.data-access.configure-hibernate-naming-strategy).
- [Nombre por defecto de una columna de unión, `@JoinColumn`](https://jakarta.ee/specifications/persistence/3.2/apidocs/jakarta.persistence/jakarta/persistence/joincolumn).

## Antes de volver al repositorio

- [ ] Puedo traducir `findByActiveTrueAndStatus` a una frase y explicar por qué solo recibe un argumento.
- [ ] Distingo entidad/atributo Java de tabla/columna SQL.
- [ ] Sé cuándo un nombre se deduce por convención y cuándo lo indica una anotación, sin confundirlo con una migración.
- [ ] Puedo separar operación, condiciones y orden en un método derivado, y contar sus argumentos.
- [ ] Sé qué aporta `@Query` y cómo se enlaza `:minSize`.
- [ ] Puedo predecir qué partidas quedan fuera y por qué.
- [ ] Sé qué test y qué motor estoy ejecutando, sin asumir un orden no solicitado.
