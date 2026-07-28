using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using TodoApi.DTOs.Auth;
using TodoApi.Services;

namespace TodoApi.Controllers;

// Controlador de autenticacion (equivalente a @RestController en Spring).
[ApiController]
[Route("api/auth")]
public class AuthController : ControllerBase
{
    private readonly IAuthService _auth;

    public AuthController(IAuthService auth)
    {
        _auth = auth;
    }

    [HttpPost("register")]
    [AllowAnonymous]
    public ActionResult<AuthResponseDto> Register(RegisterDto dto)
    {
        var response = _auth.Register(dto);
        return Ok(response);
    }

    [HttpPost("login")]
    [AllowAnonymous]
    public ActionResult<AuthResponseDto> Login(LoginDto dto)
    {
        var response = _auth.Login(dto);
        return Ok(response);
    }

    // Logout en Iteracion 2: el cliente borra el token.
    [HttpPost("logout")]
    public IActionResult Logout()
    {
        return NoContent();
    }
}
