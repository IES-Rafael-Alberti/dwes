# Aserciones — JUnit, AssertJ y Hamcrest

**Diccionario por intención.** Una aserción expresa qué resultado esperamos.
Mockito `verify` comprueba llamadas a un doble y MockMvc aplica expectativas a
una petición: se relacionan con las aserciones, pero no son instrucciones de JUnit.

## 1. Imports: el mismo nombre puede pertenecer a otra biblioteca

```java
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.hamcrest.Matchers.hasSize;
```

Aquí `assertThat` es de **AssertJ** y `hasSize` de **Hamcrest**. Hamcrest tiene
también un `MatcherAssert.assertThat(actual, matcher)` con otra firma.

No aceptar un import sugerido por el IDE sin comprobar de dónde viene. `assert`
del lenguaje Java tampoco es `assertEquals`: su activación depende de la JVM y
no es la API que utilizamos para escribir estas expectativas de tests.

## 2. Aserciones de JUnit Jupiter

Forma habitual: **esperado primero, obtenido después**. El mensaje opcional va
después de los argumentos de comprobación, no antes como en algunas APIs antiguas.

| Método | Qué comprueba | Ejemplo |
|---|---|---|
| `assertEquals(expected, actual)` | Igualdad de valores según el tipo/`equals` | `assertEquals("WON", result.status())` |
| `assertNotEquals(unexpected, actual)` | Desigualdad de valores | `assertNotEquals(first.id(), second.id())` |
| `assertSame(expected, actual)` | Misma referencia Java | `assertSame(game, ship.getGame())` |
| `assertNotSame(unexpected, actual)` | Referencias distintas | Dos objetos de valor iguales pueden no ser la misma instancia |
| `assertTrue(condition)` | Condición verdadera | `assertTrue(game.isActive())` |
| `assertFalse(condition)` | Condición falsa | `assertFalse(game.isActive())` |
| `assertNull(actual)` | Valor nulo | `assertNull(game.getCancelledAt())` |
| `assertNotNull(actual)` | Valor no nulo | `assertNotNull(result.id())` |
| `assertArrayEquals(expected, actual)` | Mismos elementos de un array | No comparar arrays con `assertEquals` esperando comparación de contenido |
| `assertIterableEquals(expected, actual)` | Mismos elementos en el mismo orden | No equivale a «contiene estos dos entre otros» |
| `assertLinesMatch(expected, actual)` | Lista de líneas con las reglas de patrones de Jupiter | Útil para salida textual; no es una igualdad literal universal |
| `assertInstanceOf(Type.class, actual)` | Tipo compatible; devuelve el valor convertido | Puede utilizarse para comprobar una respuesta polimórfica |
| `fail("motivo")` | Fallo incondicional | Para un camino que no debería alcanzarse |

### 2.1. Igualdad no es identidad

Dos records con los mismos componentes pueden ser iguales mediante `equals`,
pero dos instancias distintas. En entidades, además, el id persistente y la
igualdad Java requieren una política explícita. No usar `assertSame` para comparar
ids numéricos ni confiar en el cacheado de wrappers como prueba de identidad.

### 2.2. Números y tolerancia

```java
assertEquals(0.3, 0.1 + 0.2, 1e-9);
```

La tolerancia sirve para aproximaciones de coma flotante. No justifica redondeos
arbitrarios en una regla que exija otra precisión. Para `BigDecimal`, revisar
si necesitamos igualdad que tenga en cuenta la escala o comparación numérica.

### 2.3. Varias comprobaciones relacionadas

```java
assertAll("partida creada",
        () -> assertNotNull(result.id()),
        () -> assertEquals("PENDING", result.status()));
```

`assertAll` ejecuta las comprobaciones suministradas y agrupa los fallos de
aserción. Varias llamadas ordinarias seguidas se detienen en la primera que falla.
Las lambdas no deben depender de que otra aserción haya preparado un dato válido.

### 2.4. Excepciones y tiempo

| Método | Lectura y matiz |
|---|---|
| `assertThrows(Type.class, action)` | Ejecuta la acción y exige una excepción de ese tipo **o subtipo**; devuelve la excepción |
| `assertThrowsExactly(Type.class, action)` | Exige exactamente ese tipo, sin aceptar subtipos |
| `assertDoesNotThrow(action)` | Exige ausencia de excepción; algunas sobrecargas devuelven el resultado |
| `assertTimeout(duration, action)` | Mide ejecución en el mismo hilo; no interrumpe al cumplirse el límite |
| `assertTimeoutPreemptively(duration, action)` | Ejecuta con otro hilo e intenta interrumpir al exceder el límite |

```java
IllegalArgumentException error = assertThrows(
        IllegalArgumentException.class, () -> requirePositive(0));
assertEquals("Size must be positive", error.getMessage());
```

La lambda retrasa la llamada hasta que la aserción la ejecuta. Esto es diferente
de llamar primero al método, dejar escapar el fallo y luego intentar comprobarlo.

La variante temporal preemptiva necesita especial cuidado con código que dependa
del hilo, como transacciones de prueba de Spring. No utilizarla como decoración
en pruebas de BD. Ausencia de excepción tampoco garantiza que el resultado sea correcto.

## 3. Colecciones, arrays y extracción

Las siguientes operaciones son de **AssertJ**:

```java
assertThat(actual).as("explicación opcional").condicion();
```

`assertThat(actual)` sin una condición final **no comprueba nada**. `as(...)`
se escribe antes de la comprobación que podría fallar.

| Operación | Qué exige |
|---|---|
| `isEmpty()` / `isNotEmpty()` | Vacía / al menos un elemento |
| `hasSize(n)` | Exactamente n elementos |
| `contains(a, b)` | Están esos valores; puede haber otros y no exige ese orden |
| `doesNotContain(a)` | No aparece ese valor |
| `containsExactly(a, b)` | Solo esos elementos, en ese orden y con esas repeticiones |
| `containsExactlyInAnyOrder(a, b)` | Solo esos elementos, sin exigir orden, respetando repeticiones |
| `containsOnly(a, b)` | Solo esos valores distintos, ignorando orden y multiplicidad |
| `doesNotHaveDuplicates()` | No hay duplicados según la igualdad usada |
| `allMatch(predicate)` | Todos cumplen un predicado booleano |
| `anyMatch(predicate)` | Alguno cumple el predicado |
| `noneMatch(predicate)` | Ninguno lo cumple |
| `allSatisfy(consumer)` | Todos satisfacen las aserciones escritas en el bloque |
| `anySatisfy(consumer)` | Al menos uno satisface ese bloque de aserciones |
| `filteredOn(predicate)` | Filtra antes de las comprobaciones siguientes |
| `extracting(Type::property)` | Extrae valores para comprobar el contenido relevante |
| `singleElement()` | Exige un único elemento y permite comprobarlo después |

**`containsOnly("A", "B")` no detecta por sí solo que A se haya repetido.**
Para exigir contenido exacto sin orden y con multiplicidad se utiliza
`containsExactlyInAnyOrder`. Y un orden que la BD no garantiza no debe aparecer
como expectativa accidental: definir ordenación o elegir una aserción sin orden.

```java
assertThat(games)
        .extracting(GameView::status)
        .containsExactlyInAnyOrder("PENDING", "WON");
```

La referencia de método evita escribir el nombre del atributo como cadena.
`extracting("status")` también puede ser útil, pero los errores de nombre se
descubren más tarde. Para varias propiedades se pueden utilizar `tuple(...)`.

`allMatch` y `noneMatch` sobre una colección vacía pueden resultar verdaderos.
Si el requisito exige resultados, comprobar también que no esté vacía o su tamaño.

## 4. AssertJ por tipo de dato

| Tipo | Operaciones de consulta |
|---|---|
| Objeto | `isEqualTo`, `isNotEqualTo`, `isSameAs`, `isNotSameAs`, `isNull`, `isNotNull`, `isInstanceOf` |
| Texto | `isBlank`, `isNotBlank`, `isEmpty`, `contains`, `doesNotContain`, `startsWith`, `endsWith`, `matches`, `isEqualToIgnoringCase` |
| Booleano | `isTrue`, `isFalse` |
| Número | `isZero`, `isPositive`, `isNegative`, `isGreaterThan`, `isLessThanOrEqualTo`, `isBetween`, `isCloseTo` |
| `BigDecimal` | `isEqualByComparingTo` para comparación numérica; no confundir con igualdad sensible a escala |
| `Optional` | `isPresent`, `isEmpty`, `contains(value)`, `hasValueSatisfying(...)` |
| `Map` | `containsKey`, `doesNotContainKey`, `containsEntry`, `hasSize` |
| Fecha/instante | `isBefore`, `isAfter`, `isEqualTo`, según el tipo |
| Archivo / `Path` | `exists`, `isRegularFile`, `hasFileName`, `hasContent`, según el tipo y la API disponible |
| Comparación de estructura | `usingRecursiveComparison()` seguido de una comprobación; elegir campos relevantes deliberadamente |

No usar comparación recursiva para ocultar que el contrato de un DTO ha cambiado
o para recorrer sin intención todo un grafo de entidades. No exigir que un
timestamp generado coincida con la fecha fija escrita en un ejemplo.

### 4.1. Excepciones con AssertJ

```java
assertThatThrownBy(() -> requirePositive(0))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Size must be positive");
```

| Forma | Significado |
|---|---|
| `assertThatThrownBy(action)` | Ejecuta y captura el fallo; exige que se lance algo |
| `.isInstanceOf(Type.class)` | Acepta tipo o subtipos |
| `.isExactlyInstanceOf(Type.class)` | Exige tipo exacto |
| `.hasMessage(text)` | Mensaje completo |
| `.hasMessageContaining(text)` | Fragmento del mensaje |
| `.hasCauseInstanceOf(Type.class)` | Tipo de la causa directa |
| `assertThatExceptionOfType(Type.class).isThrownBy(action)` | Otra entrada para comprobar una excepción |
| `assertThatCode(action).doesNotThrowAnyException()` | Ausencia de excepción |

Los mensajes públicos que forman parte del contrato sí pueden comprobarse. Los
mensajes internos de un motor SQL o trazas de proveedor suelen ser demasiado
inestables y no deben devolverse como respuesta de la API.

## 5. Aserciones agrupadas y comprobaciones débiles

Con `SoftAssertions.assertSoftly(...)`, AssertJ agrupa los fallos registrados en
ese bloque y los comunica al terminar. Si se crea manualmente un `SoftAssertions`,
hay que llamar a `assertAll()`; olvidar ese cierre puede producir un falso verde.

Evitar estas comprobaciones insuficientes:

| Solo comprueba… | Puede dejar pasar… |
|---|---|
| `assertThat(result)` | Cualquier resultado: falta una condición |
| `isNotNull()` | Id o estado equivocados |
| `hasSize(2)` | Dos elementos repetidos o incorrectos |
| `assertDoesNotThrow(...)` | Resultado erróneo sin excepción |
| `contains("A")` | Otros elementos incorrectos que no debían aparecer |
| `verify(repo).save(any())` | Que se haya guardado el objeto equivocado y, con un mock, que no se persista nada real |

No se trata de añadir todas las aserciones posibles, sino la combinación que
detecta el defecto del comportamiento elegido.

## 6. Hamcrest en las expectativas MVC

Uso habitual:

```java
mvc.perform(get("/api/games"))
        .andExpect(jsonPath("$", hasSize(2)))
        .andExpect(jsonPath("$[0].status", is("PENDING")));
```

Aquí `jsonPath` es de Spring Test; `hasSize` e `is` son matchers Hamcrest.

| Matcher | Qué expresa |
|---|---|
| `is(value)` / `equalTo(value)` | Igualdad |
| `not(matcher)` | Negación |
| `nullValue()` / `notNullValue()` | Nulo / no nulo |
| `hasSize(n)` | Tamaño de una colección |
| `hasItem(value)` | Contiene un elemento |
| `contains(a, b)` | Contenido exacto y ordenado de un iterable |
| `containsInAnyOrder(a, b)` | Contenido exacto sin orden |
| `containsString(text)` | Subcadena |
| `startsWith(text)` / `endsWith(text)` | Prefijo / sufijo |
| `allOf(...)` / `anyOf(...)` | Combinación AND / OR de matchers |

**`contains` de Hamcrest no tiene la misma semántica que `contains` de AssertJ.**
Mirar el import importa tanto como leer el nombre.

En JSONPath, un entero JSON puede representarse con un tipo numérico distinto
de un `Long` de Java. Se puede ajustar la expectativa o pedir conversión con
una sobrecarga de `value(matcher, TargetType.class)` cuando sea necesario.

## 7. Ejemplo completo y verificable

<!-- ejemplo-java: AssertionsDictionaryTest.java -->
```java
package es.daw2a.referencias;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.tuple;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.*;

class AssertionsDictionaryTest {
    record GameView(Long id, String status) {}

    @Test void equalityAndIdentityAreDifferent() {
        var first = new GameView(7L, "PENDING");
        var second = new GameView(7L, "PENDING");
        assertEquals(first, second);
        assertNotSame(first, second);
        assertSame(first, first);
        assertArrayEquals(new int[]{2, 3}, new int[]{2, 3});
    }

    @Test void checksNumbersAndGroupedProperties() {
        assertEquals(0.3, 0.1 + 0.2, 1e-9);
        assertThat(new BigDecimal("1.00")).isEqualByComparingTo("1.0");
        var game = new GameView(7L, "PENDING");
        assertAll("game", () -> assertNotNull(game.id()),
                () -> assertEquals("PENDING", game.status()));
    }

    @Test void checksCollectionContentAndMultiplicity() {
        var games = List.of(new GameView(7L, "PENDING"), new GameView(8L, "WON"));
        assertThat(games).hasSize(2)
                .extracting(GameView::id, GameView::status)
                .containsExactly(tuple(7L, "PENDING"), tuple(8L, "WON"));
        assertThat(List.of("A", "A", "B")).containsOnly("A", "B");
        assertThat(List.of("A", "A", "B"))
                .containsExactlyInAnyOrder("B", "A", "A");
        assertThat(games).anyMatch(g -> g.status().equals("WON"))
                .noneMatch(g -> g.id() == null);
    }

    @Test void checksOptionalMapAndText() {
        assertThat(Optional.of("A")).contains("A");
        assertThat(Map.of("error", "NOT_FOUND"))
                .containsKey("error").containsEntry("error", "NOT_FOUND");
        assertThat("Game not found: 7").startsWith("Game").contains("not found");
        org.hamcrest.MatcherAssert.assertThat(List.of("A", "B"), hasSize(2));
    }

    @Test void checksThrownExceptionAndItsMessage() {
        var error = assertThrows(IllegalArgumentException.class, () -> requirePositive(0));
        assertEquals("Size must be positive", error.getMessage());
        assertThrowsExactly(IllegalArgumentException.class, () -> requirePositive(-1));
        assertThatThrownBy(() -> requirePositive(0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("positive");
        assertThatCode(() -> requirePositive(8)).doesNotThrowAnyException();
    }

    @Test void groupsAssertJChecks() {
        var game = new GameView(7L, "PENDING");
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(game.id()).isEqualTo(7L);
            softly.assertThat(game.status()).isEqualTo("PENDING");
        });
    }

    static void requirePositive(int size) {
        if (size <= 0) throw new IllegalArgumentException("Size must be positive");
    }
}
```

## 8. Fuentes y referencias próximas

- [API de Assertions de Jupiter](https://docs.junit.org/current/api/org.junit.jupiter.api/org/junit/jupiter/api/Assertions.html).
- [Guía de AssertJ](https://assertj.github.io/doc/).
- [Hamcrest](https://hamcrest.org/JavaHamcrest/).
- [Mockito: configurar respuestas y verificar llamadas](03-mockito.md).
- [MockMvc: comprobar HTTP y JSONPath](04-mockmvc-y-spring-test.md).
