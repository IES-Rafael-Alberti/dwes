### Pasos para Implementar un Control de Acceso Avanzado

1. **Roles y permisos**: Configuraremos roles y permisos en las entidades de usuario.
2. **Configurar JWT con claims personalizados**: Al añadir claims personalizados a los tokens JWT, podemos incluir roles y permisos en el token.
3. **Configurar filtros y reglas de autorización en Spring Security**: Especificaremos reglas avanzadas en `SecurityConfig` para que cada rol acceda solo a los recursos permitidos.
4. **Custom Pre/Post Authorization**: Usaremos anotaciones como `@PreAuthorize` para un control de acceso específico en los métodos.

---

### Paso 1: Definir Roles y Permisos en la Entidad `Usuario`

Primero, configuramos la entidad `Usuario` para manejar **roles** (como `BIBLIOTECARIO`, `USUARIO`, `ADMINISTRADOR`) y **permisos** específicos si se requiere más granularidad (por ejemplo, `CREAR_LIBRO`, `VER_PRESTAMO`).

#### Ejemplo de Entidades de Roles y Permisos

1. **Enum de Roles**:

   ```java
   public enum Rol {
       USUARIO,
       BIBLIOTECARIO,
       ADMINISTRADOR
   }
   ```

2. **Enum de Permisos (Opcional)**:

   Si quieres un control más detallado, puedes agregar permisos específicos.

   ```java
   public enum Permiso {
       CREAR_LIBRO,
       VER_PRESTAMO,
       EDITAR_USUARIO,
       ELIMINAR_LIBRO
   }
   ```

3. **Entidad Usuario con Roles y Permisos**:

   ```java
   import jakarta.persistence.*;
   import java.util.Set;

   @Entity
   public class Usuario {

       @Id
       @GeneratedValue(strategy = GenerationType.IDENTITY)
       private Long id;

       private String nombre;

       @Column(unique = true)
       private String email;

       private String password;

       @Enumerated(EnumType.STRING)
       private Rol rol;

       @ElementCollection(fetch = FetchType.EAGER)
       @Enumerated(EnumType.STRING)
       private Set<Permiso> permisos;

       // Getters y setters
   }
   ```

### Paso 2: Incluir Roles y Permisos en el JWT

Modifica el `JwtUtil` para incluir los roles y permisos en los **claims** del JWT. Esto permite a Spring Security recuperar la información del usuario desde el token.

#### Ejemplo de `JwtUtil` con Roles y Permisos en Claims

```java
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Component;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtil {

    private String SECRET_KEY = "biblioteca_secret";

    public String generateToken(UserDetails userDetails, Usuario usuario) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("rol", usuario.getRol());
        claims.put("permisos", usuario.getPermisos());
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

    // Métodos para validar y extraer claims del token
}
```

### Paso 3: Configurar Reglas de Seguridad Avanzadas en `SecurityConfig`

Configura reglas de acceso basadas en roles y permisos en `SecurityConfig`.

#### Ejemplo de `SecurityConfig` con Autorización Avanzada

```java
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .authorizeRequests()
                .antMatchers("/libros/**").hasRole("BIBLIOTECARIO") // Solo bibliotecarios pueden gestionar libros
                .antMatchers("/prestamos/**").hasRole("USUARIO") // Solo usuarios pueden gestionar préstamos
                .antMatchers("/admin/**").hasRole("ADMINISTRADOR") // Solo administradores pueden acceder a /admin
                .antMatchers("/public/**").permitAll() // Endpoints públicos
                .anyRequest().authenticated()
                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        // Añadir filtro JWT antes del filtro de autenticación predeterminado
        http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
    }
}
```

Aquí hemos configurado permisos de acceso a rutas específicas para cada rol:

- **`/libros/**`**: Solo los usuarios con el rol `BIBLIOTECARIO` pueden acceder a estos endpoints.
- **`/prestamos/**`**: Solo accesible por `USUARIO`.
- **`/admin/**`**: Solo accesible para `ADMINISTRADOR`.
- **`/public/**`**: Acceso permitido para todos (sin autenticación).

### Paso 4: Configurar Acceso Detallado con `@PreAuthorize`

Usa `@PreAuthorize` para añadir control de acceso detallado a nivel de método en los servicios o controladores. Esto es útil para proteger funciones específicas sin limitar toda la clase.

#### Ejemplo de `@PreAuthorize` en un Servicio

Supongamos que solo un `ADMINISTRADOR` puede eliminar un libro y que cualquier `BIBLIOTECARIO` o `ADMINISTRADOR` puede crear un libro.

```java
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
public class LibroService {

    @PreAuthorize("hasRole('BIBLIOTECARIO') or hasRole('ADMINISTRADOR')")
    public Libro crearLibro(Libro libro) {
        return libroRepository.save(libro);
    }

    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public void eliminarLibro(Long id) {
        libroRepository.deleteById(id);
    }
}
```

- **`@PreAuthorize("hasRole('BIBLIOTECARIO') or hasRole('ADMINISTRADOR')")`**: Permite que el método `crearLibro` sea ejecutado solo por `BIBLIOTECARIO` o `ADMINISTRADOR`.
- **`@PreAuthorize("hasRole('ADMINISTRADOR')")`**: Limita el método `eliminarLibro` solo para el rol `ADMINISTRADOR`.

### Paso 5: Activar `@PreAuthorize` en la Configuración de Seguridad

Para que `@PreAuthorize` funcione, debemos habilitar las anotaciones de seguridad en la configuración de Spring Security.

```java
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class MethodSecurityConfig {
}
```

Esta configuración permite que Spring Security respete las anotaciones `@PreAuthorize` y `@PostAuthorize`, aplicando las restricciones en cada método.

---

### Resumen

1. **Definir Roles y Permisos**: Configura roles (`USUARIO`, `BIBLIOTECARIO`, `ADMINISTRADOR`) y permisos específicos si se necesita un control detallado.
2. **Incluir Roles y Permisos en el JWT**: Añade roles y permisos como claims en el JWT para acceder a ellos en las reglas de seguridad.
3. **Configurar `SecurityConfig` con Reglas Avanzadas**: Define reglas de acceso específicas para cada rol y endpoint.
4. **Control de Acceso Detallado con `@PreAuthorize`**: Usa `@PreAuthorize` en métodos específicos para personalizar el acceso a nivel de función.
5. **Habilitar Seguridad a Nivel de Método**: Con `@EnableGlobalMethodSecurity(prePostEnabled = true)`, activamos `@PreAuthorize` en todo el proyecto.

Este enfoque permite un control de acceso avanzado y flexible en la aplicación, asegurando que cada usuario pueda acceder solo a los recursos y operaciones permitidos según su rol o permisos.
---
¡Claro! La **autorización personalizada** en Spring Boot permite definir reglas y condiciones específicas para controlar el acceso a recursos o funcionalidades de manera detallada. Esto es útil cuando los roles y permisos convencionales no son suficientes para cubrir todas las reglas de negocio. Aquí te explico varias estrategias para implementar una autorización personalizada:

### Estrategias para la Autorización Personalizada

1. **Métodos personalizados de autorización**: Usa anotaciones como `@PreAuthorize` con expresiones de autorización avanzadas.
2. **Creación de una clase de autorización personalizada**: Implementa una lógica de autorización específica usando componentes personalizados.
3. **Uso de `@PostAuthorize` para validar después de ejecutar el método**: Útil cuando necesitas evaluar permisos basados en el resultado del método.
4. **Creación de un filtro de autorización personalizado**: Para decisiones de autorización antes de que el controlador procese la solicitud.

---

### Paso 1: Autorización Personalizada con `@PreAuthorize` y Expresiones SpEL

Spring Security permite el uso de **Spring Expression Language (SpEL)** en anotaciones como `@PreAuthorize` para definir reglas de autorización avanzadas.

#### Ejemplo de `@PreAuthorize` con Condiciones Personalizadas

En este ejemplo, solo el `ADMINISTRADOR` o el `USUARIO` que creó un recurso específico pueden acceder a él.

```java
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
public class LibroService {

    @PreAuthorize("hasRole('ADMINISTRADOR') or #usuarioId == principal.id")
    public Libro obtenerLibro(Long libroId, Long usuarioId) {
        return libroRepository.findById(libroId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Libro no encontrado"));
    }
}
```

Aquí, `#usuarioId == principal.id` verifica que el `usuarioId` proporcionado coincida con el ID del usuario autenticado (`principal.id`). Esto permite que solo el creador o un administrador acceda al recurso.

### Paso 2: Autorización con Clases de Autorización Personalizadas

Si necesitas una lógica de autorización más compleja, puedes definir una clase que encapsule estas reglas.

#### Paso 2.1: Crear un Servicio de Autorización

Crea un servicio que verifique reglas de acceso complejas.

```java
import org.springframework.stereotype.Component;

@Component
public class AuthorizationService {

    public boolean puedeAccederLibro(Long libroId, Long usuarioId) {
        // Implementa la lógica de verificación: solo el creador o el admin puede acceder
        Libro libro = libroRepository.findById(libroId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Libro no encontrado"));

        // Verifica si el usuario es el creador del libro o tiene rol de administrador
        return libro.getUsuarioCreador().getId().equals(usuarioId) || usuarioEsAdmin(usuarioId);
    }

    private boolean usuarioEsAdmin(Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado"));
        return usuario.getRol().equals(Rol.ADMINISTRADOR);
    }
}
```

#### Paso 2.2: Usar el Servicio de Autorización en `@PreAuthorize`

Luego, usa el servicio de autorización en `@PreAuthorize` para realizar la comprobación en el método.

```java
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
public class LibroService {

    @Autowired
    private AuthorizationService authorizationService;

    @PreAuthorize("@authorizationService.puedeAccederLibro(#libroId, principal.id)")
    public Libro obtenerLibro(Long libroId) {
        return libroRepository.findById(libroId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Libro no encontrado"));
    }
}
```

- **`@PreAuthorize("@authorizationService.puedeAccederLibro(#libroId, principal.id)")`**: Llama al método `puedeAccederLibro` de `AuthorizationService` para verificar si el usuario autenticado tiene acceso al libro específico.

### Paso 3: Autorización Post-Ejecución con `@PostAuthorize`

Si necesitas realizar la verificación después de ejecutar el método, puedes usar `@PostAuthorize`. Esto es útil cuando quieres tomar decisiones de autorización basadas en el resultado del método.

#### Ejemplo de `@PostAuthorize`

Supongamos que quieres verificar el acceso a un libro después de obtenerlo.

```java
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.stereotype.Service;

@Service
public class LibroService {

    @PostAuthorize("returnObject.usuarioCreador.id == principal.id or hasRole('ADMINISTRADOR')")
    public Libro obtenerLibro(Long libroId) {
        return libroRepository.findById(libroId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Libro no encontrado"));
    }
}
```

- **`@PostAuthorize`**: Este método se ejecuta y después se verifica si el usuario autenticado es el creador (`returnObject.usuarioCreador.id == principal.id`) o tiene el rol de `ADMINISTRADOR`.

### Paso 4: Crear un Filtro de Autorización Personalizado

Para decisiones de autorización que se deben tomar antes de que el controlador procese la solicitud, puedes implementar un filtro de autorización personalizado.

#### Ejemplo de Filtro Personalizado para Autorización Avanzada

Este filtro intercepta las solicitudes, verifica el rol o permiso y decide si permite o rechaza la solicitud.

```java
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAuthorizationFilter implements Filter {

    @Override
    public void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && hasAccess(authentication, request)) {
            chain.doFilter(request, response);
        } else {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "No tienes permiso para acceder a este recurso.");
        }
    }

    private boolean hasAccess(Authentication authentication, HttpServletRequest request) {
        // Lógica de autorización personalizada según el rol o permiso
        String role = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .filter(auth -> auth.equals("ROLE_ADMIN"))
                .findFirst()
                .orElse(null);

        // Ejemplo: solo ROLE_ADMIN puede acceder a rutas que comienzan con /admin
        if (request.getRequestURI().startsWith("/admin") && role == null) {
            return false;
        }

        return true;
    }

    @Override
    public void init(FilterConfig filterConfig) {}

    @Override
    public void destroy() {}
}
```

#### Añadir el Filtro en `SecurityConfig`

Para que este filtro se ejecute, agrégalo en la configuración de seguridad.

```java
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private CustomAuthorizationFilter customAuthorizationFilter;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
            .authorizeRequests()
            .anyRequest().authenticated()
            .and()
            .addFilterBefore(customAuthorizationFilter, UsernamePasswordAuthenticationFilter.class);
    }
}
```

---

### Resumen de Opciones para Autorización Personalizada

1. **`@PreAuthorize` con Expresiones SpEL**: Define expresiones avanzadas basadas en el usuario autenticado o propiedades de la entidad.
2. **Servicio de Autorización Personalizado**: Implementa reglas de negocio avanzadas en un componente que se invoca desde `@PreAuthorize`.
3. **`@PostAuthorize` para Validación Después del Método**: Autoriza en función del resultado devuelto por el método.
4. **Filtro de Autorización Personalizado**: Intercepta y verifica autorizaciones antes de que el controlador reciba la solicitud.

Esta configuración te permite un control preciso sobre quién puede acceder a cada recurso y acción, integrando lógica de negocio específica en cada método o ruta de la aplicación. ¡Avísame si necesitas ayuda con la implementación de alguno de estos pasos!
---
¡Claro! `@PostAuthorize` y los filtros de Spring Security son herramientas clave para personalizar y controlar la seguridad en aplicaciones Spring Boot. Aquí te explico cómo se usan y se configuran para obtener un control de acceso avanzado.

---

### Uso de `@PostAuthorize` en Spring Security

`@PostAuthorize` es una anotación en Spring Security que permite realizar la **autorización después de que un método ha sido ejecutado**. A diferencia de `@PreAuthorize`, que se ejecuta antes del método, `@PostAuthorize` se basa en el **resultado del método**. Esto es útil cuando necesitas tomar decisiones de autorización basadas en el valor devuelto o en los cambios hechos por el método.

#### Cuándo Usar `@PostAuthorize`
- Cuando necesitas **autorización basada en el contenido devuelto** por el método.
- Para filtrar o restringir acceso a ciertas partes de los datos devueltos.
- Si se quiere autorizar en base a **propiedades de la entidad resultante** (por ejemplo, el ID del creador de un recurso).

#### Ejemplo de `@PostAuthorize`

Supongamos que tenemos un método en el servicio `LibroService` que devuelve un `Libro`. Queremos permitir que solo el **creador del libro** o un **administrador** puedan ver el recurso.

```java
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.stereotype.Service;

@Service
public class LibroService {

    @PostAuthorize("returnObject.usuarioCreador.id == principal.id or hasRole('ADMINISTRADOR')")
    public Libro obtenerLibro(Long libroId) {
        return libroRepository.findById(libroId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Libro no encontrado"));
    }
}
```

- **`returnObject.usuarioCreador.id == principal.id`**: Verifica que el `usuarioCreador` del objeto devuelto (`Libro`) sea el usuario autenticado (`principal.id`).
- **`or hasRole('ADMINISTRADOR')`**: Permite que los administradores vean cualquier libro independientemente de su creador.

Este tipo de autorización es útil cuando se debe verificar propiedades del objeto devuelto o cuando el acceso depende del resultado de la ejecución del método.

---

### Filtros en Spring Security

Spring Security utiliza filtros para **interceptar, autenticar y autorizar** solicitudes HTTP en diferentes fases de la cadena de seguridad. Los filtros aplican reglas y condiciones de seguridad que controlan quién puede acceder a los recursos protegidos y cómo.

#### Principales Filtros de Spring Security

1. **Filtro de Autenticación (`UsernamePasswordAuthenticationFilter`)**:
   - Maneja la autenticación de usuario, típicamente usando nombre de usuario y contraseña.
   - Se activa cuando el usuario envía las credenciales, generalmente en la ruta de login.

2. **Filtro de Autenticación JWT (Custom JWT Filter)**:
   - Si usas autenticación basada en JWT, este filtro se implementa para verificar el token en cada solicitud.
   - Extrae el token del encabezado, lo valida y autentica al usuario sin necesidad de nombre de usuario y contraseña.
   - **Ejemplo**: Un filtro JWT personalizado para Spring Security.

     ```java
     import jakarta.servlet.FilterChain;
     import jakarta.servlet.ServletException;
     import jakarta.servlet.http.HttpServletRequest;
     import jakarta.servlet.http.HttpServletResponse;
     import org.springframework.beans.factory.annotation.Autowired;
     import org.springframework.security.core.context.SecurityContextHolder;
     import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
     import org.springframework.web.filter.OncePerRequestFilter;

     import java.io.IOException;

     public class JwtRequestFilter extends OncePerRequestFilter {

         @Autowired
         private JwtUtil jwtUtil;

         @Autowired
         private MyUserDetailsService userDetailsService;

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

                 if (jwtUtil.validateToken(jwt, userDetails)) {
                     UsernamePasswordAuthenticationToken authenticationToken =
                         new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                     authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                     SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                 }
             }
             chain.doFilter(request, response);
         }
     }
     ```

3. **Filtro de Autorización (`AuthorizationFilter`)**:
   - Verifica que el usuario autenticado tenga los permisos necesarios para acceder a los recursos solicitados.
   - Generalmente funciona en conjunto con `@PreAuthorize` y `@PostAuthorize` o reglas de acceso definidas en `SecurityConfig`.

4. **Filtro de Protección contra CSRF (`CsrfFilter`)**:
   - Evita ataques de falsificación de solicitudes entre sitios (CSRF).
   - Genera y valida un token CSRF en las solicitudes mutables (POST, PUT, DELETE).

5. **Filtro de CORS (`CorsFilter`)**:
   - Controla el intercambio de recursos entre diferentes dominios (Cross-Origin Resource Sharing).
   - Este filtro permite configurar las políticas de acceso de dominios externos para acceder a la API de forma segura.

#### Orden de los Filtros

Spring Security aplica filtros en un orden específico, y cada uno desempeña un papel en el proceso de seguridad. Algunos de los filtros predeterminados y su orden aproximado son:

1. **SecurityContextPersistenceFilter**: Gestiona el contexto de seguridad en la sesión.
2. **UsernamePasswordAuthenticationFilter**: Autenticación basada en nombre de usuario y contraseña.
3. **BasicAuthenticationFilter**: Autenticación básica con encabezado `Authorization: Basic`.
4. **BearerTokenAuthenticationFilter**: Si se utiliza autenticación basada en token de portador (Bearer Token).
5. **CsrfFilter**: Filtro de protección contra CSRF.
6. **LogoutFilter**: Maneja las solicitudes de cierre de sesión.
7. **ExceptionTranslationFilter**: Maneja excepciones de seguridad y redirige a la página de error adecuada.
8. **FilterSecurityInterceptor**: Intercepta las solicitudes y aplica las reglas de acceso.

#### Personalización de Filtros

Podemos añadir filtros personalizados, como el **filtro JWT** en el ejemplo anterior, para interceptar y manipular las solicitudes antes de que lleguen a los controladores.

En la configuración de seguridad, los filtros personalizados se añaden con `addFilterBefore()` o `addFilterAfter()`, indicando la posición relativa respecto a un filtro existente.

#### Ejemplo de Configuración de Filtros en `SecurityConfig`

```java
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private JwtRequestFilter jwtRequestFilter;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
            .authorizeRequests()
            .antMatchers("/public/**").permitAll()
            .anyRequest().authenticated()
            .and()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        // Añadir filtro JWT antes del UsernamePasswordAuthenticationFilter
        http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
    }
}
```

En este ejemplo:
- **`addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class)`**: Inserta el filtro `jwtRequestFilter` antes del filtro de autenticación de nombre de usuario y contraseña.
- **`SessionCreationPolicy.STATELESS`**: Configura la sesión como sin estado, para que Spring Security no gestione sesiones.

---

### Resumen

1. **`@PostAuthorize`**: Se utiliza para verificar permisos después de ejecutar un método, permitiendo realizar autorizaciones basadas en el resultado del método.
2. **Filtros de Spring Security**:
   - Permiten interceptar solicitudes en diferentes etapas del proceso de seguridad (autenticación, autorización, CSRF, CORS).
   - **Filtros personalizados**: Puedes añadir filtros como un **filtro JWT** para autenticar y autorizar solicitudes usando tokens.

3. **Configuración de Filtros en Spring Security**:
   - `addFilterBefore()` y `addFilterAfter()` permiten personalizar la posición de los filtros en la cadena de seguridad para adaptarse a los requerimientos específicos.

Esto permite un control preciso y seguro en cada paso de la solicitud, asegurando que solo usuarios autorizados accedan a los recursos protegidos según las políticas de la aplicación.
---
¡Claro! Vamos a ver cómo funciona `@Secured` y cómo configurar un cierre de sesión seguro en Spring Security.

---

### Anotación `@Secured`

`@Secured` es una anotación en Spring Security que permite restringir el acceso a métodos en base a **roles específicos**. A diferencia de `@PreAuthorize` o `@PostAuthorize`, que permiten usar expresiones complejas con Spring Expression Language (SpEL), `@Secured` solo admite la verificación directa de roles.

#### Cuándo Usar `@Secured`

`@Secured` es ideal cuando necesitas verificar que un usuario tiene uno o varios **roles específicos** sin necesidad de condiciones adicionales. Es más simple y directo que `@PreAuthorize` y es útil en configuraciones donde el control de acceso está basado en roles sencillos y específicos.

#### Ejemplo de Uso de `@Secured`

En este ejemplo, solo los usuarios con el rol `ADMINISTRADOR` o `BIBLIOTECARIO` pueden acceder a métodos específicos en el servicio `LibroService`.

```java
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;

@Service
public class LibroService {

    @Secured("ROLE_ADMINISTRADOR")
    public Libro crearLibro(Libro libro) {
        return libroRepository.save(libro);
    }

    @Secured({"ROLE_ADMINISTRADOR", "ROLE_BIBLIOTECARIO"})
    public Libro obtenerLibro(Long id) {
        return libroRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Libro no encontrado"));
    }
}
```

- **`@Secured("ROLE_ADMINISTRADOR")`**: Solo los usuarios con el rol `ADMINISTRADOR` pueden acceder al método `crearLibro`.
- **`@Secured({"ROLE_ADMINISTRADOR", "ROLE_BIBLIOTECARIO"})`**: Permite el acceso a `obtenerLibro` tanto a `ADMINISTRADOR` como a `BIBLIOTECARIO`.

> **Nota**: Los roles deben tener el prefijo `ROLE_` en `@Secured`. Esto es importante, ya que Spring Security lo espera en esta anotación.

#### Habilitar `@Secured` en la Configuración de Seguridad

Para que `@Secured` funcione, debemos habilitarlo en nuestra clase de configuración de seguridad.

```java
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;

@Configuration
@EnableGlobalMethodSecurity(securedEnabled = true)
public class MethodSecurityConfig {
}
```

La anotación `@EnableGlobalMethodSecurity(securedEnabled = true)` permite el uso de `@Secured` en toda la aplicación.

---

### Manejo Seguro de Cierre de Sesión en Spring Security

El cierre de sesión seguro es una práctica importante para proteger la aplicación de accesos no autorizados después de que un usuario decide cerrar sesión. Spring Security facilita la configuración de un sistema de cierre de sesión seguro, especialmente en aplicaciones con autenticación basada en tokens JWT.

#### Estrategia de Cierre de Sesión para Autenticación JWT

Dado que JWT es un método **sin estado**, no se mantiene una sesión del lado del servidor. Una vez que se emite un token JWT, el servidor no puede invalidarlo de manera automática, ya que el token es auto-contenido. Aquí tienes algunas prácticas recomendadas para manejar el cierre de sesión de forma segura en aplicaciones con JWT:

1. **Listas de Revocación de Tokens (Blacklist)**:
   - Una opción es mantener una lista de tokens JWT revocados en el servidor (como en una base de datos o caché).
   - Cuando un usuario cierra sesión, el token se agrega a esta lista de revocación.
   - En cada solicitud, el token se verifica contra la lista de revocación; si está en la lista, se considera inválido.
   - **Contras**: Puede ser costoso y complejo si hay muchos usuarios o tokens a verificar.

2. **Reducir la Duración de Vida del Token**:
   - Configura el token JWT con un **tiempo de expiración corto** (por ejemplo, 15 minutos).
   - Combina este enfoque con un **token de actualización** (refresh token), que permite obtener un nuevo JWT después de la expiración.
   - Cuando el usuario cierra sesión, el token de actualización se revoca, lo que evita que se puedan obtener nuevos tokens JWT.

3. **Invalidar el Token en el Cliente**:
   - Al cerrar sesión, simplemente borra el token JWT del almacenamiento local en el cliente (como `localStorage` o `sessionStorage` en una aplicación web).
   - Si el token no se almacena en el cliente, no puede enviarse en solicitudes futuras.
   - **Contras**: Esto no es seguro por sí solo, ya que el token sigue siendo válido y puede ser usado si alguien lo ha obtenido antes del cierre de sesión.

#### Ejemplo de Configuración de Cierre de Sesión en Spring Security

Si estás utilizando autenticación basada en sesiones, Spring Security proporciona un endpoint de cierre de sesión (`/logout`) que se configura fácilmente. Aquí te muestro cómo configurarlo:

```java
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .authorizeRequests()
                .anyRequest().authenticated()
            .and()
            .logout()
                .logoutUrl("/logout")
                .logoutSuccessHandler(logoutSuccessHandler())
                .invalidateHttpSession(true) // Invalida la sesión HTTP
                .deleteCookies("JSESSIONID") // Borra la cookie de sesión
            .and()
            .csrf().disable();
    }

    private LogoutSuccessHandler logoutSuccessHandler() {
        return (request, response, authentication) -> {
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().flush();
        };
    }
}
```

- **`logoutUrl("/logout")`**: Define la URL de cierre de sesión. Puedes personalizar esta ruta.
- **`invalidateHttpSession(true)`**: Invalida la sesión en el servidor para evitar que el token de sesión siga activo.
- **`deleteCookies("JSESSIONID")`**: Borra la cookie de sesión, asegurando que la sesión no persista en el navegador.
- **`logoutSuccessHandler`**: Maneja la respuesta después del cierre de sesión, permitiéndote personalizar la respuesta del servidor.

#### Ejemplo de Cierre de Sesión en Aplicación JWT

En una configuración con JWT sin estado, el enfoque principal sería eliminar el token del cliente y, opcionalmente, mantener una lista de revocación.

1. **Eliminar el Token en el Cliente**:
   - En una aplicación web, puedes eliminar el token JWT almacenado en `localStorage` o `sessionStorage` durante el proceso de cierre de sesión.

   ```javascript
   // Cierre de sesión en el lado del cliente (JavaScript)
   localStorage.removeItem("jwtToken");
   window.location.href = "/login"; // Redirige al usuario a la página de inicio de sesión
   ```

2. **Lista de Revocación en el Servidor** (opcional):
   - Al recibir la solicitud de cierre de sesión, almacena el token JWT en una lista de revocación en el servidor. Luego, valida cada token de cada solicitud futura contra esta lista para evitar su uso.

#### Estrategia Adicional: Almacenamiento de JWT en Cookies Seguras

Si usas cookies para almacenar el JWT, configura la cookie para que expire después de un cierto tiempo, o asegúrate de eliminarla en el cierre de sesión. La cookie debe ser segura y tener los atributos `HttpOnly` y `Secure`:

```java
// Ejemplo de configuración de cookie en Spring Boot
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

public void logout(HttpServletResponse response) {
    Cookie cookie = new Cookie("JWT_TOKEN", null);
    cookie.setHttpOnly(true);
    cookie.setSecure(true); // Solo en HTTPS
    cookie.setPath("/");
    cookie.setMaxAge(0); // Expira inmediatamente
    response.addCookie(cookie);
}
```

### Resumen

1. **`@Secured`**:
   - Restringe el acceso a roles específicos en métodos con una sintaxis simple.
   - Habilitar con `@EnableGlobalMethodSecurity(securedEnabled = true)`.

2. **Cierre de Sesión Seguro**:
   - En aplicaciones JWT sin estado, borra el token en el cliente o implementa una lista de revocación.
   - Con sesiones, usa el endpoint `/logout` de Spring Security para invalidar la sesión y eliminar la cookie.
   - Opcionalmente, almacena JWT en cookies seguras (`HttpOnly`, `Secure`) para limitar su exposición.

El cierre de sesión seguro y un manejo adecuado de roles con `@Secured` proporcionan una capa de seguridad sólida y aseguran que el acceso esté restringido solo a usuarios autorizados en toda la aplicación.
