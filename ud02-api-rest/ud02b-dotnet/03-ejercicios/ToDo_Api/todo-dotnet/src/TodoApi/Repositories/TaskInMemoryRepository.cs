using TodoApi.Exceptions;
using TodoApi.Models;

namespace TodoApi.Repositories;

// Implementacion en memoria: no persiste entre reinicios.
public class TasksInMemoryRepository : ITasksRepository
{
    private const int MaxPending = 10;
    private readonly List<TodoTask> _tasks = new();
    // Campo privado que lleva el siguiente id (simula auto-incremento).
    private int _nextId = 1;

    // Devuelve la misma lista en memoria.
    public IEnumerable<TodoTask> GetAll(TaskStatus? status = null)
    {
        var query = _tasks.AsEnumerable();
        if (status is not null)
            query = query.Where(t => t.Status == status);

        return query
            .OrderBy(t => t.Status == TaskStatus.Completed ? 1 : 0)
            .ThenByDescending(t => t.CreationDate);
    }

    // Devuelve las tareas de un usuario concreto.
    public IEnumerable<TodoTask> GetAllByUser(int userId, TaskStatus? status = null)
    {
        var query = _tasks.Where(t => t.UserId == userId);
        if (status is not null)
            query = query.Where(t => t.Status == status);

        return query
            .OrderBy(t => t.Status == TaskStatus.Completed ? 1 : 0)
            .ThenByDescending(t => t.CreationDate);
    }

    // Busca por id usando LINQ.
    public TodoTask? GetById(int id)
    {
        return _tasks.FirstOrDefault(t => t.Id == id);
    }

    public TodoTask? GetByIdForUser(int id, int userId)
    {
        return _tasks.FirstOrDefault(t => t.Id == id && t.UserId == userId);
    }

    // Asigna un id incremental y guarda.
    public TodoTask Add(TodoTask task)
    {
        if (task.Status == TaskStatus.Pending && CountPending() >= MaxPending)
            throw new ConflictException(
                errorCode: "MAX_PENDING_REACHED",
                message: $"No se pueden crear más de {MaxPending} tareas pendientes.");

        task.Id = _nextId++;
        _tasks.Add(task);
        return task;
    }

    public int CountPending()
    {
        return _tasks.Count(t => t.Status == TaskStatus.Pending);
    }

    // Sustituye los campos editables si existe.
    public bool Update(int id, TodoTask task)
    {
        var existing = GetById(id);
        if (existing == null)
            return false;

        if (task.Status == TaskStatus.Pending && existing.Status != TaskStatus.Pending
            && CountPending() >= MaxPending)
            throw new ConflictException(
                errorCode: "MAX_PENDING_REACHED",
                message: $"No se pueden crear más de {MaxPending} tareas pendientes.");

        existing.Title = task.Title;
        existing.Description = task.Description;
        existing.DueDate = task.DueDate;
        existing.Status = task.Status;
        return true;
    }

    // Elimina por id si existe.
    public bool Delete(int id)
    {
        var task = GetById(id);
        if (task == null)
            return false;

        _tasks.Remove(task);
        return true;
    }
}
