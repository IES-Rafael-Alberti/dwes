using Microsoft.AspNetCore.Identity;
using Microsoft.Extensions.Options;
using TodoApi.DTOs.Auth;
using TodoApi.Exceptions;
using TodoApi.Models;
using TodoApi.Options;
using TodoApi.Repositories;

namespace TodoApi.Services;

// Servicio de autenticacion (equivalente a un @Service en Spring).
public class AuthService : IAuthService
{
    private readonly IUsersRepository _users;
    private readonly IJwtTokenService _jwt;
    private readonly JwtOptions _jwtOptions;
    private readonly PasswordHasher<User> _hasher;

    public AuthService(
        IUsersRepository users,
        IJwtTokenService jwt,
        IOptions<JwtOptions> jwtOptions)
    {
        _users = users;
        _jwt = jwt;
        _jwtOptions = jwtOptions.Value;
        _hasher = new PasswordHasher<User>();
    }

    public AuthResponseDto Register(RegisterDto dto)
    {
        var email = dto.Email.Trim().ToLowerInvariant();
        var username = dto.Username.Trim();

        if (_users.GetByEmail(email) != null)
            throw new ConflictException("USER_ALREADY_EXISTS", "El email ya esta registrado.");

        if (_users.GetByUsername(username) != null)
            throw new ConflictException("USER_ALREADY_EXISTS", "El nombre de usuario ya existe.");

        var user = new User
        {
            Email = email,
            Username = username,
            Role = UserRole.User
        };

        user.PasswordHash = _hasher.HashPassword(user, dto.Password);

        var created = _users.Add(user);
        var token = _jwt.CreateToken(created);

        return new AuthResponseDto
        {
            Token = token,
            ExpiresIn = _jwtOptions.ExpiresInSeconds
        };
    }

    public AuthResponseDto Login(LoginDto dto)
    {
        var email = dto.Email.Trim().ToLowerInvariant();
        var user = _users.GetByEmail(email);

        if (user == null)
            throw new UnauthorizedException("INVALID_CREDENTIALS", "Email o password incorrectos.");

        var result = _hasher.VerifyHashedPassword(user, user.PasswordHash, dto.Password);
        if (result == PasswordVerificationResult.Failed)
            throw new UnauthorizedException("INVALID_CREDENTIALS", "Email o password incorrectos.");

        var token = _jwt.CreateToken(user);

        return new AuthResponseDto
        {
            Token = token,
            ExpiresIn = _jwtOptions.ExpiresInSeconds
        };
    }
}
