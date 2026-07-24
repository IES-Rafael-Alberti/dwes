# Controladores y vistas

Un controlador MVC devuelve un nombre de vista y prepara su modelo. `@RestController`, en cambio, escribe datos directamente en la respuesta y no es la herramienta adecuada para renderizar una plantilla.

## Primer controlador

```java
@Controller
public class GreetingController {

    @GetMapping("/greeting")
    public String showGreeting(
            @RequestParam(defaultValue = "visitor") String name,
            Model model) {
        model.addAttribute("name", name);
        return "greeting";
    }
}
```

La plantilla correspondiente es `src/main/resources/templates/greeting.html`:

```html
<!doctype html>
<html lang="en" xmlns:th="http://www.thymeleaf.org">
<head><meta charset="UTF-8"><title>Greeting</title></head>
<body>
  <h1 th:text="|Hello, ${name}!|">Hello, visitor!</h1>
</body>
</html>
```

El texto estático permite abrir la plantilla sin ejecutar la aplicación; Thymeleaf sustituye el contenido cuando la renderiza.

## Datos de entrada

| Anotación | Origen | Ejemplo |
| --- | --- | --- |
| `@PathVariable` | Segmento de la ruta | `/products/{id}` |
| `@RequestParam` | Cadena de consulta o campo simple | `/products?category=books` |
| `@ModelAttribute` | Campos de formulario vinculados a un objeto | `ProductForm` |

El método HTTP expresa la intención: GET consulta y presenta; POST crea o ejecuta una operación con efecto. Editar y borrar no deben ocultarse detrás de un GET.

## Resultados del controlador

- `"products/list"`: renderiza `templates/products/list.html`.
- `"redirect:/products"`: responde con una redirección; el navegador realiza un nuevo GET.
- `ResponseEntity` y `@ResponseBody`: pertenecen al recorrido de API, no al de vistas de esta unidad.

## Controladores finos

El controlador coordina, pero no decide reglas de negocio:

```java
@GetMapping("/products/{id}")
public String show(@PathVariable Long id, Model model) {
    model.addAttribute("product", productService.getRequired(id));
    return "products/detail";
}
```

La búsqueda y el tratamiento de “no encontrado” pertenecen al servicio. Esta separación permite probar HTTP y negocio con alcances distintos.

## Prueba observable

Una prueba MVC debe comprobar, como mínimo, estado, vista y atributo del modelo:

```java
mockMvc.perform(get("/greeting").param("name", "Ada"))
    .andExpect(status().isOk())
    .andExpect(view().name("greeting"))
    .andExpect(model().attribute("name", "Ada"));
```

Continúa con la [sintaxis esencial de Thymeleaf](03-thymeleaf.md).
