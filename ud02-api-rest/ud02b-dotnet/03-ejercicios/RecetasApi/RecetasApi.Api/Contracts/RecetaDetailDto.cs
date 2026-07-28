namespace RecetasApi.Api.Contracts;

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