namespace RecetasApi.Api.Contracts;

public record ComentarioDto(
    long Id,
    string Usuario,
    string Texto,
    DateTime CreatedAt
);