package es.iesrafaelalberti.dwes.catalogoculturalhibrido.cache;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

/**
 * Spring Cache configuration for the Open Library search results.
 *
 * <p><strong>Dedicated configuration.</strong> This is the only place where
 * caching is enabled ({@code @EnableCaching}). Spring Boot auto-configures the
 * official Caffeine cache manager from {@code application.properties}.</p>
 *
 * <p><strong>Predeclared, bounded, expiring.</strong> The cache name is a
 * constant (never lazily created), capacity is capped at
 * {@link #SEARCH_CACHE_MAX_ENTRIES} entries and every entry expires
 * {@link #SEARCH_CACHE_TTL} after it is written. This honours the Open Library
 * policy of caching responses (24 h contract) without ever growing unbounded.</p>
 */
@Configuration(proxyBeanMethods = false)
@EnableCaching
public class SearchCachingConfig {

    /** Predeclared cache name used by {@code @Cacheable} on the search client. */
    public static final String SEARCH_RESULTS_CACHE = "openLibrarySearchResults";

    /** Maximum number of distinct search keys kept in memory. */
    public static final int SEARCH_CACHE_MAX_ENTRIES = 100;

    /** Responses expire 24 h after being written (Open Library caching contract). */
    public static final Duration SEARCH_CACHE_TTL = Duration.ofHours(24);

}
