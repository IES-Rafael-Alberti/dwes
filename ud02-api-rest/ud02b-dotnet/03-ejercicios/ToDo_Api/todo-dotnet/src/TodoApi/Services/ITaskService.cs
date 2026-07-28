using TodoApi.DTOs;
using TodoApi.Models;

namespace TodoApi.Services;

public interface ITasksService
{
    IEnumerable<TaskReadDto> GetAll(int userId, UserRole role, TaskStatus? status = null);
    TaskReadDto? GetById(int id, int userId, UserRole role);
    TaskReadDto Create(TaskCreateDto dto, int userId);
    void Update(int id, TaskUpdateDto dto, int userId, UserRole role);
    void Delete(int id, int userId, UserRole role);
}
