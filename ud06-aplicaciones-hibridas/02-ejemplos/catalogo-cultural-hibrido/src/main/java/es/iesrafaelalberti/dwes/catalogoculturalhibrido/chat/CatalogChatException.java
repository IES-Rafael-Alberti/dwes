package es.iesrafaelalberti.dwes.catalogoculturalhibrido.chat;

/**
 * Controlled failure at the optional chat boundary. Infrastructure details and
 * catalog content are deliberately not included in its public messages.
 */
public class CatalogChatException extends RuntimeException {

    public enum Reason {
        MODEL_UNAVAILABLE,
        INVALID_RESPONSE
    }

    private final Reason reason;

    private CatalogChatException(Reason reason, String message, Throwable cause) {
        super(message, cause);
        this.reason = reason;
    }

    public static CatalogChatException modelUnavailable(Throwable cause) {
        return new CatalogChatException(
                Reason.MODEL_UNAVAILABLE,
                "The optional chat model is unavailable",
                cause);
    }

    public static CatalogChatException invalidResponse() {
        return new CatalogChatException(
                Reason.INVALID_RESPONSE,
                "The optional chat model returned an invalid recommendation",
                null);
    }

    public Reason getReason() {
        return reason;
    }
}
