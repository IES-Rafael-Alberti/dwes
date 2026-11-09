## Paso 5: Manejo de Errores y Excepciones Personalizadas

### 1. Implementación de `@ExceptionHandler` en Controladores

Podemos usar `@ExceptionHandler` para manejar excepciones específicas en controladores. Agregaremos excepciones personalizadas y genéricas para manejar errores, como solicitudes inválidas o recursos no encontrados.

#### Ejemplo de Excepciones Personalizadas

Creamos una excepción personalizada `RecursoNoEncontradoException` para manejar errores de búsqueda de entidades, como `Libro` o `Usuario`.

```java
public class RecursoNoEncontradoException extends RuntimeException {
    public RecursoNoEncontradoException(String mensaje) {
        super(mensaje);
    }
}
```

#### Ejemplo de `@ExceptionHandler` en `LibroController`

```java
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LibroController {

    // Métodos de controlador (omitir por brevedad)

    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ResponseEntity<String> manejarRecursoNoEncontradoException(RecursoNoEncontradoException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> manejarIllegalArgumentException(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Solicitud inválida: " + ex.getMessage());
    }
}
```

- **`@ExceptionHandler(RecursoNoEncontradoException.class)`**: Captura esta excepción y devuelve un estado `404 Not Found` con el mensaje del error.
- **`@ExceptionHandler(IllegalArgumentException.class)`**: Captura `IllegalArgumentException` y responde con un estado `400 Bad Request`.

### 2. Implementación de un Controlador Global de Excepciones con `@ControllerAdvice`

Para capturar y manejar todas las excepciones de manera centralizada, usaremos `@ControllerAdvice`. Esto permite gestionar errores de forma unificada sin repetir código en cada controlador.

#### Ejemplo de `@ControllerAdvice` Global

```java
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ResponseEntity<String> manejarRecursoNoEncontradoException(RecursoNoEncontradoException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> manejarIllegalArgumentException(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Solicitud inválida: " + ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> manejarExcepcionGenerica(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ha ocurrido un error inesperado.");
    }
}
```

- **`@ControllerAdvice`**: Define un controlador global para manejar todas las excepciones que ocurran en la aplicación.
- **`@ExceptionHandler(Exception.class)`**: Captura cualquier excepción no especificada y devuelve un `500 Internal Server Error`.

### 3. Implementación de `ErrorController` para Personalizar Errores de Navegador

Un `ErrorController` permite personalizar respuestas de errores genéricos, como `404` o `500`, para solicitudes no mapeadas en los controladores.

#### Ejemplo de `ErrorController`

```java
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;

@Controller
public class CustomErrorController implements ErrorController {

    @RequestMapping("/error")
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String manejarError(HttpServletRequest request) {
        Integer statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");

        if (statusCode != null && statusCode == HttpStatus.NOT_FOUND.value()) {
            return "error-404"; // Nombre de la vista de error 404 personalizada
        } else {
            return "error-500"; // Nombre de la vista de error 500 personalizada
        }
    }
}
```

- **`@RequestMapping("/error")`**: Define la ruta de error personalizada.
- **`HttpStatus.NOT_FOUND`**: Devuelve una vista específica para el error `404`.
- Puedes personalizar aún más el manejo de errores para otros códigos de estado, como `500`, agregando lógica adicional.

### Integración y Refactorización en los Servicios

Con la lógica de negocio ya en los servicios, también podemos lanzar excepciones desde los servicios y dejarlas capturar por `@ControllerAdvice` y `@ExceptionHandler`.

Por ejemplo, en `LibroService` podemos lanzar `RecursoNoEncontradoException`:

```java
public Optional<Libro> obtenerLibro(Long id) {
    return libroRepository.findById(id)
            .orElseThrow(() -> new RecursoNoEncontradoException("Libro con ID " + id + " no encontrado"));
}
```

De esta manera, el `GlobalExceptionHandler` captura la excepción lanzada desde el servicio y la maneja de forma centralizada.

---

### Resumen

1. **Añadimos manejo de errores** con:
   - **`@ExceptionHandler`** en controladores para errores específicos.
   - **`@ControllerAdvice`** para manejo global de errores.
   - **`ErrorController`** para personalizar respuestas de errores de navegador (404 y otros).

2. **Refactorización**: Integramos estos cambios en los servicios, lanzando excepciones desde los servicios y centralizando el manejo en el controlador global.

Esto hará que la aplicación sea más robusta y fácil de mantener, además de proporcionar una respuesta clara y consistente para los usuarios ante cualquier error. ¡Avísame si quieres profundizar en algún punto o si tienes algún ajuste en mente!

---
Agregar excepciones personalizadas en una aplicación **Spring Boot** permite un manejo de errores más preciso y mensajes más claros, especialmente en casos donde las excepciones genéricas no proporcionan suficiente información. A continuación, vamos a revisar cómo crear y usar excepciones personalizadas de manera efectiva.

### Pasos para Agregar Excepciones Personalizadas

1. **Crear la clase de excepción personalizada**: Define una nueva clase para cada tipo de excepción personalizada que necesites. Extiende `RuntimeException` para evitar el manejo obligatorio de excepciones (excepción no verificada).
2. **Usar excepciones personalizadas en servicios**: Lanza estas excepciones en los métodos de servicio cuando se cumplan condiciones de error específicas.
3. **Manejar excepciones personalizadas con `@ExceptionHandler` o `@ControllerAdvice`**: Captura y gestiona las excepciones personalizadas en los controladores o de forma global.

---

### Paso 1: Definir Clases de Excepción Personalizadas

Para cada tipo de condición de error, crea una clase de excepción dedicada. Aquí tienes algunos ejemplos comunes:

#### Ejemplo 1: `RecursoNoEncontradoException`

Esta excepción se lanza cuando un recurso solicitado (como un `Libro` o `Usuario`) no se encuentra en la base de datos.

```java
public class RecursoNoEncontradoException extends RuntimeException {
    public RecursoNoEncontradoException(String mensaje) {
        super(mensaje);
    }
}
```

#### Ejemplo 2: `SolicitudInvalidaException`

Esta excepción se puede lanzar cuando se recibe una entrada o solicitud no válida.

```java
public class SolicitudInvalidaException extends RuntimeException {
    public SolicitudInvalidaException(String mensaje) {
        super(mensaje);
    }
}
```

#### Ejemplo 3: `OperacionNoPermitidaException`

Esta excepción puede manejar casos donde no se permite una operación, como intentar eliminar un recurso en uso.

```java
public class OperacionNoPermitidaException extends RuntimeException {
    public OperacionNoPermitidaException(String mensaje) {
        super(mensaje);
    }
}
```

Cada clase de excepción es sencilla e incluye solo un constructor con un mensaje. Puedes añadir más constructores o métodos si es necesario, como pasar una `causa` para mayor contexto.

---

### Paso 2: Usar Excepciones Personalizadas en los Servicios

En tus clases de servicio, lanza las excepciones personalizadas cuando se cumplan condiciones de error específicas. A continuación, cómo usarlas en un servicio, como `LibroService`.

```java
import com.biblioteca.gestion.entities.Libro;
import com.biblioteca.gestion.repositories.LibroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class LibroService {

    @Autowired
    private LibroRepository libroRepository;

    public Libro obtenerLibro(Long id) {
        return libroRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Libro con ID " + id + " no encontrado"));
    }

    public Libro actualizarLibro(Long id, Libro libroActualizado) {
        if (!libroRepository.existsById(id)) {
            throw new RecursoNoEncontradoException("Libro con ID " + id + " no encontrado");
        }

        // Validación adicional
        if (libroActualizado.getTitulo() == null || libroActualizado.getTitulo().isEmpty()) {
            throw new SolicitudInvalidaException("El título del libro no puede estar vacío");
        }

        // Lógica de actualización aquí
        Libro libro = libroRepository.findById(id).orElseThrow();
        libro.setTitulo(libroActualizado.getTitulo());
        // Actualizaciones adicionales

        return libroRepository.save(libro);
    }
}
```

- **`RecursoNoEncontradoException`**: Se lanza si el libro solicitado no existe.
- **`SolicitudInvalidaException`**: Se utiliza para manejar problemas de validación.

---

### Paso 3: Manejar Excepciones Personalizadas de Forma Global con `@ControllerAdvice`

Para capturar y manejar todas las excepciones de manera centralizada, usaremos `@ControllerAdvice`. Esto permite gestionar errores de forma unificada sin repetir código en cada controlador.

#### Ejemplo de `GlobalExceptionHandler`

```java
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ResponseEntity<String> manejarRecursoNoEncontradoException(RecursoNoEncontradoException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(SolicitudInvalidaException.class)
    public ResponseEntity<String> manejarSolicitudInvalidaException(SolicitudInvalidaException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(OperacionNoPermitidaException.class)
    public ResponseEntity<String> manejarOperacionNoPermitidaException(OperacionNoPermitidaException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
    }

    // Manejador genérico para cualquier otra excepción
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> manejarExcepcionGenerica(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                             .body("Ha ocurrido un error inesperado: " + ex.getMessage());
    }
}
```

Cada método con `@ExceptionHandler`:
- Captura el tipo de excepción especificado en el parámetro del método.
- Devuelve un código de estado HTTP apropiado para el error (por ejemplo, `404 Not Found` para `RecursoNoEncontradoException`).
- Proporciona un mensaje de error personalizado en el cuerpo de la respuesta, a menudo derivado de `ex.getMessage()`.

Este enfoque mantiene la lógica de manejo de errores limpia y centralizada.

---

### Paso 4: Probar las Excepciones Personalizadas

Una vez que las excepciones personalizadas están en su lugar, prueba tus endpoints usando **Postman**, **Insomnia** o una herramienta similar para asegurarte de que cada escenario devuelve la respuesta y el código de estado esperados.

1. **Recurso no existente**: Intenta recuperar un recurso que no existe. Esto debería activar una `RecursoNoEncontradoException`.
2. **Entrada inválida**: Envía datos no válidos (por ejemplo, campos requeridos ausentes) para activar una `SolicitudInvalidaException`.
3. **Operación prohibida**: Intenta realizar una operación que esté prohibida por las reglas de negocio para activar una `OperacionNoPermitidaException`.

### Resumen

1. **Definir Excepciones Personalizadas**: Crea excepciones específicas para las condiciones de error más comunes.
2. **Lanzar Excepciones en los Servicios**: Usa las excepciones personalizadas para manejar errores en tus métodos de servicio.
3. **Manejo Centralizado**: Usa `@ControllerAdvice` y `@ExceptionHandler` para gestionar estas excepciones de forma global.
4. **Probar las Respuestas**: Verifica que las excepciones personalizadas devuelvan los códigos de estado y mensajes de error apropiados.

Esta estructura proporciona un mecanismo de manejo de errores claro y estandarizado para tu API, que además es fácil de extender y mantener.

---

Para manejar excepciones específicas en servicios y en toda la aplicación, Spring Boot proporciona herramientas como **excepciones personalizadas**, `@ControllerAdvice` y `@ExceptionHandler`. Esto permite que los servicios arrojen excepciones específicas según el contexto, y que el manejo de errores se centralice y estandarice.

A continuación, detallo cómo manejar excepciones en los servicios y cómo `@ControllerAdvice` facilita el manejo centralizado de excepciones en toda la aplicación.

---

### Manejo de Excepciones Específicas en Servicios

1. **Lanza Excepciones en los Servicios**:
   - En los métodos de servicio, cuando ocurre una condición de error (por ejemplo, un recurso no encontrado o una entrada inválida), se lanza una excepción específica.
   - Esto permite que el controlador o `@ControllerAdvice` capture la excepción y devuelva una respuesta de error clara al cliente.

#### Ejemplo de Uso de Excepciones en un Servicio

Supongamos que tenemos un servicio `LibroService` y queremos manejar situaciones específicas como:
- **Libro no encontrado**.
- **Actualización con datos no válidos**.

Primero, definimos excepciones personalizadas para estas situaciones.

```java
public class RecursoNoEncontradoException extends RuntimeException {
    public RecursoNoEncontradoException(String mensaje) {
        super(mensaje);
    }
}

public class SolicitudInvalidaException extends RuntimeException {
    public SolicitudInvalidaException(String mensaje) {
        super(mensaje);
    }
}
```

Ahora, usamos estas excepciones en `LibroService`:

```java
import com.biblioteca.gestion.entities.Libro;
import com.biblioteca.gestion.repositories.LibroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class LibroService {

    @Autowired
    private LibroRepository libroRepository;

    public Libro obtenerLibro(Long id) {
        return libroRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Libro con ID " + id + " no encontrado"));
    }

    public Libro actualizarLibro(Long id, Libro libroActualizado) {
        if (!libroRepository.existsById(id)) {
            throw new RecursoNoEncontradoException("Libro con ID " + id + " no encontrado");
        }

        if (libroActualizado.getTitulo() == null || libroActualizado.getTitulo().isEmpty()) {
            throw new SolicitudInvalidaException("El título del libro no puede estar vacío");
        }

        Libro libro = libroRepository.findById(id).orElseThrow();
        libro.setTitulo(libroActualizado.getTitulo());
        return libroRepository.save(libro);
    }
}
```

- **RecursoNoEncontradoException**: Se lanza si el libro solicitado no existe.
- **SolicitudInvalidaException**: Se usa para manejar problemas de validación (por ejemplo, título vacío).

Al delegar estos errores a excepciones específicas en el servicio, los controladores pueden encargarse de manejarlas y responder adecuadamente.

---

### Manejo Centralizado de Excepciones con `@ControllerAdvice`

**`@ControllerAdvice`** es una anotación de Spring que permite definir una clase centralizada para manejar excepciones de todos los controladores. Esto es especialmente útil para aplicar lógica de manejo de errores consistente en toda la aplicación sin duplicar código en cada controlador.

#### ¿Cómo Funciona `@ControllerAdvice`?

- **Intercepta Excepciones**: `@ControllerAdvice` captura las excepciones lanzadas en los controladores y servicios.
- **Define Respuestas Personalizadas**: Con `@ExceptionHandler`, puedes definir cómo responder a excepciones específicas, configurando mensajes de error y códigos de estado HTTP personalizados.
- **Centralización del Manejo de Errores**: Al usar `@ControllerAdvice`, el manejo de excepciones se concentra en una clase, facilitando el mantenimiento y la claridad del código.

#### Ejemplo de `@ControllerAdvice`

Aquí definimos una clase `GlobalExceptionHandler` que maneja nuestras excepciones personalizadas.

```java
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ResponseEntity<String> manejarRecursoNoEncontradoException(RecursoNoEncontradoException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(SolicitudInvalidaException.class)
    public ResponseEntity<String> manejarSolicitudInvalidaException(SolicitudInvalidaException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    // Manejo de excepciones generales no previstas
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> manejarExcepcionGenerica(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                             .body("Ha ocurrido un error inesperado: " + ex.getMessage());
    }
}
```

- **`@ExceptionHandler(RecursoNoEncontradoException.class)`**: Este método maneja la excepción `RecursoNoEncontradoException`. Devuelve un estado `404 Not Found` y el mensaje de la excepción.
- **`@ExceptionHandler(SolicitudInvalidaException.class)`**: Este método maneja `SolicitudInvalidaException`, devolviendo un estado `400 Bad Request`.
- **Manejo genérico de excepciones**: El método `manejarExcepcionGenerica` captura cualquier excepción que no haya sido manejada, devolviendo un estado `500 Internal Server Error`.

#### ¿Cómo Se Aplica `@ControllerAdvice`?

Con `@ControllerAdvice`, no es necesario añadir `@ExceptionHandler` en cada controlador. Todos los controladores de la aplicación heredan el manejo de excepciones definido en `GlobalExceptionHandler`, lo que mejora la organización y evita duplicaciones de código.

---

### Beneficios de Usar `@ControllerAdvice`

1. **Centralización del Manejo de Errores**: Todo el manejo de errores está en una clase, lo que hace que el código sea más fácil de leer y mantener.
2. **Consistencia en las Respuestas**: Puedes definir respuestas de error estándar, asegurando que los errores se comuniquen de la misma forma en todos los endpoints.
3. **Reducción de Código Duplicado**: Evita la necesidad de manejar las excepciones en cada controlador individualmente.
4. **Flexibilidad y Escalabilidad**: Permite agregar nuevos tipos de excepciones personalizadas sin modificar los controladores o servicios.

---

### Ejemplo Completo de Manejo de Excepciones en Servicios y `@ControllerAdvice`

Supongamos que en `LibroService` lanzamos `RecursoNoEncontradoException` o `SolicitudInvalidaException` según corresponda. En `GlobalExceptionHandler`, definimos cómo se manejan y responden estas excepciones, asegurando que se devuelvan códigos HTTP claros y mensajes consistentes.

Cuando el controlador recibe una excepción desde el servicio, `@ControllerAdvice` la intercepta y responde de acuerdo a las reglas definidas, sin necesidad de manejar la excepción en el controlador directamente.

Esto permite un manejo de errores robusto y una respuesta clara y uniforme para el cliente final. Así, `@ControllerAdvice` y excepciones personalizadas en los servicios mejoran la seguridad y claridad de la API, proporcionando una estructura más profesional y fácil de mantener.

---
¡Perfecto! Vamos a implementar el manejo de errores de validación en el proyecto para mejorar la respuesta ante datos inválidos. Seguiremos estos pasos:

1. **Definir validaciones en las entidades**: Añadir anotaciones de validación en las entidades `Usuario`, `Libro`, etc.
2. **Aplicar validación en los controladores**: Asegurarse de que cada método que recibe datos del cliente aplique `@Valid`.
3. **Implementar un manejador global de excepciones**: Crear un controlador global con `@ControllerAdvice` para capturar y manejar los errores de validación.

---

### Paso 1: Añadir Validaciones en las Entidades

Empezaremos añadiendo validaciones a los atributos clave de las entidades. Esto asegura que cada campo cumpla con las restricciones deseadas antes de procesarse en la aplicación.

#### Ejemplo de Validación en la Entidad `Usuario`

```java
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Email;

public class Usuario {

    @NotNull(message = "El nombre no puede ser nulo")
    @Size(min = 3, max = 50, message = "El nombre debe tener entre 3 y 50 caracteres")
    private String nombre;

    @NotNull(message = "El email no puede ser nulo")
    @Email(message = "El formato del email es inválido")
    private String email;

    @NotNull(message = "La contraseña no puede ser nula")
    @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
    private String password;

    // Getters y Setters
}
```

### Paso 2: Validación en el Controlador

En los métodos del controlador que reciben datos de usuario, como en el caso de crear o actualizar un `Usuario`, añadimos `@Valid` para activar la validación. Si los datos no cumplen con las restricciones, se lanzará automáticamente una excepción de validación (`MethodArgumentNotValidException`).

#### Ejemplo de `UsuarioController` con Validación

```java
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @PostMapping
    public ResponseEntity<Usuario> crearUsuario(@RequestBody @Valid Usuario usuario) {
        Usuario nuevoUsuario = usuarioService.registrarUsuario(usuario);
        return ResponseEntity.ok(nuevoUsuario);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Usuario> actualizarUsuario(@PathVariable Long id, @RequestBody @Valid Usuario usuarioActualizado) {
        Usuario usuario = usuarioService.actualizarUsuario(id, usuarioActualizado);
        return ResponseEntity.ok(usuario);
    }
}
```

Aquí, `@Valid` asegura que los datos de `usuario` o `usuarioActualizado` cumplan con las restricciones antes de que el servicio procese la solicitud.

### Paso 3: Crear un Manejador Global para Errores de Validación con `@ControllerAdvice`

Con `@ControllerAdvice`, podemos capturar y manejar los errores de validación de forma centralizada, proporcionando respuestas personalizadas y detalladas.

#### `GlobalExceptionHandler` para Capturar Errores de Validación

Aquí capturamos `MethodArgumentNotValidException` para manejar errores de validación y devolver una respuesta estructurada:

```java
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> manejarErroresDeValidacion(MethodArgumentNotValidException ex) {
        Map<String, String> errores = new HashMap<>();

        // Extraer errores de validación específicos para cada campo
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String nombreCampo = ((FieldError) error).getField();
            String mensajeError = error.getDefaultMessage();
            errores.put(nombreCampo, mensajeError);
        });

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errores);
    }

    // Otros manejadores de excepciones (pueden añadirse aquí)
}
```

#### ¿Qué hace este `GlobalExceptionHandler`?

- **Captura `MethodArgumentNotValidException`**: Esta excepción se lanza cuando una validación de un objeto con `@Valid` falla.
- **Extrae errores de validación detallados**: Cada error de campo se convierte en una entrada de mapa con el nombre del campo como clave y el mensaje de error como valor.
- **Devuelve una respuesta estructurada**: La respuesta es un JSON con todos los errores de validación, permitiendo al cliente ver claramente cuáles campos son incorrectos y por qué.

### Ejemplo de Respuesta JSON de Errores de Validación

Si enviamos datos inválidos al endpoint de creación de `Usuario`, la respuesta de error podría verse así:

```json
{
    "nombre": "El nombre debe tener entre 3 y 50 caracteres",
    "email": "El formato del email es inválido",
    "password": "La contraseña debe tener al menos 8 caracteres"
}
```

Este formato es claro y ayuda al cliente a identificar exactamente qué necesita corregir en los datos enviados.

---

### Resumen

1. **Añadir Validaciones en las Entidades**: Usa anotaciones de validación como `@NotNull`, `@Size`, y `@Email` en las entidades para asegurar la validez de los datos.
2. **Aplicar Validación en el Controlador**: Usa `@Valid` en los parámetros de los métodos del controlador para activar la validación.
3. **Crear un Manejador Global para Errores de Validación**: Usa `@ControllerAdvice` para capturar `MethodArgumentNotValidException` y devolver una respuesta estructurada de errores.

Con estos pasos, hemos implementado un manejo de errores de validación completo en el proyecto, proporcionando respuestas claras y detalladas para los errores de entrada, lo que facilita la corrección y mejora la experiencia del usuario.
