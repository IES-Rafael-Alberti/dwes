---
title: "Tests de errores y respuestas HTTP coherentes"
author: "José MSA"
output: pdf_document
---



---

# Capítulo 2 bis — Tests de errores y payload consistente (Tasks)

> **Material de apoyo sobre Tasks.** El recorrido ejecutable de Battleship está
> en [05 — manejo de errores](../02-ejemplos/battleship/docs/05-manejo-errores.md),
> desde el checkpoint completo de 04b. Esa guía conserva la distinción entre
> recurso inexistente (404), argumento inválido (400) y conflicto de estado (409).

**Módulo:** DWES  
**Capítulos:** 2 bis (Tests)  
**RA relacionados:** RA1, RA5

## Objetivo
Asegurar que la API devuelve **errores coherentes y uniformes** (códigos + cuerpo JSON) usando `@ControllerAdvice` y probarlos con **MockMvc**.

Este apunte utiliza una API Tasks en memoria para ilustrar el contrato. Los
nombres de paquete y controlador son ejemplos que debes adaptar a tu proyecto;
no se copia otra API completa encima de Mini Tasks. Si el controlador ya delega
en un servicio, simula ese colaborador con `@MockitoBean` y prepara su respuesta
o excepción en cada test web.

| Situación | HTTP | Campo `error` |
|---|---:|---|
| Tarea inexistente | 404 | `NOT_FOUND` |
| Datos o argumento rechazado | 400 | `BAD_REQUEST` |
| Bean Validation rechaza el DTO | 400 | `VALIDATION_ERROR` |
| Operación incompatible con el estado | 409 | `CONFLICT` |
| Fallo inesperado | 500 | `INTERNAL_ERROR` |

Una tarea inexistente **no es un argumento inválido**. Para distinguir ambas
causas, usamos una excepción de aplicación específica:

<!-- archivo: src/main/java/com/example/tasks/domain/exceptions/TaskNotFoundException.java -->
```java
package com.example.tasks.domain.exceptions;

public class TaskNotFoundException extends RuntimeException {
    public TaskNotFoundException(String message) {
        super(message);
    }
}
```

## 1. Handler global de errores (`@ControllerAdvice`)
Creamos un payload de error uniforme: `{ error, message, timestamp }`.

<!-- archivo: src/main/java/com/example/tasks/web/GlobalExceptionHandler.java -->
```java
// src/main/java/com/example/tasks/web/GlobalExceptionHandler.java
package com.example.tasks.web;

import com.example.tasks.domain.exceptions.TaskNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import java.time.Instant;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(TaskNotFoundException.class)
  public ResponseEntity<Map<String, String>> notFound(TaskNotFoundException ex) {
    return ResponseEntity.status(404).body(Map.of(
      "error", "NOT_FOUND",
      "message", ex.getMessage(),
      "timestamp", Instant.now().toString()
    ));
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<Map<String, String>> validation(MethodArgumentNotValidException ex) {
    String msg = ex.getBindingResult().getAllErrors().get(0).getDefaultMessage();
    return ResponseEntity.badRequest().body(Map.of(
      "error", "VALIDATION_ERROR",
      "message", msg,
      "timestamp", Instant.now().toString()
    ));
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<Map<String, String>> badRequest(IllegalArgumentException ex) {
    return ResponseEntity.badRequest().body(Map.of(
      "error", "BAD_REQUEST",
      "message", ex.getMessage(),
      "timestamp", Instant.now().toString()
    ));
  }

  @ExceptionHandler(IllegalStateException.class)
  public ResponseEntity<Map<String, String>> conflict(IllegalStateException ex) {
    return ResponseEntity.status(409).body(Map.of(
      "error", "CONFLICT",
      "message", ex.getMessage(),
      "timestamp", Instant.now().toString()
    ));
  }

  @ExceptionHandler({HttpMessageNotReadableException.class, MethodArgumentTypeMismatchException.class})
  public ResponseEntity<Map<String, String>> malformed(Exception ex) {
    return ResponseEntity.badRequest().body(Map.of(
      "error", "BAD_REQUEST",
      "message", "Malformed request input",
      "timestamp", Instant.now().toString()
    ));
  }

  @ExceptionHandler(RuntimeException.class)
  public ResponseEntity<Map<String, String>> generic(RuntimeException ex) {
    return ResponseEntity.status(500).body(Map.of(
      "error", "INTERNAL_ERROR",
      "message", "Unexpected error",
      "timestamp", Instant.now().toString()
    ));
  }
}
```

`@RestControllerAdvice` incorpora la escritura del cuerpo de respuesta. Los
handlers locales del controlador tienen prioridad: retíralos si sustituyes su
mapeo por este advice. Dentro del advice se elige la excepción más específica,
no el primer método escrito.

En este ejemplo el fallback se limita a **`RuntimeException`**. El JSON ilegible
y los tipos incorrectos tienen su handler específico de 400; las excepciones
de protocolo de Spring para 405/415 siguen su resolución habitual. Si decides
capturar `Exception` en general, debes preservar esos mapeos explícitamente:
no conviertas un método o contenido no admitido en un falso 500.

Los mensajes de errores esperables deben estar diseñados para el usuario. El
fallo inesperado usa un mensaje genérico y no devuelve detalles internos.

## 2. Ajustes mínimos del controlador para provocar errores

* En el POST, usar `@Valid @RequestBody CreateTaskDTO dto`; `CreateTaskDTO`
  declara `@NotBlank String title`. La restricción está en el DTO y `@Valid`
  activa su comprobación al recibir la petición.
* En un controlador en memoria, importar `TaskNotFoundException` y traducir
  la consulta inexistente a esa excepción. Si existe servicio, esa decisión
  pertenece al servicio y el controlador solo delega:

```java
@GetMapping("/{id}")
public ResponseEntity<Task> get(@PathVariable("id") Long id) {
  return tasks.stream()
      .filter(t -> t.getId().equals(id))
      .findFirst()
      .map(ResponseEntity::ok)
      .orElseThrow(() -> new TaskNotFoundException("Task not found"));
}
```

No se cambia el código HTTP de una consulta inexistente a 400 para hacer que
encaje con un handler genérico: se corrige el tipo de excepción y se comprueba
el contrato 404. En un servicio, `IllegalArgumentException` queda para rechazos
de datos/reglas y `IllegalStateException` para un conflicto de estado.

## 3. Tests con MockMvc: 400 de validación y 404 de inexistente

<!-- archivo: src/test/java/com/example/tasks/web/TaskErrorsTest.java -->
```java
// src/test/java/com/example/tasks/web/TaskErrorsTest.java
package com.example.tasks.web;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TaskController.class)
@Import(GlobalExceptionHandler.class)
class TaskErrorsTest {

  @Autowired MockMvc mvc;

  @Test
  void create_shouldReturnValidationError_whenTitleBlank() throws Exception {
    String invalid = """
        { "title": "   " }
        """;

    mvc.perform(post("/api/tasks")
            .contentType(MediaType.APPLICATION_JSON)
            .content(invalid))
       .andExpect(status().isBadRequest())
       .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
       .andExpect(jsonPath("$.error").value("VALIDATION_ERROR"))
        .andExpect(jsonPath("$.message", not(emptyOrNullString())))
       .andExpect(jsonPath("$.timestamp", not(emptyOrNullString())));
  }

  @Test
  void get_shouldReturnNotFoundPayload_whenTaskMissing() throws Exception {
    mvc.perform(get("/api/tasks/9999"))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.error").value("NOT_FOUND"))
       .andExpect(jsonPath("$.message", containsString("Task not found")))
       .andExpect(jsonPath("$.timestamp", not(emptyOrNullString())));
  }
}
```

## 4. Tests de contenido no soportado (415) y método no permitido (405)

Estos dos estados los gestiona Spring MVC automáticamente; verificamos **código** y opcionalmente el **Content-Type**.

Añade estos métodos dentro de `TaskErrorsTest`. El caso de JSON ilegible
comprueba además el handler de entrada mal formada del ejemplo:

<!-- añadir: src/test/java/com/example/tasks/web/TaskErrorsTest.java -->
```java
@Test
void create_shouldReturn415_whenWrongContentType() throws Exception {
  mvc.perform(post("/api/tasks")
         .contentType(MediaType.TEXT_PLAIN) // incorrecto
         .content("title=texto"))
     .andExpect(status().isUnsupportedMediaType()); // 415
}

@Test
void tasks_shouldReturn405_whenMethodNotAllowed() throws Exception {
  mvc.perform(put("/api/tasks")) // no hay PUT definido
     .andExpect(status().isMethodNotAllowed()); // 405
}

@Test
void create_shouldReturn400_whenJsonMalformed() throws Exception {
  mvc.perform(post("/api/tasks")
         .contentType(MediaType.APPLICATION_JSON)
         .content("{"))
     .andExpect(status().isBadRequest())
     .andExpect(jsonPath("$.error").value("BAD_REQUEST"));
}
```

Los tests del JSON no dependen del idioma configurado para Bean Validation ni
de un timestamp exacto. Prueba el código y la estructura; comprueba un mensaje
literal solo cuando ese mensaje forme parte de tu contrato.

## 5. Checklist de calidad del error payload

* [ ] Los errores de aplicación acordados usan `{ error, message, timestamp }`.
* [ ] Recurso inexistente → `404`, `error = NOT_FOUND`.
* [ ] `400` validación → `error = VALIDATION_ERROR`.
* [ ] `400` lógica → `error = BAD_REQUEST` + mensaje entendible.
* [ ] Conflicto de estado → `409`, `error = CONFLICT`.
* [ ] Fallo inesperado → `500`, mensaje público genérico.
* [ ] Un método o contenido no admitido conserva `405`/`415`, no termina en `500`.
* [ ] No filtrar **detalles internos** en producción (log sí, respuesta no).
* [ ] Añadir `traceId` o `correlationId` (opcional) más adelante.

## 6. Resumen docente

* Con `@ControllerAdvice` centralizamos los errores.
* Probamos **código HTTP** y **estructura del payload**.
* Deja la API lista para frontends y para depurar incidencias.



---
