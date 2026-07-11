# Seguridad, Autenticación y Autorización en Spring Boot (v2)

Guía práctica y resumida con ejemplo de API REST protegida con Spring Security + JWT.

## 1. Conceptos Fundamentales
- **Autenticación**: verificar identidad (quién eres). Ej.: login con usuario/contraseña.
- **Autorización**: decidir permisos (qué puedes hacer). Ej.: rol ADMIN puede borrar.
- **Principio de menor privilegio**: dar solo los accesos mínimos necesarios.
- **Seguridad por capas**: combinar controles (filtros, validación, cabeceras, monitoreo).

## 2. Spring Security - Introducción
- Framework de seguridad para la JVM con enfoque en filtros Servlet (`FilterChain`) que llenan un `SecurityContext`.
- Se configura vía beans (desde Spring Security 6) y anota con reglas de acceso por URL o método.
- Dependencia Maven básica:
```xml
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-security</artifactId>
</dependency>
```

## 3. Autenticación
### 3.1 Métodos habituales
- HTTP Basic (útil para pruebas; siempre sobre HTTPS).
- Form login (apps web con sesión).
- JWT (stateless, ideal para APIs REST).
- OAuth2/OIDC (delegar login en Google, GitHub, etc.).
- Stateful (sesión) vs Stateless (tokens en cada request).

### 3.2 Gestión de usuarios
- `UserDetailsService` carga usuarios (in-memory, JDBC, JPA).
- `PasswordEncoder` (BCrypt recomendado).
- Persistencia JPA con entidad `User` y roles.

## 4. Autorización
### 4.1 Control de acceso
- Roles (`ROLE_USER`, `ROLE_ADMIN`) y authorities.
- Anotaciones: `@PreAuthorize`, `@Secured`, `@RolesAllowed`.
- Reglas por URL con `HttpSecurity`.

### 4.2 Niveles
- A nivel endpoint (paths).
- A nivel método (`@EnableMethodSecurity`).
- A nivel datos (filtrar resultados por usuario).

## 5. Configuración Práctica
### 5.1 `SecurityFilterChain`
Bean Java para definir autenticación, autorización, CORS y CSRF.

### 5.2 Protección de endpoints
- Publicar `/auth/**` y proteger `/api/**`.
- CORS: habilitar orígenes permitidos para frontends.
- CSRF: desactivar para APIs stateless (JWT); activar en formularios stateful.

## 6. JWT en Profundidad
- Estructura: `header.payload.signature` (Base64Url).
- Generación: firmar claims (sub, roles, exp) con secreto/clave.
- Validación: verificar firma y expiración.
- Refresh tokens: emitir un token corto + refresh más largo.
- Almacenamiento: en memoria JS (no localStorage para datos sensibles; preferir cookies httpOnly + SameSite=Lax si es viable).
- Expiración corta y rotación.

## 7. Buenas Prácticas
- Nunca guardar contraseñas en texto plano; usar `BCryptPasswordEncoder`.
- HTTPS obligatorio en producción.
- Validar/sanitizar inputs; usar parámetros preparados (JPA lo hace).
- Rate limiting en endpoints de login.
- Logging/auditoría de eventos de seguridad.
- Manejar errores sin filtrar datos sensibles.
- Revisar cabeceras de seguridad.

## 8. Gestión de Sesiones
- APIs JWT: stateless (`sessionCreationPolicy(STATELESS)`).
- Apps web: configurar tamaño y duración de sesión; protección de fijación de sesión habilitada por defecto.
- Control de sesiones concurrentes si aplica.

## 9. Protección contra Vulnerabilidades
- CSRF: desactivar solo en APIs stateless; mantener en formularios.
- XSS: escapar salida; usar `@CrossOrigin` con cuidado.
- SQL Injection: usar repositorios JPA o consultas parametrizadas.
- Clickjacking: `X-Frame-Options`/`frameOptions()`.
- Headers: `X-Content-Type-Options`, `Strict-Transport-Security`, `Content-Security-Policy`.

## 10. Ejemplo Práctico: API REST con JWT
Pequeña API `Book` + `Review` con usuarios y roles.

### 10.1 Dependencias (pom.xml)
```xml
<dependencies>
  <dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-webmvc</artifactId>
  </dependency>
  <dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
  </dependency>
  <dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
  </dependency>
  <dependency>
    <groupId>com.h2database</groupId>
    <artifactId>h2</artifactId>
    <scope>runtime</scope>
  </dependency>
  <dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-api</artifactId>
    <version>0.12.5</version>
  </dependency>
  <dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-impl</artifactId>
    <version>0.12.5</version>
    <scope>runtime</scope>
  </dependency>
  <dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-jackson</artifactId>
    <version>0.12.5</version>
    <scope>runtime</scope>
  </dependency>
</dependencies>
```

### 10.2 Entidades JPA
```java
// User.java
@Entity
public class User {
  @Id @GeneratedValue private Long id;
  private String username;
  private String password;
  private String role; // ROLE_USER / ROLE_ADMIN
}

// Book.java
@Entity
public class Book {
  @Id @GeneratedValue private Long id;
  private String title;
  private String author;
}

// Review.java
@Entity
public class Review {
  @Id @GeneratedValue private Long id;
  private String content;
  @ManyToOne private Book book;
  @ManyToOne private User user;
}
```

### 10.3 Repositorios
```java
public interface UserRepository extends JpaRepository<User, Long> {
  Optional<User> findByUsername(String username);
}
public interface BookRepository extends JpaRepository<Book, Long> {}
public interface ReviewRepository extends JpaRepository<Review, Long> {
  List<Review> findByBookId(Long bookId);
}
```

### 10.4 Servicio de usuarios y carga de credenciales
```java
@Service
public class CustomUserDetailsService implements UserDetailsService {
  private final UserRepository repo;
  private final PasswordEncoder encoder;
  public CustomUserDetailsService(UserRepository repo, PasswordEncoder encoder) {
    this.repo = repo; this.encoder = encoder;
  }
  @Override
  public UserDetails loadUserByUsername(String username) {
    User user = repo.findByUsername(username)
        .orElseThrow(() -> new UsernameNotFoundException("Not found"));
    return org.springframework.security.core.userdetails.User
        .withUsername(user.getUsername())
        .password(user.getPassword())
        .roles(user.getRole().replace("ROLE_", ""))
        .build();
  }
  public User register(String username, String rawPassword, String role) {
    User u = new User();
    u.setUsername(username);
    u.setPassword(encoder.encode(rawPassword));
    u.setRole(role);
    return repo.save(u);
  }
}
```

### 10.5 Utilidades JWT (simplificado)
```java
@Component
public class JwtService {
  private final String secret = "super-secreto-demo-12345678901234567890";
  private final long expirationMs = 3600_000;

  public String generateToken(String username, Collection<String> roles) {
    Instant now = Instant.now();
    return Jwts.builder()
        .subject(username)
        .claim("roles", roles)
        .issuedAt(Date.from(now))
        .expiration(Date.from(now.plusMillis(expirationMs)))
        .signWith(Keys.hmacShaKeyFor(secret.getBytes()), Jwts.SIG.HS256)
        .compact();
  }

  public Jws<Claims> parse(String token) {
    return Jwts.parser()
        .verifyWith(Keys.hmacShaKeyFor(secret.getBytes()))
        .build()
        .parseSignedClaims(token);
  }
}
```

### 10.6 Filtro JWT
```java
@Component
public class JwtAuthFilter extends OncePerRequestFilter {
  private final JwtService jwtService;
  private final UserDetailsService uds;

  public JwtAuthFilter(JwtService jwtService, UserDetailsService uds) {
    this.jwtService = jwtService; this.uds = uds;
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
      throws ServletException, IOException {
    String header = request.getHeader("Authorization");
    if (header != null && header.startsWith("Bearer ")) {
      try {
        String token = header.substring(7);
        Claims claims = jwtService.parse(token).getPayload();
        String username = claims.getSubject();
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
          UserDetails user = uds.loadUserByUsername(username);
          UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
              user, null, user.getAuthorities());
          auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
          SecurityContextHolder.getContext().setAuthentication(auth);
        }
      } catch (JwtException ex) {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        return;
      }
    }
    chain.doFilter(request, response);
  }
}
```

### 10.7 Configuración de seguridad
```java
@Configuration
@EnableMethodSecurity
public class SecurityConfig {
  private final JwtAuthFilter jwtAuthFilter;

  public SecurityConfig(JwtAuthFilter jwtAuthFilter) {
    this.jwtAuthFilter = jwtAuthFilter;
  }

  @Bean
  SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    return http
        .csrf(csrf -> csrf.disable())
        .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/auth/**").permitAll()
            .requestMatchers(HttpMethod.GET, "/api/books/**").permitAll()
            .anyRequest().authenticated()
        )
        .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
        .build();
  }

  @Bean PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }
}
```

### 10.8 Controladores
```java
@RestController
@RequestMapping("/auth")
public class AuthController {
  private final AuthenticationManager authManager;
  private final JwtService jwtService;
  private final CustomUserDetailsService users;

  public AuthController(AuthenticationManager authManager, JwtService jwtService, CustomUserDetailsService users) {
    this.authManager = authManager; this.jwtService = jwtService; this.users = users;
  }

  @PostMapping("/register")
  public User register(@RequestBody AuthRequest request) {
    return users.register(request.username(), request.password(), "ROLE_USER");
  }

  @PostMapping("/login")
  public TokenResponse login(@RequestBody AuthRequest request) {
    Authentication auth = authManager.authenticate(
        new UsernamePasswordAuthenticationToken(request.username(), request.password()));
    UserDetails user = (UserDetails) auth.getPrincipal();
    String token = jwtService.generateToken(user.getUsername(),
        user.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList());
    return new TokenResponse(token);
  }
}

@RestController
@RequestMapping("/api/books")
public class BookController {
  private final BookRepository books;
  public BookController(BookRepository books) { this.books = books; }

  @GetMapping public List<Book> all() { return books.findAll(); }

  @PreAuthorize("hasRole('ADMIN')")
  @PostMapping public Book create(@RequestBody Book b) { return books.save(b); }
}

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {
  private final ReviewRepository reviews;
  private final BookRepository books;
  public ReviewController(ReviewRepository reviews, BookRepository books) {
    this.reviews = reviews; this.books = books;
  }

  @PreAuthorize("hasRole('USER')")
  @PostMapping
  public Review create(@RequestBody Review r, Authentication auth) {
    r.setUser(new User(null, auth.getName(), null, "ROLE_USER"));
    return reviews.save(r);
  }

  @GetMapping("/book/{bookId}")
  public List<Review> byBook(@PathVariable Long bookId) {
    return reviews.findByBookId(bookId);
  }
}
```

### 10.9 DTOs
```java
public record AuthRequest(String username, String password) {}
public record TokenResponse(String token) {}
```

### 10.10 Datos iniciales (H2 en memoria)
En `application.yml`:
```yaml
spring:
  datasource:
    url: jdbc:h2:mem:demo;DB_CLOSE_DELAY=-1
    driverClassName: org.h2.Driver
  jpa:
    hibernate:
      ddl-auto: update
    properties:
      hibernate:
        format_sql: true
  h2:
    console:
      enabled: true
```

### 10.11 Flujo de uso
1. POST `/auth/register` con usuario y contraseña.
2. POST `/auth/login` para obtener JWT.
3. Incluir header `Authorization: Bearer <token>` en llamadas a `/api/**`.
4. GET `/api/books` es público; POST `/api/books` requiere `ROLE_ADMIN`; POST `/api/reviews` requiere `ROLE_USER`.

## 11. Testing de Seguridad
- `@WithMockUser` y `@WithUserDetails` para simular identidades.
- `MockMvc` + `SecurityMockMvcRequestPostProcessors.jwt()` para pruebas JWT.
- Probar reglas de acceso (403 esperado) y éxito (200/201).

## 12. Configuración para Producción
- Secretos en variables de entorno (no en el código).
- Rotación de claves y expiraciones cortas.
- HTTPS con HSTS.
- Logs y auditoría centralizada.
- Mantener dependencias al día (Spring Boot/Security LTS).

## Apéndice: Endpoints de ejemplo (cURL)
```bash
curl -X POST http://localhost:8080/auth/register -H "Content-Type: application/json" \
  -d '{"username":"alice","password":"1234"}'

TOKEN=$(curl -s -X POST http://localhost:8080/auth/login -H "Content-Type: application/json" \
  -d '{"username":"alice","password":"1234"}' | jq -r .token)

curl -H "Authorization: Bearer $TOKEN" http://localhost:8080/api/books
```
