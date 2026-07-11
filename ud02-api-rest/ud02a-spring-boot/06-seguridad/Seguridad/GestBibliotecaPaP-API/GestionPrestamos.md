Partimos de que hemos refactorizado los controladores y hemos sacado de ellos la lógica y la hemos puesto en los servicios.

Para gestionar los **préstamos vencidos** y añadir **validación** en la creación de préstamos, podemos hacer lo siguiente:

1. **Añadir un chequeo para préstamos vencidos**: En el servicio `PrestamoService`, agregamos una lógica para calcular si un préstamo está vencido o no en base a la fecha actual y la fecha de devolución.
2. **Añadir validación para la creación de préstamos**: Verificar las reglas adicionales antes de permitir la creación, como si el usuario tiene préstamos vencidos y si el libro ya está prestado.

A continuación, te muestro cómo implementar estas funcionalidades.

### Paso 1: Añadir un Campo de Duración de Préstamo

Podemos añadir un campo de duración en días, o en el servicio mismo establecer el periodo en el que el préstamo se considera válido.

#### Ejemplo de la Clase `Prestamo`

Si deseas mantener la flexibilidad en la duración del préstamo, puedes añadir un campo `duracionDias` en la entidad `Prestamo`.

```java
// Ubicación: daw2a.gestionbiblioteca.entities.Prestamo.java

@Entity
public class Prestamo {
    // ... campos existentes ...

    @NotNull
    private LocalDate fechaPrestamo;

    private LocalDate fechaDevolucion;

    // Duración en días del préstamo
    private int duracionDias = 14; // Ejemplo: préstamo por 14 días

    // Método para verificar si el préstamo está vencido
    public boolean estaVencido() {
        return fechaDevolucion == null && fechaPrestamo.plusDays(duracionDias).isBefore(LocalDate.now());
    }

    // ... resto de la clase ...
}
```

### Paso 2: Añadir Validación en el Servicio `PrestamoService`

En el servicio `PrestamoService`, añadiremos:
- Validación para verificar si el libro está disponible.
- Validación para comprobar si el usuario tiene algún préstamo vencido.

```java
// Ubicación: daw2a.gestionbiblioteca.services.PrestamoService.java

@Service
public class PrestamoService {

    private final PrestamoRepository prestamoRepository;
    private final LibroRepository libroRepository;

    @Autowired
    public PrestamoService(PrestamoRepository prestamoRepository, LibroRepository libroRepository) {
        this.prestamoRepository = prestamoRepository;
        this.libroRepository = libroRepository;
    }

    // Crear un nuevo préstamo con validación
    @Transactional
    public Prestamo crearPrestamo(Prestamo prestamo) {
        Libro libro = prestamo.getLibro();
        Long usuarioId = prestamo.getUsuario().getId();

        // Verificar si el libro ya está prestado
        if ("prestado".equalsIgnoreCase(libro.getEstado())) {
            throw new IllegalArgumentException("El libro ya está prestado.");
        }

        // Verificar si el usuario tiene préstamos vencidos
        if (tienePrestamosVencidos(usuarioId)) {
            throw new IllegalArgumentException("El usuario tiene préstamos vencidos. No se puede realizar un nuevo préstamo.");
        }

        // Cambiar el estado del libro a "prestado"
        libro.setEstado("prestado");
        libroRepository.save(libro);

        prestamo.setFechaPrestamo(LocalDate.now());
        return prestamoRepository.save(prestamo);
    }

    // Método para verificar si el usuario tiene préstamos vencidos
    public boolean tienePrestamosVencidos(Long usuarioId) {
        List<Prestamo> prestamos = prestamoRepository.findByUsuarioId(usuarioId);
        return prestamos.stream().anyMatch(Prestamo::estaVencido);
    }

    // Método para registrar la devolución de un préstamo
    @Transactional
    public Prestamo devolverPrestamo(Long id) {
        return prestamoRepository.findById(id)
                .map(prestamo -> {
                    Libro libro = prestamo.getLibro();

                    // Cambiar el estado del libro a "disponible"
                    libro.setEstado("disponible");
                    libroRepository.save(libro);

                    // Actualizar la fecha de devolución
                    prestamo.setFechaDevolucion(LocalDate.now());
                    return prestamoRepository.save(prestamo);
                })
                .orElseThrow(() -> new RecursoNoEncontradoException("Préstamo no encontrado con id " + id));
    }

    // Otros métodos del servicio (listar, obtener por id, etc.)
}
```

### Explicación de las Validaciones

1. **Verificar si el Libro Está Prestado**:
   - Antes de crear un préstamo, se comprueba si el estado del libro es "prestado". Si lo está, se lanza una excepción indicando que el libro no está disponible.

2. **Verificar si el Usuario Tiene Préstamos Vencidos**:
   - Antes de crear un nuevo préstamo, el servicio llama a `tienePrestamosVencidos` para revisar si el usuario tiene algún préstamo vencido. Si es así, se lanza una excepción que prohíbe el nuevo préstamo.

3. **`estaVencido()`**:
   - En la clase `Prestamo`, el método `estaVencido()` verifica si el préstamo está vencido comparando la fecha de préstamo con la duración permitida.

### Paso 3: Manejo de Errores en el Controlador

En el controlador `PrestamoController`, puedes manejar las excepciones específicas para préstamos vencidos y libros no disponibles.

```java
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ControllerAdvice;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }
}
```

### Resumen

Con estas modificaciones:

1. **Validación Completa en `crearPrestamo`**:
   - Se verifica que el libro esté disponible y que el usuario no tenga préstamos vencidos.

2. **Método `estaVencido`**:
   - Permite verificar si un préstamo está vencido y aplicar restricciones para nuevos préstamos.

3. **Manejo de Errores**:
   - Cualquier problema de validación lanza una excepción `IllegalArgumentException`, que es capturada por `GlobalExceptionHandler` para proporcionar mensajes de error claros y específicos.

Esta configuración permite gestionar de manera eficaz tanto los préstamos válidos como los vencidos, mejorando la integridad de los datos en el sistema de préstamos.


Para manejar **renovaciones de préstamos** y comprender cómo funciona `@Transactional` en el contexto de estas operaciones, te detallo los pasos y conceptos clave:

### 1. Manejo de Renovaciones de Préstamos

Para implementar una **renovación de préstamo**, debemos extender la fecha de vencimiento del préstamo si se cumplen ciertas condiciones, como que el libro no esté reservado por otro usuario o que el usuario no tenga otros préstamos vencidos.

#### A. Crear un Método de Renovación en `PrestamoService`

Vamos a añadir un método `renovarPrestamo` en `PrestamoService` que:

1. **Verifique si el préstamo es renovable**.
2. **Extienda la fecha de vencimiento** (por ejemplo, añadiendo más días al `fechaPrestamo` o estableciendo una nueva fecha).
3. **Registre la renovación en la base de datos**.

#### Ejemplo de `PrestamoService` con Renovación de Préstamo

```java
import daw2a.gestionbiblioteca.exceptions.PrestamoNoRenovableException;

@Service
public class PrestamoService {

    private final PrestamoRepository prestamoRepository;

    @Autowired
    public PrestamoService(PrestamoRepository prestamoRepository) {
        this.prestamoRepository = prestamoRepository;
    }

    // Renovar un préstamo existente
    @Transactional
    public Prestamo renovarPrestamo(Long prestamoId, int diasExtension) {
        return prestamoRepository.findById(prestamoId)
                .map(prestamo -> {
                    // Verificar si el préstamo es renovable (por ejemplo, si aún no está vencido)
                    if (prestamo.estaVencido()) {
                        throw new PrestamoNoRenovableException("No se puede renovar un préstamo vencido.");
                    }

                    // Extender la fecha de devolución
                    prestamo.setFechaPrestamo(prestamo.getFechaPrestamo().plusDays(diasExtension));
                    return prestamoRepository.save(prestamo);
                })
                .orElseThrow(() -> new RecursoNoEncontradoException("Préstamo no encontrado con id " + prestamoId));
    }
}
```

#### B. Explicación de `renovarPrestamo`

1. **Comprobar si el préstamo es renovable**:
   - Llamamos a `estaVencido()` en el préstamo para verificar si se puede renovar.
   - Si el préstamo está vencido, lanzamos una `PrestamoNoRenovableException` para indicar que no puede ser renovado.

2. **Extender el plazo del préstamo**:
   - Extendemos la fecha de préstamo usando `plusDays(diasExtension)`, lo cual permite añadir un número específico de días.
   - Finalmente, guardamos el préstamo actualizado.

3. **Uso de `@Transactional`**:
   - `@Transactional` asegura que, si ocurre un error durante la actualización, toda la transacción se revierte, dejando el préstamo sin modificaciones parciales.

#### C. Controlador para Renovación de Préstamos

Ahora, añadimos un endpoint en `PrestamoController` para gestionar la renovación:

```java
@RestController
@RequestMapping("/prestamos")
public class PrestamoController {

    private final PrestamoService prestamoService;

    @Autowired
    public PrestamoController(PrestamoService prestamoService) {
        this.prestamoService = prestamoService;
    }

    // Endpoint para renovar un préstamo
    @PutMapping("/renovar/{id}")
    public ResponseEntity<Prestamo> renovarPrestamo(@PathVariable Long id, @RequestParam int diasExtension) {
        Prestamo prestamoRenovado = prestamoService.renovarPrestamo(id, diasExtension);
        return ResponseEntity.ok(prestamoRenovado);
    }
}
```

- **`@PutMapping("/renovar/{id}")`**: Define el endpoint para renovar el préstamo.
- **`diasExtension`**: Parámetro que especifica cuántos días se desea extender el préstamo.
- **Respuesta**: Si la renovación es exitosa, devuelve el préstamo renovado. Si falla, el controlador maneja las excepciones y envía el mensaje de error correspondiente.

### 2. ¿Qué es `@Transactional` y Por Qué es Importante?

`@Transactional` es una anotación de Spring utilizada para **gestionar transacciones** en métodos específicos de servicios o repositorios. Garantiza que todas las operaciones en un método se ejecuten como una única transacción. Si una parte falla, **se revierte toda la transacción** para asegurar la integridad de los datos.

#### A. Beneficios de `@Transactional`

- **Atomicidad**: Todas las operaciones dentro de una transacción se completan o ninguna se aplica, asegurando que no haya estados parciales.
- **Aislamiento**: Permite que las transacciones sean independientes entre sí, previniendo que otros procesos vean datos parciales o en estado de modificación.
- **Consistencia**: `@Transactional` ayuda a mantener la base de datos en un estado consistente en caso de error.

#### B. Ejemplo de Uso de `@Transactional` en un Método de Servicio

El siguiente ejemplo muestra cómo `@Transactional` se asegura de que, si ocurre una excepción durante una serie de operaciones, todos los cambios se revierten:

```java
@Transactional
public Prestamo crearPrestamo(Prestamo prestamo) {
    Libro libro = prestamo.getLibro();

    // Verificar disponibilidad del libro
    if ("prestado".equalsIgnoreCase(libro.getEstado())) {
        throw new LibroNoDisponibleException("El libro ya está prestado.");
    }

    // Cambiar estado del libro a "prestado"
    libro.setEstado("prestado");
    libroRepository.save(libro);

    // Guardar el préstamo
    prestamo.setFechaPrestamo(LocalDate.now());
    return prestamoRepository.save(prestamo);
}
```

Si la operación de guardar el libro (`libroRepository.save(libro)`) falla por alguna razón (por ejemplo, problemas de conexión), **ningún cambio se aplicará** y se revertirá cualquier cambio intermedio, manteniendo el sistema en un estado coherente.

#### C. Opciones de Propagación y Aislamiento en `@Transactional`

- **Propagación** (`propagation`): Controla cómo se comporta la transacción actual al llamar a otros métodos transaccionales.
  - **REQUIRED**: Usa una transacción existente o crea una nueva si no hay ninguna.
  - **REQUIRES_NEW**: Siempre inicia una nueva transacción, suspendiendo la actual.
  - **MANDATORY**: Requiere una transacción existente; lanza una excepción si no la hay.

- **Aislamiento** (`isolation`): Controla el nivel de visibilidad de los cambios realizados en la base de datos antes de que la transacción se complete.
  - **READ_COMMITTED**: Solo permite leer datos ya confirmados.
  - **REPEATABLE_READ**: Asegura que los datos leídos al inicio de la transacción no cambien durante la misma.
  - **SERIALIZABLE**: Máximo nivel de aislamiento, asegurando que las transacciones se ejecuten de manera completamente independiente.

### Resumen

1. **Renovaciones de Préstamos**:
   - En `PrestamoService`, añadimos lógica para extender la fecha de préstamo, y lanzamos `PrestamoNoRenovableException` si el préstamo no puede renovarse.

2. **Controlador de Renovación**:
   - Añadimos un endpoint `/renovar/{id}` en `PrestamoController` que extiende el préstamo si es válido.

3. **`@Transactional`**:
   - Asegura que todas las operaciones en un método se completan juntas o se revierten en caso de error, manteniendo la integridad de los datos.

Esta implementación garantiza que el manejo de renovaciones y transacciones en la aplicación sea seguro y esté protegido contra inconsistencias en la base de datos.
