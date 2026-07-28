using RecetasApi.Api.Domain;

namespace RecetasApi.Api.Infrastructure;

public interface IRecetasRepository
{
    IEnumerable<Receta> GetAll();
    Receta? GetById(long id);
    Receta Add(Receta receta);
    bool Update(Receta receta);
    bool Delete(long id);
    LikeResult AddLike(long recetaId, string usuario);
    LikeResult RemoveLike(long recetaId, string usuario);
    int GetLikesCount(long recetaId);
    
    IEnumerable<RecetaComentario> GetComentarios(long recetaId);
    CommentCreateResult AddComentario(long recetaId, string usuario, string texto);
    CommentResult DeleteComentario(long recetaId, long comentarioId, string usuario);
    // Validación de negocio
    bool ExistsByAutorAndTitulo(string autor, string titulo);
    
    // Copias
    Receta? CopyReceta(long originalId, string newAutor);
    bool DetachCopy(long recetaId, string autor); // desvincula copia si eres autor
    
    IEnumerable<Receta> GetHistorico();
    bool Archive(long id);
}