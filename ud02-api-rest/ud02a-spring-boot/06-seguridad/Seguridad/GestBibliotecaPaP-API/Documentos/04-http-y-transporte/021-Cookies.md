
Para almacenar el token JWT en una cookie, se pueden seguir estos pasos. Esto ayuda a mantener el token accesible desde el navegador sin necesidad de gestionarlo manualmente en el cliente, pero también introduce ciertas consideraciones de seguridad.


### **1. Modificar el Endpoint para Autenticar y Enviar el JWT en una Cookie**
Para enviar el token JWT en una cookie en lugar de en el cuerpo de la respuesta, debes modificar el controlador de autenticación para crear y agregar una cookie HTTP a la respuesta.

#### **Controlador de Autenticación**

Actualiza el método de autenticación para incluir el JWT en una cookie:

```java
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

// Endpoint de autenticación
@PostMapping("/authenticate")
public void authenticate(@RequestBody LoginUsuarioDTO request, HttpServletResponse response) {
    Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
    String token = jwtUtil.generateToken((CustomUserDetails) authentication.getPrincipal());

    // Crear la cookie
    Cookie jwtCookie = new Cookie("jwt", token);
    jwtCookie.setHttpOnly(true); // Solo accesible desde el servidor
    jwtCookie.setSecure(true); // Solo en conexiones HTTPS
    jwtCookie.setPath("/"); // Disponible para toda la aplicación
    jwtCookie.setMaxAge(60 * 60 * 10); // Validez de 10 horas

    // Agregar la cookie a la respuesta
    response.addCookie(jwtCookie);
}
```

---

### **2. Configurar CORS para Incluir Cookies**

Para que el navegador envíe las cookies junto con las solicitudes, debes habilitar `credentials` en la configuración de CORS.

#### **Configuración de CORS**

En tu archivo `WebConfig.java` o donde configures CORS:

```java
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class WebConfig {

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedOrigin("http://localhost:3000"); // Cambiar según el dominio del frontend
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        config.setAllowCredentials(true); // Habilitar envío de cookies

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return new CorsFilter(source);
    }
}
```

---

### **3. Extraer el Token JWT desde la Cookie en el Filtro JWT**

Modifica tu filtro `JwtRequestFilter` para buscar el JWT en las cookies.

```java
@Override
protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
        throws ServletException, IOException {

    // Obtener el token desde la cookie
    String jwt = null;
    if (request.getCookies() != null) {
        for (Cookie cookie : request.getCookies()) {
            if ("jwt".equals(cookie.getName())) {
                jwt = cookie.getValue();
            }
        }
    }

    if (jwt != null && SecurityContextHolder.getContext().getAuthentication() == null) {
        String username = jwtUtil.extractUsername(jwt);
        UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

        if (jwtUtil.validateToken(jwt, userDetails)) {
            var authentication = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
    }

    chain.doFilter(request, response);
}
```

---

### **4. Ventajas y Desventajas de Usar Cookies**

#### **Ventajas**:
- **Automatización**: Las cookies son gestionadas automáticamente por el navegador, eliminando la necesidad de manejar tokens manualmente en el cliente.
- **HttpOnly**: Reduce la exposición del token en scripts maliciosos (prevención de XSS).

#### **Desventajas**:
- **CSRF**: Las cookies son vulnerables a ataques de falsificación de solicitudes entre sitios (CSRF). Se recomienda implementar protección CSRF con tokens adicionales.
- **HTTPS Requerido**: Es esencial habilitar `secure=true` para que las cookies solo sean enviadas a través de conexiones HTTPS.

---

### **5. Probando con Insomnia**

Si deseas probar con **Insomnia** o **Postman**:
1. Realiza una solicitud `POST` al endpoint `/authenticate`.
2. Observa las cookies en la respuesta.
3. Incluye la cookie `jwt` manualmente en futuras solicitudes como encabezado o configurando el cliente para que las maneje automáticamente.

Esto completa la integración del token JWT con cookies en tu aplicación.
