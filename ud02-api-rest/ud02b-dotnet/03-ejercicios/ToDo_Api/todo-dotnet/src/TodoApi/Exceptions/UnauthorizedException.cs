namespace TodoApi.Exceptions;

// Excepcion de dominio para autenticacion/credenciales.
public class UnauthorizedException : Exception
{
    public string ErrorCode { get; }

    public UnauthorizedException(string errorCode, string message) : base(message)
    {
        ErrorCode = errorCode;
    }
}
