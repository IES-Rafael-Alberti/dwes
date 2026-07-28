using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using System.Security.Claims;
using System.IdentityModel.Tokens.Jwt;
using TodoApi.DTOs;
using TodoApi.Models;
using TodoApi.Services;

namespace TodoApi.Controllers;

[ApiController]
// [Route] define la ruta base del recurso.
[Route("api/tasks")]
// Protegido por JWT (equivalente a @PreAuthorize en Spring).
[Authorize]
// Hereda de ControllerBase para usar helpers de API (Ok, CreatedAtAction, etc.).
public class TasksController : ControllerBase
{
    private readonly ITasksService _service;

    public TasksController(ITasksService service)
    {
        // Inyeccion de dependencias (DI): el framework nos pasa el servicio.
        _service = service;
    }

    [HttpGet]
    public ActionResult<IEnumerable<TaskReadDto>> GetAll([FromQuery] TaskStatus? status)
    {
        var userId = GetUserId();
        var role = GetUserRole();
        // 200 OK con la lista de tareas.
        return Ok(_service.GetAll(userId, role, status));
    }

    [HttpGet("{id}")]
    public ActionResult<TaskReadDto> GetById(int id)
    {
        var userId = GetUserId();
        var role = GetUserRole();
        // {id} en la ruta se enlaza al parametro id.
        return Ok(_service.GetById(id, userId, role));
    }

    [HttpPost]
    public ActionResult<TaskReadDto> Create(TaskCreateDto dto)
    {
        // CreatedAtAction genera 201 con Location apuntando al GET por id.
        var userId = GetUserId();
        var created = _service.Create(dto, userId);
        return CreatedAtAction(nameof(GetById), new { id = created.Id }, created);
    }

    [HttpPut("{id}")]
    public IActionResult Update(int id, TaskUpdateDto dto)
    {
        var userId = GetUserId();
        var role = GetUserRole();
        // 204 NoContent cuando la actualizacion es correcta.
        _service.Update(id, dto, userId, role);
        return NoContent();
    }

    [HttpDelete("{id}")]
    public IActionResult Delete(int id)
    {
        var userId = GetUserId();
        var role = GetUserRole();
        // 204 NoContent al borrar.
       _service.Delete(id, userId, role);
       return NoContent();
    }

    private int GetUserId()
    {
        // En JWT guardamos el id en "sub"; tambien puede mapearse a NameIdentifier.
        var sub = User.FindFirstValue(JwtRegisteredClaimNames.Sub)
                  ?? User.FindFirstValue(ClaimTypes.NameIdentifier);

        if (string.IsNullOrWhiteSpace(sub) || !int.TryParse(sub, out var userId))
            throw new TodoApi.Exceptions.UnauthorizedException(
                "INVALID_TOKEN",
                "Token invalido o sin identificador de usuario.");

        return userId;
    }

    private UserRole GetUserRole()
    {
        var roleClaim = User.FindFirstValue(ClaimTypes.Role)
            ?? User.FindFirstValue("role");

        if (string.IsNullOrWhiteSpace(roleClaim)
            || !Enum.TryParse<UserRole>(roleClaim, ignoreCase: true, out var role))
        {
            throw new TodoApi.Exceptions.UnauthorizedException(
                "INVALID_TOKEN_ROLE",
                "Token invalido o sin rol de usuario.");
        }

        return role;
    }
}
