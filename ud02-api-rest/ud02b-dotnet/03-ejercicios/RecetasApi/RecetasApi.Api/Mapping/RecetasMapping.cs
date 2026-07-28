using RecetasApi.Api.Contracts;
using RecetasApi.Api.Domain;

namespace RecetasApi.Api.Mapping;

public static class RecetasMapping
{
    public static RecetaSummaryDto ToSummaryDto(this Receta r) =>
        new(r.Id, r.Titulo, r.Autor, r.LikesCount, r.OriginalRecetaId, r.IsArchived);



    public static RecetaDetailDto ToDetailDto(this Receta r) =>
        new(
            r.Id,
            r.Titulo,
            r.Autor,
            r.Ingredientes.Select(i => new IngredienteItemDto(i.Nombre, i.Cantidad)).ToList(),
            r.Pasos.OrderBy(p => p.Orden).Select(p => new PasoDto(p.Orden, p.Descripcion, p.DuracionEstimadaMin)).ToList(),
            r.LikesCount,
            r.OriginalRecetaId,
            r.IsArchived,
            r.ArchivedAt,
            r.CreatedAt,
            r.UpdatedAt
        );

    public static Receta ToEntity(this CreateRecetaRequest req, string autor) =>
        new()
        {
            Titulo = req.Titulo.Trim(),
            Autor = autor,
            Ingredientes = req.Ingredientes.Select(i => new IngredienteItem { Nombre = i.Nombre.Trim(), Cantidad = i.Cantidad.Trim() }).ToList(),
            Pasos = req.Pasos.Select(p => new Paso { Orden = p.Orden, Descripcion = p.Descripcion.Trim(), DuracionEstimadaMin = p.DuracionEstimadaMin }).ToList(),
            CreatedAt = DateTime.UtcNow,
            UpdatedAt = DateTime.UtcNow
        };

    public static void Apply(this UpdateRecetaRequest req, Receta entity)
    {
        entity.Titulo = req.Titulo.Trim();
        entity.Ingredientes = req.Ingredientes.Select(i => new IngredienteItem { Nombre = i.Nombre.Trim(), Cantidad = i.Cantidad.Trim() }).ToList();
        entity.Pasos = req.Pasos.Select(p => new Paso { Orden = p.Orden, Descripcion = p.Descripcion.Trim(), DuracionEstimadaMin = p.DuracionEstimadaMin }).ToList();
        entity.UpdatedAt = DateTime.UtcNow;
    }
}