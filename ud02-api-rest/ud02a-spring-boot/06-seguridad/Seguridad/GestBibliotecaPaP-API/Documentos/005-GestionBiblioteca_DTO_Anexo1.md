### Estado del libro
Hay varios lugares donde el estado del libro debe cambiar dependiendo de la lógica de negocio. Aquí tienes una lista de escenarios comunes en el contexto de tu proyecto y dónde deberías manejar el cambio de estado del libro:

---

### **1. Al Crear un Préstamo**
Cuando se crea un préstamo, el estado del libro debe cambiar a `"prestado"`. Esto debe manejarse en el **`PrestamoService`**.

#### **Ejemplo:**
```java
@Transactional
public PrestamoDTO crearPrestamo(CrearPrestamoDTO crearPrestamoDTO) {
    Libro libro = libroRepository.findById(crearPrestamoDTO.getLibroId())
            .orElseThrow(() -> new RecursoNoEncontradoException("Libro no encontrado con id " + crearPrestamoDTO.getLibroId()));

    if ("prestado".equalsIgnoreCase(libro.getEstado())) {
        throw new LibroNoDisponibleException("El libro ya está prestado.");
    }

    // Cambiar el estado del libro a "prestado"
    libro.setEstado("prestado");
    libroRepository.save(libro);

    // Crear el préstamo
    Prestamo prestamo = convertirDesdeDTO(crearPrestamoDTO, libro);
    prestamo.setFechaPrestamo(LocalDate.now());

    Prestamo prestamoGuardado = prestamoRepository.save(prestamo);
    return convertirADTO(prestamoGuardado);
}
```

---

### **2. Al Registrar la Devolución de un Préstamo**
Cuando se devuelve un préstamo, el estado del libro debe cambiar de `"prestado"` a `"disponible"`. Esto también se maneja en el **`PrestamoService`**.

#### **Ejemplo:**
```java
@Transactional
public PrestamoDTO devolverPrestamo(Long id) {
    Prestamo prestamo = prestamoRepository.findById(id)
            .orElseThrow(() -> new RecursoNoEncontradoException("Préstamo no encontrado con id " + id));

    // Cambiar el estado del libro a "disponible"
    Libro libro = prestamo.getLibro();
    libro.setEstado("disponible");
    libroRepository.save(libro);

    // Registrar la devolución del préstamo
    prestamo.setFechaDevolucion(LocalDate.now());
    Prestamo prestamoGuardado = prestamoRepository.save(prestamo);

    return convertirADTO(prestamoGuardado);
}
```

---

### **3. Al Eliminar un Préstamo**
Si decides permitir la eliminación de un préstamo, también deberías actualizar el estado del libro asociado a `"disponible"`. Sin embargo, esta acción debe ser cuidadosamente considerada, ya que eliminar un préstamo puede tener implicaciones en la trazabilidad.

#### **Ejemplo:**
```java
@Transactional
public void eliminarPrestamo(Long id) {
    Prestamo prestamo = prestamoRepository.findById(id)
            .orElseThrow(() -> new RecursoNoEncontradoException("Préstamo no encontrado con id " + id));

    // Cambiar el estado del libro a "disponible"
    Libro libro = prestamo.getLibro();
    libro.setEstado("disponible");
    libroRepository.save(libro);

    // Eliminar el préstamo
    prestamoRepository.delete(prestamo);
}
```

---

### **4. En Métodos Administrativos (Opcional)**
Si tienes funcionalidades para gestionar libros directamente (por ejemplo, bloquear un libro, marcarlo como perdido, etc.), podrías cambiar el estado en el **`LibroService`** según la lógica requerida.

#### **Ejemplo:**
```java
public LibroDTO marcarComoPerdido(Long libroId) {
    Libro libro = libroRepository.findById(libroId)
            .orElseThrow(() -> new RecursoNoEncontradoException("Libro no encontrado con id " + libroId));

    libro.setEstado("perdido");
    libroRepository.save(libro);

    return convertirADTO(libro);
}
```

---

### **5. Al Modificar un Libro**
En el **`LibroService`**, si permites que un libro sea modificado directamente, asegúrate de no sobrescribir el estado accidentalmente. Puedes proteger el estado actual o manejarlo explícitamente si la lógica requiere cambiarlo.

#### **Ejemplo:**
```java
public LibroDTO actualizarLibro(Long id, ActualizarLibroDTO libroActualizadoDTO) {
    Libro libro = libroRepository.findById(id)
            .orElseThrow(() -> new RecursoNoEncontradoException("Libro no encontrado con id " + id));

    Optional.ofNullable(libroActualizadoDTO.getTitulo()).ifPresent(libro::setTitulo);
    Optional.ofNullable(libroActualizadoDTO.getGenero()).ifPresent(libro::setGenero);
    Optional.ofNullable(libroActualizadoDTO.getAnyoPublicacion()).ifPresent(libro::setAnyoPublicacion);

    // Si se proporciona un estado explícito en el DTO, actualizarlo
    Optional.ofNullable(libroActualizadoDTO.getEstado()).ifPresent(libro::setEstado);

    Libro libroActualizado = libroRepository.save(libro);
    return convertirADTO(libroActualizado);
}
```

---

### **Resumen de Cambios del Estado del Libro**
| **Escenario**                  | **Estado Anterior** | **Estado Nuevo** | **Responsable**      |
|--------------------------------|---------------------|------------------|----------------------|
| Crear Libro                    | No existe          | "disponible"     | `LibroService`       |
| Crear Préstamo                 | "disponible"       | "prestado"       | `PrestamoService`    |
| Registrar Devolución           | "prestado"         | "disponible"     | `PrestamoService`    |
| Eliminar Préstamo (opcional)   | "prestado"         | "disponible"     | `PrestamoService`    |
| Marcar como Perdido (opcional) | Cualquiera         | "perdido"        | `LibroService`       |

---

### **Pruebas**
Para cada caso donde el estado cambia, agrega pruebas unitarias y de integración para verificar que:
1. El estado cambia correctamente según las condiciones.
2. No hay actualizaciones accidentales en el estado cuando no es necesario.

Con este enfoque, puedes garantizar que el estado del libro se maneje correctamente en cada escenario del flujo del sistema. 😊
