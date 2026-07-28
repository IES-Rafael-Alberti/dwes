using Microsoft.AspNetCore.Identity;
using Microsoft.Extensions.Options;
using Moq;
using TodoApi.DTOs.Auth;
using TodoApi.Exceptions;
using TodoApi.Models;
using TodoApi.Options;
using TodoApi.Repositories;
using TodoApi.Services;

namespace TodoApi.Tests.Services;

public class AuthServiceTests
{
    private readonly Mock<IUsersRepository> _usersMock;
    private readonly Mock<IJwtTokenService> _jwtMock;
    private readonly AuthService _service;

    public AuthServiceTests()
    {
        _usersMock = new Mock<IUsersRepository>();
        _jwtMock = new Mock<IJwtTokenService>();

        var options = Microsoft.Extensions.Options.Options.Create(
            new JwtOptions { ExpiresInSeconds = 3600 });
        _service = new AuthService(_usersMock.Object, _jwtMock.Object, options);
    }

    [Fact]
    public void Register_WhenEmailExists_ThrowsConflict()
    {
        _usersMock.Setup(r => r.GetByEmail("ana@example.com"))
            .Returns(new User { Id = 1, Email = "ana@example.com", Username = "ana" });

        var dto = new RegisterDto
        {
            Username = "ana",
            Email = "ana@example.com",
            Password = "123456"
        };

        Assert.Throws<ConflictException>(() => _service.Register(dto));
    }

    [Fact]
    public void Register_WhenOk_ReturnsToken()
    {
        _usersMock.Setup(r => r.GetByEmail(It.IsAny<string>())).Returns((User?)null);
        _usersMock.Setup(r => r.GetByUsername(It.IsAny<string>())).Returns((User?)null);
        _usersMock.Setup(r => r.Add(It.IsAny<User>()))
            .Returns((User u) =>
            {
                u.Id = 1;
                return u;
            });
        _jwtMock.Setup(j => j.CreateToken(It.IsAny<User>())).Returns("jwt-token");

        var dto = new RegisterDto
        {
            Username = "ana",
            Email = "ana@example.com",
            Password = "123456"
        };

        var result = _service.Register(dto);

        Assert.Equal("jwt-token", result.Token);
        Assert.Equal(3600, result.ExpiresIn);
    }

    [Fact]
    public void Login_WhenInvalidPassword_ThrowsUnauthorized()
    {
        var hasher = new PasswordHasher<User>();
        var user = new User { Id = 1, Email = "ana@example.com", Username = "ana" };
        user.PasswordHash = hasher.HashPassword(user, "correct-password");

        _usersMock.Setup(r => r.GetByEmail("ana@example.com")).Returns(user);

        var dto = new LoginDto
        {
            Email = "ana@example.com",
            Password = "wrong-password"
        };

        Assert.Throws<UnauthorizedException>(() => _service.Login(dto));
    }

    [Fact]
    public void Login_WhenOk_ReturnsToken()
    {
        var hasher = new PasswordHasher<User>();
        var user = new User { Id = 1, Email = "ana@example.com", Username = "ana" };
        user.PasswordHash = hasher.HashPassword(user, "123456");

        _usersMock.Setup(r => r.GetByEmail("ana@example.com")).Returns(user);
        _jwtMock.Setup(j => j.CreateToken(user)).Returns("jwt-token");

        var dto = new LoginDto
        {
            Email = "ana@example.com",
            Password = "123456"
        };

        var result = _service.Login(dto);

        Assert.Equal("jwt-token", result.Token);
        Assert.Equal(3600, result.ExpiresIn);
    }
}
