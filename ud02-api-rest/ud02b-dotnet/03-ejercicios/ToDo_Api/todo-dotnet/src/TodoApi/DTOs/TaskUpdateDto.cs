using System.ComponentModel.DataAnnotations;
using TodoApi.Models;

namespace TodoApi.DTOs;

public class TaskUpdateDto
{
    // DTO de entrada para editar: incluye los campos modificables.
    [Required(ErrorMessage = "El título es obligatorio.")]
    [StringLength(100, MinimumLength = 3, 
        ErrorMessage = "El título debe tener entre 3 y 100 caracteres.")]
    public string Title { get; set; } = string.Empty;

    [StringLength(2000, ErrorMessage = "La descripción no puede superar 2000 caracteres.")]
    public string? Description { get; set; }

    [Required(ErrorMessage = "La fecha límite es obligatoria.")]
    public DateTime? DueDate { get; set; }

    public TaskStatus Status { get; set; } = TaskStatus.Pending;
}
