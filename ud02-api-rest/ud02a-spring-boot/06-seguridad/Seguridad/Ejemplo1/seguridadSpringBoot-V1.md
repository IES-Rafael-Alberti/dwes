# Seguridad, Autenticación y Autorización en Spring Boot

## 📋 Índice
1. [Conceptos Fundamentales](#1-conceptos-fundamentales)
2. [Spring Security - Introducción](#2-spring-security---introducción)
3. [Autenticación](#3-autenticación)
4. [Autorización](#4-autorización)
5. [Configuración Práctica](#5-configuración-práctica)
6. [JWT en Profundidad](#6-jwt-en-profundidad)
7. [Buenas Prácticas de Seguridad](#7-buenas-prácticas-de-seguridad)
8. [Gestión de Sesiones](#8-gestión-de-sesiones)
9. [Protección contra Vulnerabilidades](#9-protección-contra-vulnerabilidades)
10. [Casos de Uso Prácticos](#10-casos-de-uso-prácticos)
11. [Testing de Seguridad](#11-testing-de-seguridad)
12. [Configuración para Producción](#12-configuración-para-producción)

---

## 1. Conceptos Fundamentales

### 1.1 Autenticación vs Autorización

**Autenticación** (Authentication): Proceso de verificar **quién eres**
- Validar credenciales (usuario/contraseña, token, certificado)
- Establecer la identidad del usuario
- Ejemplo: Login con usuario y contraseña

**Autorización** (Authorization): Proceso de verificar **qué puedes hacer**
- Determinar permisos y accesos
- Basado en roles o permisos específicos
- Ejemplo: Solo ADMIN puede eliminar usuarios

### 1.2 Principios de Seguridad

#### Principio de Menor Privilegio
- Dar solo los permisos mínimos necesarios
- Reducir el impacto de posibles compromisos
- Revisar y actualizar permisos regularmente

#### Defensa en Profundidad (Defense in Depth)
- Múltiples capas de seguridad
- Si una capa falla, otras protegen
- Ejemplos: validación frontend + backend, firewall + autenticación

#### No Confiar en el Cliente
- Nunca confiar en datos del frontend
- Validar y sanitizar todo en el backend
- Verificar permisos en cada operación

---

## 2. Spring Security - Introducción

### 2.1 ¿Qué es Spring Security?

Spring Security es el framework estándar de facto para asegurar aplicaciones Spring Boot. Proporciona:
- Autenticación y autorización
- Protección contra ataques comunes
- Integración con múltiples proveedores de identidad
- Soporte para OAuth2, JWT, LDAP, etc.

### 2.2 Dependencias

#### Maven
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>

<!-- Para JWT -->
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
```

#### Gradle
```gradle
implementation 'org.springframework.boot:spring-boot-starter-security'
implementation 'io.jsonwebtoken:jjwt-api:0.12.5'
runtimeOnly 'io.jsonwebtoken:jjwt-impl:0.12.5'
runtimeOnly 'io.jsonwebtoken:jjwt-jackson:0.12.5'
```

### 2.3 Arquitectura Básica

```
Cliente → SecurityFilterChain → Controller
              ↓
         SecurityContext
              ↓
         Authentication
```

**Componentes Principales:**
- **SecurityFilterChain**: Cadena de filtros que procesan las peticiones
- **SecurityContext**: Almacena información de seguridad del usuario actual
- **Authentication**: Objeto que representa la autenticación
- **UserDetailsService**: Carga información del usuario

---

## 3. Autenticación

### 3.1 Métodos de Autenticación

#### 3.1.1 Basic Authentication

Envía credenciales en cada petición (codificadas en Base64).

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .anyRequest().authenticated()
            )
            .httpBasic(Customizer.withDefaults());
        
        return http.build();
    }
}
```

**Ventajas:**
- Sencillo de implementar
- No requiere sesiones

**Desventajas:**
- Las credenciales viajan en cada petición
- Requiere HTTPS obligatoriamente
- No recomendado para aplicaciones web modernas

#### 3.1.2 Form-Based Authentication

Login tradicional con formulario HTML.

```java
@Bean
public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/login", "/register", "/public/**").permitAll()
            .anyRequest().authenticated()
        )
        .formLogin(form -> form
            .loginPage("/login")
            .loginProcessingUrl("/perform-login")
            .defaultSuccessUrl("/dashboard", true)
            .failureUrl("/login?error=true")
        )
        .logout(logout -> logout
            .logoutUrl("/logout")
            .logoutSuccessUrl("/login?logout=true")
            .deleteCookies("JSESSIONID")
        );
    
    return http.build();
}
```

**Casos de uso:**
- Aplicaciones web tradicionales
- Interfaces de administración
- Sistemas internos

#### 3.1.3 JWT (JSON Web Tokens) ⭐

El método más usado para APIs REST modernas. Ver sección completa en [JWT en Profundidad](#6-jwt-en-profundidad).

#### 3.1.4 OAuth2 / OpenID Connect

Autenticación delegada con proveedores externos.

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-oauth2-client</artifactId>
</dependency>
```

```yaml
spring:
  security:
    oauth2:
      client:
        registration:
          google:
            client-id: ${GOOGLE_CLIENT_ID}
            client-secret: ${GOOGLE_CLIENT_SECRET}
            scope: profile, email
          github:
            client-id: ${GITHUB_CLIENT_ID}
            client-secret: ${GITHUB_CLIENT_SECRET}
            scope: user:email
```

```java
@Bean
public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/", "/login", "/error").permitAll()
            .anyRequest().authenticated()
        )
        .oauth2Login(oauth -> oauth
            .loginPage("/login")
            .defaultSuccessUrl("/dashboard", true)
        );
    
    return http.build();
}
```

### 3.2 Gestión de Usuarios

#### 3.2.1 UserDetailsService

Interfaz para cargar información del usuario.

```java
@Service
public class CustomUserDetailsService implements UserDetailsService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Override
    public UserDetails loadUserByUsername(String username) 
            throws UsernameNotFoundException {
        
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> 
                new UsernameNotFoundException("Usuario no encontrado: " + username));
        
        return org.springframework.security.core.userdetails.User
            .withUsername(user.getUsername())
            .password(user.getPassword())
            .authorities(user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toList()))
            .accountExpired(!user.isAccountNonExpired())
            .accountLocked(!user.isAccountNonLocked())
            .credentialsExpired(!user.isCredentialsNonExpired())
            .disabled(!user.isEnabled())
            .build();
    }
}
```

#### 3.2.2 PasswordEncoder

**NUNCA** guardar contraseñas en texto plano.

```java
@Configuration
public class SecurityConfig {
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // Recomendado
        // return new Argon2PasswordEncoder(); // Alternativa más moderna
    }
}
```

**Uso al registrar usuarios:**
```java
@Service
public class UserService {
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private UserRepository userRepository;
    
    public User registerUser(UserRegistrationDto dto) {
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(passwordEncoder.encode(dto.getPassword())); // ✅
        user.setEmail(dto.getEmail());
        
        return userRepository.save(user);
    }
}
```

#### 3.2.3 Entidades de Usuario

```java
@Entity
@Table(name = "users")
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String username;
    
    @Column(nullable = false)
    private String password; // Hash, nunca texto plano
    
    @Column(unique = true, nullable = false)
    private String email;
    
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "user_roles",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();
    
    private boolean enabled = true;
    private boolean accountNonExpired = true;
    private boolean accountNonLocked = true;
    private boolean credentialsNonExpired = true;
    
    // Getters y Setters
}

@Entity
@Table(name = "roles")
public class Role {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String name; // ROLE_USER, ROLE_ADMIN, etc.
    
    // Getters y Setters
}
```

---

## 4. Autorización

### 4.1 Roles vs Authorities

**Roles**: Agrupaciones de permisos
- Prefijo convencional: `ROLE_`
- Ejemplos: `ROLE_USER`, `ROLE_ADMIN`, `ROLE_MODERATOR`

**Authorities**: Permisos específicos
- Ejemplos: `READ_PRIVILEGES`, `WRITE_PRIVILEGES`, `DELETE_PRIVILEGES`

```java
// Rol
new SimpleGrantedAuthority("ROLE_ADMIN")

// Authority
new SimpleGrantedAuthority("DELETE_USER")
```

### 4.2 Control de Acceso por URL

```java
@Bean
public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
        .authorizeHttpRequests(auth -> auth
            // Público
            .requestMatchers("/", "/home", "/register", "/api/public/**").permitAll()
            
            // Solo autenticados
            .requestMatchers("/profile/**").authenticated()
            
            // Por rol
            .requestMatchers("/admin/**").hasRole("ADMIN")
            .requestMatchers("/api/users/**").hasAnyRole("ADMIN", "MODERATOR")
            
            // Por authority
            .requestMatchers(HttpMethod.DELETE, "/api/**").hasAuthority("DELETE_PRIVILEGE")
            
            // Por defecto, requiere autenticación
            .anyRequest().authenticated()
        );
    
    return http.build();
}
```

### 4.3 Autorización a Nivel de Método

#### Habilitar Method Security

```java
@Configuration
@EnableMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class MethodSecurityConfig {
    // Configuración adicional si es necesaria
}
```

#### Anotaciones de Seguridad

**@PreAuthorize** - Evaluar antes de ejecutar el método:
```java
@Service
public class UserService {
    
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteUser(Long userId) {
        // Solo ADMIN puede ejecutar esto
    }
    
    @PreAuthorize("hasAuthority('WRITE_PRIVILEGE')")
    public User updateUser(User user) {
        // Requiere permiso específico
    }
    
    @PreAuthorize("#username == authentication.principal.username or hasRole('ADMIN')")
    public User getUser(String username) {
        // Solo el propio usuario o ADMIN puede ver el perfil
    }
    
    @PreAuthorize("@userSecurity.canAccessUser(authentication, #userId)")
    public User getUserDetails(Long userId) {
        // Lógica personalizada en bean userSecurity
    }
}
```

**@PostAuthorize** - Evaluar después de ejecutar (filtra resultado):
```java
@PostAuthorize("returnObject.username == authentication.principal.username")
public User getUserById(Long id) {
    // El usuario solo puede ver su propia información
}
```

**@Secured** - Más simple, solo roles:
```java
@Secured("ROLE_ADMIN")
public void adminOperation() {
    // Solo ADMIN
}

@Secured({"ROLE_ADMIN", "ROLE_MODERATOR"})
public void moderationOperation() {
    // ADMIN o MODERATOR
}
```

**@RolesAllowed** - Estándar JSR-250:
```java
@RolesAllowed("ADMIN")
public void deleteContent() {
    // Solo ADMIN
}
```

### 4.4 Seguridad Personalizada con Beans

```java
@Component("userSecurity")
public class UserSecurity {
    
    @Autowired
    private UserRepository userRepository;
    
    public boolean canAccessUser(Authentication authentication, Long userId) {
        String username = authentication.getName();
        User user = userRepository.findById(userId).orElse(null);
        
        if (user == null) return false;
        
        // El usuario puede acceder a sus propios datos
        if (user.getUsername().equals(username)) return true;
        
        // O si es ADMIN
        return authentication.getAuthorities().stream()
            .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
    }
    
    public boolean isOwnerOrAdmin(Authentication authentication, Long resourceId) {
        // Lógica personalizada para verificar propiedad del recurso
        return true; // Implementar según necesidad
    }
}
```

**Uso:**
```java
@PreAuthorize("@userSecurity.canAccessUser(authentication, #userId)")
public User getUserProfile(Long userId) {
    return userRepository.findById(userId).orElseThrow();
}
```

---

## 5. Configuración Práctica

### 5.1 SecurityFilterChain Completo

```java
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    
    @Autowired
    private JwtAuthenticationFilter jwtAuthFilter;
    
    @Autowired
    private AuthenticationProvider authenticationProvider;
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // Desactivar para APIs REST con JWT
            .cors(Customizer.withDefaults()) // Habilitar CORS
            
            .authorizeHttpRequests(auth -> auth
                // Endpoints públicos
                .requestMatchers(
                    "/api/auth/**",
                    "/api/public/**",
                    "/swagger-ui/**",
                    "/v3/api-docs/**"
                ).permitAll()
                
                // Endpoints protegidos
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                .requestMatchers("/api/user/**").hasAnyRole("USER", "ADMIN")
                
                // Todos los demás requieren autenticación
                .anyRequest().authenticated()
            )
            
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // Para JWT
            )
            
            .authenticationProvider(authenticationProvider)
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
            
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint(
                    (request, response, authException) -> {
                        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                        response.setContentType("application/json");
                        response.getWriter().write(
                            "{\"error\": \"No autorizado\", \"message\": \"" 
                            + authException.getMessage() + "\"}"
                        );
                    }
                )
            );
        
        return http.build();
    }
    
    @Bean
    public AuthenticationProvider authenticationProvider(
            UserDetailsService userDetailsService,
            PasswordEncoder passwordEncoder) {
        
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }
    
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
```

### 5.2 Configuración de CORS

```java
@Configuration
public class CorsConfig {
    
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        configuration.setAllowedOrigins(Arrays.asList(
            "http://localhost:3000",  // React/Vue frontend
            "http://localhost:4200",  // Angular frontend
            "https://miapp.com"       // Producción
        ));
        
        configuration.setAllowedMethods(Arrays.asList(
            "GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"
        ));
        
        configuration.setAllowedHeaders(Arrays.asList(
            "Authorization",
            "Content-Type",
            "X-Requested-With"
        ));
        
        configuration.setExposedHeaders(Arrays.asList(
            "Authorization",
            "X-Total-Count"
        ));
        
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = 
            new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        
        return source;
    }
}
```

### 5.3 CSRF Protection

**Para aplicaciones web tradicionales (con sesiones):**
```java
http.csrf(csrf -> csrf
    .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
);
```

**Para APIs REST con JWT (sin sesiones):**
```java
http.csrf(csrf -> csrf.disable());
```

---

## 6. JWT en Profundidad

### 6.1 Estructura de un JWT

Un JWT consta de tres partes separadas por puntos:

```
header.payload.signature
```

**Ejemplo:**
```
eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJqb2huZG9lIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c
```

**Header** (Algoritmo y tipo):
```json
{
  "alg": "HS256",
  "typ": "JWT"
}
```

**Payload** (Claims - Datos):
```json
{
  "sub": "johndoe",
  "name": "John Doe",
  "roles": ["ROLE_USER"],
  "iat": 1516239022,
  "exp": 1516242622
}
```

**Signature** (Firma para verificar integridad):
```
HMACSHA256(
  base64UrlEncode(header) + "." + base64UrlEncode(payload),
  secret
)
```

### 6.2 Servicio JWT

```java
@Service
public class JwtService {
    
    @Value("${jwt.secret}")
    private String SECRET_KEY;
    
    @Value("${jwt.expiration}")
    private long jwtExpiration; // en milisegundos (ej: 86400000 = 24h)
    
    @Value("${jwt.refresh-expiration}")
    private long refreshExpiration;
    
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }
    
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }
    
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> extraClaims = new HashMap<>();
        
        // Agregar roles/authorities
        extraClaims.put("roles", userDetails.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .collect(Collectors.toList()));
        
        return generateToken(extraClaims, userDetails);
    }
    
    public String generateToken(
            Map<String, Object> extraClaims,
            UserDetails userDetails) {
        
        return buildToken(extraClaims, userDetails, jwtExpiration);
    }
    
    public String generateRefreshToken(UserDetails userDetails) {
        return buildToken(new HashMap<>(), userDetails, refreshExpiration);
    }
    
    private String buildToken(
            Map<String, Object> extraClaims,
            UserDetails userDetails,
            long expiration) {
        
        return Jwts.builder()
            .claims(extraClaims)
            .subject(userDetails.getUsername())
            .issuedAt(new Date(System.currentTimeMillis()))
            .expiration(new Date(System.currentTimeMillis() + expiration))
            .signWith(getSignInKey(), Jwts.SIG.HS256)
            .compact();
    }
    
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }
    
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }
    
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
    
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
            .verifyWith(getSignInKey())
            .build()
            .parseSignedClaims(token)
            .getPayload();
    }
    
    private SecretKey getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
```

### 6.3 Filtro JWT

```java
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    
    @Autowired
    private JwtService jwtService;
    
    @Autowired
    private UserDetailsService userDetailsService;
    
    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {
        
        final String authHeader = request.getHeader("Authorization");
        
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }
        
        try {
            final String jwt = authHeader.substring(7);
            final String username = jwtService.extractUsername(jwt);
            
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                
                if (jwtService.isTokenValid(jwt, userDetails)) {
                    UsernamePasswordAuthenticationToken authToken = 
                        new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                        );
                    
                    authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                    );
                    
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
            
        } catch (Exception e) {
            logger.error("Cannot set user authentication: {}", e);
        }
        
        filterChain.doFilter(request, response);
    }
}
```

### 6.4 Controlador de Autenticación

```java
@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {
    
    @Autowired
    private AuthenticationManager authenticationManager;
    
    @Autowired
    private UserDetailsService userDetailsService;
    
    @Autowired
    private JwtService jwtService;
    
    @Autowired
    private UserService userService;
    
    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(
            @Valid @RequestBody RegisterRequest request) {
        
        User user = userService.registerUser(request);
        
        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());
        String accessToken = jwtService.generateToken(userDetails);
        String refreshToken = jwtService.generateRefreshToken(userDetails);
        
        return ResponseEntity.ok(AuthenticationResponse.builder()
            .accessToken(accessToken)
            .refreshToken(refreshToken)
            .build());
    }
    
    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(
            @Valid @RequestBody LoginRequest request) {
        
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                request.getUsername(),
                request.getPassword()
            )
        );
        
        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getUsername());
        String accessToken = jwtService.generateToken(userDetails);
        String refreshToken = jwtService.generateRefreshToken(userDetails);
        
        return ResponseEntity.ok(AuthenticationResponse.builder()
            .accessToken(accessToken)
            .refreshToken(refreshToken)
            .build());
    }
    
    @PostMapping("/refresh")
    public ResponseEntity<AuthenticationResponse> refreshToken(
            @RequestBody RefreshTokenRequest request) {
        
        String refreshToken = request.getRefreshToken();
        String username = jwtService.extractUsername(refreshToken);
        
        if (username != null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            
            if (jwtService.isTokenValid(refreshToken, userDetails)) {
                String accessToken = jwtService.generateToken(userDetails);
                
                return ResponseEntity.ok(AuthenticationResponse.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .build());
            }
        }
        
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
}
```

### 6.5 DTOs de Autenticación

```java
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {
    
    @NotBlank(message = "El username es obligatorio")
    @Size(min = 3, max = 20)
    private String username;
    
    @NotBlank(message = "El email es obligatorio")
    @Email(message = "Email inválido")
    private String email;
    
    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
    private String password;
}

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {
    
    @NotBlank
    private String username;
    
    @NotBlank
    private String password;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthenticationResponse {
    private String accessToken;
    private String refreshToken;
    private String tokenType = "Bearer";
}

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RefreshTokenRequest {
    @NotBlank
    private String refreshToken;
}
```

### 6.6 Configuración de Propiedades

```yaml
# application.yml
jwt:
  secret: ${JWT_SECRET:404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970}
  expiration: 86400000      # 24 horas en milisegundos
  refresh-expiration: 604800000  # 7 días en milisegundos
```

**Generar una clave secreta segura:**
```java
// Método para generar una clave de 256 bits
public static String generateSecretKey() {
    SecretKey key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    return Encoders.BASE64.encode(key.getEncoded());
}
```

### 6.7 Almacenamiento en el Frontend

**LocalStorage** (más común pero menos seguro):
```javascript
// Guardar
localStorage.setItem('accessToken', token);

// Recuperar
const token = localStorage.getItem('accessToken');

// Eliminar
localStorage.removeItem('accessToken');
```

**SessionStorage** (más seguro, se pierde al cerrar pestaña):
```javascript
sessionStorage.setItem('accessToken', token);
```

**Cookies HttpOnly** (más seguro, requiere configuración backend):
```java
Cookie cookie = new Cookie("accessToken", token);
cookie.setHttpOnly(true);
cookie.setSecure(true); // Solo HTTPS
cookie.setPath("/");
cookie.setMaxAge(24 * 60 * 60); // 24 horas
response.addCookie(cookie);
```

---

## 7. Buenas Prácticas de Seguridad

### 7.1 Gestión de Contraseñas

✅ **SÍ hacer:**
- Usar BCrypt, Argon2 o PBKDF2 para hashear
- Validar complejidad de contraseñas
- Implementar política de expiración
- Permitir recuperación segura
- Implementar rate limiting en login

❌ **NO hacer:**
- Guardar contraseñas en texto plano
- Usar algoritmos débiles (MD5, SHA1)
- Enviar contraseñas por email
- Permitir contraseñas débiles
- Almacenar contraseñas en logs

```java
@Component
public class PasswordValidator {
    
    private static final String PASSWORD_PATTERN = 
        "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$";
    
    private static final Pattern pattern = Pattern.compile(PASSWORD_PATTERN);
    
    public boolean isValid(String password) {
        if (password == null) return false;
        
        Matcher matcher = pattern.matcher(password);
        return matcher.matches();
    }
    
    // Al menos 8 caracteres
    // Al menos un dígito
    // Al menos una minúscula
    // Al menos una mayúscula
    // Al menos un carácter especial
    // Sin espacios
}
```

### 7.2 Validación de Entrada

```java
@RestController
@RequestMapping("/api/users")
@Validated
public class UserController {
    
    @PostMapping
    public ResponseEntity<User> createUser(
            @Valid @RequestBody UserDto userDto) {
        
        // Spring valida automáticamente con @Valid
        User user = userService.createUser(userDto);
        return ResponseEntity.ok(user);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<User> getUser(
            @PathVariable @Min(1) Long id) {
        
        // Validación de parámetros
        User user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }
}

@Data
public class UserDto {
    
    @NotBlank
    @Size(min = 3, max = 20)
    @Pattern(regexp = "^[a-zA-Z0-9_]+$")
    private String username;
    
    @NotBlank
    @Email
    private String email;
    
    @NotNull
    @Min(18)
    @Max(120)
    private Integer age;
}
```

### 7.3 Protección XSS

```java
// Sanitizar HTML
@Component
public class XssProtection {
    
    public String sanitize(String input) {
        if (input == null) return null;
        
        return input.replaceAll("<", "&lt;")
                   .replaceAll(">", "&gt;")
                   .replaceAll("\"", "&quot;")
                   .replaceAll("'", "&#x27;")
                   .replaceAll("/", "&#x2F;");
    }
}

// O usar librería OWASP Java HTML Sanitizer
@Component
public class HtmlSanitizer {
    
    private static final PolicyFactory POLICY = Sanitizers.FORMATTING
        .and(Sanitizers.LINKS);
    
    public String sanitize(String html) {
        return POLICY.sanitize(html);
    }
}
```

### 7.4 Headers de Seguridad

```java
@Bean
public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
        .headers(headers -> headers
            .xssProtection(xss -> xss.disable()) // Moderno: CSP es mejor
            .contentSecurityPolicy(csp -> csp
                .policyDirectives("default-src 'self'; " +
                                "script-src 'self' 'unsafe-inline'; " +
                                "style-src 'self' 'unsafe-inline'; " +
                                "img-src 'self' data: https:;")
            )
            .frameOptions(frame -> frame.deny()) // Prevenir Clickjacking
            .contentTypeOptions(Customizer.withDefaults()) // X-Content-Type-Options: nosniff
            .referrerPolicy(referrer -> referrer
                .policy(ReferrerPolicyHeaderWriter.ReferrerPolicy.STRICT_ORIGIN_WHEN_CROSS_ORIGIN)
            )
            .permissionsPolicy(permissions -> permissions
                .policy("geolocation=(self), microphone=()")
            )
        );
    
    return http.build();
}
```

### 7.5 Rate Limiting

```java
// Usando Bucket4j
@Component
public class RateLimitInterceptor implements HandlerInterceptor {
    
    private final Map<String, Bucket> cache = new ConcurrentHashMap<>();
    
    @Override
    public boolean preHandle(HttpServletRequest request,
                            HttpServletResponse response,
                            Object handler) throws Exception {
        
        String ip = request.getRemoteAddr();
        Bucket bucket = resolveBucket(ip);
        
        if (bucket.tryConsume(1)) {
            return true;
        }
        
        response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
        response.getWriter().write("{\"error\": \"Demasiadas peticiones\"}");
        return false;
    }
    
    private Bucket resolveBucket(String ip) {
        return cache.computeIfAbsent(ip, k -> createNewBucket());
    }
    
    private Bucket createNewBucket() {
        // 100 peticiones por minuto
        Bandwidth limit = Bandwidth.simple(100, Duration.ofMinutes(1));
        return Bucket.builder()
            .addLimit(limit)
            .build();
    }
}
```

### 7.6 Logging de Seguridad

```java
@Component
@Slf4j
public class SecurityAuditLogger {
    
    @EventListener
    public void onAuthenticationSuccess(AuthenticationSuccessEvent event) {
        String username = event.getAuthentication().getName();
        log.info("Login exitoso - Usuario: {}", username);
    }
    
    @EventListener
    public void onAuthenticationFailure(AbstractAuthenticationFailureEvent event) {
        String username = event.getAuthentication().getName();
        String error = event.getException().getMessage();
        log.warn("Login fallido - Usuario: {}, Error: {}", username, error);
    }
    
    @EventListener
    public void onAuthorizationFailure(AuthorizationDeniedEvent event) {
        Authentication auth = event.getAuthentication().get();
        log.warn("Acceso denegado - Usuario: {}, Recurso: {}", 
                 auth.getName(), 
                 event.getAuthorizationDecision());
    }
}
```

### 7.7 Secrets Management

**❌ NO hacer:**
```java
// NUNCA hardcodear secrets
private String apiKey = "sk-1234567890abcdef";
```

**✅ SÍ hacer:**
```yaml
# application.yml
jwt:
  secret: ${JWT_SECRET}

database:
  password: ${DB_PASSWORD}

api:
  key: ${API_KEY}
```

```bash
# Variables de entorno
export JWT_SECRET="tu-secreto-super-seguro"
export DB_PASSWORD="password-seguro"
```

---

## 8. Gestión de Sesiones

### 8.1 Configuración de Sesiones

```java
@Bean
public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
        .sessionManagement(session -> session
            // STATELESS: Sin sesiones (JWT)
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            
            // O para aplicaciones tradicionales:
            // .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
            // .maximumSessions(1) // Solo una sesión activa por usuario
            // .maxSessionsPreventsLogin(true) // Prevenir nuevos logins si hay sesión activa
            // .expiredUrl("/session-expired")
        );
    
    return http.build();
}
```

### 8.2 Session Fixation Protection

Spring Security protege automáticamente contra ataques de fijación de sesión:

```java
http.sessionManagement(session -> session
    .sessionFixation()
    .newSession() // Crea una nueva sesión y no copia atributos
    // .migrateSession() // Crea nueva sesión y copia atributos (default)
    // .changeSessionId() // Mantiene sesión pero cambia ID (Servlet 3.1+)
);
```

### 8.3 Control de Sesiones Concurrentes

```java
@Configuration
public class SessionConfig {
    
    @Bean
    public HttpSessionEventPublisher httpSessionEventPublisher() {
        return new HttpSessionEventPublisher();
    }
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.sessionManagement(session -> session
            .maximumSessions(1)
            .maxSessionsPreventsLogin(false) // Última sesión cierra las anteriores
            .expiredUrl("/login?expired=true")
        );
        
        return http.build();
    }
}
```

---

## 9. Protección contra Vulnerabilidades

### 9.1 CSRF (Cross-Site Request Forgery)

**Para aplicaciones web con formularios:**
```java
http.csrf(csrf -> csrf
    .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
);
```

```html
<!-- En Thymeleaf -->
<form th:action="@{/transfer}" method="post">
    <input type="hidden" th:name="${_csrf.parameterName}" th:value="${_csrf.token}"/>
    <!-- Resto del formulario -->
</form>
```

**Para APIs REST con JWT:**
```java
http.csrf(csrf -> csrf.disable());
```

### 9.2 SQL Injection

✅ **Usar JPA/Hibernate correctamente:**
```java
// CORRECTO - Parámetros con placeholders
@Query("SELECT u FROM User u WHERE u.username = :username")
User findByUsername(@Param("username") String username);

// CORRECTO - Método derivado
User findByUsernameAndEmail(String username, String email);
```

❌ **NUNCA hacer:**
```java
// PELIGROSO - Concatenación de strings
@Query(value = "SELECT * FROM users WHERE username = '" + username + "'", 
       nativeQuery = true)
User findByUsernameDangerous(String username);
```

### 9.3 Clickjacking

```java
http.headers(headers -> headers
    .frameOptions(frame -> frame
        .deny() // No permitir iframes
        // .sameOrigin() // Solo iframes del mismo origen
    )
);
```

### 9.4 Man-in-the-Middle (MITM)

**Forzar HTTPS en producción:**
```java
@Configuration
public class SecurityConfig {
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.requiresChannel(channel -> channel
            .anyRequest().requiresSecure()
        );
        
        return http.build();
    }
}
```

```yaml
# application.yml
server:
  ssl:
    enabled: true
    key-store: classpath:keystore.p12
    key-store-password: ${KEYSTORE_PASSWORD}
    key-store-type: PKCS12
    key-alias: tomcat
```

### 9.5 Sensitive Data Exposure

```java
@Entity
public class User {
    
    @Id
    private Long id;
    
    private String username;
    
    @JsonIgnore // No serializar en JSON
    private String password;
    
    @JsonIgnore
    private String ssn; // Número de seguridad social
    
    // Getters y Setters
}

// O usar DTOs
@Data
public class UserResponseDto {
    private Long id;
    private String username;
    private String email;
    // NO incluir password ni datos sensibles
}
```

---

## 10. Casos de Uso Prácticos

### 10.1 API REST con JWT

**Estructura del proyecto:**
```
src/main/java/com/example/api/
├── config/
│   ├── SecurityConfig.java
│   ├── CorsConfig.java
│   └── JwtConfig.java
├── controller/
│   ├── AuthController.java
│   └── UserController.java
├── dto/
│   ├── LoginRequest.java
│   ├── RegisterRequest.java
│   └── AuthResponse.java
├── entity/
│   ├── User.java
│   └── Role.java
├── filter/
│   └── JwtAuthenticationFilter.java
├── repository/
│   ├── UserRepository.java
│   └── RoleRepository.java
├── service/
│   ├── JwtService.java
│   ├── UserService.java
│   └── AuthService.java
└── exception/
    ├── GlobalExceptionHandler.java
    └── UnauthorizedException.java
```

**Flujo completo:**

1. Usuario se registra → `/api/auth/register`
2. Usuario hace login → `/api/auth/login` → Recibe JWT
3. Usuario usa JWT en header → `Authorization: Bearer <token>`
4. Backend valida JWT y procesa petición
5. Token expira → Usuario usa refresh token → Obtiene nuevo access token

### 10.2 Aplicación Web con Formulario de Login

```java
@Controller
public class LoginController {
    
    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }
    
    @GetMapping("/dashboard")
    public String dashboard(Authentication authentication, Model model) {
        model.addAttribute("username", authentication.getName());
        model.addAttribute("roles", authentication.getAuthorities());
        return "dashboard";
    }
}
```

```html
<!-- login.html (Thymeleaf) -->
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <title>Login</title>
</head>
<body>
    <h2>Login</h2>
    
    <div th:if="${param.error}">
        <p style="color: red;">Credenciales inválidas</p>
    </div>
    
    <div th:if="${param.logout}">
        <p style="color: green;">Has cerrado sesión exitosamente</p>
    </div>
    
    <form th:action="@{/perform-login}" method="post">
        <div>
            <label>Usuario:</label>
            <input type="text" name="username" required/>
        </div>
        <div>
            <label>Contraseña:</label>
            <input type="password" name="password" required/>
        </div>
        <div>
            <input type="checkbox" name="remember-me"/> Recordarme
        </div>
        <button type="submit">Login</button>
    </form>
</body>
</html>
```

### 10.3 OAuth2 con Google

```yaml
spring:
  security:
    oauth2:
      client:
        registration:
          google:
            client-id: ${GOOGLE_CLIENT_ID}
            client-secret: ${GOOGLE_CLIENT_SECRET}
            scope:
              - email
              - profile
```

```java
@Controller
public class OAuth2LoginController {
    
    @GetMapping("/")
    public String home() {
        return "home";
    }
    
    @GetMapping("/loginSuccess")
    public String loginSuccess(OAuth2AuthenticationToken authentication, Model model) {
        OAuth2User oauth2User = authentication.getPrincipal();
        
        String email = oauth2User.getAttribute("email");
        String name = oauth2User.getAttribute("name");
        
        model.addAttribute("email", email);
        model.addAttribute("name", name);
        
        return "dashboard";
    }
}
```

### 10.4 Multi-Tenancy (Aislamiento por Usuario)

```java
@Service
public class TenantAwareUserService {
    
    @Autowired
    private UserRepository userRepository;
    
    @PreAuthorize("isAuthenticated()")
    public List<Document> getUserDocuments() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = auth.getName();
        
        // Solo documentos del usuario actual
        return documentRepository.findByOwnerUsername(currentUsername);
    }
    
    @PreAuthorize("@documentSecurity.canAccess(#documentId)")
    public Document getDocument(Long documentId) {
        return documentRepository.findById(documentId)
            .orElseThrow(() -> new ResourceNotFoundException("Documento no encontrado"));
    }
}

@Component("documentSecurity")
public class DocumentSecurity {
    
    @Autowired
    private DocumentRepository documentRepository;
    
    public boolean canAccess(Long documentId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        
        Document doc = documentRepository.findById(documentId).orElse(null);
        if (doc == null) return false;
        
        // Verificar propiedad o permisos compartidos
        return doc.getOwner().getUsername().equals(username) ||
               doc.getSharedWith().stream()
                   .anyMatch(u -> u.getUsername().equals(username));
    }
}
```

---

## 11. Testing de Seguridad

### 11.1 Configuración de Tests

```xml
<dependency>
    <groupId>org.springframework.security</groupId>
    <artifactId>spring-security-test</artifactId>
    <scope>test</scope>
</dependency>
```

### 11.2 Tests con @WithMockUser

```java
@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Test
    public void whenNoAuth_thenUnauthorized() throws Exception {
        mockMvc.perform(get("/api/users"))
            .andExpect(status().isUnauthorized());
    }
    
    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void whenUserAuth_thenOk() throws Exception {
        mockMvc.perform(get("/api/users/profile"))
            .andExpect(status().isOk());
    }
    
    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void whenAdminAuth_thenCanAccessAdminEndpoint() throws Exception {
        mockMvc.perform(get("/api/admin/users"))
            .andExpect(status().isOk());
    }
    
    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void whenUserAuth_thenCannotAccessAdminEndpoint() throws Exception {
        mockMvc.perform(get("/api/admin/users"))
            .andExpect(status().isForbidden());
    }
}
```

### 11.3 Tests con JWT Real

```java
@SpringBootTest
@AutoConfigureMockMvc
public class JwtAuthenticationTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private JwtService jwtService;
    
    @Autowired
    private UserDetailsService userDetailsService;
    
    @Test
    public void whenValidToken_thenAccessProtectedResource() throws Exception {
        UserDetails userDetails = userDetailsService.loadUserByUsername("testuser");
        String token = jwtService.generateToken(userDetails);
        
        mockMvc.perform(get("/api/users/profile")
                .header("Authorization", "Bearer " + token))
            .andExpect(status().isOk());
    }
    
    @Test
    public void whenInvalidToken_thenUnauthorized() throws Exception {
        mockMvc.perform(get("/api/users/profile")
                .header("Authorization", "Bearer invalid-token"))
            .andExpect(status().isUnauthorized());
    }
    
    @Test
    public void whenExpiredToken_thenUnauthorized() throws Exception {
        // Generar token expirado
        String expiredToken = generateExpiredToken();
        
        mockMvc.perform(get("/api/users/profile")
                .header("Authorization", "Bearer " + expiredToken))
            .andExpect(status().isUnauthorized());
    }
}
```

### 11.4 Tests de Method Security

```java
@SpringBootTest
public class UserServiceSecurityTest {
    
    @Autowired
    private UserService userService;
    
    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void whenAdmin_thenCanDeleteUser() {
        assertDoesNotThrow(() -> userService.deleteUser(1L));
    }
    
    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void whenUser_thenCannotDeleteUser() {
        assertThrows(AccessDeniedException.class, 
                    () -> userService.deleteUser(1L));
    }
    
    @Test
    @WithMockUser(username = "john", roles = {"USER"})
    public void whenUser_thenCanAccessOwnProfile() {
        assertDoesNotThrow(() -> userService.getUser("john"));
    }
    
    @Test
    @WithMockUser(username = "john", roles = {"USER"})
    public void whenUser_thenCannotAccessOtherProfile() {
        assertThrows(AccessDeniedException.class, 
                    () -> userService.getUser("jane"));
    }
}
```

### 11.5 Custom Test Annotations

```java
@Retention(RetentionPolicy.RUNTIME)
@WithMockUser(username = "admin", roles = {"ADMIN"})
public @interface WithMockAdmin {
}

@Retention(RetentionPolicy.RUNTIME)
@WithMockUser(username = "user", roles = {"USER"})
public @interface WithMockRegularUser {
}

// Uso
@Test
@WithMockAdmin
public void adminTest() {
    // Test con permisos de admin
}

@Test
@WithMockRegularUser
public void userTest() {
    // Test con permisos de usuario regular
}
```

---

## 12. Configuración para Producción

### 12.1 Variables de Entorno

```yaml
# application-prod.yml
spring:
  datasource:
    url: ${DATABASE_URL}
    username: ${DATABASE_USERNAME}
    password: ${DATABASE_PASSWORD}
  
jwt:
  secret: ${JWT_SECRET}
  expiration: ${JWT_EXPIRATION:3600000}

server:
  ssl:
    enabled: true
    key-store: ${SSL_KEYSTORE_PATH}
    key-store-password: ${SSL_KEYSTORE_PASSWORD}
```

### 12.2 Logging y Auditoría

```yaml
logging:
  level:
    org.springframework.security: INFO
    com.example.security: DEBUG
  
  file:
    name: logs/security-audit.log
  
  pattern:
    console: "%d{yyyy-MM-dd HH:mm:ss} - %msg%n"
    file: "%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n"
```

```java
@Component
@Slf4j
public class AuditLogger {
    
    public void logSecurityEvent(String event, String username, String details) {
        log.info("SECURITY_EVENT | Event: {} | User: {} | Details: {}", 
                 event, username, details);
    }
    
    public void logAccessDenied(String username, String resource) {
        log.warn("ACCESS_DENIED | User: {} | Resource: {}", username, resource);
    }
    
    public void logSuspiciousActivity(String username, String activity) {
        log.error("SUSPICIOUS_ACTIVITY | User: {} | Activity: {}", username, activity);
    }
}
```

### 12.3 Monitoreo con Actuator

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```

```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics
      base-path: /actuator
  
  endpoint:
    health:
      show-details: when-authorized
      roles: ADMIN
```

```java
@Configuration
public class ActuatorSecurityConfig {
    
    @Bean
    public SecurityFilterChain actuatorFilterChain(HttpSecurity http) throws Exception {
        http
            .securityMatcher("/actuator/**")
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/actuator/health").permitAll()
                .requestMatchers("/actuator/**").hasRole("ADMIN")
            );
        
        return http.build();
    }
}
```

### 12.4 Checklist Pre-Producción

#### Configuración
- [ ] Variables de entorno configuradas (no hardcoded)
- [ ] JWT secret fuerte y único
- [ ] HTTPS habilitado
- [ ] CORS configurado correctamente
- [ ] CSRF según tipo de aplicación

#### Seguridad
- [ ] Contraseñas hasheadas con BCrypt/Argon2
- [ ] Validación de entrada en todos los endpoints
- [ ] Rate limiting implementado
- [ ] Headers de seguridad configurados
- [ ] SQL injection prevenido (usar JPA correctamente)

#### Autenticación/Autorización
- [ ] Roles y permisos bien definidos
- [ ] Endpoints públicos vs privados identificados
- [ ] Method security en métodos críticos
- [ ] Refresh tokens implementados
- [ ] Logout adecuadamente implementado

#### Monitoreo
- [ ] Logging de eventos de seguridad
- [ ] Actuator endpoints protegidos
- [ ] Alertas para actividades sospechosas
- [ ] Auditoría de accesos críticos

#### Testing
- [ ] Tests de seguridad para todos los endpoints
- [ ] Tests de autorización por rol
- [ ] Tests de JWT (válido, inválido, expirado)
- [ ] Tests de vulnerabilidades (XSS, CSRF, SQL Injection)

### 12.5 Actualización y Mantenimiento

```xml
<!-- Mantener dependencias actualizadas -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
    <!-- Usar la última versión estable -->
</dependency>
```

**Estrategia de actualización:**
1. Monitorear CVEs de Spring Security
2. Actualizar parches de seguridad inmediatamente
3. Testear exhaustivamente antes de deploy
4. Mantener backups y rollback plan

---

## 📚 Recursos Adicionales

### Documentación Oficial
- [Spring Security Reference](https://docs.spring.io/spring-security/reference/)
- [Spring Boot Security Auto-configuration](https://docs.spring.io/spring-boot/docs/current/reference/html/web.html#web.security)
- [JWT.io](https://jwt.io/)

### Herramientas
- [OWASP Dependency-Check](https://owasp.org/www-project-dependency-check/)
- [Snyk](https://snyk.io/) - Escaneo de vulnerabilidades
- [SonarQube](https://www.sonarqube.org/) - Análisis de código

### Guías OWASP
- [OWASP Top 10](https://owasp.org/www-project-top-ten/)
- [OWASP Cheat Sheet Series](https://cheatsheetseries.owasp.org/)
- [OWASP API Security Top 10](https://owasp.org/www-project-api-security/)

---

## 🎯 Resumen Rápido

### Autenticación
1. **JWT** para APIs REST (stateless)
2. **Form-based** para aplicaciones web tradicionales
3. **OAuth2** para login social

### Autorización
1. Usar `@PreAuthorize` para control fino
2. Configurar `SecurityFilterChain` para URLs
3. Implementar custom security beans si es necesario

### Buenas Prácticas
1. ✅ HTTPS en producción
2. ✅ BCrypt/Argon2 para contraseñas
3. ✅ Variables de entorno para secrets
4. ✅ Validación y sanitización de entradas
5. ✅ Rate limiting
6. ✅ Logging de eventos de seguridad
7. ❌ NUNCA hardcodear secrets
8. ❌ NUNCA confiar en el cliente

---

**Versión:** 1.0  
**Fecha:** Noviembre 2025  
**Autor:** Documentación para DWES - Rafael Alberti
