using System.ComponentModel.DataAnnotations;

namespace TodoApi.DTOs.Auth;

public class LoginDto
{
    [Required(ErrorMessage = "El email es obligatorio.")]
    [EmailAddress(ErrorMessage = "El email no tiene un formato valido.")]
    public string Email { get; set; } = string.Empty;

    [Required(ErrorMessage = "La password es obligatoria.")]
    public string Password { get; set; } = string.Empty;
}
