using Microsoft.EntityFrameworkCore;
using TodoApi.Data;
using TodoApi.Models;

namespace TodoApi.Repositories;

// Implementacion EF Core del repositorio de usuarios.
public class UsersEfRepository : IUsersRepository
{
    private readonly TodoDbContext _db;

    public UsersEfRepository(TodoDbContext db)
    {
        _db = db;
    }

    public User? GetById(int id)
    {
        return _db.Users.AsNoTracking().FirstOrDefault(u => u.Id == id);
    }

    public User? GetByEmail(string email)
    {
        return _db.Users.AsNoTracking().FirstOrDefault(u => u.Email == email);
    }

    public User? GetByUsername(string username)
    {
        return _db.Users.AsNoTracking().FirstOrDefault(u => u.Username == username);
    }

    public User Add(User user)
    {
        _db.Users.Add(user);
        _db.SaveChanges();
        return user;
    }
}
