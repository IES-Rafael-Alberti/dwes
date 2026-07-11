## Autenticación con JWT: Userdetail vs User (usuario)

En una implementación típica de **Spring Security con JWT**, es necesario separar el concepto de **Usuario de la aplicación** (la entidad `Usuario` en tu modelo de dominio) del **Usuario para la autenticación** (usado internamente por Spring Security). Esto se hace mediante una implementación de la interfaz **`UserDetails`**.

Esta separación permite:

1. **Mantener la lógica del modelo de dominio desacoplada de la seguridad**:
   - La entidad `Usuario` puede contener campos como `nombre`, `email`, etc.
   - El objeto de seguridad debe centrarse en lo que Spring necesita para autenticar y autorizar (e.g., roles, credenciales).

2. **Flexibilidad para mapear datos**:
   - Si decides cambiar cómo se almacenan los datos o añadir nuevos métodos de autenticación, solo necesitas ajustar esta capa.

---

### **Pasos para integrar `UserDetails` con tu modelo `Usuario`**

#### **Paso 1: Crear una clase que implemente `UserDetails`**
Mapea los datos de la entidad `Usuario` a un objeto que Spring Security pueda usar.

#### Ejemplo: Clase `CustomUserDetails`

```java
package daw2a.gestionbiblioteca.security;

import daw2a.gestionbiblioteca.entities.Usuario;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.stream.Collectors;

public class CustomUserDetails implements UserDetails {

    private final Usuario usuario;

    public CustomUserDetails(Usuario usuario) {
        this.usuario = usuario;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Convertir roles a GrantedAuthority
        return usuario.getRoles().stream()
                .map(rol -> new SimpleGrantedAuthority("ROLE_" + rol.name()))
                .collect(Collectors.toList());
    }

    @Override
    public String getPassword() {
        return usuario.getPassword();
    }

    @Override
    public String getUsername() {
        return usuario.getEmail(); // Usamos email como identificador
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // Personaliza según lógica de tu negocio
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // Personaliza según lógica de tu negocio
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // Personaliza según lógica de tu negocio
    }

    @Override
    public boolean isEnabled() {
        return true; // Personaliza según lógica de tu negocio
    }

    public Usuario getUsuario() {
        return usuario;
    }
}
```

---

#### **Paso 2: Crear un `UserDetailsService` personalizado**
Este servicio carga la información del usuario desde tu base de datos.

#### Ejemplo: Clase `CustomUserDetailsService`

```java
package daw2a.gestionbiblioteca.security;

import daw2a.gestionbiblioteca.entities.Usuario;
import daw2a.gestionbiblioteca.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    @Autowired
    public CustomUserDetailsService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findUsuarioByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con email: " + email));
        return new CustomUserDetails(usuario);
    }
}
```

---

#### **Paso 3: Configurar Spring Security para usar `UserDetailsService`**
Asegúrate de que `SecurityConfig` esté configurado para usar tu servicio personalizado.

```java
@Override
protected void configure(AuthenticationManagerBuilder auth) throws Exception {
    auth.userDetailsService(customUserDetailsService).passwordEncoder(passwordEncoder());
}
```

---

### **Ventajas de este enfoque**

1. **Escalabilidad**:
   - Si necesitas añadir autenticación por otro método (e.g., OAuth2, LDAP), no tendrás que cambiar tu modelo de dominio.

2. **Cumplimiento con Spring Security**:
   - El uso de `UserDetails` y `UserDetailsService` asegura que sigues las mejores prácticas de Spring.

3. **Reutilización**:
   - Puedes usar `CustomUserDetails` en cualquier parte del código para obtener información del usuario autenticado.

---
### **¿Debe `CustomUserDetails` ser una entidad?**

No, **`CustomUserDetails` no necesita ser una entidad**. Es simplemente una representación específica para que Spring Security maneje los datos del usuario durante el proceso de autenticación y autorización.

- **`Usuario`** es tu entidad persistente mapeada a la base de datos mediante JPA.
- **`CustomUserDetails`** es una implementación de la interfaz `UserDetails` de Spring Security que mapea datos de `Usuario` al modelo de seguridad de Spring.

---

### **Cuándo usar `Usuario` y cuándo usar `CustomUserDetails`**

1. **Usar `Usuario`**:
   - Cuando trabajas con datos relacionados con el dominio de negocio de tu aplicación (e.g., registrar, actualizar o consultar información del usuario desde la base de datos).
   - En servicios de la aplicación, como manejar lógica de negocio.

   **Ejemplo**:
   - Registrar un nuevo usuario en la base de datos.
   - Consultar todos los usuarios con paginación.
   - Actualizar un perfil de usuario.

   ```java
   Usuario usuario = usuarioRepository.findById(id).orElseThrow(...);
   ```

---

2. **Usar `CustomUserDetails`**:
   - Exclusivamente cuando trabajas con Spring Security para tareas de autenticación y autorización.
   - Este objeto es utilizado por Spring Security después de que un usuario se autentique con éxito.
   - Cuando necesitas verificar roles o privilegios de un usuario autenticado.

   **Ejemplo**:
   - Recuperar información del usuario autenticado mediante `SecurityContextHolder`.

   ```java
   Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
   CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
   Usuario usuarioAutenticado = userDetails.getUsuario();
   ```

---

### **Por qué separarlos**
Separar `Usuario` y `CustomUserDetails` proporciona:

1. **Desacoplamiento del modelo de negocio y la seguridad**:
   - Mantienes limpia la lógica de tu dominio sin mezclarla con detalles específicos de seguridad.

2. **Flexibilidad para cambiar estrategias de autenticación**:
   - Si cambias de autenticación basada en JWT a OAuth2 o LDAP, solo necesitarás actualizar `CustomUserDetails`, no la entidad `Usuario`.

3. **Compatibilidad con Spring Security**:
   - Spring Security espera una implementación de `UserDetails` para trabajar correctamente.

---

### **¿Cómo se usan juntos?**

1. Durante la autenticación:
   - Spring llama a `CustomUserDetailsService` para cargar un usuario desde la base de datos (usando la entidad `Usuario`).
   - Convierte el objeto `Usuario` en un objeto `CustomUserDetails`.

2. Durante la autorización:
   - Spring utiliza `CustomUserDetails` para verificar roles y privilegios.
   - Puedes usar `CustomUserDetails` para acceder a información del usuario autenticado cuando sea necesario.

   **Ejemplo**: Acceder al usuario autenticado.

   ```java
   Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
   CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
   String email = userDetails.getUsername(); // Obtener email
   ```

3. En la aplicación:
   - Continuas usando `Usuario` para tareas relacionadas con tu negocio.

---

### **¿Cuándo podrían combinarse?**

Si tu aplicación tiene requisitos simples y no necesitas roles o privilegios avanzados, podrías usar `Usuario` directamente en lugar de crear `CustomUserDetails`. Sin embargo, esto **no es una buena práctica**, ya que acopla la lógica de negocio con la seguridad, dificultando cambios futuros.
---

### **Paso 1: Crear `CustomUserDetails`**

Este será el objeto que mapeará la entidad `Usuario` al modelo esperado por Spring Security.

```java
package daw2a.gestionbiblioteca.security;

import daw2a.gestionbiblioteca.entities.Usuario;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.stream.Collectors;

public class CustomUserDetails implements UserDetails {

    private final Usuario usuario;

    public CustomUserDetails(Usuario usuario) {
        this.usuario = usuario;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return usuario.getRoles().stream()
                .map(rol -> new SimpleGrantedAuthority("ROLE_" + rol.name()))
                .collect(Collectors.toList());
    }

    @Override
    public String getPassword() {
        return usuario.getPassword();
    }

    @Override
    public String getUsername() {
        return usuario.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public Usuario getUsuario() {
        return usuario;
    }
}
```

---

### **Paso 2: Crear `CustomUserDetailsService`**

Este servicio cargará la información del usuario desde la base de datos.

```java
package daw2a.gestionbiblioteca.security;

import daw2a.gestionbiblioteca.entities.Usuario;
import daw2a.gestionbiblioteca.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    @Autowired
    public CustomUserDetailsService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findUsuarioByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con email: " + email));
        return new CustomUserDetails(usuario);
    }
}
```

---

### **Paso 3: Configurar `SecurityConfig`**

Integramos el servicio de usuarios y el encoder de contraseñas en la configuración de seguridad.

```java
package daw2a.gestionbiblioteca.config;

import daw2a.gestionbiblioteca.security.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;

    public SecurityConfig(CustomUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authManager(HttpSecurity http) throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class)
                .authenticationProvider(daoAuthenticationProvider())
                .build();
    }

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/authenticate", "/register").permitAll()
                        .anyRequest().authenticated()
                );
        return http.build();
    }
}
```

---

### **Paso 4: Crear la clase `JwtUtil`**

Esta clase se encargará de generar, extraer y validar los tokens JWT.

```java
package daw2a.gestionbiblioteca.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtUtil {

    private final String SECRET_KEY = "my_secret_key";

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder().setSigningKey(SECRET_KEY.getBytes()).build().parseClaimsJws(token).getBody();
    }

    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public String generateToken(CustomUserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, userDetails.getUsername());
    }

    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY.getBytes())
                .compact();
    }

    public Boolean validateToken(String token, CustomUserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
}
```

---

### **Paso 5: Crear el filtro `JwtRequestFilter`**

Este filtro interceptará cada solicitud para validar el token JWT.

```java
package daw2a.gestionbiblioteca.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        final String authorizationHeader = request.getHeader("Authorization");

        String username = null;
        String jwt = null;

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7);
            username = jwtUtil.extractUsername(jwt);
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

            if (jwtUtil.validateToken(jwt, (CustomUserDetails) userDetails)) {
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        chain.doFilter(request, response);
    }
}
```

---

### **Paso 6: Crear el `AuthController`**

El controlador `AuthController` manejará los endpoints para la autenticación y registro de usuarios.

#### **Código de `AuthController`**

```java
package daw2a.gestionbiblioteca.controllers;

import daw2a.gestionbiblioteca.entities.Usuario;
import daw2a.gestionbiblioteca.security.CustomUserDetails;
import daw2a.gestionbiblioteca.security.JwtUtil;
import daw2a.gestionbiblioteca.services.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UsuarioService usuarioService;
    private final JwtUtil jwtUtil;

    @Autowired
    public AuthController(AuthenticationManager authenticationManager, UsuarioService usuarioService, JwtUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.usuarioService = usuarioService;
        this.jwtUtil = jwtUtil;
    }

    // Endpoint para autenticar y generar JWT
    @PostMapping("/login")
    public ResponseEntity<?> authenticate(@RequestParam String email, @RequestParam String password) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(401).body("Credenciales inválidas");
        }

        final UserDetails userDetails = usuarioService.loadUserByUsername(email);
        final String jwt = jwtUtil.generateToken((CustomUserDetails) userDetails);

        return ResponseEntity.ok().body(jwt);
    }

    // Endpoint para registrar nuevos usuarios
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody @Valid Usuario usuario) {
        try {
            Usuario nuevoUsuario = usuarioService.registrarUsuario(usuario);
            return ResponseEntity.ok(nuevoUsuario);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
```

---

### **Paso 7: Ajustar el `SecurityConfig`**

Asegúrate de que los endpoints de autenticación no requieran autenticación previa y que el filtro JWT se aplique al resto de las rutas.

#### **Ajustes en `SecurityConfig`**

```java
@Bean
public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtRequestFilter jwtRequestFilter) throws Exception {
    http.csrf().disable()
            .authorizeHttpRequests(auth -> auth
                    .requestMatchers("/auth/**").permitAll()
                    .anyRequest().authenticated()
            )
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

    return http.build();
}
```

---

### **Paso 8: Pruebas de autenticación y flujo JWT**

#### **Pruebas Manuales**

- Usar herramientas como Postman o Insomnia para probar los endpoints:
  - **Registro (`/auth/register`)**: Enviar una solicitud POST con un cuerpo JSON válido.
  - **Login (`/auth/login`)**: Enviar las credenciales correctas e incorrectas y validar el JWT recibido.

#### **Pruebas Automatizadas**

Implementar pruebas automatizadas para verificar que:
1. El token se genera correctamente en `/auth/login`.
2. Los usuarios no autenticados no pueden acceder a rutas protegidas.
3. Los usuarios autenticados con un token válido tienen acceso a rutas protegidas.

---

### **Paso 9: Proteger rutas sensibles**

Asegúrate de que las rutas más sensibles (como los endpoints de préstamo o creación de usuarios) estén protegidas mediante roles.

#### **Ajustar Seguridad con Roles**

En `SecurityConfig`, actualiza las rutas protegidas:

```java
.authorizeHttpRequests(auth -> auth
        .requestMatchers("/auth/**").permitAll()
        .requestMatchers("/usuarios/**").hasRole("ADMIN")
        .requestMatchers("/prestamos/**").hasRole("USUARIO")
        .anyRequest().authenticated()
)
```

---

### **Resumen**

1. **Autenticación con JWT**:
   - Implementación de `CustomUserDetails`, `CustomUserDetailsService`, y `JwtUtil`.
   - Configuración de `JwtRequestFilter` para validar tokens.
   - `AuthController` con endpoints para login y registro.

2. **Pruebas y ajustes**:
   - Validar manualmente el flujo de autenticación y roles.
   - Automatizar pruebas para validar autenticación y autorización.

### **Pruebas después de implementar autenticación con JWT**

Para asegurar que la autenticación JWT funciona correctamente, debemos realizar una serie de pruebas automatizadas. Estas pruebas cubrirán el flujo de autenticación y la protección de rutas.

---

### **1. Pruebas Unitarias para `JwtUtil`**

#### **Prueba de generación y validación de tokens**

```java
package daw2a.gestionbiblioteca.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
    }

    @Test
    void testGenerateTokenAndValidate() {
        UserDetails userDetails = User.withUsername("test@example.com").password("password").roles("USER").build();
        String token = jwtUtil.generateToken(userDetails);

        assertNotNull(token);
        assertEquals("test@example.com", jwtUtil.extractUsername(token));
        assertFalse(jwtUtil.isTokenExpired(token));
        assertTrue(jwtUtil.validateToken(token, userDetails));
    }

    @Test
    void testExpiredToken() {
        UserDetails userDetails = User.withUsername("test@example.com").password("password").roles("USER").build();
        String token = jwtUtil.generateToken(userDetails);

        // Manipular la fecha de expiración para simular un token vencido
        jwtUtil.setExpirationDate(new Date(System.currentTimeMillis() - 1000));

        assertFalse(jwtUtil.validateToken(token, userDetails));
    }
}
```

---

### **2. Pruebas de Integración para `AuthController`**

#### **Prueba de registro y autenticación**

Usamos **MockMvc** para probar los endpoints del controlador:

```java
package daw2a.gestionbiblioteca.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import daw2a.gestionbiblioteca.entities.Usuario;
import daw2a.gestionbiblioteca.security.JwtUtil;
import daw2a.gestionbiblioteca.services.UsuarioService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtUtil jwtUtil;

    @Test
    void testRegister() throws Exception {
        Usuario usuario = new Usuario();
        usuario.setNombre("Test User");
        usuario.setEmail("test@example.com");
        usuario.setPassword("password");

        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(usuario)))
                .andExpect(status().isOk());
    }

    @Test
    void testLogin() throws Exception {
        String email = "test@example.com";
        String password = "password";

        mockMvc.perform(post("/auth/login")
                .param("email", email)
                .param("password", password))
                .andExpect(status().isOk())
                .andExpect(content().string(jwtUtil.generateToken(new Usuario())));
    }
}
```

---

### **3. Pruebas para rutas protegidas**

#### **Simular solicitudes con JWT válido**

```java
@Test
void testProtectedEndpointWithValidToken() throws Exception {
    String email = "test@example.com";
    String token = jwtUtil.generateToken(new Usuario(email, "password", "ROLE_USER"));

    mockMvc.perform(get("/usuarios/me")
            .header("Authorization", "Bearer " + token))
            .andExpect(status().isOk());
}

@Test
void testProtectedEndpointWithInvalidToken() throws Exception {
    mockMvc.perform(get("/usuarios/me")
            .header("Authorization", "Bearer invalid_token"))
            .andExpect(status().isUnauthorized());
}
```

---

### **4. Pruebas de roles y autorización**

#### **Probar accesos según roles**

```java
@Test
void testAdminAccess() throws Exception {
    String adminToken = jwtUtil.generateToken(new Usuario("admin@example.com", "password", "ROLE_ADMIN"));

    mockMvc.perform(get("/usuarios")
            .header("Authorization", "Bearer " + adminToken))
            .andExpect(status().isOk());
}

@Test
void testUserAccessToAdminRoute() throws Exception {
    String userToken = jwtUtil.generateToken(new Usuario("user@example.com", "password", "ROLE_USER"));

    mockMvc.perform(get("/usuarios")
            .header("Authorization", "Bearer " + userToken))
            .andExpect(status().isForbidden());
}
```

---

### **Próximos pasos**

1. **Agregar más pruebas específicas**:
   - Validación de campos durante el registro.
   - Manejo de credenciales incorrectas en el login.
   - Pruebas de expiración del token.

2. **Optimizar el flujo de seguridad**:
   - Implementar `@PreAuthorize` o `@Secured` para mayor granularidad.
   - Probar más casos de bordes y errores.


Asegurar rutas específicas en una aplicación con **Spring Security** implica establecer reglas claras de autorización basadas en roles y configuraciones específicas para proteger los endpoints. Te guiaré a través de cómo hacerlo y asegurarte de que tus rutas estén bien protegidas.

---

### **1. Configuración Básica de Seguridad**

#### **Seguridad con Roles en `SecurityConfig`**

Aseguramos las rutas específicas según los roles de los usuarios en la configuración de seguridad. Por ejemplo:

```java
@Bean
public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtRequestFilter jwtRequestFilter) throws Exception {
    http.csrf().disable()
            .authorizeHttpRequests(auth -> auth
                    // Rutas públicas
                    .requestMatchers("/auth/**").permitAll()
                    // Rutas protegidas
                    .requestMatchers("/usuarios/**").hasRole("ADMIN")
                    .requestMatchers("/prestamos/**").hasAnyRole("ADMIN", "USER")
                    .requestMatchers("/libros/**").hasRole("BIBLIOTECARIO")
                    // Otras rutas requieren autenticación
                    .anyRequest().authenticated()
            )
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

    return http.build();
}
```

---

### **2. Uso de `@PreAuthorize` y `@Secured`**

Las anotaciones **`@PreAuthorize`** y **`@Secured`** ofrecen un control más granular de acceso a nivel de método en controladores o servicios.

#### **Ejemplo con `@PreAuthorize`**

```java
@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Usuario>> listarUsuarios() {
        return ResponseEntity.ok(usuarioService.listarTodos());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<Usuario> obtenerUsuario(@PathVariable Long id) {
        return ResponseEntity.ok(usuarioService.obtenerUsuario(id));
    }
}
```

#### **Ejemplo con `@Secured`**

```java
@RestController
@RequestMapping("/libros")
public class LibroController {

    @GetMapping
    @Secured("ROLE_BIBLIOTECARIO")
    public ResponseEntity<List<Libro>> listarLibros() {
        return ResponseEntity.ok(libroService.listarTodos());
    }
}
```

---

### **3. Protección Basada en Condiciones Dinámicas**

#### **Ejemplo con `@PostAuthorize`**

Permite aplicar reglas después de la ejecución de un método, útil para validar la respuesta:

```java
@GetMapping("/{id}")
@PostAuthorize("returnObject.usuario.email == authentication.name")
public ResponseEntity<Prestamo> obtenerPrestamo(@PathVariable Long id) {
    return ResponseEntity.ok(prestamoService.obtenerPrestamo(id));
}
```

En este caso, se asegura que solo el propietario del préstamo pueda acceder a él.

---

### **4. Asegurar Rutas para JWT**

#### **Filtro JWT**

El filtro JWT debe validar el token antes de permitir el acceso a rutas protegidas. Configúralo para interceptar todas las solicitudes excepto las públicas:

```java
@Override
protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
        throws ServletException, IOException {

    final String authorizationHeader = request.getHeader("Authorization");

    String username = null;
    String jwt = null;

    if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
        jwt = authorizationHeader.substring(7);
        username = jwtUtil.extractUsername(jwt);
    }

    if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        if (jwtUtil.validateToken(jwt, userDetails)) {
            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authToken);
        }
    }
    chain.doFilter(request, response);
}
```

---

### **5. Pruebas de Rutas Protegidas**

#### **Probar con roles incorrectos**

- Intentar acceder a rutas restringidas con roles insuficientes:

```java
@Test
void testAccessWithInvalidRole() throws Exception {
    String token = jwtUtil.generateToken(new Usuario("user@example.com", "password", "ROLE_USER"));

    mockMvc.perform(get("/usuarios")
            .header("Authorization", "Bearer " + token))
            .andExpect(status().isForbidden());
}
```

#### **Probar con JWT inválido o ausente**

```java
@Test
void testAccessWithInvalidToken() throws Exception {
    mockMvc.perform(get("/libros"))
            .andExpect(status().isUnauthorized());
}
```

---

### **6. Recomendaciones Finales**

- **Seguridad Granular**: Usa `@PreAuthorize` para condiciones específicas en servicios o controladores.
- **Validación JWT**: Asegúrate de que el filtro JWT esté correctamente configurado y valide tokens antes de acceder a rutas.
- **Pruebas Exhaustivas**: Cubre escenarios como acceso con roles incorrectos, tokens vencidos, y rutas protegidas.
- **Documentación**: Proporciona una lista clara de rutas y los roles necesarios para cada una.


Asegurar los endpoints JWT es fundamental para garantizar el correcto funcionamiento y la seguridad de tu aplicación. El objetivo principal es permitir el acceso a estos endpoints únicamente a usuarios autorizados con JWT válidos. A continuación, se detallan los pasos y las mejores prácticas para asegurar los endpoints JWT en una aplicación **Spring Boot**:

---

### **1. Asegurar los endpoints en `SecurityConfig`**

En tu **`SecurityConfig`**, puedes definir qué endpoints requieren autenticación y autorización. Usa el método `authorizeHttpRequests` para especificar las reglas.

#### Configuración de ejemplo para JWT

```java
@Bean
public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtRequestFilter jwtRequestFilter) throws Exception {
    http.csrf().disable()
        .authorizeHttpRequests(auth -> auth
            // Endpoints públicos (no requieren autenticación)
            .requestMatchers("/auth/**").permitAll()
            .requestMatchers("/public/**").permitAll()

            // Endpoints protegidos (requieren autenticación)
            .requestMatchers("/usuarios/**").authenticated()
            .requestMatchers("/prestamos/**").authenticated()
            .requestMatchers("/libros/**").hasRole("BIBLIOTECARIO")

            // Regla por defecto: cualquier otro endpoint requiere autenticación
            .anyRequest().authenticated()
        )
        .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

    return http.build();
}
```

---

### **2. Usar `@PreAuthorize` y roles**

Puedes agregar las anotaciones **`@PreAuthorize`** o **`@Secured`** en métodos específicos de tus controladores o servicios para aplicar seguridad adicional basada en roles.

#### Ejemplo:

```java
@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Usuario>> listarUsuarios() {
        return ResponseEntity.ok(usuarioService.listarTodos());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<Usuario> obtenerUsuario(@PathVariable Long id) {
        return ResponseEntity.ok(usuarioService.obtenerUsuario(id));
    }
}
```

---

### **3. Validar JWT en el filtro**

El filtro JWT (`JwtRequestFilter`) valida el token antes de permitir el acceso a los endpoints protegidos.

#### Pasos clave en el filtro:

1. Extraer el token del encabezado `Authorization`.
2. Validar el token usando **JwtUtil**.
3. Configurar el contexto de autenticación si el token es válido.

#### Ejemplo de filtro:

```java
@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        final String authorizationHeader = request.getHeader("Authorization");

        String username = null;
        String jwt = null;

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7);
            username = jwtUtil.extractUsername(jwt);
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            if (jwtUtil.validateToken(jwt, userDetails)) {
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        chain.doFilter(request, response);
    }
}
```

---

### **4. Asegurar un tiempo de expiración adecuado para el token**

Asegúrate de que el JWT tenga un tiempo de expiración razonable para reducir el riesgo de que se reutilicen tokens robados indefinidamente. Usa **JwtUtil** para manejar la creación y validación de tokens.

#### Ejemplo de expiración en `JwtUtil`:

```java
public String generateToken(UserDetails userDetails) {
    Map<String, Object> claims = new HashMap<>();
    return createToken(claims, userDetails.getUsername());
}

private String createToken(Map<String, Object> claims, String subject) {
    return Jwts.builder()
            .setClaims(claims)
            .setSubject(subject)
            .setIssuedAt(new Date(System.currentTimeMillis()))
            .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10)) // Token válido por 10 horas
            .signWith(SignatureAlgorithm.HS256, SECRET_KEY.getBytes())
            .compact();
}
```

---

### **5. Manejar accesos no autorizados de forma amigable**

Personaliza tu `AuthenticationEntryPoint` para manejar accesos no autorizados a endpoints protegidos.

#### Ejemplo:

```java
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
            throws IOException, ServletException {
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "No autorizado");
    }
}
```

---

### **6. Probar endpoints protegidos con JWT**

Escribe pruebas de integración para verificar que el JWT funciona como se espera.

#### Ejemplo de prueba:

```java
@Test
void testAccessWithValidToken() throws Exception {
    String token = jwtUtil.generateToken(new Usuario("admin@example.com", "password", "ROLE_ADMIN"));

    mockMvc.perform(get("/usuarios")
            .header("Authorization", "Bearer " + token))
            .andExpect(status().isOk());
}

@Test
void testAccessWithInvalidToken() throws Exception {
    mockMvc.perform(get("/usuarios")
            .header("Authorization", "Bearer invalid_token"))
            .andExpect(status().isUnauthorized());
}
```

---

### **Mejores prácticas para asegurar endpoints JWT**

1. **Usar HTTPS**: Asegúrate de que toda la comunicación API esté encriptada.
2. **Expiración corta de tokens**: Establece un tiempo de expiración razonable y ofrece un mecanismo de renovación si es necesario.
3. **Control de acceso basado en roles**: Usa roles y permisos para restringir el acceso a los endpoints.
4. **Limitar los endpoints públicos**: Define claramente qué endpoints son públicos y restringe el acceso a operaciones sensibles.
5. **Registrar intentos no autorizados**: Mantén un registro de los intentos fallidos de autenticación para auditorías y depuración.

---
