
# 📘 Guía práctica de Thymeleaf en Spring Boot

## 1. 🧩 ¿Qué es Thymeleaf?

Thymeleaf es un motor de plantillas para Java que permite generar vistas HTML dinámicas. Se integra fácilmente con Spring Boot y respeta el modelo MVC.

---

## 2. ⚙️ Configuración básica

En `application.properties`:

```properties
spring.thymeleaf.prefix=classpath:/templates/
spring.thymeleaf.suffix=.html
spring.thymeleaf.mode=HTML
spring.thymeleaf.encoding=UTF-8
spring.thymeleaf.cache=false
```

Las vistas deben estar en `src/main/resources/templates/`.

---

## 3. 📤 Enviar datos desde el controlador a la vista

### ✅ Enviar un solo objeto

```java
@GetMapping("/usuario/{id}")
public String verUsuario(@PathVariable Long id, Model model) {
    Usuario usuario = servicio.buscarPorId(id);
    model.addAttribute("usuario", usuario);
    return "usuario/detalle";
}
```

### ✅ Enviar una lista

```java
@GetMapping("/usuarios")
public String listarUsuarios(Model model) {
    List<Usuario> usuarios = servicio.listarTodos();
    model.addAttribute("usuarios", usuarios);
    return "usuario/listado";
}
```

---

## 4. 📥 Recibir datos desde la vista (formulario)

### ✅ Formulario HTML con Thymeleaf

```html
<form th:action="@{/usuario/guardar}" th:object="${usuario}" method="post">
    <input type="text" th:field="*{nombre}" />
    <input type="email" th:field="*{email}" />
    <button type="submit">Guardar</button>
</form>
```

### ✅ Controlador que recibe el formulario

```java
@PostMapping("/usuario/guardar")
public String guardarUsuario(@ModelAttribute Usuario usuario) {
    servicio.guardar(usuario);
    return "redirect:/usuarios";
}
```

---

## 5. 📋 Mostrar datos en la vista

### ✅ Mostrar un solo objeto

```html
<h2 th:text="${usuario.nombre}">Nombre</h2>
<p th:text="${usuario.email}">Email</p>
```

### ✅ Mostrar una lista en tabla

```html
<table>
    <thead>
        <tr><th>Nombre</th><th>Email</th></tr>
    </thead>
    <tbody>
        <tr th:each="usuario : ${usuarios}">
            <td th:text="${usuario.nombre}"></td>
            <td th:text="${usuario.email}"></td>
        </tr>
    </tbody>
</table>
```

---

## 6. 🧠 Extras útiles

### 🔁 Condicionales

```html
<p th:if="${usuario.activo}">Usuario activo</p>
<p th:unless="${usuario.activo}">Usuario inactivo</p>
```

### 🔄 Iterar con índice

```html
<tr th:each="usuario, iterStat : ${usuarios}">
    <td th:text="${iterStat.index}"></td>
    <td th:text="${usuario.nombre}"></td>
</tr>
```

### 📎 Enlaces dinámicos

```html
<a th:href="@{/usuario/{id}(id=${usuario.id})}">Ver detalle</a>
```

---

## 7. 🧪 Buenas prácticas docentes

- Usa `ModelAttribute` para formularios.
- Separa vistas y APIs (`@Controller` vs `@RestController`).
- Evita lógica compleja en las plantillas.
- Usa `th:object` y `th:field` para formularios vinculados.

---
