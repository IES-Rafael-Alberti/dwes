package es.iesrafaelalberti.dwes.catalogoculturalhibrido.client;

import es.iesrafaelalberti.dwes.catalogoculturalhibrido.cache.SearchCachingConfig;
import es.iesrafaelalberti.dwes.catalogoculturalhibrido.client.OpenLibraryClientException.OpenLibraryMalformedResponseException;
import es.iesrafaelalberti.dwes.catalogoculturalhibrido.client.OpenLibraryClientException.OpenLibraryRateLimitException;
import es.iesrafaelalberti.dwes.catalogoculturalhibrido.client.OpenLibraryClientException.OpenLibraryServerException;
import es.iesrafaelalberti.dwes.catalogoculturalhibrido.client.OpenLibraryClientException.OpenLibraryTimeoutException;
import es.iesrafaelalberti.dwes.catalogoculturalhibrido.service.dto.CulturalRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;

/**
 * Caching, throttling and observability wrapper around {@link OpenLibraryClient}.
 *
 * <p>This is the single entry point for uncached provider calls:
 * <ol>
 *   <li>the {@link OpenLibrarySearchKeyGenerator} normalizes the query and the
 *       bounded limit into the cache key;</li>
 *   <li>on a miss, the {@link RequestThrottle} enforces roughly one request per
 *       second (never on hits, which must skip provider <em>and</em> throttle);</li>
 *   <li>the raw {@link OpenLibraryClient} is invoked and its controlled failures
 *       propagate unchanged — never cached;</li>
 *   <li>lifecycle and failure category are logged via SLF4J, with the raw query
 *       only ever at DEBUG level (privacy-safe).</li>
 * </ol>
 * No retry is attempted: retrying 429/5xx blindly amplifies provider load and
 * remains explicitly deferred.</p>
 */
@Service
public class CachedOpenLibraryClient {

    private static final Logger log = LoggerFactory.getLogger(CachedOpenLibraryClient.class);

    private final OpenLibraryClient openLibraryClient;
    private final RequestThrottle requestThrottle;

    public CachedOpenLibraryClient(OpenLibraryClient openLibraryClient, RequestThrottle requestThrottle) {
        this.openLibraryClient = openLibraryClient;
        this.requestThrottle = requestThrottle;
    }

    /**
     * Searches Open Library through the cache. On a hit neither the provider nor
     * the throttle is touched; on a miss the throttle is acquired before the call.
     *
     * <p>Failures and {@code null} results are never cached: the cache interceptor
     * only stores normal non-null returns.</p>
     *
     * @return the mapped results (possibly cached), never {@code null}
     */
    @Cacheable(
            cacheNames = SearchCachingConfig.SEARCH_RESULTS_CACHE,
            keyGenerator = "openLibrarySearchKeyGenerator",
            unless = "#result == null")
    public List<CulturalRecord> search(String query, int limit) {
        long started = System.nanoTime();
        log.info("Open Library search started (query is logged at DEBUG level only)");
        log.debug("Open Library search query='{}' limit={}", query, limit);
        requestThrottle.acquire();
        try {
            List<CulturalRecord> records = openLibraryClient.search(query, limit);
            log.info("Open Library search completed in {} ms with {} results",
                    Duration.ofNanos(System.nanoTime() - started).toMillis(), records.size());
            return records;
        } catch (OpenLibraryClientException e) {
            log.warn("Open Library search failed: category={}, duration={} ms",
                    failureCategory(e), Duration.ofNanos(System.nanoTime() - started).toMillis());
            throw e;
        }
    }

    /** Controlled failure category for observability, without leaking the query. */
    static String failureCategory(OpenLibraryClientException e) {
        if (e instanceof OpenLibraryRateLimitException) {
            return "rate_limit";
        }
        if (e instanceof OpenLibraryServerException) {
            return "server_error";
        }
        if (e instanceof OpenLibraryTimeoutException) {
            return "timeout";
        }
        if (e instanceof OpenLibraryMalformedResponseException) {
            return "malformed_response";
        }
        return "unreachable";
    }
}
