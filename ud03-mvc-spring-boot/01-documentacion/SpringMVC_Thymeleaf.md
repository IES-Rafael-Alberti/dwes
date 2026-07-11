### **Introducción a Thymeleaf**

Thymeleaf es un motor de plantillas moderno y flexible para Java. Está diseñado para integrarse fácilmente con **Spring Boot** y proporciona una forma poderosa de generar vistas dinámicas en aplicaciones web. Thymeleaf es muy similar a Mustache en funcionalidad, pero ofrece más características y una integración más profunda con Spring.

---

### **Ventajas de Thymeleaf**
1. **Integración con Spring Boot**:
   - Thymeleaf se configura automáticamente en proyectos de Spring Boot.
2. **Soporte para HTML5**:
   - Genera vistas compatibles con estándares modernos de HTML.
3. **Lenguaje de Plantillas**:
   - Usa atributos HTML como `th:text` o `th:each` para manipular contenido dinámico.
4. **Vista previa en navegadores**:
   - Las plantillas Thymeleaf son HTML válido, lo que facilita su vista previa en navegadores.

---

### **Configuración Básica de Thymeleaf**

1. **Agregar Thymeleaf al Proyecto**

Si usas Spring Boot, simplemente asegúrate de incluir la dependencia de Thymeleaf en el archivo `pom.xml`:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-thymeleaf</artifactId>
</dependency>
```

> Si trabajas con Gradle:
```groovy
implementation 'org.springframework.boot:spring-boot-starter-thymeleaf'
```

2. **Ubicación de las Plantillas**
   - Las plantillas Thymeleaf se colocan en `src/main/resources/templates/`.
   - El motor Thymeleaf renderizará los archivos `.html` desde esta carpeta.

---

### **Sintaxis Básica de Thymeleaf**

#### **1. Mostrar Datos Dinámicos**
Usa el atributo `th:text` para mostrar contenido dinámico desde el modelo:

```html
<h1 th:text="${title}">Título por defecto</h1>
```
- **`${title}`**: Referencia a una variable en el modelo que contiene el título.

#### **2. Iterar Sobre Listas**
Usa `th:each` para iterar sobre una colección:

```html
<ul>
    <li th:each="product : ${products}">
        <span th:text="${product.name}">Nombre del producto</span>
        <span th:text="${product.price}">Precio del producto</span>
    </li>
</ul>
```
- **`th:each="product : ${products}"`**: Itera sobre la lista `products` y genera un elemento por cada producto.

#### **3. Formularios**
Thymeleaf permite enlazar formularios a objetos directamente:

```html
<form th:action="@{/products}" th:object="${product}" method="post">
    <label for="name">Nombre:</label>
    <input id="name" type="text" th:field="*{name}" />

    <label for="price">Precio:</label>
    <input id="price" type="number" th:field="*{price}" />

    <button type="submit">Guardar</button>
</form>
```
- **`th:object`**: Enlaza el formulario a un objeto del modelo.
- **`th:field`**: Enlaza cada campo del formulario a una propiedad del objeto.

#### **4. Rutas Dinámicas**
Usa `th:href` o `th:action` para generar enlaces dinámicos:

```html
<a th:href="@{/products/new}">Añadir Producto</a>
<a th:href="@{/products/{id}/edit(id=${product.id})}">Editar</a>
```
- **`@{/ruta}`**: Genera rutas absolutas.
- **`@{/ruta/{id}(id=${product.id})}`**: Inserta valores dinámicos en la ruta.

---

### **Migración de Mustache a Thymeleaf**

#### **1. Plantilla para Listar Productos (`products.html`)**

```html
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Lista de Productos</title>
</head>
<body>
    <h1>Lista de Productos</h1>
    <a th:href="@{/products/new}">Añadir Producto</a>

    <table>
        <thead>
            <tr>
                <th>Nombre</th>
                <th>Precio</th>
                <th>Descripción</th>
                <th>Acciones</th>
            </tr>
        </thead>
        <tbody>
            <tr th:each="product : ${products}">
                <td th:text="${product.name}">Nombre</td>
                <td th:text="${product.price}">Precio</td>
                <td th:text="${product.description}">Descripción</td>
                <td>
                    <a th:href="@{/products/{id}/edit(id=${product.id})}">Editar</a>
                    <form th:action="@{/products/{id}/delete(id=${product.id})}" method="post" style="display:inline;">
                        <button type="submit">Eliminar</button>
                    </form>
                </td>
            </tr>
        </tbody>
    </table>
</body>
</html>
```

---

#### **2. Plantilla para Formulario de Productos (`product-form.html`)**

```html
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title th:text="${title}">Formulario</title>
</head>
<body>
    <h1 th:text="${title}">Formulario de Producto</h1>
    <form th:action="@{/products}" th:object="${product}" method="post">
        <div>
            <label for="name">Nombre:</label>
            <input id="name" type="text" th:field="*{name}" required />
        </div>
        <div>
            <label for="price">Precio:</label>
            <input id="price" type="number" th:field="*{price}" required />
        </div>
        <div>
            <label for="description">Descripción:</label>
            <textarea id="description" th:field="*{description}"></textarea>
        </div>
        <button type="submit">Guardar</button>
    </form>
    <a th:href="@{/products}">Volver a la lista</a>
</body>
</html>
```

---

### **Ejercicio**
1. **Configuración**:
   - Sustituye Mustache por Thymeleaf en el proyecto actual.
   - Asegúrate de que las vistas están en la carpeta `src/main/resources/templates/`.

2. **Implementación**:
   - Modifica las plantillas Mustache para usar Thymeleaf.
   - Cambia las rutas dinámicas, iteraciones y formularios según la sintaxis de Thymeleaf.

3. **Validación**:
   - Comprueba que las operaciones CRUD funcionan correctamente con las nuevas plantillas Thymeleaf.

---

### **Puntos Clave**
- Thymeleaf usa atributos HTML (`th:text`, `th:each`) para generar contenido dinámico.
- La transición de Mustache a Thymeleaf es directa gracias a la similitud en la filosofía de plantillas.
- Thymeleaf ofrece mayor flexibilidad en la generación de vistas dinámicas.
