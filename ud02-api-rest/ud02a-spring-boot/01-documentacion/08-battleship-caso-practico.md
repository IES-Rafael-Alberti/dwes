## Bloque B — UD2/Cap. 3 “Controladores y vistas” → **Controladores REST**

> En vez de Thymeleaf, veremos **controladores REST** y retornos JSON.

### Objetivos didácticos (RA5, inicio)

* Entender **responsabilidad del controlador** en una API REST.
* Manejar **rutas**, **path variables**, **query params**, **headers** y **códigos de estado**.
* Usar **DTOs** en entrada/salida y validación con `@Valid`.
* Diferenciar retornos **“body directo”** vs **`ResponseEntity`**.
* Gestionar errores con `@ControllerAdvice`.

### Temario Cap. 3 (adaptado a REST)

* **3.1 Características del controlador**

  * `@RestController`, `@RequestMapping`, `@GetMapping`/`@PostMapping`/…
  * idempotencia/métodos HTTP (visión práctica).
* **3.2 Paso de datos a la “vista”** (→ **al cuerpo JSON de la respuesta**).

  * Retornos simples y DTOs de salida.
* **3.3 Parámetros de URL**

  * `@PathVariable`, `@RequestParam`, `@RequestHeader`, `@RequestBody`.
* **3.4 Retorno de métodos**

  * Objeto directo → 200 OK; `ResponseEntity` para **status** y **headers**.
  * Códigos: 201 (creado), 204 (sin contenido), 400/404/409/422 (errores).
* **3.5 Construcción de rutas dinámicas**

  * Estructura de endpoints y **convenciones REST**.
* **3.6 Gestión de errores**

  * `@ControllerAdvice` + payload uniforme (ya lo tenemos como base).

---

## Guion práctico (paso a paso) — 90–120 min

> Partimos del **ZIP final** ya descomprimido y funcionando.

### Paso 1 — Un controlador de ejemplo nuevo (10–15 min)

* Crear `HelloController` con endpoints demostrativos:

  * `GET /hello` → `{ "message": "Hello, DWES" }`
  * `GET /hello/{name}?y=YYYY` → usar `@PathVariable name` y `@RequestParam y` (opcional)
  * Demostrar `ResponseEntity` devolviendo `202 Accepted` o `204 No Content`.

### Paso 2 — Buenas prácticas en **GameController** (30–40 min)

* **Lectura**: añadir `GET /games/{id}` y `GET /games` con query `status`.
* **Creación** *(ya existe `POST /games`)*:

  * Cambiar retorno a `ResponseEntity<Game>` con **201** + `Location: /games/{id}` (`URI.create()`).
* **Errores**:

  * Lanzar `IllegalArgumentException` con mensajes claros (entra por `GlobalExceptionHandler` y devuelve 400).
  * Explicar por qué preferimos un **payload consistente de error** (código, mensaje, timestamp).

> **Micro-ejercicio**:
> Alumnos añaden `GET /games/search?sizeMin=&sizeMax=` y devuelven lista filtrada (en memoria/BD simple).

### Paso 3 — Entrada/salida con **DTOs** (20–30 min)

* Recordar DTOs existentes (`CreateGameDTO`, …).
* Añadir un **DTO de salida** (p. ej. `GameSummaryDTO { id, boardSize, status }`).
* Transformación en el controlador/servicio (manual o mapper simple).

### Paso 4 — Validación en entrada (10–15 min)

* Reforzar `@Valid` + anotaciones (`@Min`, `@Max`, `@NotNull`, `@Pattern`).
* Ver comportamiento con error → handler devuelve JSON uniforme.

### Paso 5 — Probar todo en Insomnia/Postman (10–15 min)

* Requests:

  * `GET /hello`, `GET /hello/Ana?y=2025`
  * `POST /games` (correcto y con error de validación)
  * `GET /games`, `GET /games/{id}`, `GET /games/search?sizeMin=...`
* Confirmar **status codes** y **cuerpos**.

---

## Material de apoyo para la sesión

* **Código base** del proyecto (ZIP final).
* **Snippet de `ResponseEntity`**:

```java
@PostMapping
public ResponseEntity<GameSummaryDTO> create(@Valid @RequestBody CreateGameDTO dto){
    Game g = service.create(dto);
    URI loc = URI.create("/games/" + g.getId());
    GameSummaryDTO out = new GameSummaryDTO(g.getId(), g.getBoardSize(), g.getStatus());
    return ResponseEntity.created(loc).body(out); // 201 + Location
}
```

* **Snippet de handler de validación** (ya lo tenemos).
* **Tabla rápida de códigos**: 200/201/204/400/404/409/422.

---

## Entregable mini (al terminar Cap. 3)

* **Rama/commit “ud2-cap3”** con:

  1. `HelloController` (2 endpoints).
  2. `GET /games`, `GET /games/{id}`, `GET /games/search`.
  3. `POST /games` devolviendo **201 + Location**.
  4. Un **DTO de salida** para lista de juegos.
  5. Colección Insomnia/Postman actualizada (4–6 requests).

**Criterios de logro (rápida):**

* [ ] Uso correcto de `@PathVariable`, `@RequestParam`, `@RequestBody`.
* [ ] Manejo de **status** con `ResponseEntity`.
* [ ] **DTO** de salida implementado.
* [ ] **Validación** en entrada y error JSON consistente.
* [ ] Colección de pruebas funcional.

**Exit ticket Cap. 3 (2–3 ítems):**

1. ¿Cuándo debo usar `ResponseEntity` en lugar de devolver el objeto directamente?
2. ¿Qué ventajas tiene devolver `201 + Location` en creaciones?
3. Pon un ejemplo de error 400 bien devuelto por validación.

---

## Tarea (opcional)

* Añadir `DELETE /games/{id}` → devolver `204` si elimina; `404` si no existe.
* Añadir paginación simple a `GET /games` con `page` y `size`.

---