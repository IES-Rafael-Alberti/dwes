# JUnit Jupiter — ejecución, anotaciones y parametrizados

**Diccionario de consulta.** JUnit descubre y ejecuta pruebas; Mockito simula
colaboradores y AssertJ ofrece otra API de aserciones. Se pueden utilizar juntos,
pero no son la misma herramienta. Vuelve al [mapa de referencias](README.md)
si no sabes a cuál pertenece una instrucción.

## 1. Las piezas de JUnit

| Nombre | Qué significa |
|---|---|
| JUnit Platform | Infraestructura para descubrir y ejecutar motores de pruebas |
| JUnit Jupiter | Modelo de programación que utilizamos: anotaciones, extensiones y motor de ejecución |
| JUnit Vintage | Compatibilidad con tests antiguos de JUnit 3/4 cuando se incluye ese motor; no es el modelo nuevo de nuestros ejemplos |
| Test | Caso ejecutable que comprueba un comportamiento concreto |
| Fixture | Datos y objetos preparados para el caso |
| Aserción | Comprobación que falla si no se cumple una expectativa |
| Suite | Conjunto de pruebas; en clase también usamos el término para la ejecución completa de tests del proyecto |

### Imports que debes reconocer

```java
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
```

`org.junit.Test` es el import antiguo de JUnit 4. No mezclarlo accidentalmente
con las anotaciones de Jupiter. Las dependencias del proyecto determinan qué
versiones se ejecutan; el nombre del paquete no basta para deducir la versión mayor.

## 2. Ciclo de vida

| Anotación | Qué hace | Uso y matiz |
|---|---|---|
| `@Test` | Declara un método de prueba ordinario | No basta con que su nombre termine en `Test` |
| `@BeforeEach` | Se ejecuta antes de cada caso/invocación | Preparar objetos nuevos y evitar estado compartido |
| `@AfterEach` | Se ejecuta después de cada caso/invocación | Liberar recursos abiertos por ese caso |
| `@BeforeAll` | Preparación de la clase antes de sus casos | Normalmente método `static` |
| `@AfterAll` | Limpieza tras los casos de la clase | Normalmente método `static` |
| `@TestInstance(PER_METHOD)` | Instancia de la clase de test por método/invocación | Modo predeterminado de Jupiter |
| `@TestInstance(PER_CLASS)` | Comparte una instancia para la clase | Permite `BeforeAll/AfterAll` no estáticos; obliga a vigilar el estado compartido |

Para dos métodos ordinarios, el esquema habitual es:

```text
BeforeAll
  nueva instancia → BeforeEach → caso A → AfterEach
  nueva instancia → BeforeEach → caso B → AfterEach
AfterAll
```

Esto **no fija el orden A/B**. Tampoco significa que Spring cree un contexto nuevo
por test: el ciclo de instancias de JUnit y la caché de contextos de Spring son
responsabilidades distintas.

Un método de prueba no debe ser `private`, `static` ni abstracto; normalmente
devuelve `void`. Las clases y métodos pueden ser package-private. `@BeforeAll`
y `@AfterAll` son los casos habituales donde sí se usa `static`.

## 3. Organización, nombres y selección

| Anotación / API | Lectura | Cuidado con… |
|---|---|---|
| `@DisplayName("...")` | Nombre legible en el informe | No cambia el método Java ni lo que comprueba |
| `@Nested` | Agrupa casos en una clase interna no estática | Organización del test, no otra capa de aplicación |
| `@Tag("integration")` | Etiqueta los casos | No hace que Maven los excluya o separe sin configurar el filtrado |
| `@Disabled("motivo")` | Omite el caso/clase | Un caso omitido no cuenta como comportamiento probado |
| `@RepeatedTest(3)` | Repite el mismo caso tres veces | No sustituye una tabla de entradas ni demuestra ausencia de carreras |
| `@Timeout(...)` | Fija un límite de ejecución para el elemento anotado | Una condición temporal no debe convertirse en un test de rendimiento frágil |
| `@TestMethodOrder(...)` y `@Order` | Configuran orden explícito | No usarlos para que un test cree los datos que otro necesita |
| `@ExtendWith(...)` | Registra extensiones de Jupiter | `MockitoExtension` inicializa mocks; `SpringExtension` integra Spring Test |
| `TestInfo` | Recibe información del caso como parámetro | Se puede inyectar en métodos soportados por Jupiter |
| `@TempDir` | Proporciona un directorio temporal para una prueba | Utilizarlo para pruebas de archivos en vez de rutas del equipo del autor |

`@SpringBootTest` y los slices de Boot ya integran la extensión de Spring en el
uso normal. No hay que apilar anotaciones por costumbre para «activar todo».

## 4. Pruebas parametrizadas

Una prueba parametrizada ejecuta el mismo comportamiento para varias entradas.
**No lleva además `@Test`.** La fuente proporciona argumentos al método.

| Anotación | Datos que aporta | Ejemplo de uso |
|---|---|---|
| `@ParameterizedTest` | Declara el caso con varias invocaciones | Nombre configurable con `{index}` y argumentos |
| `@ValueSource` | Un argumento simple por invocación | `ints`, `strings`, `longs`… |
| `@CsvSource` | Varias columnas por fila | Entrada y resultado esperado |
| `@CsvFileSource` | Filas desde un recurso CSV | Conjuntos mayores que no conviene incrustar en la clase |
| `@MethodSource` | Argumentos creados por un método Java | Objetos o escenarios complejos |
| `@EnumSource` | Valores de un enum | Seleccionar todos o algunos estados |
| `@NullSource` | Un argumento `null` | No sirve para un parámetro primitivo |
| `@EmptySource` | Un valor vacío de un tipo soportado | Por ejemplo una cadena o colección vacía |
| `@NullAndEmptySource` | Combina nulo y vacío | Se puede combinar con `ValueSource` para añadir espacios |

### 4.1. Ejemplo de lectura

```java
@ParameterizedTest
@CsvSource({"2, 3, 5", "0, 4, 4"})
void adds(int left, int right, int expected) {
    assertEquals(expected, left + right);
}
```

Son dos invocaciones: `(2,3,5)` y `(0,4,4)`. JUnit convierte los argumentos al
tipo declarado. Los espacios y las reglas de comillas/nulos de CSV no equivalen
automáticamente a las de un archivo separado por comas escrito a mano: consulta
la anotación si necesitas distinguir cadena vacía y `null`.

### 4.2. Fuentes mediante métodos

```java
static Stream<Arguments> boardSizes() {
    return Stream.of(Arguments.of(8, true), Arguments.of(0, false));
}
```

Un método local usado con `@MethodSource` suele ser `static`, salvo que se utilice
`PER_CLASS`. Los tipos `Stream` y `Arguments` requieren sus imports; el ejemplo
completo de abajo los incluye.

## 5. Aserciones, supuestos y resultados

- Una **aserción** que no se cumple marca fallo: `assertEquals`, `assertThrows`…
- Un **supuesto** que no se cumple aborta esa invocación: `Assumptions.assumeTrue`…
- Un caso **deshabilitado** se omite por configuración/anotación.
- Un error de montaje o de arranque puede impedir ejecutar el comportamiento.

Ejemplo de supuesto: una prueba requiere una condición del entorno que no está
disponible. No debe usarse `assumeTrue` para esconder una regla de negocio fallida.

Para los métodos de comprobación concretos, consulta el
[diccionario de aserciones](02-aserciones-junit-assertj-hamcrest.md).

## 6. Ejemplo completo de consulta

El ejemplo usa una validación mínima de tamaño para mostrar el mecanismo de
JUnit. No impone nuevos límites al contrato del juego ni añade otra práctica.

<!-- ejemplo-java: JunitDictionaryTest.java -->
```java
package es.daw2a.referencias;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class JunitDictionaryTest {
    private List<String> names;

    @BeforeEach void prepare() {
        names = new ArrayList<>();
    }

    @Test
    @DisplayName("Cada caso prepara una lista vacía")
    void startsEmpty() {
        assertTrue(names.isEmpty());
        names.add("A");
        assertEquals(1, names.size());
    }

    @ParameterizedTest(name = "{0} + {1} = {2}")
    @CsvSource({"2, 3, 5", "0, 4, 4"})
    void adds(int left, int right, int expected) {
        assertEquals(expected, left + right);
    }

    @ParameterizedTest
    @MethodSource("boardSizes")
    void recognizesPositiveSize(int size, boolean expected) {
        assertEquals(expected, size > 0);
    }

    static Stream<Arguments> boardSizes() {
        return Stream.of(Arguments.of(8, true), Arguments.of(0, false));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "   "})
    void rejectsMissingName(String name) {
        assertThrows(IllegalArgumentException.class, () -> requireName(name));
    }

    static String requireName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name is required");
        }
        return name;
    }
}
```

La clase tiene cuatro métodos de prueba, pero más invocaciones ejecutadas por
los parametrizados. **Número de métodos, número de invocaciones y garantías de
comportamiento no son tres formas equivalentes de medir cobertura.**

## 7. Ejecutar y leer la salida

Desde la raíz de un proyecto Maven con wrapper:

```bash
./mvnw test
./mvnw -Dtest=NombreDelTest test
./mvnw -Dtest=NombreDelTest#nombreDelMetodo test
```

En Windows: `mvnw.cmd`. En un proyecto Gradle se utiliza su wrapper y sus filtros,
no se copian las opciones `-Dtest` de Maven. En IntelliJ se puede ejecutar un
método, una clase o el goal de Maven; este último comparte la configuración de
Surefire, por ejemplo el agente de Mockito que ya utiliza el proyecto de aula.

`@Test` no define por sí mismo la selección de archivos de Maven: los patrones
del plugin y sus opciones de ejecución también intervienen.

Al leer un fallo, buscar **el primer comportamiento que no se cumple**, la línea
del test y los valores esperado/obtenido. Si falla el arranque del contexto o
la compilación, todavía no se ha probado la regla que pretendíamos ejecutar.

## 8. Fuentes y continuidad

- [JUnit User Guide](https://docs.junit.org/current/user-guide/).
- [API de Jupiter](https://docs.junit.org/current/api/org.junit.jupiter.api/org/junit/jupiter/api/package-summary.html).
- [Aserciones](02-aserciones-junit-assertj-hamcrest.md), [Mockito](03-mockito.md)
  y [Spring Test / MockMvc](04-mockmvc-y-spring-test.md).
- [Anotaciones de Spring y Boot](../../05-referencias/anexo-anotaciones-spring-boot.md).
