using TodoApi.Models;

namespace TodoApi.DTOs;

// DTO de salida: lo que se devuelve al cliente.
public class TaskReadDto
{
    // DTO de salida: datos que devolvemos al cliente.
    public int Id { get; set; }
    public string Title { get; set; } = string.Empty;
    public string? Description { get; set; }
    public DateTime CreationDate { get; set; }
    public DateTime DueDate { get; set; }
    public TaskStatus Status { get; set; }
}
