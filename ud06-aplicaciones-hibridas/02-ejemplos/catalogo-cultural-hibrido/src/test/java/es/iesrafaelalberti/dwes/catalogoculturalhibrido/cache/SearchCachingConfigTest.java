package es.iesrafaelalberti.dwes.catalogoculturalhibrido.cache;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.Cache;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.test.context.ActiveProfiles;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Offline configuration tests for {@link SearchCachingConfig}: the cache is
 * predeclared under the expected name and bounded to the contract values
 * (max 100 entries, expire-after-write 24 h).
 */
@SpringBootTest
@ActiveProfiles("test")
class SearchCachingConfigTest {

    @Autowired
    private CaffeineCacheManager cacheManager;

    @Test
    void shouldPredeclareTheSearchResultsCache() {
        assertNotNull(cacheManager.getCache(SearchCachingConfig.SEARCH_RESULTS_CACHE),
                "the search cache must be declared eagerly, not lazily created");
        assertTrue(cacheManager.getCacheNames().contains(SearchCachingConfig.SEARCH_RESULTS_CACHE));
    }

    @Test
    void shouldBoundTheSearchCacheToMaxEntriesAndTtl() {
        Cache cache = cacheManager.getCache(SearchCachingConfig.SEARCH_RESULTS_CACHE);
        assertNotNull(cache);

        @SuppressWarnings("unchecked")
        var nativeCache = (com.github.benmanes.caffeine.cache.Cache<Object, Object>) cache.getNativeCache();

        assertTrue(nativeCache.policy().expireAfterWrite().isPresent(),
                "expire-after-write must be configured (24 h contract)");
        assertEquals(100L,
                nativeCache.policy().eviction().orElseThrow().getMaximum(),
                "the cache must be bounded to 100 entries");
        assertEquals(Duration.ofHours(24),
                nativeCache.policy().expireAfterWrite().orElseThrow().getExpiresAfter(),
                "entries must expire exactly 24 hours after being written");
    }
}
