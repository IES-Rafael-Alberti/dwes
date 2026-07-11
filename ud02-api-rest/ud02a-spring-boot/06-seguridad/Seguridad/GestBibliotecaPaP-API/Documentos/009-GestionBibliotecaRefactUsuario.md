El diseño de `Usuario` con rol como `String` no es compatible con el diseño estandar de `CustomUserDetails` **. En su forma normal, espera que los roles en `Usuario` sean una colección de objetos que pueden ser iterados (`usuario.getRoles()`), lo que no se aplica a un simple campo `String`.

### Opciones para Resolver la Compatibilidad con Roles y JWT

#### **Opción 1: Refactorizar para Usar un Enum para Roles**
Esta es la mejor opción porque:
- **Claridad y seguridad**: Los enums garantizan que solo se usen valores válidos (e.g., `USUARIO`, `BIBLIOTECARIO`).
- **Compatibilidad con `CustomUserDetails`**: Se ajusta perfectamente al diseño actual que convierte roles en `GrantedAuthority`.

##### Cambios Requeridos:
1. **Modificar la entidad `Usuario`:**
   - Cambiar el campo `rol` de `String` a un enum como `Set<Rol>` (o `Rol` si solo permitimos un rol por usuario).

```java
@Entity
public class Usuario {
    // Otros campos...

    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    private Set<Rol> roles = new HashSet<>();
}
```

2. **Definir el enum `Rol`:**

```java
public enum Rol {
    USUARIO, BIBLIOTECARIO
}
```

3. **Actualizar `CustomUserDetails`:**
   - Esto ya es compatible con la implementación actual:

```java
@Override
public Collection<? extends GrantedAuthority> getAuthorities() {
    return usuario.getRoles().stream()
            .map(rol -> new SimpleGrantedAuthority("ROLE_" + rol.name()))
            .collect(Collectors.toList());
}
```

4. **Asegurarte de asignar roles al registrar usuarios:**
   - Asignar `Set.of(Rol.USUARIO)` para usuarios por defecto.

---

#### **Opción 2: Adaptar `CustomUserDetails` para Usar `String`**
Si prefieres no cambiar el diseño actual de roles como `String`, necesitas modificar `CustomUserDetails` para mapear el `String` directamente.

##### Cambios Requeridos:
1. **Actualizar el método `getAuthorities`:**

```java
@Override
public Collection<? extends GrantedAuthority> getAuthorities() {
    // Si el rol es un String en Usuario
    return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + usuario.getRol().toUpperCase()));
}
```

2. **Mantener el rol como `String` en la entidad `Usuario`:**

```java
@Entity
public class Usuario {
    // Otros campos...

    private String rol;
}
```

##### Desventajas:
- Menos seguro que un `enum`, ya que `String` permite cualquier valor y no garantiza consistencia.

---

### **Recomendación**
Opta por la **Opción 1: Refactorizar para usar un Enum**. Esto alinea tu diseño con las mejores prácticas de Spring Security:
- Los `Roles` como enums son más seguros y legibles.
- El diseño es más extensible si necesitas varios roles por usuario.
- Mejora la compatibilidad con `GrantedAuthority` y futuras implementaciones de autorización.

#### **Pasos Inmediatos si Refactorizas a Enum:**
1. Cambiar `String` por `Set<Rol>` en `Usuario`.
2. Actualizar el registro de usuarios para asignar un rol por defecto (`Set.of(Rol.USUARIO)`).
3. Adaptar las consultas y validaciones existentes (e.g., `usuario.getRol().equals("BIBLIOTECARIO")` → `usuario.getRoles().contains(Rol.BIBLIOTECARIO)`).

---

¿Deseas proceder con la refactorización a enum o adaptar la implementación actual para roles como `String`? 😊

 Refactoricemos para usar un `enum` para los roles. A continuación, detallo los pasos para realizar esta transición de manera segura y efectiva:

---

### **1. Crear el Enum `Rol`**
Define un `enum` que represente los roles disponibles en el sistema.

```java
package daw2a.gestionbiblioteca.enums;

public enum Rol {
    USUARIO, 
    BIBLIOTECARIO
}
```

---

### **2. Actualizar la Entidad `Usuario`**
Reemplaza el campo `rol` actual (que es un `String`) por un conjunto de roles (`Set<Rol>`). Usaremos `@ElementCollection` para almacenar los roles en una tabla separada.

```java
package daw2a.gestionbiblioteca.entities;

import daw2a.gestionbiblioteca.enums.Rol;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    private Set<Rol> roles = new HashSet<>();
}
```

---

### **3. Adaptar `CustomUserDetails`**
Este cambio se alinea perfectamente con la implementación actual de `CustomUserDetails`.

```java
package daw2a.gestionbiblioteca.security;

import daw2a.gestionbiblioteca.entities.Usuario;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.stream.Collectors;

public class CustomUserDetails implements UserDetails {

    private final Usuario usuario;

    public CustomUserDetails(Usuario usuario) {
        this.usuario = usuario;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return usuario.getRoles().stream()
                .map(rol -> new SimpleGrantedAuthority("ROLE_" + rol.name()))
                .collect(Collectors.toList());
    }

    @Override
    public String getPassword() {
        return usuario.getPassword();
    }

    @Override
    public String getUsername() {
        return usuario.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
```

---

### **4. Adaptar el Registro de Usuarios**
Asegúrate de asignar un rol predeterminado al registrar un usuario. Por ejemplo, un nuevo usuario podría tener el rol `USUARIO` por defecto.

```java
public Usuario registrarUsuario(Usuario usuario) {
    if (usuarioRepository.findUsuarioByEmail(usuario.getEmail()).isPresent()) {
        throw new RecursoDuplicadoException("El email " + usuario.getEmail() + " ya está en uso.");
    }

    // Codificar la contraseña y asignar un rol predeterminado
    usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
    usuario.setRoles(Set.of(Rol.USUARIO));
    return usuarioRepository.save(usuario);
}
```

---

### **5. Modificar las Consultas de Roles**
Asegúrate de que todas las partes del sistema que verifican roles usen el nuevo enfoque. Por ejemplo:

Antes:
```java
if (usuario.getRol().equals("BIBLIOTECARIO")) {
    // Lógica para bibliotecario
}
```

Después:
```java
if (usuario.getRoles().contains(Rol.BIBLIOTECARIO)) {
    // Lógica para bibliotecario
}
```

---

### **6. Actualizar Pruebas**
Actualiza las pruebas unitarias para reflejar los nuevos roles como `Set<Rol>`.

Ejemplo:
```java
Usuario usuario = Usuario.builder()
        .nombre("Juan")
        .email("juan@example.com")
        .password("1234")
        .roles(Set.of(Rol.USUARIO))
        .build();
```

---

### **7. Actualizar la Base de Datos**
Si estás usando una base de datos real (como MySQL o PostgreSQL), aplica las migraciones necesarias:
- Crea una tabla para almacenar los roles (`usuario_roles`).
- Asocia los roles con los usuarios.

Si usas `data.sql` para inicializar datos, añade roles al registrar usuarios.

---

### **8. Validar con Pruebas**
Ejecuta las pruebas del sistema para garantizar que la transición a `enum` no rompe la funcionalidad existente:
- Pruebas de autenticación.
- Pruebas de autorización (con roles específicos).

---

### **Beneficios de la Refactorización**
- **Mayor seguridad:** Los roles ahora son valores predefinidos y no dependen de `String`.
- **Claridad y mantenibilidad:** Es más fácil entender y trabajar con roles usando enums.
- **Compatibilidad con Spring Security:** Los enums se integran perfectamente con `GrantedAuthority`.

¡Por supuesto! Al realizar la refactorización de `Usuario` para usar un conjunto de roles (`Set<Rol>`), también debemos actualizar los métodos `equals` y `hashCode` para reflejar correctamente las nuevas características de la entidad. Aquí está cómo puedes implementarlos:

---

### **Actualización de `equals` y `hashCode`**

#### **Nuevo Código para `Usuario`**
Incluye los campos más relevantes como `id`, `email` y otros identificadores únicos en `equals` y `hashCode`. Los roles pueden incluirse si es necesario, pero generalmente `id` o `email` son suficientes para garantizar la unicidad.

```java
@Override
public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Usuario usuario = (Usuario) o;
    return Objects.equals(id, usuario.id) &&
           Objects.equals(email, usuario.email);
}

@Override
public int hashCode() {
    return Objects.hash(id, email);
}
```

---

### **Explicación del Código**

1. **`equals`:**
   - Comprueba primero si los objetos comparados son el mismo (`this == o`).
   - Comprueba si el objeto `o` no es `null` y es de la misma clase que la instancia actual (`getClass()`).
   - Compara los valores de los campos que definen la identidad del usuario. En este caso, utilizamos `id` y `email` porque son únicos por naturaleza.

2. **`hashCode`:**
   - Genera un hash basado en los mismos campos utilizados en `equals`. Esto garantiza la coherencia entre los dos métodos.

---

### **¿Por qué no incluir los roles en `equals` y `hashCode`?**

- Los roles (`Set<Rol>`) pueden cambiar durante la vida del usuario (por ejemplo, al actualizar los permisos), pero no necesariamente deben afectar su identidad.
- Solo debe usarse en `equals` y `hashCode` si es crucial para identificar la unicidad de un usuario, lo cual no es el caso aquí porque `id` y `email` son suficientes.

---

### **Beneficios de esta Implementación**
- **Rendimiento:** Solo incluye campos clave como `id` y `email`, lo que hace que las comparaciones sean rápidas.
- **Consistencia:** Cambiar un rol no afecta la identidad del usuario en colecciones como `Set` o `Map`.

---

