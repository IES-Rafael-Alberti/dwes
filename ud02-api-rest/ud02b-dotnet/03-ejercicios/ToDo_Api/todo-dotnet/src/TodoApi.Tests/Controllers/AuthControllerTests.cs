using Microsoft.AspNetCore.Mvc;
using Moq;
using TodoApi.Controllers;
using TodoApi.DTOs.Auth;
using TodoApi.Services;
using Xunit;

namespace TodoApi.Tests.Controllers;

public class AuthControllerTests
{
    private readonly Mock<IAuthService> _authMock;
    private readonly AuthController _controller;

    public AuthControllerTests()
    {
        _authMock = new Mock<IAuthService>();
        _controller = new AuthController(_authMock.Object);
    }

    [Fact]
    public void Register_ReturnsOk()
    {
        var dto = new RegisterDto
        {
            Username = "ana",
            Email = "ana@example.com",
            Password = "123456"
        };
        _authMock.Setup(s => s.Register(dto)).Returns(new AuthResponseDto
        {
            Token = "jwt-token",
            ExpiresIn = 3600
        });

        var result = _controller.Register(dto);

        var ok = Assert.IsType<OkObjectResult>(result.Result);
        Assert.IsType<AuthResponseDto>(ok.Value);
    }

    [Fact]
    public void Login_ReturnsOk()
    {
        var dto = new LoginDto
        {
            Email = "ana@example.com",
            Password = "123456"
        };
        _authMock.Setup(s => s.Login(dto)).Returns(new AuthResponseDto
        {
            Token = "jwt-token",
            ExpiresIn = 3600
        });

        var result = _controller.Login(dto);

        var ok = Assert.IsType<OkObjectResult>(result.Result);
        Assert.IsType<AuthResponseDto>(ok.Value);
    }
}
