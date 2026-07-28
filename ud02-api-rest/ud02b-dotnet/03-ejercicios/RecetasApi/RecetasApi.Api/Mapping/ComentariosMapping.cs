using RecetasApi.Api.Contracts;
using RecetasApi.Api.Domain;

namespace RecetasApi.Api.Mapping;

public static class ComentariosMapping
{
    public static ComentarioDto ToDto(this RecetaComentario c) =>
        new(c.Id, c.Usuario, c.Texto, c.CreatedAt);
}