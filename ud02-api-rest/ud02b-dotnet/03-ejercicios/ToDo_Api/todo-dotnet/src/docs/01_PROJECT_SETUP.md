
# docs/01_PROJECT_SETUP.md

## Configuración inicial del proyecto

### Creación del proyecto
```bash
dotnet new webapi -n TodoApi
cd TodoApi
```

### Paquetes principales
- `Microsoft.EntityFrameworkCore`
- `Microsoft.EntityFrameworkCore.Sqlite`
- `Microsoft.EntityFrameworkCore.Tools`
- `Swashbuckle.AspNetCore`

### Estructura de carpetas
```
TodoApi/
 ├─ Controllers/
 ├─ Services/
 ├─ Repositories/
 ├─ Models/
 ├─ DTOs/
 ├─ Data/
 ├─ Middleware/
 └─ wwwroot/
```

### Program.cs (ideas clave)
- Registrar controllers y Swagger.
- Registrar `DbContext` con SQLite.
- Registrar repositorios y servicios.
- Servir frontend estático con:
```csharp
app.UseDefaultFiles();
app.UseStaticFiles();
```

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