# Guía rápida: Swagger/OpenAPI en Spring Boot (con JWT)

## Dependencias (Maven/Gradle)
- **Maven**:
  ```xml
  <dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>2.5.0</version>
  </dependency>
  ```
- **Gradle (KTS)**:
  ```kotlin
  implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.5.0")
  ```
Requiere Spring Boot 3+ (Jakarta). Para WebFlux usa `...-webflux-ui`.

## Endpoints y UI
- Document JSON: `/v3/api-docs` (por defecto).
- UI Swagger: `/swagger-ui.html` y `/swagger-ui/index.html`.

## Configuración básica
Si quieres personalizar título/versión:
```java
@Configuration
public class OpenApiConfig {
  @Bean
  public OpenAPI apiInfo() {
    return new OpenAPI()
        .info(new Info()
            .title("API Gestión de Eventos")
            .description("Endpoints de eventos/organizadores/participantes")
            .version("1.0.0"));
  }
}
```

## Anotaciones útiles
- `@Operation(summary = "...", description = "...")` en métodos de controlador.
- `@Parameter` para describir path/query params.
- `@Schema` en DTOs para ejemplos/formatos.
- `@SecurityRequirement(name = "bearerAuth")` para marcar endpoints que requieren JWT.

## Seguridad (JWT) en Swagger
1. Declara el esquema de seguridad:
   ```java
   @Bean
   public OpenAPI apiInfo() {
     return new OpenAPI()
       .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
       .components(new Components().addSecuritySchemes("bearerAuth",
         new SecurityScheme()
           .name("bearerAuth")
           .type(SecurityScheme.Type.HTTP)
           .scheme("bearer")
           .bearerFormat("JWT")))
       .info(new Info().title("API Gestión de Eventos").version("1.0.0"));
   }
   ```
2. Marca los controladores/operaciones protegidas con `@SecurityRequirement(name = "bearerAuth")`.
3. En la UI, pulsa “Authorize” y pega el token (`Bearer <jwt>`).

## Ajustes de seguridad (Spring Security)
- Permitir acceso sin autenticación a Swagger y docs:
  ```java
  http
    .authorizeHttpRequests(auth -> auth
      .requestMatchers(
        "/v3/api-docs/**",
        "/swagger-ui.html",
        "/swagger-ui/**"
      ).permitAll()
      .anyRequest().authenticated());
  ```
- CORS: ya cubierto en la config JWT; Swagger UI usa `Authorization` header.

## Buenas prácticas
- No expongas swagger en producción salvo tras protección (auth/restricción por red).
- Usa DTOs anotados para generar esquemas claros; evita exponer entidades.
- Versiona la API en el path (`/api/v1/...`) y refleja la versión en el `Info`.
- Si añades Actuator, excluye `/actuator/**` de Swagger o documéntalo aparte.

## Ejemplo de anotaciones en controladores
```java
@RestController
@RequestMapping("/api/v1/eventos")
@SecurityRequirement(name = "bearerAuth")
public class EventoController {

  @Operation(summary = "Lista paginada de eventos",
             description = "Devuelve eventos en formato DTO; requiere rol autenticado")
  @GetMapping
  public Page<EventoResponseDTO> listar(Pageable pageable) { ... }

  @Operation(summary = "Crear evento",
             description = "Solo ORGANIZADOR puede crear eventos")
  @PostMapping
  @PreAuthorize("hasRole('ORGANIZADOR')")
  public ResponseEntity<EventoResponseDTO> crear(
        @RequestBody @Valid EventoRequestDTO dto) { ... }
}
```
