using Microsoft.EntityFrameworkCore;
using TodoApi.Models;

namespace TodoApi.Data;

// DbContext es la clase base de EF Core para trabajar con la base de datos.
public class TodoDbContext : DbContext
{
    public TodoDbContext(DbContextOptions<TodoDbContext> options)
        : base(options) // llama al constructor de la clase base (DbContext)
    {
    }

    // DbSet representa una "tabla" de EF; esta propiedad usa expression-bodied member.
    public DbSet<TodoTask> Tasks => Set<TodoTask>();
    public DbSet<User> Users => Set<User>();
}
