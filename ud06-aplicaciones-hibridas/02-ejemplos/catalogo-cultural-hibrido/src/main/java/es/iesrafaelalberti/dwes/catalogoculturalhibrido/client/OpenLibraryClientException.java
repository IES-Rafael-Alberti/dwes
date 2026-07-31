package es.iesrafaelalberti.dwes.catalogoculturalhibrido.client;

/**
 * Base exception for controlled provider failures of the Open Library client.
 *
 * <p>Only provider-level failures are wrapped in this hierarchy: transport
 * errors, timeouts, HTTP 429/5xx and malformed JSON. Programming and JPA
 * failures are never caught here — they must propagate unchanged.</p>
 *
 * <p>No automatic retry is attempted in this slice. Open Library rate-limits
 * unauthenticated clients to roughly 1 req/s; blind retries on 429 or 5xx can
 * amplify provider load (thundering herd) and are explicitly deferred to a
 * dedicated resilience layer (P1C) with backoff, jitter, budgets and caching.</p>
 */
public class OpenLibraryClientException extends RuntimeException {

    public OpenLibraryClientException(String message) {
        super(message);
    }

    public OpenLibraryClientException(String message, Throwable cause) {
        super(message, cause);
    }

    /** Open Library answered {@code HTTP 429 Too Many Requests}. */
    public static class OpenLibraryRateLimitException extends OpenLibraryClientException {
        public OpenLibraryRateLimitException(String message) {
            super(message);
        }
    }

    /** Open Library answered an {@code HTTP 5xx} server error. */
    public static class OpenLibraryServerException extends OpenLibraryClientException {
        public OpenLibraryServerException(String message) {
            super(message);
        }
    }

    /** The provider did not answer within the configured request timeout. */
    public static class OpenLibraryTimeoutException extends OpenLibraryClientException {
        public OpenLibraryTimeoutException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    /** The provider returned a body that is not valid JSON. */
    public static class OpenLibraryMalformedResponseException extends OpenLibraryClientException {
        public OpenLibraryMalformedResponseException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
