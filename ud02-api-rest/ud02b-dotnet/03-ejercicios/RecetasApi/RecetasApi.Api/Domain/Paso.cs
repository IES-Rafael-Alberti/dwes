namespace RecetasApi.Api.Domain;

public class Paso
{
    public long Id { get; set; } // útil para EF Core
    public int Orden { get; set; }
    public string Descripcion { get; set; } = string.Empty;
    public int? DuracionEstimadaMin { get; set; }
}