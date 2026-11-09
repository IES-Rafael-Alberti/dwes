Vamos a implementar **roles** y la **autorización** en el sistema. Aquí está el plan para abordar esta funcionalidad:

---

## **Pasos para implementar roles y autorización**

### 1. **Definir roles y asignarlos a los usuarios**

Los roles ya están definidos en el proyecto. Verificaremos que cada usuario tiene un campo `Rol` asociado y que está configurado correctamente. Los roles son:

- **USUARIO**: Para usuarios normales que pueden realizar préstamos.
- **BIBLIOTECARIO**: Para bibliotecarios que gestionan libros y autores.

### 2. **Configurar roles en `SecurityConfig`**

Configura las reglas de acceso para los endpoints según los roles. Los roles se aplicarán con anotaciones como `@PreAuthorize`.

---

### **Código de autorización basado en roles**

#### **Actualizar `SecurityConfig`**

Añadiremos reglas de acceso según roles en la configuración de seguridad:

```java
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true) // Habilitar anotaciones de método como @PreAuthorize
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final JwtRequestFilter jwtRequestFilter;
    private final UserDetailsService userDetailsService;

    public SecurityConfig(JwtRequestFilter jwtRequestFilter, UserDetailsService userDetailsService) {
        this.jwtRequestFilter = jwtRequestFilter;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
            .authorizeRequests()
            .antMatchers("/authenticate", "/register").permitAll() // Endpoints públicos
            .antMatchers("/libros/**").hasRole("BIBLIOTECARIO") // Solo bibliotecarios
            .antMatchers("/prestamos/**").hasRole("USUARIO") // Solo usuarios
            .anyRequest().authenticated() // Cualquier otra ruta requiere autenticación
            .and()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
    }
}
```

---

#### **Uso de `@PreAuthorize` en los controladores**

En lugar de definir las reglas en `SecurityConfig`, puedes utilizar `@PreAuthorize` directamente en los controladores para proteger métodos específicos.

```java
@RestController
@RequestMapping("/libros")
public class LibroController {

    @PreAuthorize("hasRole('BIBLIOTECARIO')")
    @PostMapping
    public ResponseEntity<LibroDTO> crearLibro(@RequestBody @Valid CrearLibroDTO crearLibroDTO) {
        LibroDTO nuevoLibro = libroService.crearLibro(crearLibroDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoLibro);
    }

    @PreAuthorize("hasRole('BIBLIOTECARIO')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarLibro(@PathVariable Long id) {
        libroService.borrarLibro(id);
        return ResponseEntity.noContent().build();
    }
}
```

---

### **Pruebas de los roles y autorización**

1. **Usuarios con roles correctos:**
   - Prueba endpoints como `/libros` con usuarios con el rol **BIBLIOTECARIO**.
   - Prueba endpoints como `/prestamos` con usuarios con el rol **USUARIO**.

2. **Usuarios con roles incorrectos:**
   - Intenta acceder a `/libros` con un usuario normal y verifica que devuelve `403 Forbidden`.
   - Intenta acceder a `/prestamos` con un bibliotecario y verifica que devuelve `403 Forbidden`.

3. **Usuarios no autenticados:**
   - Intenta acceder a cualquier endpoint protegido sin un JWT y verifica que devuelve `401 Unauthorized`.

---

### **Siguientes pasos**
1. **Implementar roles en el sistema existente:**
   - Asegúrate de que cada usuario tiene un rol asignado correctamente.
   - Configura pruebas automáticas para roles y autorización.

2. **Ajustar los DTO y controladores si es necesario.**

¡Perfecto! Implementar roles en el sistema existente implica realizar ajustes en varias partes del proyecto para garantizar que los usuarios tengan roles definidos y que estos roles se utilicen en la autorización. Aquí tienes los pasos:

---

## **Pasos para implementar roles**

### 1. **Definir los roles en el sistema**

Ya tenemos la enumeración `Rol`:

```java
public enum Rol {
    USUARIO,
    BIBLIOTECARIO
}
```

Asegúrate de que la entidad `Usuario` ya tiene un campo `Rol`:

```java
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Rol rol;
}
```

---

### 2. **Asignar roles al registrar un usuario**

Ajustemos el método de registro en el `UsuarioService` para que asigne roles. El rol por defecto será **USUARIO**:

#### **Cambios en `UsuarioService`**
```java
public Usuario registrarUsuario(CrearUsuarioDTO crearUsuarioDTO) {
    if (usuarioRepository.findUsuarioByEmail(crearUsuarioDTO.getEmail()).isPresent()) {
        throw new RecursoDuplicadoException("El email " + crearUsuarioDTO.getEmail() + " ya está en uso.");
    }

    Usuario usuario = new Usuario();
    usuario.setNombre(crearUsuarioDTO.getNombre());
    usuario.setEmail(crearUsuarioDTO.getEmail());
    usuario.setPassword(passwordEncoder.encode(crearUsuarioDTO.getPassword()));
    usuario.setRol(Rol.USUARIO); // Rol por defecto

    return usuarioRepository.save(usuario);
}
```

Si deseas permitir que un administrador asigne roles al registrar usuarios, puedes agregar un campo opcional `rol` al DTO y manejarlo en el servicio:

```java
if (crearUsuarioDTO.getRol() != null) {
    usuario.setRol(crearUsuarioDTO.getRol());
}
```

---

### 3. **Configurar roles en la autenticación JWT**

Asegúrate de que el token JWT incluya el rol del usuario. En la clase `JwtUtil`, ajusta la generación del token para añadir un claim con el rol:

```java
public String generateToken(UserDetails userDetails, String rol) {
    Map<String, Object> claims = new HashMap<>();
    claims.put("rol", rol); // Incluir el rol en el token
    return createToken(claims, userDetails.getUsername());
}

private String createToken(Map<String, Object> claims, String subject) {
    return Jwts.builder()
            .setClaims(claims)
            .setSubject(subject)
            .setIssuedAt(new Date(System.currentTimeMillis()))
            .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10)) // 10 horas
            .signWith(SignatureAlgorithm.HS256, SECRET_KEY.getBytes())
            .compact();
}
```

También, ajusta la validación del token para extraer el rol del usuario si es necesario.

---

### 4. **Añadir reglas de autorización por roles**

Actualiza el `SecurityConfig` para incluir las reglas de acceso basadas en roles.

#### **Ejemplo de reglas de acceso en `SecurityConfig`**
```java
@Override
protected void configure(HttpSecurity http) throws Exception {
    http.csrf().disable()
        .authorizeRequests()
        .antMatchers("/authenticate", "/register").permitAll() // Endpoints públicos
        .antMatchers("/libros/**").hasRole("BIBLIOTECARIO") // Solo bibliotecarios
        .antMatchers("/prestamos/**").hasRole("USUARIO") // Solo usuarios
        .anyRequest().authenticated()
        .and()
        .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

    http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
}
```

---

### 5. **Proteger métodos específicos con `@PreAuthorize`**

En los controladores, puedes usar `@PreAuthorize` para proteger métodos específicos según los roles. Ejemplo:

```java
@RestController
@RequestMapping("/libros")
public class LibroController {

    @PreAuthorize("hasRole('BIBLIOTECARIO')")
    @PostMapping
    public ResponseEntity<LibroDTO> crearLibro(@RequestBody @Valid CrearLibroDTO crearLibroDTO) {
        LibroDTO nuevoLibro = libroService.crearLibro(crearLibroDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoLibro);
    }

    @PreAuthorize("hasRole('BIBLIOTECARIO')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarLibro(@PathVariable Long id) {
        libroService.borrarLibro(id);
        return ResponseEntity.noContent().build();
    }
}
```

---

### 6. **Probar roles y autorización**

#### **Pruebas automatizadas**
- Crea pruebas para verificar que un usuario con rol **USUARIO** no puede acceder a endpoints protegidos para **BIBLIOTECARIO**.
- Verifica que el rol **BIBLIOTECARIO** tiene acceso a todos los endpoints.

#### **Pruebas manuales**
- Usa un cliente REST como Postman o Insomnia para autenticar usuarios con diferentes roles y probar acceso a los endpoints.

---

### 7. **Opcional: Endpoints para cambiar roles**

Si necesitas cambiar el rol de un usuario, crea un endpoint específico en `UsuarioController`. Por ejemplo:

```java
@PreAuthorize("hasRole('BIBLIOTECARIO')")
@PatchMapping("/{id}/rol")
public ResponseEntity<UsuarioDTO> cambiarRol(@PathVariable Long id, @RequestParam Rol nuevoRol) {
    UsuarioDTO usuarioActualizado = usuarioService.cambiarRol(id, nuevoRol);
    return ResponseEntity.ok(usuarioActualizado);
}
```

#### **Método en el servicio**
```java
public Usuario cambiarRol(Long id, Rol nuevoRol) {
    Usuario usuario = usuarioRepository.findById(id)
            .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado con id " + id));
    usuario.setRol(nuevoRol);
    return usuarioRepository.save(usuario);
}
```

---
¡Vamos con la implementación de roles! A continuación, seguimos paso a paso.

---

### **Paso 1: Configuración del Rol en el Sistema**

1. **Revisión de la entidad `Usuario`**
   - Verifica que el campo `Rol` ya esté definido en la entidad `Usuario` como `@Enumerated` para manejarlo como un enumerado en JPA.

2. **Enumeración `Rol`**
   - Confirma que la enumeración `Rol` tenga valores como `USUARIO` y `BIBLIOTECARIO`.

---

### **Paso 2: Ajustes en `UsuarioService`**

1. **Registrar usuario con rol predeterminado (`USUARIO`)**
   - Aseguramos que todos los nuevos usuarios tengan un rol predeterminado al registrarse.

#### **Código actualizado para registro en `UsuarioService`**
```java
public Usuario registrarUsuario(CrearUsuarioDTO crearUsuarioDTO) {
    if (usuarioRepository.findUsuarioByEmail(crearUsuarioDTO.getEmail()).isPresent()) {
        throw new RecursoDuplicadoException("El email " + crearUsuarioDTO.getEmail() + " ya está en uso.");
    }

    Usuario usuario = new Usuario();
    usuario.setNombre(crearUsuarioDTO.getNombre());
    usuario.setEmail(crearUsuarioDTO.getEmail());
    usuario.setPassword(passwordEncoder.encode(crearUsuarioDTO.getPassword()));
    usuario.setRol(Rol.USUARIO); // Rol por defecto

    return usuarioRepository.save(usuario);
}
```

2. **Cambiar rol del usuario**
   - Creamos un método para cambiar el rol de un usuario en `UsuarioService`.

#### **Código para cambiar rol en `UsuarioService`**
```java
public Usuario cambiarRol(Long id, Rol nuevoRol) {
    Usuario usuario = usuarioRepository.findById(id)
            .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado con id " + id));
    usuario.setRol(nuevoRol);
    return usuarioRepository.save(usuario);
}
```

---

### **Paso 3: Ajustes en `SecurityConfig`**

1. **Configurar roles en la seguridad**
   - Actualizamos las reglas de acceso en `SecurityConfig` para proteger endpoints según los roles.

#### **Reglas actualizadas**
```java
@Override
protected void configure(HttpSecurity http) throws Exception {
    http.csrf().disable()
        .authorizeRequests()
        .antMatchers("/authenticate", "/register").permitAll() // Públicos
        .antMatchers("/libros/**").hasRole("BIBLIOTECARIO") // Solo bibliotecarios
        .antMatchers("/prestamos/**").hasRole("USUARIO") // Solo usuarios
        .anyRequest().authenticated()
        .and()
        .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

    http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
}
```

2. **Incluir rol en el JWT**
   - Ajustamos `JwtUtil` para incluir el rol del usuario en el token.

#### **Código para generar token con rol**
```java
public String generateToken(UserDetails userDetails, String rol) {
    Map<String, Object> claims = new HashMap<>();
    claims.put("rol", rol); // Incluir el rol en el token
    return createToken(claims, userDetails.getUsername());
}
```

---

### **Paso 4: Cambios en los Controladores**

1. **Proteger métodos con `@PreAuthorize`**
   - En los controladores, usamos `@PreAuthorize` para proteger los métodos según los roles.

#### **Ejemplo en `LibroController`**
```java
@RestController
@RequestMapping("/libros")
public class LibroController {

    @PreAuthorize("hasRole('BIBLIOTECARIO')")
    @PostMapping
    public ResponseEntity<LibroDTO> crearLibro(@RequestBody @Valid CrearLibroDTO crearLibroDTO) {
        LibroDTO nuevoLibro = libroService.crearLibro(crearLibroDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoLibro);
    }

    @PreAuthorize("hasRole('BIBLIOTECARIO')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarLibro(@PathVariable Long id) {
        libroService.borrarLibro(id);
        return ResponseEntity.noContent().build();
    }
}
```

2. **Endpoint para cambiar rol**
   - Añadimos un endpoint en `UsuarioController` para cambiar el rol de un usuario.

#### **Código en `UsuarioController`**
```java
@PreAuthorize("hasRole('BIBLIOTECARIO')")
@PatchMapping("/{id}/rol")
public ResponseEntity<UsuarioDTO> cambiarRol(@PathVariable Long id, @RequestParam Rol nuevoRol) {
    UsuarioDTO usuarioActualizado = usuarioService.cambiarRol(id, nuevoRol);
    return ResponseEntity.ok(usuarioActualizado);
}
```

---

### **Paso 5: Pruebas**

1. **Pruebas automatizadas**
   - Verificamos que los roles funcionan correctamente con pruebas automatizadas:
     - Usuario con rol **USUARIO** no puede acceder a endpoints de **BIBLIOTECARIO**.
     - Usuario con rol **BIBLIOTECARIO** puede acceder a todos los endpoints.

2. **Pruebas manuales**
   - Usamos herramientas como Postman para autenticar usuarios con diferentes roles y probar acceso a los endpoints.

---
