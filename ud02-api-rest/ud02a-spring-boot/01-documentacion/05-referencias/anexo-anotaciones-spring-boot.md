# Anotaciones — Spring, Spring Boot, JPA y validación

Referencia para localizar **quién interpreta una anotación, qué activa y qué no
garantiza por sí sola**. Reúne familias que utilizamos juntas, pero que no
pertenecen todas a Spring Boot.

| Familia | Paquete habitual / responsabilidad |
|---|---|
| Spring y MVC | `org.springframework…`: componentes, configuración, web y transacciones |
| Spring Boot | `org.springframework.boot…`: arranque y autoconfiguración, incluidos sus slices de test |
| Jakarta Persistence | `jakarta.persistence…`: entidades, asociaciones y mapeo |
| Bean Validation | `jakarta.validation…`: validación de datos al ejecutarse el mecanismo correspondiente |
| JUnit / Mockito / Spring Test | [Diccionarios de testing](../03-pruebas/diccionarios/README.md) |
| Lombok | `lombok…`: [generación de código](anexo-lombok.md) durante la compilación |

Las anotaciones de referencia avanzada no indican que todo ese contenido ya
se haya impartido. Se consulta lo necesario para el bloque actual.

## 🔹 General del framework

| Anotación                  | Significado / Uso                                                                                                                                            |
| -------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------ |
| **@SpringBootApplication** | Combina `@SpringBootConfiguration`, `@EnableAutoConfiguration` y `@ComponentScan`. Su paquete sirve de referencia al escaneo habitual; no registra cualquier clase de cualquier paquete. |
| **@Component**             | Declara un candidato a componente; debe quedar incluido en el escaneo o registro correspondiente para ser un bean. |
| **@Configuration**         | Declara una clase de configuración, normalmente con métodos `@Bean`; sus opciones controlan también cómo se interceptan llamadas a esos métodos. |
| **@Bean**                  | Declara un método que devuelve un objeto a registrar como bean en el contexto de Spring.                                                                     |
| **@Autowired**             | Inyecta automáticamente un bean disponible en el contexto (por tipo). Se puede usar en constructores, campos o setters.                                      |
| **@Value**                 | Inyecta un valor literal o de configuración (`application.yml`) en un campo.                                                                                 |
| **@Profile("...")**        | Asocia un bean o configuración a un perfil concreto (`dev`, `prod`, etc.).                                                                                   |
| **@ComponentScan** | Define paquetes y criterios de búsqueda de componentes; no es necesario repetirlo si el escaneo de la aplicación ya cubre las clases. |
| **@Primary** | Prioriza un candidato cuando hay varios beans compatibles; no elimina los demás. |
| **@Qualifier("...")** | Restringe qué candidato se inyecta en ese punto cuando el tipo no basta. |
| **@PostConstruct**, **@PreDestroy** | Callbacks de inicialización/destrucción de un bean gestionado, de `jakarta.annotation`; no son callbacks JPA ni garantizan por sí solos una estrategia correcta de carga de datos. |

Con un único constructor, Spring puede usar inyección por constructor sin
`@Autowired`. `@RequiredArgsConstructor`, si aparece, es Lombok generando esa
firma; no una segunda infraestructura de inyección.

---

## 🔹 Controladores y capa web (Spring MVC)

| Anotación                                                                                 | Significado / Uso                                                                                                                                    |
| ----------------------------------------------------------------------------------------- | ---------------------------------------------------------------------------------------------------------------------------------------------------- |
| **@RestController**                                                                       | Combina `@Controller` + `@ResponseBody`. Indica que la clase gestiona peticiones HTTP y sus métodos devuelven directamente datos (normalmente JSON). |
| **@Controller** | Estereotipo web habitual para devolver nombres de vista; sus métodos también pueden devolver cuerpo con `@ResponseBody` o tipos de respuesta adecuados. |
| **@RequestMapping**                                                                       | Define la ruta base y/o el método HTTP a nivel de clase o método. Puede combinarse con `@GetMapping`, `@PostMapping`, etc.                           |
| **@GetMapping**, **@PostMapping**, **@PutMapping**, **@DeleteMapping**, **@PatchMapping** | Versiones específicas de `@RequestMapping` para cada verbo HTTP.                                                                                     |
| **@RequestParam** | Vincula parámetros de petición, como query o formulario según el caso; no deserializa un cuerpo JSON. |
| **@PathVariable**                                                                         | Obtiene partes de la URL indicadas como variables (`/users/{id}`).                                                                                   |
| **@RequestBody** | Lee el cuerpo mediante conversores HTTP, por ejemplo JSON a un DTO según `Content-Type`; no activa por sí solo Bean Validation. |
| **@RequestHeader**                                                                        | Extrae valores de cabeceras HTTP.                                                                                                                    |
| **@ResponseStatus**                                                                       | Permite establecer el código HTTP devuelto sin usar `ResponseEntity`.                                                                                |
| **@CrossOrigin** | Configura CORS para controladores/métodos. El origen incluye esquema, host y puerto; no sustituye autenticación o permisos. |
| **@ControllerAdvice** | Comparte manejo de excepciones, binding o atributos de modelo entre controladores seleccionados; no implica por sí solo que todo resultado se escriba como JSON. |
| **@ExceptionHandler**                                                                     | En un controlador o un advice, trata excepciones específicas y define la respuesta HTTP. Un handler local tiene prioridad sobre el global.          |
| **@ResponseBody** | Escribe el valor devuelto en el cuerpo con los conversores disponibles, en lugar de interpretarlo como nombre de vista. |
| **@RestControllerAdvice** | Advice con `@ResponseBody`: habitual para los errores JSON compartidos que ya se han trabajado en clase. |
| **@ModelAttribute** | Vincula o prepara un atributo del modelo, útil con formularios MVC; no equivale a un DTO JSON recibido mediante `@RequestBody`. |
| **@RequestPart** | Vincula una parte de una petición multipart, pudiendo convertir su contenido; se distingue de enviar un JSON ordinario como cuerpo completo. |
| **@CookieValue**, **@SessionAttribute** | Leen cookies o atributos de sesión; no crean por sí solos una política de autenticación. |

### Lectura de una firma

```java
@PostMapping
public ResponseEntity<GameResponseDTO> create(@Valid @RequestBody CreateGameDTO input) {
    return created(service.createGame(input));
}
```

`@PostMapping` selecciona la operación HTTP; `@RequestBody` convierte el cuerpo;
`@Valid` solicita la validación del DTO en ese flujo. `ResponseEntity` es una
clase de respuesta, **no una anotación**. Las variables, tipos y helper del
fragmento corresponden al controlador del juego.

---

## 🔹 Validación de datos

| Anotación                | Significado / Uso                                                                       |
| ------------------------ | --------------------------------------------------------------------------------------- |
| **@Valid**               | Indica que el objeto recibido debe validarse usando las reglas de `jakarta.validation`. |
| **@NotBlank**            | Exige una cadena no nula con al menos un carácter que no sea un espacio en blanco.       |
| **@NotNull**             | El valor no puede ser nulo.                                                             |
| **@Min**, **@Max**       | Valida valores numéricos mínimos o máximos.                                             |
| **@Email**, **@Pattern** | Valida formato de email o expresión regular.                                            |
| **@Size**                | Valida longitud mínima/máxima de cadenas o colecciones.                                 |
| **@NotEmpty** | Rechaza nulo y vacío en los tipos soportados; en texto, una cadena de espacios no está vacía. |
| **@Positive**, **@PositiveOrZero** | Restricción numérica de signo. |
| **@Past**, **@Future** | Restricción temporal sobre tipos soportados; escoger la variante inclusiva si se admite el presente. |
| **@Validated** | Variante de Spring para selección de grupos e integración de validación de métodos según el contexto; no añadirla a todos los tipos por costumbre. |

Varias restricciones, como `@Email`, `@Pattern` o `@Size`, permiten `null`:
si el valor es obligatorio se combina con una restricción apropiada. `@NotBlank`
no se aplica a cualquier tipo, sino a texto. `@Valid` permite validación en
cascada cuando se ejecuta el validador; anotar un objeto Java no hace que se valide
en cualquier llamada realizada con `new`.

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
| **@PrePersist**, **@PreUpdate**                                | Marcan callbacks del ciclo de vida de la entidad antes de su inserción o actualización; útiles, por ejemplo, para fechas. |
| **@Embeddable**, **@Embedded**, **@EmbeddedId** | Mapean tipos integrados o claves compuestas. Su diseño y las capacidades concretas del proveedor se estudian cuando el caso lo requiere. |
| **@ManyToOne**, **@OneToMany**, **@OneToOne**, **@ManyToMany** | Declaran asociaciones entre entidades; el bloque A desarrolla el 1:N y los bloques posteriores aplicarán otras cardinalidades. |
| **@JoinColumn**, **@JoinTable**                                | Configura columnas de unión en relaciones.                                                  |
| **@UniqueConstraint** | Declara una combinación única en el mapeo de tabla; `columnNames` utiliza nombres SQL. Con Flyway debe existir la restricción real en una migración. |
| **@Version** | Campo de versión para control optimista de modificaciones; no es un simple timestamp de creación ni un bloqueo universal. |
| **@PersistenceContext** | Inyecta un `EntityManager` gestionado en el contexto correspondiente. |
| **@OrderBy** | Ordena una asociación al recuperarla según atributos; no equivale a paginar un endpoint. |
| **@PostLoad**, **@PostPersist**, **@PreRemove** | Callbacks tras cargar/persistir o antes de eliminar mediante el ciclo JPA; no se ejecutan por el mero hecho de llamar a un setter. |

`mappedBy`, `fetch`, `cascade` y `orphanRemoval` son **atributos** de anotaciones,
no anotaciones independientes. `mappedBy` utiliza el nombre Java propietario;
`@JoinColumn(name=...)` utiliza una columna. El [bloque A](../04-persistencia/bloque-a/01-entidades-relaciones-y-ciclo-de-vida.md)
explica sus efectos y los problemas de carga/ciclo de vida.

`@PrePersist` hace que JPA invoque un método callback antes de insertar una
entidad nueva. El método no recibe parámetros y devuelve `void`; su nombre es
libre. Por ejemplo, si `createdAt` no se ha proporcionado, podemos asignarlo al
crear el registro:

```java
@PrePersist
void onCreate() {
    if (createdAt == null) {
        createdAt = LocalDateTime.now();
    }
}
```

La comprobación de `null` conserva una fecha que ya hubiera establecido el
programa. `@PreUpdate` se ejecuta antes de actualizar una entidad existente y
puede servir para mantener `updatedAt`; no sustituye a `@PrePersist` para la
creación. Estos callbacks pertenecen a JPA (`jakarta.persistence`), no son
anotaciones exclusivas de Spring Boot, y solo se ejecutan cuando la operación
pasa por el ciclo de persistencia de JPA.

`LocalDateTime.now()` usa el reloj y la zona predeterminada del proceso. Para
aplicaciones distribuidas conviene acordar una política temporal común, a menudo
guardar instantes UTC con `Instant` y convertirlos a la zona del usuario al
presentarlos. En el code-along aparece el callback en las entidades
[`Game` y `Attack`](../../02-ejemplos/battleship/docs/01-construccion/04b-reglas-de-negocio.md#12-entidades-completas-de-este-checkpoint).

Para campos de auditoría repetidos en varias entidades, Spring Data JPA ofrece
`@CreatedDate` y `@LastModifiedDate`. Requiere activar auditoría con
`@EnableJpaAuditing` y registrar `AuditingEntityListener`; es una opción más
centralizada, pero no es necesaria para una única fecha de creación ni sustituye
las reglas de negocio.

---

## 🔹 Servicios y capa de negocio

| Anotación          | Significado / Uso                                                                                                                                         |
| ------------------ | --------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **@Service**       | Marca una clase como componente de la capa de negocio. Se detecta automáticamente y puede inyectarse.                                                     |
| **@Transactional** | Configura el límite transaccional de los recursos gestionados por Spring cuando la llamada es interceptada por su infraestructura transaccional.           |
| **@Repository**    | Indica que la clase gestiona el acceso a datos. También traduce excepciones específicas de JPA a excepciones genéricas de Spring (`DataAccessException`). |

La traducción de excepciones requiere la infraestructura de Spring aplicable;
no sucede al instanciar por nuestra cuenta cualquier clase anotada. Los repositorios
generados por Spring Data no necesitan un `@Repository` añadido para cada interfaz.

En el modo habitual basado en proxies, la llamada debe pasar por el bean de
Spring: crear el servicio con `new` en un test unitario no activa por sí solo
las transacciones, y una llamada interna entre métodos de la misma instancia
no aplica una nueva interceptación. `readOnly = true` expresa una intención de
lectura y puede permitir optimizaciones; no es una autorización de seguridad.
No convierte llamadas HTTP externas o escritura de ficheros en operaciones
reversibles junto con la transacción de base de datos.

---

## 🔹 Pruebas y depuración

| Anotación           | Significado / Uso                                                                  |
| ------------------- | ---------------------------------------------------------------------------------- |
| **@SpringBootTest** | Ejecuta una prueba integrando todo el contexto Spring Boot.                        |
| **@DataJpaTest** | Configura un slice JPA; motor, reemplazo de datasource, migraciones y esquema dependen de módulos y configuración. No significa siempre H2 ni ausencia de Flyway. |
| **@WebMvcTest** | Configura un slice web, que puede incorporar advice, filtros y seguridad; no carga toda la aplicación ni desactiva siempre Spring Security. |
| **@MockitoBean**    | Crea un mock gestionado por Spring para sustituir un bean real durante una prueba en Spring Boot 4. |
| **@Test**           | Indica un método de prueba (JUnit).                                                |

Para `@BeforeEach`, parametrizados, `@Mock`, `@InjectMocks`, `@Captor`,
`@ActiveProfiles`, `@Import`, transacciones de prueba y expectativas HTTP:
consultar los [diccionarios de testing](../03-pruebas/diccionarios/README.md).

---

## 🔹 Lombok

| Anotación                                       | Significado / Uso                                                   |
| ----------------------------------------------- | ------------------------------------------------------------------- |
| **@Getter**, **@Setter**                        | Genera automáticamente los getters/setters.                         |
| **@NoArgsConstructor**, **@AllArgsConstructor** | Genera constructores sin o con todos los argumentos.                |
| **@Builder**                                    | Crea un patrón *builder* para construir instancias de forma fluida. |
| **@ToString**, **@EqualsAndHashCode**           | Genera `toString()` y `equals()`/`hashCode()`.                      |

La referencia completa de [Lombok](anexo-lombok.md) añade `@RequiredArgsConstructor`,
`@Data`, `@Value`, `@NonNull`, `@Builder.Default`, exclusiones de relaciones y
ejemplos del Java generado. Lombok no registra beans ni valida la BD.

---

## 🔹 Swagger / OpenAPI

| Anotación        | Significado / Uso                                                                         |
| ---------------- | ----------------------------------------------------------------------------------------- |
| **@Operation**   | Describe un endpoint concreto en la documentación OpenAPI (título, resumen, parámetros…). |
| **@ApiResponse** | Documenta los posibles códigos de respuesta.                                              |
| **@Schema**      | Define el modelo de datos de entrada/salida en la documentación.                          |

Son anotaciones de Swagger/OpenAPI, no de Spring core. Se utilizan si el diseño
de documentación elegido las necesita. En la referencia de Battleship el contrato
canónico es el YAML versionado: un visor no lo convierte en un contrato generado
por anotaciones ni justifica mantener dos fuentes contradictorias.

---

## 🔹 Anotaciones avanzadas y de consulta

Estas anotaciones aparecen en algunas sesiones y ejemplos del módulo. Se
incluyen como referencia; no significa que haya que usarlas todas en los
primeros ejercicios.

### Errores REST

| Anotación | Uso |
|---|---|
| **@RestControllerAdvice** | Aplica manejo compartido a los controladores REST y escribe los resultados en el cuerpo de la respuesta, normalmente JSON. Es la combinación habitual de `@ControllerAdvice` y `@ResponseBody`. |

### Seguridad a nivel de método

| Anotación | Uso |
|---|---|
| **@EnableMethodSecurity** | Activa la autorización basada en anotaciones en métodos de Spring, como `@PreAuthorize`. Se declara en la configuración de seguridad. |
| **@PreAuthorize** | Rechaza la invocación de un método si no se cumple una expresión de autorización, por ejemplo `@PreAuthorize("hasRole('PLAYER')")`. La comprobación se ejecuta antes del método. |

Estas anotaciones complementan las reglas HTTP de Spring Security; no sustituyen
la autenticación ni la configuración de rutas.

### Paginación y pruebas

| Anotación | Uso |
|---|---|
| **@PageableDefault** | Define valores predeterminados para un parámetro `Pageable`, como tamaño y orden, cuando no se indican en la petición. |
| **@AutoConfigureMockMvc** | En una prueba con contexto Spring, configura `MockMvc` para enviar peticiones sin arrancar un servidor HTTP real. |
| **@WithMockUser** | En pruebas de Spring Security, ejecuta el test con un usuario y roles simulados; no prueba autenticación real ni emisión de JWT. |

### Configuración tipada

| Anotación | Uso |
|---|---|
| **@ConfigurationProperties** | Vincula un grupo de propiedades de configuración a un objeto tipado, en vez de inyectar valores sueltos uno a uno. |
| **@EnableConfigurationProperties** | Registra explícitamente clases `@ConfigurationProperties` en el contexto cuando no se usa escaneo de configuración para ellas. |

**Criterio:** consulta estas anotaciones cuando el caso lo requiera. No añadas
seguridad por método, paginación ni clases de configuración tipada solo para
seguir una lista; deben responder a una necesidad concreta del contrato o de la
aplicación.

## Referencias de origen

- [Spring Framework: contenedor e inyección](https://docs.spring.io/spring-framework/reference/core/beans.html).
- [Spring MVC: controladores anotados](https://docs.spring.io/spring-framework/reference/web/webmvc/mvc-controller.html).
- [Spring Test: anotaciones](https://docs.spring.io/spring-framework/reference/testing/annotations.html).
- [Jakarta Persistence](https://jakarta.ee/specifications/persistence/).
- [Jakarta Validation](https://jakarta.ee/specifications/bean-validation/).
- [Lombok: diccionario del módulo](anexo-lombok.md).
