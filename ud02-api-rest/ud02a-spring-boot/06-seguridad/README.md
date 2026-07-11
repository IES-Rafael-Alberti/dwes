# Seguridad en API REST con Spring Boot

## Arquitectura general

La seguridad en las APIs REST de la unidad se apoya en **Spring Security** + **JWT** siguiendo el patrón de filtros (`SecurityFilterChain`). El flujo general es:

```
Request → CORS filter → SecurityFilterChain → JwtAuthFilter → Controller
                                  ↓
                          (si no hay token → 401)
```

### Componentes clave

1. **SecurityFilterChain**: Define qué rutas son públicas y cuáles requieren autenticación. Se configura en una clase `@Configuration` con `SecurityFilterChain`.
2. **JwtAuthFilter**: Filtro personalizado que intercepta cada request, extrae el token JWT del header `Authorization`, lo valida y construye el `Authentication` para el contexto de Spring Security.
3. **UserDetailsService**: Carga los datos del usuario desde la BD y los mapea al modelo de Spring Security (`UserDetails`).
4. **JwtUtil**: Utilidad para generar, firmar y validar tokens JWT (jjwt 0.12.5+).
5. **AuthController**: Endpoints `POST /auth/login`, `POST /auth/register`, `GET /auth/me`.

## CORS

Configuración típica para permitir peticiones desde un frontend Angular/React:

```java
@Bean
public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
        .cors(cors -> cors.configurationSource(corsConfigurationSource()))
        // ...
    return http.build();
}

@Bean
public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration config = new CorsConfiguration();
    config.setAllowedOrigins(List.of("http://localhost:4200"));
    config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
    config.setAllowedHeaders(List.of("Authorization", "Content-Type"));
    config.setAllowCredentials(true);
    // ...
}
```

## Perfiles y propiedades

| Propiedad | Descripción | Perfil |
|-----------|-------------|--------|
| `jwt.secret` | Clave secreta para firmar JWT | dev/test/prod |
| `jwt.expiration` | Tiempo de expiración (ms) | dev/test/prod |
| `app.cors.allowed-origins` | Orígenes permitidos CORS | dev/test/prod |
| `server.ssl.enabled` | HTTPS habilitado | prod |
| `spring.h2.console.enabled` | Consola H2 | dev |

Ejemplo `application-dev.properties`:
```properties
jwt.secret=clave-secreta-temporal-solo-para-desarrollo
jwt.expiration=86400000
app.cors.allowed-origins=http://localhost:4200
```

Ejemplo `application-prod.properties`:
```properties
jwt.secret=${JWT_SECRET}
jwt.expiration=3600000
app.cors.allowed-origins=https://mialumno.centro.edu
server.ssl.enabled=true
```

## Referencias

- [`Seguridad/`](./Seguridad/): Proyectos de ejemplo completos (Ejemplo1 JWT básico, GestionBiblioteca)
- [`seguridadSpringBoot-V1.md`](./Seguridad/Ejemplo1/seguridadSpringBoot-V1.md): Guía completa de seguridad (2006 l.)
- [`SeguridadSpringBoot-V2.md`](./Seguridad/Ejemplo1/SeguridadSpringBoot-V2.md): Versión condensada (406 l.)
- Documentación relacionada en `02-ejemplos/Documentacion-SpringBoot/02-intermedio/07-cors/`
