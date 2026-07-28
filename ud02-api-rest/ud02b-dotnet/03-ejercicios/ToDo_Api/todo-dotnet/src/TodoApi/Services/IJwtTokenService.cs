using TodoApi.Models;

namespace TodoApi.Services;

// Generador de JWT para separar autenticacion de la logica de negocio.
public interface IJwtTokenService
{
    string CreateToken(User user);
}
