using TodoApi.Models;

namespace TodoApi.Repositories;

// Repositorio de usuarios (equivalente a un @Repository en Spring).
public interface IUsersRepository
{
    User? GetById(int id);
    User? GetByEmail(string email);
    User? GetByUsername(string username);
    User Add(User user);
}
