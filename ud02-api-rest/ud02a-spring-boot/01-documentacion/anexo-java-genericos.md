

# 📘 Java Genéricos y Estructuras de Datos — Guía para Spring Boot

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

## 3️⃣ Qué significa `ResponseEntity<T>`

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

## 4️⃣ Genéricos dentro de genéricos

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

## 5️⃣ ¿Por qué Spring Boot usa tanto genéricos?

Porque:

* Los genéricos garantizan **seguridad de tipos** en tiempo de compilación.
* Evitan errores como “`ClassCastException`”.
* Permiten a Spring Boot **saber qué tipo de datos serializar a JSON** (por ejemplo, una lista de `Task` → `[{"id":1,"title":"..."}]`).

---

## 6️⃣ Relación con JSON

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

## 🧠 En resumen

| Código Java                  | Significado          | Tipo de respuesta                 |
| ---------------------------- | -------------------- | --------------------------------- |
| `Task`                       | Un objeto            | `{"id":1,"title":"..."}`          |
| `List<Task>`                 | Una lista            | `[ {...}, {...} ]`                |
| `ResponseEntity<Task>`       | Objeto + código HTTP | `200 OK + {"id":1,"title":"..."}` |
| `ResponseEntity<List<Task>>` | Lista + código HTTP  | `200 OK + [ {...}, {...} ]`       |

---

