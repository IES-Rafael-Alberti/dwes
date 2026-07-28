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

## Estado del material heredado

Los proyectos y documentos de `Seguridad/` permanecen fuera de la publicación mientras se revisan. Mezclan versiones antiguas de Spring Security, secretos de demostración, soluciones completas y formatos generados; no son referencias vigentes para el alumnado.

El contrato canónico de seguridad se consolidará a partir de Battleship y deberá cubrir autenticación, autorización, ownership, `401`/`403`, CORS, CSRF, gestión de claves y pruebas con filtros activos antes de cerrar UD2.

## Práctica del alumnado

El ejercicio [Gestión de eventos](../03-ejercicios/03-gestion-eventos/README.md)
aplica JWT, roles y CORS sobre una API existente. La
[sesión de seguridad de Battleship](../02-ejemplos/battleship/docs/07-seguridad-jwt.md)
es la referencia canónica para preparar esa práctica.
