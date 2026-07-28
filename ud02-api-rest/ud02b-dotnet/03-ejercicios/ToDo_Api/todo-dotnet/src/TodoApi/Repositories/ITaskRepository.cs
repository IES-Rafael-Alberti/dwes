using TodoApi.Models;

namespace TodoApi.Repositories;

// Contrato (interface): define que metodos debe tener cualquier repositorio.
public interface ITasksRepository
{
    // Devuelve todas las tareas.
    IEnumerable<TodoTask> GetAll(TaskStatus? status = null);
    // Devuelve todas las tareas de un usuario.
    IEnumerable<TodoTask> GetAllByUser(int userId, TaskStatus? status = null);
    // Busca una tarea por id.
    TodoTask? GetById(int id);
    // Busca una tarea por id y usuario (propiedad).
    TodoTask? GetByIdForUser(int id, int userId);
    // Inserta una tarea nueva.
    TodoTask Add(TodoTask task);
    // Cuenta las tareas pendientes.
    int CountPending();
    // Actualiza una tarea existente.
    bool Update(int id, TodoTask task);
    // Elimina una tarea.
    bool Delete(int id);
}
