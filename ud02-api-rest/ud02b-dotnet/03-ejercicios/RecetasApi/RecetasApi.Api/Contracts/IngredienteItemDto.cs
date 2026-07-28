using System.ComponentModel.DataAnnotations;

namespace RecetasApi.Api.Contracts;

public record IngredienteItemDto
(
    [Required]
    string Nombre,

    [Required]
    string Cantidad
);