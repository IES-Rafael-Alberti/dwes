##DTO


### Introducción a los DTO

Un DTO es un objeto que se utiliza para transferir datos entre diferentes capas de una aplicación. En el contexto de una aplicación Spring Boot, los DTOs se utilizan principalmente para transferir datos entre la capa de presentación (controladores) y la capa de servicio.

### Utilidad de los DTO

1. **Encapsulamiento de Datos**: Los DTOs permiten encapsular los datos que se transfieren entre las capas de la aplicación. Esto ayuda a mantener una separación clara entre las diferentes capas y a evitar la exposición de detalles internos de las entidades de la base de datos.

2. **Reducción de Datos Transferidos**: Al utilizar DTOs, puedes controlar exactamente qué datos se transfieren entre las capas. Esto es especialmente útil cuando solo necesitas una parte de los datos de una entidad, lo que reduce la cantidad de datos transferidos y mejora el rendimiento.

3. **Seguridad**: Los DTOs ayudan a proteger la aplicación de ataques como la inyección de datos. Al definir explícitamente qué datos se pueden transferir, se reduce el riesgo de que datos no deseados o maliciosos se filtren a través de la aplicación.

4. **Validación de Datos**: Los DTOs permiten realizar validaciones específicas en los datos que se transfieren. Puedes utilizar anotaciones de validación de Spring para asegurarte de que los datos cumplen con ciertos criterios antes de procesarlos.

5. **Flexibilidad y Mantenimiento**: Al utilizar DTOs, puedes cambiar la estructura de las entidades de la base de datos sin afectar a las capas superiores de la aplicación. Esto facilita el mantenimiento y la evolución de la aplicación a lo largo del tiempo.

### Ejemplo de Uso de DTO en Spring Boot

Supongamos que tienes una entidad `Usuario` y quieres transferir solo algunos de sus datos a través de un controlador. Aquí tienes un ejemplo de cómo podrías hacerlo:

#### Entidad `Usuario`
```java
@Entity
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nombre;
    private String email;
    private String password;
    private String rol;

    // Getters y Setters
}
```

#### DTO `UsuarioDTO`
```java
public class UsuarioDTO {
    private Long id;
    private String nombre;
    private String email;

    // Getters y Setters
}
```

#### Controlador que utiliza el DTO
```java
@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioDTO> obtenerUsuario(@PathVariable Long id) {
        Usuario usuario = usuarioService.obtenerUsuarioPorId(id);
        UsuarioDTO usuarioDTO = new UsuarioDTO();
        usuarioDTO.setId(usuario.getId());
        usuarioDTO.setNombre(usuario.getNombre());
        usuarioDTO.setEmail(usuario.getEmail());
        return ResponseEntity.ok(usuarioDTO);
    }
}
```

En este ejemplo, el controlador utiliza el `UsuarioDTO` para transferir solo los datos necesarios (`id`, `nombre`, `email`) de la entidad `Usuario` al cliente. Esto mejora la seguridad y la eficiencia de la aplicación.

Los **DTOs** (Data Transfer Objects) se introducen generalmente en el **controlador** para transferir datos hacia y desde la capa de servicios, y de esta forma controlar el formato de los datos expuestos en los endpoints. La idea es que los DTOs sirvan como una capa de interfaz entre la API y la lógica de negocio (que reside en los servicios). Aquí tienes cómo funciona esta estructura en cada capa y el motivo:

### 1. **Controladores**
   - **Uso principal**: Los controladores son el punto de entrada y salida de datos de la API. Al recibir una solicitud, el controlador mapea los datos entrantes a DTOs y los envía al servicio, manteniendo la entidad separada del formato de solicitud.
   - **Motivo**: Evitar exponer directamente las entidades de la base de datos al usuario. Esto permite ajustar la visibilidad y formato de campos específicos según sea necesario, como evitar incluir contraseñas o campos internos en las respuestas.

### 2. **Servicios**
   - **Uso secundario**: La capa de servicio utiliza los DTOs para recibir datos de entrada procesados desde el controlador y devolver los resultados que luego serán mapeados a DTOs de respuesta.
   - **Motivo**: Los servicios pueden procesar la lógica de negocio utilizando entidades internas. Si es necesario, el servicio puede transformar una entidad a un DTO de respuesta antes de enviarlo de vuelta al controlador. En la mayoría de los casos, el servicio devuelve entidades, y el controlador las transforma a DTOs.

### Flujo General de Datos con DTOs

1. **Solicitud Entrante**:
   - El controlador recibe la solicitud con datos en formato JSON y mapea los datos a un **DTO de entrada**.
   - Envía el DTO a la capa de servicio para procesar la lógica de negocio.

2. **Lógica de Negocio en el Servicio**:
   - El servicio realiza las operaciones necesarias, ya sea creando, leyendo, actualizando o eliminando entidades.
   - Cuando devuelve datos, lo hace en forma de entidades o de **DTOs de salida** si el servicio los convierte.

3. **Respuesta**:
   - El controlador toma la respuesta del servicio y, si es necesario, transforma las entidades en DTOs de salida.
   - Devuelve el DTO al cliente en el formato JSON deseado.

### Ejemplo en Código

1. **DTO de Entrada y Salida**:

   ```java
   public class UsuarioDTO {
       private Long id;
       private String nombre;
       private String email;
       // Evitamos exponer el campo de contraseña en el DTO de salida
   }

   public class CrearUsuarioDTO {
       private String nombre;
       private String email;
       private String password; // Solo en DTO de entrada
   }
   ```

2. **Uso en Controlador**:

   ```java
   @PostMapping("/usuarios")
   public ResponseEntity<UsuarioDTO> registrarUsuario(@RequestBody @Valid CrearUsuarioDTO crearUsuarioDTO) {
       UsuarioDTO usuarioDTO = usuarioService.crearUsuario(crearUsuarioDTO);
       return ResponseEntity.status(HttpStatus.CREATED).body(usuarioDTO);
   }
   ```

3. **En el Servicio**:

   ```java
   public UsuarioDTO crearUsuario(CrearUsuarioDTO crearUsuarioDTO) {
       Usuario usuario = new Usuario();
       usuario.setNombre(crearUsuarioDTO.getNombre());
       usuario.setEmail(crearUsuarioDTO.getEmail());
       usuario.setPassword(passwordEncoder.encode(crearUsuarioDTO.getPassword()));

       Usuario savedUsuario = usuarioRepository.save(usuario);

       return new UsuarioDTO(savedUsuario.getId(), savedUsuario.getNombre(), savedUsuario.getEmail());
   }
   ```

Este diseño permite que las entidades de base de datos permanezcan encapsuladas en la lógica de negocio, y los controladores solo manejen DTOs, evitando la exposición directa de las entidades y manteniendo los datos consistentes y seguros.


## DTO en nuestro proyecto
### DTOs para libro
1. **CrearLibroDTO**: Para capturar los datos necesarios al crear un libro.
2. **LibroDetalleDTO**: Para mostrar detalles completos de un libro específico.
3. **LibroListadoDTO**: Para listar libros con campos más reducidos, como en un listado general.
4. **ModificarLibroDTO**: Para capturar los datos al modificar un libro.

A continuación, te muestro cómo crear estos DTOs y adaptar el controlador para usarlos.

---

### Paso 1: Crear los DTOs en el Paquete `dto`

#### 1. `CrearLibroDTO.java`

Este DTO contendrá los datos requeridos para crear un libro. Normalmente, se incluyen los campos necesarios sin el `id`, ya que el `id` se generará al guardar la entidad en la base de datos.

```java
package com.miapp.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class CrearLibroDTO {

    @NotNull
    @Size(min = 1, max = 100)
    private String titulo;

    @NotNull
    private String genero;

    private Long autorId;

    private Integer añoPublicacion;

    // Getters y Setters
}
```

#### 2. `LibroDetalleDTO.java`

Este DTO será utilizado para devolver detalles completos de un libro.

```java
package com.miapp.dto;

public class LibroDetalleDTO {

    private Long id;
    private String titulo;
    private String genero;
    private String estado;
    private Integer añoPublicacion;
    private String nombreAutor;

    // Getters y Setters
}
```

#### 3. `LibroListadoDTO.java`

Este DTO se usa para listar varios libros en una vista de lista, mostrando solo campos clave.

```java
package com.miapp.dto;

public class LibroListadoDTO {

    private Long id;
    private String titulo;
    private String genero;

    // Getters y Setters
}
```

#### 4. `ModificarLibroDTO.java`

Este DTO se utiliza para actualizar datos de un libro.

```java
package com.miapp.dto;

import jakarta.validation.constraints.Size;

public class ModificarLibroDTO {

    @Size(min = 1, max = 100)
    private String titulo;

    private String genero;

    private Integer añoPublicacion;

    private Long autorId;

    // Getters y Setters
}
```

---

### Paso 2: Adaptar el `LibroService` para Utilizar los DTOs

En el servicio de `LibroService`, transforma la lógica de negocio para que utilice y devuelva DTOs en lugar de la entidad `Libro` directamente.

#### LibroService.java

```java
package com.miapp.services;

import com.miapp.dto.CrearLibroDTO;
import com.miapp.dto.LibroDetalleDTO;
import com.miapp.dto.LibroListadoDTO;
import com.miapp.dto.ModificarLibroDTO;
import com.miapp.entities.Libro;
import com.miapp.exceptions.RecursoNoEncontradoException;
import com.miapp.repositories.AutorRepository;
import com.miapp.repositories.LibroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
public class LibroService {

    private final LibroRepository libroRepository;
    private final AutorRepository autorRepository;

    @Autowired
    public LibroService(LibroRepository libroRepository, AutorRepository autorRepository) {
        this.libroRepository = libroRepository;
        this.autorRepository = autorRepository;
    }

    public LibroDetalleDTO obtenerLibro(Long id) {
        Libro libro = libroRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Libro no encontrado con id: " + id));
        return convertirALibroDetalleDTO(libro);
    }

    public Page<LibroListadoDTO> listarLibros(Pageable pageable) {
        return libroRepository.findAll(pageable)
                .map(this::convertirALibroListadoDTO);
    }

    public LibroDetalleDTO crearLibro(CrearLibroDTO crearLibroDTO) {
        Libro libro = new Libro();
        libro.setTitulo(crearLibroDTO.getTitulo());
        libro.setGenero(crearLibroDTO.getGenero());
        libro.setAñoPublicacion(crearLibroDTO.getAñoPublicacion());
        libro.setAutor(autorRepository.findById(crearLibroDTO.getAutorId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Autor no encontrado con id: " + crearLibroDTO.getAutorId())));

        Libro nuevoLibro = libroRepository.save(libro);
        return convertirALibroDetalleDTO(nuevoLibro);
    }

    public LibroDetalleDTO actualizarLibro(Long id, ModificarLibroDTO modificarLibroDTO) {
        Libro libro = libroRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Libro no encontrado con id: " + id));
        if (modificarLibroDTO.getTitulo() != null) libro.setTitulo(modificarLibroDTO.getTitulo());
        if (modificarLibroDTO.getGenero() != null) libro.setGenero(modificarLibroDTO.getGenero());
        if (modificarLibroDTO.getAñoPublicacion() != null) libro.setAñoPublicacion(modificarLibroDTO.getAñoPublicacion());
        if (modificarLibroDTO.getAutorId() != null) {
            libro.setAutor(autorRepository.findById(modificarLibroDTO.getAutorId())
                    .orElseThrow(() -> new RecursoNoEncontradoException("Autor no encontrado con id: " + modificarLibroDTO.getAutorId())));
        }
        return convertirALibroDetalleDTO(libroRepository.save(libro));
    }

    private LibroDetalleDTO convertirALibroDetalleDTO(Libro libro) {
        LibroDetalleDTO dto = new LibroDetalleDTO();
        dto.setId(libro.getId());
        dto.setTitulo(libro.getTitulo());
        dto.setGenero(libro.getGenero());
        dto.setEstado(libro.getEstado());
        dto.setAñoPublicacion(libro.getAñoPublicacion());
        dto.setNombreAutor(libro.getAutor().getNombre());
        return dto;
    }

    private LibroListadoDTO convertirALibroListadoDTO(Libro libro) {
        LibroListadoDTO dto = new LibroListadoDTO();
        dto.setId(libro.getId());
        dto.setTitulo(libro.getTitulo());
        dto.setGenero(libro.getGenero());
        return dto;
    }
}
```

---

### Paso 3: Adaptar el `LibroController` para Utilizar los DTOs

Finalmente, ajusta el controlador para que use los DTOs en lugar de la entidad `Libro` directamente.

#### LibroController.java

```java
package com.miapp.controllers;

import com.miapp.dto.CrearLibroDTO;
import com.miapp.dto.LibroDetalleDTO;
import com.miapp.dto.LibroListadoDTO;
import com.miapp.dto.ModificarLibroDTO;
import com.miapp.services.LibroService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/libros")
public class LibroController {

    private final LibroService libroService;

    @Autowired
    public LibroController(LibroService libroService) {
        this.libroService = libroService;
    }

    @GetMapping
    public ResponseEntity<Page<LibroListadoDTO>> listarLibros(Pageable pageable) {
        return ResponseEntity.ok(libroService.listarLibros(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<LibroDetalleDTO> obtenerLibro(@PathVariable Long id) {
        return ResponseEntity.ok(libroService.obtenerLibro(id));
    }

    @PostMapping
    public ResponseEntity<LibroDetalleDTO> crearLibro(@RequestBody @Valid CrearLibroDTO crearLibroDTO) {
        return ResponseEntity.status(201).body(libroService.crearLibro(crearLibroDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<LibroDetalleDTO> actualizarLibro(@PathVariable Long id, @RequestBody @Valid ModificarLibroDTO modificarLibroDTO) {
        return ResponseEntity.ok(libroService.actualizarLibro(id, modificarLibroDTO));
    }
}
```

---

### Resumen

1. **DTOs**: Se crean `CrearLibroDTO`, `LibroDetalleDTO`, `LibroListadoDTO` y `ModificarLibroDTO` para encapsular diferentes vistas y formatos de datos.
2. **Servicio (`LibroService`)**: Los métodos del servicio ahora utilizan y devuelven DTOs.
3. **Controlador (`LibroController`)**: El controlador recibe DTOs en las solicitudes y devuelve DTOs en las respuestas.

Esto asegura una separación clara entre las entidades y los datos expuestos en la API, protegiendo y estandarizando los datos que se transfieren.
¡Sí, es un buen momento para añadir los **DTOs** para la entidad **Usuario**! Esto nos ayudará a refactorizar el código actual y facilitar la gestión de datos expuestos a través de la API.

### **Paso 1: Crear DTOs para Usuario**

#### DTOs necesarios:
1. **CrearUsuarioDTO**:
   - Para la creación de un usuario, incluirá los campos necesarios.
2. **UsuarioDetalleDTO**:
   - Para obtener los detalles completos de un usuario específico.
3. **UsuarioListadoDTO**:
   - Para listar los usuarios de forma resumida.
4. **ModificarUsuarioDTO**:
   - Para actualizar datos de un usuario, permitiendo cambios parciales.

#### Código de los DTOs:

### DTOs de Usuario

#### CrearUsuarioDTO
```java
package daw2a.gestionbiblioteca.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class CrearUsuarioDTO {
    @NotBlank
    private String nombre;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String password;

    @NotBlank
    private String rol;

    // Getters y Setters
}
```

---

#### ModificarUsuarioDTO
```java
package daw2a.gestionbiblioteca.dto;

import jakarta.validation.constraints.Email;

public class ModificarUsuarioDTO {
    private String nombre;

    @Email
    private String email;

    private String password;

    private String rol;

    // Getters y Setters
}
```

---

#### UsuarioDetalleDTO
```java
package daw2a.gestionbiblioteca.dto;

public class UsuarioDetalleDTO {
    private Long id;
    private String nombre;
    private String email;
    private String rol;

    // Getters y Setters
}
```

---

#### UsuarioListadoDTO
```java
package daw2a.gestionbiblioteca.dto;

public class UsuarioListadoDTO {
    private Long id;
    private String nombre;
    private String email;

    // Getters y Setters
}
```

---

### UsuarioService Refactorizado

```java
package daw2a.gestionbiblioteca.services;

import daw2a.gestionbiblioteca.dto.CrearUsuarioDTO;
import daw2a.gestionbiblioteca.dto.ModificarUsuarioDTO;
import daw2a.gestionbiblioteca.dto.UsuarioDetalleDTO;
import daw2a.gestionbiblioteca.dto.UsuarioListadoDTO;
import daw2a.gestionbiblioteca.entities.Usuario;
import daw2a.gestionbiblioteca.exceptions.RecursoDuplicadoException;
import daw2a.gestionbiblioteca.exceptions.RecursoNoEncontradoException;
import daw2a.gestionbiblioteca.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService {
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Page<UsuarioListadoDTO> listarUsuarios(String nombre, Pageable pageable) {
        Page<Usuario> usuarios = (nombre != null)
                ? usuarioRepository.findByNombreContainingIgnoreCase(nombre, pageable)
                : usuarioRepository.findAll(pageable);

        return usuarios.map(this::convertirAUsuarioListadoDTO);
    }

    public UsuarioDetalleDTO obtenerUsuario(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado con id " + id));
        return convertirAUsuarioDetalleDTO(usuario);
    }

    public UsuarioDetalleDTO obtenerUsuarioPorEmail(String email) {
        Usuario usuario = usuarioRepository.findUsuarioByEmail(email)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado con email " + email));
        return convertirAUsuarioDetalleDTO(usuario);
    }

    public UsuarioDetalleDTO registrarUsuario(CrearUsuarioDTO crearUsuarioDTO) {
        if (usuarioRepository.findUsuarioByEmail(crearUsuarioDTO.getEmail()).isPresent()) {
            throw new RecursoDuplicadoException("El email " + crearUsuarioDTO.getEmail() + " ya está en uso.");
        }

        Usuario usuario = new Usuario();
        usuario.setNombre(crearUsuarioDTO.getNombre());
        usuario.setEmail(crearUsuarioDTO.getEmail());
        usuario.setRol(crearUsuarioDTO.getRol());
        usuario.setPassword(passwordEncoder.encode(crearUsuarioDTO.getPassword()));

        return convertirAUsuarioDetalleDTO(usuarioRepository.save(usuario));
    }

    public UsuarioDetalleDTO actualizarUsuario(Long id, ModificarUsuarioDTO modificarUsuarioDTO) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado con id " + id));

        if (modificarUsuarioDTO.getNombre() != null) usuario.setNombre(modificarUsuarioDTO.getNombre());
        if (modificarUsuarioDTO.getEmail() != null) usuario.setEmail(modificarUsuarioDTO.getEmail());
        if (modificarUsuarioDTO.getPassword() != null) usuario.setPassword(passwordEncoder.encode(modificarUsuarioDTO.getPassword()));
        if (modificarUsuarioDTO.getRol() != null) usuario.setRol(modificarUsuarioDTO.getRol());

        return convertirAUsuarioDetalleDTO(usuarioRepository.save(usuario));
    }

    public void eliminarUsuario(Long id) {
        if (!usuarioRepository.existsById(id)) {
            throw new RecursoNoEncontradoException("Usuario no encontrado con id " + id);
        }
        usuarioRepository.deleteById(id);
    }

    private UsuarioDetalleDTO convertirAUsuarioDetalleDTO(Usuario usuario) {
        UsuarioDetalleDTO dto = new UsuarioDetalleDTO();
        dto.setId(usuario.getId());
        dto.setNombre(usuario.getNombre());
        dto.setEmail(usuario.getEmail());
        dto.setRol(usuario.getRol());
        return dto;
    }

    private UsuarioListadoDTO convertirAUsuarioListadoDTO(Usuario usuario) {
        UsuarioListadoDTO dto = new UsuarioListadoDTO();
        dto.setId(usuario.getId());
        dto.setNombre(usuario.getNombre());
        dto.setEmail(usuario.getEmail());
        return dto;
    }
}
```

---

### UsuarioController Refactorizado

```java
package daw2a.gestionbiblioteca.controllers;

import daw2a.gestionbiblioteca.dto.CrearUsuarioDTO;
import daw2a.gestionbiblioteca.dto.ModificarUsuarioDTO;
import daw2a.gestionbiblioteca.dto.UsuarioDetalleDTO;
import daw2a.gestionbiblioteca.dto.UsuarioListadoDTO;
import daw2a.gestionbiblioteca.services.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {
    private final UsuarioService usuarioService;

    @Autowired
    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public ResponseEntity<Page<UsuarioListadoDTO>> listarUsuarios(@RequestParam(required = false) String nombre,
                                                                  Pageable pageable) {
        return ResponseEntity.ok(usuarioService.listarUsuarios(nombre, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioDetalleDTO> obtenerUsuario(@PathVariable Long id) {
        return ResponseEntity.ok(usuarioService.obtenerUsuario(id));
    }

    @GetMapping("/email")
    public ResponseEntity<UsuarioDetalleDTO> obtenerUsuarioPorEmail(@RequestParam("email") String email) {
        return ResponseEntity.ok(usuarioService.obtenerUsuarioPorEmail(email));
    }

    @PostMapping
    public ResponseEntity<UsuarioDetalleDTO> registrarUsuario(@RequestBody @Valid CrearUsuarioDTO crearUsuarioDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioService.registrarUsuario(crearUsuarioDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UsuarioDetalleDTO> actualizarUsuario(@PathVariable Long id,
                                                               @RequestBody @Valid ModificarUsuarioDTO modificarUsuarioDTO) {
        return ResponseEntity.ok(usuarioService.actualizarUsuario(id, modificarUsuarioDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarUsuario(@PathVariable Long id) {
        usuarioService.eliminarUsuario(id);
        return ResponseEntity.noContent().build();
    }
}
```

---

Con este código, el módulo de usuario está completamente refactorizado para usar **DTOs** en todos los métodos del controlador y del servicio. Esto mejora la separación de responsabilidades y la validación de datos. ¿Quieres que trabajemos en los tests para este controlador o pasamos a otra entidad?


### DTOs Autor
### Refactorización Completa de Autor

A continuación, incluyo todos los archivos necesarios refactorizados para trabajar con **DTOs**, incluyendo el **controlador**, el **servicio**, y los **DTOs**.

---

### DTOs

#### `AutorDTO` (Sin libros)
```java
package daw2a.gestionbiblioteca.dto;

public class AutorDTO {
    private Long id;
    private String nombre;
    private String nacionalidad;

    // Getters y Setters
}
```

---

#### `AutorConLibrosDTO` (Con libros)
```java
package daw2a.gestionbiblioteca.dto;

import java.util.List;

public class AutorConLibrosDTO {
    private Long id;
    private String nombre;
    private String nacionalidad;
    private List<String> libros;

    // Getters y Setters
}
```

---

#### `CrearAutorDTO`
```java
package daw2a.gestionbiblioteca.dto;

import jakarta.validation.constraints.NotBlank;

public class CrearAutorDTO {
    @NotBlank
    private String nombre;

    @NotBlank
    private String nacionalidad;

    // Getters y Setters
}
```

---

#### `ModificarAutorDTO`
```java
package daw2a.gestionbiblioteca.dto;

public class ModificarAutorDTO {
    private String nombre;
    private String nacionalidad;

    // Getters y Setters
}
```

---

### Servicio (`AutorService`)
```java
package daw2a.gestionbiblioteca.services;

import daw2a.gestionbiblioteca.dto.AutorConLibrosDTO;
import daw2a.gestionbiblioteca.dto.AutorDTO;
import daw2a.gestionbiblioteca.dto.CrearAutorDTO;
import daw2a.gestionbiblioteca.dto.ModificarAutorDTO;
import daw2a.gestionbiblioteca.entities.Autor;
import daw2a.gestionbiblioteca.exceptions.RecursoNoEncontradoException;
import daw2a.gestionbiblioteca.repositories.AutorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
public class AutorService {
    private final AutorRepository autorRepository;

    @Autowired
    public AutorService(AutorRepository autorRepository) {
        this.autorRepository = autorRepository;
    }

    public Page<?> listarAutores(String nombre, boolean conLibros, Pageable pageable) {
        Page<Autor> autores;

        if (nombre != null && !nombre.isEmpty()) {
            autores = autorRepository.findByNombreContainingIgnoreCase(nombre, pageable);
        } else {
            autores = autorRepository.findAll(pageable);
        }

        if (conLibros) {
            return autores.map(this::convertirAAutorConLibrosDTO);
        } else {
            return autores.map(this::convertirAAutorDTO);
        }
    }

    public AutorDTO obtenerAutor(Long id) {
        Autor autor = autorRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Autor no encontrado con id " + id));
        return convertirAAutorDTO(autor);
    }

    public AutorDTO crearAutor(CrearAutorDTO crearAutorDTO) {
        Autor autor = new Autor();
        autor.setNombre(crearAutorDTO.getNombre());
        autor.setNacionalidad(crearAutorDTO.getNacionalidad());
        return convertirAAutorDTO(autorRepository.save(autor));
    }

    public AutorDTO actualizarAutor(Long id, ModificarAutorDTO modificarAutorDTO) {
        Autor autor = autorRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Autor no encontrado con id " + id));

        if (modificarAutorDTO.getNombre() != null) {
            autor.setNombre(modificarAutorDTO.getNombre());
        }
        if (modificarAutorDTO.getNacionalidad() != null) {
            autor.setNacionalidad(modificarAutorDTO.getNacionalidad());
        }

        return convertirAAutorDTO(autorRepository.save(autor));
    }

    public void eliminarAutor(Long id) {
        if (!autorRepository.existsById(id)) {
            throw new RecursoNoEncontradoException("Autor no encontrado con id " + id);
        }
        autorRepository.deleteById(id);
    }

    private AutorDTO convertirAAutorDTO(Autor autor) {
        AutorDTO dto = new AutorDTO();
        dto.setId(autor.getId());
        dto.setNombre(autor.getNombre());
        dto.setNacionalidad(autor.getNacionalidad());
        return dto;
    }

    private AutorConLibrosDTO convertirAAutorConLibrosDTO(Autor autor) {
        AutorConLibrosDTO dto = new AutorConLibrosDTO();
        dto.setId(autor.getId());
        dto.setNombre(autor.getNombre());
        dto.setNacionalidad(autor.getNacionalidad());
        dto.setLibros(autor.getLibros().stream()
                .map(libro -> libro.getTitulo())
                .collect(Collectors.toList()));
        return dto;
    }
}
```

---

### Controlador (`AutorController`)

```java
package daw2a.gestionbiblioteca.controllers;

import daw2a.gestionbiblioteca.dto.AutorConLibrosDTO;
import daw2a.gestionbiblioteca.dto.AutorDTO;
import daw2a.gestionbiblioteca.dto.CrearAutorDTO;
import daw2a.gestionbiblioteca.dto.ModificarAutorDTO;
import daw2a.gestionbiblioteca.services.AutorService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/autores")
public class AutorController {
    private final AutorService autorService;

    @Autowired
    public AutorController(AutorService autorService) {
        this.autorService = autorService;
    }

    @GetMapping
    public ResponseEntity<Page<?>> listarAutores(@RequestParam(required = false) String nombre,
                                                 @RequestParam(defaultValue = "false") boolean conLibros,
                                                 Pageable pageable) {
        Page<?> autores = autorService.listarAutores(nombre, conLibros, pageable);
        return ResponseEntity.ok(autores);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AutorDTO> obtenerAutor(@PathVariable Long id) {
        return ResponseEntity.ok(autorService.obtenerAutor(id));
    }

    @PostMapping
    public ResponseEntity<AutorDTO> crearAutor(@RequestBody @Valid CrearAutorDTO crearAutorDTO) {
        return ResponseEntity.ok(autorService.crearAutor(crearAutorDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AutorDTO> actualizarAutor(@PathVariable Long id,
                                                    @RequestBody @Valid ModificarAutorDTO modificarAutorDTO) {
        return ResponseEntity.ok(autorService.actualizarAutor(id, modificarAutorDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarAutor(@PathVariable Long id) {
        autorService.eliminarAutor(id);
        return ResponseEntity.noContent().build();
    }
}
```

---

### Beneficios de la Refactorización

1. **Uso de DTOs**:
   - Mejora la separación de responsabilidades.
   - Evita exponer directamente las entidades de la base de datos.

2. **Flexibilidad en el Listado**:
   - Soporte para incluir o excluir libros según el valor del parámetro `conLibros`.

3. **Filtrado por Nombre**:
   - Permite buscar autores por coincidencia parcial en el nombre (`nombre`).

4. **Paginación**:
   - Soporte para paginación eficiente con `Pageable`.


## DTOs y refactorización para Prestamos
### Refactorización del Módulo de Préstamos con DTOs

Vamos a introducir **DTOs** para estructurar mejor los datos que se envían y reciben en las operaciones relacionadas con los préstamos. Estos son los cambios:

---

### **DTOs para Préstamo**

#### `PrestamoDTO` (Detalles de un préstamo)
```java
package daw2a.gestionbiblioteca.dto;

import java.time.LocalDate;

public class PrestamoDTO {
    private Long id;
    private Long libroId;
    private String libroTitulo;
    private Long usuarioId;
    private String usuarioNombre;
    private LocalDate fechaPrestamo;
    private LocalDate fechaDevolucion;

    // Getters y Setters
}
```

---

#### `CrearPrestamoDTO` (Para crear un préstamo)
```java
package daw2a.gestionbiblioteca.dto;

import jakarta.validation.constraints.NotNull;

public class CrearPrestamoDTO {
    @NotNull
    private Long libroId;

    @NotNull
    private Long usuarioId;

    // Getters y Setters
}
```

---

#### `RenovarPrestamoDTO` (Para renovar un préstamo)
```java
package daw2a.gestionbiblioteca.dto;

import jakarta.validation.constraints.NotNull;

public class RenovarPrestamoDTO {
    @NotNull
    private int diasExtension;

    // Getters y Setters
}
```

---

### **Modificaciones en el Servicio de Préstamos**

#### Servicio Refactorizado
```java
package daw2a.gestionbiblioteca.services;

import daw2a.gestionbiblioteca.dto.CrearPrestamoDTO;
import daw2a.gestionbiblioteca.dto.PrestamoDTO;
import daw2a.gestionbiblioteca.entities.Libro;
import daw2a.gestionbiblioteca.entities.Prestamo;
import daw2a.gestionbiblioteca.exceptions.LibroNoDisponibleException;
import daw2a.gestionbiblioteca.exceptions.PrestamoVencidoException;
import daw2a.gestionbiblioteca.exceptions.RecursoNoEncontradoException;
import daw2a.gestionbiblioteca.repositories.LibroRepository;
import daw2a.gestionbiblioteca.repositories.PrestamoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PrestamoService {

    private final PrestamoRepository prestamoRepository;
    private final LibroRepository libroRepository;

    @Autowired
    public PrestamoService(PrestamoRepository prestamoRepository, LibroRepository libroRepository) {
        this.prestamoRepository = prestamoRepository;
        this.libroRepository = libroRepository;
    }

    public Page<PrestamoDTO> listarPrestamos(Pageable pageable) {
        return prestamoRepository.findAll(pageable).map(this::convertirAPrestamoDTO);
    }

    public PrestamoDTO obtenerPrestamoPorId(Long id) {
        Prestamo prestamo = prestamoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Préstamo no encontrado con id " + id));
        return convertirAPrestamoDTO(prestamo);
    }

    @Transactional
    public PrestamoDTO crearPrestamo(CrearPrestamoDTO crearPrestamoDTO) {
        Libro libro = libroRepository.findById(crearPrestamoDTO.getLibroId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Libro no encontrado con id " + crearPrestamoDTO.getLibroId()));

        if ("prestado".equalsIgnoreCase(libro.getEstado())) {
            throw new LibroNoDisponibleException("El libro ya está prestado.");
        }

        Prestamo prestamo = new Prestamo();
        prestamo.setLibro(libro);
        prestamo.setUsuarioId(crearPrestamoDTO.getUsuarioId());
        prestamo.setFechaPrestamo(LocalDate.now());

        libro.setEstado("prestado");
        libroRepository.save(libro);

        return convertirAPrestamoDTO(prestamoRepository.save(prestamo));
    }

    @Transactional
    public PrestamoDTO renovarPrestamo(Long id, int diasExtension) {
        Prestamo prestamo = prestamoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Préstamo no encontrado con id " + id));

        if (prestamo.estaVencido()) {
            throw new PrestamoVencidoException("No se puede renovar un préstamo vencido.");
        }

        prestamo.setFechaPrestamo(prestamo.getFechaPrestamo().plusDays(diasExtension));
        return convertirAPrestamoDTO(prestamoRepository.save(prestamo));
    }

    @Transactional
    public PrestamoDTO devolverPrestamo(Long id) {
        Prestamo prestamo = prestamoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Préstamo no encontrado con id " + id));

        Libro libro = prestamo.getLibro();
        libro.setEstado("disponible");
        libroRepository.save(libro);

        prestamo.setFechaDevolucion(LocalDate.now());
        return convertirAPrestamoDTO(prestamoRepository.save(prestamo));
    }

    public void eliminarPrestamo(Long id) {
        Prestamo prestamo = prestamoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Préstamo no encontrado con id " + id));
        prestamoRepository.delete(prestamo);
    }

    private PrestamoDTO convertirAPrestamoDTO(Prestamo prestamo) {
        PrestamoDTO dto = new PrestamoDTO();
        dto.setId(prestamo.getId());
        dto.setLibroId(prestamo.getLibro().getId());
        dto.setLibroTitulo(prestamo.getLibro().getTitulo());
        dto.setUsuarioId(prestamo.getUsuarioId());
        dto.setFechaPrestamo(prestamo.getFechaPrestamo());
        dto.setFechaDevolucion(prestamo.getFechaDevolucion());
        return dto;
    }
}
```

---

### **Modificaciones en el Controlador de Préstamos**

#### Controlador Refactorizado
```java
package daw2a.gestionbiblioteca.controllers;

import daw2a.gestionbiblioteca.dto.CrearPrestamoDTO;
import daw2a.gestionbiblioteca.dto.PrestamoDTO;
import daw2a.gestionbiblioteca.services.PrestamoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/prestamos")
public class PrestamoController {

    private final PrestamoService prestamoService;

    @Autowired
    public PrestamoController(PrestamoService prestamoService) {
        this.prestamoService = prestamoService;
    }

    @GetMapping
    public ResponseEntity<Page<PrestamoDTO>> listarPrestamos(Pageable pageable) {
        return ResponseEntity.ok(prestamoService.listarPrestamos(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PrestamoDTO> obtenerPrestamo(@PathVariable Long id) {
        return ResponseEntity.ok(prestamoService.obtenerPrestamoPorId(id));
    }

    @PostMapping
    public ResponseEntity<PrestamoDTO> crearPrestamo(@RequestBody @Valid CrearPrestamoDTO crearPrestamoDTO) {
        return ResponseEntity.ok(prestamoService.crearPrestamo(crearPrestamoDTO));
    }

    @PutMapping("/renovar/{id}")
    public ResponseEntity<PrestamoDTO> renovarPrestamo(@PathVariable Long id, @RequestParam int diasExtension) {
        return ResponseEntity.ok(prestamoService.renovarPrestamo(id, diasExtension));
    }

    @PutMapping("/devolver/{id}")
    public ResponseEntity<PrestamoDTO> devolverPrestamo(@PathVariable Long id) {
        return ResponseEntity.ok(prestamoService.devolverPrestamo(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarPrestamo(@PathVariable Long id) {
        prestamoService.eliminarPrestamo(id);
        return ResponseEntity.noContent().build();
    }
}
```

---

### Beneficios de la Refactorización

1. **Estandarización**:
   - Uso de DTOs evita exponer las entidades directamente.
   - Facilita la validación y estructuración de datos.

2. **Separación de Responsabilidades**:
   - Los controladores se enfocan en manejar solicitudes HTTP.
   - Los servicios gestionan la lógica de negocio.

3. **Reutilización**:
   - Los DTOs permiten construir distintas vistas según las necesidades de la API.
En el caso de la funcionalidad **Crear Préstamo**, incluir la fecha como parte de la entrada puede no ser necesario, ya que la fecha de préstamo suele establecerse automáticamente como la fecha actual en la lógica de negocio. Esto simplifica la API y reduce errores del cliente.

### **Razones para No Incluir la Fecha de Préstamo en la Entrada**
1. **Simplicidad**: La API es más fácil de usar, ya que el cliente no tiene que calcular o enviar una fecha.
2. **Precisión**: La fecha de préstamo generalmente corresponde al momento en que el préstamo se registra, lo que puede asegurarse automáticamente en el backend.
3. **Consistencia**: Reduce el riesgo de discrepancias entre la fecha enviada por el cliente y la fecha real.

### **Cómo Manejarlo en el Código**
- **DTO de Creación de Préstamo**: El DTO no debe incluir un campo de fecha.
- **Lógica de Servicio**: Al registrar el préstamo, el servicio debe asignar la fecha actual (`LocalDate.now()`).

#### Ejemplo de Creación en el Servicio:
```java
@Transactional
public PrestamoDTO crearPrestamo(CrearPrestamoDTO crearPrestamoDTO) {
    Libro libro = libroRepository.findById(crearPrestamoDTO.getLibroId())
            .orElseThrow(() -> new RecursoNoEncontradoException("Libro no encontrado con id " + crearPrestamoDTO.getLibroId()));

    if ("prestado".equalsIgnoreCase(libro.getEstado())) {
        throw new LibroNoDisponibleException("El libro ya está prestado.");
    }

    Prestamo prestamo = new Prestamo();
    prestamo.setLibro(libro);
    prestamo.setUsuarioId(crearPrestamoDTO.getUsuarioId());
    prestamo.setFechaPrestamo(LocalDate.now()); // Fecha actual asignada aquí

    libro.setEstado("prestado");
    libroRepository.save(libro);

    return convertirAPrestamoDTO(prestamoRepository.save(prestamo));
}
```

#### DTO de Creación:
```java
public class CrearPrestamoDTO {
    @NotNull
    private Long libroId;

    @NotNull
    private Long usuarioId;

    // No incluimos fecha aquí

    // Getters y Setters
}
```

---

### **Cuándo Podría Ser Necesario Incluir la Fecha de Préstamo**
Si la API soporta escenarios como registrar préstamos retroactivos (por ejemplo, préstamos realizados en el pasado), entonces podría ser útil permitir que el cliente proporcione una fecha. En este caso:

1. El DTO de creación incluiría la fecha como un campo opcional.
2. La lógica del servicio podría usar esa fecha, o la fecha actual como predeterminada si no se proporciona.

#### Ejemplo con Fecha Opcional:
```java
public PrestamoDTO crearPrestamo(CrearPrestamoDTO crearPrestamoDTO) {
    Prestamo prestamo = new Prestamo();
    prestamo.setFechaPrestamo(crearPrestamoDTO.getFechaPrestamo() != null ?
        crearPrestamoDTO.getFechaPrestamo() : LocalDate.now());
    // Resto de la lógica...
}
```

---

### **Recomendación**
- **No incluir la fecha en el DTO** para escenarios normales, donde la fecha siempre es la actual.
- **Incluirla como opcional** solo si existen casos de uso específicos que lo requieran, como la creación retroactiva de préstamos.

Esto depende del contexto del proyecto y los requerimientos del cliente o usuario.
Un **DTO específico para la devolución de un préstamo** puede ser útil dependiendo de cómo desees manejar la lógica y la claridad de tu API. Vamos a analizar los pros y contras:

---

### **¿Cuándo se necesita un DTO para devolver un préstamo?**

#### **1. Si la API espera datos adicionales en la devolución**
   Por ejemplo:
   - Si necesitas un comentario del usuario sobre el estado del libro.
   - Si la fecha de devolución debe ser específica y no la actual.
   - Si el flujo de devolución tiene validaciones complejas.

En estos casos, un DTO dedicado a la devolución (como `DevolverPrestamoDTO`) ayuda a estructurar y validar la información.

#### **Ejemplo de DTO para devolución:**
```java
public class DevolverPrestamoDTO {
    private LocalDate fechaDevolucion; // Opcional, puede ser null para usar LocalDate.now()
    private String comentario; // Comentario sobre el estado del libro

    // Getters y Setters
}
```

El controlador podría recibir este DTO:
```java
@PutMapping("/devolver/{id}")
public ResponseEntity<PrestamoDTO> devolverPrestamo(@PathVariable Long id, @RequestBody @Valid DevolverPrestamoDTO devolverPrestamoDTO) {
    PrestamoDTO prestamo = prestamoService.devolverPrestamo(id, devolverPrestamoDTO);
    return ResponseEntity.ok(prestamo);
}
```

En el servicio:
```java
@Transactional
public PrestamoDTO devolverPrestamo(Long id, DevolverPrestamoDTO devolverPrestamoDTO) {
    return prestamoRepository.findById(id)
            .map(prestamo -> {
                Libro libro = prestamo.getLibro();
                libro.setEstado("disponible");
                libroRepository.save(libro);

                prestamo.setFechaDevolucion(devolverPrestamoDTO.getFechaDevolucion() != null
                    ? devolverPrestamoDTO.getFechaDevolucion()
                    : LocalDate.now());

                // Opcional: Manejar el comentario
                if (devolverPrestamoDTO.getComentario() != null) {
                    // Guardar o procesar el comentario
                }

                return convertirAPrestamoDTO(prestamoRepository.save(prestamo));
            })
            .orElseThrow(() -> new RecursoNoEncontradoException("Préstamo no encontrado con id " + id));
}
```

---

### **¿Cuándo NO es necesario un DTO para devolver un préstamo?**

#### **1. Si la devolución no requiere datos adicionales**
   - Si el servicio de devolución siempre usa la fecha actual (`LocalDate.now()`).
   - Si la lógica de devolución es simple y no necesita validaciones específicas.

En este caso, el controlador podría manejar la devolución con solo el ID del préstamo:
```java
@PutMapping("/devolver/{id}")
public ResponseEntity<PrestamoDTO> devolverPrestamo(@PathVariable Long id) {
    PrestamoDTO prestamo = prestamoService.devolverPrestamo(id);
    return ResponseEntity.ok(prestamo);
}
```

Y el servicio simplemente procesa la devolución:
```java
@Transactional
public PrestamoDTO devolverPrestamo(Long id) {
    return prestamoRepository.findById(id)
            .map(prestamo -> {
                Libro libro = prestamo.getLibro();
                libro.setEstado("disponible");
                libroRepository.save(libro);

                prestamo.setFechaDevolucion(LocalDate.now());
                return convertirAPrestamoDTO(prestamoRepository.save(prestamo));
            })
            .orElseThrow(() -> new RecursoNoEncontradoException("Préstamo no encontrado con id " + id));
}
```

---

### **Recomendación**
- **No usar un DTO** si la devolución es simple y siempre usa la fecha actual, sin datos adicionales.
- **Usar un DTO** si necesitas capturar datos específicos (como un comentario o una fecha personalizada).

Esto depende de los requisitos de tu aplicación, pero para casos simples, omitir el DTO es suficiente y mantiene la API limpia.
