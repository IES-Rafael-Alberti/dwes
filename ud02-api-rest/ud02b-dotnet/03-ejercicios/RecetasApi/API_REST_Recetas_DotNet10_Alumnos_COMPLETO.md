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

## Apéndice 3 — Persistencia con EF Core (SQLite y Docker DB)

(Ver cuerpo del documento para el contenido completo del apéndice: paquetes, DbContext, repositorios EF, migraciones y Docker.)

---

## Apéndice 4 — JWT y autorizaciones (docente, sencillo y funcional)

(Ver cuerpo del documento para el contenido completo del apéndice: TokenService, AuthController, atributos [Authorize] y Program.cs con JWT.)

