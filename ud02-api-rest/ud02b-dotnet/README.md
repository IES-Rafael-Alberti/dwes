# UD2b - Referencia opcional de .NET

Esta carpeta ofrece una comparación puntual con ASP.NET Core. No forma parte de
la ruta obligatoria ni de la evaluación de UD2: el itinerario canónico sigue
siendo Spring Boot con Battleship.

## Demostración verificable

La única demostración recomendada es [TodoApi](03-ejercicios/ToDo_Api/todo-dotnet/src/README.md).
Incluye API REST por capas, EF Core con SQLite, autenticación JWT, frontend
estático y 26 pruebas automatizadas sobre .NET 10.

```bash
cd 03-ejercicios/ToDo_Api/todo-dotnet/src
dotnet test TodoDotNet.sln
dotnet run --project TodoApi
```

## Material histórico

- `03-ejercicios/MiApi/` conserva el template mínimo WeatherForecast sobre
  .NET 8. Sirve para reconocer la estructura inicial, no como segunda demo.
- `03-ejercicios/RecetasApi/` conserva una evolución anterior de acceso a datos
  y documentación extensa. No está mantenida como ruta reproducible.
- `03-ejercicios/Tareas/` contiene una guía histórica de despliegue.

Este material puede consultarse para comparar tecnologías, pero no sustituye
las actividades, RA/CE ni rúbricas de `ud02a-spring-boot`.
