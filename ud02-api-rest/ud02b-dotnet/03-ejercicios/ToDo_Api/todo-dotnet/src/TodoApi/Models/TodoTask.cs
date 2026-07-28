namespace TodoApi.Models;

using System.ComponentModel.DataAnnotations;

public enum TaskStatus
{
    Pending,
    InProgress,
    Completed
}

public class TodoTask
{
    // Propiedades auto-implementadas (get/set) similares a los campos con accesores en Java.
    public int Id { get; set; }

    // Atributos (annotations) usados por validacion y EF Core.
    [Required]
    [MaxLength(100)]
    public string Title { get; set; } = string.Empty;

    [MaxLength(2000)]
    public string? Description { get; set; }

    public DateTime CreationDate { get; set; }

    public DateTime DueDate { get; set; }

    public TaskStatus Status { get; set; }

    public int UserId { get; set; }
    public User? User { get; set; }
}
