# docs/07_ITERATION_4_ROLES.md

## Iteracion 4 - Roles (User / Supervisor / Admin)

### Objetivo
Aplicar autorizacion por rol sobre tareas, manteniendo la regla de propiedad de Iteracion 3 para `User`.

### Reglas funcionales
- `User`: solo sus tareas.
- `Supervisor`:
- ver todas las tareas.
- editar tareas ajenas.
- no puede marcar como `Completed` tareas ajenas.
- no puede borrar tareas ajenas.
- `Admin`: control total.

### Implementado
- `TasksService` aplica reglas por rol en `GetAll`, `GetById`, `Update`, `Delete`.
- `TasksController` extrae `UserRole` desde claim `role` y delega al servicio.
- Nueva excepcion `ForbiddenException` para respuestas `403`.
- `ErrorHandlingMiddleware` devuelve `403` para accesos prohibidos.

### Pruebas automatizadas
- Supervisor ve todas (`GetAll` con rol Supervisor).
- Supervisor no puede completar tarea ajena.
- Supervisor no puede borrar tarea ajena.
- Admin puede borrar tarea ajena.
- Tests HTTP de integracion para `401/403/204` por rol.

### Checklist
#### Hecho
- [x] Reglas por rol implementadas en servicio.
- [x] Controller adaptado para pasar rol.
- [x] Tests de rol añadidos y en verde.

#### Por hacer
- [x] Documentar pruebas manuales de Iteracion 4 en `docs/04_API_TESTING.md`.
