# docs/06_ITERATION_3_OWNERSHIP.md

## Iteracion 3 — Propiedad de tareas

### Objetivo
Restringir el acceso a tareas para que **cada usuario solo vea y modifique las suyas**.

---

## Reglas funcionales

- Cada tarea pertenece a un usuario (`TodoTask.UserId`).
- `GET /api/tasks` devuelve solo tareas del usuario autenticado.
- `GET /api/tasks/{id}` solo si la tarea es del usuario.
- `PUT /api/tasks/{id}` y `DELETE /api/tasks/{id}` solo si la tarea es del usuario.
- Si no es suya → `403 Forbidden`.

---

## Decisiones de diseno

- La validacion de propiedad se hace en **Service**, no en Controller.
- El controlador solo obtiene el `userId` desde el token y delega.
- No se implementan roles todavia (eso es Iteracion 4).

---

## Cambios previstos en codigo

### Service
- `TasksService` debe filtrar por `userId`.
- Al obtener por id, comprobar `UserId`.

### Repository
- Nuevo metodo `GetAllByUser(int userId, TaskStatus? status)`.
- Nuevo metodo `GetByIdForUser(int id, int userId)` o verificacion en servicio.

### Controller
- Extraer `userId` desde claims (`sub`).
- Pasar `userId` al servicio en todas las operaciones.

---

## Errores esperados

- `401 Unauthorized` si no hay token.
- `403 Forbidden` si la tarea no pertenece al usuario.
- `404 Not Found` si la tarea no existe.

---

## Pruebas minimas (manuales)

1. Usuario A crea tarea.
2. Usuario B intenta leerla → `403`.
3. Usuario A la edita → `204`.
4. Usuario B intenta borrarla → `403`.

---

## Checklist (Iteracion 3)

### Por hacer (codigo)
- [x] Ajustar repositorio para filtrar por usuario.
- [x] Ajustar servicio para validar propiedad.
- [x] Ajustar controlador para pasar `userId`.

### Por hacer (tests)
- [x] Tests de acceso a tareas propias.
- [x] Tests de acceso a tareas ajenas → `403`.

### Por hacer (documentacion)
- [x] Añadir pruebas de propiedad en `docs/04_API_TESTING.md`.
