using System.ComponentModel.DataAnnotations;

namespace RecetasApi.Api.Contracts;

public record CreateComentarioRequest(
    [Required]
    [MinLength(1)]
    [MaxLength(1000)]
    string Texto
);