# TodoApi (introduccion para estudiantes)

Este proyecto es un ejemplo pequeno de una API REST en ASP.NET Core (C#) con un frontend estatico en `wwwroot`.
La idea es mostrar una arquitectura por capas sencilla: controlador -> servicio -> repositorio.

## Estructura basica

- `TodoApi/Program.cs`: arranque de la aplicacion y registro de dependencias.
- `TodoApi/Controllers/TasksController.cs`: endpoints HTTP (GET/POST/PUT/DELETE).
- `TodoApi/Services/`: reglas de negocio y mapeo entre modelos y DTOs.
- `TodoApi/Repositories/`: acceso a datos (en memoria, sin base de datos).
- `TodoApi/Models/`: modelos internos del servidor.
- `TodoApi/DTOs/`: objetos que viajan por la API.
- `TodoApi/wwwroot/`: HTML/CSS/JS estaticos servidos por el servidor.

## Flujo de una peticion

1) El navegador o cliente HTTP hace una peticion a `/api/tasks`.
2) El controlador recibe la peticion y llama al servicio.
3) El servicio aplica la logica y pide datos al repositorio.
4) El repositorio devuelve los datos en memoria.
5) El servicio convierte el modelo interno a un DTO de salida.
6) El controlador devuelve la respuesta HTTP.

## Endpoints principales

- `GET /api/tasks`: lista todas las tareas.
- `GET /api/tasks/{id}`: obtiene una tarea por id.
- `POST /api/tasks`: crea una tarea.
- `PUT /api/tasks/{id}`: actualiza una tarea completa.
- `DELETE /api/tasks/{id}`: borra una tarea.

## Ejecutar en local

Desde la carpeta `TodoApi`:

```bash
dotnet run
```

Si esta en modo desarrollo, se habilita Swagger en `/swagger` para probar la API.
