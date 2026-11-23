# Battleship: filtrar, ordenar y paginar consultas

Este recorrido continúa [PostgreSQL y primeras consultas](08-postgresql-y-consultas-bshipalong.md)
en el proyecto **`bshipAlong`**. Trabajaremos en tres cortes independientes:
filtrar en la base de datos, ordenar y consultar por páginas. Al final de cada
corte hay una prueba para comprobar lo incorporado.

## Antes de empezar

- `GameRepository` contiene `findByActiveTrueAndStatus` y
  `findActiveWithMinimumBoardSize`; `GameQueriesTest` contiene sus dos pruebas.
- Las pruebas habituales usan H2. La prueba PostgreSQL de la guía anterior se
  ejecuta por separado: `./mvnw -Dtest=PostgresVerificationIT test`.
- Ejecuta los comandos de esta guía desde la raíz del proyecto **`bshipAlong`**.
  Si hay pruebas fallidas o diferencias en los archivos, consúltalas antes de
  continuar; no borres pruebas para obtener una cifra.
- No compartas contraseñas ni el contenido de `.env`.

**Importante:** en esta guía solo cambia el filtrado del listado existente. El
endpoint sigue devolviendo un **array**. Las consultas paginadas se prueban en
el repositorio; todavía no se aceptan `page`, `size` ni `sort` por HTTP.

## 1. ¿Por qué separar las tres ideas?

Imagina estas partidas:

| Partida | Tamaño del tablero | Estado | Activa |
|---|---:|---|---|
| A | 8 | PENDING | Sí |
| B | 12 | IN_PROGRESS | Sí |
| C | 10 | CANCELLED | No |

- **Filtrar** responde a «¿cuáles cumplen las condiciones?»: si pedimos las
  activas, obtenemos A y B, no C.
- **Ordenar** responde a «¿en qué orden aparecen?»: por tamaño ascendente,
  primero A y después B.
- **Paginar** responde a «¿qué parte de esa lista pedimos?»: con tamaño de
  página 1, la página 0 contiene A y la 1 contiene B.

Son operaciones distintas. Una lista filtrada no está necesariamente ordenada
y ordenar una lista no limita por sí solo cuántos elementos se recuperan.

## 2. Filtrar en la base de datos

### 2.1. La situación actual

Abre `src/main/java/es/daw2a/bshipalong/service/GameService.java` y busca
`listGames`. El servicio llama a `repository.findAll()`: recibe todas las
partidas y después Java descarta las inactivas y aplica el estado opcional.

El resultado puede ser correcto, pero se han traído filas que no necesitábamos.
Queremos que **la consulta a la base de datos** aplique las condiciones:

| Petición | Consulta que necesitamos |
|---|---|
| Sin estado | Todas las partidas activas |
| Con estado | Partidas activas con ese estado |

Ya tenemos la segunda consulta. Añadiremos la primera, sin sustituir las
consultas de la guía anterior.

### 2.2. Añadir la consulta de partidas activas

En `src/main/java/es/daw2a/bshipalong/repository/GameRepository.java`, dentro
de la interfaz, añade:

```java
List<Game> findByActiveTrue();
```

`findBy` inicia las condiciones y `ActiveTrue` selecciona las entidades cuyo
atributo Java `active` es verdadero. Spring Data construye la consulta a
partir del nombre. El método **no especifica ningún orden**.

### 2.3. Comprobar la consulta

En `src/test/java/es/daw2a/bshipalong/repository/GameQueriesTest.java`, añade
este método **dentro de la clase**, conservando las dos pruebas anteriores:

```java
@Test
void activeQueryExcludesCancelledGames() {
    Game active = games.save(new Game(8));
    Game cancelled = new Game(12);
    cancelled.cancel();
    games.save(cancelled);
    games.flush();

    assertThat(games.findByActiveTrue())
            .extracting(Game::getId)
            .contains(active.getId())
            .doesNotContain(cancelled.getId());
}
```

`flush()` envía los cambios pendientes a la base antes de consultar. La
transacción de la clase de pruebas revierte después los datos de cada test.
El método comprueba los identificadores creados por la prueba: no presupone
que la base real de desarrollo esté vacía.

```bash
./mvnw -Dtest=GameQueriesTest test
```

**Comprueba:** pasan las dos pruebas anteriores y la nueva; la cancelada no
aparece entre las activas. Si falla, revisa la firma, los imports y la
anotación `@Test` antes de modificar las aserciones.

### 2.4. Utilizar las consultas desde el servicio

En `GameService.java`, sustituye **solo el cuerpo** de `listGames`, manteniendo
su firma y `@Transactional(readOnly = true)`:

```java
var games = status == null
        ? repository.findByActiveTrue()
        : repository.findByActiveTrueAndStatus(status);
return games.stream().map(this::toResponse).toList();
```

Si no se indica estado, se buscan todas las activas. Si se indica, se usa la
consulta que exige tanto `active = true` como el estado. Después se convierte
cada entidad al DTO que ya devolvía el servicio.

**Consecuencia:** buscar `CANCELLED` no muestra partidas canceladas, porque la
cancelación también las marca como inactivas. No hemos cambiado esa regla.

### 2.5. Adaptar la prueba del servicio

La prueba unitaria usa un **mock**: no consulta H2 y solo devuelve lo que
configuramos. Como el servicio ya no llama a `findAll()`, hay que actualizar
ese comportamiento simulado.

En `src/test/java/es/daw2a/bshipalong/service/GameServiceTest.java`, localiza
`listsAndFiltersDtos`. Sustituye su configuración `when(repository.findAll())`
por estas tres, conservando las aserciones:

```java
when(repository.findByActiveTrue()).thenReturn(
        java.util.List.of(pending(1L), pending(2L)));
when(repository.findByActiveTrueAndStatus(GameStatus.PENDING)).thenReturn(
        java.util.List.of(pending(1L), pending(2L)));
when(repository.findByActiveTrueAndStatus(GameStatus.IN_PROGRESS)).thenReturn(
        java.util.List.of());
```

Sin este ajuste el mock devuelve una lista vacía y la prueba falla. Esto no
significa automáticamente que falle la consulta real: para esa consulta usamos
`GameQueriesTest`, que trabaja con el repositorio y H2.

```bash
./mvnw -Dtest=GameQueriesTest,GameServiceTest test
./mvnw test
```

**Corte 1:** la suite debe estar verde antes de seguir. Si pruebas el listado
con el cliente HTTP, compara `/api/games` y
`/api/games?status=PENDING`: siguen devolviendo un array y excluyendo las
inactivas. Utiliza datos conocidos; no borres datos anteriores para la demo.

## 3. Ordenar con `Sort`

### 3.1. El problema de los empates

Sin un orden explícito, una base de datos no promete devolver las filas siempre
en el mismo orden. Aunque ordenemos por `boardSize`, puede haber varias partidas
con el mismo tamaño. Añadiremos `id` como **desempate**.

Por ejemplo, si las partidas tienen tamaños e identificadores `(8, 7)`,
`(8, 3)` y `(12, 5)`, ordenadas por tamaño y después por id ascendente,
los identificadores serán `3, 7, 5`.

### 3.2. Permitir un orden en el repositorio

Añade este import a `GameRepository.java`:

```java
import org.springframework.data.domain.Sort;
```

Dentro de la interfaz, **conserva el método sin parámetros del apartado 2.2 y
añade una segunda declaración**. Ese fragmento debe quedar con **las dos firmas**:

```java
List<Game> findByActiveTrue();
List<Game> findByActiveTrue(Sort sort);
```

Si la primera ya existe, no la copies otra vez: añade únicamente la segunda.
No estamos añadiendo un parámetro al método anterior ni sustituyéndolo.

Es una sobrecarga: ambos métodos filtran activas; el segundo recibe además un
criterio de ordenación. Los nombres de `Sort` son propiedades de `Game`, no
necesariamente nombres de columnas SQL.

**Por qué se conservan ambas:** `GameService.listGames`, su mock en
`GameServiceTest` y la prueba `activeQueryExcludesCancelledGames` siguen llamando
a `findByActiveTrue()`. La nueva prueba de ordenación llamará a
`findByActiveTrue(sort)`. En este paso solo comprobamos la ordenación desde el
repositorio; todavía no la exponemos por HTTP ni cambiamos el servicio.

Si aparece un error de compilación que indica que se requiere `Sort` pero no se
han pasado argumentos, comprueba que no hayas reemplazado la primera firma.
En este recorrido, recupérala junto a la segunda para conservar los llamadores.

**Alternativa válida:** mantener solo `findByActiveTrue(Sort sort)` y actualizar
**todas** las llamadas sin orden a `findByActiveTrue(Sort.unsorted())`, incluidos
el servicio, las pruebas y los mocks (con el import de `Sort` correspondiente).
`Sort.unsorted()` expresa que no se solicita ordenación; no garantiza el orden
de los resultados ni equivale a pasar `null`. Ambas opciones son válidas: aquí
usamos sobrecargas para introducir la ordenación sin cambiar las llamadas previas.

### 3.3. Comprobar orden y desempate

Añade a `GameQueriesTest.java`:

```java
@Test
void activeGamesCanBeSortedBySizeAndId() {
    Game firstSmall = games.save(new Game(8));
    Game secondSmall = games.save(new Game(8));
    Game large = games.save(new Game(12));
    Game cancelled = new Game(8);
    cancelled.cancel();
    games.save(cancelled);
    games.flush();

    var sort = org.springframework.data.domain.Sort.by("boardSize")
            .and(org.springframework.data.domain.Sort.by("id"));
    long smallerId = Math.min(firstSmall.getId(), secondSmall.getId());
    long largerId = Math.max(firstSmall.getId(), secondSmall.getId());

    assertThat(games.findByActiveTrue(sort))
            .extracting(Game::getId)
            .containsExactly(smallerId, largerId, large.getId());
}
```

```bash
./mvnw -Dtest=GameQueriesTest test
```

**Corte 2:** la prueba muestra primero las dos activas de tamaño 8, resuelve el
empate por id y excluye la cancelada. No cambia el orden del endpoint HTTP:
el servicio todavía utiliza la sobrecarga sin `Sort`.

## 4. Consultar por páginas

### 4.1. ¿Qué son `Pageable`, `PageRequest` y `Page`?

Si hay muchos resultados, normalmente no necesitamos pedirlos todos a la vez.
Una **página** es una parte de una lista:

- `Pageable` describe qué página, tamaño y orden se solicitan.
- `PageRequest.of(0, 2, sort)` crea esa petición: página **0**, hasta dos
  elementos y el orden indicado. La numeración empieza en cero.
- `Page<Game>` contiene los elementos de la página y metadatos como el total
  de elementos y de páginas. Para conocer el total, puede necesitarse una
  consulta adicional de recuento.

Por contraste, `Slice` permite saber si hay otra porción, sin ofrecer el total
como parte de su contrato. Hoy usamos `Page`; no cambiaremos la API HTTP.

Ordenar antes de dividir es importante: sin orden definido, no sabemos qué
elementos corresponden a cada página. Incluso con orden y desempate, si se
insertan o eliminan filas entre peticiones, los resultados pueden desplazarse.

### 4.2. Añadir métodos paginados

Añade a `GameRepository.java`:

```java
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
```

Dentro de la interfaz, conserva los métodos anteriores y añade:

```java
Page<Game> findByActiveTrue(Pageable pageable);

Page<Game> findByActiveTrueAndStatus(GameStatus status, Pageable pageable);
```

El primer método pagina todas las activas. El segundo mantiene además el filtro
por estado. El parámetro `Pageable` lleva el tamaño y el orden a la consulta;
no hacemos `findAll()` para recortar después una lista en memoria.

### 4.3. Comprobar primera, última y página vacía

Añade este método a `GameQueriesTest.java`:

```java
@Test
void activeGamesAreSortedAndPaged() {
    Game firstSmall = games.save(new Game(8));
    Game secondSmall = games.save(new Game(8));
    Game large = games.save(new Game(12));
    Game cancelled = new Game(15);
    cancelled.cancel();
    games.save(cancelled);
    games.flush();

    var sort = org.springframework.data.domain.Sort.by("boardSize")
            .and(org.springframework.data.domain.Sort.by("id"));
    var first = games.findByActiveTrue(
            org.springframework.data.domain.PageRequest.of(0, 2, sort));
    var second = games.findByActiveTrue(
            org.springframework.data.domain.PageRequest.of(1, 2, sort));
    var empty = games.findByActiveTrue(
            org.springframework.data.domain.PageRequest.of(2, 2, sort));

    long smallerId = Math.min(firstSmall.getId(), secondSmall.getId());
    long largerId = Math.max(firstSmall.getId(), secondSmall.getId());
    assertThat(first.getContent()).extracting(Game::getId)
            .containsExactly(smallerId, largerId);
    assertThat(second.getContent()).extracting(Game::getId)
            .containsExactly(large.getId());
    assertThat(empty.getContent()).isEmpty();
    assertThat(first.getTotalElements()).isEqualTo(3);
    assertThat(first.getTotalPages()).isEqualTo(2);
}
```

¿Por qué hay tres elementos y no cuatro? La partida cancelada no está activa y
no entra en la consulta ni en su total.

### 4.4. Comprobar también el estado

Añade otra prueba, usando partidas propias:

```java
@Test
void pagedQueryCombinesActiveAndStatus() {
    Game pending = games.save(new Game(8));
    Game running = new Game(10);
    running.setStatus(GameStatus.IN_PROGRESS);
    games.save(running);
    Game cancelled = new Game(12);
    cancelled.cancel();
    games.save(cancelled);
    games.flush();

    var request = org.springframework.data.domain.PageRequest.of(
            0, 2, org.springframework.data.domain.Sort.by("id"));
    var page = games.findByActiveTrueAndStatus(GameStatus.PENDING, request);

    assertThat(page.getContent()).extracting(Game::getId)
            .containsExactly(pending.getId());
    assertThat(page.getTotalElements()).isEqualTo(1);
}
```

```bash
./mvnw -Dtest=GameQueriesTest test
./mvnw test
```

**Corte 3:** se han comprobado dos páginas, una vacía, el total y la combinación
de estado y actividad en el repositorio. No se ha demostrado todavía un endpoint
paginado ni se ha cambiado el formato de respuesta.

## 5. Qué hemos conseguido y qué falta

El listado existente filtra en la base de datos y conserva su respuesta anterior.
El repositorio también sabe ordenar y paginar partidas activas, con o sin estado.
Más adelante decidiremos cómo exponer esas capacidades por HTTP y cómo comunicar
el cambio de contrato a los clientes; `Page` no se convierte automáticamente en
un contrato público por haberlo utilizado en una prueba.

Para repasar:

1. ¿Qué diferencia hay entre filtrar con `findAll().stream()` y hacerlo en la BD?
2. ¿Por qué añadimos `id` al ordenar por `boardSize`?
3. Con tres resultados y tamaño dos, ¿qué contienen las páginas 0, 1 y 2?
4. ¿Una página vacía implica que no existe ninguna partida?
5. ¿Por qué el mock del servicio no demuestra que la consulta SQL sea correcta?
