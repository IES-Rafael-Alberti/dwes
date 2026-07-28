using System.ComponentModel.DataAnnotations;

namespace RecetasApi.Api.Contracts;

public record PasoDto
(
    [Range(1, int.MaxValue)]
    int Orden,

    [Required]
    string Descripcion,

    [Range(0, 1440)]
    int? DuracionEstimadaMin
);