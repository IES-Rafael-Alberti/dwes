### **Tarea Práctica: Aplicación de Gestión de Productos**

#### **Descripción**
El objetivo de esta práctica es desarrollar una aplicación web sencilla utilizando **Spring MVC** y plantillas **Mustache**, siguiendo el patrón **Modelo-Vista-Controlador (MVC)**. La aplicación permitirá gestionar una lista de productos con funcionalidades CRUD (Crear, Leer, Actualizar, Eliminar).

Se evaluará el uso correcto de las plantillas Mustache, la separación de responsabilidades entre modelo, vista y controlador, y la funcionalidad general de la aplicación.

---

### **Requisitos**

1. **Funcionalidades**
   - Listar productos: Mostrar todos los productos en una tabla con su nombre, precio y descripción.
   - Añadir producto: Formulario para agregar un nuevo producto.
   - Editar producto: Formulario para actualizar los datos de un producto existente.
   - Eliminar producto: Opción para eliminar productos de la lista.

2. **Arquitectura del Proyecto**
   - Seguir el patrón MVC:
     - **Modelo**: Una clase `Product` para representar los datos de los productos.
     - **Vista**: Usar plantillas Mustache para diseñar las páginas de la aplicación.
     - **Controlador**: Manejar las rutas y la lógica para interactuar con las vistas y el modelo.
   - Organizar el proyecto en paquetes:
     - `model`: Para la clase `Product`.
     - `repository`: Para el repositorio de productos.
     - `service`: Para la lógica de negocio.
     - `controller`: Para manejar las rutas HTTP.
     - `templates`: Para las vistas Mustache.

3. **Base de Datos**
   - Utilizar una base de datos en memoria H2 para almacenar los datos de los productos.
   - La tabla `product` debe incluir los campos:
     - `id` (clave primaria autoincremental).
     - `name` (nombre del producto, obligatorio).
     - `price` (precio del producto, debe ser mayor o igual a 0).
     - `description` (descripción opcional).

4. **Validaciones**
   - Validar que el nombre del producto no esté vacío.
   - Validar que el precio no sea negativo.

---

### **Rutas Requeridas**

| **Ruta**              | **Descripción**                                                                 |
|------------------------|---------------------------------------------------------------------------------|
| `GET /products`        | Muestra la lista de productos en una tabla.                                    |
| `GET /products/new`    | Muestra un formulario para agregar un nuevo producto.                          |
| `POST /products`       | Procesa el formulario y guarda el nuevo producto.                              |
| `GET /products/{id}/edit` | Muestra un formulario para editar los detalles de un producto existente.    |
| `POST /products/{id}`  | Procesa el formulario y actualiza el producto existente.                       |
| `POST /products/{id}/delete` | Elimina el producto especificado.                                       |

---

### **Pautas**

#### **1. Modelo**

Crea una clase `Product` con los atributos:
- `id` (Long)
- `name` (String)
- `price` (BigDecimal)
- `description` (String)

Ejemplo:

```java
@Entity
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private BigDecimal price;

    private String description;

    // Getters y setters
}
```

#### **2. Controlador**

El controlador debe manejar las rutas descritas en la tabla anterior.

Ejemplo:

```java
@Controller
@RequestMapping("/products")
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public String listProducts(Model model) {
        model.addAttribute("products", productService.getAllProducts());
        return "products";
    }

    @GetMapping("/new")
    public String showAddProductForm(Model model) {
        model.addAttribute("product", new Product());
        return "product-form";
    }

    @PostMapping
    public String addProduct(@ModelAttribute Product product) {
        productService.saveProduct(product);
        return "redirect:/products";
    }

    @GetMapping("/{id}/edit")
    public String showEditProductForm(@PathVariable Long id, Model model) {
        model.addAttribute("product", productService.getProductById(id));
        return "product-form";
    }

    @PostMapping("/{id}")
    public String updateProduct(@PathVariable Long id, @ModelAttribute Product product) {
        productService.updateProduct(id, product);
        return "redirect:/products";
    }

    @PostMapping("/{id}/delete")
    public String deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return "redirect:/products";
    }
}
```

#### **3. Vistas**

1. **`products.mustache`**:
   - Mostrar los productos en una tabla.
   - Añadir botones de "Editar" y "Eliminar" para cada producto.
   - Un enlace para agregar un nuevo producto.

2. **`product-form.mustache`**:
   - Formulario reutilizable para agregar o editar productos.

---

### **Criterios de Evaluación**

1. **Correcta implementación del patrón MVC (40%)**
   - Separación adecuada entre modelo, vista y controlador.
   - Uso de servicios para la lógica de negocio.

2. **Funcionalidad de las Vistas con Mustache (30%)**
   - Listado y formularios funcionales.
   - Diseño consistente y uso de plantillas reutilizables.

3. **Validaciones (20%)**
   - Validaciones del lado del servidor.
   - Mensajes de error mostrados correctamente en las vistas.

4. **Funcionalidad Completa (10%)**
   - Operaciones CRUD funcionales para los productos.

---

### **Entrega**
- Sube el proyecto a un repositorio Git.
- Proporciona un enlace al repositorio.
- Incluir un archivo `README.md` que explique cómo ejecutar la aplicación.

---

### **Puntos Extra**
- Diseño atractivo utilizando CSS o un framework como Bootstrap.
- Implementar fragmentos para encabezado y pie de página (`header.mustache` y `footer.mustache`).
- Agregar búsqueda y filtrado de productos.

---

**¡Buena suerte!** Si tienes dudas durante la implementación, no dudes en consultarlas.
