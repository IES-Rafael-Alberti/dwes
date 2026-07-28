namespace RecetasApi.Api.Contracts;

public record RecetaSummaryDto(
    long Id,
    string Titulo,
    string Autor,
    int LikesCount,
    long? OriginalRecetaId,
    bool IsArchived
);