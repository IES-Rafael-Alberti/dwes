# 🧾 Anexo — Anotaciones utilizadas en Spring Boot (hasta Cap. 3)

## 🔹 General del framework

| Anotación                  | Significado / Uso                                                                                                                                            |
| -------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------ |
| **@SpringBootApplication** | Marca la clase principal. Agrupa `@Configuration`, `@EnableAutoConfiguration` y `@ComponentScan`. Indica a Spring Boot dónde empezar a escanear componentes. |
| **@Component**             | Declara una clase gestionada por el contenedor Spring. Cualquier clase anotada se detecta y puede inyectarse con `@Autowired`.                               |
| **@Configuration**         | Indica que una clase define beans de configuración. Equivale a ficheros XML de configuración antiguos.                                                       |
| **@Bean**                  | Declara un método que devuelve un objeto a registrar como bean en el contexto de Spring.                                                                     |
| **@Autowired**             | Inyecta automáticamente un bean disponible en el contexto (por tipo). Se puede usar en constructores, campos o setters.                                      |
| **@Value**                 | Inyecta un valor literal o de configuración (`application.yml`) en un campo.                                                                                 |
| **@Profile("...")**        | Asocia un bean o configuración a un perfil concreto (`dev`, `prod`, etc.).                                                                                   |

---

## 🔹 Controladores y capa web (Spring MVC)

| Anotación                                                                                 | Significado / Uso                                                                                                                                    |
| ----------------------------------------------------------------------------------------- | ---------------------------------------------------------------------------------------------------------------------------------------------------- |
| **@RestController**                                                                       | Combina `@Controller` + `@ResponseBody`. Indica que la clase gestiona peticiones HTTP y sus métodos devuelven directamente datos (normalmente JSON). |
| **@Controller**                                                                           | Indica que la clase gestiona peticiones web y devuelve vistas (HTML). En APIs REST se sustituye por `@RestController`.                               |
| **@RequestMapping**                                                                       | Define la ruta base y/o el método HTTP a nivel de clase o método. Puede combinarse con `@GetMapping`, `@PostMapping`, etc.                           |
| **@GetMapping**, **@PostMapping**, **@PutMapping**, **@DeleteMapping**, **@PatchMapping** | Versiones específicas de `@RequestMapping` para cada verbo HTTP.                                                                                     |
| **@RequestParam**                                                                         | Obtiene parámetros de la URL (query string).                                                                                                         |
| **@PathVariable**                                                                         | Obtiene partes de la URL indicadas como variables (`/users/{id}`).                                                                                   |
| **@RequestBody**                                                                          | Vincula el cuerpo de la petición (JSON) con un objeto Java.                                                                                          |
| **@RequestHeader**                                                                        | Extrae valores de cabeceras HTTP.                                                                                                                    |
| **@ResponseStatus**                                                                       | Permite establecer el código HTTP devuelto sin usar `ResponseEntity`.                                                                                |
| **@CrossOrigin**                                                                          | Habilita CORS (acceso entre dominios) para controladores o métodos específicos.                                                                      |
| **@ControllerAdvice**                                                                     | Define una clase global para manejar excepciones y consejos aplicables a todos los controladores.                                                    |
| **@ExceptionHandler**                                                                     | Dentro de un `@ControllerAdvice`, captura excepciones específicas y define la respuesta HTTP a devolver.                                             |

---

## 🔹 Validación de datos

| Anotación                | Significado / Uso                                                                       |
| ------------------------ | --------------------------------------------------------------------------------------- |
| **@Valid**               | Indica que el objeto recibido debe validarse usando las reglas de `jakarta.validation`. |
| **@NotBlank**            | Valida que una cadena no sea nula ni vacía.                                             |
| **@NotNull**             | El valor no puede ser nulo.                                                             |
| **@Min**, **@Max**       | Valida valores numéricos mínimos o máximos.                                             |
| **@Email**, **@Pattern** | Valida formato de email o expresión regular.                                            |
| **@Size**                | Valida longitud mínima/máxima de cadenas o colecciones.                                 |

---

## 🔹 Persistencia (JPA / Hibernate)

| Anotación                                                      | Significado / Uso                                                                           |
| -------------------------------------------------------------- | ------------------------------------------------------------------------------------------- |
| **@Entity**                                                    | Marca una clase como entidad JPA (se mapea a una tabla).                                    |
| **@Id**                                                        | Indica el campo que actúa como clave primaria.                                              |
| **@GeneratedValue**                                            | Especifica la estrategia de generación del identificador (auto, identidad, secuencia…).     |
| **@Column**                                                    | Personaliza el mapeo de un campo (nombre, longitud, nullabilidad, etc.).                    |
| **@Table**                                                     | Permite definir el nombre de la tabla o esquemas específicos.                               |
| **@Transient**                                                 | Indica que un campo no debe persistirse en la base de datos.                                |
| **@Enumerated**                                                | Define cómo se guardan los `enum` (por nombre o ordinal).                                   |
| **@Embeddable**, **@Embedded**, **@EmbeddedId**                | Definen objetos valor integrados o claves compuestas. *(se verán más adelante con records)* |
| **@ManyToOne**, **@OneToMany**, **@OneToOne**, **@ManyToMany** | Relaciones entre entidades (no usadas aún, se verán más adelante).                          |
| **@JoinColumn**, **@JoinTable**                                | Configura columnas de unión en relaciones.                                                  |

---

## 🔹 Servicios y capa de negocio

| Anotación          | Significado / Uso                                                                                                                                         |
| ------------------ | --------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **@Service**       | Marca una clase como componente de la capa de negocio. Se detecta automáticamente y puede inyectarse.                                                     |
| **@Transactional** | Define límites transaccionales (inicio/fin de transacción). Garantiza que un conjunto de operaciones se ejecuten como una sola unidad.                    |
| **@Repository**    | Indica que la clase gestiona el acceso a datos. También traduce excepciones específicas de JPA a excepciones genéricas de Spring (`DataAccessException`). |

---

## 🔹 Pruebas y depuración

| Anotación           | Significado / Uso                                                                  |
| ------------------- | ---------------------------------------------------------------------------------- |
| **@SpringBootTest** | Ejecuta una prueba integrando todo el contexto Spring Boot.                        |
| **@DataJpaTest**    | Prueba solo la capa de persistencia (configura H2 automáticamente).                |
| **@WebMvcTest**     | Prueba solo la capa web (controladores).                                           |
| **@MockitoBean**    | Crea un mock gestionado por Spring para sustituir un bean real durante una prueba en Spring Boot 4. |
| **@Test**           | Indica un método de prueba (JUnit).                                                |

---

## 🔹 Lombok

| Anotación                                       | Significado / Uso                                                   |
| ----------------------------------------------- | ------------------------------------------------------------------- |
| **@Getter**, **@Setter**                        | Genera automáticamente los getters/setters.                         |
| **@NoArgsConstructor**, **@AllArgsConstructor** | Genera constructores sin o con todos los argumentos.                |
| **@Builder**                                    | Crea un patrón *builder* para construir instancias de forma fluida. |
| **@ToString**, **@EqualsAndHashCode**           | Genera `toString()` y `equals()`/`hashCode()`.                      |

---

## 🔹 Swagger / OpenAPI

| Anotación        | Significado / Uso                                                                         |
| ---------------- | ----------------------------------------------------------------------------------------- |
| **@Operation**   | Describe un endpoint concreto en la documentación OpenAPI (título, resumen, parámetros…). |
| **@ApiResponse** | Documenta los posibles códigos de respuesta.                                              |
| **@Schema**      | Define el modelo de datos de entrada/salida en la documentación.                          |

*(Estas tres no se usan aún, pero aparecerán en la UD4 con Swagger).*
