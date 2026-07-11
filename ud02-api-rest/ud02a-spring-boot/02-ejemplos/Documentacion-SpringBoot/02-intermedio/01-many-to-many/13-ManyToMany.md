# Relaciones muchos a muchos Parte I
## @ManyToMany
Partimos de nuestras clases Producto:
```java
package com.example.producto.modelo;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Builder
@Data @NoArgsConstructor @AllArgsConstructor
@Entity
@Table(name = "producto")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id", scope = Producto.class)
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private String descripcion;
    private double precio;

    @ManyToOne
    @JoinColumn(name = "categoria_id")
    private Categoria categoria;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @ManyToMany(mappedBy="productos")
    @Builder.Default
    private List<Lote> lotes= new ArrayList<>(); // puede ser un Set?
}
```

y Lote:
```java
package com.example.producto.modelo;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "lote")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id", scope = Lote.class)
public class Lote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;

    @ManyToMany
    @JoinTable(
            name = "lote_producto",
            joinColumns = @JoinColumn(name = "lote_id"),
            inverseJoinColumns = @JoinColumn(name = "producto_id")
    )
    @Builder.Default
    private List<Producto> productos=new ArrayList<>();

    public void addProducto(Producto p) {
        this.productos.add(p);
        p.getLotes().add(this);
    }

    public void deleteProducto(Producto p) {
        this.productos.remove(p);
        p.getLotes().remove(this);
    }
}
```
La anotación `@ManyToMany` en JPA se utiliza para representar una relación de muchos a muchos entre dos entidades. En tu caso, estás utilizando esta anotación para representar la relación entre las entidades `Lote` y `Producto`.

En la entidad `Lote`, la anotación `@ManyToMany` se utiliza junto con `@JoinTable` para definir la tabla de unión y las columnas de unión para la relación. La tabla de unión es una tabla en la base de datos que se utiliza para resolver la relación de muchos a muchos. En tu caso, la tabla de unión se llama `lote_producto` y tiene dos columnas de unión: `lote_id` y `producto_id`.

En la entidad `Producto`, la anotación `@ManyToMany` se utiliza con el atributo `mappedBy` para indicar que esta es la parte no propietaria de la relación. El valor de `mappedBy` es el nombre del campo en la entidad propietaria que mapea la relación. En tu caso, el valor de `mappedBy` es `productos`, que es el nombre del campo en la entidad `Lote` que mapea la relación.

### Helpers
Además, has implementado dos métodos helper en la entidad `Lote`: `addProducto` y `deleteProducto`. Estos métodos se utilizan para añadir y eliminar productos de un lote, respectivamente, y para mantener la coherencia de la relación de ambos lados.

### Manejo de serialización circular (alternativa)
Por último, has utilizado la anotación `@JsonIdentityInfo` para manejar la serialización de la relación circular entre `Lote` y `Producto`. Esta anotación ayuda a evitar problemas de recursión infinita al serializar la relación de muchos a muchos en JSON.
Sí, te explicas perfectamente 👍
Lo que estás pidiendo es **exactamente el caso real** que justifica **DTOs + diseño de endpoints**, no anotaciones Jackson.

A continuación te dejo un **capítulo adicional completo**, listo para **pegar tal cual** en el documento de *DTO / Proyecciones*, con:

* Ejemplo **Many-to-Many User–Group**
* Evitar bucles **sin Jackson**
* **DTOs distintos según endpoint**
* Recuperar:

  * Usuario con **sus grupos (sin duplicados)**
  * Grupos con **contador de usuarios**
  * Grupo **con o sin miembros**, según endpoint o parámetro


---


## 🧩 Capítulo adicional — Relaciones Many-to-Many y serialización JSON en APIs REST, Parte II, la requetebuena

### Caso de estudio: Usuarios y Grupos

Un caso típico en aplicaciones reales es una relación **muchos a muchos**:

- Un **usuario** puede pertenecer a **muchos grupos**
- Un **grupo** puede tener **muchos usuarios**

Este tipo de relación **no debe serializarse directamente** a JSON, ya que produce:
- Bucles infinitos
- JSON ilegible
- APIs rígidas y difíciles de mantener

La solución **correcta** es **diseñar DTOs específicos por endpoint**, no “arreglar” la serialización con anotaciones Jackson.

---

## 1. Modelo de dominio (JPA)

Las entidades representan el **modelo interno**, no la API.

```java
@Entity
public class User {

    @Id @GeneratedValue
    private Long id;

    private String username;

    @ManyToMany
    @JoinTable(
        name = "user_group",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "group_id")
    )
    private Set<Group> groups = new HashSet<>();
}
````

```java
@Entity
public class Group {

    @Id @GeneratedValue
    private Long id;

    private String name;

    @ManyToMany(mappedBy = "groups")
    private Set<User> users = new HashSet<>();
}
```

📌 **Importante**
No hay ninguna anotación Jackson (`@JsonIgnore`, `@JsonBackReference`, etc.).
Las entidades **no saben cómo se exponen por la API**.

---

## 2. Problema a resolver (requisitos de la API)

Queremos soportar los siguientes casos:

### Desde usuarios

* Recuperar un usuario
* Incluir **sus grupos**
* **Sin duplicados**
* Opcionalmente indicar **cuántos usuarios tiene cada grupo**

### Desde grupos

* Recuperar un grupo **sin miembros** (vista ligera)
* Recuperar un grupo **con miembros** (vista detallada)
* Decidir esto:

  * por endpoint distinto
  * o por parámetro (`?includeUsers=true`)

---

## 3. DTOs diseñados por caso de uso

### 3.1 DTO de grupo (vista ligera)

Usado cuando:

* Se recuperan grupos desde un usuario
* No se necesitan los miembros

```java
public record GroupSummaryDTO(
    Long id,
    String name,
    long userCount
) {}
```

---

### 3.2 DTO de usuario con grupos

```java
public record UserWithGroupsDTO(
    Long id,
    String username,
    Set<GroupSummaryDTO> groups
) {}
```

✔ No hay bucles
✔ No hay entidades
✔ JSON limpio y estable

---

### 3.3 DTO de grupo con miembros

```java
public record UserSummaryDTO(
    Long id,
    String username
) {}
```

```java
public record GroupWithUsersDTO(
    Long id,
    String name,
    Set<UserSummaryDTO> users
) {}
```

---

## 4. Recuperar un usuario con sus grupos (sin duplicados)

### Servicio

```java
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public UserWithGroupsDTO findUserWithGroups(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow();

        Set<GroupSummaryDTO> groups = user.getGroups()
                .stream()
                .map(group -> new GroupSummaryDTO(
                        group.getId(),
                        group.getName(),
                        group.getUsers().size()
                ))
                .collect(Collectors.toSet()); // evita duplicados

        return new UserWithGroupsDTO(
                user.getId(),
                user.getUsername(),
                groups
        );
    }
}
```

📌 **Claves didácticas**

* `Set` garantiza **no duplicados**
* El contador se calcula **desde la entidad**
* No se expone la colección `users`

---

### Controlador

```java
@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/{id}")
    public UserWithGroupsDTO getUser(@PathVariable Long id) {
        return userService.findUserWithGroups(id);
    }
}
```

---

### Ejemplo de respuesta JSON

```json
{
  "id": 1,
  "username": "ana",
  "groups": [
    {
      "id": 10,
      "name": "admin",
      "userCount": 3
    },
    {
      "id": 20,
      "name": "editor",
      "userCount": 5
    }
  ]
}
```

✔ Sin bucles
✔ Sin duplicados
✔ API clara

---

## 5. Recuperar grupos: con o sin miembros

### 5.1 Endpoint ligero (sin usuarios)

```java
@GetMapping("/{id}")
public GroupSummaryDTO getGroup(@PathVariable Long id) {

    Group group = groupRepository.findById(id)
            .orElseThrow();

    return new GroupSummaryDTO(
            group.getId(),
            group.getName(),
            group.getUsers().size()
    );
}
```

📌 Ideal para listados y vistas generales.

---

### 5.2 Endpoint detallado (con usuarios)

```java
@GetMapping("/{id}/users")
public GroupWithUsersDTO getGroupWithUsers(@PathVariable Long id) {

    Group group = groupRepository.findById(id)
            .orElseThrow();

    Set<UserSummaryDTO> users = group.getUsers()
            .stream()
            .map(u -> new UserSummaryDTO(u.getId(), u.getUsername()))
            .collect(Collectors.toSet());

    return new GroupWithUsersDTO(
            group.getId(),
            group.getName(),
            users
    );
}
```

---

### Alternativa: parámetro en el endpoint

```java
@GetMapping("/{id}")
public Object getGroup(
        @PathVariable Long id,
        @RequestParam(defaultValue = "false") boolean includeUsers) {

    Group group = groupRepository.findById(id)
            .orElseThrow();

    if (!includeUsers) {
        return new GroupSummaryDTO(
                group.getId(),
                group.getName(),
                group.getUsers().size()
        );
    }

    Set<UserSummaryDTO> users = group.getUsers()
            .stream()
            .map(u -> new UserSummaryDTO(u.getId(), u.getUsername()))
            .collect(Collectors.toSet());

    return new GroupWithUsersDTO(
            group.getId(),
            group.getName(),
            users
    );
}
```

📌 **Didácticamente** es mejor:

* endpoints distintos
* DTOs distintos
* tipos claros

---

## 6. Por qué esta es la solución recomendada

| Criterio              | DTOs | Jackson refs |
| --------------------- | ---- | ------------ |
| Evita bucles          | ✅    | ✅            |
| JSON limpio           | ✅    | ❌            |
| Control de exposición | ✅    | ❌            |
| Escalable             | ✅    | ❌            |
| Diseño REST correcto  | ✅    | ❌            |
| Didáctica             | ✅    | ❌            |

---

## 7. Mensaje clave

> En APIs REST, **las entidades no se serializan**.
> Se **transforman** en DTOs diseñados para cada endpoint.

> Los bucles no se solucionan con anotaciones Jackson,
> se evitan con **diseño de API**.

---

## 8. Conclusión

* Many-to-Many **no se expone directamente**
* Cada endpoint define **su propia vista**
* DTOs distintos para:

  * usuario → grupos
  * grupo → sin usuarios
  * grupo → con usuarios
* Jackson deja de ser un problema cuando el diseño es correcto

Este enfoque es:

* Profesional
* Escalable
* Comprensible
* Evaluable

---
