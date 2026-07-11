    ==== Eliminar desde aquí en documento a entregar a los alumnos ====
Perfecto. Aquí tienes el **prompt redefinido** (limpio, coherente y ejecutable) y, a partir de él, arrancamos con **estructura de repo** + **Iteración 1 (API + frontend mínimo)**.

---

# Prompt definitivo para el alumnado

==== Eliminar hasta aquí en documento a entregar a los alumnos ====

## Proyecto: TO-DO API REST con .NET 10 + Frontend puro (HTML/JS/CSS)

### Contexto

Vas a construir una aplicación **TO-DO** con una **API REST en .NET 10 (C#)** y una interfaz web mínima en **HTML + JavaScript + CSS (sin frameworks)**.

El proyecto se desarrolla en **4 iteraciones**. Cada iteración debe quedar documentada en **Markdown**, y tendrás un documento extra con **pruebas de endpoints** usando **httpie y curl**.

### Objetivos didácticos

* Entender una Web API REST real: rutas, controladores, DTOs, validación, status codes.
* Ver el flujo completo cliente↔servidor: **fetch()** en frontend, JSON, headers, responses.
* Introducir seguridad de forma progresiva: JWT, autorización por propiedad, roles.
=
---

## Iteración 1 — CRUD de tareas + frontend mínimo con panel de debug

### Modelo: Task (Tarea)

Campos mínimos:

* `Id` (int, autoincrement / generado)
* `Title` (string, obligatorio)
* `Description` (string, opcional)
* `CreationDate` (DateTime, automático)
* `DueDate` (DateTime, obligatorio)
* `Status` (enum): `Pending | InProgress | Completed`

### API REST (sin autenticación real todavía)

Endpoints:

* `GET /api/tasks` → listar tareas (filtro opcional `?status=Pending`)
* `GET /api/tasks/{id}` → detalle
* `POST /api/tasks` → crear
* `PUT /api/tasks/{id}` → modificar
* `DELETE /api/tasks/{id}` → borrar

Regla de negocio 1:

* No se pueden crear más de **10 tareas** con `Status = Pending`.

Validación mínima:

* `Title` obligatorio (mín 3 caracteres recomendado)
* `DueDate` obligatorio
* `DueDate >= CreationDate` (si quieres, puede ser validación extra)

Persistencia en Iteración 1:

* **In-memory** (lista en memoria) para ir rápido.

Errores HTTP coherentes:

* 400 (validación)
* 404 (no existe)
* 409 (regla de negocio: límite de pendientes)

### Frontend mínimo (sin frameworks)

Pantallas / componentes:

* Listado de tareas
* Formulario crear/editar
* Botones: ver detalle, editar, borrar

Panel “Debug API” (muy importante):

* Cuadro “Request”: método, URL, headers, body JSON (si aplica)
* Cuadro “Response”: status code, headers relevantes, body JSON

---

## Iteración 2 — Usuarios + autenticación JWT (login / register)

* Entidad `User`: `Id`, `Username`, `Email`, `PasswordHash`, `Role`
* Endpoints:

  * `POST /api/auth/register`
  * `POST /api/auth/login` (devuelve JWT)
* Proteger endpoints de tasks con JWT
* Activar HTTPS en desarrollo (dev-certs)

---

## Iteración 3 — Propiedad: cada usuario solo ve/modifica sus tareas

* Las tareas pertenecen a un usuario (`Task.UserId`)
* `GET /api/tasks` devuelve solo las del usuario autenticado
* `PUT/DELETE` comprueba propiedad → 403 si no es suya

---

## Iteración 4 — Roles (User / Supervisor / Admin)

* User: solo sus tareas
* Supervisor:

  * ver todas
  * editar tareas ajenas **pero NO** marcarlas como Completed
  * NO borrar tareas ajenas
* Admin: control total

---

## Documentación requerida (Markdown)

* `README.md`
* `docs/01_PROJECT_SETUP.md`
* `docs/02_ITERATION_1_API.md`
* `docs/03_ITERATION_1_FRONTEND.md`
* `docs/04_API_TESTING.md` (httpie + curl)
* `docs/05_ITERATION_2_JWT.md`
* `docs/06_ITERATION_3_OWNERSHIP.md`
* `docs/07_ITERATION_4_ROLES.md`

---

# Siguiente paso: estructura del repo + Iteración 1

A partir de aquí, lo hacemos *paso a paso*. Empezamos.

---

## Estructura del repositorio propuesta

```
todo-dotnet/
  README.md
  docs/
    01_PROJECT_SETUP.md
    02_ITERATION_1_API.md
    03_ITERATION_1_FRONTEND.md
    04_API_TESTING.md
    05_ITERATION_2_JWT.md
    06_ITERATION_3_OWNERSHIP.md
    07_ITERATION_4_ROLES.md
  src/
    TodoApi/
      TodoApi.csproj
      Program.cs
      Controllers/
      Models/
      Contracts/
      Repositories/
      wwwroot/
        index.html
        app.js
        styles.css
```

Notas:

* Usamos `wwwroot/` para servir el frontend desde el propio Kestrel (más simple para clase).
* Documentación en `docs/` desde el minuto 1.

---

## Iteración 1 — Paso 1: crear solución y proyecto

### A) Terminal (recomendado para que sea igual en Rider/VSCode)

```bash
mkdir todo-dotnet
cd todo-dotnet

dotnet new sln -n TodoDotNet
dotnet new webapi -n TodoApi
dotnet sln add src/TodoApi/TodoApi.csproj
```

> Si `dotnet new webapi` te crea el proyecto en el root, muévelo a `src/TodoApi/` o crea directamente allí:

```bash
mkdir -p src
cd src
dotnet new webapi -n TodoApi
cd ..
dotnet sln add src/TodoApi/TodoApi.csproj
```

### B) Ejecutar

```bash
cd src/TodoApi
dotnet run
```

Comprueba:

* consola te dirá el puerto
* prueba en navegador:

  * `http://localhost:<puerto>/swagger`

---

## Iteración 1 — Paso 2: preparar `wwwroot` (frontend mínimo)

Crea archivos:

`src/TodoApi/wwwroot/index.html`

```html
<!doctype html>
<html lang="es">
<head>
  <meta charset="utf-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1" />
  <title>TO-DO (API + Frontend puro)</title>
  <link rel="stylesheet" href="styles.css" />
</head>
<body>
  <header>
    <h1>TO-DO</h1>
  </header>

  <main class="grid">
    <section class="panel">
      <h2>Tareas</h2>

      <form id="taskForm">
        <input type="hidden" id="taskId" />
        <label>
          Título
          <input id="title" required minlength="3" />
        </label>

        <label>
          Descripción
          <textarea id="description"></textarea>
        </label>

        <label>
          Fecha límite
          <input id="dueDate" type="datetime-local" required />
        </label>

        <label>
          Estado
          <select id="status">
            <option value="Pending">Pending</option>
            <option value="InProgress">InProgress</option>
            <option value="Completed">Completed</option>
          </select>
        </label>

        <div class="row">
          <button type="submit">Guardar</button>
          <button type="button" id="resetBtn">Nuevo</button>
        </div>
      </form>

      <ul id="taskList"></ul>
    </section>

    <section class="panel">
      <h2>Debug API</h2>

      <div class="debug">
        <div>
          <h3>Request</h3>
          <pre id="reqBox"></pre>
        </div>
        <div>
          <h3>Response</h3>
          <pre id="resBox"></pre>
        </div>
      </div>
    </section>
  </main>

  <script src="app.js"></script>
</body>
</html>
```

`src/TodoApi/wwwroot/styles.css`

```css
body { font-family: system-ui, Arial, sans-serif; margin: 0; }
header { padding: 16px; border-bottom: 1px solid #ddd; }
main.grid { display: grid; grid-template-columns: 1fr 1fr; gap: 16px; padding: 16px; }
.panel { border: 1px solid #ddd; padding: 16px; border-radius: 10px; }
label { display: block; margin: 10px 0; }
input, textarea, select, button { width: 100%; padding: 8px; box-sizing: border-box; }
.row { display: flex; gap: 8px; }
.row button { width: 50%; }
#taskList { list-style: none; padding: 0; }
#taskList li { padding: 8px; border-top: 1px solid #eee; display:flex; justify-content:space-between; gap:8px; }
.debug { display: grid; grid-template-columns: 1fr; gap: 12px; }
pre { background: #f6f6f6; padding: 12px; border-radius: 8px; overflow:auto; }
@media (max-width: 900px) { main.grid { grid-template-columns: 1fr; } }
```

`src/TodoApi/wwwroot/app.js` (sólo esqueleto, por ahora)

```js
const API = "/api/tasks";

const taskList = document.getElementById("taskList");
const form = document.getElementById("taskForm");
const resetBtn = document.getElementById("resetBtn");

const reqBox = document.getElementById("reqBox");
const resBox = document.getElementById("resBox");

function showRequest(method, url, headers, body) {
  reqBox.textContent = JSON.stringify({ method, url, headers, body }, null, 2);
}

function showResponse(status, headers, body) {
  resBox.textContent = JSON.stringify({ status, headers, body }, null, 2);
}

async function refresh() {
  // Iteración 1: cuando exista el endpoint GET /api/tasks lo conectamos aquí
  taskList.innerHTML = "<li>(sin conectar todavía)</li>";
}

form.addEventListener("submit", async (e) => {
  e.preventDefault();
  alert("En el siguiente paso conectaremos POST /api/tasks");
});

resetBtn.addEventListener("click", () => {
  form.reset();
  document.getElementById("taskId").value = "";
});

refresh();
```

---

## Iteración 1 — Paso 3: servir `wwwroot` desde .NET

En `Program.cs`, añade:

```csharp
app.UseDefaultFiles();
app.UseStaticFiles();
```

Colócalo **antes** de `app.MapControllers();`

Ejemplo:

```csharp
app.UseHttpsRedirection();

app.UseDefaultFiles();
app.UseStaticFiles();

app.UseAuthorization();
app.MapControllers();
app.Run();
```

Con esto, al ejecutar:

* `http://localhost:<puerto>/` debe mostrar `index.html`

---

## Lo próximo (paso siguiente inmediato)

En el siguiente paso implementamos **la API mínima**:

1. `Models/TodoTask.cs`
2. `Contracts` (Create/Update/Response DTOs)
3. `Repositories/InMemory`
4. `TasksController` con `GET /api/tasks` y `POST /api/tasks`
5. Conectar `fetch()` en `app.js` y rellenar el panel Debug

==== Eliminar desde aquí en documento a entregar a los alumnos ====
Dime “siguiente” y arrancamos con **Models + DTOs + Repo + Controller (GET+POST)** en Iteración 1.

==== Eliminar hasta aquí en documento a entregar a los alumnos ====

Vamos con **Iteración 1 (API mínima)**: modelo + DTOs + repo in-memory + controller con **GET /api/tasks** y **POST /api/tasks**. Al final conectamos el frontend para listar y crear, mostrando request/response en el panel debug.

---

# Iteración 1 — Paso 4: Modelo + DTOs

## 4.1 Modelo (dominio)

Crea `src/TodoApi/Models/TodoTask.cs`:

```csharp
namespace TodoApi.Models;

public enum TaskStatus
{
    Pending,
    InProgress,
    Completed
}

public class TodoTask
{
    public int Id { get; set; }
    public string Title { get; set; } = string.Empty;
    public string? Description { get; set; }
    public DateTime CreationDate { get; set; }
    public DateTime DueDate { get; set; }
    public TaskStatus Status { get; set; }
}
```

---

## 4.2 DTOs (entrada/salida)

Crea carpeta `src/TodoApi/Contracts/` y estos ficheros:

### `Contracts/TaskCreateDto.cs`

```csharp
using System.ComponentModel.DataAnnotations;
using TodoApi.Models;

namespace TodoApi.Contracts;

public record TaskCreateDto(
    [Required, MinLength(3), MaxLength(120)]
    string Title,

    [MaxLength(2000)]
    string? Description,

    [Required]
    DateTime DueDate,

    TaskStatus Status = TaskStatus.Pending
);
```

### `Contracts/TaskDto.cs`

```csharp
using TodoApi.Models;

namespace TodoApi.Contracts;

public record TaskDto(
    int Id,
    string Title,
    string? Description,
    DateTime CreationDate,
    DateTime DueDate,
    TaskStatus Status
);
```

---

# Iteración 1 — Paso 5: Repositorio in-memory

## 5.1 Interfaz

Crea `src/TodoApi/Repositories/ITasksRepository.cs`:

```csharp
using TodoApi.Models;

namespace TodoApi.Repositories;

public interface ITasksRepository
{
    IEnumerable<TodoTask> GetAll(TaskStatus? status = null);
    TodoTask? GetById(int id);

    TodoTask Add(TodoTask task);

    int CountPending();
}
```

## 5.2 Implementación in-memory

Crea `src/TodoApi/Repositories/TasksInMemoryRepository.cs`:

```csharp
using TodoApi.Models;

namespace TodoApi.Repositories;

public class TasksInMemoryRepository : ITasksRepository
{
    private readonly List<TodoTask> _tasks = new();
    private int _nextId = 1;

    public IEnumerable<TodoTask> GetAll(TaskStatus? status = null)
    {
        var query = _tasks.AsEnumerable();
        if (status is not null)
            query = query.Where(t => t.Status == status);

        // Orden: pendientes primero, y por fecha de creación descendente
        return query
            .OrderBy(t => t.Status == TaskStatus.Completed ? 1 : 0)
            .ThenByDescending(t => t.CreationDate);
    }

    public TodoTask? GetById(int id) => _tasks.FirstOrDefault(t => t.Id == id);

    public TodoTask Add(TodoTask task)
    {
        task.Id = _nextId++;
        _tasks.Add(task);
        return task;
    }

    public int CountPending() => _tasks.Count(t => t.Status == TaskStatus.Pending);
}
```

---

# Iteración 1 — Paso 6: Mapping (DTO ↔ entidad)

Crea carpeta `src/TodoApi/Mapping/` y el fichero:

### `Mapping/TasksMapping.cs`

```csharp
using TodoApi.Contracts;
using TodoApi.Models;

namespace TodoApi.Mapping;

public static class TasksMapping
{
    public static TodoTask ToEntity(this TaskCreateDto dto)
    {
        return new TodoTask
        {
            Title = dto.Title.Trim(),
            Description = string.IsNullOrWhiteSpace(dto.Description) ? null : dto.Description.Trim(),
            CreationDate = DateTime.UtcNow,
            DueDate = dto.DueDate,
            Status = dto.Status
        };
    }

    public static TaskDto ToDto(this TodoTask entity)
    {
        return new TaskDto(
            entity.Id,
            entity.Title,
            entity.Description,
            entity.CreationDate,
            entity.DueDate,
            entity.Status
        );
    }
}
```

---

# Iteración 1 — Paso 7: Controller (GET + POST)

Crea `src/TodoApi/Controllers/TasksController.cs`:

```csharp
using Microsoft.AspNetCore.Mvc;
using TodoApi.Contracts;
using TodoApi.Mapping;
using TodoApi.Models;
using TodoApi.Repositories;

namespace TodoApi.Controllers;

[ApiController]
[Route("api/[controller]")]
public class TasksController : ControllerBase
{
    private const int MAX_PENDING = 10;

    private readonly ITasksRepository _repo;

    public TasksController(ITasksRepository repo)
    {
        _repo = repo;
    }

    // GET /api/tasks?status=Pending
    [HttpGet]
    public ActionResult<IEnumerable<TaskDto>> GetAll([FromQuery] TaskStatus? status)
    {
        var tasks = _repo.GetAll(status).Select(t => t.ToDto());
        return Ok(tasks);
    }

    // POST /api/tasks
    [HttpPost]
    public ActionResult<TaskDto> Create([FromBody] TaskCreateDto request)
    {
        // Regla de negocio: máximo 10 pendientes
        if (request.Status == TaskStatus.Pending && _repo.CountPending() >= MAX_PENDING)
        {
            return Conflict(new
            {
                error = "MAX_PENDING_REACHED",
                message = $"No se pueden crear más de {MAX_PENDING} tareas pendientes."
            });
        }

        // Validación extra docente (opcional pero recomendable):
        // DueDate no puede estar en el pasado “demasiado”.
        // O al menos no antes de la fecha de creación (que es ahora).
        var now = DateTime.UtcNow;
        if (request.DueDate < now)
        {
            return BadRequest(new
            {
                error = "DUE_DATE_INVALID",
                message = "La fecha límite (DueDate) no puede ser anterior a la fecha actual."
            });
        }

        var entity = request.ToEntity();
        var created = _repo.Add(entity);

        return CreatedAtAction(nameof(GetById), new { id = created.Id }, created.ToDto());
    }

    // GET /api/tasks/{id}
    [HttpGet("{id:int}")]
    public ActionResult<TaskDto> GetById(int id)
    {
        var task = _repo.GetById(id);
        if (task is null) return NotFound();

        return Ok(task.ToDto());
    }
}
```

> Nota: he metido también `GET /api/tasks/{id}` porque el `CreatedAtAction` lo necesita y es útil desde ya.

---

# Iteración 1 — Paso 8: Registrar el repositorio en Program.cs

En `src/TodoApi/Program.cs` añade (ANTES de `builder.Build()`):

```csharp
using TodoApi.Repositories;

builder.Services.AddSingleton<ITasksRepository, TasksInMemoryRepository>();
```

Si usas Rider, te añadirá el `using`. Si no, ponlo arriba.

---

# Iteración 1 — Paso 9: Conectar frontend (GET + POST) + debug panel

Edita `wwwroot/app.js` y reemplaza el contenido por este (completo):

```js
const API = "/api/tasks";

const taskList = document.getElementById("taskList");
const form = document.getElementById("taskForm");
const resetBtn = document.getElementById("resetBtn");

const reqBox = document.getElementById("reqBox");
const resBox = document.getElementById("resBox");

function headersToObj(headers) {
  const obj = {};
  for (const [k, v] of headers.entries()) obj[k] = v;
  return obj;
}

function showRequest(method, url, headers, body) {
  reqBox.textContent = JSON.stringify({ method, url, headers, body }, null, 2);
}

function showResponse(status, headers, body) {
  resBox.textContent = JSON.stringify({ status, headers, body }, null, 2);
}

async function apiFetch(method, url, body) {
  const headers = { "Content-Type": "application/json" };
  showRequest(method, url, headers, body ?? null);

  const res = await fetch(url, {
    method,
    headers,
    body: body ? JSON.stringify(body) : undefined
  });

  const resHeaders = headersToObj(res.headers);
  let data = null;

  const text = await res.text();
  try { data = text ? JSON.parse(text) : null; }
  catch { data = text; }

  showResponse(res.status, resHeaders, data);
  if (!res.ok) throw { status: res.status, data };

  return data;
}

function renderTasks(tasks) {
  taskList.innerHTML = "";
  if (!tasks.length) {
    taskList.innerHTML = "<li>(sin tareas)</li>";
    return;
  }

  for (const t of tasks) {
    const li = document.createElement("li");
    li.innerHTML = `
      <div>
        <strong>#${t.id}</strong> ${t.title}
        <div style="font-size: 0.9em; opacity: 0.8;">
          ${t.status} · Due: ${new Date(t.dueDate).toLocaleString()}
        </div>
      </div>
      <div>
        <button data-id="${t.id}" class="detailBtn">Detalle</button>
      </div>
    `;
    taskList.appendChild(li);
  }

  document.querySelectorAll(".detailBtn").forEach(btn => {
    btn.addEventListener("click", async () => {
      const id = btn.getAttribute("data-id");
      await apiFetch("GET", `${API}/${id}`);
    });
  });
}

async function refresh() {
  const tasks = await apiFetch("GET", API);
  renderTasks(tasks);
}

function getFormData() {
  const id = document.getElementById("taskId").value.trim();
  const title = document.getElementById("title").value;
  const description = document.getElementById("description").value;
  const dueDateLocal = document.getElementById("dueDate").value;
  const status = document.getElementById("status").value;

  // datetime-local -> ISO
  const dueDate = dueDateLocal ? new Date(dueDateLocal).toISOString() : null;

  return { id, title, description, dueDate, status };
}

form.addEventListener("submit", async (e) => {
  e.preventDefault();

  const { title, description, dueDate, status } = getFormData();

  const payload = { title, description, dueDate, status };

  try {
    await apiFetch("POST", API, payload);
    form.reset();
    document.getElementById("taskId").value = "";
    await refresh();
  } catch (err) {
    // ya queda mostrado en el panel debug
    console.error(err);
  }
});

resetBtn.addEventListener("click", () => {
  form.reset();
  document.getElementById("taskId").value = "";
});

refresh().catch(console.error);
```

---

# Prueba rápida

1. Ejecuta:

```bash
cd src/TodoApi
dotnet run
```

2. Abre:

* `http://localhost:<puerto>/` (frontend)
* `http://localhost:<puerto>/swagger` (API)

3. Crea tareas desde el formulario y verifica:

* lista se actualiza
* panel “Request/Response” se rellena
* al llegar a 10 pendientes, el POST devuelve 409 con `MAX_PENDING_REACHED`

---
==== Eliminar desde aquí en documento a entregar a los alumnos ====
## Siguiente paso

Implementamos en Iteración 1:

* `PUT /api/tasks/{id}` (editar)
* `DELETE /api/tasks/{id}` (borrar)
* conectar botones “Editar” y “Borrar” en el frontend
* y dejamos lista la documentación `docs/02_ITERATION_1_API.md` + `docs/03_ITERATION_1_FRONTEND.md` + `docs/04_API_TESTING.md` (curl/httpie)

Di “siguiente” y entramos en **PUT/DELETE + UI**.

==== Eliminar hasta aquí en documento a entregar a los alumnos ====
