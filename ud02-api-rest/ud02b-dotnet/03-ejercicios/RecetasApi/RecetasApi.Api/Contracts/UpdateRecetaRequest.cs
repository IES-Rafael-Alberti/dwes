namespace RecetasApi.Api.Contracts;

public record UpdateRecetaRequest(
    string Titulo,
    List<IngredienteItemDto> Ingredientes,
    List<PasoDto> Pasos
);