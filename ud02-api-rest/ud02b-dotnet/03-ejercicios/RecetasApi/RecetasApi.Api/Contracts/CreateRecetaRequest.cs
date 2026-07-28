using System.ComponentModel.DataAnnotations;

namespace RecetasApi.Api.Contracts;

public record CreateRecetaRequest(
    [Required]
    [MinLength(3)]
    string Titulo,
    [Required]
    [MinLength(1)]
    List<IngredienteItemDto> Ingredientes,
    
    [Required]
    [MinLength(1)]
    List<PasoDto> Pasos
);