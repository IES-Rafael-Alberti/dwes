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
        _db.Recetas.Where(r => !r.IsArchived).AsNoTracking().ToList();


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
    
    public bool ExistsByAutorAndTitulo(string autor, string titulo)
    {
        return _db.Recetas.Any(r =>
            r.Autor == autor &&
            r.Titulo == titulo
        );
    }
    
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
}