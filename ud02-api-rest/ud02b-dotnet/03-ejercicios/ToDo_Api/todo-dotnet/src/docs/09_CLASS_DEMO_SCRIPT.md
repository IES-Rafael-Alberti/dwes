# docs/09_CLASS_DEMO_SCRIPT.md

## Guia de practica - Flujo completo API + Frontend

### Objetivo
Seguir un recorrido corto y ordenado para comprobar que:
- la API funciona,
- la autenticacion JWT funciona,
- y las reglas de autorizacion (propiedad y roles) se cumplen.

Duracion orientativa: 10-15 minutos.

---

## 1) Arranque del proyecto

Desde `todo-dotnet/src`:

```bash
dotnet run --project TodoApi/TodoApi.csproj
```

Abre:
- Frontend: `http://localhost:<puerto>/`
- Swagger UI: `http://localhost:<puerto>/swagger`

---

## 2) Comprobacion rapida en Swagger

1. Localiza endpoints de `Auth` y `Tasks`.
2. Prueba `POST /api/auth/register`.
3. Prueba `POST /api/auth/login`.
4. Copia el token recibido.

Resultado esperado:
- registro y login responden `200 OK`.
- se devuelve `token` JWT.

---

## 3) Flujo en frontend (cliente HTTP real)

1. Haz login desde el panel de autenticacion.
2. Activa `Usar token en requests`.
3. Crea una tarea desde el formulario.
4. Revisa panel de request:
- metodo, URL, cabeceras y body.
5. Revisa panel de response:
- status code y JSON.

Resultado esperado:
- `POST /api/tasks` responde `201 Created`.
- la tarea aparece al listar.

---

## 4) Probar seguridad basica (`401` y `403`)

1. Desactiva token y llama `GET /api/tasks`.
- esperado: `401 Unauthorized`.
2. Usa token de otro usuario sobre tarea ajena.
- esperado: `403 Forbidden`.

Interpretacion:
- `401`: no autenticado.
- `403`: autenticado, pero sin permiso.

---

## 5) Probar reglas por rol

Con usuarios de prueba (`User`, `Supervisor`, `Admin`):

1. `Supervisor` lista tareas.
- esperado: puede ver tareas de otros usuarios.
2. `Supervisor` intenta completar tarea ajena.
- esperado: `403 Forbidden`.
3. `Supervisor` intenta borrar tarea ajena.
- esperado: `403 Forbidden`.
4. `Admin` borra tarea ajena.
- esperado: `204 No Content`.

---

## 6) Validar con tests automatizados

Desde `todo-dotnet/src`:

```bash
dotnet test TodoApi.Tests/TodoApi.Tests.csproj
dotnet test TodoApi.Tests/TodoApi.Tests.csproj --filter "FullyQualifiedName~Integration"
```

Resultado esperado:
- tests unitarios en verde.
- tests de integracion en verde.

---

## 7) Evidencias recomendadas de entrega

- Captura de frontend con request/response.
- Captura de Swagger con login correcto.
- Salida de tests en verde.
- Breve explicacion de un caso `401` y un caso `403`.

