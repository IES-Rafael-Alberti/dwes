---
title: "DTO, Proyecciones y Alternativas en Spring Boot"
author: "José Manuel Sánchez Álvarez"
date: 2025-11-10
theme: white
slideNumber: true
transition: fade
center: true
---

# 🧩 DTO, Proyecciones y Alternativas en Spring Boot
## Spring Data JPA

---

## 🎯 Objetivo

Comprender las distintas formas de **transferir o representar datos** en Spring Boot sin exponer directamente las entidades:

- **DTO (Data Transfer Object)**
- **Proyecciones**
- **MapStruct / ModelMapper**
- **Records (Java 16+)**

---

## 🧱 DTO — Data Transfer Object

### 💡 Definición
Objeto que **transporta datos entre capas** sin estar mapeado a la base de datos.

Evita exponer directamente las entidades JPA.

---

### 🧰 Ejemplo básico

```java
public class UserDTO {
    private String username;
    private String email;
    public UserDTO(String username, String email) { ... }
}
````

---

### 🚀 Uso

```java
List<UserDTO> users = userRepository.findAll()
    .stream()
    .map(u -> new UserDTO(u.getUsername(), u.getEmail()))
    .toList();
```

O directamente desde JPQL:

```java
@Query("SELECT new com.example.UserDTO(u.username, u.email) FROM User u")
List<UserDTO> findAllUserDTOs();
```

---

### ✅ Ventajas

* Control total sobre los datos expuestos.
* Ideal para **APIs REST**.
* Desacopla capas.

### ❌ Inconvenientes

* Más código (constructores, mappers).
* Hay que mantenerlo sincronizado con la entidad.

---

## 🔍 Proyecciones (Projections)

### 💡 Concepto

Permiten **consultar solo algunos campos** de una entidad, sin cargarla completa.

Spring Data ofrece:

1. Proyección por **interfaz**
2. Proyección por **clase (constructor)**
3. **Proyección dinámica**

---

### 🧩 1️⃣ Proyección por interfaz

```java
public interface UserProjection {
    String getUsername();
    String getEmail();
}
```

Repositorio:

```java
public interface UserRepository extends JpaRepository<User, Long> {
    List<UserProjection> findAllProjectedBy();
}
```

---

### 📊 Resultado

Devuelve solo los campos `username` y `email`.

Spring genera automáticamente los proxies.

---

### 🧩 2️⃣ Proyección por clase

```java
public class UserSummary {
    private final String username;
    private final String email;
    public UserSummary(String username, String email) { ... }
}
```

Repositorio:

```java
@Query("SELECT new com.example.UserSummary(u.username, u.email) FROM User u")
List<UserSummary> findAllSummaries();
```

---

### 📊 Resultado

Devuelve instancias de `UserSummary` directamente desde la consulta JPQL.
Perfecta para **consultas personalizadas**.

---

### 🧩 3️⃣ Proyección dinámica

```java
public interface UserRepository extends JpaRepository<User, Long> {
    <T> List<T> findByRole(String role, Class<T> type);
}
```

Uso:

```java
findByRole("ADMIN", UserProjection.class);
findByRole("ADMIN", UserSummary.class);
```

---

### ⚡ Comparativa rápida

| Tipo     | Consulta            | Escritura    | Ventaja  |
| -------- | ------------------- | ------------ | -------- |
| Interfaz | Automática          | Solo lectura | Ligera   |
| Clase    | JPQL (`SELECT new`) | Solo lectura | Tipada   |
| Dinámica | Genérica            | Solo lectura | Flexible |

---

### 💡 Proyecciones anidadas

```java
public interface DeptProj { String getName(); }
public interface UserWithDept {
    String getUsername();
    DeptProj getDepartment();
}
```

👉 Spring Data resuelve las relaciones automáticamente.

---

### ⚠️ Limitaciones

* Solo lectura
* Nombres deben coincidir
* Sin validaciones ni lógica
* No reemplazan los DTO

---

### 🧭 Cuándo usar

| Escenario                   | Usa...              |
| --------------------------- | ------------------- |
| Listados simples            | Proyección          |
| API REST externa            | DTO / Record        |
| Transformación o validación | DTO                 |
| Múltiples vistas            | Proyección dinámica |

---

## ⚙️ Otras alternativas

### 🧩 MapStruct

```java
@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDTO toDTO(User entity);
}
```

✔️ Código generado en compilación
🚀 Rápido y sin reflexión

---

### 🧩 ModelMapper

```java
ModelMapper mapper = new ModelMapper();
UserDTO dto = mapper.map(userEntity, UserDTO.class);
```

✔️ Simplicidad
❌ Más lento en grandes datasets (usa reflexión)

---

### 🧩 Record DTOs (Java 16+)

```java
public record UserDTO(String username, String email) {}
```

✔️ Concisos
✔️ Inmutables
✔️ Integración automática con Jackson

---

## ⚖️ Comparación general

| Enfoque             | Eficiencia | Flexibilidad | Ideal para        |
| ------------------- | ---------- | ------------ | ----------------- |
| DTO manual          | Media      | Alta         | APIs REST         |
| Proyección interfaz | Alta       | Baja         | Listados ligeros  |
| Proyección clase    | Alta       | Media        | Consultas JPQL    |
| MapStruct           | Muy alta   | Alta         | Proyectos grandes |
| ModelMapper         | Media      | Media        | Prototipos        |
| Record DTO          | Alta       | Media        | APIs modernas     |

---

## 🧩 Ejemplo comparativo

Entidad:

```java
@Entity
public class User {
  @Id @GeneratedValue private Long id;
  private String username;
  private String email;
}
```

---

DTO:

```java
public record UserDTO(String username, String email) {}
```

Proyección:

```java
public interface UserProjection {
  String getUsername();
  String getEmail();
}
```

---

Controlador:

```java
@GetMapping("/dto")
List<UserDTO> dto() { ... }

@GetMapping("/projection")
List<UserProjection> projection() { ... }
```

---

## 🧭 Buenas prácticas

✅ No devolver entidades directamente
✅ DTOs inmutables (`record`)
✅ MapStruct en proyectos medianos/grandes
✅ Proyecciones para lecturas rápidas
✅ Separar entidad ↔ DTO ↔ respuesta

---

## 📚 Referencias

* [Spring Data JPA Reference](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#projections)
* [MapStruct Docs](https://mapstruct.org/)
* [Baeldung - DTO vs Entity](https://www.baeldung.com/entity-to-and-from-dto-for-a-java-spring-application)

---

## 🧠 Conclusión

* Usa **DTOs / records** para APIs REST.
* Usa **proyecciones** para mejorar rendimiento.
* Usa **MapStruct / ModelMapper** para automatizar mapeos.
* Nunca expongas entidades directamente.

---

# ✅ FIN

````

---

### 💾 Cómo usarlo

Si ya usas tu pipeline Reveal.js habitual (por ejemplo con `pandoc` o `emacs-reveal`), solo tienes que:

```bash
pandoc DTO_Proyecciones_SpringBoot_Presentacion.md \
  -t revealjs -s -o DTO_Proyecciones_SpringBoot.html \
  -V revealjs-url=https://cdn.jsdelivr.net/npm/reveal.js@5
````

Y listo 🎉
Tendrás la presentación navegable con las flechas ← → (perfecta para clase).

---
