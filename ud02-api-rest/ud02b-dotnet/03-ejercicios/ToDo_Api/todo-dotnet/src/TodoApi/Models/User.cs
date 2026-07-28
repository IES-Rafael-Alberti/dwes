using System.ComponentModel.DataAnnotations;

namespace TodoApi.Models;

public enum UserRole
{
    User,
    Supervisor,
    Admin
}

public class User
{
    public int Id { get; set; }

    [Required]
    [MaxLength(50)]
    public string Username { get; set; } = string.Empty;

    [Required]
    [MaxLength(200)]
    public string Email { get; set; } = string.Empty;

    [Required]
    public string PasswordHash { get; set; } = string.Empty;

    public UserRole Role { get; set; } = UserRole.User;

    [MaxLength(500)]
    public string? AvatarUrl { get; set; }

    public List<TodoTask> Tasks { get; set; } = new();
}
