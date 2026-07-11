# Migración de Spring Boot 3 a Spring Boot 4

En la empresa te vas a encontrar proyectos en **Spring Boot 3** durante años. Este documento recoge los cambios importantes al migrar a SB4 para que puedas reconocerlos y actuar en ambos sentidos.

## 1. Cambios en el POM

### Starter web partido

| SB3 | SB4 |
|-----|-----|
| `spring-boot-starter-web` | `spring-boot-starter-webmvc` |
| (incluido en starter-test) | `spring-boot-starter-webmvc-test` (explícito) |

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-webmvc</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-webmvc-test</artifactId>
    <scope>test</scope>
</dependency>
```

### Flag `-parameters` obligatorio

SB4 **necesita** los nombres de los parámetros en tiempo de ejecución. Sin esto, `@RequestParam` sin `name` explícito lanza un 404 silencioso.

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-compiler-plugin</artifactId>
    <configuration>
        <source>21</source>
        <target>21</target>
        <parameters>true</parameters>   <!-- ← necesario -->
    </configuration>
</plugin>
```

En SB3 esto funcionaba sin `-parameters` porque Spring Boot lo configuraba automáticamente al usar el parent POM. Con BOM (sin parent), hay que declararlo.

### Hibernate 7 cambió la estrategia de nombres

En SB3, Hibernate convertía camelCase a snake_case automáticamente (`taskTitle` → `task_title`). En Hibernate 7 (SB4) ya no: usa el nombre exacto del campo.

| SB3 | SB4 |
|-----|-----|
| `private String taskTitle;` → columna `task_title` | `private String taskTitle;` → columna `taskTitle` |

Si necesitas snake_case, usa `@Column(name = "task_title")` explícito.

## 2. Anotaciones eliminadas o renombradas

### `@DataJpaTest` — eliminado

No existe en SB4. No hay un starter sustituto. Usa `@SpringBootTest`:

```java
// SB3 — eliminado
@WebMvcTest(TaskControllerV4.class)

// SB4
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = "spring.jpa.defer-datasource-initialization=true")
class RepoTest {
    @Autowired TaskRepository repo;
}
```

La property `defer-datasource-initialization=true` evita que `data.sql` se ejecute antes de que Hibernate cree las tablas.

### `@WebMvcTest` — nuevo paquete

```java
// SB3
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

// SB4
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
```

### `@MockBean` → `@MockitoBean`

```java
// SB3
import org.springframework.boot.test.mock.mockito.MockBean;

// SB4
import org.springframework.test.context.bean.override.mockito.MockitoBean;
```

### Ojo: `@SpringBootTest` con `@DataJpaTest` anterior

Si ves código SB3 con `@DataJpaTest`, al migrar ten en cuenta que:
- `@DataJpaTest` configuraba H2, desactivaba la inicialización automática de `data.sql`, y cargaba solo la capa JPA.
- `@SpringBootTest` carga el contexto completo. Es más lento y ejecuta `data.sql` de `src/main/resources` a menos que difieras la inicialización.

## 3. Jackson 3

SB4 migró a Jackson 3.x. La mayoría de las APIs son compatibles hacia atrás, pero:
- Algunas clases de Jackson se movieron de paquete (ej. `JsonProcessingException`).
- Si usas serialización/deserialización personalizada, revisa los imports.

## 4. Flyway

En SB3, Flyway se auto-configuraba con `spring-boot-starter-web`. En SB4 se eliminó `FlywayAutoConfiguration` del autoconfigure global:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-flyway</artifactId>
</dependency>
```

## 5. Resumen: checklist de migración SB3 → SB4

- [ ] Cambiar `spring-boot-starter-web` → `spring-boot-starter-webmvc`
- [ ] Añadir `spring-boot-starter-webmvc-test` en test
- [ ] Añadir `<parameters>true</parameters>` en maven-compiler-plugin
- [ ] Cambiar `@WebMvcTest` al nuevo paquete
- [ ] Cambiar `@MockBean` → `@MockitoBean`
- [ ] Cambiar `@DataJpaTest` → `@SpringBootTest` con `defer-datasource-initialization=true`
- [ ] Si usas Flyway, añadir `spring-boot-starter-flyway` explícito
- [ ] Revisar `@Column(name = "...")` si dependías de la naming strategy de Hibernate
- [ ] Revisar imports de Jackson si usas serialización personalizada
- [ ] Java 21+ (SB4 requiere Java 21 como mínimo)
- [ ] Actualizar versión de Spring Boot BOM a `4.0.5` (o la vigente)

## 6. Lo que NO cambia

| Concepto | Sigue igual |
|----------|-------------|
| Anotaciones REST (`@RestController`, `@GetMapping`, `@PostMapping`, etc.) | Mismas anotaciones, mismos imports |
| `ResponseEntity` | Sin cambios |
| `@Service`, `@Repository`, `@Component` | Sin cambios |
| `@SpringBootApplication` | Sin cambios |
| `application.properties` / `application.yml` | Mismas propiedades (algunas nuevas, pero compatibles) |
| JPA (`@Entity`, `@Id`, `@OneToMany`, etc.) | Sin cambios (Hibernate 7, pero API compatible) |
| Validación (`@Valid`, `@NotBlank`, etc.) | Sin cambios |
| MockMvc | Sin cambios en la API de uso |
