using TodoApi.DTOs.Auth;

namespace TodoApi.Services;

// Servicio de autenticacion (equivalente a @Service).
public interface IAuthService
{
    AuthResponseDto Register(RegisterDto dto);
    AuthResponseDto Login(LoginDto dto);
}
