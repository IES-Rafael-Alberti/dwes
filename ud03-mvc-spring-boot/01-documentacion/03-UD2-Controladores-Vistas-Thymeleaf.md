
## **Capítulo 3. Controladores y Vistas**

### **3.1. Características del Controlador**
Un **controlador** es una clase que maneja las solicitudes HTTP y coordina la lógica entre el modelo (datos) y la vista (interfaz). En frameworks como Spring Boot, los controladores son responsables de:

1. **Procesar Solicitudes:**
   - Reciben las solicitudes HTTP del cliente.
   - Determinan qué acción realizar según la URL y el método HTTP.

2. **Interactuar con el Modelo:**
   - Consultan o modifican datos en la base de datos o servicios.
   - Preparan los datos para ser mostrados en la vista.

3. **Devolver Respuestas:**
   - Envían una respuesta al cliente, ya sea una vista HTML, JSON u otro tipo de contenido.

#### **Ejemplo de un Controlador en Spring Boot:**
```java
@RestController
@RequestMapping("/api")
public class DemoController {

    @GetMapping("/saludo")
    public String saludar() {
        return "¡Hola, mundo!";
    }
}
```

- **`@RestController`:** Indica que esta clase es un controlador que devuelve datos directamente (no una vista).
- **`@RequestMapping`:** Define la ruta base para todas las solicitudes manejadas por este controlador.
- **`@GetMapping`:** Maneja solicitudes GET específicas.

---

### **3.2. Thymeleaf y Contenido Dinámico**
**Thymeleaf** es un motor de plantillas que permite generar contenido HTML dinámico en aplicaciones Spring Boot. Es ideal para aplicaciones web que requieren combinar datos del servidor con HTML estático.

#### **Características de Thymeleaf:**
1. **Sintaxis Sencilla:**
   - Usa atributos personalizados como `th:text`, `th:if`, `th:each` para manipular el HTML.

2. **Compatibilidad con HTML Estático:**
   - Las plantillas Thymeleaf pueden abrirse directamente en el navegador sin necesidad de un servidor.

3. **Integración con Spring Boot:**
   - Spring Boot incluye soporte nativo para Thymeleaf.

#### **Ejemplo de Uso de Thymeleaf:**
```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <title>Ejemplo Thymeleaf</title>
</head>
<body>
    <h1 th:text="${mensaje}">Mensaje predeterminado</h1>
</body>
</html>
```

- **`${mensaje}`:** Expresión Thymeleaf que inserta el valor de la variable `mensaje` enviada desde el controlador.

---

### **3.3. Paso de Datos a la Plantilla**
Los controladores pueden pasar datos a las vistas mediante el objeto `Model`. Este objeto actúa como un contenedor para los datos que se enviarán a la plantilla.

#### **Ejemplo de Paso de Datos:**
```java
@Controller
public class SaludoController {

    @GetMapping("/saludo")
    public String mostrarSaludo(Model model) {
        model.addAttribute("nombre", "Juan");
        model.addAttribute("edad", 25);
        return "saludo"; // Nombre de la plantilla Thymeleaf
    }
}
```

- **`model.addAttribute`:** Agrega variables al modelo que estarán disponibles en la vista.
- **Plantilla Thymeleaf (`saludo.html`):**
```html
<h1 th:text="'Hola, ' + ${nombre} + '!'"></h1>
<p th:text="'Edad: ' + ${edad}"></p>
```

---

### **3.4. Rescatar Parámetros de las URL**
Es común que las solicitudes HTTP incluyan parámetros en la URL o en el cuerpo de la solicitud. Spring Boot facilita el acceso a estos parámetros mediante anotaciones.

#### **Parámetros de la URL:**
```java
@GetMapping("/saludo/{nombre}")
public String saludar(@PathVariable String nombre, Model model) {
    model.addAttribute("mensaje", "Hola, " + nombre + "!");
    return "saludo";
}
```

- **`@PathVariable`:** Extrae valores de la URL.

#### **Parámetros de Consulta:**
```java
@GetMapping("/buscar")
public String buscar(@RequestParam String query, Model model) {
    model.addAttribute("resultado", "Buscando: " + query);
    return "resultado";
}
```

- **`@RequestParam`:** Extrae parámetros de la cadena de consulta (p. ej., `?query=valor`).

---

### **3.5. Retorno de los Métodos del Controlador**
Los métodos de un controlador pueden devolver diferentes tipos de respuestas dependiendo del caso:

1. **Vistas HTML:**
   - Devuelven el nombre de una plantilla Thymeleaf.
   ```java
   return "vista";
   ```

2. **Datos JSON:**
   - Usado en APIs RESTful.
   ```java
   @GetMapping("/datos")
   public Map<String, String> obtenerDatos() {
       Map<String, String> datos = new HashMap<>();
       datos.put("nombre", "Juan");
       datos.put("edad", "25");
       return datos;
   }
   ```

3. **Redirecciones:**
   - Redirige a otra URL.
   ```java
   return "redirect:/nueva-ruta";
   ```

4. **Respuestas Personalizadas:**
   - Devuelve un objeto `ResponseEntity` para controlar el estado HTTP y los encabezados.
   ```java
   @GetMapping("/custom")
   public ResponseEntity<String> respuestaCustom() {
       return ResponseEntity.status(HttpStatus.OK).body("Respuesta personalizada");
   }
   ```

---

### **3.6. Construcción de URL Dinámicas en la Vista**
Thymeleaf permite construir URLs dinámicas utilizando expresiones como `@{...}`.

#### **Ejemplo de Construcción de URL:**
```html
<a th:href="@{/saludo/{nombre}(nombre=${usuario.nombre})}">Saludar</a>
```

- **`@{/ruta}`:** Genera una URL absoluta basada en la ruta especificada.
- **`{nombre}`:** Inserta un parámetro dinámico en la URL.

---

### **3.7. Resumen de Controlador + Vista**
En resumen:
1. **Controladores:**
   - Procesan solicitudes HTTP.
   - Interactúan con el modelo y los servicios.
   - Devuelven respuestas (vistas, JSON, redirecciones).

2. **Vistas:**
   - Muestran los datos preparados por el controlador.
   - Usan motores de plantillas como Thymeleaf para generar contenido dinámico.

3. **Colaboración:**
   - El controlador pasa datos al modelo.
   - La vista usa esos datos para renderizar la interfaz.


---

### **Configuración del Archivo `application.properties`**

El archivo `application.properties` es un archivo de configuración clave en Spring Boot. Se encuentra en la carpeta `src/main/resources` y permite personalizar el comportamiento de la aplicación. A continuación, se muestra una configuración básica para trabajar con controladores y vistas usando Thymeleaf:

```properties
# Configuración del servidor
server.port=8080 # Puerto donde se ejecutará la aplicación

# Configuración de Thymeleaf
spring.thymeleaf.prefix=classpath:/templates/ # Ubicación de las plantillas HTML
spring.thymeleaf.suffix=.html # Extensión de los archivos de plantilla
spring.thymeleaf.cache=false # Desactivar caché para desarrollo (útil para recargar cambios rápidamente)

# Configuración de codificación
server.servlet.encoding.charset=UTF-8
server.servlet.encoding.enabled=true
server.servlet.encoding.force=true

# Configuración de logging (opcional)
logging.level.org.springframework=INFO # Nivel de logs para Spring
logging.level.com.example=DEBUG # Nivel de logs para tu paquete de aplicación

# Configuración opcional de la base de datos (si usas una)
# spring.datasource.url=jdbc:mysql://localhost:3306/mi_base_de_datos
# spring.datasource.username=root
# spring.datasource.password=password
# spring.jpa.hibernate.ddl-auto=update
```

---

### **Explicación de las Propiedades**

1. **Configuración del Servidor:**
   - **`server.port`:** Define el puerto donde se ejecutará la aplicación. Por defecto, Spring Boot usa el puerto `8080`, pero puedes cambiarlo si es necesario.

2. **Configuración de Thymeleaf:**
   - **`spring.thymeleaf.prefix`:** Especifica la carpeta donde se encuentran las plantillas HTML. Por defecto, Spring busca en `src/main/resources/templates`.
   - **`spring.thymeleaf.suffix`:** Define la extensión de los archivos de plantilla. Por defecto, es `.html`.
   - **`spring.thymeleaf.cache`:** Desactiva la caché durante el desarrollo para que los cambios en las plantillas se reflejen inmediatamente sin reiniciar la aplicación.

3. **Codificación:**
   - **`server.servlet.encoding.*`:** Asegura que la aplicación maneje correctamente caracteres especiales (como tildes o emojis) usando UTF-8.

4. **Logging:**
   - **`logging.level.*`:** Configura el nivel de detalle de los logs. Esto es útil para depurar problemas o monitorear el comportamiento de la aplicación.

5. **Base de Datos (Opcional):**
   - Si tu aplicación necesita una base de datos, puedes configurarla aquí. Los parámetros `spring.datasource.*` permiten definir la URL, nombre de usuario y contraseña de la base de datos.
   - **`spring.jpa.hibernate.ddl-auto`:** Configura cómo Hibernate maneja las tablas de la base de datos (`create`, `update`, `validate`, etc.).

---

### **Ejemplo Práctico: Estructura del Proyecto**

Aquí tienes un ejemplo de cómo podría organizarse un proyecto básico con Thymeleaf y el archivo `application.properties`:

#### **Estructura del Proyecto**
```
src/
└── main/
    ├── java/
    │   └── com.example.demo/
    │       ├── DemoApplication.java
    │       └── controller/
    │           └── SaludoController.java
    └── resources/
        ├── application.properties
        ├── templates/
        │   └── saludo.html
        └── static/
            └── style.css
```

#### **Archivo `saludo.html`**
```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <title>Saludo</title>
    <link rel="stylesheet" th:href="@{/style.css}">
</head>
<body>
    <h1 th:text="'Hola, ' + ${nombre} + '!'"></h1>
    <p th:text="'Edad: ' + ${edad}"></p>
</body>
</html>
```

#### **Controlador `SaludoController.java`**
```java
package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SaludoController {

    @GetMapping("/saludo")
    public String mostrarSaludo(Model model) {
        model.addAttribute("nombre", "Juan");
        model.addAttribute("edad", 25);
        return "saludo"; // Nombre de la plantilla Thymeleaf
    }
}
```

---

### **Cómo Funciona Todo Junto**

1. **El Cliente Solicita una Página:**
   - El navegador envía una solicitud GET a `/saludo`.

2. **El Controlador Procesa la Solicitud:**
   - El método `mostrarSaludo` del controlador prepara los datos (`nombre` y `edad`) y los agrega al modelo.

3. **Thymeleaf Renderiza la Vista:**
   - La plantilla `saludo.html` utiliza los datos del modelo para generar el HTML final.

4. **La Respuesta se Envía al Cliente:**
   - El servidor devuelve el HTML renderizado al navegador, que lo muestra al usuario.


### **Ejercicios de Ampliación**
1. Crea un controlador que maneje una solicitud GET con un parámetro de consulta y muestre el resultado en una vista Thymeleaf.
2. Implementa un formulario HTML que envíe datos POST a un controlador. El controlador debe procesar los datos y devolver una respuesta.
3. Usa Thymeleaf para iterar sobre una lista de elementos y mostrarlos en una tabla HTML.

---

### **Conclusión**
El controlador y la vista son componentes esenciales en el desarrollo de aplicaciones web. Los controladores gestionan la lógica de negocio y las solicitudes HTTP, mientras que las vistas muestran los datos al usuario. Herramientas como Thymeleaf facilitan la creación de interfaces dinámicas y flexibles.


El archivo `application.properties` es fundamental para configurar el comportamiento de tu aplicación Spring Boot. Con las propiedades adecuadas, puedes personalizar aspectos como el puerto del servidor, la ubicación de las plantillas Thymeleaf y la codificación de caracteres. Además, esta configuración te permite integrar fácilmente bases de datos y otras herramientas según sea necesario.