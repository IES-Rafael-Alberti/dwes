# Mockito — dobles, respuestas y verificación de llamadas

**Diccionario de consulta.** Mockito permite sustituir un colaborador por un
doble controlado. Una prueba del servicio puede ejecutar el servicio real con
un repositorio simulado; una prueba MVC puede ejecutar el controlador real con
el servicio simulado. Hay que saber qué objeto estamos probando de verdad.

## 1. Tres operaciones distintas

```java
when(repository.findById(7L)).thenReturn(Optional.of(game));

var result = service.getGame(7L);

assertThat(result.id()).isEqualTo(7L);
verify(repository).findById(7L);
```

| Parte | Qué significa |
|---|---|
| `when(...).thenReturn(...)` | **Preparar:** cuando el mock reciba esa llamada, responderá con ese valor |
| `service.getGame(...)` | **Actuar:** ejecutar el objeto que estamos probando |
| `assertThat(...)` | **Comprobar resultado:** en este ejemplo, con AssertJ |
| `verify(...)` | **Comprobar interacción:** se recibió esa llamada con esos argumentos |

`thenThrow` o `doThrow` **preparan** un fallo del colaborador. `assertThrows` o
`assertThatThrownBy` **ejecutan y comprueban** que una acción falla. Una operación
no sustituye a la otra.

El método pasado a `when` se evalúa para configurar la llamada. En un mock normal
no ejecuta un cuerpo real; en un spy sí puede ejecutar código real durante esa
preparación. Esta diferencia explica la familia `doReturn/doThrow`.

## 2. Crear e inicializar dobles

| Instrucción / anotación | Qué hace | Matiz |
|---|---|---|
| `mock(Type.class)` | Crea explícitamente un mock | El código real del colaborador no se ejecuta por defecto |
| `@Mock` | Declara un campo/parámetro mock | Necesita inicialización por extensión o mecanismo equivalente |
| `@InjectMocks` | Construye/inicializa el objeto probado e intenta inyectar mocks/spies | No crea un contexto Spring ni resuelve un grafo de dependencias completo |
| `@ExtendWith(MockitoExtension.class)` | Integra Mockito con Jupiter | Inicializa anotaciones y aplica comprobaciones de uso de stubs |
| `spy(realObject)` / `@Spy` | Crea un doble parcial | Los métodos reales pueden ejecutarse si no se sustituyen |
| `@Captor` | Inicializa un capturador tipado | Se utiliza para inspeccionar argumentos de llamadas |
| `MockitoAnnotations.openMocks(this)` | Inicializa anotaciones manualmente | Devuelve un recurso que debe cerrarse; no combinarlo sin necesidad con `MockitoExtension` |

Imports habituales:

```java
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;
```

### Respuestas por defecto

Un mock sin configuración devuelve valores predeterminados: por ejemplo `false`,
cero, `null` para muchos tipos de referencia y valores vacíos para varios tipos
soportados como `Optional` o colecciones. No significan que el sistema haya
buscado datos correctamente. Un `save` que devuelva `null` puede revelar que
faltaba preparar su respuesta, no un fallo de la BD.

## 3. `@Mock` no es `@MockitoBean`

| Situación | Herramienta | Resultado |
|---|---|---|
| Test unitario sin Spring | `@Mock` + `@InjectMocks` y `MockitoExtension` | Objetos Java montados por Mockito |
| Slice MVC con dependencias de Spring | `@MockitoBean` | Mock registrado en el contexto para que lo reciba el controlador |
| Espiar un bean de Spring | `@MockitoSpyBean` | Integra un spy sobre el bean; verificar sus condiciones de uso |

```java
import org.springframework.test.context.bean.override.mockito.MockitoBean;
```

`@MockitoBean` pertenece a **Spring Test**. No es una anotación de Mockito core.
En los ejemplos actuales sustituye el uso de `@MockBean` de versiones anteriores
de Boot. Declarar un `@Mock` corriente no sustituye automáticamente el bean que
ya está usando un controlador dentro de Spring.

Tampoco `@InjectMocks` activa `@Transactional`, validación de métodos o seguridad:
esas funciones dependen de la infraestructura correspondiente.

## 4. Métodos que devuelven un valor: `when` y familia `then…`

| Instrucción | Lectura | Ejemplo |
|---|---|---|
| `when(call).thenReturn(value)` | Al recibir esa llamada, devuelve ese valor | Un DTO que el controlador serializará |
| `.thenReturn(first, second)` | Primera llamada devuelve first; siguientes siguen la secuencia y después mantienen el último valor | Simular dos lecturas distintas deliberadamente |
| `.thenThrow(exception)` | Al recibir la llamada, lanza la excepción | Repositorio o servicio que falla |
| `.thenAnswer(answer)` | Calcula la respuesta usando información de la invocación | Id generado o respuesta basada en el argumento |
| `.thenCallRealMethod()` | Ejecuta el método real cuando exista un cuerpo utilizable | Caso parcial avanzado, no modo normal de un mock |

### `thenReturn`: devuelve datos, no ejecuta una búsqueda

```java
when(service.getGame(7L)).thenReturn(response);
```

El objeto `response` ya se ha construido en el test. No se ha ejecutado el
servicio real ni se ha consultado la BD. Si la llamada recibe otro argumento,
esa preparación puede no coincidir.

### `thenThrow`: configurar una excepción

```java
when(service.getGame(999L)).thenThrow(new GameNotFoundException(999L));
```

Cuando el controlador invoque ese método con ese id, el mock lanzará el fallo.
Las excepciones comprobadas deben ser compatibles con la firma del método;
las de aplicación de tipo `RuntimeException` no necesitan declararse en `throws`.

### `thenAnswer`: leer los argumentos

```java
when(repository.save(any(Game.class))).thenAnswer(invocation -> {
    Game input = invocation.getArgument(0);
    input.setId(17L);
    return input;
});
```

`getArgument(0)` obtiene el primer argumento. El test está **simulando** la
asignación de un id; no demuestra que una secuencia o columna identity funcione.

## 5. Métodos void: `doThrow`, `doNothing` y `doAnswer`

Un método `void` no produce una expresión de valor que podamos pasar a `when`.
Por eso se escribe:

```java
doThrow(new GameNotFoundException(999L))
        .when(service).cancelGame(999L);
```

Lectura literal: «configura este mock para que, **cuando se invoque**
`cancelGame(999L)`, lance esa excepción». Esa línea prepara la reacción; la
invocación que queremos probar ocurre después.

| Familia `do…` | Qué prepara | Matiz |
|---|---|---|
| `doThrow(error).when(mock).operation()` | Lanzar al invocar | Habitual para métodos `void`; también puede aplicarse a otros |
| `doNothing().when(mock).operation()` | No hacer nada | Ya es el comportamiento normal de un `void` en un mock; más útil con un spy |
| `doAnswer(answer).when(mock).operation()` | Reacción calculada | Para `void`, la respuesta del answer devuelve normalmente `null` |
| `doReturn(value).when(spy).operation()` | Responder sin invocar el método real al configurar | Útil en spies y en algunas reconfiguraciones especiales |
| `doCallRealMethod().when(mock).operation()` | Invocar el método real | Requiere una implementación y un estado apropiados |

Ejemplo de uso conjunto, cada instrucción con su función:

```java
doThrow(new IllegalStateException("Cannot cancel"))
        .when(service).cancelGame(7L);

assertThrows(IllegalStateException.class, () -> service.cancelGame(7L));
verify(service).cancelGame(7L);
```

En un test MVC, la acción sería `mvc.perform(delete(...))` y la expectativa
sería el código/error público; el test no debe esperar a la vez que el advice
resuelva la excepción y que esa misma excepción escape sin resolver.

## 6. Matchers: qué argumentos acepta la preparación

| Matcher | Qué acepta |
|---|---|
| `eq(value)` | Valor igual según la comparación que usa Mockito |
| `same(object)` | La misma referencia |
| `any()` | Cualquier referencia, incluido `null` |
| `any(Type.class)` | Instancia de ese tipo, no `null` |
| `anyString`, `anyLong`, `anyInt`… | Valor del tipo correspondiente; los de referencia tipados no aceptan `null` |
| `isNull()` / `notNull()` | Nulo / no nulo |
| `nullable(Type.class)` | Nulo o instancia de ese tipo |
| `argThat(predicate)` | Condición propia sobre el argumento |

Si una llamada utiliza matchers, **todos sus argumentos deben proporcionarse
mediante matchers**:

```java
when(repository.search(eq("PENDING"), anyInt())).thenReturn(results);
```

Mezclar `"PENDING"` sin `eq` con `anyInt()` en esa misma llamada es incorrecto.
No guardar un matcher en una variable ni usarlo como valor para llamar al
servicio real. Los matchers registran criterios internos y devuelven valores
auxiliares; no son los datos que el caso de uso necesita.

Sin matchers, Mockito compara normalmente los argumentos mediante `equals`.
Por eso un DTO record suele facilitar la preparación con un valor concreto.

## 7. `verify`: comprobar lo que ya ocurrió

```java
verify(service).getGame(7L);
```

Exige exactamente **una** llamada coincidente. No ejecuta otra vez el método
real y no prepara el valor que devolverá.

| Instrucción | Garantía |
|---|---|
| `verify(mock).operation()` | Una llamada coincidente, por defecto |
| `verify(mock, times(n)).operation()` | Exactamente n |
| `verify(mock, never()).operation()` | Ninguna llamada coincidente |
| `atLeastOnce()` / `atLeast(n)` | Mínimo de llamadas |
| `atMost(n)` / `atMostOnce()` | Máximo de llamadas |
| `only()` | Esa es la única interacción del mock, según ese modo de verificación |
| `verifyNoInteractions(mock)` | No hubo interacciones con ese mock |
| `verifyNoMoreInteractions(mock)` | No quedan interacciones sin verificar |
| `inOrder(mockA, mockB)` | Permite verificar el orden de las llamadas seleccionadas |
| `timeout(ms)` | Espera hasta ese límite a que se cumpla una verificación |

`verifyNoInteractions` es útil para demostrar que una petición rechazada por
validación no alcanzó el servicio. `verifyNoMoreInteractions` puede volver una
prueba demasiado dependiente de detalles internos si se añade a todo por rutina.

Los modos con tiempo son para escenarios asíncronos concretos, no una razón
para añadir esperas a pruebas ordinarias de controlador o servicio.

## 8. Capturar argumentos y ordenar llamadas

```java
ArgumentCaptor<Game> captor = ArgumentCaptor.forClass(Game.class);
verify(repository).save(captor.capture());
assertThat(captor.getValue().getStatus()).isEqualTo(GameStatus.PENDING);
```

El captor muestra **qué objeto se pasó al mock**. No demuestra que se haya
guardado una fila. `getAllValues()` permite examinar varias llamadas verificadas.

Para el orden:

```java
InOrder order = inOrder(repository);
order.verify(repository).findById(7L);
order.verify(repository).deleteById(7L);
```

Comprobar orden solo cuando importa para el comportamiento. No inmovilizar una
implementación interna que podría refactorizarse sin cambiar sus garantías.

## 9. Spies, strict stubbing y otras entradas de consulta

- En un **spy**, `when(spy.method())` puede ejecutar el método real durante la
  preparación. `doReturn(value).when(spy).method()` evita esa ejecución.
- `UnnecessaryStubbingException` suele indicar una preparación no utilizada:
  comprobar el test y sus argumentos antes de silenciarla.
- `lenient()` relaja comprobaciones para una preparación concreta; no es la
  solución predeterminada para una suite con montajes excesivos.
- `clearInvocations(mock)` borra el historial de llamadas, conservando stubs;
  `reset(mock)` borra también la configuración. Normalmente es más claro un
  doble nuevo por test que reiniciarlo a mitad de una prueba.
- BDDMockito ofrece `given(...).willReturn(...)` y `then(mock).should()`. Es otra
  sintaxis para responsabilidades equivalentes, no un requisito añadido.
- Simular métodos estáticos o construcciones son posibilidades avanzadas; no
  sustituyen un diseño con dependencias explícitas para los ejemplos del módulo.

## 10. Ejemplo completo y verificable

La interfaz y el servicio mínimos están dentro de la clase para que el ejemplo
se pueda leer sin otro proyecto. No sustituyen a los tipos del juego.

<!-- ejemplo-java: MockitoDictionaryTest.java -->
```java
package es.daw2a.referencias;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MockitoDictionaryTest {
    record GameView(Long id, String status) {}

    interface Repository {
        Optional<GameView> find(Long id);
        GameView save(GameView game);
        void delete(Long id);
        String label(String prefix, long id);
    }

    static class Service {
        private final Repository repository;
        Service(Repository repository) { this.repository = repository; }

        GameView get(Long id) {
            if (id < 0) throw new IllegalArgumentException("Invalid id");
            return repository.find(id).orElseThrow();
        }

        GameView create() { return repository.save(new GameView(null, "PENDING")); }
        void cancel(Long id) { repository.delete(id); }
    }

    @Mock Repository repository;
    @InjectMocks Service service;
    @Captor ArgumentCaptor<GameView> captor;

    @Test void returnsPreparedValueAndVerifiesCall() {
        var game = new GameView(7L, "PENDING");
        when(repository.find(7L)).thenReturn(Optional.of(game));
        assertThat(service.get(7L)).isEqualTo(game);
        verify(repository).find(7L);
    }

    @Test void thenThrowFailsWhenInvoked() {
        when(repository.find(7L)).thenThrow(new IllegalStateException("Unavailable"));
        assertThrows(IllegalStateException.class, () -> service.get(7L));
        verify(repository).find(7L);
    }

    @Test void doThrowConfiguresVoidOperation() {
        doThrow(new IllegalStateException("Cannot cancel")).when(repository).delete(7L);
        assertThrows(IllegalStateException.class, () -> service.cancel(7L));
        verify(repository).delete(7L);
    }

    @Test void answerUsesArgumentAndCaptorInspectsIt() {
        when(repository.save(any(GameView.class))).thenAnswer(invocation -> {
            GameView input = invocation.getArgument(0);
            return new GameView(17L, input.status());
        });
        assertThat(service.create().id()).isEqualTo(17L);
        verify(repository).save(captor.capture());
        assertThat(captor.getValue().id()).isNull();
        assertThat(captor.getValue().status()).isEqualTo("PENDING");
    }

    @Test void usesMatchersForEveryArgument() {
        when(repository.label(eq("game"), anyLong())).thenReturn("game-7");
        assertThat(repository.label("game", 7L)).isEqualTo("game-7");
        verify(repository).label(eq("game"), eq(7L));
    }

    @Test void rejectsInputBeforeCallingDependency() {
        assertThrows(IllegalArgumentException.class, () -> service.get(-1L));
        verifyNoInteractions(repository);
    }

    @Test void verifiesSelectedOrder() {
        repository.find(7L);
        repository.delete(7L);
        var order = inOrder(repository);
        order.verify(repository).find(7L);
        order.verify(repository).delete(7L);
        verifyNoMoreInteractions(repository);
    }

    @Test void doReturnDoesNotReadEmptyRealListDuringStubbing() {
        List<String> list = spy(new ArrayList<String>());
        doReturn("prepared").when(list).get(0);
        assertThat(list.get(0)).isEqualTo("prepared");
        assertThat(list).isEmpty();
    }
}
```

En el último caso, el spy sigue representando una lista vacía, aunque su `get(0)`
esté preparado para devolver una cadena. Es una demostración de simulación,
no un comportamiento válido de una lista ordinaria que debamos programar.

## 11. Fuentes y referencias próximas

- [API y ejemplos oficiales de Mockito](https://javadoc.io/doc/org.mockito/mockito-core/latest/org.mockito/org/mockito/Mockito.html).
- [Integración `@MockitoBean` de Spring](https://docs.spring.io/spring-framework/reference/testing/annotations/integration-spring/annotation-mockitobean.html).
- [JUnit y ejecución](01-junit-jupiter.md).
- [Aserciones y excepciones](02-aserciones-junit-assertj-hamcrest.md).
- [Peticiones MVC](04-mockmvc-y-spring-test.md).
- [Guía extensa de Mockito y AssertJ con Eventos](../../../03-ejercicios/03-gestion-eventos/recursos/GestionEventos/Documentacion/Tests/TestsUnitarios_Mockito.md).

Si aparece el aviso del agente inline con Java reciente, consultar la
[configuración de Mockito ya documentada](../02-tdd-primeros-pasos.md#mockito-como-agente-de-la-jvm-cambio-motivado-por-un-aviso)
antes de cambiar de mock maker o fijar otra versión de dependencias.
