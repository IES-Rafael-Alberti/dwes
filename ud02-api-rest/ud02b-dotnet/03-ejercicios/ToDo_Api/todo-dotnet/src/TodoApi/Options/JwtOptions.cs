namespace TodoApi.Options;

// Opciones de JWT (equivalente a una @ConfigurationProperties en Spring).
public class JwtOptions
{
    public string Issuer { get; set; } = string.Empty;
    public string Audience { get; set; } = string.Empty;
    public string SigningKey { get; set; } = string.Empty;
    public int ExpiresInSeconds { get; set; } = 3600;
}
