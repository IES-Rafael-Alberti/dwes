# **Introducción a Spring MVC**

**¿Qué es Spring MVC? (Model-View-Controller)** es un marco de trabajo dentro del ecosistema de **Spring Framework** que permite desarrollar aplicaciones web robustas y escalables siguiendo el patrón de diseño **MVC (Modelo-Vista-Controlador)**. Este patrón divide la lógica de la aplicación en tres componentes principales, lo que facilita la organización del código, el mantenimiento y la reutilización.

**Características principales de Spring MVC**
- Basado en controladores: Spring MVC utiliza controladores para manejar las solicitudes entrantes.
- Motor de vistas flexible: Es compatible con múltiples tecnologías de vistas como JSP, Thymeleaf, Freemarker, etc.
- Inyección de dependencias: Se integra con Spring Core para gestionar los beans y sus dependencias.
- Configuración simplificada: Con anotaciones como @Controller y @RequestMapping, la configuración es más concisa y declarativa.
- Manejo de excepciones: Manejo centralizado y configurable mediante @ControllerAdvice.
- Validación: Soporte integrado para validaciones usando Hibernate Validator y anotaciones como @Valid.


**Ventajas de usar Spring MVC**
- Separación clara de preocupaciones gracias al patrón MVC.
- Extensibilidad para proyectos complejos.
- Integración nativa con otros módulos de Spring, como Spring Data y Spring Security.
- Amplio soporte para tecnologías modernas (RESTful APIs, WebSockets).

---

## **1. Arquitectura de Spring MVC**
Spring MVC sigue el patrón **Modelo-Vista-Controlador**, que tiene los siguientes roles:

1. DispatcherServlet:
    - Es el núcleo de Spring MVC.
    - Actúa como controlador frontal, interceptando todas las solicitudes entrantes y redirigiéndolas al controlador adecuado.
    - Se configura en el archivo web.xml o mediante clases de configuración.

2. **Modelo**
    - Representa los datos y la lógica de negocio de la aplicación.
    - Incluye **objetos de dominio**, **entidades** y **servicios**.
    - Es responsable de interactuar con la base de datos (generalmente a través de **Spring Data JPA**).
    - Normalmente se gestiona mediante clases POJO.

3.  **Vista**
    - Generan el contenido visual (HTML, JSON, etc.) que se devuelve al usuario.
    - Generalmente son páginas HTML o vistas renderizadas usando tecnologías como **Thymeleaf**, **Mustache**, **JSP**, entre otras.
    - Es responsable de recibir los datos del usuario y mostrarlos en un formato amigable.

4. **Controlador**
    - Gestionan la lógica para las solicitudes específicas.
        - Recibe las solicitudes HTTP del cliente.
        - Procesa la solicitud con la ayuda de servicios y repositorios.
        - Devuelve una respuesta HTTP que incluye una **vista** o **datos JSON**/XML en aplicaciones REST.
    - Utilizan anotaciones como @Controller y @RequestMapping.

5. **Configuración**
    - Definida en archivos XML o con Java Config mediante clases anotadas con @Configuration.

---

### **2. Flujo de trabajo de Spring MVC**

1. **Solicitud HTTP del cliente:**
   - Un navegador o cliente envía una solicitud al servidor.
   - El servidor analiza la solicitud y pasa el control a un controlador de Spring MVC.

2. **Controlador:**
   - El controlador procesa la solicitud, interactúa con los servicios y el modelo.
   - Se utiliza anotaciones como `@Controller` o `@RestController` para definir controladores.

3. **Modelo:**
   - Contiene los datos necesarios para la vista.
   - Estos datos pueden ser entidades de base de datos o DTOs (Data Transfer Objects).

4. **Vista:**
   - El controlador selecciona una vista y pasa el modelo a la misma.
   - La vista (como una plantilla Thymeleaf o Mustache) renderiza el modelo en formato HTML o JSON.

5. **Respuesta HTTP:**
   - La vista renderizada se devuelve al cliente como una respuesta HTTP.

---

### **3. Componentes clave en Spring MVC**

#### **Anotaciones principales**
1. `@Controller`: Define una clase como un controlador que gestiona vistas.
2. `@RestController`: Similar a `@Controller`, pero devuelve datos JSON o XML directamente (usado en APIs REST).
3. `@RequestMapping`: Asocia una clase o un método a una ruta específica.
4. `@GetMapping`, `@PostMapping`, `@PutMapping`, `@DeleteMapping`: Atajos para `@RequestMapping` para manejar solicitudes HTTP específicas.
5. `@ModelAttribute`: Vincula datos de un formulario al modelo.
6. `@RequestParam`: Vincula parámetros de consulta a un método de controlador.
7. `@PathVariable`: Vincula partes de la URL a parámetros del controlador.
8. `@ResponseBody`: Devuelve directamente el cuerpo de la respuesta (usualmente para APIs REST).

#### **Ejemplo de Controlador Simple**
```java
@Controller
@RequestMapping("/productos")
public class ProductoController {

    // Manejar solicitudes GET en "/productos"
    @GetMapping
    public String listarProductos(Model model) {
        List<Producto> productos = productoService.obtenerTodos();
        model.addAttribute("productos", productos); // Pasar datos a la vista
        return "productos"; // Nombre de la plantilla en templates/productos.html
    }
}
```

---

### **4. Configuración en Spring Boot**

Spring Boot simplifica la configuración de Spring MVC mediante **auto-configuración**. Esto significa que puedes comenzar con un mínimo esfuerzo.

#### **Configuración básica**
1. **Dependencias en `pom.xml`:**
   ```xml
   <dependency>
       <groupId>org.springframework.boot</groupId>
       <artifactId>spring-boot-starter-web</artifactId>
   </dependency>
   <dependency>
       <groupId>org.springframework.boot</groupId>
       <artifactId>spring-boot-starter-thymeleaf</artifactId>
   </dependency>
   ```

2. **Estructura de directorios típica:**
   ```
   src/
   ├── main/
       ├── java/com/example/miapp/
           ├── controllers/    (Controladores)
           ├── services/       (Lógica de negocio)
           ├── models/         (Entidades/DTOs)
           ├── repositories/   (Acceso a la base de datos)
       ├── resources/
           ├── templates/      (Vistas Thymeleaf o Mustache)
           ├── static/         (CSS/JS/Imágenes)
   ```

3. **Archivo de propiedades (`application.properties`):**
   ```properties
   spring.thymeleaf.prefix=classpath:/templates/
   spring.thymeleaf.suffix=.html
   spring.thymeleaf.cache=false
   spring.mvc.view.prefix=/WEB-INF/views/
   spring.mvc.view.suffix=.jsp
   ```

---

### **5. Ejemplo completo de Spring MVC**

#### **Entidad: Producto**
```java
@Entity
public class Producto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nombre;
    private double precio;

    // Getters y Setters
}
```

#### **Repositorio: ProductoRepository**
```java
public interface ProductoRepository extends JpaRepository<Producto, Long> {
}
```

#### **Servicio: ProductoService**
```java
@Service
public class ProductoService {
    private final ProductoRepository productoRepository;

    @Autowired
    public ProductoService(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    public List<Producto> obtenerTodos() {
        return productoRepository.findAll();
    }
}
```

#### **Controlador: ProductoController**
```java
@Controller
@RequestMapping("/productos")
public class ProductoController {

    private final ProductoService productoService;

    @Autowired
    public ProductoController(ProductoService productoService) {
        this.productoService = productoService;
    }

    @GetMapping
    public String listarProductos(Model model) {
        List<Producto> productos = productoService.obtenerTodos();
        model.addAttribute("productos", productos);
        return "productos"; // Nombre de la vista (productos.html en /templates/)
    }
}
```

#### **Plantilla Thymeleaf: `productos.html`**
```html
<!DOCTYPE html>
<html>
<head>
    <title>Lista de Productos</title>
</head>
<body>
    <h1>Lista de Productos</h1>
    <table>
        <thead>
            <tr>
                <th>ID</th>
                <th>Nombre</th>
                <th>Precio</th>
            </tr>
        </thead>
        <tbody>
            <tr th:each="producto : ${productos}">
                <td th:text="${producto.id}"></td>
                <td th:text="${producto.nombre}"></td>
                <td th:text="${producto.precio}"></td>
            </tr>
        </tbody>
    </table>
</body>
</html>
```

---

### **6. Características clave**

- **Validación:** Integración con `@Valid` y Bean Validation para validar formularios.
- **Internacionalización (i18n):** Soporte para traducciones con archivos de propiedades.
- **Manejo de excepciones:** Usar `@ControllerAdvice` para manejar errores globalmente.

---

Con **Spring MVC**, puedes construir aplicaciones web sólidas, organizadas y fáciles de mantener, utilizando tecnologías modernas como Thymeleaf o APIs REST para proporcionar contenido dinámico a los usuarios.

---

## Mustache como motor de vistas
Si quieres usar **Mustache** como motor de vistas en lugar de JSP, te proporciono las dependencias necesarias y cómo configurarlo en tu proyecto **Spring MVC**. Mustache es un motor de plantillas ligero y fácil de usar que funciona muy bien con aplicaciones Spring.

---

### **Dependencias para Mustache con Spring MVC**

#### **Con Maven**
Tu archivo `pom.xml` debe incluir las siguientes dependencias esenciales:

```xml
<dependencies>
    <!-- Spring Web MVC -->
    <dependency>
        <groupId>org.springframework</groupId>
        <artifactId>spring-webmvc</artifactId>
        <version>6.0.11</version>
    </dependency>

    <!-- Mustache Template Engine -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-mustache</artifactId>
        <version>3.2.1</version> <!-- Asegúrate de usar una versión compatible -->
    </dependency>

    <!-- Logging con SLF4J -->
    <dependency>
        <groupId>org.slf4j</groupId>
        <artifactId>slf4j-api</artifactId>
        <version>2.0.9</version>
    </dependency>
    <dependency>
        <groupId>org.slf4j</groupId>
        <artifactId>slf4j-simple</artifactId>
        <version>2.0.9</version>
    </dependency>
</dependencies>
```

#### **Con Gradle**
Tu archivo `build.gradle` debe verse así:

```groovy
dependencies {
    // Spring Web MVC
    implementation 'org.springframework:spring-webmvc:6.0.11'

    // Mustache Template Engine
    implementation 'org.springframework.boot:spring-boot-starter-mustache:3.2.1'

    // Logging con SLF4J
    implementation 'org.slf4j:slf4j-api:2.0.9'
    runtimeOnly 'org.slf4j:slf4j-simple:2.0.9'
}
```

---

### **Configuración para Mustache**
Spring Boot y Spring MVC ya tienen soporte integrado para **Mustache**, por lo que solo necesitas configurar el directorio donde estarán tus plantillas. Por defecto, las plantillas deben estar en `src/main/resources/templates`.

No necesitas una configuración explícita en `application.properties` para usar Mustache, pero puedes personalizar el directorio si lo deseas:

```properties
spring.mustache.prefix=classpath:/templates/
spring.mustache.suffix=.mustache
```

---

### **Estructura del proyecto**
El directorio base de tu proyecto se verá así:

```
src/main/java
├── com.example.controller
│   └── HomeController.java
src/main/resources
└── templates
    ├── index.mustache
    └── about.mustache
```

---

### **Ejemplo básico con Mustache**

#### **Controlador**
Crea un controlador que gestione las solicitudes:

```java
package com.example.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("title", "Bienvenido a Spring MVC con Mustache");
        model.addAttribute("description", "Este es un ejemplo usando Mustache como motor de plantillas.");
        return "index"; // Nombre de la plantilla (sin la extensión .mustache)
    }

    @GetMapping("/about")
    public String about(Model model) {
        model.addAttribute("title", "Acerca de");
        model.addAttribute("description", "Página de información usando Mustache.");
        return "about";
    }
}
```

#### **Plantillas Mustache**
Crea las plantillas en el directorio `src/main/resources/templates`:

**`index.mustache`**
```html
<!DOCTYPE html>
<html>
<head>
    <title>{{title}}</title>
</head>
<body>
    <h1>{{title}}</h1>
    <p>{{description}}</p>
    <a href="/about">Acerca de</a>
</body>
</html>
```

**`about.mustache`**
```html
<!DOCTYPE html>
<html>
<head>
    <title>{{title}}</title>
</head>
<body>
    <h1>{{title}}</h1>
    <p>{{description}}</p>
    <a href="/">Inicio</a>
</body>
</html>
```

---

### **Pruebas**
1. Inicia tu aplicación en un servidor (por ejemplo, Tomcat o el servidor embebido de Spring).
2. Abre tu navegador y accede a `http://localhost:8080/`.
3. Deberías ver la página generada por la plantilla **index.mustache**.

---

### **Ventajas de usar Mustache**
- Plantillas ligeras y de fácil lectura.
- Sin lógica en las vistas, promoviendo la separación de responsabilidades.
- Integración perfecta con Spring Boot y Spring MVC.

