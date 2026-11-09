---
title: "Spring Boot · Segunda parte"
subtitle: "Bloque A — Modelo relacional e integridad"
author: "DWES · 2026/2027"
lang: es
aspectratio: 169
---

## Del id suelto a un modelo de datos

**Pregunta del bloque:** ¿quién garantiza que los barcos pertenecen a una
partida y que nuestros datos no se contradicen?

- Parte 1: entidades, relaciones y ciclo de vida.
- Parte 2: restricciones, migraciones y pruebas.

Los apuntes desarrollan la explicación. En clase escogemos las decisiones que
necesitamos para leer y aplicar los bloques de código.

Primera lectura: [relaciones, versión didáctica](../04-persistencia/bloque-a/01-entidades-relaciones-y-ciclo-de-vida-didactica.md).

<!-- Uso docente: intercalar estas pantallas con código y trabajo en la práctica
activa. No recorrer toda la presentación como una exposición de varias horas.
Tres tramos: relación; carga/ciclo de vida; integridad y comprobación. -->

## Tres representaciones, una misma aplicación

| SQL | Java | HTTP |
|---|---|---|
| `ship.game_id` | `Ship.game` | `gameId` o un DTO diseñado |
| FK | Asociación | Contrato de respuesta |

**Una relación en la BD no obliga a devolver todo el grafo por JSON.**

*Estudio: [parte 1, §1](../04-persistencia/bloque-a/01-entidades-relaciones-y-ciclo-de-vida.md#1-tres-modelos-relacionados-pero-diferentes).*

## Lo que tenemos y lo que queremos expresar

```java
// Checkpoint anterior: Hibernate ve un número.
private Long gameId;

// Evolución: Hibernate conoce una asociación.
@ManyToOne(fetch = FetchType.LAZY, optional = false)
@JoinColumn(name = "game_id", nullable = false)
private Game game;
```

La columna puede seguir siendo **la misma**. Cambia el mapeo de objetos.

<!-- Señalar el cambio de atributo y recordar que no se conservan dos mapeos
escribibles de game_id. No afirmar que la FK no existía en la versión anterior. -->

## La cardinalidad se lee en ambos sentidos

```text
Una partida ───────────── muchos barcos
             game_id
```

- Del barco a la partida: **muchos a uno**.
- De la partida a los barcos: **uno a muchos**.
- Dos direcciones Java, **una FK SQL**.

Los apuntes ya desarrollan 1:1 y N:M. Su aplicación al proyecto tendrá su caso
al modelar usuarios, perfiles y roles.

## ¿Quién es propietario?

```java
// Ship: columna que guarda el vínculo.
@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "game_id")
private Game game;

// Game: navegación inversa.
@OneToMany(mappedBy = "game")
private List<Ship> ships = new ArrayList<>();
```

**`mappedBy` nombra `Ship.game`, no la columna `game_id`.**

<!-- Propietario del mapeo no significa responsable de todas las reglas de negocio. -->

## La BD y la memoria pueden discrepar

```java
game.getShips().add(ship); // ¿Quién ha rellenado Ship.game?

ship.setGame(game);       // ¿Qué ve una colección ya cargada?
```

Un helper mantiene **ambos lados**:

```java
game.addShip(ship);
```

Relacionar objetos no equivale a persistirlos ni a validar el tablero.

**Antes de ejecutar:** ¿qué atributo gobierna la FK?

## Primer corte — Leer el bloque y comprobar la relación

1. Revisar el mapeo y el helper en el código preparado.
2. Guardar partida y barco.
3. Sincronizar, vaciar el contexto y recuperar por id.
4. Comprobar asociación y respuesta HTTP anterior.

**Evidencia:** el vínculo sobrevive a una recuperación desde la BD.

<!-- A partir de aquí, trabajo de la práctica activa y control de equipos.
No añadir otra tarea independiente para repetir el ejemplo. -->

## `ALL` también incluye borrar

| Mecanismo | Pregunta |
|---|---|
| `cascade=PERSIST` | ¿Guardar el padre debe persistir sus hijos nuevos? |
| `cascade=REMOVE` | ¿Borrar el padre debe borrar también esos hijos? |
| `orphanRemoval=true` | ¿Quitar un hijo del vínculo debe eliminar su fila? |

**Cancelar una partida no significa borrar su histórico.**

No activar cascadas para «que funcione» sin responder estas preguntas.

## Un objeto gestionado no es solo una bolsa de datos

```text
Nuevo → gestionado → separado
              ↓
       cambios detectados
              ↓
            flush
```

- Dirty checking: seguimiento de cambios gestionados.
- Flush: ejecutar sincronización pendiente.
- Commit: confirmar la transacción.

**Flush ≠ commit.**

## El servicio delimita el caso de uso

```java
@Transactional(readOnly = true)
public GameDetailDTO getDetail(Long id) {
    Game game = findActiveGame(id);
    return toDetailDTO(game);
}
```

Preparar dentro de la unidad de trabajo los datos que necesita la respuesta.

No esperar a que el controlador o el serializador descubran asociaciones sin cargar.

<!-- Fragmento de explicación, no implementación lista para pegar: se supone
finder y mapper. Recordar proxies y autollamadas si surge la duda. -->

## Tres problemas que no se arreglan igual

| Problema | Qué revisar |
|---|---|
| `Game → ships → game → …` en JSON | DTO y contrato de respuesta |
| Recursión en `toString`, `equals` o `hashCode` | Métodos generados e identidad de entidades |
| Acceso a una relación diferida fuera del contexto | Límite transaccional y plan de carga |

**Una anotación de Jackson no resuelve los tres.**

## El DTO corta el grafo donde decidimos

```json
{
  "id": 7,
  "status": "IN_PROGRESS",
  "ships": [{"id": 12, "shipName": "A"}]
}
```

El barco de la respuesta no contiene otra partida completa.

**Pero el mapper todavía puede provocar consultas innecesarias.**

## Mirar el SQL también forma parte de comprender

Una lista de N partidas seguida de una consulta por colección puede producir
**1 + N consultas**.

- ¿Necesita el listado los barcos completos?
- ¿Basta un resumen?
- ¿Se ha elegido un plan de carga para este endpoint?

La solución no es convertir todas las asociaciones en `EAGER`.

*Estudio: [parte 1, §10–12](../04-persistencia/bloque-a/01-entidades-relaciones-y-ciclo-de-vida.md#10-carga-diferida-y-limite-transaccional).*

## Segundo corte — Conservar comportamiento y controlar la respuesta

- La API mantiene el JSON previsto.
- Cancelar conserva partidas, barcos y ataques.
- El servicio prepara el DTO con el contexto adecuado.
- El código no recorre relaciones por accidente al imprimir una entidad.

**Pregunta de control:** ¿qué pasaría si cambiásemos a `orphanRemoval=true`
y empezásemos a vaciar la colección al cancelar?

<!-- Dar paso a aplicación y revisión. La parte de integridad puede comenzar
en otra clase; no hay obligación de terminar todas las pantallas el mismo día. -->

## Un id único no evita duplicados de negocio

```text
id    game_id    ship_name
12       7          A
13       7          A
```

La PK está bien. **La regla del nombre dentro de la partida está mal.**

Necesitamos identificar qué combinación debe ser única.

## Unicidad simple frente a compuesta

```sql
UNIQUE (username)

UNIQUE (game_id, ship_name)

UNIQUE (game_id, x, y)
```

**Decide:** ¿un barco A en partida 7 impide otro barco A en partida 8?

La combinación se repite o no se repite; no hace únicos sus componentes por separado.

## Dos anotaciones, dos tipos de nombres

```java
@OneToMany(mappedBy = "game") // atributo Java
```

```java
@UniqueConstraint(columnNames = {"game_id", "ship_name"}) // columnas SQL
```

Con Flyway, las anotaciones no sustituyen la migración que crea la restricción.

## «He consultado antes» no basta

```text
A: ¿existe? → no
B: ¿existe? → no
A: inserta
B: intenta insertar
```

- La preconsulta permite anticipar un mensaje.
- La restricción de BD protege también la carrera concurrente.
- El error debe cerrar correctamente la operación y traducirse sin detalles internos.

## Una nueva regla de esquema, una nueva migración

```sql
ALTER TABLE ship
    ADD CONSTRAINT uk_ship_game_name UNIQUE (game_id, ship_name);
```

Antes: buscar datos duplicados y decidir qué hacer con ellos.

Después: comprobar base vacía y evolución desde datos anteriores.

**No editar una migración ya aplicada para ocultar el cambio.**

## ¿Cuándo se ejecuta realmente la escritura?

```text
persist / save ──► posible SQL inmediato
        │
        └────────► SQL diferido hasta flush / commit
```

Una expectativa de error debe abarcar la escritura efectiva.

Después de una violación de persistencia: rollback y tratamiento del fallo;
no continuar consultando con la misma transacción como si nada.

## Una prueba para la garantía correcta

| Quiero demostrar… | Necesito… |
|---|---|
| Que el helper mantiene ambos lados | Prueba de objetos en memoria |
| Que la BD rechaza el nombre repetido | Escritura real con la restricción migrada |
| Que ese rechazo tiene un JSON coherente | Prueba HTTP del contrato |

**Un mock del repositorio no ejecuta una restricción SQL.**

## Tercer corte — Rechazar lo incorrecto y admitir lo válido

Para cada restricción compuesta:

1. Misma combinación en la misma partida → rechazo.
2. Valores equivalentes en otra partida → permitido por esa restricción.
3. Guardar → flush → clear → recuperar para comprobar el mapeo.

No confundir un test transaccional con la prueba de todos los flujos de producción.

*Estudio: [parte 2, pruebas](../04-persistencia/bloque-a/02-integridad-migraciones-y-pruebas.md#10-ejemplo-de-pruebas-que-si-llegan-a-la-bd).*

## Lo que debemos poder explicar al salir

- Dónde está la FK y quién mantiene la asociación.
- Qué se conserva al cancelar y por qué.
- Qué publica el DTO y qué queda en el modelo interno.
- Qué restricción protege cada regla y cómo se incorpora al esquema.
- Qué test demuestra la garantía y qué no demuestra.

**Siguiente bloque:** consultas, filtros, ordenación y paginación sobre este modelo.

## Apuntes para estudio y consulta

- [Relaciones — versión didáctica](../04-persistencia/bloque-a/01-entidades-relaciones-y-ciclo-de-vida-didactica.md).
- [Parte 1 — Referencia técnica de entidades y relaciones](../04-persistencia/bloque-a/01-entidades-relaciones-y-ciclo-de-vida.md).
- [Parte 2 — Integridad, migraciones y pruebas](../04-persistencia/bloque-a/02-integridad-migraciones-y-pruebas.md).
- [Índice temático de documentación](../README.md).

Las preguntas de estas pantallas se usan para conversación y comprobación.
La programación continúa en la práctica activa; no abren una entrega nueva.
