using System.ComponentModel.DataAnnotations;

namespace TodoApi.DTOs.Auth;

public class RegisterDto
{
    [Required(ErrorMessage = "El nombre de usuario es obligatorio.")]
    [StringLength(50, MinimumLength = 3,
        ErrorMessage = "El nombre de usuario debe tener entre 3 y 50 caracteres.")]
    public string Username { get; set; } = string.Empty;

    [Required(ErrorMessage = "El email es obligatorio.")]
    [EmailAddress(ErrorMessage = "El email no tiene un formato valido.")]
    [StringLength(200, ErrorMessage = "El email no puede superar 200 caracteres.")]
    public string Email { get; set; } = string.Empty;

    [Required(ErrorMessage = "La password es obligatoria.")]
    [StringLength(100, MinimumLength = 6,
        ErrorMessage = "La password debe tener al menos 6 caracteres.")]
    public string Password { get; set; } = string.Empty;
}
