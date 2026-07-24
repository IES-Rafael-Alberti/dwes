# Formularios, validación y Post/Redirect/Get

La entrada del navegador nunca se considera válida por defecto. En MVC, un form object expresa el contrato de entrada, Jakarta Validation lo comprueba y Post/Redirect/Get evita reenvíos accidentales.

## Form object

```java
public class ProductForm {

    @NotBlank
    @Size(max = 120)
    private String name;

    @NotNull
    @PositiveOrZero
    private BigDecimal price;

    // getters and setters
}
```

El formulario no debe enlazarse directamente a una entidad JPA. Un objeto específico limita los campos modificables y separa reglas de entrada de persistencia.

## GET para presentar, POST para procesar

```java
@GetMapping("/products/new")
public String createForm(Model model) {
    model.addAttribute("productForm", new ProductForm());
    return "products/form";
}

@PostMapping("/products")
public String create(
        @Valid @ModelAttribute ProductForm productForm,
        BindingResult bindingResult) {
    if (bindingResult.hasErrors()) {
        return "products/form";
    }
    productService.create(productForm);
    return "redirect:/products";
}
```

`BindingResult` debe aparecer inmediatamente después del objeto validado. Si hay errores se renderiza el mismo formulario, con los valores recibidos. Si la operación termina correctamente se redirige.

## Por qué aplicar PRG

```text
POST /products → 302 Location: /products → GET /products → 200 HTML
```

Actualizar la página final repite el GET, no el POST. PRG mejora la navegación, pero no sustituye restricciones de base de datos ni operaciones idempotentes cuando sean necesarias.

## Mutaciones y CSRF

Crear, editar, cambiar estado, borrar y cerrar sesión son operaciones POST en esta unidad. Los formularios Thymeleaf integrados con Spring Security incluyen el token CSRF; una petición mutante sin token debe rechazarse.

## Contrato de pruebas

- El formulario vacío vuelve a la vista y muestra errores.
- Una entrada inválida no invoca el caso de uso ni cambia datos.
- Una entrada válida ejecuta el caso de uso y redirige.
- Repetir el GET posterior no repite la mutación.

Aplica estas reglas en la práctica [Productos incremental](../03-ejercicios/TareaSpringMVC-Productos/TareaSpringMVC-Productos.md).
