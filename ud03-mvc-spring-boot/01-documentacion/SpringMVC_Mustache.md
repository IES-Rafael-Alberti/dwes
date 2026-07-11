Mustache es un motor de plantillas ligero y fácil de usar que se integra perfectamente con Spring MVC para generar vistas dinámicas. Aquí tienes una explicación detallada de cómo funciona y cómo puedes usarlo en tu proyecto Spring MVC:

### **¿Qué es Mustache?**
Mustache es un motor de plantillas que permite separar la lógica de presentación del código de la aplicación. Utiliza una sintaxis simple y lógica para insertar datos en plantillas HTML, lo que facilita la creación de vistas dinámicas.

### **Integración de Mustache con Spring MVC**

#### **Dependencias**
Para usar Mustache con Spring MVC, necesitas agregar las dependencias necesarias en tu archivo de configuración de Maven o Gradle.

**Con Maven**:
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
        <version>3.2.1</version>
    </dependency>
</dependencies>
```

**Con Gradle**:
```groovy
dependencies {
    // Spring Web MVC
    implementation 'org.springframework:spring-webmvc:6.0.11'
    // Mustache Template Engine
    implementation 'org.springframework.boot:spring-boot-starter-mustache:3.2.1'
}
```

#### **Configuración**
Spring Boot y Spring MVC ya tienen soporte integrado para Mustache, por lo que no necesitas configuraciones adicionales. Las plantillas Mustache deben estar ubicadas en el directorio `src/main/resources/templates`.

#### **Ejemplo de Proyecto**

**Controlador**:
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
}
```

**Plantilla Mustache (`index.mustache`)**:
```html
<!DOCTYPE html>
<html>
<head>
    <title>{{title}}</title>
</head>
<body>
    <h1>{{title}}</h1>
    <p>{{description}}</p>
</body>
</html>
```

### **Ventajas de Usar Mustache**
- **Simplicidad**: Mustache tiene una sintaxis simple y fácil de aprender.
- **Separación de Responsabilidades**: Promueve la separación de la lógica de presentación y la lógica de negocio.
- **Compatibilidad**: Funciona bien con múltiples lenguajes y marcos, lo que lo hace muy versátil.

### **Conclusión**
Mustache es una excelente opción para generar vistas dinámicas en aplicaciones Spring MVC. Su integración es sencilla y ofrece una manera limpia y eficiente de manejar la lógica de presentación.



: [Spring MVC \u002B Mustache JS template example](https://mkyong.com/spring-mvc/spring-mvc-mustache-js-template-example/)
: [SpringMVC Mustache -- Home](https://mjeanroy.tech/springmvc-mustache/)


¡Claro! Mustache es un motor de plantillas lógico que permite insertar datos en plantillas HTML de manera sencilla y eficiente. Aquí tienes una explicación de los bloques, anotaciones y la expansión de `{}` en Mustache:

### **Bloques Mustache**

1. **Secciones (`{{#section}} ... {{/section}}`)**:
   - **Uso**: Se utilizan para iterar sobre listas o para incluir contenido condicionalmente.
   - **Ejemplo**:
     ```html
     {{#tasks}}
       <li>{{description}}</li>
     {{/tasks}}
     ```
     Si `tasks` es una lista de objetos, el contenido dentro del bloque se repetirá para cada elemento de la lista.

2. **Inversas (`{{^section}} ... {{/section}}`)**:
   - **Uso**: Se utilizan para incluir contenido si una lista está vacía o si una condición es falsa.
   - **Ejemplo**:
     ```html
     {{^tasks}}
       <p>No hay tareas.</p>
     {{/tasks}}
     ```
     Si `tasks` está vacío o es `false`, se mostrará el mensaje "No hay tareas."

### **Anotaciones**

1. **Variables (`{{variable}}`)**:
   - **Uso**: Se utilizan para insertar el valor de una variable en la plantilla.
   - **Ejemplo**:
     ```html
     <p>{{name}}</p>
     ```
     Si `name` es "Juan", el resultado será `<p>Juan</p>`.

2. **Comentarios (`{{! comentario }}`)**:
   - **Uso**: Se utilizan para agregar comentarios que no se renderizan en la salida final.
   - **Ejemplo**:
     ```html
     {{! Este es un comentario }}
     ```

3. **Partials (`{{> partial}}`)**:
   - **Uso**: Se utilizan para incluir otras plantillas dentro de una plantilla.
   - **Ejemplo**:
     ```html
     {{> header}}
     ```
     Incluirá el contenido de la plantilla `header`.

### **Expansión de `{}`**

1. **Variables sin escape (`{{{variable}}}`)**:
   - **Uso**: Se utilizan para insertar el valor de una variable sin escapar caracteres HTML.
   - **Ejemplo**:
     ```html
     {{{htmlContent}}}
     ```
     Si `htmlContent` es `<strong>Hola</strong>`, el resultado será `<strong>Hola</strong>`.

2. **Variables con escape (`{{variable}}`)**:
   - **Uso**: Se utilizan para insertar el valor de una variable escapando caracteres HTML.
   - **Ejemplo**:
     ```html
     <p>{{htmlContent}}</p>
     ```
     Si `htmlContent` es `<strong>Hola</strong>`, el resultado será `&lt;strong&gt;Hola&lt;/strong&gt;`.

### **Ejemplo Completo**

Aquí tienes un ejemplo completo que utiliza varios de estos elementos:

```html
<!DOCTYPE html>
<html>
<head>
    <title>{{title}}</title>
</head>
<body>
    <h1>{{title}}</h1>
    <p>{{description}}</p>

    {{#tasks}}
        <li>{{description}} - {{#completed}}Completada{{/completed}}{{^completed}}Pendiente{{/completed}}</li>
    {{/tasks}}

    {{^tasks}}
        <p>No hay tareas.</p>
    {{/tasks}}

    {{> footer}}
</body>
</html>
```

En este ejemplo:
- `{{title}}` y `{{description}}` insertan valores de variables.
- `{{#tasks}} ... {{/tasks}}` itera sobre una lista de tareas.
- `{{^tasks}} ... {{/tasks}}` muestra un mensaje si la lista de tareas está vacía.
- `{{> footer}}` incluye una plantilla parcial llamada `footer`.


# Más control
Además de las secciones y variables básicas, Mustache ofrece varias formas adicionales de controlar y expandir contenido en tus plantillas:

### **1. Partials (`{{> partial}}`)**
- **Uso**: Permiten incluir otras plantillas dentro de una plantilla principal.
- **Ejemplo**:
  ```html
  {{> header}}
  <div>
    <h1>{{title}}</h1>
    <p>{{content}}</p>
  </div>
  {{> footer}}
  ```
  Esto incluirá el contenido de las plantillas `header` y `footer` en la plantilla principal.

### **2. Comentarios (`{{! comentario }}`)**
- **Uso**: Permiten agregar comentarios que no se renderizan en la salida final.
- **Ejemplo**:
  ```html
  {{! Este es un comentario }}
  <p>{{name}}</p>
  ```

### **3. Variables sin escape (`{{{variable}}}`)**
- **Uso**: Permiten insertar el valor de una variable sin escapar caracteres HTML.
- **Ejemplo**:
  ```html
  {{{htmlContent}}}
  ```
  Si `htmlContent` es `<strong>Hola</strong>`, el resultado será `<strong>Hola</strong>`.

### **4. Variables con escape (`{{variable}}`)**
- **Uso**: Permiten insertar el valor de una variable escapando caracteres HTML.
- **Ejemplo**:
  ```html
  <p>{{htmlContent}}</p>
  ```
  Si `htmlContent` es `<strong>Hola</strong>`, el resultado será `&lt;strong&gt;Hola&lt;/strong&gt;`.

### **5. Secciones (`{{#section}} ... {{/section}}`)**
- **Uso**: Permiten iterar sobre listas o incluir contenido condicionalmente.
- **Ejemplo**:
  ```html
  {{#tasks}}
    <li>{{description}}</li>
  {{/tasks}}
  ```
  Si `tasks` es una lista de objetos, el contenido dentro del bloque se repetirá para cada elemento de la lista.

### **6. Inversas (`{{^section}} ... {{/section}}`)**
- **Uso**: Permiten incluir contenido si una lista está vacía o si una condición es falsa.
- **Ejemplo**:
  ```html
  {{^tasks}}
    <p>No hay tareas.</p>
  {{/tasks}}
  ```
  Si `tasks` está vacío o es `false`, se mostrará el mensaje "No hay tareas."

### **7. Bloques de Sección (`{{#section}} ... {{/section}}`)**
- **Uso**: Permiten iterar sobre listas o incluir contenido condicionalmente.
- **Ejemplo**:
  ```html
  {{#tasks}}
    <li>{{description}}</li>
  {{/tasks}}
  ```
  Si `tasks` es una lista de objetos, el contenido dentro del bloque se repetirá para cada elemento de la lista.

### **8. Bloques Inversos (`{{^section}} ... {{/section}}`)**
- **Uso**: Permiten incluir contenido si una lista está vacía o si una condición es falsa.
- **Ejemplo**:
  ```html
  {{^tasks}}
    <p>No hay tareas.</p>
  {{/tasks}}
  ```
  Si `tasks` está vacío o es `false`, se mostrará el mensaje "No hay tareas."

### **9. Variables sin escape (`{{{variable}}}`)**
- **Uso**: Permiten insertar el valor de una variable sin escapar caracteres HTML.
- **Ejemplo**:
  ```html
  {{{htmlContent}}}
  ```
  Si `htmlContent` es `<strong>Hola</strong>`, el resultado será `<strong>Hola</strong>`.

### **10. Variables con escape (`{{variable}}`)**
- **Uso**: Permiten insertar el valor de una variable escapando caracteres HTML.
- **Ejemplo**:
  ```html
  <p>{{htmlContent}}</p>
  ```
  Si `htmlContent` es `<strong>Hola</strong>`, el resultado será `&lt;strong&gt;Hola&lt;/strong&gt;`.

### **11. Partials (`{{> partial}}`)**
- **Uso**: Permiten incluir otras plantillas dentro de una plantilla principal.
- **Ejemplo**:
  ```html
  {{> header}}
  <div>
    <h1>{{title}}</h1>
    <p>{{content}}</p>
  </div>
  {{> footer}}
  ```
  Esto incluirá el contenido de las plantillas `header` y `footer` en la plantilla principal.

### **12. Comentarios (`{{! comentario }}`)**
- **Uso**: Permiten agregar comentarios que no se renderizan en la salida final.
- **Ejemplo**:
  ```html
  {{! Este es un comentario }}
  <p>{{name}}</p>
  ```

😊