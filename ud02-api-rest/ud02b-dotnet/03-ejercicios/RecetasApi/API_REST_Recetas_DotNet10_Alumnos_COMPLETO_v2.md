# API REST Recetas en .NET 10 (C#)

Documento para el alumnado. Guía paso a paso con código completo (enfoque docente).

---

## 1. Introducción y objetivos

En este documento se desarrolla una **API REST (Web API)** utilizando **.NET 10** y **C#**. El objetivo es que puedas **leer, comprender y reproducir** un proyecto backend completo aunque no hayas trabajado previamente con .NET.

La API permite gestionar **recetas de cocina** e incorpora reglas de negocio habituales en aplicaciones reales: autoría, permisos, likes, comentarios, copias de recursos e histórico.

El proyecto está diseñado como **material de estudio autónomo** (para “examinar” el código), no como una práctica guiada en clase.

---

## 2. Reglas de negocio globales

### 2.1 Usuarios y permisos (versión base)

En el cuerpo principal del proyecto **no se implementa autenticación real**. El usuario se simula mediante el header HTTP:

```
X-User: nombre_usuario
```

Reglas:

- El **autor** de una receta puede crearla, modificarla y borrarla.
- Un usuario puede comentar y dar like a recetas de otros usuarios.
- Un usuario no puede modificar ni borrar recetas ajenas.
- Una receta con muchos likes, borrada por su autor, pasa a **histórico** (consultable y copiable, pero solo lectura).
- Un usuario puede **copiar** una receta ajena. El título de la copia indicará el origen.

La autenticación real mediante **JWT y roles** se incluye en el **Apéndice 4**, sin afectar al núcleo del proyecto.

---

## 3. Modelo de dominio

### 3.1 Receta

Una receta contiene:

- Id
- Título
- Autor
- Lista de ingredientes
- Lista de pasos
- Likes (usuarios que han dado like)
- RecetaOriginalId (si es copia)
- EsHistorica (si está en histórico)

### 3.2 Ingredientes

Cada ingrediente incluye:

- Ingrediente (nombre)
- Cantidad (texto: “200 g”, “1 cucharada”, etc.)

### 3.3 Pasos

Cada paso incluye:

- Orden
- Descripción
- Minutos (tiempo estimado del paso)

---

## 4. Instalación del entorno

### 4.1 Comprobar SDK instalado

```bash
dotnet --list-sdks
dotnet --info
```

### 4.2 Linux (Ubuntu 22.04 y derivados)

Opciones habituales para instalar .NET 10:

- Repositorios oficiales de Microsoft (recomendado en entornos estándar).
- **Backports** (útil si tu distribución no ofrece SDK 10 en repos oficiales).
- SDK instalado por Rider (posible), añadiendo su ubicación al `PATH` si fuese necesario.

Recomendación: evita mezclar repositorios de otras distribuciones.

### 4.3 Windows

Instala .NET SDK desde los instaladores oficiales y verifica con:

```bash
dotnet --list-sdks
```

---

## 5. Creación del proyecto

### 5.1 Creación por terminal (sin IDE)

```bash
mkdir RecetasApi
cd RecetasApi

dotnet new sln -n RecetasApi
dotnet new webapi -n RecetasApi.Api
dotnet sln add RecetasApi.Api/RecetasApi.Api.csproj
```

Ejecutar:

```bash
cd RecetasApi.Api
dotnet run
```

### 5.2 Swagger

Instalar paquete:

```bash
dotnet add package Swashbuckle.AspNetCore
```

En `Program.cs` (ver Apéndice 2 para el fichero completo):

```csharp
builder.Services.AddEndpointsApiExplorer();
builder.Services.AddSwaggerGen();

if (app.Environment.IsDevelopment())
{
    app.UseSwagger();
    app.UseSwaggerUI();
}
```

Acceso (el puerto puede variar):

```
http://localhost:<PUERTO>/swagger
```

---

## 6. Persistencia (versión base: in-memory)

En el cuerpo principal se usa in-memory para simplificar. EF Core se añade en el **Apéndice 3**.

### 6.1 Modelos de dominio

`Models/Receta.cs`

```csharp
namespace RecetasApi.Api.Models;

public class Receta
{
    public int Id { get; set; }
    public string Titulo { get; set; } = string.Empty;
    public string Autor { get; set; } = string.Empty;

    public List<IngredienteItem> Ingredientes { get; set; } = new();
    public List<Paso> Pasos { get; set; } = new();

    // Un usuario = 1 like por receta
    public HashSet<string> Likes { get; set; } = new(StringComparer.OrdinalIgnoreCase);

    public bool EsHistorica { get; set; }
    public int? RecetaOriginalId { get; set; }
}
```

`Models/IngredienteItem.cs`

```csharp
namespace RecetasApi.Api.Models;

public class IngredienteItem
{
    public int Id { get; set; }
    public string Ingrediente { get; set; } = string.Empty;
    public string Cantidad { get; set; } = string.Empty;
}
```

`Models/Paso.cs`

```csharp
namespace RecetasApi.Api.Models;

public class Paso
{
    public int Id { get; set; }
    public int Orden { get; set; }
    public string Descripcion { get; set; } = string.Empty;
    public int Minutos { get; set; }
}
```

### 6.2 DTOs

`Contracts/RecetaCreateDto.cs`

```csharp
using System.ComponentModel.DataAnnotations;

namespace RecetasApi.Api.Contracts;

public record RecetaCreateDto(
    [Required] string Titulo,
    [Required] List<IngredienteDto> Ingredientes,
    [Required] List<PasoDto> Pasos
);
```

`Contracts/IngredienteDto.cs`

```csharp
namespace RecetasApi.Api.Contracts;

public record IngredienteDto(string Ingrediente, string Cantidad);
```

`Contracts/PasoDto.cs`

```csharp
namespace RecetasApi.Api.Contracts;

public record PasoDto(int Orden, string Descripcion, int Minutos);
```

### 6.3 Repositorios (in-memory)

`Repositories/IRecetasRepository.cs`

```csharp
using RecetasApi.Api.Models;

namespace RecetasApi.Api.Repositories;

public interface IRecetasRepository
{
    IEnumerable<Receta> GetAll();
    Receta? GetById(int id);

    void Add(Receta receta);
    void Update(Receta receta);
    void Delete(Receta receta);

    bool ExistsByAutorAndTitulo(string autor, string titulo);
}
```

`Repositories/RecetasInMemoryRepository.cs`

```csharp
using RecetasApi.Api.Models;

namespace RecetasApi.Api.Repositories;

public class RecetasInMemoryRepository : IRecetasRepository
{
    private readonly List<Receta> _recetas = new();
    private int _nextId = 1;

    public IEnumerable<Receta> GetAll() => _recetas;

    public Receta? GetById(int id) => _recetas.FirstOrDefault(r => r.Id == id);

    public void Add(Receta receta)
    {
        receta.Id = _nextId++;
        _recetas.Add(receta);
    }

    public void Update(Receta receta)
    {
        // In-memory: la entidad ya está modificada por referencia
    }

    public void Delete(Receta receta)
    {
        _recetas.Remove(receta);
    }

    public bool ExistsByAutorAndTitulo(string autor, string titulo)
    {
        return _recetas.Any(r =>
            string.Equals(r.Autor, autor, StringComparison.OrdinalIgnoreCase)
            && string.Equals(r.Titulo, titulo, StringComparison.OrdinalIgnoreCase));
    }
}
```

---

## 7. Validación y errores HTTP

Se usan **Data Annotations** en DTOs y códigos HTTP coherentes:

- **400 Bad Request**: DTO inválido
- **404 Not Found**: recurso inexistente
- **403 Forbidden**: sin permisos
- **409 Conflict**: conflicto con reglas de negocio (duplicado, histórico, etc.)

Ejemplo: título duplicado para el mismo autor (en `POST /api/recetas`):

- HTTP **409 Conflict**
- Cuerpo con `error` semántico y `message` claro

---

## 8. Controlador `RecetasController` (por partes)

El controlador se construye incrementalmente. El fichero final completo aparece en el **Apéndice 1**.

---

## 9. Likes

Implementación sencilla con `HashSet<string>` en `Receta`.

Endpoints:

- `POST /api/recetas/{id}/likes`
- `DELETE /api/recetas/{id}/likes`

Reglas:

- No se permite dar/quitar likes en histórico.
- Un usuario no puede dar like dos veces.

---

## 10. Comentarios

Comentarios in-memory con repositorio dedicado. Endpoints:

- `GET /api/recetas/{id}/comentarios`
- `POST /api/recetas/{id}/comentarios`
- `DELETE /api/recetas/{id}/comentarios/{comentarioId}`

Reglas:

- Solo el autor del comentario puede borrarlo.
- No se permiten comentarios en histórico.

---

## 11. Copias de recetas

Endpoints:

- `POST /api/recetas/{id}/copiar`
- `POST /api/recetas/{id}/desvincular-copia`

Reglas:

- La copia crea una receta nueva con autor = usuario que copia.
- Título: `Copia de <título> (de <autor>)`.
- `RecetaOriginalId` apunta al original (o al original “raíz” si copias una copia).
- Una copia puede desvincularse eliminando `RecetaOriginalId`.
- Se permite copiar recetas históricas (son consultables/copIABLES).

---

## 12. Histórico de recetas

Una receta popular borrada por su autor pasa a **histórico**.

- Consultable y copiable.
- Solo lectura: no permite `PUT`, likes ni comentarios.
- Listado: `GET /api/recetas/historico`.

---

# ANEXOS

## Apéndice 1 — `RecetasController` final (archivo completo)

`Controllers/RecetasController.cs`

```csharp
using Microsoft.AspNetCore.Mvc;
using RecetasApi.Api.Contracts;
using RecetasApi.Api.Models;
using RecetasApi.Api.Repositories;
using RecetasApi.Api.Mapping;

namespace RecetasApi.Api.Controllers;

[ApiController]
[Route("api/[controller]")]
public class RecetasController : ControllerBase
{
    private readonly IRecetasRepository _repo;
    private readonly IComentariosRepository _comentarios;

    public RecetasController(IRecetasRepository repo, IComentariosRepository comentarios)
    {
        _repo = repo;
        _comentarios = comentarios;
    }

    private string GetUsuario()
    {
        if (Request.Headers.TryGetValue("X-User", out var user) && !string.IsNullOrWhiteSpace(user))
            return user.ToString().Trim();

        return "demo";
    }

    // GET /api/recetas
    [HttpGet]
    public IActionResult GetAll()
    {
        var result = _repo.GetAll().Select(r => r.ToSummaryDto());
        return Ok(result);
    }

    // GET /api/recetas/{id}
    [HttpGet("{id:int}")]
    public IActionResult GetById(int id)
    {
        var receta = _repo.GetById(id);
        if (receta is null) return NotFound();
        return Ok(receta.ToDetailDto());
    }

    // POST /api/recetas
    [HttpPost]
    public IActionResult Create([FromBody] RecetaCreateDto request)
    {
        var autor = GetUsuario();

        if (_repo.ExistsByAutorAndTitulo(autor, request.Titulo))
        {
            return Conflict(new
            {
                error = "RECETA_DUPLICADA",
                message = "Ya existe una receta con ese título para este autor."
            });
        }

        var entity = request.ToEntity(autor);
        _repo.Add(entity);

        return CreatedAtAction(nameof(GetById), new { id = entity.Id }, entity.ToDetailDto());
    }

    // PUT /api/recetas/{id}
    [HttpPut("{id:int}")]
    public IActionResult Update(int id, [FromBody] RecetaCreateDto request)
    {
        var autor = GetUsuario();

        var existing = _repo.GetById(id);
        if (existing is null) return NotFound();

        if (existing.EsHistorica)
        {
            return Conflict(new { error = "RECETA_HISTORICA", message = "La receta está en histórico y no se puede modificar." });
        }

        if (!string.Equals(existing.Autor, autor, StringComparison.OrdinalIgnoreCase))
            return Forbid();

        request.ApplyTo(existing);
        _repo.Update(existing);

        return Ok(existing.ToDetailDto());
    }

    // DELETE /api/recetas/{id}
    [HttpDelete("{id:int}")]
    public IActionResult Delete(int id)
    {
        var autor = GetUsuario();

        var existing = _repo.GetById(id);
        if (existing is null) return NotFound();

        if (!string.Equals(existing.Autor, autor, StringComparison.OrdinalIgnoreCase))
            return Forbid();

        if (existing.EsHistorica)
        {
            return Conflict(new { error = "RECETA_HISTORICA", message = "La receta ya está en histórico." });
        }

        // Regla: si es popular, pasa a histórico en vez de borrarse
        const int ARCHIVE_LIKES_THRESHOLD = 3;
        if (existing.Likes.Count >= ARCHIVE_LIKES_THRESHOLD)
        {
            existing.EsHistorica = true;
            _repo.Update(existing);
            return NoContent();
        }

        _repo.Delete(existing);
        return NoContent();
    }

    // GET /api/recetas/historico
    [HttpGet("historico")]
    public IActionResult GetHistorico()
    {
        var result = _repo.GetAll()
            .Where(r => r.EsHistorica)
            .Select(r => r.ToSummaryDto());

        return Ok(result);
    }

    // POST /api/recetas/{id}/likes
    [HttpPost("{id:int}/likes")]
    public IActionResult Like(int id)
    {
        var usuario = GetUsuario();

        var receta = _repo.GetById(id);
        if (receta is null) return NotFound();

        if (receta.EsHistorica)
        {
            return Conflict(new { error = "RECETA_HISTORICA", message = "No se pueden dar likes a una receta histórica." });
        }

        var added = receta.Likes.Add(usuario);
        if (!added)
        {
            return Conflict(new { error = "YA_LIKEADA", message = "El usuario ya dio like a esta receta." });
        }

        _repo.Update(receta);
        return NoContent();
    }

    // DELETE /api/recetas/{id}/likes
    [HttpDelete("{id:int}/likes")]
    public IActionResult Unlike(int id)
    {
        var usuario = GetUsuario();

        var receta = _repo.GetById(id);
        if (receta is null) return NotFound();

        if (receta.EsHistorica)
        {
            return Conflict(new { error = "RECETA_HISTORICA", message = "No se pueden quitar likes a una receta histórica." });
        }

        var removed = receta.Likes.Remove(usuario);
        if (!removed)
        {
            return Conflict(new { error = "NO_LIKEADA", message = "El usuario no había dado like a esta receta." });
        }

        _repo.Update(receta);
        return NoContent();
    }

    // GET /api/recetas/{id}/comentarios
    [HttpGet("{id:int}/comentarios")]
    public IActionResult GetComentarios(int id)
    {
        var receta = _repo.GetById(id);
        if (receta is null) return NotFound();

        var result = _comentarios.GetByReceta(id)
            .Select(c => new ComentarioDto(c.Id, c.Autor, c.Texto, c.CreatedAt));

        return Ok(result);
    }

    // POST /api/recetas/{id}/comentarios
    [HttpPost("{id:int}/comentarios")]
    public IActionResult AddComentario(int id, [FromBody] ComentarioCreateDto request)
    {
        var usuario = GetUsuario();

        var receta = _repo.GetById(id);
        if (receta is null) return NotFound();

        if (receta.EsHistorica)
        {
            return Conflict(new { error = "RECETA_HISTORICA", message = "No se pueden añadir comentarios a una receta histórica." });
        }

        var entity = new Comentario
        {
            RecetaId = id,
            Autor = usuario,
            Texto = request.Texto.Trim()
        };

        var created = _comentarios.Add(entity);

        return Created($"/api/recetas/{id}/comentarios/{created.Id}",
            new ComentarioDto(created.Id, created.Autor, created.Texto, created.CreatedAt));
    }

    // DELETE /api/recetas/{id}/comentarios/{comentarioId}
    [HttpDelete("{id:int}/comentarios/{comentarioId:int}")]
    public IActionResult DeleteComentario(int id, int comentarioId)
    {
        var usuario = GetUsuario();

        var receta = _repo.GetById(id);
        if (receta is null) return NotFound();

        if (receta.EsHistorica)
        {
            return Conflict(new { error = "RECETA_HISTORICA", message = "No se pueden borrar comentarios en una receta histórica." });
        }

        var comentario = _comentarios.GetById(comentarioId);
        if (comentario is null || comentario.RecetaId != id) return NotFound();

        if (!string.Equals(comentario.Autor, usuario, StringComparison.OrdinalIgnoreCase))
            return Forbid();

        _comentarios.Delete(comentario);
        return NoContent();
    }

    // POST /api/recetas/{id}/copiar
    [HttpPost("{id:int}/copiar")]
    public IActionResult Copiar(int id)
    {
        var usuario = GetUsuario();

        var original = _repo.GetById(id);
        if (original is null) return NotFound();

        var copia = new Receta
        {
            Titulo = $"Copia de {original.Titulo} (de {original.Autor})",
            Autor = usuario,
            Ingredientes = original.Ingredientes
                .Select(i => new IngredienteItem { Ingrediente = i.Ingrediente, Cantidad = i.Cantidad })
                .ToList(),
            Pasos = original.Pasos
                .Select(p => new Paso { Orden = p.Orden, Descripcion = p.Descripcion, Minutos = p.Minutos })
                .ToList(),
            RecetaOriginalId = original.RecetaOriginalId ?? original.Id,
            EsHistorica = false
        };

        if (_repo.ExistsByAutorAndTitulo(usuario, copia.Titulo))
        {
            return Conflict(new { error = "RECETA_DUPLICADA", message = "Ya existe una receta con ese título para este autor." });
        }

        _repo.Add(copia);
        return CreatedAtAction(nameof(GetById), new { id = copia.Id }, copia.ToDetailDto());
    }

    // POST /api/recetas/{id}/desvincular-copia
    [HttpPost("{id:int}/desvincular-copia")]
    public IActionResult DesvincularCopia(int id)
    {
        var usuario = GetUsuario();

        var receta = _repo.GetById(id);
        if (receta is null) return NotFound();

        if (receta.EsHistorica)
        {
            return Conflict(new { error = "RECETA_HISTORICA", message = "No se puede modificar una receta histórica." });
        }

        if (!string.Equals(receta.Autor, usuario, StringComparison.OrdinalIgnoreCase))
            return Forbid();

        if (receta.RecetaOriginalId is null)
        {
            return Conflict(new { error = "NO_ES_COPIA", message = "La receta no es una copia." });
        }

        receta.RecetaOriginalId = null;
        _repo.Update(receta);

        return Ok(receta.ToDetailDto());
    }
}
```

---

## Apéndice 2 — Archivos completos de configuración y soporte (in-memory)

### A2.1 `Program.cs` final (in-memory)

`Program.cs`

```csharp
using RecetasApi.Api.Repositories;

var builder = WebApplication.CreateBuilder(args);

builder.Services.AddControllers();

// Swagger UI
builder.Services.AddEndpointsApiExplorer();
builder.Services.AddSwaggerGen();

// DI: repositorios in-memory
builder.Services.AddSingleton<IRecetasRepository, RecetasInMemoryRepository>();
builder.Services.AddSingleton<IComentariosRepository, ComentariosInMemoryRepository>();

var app = builder.Build();

if (app.Environment.IsDevelopment())
{
    app.UseSwagger();
    app.UseSwaggerUI();
}

app.UseHttpsRedirection();

app.UseAuthorization();
app.MapControllers();
app.Run();
```

### A2.2 Comentarios: modelos + repositorio

`Models/Comentario.cs`

```csharp
namespace RecetasApi.Api.Models;

public class Comentario
{
    public int Id { get; set; }
    public int RecetaId { get; set; }
    public string Autor { get; set; } = string.Empty;
    public string Texto { get; set; } = string.Empty;
    public DateTime CreatedAt { get; set; } = DateTime.UtcNow;
}
```

`Contracts/ComentarioCreateDto.cs`

```csharp
using System.ComponentModel.DataAnnotations;

namespace RecetasApi.Api.Contracts;

public record ComentarioCreateDto(
    [Required]
    [MinLength(1)]
    [MaxLength(1000)]
    string Texto
);
```

`Contracts/ComentarioDto.cs`

```csharp
namespace RecetasApi.Api.Contracts;

public record ComentarioDto(
    int Id,
    string Autor,
    string Texto,
    DateTime CreatedAt
);
```

`Repositories/IComentariosRepository.cs`

```csharp
using RecetasApi.Api.Models;

namespace RecetasApi.Api.Repositories;

public interface IComentariosRepository
{
    IEnumerable<Comentario> GetByReceta(int recetaId);
    Comentario Add(Comentario comentario);
    Comentario? GetById(int id);
    void Delete(Comentario comentario);
}
```

`Repositories/ComentariosInMemoryRepository.cs`

```csharp
using RecetasApi.Api.Models;

namespace RecetasApi.Api.Repositories;

public class ComentariosInMemoryRepository : IComentariosRepository
{
    private readonly List<Comentario> _comentarios = new();
    private int _nextId = 1;

    public IEnumerable<Comentario> GetByReceta(int recetaId)
        => _comentarios.Where(c => c.RecetaId == recetaId)
                      .OrderBy(c => c.CreatedAt);

    public Comentario Add(Comentario comentario)
    {
        comentario.Id = _nextId++;
        comentario.CreatedAt = DateTime.UtcNow;
        _comentarios.Add(comentario);
        return comentario;
    }

    public Comentario? GetById(int id)
        => _comentarios.FirstOrDefault(c => c.Id == id);

    public void Delete(Comentario comentario)
        => _comentarios.Remove(comentario);
}
```

### A2.3 DTOs de salida + mapeo

`Contracts/RecetaSummaryDto.cs`

```csharp
namespace RecetasApi.Api.Contracts;

public record RecetaSummaryDto(
    int Id,
    string Titulo,
    string Autor,
    int Likes,
    bool EsHistorica,
    int? RecetaOriginalId
);
```

`Contracts/RecetaDetailDto.cs`

```csharp
namespace RecetasApi.Api.Contracts;

public record RecetaDetailDto(
    int Id,
    string Titulo,
    string Autor,
    List<IngredienteDto> Ingredientes,
    List<PasoDto> Pasos,
    int Likes,
    bool EsHistorica,
    int? RecetaOriginalId
);
```

`Mapping/RecetasMapping.cs`

```csharp
using RecetasApi.Api.Contracts;
using RecetasApi.Api.Models;

namespace RecetasApi.Api.Mapping;

public static class RecetasMapping
{
    public static Receta ToEntity(this RecetaCreateDto dto, string autor)
    {
        return new Receta
        {
            Titulo = dto.Titulo.Trim(),
            Autor = autor,
            Ingredientes = dto.Ingredientes
                .Select(i => new IngredienteItem { Ingrediente = i.Ingrediente.Trim(), Cantidad = i.Cantidad.Trim() })
                .ToList(),
            Pasos = dto.Pasos
                .Select(p => new Paso { Orden = p.Orden, Descripcion = p.Descripcion.Trim(), Minutos = p.Minutos })
                .OrderBy(p => p.Orden)
                .ToList(),
            EsHistorica = false,
            RecetaOriginalId = null
        };
    }

    public static void ApplyTo(this RecetaCreateDto dto, Receta entity)
    {
        entity.Titulo = dto.Titulo.Trim();

        entity.Ingredientes = dto.Ingredientes
            .Select(i => new IngredienteItem { Ingrediente = i.Ingrediente.Trim(), Cantidad = i.Cantidad.Trim() })
            .ToList();

        entity.Pasos = dto.Pasos
            .Select(p => new Paso { Orden = p.Orden, Descripcion = p.Descripcion.Trim(), Minutos = p.Minutos })
            .OrderBy(p => p.Orden)
            .ToList();
    }

    public static RecetaSummaryDto ToSummaryDto(this Receta entity)
    {
        return new RecetaSummaryDto(
            entity.Id,
            entity.Titulo,
            entity.Autor,
            entity.Likes.Count,
            entity.EsHistorica,
            entity.RecetaOriginalId
        );
    }

    public static RecetaDetailDto ToDetailDto(this Receta entity)
    {
        return new RecetaDetailDto(
            entity.Id,
            entity.Titulo,
            entity.Autor,
            entity.Ingredientes.Select(i => new IngredienteDto(i.Ingrediente, i.Cantidad)).ToList(),
            entity.Pasos.OrderBy(p => p.Orden).Select(p => new PasoDto(p.Orden, p.Descripcion, p.Minutos)).ToList(),
            entity.Likes.Count,
            entity.EsHistorica,
            entity.RecetaOriginalId
        );
    }
}
```

---




---

## Apéndice 3 — Persistencia con EF Core (SQLite y Docker DB)

Este apéndice sustituye los repositorios in-memory por **EF Core** y una base de datos real.

### A3.1 Paquetes y herramienta

SQLite:

```bash
dotnet add package Microsoft.EntityFrameworkCore.Sqlite
dotnet add package Microsoft.EntityFrameworkCore.Design
```

Herramienta `dotnet-ef`:

```bash
dotnet tool install --global dotnet-ef
```

### A3.2 ConnectionStrings

`appsettings.json` (SQLite):

```json
{
  "ConnectionStrings": {
    "Default": "Data Source=recetas.db"
  }
}
```

`appsettings.Development.json` (opcional):

```json
{
  "ConnectionStrings": {
    "Default": "Data Source=recetas.dev.db"
  }
}
```

En desarrollo prevalece `appsettings.Development.json`.

### A3.3 `DbContext`

`Data/RecetasDbContext.cs`

```csharp
using Microsoft.EntityFrameworkCore;
using RecetasApi.Api.Models;

namespace RecetasApi.Api.Data;

public class RecetasDbContext : DbContext
{
    public RecetasDbContext(DbContextOptions<RecetasDbContext> options) : base(options) { }

    public DbSet<Receta> Recetas => Set<Receta>();
    public DbSet<Comentario> Comentarios => Set<Comentario>();

    protected override void OnModelCreating(ModelBuilder modelBuilder)
    {
        base.OnModelCreating(modelBuilder);

        modelBuilder.Entity<Receta>()
            .OwnsMany(r => r.Ingredientes, b =>
            {
                b.WithOwner().HasForeignKey("RecetaId");
                b.Property<int>("Id");
                b.HasKey("Id");
            });

        modelBuilder.Entity<Receta>()
            .OwnsMany(r => r.Pasos, b =>
            {
                b.WithOwner().HasForeignKey("RecetaId");
                b.Property<int>("Id");
                b.HasKey("Id");
            });

        // Likes: conversión simple (docente) HashSet<string> <-> string
        modelBuilder.Entity<Receta>()
            .Property(r => r.Likes)
            .HasConversion(
                v => string.Join(';', v),
                v => v.Split(';', StringSplitOptions.RemoveEmptyEntries)
                      .ToHashSet(StringComparer.OrdinalIgnoreCase)
            );

        modelBuilder.Entity<Comentario>()
            .HasIndex(c => new { c.RecetaId, c.CreatedAt });
    }
}
```

### A3.4 Registro EF Core en `Program.cs`

```csharp
using Microsoft.EntityFrameworkCore;
using RecetasApi.Api.Data;

builder.Services.AddDbContext<RecetasDbContext>(opt =>
{
    var cs = builder.Configuration.GetConnectionString("Default");
    opt.UseSqlite(cs);
});
```

### A3.5 Repositorios EF Core

`Repositories/RecetasEfRepository.cs`

```csharp
using Microsoft.EntityFrameworkCore;
using RecetasApi.Api.Data;
using RecetasApi.Api.Models;

namespace RecetasApi.Api.Repositories;

public class RecetasEfRepository : IRecetasRepository
{
    private readonly RecetasDbContext _db;

    public RecetasEfRepository(RecetasDbContext db)
    {
        _db = db;
    }

    public IEnumerable<Receta> GetAll()
    {
        return _db.Recetas
            .Include(r => r.Ingredientes)
            .Include(r => r.Pasos)
            .AsNoTracking()
            .ToList();
    }

    public Receta? GetById(int id)
    {
        return _db.Recetas
            .Include(r => r.Ingredientes)
            .Include(r => r.Pasos)
            .FirstOrDefault(r => r.Id == id);
    }

    public void Add(Receta receta)
    {
        _db.Recetas.Add(receta);
        _db.SaveChanges();
    }

    public void Update(Receta receta)
    {
        _db.Recetas.Update(receta);
        _db.SaveChanges();
    }

    public void Delete(Receta receta)
    {
        _db.Recetas.Remove(receta);
        _db.SaveChanges();
    }

    public bool ExistsByAutorAndTitulo(string autor, string titulo)
    {
        return _db.Recetas.Any(r =>
            r.Autor.ToLower() == autor.ToLower() && r.Titulo.ToLower() == titulo.ToLower());
    }
}
```

`Repositories/ComentariosEfRepository.cs`

```csharp
using RecetasApi.Api.Data;
using RecetasApi.Api.Models;

namespace RecetasApi.Api.Repositories;

public class ComentariosEfRepository : IComentariosRepository
{
    private readonly RecetasDbContext _db;

    public ComentariosEfRepository(RecetasDbContext db)
    {
        _db = db;
    }

    public IEnumerable<Comentario> GetByReceta(int recetaId)
        => _db.Comentarios.Where(c => c.RecetaId == recetaId)
                          .OrderBy(c => c.CreatedAt)
                          .ToList();

    public Comentario Add(Comentario comentario)
    {
        comentario.CreatedAt = DateTime.UtcNow;
        _db.Comentarios.Add(comentario);
        _db.SaveChanges();
        return comentario;
    }

    public Comentario? GetById(int id)
        => _db.Comentarios.FirstOrDefault(c => c.Id == id);

    public void Delete(Comentario comentario)
    {
        _db.Comentarios.Remove(comentario);
        _db.SaveChanges();
    }
}
```

### A3.6 Sustituir DI (in-memory → EF)

En `Program.cs`, cambia:

```csharp
builder.Services.AddSingleton<IRecetasRepository, RecetasInMemoryRepository>();
builder.Services.AddSingleton<IComentariosRepository, ComentariosInMemoryRepository>();
```

Por:

```csharp
builder.Services.AddScoped<IRecetasRepository, RecetasEfRepository>();
builder.Services.AddScoped<IComentariosRepository, ComentariosEfRepository>();
```

### A3.7 Migraciones

En la carpeta del proyecto API:

```bash
dotnet ef migrations add InitialCreate
dotnet ef database update
```

Los ficheros de migración se suben al repositorio:
- `*_InitialCreate.cs`
- `*_InitialCreate.Designer.cs`
- `RecetasDbContextModelSnapshot.cs`

### A3.8 Cambiar a otras bases de datos (Docker)

- PostgreSQL:
  - `dotnet add package Npgsql.EntityFrameworkCore.PostgreSQL`
  - `opt.UseNpgsql(cs)`

- MySQL/MariaDB (Pomelo):
  - `dotnet add package Pomelo.EntityFrameworkCore.MySql`
  - `opt.UseMySql(cs, ServerVersion.AutoDetect(cs))`

- SQL Server:
  - `dotnet add package Microsoft.EntityFrameworkCore.SqlServer`
  - `opt.UseSqlServer(cs)`

Ejemplo `docker-compose.yml` (PostgreSQL):

```yaml
services:
  db:
    image: postgres:16
    environment:
      POSTGRES_USER: recetas
      POSTGRES_PASSWORD: recetas
      POSTGRES_DB: recetas
    ports:
      - "5432:5432"
    volumes:
      - pgdata:/var/lib/postgresql/data

volumes:
  pgdata:
```

Connection string (PostgreSQL):

```json
{
  "ConnectionStrings": {
    "Default": "Host=localhost;Port=5432;Database=recetas;Username=recetas;Password=recetas"
  }
}
```

Nota sobre MongoDB: no se integra con EF Core como ORM relacional. Se usa `MongoDB.Driver` y cambia el enfoque.


---

## Apéndice 4 — JWT y autorizaciones (docente, sencillo y funcional)

Este apéndice añade autenticación JWT para emitir tokens y aplicar autorización/roles, sin Identity.

### A4.1 Paquetes

```bash
dotnet add package Microsoft.AspNetCore.Authentication.JwtBearer
dotnet add package System.IdentityModel.Tokens.Jwt
```

### A4.2 Configuración `appsettings.json`

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

### A4.3 Clases de soporte

`Security/JwtOptions.cs`

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

`Security/TokenService.cs`

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

### A4.4 Endpoint de login (usuarios hardcodeados)

`Contracts/LoginRequest.cs`

```csharp
using System.ComponentModel.DataAnnotations;

namespace RecetasApi.Api.Contracts;

public record LoginRequest(
    [Required] string Username,
    [Required] string Password
);
```

`Contracts/LoginResponse.cs`

```csharp
namespace RecetasApi.Api.Contracts;

public record LoginResponse(
    string Token,
    string Username,
    string Role
);
```

`Controllers/AuthController.cs`

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

### A4.5 Proteger endpoints y obtener el usuario

Proteger todo el controlador:

```csharp
using Microsoft.AspNetCore.Authorization;

[Authorize]
public class RecetasController : ControllerBase
{
}
```

Para roles:

```csharp
[Authorize(Roles = "Admin")]
[HttpGet("admin/ping")]
public IActionResult AdminPing() => Ok(new { ok = true });
```

Si JWT está activo, sustituye `GetUsuario()` por:

```csharp
private string GetUsuario()
{
    return User.Identity?.Name ?? "demo";
}
```

### A4.6 Probar JWT en Swagger

1) Login:

`POST /api/auth/login`

```json
{ "username": "alice", "password": "alice123" }
```

2) Copiar token.

3) Swagger → **Authorize** → pegar:

```
Bearer <TOKEN>
```

4) Probar endpoints protegidos.

### A4.7 `Program.cs` completo con JWT (bloque único)

```csharp
using System.Text;
using Microsoft.AspNetCore.Authentication.JwtBearer;
using Microsoft.IdentityModel.Tokens;
using RecetasApi.Api.Repositories;
using RecetasApi.Api.Security;

var builder = WebApplication.CreateBuilder(args);

builder.Services.AddControllers();

// Swagger UI
builder.Services.AddEndpointsApiExplorer();
builder.Services.AddSwaggerGen();

// DI: repositorios (in-memory)
builder.Services.AddSingleton<IRecetasRepository, RecetasInMemoryRepository>();
builder.Services.AddSingleton<IComentariosRepository, ComentariosInMemoryRepository>();

// JWT options + token service
builder.Services.Configure<JwtOptions>(builder.Configuration.GetSection("Jwt"));
builder.Services.AddSingleton<TokenService>();

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
    app.UseSwagger();
    app.UseSwaggerUI();
}

app.UseHttpsRedirection();

// IMPORTANTE: auth antes de MapControllers
app.UseAuthentication();
app.UseAuthorization();

app.MapControllers();
app.Run();
```

### A4.8 Nota de seguridad (docente)

- La clave JWT debe guardarse en **secret manager / variables de entorno** en producción.
- En producción no se usan usuarios hardcodeados.
- JWT suele integrarse con un IdP (Keycloak, Entra ID, Auth0) usando OAuth/OIDC.
