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
    private const int ARCHIVE_LIKES_THRESHOLD = 3; // en real: 10 o más

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
        // Regla de negocio: título único por autor
        if (_repo.ExistsByAutorAndTitulo(autor, request.Titulo))
        {
            return Conflict(new
            {
                error = "RECETA_DUPLICADA",
                message = "Ya existe una receta con ese título para este autor."
            });
        }

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
        if (existing.IsArchived)
        {
            return Conflict(new { error = "RECETA_HISTORICA", message = "La receta está en histórico y no se puede modificar." });
        }

        request.Apply(existing);

        var ok = _repo.Update(existing);
        if (!ok) return NotFound();

        return Ok(existing.ToDetailDto());
    }

    // DELETE /api/recetas/{id}
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

    
    private string GetUsuario()
    {
        // Permite probar usuarios distintos en Swagger: Header "X-User"
        if (Request.Headers.TryGetValue("X-User", out var user) && !string.IsNullOrWhiteSpace(user))
            return user.ToString().Trim();

        return "demo";
    }
    
    [HttpPost("{id:long}/likes")]
    public IActionResult Like(long id)
    {
        var usuario = GetUsuario();
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

        var result = _repo.AddLike(id, usuario);

        return result switch
        {
            LikeResult.Ok => NoContent(),
            LikeResult.NotFound => NotFound(),
            LikeResult.AlreadyLiked => Conflict(new { error = "YA_LIKEADA", message = "El usuario ya dio like a esta receta." }),
            _ => StatusCode(500)
        };
    }
    
    [HttpDelete("{id:long}/likes")]
    public IActionResult Unlike(long id)
    {
        var usuario = GetUsuario();
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

        var result = _repo.RemoveLike(id, usuario);

        return result switch
        {
            LikeResult.Ok => NoContent(),
            LikeResult.NotFound => NotFound(),
            LikeResult.NotLiked => Conflict(new { error = "NO_LIKEADA", message = "El usuario no había dado like a esta receta." }),
            _ => StatusCode(500)
        };
    }
    
    [HttpGet("{id:long}/likes")]
    public IActionResult LikesCount(long id)
    {
        // Si quieres, primero comprueba que la receta existe para devolver 404 coherente.
        var receta = _repo.GetById(id);
        if (receta is null) return NotFound();

        return Ok(new { recetaId = id, likes = _repo.GetLikesCount(id) });
    }
    
    [HttpGet("{id:long}/comentarios")]
    public IActionResult GetComentarios(long id)
    {
        // 404 coherente si la receta no existe
        if (_repo.GetById(id) is null) return NotFound();

        var result = _repo.GetComentarios(id).Select(c => c.ToDto());
        return Ok(result);
    }
    
    [HttpPost("{id:long}/comentarios")]
    public IActionResult AddComentario(long id, [FromBody] CreateComentarioRequest request)
    {
        var usuario = GetUsuario();
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

        var result = _repo.AddComentario(id, usuario, request.Texto);

        return result switch
        {
            CommentCreateResult.Ok => NoContent(), //Created(),  
            CommentCreateResult.NotFound => NotFound(),
            _ => StatusCode(500)
        };
    }
    
    [HttpDelete("{id:long}/comentarios/{comentarioId:long}")]
    public IActionResult DeleteComentario(long id, long comentarioId)
    {
        var usuario = GetUsuario();
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
        var result = _repo.DeleteComentario(id, comentarioId, usuario);

        return result switch
        {
            CommentResult.Ok => NoContent(),
            CommentResult.NotFound => NotFound(),
            CommentResult.Forbidden => Forbid(),
            _ => StatusCode(500)
        };
    }
    
    [HttpPost("{id:long}/copiar")]
    public IActionResult Copy(long id)
    {
        var usuario = GetUsuario();

        var copy = _repo.CopyReceta(id, usuario);
        if (copy is null) return NotFound();

        return CreatedAtAction(nameof(GetById), new { id = copy.Id }, copy.ToDetailDto());
    }
    
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
    
    [HttpGet("historico")]
    public IActionResult GetHistorico()
    {
        var result = _repo.GetHistorico().Select(r => r.ToSummaryDto());
        return Ok(result);
    }
}