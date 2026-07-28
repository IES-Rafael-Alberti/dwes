using RecetasApi.Api.Domain;

namespace RecetasApi.Api.Infrastructure;

public class InMemoryRecetasRepository : IRecetasRepository
{
    private readonly List<Receta> _data = new();
    private long _nextId = 1;

    private readonly List<RecetaLike> _likes = new();
    private long _nextLikeId = 1;
    
    private readonly List<RecetaComentario> _comentarios = new();
    private long _nextComentarioId = 1;
    
    public IEnumerable<Receta> GetAll() => _data.Where(r => !r.IsArchived);
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
        _likes.RemoveAll(l => l.RecetaId == id);
        _comentarios.RemoveAll(c => c.RecetaId == id);
        return true;
    }
    
    public bool ExistsByAutorAndTitulo(string autor, string titulo)
    {
        return _data.Any(r =>
            r.Autor == autor &&
            r.Titulo == titulo
        );
    }
    
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
}