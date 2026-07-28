namespace RecetasApi.Api.Domain;

public class IngredienteItem
{
    public long Id { get; set; } // útil para EF Core
    public string Nombre { get; set; } = string.Empty;
    public string Cantidad { get; set; } = string.Empty;
}