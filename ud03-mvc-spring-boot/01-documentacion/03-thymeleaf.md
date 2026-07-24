# Thymeleaf para vistas MVC

Thymeleaf es el motor de plantillas principal de UD3 porque se integra con el modelo, los formularios, la validación y Spring Security sin convertir la plantilla en lógica de negocio.

## Sintaxis esencial

| Necesidad | Thymeleaf |
| --- | --- |
| Mostrar texto escapado | `th:text="${product.name}"` |
| Recorrer una colección | `th:each="product : ${products}"` |
| Mostrar condicionalmente | `th:if="${products.isEmpty()}"` |
| Construir una URL | `th:href="@{/products/{id}(id=${product.id})}"` |
| Vincular un formulario | `th:object="${productForm}"` |
| Vincular un campo | `th:field="*{name}"` |
| Mostrar errores | `th:errors="*{name}"` |

## Listado

```html
<tbody>
  <tr th:each="product : ${products}">
    <td th:text="${product.name}">Sample product</td>
    <td th:text="${product.price}">0.00</td>
    <td><a th:href="@{/products/{id}(id=${product.id})}">View</a></td>
  </tr>
</tbody>
```

Las expresiones se evalúan en el servidor. `th:text` escapa el contenido antes de insertarlo y es la opción segura para texto aportado por usuarios.

## Formularios

```html
<form th:action="@{/products}" th:object="${productForm}" method="post">
  <label for="name">Name</label>
  <input id="name" th:field="*{name}">
  <p th:if="${#fields.hasErrors('name')}" th:errors="*{name}">Invalid name</p>
  <button type="submit">Save</button>
</form>
```

`th:field` mantiene el valor rechazado y asocia el campo con los errores de validación. Con Spring Security, los formularios POST procesados por Thymeleaf incorporan el token CSRF.

## Mantener las plantillas simples

- Preparar en el controlador los datos que necesita la vista.
- Mover decisiones de negocio al servicio.
- Usar fragmentos solo cuando reducen repetición real.
- No usar `th:utext` con entrada no confiable: inserta HTML sin escapar.
- No depender de ocultar botones para autorizar una operación; el servidor debe comprobarla.

## Comparación opcional con Mustache

Mustache usa secciones como `{{#products}}` y variables como `{{name}}`. Su lógica deliberadamente limitada puede ser útil en plantillas pequeñas, pero no se instala ni se evalúa en UD3. Mantener un segundo motor duplicaría configuración, ejemplos y criterios sin aportar una progresión nueva.

El siguiente paso integra la plantilla con [formularios, validación y PRG](04-formularios-validacion-prg.md).
