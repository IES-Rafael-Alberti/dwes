# MockMvc y Spring Test — peticiones, expectativas y anotaciones

**Diccionario de consulta para leer los tests web.** MockMvc tramita peticiones
a través de la infraestructura MVC con objetos de servlet de prueba. **No abre
un puerto de red.** Su nombre no significa que todos los componentes sean mocks:
depende del contexto y de los dobles configurados.

## 1. Qué se ejecuta en cada prueba

| Montaje | Qué es real | Qué puede estar simulado |
|---|---|---|
| JUnit + `MockitoExtension` | Objeto Java que se construye/prueba | Sus colaboradores `@Mock` |
| `@WebMvcTest(Controller.class)` | Slice web: controlador, resolución MVC, validación y componentes web seleccionados | Servicio con `@MockitoBean`; no se carga toda la aplicación |
| `@SpringBootTest` + `@AutoConfigureMockMvc` | Contexto de aplicación y acceso mediante MockMvc | Solo lo que se sustituya expresamente; puede haber servicio/repositorio/BD reales |
| `@DataJpaTest` | Slice de persistencia | Depende de su configuración y BD; no es una prueba HTTP |
| `@SpringBootTest(webEnvironment = RANDOM_PORT)` + cliente HTTP | Aplicación con servidor en un puerto de prueba | Dobles solo si se introducen explícitamente |

Un slice web puede incorporar advice, conversores, filtros y seguridad según su
configuración y dependencias. **No afirmar que `@WebMvcTest` desactiva siempre
Spring Security.** Si la petición devuelve 401/403, hay que entender la cadena
cargada y el alcance del caso, no quitar filtros automáticamente.

## 2. Anotaciones de consulta

| Anotación | Función y criterio |
|---|---|
| `@WebMvcTest` | Selecciona una porción web del contexto; no equivale a invocar un controlador con `new` |
| `@SpringBootTest` | Busca configuración de Boot y crea contexto amplio; sin `RANDOM_PORT/DEFINED_PORT` no implica servidor HTTP real |
| `@AutoConfigureMockMvc` | Configura MockMvc en un contexto adecuado, por ejemplo un `SpringBootTest` |
| `@MockitoBean` | Sustituye o registra un bean mock en el contexto para sus consumidores |
| `@MockitoSpyBean` | Integra un spy sobre un bean existente con las condiciones de Spring Test |
| `@Import` | Incluye configuración/componentes concretos que necesita el test; útil para un advice explícito |
| `@Autowired` | Obtiene el componente del contexto, como `MockMvc` |
| `@ActiveProfiles("test")` | Activa un perfil en ese contexto de prueba |
| `@TestPropertySource` | Aporta propiedades específicas del contexto del test |
| `@DynamicPropertySource` | Registra propiedades calculadas al preparar el contexto, por ejemplo un recurso de prueba dinámico |
| `@Transactional` | En tests integrados, permite transacción de prueba con rollback habitual al terminar; no deshace llamadas HTTP a otro servidor en otro hilo |
| `@Sql` | Ejecuta scripts SQL para preparar o limpiar datos según su configuración |
| `@DirtiesContext` | Marca el contexto para descartar/recrear; tiene coste y no es limpieza automática universal de cualquier estado externo |
| `@WithMockUser` | Simula identidad de Spring Security; no obtiene un JWT ni prueba login real |

Imports de Boot 4 que aparecen en el recorrido:

```java
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
```

Los módulos de test deben estar en las dependencias. Copiar un import de Boot 3
no cambia el paquete que realmente contiene la clase en Boot 4.

## 3. El encadenamiento principal

```java
mvc.perform(get("/api/games/{id}", 7))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(7))
        .andDo(print());
```

| Operación | Lectura |
|---|---|
| `perform(requestBuilder)` | Ejecuta la petición MVC construida |
| `andExpect(matcher)` | Comprueba una expectativa; si falla, no continúa la cadena normalmente |
| `andExpectAll(matchers...)` | Evalúa y agrupa las expectativas suministradas |
| `andDo(print())` | Muestra información de petición/respuesta para diagnosticar |
| `andReturn()` | Devuelve `MvcResult` para extraer datos o hacer otras comprobaciones |
| `getResponse()` sobre `MvcResult` | Accede al objeto de respuesta de prueba |
| `getContentAsString()` | Lee el cuerpo como texto; para un binario se utiliza el acceso de bytes apropiado |
| `getHeader("Location")` | Obtiene una cabecera concreta |
| `getResolvedException()` | Consulta la excepción que MVC resolvió, si la hubo |

Un `print()` es una observación, no una aserción. Una excepción que no tiene
tratamiento MVC puede escapar de `perform` envuelta en el error de procesamiento;
no se convierte automáticamente en una respuesta 500 comprobable por el simple
hecho de utilizar MockMvc.

## 4. Construir peticiones

Imports estáticos habituales:

```java
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
```

| Instrucción | Qué construye |
|---|---|
| `get`, `post`, `put`, `patch`, `delete` | Petición con ese método HTTP |
| `options` | Petición OPTIONS, útil en una comprobación de preflight |
| `get("/api/games/{id}", id)` | Sustituye variables del patrón de ruta |
| `.param("status", "PENDING")` | Añade un parámetro de petición; útil para query/form según el montaje |
| `.queryParam("page", "0")` | Añade explícitamente un parámetro de query |
| `.header("Authorization", "Bearer …")` | Envía una cabecera; no crea por sí sola un usuario autenticado |
| `.contentType(APPLICATION_JSON)` | Describe el formato del cuerpo enviado |
| `.accept(APPLICATION_JSON)` | Indica qué formato de respuesta acepta el cliente |
| `.content(json)` | Establece los bytes/texto del cuerpo |
| `.cookie(cookie)` / `.sessionAttr(name, value)` | Prepara cookie/atributo de sesión cuando corresponde |
| `multipart(url).file(file)` | Monta una petición multipart con un archivo de prueba |

`Content-Type` y `Accept` responden a preguntas distintas. Una petición con
`text/plain` a un endpoint que consume JSON puede fallar con 415; un `Accept`
incompatible puede producir 406. Enviar JSON como texto sin declarar el tipo
adecuado no prueba el mismo contrato.

### Multipart de referencia

```java
var file = new MockMultipartFile(
        "file", "avatar.png", "image/png", imageBytes);
mvc.perform(multipart("/api/users/me/avatar").file(file));
```

`MockMultipartFile` pertenece a `org.springframework.mock.web`. Este fragmento
supone un endpoint, bytes y autenticación que se prepararán en el bloque de
archivos. No prueba por sí mismo que se guarden los bytes ni que una imagen sea válida.

## 5. Expectativas de respuesta

| Entrada | Ejemplos / significado |
|---|---|
| `status()` | `isOk`, `isCreated`, `isNoContent`, `isBadRequest`, `isUnauthorized`, `isForbidden`, `isNotFound`, `isConflict`, `isInternalServerError` |
| `header()` | `string("Location", "/api/games/7")`, `exists`, `doesNotExist` |
| `content()` | `string`, `bytes`, `json`, `contentType`, `contentTypeCompatibleWith` |
| `jsonPath()` | Comprobación de un dato seleccionado del JSON |
| `redirectedUrl()` | Destino de redirección, más habitual en MVC con vistas |
| `view()` / `model()` / `flash()` | Vista, modelo y mensajes flash en aplicaciones MVC |
| `request()` | Propiedades de la petición o sesión y ejecución asíncrona, según el caso |

`contentTypeCompatibleWith("application/json")` evita exigir accidentalmente
una variante exacta con charset cuando el contrato no lo exige. No sustituye
las comprobaciones del contenido.

`content().json(expected)` compara JSON con las reglas de la sobrecarga utilizada,
no texto literal; si importa exactitud y orden de arrays, elegir el modo de
comparación apropiado. Para un 204 sin cuerpo, comprobar `content().string("")`
es distinto de exigir JSON vacío.

### 5.1. Diccionario JSONPath

| Expresión / expectativa | Lectura |
|---|---|
| `$` | Raíz del documento |
| `$.id` | Propiedad `id` del objeto raíz |
| `$[0].id` | `id` del primer elemento de un array raíz |
| `$.content[0].id` | `id` del primer elemento del contenido de un objeto paginado |
| `.value(expected)` | El dato seleccionado coincide con el valor |
| `.value(matcher)` | El dato satisface un matcher Hamcrest |
| `.exists()` | Hay un valor no nulo en ese camino |
| `.hasJsonPath()` | El camino existe, aunque su valor sea nulo |
| `.doesNotExist()` | No hay valor no nulo en ese camino |
| `.doesNotHaveJsonPath()` | El camino no existe |
| `.isArray`, `.isMap`, `.isString`, `.isNumber` | Tipo del dato seleccionado |
| `.isEmpty`, `.isNotEmpty` | Vacío/no vacío según el valor seleccionado |

Que exista `$.error` no demuestra que sea el error correcto; si el contrato
exige `NOT_FOUND`, comprobar su valor. El formato paginado no tiene un array
en la raíz: `$[0]` y `$.content[0]` no son intercambiables.

## 6. Preparar un error y comprobar el advice

```java
when(service.getGame(999L)).thenThrow(new GameNotFoundException(999L));
mvc.perform(get("/api/games/999"))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.error").value("NOT_FOUND"));
```

El advice debe formar parte del contexto del test. Un handler local coincidente
tiene prioridad sobre el global; preparar un nuevo advice no elimina esa prioridad.

Para una operación `void`:

```java
doThrow(new GameNotFoundException(999L)).when(service).cancelGame(999L);
mvc.perform(delete("/api/games/999")).andExpect(status().isNotFound());
```

La [referencia de Mockito](03-mockito.md) explica por qué las preparaciones son
distintas y cómo usar `verifyNoInteractions` en los rechazos de entrada.

## 7. Ejemplo completo de slice web

Los tipos web reducidos están incluidos para mostrar la infraestructura completa.
Se reutiliza la excepción del juego; en el proyecto de aula controlador, DTOs,
servicio y advice tienen sus archivos y contratos propios.

<!-- ejemplo-java: MockMvcDictionaryTest.java -->
```java
package es.daw2a.referencias;

import es.daw2a.bshipalong.domain.exceptions.GameNotFoundException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import java.net.URI;
import java.time.Instant;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.*;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MockMvcDictionaryTest.GameController.class)
@Import({MockMvcDictionaryTest.GameController.class, MockMvcDictionaryTest.Errors.class})
class MockMvcDictionaryTest {
    @Autowired MockMvc mvc;
    @MockitoBean GameService service;

    public record CreateGame(@Min(1) int boardSize) {}
    public record GameView(Long id, String status) {}
    public record ErrorView(String error, String message, String timestamp) {}

    public interface GameService {
        GameView get(Long id);
        GameView create(CreateGame request);
        void cancel(Long id);
    }

    @RestController
    @RequestMapping("/api/games")
    public static class GameController {
        private final GameService service;
        public GameController(GameService service) { this.service = service; }

        @GetMapping("/{id}")
        public GameView get(@PathVariable("id") Long id) { return service.get(id); }

        @PostMapping
        public ResponseEntity<GameView> create(@Valid @RequestBody CreateGame input) {
            GameView game = service.create(input);
            return ResponseEntity.created(URI.create("/api/games/" + game.id())).body(game);
        }

        @DeleteMapping("/{id}")
        public ResponseEntity<Void> cancel(@PathVariable("id") Long id) {
            service.cancel(id);
            return ResponseEntity.noContent().build();
        }
    }

    @RestControllerAdvice
    public static class Errors {
        @ExceptionHandler(GameNotFoundException.class)
        public ResponseEntity<ErrorView> missing(GameNotFoundException error) {
            return ResponseEntity.status(404).body(new ErrorView(
                    "NOT_FOUND", error.getMessage(), Instant.now().toString()));
        }
    }

    @Test void getsPreparedGame() throws Exception {
        when(service.get(7L)).thenReturn(new GameView(7L, "PENDING"));
        mvc.perform(get("/api/games/{id}", 7L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(7))
                .andExpect(jsonPath("$.status").value("PENDING"));
        verify(service).get(7L);
    }

    @Test void createsWithLocation() throws Exception {
        when(service.create(new CreateGame(8))).thenReturn(new GameView(17L, "PENDING"));
        mvc.perform(post("/api/games").contentType("application/json")
                        .content("{\"boardSize\":8}"))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/games/17"))
                .andExpect(content().contentTypeCompatibleWith("application/json"));
        verify(service).create(new CreateGame(8));
    }

    @Test void missingGameHasGlobalPayload() throws Exception {
        when(service.get(999L)).thenThrow(new GameNotFoundException(999L));
        mvc.perform(get("/api/games/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("NOT_FOUND"))
                .andExpect(jsonPath("$.timestamp").isNotEmpty());
    }

    @Test void invalidInputDoesNotReachService() throws Exception {
        mvc.perform(post("/api/games").contentType("application/json")
                        .content("{\"boardSize\":0}"))
                .andExpect(status().isBadRequest());
        verifyNoInteractions(service);
    }

    @Test void missingCancellationUsesVoidStubbing() throws Exception {
        doThrow(new GameNotFoundException(999L)).when(service).cancel(999L);
        mvc.perform(delete("/api/games/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("NOT_FOUND"));
        verify(service).cancel(999L);
    }

    @Test void cancellationHasNoBody() throws Exception {
        mvc.perform(delete("/api/games/7"))
                .andExpect(status().isNoContent()).andExpect(content().string(""));
        verify(service).cancel(7L);
    }

    @Test void malformedJsonDoesNotReachService() throws Exception {
        mvc.perform(post("/api/games").contentType("application/json").content("{"))
                .andExpect(status().isBadRequest());
        verifyNoInteractions(service);
    }
}
```

El servicio es un mock. Que `cancellationHasNoBody` pase demuestra la respuesta
y delegación del controlador; **no demuestra una baja lógica ni un DELETE SQL**.

La validación y el JSON ilegible solo exigen 400 en este ejemplo reducido. El
payload completo de esos errores corresponde al advice del proyecto, no se
deduce del comportamiento por defecto de este ejemplo.

## 8. Elegir una evidencia útil

Antes de añadir otro caso, identificar el defecto que detectaría:

- Servicio mock + DTO correcto → contrato y delegación web.
- Servicio real + repositorio/BD real → almacenamiento, reglas y conexión entre capas.
- JSON de error → traducción pública, además del mero código de estado.
- Entrada inválida sin interacción → el rechazo ocurrió antes del servicio.

No se necesita repetir el mismo escenario en todos los niveles si no aporta
otra garantía. Sí conviene distinguir dos pruebas sobre la misma operación que
detectan fallos diferentes.

## 9. Fuentes y continuidad

- [Spring Framework — MockMvc](https://docs.spring.io/spring-framework/reference/testing/mockmvc.html).
- [Peticiones](https://docs.spring.io/spring-framework/reference/testing/mockmvc/hamcrest/requests.html)
  y [expectativas](https://docs.spring.io/spring-framework/reference/testing/mockmvc/hamcrest/expectations.html).
- [JUnit](01-junit-jupiter.md), [aserciones](02-aserciones-junit-assertj-hamcrest.md)
  y [Mockito](03-mockito.md).
- [Anexo de anotaciones](../../05-referencias/anexo-anotaciones-spring-boot.md).
