# Slicing de tests: web, servicio y repositorio

**Módulo:** DWES  
**Capítulos:** 2 bis (Tests, ampliación)  
**RA relacionados:** RA1, RA5

## Objetivo
Aprender a **probar por capas** usando *slices* de Spring Boot:
- `@WebMvcTest` → **controladores** (rápidos, sin servidor real)
- `@DataJpaTest` → **repositorios** (con H2 e Hibernate)
- `@SpringBootTest` → **integración** (todo el contexto; opción servidor real)

> Para esta sección **evolucionamos** el ejemplo “Tasks” introduciendo **Service** y **Repository** de forma mínima.  
> *En el Cap. 2 bis principal seguimos sin servicios; aquí solo para practicar tests por capas.*

## Dependencias de test en Spring Boot 4

Además de `spring-boot-starter-test`, añade los módulos de los slices que utilices:

```xml
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-webmvc-test</artifactId>
  <scope>test</scope>
</dependency>
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-data-jpa-test</artifactId>
  <scope>test</scope>
</dependency>
```

## 1. Estructura mínima con servicio y repositorio

### 1.1 Entidad simple (sin relaciones)
```java
// src/main/java/com/example/tasks/domain/Task.java
package com.example.tasks.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Task {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String title;

  private boolean done;
}
```

### 1.2 Repositorio

```java
// src/main/java/com/example/tasks/repo/TaskRepository.java
package com.example.tasks.repo;

import com.example.tasks.domain.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {
  List<Task> findByDone(boolean done);
}
```

### 1.3 Servicio

```java
// src/main/java/com/example/tasks/service/TaskService.java
package com.example.tasks.service;

import com.example.tasks.domain.Task;
import com.example.tasks.repo.TaskRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service @Transactional
public class TaskService {
  private final TaskRepository repo;
  public TaskService(TaskRepository repo) { this.repo = repo; }

  public List<Task> list(Boolean done) {
    return (done == null) ? repo.findAll() : repo.findByDone(done);
  }

  public Task create(String title) {
    if (title == null || title.isBlank()) throw new IllegalArgumentException("title is required");
    return repo.save(Task.builder().title(title).done(false).build());
  }

  public Task toggle(Long id) {
    var t = repo.findById(id).orElseThrow(() -> new IllegalArgumentException("Task not found"));
    t.setDone(!t.isDone());
    return t;
  }

  public void delete(Long id) {
    if (!repo.existsById(id)) throw new IllegalArgumentException("Task not found");
    repo.deleteById(id);
  }
}
```

### 1.4 Controlador usando el servicio

```java
// src/main/java/com/example/tasks/web/TaskController.java
package com.example.tasks.web;

import com.example.tasks.domain.Task;
import com.example.tasks.service.TaskService;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

record CreateTaskDTO(@NotBlank String title) {}

@RestController
@RequestMapping("/api/tasks")
public class TaskController {
  private final TaskService service;
  public TaskController(TaskService service) { this.service = service; }

  @GetMapping
  public List<Task> list(@RequestParam(required = false) Boolean done) {
    return service.list(done);
  }

  @PostMapping
  public ResponseEntity<Task> create(@RequestBody CreateTaskDTO dto) {
    Task saved = service.create(dto.title());
    return ResponseEntity.created(URI.create("/api/tasks/" + saved.getId())).body(saved);
  }

  @PatchMapping("/{id}/toggle")
  public Task toggle(@PathVariable Long id) {
    return service.toggle(id);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable Long id) {
    service.delete(id);
    return ResponseEntity.noContent().build();
  }
}
```

*(Añade tu `GlobalExceptionHandler` de errores si lo quieres activo en las pruebas web.)*

---

## 2. `@WebMvcTest` → Pruebas de **controlador** con servicio **mockeado**

```java
// src/test/java/com/example/tasks/web/TaskWebSliceTest.java
package com.example.tasks.web;

import com.example.tasks.domain.Task;
import com.example.tasks.service.TaskService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest({ TaskController.class, GlobalExceptionHandler.class })
class TaskWebSliceTest {

  @Autowired MockMvc mvc;

  @MockitoBean TaskService service; // mock del servicio

  @Test
  void list_shouldReturnTasksFromService() throws Exception {
    when(service.list(null)).thenReturn(List.of(new Task(1L, "A", false)));

    mvc.perform(get("/api/tasks"))
       .andExpect(status().isOk())
       .andExpect(jsonPath("$[0].title").value("A"));
  }

  @Test
  void create_shouldReturn201AndBody() throws Exception {
    when(service.create("Nueva"))
        .thenAnswer(inv -> new Task(10L, "Nueva", false));

    mvc.perform(post("/api/tasks")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""{ "title": "Nueva" }"""))
       .andExpect(status().isCreated())
       .andExpect(header().string("Location", "/api/tasks/10"))
       .andExpect(jsonPath("$.id").value(10))
       .andExpect(jsonPath("$.title").value("Nueva"));
  }
}
```

**Qué cubre:** rutas, serialización, códigos HTTP y contrato del controlador.
**Ventaja:** **rápido**; no arranca BD ni servidor real.

---

## 3. `@DataJpaTest` → Pruebas de **repositorio**

```java
// src/test/java/com/example/tasks/repo/TaskRepositoryTest.java
package com.example.tasks.repo;

import com.example.tasks.domain.Task;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class TaskRepositoryTest {

  @Autowired TaskRepository repo;

  @Test
  void save_and_findByDone_shouldWork() {
    repo.save(Task.builder().title("A").done(false).build());
    repo.save(Task.builder().title("B").done(true).build());

    List<Task> done = repo.findByDone(true);
    assertThat(done).extracting("title").containsExactly("B");
  }
}
```

**Qué cubre:** SQL generado, mapeos JPA, consultas derivadas.
**Spring configura H2** y transacciones de prueba automáticamente.

---

## 4. `@SpringBootTest` → Prueba de **integración** (todo el contexto)

```java
// src/test/java/com/example/tasks/TaskIntegrationTest.java
package com.example.tasks;

import com.example.tasks.domain.Task;
import com.example.tasks.repo.TaskRepository;
import com.example.tasks.service.TaskService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class TaskIntegrationTest {

  @Autowired TaskService service;
  @Autowired TaskRepository repo;

  @Test
  void createAndToggle_shouldAffectDatabase() {
    Task t = service.create("E2E");
    assertThat(t.getId()).isNotNull();

    Task toggled = service.toggle(t.getId());
    assertThat(toggled.isDone()).isTrue();

    var fromDb = repo.findById(t.getId()).orElseThrow();
    assertThat(fromDb.isDone()).isTrue();
  }
}
```

**Qué cubre:** interacción **real** entre capas (service + repo + JPA).
**Úsalo con moderación**: son más lentas que las *slice tests*.

---

## 5. Checklist de slicing

* [ ] `@WebMvcTest`: controlador probado con **mocks** de dependencias.
* [ ] `@DataJpaTest`: repositorios contra **H2** con transacciones.
* [ ] `@SpringBootTest`: integración puntual (service+repo o web real).
* [ ] Nombres de test **expresivos** y **un test = un comportamiento**.
* [ ] Verificar **status + headers + body** en web; **persistencia** en JPA.

## 6. Resumen docente

Separar tests por capas:

* aumenta la **velocidad**,
* facilita el **aislamiento**,
* y ayuda a detectar errores **en el sitio correcto**.

Más adelante añadiremos **tests para seguridad (JWT)** y **documentación (OpenAPI)**.
