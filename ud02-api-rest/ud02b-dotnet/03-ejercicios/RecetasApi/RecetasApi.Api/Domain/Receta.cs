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
    
    public long? OriginalRecetaId { get; set; }  // null si es original
    public bool IsCopy => OriginalRecetaId.HasValue;
    
    public bool IsArchived { get; set; }
    public DateTime? ArchivedAt { get; set; }


}