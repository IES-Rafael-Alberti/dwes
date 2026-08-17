

# Java esencial para Spring Boot: genéricos, colecciones y APIs frecuentes

Este anexo reúne construcciones de Java que aparecen continuamente al
leer y escribir una aplicación Spring Boot. No sustituye a la guía de
Java: explica por qué aparecen `Optional`, Streams, records, lambdas y
genéricos en controladores, servicios y repositorios.

## 1️⃣ Qué son los **Genéricos**

En Java, los **genéricos** (`<T>`) permiten crear **clases y métodos que trabajan con tipos de datos de forma segura**.

Por ejemplo:

```java
List<String> nombres = new ArrayList<>();
nombres.add("Ana");
nombres.add("Luis");
```

👉 `List<String>` indica que la lista sólo puede contener **String**.
Si intentas añadir un número, el compilador avisa:

```java
nombres.add(123); // ❌ Error: no es String
```

Antes de los genéricos, se usaban estructuras sin tipo (peligrosas):

```java
List nombres = new ArrayList(); // Tipo "crudo"
nombres.add("Ana");
String nombre = (String) nombres.get(0); // 👈 Necesita CAST manual
```

Los genéricos evitan *casts* y errores de tipo en tiempo de ejecución.

---

## 2️⃣ Ejemplos con estructuras de datos

| Estructura    | Qué guarda                      | Ejemplo                                                       | Uso común                             |
| ------------- | ------------------------------- | ------------------------------------------------------------- | ------------------------------------- |
| `List<T>`     | Una lista ordenada de elementos | `List<Task> tareas = new ArrayList<>();`                      | Resultados de consultas               |
| `Set<T>`      | Colección sin duplicados        | `Set<String> roles = Set.of("ADMIN", "USER");`                | Roles, etiquetas                      |
| `Map<K,V>`    | Pares clave → valor             | `Map<String,Integer> edades = Map.of("Ana", 22, "Luis", 30);` | Diccionarios, JSON, configuración     |
| `Optional<T>` | Valor que puede o no existir    | `Optional<User> user = repo.findById(1);`                     | Evita `null` y `NullPointerException` |

---

## 3️⃣ `Optional<T>`: un resultado que puede no existir

Un repositorio no siempre encuentra el recurso pedido. Spring Data expresa
ese caso con `Optional<T>` en vez de devolver un objeto que podría ser
`null`:

```java
Optional<Task> task = taskRepository.findById(id);
```

`Optional` no es la tarea: es una caja que puede contener una tarea o estar
vacía. Esa distinción obliga a decidir qué hacer cuando el recurso no existe.

| Operación | Cuándo usarla | Ejemplo |
| --- | --- | --- |
| `orElse(valor)` | Hay un valor simple alternativo ya disponible | `task.orElse(defaultTask)` |
| `orElseGet(proveedor)` | El valor alternativo cuesta trabajo o se crea bajo demanda | `task.orElseGet(() -> defaultTask)` |
| `orElseThrow(...)` | No encontrar el recurso es un error para este caso de uso | `task.orElseThrow(() -> new TaskNotFoundException(id))` |
| `map(...)` | Hay que transformar el valor solo si existe | `task.map(TaskDTO::from)` |

Ejemplo típico en un servicio:

```java
public Task findById(Long id) {
    return taskRepository.findById(id)
            .orElseThrow(() -> new TaskNotFoundException(id));
}
```

En un controlador, `map` permite convertir el recurso encontrado a una
respuesta HTTP y `orElseGet` resolver el caso vacío:

```java
@GetMapping("/{id}")
public ResponseEntity<TaskDTO> get(@PathVariable Long id) {
    return taskRepository.findById(id)
            .map(TaskDTO::from)
            .map(ResponseEntity::ok)
            .orElseGet(() -> ResponseEntity.notFound().build());
}
```

No uses `optional.get()` salvo que se haya comprobado antes con `isPresent()`:
si está vacío, lanza una excepción. Normalmente `map`, `orElseThrow` y
`orElseGet` expresan mejor la intención. Tampoco se recomienda usar
`Optional` como atributo de una entidad, parámetro de método o tipo de JSON;
es útil sobre todo como valor de retorno.

---

## 4️⃣ Qué significa `ResponseEntity<T>`

Spring Boot usa `ResponseEntity<T>` para **devolver una respuesta HTTP personalizada**.

```java
@GetMapping("/tasks/{id}")
public ResponseEntity<Task> getTask(@PathVariable Long id) {
    Task t = service.findById(id);
    if (t != null)
        return ResponseEntity.ok(t);
    else
        return ResponseEntity.notFound().build();
}
```

👉 Aquí `ResponseEntity<Task>` significa:

> “Voy a devolver una **respuesta HTTP** cuyo cuerpo contiene un objeto `Task`.”

Otros ejemplos:

* `ResponseEntity<List<Task>>` → una lista de tareas.
* `ResponseEntity<Void>` → sin cuerpo, sólo código de estado (204 No Content).
* `ResponseEntity<String>` → texto plano (mensaje o error).

Esto da **más control** que devolver el objeto directamente, porque puedes definir:

* el **código HTTP** (200, 404, 201…)
* las **cabeceras**
* y el **cuerpo (body)**.

---

## 5️⃣ Genéricos dentro de genéricos

Sí, `ResponseEntity<List<Task>>` tiene **dos capas de genéricos**:

* `List<Task>`: lista de tareas.
* `ResponseEntity<List<Task>>`: respuesta HTTP que contiene esa lista.

Ejemplo típico:

```java
@GetMapping("/tasks")
public ResponseEntity<List<Task>> getAllTasks() {
    List<Task> tasks = service.findAll();
    return ResponseEntity.ok(tasks);
}
```

### Visualmente

```
ResponseEntity<List<Task>>
 └── ResponseEntity<body = List<Task>>
                    └── List<Task> contiene varios Task
```

---

## 6️⃣ ¿Por qué Spring Boot usa tanto genéricos?

Porque:

* Los genéricos garantizan **seguridad de tipos** en tiempo de compilación.
* Evitan errores como “`ClassCastException`”.
* Permiten a Spring Boot **saber qué tipo de datos serializar a JSON** (por ejemplo, una lista de `Task` → `[{"id":1,"title":"..."}]`).

---

## 7️⃣ Relación con JSON

Spring convierte automáticamente los objetos genéricos en JSON.

```java
List<Task> tasks = List.of(new Task(1, "Aprender Java"), new Task(2, "Probar Spring"));
return ResponseEntity.ok(tasks);
```

Se convierte en:

```json
[
  {"id":1,"title":"Aprender Java"},
  {"id":2,"title":"Probar Spring"}
]
```

Y si devuelves un único `Task`:

```json
{"id":1,"title":"Aprender Java"}
```

---

## 8️⃣ Records como DTOs REST

Un DTO (*Data Transfer Object*) representa los datos que entran o salen por
la API. No es una entidad JPA: una entidad representa estado persistente y
relaciones; un DTO representa el contrato HTTP. Por eso un `record` encaja
muy bien como DTO: es breve, inmutable y genera constructor, accesores,
`equals`, `hashCode` y `toString`.

```java
public record CreateTaskRequest(
        @NotBlank String title,
        @Size(max = 500) String description
) {}

public record TaskDTO(Long id, String title, boolean done) {
    static TaskDTO from(Task task) {
        return new TaskDTO(task.getId(), task.getTitle(), task.isDone());
    }
}
```

El controlador puede validar el DTO de entrada y devolver el de salida:

```java
@PostMapping
public ResponseEntity<TaskDTO> create(@Valid @RequestBody CreateTaskRequest request) {
    Task task = taskService.create(request.title(), request.description());
    return ResponseEntity.status(HttpStatus.CREATED).body(TaskDTO.from(task));
}
```

`@Valid` pide a Spring que aplique las restricciones de
`jakarta.validation` colocadas sobre los componentes del record. Jackson
construye el record a partir del JSON usando su constructor canónico. Para
las entidades JPA de la unidad se mantiene `class`, no `record`: tienen
identidad y ciclo de vida persistente. Consulta también
[`Clases vs Records: entidades y DTOs`](03-controladores-rest.md#clases-vs-records-entidades-y-dtos).

---

## 9️⃣ Streams: leer una colección sin perder el hilo

Un `Stream<T>` representa una secuencia de operaciones sobre datos. No es un
stream de bytes ni una colección nueva: normalmente parte de una colección,
describe transformaciones y termina con una operación final como `toList()`,
`findFirst()` o `count()`.

```java
List<TaskDTO> pending = tasks.stream()
        .filter(task -> !task.isDone())
        .map(TaskDTO::from)
        .sorted(Comparator.comparing(TaskDTO::title))
        .toList();
```

Léelo de arriba abajo: parte de `tasks`, conserva las tareas pendientes,
transforma cada entidad a DTO, ordena por título y finalmente crea la lista.
`filter` recibe un `Predicate<T>`; `map`, una `Function<T, R>`.

En los controladores, un Stream es útil para una transformación sencilla. Si
la operación tiene varias reglas de negocio, efectos secundarios o necesita
depurar cada paso, es preferible extraer un método de servicio o usar un bucle
claro. Un Stream se consume una vez: no lo guardes para recorrerlo dos veces.

---

## 🔟 Interfaces funcionales y lambdas

Una interfaz funcional tiene un único método abstracto. Java puede convertir
una lambda en esa interfaz, lo que explica las lambdas que aparecen en Streams
y en muchas APIs del framework.

| Interfaz | Representa | Ejemplo |
| --- | --- | --- |
| `Predicate<T>` | una condición que devuelve `boolean` | `task -> !task.isDone()` |
| `Function<T, R>` | una transformación de `T` a `R` | `TaskDTO::from` |
| `Consumer<T>` | una acción sin resultado | `logger::info` |
| `Supplier<T>` | una creación diferida | `() -> defaultTask` |

```java
Predicate<Task> pending = task -> !task.isDone();
Function<Task, TaskDTO> toDto = TaskDTO::from;

List<TaskDTO> pendingDtos = tasks.stream()
        .filter(pending)
        .map(toDto)
        .toList();
```

`TaskDTO::from` es una *referencia a método*: equivale a
`task -> TaskDTO.from(task)`. Úsala cuando hace el código más fácil de leer;
si oculta una transformación compleja, una lambda explícita o un método con
nombre suele ser mejor.

---

## 1️⃣1️⃣ `var`, `static` y métodos de fábrica

`var` permite que Java infiera el tipo de una variable *local* con
inicializador. No existe para campos, parámetros ni retornos, y no significa
que la variable sea inmutable:

```java
var pending = taskRepository.findByDoneFalse(); // List<Task>
pending.add(new Task());                        // sigue siendo mutable
```

Úsalo cuando el tipo sea evidente por el lado derecho. Evítalo si esconde un
tipo relevante, sobre todo en APIs públicas o expresiones largas.

Un método `static` pertenece a la clase, no a un objeto. Es apropiado para
operaciones sin estado, utilidades y fábricas sencillas:

```java
public record TaskDTO(Long id, String title, boolean done) {
    static TaskDTO from(Task task) {
        return new TaskDTO(task.getId(), task.getTitle(), task.isDone());
    }
}
```

No conviertas un servicio en una clase de métodos `static`: Spring necesita
crear e inyectar las dependencias de sus servicios. Los métodos anotados con
`@Bean` en una clase `@Configuration` son otro uso habitual de métodos que
construyen objetos para el contenedor.

---

## 1️⃣2️⃣ Texto y formato: text blocks y `formatted()`

Los *text blocks* de Java permiten escribir texto multilínea sin concatenar
cadenas ni escapar la mayoría de comillas. Son útiles para SQL, JSON de
ejemplo y plantillas de mensajes:

```java
String query = """
        select t
        from Task t
        where t.done = false
        order by t.title
        """;
```

Para insertar valores en un mensaje, usa `formatted()` sobre una cadena con
marcadores `%s`, `%d`, etc. Es la forma de instancia de `String.format()`:

```java
String message = "Task %d (%s) was not found".formatted(id, title);
String sameMessage = String.format("Task %d (%s) was not found", id, title);
```

No construyas SQL o JPQL concatenando datos del usuario. En consultas
reales usa parámetros enlazados (`:id`, `@Param`, `?`) para evitar errores y
vulnerabilidades de inyección. El text block mejora la legibilidad, no la
seguridad de una consulta.

---

## 🧠 En resumen

| Código Java                  | Significado          | Tipo de respuesta                 |
| ---------------------------- | -------------------- | --------------------------------- |
| `Task`                       | Un objeto            | `{"id":1,"title":"..."}`          |
| `List<Task>`                 | Una lista            | `[ {...}, {...} ]`                |
| `ResponseEntity<Task>`       | Objeto + código HTTP | `200 OK + {"id":1,"title":"..."}` |
| `ResponseEntity<List<Task>>` | Lista + código HTTP  | `200 OK + [ {...}, {...} ]`       |

### Checklist antes de continuar con Spring Boot

- [ ] Sé leer `Optional<T>` y elegir entre `map`, `orElseGet` y `orElseThrow`.
- [ ] Distingo entidad JPA (`class`) de DTO HTTP (`record`).
- [ ] Puedo leer una cadena `stream().filter().map().toList()` de arriba abajo.
- [ ] Reconozco `Predicate`, `Function`, `Consumer` y `Supplier` en una lambda.
- [ ] Sé que `var` solo sirve para variables locales y que no equivale a `val`.
- [ ] Uso text blocks para mejorar la legibilidad, no para concatenar datos en SQL.

---
