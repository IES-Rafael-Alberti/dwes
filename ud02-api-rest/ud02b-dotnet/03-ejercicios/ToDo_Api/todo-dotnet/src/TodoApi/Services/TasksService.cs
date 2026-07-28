using TodoApi.DTOs;
using TodoApi.Mapping;
using TodoApi.Models;
using TodoApi.Repositories;
using TodoApi.Exceptions;


namespace TodoApi.Services;

// Implementa la interfaz para poder cambiar la logica sin tocar el controlador.
public class TasksService : ITasksService
{
    private readonly ITasksRepository _repository;

    public TasksService(ITasksRepository repository)
    {
        // DI: el repositorio concreto se resuelve en Program.cs.
        _repository = repository;
    }

    public IEnumerable<TaskReadDto> GetAll(int userId, UserRole role, TaskStatus? status = null)
    {
        // Supervisor/Admin ven todas; User solo las suyas.
        var tasks = role is UserRole.Supervisor or UserRole.Admin
            ? _repository.GetAll(status)
            : _repository.GetAllByUser(userId, status);

        // Select aplica el mapeo a cada elemento (similar a stream().map()).
        return tasks
            .Select(t => t.ToReadDto());
    }

    public TaskReadDto? GetById(int id, int userId, UserRole role)
    {
        // Lanza excepcion de dominio si no existe (se traduce a 404).
        var task = _repository.GetById(id);
        if (task == null)
            throw new NotFoundException($"No existe la tarea con ID {id}.");
        if (role == UserRole.User && task.UserId != userId)
            throw new ForbiddenException(
                errorCode: "TASK_FORBIDDEN",
                message: "No tienes permisos para acceder a esta tarea.");
        return task.ToReadDto();
    }

    public TaskReadDto Create(TaskCreateDto dto, int userId)
    {
        if (dto.DueDate is null)
            throw new BadRequestException(
                errorCode: "DUE_DATE_REQUIRED",
                message: "La fecha límite (DueDate) es obligatoria.");

        var now = DateTime.UtcNow;
        if (dto.DueDate.Value < now)
            throw new BadRequestException(
                errorCode: "DUE_DATE_INVALID",
                message: "La fecha límite (DueDate) no puede ser anterior a la fecha actual.");

        // DTO -> modelo de dominio.
        var task = dto.ToEntity();
        task.UserId = userId;

        var created = _repository.Add(task);
        return created.ToReadDto();
    }

    public void Update(int id, TaskUpdateDto dto, int userId, UserRole role)
    {
        if (dto.DueDate is null)
            throw new BadRequestException(
                errorCode: "DUE_DATE_REQUIRED",
                message: "La fecha límite (DueDate) es obligatoria.");

        var existing = _repository.GetById(id);
        if (existing == null)
            throw new NotFoundException($"No existe la tarea con ID {id}.");
        var isOwner = existing.UserId == userId;

        if (role == UserRole.User && !isOwner)
            throw new ForbiddenException(
                errorCode: "TASK_FORBIDDEN",
                message: "No tienes permisos para modificar esta tarea.");

        // Supervisor puede editar ajenas, pero no completarlas.
        if (role == UserRole.Supervisor && !isOwner && dto.Status == TaskStatus.Completed)
            throw new ForbiddenException(
                errorCode: "SUPERVISOR_CANNOT_COMPLETE_FOREIGN_TASK",
                message: "Supervisor no puede marcar como Completed tareas ajenas.");

        if (dto.DueDate.Value < existing.CreationDate)
            throw new BadRequestException(
                errorCode: "DUE_DATE_INVALID",
                message: "La fecha límite (DueDate) no puede ser anterior a la fecha de creación.");

        // Pasamos un modelo "nuevo" con los datos editados.
        var updated = _repository.Update(id, task: new TodoTask
        {
            Title = dto.Title.Trim(),
            Description = string.IsNullOrWhiteSpace(dto.Description) ? null : dto.Description.Trim(),
            DueDate = dto.DueDate.Value,
            Status = dto.Status
        });

        if (!updated)
            throw new NotFoundException($"No existe la tarea con ID {id}.");
    }

    public void Delete(int id, int userId, UserRole role)
    {
        var existing = _repository.GetById(id);
        if (existing == null)
            throw new NotFoundException($"No existe la tarea con ID {id}.");

        var isOwner = existing.UserId == userId;
        if (role == UserRole.User && !isOwner)
            throw new ForbiddenException(
                errorCode: "TASK_FORBIDDEN",
                message: "No tienes permisos para eliminar esta tarea.");

        // Supervisor no puede borrar tareas ajenas.
        if (role == UserRole.Supervisor && !isOwner)
            throw new ForbiddenException(
                errorCode: "SUPERVISOR_CANNOT_DELETE_FOREIGN_TASK",
                message: "Supervisor no puede borrar tareas ajenas.");

        var deleted = _repository.Delete(id);
        if (!deleted)
            throw new NotFoundException($"No existe la tarea con ID {id}.");
    }

    // 🔹 Mapeo manual (sin AutoMapper) en TasksMapping.
}
