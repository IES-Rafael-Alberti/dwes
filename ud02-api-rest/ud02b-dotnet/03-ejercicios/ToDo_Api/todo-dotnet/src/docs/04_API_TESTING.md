# docs/04_API_TESTING.md

## Pruebas de la API (Iteracion 1)

### Usando curl

#### Crear tarea
```bash
curl -X POST http://localhost:5000/api/tasks \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Primera tarea",
    "description": "Ejemplo",
    "dueDate": "2026-12-31T23:59:00Z",
    "status": "Pending"
  }'
```

#### Listar tareas
```bash
curl http://localhost:5000/api/tasks
```

#### Filtrar por estado
```bash
curl "http://localhost:5000/api/tasks?status=Pending"
```

#### Borrar tarea
```bash
curl -X DELETE http://localhost:5000/api/tasks/1
```

### Usando httpie

#### Crear tarea
```bash
http POST :5000/api/tasks title="Tarea httpie" dueDate="2026-12-31T23:59:00Z" status=Pending
```

#### Listar tareas
```bash
http GET :5000/api/tasks
```

### Casos de error a probar
- Crear mas de 10 tareas `Pending` → `409 Conflict`.
- Acceder a una tarea inexistente → `404 Not Found`.
- Enviar datos invalidos → `400 Bad Request`.

---

## Pruebas de la API (Iteracion 2)

> Nota: requiere que la autenticacion JWT este implementada.

### Usando curl

#### Registro
```bash
curl -X POST http://localhost:5000/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "ana",
    "email": "ana@example.com",
    "password": "123456"
  }'
```

#### Login
```bash
curl -X POST http://localhost:5000/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "ana@example.com",
    "password": "123456"
  }'
```

#### Acceso a tasks con token
```bash
curl http://localhost:5000/api/tasks \
  -H "Authorization: Bearer <jwt>"
```

### Usando httpie

#### Registro
```bash
http POST :5000/api/auth/register username=ana email=ana@example.com password=123456
```

#### Login
```bash
http POST :5000/api/auth/login email=ana@example.com password=123456
```

#### Acceso a tasks con token
```bash
http GET :5000/api/tasks "Authorization:Bearer <jwt>"
```

### Casos de error a probar
- Acceder a `/api/tasks` sin token → `401 Unauthorized`.
- Login con password incorrecto → `401 Unauthorized`.

---

## Pruebas de la API (Iteracion 3)

> Nota: requiere dos usuarios autenticados (A y B).

### Flujo de propiedad (curl)

#### 1) Usuario A crea una tarea
```bash
curl -X POST http://localhost:5000/api/tasks \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <jwt_usuario_A>" \
  -d '{
    "title": "Tarea privada de A",
    "description": "Solo A puede tocarla",
    "dueDate": "2026-12-31T23:59:00Z",
    "status": "Pending"
  }'
```

#### 2) Usuario B intenta leer esa tarea
```bash
curl http://localhost:5000/api/tasks/1 \
  -H "Authorization: Bearer <jwt_usuario_B>"
```
Esperado: `403 Forbidden`.

#### 3) Usuario A la edita
```bash
curl -X PUT http://localhost:5000/api/tasks/1 \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <jwt_usuario_A>" \
  -d '{
    "title": "Tarea privada de A (editada)",
    "description": "Editada por su propietario",
    "dueDate": "2026-12-31T23:59:00Z",
    "status": "InProgress"
  }'
```
Esperado: `204 No Content`.

#### 4) Usuario B intenta borrarla
```bash
curl -X DELETE http://localhost:5000/api/tasks/1 \
  -H "Authorization: Bearer <jwt_usuario_B>"
```
Esperado: `403 Forbidden`.

### Casos de error a probar
- Acceder a tarea ajena por `GET /api/tasks/{id}` → `403 Forbidden`.
- Editar tarea ajena por `PUT /api/tasks/{id}` → `403 Forbidden`.
- Borrar tarea ajena por `DELETE /api/tasks/{id}` → `403 Forbidden`.

---

## Pruebas de la API (Iteracion 4)

> Nota: requiere tokens de un `User`, un `Supervisor` y un `Admin`.

### Escenarios de rol (curl)

#### 1) Supervisor lista todas las tareas
```bash
curl http://localhost:5000/api/tasks \
  -H "Authorization: Bearer <jwt_supervisor>"
```
Esperado: ve tareas propias y ajenas.

#### 2) Supervisor edita tarea ajena sin completar
```bash
curl -X PUT http://localhost:5000/api/tasks/1 \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <jwt_supervisor>" \
  -d '{
    "title": "Ajuste supervisor",
    "description": "Edicion permitida",
    "dueDate": "2026-12-31T23:59:00Z",
    "status": "InProgress"
  }'
```
Esperado: `204 No Content`.

#### 3) Supervisor intenta completar tarea ajena
```bash
curl -X PUT http://localhost:5000/api/tasks/1 \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <jwt_supervisor>" \
  -d '{
    "title": "No permitido",
    "description": "Intento de completar ajena",
    "dueDate": "2026-12-31T23:59:00Z",
    "status": "Completed"
  }'
```
Esperado: `403 Forbidden`.

#### 4) Supervisor intenta borrar tarea ajena
```bash
curl -X DELETE http://localhost:5000/api/tasks/1 \
  -H "Authorization: Bearer <jwt_supervisor>"
```
Esperado: `403 Forbidden`.

#### 5) Admin borra tarea ajena
```bash
curl -X DELETE http://localhost:5000/api/tasks/1 \
  -H "Authorization: Bearer <jwt_admin>"
```
Esperado: `204 No Content`.
