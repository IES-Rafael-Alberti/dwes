package es.iesrafaelalberti.dwes.catalogoculturalhibrido.client;

import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import es.iesrafaelalberti.dwes.catalogoculturalhibrido.cache.SearchCachingConfig;
import es.iesrafaelalberti.dwes.catalogoculturalhibrido.client.OpenLibraryClientException.OpenLibraryRateLimitException;
import es.iesrafaelalberti.dwes.catalogoculturalhibrido.client.OpenLibraryClientException.OpenLibraryServerException;
import es.iesrafaelalberti.dwes.catalogoculturalhibrido.service.dto.CulturalRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Offline tests for {@link CachedOpenLibraryClient}: cache hits, query
 * normalization, distinct limits, failures never cached and throttle bypass on
 * cache hits. WireMock (dynamic port) stands in for the real Open Library and a
 * recording {@link RequestThrottle} proves the throttle is never acquired on a
 * cache hit, without sleeping.
 */
@SpringBootTest
@ActiveProfiles("test")
class CachedOpenLibraryClientTest {

    private static final String TWO_DOCS_JSON = """
            {
              "numFound": 2,
              "docs": [
                {
                  "key": "/works/OL1111W",
                  "title": "Don Quixote",
                  "author_name": ["Miguel de Cervantes Saavedra"],
                  "first_publish_year": 1605,
                  "subject": ["Spanish fiction", "Classic"]
                },
                {
                  "key": "/works/OL2222W",
                  "title": "The Ingenious Gentleman Don Quixote of La Mancha",
                  "author_name": ["Miguel de Cervantes"],
                  "first_publish_year": 1615,
                  "subject": ["Quixote"]
                }
              ]
            }
            """;

    @RegisterExtension
    static WireMockExtension WIRE_MOCK = WireMockExtension.newInstance()
            .options(wireMockConfig().dynamicPort())
            .configureStaticDsl(true)
            .build();

    @DynamicPropertySource
    static void openLibraryProperties(DynamicPropertyRegistry registry) {
        registry.add("catalogo.open-library.base-url", WIRE_MOCK::baseUrl);
        registry.add("catalogo.open-library.request-timeout", () -> "2s");
        registry.add("catalogo.open-library.max-results", () -> "10");
        registry.add("spring.datasource.url", () -> "jdbc:h2:mem:catcultural-p1c;DB_CLOSE_DELAY=-1");
    }

    @TestConfiguration
    static class RecordingThrottleConfiguration {
        @Bean
        @Primary
        RequestThrottle requestThrottle() {
            return new RecordingRequestThrottle();
        }
    }

    @Autowired
    private CachedOpenLibraryClient cachedOpenLibraryClient;

    @Autowired
    private RecordingRequestThrottle throttle;

    @Autowired
    private CacheManager cacheManager;

    @BeforeEach
    void clearCacheAndThrottle() {
        throttle.reset();
        Cache cache = cacheManager.getCache(SearchCachingConfig.SEARCH_RESULTS_CACHE);
        if (cache != null) {
            cache.clear();
        }
    }

    @Test
    void shouldServeSecondIdenticalSearchFromCache() {
        stubFor(get(urlPathEqualTo("/search.json"))
                .willReturn(okJson(TWO_DOCS_JSON)));

        List<CulturalRecord> first = cachedOpenLibraryClient.search("don quixote", 10);
        List<CulturalRecord> second = cachedOpenLibraryClient.search("don quixote", 10);

        assertEquals(2, first.size());
        assertEquals(2, second.size());
        WIRE_MOCK.verify(1, getRequestedFor(urlPathEqualTo("/search.json")));
    }

    @Test
    void shouldSkipProviderAndThrottleOnCacheHit() {
        stubFor(get(urlPathEqualTo("/search.json"))
                .willReturn(okJson(TWO_DOCS_JSON)));

        cachedOpenLibraryClient.search("don quixote", 10);
        assertEquals(1, throttle.acquisitions(), "first call must acquire the throttle");

        cachedOpenLibraryClient.search("don quixote", 10);
        assertEquals(1, throttle.acquisitions(),
                "a cache hit must not acquire the throttle");
        WIRE_MOCK.verify(1, getRequestedFor(urlPathEqualTo("/search.json")));
    }

    @Test
    void shouldTreatNormalizedQueriesAsTheSameCacheEntry() {
        stubFor(get(urlPathEqualTo("/search.json"))
                .willReturn(okJson(TWO_DOCS_JSON)));

        List<CulturalRecord> upper = cachedOpenLibraryClient.search("  DON QUIXOTE  ", 10);
        List<CulturalRecord> lower = cachedOpenLibraryClient.search("don quixote", 10);

        assertEquals(2, upper.size());
        assertEquals(2, lower.size());
        WIRE_MOCK.verify(1, getRequestedFor(urlPathEqualTo("/search.json")));
    }

    @Test
    void shouldDistinguishCacheEntriesByLimit() {
        stubFor(get(urlPathEqualTo("/search.json"))
                .willReturn(okJson(TWO_DOCS_JSON)));

        cachedOpenLibraryClient.search("don quixote", 5);
        cachedOpenLibraryClient.search("don quixote", 10);

        WIRE_MOCK.verify(2, getRequestedFor(urlPathEqualTo("/search.json")));
    }

    @Test
    void shouldNotCacheProviderFailures() {
        stubFor(get(urlPathEqualTo("/search.json"))
                .willReturn(aResponse().withStatus(503)));

        assertThrows(OpenLibraryClientException.OpenLibraryServerException.class,
                () -> cachedOpenLibraryClient.search("don quixote", 10));

        stubFor(get(urlPathEqualTo("/search.json"))
                .willReturn(okJson(TWO_DOCS_JSON)));

        List<CulturalRecord> recovered = cachedOpenLibraryClient.search("don quixote", 10);

        assertEquals(2, recovered.size(),
                "a failed call must never be cached: the next identical call must reach the provider");
        WIRE_MOCK.verify(2, getRequestedFor(urlPathEqualTo("/search.json")));
    }

    @Test
    void shouldNormalizeQueryAndLimitIntoCacheKey() {
        var keyGenerator = new OpenLibrarySearchKeyGenerator();

        assertEquals("don quixote::10", keyGenerator.generate(this, null, "  DON QUIXOTE  ", 10));
        assertEquals("don quixote::10", keyGenerator.generate(this, null, "don quixote", 10));
        assertEquals("don quixote::5", keyGenerator.generate(this, null, "Don Quixote", 5));
        assertEquals("::10", keyGenerator.generate(this, null, "  ", 10));
    }

    @Test
    void shouldMapControlledFailuresToStableCategories() {
        assertEquals("rate_limit", CachedOpenLibraryClient.failureCategory(
                new OpenLibraryRateLimitException("rate limited")));
        assertEquals("server_error", CachedOpenLibraryClient.failureCategory(
                new OpenLibraryServerException("server error")));
        assertEquals("malformed_response", CachedOpenLibraryClient.failureCategory(
                new OpenLibraryClientException.OpenLibraryMalformedResponseException("bad json", null)));
        assertEquals("timeout", CachedOpenLibraryClient.failureCategory(
                new OpenLibraryClientException.OpenLibraryTimeoutException("timed out", null)));
        assertEquals("unreachable", CachedOpenLibraryClient.failureCategory(
                new OpenLibraryClientException("unreachable")));
    }

    /** Records each {@code acquire()} so tests can assert throttle usage. */
    static final class RecordingRequestThrottle implements RequestThrottle {

        private int acquisitions;

        @Override
        public synchronized void acquire() {
            acquisitions++;
        }

        synchronized int acquisitions() {
            return acquisitions;
        }

        synchronized void reset() {
            acquisitions = 0;
        }
    }
}
