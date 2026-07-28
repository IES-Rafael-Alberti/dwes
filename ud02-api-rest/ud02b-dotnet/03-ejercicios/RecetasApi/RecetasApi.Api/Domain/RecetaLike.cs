namespace RecetasApi.Api.Domain;

public class RecetaLike
{
    public long Id { get; set; }

    public long RecetaId { get; set; }

    public string Usuario { get; set; } = string.Empty;

    public DateTime CreatedAt { get; set; } = DateTime.UtcNow;
}