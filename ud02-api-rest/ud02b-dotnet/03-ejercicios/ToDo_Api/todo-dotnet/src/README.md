# TodoApi - demostración opcional de ASP.NET Core

Esta es la demostración .NET verificable de UD2. Permite comparar ASP.NET Core
con la ruta canónica Spring Boot/Battleship, pero no es obligatoria ni evaluable.
Incluye una API REST en C#, un frontend estático y una arquitectura por capas:
controlador -> servicio -> repositorio.

## Requisitos y verificación

- SDK de .NET 10.
- No requiere una base de datos externa: usa SQLite local.
- El archivo `TodoApi/todo.db` se genera localmente y no se versiona.

Desde esta carpeta:

```bash
dotnet test TodoDotNet.sln
dotnet run --project TodoApi
```

La verificación esperada es de 26 pruebas superadas. La aplicación expone el
frontend en la URL indicada por `dotnet run` y Swagger en `/swagger` cuando se
ejecuta en modo desarrollo.

## Estructura basica

- `TodoApi/Program.cs`: arranque de la aplicacion y registro de dependencias.
- `TodoApi/Controllers/TasksController.cs`: endpoints HTTP (GET/POST/PUT/DELETE).
- `TodoApi/Services/`: reglas de negocio y mapeo entre modelos y DTOs.
- `TodoApi/Repositories/`: acceso a datos mediante EF Core y SQLite.
- `TodoApi/Models/`: modelos internos del servidor.
- `TodoApi/DTOs/`: objetos que viajan por la API.
- `TodoApi/wwwroot/`: HTML/CSS/JS estaticos servidos por el servidor.

## Flujo de una peticion

1) El navegador o cliente HTTP hace una peticion a `/api/tasks`.
2) El controlador recibe la peticion y llama al servicio.
3) El servicio aplica la logica y pide datos al repositorio.
4) El repositorio consulta SQLite mediante EF Core.
5) El servicio convierte el modelo interno a un DTO de salida.
6) El controlador devuelve la respuesta HTTP.

## Endpoints principales

- `GET /api/tasks`: lista todas las tareas.
- `GET /api/tasks/{id}`: obtiene una tarea por id.
- `POST /api/tasks`: crea una tarea.
- `PUT /api/tasks/{id}`: actualiza una tarea completa.
- `DELETE /api/tasks/{id}`: borra una tarea.
