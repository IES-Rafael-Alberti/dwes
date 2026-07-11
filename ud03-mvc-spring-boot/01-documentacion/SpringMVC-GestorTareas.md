# Gestión de Tareas (Tasks)
Vamos e ir explicando Spring MVC y algunos de sus motores de plantillas con un proyecto Spring MVC dedicado a la creación y seguimiento de una lista de tareas.

## Dependencias inciales
¡Perfecto! Si quieres usar **Mustache** como motor de vistas en lugar de JSP, te proporciono las dependencias necesarias y cómo configurarlo en tu proyecto **Spring MVC**. Mustache es un motor de plantillas ligero y fácil de usar que funciona muy bien con aplicaciones Spring.

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

    // Logging con SLF4J, casi que olvidamos esta parte, no he conseguido que se instale, parece que no existe o no con ese nombre (antes puede)
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
Página "home"
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

😊

¡Perfecto! Vamos a extender el ejemplo utilizando una base de datos para manejar información, y añadiremos un formulario para interactuar con el sistema. Implementaremos una aplicación sencilla que permite gestionar una lista de tareas con las siguientes funcionalidades:

1. Mostrar todas las tareas desde la base de datos.
2. Agregar una nueva tarea mediante un formulario.
3. Persistir las tareas en una base de datos usando Spring Data JPA.

---

### **Dependencias actualizadas**

#### **Con Maven**
Actualiza tu archivo `pom.xml` para incluir las dependencias necesarias:

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

    <!-- Spring Data JPA -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-jpa</artifactId>
        <version>3.2.1</version>
    </dependency>

    <!-- H2 Database (para pruebas rápidas en memoria) -->
    <dependency>
        <groupId>com.h2database</groupId>
        <artifactId>h2</artifactId>
        <version>2.2.220</version>
    </dependency>
</dependencies>
```

#### **Con Gradle**
Actualiza tu archivo `build.gradle`:

```groovy
dependencies {
    // Spring Web MVC
    implementation 'org.springframework:spring-webmvc:6.0.11'

    // Mustache Template Engine
    implementation 'org.springframework.boot:spring-boot-starter-mustache:3.2.1'

    // Spring Data JPA
    implementation 'org.springframework.boot:spring-boot-starter-data-jpa:3.2.1'

    // H2 Database
    runtimeOnly 'com.h2database:h2:2.2.220'
}
```

---

### **Configuración de base de datos**
Configura la base de datos en el archivo `application.properties` (o `application.yml`):

```properties
# Configuración H2 Database
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=
spring.jpa.hibernate.ddl-auto=update
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console
```

Esto configura una base de datos en memoria H2 y habilita una consola para inspeccionar los datos en `http://localhost:8080/h2-console`.

---

### **Estructura del proyecto**
La estructura del proyecto será la siguiente:

```
src/main/java
├── com.example.controller
│   └── TaskController.java
├── com.example.model
│   └── Task.java
├── com.example.repository
│   └── TaskRepository.java
├── com.example.service
│   └── TaskService.java
└── com.example
    └── SpringMvcApp.java
src/main/resources
└── templates
    ├── tasks.mustache
    └── add-task.mustache
```

---

### **Modelo: `Task`**
Creamos una entidad para representar las tareas en la base de datos.

```java
package com.example.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String description;

    private boolean completed;

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }
}
```

---

### **Repositorio: `TaskRepository`**
Creamos un repositorio usando Spring Data JPA.

```java
package com.example.repository;

import com.example.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task, Long> {
}
```

---

### **Servicio: `TaskService`**
Creamos una capa de servicio para manejar la lógica de negocio.

```java
package com.example.service;

import com.example.model.Task;
import com.example.repository.TaskRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskService {

    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    public void saveTask(Task task) {
        taskRepository.save(task);
    }
}
```

---

### **Controlador: `TaskController`**
Gestiona las solicitudes y renderiza las vistas.

```java
package com.example.controller;

import com.example.model.Task;
import com.example.service.TaskService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping("/tasks")
    public String listTasks(Model model) {
        model.addAttribute("tasks", taskService.getAllTasks());
        return "tasks";
    }

    @GetMapping("/tasks/new")
    public String showAddTaskForm(Model model) {
        model.addAttribute("task", new Task());
        return "add-task";
    }

    @PostMapping("/tasks")
    public String addTask(@ModelAttribute Task task) {
        taskService.saveTask(task);
        return "redirect:/tasks";
    }
}
```

---

### **Plantillas Mustache**

#### **Lista de tareas: `tasks.mustache`**
```html
<!DOCTYPE html>
<html>
<head>
    <title>Tareas</title>
</head>
<body>
    <h1>Lista de Tareas</h1>
    <ul>
        {{#tasks}}
        <li>{{description}} - {{#completed}}Completada{{/completed}}{{^completed}}Pendiente{{/completed}}</li>
        {{/tasks}}
    </ul>
    <a href="/tasks/new">Agregar Nueva Tarea</a>
</body>
</html>
```

#### **Formulario para agregar tareas: `add-task.mustache`**
```html
<!DOCTYPE html>
<html>
<head>
    <title>Agregar Tarea</title>
</head>
<body>
    <h1>Agregar Nueva Tarea</h1>
    <form action="/tasks" method="post">
        <label for="description">Descripción:</label>
        <input type="text" id="description" name="description" required>
        <br>
        <label for="completed">¿Completada?</label>
        <input type="checkbox" id="completed" name="completed" value="true">
        <br>
        <button type="submit">Guardar</button>
    </form>
    <a href="/tasks">Volver a la lista de tareas</a>
</body>
</html>
```

---

### **Pruebas**

1. **Inicia el servidor**: Ejecuta la aplicación y accede a `http://localhost:8080/tasks`.
2. **Agrega tareas**: Usa el formulario en `http://localhost:8080/tasks/new`.
3. **Consulta la base de datos**: Accede a la consola de H2 en `http://localhost:8080/h2-console` con las credenciales configuradas (`sa` como usuario).

---

Con esto, tienes un sistema funcional que utiliza Spring MVC, Mustache, y Spring Data JPA para gestionar tareas. 🚀 

En **Spring MVC**, la clase **`Model`** es una parte esencial del flujo de datos entre el controlador y la vista. Se utiliza para pasar datos desde el controlador a la vista. Es una interfaz que forma parte del paquete **`org.springframework.ui`**.

Aquí tienes una explicación completa de la clase **`Model`**:

---

### **¿Qué es la clase Model?**

La clase **`Model`** en Spring MVC actúa como un contenedor para los datos que deben ser enviados desde el controlador a la vista. El objetivo principal de esta clase es encapsular y organizar los datos para que las vistas puedan acceder a ellos fácilmente.

- **Ubicación**: La interfaz `Model` está en el paquete `org.springframework.ui`.
- **Propósito**: Almacenar pares clave-valor donde:
  - **Clave**: El nombre del atributo (String).
  - **Valor**: El objeto (cualquier tipo de datos) que se enviará a la vista.

---

### **Métodos clave en Model**

La interfaz **`Model`** tiene métodos que permiten agregar atributos o manipular los datos. Los más comunes son:

1. **`addAttribute(String attributeName, Object attributeValue)`**  
   - Agrega un atributo al modelo con una clave específica.
   - Ejemplo:
     ```java
     model.addAttribute("name", "Juan");
     ```

2. **`addAttribute(Object attributeValue)`**  
   - Agrega un atributo al modelo sin especificar un nombre. Spring asignará automáticamente un nombre al atributo basado en el nombre de la clase.
   - Ejemplo:
     ```java
     model.addAttribute(new User("Juan"));
     ```
     En este caso, si el objeto es de tipo `User`, el nombre será `"user"`.

3. **`addAllAttributes(Map<String, ?> attributes)`**  
   - Permite agregar varios atributos al modelo usando un `Map`.
   - Ejemplo:
     ```java
     Map<String, Object> data = new HashMap<>();
     data.put("name", "Juan");
     data.put("age", 30);
     model.addAllAttributes(data);
     ```

4. **`containsAttribute(String attributeName)`**  
   - Verifica si el modelo contiene un atributo con un nombre específico.
   - Ejemplo:
     ```java
     if (model.containsAttribute("name")) {
         System.out.println("El atributo 'name' está presente.");
     }
     ```

---

### **Cómo se utiliza Model en un controlador**

La clase `Model` es frecuentemente utilizada como un parámetro en los métodos del controlador para preparar los datos que serán enviados a las vistas. Aquí tienes un ejemplo práctico:

```java
@Controller
public class HomeController {

    @GetMapping("/greeting")
    public String greeting(Model model) {
        // Agregar datos al modelo
        model.addAttribute("message", "¡Hola, Mundo!");
        model.addAttribute("name", "Juan");

        // Retornar el nombre de la vista (greeting.mustache)
        return "greeting";
    }
}
```

En este ejemplo:

1. El controlador agrega dos atributos al modelo: `message` y `name`.
2. Estos datos estarán disponibles en la plantilla `greeting.mustache` (o cualquier motor de vistas configurado).

---

### **Cómo acceden las vistas al Modelo**

Las vistas acceden a los atributos del modelo utilizando la clave con la que se agregaron. El acceso varía dependiendo del motor de plantillas utilizado. Por ejemplo:

#### **En Mustache**
```html
<!DOCTYPE html>
<html>
<head>
    <title>Saludo</title>
</head>
<body>
    <h1>{{message}}</h1>
    <p>Nombre: {{name}}</p>
</body>
</html>
```

#### **En Thymeleaf**
```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <title>Saludo</title>
</head>
<body>
    <h1 th:text="${message}"></h1>
    <p>Nombre: <span th:text="${name}"></span></p>
</body>
</html>
```

---

### **Diferencia entre Model, ModelMap y ModelAndView**

Spring MVC ofrece varias formas de pasar datos a las vistas, y las tres opciones principales son:

1. **`Model`**:
   - Una interfaz que se utiliza para agregar datos al modelo.
   - Es el enfoque más común y sencillo.

   Ejemplo:
   ```java
   model.addAttribute("key", "value");
   ```

2. **`ModelMap`**:
   - Es una implementación de `Map` que funciona igual que `Model`, pero permite un manejo más directo de los datos.
   - Ejemplo:
     ```java
     @GetMapping("/example")
     public String example(ModelMap modelMap) {
         modelMap.addAttribute("key", "value");
         return "view";
     }
     ```

3. **`ModelAndView`**:
   - Combina el modelo y la vista en un solo objeto.
   - Se usa para controlar tanto los datos como la vista desde un método.
   - Ejemplo:
     ```java
     @GetMapping("/example")
     public ModelAndView example() {
         ModelAndView mav = new ModelAndView("view");
         mav.addObject("key", "value");
         return mav;
     }
     ```

---

### **Cuándo usar Model**
- Usar `Model` es ideal cuando quieres mantener simple tu código y separar la lógica de negocio (controlador) de la presentación (vista).
- Utilízalo cuando solo necesitas pasar datos a una vista específica sin preocuparte por configuraciones adicionales.

---

### **Conclusión**
La clase `Model` en Spring MVC es una herramienta sencilla y poderosa para preparar y enviar datos desde un controlador a la vista. Se integra perfectamente con los motores de plantillas como Mustache, Thymeleaf, o incluso JSON en respuestas REST. Es una parte esencial de Spring MVC para implementar la separación de preocupaciones. 😊

## Explicación de la vista Mustache de lista de tareas

```html
<!DOCTYPE html>
<html>
<head>
    <title>Tareas</title>
</head>
<body>
    <h1>Lista de Tareas</h1>
    <ul>
        {{#tasks}}
        <li>{{description}} - {{#completed}}Completada{{/completed}}{{^completed}}Pendiente{{/completed}}</li>
        {{/tasks}}
    </ul>
    <a href="/tasks/new">Agregar Nueva Tarea</a>
</body>
</html>
```

¡Claro! Analicemos paso a paso el bloque de código Mustache que has señalado:

---

### **Contexto**
Este fragmento genera una lista de tareas en HTML utilizando **Mustache** como motor de plantillas. Mustache es un motor de plantillas lógico que permite iterar sobre listas de objetos y renderizar su contenido dinámicamente en HTML o cualquier otro formato.

---

### **Estructura del código**

#### **Título**
```html
<h1>Lista de Tareas</h1>
```
- Genera un encabezado de nivel 1 (H1) con el texto "Lista de Tareas".
- Esto es estático, por lo que aparece igual en todas las páginas.

#### **Lista no ordenada**
```html
<ul>
```
- Inicia una lista no ordenada (bullets) en HTML. Todo el contenido generado dinámicamente estará dentro de esta lista.

#### **Iteración sobre las tareas**
```html
{{#tasks}}
<li>{{description}} - {{#completed}}Completada{{/completed}}{{^completed}}Pendiente{{/completed}}</li>
{{/tasks}}
```

Aquí sucede la magia de Mustache. Vamos paso a paso:

---

### **Explicación de los bloques Mustache**

1. **`{{#tasks}}` y `{{/tasks}}`**
   - Este es un **bloque de sección** en Mustache.
   - `{{#tasks}}` indica que debe iterar sobre el objeto o lista llamado `tasks`.
   - `{{/tasks}}` cierra el bloque de iteración.

   **Qué hace aquí**:
   - `tasks` es una lista de objetos que se pasa desde el controlador al modelo.
   - Mustache itera sobre esta lista y repite el contenido del bloque `<li>` por cada elemento en la lista.

   **Ejemplo de datos de entrada (Modelo):**
   ```json
   {
       "tasks": [
           { "description": "Comprar leche", "completed": true },
           { "description": "Hacer ejercicio", "completed": false }
       ]
   }
   ```

2. **`<li>{{description}} - ...</li>`**
   - Renderiza cada elemento de la lista como un elemento `<li>` en HTML.
   - `{{description}}`:
     - Hace referencia al campo `description` de cada objeto en la lista `tasks`.
     - Inserta dinámicamente el texto de la descripción en este lugar.

   **Salida renderizada:**
   - Si `description` es `"Comprar leche"`, el resultado será:
     ```html
     <li>Comprar leche - ...</li>
     ```

3. **`{{#completed}}Completada{{/completed}}`**
   - Este es otro **bloque condicional**.
   - `{{#completed}}`:
     - Evalúa si el campo `completed` del objeto actual es `true`.
     - Si lo es, renderiza el contenido dentro del bloque (en este caso, la palabra "Completada").

   **Ejemplo:**
   - Si `completed` es `true`, el resultado será:
     ```html
     Completada
     ```

4. **`{{^completed}}Pendiente{{/completed}}`**
   - Este es un **bloque inverso**.
   - `{{^completed}}`:
     - Evalúa si el campo `completed` del objeto actual es `false`.
     - Si lo es, renderiza el contenido dentro del bloque (en este caso, la palabra "Pendiente").

   **Ejemplo:**
   - Si `completed` es `false`, el resultado será:
     ```html
     Pendiente
     ```

---

### **Combinación completa**
El contenido final de cada tarea (`<li>`) dependerá de los valores de `description` y `completed`.

#### **Con los datos del ejemplo:**
```json
{
    "tasks": [
        { "description": "Comprar leche", "completed": true },
        { "description": "Hacer ejercicio", "completed": false }
    ]
}
```

#### **Renderización resultante:**
```html
<h1>Lista de Tareas</h1>
<ul>
    <li>Comprar leche - Completada</li>
    <li>Hacer ejercicio - Pendiente</li>
</ul>
```

---

### **Resumen de Mustache usado en este código**

- **`{{#var}}...{{/var}}`**: Bloque de sección que itera o muestra contenido si `var` es un objeto no nulo o una lista.
- **`{{^var}}...{{/var}}`**: Bloque inverso, que muestra contenido si `var` es nulo o `false`.
- **`{{var}}`**: Inserta el valor del campo `var` directamente en el contenido.

---

### **Flujo entre Spring MVC y Mustache**
1. **Controlador:**
   Los datos de las tareas (`tasks`) son enviados desde el controlador al modelo:
   ```java
   @GetMapping("/tasks")
   public String listTasks(Model model) {
       List<Task> tasks = taskService.getAllTasks();
       model.addAttribute("tasks", tasks);
       return "tasks";
   }
   ```
   Aquí, `tasks` es una lista de objetos `Task` que contiene `description` y `completed`.

2. **Vista:**
   La plantilla Mustache usa `tasks` para renderizar dinámicamente la lista de tareas según su estado (`completed` o no).

---

### **Ventajas de este enfoque**

1. **Simplicidad**:
   Mustache no incluye lógica compleja en las plantillas, lo que ayuda a mantener una separación estricta entre la lógica del controlador y la presentación.

2. **Legibilidad**:
   - Las plantillas son claras y fáciles de leer.
   - No hay bloques anidados ni estructuras complejas.

3. **Compatibilidad**:
   Mustache es compatible con múltiples lenguajes y marcos, lo que lo hace portátil.

---

## Modificar una tarea
Para añadir la funcionalidad de modificar una tarea y cambiar su estado de "Pendiente" a "Completada" directamente en la vista, debes hacer lo siguiente:

---

### **Actualización general**

1. **Añadir un botón de acción en la vista para cambiar el estado de la tarea.**
2. **Agregar un endpoint en el controlador para manejar la solicitud de actualización.**
3. **Actualizar la lógica de servicio para cambiar el estado en la base de datos.**

---

### **Vista: Actualización de tareas**

Modifica la plantilla **`tasks.mustache`** para incluir un botón que permita cambiar el estado de cada tarea. El botón enviará una solicitud `POST` al servidor para actualizar el estado.

```html
<!DOCTYPE html>
<html>
<head>
    <title>Lista de Tareas</title>
</head>
<body>
    <h1>Lista de Tareas</h1>
    <ul>
        {{#tasks}}
        <li>
            {{description}} - 
            {{#completed}}
                Completada
            {{/completed}}
            {{^completed}}
                Pendiente
                <form action="/tasks/{{id}}/complete" method="post" style="display:inline;">
                    <button type="submit">Marcar como Completada</button>
                </form>
            {{/completed}}
        </li>
        {{/tasks}}
    </ul>
    <a href="/tasks/new">Agregar Nueva Tarea</a>
</body>
</html>
```

#### **¿Qué hace este código?**
1. Cada tarea muestra su descripción y estado.
2. Si la tarea está **pendiente**, aparece un formulario con un botón que envía una solicitud `POST` a `/tasks/{id}/complete`, donde `{id}` es el ID de la tarea.
3. Si la tarea está **completada**, simplemente muestra "Completada" sin un botón.

---

### **Controlador: Endpoint para actualizar el estado**

Agrega un nuevo método en el controlador para manejar la solicitud de completar una tarea:

```java
package com.example.controller;

import com.example.service.TaskService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    // Otros métodos...

    @PostMapping("/tasks/{id}/complete")
    public String markTaskAsComplete(@PathVariable Long id) {
        taskService.markAsComplete(id);
        return "redirect:/tasks";
    }
}
```

#### **Explicación:**
1. **`@PostMapping("/tasks/{id}/complete")`:**
   - Este endpoint recibe el ID de la tarea como parámetro en la URL.
   - Llama al servicio para actualizar el estado de la tarea.
2. **`taskService.markAsComplete(id)`**:
   - Este método cambia el estado de la tarea en la base de datos.
3. **`return "redirect:/tasks"`**:
   - Después de actualizar la tarea, redirige a la lista de tareas actualizada.

---

### **Servicio: Lógica para completar la tarea**

Agrega un método en el servicio para actualizar el estado de la tarea:

```java
package com.example.service;

import com.example.model.Task;
import com.example.repository.TaskRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class TaskService {

    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    // Otros métodos...

    public void markAsComplete(Long id) {
        Optional<Task> optionalTask = taskRepository.findById(id);
        if (optionalTask.isPresent()) {
            Task task = optionalTask.get();
            task.setCompleted(true); // Cambia el estado a completada
            taskRepository.save(task); // Guarda los cambios
        }
    }
}
```

#### **Explicación:**
1. **`taskRepository.findById(id)`**:
   - Busca la tarea en la base de datos por su ID.
2. **`task.setCompleted(true)`**:
   - Cambia el estado de la tarea a "Completada".
3. **`taskRepository.save(task)`**:
   - Guarda los cambios en la base de datos.

---

### **Prueba de la funcionalidad**

1. Inicia la aplicación.
2. Accede a `http://localhost:8080/tasks`.
3. Para una tarea pendiente, haz clic en el botón **"Marcar como Completada"**.
4. Verifica que la tarea aparece como **"Completada"** después de actualizar la página.

---

### **Mejoras posibles**
1. **Evitar duplicación de botones:**
   Si deseas que las tareas completadas puedan volver a estado "Pendiente", puedes agregar un botón para revertir el estado, utilizando un endpoint similar.

2. **Uso de peticiones AJAX:**
   Puedes usar JavaScript para enviar la solicitud sin recargar toda la página.

Perfecto, si ya tienes implementada la lógica de **"Marcar como Pendiente"**, vamos a modificar el **template Mustache** para que ambos botones (marcar como **"Completada"** y **"Pendiente"**) se ubiquen correctamente.

---

### **Objetivo**
- Mostrar **"Marcar como Completada"** solo si la tarea está pendiente.
- Mostrar **"Marcar como Pendiente"** solo si la tarea ya está completada.

---

### **Template Mustache actualizado**

Modifiquemos la lista de tareas en **`tasks.mustache`**:

```html
<!DOCTYPE html>
<html>
<head>
    <title>Lista de Tareas</title>
</head>
<body>
    <h1>Lista de Tareas</h1>
    <ul>
        {{#tasks}}
        <li>
            {{description}} - 
            {{#completed}}
                Completada
                <!-- Botón para marcar como pendiente -->
                <form action="/tasks/{{id}}/pending" method="post" style="display:inline;">
                    <button type="submit">Marcar como Pendiente</button>
                </form>
            {{/completed}}
            {{^completed}}
                Pendiente
                <!-- Botón para marcar como completada -->
                <form action="/tasks/{{id}}/complete" method="post" style="display:inline;">
                    <button type="submit">Marcar como Completada</button>
                </form>
            {{/completed}}
        </li>
        {{/tasks}}
    </ul>
    <a href="/tasks/new">Agregar Nueva Tarea</a>
</body>
</html>
```

---

### **Explicación del código**

1. **`{{#completed}}` y `{{^completed}}`**:
   - `{{#completed}}`: Se ejecuta si el atributo `completed` es `true` (la tarea está completada).
     - Muestra el texto "Completada" y el botón **"Marcar como Pendiente"**.
   - `{{^completed}}`: Se ejecuta si el atributo `completed` es `false` (la tarea está pendiente).
     - Muestra el texto "Pendiente" y el botón **"Marcar como Completada"**.

2. **Formularios con `POST`**:
   - Cada botón envía una solicitud **POST** al servidor.
   - Los endpoints son `/tasks/{id}/complete` y `/tasks/{id}/pending` respectivamente.

3. **`style="display:inline;"`**:
   - Permite que el botón aparezca en la misma línea que el texto "Completada" o "Pendiente".

---

### **Resultado esperado en la vista**

Supongamos que tienes las siguientes tareas:

| Descripción           | Estado       |
|-----------------------|--------------|
| Comprar leche         | Pendiente    |
| Hacer ejercicio       | Completada   |

La salida en el navegador será:

```plaintext
Lista de Tareas
- Comprar leche - Pendiente [Marcar como Completada]
- Hacer ejercicio - Completada [Marcar como Pendiente]
```

Los botones se colocarán correctamente junto a cada estado.

---

### **Flujo completo de lógica**

Si todavía no lo has hecho, asegúrate de tener los siguientes endpoints en tu **controlador**:

```java
@PostMapping("/tasks/{id}/complete")
public String markAsComplete(@PathVariable Long id) {
    taskService.markAsComplete(id);
    return "redirect:/tasks";
}

@PostMapping("/tasks/{id}/pending")
public String markAsPending(@PathVariable Long id) {
    taskService.markAsPending(id);
    return "redirect:/tasks";
}
```

Y en el **servicio**:

```java
public void markAsPending(Long id) {
    Optional<Task> optionalTask = taskRepository.findById(id);
    if (optionalTask.isPresent()) {
        Task task = optionalTask.get();
        task.setCompleted(false);
        taskRepository.save(task);
    }
}
```

---

### **Conclusión**
Con este ajuste:

- Los botones **"Marcar como Completada"** y **"Marcar como Pendiente"** se colocan correctamente.
- Se renderizan dinámicamente según el estado de cada tarea.

## Borrar tarea

 Vamos a añadir la funcionalidad para **borrar una tarea** en nuestra aplicación. Esto incluirá:

1. **Agregar un botón "Eliminar"** en la vista junto a cada tarea.
2. **Crear un endpoint en el controlador** para manejar la eliminación.
3. **Actualizar la lógica de servicio** para eliminar la tarea de la base de datos.

---

### **Actualización del template Mustache**

Agregamos un botón "Eliminar" que envía una solicitud `POST` (o `DELETE`, si usas AJAX) para borrar una tarea específica. Aquí está la versión actualizada de **`tasks.mustache`**:

```html
<!DOCTYPE html>
<html>
<head>
    <title>Lista de Tareas</title>
</head>
<body>
    <h1>Lista de Tareas</h1>
    <ul>
        {{#tasks}}
        <li>
            {{description}} - 
            <!-- Botón para alternar estado -->
            <form action="/tasks/{{id}}/toggle" method="post" style="display:inline;">
                <button type="submit">
                    {{#completed}}Marcar como Pendiente{{/completed}}
                    {{^completed}}Marcar como Completada{{/completed}}
                </button>
            </form>

            <!-- Botón para eliminar tarea -->
            <form action="/tasks/{{id}}/delete" method="post" style="display:inline;">
                <button type="submit" style="color:red;">Eliminar</button>
            </form>
        </li>
        {{/tasks}}
    </ul>
    <a href="/tasks/new">Agregar Nueva Tarea</a>
</body>
</html>
```

---

### **Endpoint en el controlador**

Agregamos un nuevo método en el **controlador** para manejar la eliminación de una tarea:

```java
@PostMapping("/tasks/{id}/delete")
public String deleteTask(@PathVariable Long id) {
    taskService.deleteTask(id);
    return "redirect:/tasks";
}
```

- **`@PostMapping("/tasks/{id}/delete")`**: Recibe la solicitud `POST` para borrar la tarea con el ID dado.
- **`taskService.deleteTask(id)`**: Llama al servicio para eliminar la tarea.
- **`redirect:/tasks`**: Redirige a la lista de tareas actualizada.

---

### **Lógica en el servicio**

Implementamos la lógica para borrar una tarea en la base de datos:

```java
public void deleteTask(Long id) {
    taskRepository.deleteById(id);
}
```

Aquí:

- **`taskRepository.deleteById(id)`**: Usa el método proporcionado por **Spring Data JPA** para eliminar la tarea según su ID.

---

### **Resumen del flujo**
1. El usuario hace clic en el botón **"Eliminar"** junto a una tarea.
2. Se envía una solicitud `POST` al endpoint `/tasks/{id}/delete`.
3. El controlador llama al servicio para eliminar la tarea.
4. La tarea es eliminada de la base de datos.
5. El navegador redirige automáticamente a la página de la lista de tareas actualizada.

---

### **Resultado esperado**

Con los siguientes datos:

| Descripción           | Estado       |
|-----------------------|--------------|
| Comprar leche         | Pendiente    |
| Hacer ejercicio       | Completada   |

La salida en la página será:

```plaintext
Lista de Tareas
- Comprar leche - [Marcar como Completada] [Eliminar]
- Hacer ejercicio - [Marcar como Pendiente] [Eliminar]
```

Cuando el usuario hace clic en "Eliminar", la tarea se borra y la lista se actualiza sin mostrar esa tarea.

---


## Confirmación de borrado

¡Perfecto! Vamos a agregar una **confirmación de borrado** antes de eliminar una tarea. Esto mejorará la experiencia del usuario y evitará eliminaciones accidentales.

Utilizaremos **JavaScript** para mostrar una alerta de confirmación cuando el usuario haga clic en el botón "Eliminar". Solo si el usuario confirma, se enviará la solicitud al servidor.

---

### **Actualización del template Mustache**

Modificaremos el template para incluir la confirmación usando el evento **`onclick`** en JavaScript:

```html
<!DOCTYPE html>
<html>
<head>
    <title>Lista de Tareas</title>
    <script>
        // Función para confirmar borrado
        function confirmDelete(taskId) {
            const confirmed = confirm("¿Estás seguro de que deseas eliminar esta tarea?");
            if (confirmed) {
                // Si el usuario confirma, enviar el formulario
                document.getElementById("delete-form-" + taskId).submit();
            }
        }
    </script>
</head>
<body>
    <h1>Lista de Tareas</h1>
    <ul>
        {{#tasks}}
        <li>
            {{description}} - 
            <!-- Botón para alternar estado -->
            <form action="/tasks/{{id}}/toggle" method="post" style="display:inline;">
                <button type="submit">
                    {{#completed}}Marcar como Pendiente{{/completed}}
                    {{^completed}}Marcar como Completada{{/completed}}
                </button>
            </form>

            <!-- Botón para eliminar tarea con confirmación -->
            <form id="delete-form-{{id}}" action="/tasks/{{id}}/delete" method="post" style="display:inline;">
                <button type="button" onclick="confirmDelete({{id}})" style="color:red;">
                    Eliminar
                </button>
            </form>
        </li>
        {{/tasks}}
    </ul>
    <a href="/tasks/new">Agregar Nueva Tarea</a>
</body>
</html>
```

---

### **Explicación del código**

1. **JavaScript para confirmación**:
   - La función **`confirmDelete(taskId)`** muestra un cuadro de confirmación con el mensaje:
     ```
     ¿Estás seguro de que deseas eliminar esta tarea?
     ```
   - Si el usuario hace clic en **"Aceptar"**, se envía el formulario correspondiente al ID de la tarea usando `submit()`.

2. **Botón "Eliminar"**:
   - El botón **no envía el formulario directamente**. Su tipo es `button`, y el evento `onclick` llama a `confirmDelete(taskId)`.

3. **Formulario con `id` único**:
   - Cada formulario tiene un identificador único basado en el ID de la tarea: `delete-form-{{id}}`.
   - Esto permite que JavaScript seleccione y envíe el formulario correcto.

---

### **Comportamiento en el navegador**

1. El usuario hace clic en **"Eliminar"**.
2. Aparece un cuadro de confirmación con el mensaje:
   ```
   ¿Estás seguro de que deseas eliminar esta tarea?
   ```
3. Si el usuario selecciona **"Aceptar"**, el formulario correspondiente se envía al servidor.
4. Si el usuario selecciona **"Cancelar"**, no sucede nada.

---

### **Ventajas de este enfoque**
- **No recarga la página innecesariamente**: Solo se envía el formulario si el usuario confirma.
- **Protección contra eliminaciones accidentales**.
- **JavaScript sencillo y efectivo**: Compatible con todos los navegadores modernos.

---

### **¿Quieres mejorar aún más?**
Podemos reemplazar el cuadro de confirmación predeterminado (`confirm`) con una librería más estilizada como **SweetAlert2** para una experiencia más amigable.

Por ejemplo, usando SweetAlert2:
```html
<script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
<script>
    function confirmDelete(taskId) {
        Swal.fire({
            title: "¿Estás seguro?",
            text: "¡No podrás revertir esto!",
            icon: "warning",
            showCancelButton: true,
            confirmButtonColor: "#3085d6",
            cancelButtonColor: "#d33",
            confirmButtonText: "Sí, eliminarlo"
        }).then((result) => {
            if (result.isConfirmed) {
                document.getElementById("delete-form-" + taskId).submit();
            }
        });
    }
</script>
```

Esto mostrará un cuadro de confirmación con un diseño mucho más atractivo.

Opción: implementar esta versión con **SweetAlert2** 🚀😊

## Corrección al punto anterior
Si el formulario se envía **tanto si dices "Sí" como si seleccionas "Cancelar"**, el problema seguramente radica en cómo se está llamando a la función `submit()` o en la configuración del botón.

Vamos a revisar y corregir el comportamiento para que el formulario **solo se envíe** si el usuario confirma.

---

### **Corrección para el botón estándar**

Primero, asegúrate de que el evento `onclick` solo ejecute la acción de enviar el formulario si el usuario confirma.

#### **Código corregido del template Mustache**

```html
<!DOCTYPE html>
<html>
<head>
    <title>Lista de Tareas</title>
    <script>
        // Función para confirmar borrado
        function confirmDelete(taskId) {
            const confirmed = confirm("¿Estás seguro de que deseas eliminar esta tarea?");
            if (confirmed) {
                // Si el usuario confirma, envía el formulario
                document.getElementById("delete-form-" + taskId).submit();
            }
        }
    </script>
</head>
<body>
    <h1>Lista de Tareas</h1>
    <ul>
        {{#tasks}}
        <li>
            {{description}} - 
            <!-- Botón para alternar estado -->
            <form action="/tasks/{{id}}/toggle" method="post" style="display:inline;">
                <button type="submit">
                    {{#completed}}Marcar como Pendiente{{/completed}}
                    {{^completed}}Marcar como Completada{{/completed}}
                </button>
            </form>

            <!-- Botón para eliminar tarea con confirmación -->
            <form id="delete-form-{{id}}" action="/tasks/{{id}}/delete" method="post" style="display:inline;">
                <button type="button" onclick="confirmDelete({{id}})" style="color:red;">
                    Eliminar
                </button>
            </form>
        </li>
        {{/tasks}}
    </ul>
    <a href="/tasks/new">Agregar Nueva Tarea</a>
</body>
</html>
```

---

### **Detalles importantes**
1. El **tipo del botón** es `type="button"`. Esto es esencial porque evita que el formulario se envíe automáticamente al hacer clic en el botón.
2. La función **`confirmDelete(taskId)`** solo llama a `submit()` si el usuario selecciona "Aceptar" en el cuadro de confirmación.

---

### **Posible problema anterior**
Si el botón tenía un `type="submit"`, el formulario se enviaba automáticamente incluso antes de que JavaScript decidiera qué hacer. Al cambiar el botón a **`type="button"`**, evitamos este comportamiento.

---

### **Confirmación avanzada con SweetAlert2 (opcional)**

Si deseas un cuadro de confirmación más amigable y con mejor apariencia, aquí te dejo el código usando **SweetAlert2**:

#### **Actualización de la función de confirmación**

```html
<script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
<script>
    function confirmDelete(taskId) {
        Swal.fire({
            title: "¿Estás seguro?",
            text: "¡No podrás revertir esto!",
            icon: "warning",
            showCancelButton: true,
            confirmButtonColor: "#3085d6",
            cancelButtonColor: "#d33",
            confirmButtonText: "Sí, eliminar",
            cancelButtonText: "Cancelar"
        }).then((result) => {
            if (result.isConfirmed) {
                document.getElementById("delete-form-" + taskId).submit();
            }
        });
    }
</script>
```

---

### **Comprobación final**
1. Al hacer clic en "Eliminar", debe aparecer un cuadro de confirmación.
2. Si seleccionas **"Cancelar"**, el formulario **no se envía**.
3. Si seleccionas **"Sí"** (o "Aceptar"), el formulario se envía y la tarea se elimina.

## Borrar con checkboxes
¡No es complicado! 🚀 Podemos implementar la funcionalidad de **borrar múltiples tareas seleccionadas** mediante **checkboxes** junto a cada tarea y un botón general "Eliminar Seleccionadas".

---

## **Pasos para implementar la funcionalidad**

1. **Agregar checkboxes** a cada tarea en la vista.
2. **Crear un formulario principal** que agrupe las tareas seleccionadas.
3. **Enviar los IDs de las tareas seleccionadas** al servidor.
4. **Crear un endpoint en el controlador** para procesar la eliminación de múltiples tareas.
5. **Actualizar la lógica del servicio** para eliminar las tareas usando una lista de IDs.

---

## **1. Vista Mustache actualizada**

Actualizamos el template `tasks.mustache` para incluir checkboxes y un formulario general que agrupe las tareas seleccionadas.

```html
<!DOCTYPE html>
<html>
<head>
    <title>Lista de Tareas</title>
</head>
<body>
    <h1>Lista de Tareas</h1>

    <!-- Formulario general para borrar seleccionadas -->
    <form id="delete-selected-form" action="/tasks/delete-selected" method="post">
        <ul>
            {{#tasks}}
            <li>
                <input type="checkbox" name="taskIds" value="{{id}}"> <!-- Checkbox -->
                {{description}} - 
                {{#completed}}Completada{{/completed}}
                {{^completed}}Pendiente{{/completed}}
            </li>
            {{/tasks}}
        </ul>
        <button type="submit" style="color:red;">Eliminar Seleccionadas</button>
    </form>

    <a href="/tasks/new">Agregar Nueva Tarea</a>
</body>
</html>
```

---

### **Explicación del código**

1. **Checkboxes**:
   - Cada tarea tiene un `<input>` con `type="checkbox"`, el atributo `name="taskIds"`, y su **valor** es el ID de la tarea.

2. **Formulario general**:
   - El formulario tiene un botón **"Eliminar Seleccionadas"**.
   - Al hacer clic en el botón, todos los **checkboxes seleccionados** se envían al servidor como una lista de IDs.

3. **Clave importante**:
   - Al usar `name="taskIds"`, Spring recibirá automáticamente los valores como una lista de IDs.

---

## **2. Endpoint en el controlador**

Creamos un endpoint en el controlador para recibir y procesar los IDs de las tareas seleccionadas.

```java
@PostMapping("/tasks/delete-selected")
public String deleteSelectedTasks(@RequestParam List<Long> taskIds) {
    taskService.deleteTasksByIds(taskIds);
    return "redirect:/tasks";
}
```

### **Explicación del endpoint**
- **`@RequestParam List<Long> taskIds`**: Spring mapea automáticamente los valores seleccionados de `taskIds` (enviados desde los checkboxes) en una lista de IDs.
- **`taskService.deleteTasksByIds(taskIds)`**: Llama al servicio para eliminar todas las tareas correspondientes a los IDs recibidos.

---

## **3. Lógica en el servicio**

Añadimos un método al servicio para eliminar múltiples tareas en una sola operación.

```java
public void deleteTasksByIds(List<Long> taskIds) {
    taskRepository.deleteAllById(taskIds);
}
```

### **Explicación del código**
- **`deleteAllById(taskIds)`**: Es un método proporcionado por **Spring Data JPA** que elimina todas las entidades cuyo ID coincida con los IDs en la lista.

---

## **Flujo de la funcionalidad**

1. El usuario selecciona las tareas que quiere eliminar marcando las **checkboxes**.
2. Al hacer clic en el botón **"Eliminar Seleccionadas"**, el formulario envía los IDs seleccionados al servidor.
3. El controlador recibe la lista de IDs y llama al servicio para eliminar las tareas.
4. Las tareas se eliminan de la base de datos y la página se redirige a la lista actualizada.

---

## **Resultado final en la interfaz**

### **Antes de seleccionar:**
```
Lista de Tareas
- [ ] Comprar leche - Pendiente
- [ ] Hacer ejercicio - Completada
- [ ] Leer un libro - Pendiente

[Eliminar Seleccionadas]
```

### **Después de seleccionar:**
```
Lista de Tareas
- [x] Comprar leche - Pendiente
- [ ] Hacer ejercicio - Completada
- [x] Leer un libro - Pendiente

[Eliminar Seleccionadas]
```

Al hacer clic en **"Eliminar Seleccionadas"**, las tareas **"Comprar leche"** y **"Leer un libro"** se eliminan.

---

## **¿Quieres mejorarlo aún más?**
Podemos añadir:
1. **Confirmación previa** al borrado (con un cuadro de confirmación usando JavaScript).
2. **Uso de AJAX** para realizar la eliminación sin recargar la página.
3. **Seleccionar/Deseleccionar todas las tareas** con un checkbox general.


## Confirmación previa
¡Perfecto! Vamos a añadir una **confirmación previa** al borrado de tareas seleccionadas utilizando **JavaScript**. Mostraremos un cuadro de confirmación antes de enviar el formulario. Si el usuario confirma, se enviará el formulario; de lo contrario, no pasará nada.

---

## **Actualización del Template Mustache**

Modificaremos la plantilla **`tasks.mustache`** para incluir la lógica de confirmación en JavaScript.

```html
<!DOCTYPE html>
<html>
<head>
    <title>Lista de Tareas</title>
    <script>
        // Función para confirmar el borrado de tareas seleccionadas
        function confirmDeleteSelected() {
            const checkboxes = document.querySelectorAll('input[name="taskIds"]:checked');
            if (checkboxes.length === 0) {
                alert("Por favor, selecciona al menos una tarea para eliminar.");
                return false;
            }
            
            const confirmed = confirm("¿Estás seguro de que deseas eliminar las tareas seleccionadas?");
            if (confirmed) {
                document.getElementById("delete-selected-form").submit();
            }
        }
    </script>
</head>
<body>
    <h1>Lista de Tareas</h1>

    <!-- Formulario general para borrar seleccionadas -->
    <form id="delete-selected-form" action="/tasks/delete-selected" method="post">
        <ul>
            {{#tasks}}
            <li>
                <input type="checkbox" name="taskIds" value="{{id}}"> <!-- Checkbox -->
                {{description}} - 
                {{#completed}}Completada{{/completed}}
                {{^completed}}Pendiente{{/completed}}
            </li>
            {{/tasks}}
        </ul>
        <button type="button" onclick="confirmDeleteSelected()" style="color:red;">Eliminar Seleccionadas</button>
    </form>

    <a href="/tasks/new">Agregar Nueva Tarea</a>
</body>
</html>
```

---

## **Cambios realizados**
1. **JavaScript para confirmar el borrado**:
   - `confirmDeleteSelected()` verifica si al menos un checkbox está marcado.
   - Muestra un cuadro de confirmación con el mensaje:
     ```plaintext
     ¿Estás seguro de que deseas eliminar las tareas seleccionadas?
     ```
   - Si el usuario confirma, se envía el formulario con **`submit()`**.
   - Si el usuario cancela, no sucede nada.

2. **Botón tipo `button`**:
   - Cambiamos el tipo del botón a `type="button"` para evitar el envío automático del formulario.

3. **Validación de checkboxes vacíos**:
   - Si no hay tareas seleccionadas, muestra un mensaje de alerta.

---

## **Comportamiento esperado**
1. El usuario selecciona las tareas marcando las checkboxes.
2. Al hacer clic en **"Eliminar Seleccionadas"**:
   - Si **no hay tareas seleccionadas**, aparece una alerta:  
     ```plaintext
     Por favor, selecciona al menos una tarea para eliminar.
     ```
   - Si hay tareas seleccionadas, aparece un cuadro de confirmación:
     ```plaintext
     ¿Estás seguro de que deseas eliminar las tareas seleccionadas?
     ```
3. Si el usuario selecciona **"Aceptar"**, el formulario se envía.
4. Si el usuario selecciona **"Cancelar"**, no se envía el formulario.

---

## **Prueba el flujo**

### **Ejemplo en la página:**
```
Lista de Tareas
- [x] Comprar leche - Pendiente
- [ ] Hacer ejercicio - Completada
- [x] Leer un libro - Pendiente

[Eliminar Seleccionadas]
```

### **Resultado esperado:**
1. Selecciona "Comprar leche" y "Leer un libro".
2. Haz clic en **"Eliminar Seleccionadas"**.
3. Aparece el cuadro de confirmación.  
   - Si seleccionas **"Aceptar"**, se eliminan las tareas seleccionadas.
   - Si seleccionas **"Cancelar"**, no se hace nada.

---

## Cambiar estado con checkboxes y botón
¡Perfecto! Vamos a implementar la funcionalidad para cambiar el estado de las **tareas seleccionadas** (de **pendiente** a **completada** y viceversa) usando **checkboxes** y un botón que enviará la solicitud al servidor.

---

## **Actualización del Template Mustache**

Añadiremos un **botón "Cambiar Estado"** que actuará sobre las tareas seleccionadas. Aquí está el template `tasks.mustache` modificado:

```html
<!DOCTYPE html>
<html>
<head>
    <title>Lista de Tareas</title>
    <script>
        // Función para confirmar el cambio de estado
        function confirmChangeStatus() {
            const checkboxes = document.querySelectorAll('input[name="taskIds"]:checked');
            if (checkboxes.length === 0) {
                alert("Por favor, selecciona al menos una tarea para cambiar su estado.");
                return false;
            }

            const confirmed = confirm("¿Estás seguro de que deseas cambiar el estado de las tareas seleccionadas?");
            if (confirmed) {
                document.getElementById("change-status-form").submit();
            }
        }

        // Función para confirmar borrado
        function confirmDeleteSelected() {
            const checkboxes = document.querySelectorAll('input[name="taskIds"]:checked');
            if (checkboxes.length === 0) {
                alert("Por favor, selecciona al menos una tarea para eliminar.");
                return false;
            }

            const confirmed = confirm("¿Estás seguro de que deseas eliminar las tareas seleccionadas?");
            if (confirmed) {
                document.getElementById("delete-selected-form").submit();
            }
        }
    </script>
</head>
<body>
    <h1>Lista de Tareas</h1>

    <!-- Formulario general para cambiar estado -->
    <form id="change-status-form" action="/tasks/change-status" method="post">
        <ul>
            {{#tasks}}
            <li>
                <input type="checkbox" name="taskIds" value="{{id}}"> <!-- Checkbox -->
                {{description}} - 
                {{#completed}}Completada{{/completed}}
                {{^completed}}Pendiente{{/completed}}
            </li>
            {{/tasks}}
        </ul>
        <button type="button" onclick="confirmChangeStatus()">Cambiar Estado</button>
    </form>

    <!-- Formulario general para borrar seleccionadas -->
    <form id="delete-selected-form" action="/tasks/delete-selected" method="post">
        <button type="button" onclick="confirmDeleteSelected()" style="color:red;">Eliminar Seleccionadas</button>
    </form>

    <a href="/tasks/new">Agregar Nueva Tarea</a>
</body>
</html>
```

---

## **Controlador: Endpoint para cambiar estado**

Creamos un nuevo endpoint que recibe una lista de IDs y alterna el estado (**pendiente ↔ completada**) de las tareas seleccionadas.

```java
@PostMapping("/tasks/change-status")
public String changeStatusForSelected(@RequestParam List<Long> taskIds) {
    taskService.toggleStatusForTasks(taskIds);
    return "redirect:/tasks";
}
```

### **Explicación del código**
- **`@RequestParam List<Long> taskIds`**: Recibe los IDs seleccionados desde el formulario.
- **`taskService.toggleStatusForTasks(taskIds)`**: Llama al servicio para cambiar el estado de cada tarea.

---

## **Lógica en el servicio**

Añadimos un método en el servicio para alternar el estado de una lista de tareas:

```java
public void toggleStatusForTasks(List<Long> taskIds) {
    List<Task> tasks = taskRepository.findAllById(taskIds);
    for (Task task : tasks) {
        task.setCompleted(!task.isCompleted()); // Alterna el estado
    }
    taskRepository.saveAll(tasks); // Guarda los cambios en lote
}
```

### **Explicación del código**
1. **`findAllById(taskIds)`**: Recupera todas las tareas con los IDs seleccionados.
2. **`task.setCompleted(!task.isCompleted())`**: Alterna el estado actual de la tarea (`true ↔ false`).
3. **`saveAll(tasks)`**: Guarda todos los cambios en la base de datos en una sola operación.

---

## **Resumen del flujo**

1. El usuario selecciona varias tareas marcando las checkboxes.
2. Al hacer clic en **"Cambiar Estado"**, aparece una confirmación:
   ```plaintext
   ¿Estás seguro de que deseas cambiar el estado de las tareas seleccionadas?
   ```
3. Si el usuario confirma, se envía el formulario al servidor.
4. El servidor actualiza el estado de las tareas (pendiente ↔ completada).
5. La página se redirige a la lista de tareas actualizada.

---

## **Prueba el flujo**

### **Antes de seleccionar:**
```
Lista de Tareas
- [ ] Comprar leche - Pendiente
- [ ] Hacer ejercicio - Completada
- [ ] Leer un libro - Pendiente

[Cambiar Estado] [Eliminar Seleccionadas]
```

### **Después de seleccionar y confirmar:**
1. Selecciona las tareas **"Comprar leche"** y **"Leer un libro"**.
2. Haz clic en **"Cambiar Estado"**.
3. Resultado en la lista:
```
Lista de Tareas
- [ ] Comprar leche - Completada
- [ ] Hacer ejercicio - Completada
- [ ] Leer un libro - Completada
```

---

## Corregir el borrado
En el paso anterior se introdujeron elemenots que hacen que no funcione el borrado, vamos a corregirlo.
¡Entendido! Si el **borrar** dejó de funcionar y no se están enviando correctamente los **taskIds**, es probable que el problema radique en la forma en que se envían los checkboxes. Al tener múltiples formularios, es posible que los checkboxes no estén siendo enviados correctamente.

Vamos a solucionarlo asegurándonos de que los **checkboxes** compartan el mismo formulario y sean enviados correctamente tanto para **borrar** como para **cambiar el estado**.

---

## **Solución: Unificar formularios**

En lugar de tener dos formularios separados (uno para "Cambiar Estado" y otro para "Eliminar"), utilizaremos **un solo formulario** y manejaremos ambas acciones con botones separados. Agregaremos un campo oculto para determinar qué acción ejecutar.

---

## **Template Mustache corregido**

```html
<!DOCTYPE html>
<html>
<head>
    <title>Lista de Tareas</title>
    <script>
        // Función para confirmar el borrado
        function confirmDeleteSelected() {
            const checkboxes = document.querySelectorAll('input[name="taskIds"]:checked');
            if (checkboxes.length === 0) {
                alert("Por favor, selecciona al menos una tarea para eliminar.");
                return false;
            }

            const confirmed = confirm("¿Estás seguro de que deseas eliminar las tareas seleccionadas?");
            if (confirmed) {
                document.getElementById("action-type").value = "delete";
                document.getElementById("task-action-form").submit();
            }
        }

        // Función para confirmar cambio de estado
        function confirmChangeStatus() {
            const checkboxes = document.querySelectorAll('input[name="taskIds"]:checked');
            if (checkboxes.length === 0) {
                alert("Por favor, selecciona al menos una tarea para cambiar su estado.");
                return false;
            }

            const confirmed = confirm("¿Estás seguro de que deseas cambiar el estado de las tareas seleccionadas?");
            if (confirmed) {
                document.getElementById("action-type").value = "toggle";
                document.getElementById("task-action-form").submit();
            }
        }
    </script>
</head>
<body>
    <h1>Lista de Tareas</h1>

    <!-- Formulario general para ambas acciones -->
    <form id="task-action-form" action="/tasks/action" method="post">
        <ul>
            {{#tasks}}
            <li>
                <input type="checkbox" name="taskIds" value="{{id}}"> <!-- Checkbox -->
                {{description}} - 
                {{#completed}}Completada{{/completed}}
                {{^completed}}Pendiente{{/completed}}
            </li>
            {{/tasks}}
        </ul>
        <!-- Campo oculto para determinar la acción -->
        <input type="hidden" id="action-type" name="actionType" value="">

        <!-- Botones para acciones -->
        <button type="button" onclick="confirmChangeStatus()">Cambiar Estado</button>
        <button type="button" onclick="confirmDeleteSelected()" style="color:red;">Eliminar Seleccionadas</button>
    </form>

    <a href="/tasks/new">Agregar Nueva Tarea</a>
</body>
</html>
```

---

## **Controlador: Un único endpoint**

En el controlador, usamos un **único endpoint** para manejar ambas acciones (cambiar estado y borrar) según el valor del parámetro `actionType`.

```java
@PostMapping("/tasks/action")
public String handleTaskActions(@RequestParam List<Long> taskIds, @RequestParam String actionType) {
    if ("delete".equals(actionType)) {
        taskService.deleteTasksByIds(taskIds);
    } else if ("toggle".equals(actionType)) {
        taskService.toggleStatusForTasks(taskIds);
    }
    return "redirect:/tasks";
}
```

---

## **Servicio: Métodos existentes**

Aquí reutilizamos los métodos que ya habíamos creado:

1. **Eliminar tareas seleccionadas:**
```java
public void deleteTasksByIds(List<Long> taskIds) {
    taskRepository.deleteAllById(taskIds);
}
```

2. **Alternar el estado de las tareas seleccionadas:**
```java
public void toggleStatusForTasks(List<Long> taskIds) {
    List<Task> tasks = taskRepository.findAllById(taskIds);
    for (Task task : tasks) {
        task.setCompleted(!task.isCompleted()); // Alterna el estado
    }
    taskRepository.saveAll(tasks);
}
```

---

## **Flujo de funcionamiento**

1. **El usuario selecciona varias tareas** marcando las checkboxes.
2. Al hacer clic en:
   - **"Cambiar Estado"**, el valor del campo oculto `actionType` se establece en `"toggle"`.
   - **"Eliminar Seleccionadas"**, el valor de `actionType` se establece en `"delete"`.
3. El formulario se envía al endpoint `/tasks/action` con:
   - La lista de IDs seleccionados (`taskIds`).
   - La acción a realizar (`actionType`).
4. El controlador ejecuta la acción correspondiente (`delete` o `toggle`) y redirige a la página de tareas.

---

## **Ventajas de este enfoque**

1. **Un único formulario**: Evitamos duplicación de código y errores.
2. **Flexibilidad**: Podemos manejar múltiples acciones usando el campo oculto `actionType`.
3. **Confirmación previa**: Ambas acciones tienen confirmación mediante **JavaScript**.
4. **Código limpio**: El controlador se mantiene simple y claro.

---

## **Prueba del flujo**

1. Selecciona varias tareas marcando las checkboxes.
2. Haz clic en:
   - **"Cambiar Estado"** para alternar entre completada y pendiente.
   - **"Eliminar Seleccionadas"** para borrar las tareas seleccionadas.
3. Confirma la acción en el cuadro de confirmación.

---

## Mostrar botones sólo cuando hay tareas
Para asegurarnos de que los botones **"Cambiar Estado"** y **"Eliminar Seleccionadas"** solo se muestren cuando existan tareas en la lista, debemos realizar una validación simple en la plantilla **Mustache**.

---

## **Actualización del Template Mustache**

Podemos usar una **condicional** Mustache (`{{#tasks}}` y `{{/tasks}}`) para verificar si la lista de tareas tiene elementos.

```html
<!DOCTYPE html>
<html>
<head>
    <title>Lista de Tareas</title>
    <script>
        // Función para confirmar el borrado
        function confirmDeleteSelected() {
            const checkboxes = document.querySelectorAll('input[name="taskIds"]:checked');
            if (checkboxes.length === 0) {
                alert("Por favor, selecciona al menos una tarea para eliminar.");
                return false;
            }

            const confirmed = confirm("¿Estás seguro de que deseas eliminar las tareas seleccionadas?");
            if (confirmed) {
                document.getElementById("action-type").value = "delete";
                document.getElementById("task-action-form").submit();
            }
        }

        // Función para confirmar cambio de estado
        function confirmChangeStatus() {
            const checkboxes = document.querySelectorAll('input[name="taskIds"]:checked');
            if (checkboxes.length === 0) {
                alert("Por favor, selecciona al menos una tarea para cambiar su estado.");
                return false;
            }

            const confirmed = confirm("¿Estás seguro de que deseas cambiar el estado de las tareas seleccionadas?");
            if (confirmed) {
                document.getElementById("action-type").value = "toggle";
                document.getElementById("task-action-form").submit();
            }
        }
    </script>
</head>
<body>
    <h1>Lista de Tareas</h1>

    {{#tasks}} <!-- Solo muestra el formulario si hay tareas -->
    <!-- Formulario general para ambas acciones -->
    <form id="task-action-form" action="/tasks/action" method="post">
        <ul>
            {{#tasks}}
            <li>
                <input type="checkbox" name="taskIds" value="{{id}}"> <!-- Checkbox -->
                {{description}} - 
                {{#completed}}Completada{{/completed}}
                {{^completed}}Pendiente{{/completed}}
            </li>
            {{/tasks}}
        </ul>
        <!-- Campo oculto para determinar la acción -->
        <input type="hidden" id="action-type" name="actionType" value="">

        <!-- Botones para acciones -->
        <button type="button" onclick="confirmChangeStatus()">Cambiar Estado</button>
        <button type="button" onclick="confirmDeleteSelected()" style="color:red;">Eliminar Seleccionadas</button>
    </form>
    {{/tasks}}

    {{^tasks}}
    <p>No hay tareas disponibles.</p>
    {{/tasks}}

    <a href="/tasks/new">Agregar Nueva Tarea</a>
</body>
</html>
```

---

## **Explicación del código**

1. **Condicional `{{#tasks}}` y `{{/tasks}}`**:
   - `{{#tasks}}`: Muestra el formulario y los botones solo si la lista `tasks` tiene elementos.
   - `{{/tasks}}`: Muestra un mensaje alternativo **"No hay tareas disponibles"** si la lista está vacía.

2. **Formulario con botones**:
   - Se coloca dentro del bloque `{{#tasks}}`, por lo que **solo se renderiza si hay tareas**.

3. **Mensaje de "No hay tareas"**:
   - Se muestra si `tasks` está vacío usando el bloque inverso `{{^tasks}}`.

---

## **Comportamiento esperado**

### **Caso 1: Hay tareas en la lista**
- Se muestran las tareas en una lista.
- Aparecen los botones **"Cambiar Estado"** y **"Eliminar Seleccionadas"**.

```
Lista de Tareas
- [ ] Comprar leche - Pendiente
- [ ] Hacer ejercicio - Completada

[Cambiar Estado] [Eliminar Seleccionadas]
```

### **Caso 2: No hay tareas en la lista**
- No se muestran los botones ni el formulario.
- Aparece un mensaje indicando que no hay tareas.

```
No hay tareas disponibles.

[Agregar Nueva Tarea]
```

---

## **Ventajas de esta solución**
1. **Limpieza en la interfaz**: Los botones no aparecen cuando no hay tareas que manipular.
2. **Uso eficiente de Mustache**: Aprovechamos las secciones condicionales (`{{#tasks}}` y `{{^tasks}}`).
3. **Código mantenible**: La lógica se mantiene en la vista sin necesidad de cambios en el backend.

---

## Eliminar la duplicación de botones
Si las tareas siguen apareciendo duplicadas, **es probable que aún estés iterando sobre la lista `tasks` más de una vez** dentro del template **Mustache**.

Para evitar este problema completamente, **debes asegurarte de usar un solo bloque `{{#tasks}}`** para renderizar las tareas. El formulario y los botones deben estar **fuera del bloque de iteración**.

---

## **Template Mustache corregido (Solución final)**

Aquí está el código **corregido**:

```html
<!DOCTYPE html>
<html>
<head>
    <title>Lista de Tareas</title>
    <script>
        // Función para confirmar el borrado
        function confirmDeleteSelected() {
            const checkboxes = document.querySelectorAll('input[name="taskIds"]:checked');
            if (checkboxes.length === 0) {
                alert("Por favor, selecciona al menos una tarea para eliminar.");
                return false;
            }

            const confirmed = confirm("¿Estás seguro de que deseas eliminar las tareas seleccionadas?");
            if (confirmed) {
                document.getElementById("action-type").value = "delete";
                document.getElementById("task-action-form").submit();
            }
        }

        // Función para confirmar cambio de estado
        function confirmChangeStatus() {
            const checkboxes = document.querySelectorAll('input[name="taskIds"]:checked');
            if (checkboxes.length === 0) {
                alert("Por favor, selecciona al menos una tarea para cambiar su estado.");
                return false;
            }

            const confirmed = confirm("¿Estás seguro de que deseas cambiar el estado de las tareas seleccionadas?");
            if (confirmed) {
                document.getElementById("action-type").value = "toggle";
                document.getElementById("task-action-form").submit();
            }
        }
    </script>
</head>
<body>
    <h1>Lista de Tareas</h1>

    {{#tasks}}
    <!-- Formulario general para acciones -->
    <form id="task-action-form" action="/tasks/action" method="post">
        <ul>
            <!-- Iteramos solo una vez sobre la lista -->
            {{#tasks}}
            <li>
                <input type="checkbox" name="taskIds" value="{{id}}"> <!-- Checkbox -->
                {{description}} - 
                {{#completed}}Completada{{/completed}}
                {{^completed}}Pendiente{{/completed}}
            </li>
            {{/tasks}}
        </ul>

        <!-- Campo oculto para determinar la acción -->
        <input type="hidden" id="action-type" name="actionType" value="">

        <!-- Botones para acciones -->
        <button type="button" onclick="confirmChangeStatus()">Cambiar Estado</button>
        <button type="button" onclick="confirmDeleteSelected()" style="color:red;">Eliminar Seleccionadas</button>
    </form>
    {{/tasks}}

    <!-- Mensaje cuando no hay tareas -->
    {{^tasks}}
    <p>No hay tareas disponibles.</p>
    {{/tasks}}

    <a href="/tasks/new">Agregar Nueva Tarea</a>
</body>
</html>
```

---

## **Explicación de la corrección**

1. **Uso de un solo bloque `{{#tasks}}` externo**:
   - El bloque `{{#tasks}}` envuelve el formulario y la lista. Esto asegura que el formulario y sus botones solo aparezcan si la lista tiene tareas.

2. **Iteración única sobre la lista**:
   - La lista de tareas dentro del formulario **se itera solo una vez** usando `{{#tasks}}`.
   - Antes, si había varios bloques `{{#tasks}}`, se renderizaban duplicados.

3. **Condicional `{{^tasks}}`**:
   - Si no hay tareas (`tasks` está vacío), se muestra el mensaje **"No hay tareas disponibles"**.

4. **Botones ubicados fuera del bloque de iteración**:
   - Los botones **"Cambiar Estado"** y **"Eliminar Seleccionadas"** solo se renderizan una vez.

---

## **Comportamiento esperado**

### **Caso 1: Hay tareas disponibles**
```
Lista de Tareas
- [ ] Comprar leche - Pendiente
- [ ] Hacer ejercicio - Completada

[Cambiar Estado] [Eliminar Seleccionadas]
```

### **Caso 2: No hay tareas disponibles**
```
No hay tareas disponibles.

[Agregar Nueva Tarea]
```

---

## **Puntos clave**
- La lista ya no debería duplicarse porque **solo se está iterando una vez**.
- Los botones se muestran solo si hay tareas (`{{#tasks}}`).
- El mensaje "No hay tareas disponibles" se muestra correctamente cuando la lista está vacía.

---



## **Pasemos a otra funcionalidad interesante búsqueda y filtrado de tareas** 

1. **Búsqueda de tareas por descripción**: Un campo de texto donde el usuario puede escribir una palabra clave y buscar tareas que contengan esa palabra.

2. **Filtrado de tareas por estado**: Botones para filtrar solo las tareas "Pendientes" o "Completadas".


## **1. Implementar Búsqueda de Tareas**



### **Controlador**
Añadimos un endpoint para manejar la búsqueda de tareas. Usaremos un parámetro llamado `keyword` para buscar tareas por descripción.

```java
@GetMapping("/tasks/search")
public String searchTasks(@RequestParam(required = false) String keyword, Model model) {
    List<Task> tasks;
    if (keyword != null && !keyword.isEmpty()) {
        tasks = taskService.searchTasks(keyword);
    } else {
        tasks = taskService.getAllTasks();
    }
    model.addAttribute("tasks", tasks);
    model.addAttribute("keyword", keyword);
    return "tasks";
}
```

---

### **Servicio**
Creamos un método en el servicio que filtre las tareas que contienen la palabra clave.

```java
public List<Task> searchTasks(String keyword) {
    return taskRepository.findByDescriptionContainingIgnoreCase(keyword);
}
```

---

### **Repositorio**
En el repositorio, usamos un método derivado de **Spring Data JPA** para filtrar las tareas.

```java
List<Task> findByDescriptionContainingIgnoreCase(String keyword);
```

---

### **Template Mustache**
Añadimos un formulario con un campo de texto para la búsqueda y un botón de envío.

```html
<!DOCTYPE html>
<html>
<head>
    <title>Lista de Tareas</title>
</head>
<body>
    <h1>Lista de Tareas</h1>

    <!-- Formulario de búsqueda -->
    <form action="/tasks/search" method="get">
        <input type="text" name="keyword" placeholder="Buscar tareas..." value="{{keyword}}">
        <button type="submit">Buscar</button>
    </form>

    <!-- Mostrar tareas -->
    {{#tasks}}
    <ul>
        {{#tasks}}
        <li>
            {{description}} - 
            {{#completed}}Completada{{/completed}}
            {{^completed}}Pendiente{{/completed}}
        </li>
        {{/tasks}}
    </ul>
    {{/tasks}}
    
    {{^tasks}}
    <p>No se encontraron tareas.</p>
    {{/tasks}}

    <a href="/tasks/new">Agregar Nueva Tarea</a>
</body>
</html>
```

---

## **2. Filtrado por Estado**

---

### **Controlador**
Añadimos un endpoint para filtrar las tareas por su estado (pendiente o completada).

```java
@GetMapping("/tasks/filter")
public String filterTasks(@RequestParam String status, Model model) {
    List<Task> tasks;
    if ("completed".equalsIgnoreCase(status)) {
        tasks = taskService.getTasksByCompleted(true);
    } else if ("pending".equalsIgnoreCase(status)) {
        tasks = taskService.getTasksByCompleted(false);
    } else {
        tasks = taskService.getAllTasks();
    }
    model.addAttribute("tasks", tasks);
    model.addAttribute("status", status);
    return "tasks";
}
```

---

### **Servicio**
Añadimos métodos para filtrar tareas por su estado.

```java
public List<Task> getTasksByCompleted(boolean completed) {
    return taskRepository.findByCompleted(completed);
}
```

---

### **Repositorio**
Añadimos otro método derivado en el repositorio.

```java
List<Task> findByCompleted(boolean completed);
```

---

### **Template Mustache**
Agregamos botones de filtro.

```html
<!-- Botones de filtro -->
<a href="/tasks/filter?status=all">Todas</a> |
<a href="/tasks/filter?status=completed">Completadas</a> |
<a href="/tasks/filter?status=pending">Pendientes</a>
```

---

## **Prueba el flujo**

1. Accede a `/tasks` para ver todas las tareas.
2. Usa el campo de búsqueda para buscar tareas por palabras clave en la descripción.
3. Usa los botones de filtro **"Todas"**, **"Completadas"** y **"Pendientes"** para ver las tareas según su estado.

---

### Aclaraciones sobre esta nueva funcionalidad
 Vamos a integrarle las nuevas funcionalidades de **búsqueda** y **filtrado** sin alterar las funcionalidades existentes. Añadiremos un formulario para la búsqueda y botones para el filtrado.



## **Ubicaciones de las modificaciones**

### **1. Formulario para búsqueda**

El formulario de búsqueda debería ir **arriba de la lista de tareas**, justo antes del formulario general, para que sea lo primero que los usuarios vean.

---

### **2. Botones para filtrado**

Los botones de filtrado pueden ir junto al formulario de búsqueda o justo encima de la lista de tareas.

---

## **Plantilla modificada**

Aquí tienes el archivo actualizado con las funcionalidades de búsqueda y filtrado:

```html
<!DOCTYPE html>
<html>
<head>
    <title>Lista de Tareas</title>
    <script>
        // Función para confirmar el borrado
        function confirmDeleteSelected() {
            const checkboxes = document.querySelectorAll('input[name="taskIds"]:checked');
            if (checkboxes.length === 0) {
                alert("Por favor, selecciona al menos una tarea para eliminar.");
                return false;
            }

            const confirmed = confirm("¿Estás seguro de que deseas eliminar las tareas seleccionadas?");
            if (confirmed) {
                document.getElementById("action-type").value = "delete";
                document.getElementById("task-action-form").submit();
            }
        }

        // Función para confirmar cambio de estado
        function confirmChangeStatus() {
            const checkboxes = document.querySelectorAll('input[name="taskIds"]:checked');
            if (checkboxes.length === 0) {
                alert("Por favor, selecciona al menos una tarea para cambiar su estado.");
                return false;
            }

            const confirmed = confirm("¿Estás seguro de que deseas cambiar el estado de las tareas seleccionadas?");
            if (confirmed) {
                document.getElementById("action-type").value = "toggle";
                document.getElementById("task-action-form").submit();
            }
        }
    </script>
</head>
<body>
<h1>Lista de Tareas</h1>

<!-- Formulario de búsqueda -->
<form action="/tasks/search" method="get" style="margin-bottom: 20px;">
    <input type="text" name="keyword" placeholder="Buscar tareas..." value="{{keyword}}">
    <button type="submit">Buscar</button>
</form>

<!-- Botones de filtrado -->
<div style="margin-bottom: 20px;">
    <a href="/tasks/filter?status=all">Todas</a> |
    <a href="/tasks/filter?status=completed">Completadas</a> |
    <a href="/tasks/filter?status=pending">Pendientes</a>
</div>

<!-- Formulario general para ambas acciones -->
<form id="task-action-form" action="/tasks/action" method="post">
    <ul>
        {{#tasks}}
        <li>
            <input type="checkbox" name="taskIds" value="{{id}}"> <!-- Checkbox -->
            {{description}} -
            {{#completed}}Completada{{/completed}}
            {{^completed}}Pendiente{{/completed}}
        </li>
        {{/tasks}}
    </ul>
    <!-- Campo oculto para determinar la acción -->
    <input type="hidden" id="action-type" name="actionType" value="">

    <!-- Botones para acciones -->
    <button type="button" onclick="confirmChangeStatus()">Cambiar Estado</button>
    <button type="button" onclick="confirmDeleteSelected()" style="color:red;">Eliminar Seleccionadas</button>
</form>

<a href="/tasks/new">Agregar Nueva Tarea</a>
</body>
</html>
```

---

## **Explicación de los cambios**

1. **Formulario de búsqueda**:
   - Se agregó al principio, antes del formulario principal.
   - Usa el método `GET` para enviar la palabra clave al servidor.

   ```html
   <form action="/tasks/search" method="get" style="margin-bottom: 20px;">
       <input type="text" name="keyword" placeholder="Buscar tareas..." value="{{keyword}}">
       <button type="submit">Buscar</button>
   </form>
   ```

   - **`value="{{keyword}}"`**: Mantiene la palabra clave ingresada en el campo de texto para que no se pierda después de la búsqueda.

2. **Botones de filtrado**:
   - Añadimos un conjunto de enlaces para filtrar por estado (`all`, `completed`, `pending`).

   ```html
   <div style="margin-bottom: 20px;">
       <a href="/tasks/filter?status=all">Todas</a> |
       <a href="/tasks/filter?status=completed">Completadas</a> |
       <a href="/tasks/filter?status=pending">Pendientes</a>
   </div>
   ```

   - Los enlaces redirigen al servidor con el parámetro `status`.

3. **Integración con la lista de tareas**:
   - La lista de tareas se mantiene igual, pero ahora reflejará los resultados según el filtro o búsqueda aplicados.

---

## **Flujo esperado**

1. **Búsqueda**:
   - Ingresar una palabra clave en el campo de texto y hacer clic en "Buscar".
   - El servidor filtra las tareas según la descripción y devuelve los resultados.

2. **Filtrado**:
   - Al hacer clic en "Todas", "Completadas" o "Pendientes", el servidor devuelve solo las tareas correspondientes.

3. **Acciones existentes (Cambiar Estado / Eliminar)**:
   - Siguen funcionando como antes y no se ven afectadas por las nuevas funcionalidades.

---

## Refactorizar HomeController, index y about
 Vamos a modificar el **HomeController**, el template `index.mustache`, y el template `about.mustache` para que sean más útiles y estén conectados con la funcionalidad de gestión de tareas.

---

## **Modificación del HomeController**

Actualizamos el controlador para incluir enlaces útiles y descripciones relevantes sobre la aplicación.

```java
package daw2a.springmvc.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("title", "Gestión de Tareas");
        model.addAttribute("description", "Bienvenido a la aplicación de gestión de tareas. Aquí puedes organizar tus tareas, marcarlas como completadas, eliminarlas y mucho más.");
        return "index";
    }

    @GetMapping("/about")
    public String about(Model model) {
        model.addAttribute("title", "Acerca de esta Aplicación");
        model.addAttribute("description", "Esta aplicación permite gestionar tareas fácilmente, incluyendo opciones para buscar, filtrar, marcar como completadas o pendientes, y eliminarlas. Está desarrollada utilizando Spring MVC y Mustache.");
        return "about";
    }
}
```

---

## **Modificación del template `index.mustache`**

Actualizamos la página principal para incluir enlaces útiles a las funcionalidades de la aplicación.

```html
<!DOCTYPE html>
<html>
<head>
    <title>{{title}}</title>
</head>
<body>
    <h1>{{title}}</h1>
    <p>{{description}}</p>

    <h2>Opciones disponibles:</h2>
    <ul>
        <li><a href="/tasks">Ver lista de tareas</a></li>
        <li><a href="/about">Acerca de esta aplicación</a></li>
        <li><a href="/tasks/new">Agregar nueva tarea</a></li>
    </ul>
</body>
</html>
```

---

## **Modificación del template `about.mustache`**

Actualizamos la página "Acerca de" para proporcionar más detalles sobre la aplicación.

```html
<!DOCTYPE html>
<html>
<head>
    <title>{{title}}</title>
</head>
<body>
    <h1>{{title}}</h1>
    <p>{{description}}</p>

    <h2>Características de la aplicación:</h2>
    <ul>
        <li>Gestión completa de tareas: creación, edición, eliminación y cambio de estado.</li>
        <li>Búsqueda de tareas por palabras clave.</li>
        <li>Filtrado por estado: ver tareas completadas o pendientes.</li>
        <li>Diseño sencillo y funcional con Mustache.</li>
    </ul>

    <a href="/">Volver al inicio</a>
</body>
</html>
```

---

## **Resumen de los cambios**

### **1. `HomeController`**
- **`/` (Home):** Ahora es más informativo y redirige a funcionalidades importantes.
- **`/about` (Acerca de):** Proporciona información sobre la aplicación y sus características.

### **2. `index.mustache`**
- Muestra una introducción y enlaces útiles para navegar hacia:
  - La lista de tareas.
  - La página "Acerca de".
  - La opción de agregar nuevas tareas.

### **3. `about.mustache`**
- Explica las características principales de la aplicación.
- Proporciona un enlace para volver al inicio.

---

### **Prueba del flujo**

1. **Página principal (`/`)**:  
   Muestra la descripción general de la aplicación y los enlaces a las secciones importantes.

2. **Página "Acerca de" (`/about`)**:  
   Proporciona detalles útiles sobre la aplicación y redirige de vuelta al inicio.

3. **Navegación**:  
   Usa los enlaces para explorar las funcionalidades de la aplicación de forma más accesible.

---

¡Perfecto! Implementaremos la SPA utilizando **JavaScript Vanilla** (sin jQuery). Esto nos permitirá tener un mayor control sobre el código y evitar dependencias adicionales. Sigamos los pasos necesarios para lograrlo.

---

## **Plan**

1. **Estructura HTML básica**: Una página principal con un contenedor donde se cargará dinámicamente el contenido.
2. **Navegación SPA**: Implementar un manejador de eventos para cargar contenido dinámicamente al hacer clic en los enlaces.
3. **AJAX con Fetch API**: Usar Fetch API para cargar fragmentos desde el servidor.
4. **Manejo del historial del navegador**: Implementar soporte para los botones "Atrás" y "Adelante".
5. **Controladores y fragmentos Mustache**: Asegurar que los controladores devuelvan solo los fragmentos de contenido.

---

### **1. Estructura HTML**

Crearemos un archivo HTML principal que actuará como el contenedor de la SPA.

```html
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Gestión de Tareas</title>
    <script>
        // Cargar contenido dinámicamente
        async function loadContent(url) {
            try {
                const response = await fetch(url, { method: "GET" });
                if (!response.ok) throw new Error(`Error ${response.status}: ${response.statusText}`);
                const content = await response.text();
                document.getElementById("content").innerHTML = content;
            } catch (error) {
                console.error("Error al cargar contenido:", error);
                document.getElementById("content").innerHTML = "<p>Error al cargar el contenido.</p>";
            }
        }

        // Manejar clics en enlaces SPA
        document.addEventListener("click", function (event) {
            if (event.target.tagName === "A" && event.target.dataset.spa === "true") {
                event.preventDefault();
                const url = event.target.href;
                loadContent(url);
                history.pushState(null, "", url); // Actualiza la URL
            }
        });

        // Manejar navegación con los botones Atrás/Adelante
        window.addEventListener("popstate", function () {
            loadContent(location.pathname);
        });

        // Cargar contenido inicial
        document.addEventListener("DOMContentLoaded", function () {
            loadContent("/tasks");
        });
    </script>
</head>
<body>
    <header>
        <h1>Gestión de Tareas</h1>
        <nav>
            <a href="/tasks" data-spa="true">Tareas</a>
            <a href="/about" data-spa="true">Acerca de</a>
            <a href="/tasks/new" data-spa="true">Nueva Tarea</a>
        </nav>
    </header>
    <main id="content">
        <!-- Aquí se cargará el contenido dinámico -->
    </main>
</body>
</html>
```

---

### **2. Actualización de los controladores**

Los controladores se adaptarán para devolver solo los fragmentos HTML necesarios para las secciones dinámicas.

#### **TaskController**

```java
// Listar tareas
@GetMapping("/tasks")
public String listTasks(Model model) {
    model.addAttribute("tasks", taskService.getAllTasks());
    return "fragments/tasks :: tasks";
}

// Mostrar formulario para añadir tarea
@GetMapping("/tasks/new")
public String showAddTaskForm(Model model) {
    model.addAttribute("task", new Task());
    return "fragments/add-task :: addTaskForm";
}
```

#### **HomeController**

```java
@GetMapping("/about")
public String about(Model model) {
    model.addAttribute("title", "Acerca de esta Aplicación");
    model.addAttribute("description", "Esta aplicación permite gestionar tareas fácilmente.");
    return "fragments/about :: about";
}
```

---

### **3. Fragmentos HTML**

Creamos vistas parciales para cada sección.

#### **Fragmento de tareas (tasks.mustache)**

```html
<div id="tasks">
    <h2>Lista de Tareas</h2>
    <ul>
        {{#tasks}}
        <li>
            {{description}} - {{#completed}}Completada{{/completed}}{{^completed}}Pendiente{{/completed}}
        </li>
        {{/tasks}}
    </ul>
</div>
```

#### **Fragmento para "Nueva Tarea" (add-task.mustache)**

```html
<div id="add-task">
    <h2>Agregar Nueva Tarea</h2>
    <form action="/tasks" method="post">
        <label for="description">Descripción:</label>
        <input type="text" id="description" name="description" required>
        <br>
        <label for="completed">¿Completada?</label>
        <input type="checkbox" id="completed" name="completed" value="true">
        <br>
        <button type="submit">Guardar</button>
    </form>
</div>
```

#### **Fragmento para "Acerca de" (about.mustache)**

```html
<div id="about">
    <h2>{{title}}</h2>
    <p>{{description}}</p>
</div>
```

---

### **4. Flujo completo**

1. **Carga inicial**:
   - Al cargar la página, se ejecuta la función `loadContent("/tasks")` para mostrar la lista de tareas.
2. **Navegación SPA**:
   - Los enlaces con `data-spa="true"` disparan una solicitud dinámica usando `fetch`.
3. **Manejo del historial**:
   - El historial del navegador se actualiza dinámicamente con `history.pushState` y es manejado con el evento `popstate`.
4. **Vistas dinámicas**:
   - Cada sección (`/tasks`, `/about`, `/tasks/new`) se renderiza en el contenedor `<main id="content">` sin recargar la página.

---

### **Prueba del flujo**

1. **Navegación inicial**: Verifica que `/tasks` se carga al inicio.
2. **Cambio de secciones**: Navega entre "Tareas", "Acerca de" y "Nueva Tarea" sin recargar la página.
3. **Historial**: Usa los botones "Atrás" y "Adelante" del navegador para verificar la funcionalidad.

---

### **Siguientes pasos**

1. **Integrar operaciones dinámicas (CRUD)** con Fetch API.
2. **Agregar notificaciones visuales para operaciones exitosas o fallidas**.
3. **Mejorar el diseño con Bootstrap o CSS personalizado**.

¡Perfecto! Vamos a implementar un sistema de **usuarios** y **login** en tu aplicación para garantizar que cada usuario pueda acceder solo a sus propias tareas. Esto también sentará las bases para gestionar **roles** más adelante.

---

## **Plan Paso a Paso**

1. **Crear la entidad Usuario**: Guardaremos información básica como nombre de usuario y contraseña.
2. **Configurar Spring Security**: Añadiremos la seguridad para manejar el inicio de sesión.
3. **Añadir vistas para el login y registro**.
4. **Proteger los endpoints**: Solo los usuarios autenticados podrán acceder a las tareas.
5. **Asignar tareas a usuarios**: Asociar cada tarea con el usuario que la creó.

---

### **1. Crear la Entidad Usuario**

Creamos una entidad `User` para almacenar información básica de los usuarios.

#### **Entidad Usuario**

```java
package daw2a.springmvc.model;

import jakarta.persistence.*;
import java.util.Set;

@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "user_roles",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles;

    // Getters y setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }
}
```

#### **Entidad Role**

También crearemos una entidad `Role` para manejar roles más adelante.

```java
package daw2a.springmvc.model;

import jakarta.persistence.*;
import java.util.Set;

@Entity
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;

    @ManyToMany(mappedBy = "roles")
    private Set<User> users;

    // Getters y setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<User> getUsers() {
        return users;
    }

    public void setUsers(Set<User> users) {
        this.users = users;
    }
}
```

---

### **2. Repositorios para Usuarios y Roles**

Creamos repositorios para las entidades `User` y `Role`.

#### **UserRepository**

```java
package daw2a.springmvc.repository;

import daw2a.springmvc.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);
}
```

#### **RoleRepository**

```java
package daw2a.springmvc.repository;

import daw2a.springmvc.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Role findByName(String name);
}
```

---

### **3. Configurar Spring Security**

Añadimos configuración básica para manejar el inicio de sesión.

#### **Clase de Configuración de Seguridad**

```java
package daw2a.springmvc.config;

import daw2a.springmvc.service.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class SecurityConfig extends org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter {

    private final CustomUserDetailsService userDetailsService;

    public SecurityConfig(CustomUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .csrf().disable()
            .authorizeRequests()
            .antMatchers("/login", "/register", "/css/**", "/js/**").permitAll()
            .anyRequest().authenticated()
            .and()
            .formLogin()
            .loginPage("/login")
            .defaultSuccessUrl("/tasks", true)
            .permitAll()
            .and()
            .logout()
            .logoutUrl("/logout")
            .logoutSuccessUrl("/login")
            .permitAll();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
```

---

### **4. Servicio de Usuarios**

Creamos un servicio para cargar los usuarios desde la base de datos.

#### **CustomUserDetailsService**

```java
package daw2a.springmvc.service;

import daw2a.springmvc.model.User;
import daw2a.springmvc.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("Usuario no encontrado");
        }
        return new org.springframework.security.core.userdetails.User(
            user.getUsername(),
            user.getPassword(),
            user.getRoles().stream().map(role -> new org.springframework.security.core.authority.SimpleGrantedAuthority(role.getName())).toList()
        );
    }
}
```

---

### **5. Vistas para Login y Registro**

#### **login.mustache**

```html
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Login</title>
</head>
<body>
    <h1>Iniciar Sesión</h1>
    <form action="/login" method="post">
        <label for="username">Usuario:</label>
        <input type="text" id="username" name="username" required>
        <br>
        <label for="password">Contraseña:</label>
        <input type="password" id="password" name="password" required>
        <br>
        <button type="submit">Entrar</button>
    </form>
</body>
</html>
```

#### **register.mustache**

```html
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Registro</title>
</head>
<body>
    <h1>Registro de Usuario</h1>
    <form action="/register" method="post">
        <label for="username">Usuario:</label>
        <input type="text" id="username" name="username" required>
        <br>
        <label for="password">Contraseña:</label>
        <input type="password" id="password" name="password" required>
        <br>
        <button type="submit">Registrar</button>
    </form>
</body>
</html>
```

---

Con estos pasos, tendrás un sistema básico de usuarios y login.