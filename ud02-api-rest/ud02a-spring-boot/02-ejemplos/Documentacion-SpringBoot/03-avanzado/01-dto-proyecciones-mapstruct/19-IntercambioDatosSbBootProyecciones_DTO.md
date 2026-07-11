---
title: "DTO, Proyecciones y Alternativas en Spring Boot"
author: "José Manuel Sánchez Álvarez"
date: 2025-11-10
subject: "Desarrollo Web en Entorno Servidor (DWES)"
keywords: ["Spring Boot", "JPA", "DTO", "Proyecciones", "MapStruct", "Interfaces", "Record", "HATEOAS", "Paginación"]
output: 
  pdf_document:
    toc: true
    number_sections: true
---

# 🧩 DTO, Proyecciones y Alternativas en Spring Boot

En el desarrollo de aplicaciones con **Spring Boot y Spring Data JPA**, es común que no queramos devolver entidades completas (con todos sus campos y relaciones) al cliente.  
Para optimizar el rendimiento, reducir acoplamiento y controlar qué datos se exponen, podemos utilizar diferentes estrategias:

- **DTO (Data Transfer Object)**  
- **Proyecciones (Projections)**  
- **Interfaces basadas en proyección**  
- **Clases “record”**  
- **MapStruct / ModelMapper / mapeo manual**

A continuación se explica qué es cada una, cómo se usa y en qué casos se recomienda.

---

## 🧱 1. DTO (Data Transfer Object)

### 📖 Definición
Un **DTO** es un **objeto simple** que se utiliza para **transferir datos** entre capas de la aplicación (por ejemplo, entre la capa de persistencia y la de presentación o API).

Los DTO **no están mapeados a la base de datos**: solo contienen los datos que realmente queremos exponer o recibir.

### 🧰 Cómo se define
Se crea una **clase Java normal**, generalmente en el paquete `dto` o `model.dto`:

```java
public class UserDTO {
    private String username;
    private String email;

    public UserDTO(String username, String email) {
        this.username = username;
        this.email = email;
    }

    // Getters y Setters
}
````

### 🚀 Cómo se usa

En el servicio o repositorio, se mapea la entidad a DTO:

```java
@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    public List<UserDTO> findAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(user -> new UserDTO(user.getUsername(), user.getEmail()))
                .toList();
    }
}
```

O directamente desde una consulta JPQL:

```java
@Query("SELECT new com.example.dto.UserDTO(u.username, u.email) FROM User u")
List<UserDTO> findAllUserDTOs();
```

### ✅ Ventajas

* Control total sobre los datos expuestos.
* Reduce dependencias entre capas.
* Facilita la validación y transformación de datos.
* Ideal para APIs REST (evita fugas de modelo de dominio).

### ❌ Desventajas

* Requiere más código (constructores, mapeos, etc.).
* Si cambia el modelo, hay que mantener el DTO sincronizado.

---

## 🔍 2. Proyecciones (Projections) — Definición clara y ejemplos

Las **proyecciones** en Spring Data JPA son una forma de **definir una vista parcial de una entidad**, es decir, obtener **solo algunos campos** sin cargar la entidad completa.

Spring Data permite tres formas principales de definir una proyección:

1. **Proyección por interfaz**
2. **Proyección por clase (constructor)**
3. **Proyección dinámica (definida en tiempo de ejecución)**

---

### 🧩 2.1. Proyección por interfaz (Interface-based projection)

Esta es la forma **más común y simple**.

#### 📘 Definición

```java
// Entidad base
@Entity
public class User {
    @Id @GeneratedValue
    private Long id;
    private String username;
    private String email;
    private String password;
    private String role;

    @ManyToOne
    private Department department;
}
```

#### 🧱 Proyección

```java
public interface UserProjection {
    String getUsername();
    String getEmail();
    String getRole();
}
```

> 🔹 Los métodos deben coincidir con los nombres de las propiedades de la entidad.
> 🔹 No se implementa la interfaz: **Spring Data genera automáticamente una implementación en tiempo de ejecución**.

#### 📦 Repositorio

```java
public interface UserRepository extends JpaRepository<User, Long> {
    List<UserProjection> findAllProjectedBy();
}
```

> 🔹 El método `findAllProjectedBy()` es una convención: Spring Data entiende que queremos devolver los datos según la proyección.
> 🔹 También puedes añadir filtros, por ejemplo:
>
> ```java
> List<UserProjection> findByRole(String role);
> ```

#### 🧭 Uso en controlador

```java
@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/projection")
    public List<UserProjection> getUsersProjection() {
        return userRepository.findAllProjectedBy();
    }
}
```

📊 **Resultado:** solo se cargan los campos `username`, `email` y `role` desde la base de datos.

---

### 🧩 2.2. Proyección por clase (Class-based projection)

Se define una **clase normal o record** con un **constructor que recibe los campos deseados**.
Spring Data la utilizará automáticamente **si usas una consulta JPQL con `SELECT new`**.

#### 📘 Definición

```java
public class UserSummary {
    private final String username;
    private final String email;

    public UserSummary(String username, String email) {
        this.username = username;
        this.email = email;
    }

    public String getUsername() { return username; }
    public String getEmail() { return email; }
}
```

#### 📦 Repositorio

```java
public interface UserRepository extends JpaRepository<User, Long> {

    @Query("SELECT new com.example.dto.UserSummary(u.username, u.email) FROM User u")
    List<UserSummary> findAllSummaries();
}
```

> 🔹 Aquí **la consulta JPQL crea instancias de la clase `UserSummary` directamente.**
> 🔹 Los nombres y orden de los parámetros del constructor deben coincidir con la selección del `SELECT`.

#### 🧭 Uso en controlador

```java
@GetMapping("/summary")
public List<UserSummary> getUserSummaries() {
    return userRepository.findAllSummaries();
}
```

📊 **Resultado:** devuelve objetos `UserSummary` con solo dos campos, sin cargar el resto de la entidad `User`.

---

### 🧩 2.3. Proyección dinámica (Dynamic projection)

Permite **decidir el tipo de proyección en tiempo de ejecución**, según el parámetro genérico que se le pase al método del repositorio.

#### 📘 Definición

```java
public interface UserRepository extends JpaRepository<User, Long> {
    <T> List<T> findByRole(String role, Class<T> type);
}
```

#### 📦 Uso

```java
List<UserProjection> users1 = userRepository.findByRole("ADMIN", UserProjection.class);
List<UserSummary> users2 = userRepository.findByRole("ADMIN", UserSummary.class);
```

> 🔹 Spring genera automáticamente la consulta que devuelva solo los campos necesarios según la clase o interfaz pasada como argumento.
> 🔹 Muy útil si una misma entidad tiene distintas “vistas” según el contexto.

---

### ⚡ Resumen visual

| Tipo                       | Definición                                  | Consulta personalizada | Escritura    | Ventajas principales                       |
| -------------------------- | ------------------------------------------- | ---------------------- | ------------ | ------------------------------------------ |
| **Interface projection**   | `interface UserView { String getEmail(); }` | No necesaria           | Solo lectura | Muy ligera y automática                    |
| **Class projection (DTO)** | `class UserDTO { UserDTO(String e){} }`     | Sí (`SELECT new`)      | Solo lectura | Control total, tipo fuerte                 |
| **Dynamic projection**     | `<T> List<T> findBy...(Class<T> type)`      | No necesaria           | Solo lectura | Flexible: cambia el tipo según el contexto |

---

### 💡 Proyecciones anidadas

Puedes acceder a campos de relaciones con **proyecciones anidadas**, definiendo otra interfaz:

```java
public interface DepartmentProjection {
    String getName();
}

public interface UserWithDepartment {
    String getUsername();
    DepartmentProjection getDepartment();
}
```

El repositorio puede devolver:

```java
List<UserWithDepartment> findAllBy();
```

> ⚙️ Spring Data resolverá automáticamente las relaciones necesarias para llenar la proyección anidada.

---

### ⚠️ Limitaciones importantes

* No puedes modificar ni guardar entidades desde una proyección.
* Las proyecciones **solo sirven para lectura**.
* Si usas **`fetch = LAZY`**, puede haber cargas diferidas si accedes a propiedades no seleccionadas.
* En proyecciones basadas en **interfaces**, los nombres deben coincidir exactamente con los campos de la entidad.

---

### 🧭 Cuándo usar proyecciones

* Cuando quieres **mejorar el rendimiento** cargando solo los campos necesarios.
* Cuando el resultado se **consume directamente por la API** y no requiere lógica adicional.
* Cuando no necesitas modificar los datos (solo lectura).
* Cuando hay **consultas repetitivas** de solo lectura (por ejemplo, listados o paneles).

---

### 🚫 Cuándo NO usar proyecciones

* Cuando necesitas lógica de negocio o validaciones (usa DTO).
* Cuando vas a **modificar** los datos (usa entidades o DTOs de entrada).
* Cuando los campos se construyen con lógica adicional (usa DTO manual o MapStruct).

---

✅ **Conclusión:**
Las proyecciones son ideales para **consultas de solo lectura y optimización**, mientras que los **DTOs** son preferibles cuando hay **transformación, validación o intercambio con la capa de presentación (API)**.

---

## ⚙️ 3. Otras alternativas

Además de DTO y Proyecciones, existen otros mecanismos comunes en Spring Boot:

---

### 🧩 3.1. MapStruct

**MapStruct** es una librería que **genera automáticamente código de mapeo** entre entidades y DTOs en tiempo de compilación.

**Ejemplo:**

```java
@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDTO toDTO(User user);
    User toEntity(UserDTO userDTO);
}
```

Y luego:

```java
@Autowired
private UserMapper mapper;

UserDTO dto = mapper.toDTO(userEntity);
```

✅ Rápido y sin reflexión.
💡 Ideal cuando hay muchos DTOs.

---

### 🧩 3.2. ModelMapper

Alternativa que usa **reflexión en tiempo de ejecución**:

```java
ModelMapper mapper = new ModelMapper();
UserDTO dto = mapper.map(userEntity, UserDTO.class);
```

✅ Menos configuración.
❌ Peor rendimiento en grandes volúmenes.

---

### 🧩 3.3. Record DTOs (Java 16+)

Desde Java 16, los **records** son una forma concisa de crear DTOs inmutables:

```java
public record UserDTO(String username, String email) {}
```

Ventajas:

* Inmutables y concisos.
* Perfectos para APIs REST.
* Integran bien con librerías de serialización (Jackson los maneja automáticamente).

---

## ⚖️ 4. Comparación general

| Enfoque                   | Lectura/Escritura | Eficiencia | Mantenimiento | Flexibilidad | Ideal para                       |
| ------------------------- | ----------------- | ---------- | ------------- | ------------ | -------------------------------- |
| **DTO manual**            | Ambos             | Media      | Media         | Alta         | API REST complejas               |
| **Proyección (interfaz)** | Solo lectura      | Alta       | Alta          | Baja         | Listados ligeros                 |
| **Proyección (clase)**    | Lectura           | Alta       | Media         | Media        | Consultas específicas            |
| **MapStruct**             | Ambos             | Muy alta   | Alta          | Alta         | Grandes proyectos con muchos DTO |
| **ModelMapper**           | Ambos             | Media-baja | Alta          | Media        | Prototipos o apps pequeñas       |
| **Record DTO**            | Ambos             | Alta       | Alta          | Media        | APIs modernas (Java 17+)         |

---

## 🧭 5. Cuándo usar cada una

| Caso de uso                                  | Recomendación                      |
| -------------------------------------------- | ---------------------------------- |
| Endpoints REST simples                       | DTO o Record DTO                   |
| Listas o informes con pocos campos           | Proyección (interfaz o clase)      |
| Grandes sistemas con mucho mapeo entre capas | MapStruct                          |
| Prototipos o desarrollo rápido               | ModelMapper                        |
| Consultas personalizadas JPQL                | Proyección por clase (constructor) |
| Campos derivados o transformaciones          | DTO con lógica propia              |

---

## 🧩 6. Ejemplo comparativo

### Entidad base:

```java
@Entity
public class User {
    @Id @GeneratedValue
    private Long id;
    private String username;
    private String email;
    private String password;
}
```

### Ejemplo DTO:

```java
public record UserDTO(String username, String email) {}
```

### Ejemplo Proyección (interfaz):

```java
public interface UserProjection {
    String getUsername();
    String getEmail();
}
```

### Uso en controlador:

```java
@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserRepository repo;

    @GetMapping("/dto")
    public List<UserDTO> getUsersDTO() {
        return repo.findAll().stream()
                .map(u -> new UserDTO(u.getUsername(), u.getEmail()))
                .toList();
    }

    @GetMapping("/projection")
    public List<UserProjection> getUsersProjection() {
        return repo.findAllProjectedBy();
    }
}
```

---

## 🧩 7. Buenas prácticas

* **Nunca devuelvas entidades directamente** a la API → pueden exponer campos sensibles (como contraseñas).
* Prefiere **DTOs inmutables** (`record` o sin setters).
* Usa **MapStruct** en proyectos medianos o grandes.
* **Proyecciones** son perfectas para **optimización de consultas** (evitan `SELECT *`).
* Mantén los **DTOs desacoplados** de las entidades (no anotes con `@Entity` ni relaciones JPA).

---

## 📚 8. Referencias

* [Spring Data JPA Reference - Projections](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#projections)
* [MapStruct Official Docs](https://mapstruct.org/)
* [Baeldung - Spring Data Projections](https://www.baeldung.com/spring-data-jpa-projections)
* [Baeldung - DTO vs Entity](https://www.baeldung.com/entity-to-and-from-dto-for-a-java-spring-application)

---

# 🧭 Conclusión

En resumen:

* Usa **DTOs o records** cuando expongas datos al exterior (APIs, servicios).
* Usa **proyecciones** cuando necesites mejorar el rendimiento en consultas específicas.
* Usa **MapStruct o ModelMapper** para automatizar conversiones y mantener el código limpio.
* Evita mezclar entidades de dominio con los objetos que viajan entre capas.

Cada enfoque tiene su lugar, y la decisión depende del equilibrio entre **rendimiento**, **claridad** y **mantenimiento**.

---

# 🧩 9. Consideraciones en el diseño de una API REST
Cuando exponemos datos mediante una **API REST**, no basta con decidir cómo obtenerlos o transformarlos.  
También debemos pensar **cómo los presentamos y estructuramos las respuestas**.

## 🌐 Principios básicos
1. **No exponer entidades directamente.**  
   Usa DTOs o proyecciones para evitar fugas de datos sensibles.
2. **Usar convenciones REST.**  
   - Endpoints en plural (`/users`, `/orders`)  
   - Verbos HTTP correctos (`GET`, `POST`, `PUT`, `DELETE`)  
   - Códigos de estado coherentes (`200 OK`, `201 Created`, `404 Not Found`, etc.)
3. **Diseñar respuestas coherentes.**  
   Devuelve siempre estructuras JSON uniformes, con secciones claras como `data`, `meta`, y `links`.
4. **Incluir enlaces y paginación.**  
   Permite al cliente navegar fácilmente por el conjunto de datos.

---

### 📦 Ejemplo de estructura de respuesta REST

```json
{
  "data": [
    { "id": 1, "username": "ana", "email": "ana@example.com" },
    { "id": 2, "username": "juan", "email": "juan@example.com" }
  ],
  "meta": { "total": 2 },
  "links": { "self": "/api/users", "next": null }
}
````

> Esta estructura hace que la API sea más predecible y facilita su consumo desde clientes frontend o móviles.

---

# ⚙️ 10. Paginación y ordenación (introducción)

En listados grandes, devolver todos los registros a la vez **no es eficiente**.
Spring Data JPA proporciona soporte nativo para **paginación y ordenación** mediante las clases `Pageable` y `Page<T>`.

## 🧱 Repositorio

```java
public interface UserRepository extends JpaRepository<User, Long> {
    Page<User> findAll(Pageable pageable);
}
```

## 🧭 Controlador

```java
@GetMapping
public Page<UserDTO> getUsers(Pageable pageable) {
    return userRepository.findAll(pageable)
            .map(u -> new UserDTO(u.getUsername(), u.getEmail()));
}
```

## 🔢 Ejemplo de uso

```
GET /api/users?page=0&size=5&sort=username,asc
```

> 📘 Spring interpreta los parámetros `page`, `size` y `sort` automáticamente.
> El resultado incluye metadatos útiles (`totalPages`, `totalElements`, `number`, `size`, etc.).

Ejemplo de respuesta resumida:

```json
{
  "content": [
    { "username": "ana", "email": "ana@example.com" },
    { "username": "juan", "email": "juan@example.com" }
  ],
  "pageable": { "pageNumber": 0, "pageSize": 5 },
  "totalElements": 12,
  "totalPages": 3,
  "last": false,
  "sort": [{ "property": "username", "direction": "ASC" }]
}
```

> 🔹 Más adelante, en el documento dedicado a *Paginación, Ordenación y Filtrado*, se abordará este tema con ejemplos completos, integración con HATEOAS y filtrado dinámico.

---

# 🔗 11. HATEOAS (Hypermedia as the Engine of Application State) "manual"

HATEOAS es un principio REST que permite a los clientes descubrir las acciones disponibles mediante **enlaces incluidos en la respuesta**.
Spring Boot lo implementa con la librería **Spring HATEOAS**.

## ⚙️ Dependencia Maven

```xml
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-hateoas</artifactId>
</dependency>
```

---

## 🧩 Ejemplo básico

```java
@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserRepository repo;

    @GetMapping("/{id}")
    public EntityModel<UserDTO> getUser(@PathVariable Long id) {
        User user = repo.findById(id).orElseThrow();
        UserDTO dto = new UserDTO(user.getUsername(), user.getEmail());

        return EntityModel.of(dto,
            linkTo(methodOn(UserController.class).getUser(id)).withSelfRel(),
            linkTo(methodOn(UserController.class).getAllUsers()).withRel("users"));
    }

    @GetMapping
    public CollectionModel<EntityModel<UserDTO>> getAllUsers() {
        List<EntityModel<UserDTO>> users = repo.findAll().stream()
            .map(u -> EntityModel.of(
                    new UserDTO(u.getUsername(), u.getEmail()),
                    linkTo(methodOn(UserController.class).getUser(u.getId())).withSelfRel()
            ))
            .toList();

        return CollectionModel.of(users,
            linkTo(methodOn(UserController.class).getAllUsers()).withSelfRel());
    }
}
```

---

### 📤 Ejemplo de respuesta JSON

```json
{
  "username": "ana",
  "email": "ana@example.com",
  "_links": {
    "self": { "href": "/users/1" },
    "users": { "href": "/users" }
  }
}
```

> ⚙️ Los enlaces (`_links`) permiten al cliente navegar por la API sin necesidad de conocer todos los endpoints de antemano.

---

## 💡 Ventajas de HATEOAS

| Ventaja                                | Descripción                                                            |
| -------------------------------------- | ---------------------------------------------------------------------- |
| **Descubrimiento automático**          | El cliente conoce los pasos siguientes sin documentación adicional.    |
| **Desacoplamiento**                    | El cliente no depende de rutas estáticas.                              |
| **Autodescripción**                    | Cada recurso “explica” cómo interactuar con él.                        |
| **Integración natural con paginación** | Las respuestas pueden incluir enlaces `first`, `next`, `prev`, `last`. |

---

# ⚙️ 12. Spring Data REST — HATEOAS automático

Hasta ahora hemos visto cómo añadir enlaces **manualmente** con la librería *Spring HATEOAS*.  
Sin embargo, **Spring Data REST** ofrece una alternativa más automática:  
expone directamente los **repositorios JPA como endpoints RESTful** con hipermedia activa (*HATEOAS integrado*).

---

## 🧠 Concepto

Spring Data REST convierte los repositorios de Spring Data JPA en recursos REST **sin necesidad de escribir controladores**.  
Los endpoints se generan automáticamente y devuelven respuestas con:

- Colecciones de entidades bajo `_embedded`
- Enlaces hipermedia (`_links`)
- Metadatos de paginación (`page`)
- Búsquedas automáticas derivadas de los métodos del repositorio

---

## ⚙️ Dependencias necesarias

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-rest</artifactId>
</dependency>

<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-hateoas</artifactId>
</dependency>
````

> 📘 Spring Boot incluirá automáticamente los endpoints REST al detectar repositorios JPA.

---

## 🧱 Ejemplo básico

### Entidad

```java
@Entity
public class User {
    @Id @GeneratedValue
    private Long id;
    private String username;
    private String email;
    private String role;
}
```

### Repositorio REST

```java
@RepositoryRestResource(path = "users")
public interface UserRepository extends JpaRepository<User, Long> {
    List<User> findByRole(@Param("role") String role);
}
```

---

## 🔗 Endpoints generados automáticamente

| Método   | Endpoint                           | Descripción                             |
| -------- | ---------------------------------- | --------------------------------------- |
| `GET`    | `/users`                           | Lista todos los usuarios con paginación |
| `GET`    | `/users/{id}`                      | Devuelve un usuario                     |
| `POST`   | `/users`                           | Crea un nuevo usuario                   |
| `PUT`    | `/users/{id}`                      | Actualiza un usuario existente          |
| `DELETE` | `/users/{id}`                      | Elimina un usuario                      |
| `GET`    | `/users/search/by-role?role=ADMIN` | Consulta personalizada                  |

---

## 📤 Ejemplo de respuesta JSON

```json
{
  "_embedded": {
    "users": [
      {
        "username": "ana",
        "email": "ana@example.com",
        "_links": {
          "self": { "href": "http://localhost:8080/users/1" },
          "user": { "href": "http://localhost:8080/users/1" }
        }
      },
      {
        "username": "juan",
        "email": "juan@example.com",
        "_links": {
          "self": { "href": "http://localhost:8080/users/2" },
          "user": { "href": "http://localhost:8080/users/2" }
        }
      }
    ]
  },
  "_links": {
    "self": { "href": "http://localhost:8080/users" },
    "profile": { "href": "http://localhost:8080/profile/users" }
  },
  "page": {
    "size": 20,
    "totalElements": 2,
    "totalPages": 1,
    "number": 0
  }
}
```

> 🚀 Todo esto se genera automáticamente sin escribir controladores ni ensamblar enlaces manualmente.

---

## ⚙️ Personalización

Spring Data REST permite ajustar qué se expone y cómo:

```java
@RepositoryRestResource(path = "users", collectionResourceRel = "usuarios")
public interface UserRepository extends JpaRepository<User, Long> {

    @RestResource(path = "by-role", rel = "byRole")
    List<User> findByRole(@Param("role") String role);
}
```

También se puede:

* Cambiar la ruta base en `application.properties`:

  ```properties
  spring.data.rest.base-path=/api
  ```
* Ocultar un repositorio:

  ```java
  @RepositoryRestResource(exported = false)
  public interface InternalRepository extends JpaRepository<Log, Long> { }
  ```
* Excluir campos sensibles con `@JsonIgnore` o `@JsonView`.

---

## 🧭 Comparativa final

| Enfoque                           | Implementación                            | Ventajas                                          | Inconvenientes                       |
| --------------------------------- | ----------------------------------------- | ------------------------------------------------- | ------------------------------------ |
| **HATEOAS manual**                | Controladores + `EntityModel`, `linkTo()` | Control total, personalización, DTOs              | Más código                           |
| **Spring Data REST (automático)** | Repositorios anotados                     | Fire-and-forget, enlaces y paginación automáticos | Menos control sobre formato y lógica |

---

## 💡 Cuándo usar cada uno

* ✅ **Spring Data REST**
  Ideal para prototipos, backends internos o proyectos donde se desea exponer datos rápidamente.

* ✅ **HATEOAS manual**
  Recomendado para APIs REST profesionales o públicas, donde se requiere control sobre formato, seguridad y DTOs personalizados.

---

## 📚 Referencias

* [Spring Data REST Reference Documentation](https://docs.spring.io/spring-data/rest/docs/current/reference/html/)
* [Spring HATEOAS Reference Guide](https://docs.spring.io/spring-hateoas/docs/current/reference/html/)
* [Baeldung – Spring Data REST](https://www.baeldung.com/spring-data-rest-intro)



---
# 🧭 Conclusión actualizada

Al diseñar APIs REST en Spring Boot, no basta con definir cómo se **obtienen** los datos (consultas, proyecciones, DTOs),
sino también cómo se **exponen y navegan**:

* **DTOs / Records:** control y claridad en la salida.
* **Proyecciones:** eficiencia en las consultas.
* **Paginación:** control del volumen de datos.
* **HATEOAS:** navegación e interacción autoexplicativa.

Combinando estas técnicas se logran **APIs REST completas, robustas y mantenibles**, alineadas con las buenas prácticas del ecosistema Spring.

---

## 📚 Referencias adicionales

* [Spring Data JPA Reference - Projections](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#projections)
* [Spring HATEOAS Reference Guide](https://docs.spring.io/spring-hateoas/docs/current/reference/html/)
* [Spring Pageable and Sorting](https://docs.spring.io/spring-data/commons/docs/current/reference/html/#repositories.paging-and-sorting)
* [Baeldung - REST Pagination in Spring](https://www.baeldung.com/spring-data-jpa-pagination-sorting)

```

---

