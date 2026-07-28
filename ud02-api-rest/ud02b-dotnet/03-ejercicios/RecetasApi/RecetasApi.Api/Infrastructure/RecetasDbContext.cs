using Microsoft.EntityFrameworkCore;
using RecetasApi.Api.Domain;

namespace RecetasApi.Api.Infrastructure;

public class RecetasDbContext : DbContext
{
    public RecetasDbContext(DbContextOptions<RecetasDbContext> options) : base(options) {}

    public DbSet<Receta> Recetas => Set<Receta>();
    public DbSet<IngredienteItem> Ingredientes => Set<IngredienteItem>();
    public DbSet<Paso> Pasos => Set<Paso>();
    public DbSet<RecetaLike> Likes => Set<RecetaLike>();
    public DbSet<RecetaComentario> Comentarios => Set<RecetaComentario>();



    protected override void OnModelCreating(ModelBuilder modelBuilder)
    {
        modelBuilder.Entity<Receta>()
            .HasMany(r => r.Ingredientes)
            .WithOne()
            .OnDelete(DeleteBehavior.Cascade);

        modelBuilder.Entity<Receta>()
            .HasMany(r => r.Pasos)
            .WithOne()
            .OnDelete(DeleteBehavior.Cascade);

        modelBuilder.Entity<Paso>()
            .HasIndex(p => p.Orden);
        
        modelBuilder.Entity<RecetaLike>()
            .HasIndex(l => new { l.RecetaId, l.Usuario })
            .IsUnique();
        
        modelBuilder.Entity<RecetaComentario>()
            .HasIndex(c => c.RecetaId);
    }
}