---
title: "Paginación, Ordenación y Filtrado en Spring Boot con Spring Data JPA"
author: "José Manuel Sánchez Álvarez"
center: "IES Rafael Alberti"
course: "Desarrollo Web en Entorno Servidor — Curso 2025–2026"
date: 
lang: "es"
geometry: margin=2.5cm
fontsize: 11pt
mainfont: "DejaVu Sans"
monofont: "JetBrains Mono"
colorlinks: true
linkcolor: blue

    
header-includes:
  - \usepackage{titlesec}
  - \titleformat{\section}{\normalfont\Large\bfseries\color{blue}}{\thesection}{1em}{}
  - \titleformat{\subsection}{\normalfont\large\bfseries\color{teal}}{\thesubsection}{1em}{}
  - \titleformat{\subsubsection}{\normalfont\normalsize\bfseries\color{gray}}{\thesubsubsection}{1em}{}
  - \usepackage{fontspec}
  - \newfontfamily\emoji{Noto Color Emoji}
  - \setmonofont{DejaVu Sans Mono}
  - \setmainfont{DejaVu Sans}
---

# 🧩 Paginación, Ordenación y Filtrado en Spring Boot con Spring Data JPA

Las APIs REST modernas deben ser **eficientes, escalables y fáciles de consumir**.  
Spring Data JPA ofrece soporte nativo para manejar **paginación, ordenación y filtrado dinámico** de datos sin necesidad de escribir consultas SQL manuales.

---

## 1️⃣ Conceptos básicos

| Concepto | Descripción |
|-----------|-------------|
| **Paginación (Pagination)** | Dividir los resultados en páginas de tamaño fijo para evitar respuestas demasiado grandes. |
| **Ordenación (Sorting)** | Establecer el orden de los resultados según uno o varios campos. |
| **Filtrado (Filtering)** | Restringir los resultados según uno o varios criterios dinámicos. |

> Estas tres características pueden combinarse fácilmente en Spring Boot.

---

## 2️⃣ Paginación con `Pageable` y `Page<T>`

Spring Data proporciona la interfaz `Pageable` y la clase `PageRequest` para solicitar páginas de resultados.

### 📘 Ejemplo básico

#### 🧱 Repositorio

```java
public interface UserRepository extends JpaRepository<User, Long> { }
````

#### 🧭 Controlador

```java
@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserRepository repo;

    @GetMapping
    public Page<UserDTO> getUsers(Pageable pageable) {
        return repo.findAll(pageable)
                .map(u -> new UserDTO(u.getUsername(), u.getEmail()));
    }
}
```

#### 🔢 Petición de ejemplo

```
GET /users?page=0&size=5
```

#### 📦 Respuesta

```json
{
  "content": [
    { "username": "ana", "email": "ana@example.com" },
    { "username": "juan", "email": "juan@example.com" }
  ],
  "pageable": { "pageNumber": 0, "pageSize": 5 },
  "totalElements": 23,
  "totalPages": 5,
  "last": false,
  "first": true
}
```

> 🔹 Spring interpreta automáticamente los parámetros `page` y `size`.
> 🔹 La interfaz `Page<T>` contiene los metadatos de la paginación (total de elementos, páginas, etc.).

---

## 3️⃣ Ordenación (`Sort`)

Spring permite ordenar resultados fácilmente con el parámetro `sort`.

#### Ejemplo

```
GET /users?page=0&size=5&sort=username,asc
```

> También se pueden encadenar varios campos:
>
> ```
> GET /users?sort=role,desc&sort=username,asc
> ```

#### 📘 Cómo funciona

El parámetro `sort` se convierte automáticamente en un objeto `Sort` dentro del `Pageable` inyectado por Spring.

---

### 🧩 Ordenación manual

También puedes construir el `Pageable` manualmente:

```java
Pageable pageable = PageRequest.of(0, 10, Sort.by("username").ascending());
Page<User> page = userRepository.findAll(pageable);
```

---

## 4️⃣ Filtrado dinámico con parámetros

Para consultas flexibles, puedes filtrar según los parámetros enviados por el cliente.

### Ejemplo simple con filtros opcionales

```java
@GetMapping("/filter")
public Page<UserDTO> filterUsers(
        @RequestParam(required = false) String role,
        @RequestParam(required = false) String email,
        Pageable pageable) {

    return repo.findAll((root, query, cb) -> {
        List<Predicate> predicates = new ArrayList<>();
        if (role != null)
            predicates.add(cb.equal(root.get("role"), role));
        if (email != null)
            predicates.add(cb.like(root.get("email"), "%" + email + "%"));
        return cb.and(predicates.toArray(new Predicate[0]));
    }, pageable).map(u -> new UserDTO(u.getUsername(), u.getEmail()));
}
```

> 🔹 Este enfoque combina paginación, ordenación y filtrado mediante **Criteria API**.
> 🔹 El método `findAll(Specification, Pageable)` está disponible si el repositorio extiende `JpaSpecificationExecutor<T>`.

---

## 5️⃣ Ejemplo completo: paginación + ordenación + filtrado

### 🧱 Entidad

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

### 📦 Repositorio

```java
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> { }
```

### 🧠 Specification (filtros dinámicos)

```java
public class UserSpecifications {
    public static Specification<User> hasRole(String role) {
        return (root, query, cb) ->
                role == null ? cb.conjunction() : cb.equal(root.get("role"), role);
    }

    public static Specification<User> emailContains(String email) {
        return (root, query, cb) ->
                email == null ? cb.conjunction() : cb.like(root.get("email"), "%" + email + "%");
    }
}
```

### 🧭 Controlador

```java
@GetMapping("/search")
public Page<UserDTO> searchUsers(
        @RequestParam(required = false) String role,
        @RequestParam(required = false) String email,
        Pageable pageable) {

    Specification<User> spec = Specification
            .where(UserSpecifications.hasRole(role))
            .and(UserSpecifications.emailContains(email));

    return repo.findAll(spec, pageable)
            .map(u -> new UserDTO(u.getUsername(), u.getEmail()));
}
```

#### 🧩 Ejemplo de petición

```
GET /users/search?role=ADMIN&email=gmail&page=0&size=5&sort=username,asc
```

#### 📤 Ejemplo de respuesta

```json
{
  "content": [
    { "username": "ana", "email": "ana@gmail.com" },
    { "username": "carlos", "email": "carlos@gmail.com" }
  ],
  "pageable": { "pageNumber": 0, "pageSize": 5 },
  "totalElements": 7,
  "totalPages": 2,
  "_links": {
    "self": { "href": "/users/search?role=ADMIN&page=0&size=5" },
    "next": { "href": "/users/search?role=ADMIN&page=1&size=5" }
  }
}
```

> 🔹 Aquí combinamos **paginación**, **ordenación** y **filtrado dinámico**, e incluso añadimos enlaces HATEOAS (`_links`).

---

## 6️⃣ Integración con HATEOAS (`PagedModel`)

Spring HATEOAS ofrece `PagedResourcesAssembler` para incluir enlaces a páginas anteriores, siguientes, etc.

```java
@Autowired
private PagedResourcesAssembler<UserDTO> assembler;

@GetMapping("/paged")
public PagedModel<EntityModel<UserDTO>> getUsersPaged(Pageable pageable) {
    Page<UserDTO> page = repo.findAll(pageable)
                             .map(u -> new UserDTO(u.getUsername(), u.getEmail()));

    return assembler.toModel(page, user ->
            EntityModel.of(user,
                linkTo(methodOn(UserController.class).getUsersPaged(pageable)).withSelfRel()));
}
```

> Resultado: la respuesta incluirá automáticamente enlaces `first`, `prev`, `self`, `next`, `last`.

---

## 7️⃣ Buenas prácticas

| Recomendación                                   | Descripción                                                      |
| ----------------------------------------------- | ---------------------------------------------------------------- |
| **Tamaño máximo de página (`size`)**            | Define un límite (por ejemplo, 50) para evitar cargas excesivas. |
| **Valores por defecto**                         | Usa `@PageableDefault(size=10, sort="id")` para control interno. |
| **Combinar con DTOs**                           | No devuelvas entidades directamente, incluso en paginación.      |
| **Documentar los parámetros REST**              | Facilita el uso correcto desde frontend.                         |
| **Evitar ordenaciones por campos no indexados** | Puede afectar al rendimiento.                                    |
| **Incluir metadatos y enlaces**                 | Mejora la navegabilidad y autodescripción de la API.             |

---

## 8️⃣ Conclusión

* **Paginación** → controla la cantidad de datos devueltos.
* **Ordenación** → organiza los resultados de forma flexible.
* **Filtrado** → ofrece búsquedas dinámicas y precisas.
* **HATEOAS** → mejora la navegabilidad entre páginas.

Combinadas, estas técnicas permiten construir APIs REST **eficientes, escalables y fáciles de consumir**.

---

## 📚 Referencias

* [Spring Data JPA - Paging and Sorting](https://docs.spring.io/spring-data/commons/docs/current/reference/html/#repositories.paging-and-sorting)
* [Spring HATEOAS Reference Guide](https://docs.spring.io/spring-hateoas/docs/current/reference/html/)
* [Baeldung - Pagination and Sorting](https://www.baeldung.com/spring-data-jpa-pagination-sorting)
* [Baeldung - Specifications in Spring Data JPA](https://www.baeldung.com/rest-api-search-language-spring-data-specifications)

