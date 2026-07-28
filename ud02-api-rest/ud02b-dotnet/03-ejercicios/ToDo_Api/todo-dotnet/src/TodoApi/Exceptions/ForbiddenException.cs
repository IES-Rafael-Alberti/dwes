namespace TodoApi.Exceptions;

// Excepcion de dominio para accesos no permitidos por propiedad.
public class ForbiddenException : Exception
{
    public string ErrorCode { get; }

    public ForbiddenException(string errorCode, string message) : base(message)
    {
        ErrorCode = errorCode;
    }
}
