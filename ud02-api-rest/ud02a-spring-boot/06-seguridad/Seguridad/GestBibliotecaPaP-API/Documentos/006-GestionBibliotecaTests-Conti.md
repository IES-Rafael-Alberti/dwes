## Tests

Conjunto consolidado de todos los tests que llevamos en el proyecto. Esto incluye los tests actualizados para reflejar el uso de DTOs, además de nuevos tests para los casos que no se hayan cubierto.

### **Tests para `LibroController`**

#### **Archivo: `LibroControllerTest.java`**

```java
package daw2a.gestionbiblioteca.controllers;

import daw2a.gestionbiblioteca.dto.libro.CrearLibroDTO;
import daw2a.gestionbiblioteca.dto.libro.LibroDTO;
import daw2a.gestionbiblioteca.services.LibroService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class LibroControllerTest {

    @Mock
    private LibroService libroService;

    @InjectMocks
    private LibroController libroController;

    private CrearLibroDTO crearLibroDTO;
    private LibroDTO libroDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        crearLibroDTO = new CrearLibroDTO("Titulo", "Genero", "2023", 1L);
        libroDTO = new LibroDTO(1L, "Titulo", "Genero", "disponible", 1L);
    }

    @Test
    void listarLibros() {
        PageRequest pageable = PageRequest.of(0, 10);
        Page<LibroDTO> page = new PageImpl<>(Arrays.asList(libroDTO));
        when(libroService.listarLibros(null, null, pageable)).thenReturn(page);

        ResponseEntity<Page<LibroDTO>> response = libroController.listarLibros(null, null, pageable);
        Page<LibroDTO> result = response.getBody();

        assertEquals(1, result.getTotalElements());
        verify(libroService, times(1)).listarLibros(null, null, pageable);
    }

    @Test
    void obtenerLibro() {
        when(libroService.obtenerLibro(1L)).thenReturn(libroDTO);

        ResponseEntity<LibroDTO> response = libroController.obtenerLibro(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(libroDTO, response.getBody());
        verify(libroService, times(1)).obtenerLibro(1L);
    }

    @Test
    void crearLibro() {
        when(libroService.crearLibro(crearLibroDTO)).thenReturn(libroDTO);

        ResponseEntity<LibroDTO> response = libroController.crearLibro(crearLibroDTO);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(libroDTO, response.getBody());
        verify(libroService, times(1)).crearLibro(crearLibroDTO);
    }

    @Test
    void actualizarLibro() {
        when(libroService.actualizarLibro(1L, crearLibroDTO)).thenReturn(libroDTO);

        ResponseEntity<LibroDTO> response = libroController.actualizarLibro(1L, crearLibroDTO);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(libroDTO, response.getBody());
        verify(libroService, times(1)).actualizarLibro(1L, crearLibroDTO);
    }

    @Test
    void eliminarLibro() {
        doNothing().when(libroService).borrarLibro(1L);

        ResponseEntity<Void> response = libroController.eliminarLibro(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(libroService, times(1)).borrarLibro(1L);
    }
}
```

---

### **Tests para `UsuarioController`**

#### **Archivo: `UsuarioControllerTest.java`**

```java
package daw2a.gestionbiblioteca.controllers;

import daw2a.gestionbiblioteca.dto.usuario.UsuarioDTO;
import daw2a.gestionbiblioteca.dto.usuario.RegistrarUsuarioDTO;
import daw2a.gestionbiblioteca.services.UsuarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class UsuarioControllerTest {

    @Mock
    private UsuarioService usuarioService;

    @InjectMocks
    private UsuarioController usuarioController;

    private RegistrarUsuarioDTO registrarUsuarioDTO;
    private UsuarioDTO usuarioDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        registrarUsuarioDTO = new RegistrarUsuarioDTO("Nombre", "email@example.com", "password123");
        usuarioDTO = new UsuarioDTO(1L, "Nombre", "email@example.com", "USUARIO");
    }

    @Test
    void listarUsuarios() {
        PageRequest pageable = PageRequest.of(0, 10);
        Page<UsuarioDTO> page = new PageImpl<>(Arrays.asList(usuarioDTO));
        when(usuarioService.listarUsuarios(null, pageable)).thenReturn(page);

        ResponseEntity<Page<UsuarioDTO>> response = usuarioController.listarUsuarios(null, pageable);
        Page<UsuarioDTO> result = response.getBody();

        assertEquals(1, result.getTotalElements());
        verify(usuarioService, times(1)).listarUsuarios(null, pageable);
    }

    @Test
    void obtenerUsuario() {
        when(usuarioService.obtenerUsuario(1L)).thenReturn(usuarioDTO);

        ResponseEntity<UsuarioDTO> response = usuarioController.obtenerUsuario(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(usuarioDTO, response.getBody());
        verify(usuarioService, times(1)).obtenerUsuario(1L);
    }

    @Test
    void registrarUsuario() {
        when(usuarioService.registrarUsuario(registrarUsuarioDTO)).thenReturn(usuarioDTO);

        ResponseEntity<UsuarioDTO> response = usuarioController.registrarUsuario(registrarUsuarioDTO);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(usuarioDTO, response.getBody());
        verify(usuarioService, times(1)).registrarUsuario(registrarUsuarioDTO);
    }

    @Test
    void eliminarUsuario() {
        doNothing().when(usuarioService).eliminarUsuario(1L);

        ResponseEntity<Void> response = usuarioController.eliminarUsuario(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(usuarioService, times(1)).eliminarUsuario(1L);
    }
}
```

---

### **Tests para `PrestamoController`**

#### **Archivo: `PrestamoControllerTest.java`**

Puedes seguir una estructura similar a los anteriores, adaptando los tests para los DTOs relacionados con préstamos y cubriendo casos como creación, devolución y renovación de préstamos.

---
### Tests para `PrestamoController` usando DTOs

Aquí tienes los tests adaptados para el `PrestamoController` que utiliza DTOs. Cubriremos las operaciones principales como listar, obtener, crear, actualizar, devolver y renovar préstamos.

#### **Archivo: `PrestamoControllerTest.java`**

```java
package daw2a.gestionbiblioteca.controllers;

import daw2a.gestionbiblioteca.dto.prestamo.CrearPrestamoDTO;
import daw2a.gestionbiblioteca.dto.prestamo.PrestamoDTO;
import daw2a.gestionbiblioteca.services.PrestamoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class PrestamoControllerTest {

    @Mock
    private PrestamoService prestamoService;

    @InjectMocks
    private PrestamoController prestamoController;

    private CrearPrestamoDTO crearPrestamoDTO;
    private PrestamoDTO prestamoDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        crearPrestamoDTO = new CrearPrestamoDTO(1L, 1L);
        prestamoDTO = new PrestamoDTO(1L, "Libro A", "Usuario A", "2023-11-09", null);
    }

    @Test
    void listarPrestamos() {
        PageRequest pageable = PageRequest.of(0, 10);
        Page<PrestamoDTO> page = new PageImpl<>(Arrays.asList(prestamoDTO));
        when(prestamoService.listarPrestamos(pageable)).thenReturn(page);

        ResponseEntity<Page<PrestamoDTO>> response = prestamoController.listarPrestamos(pageable);
        Page<PrestamoDTO> result = response.getBody();

        assertEquals(1, result.getTotalElements());
        verify(prestamoService, times(1)).listarPrestamos(pageable);
    }

    @Test
    void obtenerPrestamo() {
        when(prestamoService.obtenerPrestamoPorId(1L)).thenReturn(prestamoDTO);

        ResponseEntity<PrestamoDTO> response = prestamoController.obtenerPrestamo(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(prestamoDTO, response.getBody());
        verify(prestamoService, times(1)).obtenerPrestamoPorId(1L);
    }

    @Test
    void crearPrestamo() {
        when(prestamoService.crearPrestamo(crearPrestamoDTO)).thenReturn(prestamoDTO);

        ResponseEntity<PrestamoDTO> response = prestamoController.crearPrestamo(crearPrestamoDTO);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(prestamoDTO, response.getBody());
        verify(prestamoService, times(1)).crearPrestamo(crearPrestamoDTO);
    }

    @Test
    void renovarPrestamo() {
        when(prestamoService.renovarPrestamo(1L, 7)).thenReturn(prestamoDTO);

        ResponseEntity<PrestamoDTO> response = prestamoController.renovarPrestamo(1L, 7);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(prestamoDTO, response.getBody());
        verify(prestamoService, times(1)).renovarPrestamo(1L, 7);
    }

    @Test
    void devolverPrestamo() {
        when(prestamoService.devolverPrestamo(1L)).thenReturn(prestamoDTO);

        ResponseEntity<PrestamoDTO> response = prestamoController.devolverPrestamo(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(prestamoDTO, response.getBody());
        verify(prestamoService, times(1)).devolverPrestamo(1L);
    }

    @Test
    void eliminarPrestamo() {
        doNothing().when(prestamoService).eliminarPrestamo(1L);

        ResponseEntity<Void> response = prestamoController.eliminarPrestamo(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(prestamoService, times(1)).eliminarPrestamo(1L);
    }
}
```

---

### Explicación

1. **`listarPrestamos()`**:
   - Simula una llamada al servicio para listar préstamos con paginación.
   - Verifica que el resultado contiene el número esperado de elementos.

2. **`obtenerPrestamo()`**:
   - Simula la obtención de un préstamo por su ID.
   - Comprueba que el cuerpo de la respuesta coincide con el DTO esperado.

3. **`crearPrestamo()`**:
   - Verifica que se puede crear un préstamo y que el DTO esperado se devuelve con un estado `201 CREATED`.

4. **`renovarPrestamo()`**:
   - Simula la renovación de un préstamo y valida que se retorna el DTO actualizado.

5. **`devolverPrestamo()`**:
   - Comprueba que un préstamo puede ser devuelto y que el estado del préstamo es el esperado.

6. **`eliminarPrestamo()`**:
   - Simula la eliminación de un préstamo y valida que el estado de la respuesta es `204 NO CONTENT`.

---
### Paso 1: Asegurar cobertura completa de los DTOs recién creados

Para este paso, vamos a:

1. **Verificar que todos los DTOs tengan cobertura en los tests existentes**.
2. **Crear pruebas adicionales para asegurarnos de que los DTOs se utilizan correctamente en todos los endpoints**.

#### **Pruebas de DTOs en `PrestamoController`**

Ya tenemos pruebas para:
- `CrearPrestamoDTO`: usado en el endpoint de creación de préstamos.
- `PrestamoDTO`: usado en la mayoría de los endpoints.

Pruebas adicionales necesarias:
- Verificar validaciones de los DTOs como restricciones de campos nulos o tamaños inválidos.

##### Prueba para `CrearPrestamoDTO`:
```java
package daw2a.gestionbiblioteca.dto;

import daw2a.gestionbiblioteca.dto.prestamo.CrearPrestamoDTO;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CrearPrestamoDTOTest {

    private static Validator validator;

    @BeforeAll
    static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void validarCamposCorrectos() {
        CrearPrestamoDTO dto = new CrearPrestamoDTO(1L, 1L);

        assertTrue(validator.validate(dto).isEmpty(), "Los campos son válidos pero se encontraron errores");
    }

    @Test
    void validarCamposNulos() {
        CrearPrestamoDTO dto = new CrearPrestamoDTO(null, null);

        var violaciones = validator.validate(dto);

        assertEquals(2, violaciones.size(), "Se esperaban errores de validación para campos nulos");
    }
}
```

#### **Prueba de validación para `PrestamoDTO`**
Como `PrestamoDTO` es de solo lectura, su validación recae en el servicio/controlador.

---

### Paso 2: Pruebas de validación para entradas incorrectas

Es importante probar casos en los que las entradas no cumplan con las restricciones. Estos tests deben cubrir:
- Campos nulos.
- Tamaños mínimos/máximos de cadenas.
- IDs inválidos.

#### **Ejemplo: Validación en el controlador de creación de préstamos**

Prueba para asegurar que los controladores manejan correctamente los errores de validación:

```java
@Test
void crearPrestamo_ConCamposInvalidos() {
    CrearPrestamoDTO dtoInvalido = new CrearPrestamoDTO(null, null);

    ResponseEntity<?> response = prestamoController.crearPrestamo(dtoInvalido);

    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode(), "Se esperaba un error 400 BAD_REQUEST");
}
```

---

### Paso 3: Asegurarse de que todos los tests pasen después de la integración

1. Ejecutar todos los tests existentes usando `mvn test`.
2. Resolver fallos relacionados con:
   - Errores de DTOs no reflejados en servicios/controladores.
   - Problemas de serialización/deserialización en los DTOs.

Revisión específica:
- Validar los cambios en los endpoints que retornan DTOs paginados.
- Asegurarse de que los servicios ya no devuelven entidades.

---

### Paso 4: Añadir validaciones específicas en los tests para escenarios como errores de negocio

#### **Prueba para `LibroNoDisponibleException`**
Validar que no se permita crear un préstamo si el libro ya está prestado:
```java
@Test
void crearPrestamo_LibroNoDisponible() {
    when(prestamoService.crearPrestamo(any(CrearPrestamoDTO.class)))
            .thenThrow(new LibroNoDisponibleException("El libro ya está prestado."));

    CrearPrestamoDTO dto = new CrearPrestamoDTO(1L, 1L);
    ResponseEntity<?> response = prestamoController.crearPrestamo(dto);

    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode(), "Se esperaba un error 400 BAD_REQUEST");
    verify(prestamoService, times(1)).crearPrestamo(any(CrearPrestamoDTO.class));
}
```

#### **Prueba para `PrestamoVencidoException`**
Validar que no se permita crear un préstamo si el usuario tiene préstamos vencidos:
```java
@Test
void crearPrestamo_UsuarioConPrestamosVencidos() {
    when(prestamoService.crearPrestamo(any(CrearPrestamoDTO.class)))
            .thenThrow(new PrestamoVencidoException("El usuario tiene préstamos vencidos."));

    CrearPrestamoDTO dto = new CrearPrestamoDTO(1L, 1L);
    ResponseEntity<?> response = prestamoController.crearPrestamo(dto);

    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode(), "Se esperaba un error 400 BAD_REQUEST");
    verify(prestamoService, times(1)).crearPrestamo(any(CrearPrestamoDTO.class));
}
```

---

### Resumen de próximos pasos:
1. Completar las pruebas de validación para todos los DTOs creados.
2. Asegurar la cobertura de casos límite y escenarios de error en todos los tests.
3. Ejecutar nuevamente `mvn test` para verificar la cobertura completa.
