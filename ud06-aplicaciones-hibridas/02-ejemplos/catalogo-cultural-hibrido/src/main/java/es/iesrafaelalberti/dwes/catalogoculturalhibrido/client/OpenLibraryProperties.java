package es.iesrafaelalberti.dwes.catalogoculturalhibrido.client;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.net.URI;
import java.time.Duration;

/**
 * Type-safe configuration for the Open Library Search API client.
 *
 * <p>Bound from the {@code catalogo.open-library.*} prefix. Defaults are
 * deliberately classroom-safe: no API key (Open Library requires none for
 * occasional human queries), low volume (max 10 results) and a bounded request
 * timeout.</p>
 *
 * <p>Usage constraints (Open Library developers docs): the API is for
 * <em>occasional human queries</em>, roughly 1 request/second (3 req/s when
 * the application is identified via User-Agent). Bulk harvesting is forbidden
 * and responses should be cached where appropriate. This project does not
 * implement response caching yet — see README (P1C boundary).</p>
 *
 * <p>Validation is fail-fast in the compact constructor: a misconfigured
 * application fails at startup instead of failing on the first request.</p>
 */
@ConfigurationProperties(prefix = "catalogo.open-library")
public record OpenLibraryProperties(
        String baseUrl,
        String userAgent,
        Duration requestTimeout,
        Integer maxResults) {

    public static final String DEFAULT_BASE_URL = "https://openlibrary.org";
    public static final String DEFAULT_USER_AGENT =
            "DWES-UD6/1.0 (docencia; contacto: <email-del-docente>)";
    public static final Duration DEFAULT_REQUEST_TIMEOUT = Duration.ofSeconds(5);
    public static final int DEFAULT_MAX_RESULTS = 10;
    public static final int MAX_ALLOWED_RESULTS = 10;

    public OpenLibraryProperties {
        baseUrl = (baseUrl == null || baseUrl.isBlank()) ? DEFAULT_BASE_URL : baseUrl.trim();
        userAgent = (userAgent == null || userAgent.isBlank()) ? DEFAULT_USER_AGENT : userAgent.trim();
        requestTimeout = (requestTimeout == null || requestTimeout.isZero() || requestTimeout.isNegative())
                ? DEFAULT_REQUEST_TIMEOUT
                : requestTimeout;
        // Integer (not int) so an absent property binds null and defaults to 10,
        // while an explicit 0 or negative value is rejected by the fail-fast check below.
        maxResults = (maxResults == null) ? DEFAULT_MAX_RESULTS : maxResults;

        if (!isHttpUri(baseUrl)) {
            throw new IllegalArgumentException(
                    "catalogo.open-library.base-url must be an absolute http(s) URL, got: " + baseUrl);
        }
        if (maxResults < 1 || maxResults > MAX_ALLOWED_RESULTS) {
            throw new IllegalArgumentException(
                    "catalogo.open-library.max-results must be between 1 and " + MAX_ALLOWED_RESULTS
                            + ", got: " + maxResults);
        }
    }

    private static boolean isHttpUri(String value) {
        try {
            URI uri = URI.create(value);
            // A scheme-relative URI (e.g. "//openlibrary.org") has a host but no
            // scheme; check the scheme first so it fails the validation below
            // instead of throwing an NPE.
            return uri.getScheme() != null
                    && uri.getHost() != null
                    && (uri.getScheme().equalsIgnoreCase("http")
                    || uri.getScheme().equalsIgnoreCase("https"));
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
