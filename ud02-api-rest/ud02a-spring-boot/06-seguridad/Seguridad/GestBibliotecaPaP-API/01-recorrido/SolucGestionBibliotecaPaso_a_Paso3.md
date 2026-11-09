**CORS (Cross-Origin Resource Sharing)** es fundamental en las aplicaciones modernas, especialmente cuando el **frontend y backend** están alojados en dominios diferentes, como en arquitecturas de aplicaciones web **SPA** (Single Page Applications). **CORS** permite que los navegadores gestionen y controlen la seguridad de las solicitudes HTTP entre distintos orígenes, y configurarlo correctamente en la aplicación garantiza que solo los dominios de confianza puedan acceder a los recursos de la API.

### ¿Por Qué Es Importante CORS?

1. **Protección Contra el Acceso No Autorizado**:
   - Sin una configuración de **CORS** adecuada, cualquier sitio web podría intentar realizar peticiones a tu API desde el navegador del usuario. Esto representa un riesgo porque permite que otros dominios accedan a datos sensibles sin tu control.
   - **CORS** actúa como una capa de seguridad que previene que otros sitios web realicen solicitudes a tu servidor sin permiso. Solo los orígenes permitidos pueden acceder a tus recursos.

2. **Evitar Ataques de CSRF (Cross-Site Request Forgery)**:
   - **CSRF** es un ataque donde un sitio malicioso induce al navegador de un usuario autenticado a realizar una acción no deseada en otra aplicación en la que está autenticado.
   - Aunque **JWT** u otras estrategias de autenticación se usan para mitigar estos ataques, **CORS** añade una capa de control adicional, restringiendo el acceso solo a dominios confiables.

3. **Seguridad en APIs Abiertas**:
   - Para APIs abiertas al público, **CORS** ayuda a controlar qué dominios específicos pueden acceder a la API, reduciendo el riesgo de explotación o uso no autorizado.
   - Al configurar CORS, puedes decidir permitir cualquier dominio en un entorno de desarrollo o permitir solo dominios específicos en producción, manteniendo la seguridad de los datos.

4. **Compatibilidad y Control con SPA y Aplicaciones de Múltiples Dominios**:
   - En aplicaciones modernas, es común tener el frontend y backend separados (por ejemplo, una aplicación de frontend en React y un backend en Spring Boot). Como estos se alojan en dominios diferentes, **CORS** permite que el frontend acceda al backend de manera segura.
   - Sin una configuración adecuada de **CORS**, el navegador bloqueará estas solicitudes, y la aplicación no funcionará correctamente. Configurar **CORS** garantiza que solo los dominios de frontend conocidos (como `http://localhost:3000` o el dominio de producción del frontend) puedan interactuar con la API.

5. **Mejora la Experiencia de Desarrollo y Pruebas**:
   - Configurar **CORS** en entornos de desarrollo, pruebas, y producción con diferentes orígenes permitidos facilita la integración y el trabajo en equipo.
   - Permitir todos los orígenes en desarrollo o usar una configuración menos restrictiva (por ejemplo, `*`) facilita el trabajo en local, mientras que en producción puedes limitar el acceso a dominios específicos.

### ¿Qué Pasa si No Configuras CORS en la Aplicación?

- **Bloqueo de Solicitudes en el Navegador**: Los navegadores modernos implementan políticas de seguridad estrictas, bloqueando automáticamente las solicitudes a diferentes dominios sin la configuración CORS adecuada. Sin CORS, una aplicación frontend no puede acceder a la API backend en otro dominio.

- **Falta de Control de Acceso en APIs Públicas**: Sin una configuración de CORS, cualquier dominio puede hacer solicitudes a tu API. Esto es especialmente riesgoso si tu API maneja datos confidenciales o tiene endpoints que no deberían ser accesibles a través de cualquier origen.

### Cómo CORS Complementa la Seguridad con JWT y Spring Security

**CORS** se configura para controlar qué dominios pueden realizar solicitudes a la API, mientras que **JWT** y **Spring Security** gestionan la autenticación y autorización de los usuarios. En este flujo:
- **CORS** filtra las solicitudes según el origen antes de que lleguen a los endpoints de la API.
- **JWT** valida la autenticidad de la solicitud después de que ha pasado la verificación de CORS.

Juntos, **CORS** y **JWT**:
- Permiten a la aplicación decidir quién puede realizar una solicitud y qué usuarios están autorizados para acceder a cada recurso.
- Aseguran que solo solicitudes válidas y autorizadas puedan acceder a los recursos protegidos.

### Resumen

Configurar **CORS** es esencial para:
- Controlar el acceso a los recursos de tu API desde dominios específicos.
- Proteger contra ataques de CSRF.
- Asegurar la correcta integración de frontend y backend en entornos de desarrollo y producción.
- Mejorar la seguridad general de la API y la experiencia de desarrollo.

**CORS**, cuando se configura adecuadamente junto con **JWT y Spring Security**, se convierte en una herramienta poderosa para mantener segura tu aplicación, controlando el acceso y protegiendo los datos frente a solicitudes maliciosas o no autorizadas.

**CORS (Cross-Origin Resource Sharing)** es fundamental en las aplicaciones modernas, especialmente cuando el **frontend y backend** están alojados en dominios diferentes, como en arquitecturas de aplicaciones web **SPA** (Single Page Applications). **CORS** permite que los navegadores gestionen y controlen la seguridad de las solicitudes HTTP entre distintos orígenes, y configurarlo correctamente en la aplicación garantiza que solo los dominios de confianza puedan acceder a los recursos de la API.

### ¿Por Qué Es Importante CORS?

1. **Protección Contra el Acceso No Autorizado**:
   - Sin una configuración de **CORS** adecuada, cualquier sitio web podría intentar realizar peticiones a tu API desde el navegador del usuario. Esto representa un riesgo porque permite que otros dominios accedan a datos sensibles sin tu control.
   - **CORS** actúa como una capa de seguridad que previene que otros sitios web realicen solicitudes a tu servidor sin permiso. Solo los orígenes permitidos pueden acceder a tus recursos.

2. **Evitar Ataques de CSRF (Cross-Site Request Forgery)**:
   - **CSRF** es un ataque donde un sitio malicioso induce al navegador de un usuario autenticado a realizar una acción no deseada en otra aplicación en la que está autenticado.
   - Aunque **JWT** u otras estrategias de autenticación se usan para mitigar estos ataques, **CORS** añade una capa de control adicional, restringiendo el acceso solo a dominios confiables.

3. **Seguridad en APIs Abiertas**:
   - Para APIs abiertas al público, **CORS** ayuda a controlar qué dominios específicos pueden acceder a la API, reduciendo el riesgo de explotación o uso no autorizado.
   - Al configurar CORS, puedes decidir permitir cualquier dominio en un entorno de desarrollo o permitir solo dominios específicos en producción, manteniendo la seguridad de los datos.

4. **Compatibilidad y Control con SPA y Aplicaciones de Múltiples Dominios**:
   - En aplicaciones modernas, es común tener el frontend y backend separados (por ejemplo, una aplicación de frontend en React y un backend en Spring Boot). Como estos se alojan en dominios diferentes, **CORS** permite que el frontend acceda al backend de manera segura.
   - Sin una configuración adecuada de **CORS**, el navegador bloqueará estas solicitudes, y la aplicación no funcionará correctamente. Configurar **CORS** garantiza que solo los dominios de frontend conocidos (como `http://localhost:3000` o el dominio de producción del frontend) puedan interactuar con la API.

5. **Mejora la Experiencia de Desarrollo y Pruebas**:
   - Configurar **CORS** en entornos de desarrollo, pruebas, y producción con diferentes orígenes permitidos facilita la integración y el trabajo en equipo.
   - Permitir todos los orígenes en desarrollo o usar una configuración menos restrictiva (por ejemplo, `*`) facilita el trabajo en local, mientras que en producción puedes limitar el acceso a dominios específicos.

### ¿Qué Pasa si No Configuras CORS en la Aplicación?

- **Bloqueo de Solicitudes en el Navegador**: Los navegadores modernos implementan políticas de seguridad estrictas, bloqueando automáticamente las solicitudes a diferentes dominios sin la configuración CORS adecuada. Sin CORS, una aplicación frontend no puede acceder a la API backend en otro dominio.

- **Falta de Control de Acceso en APIs Públicas**: Sin una configuración de CORS, cualquier dominio puede hacer solicitudes a tu API. Esto es especialmente riesgoso si tu API maneja datos confidenciales o tiene endpoints que no deberían ser accesibles a través de cualquier origen.

### Cómo CORS Complementa la Seguridad con JWT y Spring Security

**CORS** se configura para controlar qué dominios pueden realizar solicitudes a la API, mientras que **JWT** y **Spring Security** gestionan la autenticación y autorización de los usuarios. En este flujo:
- **CORS** filtra las solicitudes según el origen antes de que lleguen a los endpoints de la API.
- **JWT** valida la autenticidad de la solicitud después de que ha pasado la verificación de CORS.

Juntos, **CORS** y **JWT**:
- Permiten a la aplicación decidir quién puede realizar una solicitud y qué usuarios están autorizados para acceder a cada recurso.
- Aseguran que solo solicitudes válidas y autorizadas puedan acceder a los recursos protegidos.

### Resumen

Configurar **CORS** es esencial para:
- Controlar el acceso a los recursos de tu API desde dominios específicos.
- Proteger contra ataques de CSRF.
- Asegurar la correcta integración de frontend y backend en entornos de desarrollo y producción.
- Mejorar la seguridad general de la API y la experiencia de desarrollo.

**CORS**, cuando se configura adecuadamente junto con **JWT y Spring Security**, se convierte en una herramienta poderosa para mantener segura tu aplicación, controlando el acceso y protegiendo los datos frente a solicitudes maliciosas o no autorizadas.


## Configurar **CORS (Cross-Origin Resource Sharing)** en una aplicación **Spring Boot** permite definir qué dominios pueden acceder a los recursos de la API. Esta configuración es especialmente útil cuando la API se consume desde un frontend alojado en un dominio distinto.

### Opciones para Configurar CORS en Spring Boot

1. **Configurar CORS a nivel global** en la aplicación, aplicándose a todos los endpoints.
2. **Configurar CORS en un controlador específico** si solo necesitas que ciertos endpoints acepten solicitudes de otros dominios.

### **Opción 1: Configurar CORS a Nivel Global**

Para aplicar la configuración CORS a todos los endpoints de la aplicación, crearemos una clase de configuración y definiremos un bean de `CorsConfigurationSource`.

#### **Clase de Configuración Global de CORS**

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
        config.addAllowedOrigin("http://localhost:3000"); // Dominio permitido (cambiar según el dominio de frontend)
        config.addAllowedHeader("*"); // Permite todos los headers
        config.addAllowedMethod("*"); // Permite todos los métodos (GET, POST, PUT, DELETE, etc.)

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return new CorsFilter(source);
    }
}
```

#### **Explicación de los Ajustes**

- **`addAllowedOrigin("http://localhost:3000")`**: Permite solicitudes desde el dominio especificado (por ejemplo, un frontend en React en `localhost:3000`). Puedes añadir más dominios llamando a `config.addAllowedOrigin()` varias veces o usando `config.setAllowedOrigins(List<String>)`.
- **`addAllowedHeader("*")`**: Permite todos los headers. Esto es útil para permitir el envío de headers personalizados.
- **`addAllowedMethod("*")`**: Permite todos los métodos HTTP (`GET`, `POST`, `PUT`, `DELETE`, etc.).

---

### **Opción 2: Configurar CORS en un Controlador Específico**

Si solo necesitas aplicar CORS a ciertos controladores o endpoints, puedes configurarlo directamente en el controlador con la anotación `@CrossOrigin`.

#### **Ejemplo de CORS en el Controlador**

```java
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/eventos")
@CrossOrigin(origins = "http://localhost:3000")
public class EventoController {

    @GetMapping
    public List<Evento> listarEventos() {
        // lógica para listar eventos
    }

    // Otros métodos del controlador
}
```

#### **Explicación de los Ajustes**

- **`@CrossOrigin(origins = "http://localhost:3000")`**: Permite el acceso a este controlador específico solo desde el dominio especificado (`localhost:3000`).
- **Aplicación de `@CrossOrigin` a nivel de método**: También puedes colocar `@CrossOrigin` en métodos individuales si solo necesitas que algunos endpoints estén accesibles desde otros dominios.

---

### **Opción 3: Configurar CORS en `SecurityConfig`**

Si tienes configurada la seguridad de Spring con **JWT**, también puedes configurar CORS dentro de la clase de configuración de seguridad.

#### **Configuración en `SecurityConfig`**

```java
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.cors().and()
            .csrf().disable()
            .authorizeRequests()
            .antMatchers("/authenticate", "/register").permitAll()
            .anyRequest().authenticated();

        return http.build();
    }
}
```

#### **Configuración de CORS en `WebConfig`**

Para que el ajuste de `http.cors()` en `SecurityConfig` funcione, se debe configurar el `CorsConfigurationSource` como en la **Opción 1**:

```java
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
public class WebConfig {

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedOrigin("http://localhost:3000");
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
```

---

### **Resumen**

1. **Nivel Global**: Usa una clase de configuración con `CorsConfigurationSource` para aplicar CORS a todos los endpoints.
2. **Nivel de Controlador**: Usa `@CrossOrigin` en controladores específicos para definir reglas por endpoint.
3. **Configuración en Seguridad (`SecurityConfig`)**: Integra CORS en `SecurityConfig` con un bean de configuración para gestionar dominios permitidos en endpoints seguros.

Con esta configuración de CORS en Spring Boot, la API permitirá solicitudes de otros dominios según los criterios especificados, como el dominio de frontend `localhost:3000` en desarrollo. Una vez configurado, ¡podemos avanzar con la autenticación JWT!

Configurar **CORS (Cross-Origin Resource Sharing)** correctamente es crucial en aplicaciones **Spring Boot** que utilizan **JWT** para autenticación, especialmente cuando el frontend y backend están en dominios diferentes. **CORS** permite controlar qué dominios pueden acceder a los recursos de la API, mientras que **JWT** garantiza que solo solicitudes autorizadas puedan acceder a recursos protegidos. Aquí explico cómo configurar CORS en **Spring Security** y su interacción con JWT.

### **Configuración de CORS en Spring Security**

La configuración de CORS en **Spring Security** puede hacerse de forma centralizada, permitiendo definir qué dominios pueden acceder a los endpoints protegidos. Existen diferentes formas de configurar CORS en **Spring Security**:

1. **Configurar CORS en `SecurityConfig`** (a nivel de seguridad).
2. **Configurar CORS a nivel global** (fuera de la configuración de seguridad).
3. **Configurar CORS en controladores específicos** (cuando solo ciertos endpoints deben permitir solicitudes de dominios externos).

#### **1. Configuración de CORS en `SecurityConfig`**

En `SecurityConfig`, se usa **`http.cors()`** para habilitar CORS en toda la configuración de seguridad de Spring Security. Este paso permite que las reglas de CORS se apliquen a las solicitudes autenticadas y no autenticadas.

```java
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.cors() // Habilita CORS
            .and()
            .csrf().disable()
            .authorizeRequests()
            .antMatchers("/authenticate", "/register").permitAll() // Endpoints públicos
            .anyRequest().authenticated() // Resto de endpoints necesitan autenticación
            .and()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS); // JWT es stateless

        return http.build();
    }
}
```

- **`http.cors()`**: Habilita CORS en toda la configuración de seguridad.
- **`sessionCreationPolicy(SessionCreationPolicy.STATELESS)`**: Configura Spring Security para no mantener sesiones en el servidor (JWT es stateless).

> **Nota**: `http.cors()` solo habilita el soporte de CORS, pero las reglas (permitir orígenes, métodos, etc.) deben definirse aparte en una configuración global.

#### **2. Configuración Global de CORS**

Para que la configuración de CORS funcione con Spring Security, se define un `CorsConfigurationSource` en una clase de configuración general de Spring Boot. Aquí es donde se especifican los dominios permitidos, los métodos, y los headers.

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
        config.addAllowedOrigin("http://localhost:3000"); // Dominio del frontend
        config.addAllowedHeader("*"); // Permite todos los headers
        config.addAllowedMethod("*"); // Permite todos los métodos HTTP
        config.setAllowCredentials(true); // Permite enviar cookies (opcional, según el flujo de autenticación)

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return new CorsFilter(source);
    }
}
```

- **`addAllowedOrigin("http://localhost:3000")`**: Define el dominio permitido. Para múltiples dominios, usa una lista o llama a `addAllowedOrigin()` varias veces.
- **`addAllowedHeader("*")`**: Permite todos los headers, incluidos los customizados, como el de autorización.
- **`addAllowedMethod("*")`**: Permite todos los métodos HTTP (`GET`, `POST`, etc.).
- **`setAllowCredentials(true)`**: Permite el envío de cookies. Es útil si el frontend necesita autenticarse usando credenciales de sesión, aunque en JWT suele ser innecesario.

#### **3. Configurar CORS en Controladores Específicos**

Si solo ciertos endpoints deben permitir CORS, se puede configurar directamente en los controladores usando `@CrossOrigin`.

```java
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/eventos")
@CrossOrigin(origins = "http://localhost:3000") // Permite solo desde el dominio del frontend
public class EventoController {

    @GetMapping
    public List<Evento> listarEventos() {
        // lógica para listar eventos
    }
}
```

- **`@CrossOrigin(origins = "http://localhost:3000")`**: Permite acceso desde `http://localhost:3000`. Si el origen no coincide con el especificado, el navegador bloquea la solicitud.

---

### **Integración de JWT con CORS**

El flujo de integración de **JWT** con **CORS** implica algunos pasos clave:

1. **Front-End Envía Solicitud con JWT**: El cliente (frontend) envía una solicitud HTTP incluyendo el token JWT en el header `Authorization`, usando el formato `Authorization: Bearer <token>`.

2. **Solicitud Preflight de CORS**: Cuando el frontend hace una solicitud HTTP con credenciales (como el header de autorización), el navegador envía una solicitud *preflight* `OPTIONS` al backend para verificar si el dominio, headers, y métodos están permitidos.

3. **Autorización con JWT Después de CORS**: El filtro CORS se aplica antes que el filtro de autorización JWT. Una vez que la solicitud supera la verificación de CORS, el **JwtRequestFilter** (definido en `SecurityConfig`) intercepta la solicitud para validar el token.

4. **Validación de Token JWT**: Si el token es válido, el **JwtRequestFilter** autentica al usuario, permitiéndole el acceso a los recursos protegidos. Si el token no es válido o está ausente, se bloquea el acceso.

#### **JwtRequestFilter Ejemplo: Validación del Token**

```java
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
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

            if (jwtUtil.validateToken(jwt, userDetails.getUsername())) {
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities()
                );
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        chain.doFilter(request, response);
    }
}
```

### **Resumen del Flujo CORS + JWT**

1. **CORS Preflight**: La solicitud `OPTIONS` se envía antes de las solicitudes que contienen credenciales (como el token JWT). Si la configuración de CORS permite el dominio, método y headers, la solicitud procede.
2. **JwtRequestFilter**: Una vez que se pasa el CORS, el filtro JWT valida el token y autoriza la solicitud según el rol del usuario.
3. **Seguridad y Permisos**: Si el token es válido, el usuario accede al recurso; si no, se deniega el acceso.

Este flujo asegura que solo los dominios permitidos pueden hacer solicitudes a la API, y solo los usuarios autenticados (con un token JWT válido) pueden acceder a los recursos protegidos.

Configurar **CORS** correctamente para admitir varias fuentes de solicitudes (navegadores, REST clients como **Insomnia** o **Postman**, etc.) es útil en varios escenarios. A continuación se explica cómo ajustar la configuración en **Spring Security** para cubrir cada caso.

---

### **1. Hacer Peticiones desde el Navegador sin Frameworks de Frontend (React, Vue, etc.)**

Si estás haciendo peticiones HTTP directamente desde el navegador (por ejemplo, usando **JavaScript puro** con `fetch` o `XMLHttpRequest`), la configuración de CORS sigue siendo necesaria porque las solicitudes entre distintos orígenes serán bloqueadas por el navegador si los dominios no están configurados.

Para permitir esto:
1. Configura **CORS** para permitir el dominio de origen del frontend.
2. Asegúrate de que el **JWT** se envíe en el header `Authorization` en cada solicitud.

#### Ejemplo en `fetch` con JavaScript:
```javascript
fetch("http://localhost:8080/api/eventos", {
    method: "GET",
    headers: {
        "Authorization": "Bearer <tu_token_jwt>",
        "Content-Type": "application/json"
    }
})
.then(response => response.json())
.then(data => console.log(data))
.catch(error => console.error("Error:", error));
```

En la configuración de CORS en Spring Boot, permite el dominio del origen (`http://localhost`) en `CorsConfiguration`.

#### Ejemplo de Configuración:
```java
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.CorsFilter;

@Configuration
public class WebConfig {

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedOrigin("http://localhost"); // Permitir el origen desde el navegador
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return new CorsFilter(source);
    }
}
```

---

### **2. Permitir Solicitudes desde REST Clients como Insomnia o Postman**

Los clientes REST como **Insomnia** o **Postman** no requieren configuración de CORS, ya que no están sujetos a las restricciones de los navegadores. Sin embargo:
- Si estás usando JWT, asegúrate de enviar el token en el header `Authorization` con el valor `Bearer <token>`.
- La configuración de CORS en el servidor no afectará a estas herramientas, y podrás hacer solicitudes a tu API sin problemas, siempre y cuando tengas la autenticación configurada.

#### Ejemplo en Insomnia:
En **Insomnia** o **Postman**, configura el header de la siguiente forma:
- **Header**: `Authorization`
- **Valor**: `Bearer <tu_token_jwt>`

---

### **3. Permitir Solicitudes de Cualquier Origen (Configuración de CORS para “Permitir Todos”)**

En algunos casos, puedes necesitar permitir cualquier origen para que la API esté accesible sin restricciones (por ejemplo, en un entorno de pruebas o desarrollo).

Para esto, configura `CorsConfiguration` de la siguiente manera:

```java
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.CorsFilter;

@Configuration
public class WebConfig {

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedOrigin("*"); // Permitir cualquier origen
        config.addAllowedHeader("*"); // Permitir todos los headers
        config.addAllowedMethod("*"); // Permitir todos los métodos (GET, POST, etc.)

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return new CorsFilter(source);
    }
}
```

- **`addAllowedOrigin("*")`**: Permite cualquier origen. Es especialmente útil en desarrollo o pruebas.
- **`addAllowedHeader("*")`**: Permite todos los headers, necesario para admitir headers personalizados como `Authorization`.
- **`addAllowedMethod("*")`**: Permite todos los métodos HTTP (`GET`, `POST`, `PUT`, etc.).

> **Nota**: Aunque permitir cualquier origen es conveniente, no es recomendable en producción debido a preocupaciones de seguridad.

---

### **Consideraciones de Seguridad**

- **Uso en Producción**: En producción, limita `addAllowedOrigin` a dominios específicos para restringir el acceso solo a tus aplicaciones confiables.
- **JWT y CORS**: CORS se verifica antes de que el servidor procese la solicitud, de modo que si la solicitud supera la verificación de CORS, el servidor validará el JWT en el filtro de seguridad para autorizar al usuario.

Este enfoque garantiza que la API pueda recibir solicitudes desde navegadores, herramientas REST, y entornos controlados o públicos de forma segura y flexible según las necesidades del proyecto.


¡Claro! Vamos a cubrir ambos puntos: **asegurar los endpoints públicos** (permitir el acceso sin autenticación mientras se mantiene segura la API) y **restringir CORS a rutas específicas** para permitir solo ciertos orígenes en endpoints específicos.

### 1. **Asegurar Endpoints Públicos en Spring Security**

Para proteger algunos endpoints sin necesidad de autenticación mientras mantienes otros seguros, puedes configurar **Spring Security** de manera que permita el acceso a ciertas rutas específicas sin autenticación. Esto es útil para endpoints como `/register`, `/login` o `/public` que quieres que estén abiertos, y puedes configurarlo en la clase de seguridad de la siguiente forma:

#### Ejemplo de Configuración en `SecurityConfig`

```java
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .cors().and()  // Habilitar CORS en toda la aplicación
            .csrf().disable() // Deshabilitar CSRF para API REST
            .authorizeRequests()
            .antMatchers("/authenticate", "/register", "/public/**").permitAll() // Endpoints públicos
            .anyRequest().authenticated() // El resto requiere autenticación
            .and()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS); // Sin sesiones

        return http.build();
    }
}
```

- **`antMatchers("/authenticate", "/register", "/public/**").permitAll()`**: Permite el acceso sin autenticación a los endpoints públicos definidos (`/authenticate`, `/register`, y cualquier ruta bajo `/public`).
- **`anyRequest().authenticated()`**: Exige autenticación en el resto de los endpoints.
- **`sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)`**: Configura Spring Security para que no gestione sesiones en el servidor, ya que usamos JWT, que es stateless.

Con esta configuración, los endpoints especificados serán públicos, mientras que el resto de la API requerirá autenticación.

### 2. **Restringir CORS a Rutas Específicas**

Para limitar los orígenes permitidos en rutas específicas, Spring permite una configuración detallada de CORS en cada endpoint. Esto se puede lograr configurando **CORS** dentro de la clase de configuración de seguridad, o directamente en cada controlador usando la anotación `@CrossOrigin`.

#### Opción 1: Configuración de CORS Específica en `SecurityConfig`

Para aplicar restricciones de CORS solo a ciertas rutas, se puede configurar un `CorsConfigurationSource` en **SecurityConfig**. Aquí definimos diferentes reglas CORS para rutas específicas:

```java
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.CorsFilter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.cors().and() // Habilita CORS y aplica el bean corsConfigurationSource
            .csrf().disable()
            .authorizeRequests()
            .antMatchers("/public/**", "/authenticate", "/register").permitAll() // Endpoints públicos
            .anyRequest().authenticated();

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configPublic = new CorsConfiguration();
        configPublic.addAllowedOrigin("http://example.com"); // Dominio permitido para rutas públicas
        configPublic.addAllowedMethod("*");
        configPublic.addAllowedHeader("*");

        CorsConfiguration configPrivate = new CorsConfiguration();
        configPrivate.addAllowedOrigin("http://mysecureapp.com"); // Dominio seguro para rutas autenticadas
        configPrivate.addAllowedMethod("*");
        configPrivate.addAllowedHeader("*");

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/public/**", configPublic); // CORS para rutas públicas
        source.registerCorsConfiguration("/secure/**", configPrivate); // CORS para rutas seguras

        return source;
    }
}
```

- **`registerCorsConfiguration("/public/**", configPublic)`**: Permite acceso desde `http://example.com` solo para las rutas que empiezan con `/public/`.
- **`registerCorsConfiguration("/secure/**", configPrivate)`**: Permite acceso desde `http://mysecureapp.com` solo para las rutas que empiezan con `/secure/`.

#### Opción 2: Configuración de CORS en los Controladores con `@CrossOrigin`

Si necesitas aplicar configuraciones de CORS específicas en ciertos controladores o métodos, puedes usar `@CrossOrigin` directamente en ellos:

```java
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/eventos")
@CrossOrigin(origins = "http://example.com") // Permitir solo desde http://example.com
public class EventoController {

    @GetMapping("/public")
    @CrossOrigin(origins = "http://anotherexample.com") // Permitir solo desde otro dominio
    public String obtenerEventosPublicos() {
        return "Eventos públicos";
    }

    @GetMapping("/secure")
    public String obtenerEventosPrivados() {
        return "Eventos privados";
    }
}
```

- **`@CrossOrigin(origins = "http://example.com")`**: Permite el acceso al controlador completo solo desde `http://example.com`.
- **`@CrossOrigin` a nivel de método**: Permite definir CORS solo para un endpoint específico (como `"/public"` en este ejemplo).

> **Nota**: Usa `@CrossOrigin` para configuraciones específicas de CORS en un controlador o método, y `CorsConfigurationSource` en `SecurityConfig` para una configuración global en la aplicación.

---

### **Resumen: Endpoints Públicos y CORS en Spring Security**

1. **Asegurar Endpoints Públicos**: Configura los endpoints públicos en `SecurityConfig` usando `permitAll()` en las rutas que no necesitan autenticación.
2. **Restringir CORS a Rutas Específicas**:
   - Usa `CorsConfigurationSource` en `SecurityConfig` para definir orígenes permitidos en rutas específicas.
   - Alternativamente, usa `@CrossOrigin` en los controladores o métodos individuales para configuraciones más puntuales.

Esta configuración permite tener un control detallado de acceso a la API, con endpoints públicos y privados, y una configuración de CORS para definir orígenes permitidos en diferentes rutas. ¡Así se asegura un acceso seguro y flexible a la API!
