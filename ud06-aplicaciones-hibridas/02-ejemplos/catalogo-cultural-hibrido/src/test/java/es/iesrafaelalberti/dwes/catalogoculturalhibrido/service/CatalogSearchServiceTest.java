package es.iesrafaelalberti.dwes.catalogoculturalhibrido.service;

import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import es.iesrafaelalberti.dwes.catalogoculturalhibrido.cache.SearchCachingConfig;
import es.iesrafaelalberti.dwes.catalogoculturalhibrido.client.OpenLibraryClient;
import es.iesrafaelalberti.dwes.catalogoculturalhibrido.client.OpenLibraryClientException;
import es.iesrafaelalberti.dwes.catalogoculturalhibrido.model.CulturalItem;
import es.iesrafaelalberti.dwes.catalogoculturalhibrido.model.Source;
import es.iesrafaelalberti.dwes.catalogoculturalhibrido.repository.CulturalItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Orchestration tests for {@link CatalogSearchService}: remote search via
 * WireMock (dynamic port, fully offline) plus the idempotent ingestion pipeline.
 *
 * <p>The provider endpoint is redirected to WireMock through
 * {@code @DynamicPropertySource}, so the real Open Library is never contacted.</p>
 */
@SpringBootTest
@ActiveProfiles("test")
class CatalogSearchServiceTest {

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
        registry.add("spring.datasource.url", () -> "jdbc:h2:mem:catcultural-p1b;DB_CLOSE_DELAY=-1");
    }

    @Autowired
    private CatalogSearchService catalogSearchService;

    @Autowired
    private CulturalItemRepository repository;

    @Autowired
    private CacheManager cacheManager;

    @BeforeEach
    void cleanDatabaseAndCache() {
        repository.deleteAll();
        Cache cache = cacheManager.getCache(SearchCachingConfig.SEARCH_RESULTS_CACHE);
        if (cache != null) {
            cache.clear();
        }
    }

    @Test
    void shouldSearchAndIngestRemoteResults() {
        stubFor(get(urlPathEqualTo("/search.json"))
                .willReturn(okJson(TWO_DOCS_JSON)));

        List<CulturalItem> items = catalogSearchService.searchAndIngest("don quixote", 10);

        assertEquals(2, items.size());
        assertEquals(2, repository.count());

        var donQuixote = repository.findBySourceAndExternalId(Source.OPEN_LIBRARY, "OL1111W");
        assertTrue(donQuixote.isPresent());
        assertEquals("Don Quixote", donQuixote.get().getTitle());
        assertEquals("https://openlibrary.org/works/OL1111W", donQuixote.get().getSourceUrl());
        assertEquals(OpenLibraryClient.OPEN_LIBRARY_LICENSE_NOTICE, donQuixote.get().getLicense());
    }

    @Test
    void shouldBeIdempotentOnRemoteReImport() {
        stubFor(get(urlPathEqualTo("/search.json"))
                .willReturn(okJson(TWO_DOCS_JSON)));

        catalogSearchService.searchAndIngest("don quixote", 10);
        assertEquals(2, repository.count());

        List<CulturalItem> reImported = catalogSearchService.searchAndIngest("don quixote", 10);
        assertEquals(2, reImported.size());
        assertEquals(2, repository.count(), "Remote re-import must not create duplicates");
    }

    @Test
    void shouldNotPersistAnythingWhenProviderFails() {
        stubFor(get(urlPathEqualTo("/search.json"))
                .willReturn(aResponse().withStatus(503)));

        assertThrows(OpenLibraryClientException.OpenLibraryServerException.class,
                () -> catalogSearchService.searchAndIngest("don quixote", 10));
        assertEquals(0, repository.count(), "Provider failure must not leave partial state");
    }
}
