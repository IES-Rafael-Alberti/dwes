using TodoApi.DTOs;
using TodoApi.Models;

namespace TodoApi.Mapping;

public static class TasksMapping
{
    public static TodoTask ToEntity(this TaskCreateDto dto)
    {
        return new TodoTask
        {
            Title = dto.Title.Trim(),
            Description = string.IsNullOrWhiteSpace(dto.Description) ? null : dto.Description.Trim(),
            CreationDate = DateTime.UtcNow,
            DueDate = dto.DueDate!.Value,
            Status = dto.Status
        };
    }

    public static void ApplyUpdate(this TodoTask entity, TaskUpdateDto dto)
    {
        entity.Title = dto.Title.Trim();
        entity.Description = string.IsNullOrWhiteSpace(dto.Description) ? null : dto.Description.Trim();
        entity.DueDate = dto.DueDate!.Value;
        entity.Status = dto.Status;
    }

    public static TaskReadDto ToReadDto(this TodoTask entity)
    {
        return new TaskReadDto
        {
            Id = entity.Id,
            Title = entity.Title,
            Description = entity.Description,
            CreationDate = entity.CreationDate,
            DueDate = entity.DueDate,
            Status = entity.Status
        };
    }
}
