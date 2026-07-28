# docs/03_ITERATION_1_API.md

## Iteracion 1 — API REST de tareas

### Modelo `TodoTask`
Campos principales:
- `Id`
- `Title`
- `Description` (opcional)
- `CreationDate` (auto)
- `DueDate` (obligatorio)
- `Status` (`Pending | InProgress | Completed`)

### Endpoints
- `GET /api/tasks` (filtro opcional `?status=Pending`)
- `GET /api/tasks/{id}`
- `POST /api/tasks`
- `PUT /api/tasks/{id}`
- `DELETE /api/tasks/{id}`

### Reglas de negocio
- Maximo 10 tareas con `Status = Pending`.

### Validaciones
- `Title` obligatorio (min 3, max 100).
- `DueDate` obligatorio.
- `DueDate >= CreationDate`.

### Codigos HTTP usados
- `200 OK`
- `201 Created`
- `204 No Content`
- `400 Bad Request`
- `404 Not Found`
- `409 Conflict`

---

## DTOs (entrada/salida)

Carpeta usada: `TodoApi/DTOs/`.

> Nota: en .NET se usa mucho el termino **DTO**; en otros ecosistemas se puede ver **Contracts** para la misma idea.

### TaskCreateDto
- `Title`
- `Description`
- `DueDate`
- `Status`

### TaskUpdateDto
- `Title`
- `Description`
- `DueDate`
- `Status`

### TaskReadDto
- `Id`
- `Title`
- `Description`
- `CreationDate`
- `DueDate`
- `Status`

---

## Mapping (DTO <-> entidad)

Archivo: `TodoApi/Mapping/TasksMapping.cs`

- `ToEntity(TaskCreateDto)` crea entidad con `CreationDate = DateTime.UtcNow`.
- `ToReadDto(TodoTask)` para respuestas de API.

---

## Repositorio y servicio

Interfaces:
- `TodoApi/Repositories/ITasksRepository.cs`
- `TodoApi/Services/ITasksService.cs`

Notas:
- `GetAll` admite filtro por `Status`.
- Regla de negocio de 10 pendientes se aplica en repositorio.
