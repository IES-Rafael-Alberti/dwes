# API REST Recetas en .NET 10 (C#)

Guía paso a paso con código completo.

## Enunciado: API REST “Recetas”

### 1) Contexto y objetivo

Se va a desarrollar una **API REST (ASP.NET Core Web API) en .NET 10 (C#)**.  
El objetivo es construir un backend sencillo para **crear, consultar y reutilizar recetas**, incorporando control de autoría, interacción social (likes y comentarios) y una política de “histórico” cuando una receta popular es eliminada por su autor.

### 2) Roles y permisos (reglas de negocio)

**Usuarios registrados** (no se contempla rol administrador en esta primera versión, salvo que se indique más adelante):

- Un usuario puede:
    
    - **Crear** recetas propias.
        
    - **Modificar** y **eliminar** únicamente sus recetas.
        
    - **Comentar** recetas de otros usuarios.
        
    - **Dar like** a recetas de otros usuarios (y opcionalmente retirar el like).
        
    - **Copiar** recetas de otros usuarios (ver reglas de copia).
        
- Un usuario **no puede**:
    
    - Modificar ni eliminar recetas que no sean suyas.
        
    - Borrar comentarios de otros (salvo que se defina lo contrario más adelante).
        

### 3) Modelo de receta

Cada **receta** contiene, como mínimo:

- **Título**
    
- **Autor** (usuario creador)
    
- **Ingredientes**: lista de elementos `{ ingrediente, cantidad }`
    
    - `ingrediente`: nombre (texto)
        
    - `cantidad`: texto libre o estructura (por ejemplo “200 g”, “1 cucharada”, etc.)
        
- **Pasos**: lista ordenada de pasos
    
    - Cada paso incluye:
        
        - `descripcion` (texto)
            
        - `duracionEstimadaMin` (entero o decimal, en minutos; si no se conoce, puede ser 0 o null)
            
- Metadatos recomendables (para el ejemplo didáctico):
    
    - `createdAt`, `updatedAt`
        
    - Contadores: `likesCount`, `commentsCount`
        
    - Estado: `ACTIVA` / `HISTORICA` (ver apartado 6)
        

### 4) Likes

- Un usuario puede dar **un único like por receta**.
    
- El sistema debe impedir duplicados.
    
- (Opcional para la práctica) permitir “unlike”.
    

### 5) Comentarios

- Un comentario pertenece a:
    
    - una receta
        
    - un autor (usuario)
        
    - un texto
        
    - timestamps
        
- Por defecto, se permite que cualquier usuario comente recetas ajenas.
    

### 6) Copias de recetas (reutilización con trazabilidad)

Se permite crear una receta nueva tomando como base otra receta existente.

Reglas:

1. Al copiar, se crea una **nueva receta** cuyo autor es el usuario que copia.
    
2. La receta copiada mantiene un enlace de procedencia:
    
    - `originalRecipeId` (la receta de la que proviene)
        
    - `originalAuthorId` (autor de la original)
        
3. El **título inicial** se genera automáticamente con un prefijo del estilo:
    
    - `"Copia de '<Título original>' (de <Autor original>)"`
        
4. El usuario puede **cambiar el título**, pero mientras la receta siga siendo “sustancialmente similar” a la original, debe conservarse una marca visible de que es una copia.
    
5. Cuando existan **cambios sustanciales** respecto a la original, la receta puede dejar de mostrarse como “copia”.
    

**Nota importante (para concretar más adelante):**  
“Cambios sustanciales” necesita una definición objetiva para implementarlo (por ejemplo: cambio de X% de ingredientes o pasos, o un flag manual de “desvincular” con validación). De momento queda como requisito de negocio a concretar en la implementación.

### 7) Eliminación y “Histórico de recetas”

Cuando un autor elimina una receta:

- Si la receta **no es popular**, se elimina (borrado lógico o físico, según decidáis).
    
- Si la receta tiene “muchos likes”, **no desaparece**: pasa a un **histórico** donde:
    
    - Sigue siendo **consultable**.
        
    - Sigue siendo **copiable**.
        
    - Ya no puede recibir modificaciones (ni por su autor original, al estar eliminada).
        
    - Se conserva su autoría y trazabilidad.
        

**Nota importante (para concretar más adelante):**

- “Muchos likes” debe definirse con un umbral: por ejemplo `likesCount >= 50` (o el valor que decidáis).
    
- También conviene decidir si el histórico es:
    
    - un estado de la receta (`HISTORICA`) con borrado lógico, o
        
    - un recurso separado (tabla/colección distinta).
        

### 8) Alcance didáctico (lo que se busca practicar)

Este proyecto está pensado para practicar:

- Diseño de recursos REST y endpoints (CRUD + acciones).
    
- Autorización por propietario (owner-based authorization).
    
- Relaciones 1-N y N-N (recetas-ingredientes/pasos, recetas-likes, recetas-comentarios).
    
- Reglas de negocio con estados (activa/histórica).
    
- Trazabilidad de copias (receta derivada de otra).
    

---
## 1. Instalación de .NET SDK 10

### 1.1 Comprobación previa

```bash
dotnet --info
```

Si no aparece **.NET SDK 10.x**, hay que instalarlo.

---

## 1.2 Linux (Ubuntu 22.04 y derivados)

### Opción A — Repositorio oficial de Microsoft (cuando esté disponible)

*(Puede no ofrecer SDK 10 en algunos derivados)*

```bash
sudo apt update
sudo apt install dotnet-sdk-10.0
```

Si el paquete **no existe**, pasar a la opción B.

---

### Opción B — Backports de .NET (recomendada para 22.04 cuando no aparece el SDK 10)

Esta es la opción **soportada y estable** cuando el SDK 10 no está disponible en los repositorios estándar del sistema.

```bash
sudo add-apt-repository ppa:dotnet/backports
sudo apt update
sudo apt install dotnet-sdk-10.0
```

Verificación:

```bash
dotnet --version
dotnet --info
```

Resultado esperado:

```text
10.0.x
```

**Ventajas de esta opción**

* No mezcla repositorios de otras versiones de Ubuntu o Debian
* Funciona correctamente fuera del IDE
* Reproducible en entornos docentes
* Compatible con Rider, VS Code, Docker y CI/CD

---

### Opción C — Script oficial `dotnet-install` (alternativa neutral)

Recomendada si:

* no se puede usar APT
* no se tienen permisos de administrador
* se quieren varias versiones de .NET en paralelo

```bash
curl -fsSL https://dot.net/v1/dotnet-install.sh | bash -s -- --channel 10.0
```

Añadir al `PATH` (por ejemplo en `~/.bashrc`):

```bash
export DOTNET_ROOT="$HOME/.dotnet"
export PATH="$HOME/.dotnet:$PATH"
```

---

### Opción NO recomendada

❌ Añadir repositorios de:

* Ubuntu 24.04
* Debian 12

en un sistema basado en Ubuntu 22.04.

Motivo:

* Riesgo real de conflictos de dependencias
* Difícil de replicar en el alumnado
* Poco mantenible a medio plazo

---

## 1.3 Windows

Descargar e instalar **.NET SDK 10.x** (no solo runtime):

[https://dotnet.microsoft.com/download/dotnet/10.0](https://dotnet.microsoft.com/download/dotnet/10.0)

Verificar en PowerShell o CMD:

```powershell
dotnet --info
```

---

## Nota importante sobre IDEs (Rider / VS Code)

* El SDK debe estar **instalado a nivel sistema o usuario**
* El IDE **no debe ser el responsable** de instalar .NET
* Rider y VS Code deben apuntar al `dotnet` real del sistema

Esto garantiza que:

* `dotnet run` funciona en terminal
* el proyecto es portable
* no hay dependencias ocultas del IDE

---

# Creación del proyecto API REST (.NET 10 – C#)

## 0. Premisas importantes
1. **El proyecto se crea con la terminal**, no con el IDE.
2. El IDE **solo abre, ejecuta y depura**. 
3. Si funciona con `dotnet run`, funcionará en:
    - Rider
    - VS Code 
    - Eclipse
    - CI/CD
    - Docker

---

## 1. Creación del proyecto **sin IDE** (terminal)

### 1.1 Crear carpeta de trabajo

```bash
mkdir RecetasApi
cd RecetasApi
```

---

### 1.2 Crear la solución

```bash
dotnet new sln -n RecetasApi
```

Resultado:

```text
RecetasApi.sln
```

---

### 1.3 Crear el proyecto Web API
Usamos **Controllers** (más claro para FP y más cercano a Spring/Laravel).

```bash
dotnet new webapi -n RecetasApi.Api --use-controllers
```

Esto crea:
- Swagger
- Controllers
- Program.cs clásico

---

### 1.4 Añadir el proyecto a la solución

```bash
dotnet sln add RecetasApi.Api/RecetasApi.Api.csproj
```

---

### 1.5 Ejecutar y comprobar

```bash
cd RecetasApi.Api
dotnet run
```

Salida típica:

```text
Now listening on: http://localhost:5000
```

Abrir:

```
http://localhost:5000/swagger
```

✔️ Si Swagger se ve → el proyecto está bien creado.

---
Nota: para .net 10

La plantilla generada **no está usando Swagger UI** (ni `AddSwaggerGen`/`UseSwaggerUI`). Está usando el **nuevo mecanismo de OpenAPI** (`AddOpenApi` + `MapOpenApi`) que **solo publica el documento OpenAPI** (normalmente en `/openapi/v1.json`) y, además, **solo en Development**. Por eso `/swagger` devuelve 404: **esa ruta no existe**.

## Qué tienes ahora mismo

- En **Development**:
    
    - Se publica el documento OpenAPI en una ruta tipo:
        
        - `GET /openapi/v1.json` (lo más habitual)
            
- No hay UI de Swagger en `/swagger` a menos que la añadas.
    

### 1) Prueba rápida (sin tocar nada)

Arranca en Development y prueba:

- `http://localhost:5154/openapi/v1.json`
    

Si tu entorno no es Development, fuerza:

```bash
ASPNETCORE_ENVIRONMENT=Development dotnet run
```

Si eso responde JSON, todo está correcto: simplemente **no hay Swagger UI**.

---

## Opción A (recomendada para alumnos): activar Swagger UI “clásico”

Es lo más familiar y práctico.

### A.1 Instala el paquete (Swashbuckle)

En la carpeta del proyecto (`RecetasApi.Api`):

```bash
dotnet add package Swashbuckle.AspNetCore
```

### A.2 Sustituye `Program.cs` por esta versión

(Dejo tu estructura intacta y añado Swagger UI.)

```csharp
var builder = WebApplication.CreateBuilder(args);

builder.Services.AddControllers();

// Swagger (OpenAPI) + UI
builder.Services.AddEndpointsApiExplorer();
builder.Services.AddSwaggerGen();

var app = builder.Build();

if (app.Environment.IsDevelopment())
{
    app.UseSwagger();
    app.UseSwaggerUI(); // /swagger
}

app.UseHttpsRedirection();

app.UseAuthorization();

app.MapControllers();

app.Run();
```

### A.3 Ejecuta y prueba

- `http://localhost:5154/swagger`
    

---

## Opción B: mantener `AddOpenApi()` y añadir UI compatible

Se puede, pero para una visión rápida aporta menos y es más lioso (porque necesitas una UI que apunte al JSON). Para vuestro caso, **mejor Opción A** y listo.

---

## Nota importante sobre el puerto 5000... 5154
En teoría se usa el puerto 5000 en la práctica puede ser otro, en mi caso 5154. Tu puerto real es el que salga en consola (“Now listening on …”). `launchSettings.json` suele asignar puertos aleatorios.



---

### 1.6 Estructura inicial

```text
RecetasApi/
├── RecetasApi.sln
└── RecetasApi.Api/
    ├── Controllers/
    │   └── WeatherForecastController.cs
    ├── Program.cs
    ├── appsettings.json
    └── RecetasApi.Api.csproj
```

> `WeatherForecastController` se usará solo como prueba inicial.  
> Se eliminará más adelante.


Voy a estructurarlo como **tres vías equivalentes**, pero dejando explícito que **internamente hacen lo mismo**.

---

# Creación del proyecto API REST (.NET 10 – C#)

## Idea clave

> Existen **tres formas de crear el proyecto**, pero **todas acaban generando exactamente lo mismo**:
> un proyecto creado con `dotnet new webapi`.

---

## 1A. Creación del proyecto **sin IDE (terminal)** ✅ *recomendada*

*(Ya descrita; se mantiene como referencia y “plan B”)*

---

## 1B. Creación del proyecto **con JetBrains Rider** (sin tocar la terminal)

![Image](https://blog.jetbrains.com/wp-content/uploads/2019/11/dotnet-aspcore-new-project-dialog.png)

![Image](https://blog.jetbrains.com/wp-content/uploads/2017/02/dotnet-multiple-projects-opened.png)

![Image](https://i.sstatic.net/Yy9JV.png)

### 1B.1 Nuevo proyecto

1. Abrir **Rider**
2. **New Solution**
3. Tipo de proyecto:

   * **ASP.NET Core Web API**
4. Lenguaje:

   * **C#**
5. Framework:

   * **.NET 10.0**

---

### 1B.2 Opciones importantes (pantalla de configuración)

Seleccionar:

* ✅ **Use controllers**
* ✅ **Enable OpenAPI support (Swagger)**

No seleccionar:

* ❌ Authentication (de momento)
* ❌ HTTPS (se puede activar después)
* ❌ Docker (más adelante)

---

### 1B.3 Nombre y ubicación

* Solution name: `RecetasApi`
* Project name: `RecetasApi.Api`
* Location: carpeta de trabajo habitual

Rider crea:

* `.sln`
* `.csproj`
* estructura Web API completa

---

### 1B.4 Verificación (sin saber terminal)

* Pulsar **Run ▶**
* Abrir navegador:

  ```
  http://localhost:5000/swagger
  ```

Si Swagger aparece → proyecto correcto.

---

### 1B.5 Comprobación importante (para el profesor)

En:

```
Settings → Build, Execution, Deployment → Toolset and Build
```

Verificar que:

* Rider usa **dotnet del sistema**
* SDK = **10.x**
* No un SDK interno “descargado por Rider”

---

## 1C. Creación del proyecto **con VS Code** (mínima terminal)

![Image](https://learn.microsoft.com/en-us/visualstudio/get-started/csharp/media/vs-2022/csharp-create-new-project-aspnet-core.png?view=visualstudio)

![Image](https://devblogs.microsoft.com/visualstudio/wp-content/uploads/sites/4/2023/06/3ddevkit.png)

![Image](https://learn.microsoft.com/en-us/aspnet/core/tutorials/first-web-api/_static/9/http-file-window-with-response-vs17.13.0.png?view=aspnetcore-10.0)

### 1C.1 Extensiones necesarias

Instalar:

* **C# Dev Kit**
* **C#**

---

### 1C.2 Crear proyecto desde el asistente

1. `Ctrl + Shift + P`
2. **.NET: New Project**
3. Seleccionar:

   * **ASP.NET Core Web API**
4. Framework:

   * **.NET 10.0**
5. Project name:

   * `RecetasApi.Api`
6. Carpeta:

   * crear/seleccionar `RecetasApi`

---

### 1C.3 Opciones del asistente

Elegir:

* ✅ Controllers
* ✅ OpenAPI / Swagger
* ❌ Authentication
* ❌ Docker

---

### 3.4 Ejecución (sin escribir comandos)

* VS Code muestra botón **Run**
* O `Run → Start Debugging`
* Navegador:

  ```
  http://localhost:5000/swagger
  ```

---

## 4. ¿Qué pasa “por debajo”?

Mensaje tranquilizador para los alumnos:

> Rider y VS Code **están ejecutando `dotnet new webapi`**
> aunque tú no lo veas.

Por eso:

* El proyecto se puede abrir en cualquier IDE
* Funciona igual en Linux y Windows
* No depende de un entorno concreto

---

## 5. Comparativa rápida (para clase)

| Método         | Terminal | IDE     | Recomendado |
| -------------- | -------- | ------- | ----------- |
| `dotnet new`   | Sí       | No      | ⭐⭐⭐⭐⭐       |
| Rider wizard   | No       | Rider   | ⭐⭐⭐⭐        |
| VS Code wizard | Poco     | VS Code | ⭐⭐⭐         |

---

## 6. Mensaje pedagógico importante

> **Aprender a usar la terminal es recomendable**,
> pero **no es un requisito** para entender una API REST ni C#.

El proyecto:

* se crea una vez
* se usa meses
* y el código es idéntico

---

## Siguiente paso (ahora sí)

Una vez **todos** tengan el proyecto creado, el siguiente bloque será:

1. Limpieza del proyecto base
2. Eliminación de `WeatherForecast`
3. Primer `RecetasController`
4. Primer endpoint real

Cuando quieras, pasamos a eso.

---

## 2. Abrir el proyecto con **JetBrains Rider 2025.x**

![Image](https://blog.jetbrains.com/wp-content/uploads/2017/02/dotnet-multiple-projects-opened.png)

![Image](https://rider-support.jetbrains.com/hc/user_images/J_XmKkQXG4STTYBXeIGKJg.png)

![Image](https://resources.jetbrains.com/help/img/rider/2025.3/launch_settings_edit_configuration.png)

### 2.1 Abrir proyecto existente

1. Rider → **Open**
    
2. Seleccionar la carpeta `RecetasApi`
    
3. Rider detecta automáticamente `RecetasApi.sln`
    

---

### 2.2 Verificar SDK usado por Rider

`Settings → Build, Execution, Deployment → Toolset and Build`

- .NET CLI executable:
    
    - Linux: `/usr/bin/dotnet`
        
    - Windows: `C:\\Program Files\\dotnet\\dotnet.exe`
        
- SDK version: **10.x**
    

⚠️ Importante:  
No usar un SDK “embebido” descargado solo por Rider.

---

### 2.3 Ejecutar desde Rider

- Seleccionar configuración `RecetasApi.Api`
    
- Run ▶
    

Swagger debería abrir automáticamente en el navegador.

---

### 2.4 Comprobación adicional (terminal integrada)

```bash
dotnet --version
dotnet run
```

Si funciona aquí → entorno correcto.

---

## 3. Abrir el proyecto con **VS Code**

![Image](https://www.alphr.com/wp-content/uploads/2023/03/How-to-Open-Solution-Explorer-2.png)

![Image](https://code.visualstudio.com/assets/docs/languages/csharp/csharp-devkit.png)

![Image](https://cann0nf0dder.wordpress.com/wp-content/uploads/2020/08/083020_1200_basicdotnet3.png)

### 3.1 Extensiones recomendadas

Instalar:

- **C# Dev Kit**
    
- **C#**
    
- **.NET Install Tool** (opcional)
    

---

### 3.2 Abrir proyecto

Desde la carpeta padre:

```bash
code RecetasApi
```

VS Code detecta:

- `.sln`
    
- `.csproj`
    

---

### 3.3 Ejecutar desde terminal integrada

```bash
cd RecetasApi.Api
dotnet run
```

Abrir:

```
http://localhost:5000/swagger
```

---

### 3.4 Ejecutar con Run & Debug (opcional)

VS Code suele ofrecer:

> “Run and Debug ASP.NET Core”

Si se acepta:

- genera `launch.json`
    
- ejecuta `dotnet run` internamente
    

---

## 4. Nota sobre **Eclipse** (para el alumno rezagado 😄)

Eclipse no es el entorno recomendado hoy para .NET, pero si alguien insiste:

- Eclipse **no crea** el proyecto
    
- Solo importa el `.sln` / `.csproj`
    
- Toda ejecución real sigue siendo:
    
    ```bash
    dotnet run
    ```
    

👉 Mensaje claro para el alumnado:

> “Puedes usar Eclipse, pero no te va a ayudar; el SDK manda.”

---

## 5. Resumen para el alumnado

|Paso|Dónde|
|---|---|
|Instalar .NET SDK 10|Sistema|
|Crear proyecto|Terminal (`dotnet new`)|
|Ejecutar|Terminal (`dotnet run`)|
|Editar / depurar|Rider / VS Code|
|Swagger|Navegador|

---

# 1) Limpieza del proyecto base

## 1.1 Revisión de ficheros iniciales (qué nos quedamos)

En `RecetasApi.Api/` normalmente tendrás:

* `Program.cs` (se queda)
* `appsettings.json` (se queda)
* `Controllers/WeatherForecastController.cs` (se irá)
* `WeatherForecast.cs` (se irá)
* `Properties/launchSettings.json` (se queda)
* `RecetasApi.Api.csproj` (se queda)

## 1.2 Asegurar que Swagger está habilitado

Abre `Program.cs` y comprueba que existe algo equivalente a:

* `AddEndpointsApiExplorer()`
* `AddSwaggerGen()`
* `UseSwagger()`
* `UseSwaggerUI()`

En template actual suele estar **activado en Development**.

### Verificación

Ejecuta la app y confirma que sigue funcionando:

* `http://localhost:xxxx/swagger`

---

# 2) Eliminar WeatherForecast (plantilla)

## 2.1 Eliminar archivos

Borra:

* `RecetasApi.Api/WeatherForecast.cs`
* `RecetasApi.Api/Controllers/WeatherForecastController.cs`

## 2.2 Limpieza de referencias (si las hubiera)

En algunos templates no hay nada más que tocar. Si por casualidad hubiera referencias a `WeatherForecast`, elimínalas.

## 2.3 Compilar y ejecutar

Desde `RecetasApi.Api/`:

```bash
dotnet build
dotnet run
```

### Qué debe pasar

* La API arranca sin errores.
* Swagger abre, pero **estará vacío** (sin endpoints), y eso es correcto.

---

# 3) Crear `RecetasController` (primer controlador real)

## 3.1 Crear carpeta y archivo

Si no existe, crea carpeta:

* `RecetasApi.Api/Controllers/`

Crea archivo:

* `RecetasController.cs`

## 3.2 Código del controlador (versión mínima, lista para Swagger)

```csharp
using Microsoft.AspNetCore.Mvc;

namespace RecetasApi.Api.Controllers;

[ApiController]
[Route("api/[controller]")]
public class RecetasController : ControllerBase
{
    [HttpGet]
    public IActionResult GetAll()
    {
        // Placeholder: devolveremos datos reales cuando metamos modelos + persistencia
        var result = new[]
        {
            new
            {
                id = 1,
                titulo = "Tortilla de patatas",
                autor = "demo",
                likes = 0
            }
        };

        return Ok(result);
    }
}
```

**Notas didácticas**

* `api/[controller]` ⇒ `api/recetas`
* `[HttpGet]` ⇒ GET a la colección

## 3.3 Ejecutar y comprobar

```bash
dotnet run
```

Swagger debe mostrar:

* `GET /api/Recetas`

---

# 4) Primer endpoint real (GET /api/recetas)

En este punto ya lo tienes: **es el `GetAll()`**.

## 4.1 Prueba rápida en Swagger

1. Swagger → `GET /api/Recetas`
2. `Try it out`
3. `Execute`

Deberías ver un JSON similar a:

```json
[
  {
    "id": 1,
    "titulo": "Tortilla de patatas",
    "autor": "demo",
    "likes": 0
  }
]
```

---

## 1) Modelo propio: `Receta`, `IngredienteItem`, `Paso`

Crea carpeta: `RecetasApi.Api/Domain/`

### `Domain/Receta.cs`

```csharp
namespace RecetasApi.Api.Domain;

public class Receta
{
    public long Id { get; set; }

    public string Titulo { get; set; } = string.Empty;

    // Autor (simplificado por ahora; luego se puede enlazar a Usuario)
    public string Autor { get; set; } = string.Empty;

    public List<IngredienteItem> Ingredientes { get; set; } = new();

    public List<Paso> Pasos { get; set; } = new();

    public int LikesCount { get; set; }

    public DateTime CreatedAt { get; set; } = DateTime.UtcNow;
    public DateTime UpdatedAt { get; set; } = DateTime.UtcNow;
}
```

### `Domain/IngredienteItem.cs`

```csharp
namespace RecetasApi.Api.Domain;

public class IngredienteItem
{
    public long Id { get; set; } // útil para EF Core
    public string Nombre { get; set; } = string.Empty;
    public string Cantidad { get; set; } = string.Empty;
}
```

### `Domain/Paso.cs`

```csharp
namespace RecetasApi.Api.Domain;

public class Paso
{
    public long Id { get; set; } // útil para EF Core
    public int Orden { get; set; }
    public string Descripcion { get; set; } = string.Empty;
    public int? DuracionEstimadaMin { get; set; }
}
```

---

## 2) DTOs de entrada/salida (contrato API)

Crea carpeta: `RecetasApi.Api/Contracts/`

### DTOs

```csharp
namespace RecetasApi.Api.Contracts;

public record IngredienteItemDto(string Nombre, string Cantidad);

public record PasoDto(int Orden, string Descripcion, int? DuracionEstimadaMin);

public record RecetaSummaryDto(long Id, string Titulo, string Autor, int LikesCount);

public record RecetaDetailDto(
    long Id,
    string Titulo,
    string Autor,
    List<IngredienteItemDto> Ingredientes,
    List<PasoDto> Pasos,
    int LikesCount,
    DateTime CreatedAt,
    DateTime UpdatedAt
);

public record CreateRecetaRequest(
    string Titulo,
    List<IngredienteItemDto> Ingredientes,
    List<PasoDto> Pasos
);

public record UpdateRecetaRequest(
    string Titulo,
    List<IngredienteItemDto> Ingredientes,
    List<PasoDto> Pasos
);
```

### Mapper mínimo (sin AutoMapper)

Crea carpeta: `RecetasApi.Api/Mapping/RecetasMapping.cs`

```csharp
using RecetasApi.Api.Contracts;
using RecetasApi.Api.Domain;

namespace RecetasApi.Api.Mapping;

public static class RecetasMapping
{
    public static RecetaSummaryDto ToSummaryDto(this Receta r) =>
        new(r.Id, r.Titulo, r.Autor, r.LikesCount);

    public static RecetaDetailDto ToDetailDto(this Receta r) =>
        new(
            r.Id,
            r.Titulo,
            r.Autor,
            r.Ingredientes.Select(i => new IngredienteItemDto(i.Nombre, i.Cantidad)).ToList(),
            r.Pasos.OrderBy(p => p.Orden).Select(p => new PasoDto(p.Orden, p.Descripcion, p.DuracionEstimadaMin)).ToList(),
            r.LikesCount,
            r.CreatedAt,
            r.UpdatedAt
        );

    public static Receta ToEntity(this CreateRecetaRequest req, string autor) =>
        new()
        {
            Titulo = req.Titulo.Trim(),
            Autor = autor,
            Ingredientes = req.Ingredientes.Select(i => new IngredienteItem { Nombre = i.Nombre.Trim(), Cantidad = i.Cantidad.Trim() }).ToList(),
            Pasos = req.Pasos.Select(p => new Paso { Orden = p.Orden, Descripcion = p.Descripcion.Trim(), DuracionEstimadaMin = p.DuracionEstimadaMin }).ToList(),
            CreatedAt = DateTime.UtcNow,
            UpdatedAt = DateTime.UtcNow
        };

    public static void Apply(this UpdateRecetaRequest req, Receta entity)
    {
        entity.Titulo = req.Titulo.Trim();
        entity.Ingredientes = req.Ingredientes.Select(i => new IngredienteItem { Nombre = i.Nombre.Trim(), Cantidad = i.Cantidad.Trim() }).ToList();
        entity.Pasos = req.Pasos.Select(p => new Paso { Orden = p.Orden, Descripcion = p.Descripcion.Trim(), DuracionEstimadaMin = p.DuracionEstimadaMin }).ToList();
        entity.UpdatedAt = DateTime.UtcNow;
    }
}
```

---

## 3) Persistencia (iteración 1): In-memory con repositorio simple

Esto es ideal para que entiendan el flujo **sin EF**.

### 3.1 Repositorio

Crea carpeta: `RecetasApi.Api/Infrastructure/`

#### `Infrastructure/IRecetasRepository.cs`

```csharp
using RecetasApi.Api.Domain;

namespace RecetasApi.Api.Infrastructure;

public interface IRecetasRepository
{
    IEnumerable<Receta> GetAll();
    Receta? GetById(long id);
    Receta Add(Receta receta);
    bool Update(Receta receta);
    bool Delete(long id);
}
```

#### `Infrastructure/InMemoryRecetasRepository.cs`

```csharp
using RecetasApi.Api.Domain;

namespace RecetasApi.Api.Infrastructure;

public class InMemoryRecetasRepository : IRecetasRepository
{
    private readonly List<Receta> _data = new();
    private long _nextId = 1;

    public IEnumerable<Receta> GetAll() => _data;

    public Receta? GetById(long id) => _data.FirstOrDefault(r => r.Id == id);

    public Receta Add(Receta receta)
    {
        receta.Id = _nextId++;
        _data.Add(receta);
        return receta;
    }

    public bool Update(Receta receta)
    {
        var idx = _data.FindIndex(r => r.Id == receta.Id);
        if (idx < 0) return false;

        _data[idx] = receta;
        return true;
    }

    public bool Delete(long id)
    {
        var existing = GetById(id);
        if (existing is null) return false;
        _data.Remove(existing);
        return true;
    }
}
```

### 3.2 Registrar el repositorio en DI (`Program.cs`)

En `Program.cs`, añade:

```csharp
using RecetasApi.Api.Infrastructure;

builder.Services.AddSingleton<IRecetasRepository, InMemoryRecetasRepository>();
```

---

## 4) Actualizar `RecetasController` a modelo + DTOs + repo

Reemplaza tu controlador por este (CRUD mínimo, ya sin hardcode):

```csharp
using Microsoft.AspNetCore.Mvc;
using RecetasApi.Api.Contracts;
using RecetasApi.Api.Infrastructure;
using RecetasApi.Api.Mapping;

namespace RecetasApi.Api.Controllers;

[ApiController]
[Route("api/[controller]")]
public class RecetasController : ControllerBase
{
    private readonly IRecetasRepository _repo;

    public RecetasController(IRecetasRepository repo)
    {
        _repo = repo;
    }

    // GET /api/recetas
    [HttpGet]
    public IActionResult GetAll()
    {
        var result = _repo.GetAll().Select(r => r.ToSummaryDto());
        return Ok(result);
    }

    // GET /api/recetas/{id}
    [HttpGet("{id:long}")]
    public IActionResult GetById(long id)
    {
        var receta = _repo.GetById(id);
        if (receta is null) return NotFound();
        return Ok(receta.ToDetailDto());
    }

    // POST /api/recetas
    [HttpPost]
    public IActionResult Create([FromBody] CreateRecetaRequest request)
    {
        // Autor simulado (más adelante: auth)
        var autor = "demo";

        var entity = request.ToEntity(autor);
        var created = _repo.Add(entity);

        return CreatedAtAction(nameof(GetById), new { id = created.Id }, created.ToDetailDto());
    }

    // PUT /api/recetas/{id}
    [HttpPut("{id:long}")]
    public IActionResult Update(long id, [FromBody] UpdateRecetaRequest request)
    {
        var existing = _repo.GetById(id);
        if (existing is null) return NotFound();

        request.Apply(existing);

        var ok = _repo.Update(existing);
        if (!ok) return NotFound();

        return Ok(existing.ToDetailDto());
    }

    // DELETE /api/recetas/{id}
    [HttpDelete("{id:long}")]
    public IActionResult Delete(long id)
    {
        var ok = _repo.Delete(id);
        return ok ? NoContent() : NotFound();
    }
}
```

### Pruebas rápidas en Swagger

1. `POST /api/recetas` con body:

```json
{
  "titulo": "Tortilla de patatas",
  "ingredientes": [
    { "nombre": "Patatas", "cantidad": "500 g" },
    { "nombre": "Huevos", "cantidad": "4" }
  ],
  "pasos": [
    { "orden": 1, "descripcion": "Pelar y cortar patatas", "duracionEstimadaMin": 10 },
    { "orden": 2, "descripcion": "Freír patatas", "duracionEstimadaMin": 20 }
  ]
}
```

2. `GET /api/recetas` (summary)
3. `GET /api/recetas/{id}` (detail)

---

## 5) Persistencia (iteración 2): EF Core + SQLite (recomendada para ejemplo “real”)

Cuando  in-memory esté entendido, el cambio a EF es mecánico.

### 5.1 Paquetes

Desde `RecetasApi.Api/`:

```bash
dotnet add package Microsoft.EntityFrameworkCore
dotnet add package Microsoft.EntityFrameworkCore.Sqlite
dotnet add package Microsoft.EntityFrameworkCore.Design
```

(Para migraciones) instala herramienta:

```bash
dotnet tool install --global dotnet-ef
```

### 5.2 DbContext

Crea `Infrastructure/RecetasDbContext.cs`:

```csharp
using Microsoft.EntityFrameworkCore;
using RecetasApi.Api.Domain;

namespace RecetasApi.Api.Infrastructure;

public class RecetasDbContext : DbContext
{
    public RecetasDbContext(DbContextOptions<RecetasDbContext> options) : base(options) {}

    public DbSet<Receta> Recetas => Set<Receta>();
    public DbSet<IngredienteItem> Ingredientes => Set<IngredienteItem>();
    public DbSet<Paso> Pasos => Set<Paso>();

    protected override void OnModelCreating(ModelBuilder modelBuilder)
    {
        modelBuilder.Entity<Receta>()
            .HasMany(r => r.Ingredientes)
            .WithOne()
            .OnDelete(DeleteBehavior.Cascade);

        modelBuilder.Entity<Receta>()
            .HasMany(r => r.Pasos)
            .WithOne()
            .OnDelete(DeleteBehavior.Cascade);

        modelBuilder.Entity<Paso>()
            .HasIndex(p => p.Orden);
    }
}
```

### 5.3 Connection string (SQLite)

En `appsettings.json`:

```json
{
  "ConnectionStrings": {
    "RecetasDb": "Data Source=recetas.db"
  }
}
```

### 5.4 Registrar EF en `Program.cs`

```csharp
using Microsoft.EntityFrameworkCore;
using RecetasApi.Api.Infrastructure;

builder.Services.AddDbContext<RecetasDbContext>(opt =>
    opt.UseSqlite(builder.Configuration.GetConnectionString("RecetasDb")));
```

### 5.5 Repositorio EF (sustituye al in-memory)

Crea `Infrastructure/EfRecetasRepository.cs`:

```csharp
using Microsoft.EntityFrameworkCore;
using RecetasApi.Api.Domain;

namespace RecetasApi.Api.Infrastructure;

public class EfRecetasRepository : IRecetasRepository
{
    private readonly RecetasDbContext _db;

    public EfRecetasRepository(RecetasDbContext db)
    {
        _db = db;
    }

    public IEnumerable<Receta> GetAll() =>
        _db.Recetas.AsNoTracking().ToList();

    public Receta? GetById(long id) =>
        _db.Recetas
            .Include(r => r.Ingredientes)
            .Include(r => r.Pasos)
            .AsNoTracking()
            .FirstOrDefault(r => r.Id == id);

    public Receta Add(Receta receta)
    {
        _db.Recetas.Add(receta);
        _db.SaveChanges();
        return receta;
    }

    public bool Update(Receta receta)
    {
        _db.Recetas.Update(receta);
        return _db.SaveChanges() > 0;
    }

    public bool Delete(long id)
    {
        var entity = _db.Recetas
            .Include(r => r.Ingredientes)
            .Include(r => r.Pasos)
            .FirstOrDefault(r => r.Id == id);

        if (entity is null) return false;

        _db.Recetas.Remove(entity);
        return _db.SaveChanges() > 0;
    }
}
```

En `Program.cs`, cambia el registro del repo:

```csharp
builder.Services.AddScoped<IRecetasRepository, EfRecetasRepository>();
```

(Quita el `AddSingleton` del in-memory.)

### 5.6 Migraciones y creación de BD

Desde la raíz de `RecetasApi.Api/`:

```bash
dotnet ef migrations add InitialCreate
dotnet ef database update
```

Ejecuta y prueba Swagger igual que antes: ahora persiste en `recetas.db`.

---

## 6) PostgreSQL (variante, cuando toque)


* `Npgsql.EntityFrameworkCore.PostgreSQL`
* `UseNpgsql(...)`
* connection string con host/puerto/user/pass

---

# Siguiente fase del proyecto (con EF Core)

1. **Validación de datos**

   * Data Annotations (base, estándar)
   * FluentValidation (opcional, como alternativa moderna)
2. **Errores HTTP coherentes**

   * 400, 404, 409, 422…
   * Mensajes claros y consistentes
3. (Después) **Reglas de negocio**

   * copias
   * likes
   * comentarios
   * histórico

---

## 1) Validación con Data Annotations (primer nivel)

### Por qué empezar aquí

* No añade dependencias
* Es estándar .NET
* Se entiende rápido viniendo de Java (`@NotNull`, `@Size`, etc.)
* Swagger muestra automáticamente los errores

---

### 1.1 Añadir validaciones a los DTOs de entrada

👉 **La validación debe ir en los DTOs**, no en las entidades de dominio.

#### `CreateRecetaRequest.cs`

```csharp
using System.ComponentModel.DataAnnotations;

namespace RecetasApi.Api.Contracts;

public record CreateRecetaRequest
(
    [Required]
    [MinLength(3)]
    string Titulo,

    [Required]
    [MinLength(1)]
    List<IngredienteItemDto> Ingredientes,

    [Required]
    [MinLength(1)]
    List<PasoDto> Pasos
);
```

#### `IngredienteItemDto.cs`

```csharp
using System.ComponentModel.DataAnnotations;

namespace RecetasApi.Api.Contracts;

public record IngredienteItemDto
(
    [Required]
    string Nombre,

    [Required]
    string Cantidad
);
```

#### `PasoDto.cs`

```csharp
using System.ComponentModel.DataAnnotations;

namespace RecetasApi.Api.Contracts;

public record PasoDto
(
    [Range(1, int.MaxValue)]
    int Orden,

    [Required]
    string Descripcion,

    [Range(0, 1440)]
    int? DuracionEstimadaMin
);
```

---

### 1.2 Qué ocurre automáticamente

Gracias a `[ApiController]`:

* Si el JSON **no cumple** las validaciones:

  * ASP.NET Core devuelve **400 Bad Request**
  * con un cuerpo detallando los errores
* **No entra en el método del controller**
* Swagger lo muestra claramente

👉 Esto es importante para que el alumnado vea que **no hay `if` manuales**.

---

### 1.3 Prueba rápida en Swagger

POST `/api/recetas` con:

```json
{
  "titulo": "",
  "ingredientes": [],
  "pasos": []
}
```

Resultado esperado:

* **400 Bad Request**
* JSON con errores por campo

---

## 2) Errores HTTP coherentes (más allá del 404)

Ahora refinamos cómo responde la API.

---

### 2.1 Errores que ya tenemos bien

* `404 NotFound()` → recurso no existe
* `201 CreatedAtAction()` → creación correcta
* `204 NoContent()` → borrado correcto

Eso está **bien hecho**.

---

### 2.2 Añadir errores de negocio (ejemplo sencillo)

Ejemplo: **título duplicado para el mismo autor** (regla ficticia, pero ilustrativa).

En el repositorio o servicio (simplificado):

```csharp
if (_db.Recetas.Any(r => r.Autor == autor && r.Titulo == entity.Titulo))
{
    return Conflict("Ya existe una receta con ese título para este autor.");
}
```

En el controller:

```csharp
return Conflict(new
{
    error = "RECETA_DUPLICADA",
    message = "Ya existe una receta con ese título para este autor."
});
```

HTTP:

* **409 Conflict**
* Mensaje claro
* Código semántico

Todo lo demás (service layer, variantes, explicaciones largas) va al **Anexo**, como has decidido.

---

## Ejemplo conciso: validación de negocio (título duplicado)

### En el repositorio

```csharp
public bool ExistsByAutorAndTitulo(string autor, string titulo)
{
    return _db.Recetas.Any(r =>
        r.Autor == autor &&
        r.Titulo == titulo
    );
}
```

---

### En el controller

```csharp
if (_repo.ExistsByAutorAndTitulo(autor, entity.Titulo))
{
    return Conflict(new
    {
        error = "RECETA_DUPLICADA",
        message = "Ya existe una receta con ese título para este autor."
    });
}
```

* **Regla de negocio**: no permitir títulos duplicados por autor
* **HTTP**: `409 Conflict`
* **Controller**: traduce a HTTP
* **Repositorio**: comprueba la condición

---

### 2.3 Estructura mínima de error recomendada

Sin complicarse todavía:

```json
{
  "error": "CODIGO_ERROR",
  "message": "Descripción legible del problema"
}
```

Esto prepara el terreno para:

* frontend
* logs
* APIs reales

---

## 3) (Opcional pero muy limpio) Centralizar errores

Más adelante (o como extra), se puede usar:

* `ProblemDetails`
* o `ExceptionHandlerMiddleware`

Pero **no ahora**. Para este proyecto:

* explícito en controller
* claro
* visible

---

## Punto de control (antes de reglas de negocio)

En este momento, el proyecto ya tiene:

* EF Core + BD real
* Migraciones versionadas
* Validación automática
* Errores HTTP coherentes
* Swagger mostrando contratos reales

👉 **Base sólida terminada.**

A partir de aquí, todo lo que viene son **reglas de negocio**, no infraestructura.

---
## Likes
La idea:

* Un *like* es una relación **(Usuario, Receta)**.
* Un usuario solo puede dar **1 like** por receta (unicidad).
* Endpoints:

  * `POST /api/recetas/{id}/likes` → dar like
  * `DELETE /api/recetas/{id}/likes` → quitar like
  * (opcional) `GET /api/recetas/{id}/likes` → ver contador

Usaremos un “usuario” simulado por cabecera `X-User` (si no viene, `demo`). Esto permite probar varios usuarios desde Swagger sin autenticación.

---

## 1) Modelo: entidad `RecetaLike`

Crea fichero: `Domain/RecetaLike.cs`

```csharp
namespace RecetasApi.Api.Domain;

public class RecetaLike
{
    public long Id { get; set; }

    public long RecetaId { get; set; }

    public string Usuario { get; set; } = string.Empty;

    public DateTime CreatedAt { get; set; } = DateTime.UtcNow;
}
```

> Nota: no hace falta navegación `Receta` para este ejemplo (lo dejamos simple).

---

## 2) EF Core: DbSet + restricción de unicidad

En `RecetasDbContext` añade:

```csharp
public DbSet<RecetaLike> Likes => Set<RecetaLike>();
```

Y en `OnModelCreating` añade:

```csharp
modelBuilder.Entity<RecetaLike>()
    .HasIndex(l => new { l.RecetaId, l.Usuario })
    .IsUnique();
```

Esto garantiza a nivel de BD que:

* un usuario no puede duplicar el like

---

## 3) Repositorio: métodos para Like/Unlike

### 3.1 Añadir a `IRecetasRepository`

Añade:

```csharp
LikeResult AddLike(long recetaId, string usuario);
LikeResult RemoveLike(long recetaId, string usuario);
int GetLikesCount(long recetaId);
```

Define el `enum` (por ejemplo en `Infrastructure/LikeResult.cs`):

```csharp
namespace RecetasApi.Api.Infrastructure;

public enum LikeResult
{
    NotFound,
    AlreadyLiked,
    NotLiked
}
```

---

### 3.2 Implementación EF: `EfRecetasRepository`

Añade estos métodos:

```csharp
using RecetasApi.Api.Domain;

public LikeResult AddLike(long recetaId, string usuario)
{
    var recetaExists = _db.Recetas.Any(r => r.Id == recetaId);
    if (!recetaExists) return LikeResult.NotFound;

    var already = _db.Likes.Any(l => l.RecetaId == recetaId && l.Usuario == usuario);
    if (already) return LikeResult.AlreadyLiked;

    _db.Likes.Add(new RecetaLike { RecetaId = recetaId, Usuario = usuario, CreatedAt = DateTime.UtcNow });

    // Mantener contador (si quieres seguir usando LikesCount en Receta)
    var receta = _db.Recetas.First(r => r.Id == recetaId);
    receta.LikesCount += 1;

    _db.SaveChanges();
    return LikeResult.Ok;
}

public LikeResult RemoveLike(long recetaId, string usuario)
{
    var recetaExists = _db.Recetas.Any(r => r.Id == recetaId);
    if (!recetaExists) return LikeResult.NotFound;

    var like = _db.Likes.FirstOrDefault(l => l.RecetaId == recetaId && l.Usuario == usuario);
    if (like is null) return LikeResult.NotLiked;

    _db.Likes.Remove(like);

    var receta = _db.Recetas.First(r => r.Id == recetaId);
    if (receta.LikesCount > 0) receta.LikesCount -= 1;

    _db.SaveChanges();
    return LikeResult.Ok;
}

public int GetLikesCount(long recetaId)
{
    return _db.Likes.Count(l => l.RecetaId == recetaId);
}
```

> Nota: aquí usamos `LikesCount` como “contador cacheado”. También podrías eliminarlo y calcular siempre con `Count`, pero mantengo tu diseño.

---

## 4) Controller: endpoints de likes

En `RecetasController` añade estos métodos (y un helper para usuario):

```csharp
private string GetUsuario()
{
    // Permite probar usuarios distintos en Swagger: Header "X-User"
    if (Request.Headers.TryGetValue("X-User", out var user) && !string.IsNullOrWhiteSpace(user))
        return user.ToString().Trim();

    return "demo";
}
```

### 4.1 Dar like

```csharp
[HttpPost("{id:long}/likes")]
public IActionResult Like(long id)
{
    var usuario = GetUsuario();
    var result = _repo.AddLike(id, usuario);

    return result switch
    {
        LikeResult.Ok => NoContent(),
        LikeResult.NotFound => NotFound(),
        LikeResult.AlreadyLiked => Conflict(new { error = "YA_LIKEADA", message = "El usuario ya dio like a esta receta." }),
        _ => StatusCode(500)
    };
}
```

### 4.2 Quitar like

```csharp
[HttpDelete("{id:long}/likes")]
public IActionResult Unlike(long id)
{
    var usuario = GetUsuario();
    var result = _repo.RemoveLike(id, usuario);

    return result switch
    {
        LikeResult.Ok => NoContent(),
        LikeResult.NotFound => NotFound(),
        LikeResult.NotLiked => Conflict(new { error = "NO_LIKEADA", message = "El usuario no había dado like a esta receta." }),
        _ => StatusCode(500)
    };
}
```

### 4.3 (Opcional) contador

```csharp
[HttpGet("{id:long}/likes")]
public IActionResult LikesCount(long id)
{
    // Si quieres, primero comprueba que la receta existe para devolver 404 coherente.
    var receta = _repo.GetById(id);
    if (receta is null) return NotFound();

    return Ok(new { recetaId = id, likes = _repo.GetLikesCount(id) });
}
```

---

## 5) Migración

Como has añadido una entidad nueva y un índice único:

```bash
dotnet ef migrations add AddLikes
dotnet ef database update
```

---

## 6) Pruebas rápidas en Swagger

1. Crea receta (POST `/api/recetas`)
2. Da like:

   * `POST /api/recetas/{id}/likes`
   * Header: `X-User: alice`
3. Repite el like con el mismo usuario → debe dar **409**
4. Quita like:

   * `DELETE /api/recetas/{id}/likes`
   * Header: `X-User: alice`
5. Quita otra vez → **409** (NO_LIKEADA)

---


## 1) Añade almacenamiento interno de likes

En `InMemoryRecetasRepository`:

```csharp
using RecetasApi.Api.Domain;

private readonly List<RecetaLike> _likes = new();
private long _nextLikeId = 1;
```

(Colócalo junto al resto de campos privados.)

---

## 2) Implementa los métodos de likes

Añade estos métodos completos:

```csharp
public LikeResult AddLike(long recetaId, string usuario)
{
    var receta = _data.FirstOrDefault(r => r.Id == recetaId);
    if (receta is null) return LikeResult.NotFound;

    var already = _likes.Any(l => l.RecetaId == recetaId && l.Usuario == usuario);
    if (already) return LikeResult.AlreadyLiked;

    _likes.Add(new RecetaLike
    {
        Id = _nextLikeId++,
        RecetaId = recetaId,
        Usuario = usuario,
        CreatedAt = DateTime.UtcNow
    });

    receta.LikesCount += 1;
    receta.UpdatedAt = DateTime.UtcNow;

    return LikeResult.Ok;
}

public LikeResult RemoveLike(long recetaId, string usuario)
{
    var receta = _data.FirstOrDefault(r => r.Id == recetaId);
    if (receta is null) return LikeResult.NotFound;

    var like = _likes.FirstOrDefault(l => l.RecetaId == recetaId && l.Usuario == usuario);
    if (like is null) return LikeResult.NotLiked;

    _likes.Remove(like);

    if (receta.LikesCount > 0) receta.LikesCount -= 1;
    receta.UpdatedAt = DateTime.UtcNow;

    return LikeResult.Ok;
}

public int GetLikesCount(long recetaId)
{
    return _likes.Count(l => l.RecetaId == recetaId);
}
```

---

## 3) Muy importante: al borrar receta, borra likes asociados

En tu método `Delete(long id)` del in-memory, añade (antes de `return true;`):

```csharp
_likes.RemoveAll(l => l.RecetaId == id);
```

Así evitas “likes huérfanos”.

---

## 4) (Recomendado) Al actualizar receta, no tocar likes

Tu `Update` no debería reiniciar `LikesCount` ni tocar `_likes`. Solo actualiza título/ingredientes/pasos y `UpdatedAt`. (Como ya estabas haciendo.)

---

Con esto ya no fallará y podrás:

- correr en in-memory si quieres
    
- o cambiar a EF sin tocar el controller
    

Si esto te encaja, el siguiente paso natural (muy directo) es **Comentarios** (1–N) porque reutiliza el patrón: entidad nueva, endpoints bajo `/api/recetas/{id}/comentarios`, validación + 404 + 403 (si luego metemos “autor del comentario”).


Objetivo funcional mínimo:

- Un usuario puede **crear** comentario en una receta ajena o propia.
    
- Puede **borrar solo sus comentarios** (regla típica).  
    (Si prefieres simplificar: permitir borrar cualquiera; pero te dejo la opción correcta.)
    
- Endpoints:
    
    - `GET /api/recetas/{id}/comentarios`
        
    - `POST /api/recetas/{id}/comentarios`
        
    - `DELETE /api/recetas/{id}/comentarios/{comentarioId}`
        

Seguimos con “usuario simulado” por cabecera `X-User` (`demo` por defecto).

---

# 1) Modelo: `RecetaComentario`

Crea `Domain/RecetaComentario.cs`

```csharp
namespace RecetasApi.Api.Domain;

public class RecetaComentario
{
    public long Id { get; set; }

    public long RecetaId { get; set; }

    public string Usuario { get; set; } = string.Empty;

    public string Texto { get; set; } = string.Empty;

    public DateTime CreatedAt { get; set; } = DateTime.UtcNow;
}
```

---

# 2) DTOs (Contracts)

Crea en `Contracts/`:

## `ComentarioDto.cs`

```csharp
namespace RecetasApi.Api.Contracts;

public record ComentarioDto(
    long Id,
    string Usuario,
    string Texto,
    DateTime CreatedAt
);
```

## `CreateComentarioRequest.cs` (con validación)

```csharp
using System.ComponentModel.DataAnnotations;

namespace RecetasApi.Api.Contracts;

public record CreateComentarioRequest(
    [Required]
    [MinLength(1)]
    [MaxLength(1000)]
    string Texto
);
```

---

# 3) Mapping (manual, mínimo)

Crea `Mapping/ComentariosMapping.cs`

```csharp
using RecetasApi.Api.Contracts;
using RecetasApi.Api.Domain;

namespace RecetasApi.Api.Mapping;

public static class ComentariosMapping
{
    public static ComentarioDto ToDto(this RecetaComentario c) =>
        new(c.Id, c.Usuario, c.Texto, c.CreatedAt);
}
```

---

# 4) EF Core: DbSet + (opcional) índice

En `RecetasDbContext` añade:

```csharp
public DbSet<RecetaComentario> Comentarios => Set<RecetaComentario>();
```

En `OnModelCreating` (opcional, pero útil):

```csharp
modelBuilder.Entity<RecetaComentario>()
    .HasIndex(c => c.RecetaId);
```

---

# 5) Repositorio: interfaz + enum de resultado

## 5.1 Nuevo enum `CommentResult`

Crea `Infrastructure/CommentResult.cs`

```csharp
namespace RecetasApi.Api.Infrastructure;

public enum CommentResult
{
    NotFound,
    Forbidden
}
```

## 5.2 Añadir métodos a `IRecetasRepository`

```csharp
IEnumerable<RecetaComentario> GetComentarios(long recetaId);

CommentCreateResult AddComentario(long recetaId, string usuario, string texto);

CommentResult DeleteComentario(long recetaId, long comentarioId, string usuario);
```

Y crea el enum para creación:

### `Infrastructure/CommentCreateResult.cs`

```csharp
namespace RecetasApi.Api.Infrastructure;

public enum CommentCreateResult
{
    NotFound
}
```

---

# 6) Implementación EF: `EfRecetasRepository`

Añade `using RecetasApi.Api.Domain;` si hace falta.

```csharp
public IEnumerable<RecetaComentario> GetComentarios(long recetaId)
{
    return _db.Comentarios
        .Where(c => c.RecetaId == recetaId)
        .OrderBy(c => c.CreatedAt)
        .AsNoTracking()
        .ToList();
}

public CommentCreateResult AddComentario(long recetaId, string usuario, string texto)
{
    var recetaExists = _db.Recetas.Any(r => r.Id == recetaId);
    if (!recetaExists) return CommentCreateResult.NotFound;

    _db.Comentarios.Add(new RecetaComentario
    {
        RecetaId = recetaId,
        Usuario = usuario,
        Texto = texto.Trim(),
        CreatedAt = DateTime.UtcNow
    });

    _db.SaveChanges();
    return CommentCreateResult.Ok;
}

public CommentResult DeleteComentario(long recetaId, long comentarioId, string usuario)
{
    var recetaExists = _db.Recetas.Any(r => r.Id == recetaId);
    if (!recetaExists) return CommentResult.NotFound;

    var comentario = _db.Comentarios.FirstOrDefault(c => c.Id == comentarioId && c.RecetaId == recetaId);
    if (comentario is null) return CommentResult.NotFound;

    // Regla: solo el autor del comentario puede borrarlo
    if (!string.Equals(comentario.Usuario, usuario, StringComparison.OrdinalIgnoreCase))
        return CommentResult.Forbidden;

    _db.Comentarios.Remove(comentario);
    _db.SaveChanges();
    return CommentResult.Ok;
}
```

---

# 7) Implementación In-memory: `InMemoryRecetasRepository`

Añade almacenamiento:

```csharp
using RecetasApi.Api.Domain;

private readonly List<RecetaComentario> _comentarios = new();
private long _nextComentarioId = 1;
```

Implementa:

```csharp
public IEnumerable<RecetaComentario> GetComentarios(long recetaId)
{
    return _comentarios
        .Where(c => c.RecetaId == recetaId)
        .OrderBy(c => c.CreatedAt)
        .ToList();
}

public CommentCreateResult AddComentario(long recetaId, string usuario, string texto)
{
    var receta = _data.FirstOrDefault(r => r.Id == recetaId);
    if (receta is null) return CommentCreateResult.NotFound;

    _comentarios.Add(new RecetaComentario
    {
        Id = _nextComentarioId++,
        RecetaId = recetaId,
        Usuario = usuario,
        Texto = texto.Trim(),
        CreatedAt = DateTime.UtcNow
    });

    receta.UpdatedAt = DateTime.UtcNow;

    return CommentCreateResult.Ok;
}

public CommentResult DeleteComentario(long recetaId, long comentarioId, string usuario)
{
    var receta = _data.FirstOrDefault(r => r.Id == recetaId);
    if (receta is null) return CommentResult.NotFound;

    var comentario = _comentarios.FirstOrDefault(c => c.Id == comentarioId && c.RecetaId == recetaId);
    if (comentario is null) return CommentResult.NotFound;

    if (!string.Equals(comentario.Usuario, usuario, StringComparison.OrdinalIgnoreCase))
        return CommentResult.Forbidden;

    _comentarios.Remove(comentario);
    receta.UpdatedAt = DateTime.UtcNow;

    return CommentResult.Ok;
}
```

Y al borrar receta (`Delete(long id)`), añade:

```csharp
_comentarios.RemoveAll(c => c.RecetaId == id);
```

---

# 8) Controller: endpoints de comentarios

En `RecetasController`, reutiliza el helper `GetUsuario()` y añade:

```csharp
using RecetasApi.Api.Contracts;
using RecetasApi.Api.Mapping;
using RecetasApi.Api.Infrastructure;
```

## 8.1 GET comentarios

```csharp
[HttpGet("{id:long}/comentarios")]
public IActionResult GetComentarios(long id)
{
    // 404 coherente si la receta no existe
    if (_repo.GetById(id) is null) return NotFound();

    var result = _repo.GetComentarios(id).Select(c => c.ToDto());
    return Ok(result);
}
```

## 8.2 POST comentario

```csharp
[HttpPost("{id:long}/comentarios")]
public IActionResult AddComentario(long id, [FromBody] CreateComentarioRequest request)
{
    var usuario = GetUsuario();

    var result = _repo.AddComentario(id, usuario, request.Texto);

    return result switch
    {
        CommentCreateResult.Ok => NoContent(),
        CommentCreateResult.NotFound => NotFound(),
        _ => StatusCode(500)
    };
}
```

> Si prefieres devolver el comentario creado con `201`, también se puede, pero `204 NoContent` es suficiente para este ejemplo.

## 8.3 DELETE comentario (solo autor)

```csharp
[HttpDelete("{id:long}/comentarios/{comentarioId:long}")]
public IActionResult DeleteComentario(long id, long comentarioId)
{
    var usuario = GetUsuario();

    var result = _repo.DeleteComentario(id, comentarioId, usuario);

    return result switch
    {
        CommentResult.Ok => NoContent(),
        CommentResult.NotFound => NotFound(),
        CommentResult.Forbidden => Forbid(),
        _ => StatusCode(500)
    };
}
```

---

# 9) Migración

```bash
dotnet ef migrations add AddComentarios
dotnet ef database update
```

---

# 10) Pruebas rápidas en Swagger

1. Crear receta
    
2. POST comentario:
    
    - `POST /api/recetas/{id}/comentarios`
        
    - Header: `X-User: alice`
        
    - Body:
        
        ```json
        { "texto": "Muy buena receta, la probé ayer." }
        ```
        
3. GET comentarios:
    
    - `GET /api/recetas/{id}/comentarios`
        
4. DELETE comentario:
    
    - `DELETE /api/recetas/{id}/comentarios/{comentarioId}`
        
    - Header `X-User: alice` → **204**
        
    - Header `X-User: bob` → **403**
        

---

## Copias de recetas

1. Una receta puede ser **original** o **copia** de otra.
2. Al copiar:

   * se crea una nueva receta
   * con `Autor = usuario actual`
   * el título se genera como:
     **`"Copia de <TítuloOriginal> (de <AutorOriginal>)"`**
3. Mientras la receta siga siendo “copia”, el título **debe mantener ese prefijo** (aunque el usuario pueda añadir texto después si quieres).
4. Si el usuario hace “cambios sustanciales”, entonces se considera receta derivada y **deja de ser copia** (simplificaremos: una acción explícita).

   * Endpoint: `POST /api/recetas/{id}/desvincular-copia` (o `promover`)
5. Los datos del vínculo se guardan:

   * `OriginalRecetaId` (nullable)
   * `IsCopy` (derivable de OriginalRecetaId != null, pero lo dejamos simple)

> Nota: Definir “cambio sustancial” automáticamente (por comparación de ingredientes/pasos) es posible, pero no compensa para este ejemplo. Lo dejamos como acción explícita y lo mencionas en el documento.

---

# 1) Modelo: añadir campos a `Receta`

En `Domain/Receta.cs` añade:

```csharp
public long? OriginalRecetaId { get; set; }  // null si es original

public bool IsCopy => OriginalRecetaId.HasValue;
```

Si prefieres que sea persistente (columna) en vez de calculado, se puede, pero no hace falta.

---

# 2) DTOs: exponer información de copia

### `RecetaSummaryDto`

Añade `OriginalRecetaId`:

```csharp
public record RecetaSummaryDto(long Id, string Titulo, string Autor, int LikesCount, long? OriginalRecetaId);
```

### `RecetaDetailDto`

Añade `OriginalRecetaId`:

```csharp
public record RecetaDetailDto(
    long Id,
    string Titulo,
    string Autor,
    List<IngredienteItemDto> Ingredientes,
    List<PasoDto> Pasos,
    int LikesCount,
    long? OriginalRecetaId,
    DateTime CreatedAt,
    DateTime UpdatedAt
);
```

---

# 3) Mapping: actualizar mappers

En `RecetasMapping.cs`:

### Summary

```csharp
public static RecetaSummaryDto ToSummaryDto(this Receta r) =>
    new(r.Id, r.Titulo, r.Autor, r.LikesCount, r.OriginalRecetaId);
```

### Detail

```csharp
public static RecetaDetailDto ToDetailDto(this Receta r) =>
    new(
        r.Id,
        r.Titulo,
        r.Autor,
        r.Ingredientes.Select(i => new IngredienteItemDto(i.Nombre, i.Cantidad)).ToList(),
        r.Pasos.OrderBy(p => p.Orden).Select(p => new PasoDto(p.Orden, p.Descripcion, p.DuracionEstimadaMin)).ToList(),
        r.LikesCount,
        r.OriginalRecetaId,
        r.CreatedAt,
        r.UpdatedAt
    );
```

---

# 4) Repositorio: operaciones de copia

## 4.1 En `IRecetasRepository`

Añade:

```csharp
Receta? CopyReceta(long originalId, string newAutor);
bool DetachCopy(long recetaId, string autor); // desvincula copia si eres autor
```

---

## 4.2 EF repo: implementación

En `EfRecetasRepository`:

```csharp
public Receta? CopyReceta(long originalId, string newAutor)
{
    var original = _db.Recetas
        .Include(r => r.Ingredientes)
        .Include(r => r.Pasos)
        .FirstOrDefault(r => r.Id == originalId);

    if (original is null) return null;

    var copy = new Receta
    {
        Autor = newAutor,
        OriginalRecetaId = original.Id,
        Titulo = $"Copia de {original.Titulo} (de {original.Autor})",
        Ingredientes = original.Ingredientes
            .Select(i => new IngredienteItem { Nombre = i.Nombre, Cantidad = i.Cantidad })
            .ToList(),
        Pasos = original.Pasos
            .Select(p => new Paso { Orden = p.Orden, Descripcion = p.Descripcion, DuracionEstimadaMin = p.DuracionEstimadaMin })
            .ToList(),
        LikesCount = 0,
        CreatedAt = DateTime.UtcNow,
        UpdatedAt = DateTime.UtcNow
    };

    _db.Recetas.Add(copy);
    _db.SaveChanges();
    return copy;
}

public bool DetachCopy(long recetaId, string autor)
{
    var receta = _db.Recetas.FirstOrDefault(r => r.Id == recetaId);
    if (receta is null) return false;

    // Solo el autor puede desvincular su copia
    if (!string.Equals(receta.Autor, autor, StringComparison.OrdinalIgnoreCase))
        return false;

    if (receta.OriginalRecetaId is null) return true; // ya era original

    receta.OriginalRecetaId = null;
    receta.UpdatedAt = DateTime.UtcNow;

    _db.SaveChanges();
    return true;
}
```

> Nota: si quieres distinguir “no encontrado” vs “forbidden”, lo hacemos con un enum. Para mantenerlo conciso, lo dejamos booleano y el controller decide.

---

## 4.3 In-memory repo: implementación

En `InMemoryRecetasRepository`:

```csharp
public Receta? CopyReceta(long originalId, string newAutor)
{
    var original = _data.FirstOrDefault(r => r.Id == originalId);
    if (original is null) return null;

    var copy = new Receta
    {
        Id = _nextId++,
        Autor = newAutor,
        OriginalRecetaId = original.Id,
        Titulo = $"Copia de {original.Titulo} (de {original.Autor})",
        Ingredientes = original.Ingredientes
            .Select(i => new IngredienteItem { Nombre = i.Nombre, Cantidad = i.Cantidad })
            .ToList(),
        Pasos = original.Pasos
            .Select(p => new Paso { Orden = p.Orden, Descripcion = p.Descripcion, DuracionEstimadaMin = p.DuracionEstimadaMin })
            .ToList(),
        LikesCount = 0,
        CreatedAt = DateTime.UtcNow,
        UpdatedAt = DateTime.UtcNow
    };

    _data.Add(copy);
    return copy;
}

public bool DetachCopy(long recetaId, string autor)
{
    var receta = _data.FirstOrDefault(r => r.Id == recetaId);
    if (receta is null) return false;

    if (!string.Equals(receta.Autor, autor, StringComparison.OrdinalIgnoreCase))
        return false;

    receta.OriginalRecetaId = null;
    receta.UpdatedAt = DateTime.UtcNow;
    return true;
}
```

---

# 5) Controller: endpoints de copia

En `RecetasController`, usando `GetUsuario()`:

## 5.1 Copiar receta

`POST /api/recetas/{id}/copiar`

```csharp
[HttpPost("{id:long}/copiar")]
public IActionResult Copy(long id)
{
    var usuario = GetUsuario();

    var copy = _repo.CopyReceta(id, usuario);
    if (copy is null) return NotFound();

    return CreatedAtAction(nameof(GetById), new { id = copy.Id }, copy.ToDetailDto());
}
```

## 5.2 Desvincular copia (convertirla en “propia”)

`POST /api/recetas/{id}/desvincular-copia`

```csharp
[HttpPost("{id:long}/desvincular-copia")]
public IActionResult DetachCopy(long id)
{
    var usuario = GetUsuario();

    var receta = _repo.GetById(id);
    if (receta is null) return NotFound();

    if (!string.Equals(receta.Autor, usuario, StringComparison.OrdinalIgnoreCase))
        return Forbid();

    var ok = _repo.DetachCopy(id, usuario);
    return ok ? NoContent() : StatusCode(500);
}
```

---

# 6) Migración EF Core

```bash
dotnet ef migrations add AddCopySupport
dotnet ef database update
```

---

# 7) Pruebas rápidas en Swagger

1. Crear receta original (autor `alice`)

   * Header `X-User: alice`
2. Copiarla (autor `bob`)

   * `POST /api/recetas/{id}/copiar`
   * Header `X-User: bob`
3. Ver detalle de la copia

   * Debe tener:

     * `OriginalRecetaId` con el ID original
     * Título: `Copia de ... (de alice)`
4. Desvincular copia:

   * `POST /api/recetas/{idCopia}/desvincular-copia`
   * Header `X-User: bob`
   * Después `OriginalRecetaId` pasa a `null`

---

## Nota para el documento (muy breve)

> “Cambio sustancial” se modela aquí como una acción explícita (`desvincular-copia`). En un sistema real podría detectarse comparando contenido o aplicando umbrales de diferencia.

---

## Histórico de recetas

## Diseño mínimo (pero correcto)
* Añadimos a `Receta`:

    * `IsArchived` (bool)
    * `ArchivedAt` (DateTime?)
* Regla:

    * Si el autor hace `DELETE`:

        * si `LikesCount >= UMBRAL` ⇒ **no se elimina**, se archiva (pasa a histórico)
        * si no ⇒ se borra físicamente (como hasta ahora)
* Las recetas archivadas:

    * **se pueden consultar** (`GET`)
    * **se pueden listar** (endpoint específico)
    * **se pueden copiar**
    * **no se pueden modificar** (`PUT` devuelve 409) ni dar like/comentar (opcional, yo lo recomiendo por coherencia “solo lectura”)

> Umbral: define una constante (por ejemplo 10). En docencia, vale 3 para poder probar fácil.

---

# 1) Modelo: añadir campos a `Receta`

En `Domain/Receta.cs` añade:

```csharp
public long? OriginalRecetaId { get; set; }  // null si es original
public bool IsCopy => OriginalRecetaId.HasValue;
```

Si prefieres que sea persistente (columna) en vez de calculado, se puede, pero no hace falta.

---

# 2) DTOs: exponer información de copia

### `RecetaSummaryDto`

Añade `OriginalRecetaId`:

```csharp
public record RecetaSummaryDto(long Id, string Titulo, string Autor, int LikesCount, long? OriginalRecetaId);
```

### `RecetaDetailDto`

Añade `OriginalRecetaId`:

```csharp
public record RecetaDetailDto(
    long Id,
    string Titulo,
    string Autor,
    List<IngredienteItemDto> Ingredientes,
    List<PasoDto> Pasos,
    int LikesCount,
    long? OriginalRecetaId,
    DateTime CreatedAt,
    DateTime UpdatedAt
);
```

---

# 3) Mapping: actualizar mappers

En `RecetasMapping.cs`:

### Summary

```csharp
public static RecetaSummaryDto ToSummaryDto(this Receta r) =>
    new(r.Id, r.Titulo, r.Autor, r.LikesCount, r.OriginalRecetaId);
```

### Detail

```csharp
public static RecetaDetailDto ToDetailDto(this Receta r) =>
    new(
        r.Id,
        r.Titulo,
        r.Autor,
        r.Ingredientes.Select(i => new IngredienteItemDto(i.Nombre, i.Cantidad)).ToList(),
        r.Pasos.OrderBy(p => p.Orden).Select(p => new PasoDto(p.Orden, p.Descripcion, p.DuracionEstimadaMin)).ToList(),
        r.LikesCount,
        r.OriginalRecetaId,
        r.CreatedAt,
        r.UpdatedAt
    );
```

---

# 4) Repositorio: operaciones de copia

## 4.1 En `IRecetasRepository`

Añade:

```csharp
Receta? CopyReceta(long originalId, string newAutor);
bool DetachCopy(long recetaId, string autor); // desvincula copia si eres autor
```

---

## 4.2 EF repo: implementación

En `EfRecetasRepository`:

```csharp
public Receta? CopyReceta(long originalId, string newAutor)
{
    var original = _db.Recetas
        .Include(r => r.Ingredientes)
        .Include(r => r.Pasos)
        .FirstOrDefault(r => r.Id == originalId);

    if (original is null) return null;

    var copy = new Receta
    {
        Autor = newAutor,
        OriginalRecetaId = original.Id,
        Titulo = $"Copia de {original.Titulo} (de {original.Autor})",
        Ingredientes = original.Ingredientes
            .Select(i => new IngredienteItem { Nombre = i.Nombre, Cantidad = i.Cantidad })
            .ToList(),
        Pasos = original.Pasos
            .Select(p => new Paso { Orden = p.Orden, Descripcion = p.Descripcion, DuracionEstimadaMin = p.DuracionEstimadaMin })
            .ToList(),
        LikesCount = 0,
        CreatedAt = DateTime.UtcNow,
        UpdatedAt = DateTime.UtcNow
    };

    _db.Recetas.Add(copy);
    _db.SaveChanges();
    return copy;
}

public bool DetachCopy(long recetaId, string autor)
{
    var receta = _db.Recetas.FirstOrDefault(r => r.Id == recetaId);
    if (receta is null) return false;

    // Solo el autor puede desvincular su copia
    if (!string.Equals(receta.Autor, autor, StringComparison.OrdinalIgnoreCase))
        return false;

    if (receta.OriginalRecetaId is null) return true; // ya era original

    receta.OriginalRecetaId = null;
    receta.UpdatedAt = DateTime.UtcNow;

    _db.SaveChanges();
    return true;
}
```

> Nota: si quieres distinguir “no encontrado” vs “forbidden”, lo hacemos con un enum. Para mantenerlo conciso, lo dejamos booleano y el controller decide.

---

## 4.3 In-memory repo: implementación

En `InMemoryRecetasRepository`:

```csharp
public Receta? CopyReceta(long originalId, string newAutor)
{
    var original = _data.FirstOrDefault(r => r.Id == originalId);
    if (original is null) return null;

    var copy = new Receta
    {
        Id = _nextId++,
        Autor = newAutor,
        OriginalRecetaId = original.Id,
        Titulo = $"Copia de {original.Titulo} (de {original.Autor})",
        Ingredientes = original.Ingredientes
            .Select(i => new IngredienteItem { Nombre = i.Nombre, Cantidad = i.Cantidad })
            .ToList(),
        Pasos = original.Pasos
            .Select(p => new Paso { Orden = p.Orden, Descripcion = p.Descripcion, DuracionEstimadaMin = p.DuracionEstimadaMin })
            .ToList(),
        LikesCount = 0,
        CreatedAt = DateTime.UtcNow,
        UpdatedAt = DateTime.UtcNow
    };

    _data.Add(copy);
    return copy;
}

public bool DetachCopy(long recetaId, string autor)
{
    var receta = _data.FirstOrDefault(r => r.Id == recetaId);
    if (receta is null) return false;

    if (!string.Equals(receta.Autor, autor, StringComparison.OrdinalIgnoreCase))
        return false;

    receta.OriginalRecetaId = null;
    receta.UpdatedAt = DateTime.UtcNow;
    return true;
}
```

---

# 5) Controller: endpoints de copia

En `RecetasController`, usando `GetUsuario()`:

## 5.1 Copiar receta

`POST /api/recetas/{id}/copiar`

```csharp
[HttpPost("{id:long}/copiar")]
public IActionResult Copy(long id)
{
    var usuario = GetUsuario();

    var copy = _repo.CopyReceta(id, usuario);
    if (copy is null) return NotFound();

    return CreatedAtAction(nameof(GetById), new { id = copy.Id }, copy.ToDetailDto());
}
```

## 5.2 Desvincular copia (convertirla en “propia”)

`POST /api/recetas/{id}/desvincular-copia`

```csharp
[HttpPost("{id:long}/desvincular-copia")]
public IActionResult DetachCopy(long id)
{
    var usuario = GetUsuario();

    var receta = _repo.GetById(id);
    if (receta is null) return NotFound();

    if (!string.Equals(receta.Autor, usuario, StringComparison.OrdinalIgnoreCase))
        return Forbid();

    var ok = _repo.DetachCopy(id, usuario);
    return ok ? NoContent() : StatusCode(500);
}
```

---

# 6) Migración EF Core

```bash
dotnet ef migrations add AddCopySupport
dotnet ef database update
```

---

# 7) Pruebas rápidas en Swagger

1. Crear receta original (autor `alice`)

   * Header `X-User: alice`
2. Copiarla (autor `bob`)

   * `POST /api/recetas/{id}/copiar`
   * Header `X-User: bob`
3. Ver detalle de la copia

   * Debe tener:

     * `OriginalRecetaId` con el ID original
     * Título: `Copia de ... (de alice)`
4. Desvincular copia:

   * `POST /api/recetas/{idCopia}/desvincular-copia`
   * Header `X-User: bob`
   * Después `OriginalRecetaId` pasa a `null`

---

## Nota para el documento (muy breve)

> “Cambio sustancial” se modela aquí como una acción explícita (`desvincular-copia`). En un sistema real podría detectarse comparando contenido o aplicando umbrales de diferencia.

---

Si esto te encaja, el siguiente paso natural (ya conecta con tu enunciado) es **Histórico**: cuando el autor borra una receta muy likeada, en vez de eliminarla se marca como histórica y queda consultable/copiable.


## Diseño mínimo (coherente con el enunciado)

* Añadimos a `Receta`:

  * `IsArchived` (bool)
  * `ArchivedAt` (DateTime?)
* Regla:

  * Si el autor hace `DELETE`:

    * si `LikesCount >= UMBRAL` ⇒ **no se elimina**, se archiva (pasa a histórico)
    * si no ⇒ se borra físicamente (como hasta ahora)
* Las recetas archivadas:

  * **se pueden consultar** (`GET`)
  * **se pueden listar** (endpoint específico)
  * **se pueden copiar**
  * **no se pueden modificar** (`PUT` devuelve 409) ni dar like/comentar (opcional, yo lo recomiendo por coherencia “solo lectura”)

> Umbral: define una constante (por ejemplo 10). En docencia, vale 3 para poder probar fácil.

---

# 1) Modelo: ampliar `Receta`

En `Domain/Receta.cs` añade:

```csharp
public bool IsArchived { get; set; }
public DateTime? ArchivedAt { get; set; }
```

---

# 2) DTOs: exponer estado de histórico

Añade en `RecetaSummaryDto` y `RecetaDetailDto` los campos:

* `IsArchived`
* `ArchivedAt`

### `RecetaSummaryDto`

```csharp
public record RecetaSummaryDto(
    long Id,
    string Titulo,
    string Autor,
    int LikesCount,
    long? OriginalRecetaId,
    bool IsArchived
);
```

### `RecetaDetailDto`

```csharp
public record RecetaDetailDto(
    long Id,
    string Titulo,
    string Autor,
    List<IngredienteItemDto> Ingredientes,
    List<PasoDto> Pasos,
    int LikesCount,
    long? OriginalRecetaId,
    bool IsArchived,
    DateTime? ArchivedAt,
    DateTime CreatedAt,
    DateTime UpdatedAt
);
```

---

# 3) Mapping: actualizar `RecetasMapping`

### Summary

```csharp
public static RecetaSummaryDto ToSummaryDto(this Receta r) =>
    new(r.Id, r.Titulo, r.Autor, r.LikesCount, r.OriginalRecetaId, r.IsArchived);
```

### Detail

```csharp
public static RecetaDetailDto ToDetailDto(this Receta r) =>
    new(
        r.Id,
        r.Titulo,
        r.Autor,
        r.Ingredientes.Select(i => new IngredienteItemDto(i.Nombre, i.Cantidad)).ToList(),
        r.Pasos.OrderBy(p => p.Orden).Select(p => new PasoDto(p.Orden, p.Descripcion, p.DuracionEstimadaMin)).ToList(),
        r.LikesCount,
        r.OriginalRecetaId,
        r.IsArchived,
        r.ArchivedAt,
        r.CreatedAt,
        r.UpdatedAt
    );
```

---

# 4) Repositorio: listar históricos y archivar

## 4.1 Interfaz `IRecetasRepository`

Añade:

```csharp
IEnumerable<Receta> GetHistorico();
bool Archive(long id);
```

> Mantenemos `Delete` para borrado “físico” y el controller decide archivar o borrar.

---

## 4.2 EF repo: implementación

En `EfRecetasRepository`:

```csharp
public IEnumerable<Receta> GetHistorico()
{
    return _db.Recetas
        .Where(r => r.IsArchived)
        .AsNoTracking()
        .ToList();
}

public bool Archive(long id)
{
    var receta = _db.Recetas.FirstOrDefault(r => r.Id == id);
    if (receta is null) return false;

    receta.IsArchived = true;
    receta.ArchivedAt = DateTime.UtcNow;
    receta.UpdatedAt = DateTime.UtcNow;

    return _db.SaveChanges() > 0;
}
```

Y ajusta `GetAll()` para no mezclar histórico con activo (si quieres):

```csharp
public IEnumerable<Receta> GetAll() =>
    _db.Recetas.Where(r => !r.IsArchived).AsNoTracking().ToList();
```

> Si prefieres que `GET /api/recetas` liste todo, no lo filtres. Pero didácticamente, suele ser mejor separar.

---

## 4.3 In-memory repo: implementación

En `InMemoryRecetasRepository`:

```csharp
public IEnumerable<Receta> GetHistorico()
{
    return _data.Where(r => r.IsArchived).ToList();
}

public bool Archive(long id)
{
    var receta = _data.FirstOrDefault(r => r.Id == id);
    if (receta is null) return false;

    receta.IsArchived = true;
    receta.ArchivedAt = DateTime.UtcNow;
    receta.UpdatedAt = DateTime.UtcNow;
    return true;
}
```

Y ajusta `GetAll()` para devolver solo activos:

```csharp
public IEnumerable<Receta> GetAll() => _data.Where(r => !r.IsArchived);
```

---

# 5) Controller: endpoint histórico + cambio en DELETE

## 5.1 Listar histórico

Añade a `RecetasController`:

```csharp
[HttpGet("historico")]
public IActionResult GetHistorico()
{
    var result = _repo.GetHistorico().Select(r => r.ToSummaryDto());
    return Ok(result);
}
```

Ruta resultante:

* `GET /api/recetas/historico`

---

## 5.2 Cambiar el `DELETE /api/recetas/{id}`

Definimos un umbral:

```csharp
private const int ARCHIVE_LIKES_THRESHOLD = 3; // en real: 10 o más
```

Y modifica `Delete`:

```csharp
[HttpDelete("{id:long}")]
public IActionResult Delete(long id)
{
    var usuario = GetUsuario();

    var receta = _repo.GetById(id);
    if (receta is null) return NotFound();

    if (!string.Equals(receta.Autor, usuario, StringComparison.OrdinalIgnoreCase))
        return Forbid();

    if (receta.IsArchived)
        return Conflict(new { error = "RECETA_HISTORICA", message = "La receta está en histórico y no puede borrarse." });

    // Si es popular, pasa a histórico en vez de borrarse
    if (receta.LikesCount >= ARCHIVE_LIKES_THRESHOLD)
    {
        var ok = _repo.Archive(id);
        return ok ? NoContent() : StatusCode(500);
    }

    // Si no, borrado físico
    var deleted = _repo.Delete(id);
    return deleted ? NoContent() : NotFound();
}
```

---

# 6) Bloquear modificaciones si está en histórico (recomendado)

En `PUT /api/recetas/{id}` añade tras cargar `existing`:

```csharp
if (existing.IsArchived)
{
    return Conflict(new { error = "RECETA_HISTORICA", message = "La receta está en histórico y no se puede modificar." });
}
```

Y en Likes/Comentarios (opcional, coherencia “solo lectura”):

* si la receta está archivada ⇒ `409 Conflict` o `403 Forbidden`.
  Yo usaría **409** por “estado inválido”.

---

# 7) Copiar desde histórico (ya funciona)

Tu `CopyReceta` no filtra por `IsArchived`, así que ya se puede copiar (bien).

---

# 8) Migración EF Core

```bash
dotnet ef migrations add AddHistorico
dotnet ef database update
```

---

# 9) Pruebas rápidas (Swagger)

1. Crear receta con `X-User: alice`
2. Dar likes con varios usuarios hasta llegar al umbral (3)
3. `DELETE /api/recetas/{id}` con `X-User: alice`

   * Debe **archivar** (no borrar)
4. `GET /api/recetas/historico`

   * Debe aparecer
5. `GET /api/recetas/{id}`

   * Debe devolver detalle con `IsArchived=true`
6. `PUT /api/recetas/{id}`

   * Debe devolver **409**

---

Con esto ya tienes implementado el comportamiento clave del enunciado: “si era popular, pasa a histórico y sigue consultable/copiable”.


No. Es un cambio **muy pequeño**: básicamente añadir una comprobación de estado (`IsArchived`) antes de ejecutar la operación. Son **2–3 `if`** (uno por endpoint), y como ya tenemos `GetById`, queda directo.


---

## Opción recomendada: bloquear en el Controller (mínimo cambio)

### 1) Bloquear `POST /recetas/{id}/likes` y `DELETE /recetas/{id}/likes`

En ambos métodos, justo después de cargar la receta:

```csharp
var receta = _repo.GetById(id);
if (receta is null) return NotFound();

if (receta.IsArchived)
{
    return Conflict(new
    {
        error = "RECETA_HISTORICA",
        message = "No se pueden dar o quitar likes a una receta histórica."
    });
}
```

Y luego ya llamas a `_repo.AddLike(...)` / `_repo.RemoveLike(...)` como hasta ahora.

---

### 2) Bloquear `POST /recetas/{id}/comentarios`

Igual:

```csharp
var receta = _repo.GetById(id);
if (receta is null) return NotFound();

if (receta.IsArchived)
{
    return Conflict(new
    {
        error = "RECETA_HISTORICA",
        message = "No se pueden añadir comentarios a una receta histórica."
    });
}
```

---

### 3) ¿Y `GET` de comentarios?

Yo **lo dejaría permitido** (solo lectura) porque el histórico “se consulta”.

No cambies nada en:

* `GET /recetas/{id}/comentarios`
* `GET /recetas/{id}`

---

## ¿Alternativa? Bloquear en repositorio

Se puede, pero para este proyecto:

* añade complejidad innecesaria
* duplicas chequeos
* el controller ya está gestionando estado (`PUT` bloqueado, etc.)

Por tanto: **controller**.

---

## Resumen

* No, no hay que modificar mucho.
* Son **3 checks** (likes add, likes remove, add comentario).
* Respuesta recomendada: **409 Conflict** con error `RECETA_HISTORICA`.
* Las lecturas siguen funcionando.


# Anexo I
Sí. **Hay que cambiar ese paquete**, y además **un par de cosas más**, pero el cambio es **muy mecánico**. Te dejo el checklist exacto para **SQLite → PostgreSQL con EF Core**, sin sorpresas.

---

## 1) Paquetes NuGet: qué cambiar

### ❌ Quitar SQLite

No es obligatorio desinstalarlo si no molesta, pero conceptualmente **ya no se usa**:

```bash
dotnet remove package Microsoft.EntityFrameworkCore.Sqlite
```

### ✅ Añadir PostgreSQL (Npgsql)

Instala el proveedor oficial:

```bash
dotnet add package Npgsql.EntityFrameworkCore.PostgreSQL
```

Mantén (si no los tenías ya):

```bash
dotnet add package Microsoft.EntityFrameworkCore
dotnet add package Microsoft.EntityFrameworkCore.Design
```

---

## 2) Cambiar el `DbContext` **NO**

👉 **Tu `RecetasDbContext` no cambia**.

Esto es importante y didáctico:

* EF Core abstrae la base de datos
* El `DbContext` y las entidades son iguales
* El cambio está en **configuración**, no en dominio

---

## 3) Cambiar la cadena de conexión

### `appsettings.json`

Antes (SQLite):

```json
{
  "ConnectionStrings": {
    "RecetasDb": "Data Source=recetas.db"
  }
}
```

Ahora (PostgreSQL):

```json
{
  "ConnectionStrings": {
    "RecetasDb": "Host=localhost;Port=5432;Database=recetas_db;Username=recetas_user;Password=recetas_pass"
  }
}
```

(Valores de ejemplo, ajustables a Docker / local / empresa).

---

## 4) Cambiar `Program.cs`: `UseSqlite` → `UseNpgsql`

Antes:

```csharp
builder.Services.AddDbContext<RecetasDbContext>(opt =>
    opt.UseSqlite(builder.Configuration.GetConnectionString("RecetasDb")));
```

Ahora:

```csharp
builder.Services.AddDbContext<RecetasDbContext>(opt =>
    opt.UseNpgsql(builder.Configuration.GetConnectionString("RecetasDb")));
```

Y **ya está** en cuanto a configuración.

---

## 5) Migraciones: borrar y recrear (recomendado)

Las migraciones **no son portables entre proveedores**.

### 5.1 Borra migraciones anteriores

Si venías de SQLite:

```bash
rm -rf Migrations
```

(En Windows: borra la carpeta desde el explorador).

### 5.2 Crear migración nueva

```bash
dotnet ef migrations add InitialCreate
dotnet ef database update
```

---

## 6) PostgreSQL en local (recordatorio rápido)

Si usan PostgreSQL en local, lo mínimo es:

* Servicio levantado
* Base de datos creada
* Usuario con permisos

Ejemplo rápido (psql):

```sql
CREATE DATABASE recetas_db;
CREATE USER recetas_user WITH PASSWORD 'recetas_pass';
GRANT ALL PRIVILEGES ON DATABASE recetas_db TO recetas_user;
```

---

## 7) ¿Algo más a tener en cuenta?

### Tipos de datos

EF Core ajusta tipos automáticamente:

* `string` → `text` / `varchar`
* `DateTime` → `timestamp`
* `long` → `bigint`

Para este proyecto **no necesitas tocar nada**.

### Case sensitivity

PostgreSQL es sensible a mayúsculas si usas comillas:

* EF Core **no las usa por defecto**
* No tendrás problemas aquí

---

## 8) Resumen ultra-claro (para el documento)

**Para cambiar de SQLite a PostgreSQL:**

1. Cambiar paquete NuGet:

   * `Microsoft.EntityFrameworkCore.Sqlite` → `Npgsql.EntityFrameworkCore.PostgreSQL`
2. Cambiar connection string
3. Cambiar `UseSqlite` por `UseNpgsql`
4. Recrear migraciones

👉 **El dominio, los DTOs y el controlador no cambian.**

---

## Nota didáctica 

Esto demuestra justo que:

> Cambiar de base de datos **no cambia la API**,
> solo la infraestructura.

---

# Anexo — EF Core: cambiar de proveedor de base de datos

## A. Regla general (aplica a PostgreSQL, MySQL, Oracle, SQL Server, SQLite)

* El **modelo de dominio**, DTOs, mappers y controllers **no cambian**.
* Cambia **infraestructura**: el provider, la cadena de conexión y el `UseXxx`.
* Si ya tenías migraciones con otra BD, lo más limpio es:

  * borrar carpeta `Migrations/`
  * crear migración nueva
  * ejecutar `database update`

Microsoft recalca que los `UseSomething()` vienen de paquetes externos (providers/extensiones), por eso al cambiar proveedor cambias paquete y método `UseXxx`. ([Microsoft Learn][1])

---

## B. PostgreSQL (Npgsql)

### Paquetes

```bash
dotnet remove package Microsoft.EntityFrameworkCore.Sqlite
dotnet add package Npgsql.EntityFrameworkCore.PostgreSQL
dotnet add package Microsoft.EntityFrameworkCore.Design
```

El provider es `Npgsql.EntityFrameworkCore.PostgreSQL`. ([NuGet][2])

### Connection string (`appsettings.json`)

```json
{
  "ConnectionStrings": {
    "RecetasDb": "Host=localhost;Port=5432;Database=recetas_db;Username=recetas_user;Password=recetas_pass"
  }
}
```

### `Program.cs`

```csharp
using Microsoft.EntityFrameworkCore;

builder.Services.AddDbContext<RecetasDbContext>(opt =>
    opt.UseNpgsql(builder.Configuration.GetConnectionString("RecetasDb")));
```

---

## C. SQL Server (Microsoft SQL Server / Azure SQL)

### Paquetes

```bash
dotnet add package Microsoft.EntityFrameworkCore.SqlServer
dotnet add package Microsoft.EntityFrameworkCore.Design
```

Provider y `UseSqlServer` están en `Microsoft.EntityFrameworkCore.SqlServer`. ([NuGet][3])

### Connection string (ejemplos)

**SQL Server local (usuario/contraseña):**

```json
{
  "ConnectionStrings": {
    "RecetasDb": "Server=localhost,1433;Database=recetas_db;User Id=sa;Password=YourStrong!Passw0rd;TrustServerCertificate=True;"
  }
}
```

**Windows Integrated Security (entornos concretos):**

```json
{
  "ConnectionStrings": {
    "RecetasDb": "Server=(localdb)\\mssqllocaldb;Database=recetas_db;Trusted_Connection=True;"
  }
}
```

### `Program.cs`

```csharp
using Microsoft.EntityFrameworkCore;

builder.Services.AddDbContext<RecetasDbContext>(opt =>
    opt.UseSqlServer(builder.Configuration.GetConnectionString("RecetasDb")));
```

---

## D. MySQL / MariaDB (Pomelo)

En EF Core, el provider más habitual para MySQL/MariaDB es `Pomelo.EntityFrameworkCore.MySql`. ([GitHub][4])

### Paquetes

```bash
dotnet add package Pomelo.EntityFrameworkCore.MySql
dotnet add package Microsoft.EntityFrameworkCore.Design
```

### Connection string

```json
{
  "ConnectionStrings": {
    "RecetasDb": "Server=localhost;Port=3306;Database=recetas_db;User=recetas_user;Password=recetas_pass;"
  }
}
```

### `Program.cs` (nota importante: ServerVersion)

Pomelo requiere indicar `ServerVersion`:

```csharp
using Microsoft.EntityFrameworkCore;

var cs = builder.Configuration.GetConnectionString("RecetasDb");
builder.Services.AddDbContext<RecetasDbContext>(opt =>
    opt.UseMySql(cs, ServerVersion.AutoDetect(cs)));
```

---

## E. Oracle (ODP.NET EF Core provider)

Oracle mantiene su provider en `Oracle.EntityFrameworkCore`. ([NuGet][5])

### Paquetes

```bash
dotnet add package Oracle.EntityFrameworkCore
dotnet add package Microsoft.EntityFrameworkCore.Design
```

### Connection string (ejemplo típico)

```json
{
  "ConnectionStrings": {
    "RecetasDb": "User Id=recetas_user;Password=recetas_pass;Data Source=localhost:1521/XEPDB1;"
  }
}
```

### `Program.cs`

```csharp
using Microsoft.EntityFrameworkCore;

builder.Services.AddDbContext<RecetasDbContext>(opt =>
    opt.UseOracle(builder.Configuration.GetConnectionString("RecetasDb")));
```

> Nota: el método `UseOracle` aparece al instalar el paquete correcto; si no está, casi siempre es porque falta el provider o hay mismatch de versiones.

---

## F. MongoDB

Aquí hay **dos caminos**. Importante: MongoDB no es relacional, y aunque exista provider EF Core, el modelo y el enfoque “ORM relacional” no siempre encajan igual.

### F.1 Opción 1 — MongoDB con EF Core Provider (oficial)

MongoDB dispone de provider EF Core oficial (`MongoDB.EntityFrameworkCore`). ([MongoDB][6])
Tiene extensión `UseMongoDB`. ([mongodb.github.io][7])

**Paquetes (mínimo)**

```bash
dotnet add package MongoDB.EntityFrameworkCore
dotnet add package MongoDB.Driver
```

**Configuración típica**
En MongoDB, además del servidor, debes indicar **databaseName**.

Ejemplo `Program.cs`:

```csharp
using Microsoft.EntityFrameworkCore;
using MongoDB.Driver;

var mongoConn = builder.Configuration.GetConnectionString("Mongo");
var mongoDbName = builder.Configuration["Mongo:DatabaseName"];

builder.Services.AddSingleton<IMongoClient>(_ => new MongoClient(mongoConn));

builder.Services.AddDbContext<RecetasDbContext>((sp, opt) =>
{
    var client = sp.GetRequiredService<IMongoClient>();
    opt.UseMongoDB(client, mongoDbName);
});
```

`appsettings.json`:

```json
{
  "ConnectionStrings": {
    "Mongo": "mongodb://localhost:27017"
  },
  "Mongo": {
    "DatabaseName": "recetas_db"
  }
}
```

**Advertencia didáctica (recomendada):**

* Esto es útil como demostración, pero el provider puede tener limitaciones frente a EF relacional (migraciones, transacciones, ciertas traducciones LINQ, etc.). En un proyecto “solo lectura” yo lo dejaría como anexo, tal como pides.

### F.2 Opción 2 — MongoDB “nativo” (sin EF Core)


* usar `MongoDB.Driver`
* implementar un repositorio específico
* evitar migraciones EF

Esto implica cambiar más que “tres líneas”, así que, si lo incluyes, ponlo como “alternativa” (no como sustitución directa de EF relacional).

---

## G. Migraciones (recomendación cuando cambias de proveedor)

Si ya generaste migraciones para otra BD:

1. Borrar carpeta `Migrations/`
2. Crear migración nueva:

```bash
dotnet ef migrations add InitialCreate
```

3. Aplicar:

```bash
dotnet ef database update
```

---

## H. Mini-resumen comparativo (para el documento)

* **PostgreSQL**: Npgsql, muy común en backend moderno. ([NuGet][2])
* **SQL Server**: provider oficial Microsoft, muy estándar en entorno Windows/Azure. ([NuGet][3])
* **MySQL/MariaDB**: Pomelo (muy usado), requiere `ServerVersion`. ([NuGet][8])
* **Oracle**: provider oficial Oracle (`Oracle.EntityFrameworkCore`). ([NuGet][5])
* **MongoDB**: provider EF Core oficial disponible; alternativa nativa con `MongoDB.Driver`. ([MongoDB][6])

---


[1]: https://learn.microsoft.com/en-us/ef/core/dbcontext-configuration/?utm_source=chatgpt.com "DbContext Lifetime, Configuration, and Initialization"
[2]: https://www.nuget.org/packages/npgsql.entityframeworkcore.postgresql?utm_source=chatgpt.com "Npgsql.EntityFrameworkCore.PostgreSQL 10.0.0"
[3]: https://www.nuget.org/packages/Microsoft.EntityFrameworkCore.sqlserver/?utm_source=chatgpt.com "Microsoft.EntityFrameworkCore.SqlServer 10.0.1"
[4]: https://github.com/PomeloFoundation/Pomelo.EntityFrameworkCore.MySql?utm_source=chatgpt.com "PomeloFoundation/Pomelo.EntityFrameworkCore.MySql"
[5]: https://www.nuget.org/packages/oracle.entityframeworkcore?utm_source=chatgpt.com "Oracle.EntityFrameworkCore 10.23.26000"
[6]: https://www.mongodb.com/docs/entity-framework/current/?utm_source=chatgpt.com "MongoDB Entity Framework Core Provider"
[7]: https://mongodb.github.io/mongo-efcore-provider/8.0.0/api/Microsoft.EntityFrameworkCore.MongoDbContextOptionsExtensions.UseMongoDB.html?utm_source=chatgpt.com "Method UseMongoDB | MongoDB EF Core Provider API ..."
[8]: https://www.nuget.org/packages/Pomelo.EntityFrameworkCore.MySql?utm_source=chatgpt.com "Pomelo.EntityFrameworkCore.MySql 9.0.0"
# Anexo II


* validaciones reales en APIs
* cosas que el alumnado ya entiende
* y que luego puedas referenciar en el anexo de *“validaciones y errores habituales”*

---

# Lista de comprobaciones habituales en validaciones (API REST)

> Esta lista **no implica que todas deban implementarse**.
> Sirve como **catálogo de posibilidades** y como guía de lectura del código.

---

## 1. Validaciones estructurales (formato / presencia)

Son las más básicas y suelen hacerse con **Data Annotations**.

### 1.1 Campos obligatorios

* No nulos
* No vacíos
* No listas vacías

Ejemplos:

* título de receta obligatorio
* al menos un ingrediente
* al menos un paso

Anotaciones típicas:

* `[Required]`
* `[MinLength(1)]`

---

### 1.2 Longitudes mínimas y máximas

* Strings demasiado cortos o absurdamente largos

Ejemplos:

* título ≥ 3 caracteres
* descripción de paso ≥ 5 caracteres

Anotaciones:

* `[MinLength]`
* `[MaxLength]`
* `[StringLength]`

---

### 1.3 Rangos numéricos

* Valores fuera de sentido

Ejemplos:

* duración de paso negativa
* duración excesiva (ej. > 24h)
* orden del paso < 1

Anotaciones:

* `[Range(min, max)]`

---

## 2. Validaciones semánticas simples

Ya no es solo “formato”, sino **significado básico**.

### 2.1 Valores coherentes entre sí

* pasos con orden duplicado
* pasos con orden negativo
* ingredientes repetidos (mismo nombre)

Estas validaciones:

* **no suelen ir en Data Annotations**
* se hacen en código (controller o servicio)

---

### 2.2 Normalización previa

Antes de validar, muchas veces se hace:

* `Trim()`
* pasar a minúsculas/mayúsculas
* eliminar espacios duplicados

Ejemplo:

* `" Tortilla "` → `"Tortilla"`

---

## 3. Validaciones de negocio (reglas propias)

Estas **no son genéricas**, dependen del dominio.

### 3.1 Unicidad

* título único por autor
* no duplicar likes del mismo usuario
* no comentar dos veces exactamente lo mismo (opcional)

HTTP típico:

* **409 Conflict**

---

### 3.2 Propiedad / permisos

* solo el autor puede modificar o borrar su receta
* no se puede borrar receta ajena
* no se puede modificar receta histórica

HTTP típico:

* **403 Forbidden**

---

### 3.3 Estado del recurso

* receta borrada pero histórica → solo lectura
* receta activa → editable
* receta inexistente → no encontrada

HTTP típico:

* **404 Not Found**
* **409 Conflict** (estado inválido)

---

## 4. Validaciones relacionales (con BD)

Aparecen cuando ya hay persistencia real.

### 4.1 Existencia de referencias

* comentar una receta que no existe
* dar like a una receta inexistente

HTTP típico:

* **404 Not Found**

---

### 4.2 Cardinalidad

* un usuario solo puede dar un like
* una receta puede tener muchos comentarios

Se valida:

* antes de insertar
* o mediante restricción en BD + control de error

---

## 5. Validaciones de entrada JSON

### 5.1 JSON mal formado

* JSON inválido
* tipos incorrectos (string donde va int)

Esto lo gestiona automáticamente ASP.NET Core:

* **400 Bad Request**
* mensaje estándar

---

### 5.2 Campos inesperados

Por defecto:

* se ignoran
* o se rechazan según configuración

(No es prioritario para este proyecto.)

---

## 6. Validaciones temporales (si aplica)

(No las usaremos ahora, pero conviene mencionarlas.)

Ejemplos:

* fecha futura no permitida
* duración total demasiado larga
* límite de acciones por tiempo (rate limit)

---

## 7. Relación validación ↔ código HTTP (resumen)

| Tipo de problema       | Código HTTP     |
| ---------------------- | --------------- |
| Datos mal formados     | 400 Bad Request |
| Recurso inexistente    | 404 Not Found   |
| Conflicto de negocio   | 409 Conflict    |
| Permisos insuficientes | 403 Forbidden   |
| Operación correcta     | 200 / 201 / 204 |

---

## Nota pedagógica (muy importante)

> No todas las validaciones deben implementarse.
> Lo importante es **saber identificarlas**,
> saber **dónde irían**,
> y saber **qué código HTTP corresponde**.

---

## Qué hacemos con esto ahora

* ✔️ **Lo dejamos anotado**
* ✔️ Irá a un **Anexo final** junto con:

  * errores habituales en .NET
  * AutoMapper
  * problemas típicos de EF Core
* ✔️ Durante el desarrollo solo aplicamos **las mínimas necesarias**


---

# Anexo C — Autenticación y autorización (JWT docente)

Este anexo añade autenticación **funcional y fácil de entender** usando **JWT** con usuarios hardcodeados.
No se usa ASP.NET Identity ni base de datos de usuarios. El objetivo es poder **probar roles** y entender el flujo.

## C.1 Objetivo y alcance

- Emitir un token JWT con un endpoint `POST /api/auth/login`.
- Proteger endpoints con `[Authorize]`.
- Restringir acciones por rol con `[Authorize(Roles = "Admin")]`.
- Mantener el resto del proyecto igual (dominio, repositorios, reglas de negocio).

## C.2 Paquetes necesarios

En la carpeta del proyecto API:

```bash
dotnet add package Microsoft.AspNetCore.Authentication.JwtBearer
dotnet add package System.IdentityModel.Tokens.Jwt
```

## C.3 Configuración en `appsettings.json`

Añade una sección `Jwt` (valores de ejemplo; la clave debe ser larga):

```json
{
  "Jwt": {
    "Issuer": "RecetasApi",
    "Audience": "RecetasApi",
    "Key": "CAMBIA_ESTA_CLAVE_POR_UNA_LARGA_Y_SEGURA_MIN_32_CHARS",
    "ExpiresMinutes": 120
  }
}
```

## C.4 Clases de soporte

Crea `Security/JwtOptions.cs`:

```csharp
namespace RecetasApi.Api.Security;

public class JwtOptions
{
    public string Issuer { get; set; } = string.Empty;
    public string Audience { get; set; } = string.Empty;
    public string Key { get; set; } = string.Empty;
    public int ExpiresMinutes { get; set; } = 120;
}
```

Crea `Security/TokenService.cs`:

```csharp
using System.IdentityModel.Tokens.Jwt;
using System.Security.Claims;
using System.Text;
using Microsoft.Extensions.Options;
using Microsoft.IdentityModel.Tokens;

namespace RecetasApi.Api.Security;

public class TokenService
{
    private readonly JwtOptions _opt;

    public TokenService(IOptions<JwtOptions> options)
    {
        _opt = options.Value;
    }

    public string CreateToken(string username, string role)
    {
        var claims = new List<Claim>
        {
            new Claim(ClaimTypes.Name, username),
            new Claim(ClaimTypes.Role, role)
        };

        var key = new SymmetricSecurityKey(Encoding.UTF8.GetBytes(_opt.Key));
        var creds = new SigningCredentials(key, SecurityAlgorithms.HmacSha256);

        var token = new JwtSecurityToken(
            issuer: _opt.Issuer,
            audience: _opt.Audience,
            claims: claims,
            expires: DateTime.UtcNow.AddMinutes(_opt.ExpiresMinutes),
            signingCredentials: creds
        );

        return new JwtSecurityTokenHandler().WriteToken(token);
    }
}
```

## C.5 Activar JWT Bearer en `Program.cs`

Añade (o ajusta) la configuración:

```csharp
using System.Text;
using Microsoft.AspNetCore.Authentication.JwtBearer;
using Microsoft.IdentityModel.Tokens;
using RecetasApi.Api.Security;

var builder = WebApplication.CreateBuilder(args);

builder.Services.AddControllers();
// OpenAPI/Swagger según tengas en el documento principal
builder.Services.AddOpenApi();

// JWT options
builder.Services.Configure<JwtOptions>(builder.Configuration.GetSection("Jwt"));
builder.Services.AddSingleton<TokenService>();

// AuthN + AuthZ
var jwt = builder.Configuration.GetSection("Jwt").Get<JwtOptions>()!;

builder.Services
    .AddAuthentication(JwtBearerDefaults.AuthenticationScheme)
    .AddJwtBearer(options =>
    {
        options.TokenValidationParameters = new TokenValidationParameters
        {
            ValidateIssuer = true,
            ValidateAudience = true,
            ValidateLifetime = true,
            ValidateIssuerSigningKey = true,
            ValidIssuer = jwt.Issuer,
            ValidAudience = jwt.Audience,
            IssuerSigningKey = new SymmetricSecurityKey(Encoding.UTF8.GetBytes(jwt.Key))
        };
    });

builder.Services.AddAuthorization();

var app = builder.Build();

if (app.Environment.IsDevelopment())
{
    app.MapOpenApi();
}

app.UseHttpsRedirection();

// IMPORTANTE: auth antes de MapControllers
app.UseAuthentication();
app.UseAuthorization();

app.MapControllers();
app.Run();
```

## C.6 Endpoint de login (usuarios hardcodeados)

Crea `Contracts/LoginRequest.cs`:

```csharp
using System.ComponentModel.DataAnnotations;

namespace RecetasApi.Api.Contracts;

public record LoginRequest(
    [Required] string Username,
    [Required] string Password
);
```

Crea `Contracts/LoginResponse.cs`:

```csharp
namespace RecetasApi.Api.Contracts;

public record LoginResponse(
    string Token,
    string Username,
    string Role
);
```

Crea `Controllers/AuthController.cs`:

```csharp
using Microsoft.AspNetCore.Mvc;
using RecetasApi.Api.Contracts;
using RecetasApi.Api.Security;

namespace RecetasApi.Api.Controllers;

[ApiController]
[Route("api/[controller]")]
public class AuthController : ControllerBase
{
    private readonly TokenService _tokens;

    // “Base de usuarios” docente: username -> (password, role)
    private static readonly Dictionary<string, (string Password, string Role)> Users = new()
    {
        ["alice"] = ("alice123", "User"),
        ["bob"]   = ("bob123",   "User"),
        ["admin"] = ("admin123", "Admin")
    };

    public AuthController(TokenService tokens)
    {
        _tokens = tokens;
    }

    [HttpPost("login")]
    public ActionResult<LoginResponse> Login([FromBody] LoginRequest request)
    {
        if (!Users.TryGetValue(request.Username, out var data))
            return Unauthorized(new { error = "LOGIN_INVALIDO", message = "Usuario o contraseña incorrectos." });

        if (request.Password != data.Password)
            return Unauthorized(new { error = "LOGIN_INVALIDO", message = "Usuario o contraseña incorrectos." });

        var token = _tokens.CreateToken(request.Username, data.Role);
        return Ok(new LoginResponse(token, request.Username, data.Role));
    }
}
```

## C.7 Proteger endpoints y obtener el usuario

- Añade `[Authorize]` a los controllers o endpoints que quieras proteger.
- Para restricciones por rol: `[Authorize(Roles = "Admin")]`.

Para usar el usuario autenticado (en lugar de `X-User`):

```csharp
private string GetUsuario()
{
    return User.Identity?.Name ?? "demo";
}
```

## C.8 Seguridad en empresa (solo esquema, lectura opcional)

En entornos reales es habitual:

- OAuth 2.0 / OpenID Connect
- Proveedores de identidad: Keycloak, Entra ID/Azure AD, Auth0, Okta
- Access token + refresh token (rotación, expiración, revocación)
- HTTPS obligatorio, CORS, rate limiting, auditoría, gestión de secretos

Referencias:
- https://learn.microsoft.com/aspnet/core/security/
- https://learn.microsoft.com/aspnet/core/security/authentication/configure-jwt-bearer-authentication
- https://oauth.net/2/
- https://openid.net/
