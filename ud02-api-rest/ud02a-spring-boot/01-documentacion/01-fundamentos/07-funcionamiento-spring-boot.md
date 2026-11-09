
# ⚙️ Cómo funciona Spring Boot cuando llega una petición HTTP

> Este documento explica lo que “ocurre por dentro” cuando ejecutamos una aplicación Spring Boot.
> Muchos de estos pasos en PHP o Node.js se hacían “a mano”, y aquí los realiza el framework automáticamente.

---

## 🧩 1️⃣ En PHP todo era manual

En PHP el servidor (Apache o Nginx) ejecuta un *script* por cada petición.
Ese script lee variables globales (`$_GET`, `$_POST`, `$_SERVER`, etc.), analiza la URL y llama a una función.

```php
<?php
$path = $_GET['path'] ?? '/';
if ($path === '/books') listBooks();
elseif ($path === '/books/add') addBook();
```

El desarrollador decide qué función maneja cada URL.
Spring Boot hace lo mismo, pero con una infraestructura más ordenada y robusta.

---

## 🧭 2️⃣ Flujo interno de una petición HTTP en Spring Boot

Imaginemos que llega:

```
GET /books/7
Host: localhost:8080
Accept: application/json
```

### 🧱 Paso 1: Servidor embebido (Tomcat)

Spring Boot arranca **Tomcat** dentro de la propia aplicación.
Tomcat escucha en el puerto 8080 y recibe las peticiones HTTP.

👉 En PHP esto lo haría Apache o Nginx.

---

### 🧱 Paso 2: Creación del `HttpServletRequest`

Tomcat crea un objeto Java que representa la solicitud:

* Método (`GET`, `POST`…)
* Ruta (`/books/7`)
* Cabeceras (`Accept`, `User-Agent`, …)
* Parámetros (`?done=true`)
* Cuerpo (JSON si es `POST`/`PUT`)

Ese objeto se pasa al **DispatcherServlet** de Spring MVC.

---

### 🧱 Paso 3: `DispatcherServlet` busca el método adecuado

Spring registra todos los métodos con anotaciones como `@GetMapping`, `@PostMapping`, etc.
Cada anotación define una “ruta” y un “verbo”.

Ejemplo:

```java
@GetMapping("/books/{id}")
public Book getOne(@PathVariable Long id) { ... }
```

Cuando llega la petición `/books/7`, el DispatcherServlet encuentra el método que coincide.

---

### 🧱 Paso 4: Inyección de parámetros y cabeceras

Antes de llamar al método, Spring **rellena automáticamente** los parámetros:

| Anotación                      | Qué hace                   | Equivalente en PHP                              |
| ------------------------------ | -------------------------- | ----------------------------------------------- |
| `@PathVariable Long id`        | Extrae el `{id}` de la URL | `$_GET['id']`                                   |
| `@RequestParam String q`       | Lee `?q=valor`             | `$_GET['q']`                                    |
| `@RequestBody Book dto`        | Deserializa el cuerpo JSON | `json_decode(file_get_contents('php://input'))` |
| `@RequestHeader("User-Agent")` | Lee cabeceras HTTP         | `$_SERVER['HTTP_USER_AGENT']`                   |

---

### 🧱 Paso 5: Ejecución del método del controlador

Spring invoca el método y recoge su resultado (un objeto Java, texto o `ResponseEntity`).

---

### 🧱 Paso 6: Conversión a respuesta HTTP

El resultado pasa por un **`HttpMessageConverter`**, que decide cómo enviarlo:

| Tipo devuelto    | Salida                      | `Content-Type`     |
| ---------------- | --------------------------- | ------------------ |
| `String`         | texto plano                 | `text/plain`       |
| `Object`         | JSON                        | `application/json` |
| `ResponseEntity` | cuerpo + código + cabeceras | personalizado      |

Luego Spring empaqueta todo en un `HttpServletResponse` y Tomcat lo envía al cliente.

---

## 🔄 3️⃣ Esquema general del flujo

```
Cliente HTTP
   ↓
Tomcat embebido
   ↓
DispatcherServlet (Spring MVC)
   ↓
HandlerMapping → busca el método del controlador
   ↓
HandlerAdapter → invoca el método Java
   ↓
HttpMessageConverter → prepara la respuesta
   ↓
HttpServletResponse
   ↓
Cliente
```

---

## 🧮 4️⃣ PHP vs Spring Boot (comparativa directa)

| Acción           | En PHP                             | En Spring Boot                   |
| ---------------- | ---------------------------------- | -------------------------------- |
| Leer parámetros  | `$_GET['q']`                       | `@RequestParam`                  |
| Leer JSON        | `file_get_contents('php://input')` | `@RequestBody`                   |
| Leer cabeceras   | `$_SERVER['HTTP_*']`               | `@RequestHeader`                 |
| Determinar ruta  | `$_SERVER['REQUEST_URI']`          | `@GetMapping`, `@PostMapping`    |
| Decidir función  | `if ($path==...)`                  | DispatcherServlet                |
| Enviar respuesta | `echo json_encode($data)`          | return objeto / `ResponseEntity` |

---

## 🔍 5️⃣ Cómo ver la “magia” en acción

Activa el log en `application.properties`:

```properties
logging.level.org.springframework.web=DEBUG
```

Verás líneas como:

```
Mapped "{[/books/{id}],methods=[GET]}" onto public Book BookController.getOne(Long)
DispatcherServlet: GET "/books/7", handler=BookController#getOne(Long)
```

Así puedes observar cómo Spring enruta cada petición.

---

## 🧠 Conclusión

Spring Boot **no hace magia**: simplemente automatiza lo que antes hacíamos “a mano” en PHP.
Tú defines los controladores, y el framework se encarga de:

* Recibir la petición HTTP.
* Interpretar cabeceras y parámetros.
* Llamar al método correcto.
* Convertir el resultado en una respuesta.

> Saber esto te ayuda a entender qué ocurre entre el cliente y tu código, y a depurar con confianza.
