using Microsoft.EntityFrameworkCore;
using TodoApi.Data;
using TodoApi.Exceptions;
using TodoApi.Models;

namespace TodoApi.Repositories;

public class TasksEfRepository : ITasksRepository
{
    private const int MaxPending = 10;
    private readonly TodoDbContext _db;

    public TasksEfRepository(TodoDbContext db)
    {
        _db = db;
    }

    public IEnumerable<TodoTask> GetAll(TaskStatus? status = null)
    {
        // AsNoTracking: lectura sin seguimiento de cambios (mas rapido para GET).
        var query = _db.Tasks.AsNoTracking().AsQueryable();
        if (status is not null)
            query = query.Where(t => t.Status == status);

        return query
            .OrderBy(t => t.Status == TaskStatus.Completed ? 1 : 0)
            .ThenByDescending(t => t.CreationDate)
            .ToList();
    }

    public IEnumerable<TodoTask> GetAllByUser(int userId, TaskStatus? status = null)
    {
        var query = _db.Tasks.AsNoTracking()
            .Where(t => t.UserId == userId)
            .AsQueryable();

        if (status is not null)
            query = query.Where(t => t.Status == status);

        return query
            .OrderBy(t => t.Status == TaskStatus.Completed ? 1 : 0)
            .ThenByDescending(t => t.CreationDate)
            .ToList();
    }

    public TodoTask? GetById(int id)
    {
        // FirstOrDefault devuelve null si no existe.
        return _db.Tasks.AsNoTracking()
            .FirstOrDefault(t => t.Id == id);
    }

    public TodoTask? GetByIdForUser(int id, int userId)
    {
        return _db.Tasks.AsNoTracking()
            .FirstOrDefault(t => t.Id == id && t.UserId == userId);
    }

    public TodoTask Add(TodoTask task)
    {
        if (task.Status == TaskStatus.Pending && CountPending() >= MaxPending)
            throw new ConflictException(
                errorCode: "MAX_PENDING_REACHED",
                message: $"No se pueden crear más de {MaxPending} tareas pendientes.");

        _db.Tasks.Add(task);
        _db.SaveChanges();
        return task;
    }

    public int CountPending()
    {
        return _db.Tasks.Count(t => t.Status == TaskStatus.Pending);
    }

    public bool Update(int id, TodoTask task)
    {
        // Find usa la clave primaria y puede devolver null si no existe.
        var existing = _db.Tasks.Find(id);
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

        _db.SaveChanges();
        return true;
    }

    public bool Delete(int id)
    {
        var task = _db.Tasks.Find(id);
        if (task == null)
            return false;

        _db.Tasks.Remove(task);
        _db.SaveChanges();
        return true;
    }
}
