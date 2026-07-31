package es.iesrafaelalberti.dwes.catalogoculturalhibrido.client;

import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import es.iesrafaelalberti.dwes.catalogoculturalhibrido.model.Source;
import es.iesrafaelalberti.dwes.catalogoculturalhibrido.service.dto.CulturalRecord;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import tools.jackson.databind.ObjectMapper;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Offline tests for {@link OpenLibraryClient} against WireMock 3 (dynamic port).
 *
 * <p>No Spring context is started: the client is wired directly with a real
 * {@code WebClient} pointed at the WireMock server. All stubs live on
 * {@code /search.json}, so no test ever touches the real Open Library.</p>
 */
class OpenLibraryClientTest {

    private static final String USER_AGENT = "DWES-UD6-test/1.0 (test; contacto: <test@example.com>)";
    private static final String REQUESTED_FIELDS = "key,title,author_name,first_publish_year,subject";

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

    private final ObjectMapper objectMapper = new ObjectMapper();

    private OpenLibraryClient client() {
        return clientWithTimeout(Duration.ofSeconds(5));
    }

    private OpenLibraryClient clientWithTimeout(Duration requestTimeout) {
        return new OpenLibraryClient(
                new OpenLibraryProperties(WIRE_MOCK.baseUrl(), USER_AGENT, requestTimeout, 10),
                objectMapper);
    }

    @Test
    void shouldRequestSearchPathWithQueryLimitAndFields() {
        stubFor(get(urlPathEqualTo("/search.json"))
                .willReturn(okJson(TWO_DOCS_JSON)));

        client().search("don quixote");

        WIRE_MOCK.verify(getRequestedFor(urlPathEqualTo("/search.json"))
                .withQueryParam("q", equalTo("don quixote"))
                .withQueryParam("limit", equalTo("10"))
                .withQueryParam("fields", equalTo(REQUESTED_FIELDS)));
    }

    @Test
    void shouldSendConfiguredUserAgent() {
        stubFor(get(urlPathEqualTo("/search.json"))
                .willReturn(okJson(TWO_DOCS_JSON)));

        client().search("don quixote");

        WIRE_MOCK.verify(getRequestedFor(urlPathEqualTo("/search.json"))
                .withHeader("User-Agent", equalTo(USER_AGENT)));
    }

    @Test
    void shouldMapProviderDocsToNormalizedRecords() {
        stubFor(get(urlPathEqualTo("/search.json"))
                .willReturn(okJson(TWO_DOCS_JSON)));

        List<CulturalRecord> results = client().search("don quixote");

        assertEquals(2, results.size());

        CulturalRecord first = results.get(0);
        assertEquals(Source.OPEN_LIBRARY.name(), first.getSource());
        assertEquals("OL1111W", first.getExternalId());
        assertEquals("Don Quixote", first.getTitle());
        assertEquals(List.of("Miguel de Cervantes Saavedra"), first.getCreators());
        assertEquals(1605, first.getYear());
        assertEquals(List.of("Spanish fiction", "Classic"), first.getSubjects());
        assertEquals("https://openlibrary.org/works/OL1111W", first.getSourceUrl());
        assertEquals(OpenLibraryClient.OPEN_LIBRARY_LICENSE_NOTICE, first.getLicense());
        assertNotNull(first.getRetrievedAt());
        assertDoesNotThrow(() -> LocalDateTime.parse(first.getRetrievedAt()),
                "retrievedAt must be an ISO-8601 timestamp readable by IngestionService");

        CulturalRecord second = results.get(1);
        assertEquals("OL2222W", second.getExternalId());
        assertEquals("https://openlibrary.org/works/OL2222W", second.getSourceUrl());
    }

    @Test
    void shouldReturnEmptyListWhenProviderReturnsNoDocs() {
        stubFor(get(urlPathEqualTo("/search.json"))
                .willReturn(okJson("{\"numFound\": 0, \"docs\": []}")));

        List<CulturalRecord> results = client().search("zzz non-existent");

        assertTrue(results.isEmpty());
    }

    @Test
    void shouldIgnoreDocsWithoutKeyOrTitle() {
        stubFor(get(urlPathEqualTo("/search.json"))
                .willReturn(okJson("""
                        {
                          "numFound": 3,
                          "docs": [
                            {"key": "/works/OL1W", "title": "Valid"},
                            {"key": null, "title": "No Key"},
                            {"key": "/works/OL2W", "title": ""}
                          ]
                        }
                        """)));

        List<CulturalRecord> results = client().search("whatever");

        assertEquals(1, results.size());
        assertEquals("OL1W", results.get(0).getExternalId());
    }

    @Test
    void shouldRejectBlankQuery() {
        OpenLibraryClient client = client();

        assertThrows(IllegalArgumentException.class, () -> client.search("   "));
        assertThrows(IllegalArgumentException.class, () -> client.search(null));
    }

    @Test
    void shouldRejectLimitOutsideConfiguredBounds() {
        OpenLibraryClient client = client();

        assertThrows(IllegalArgumentException.class, () -> client.search("don quixote", 0));
        assertThrows(IllegalArgumentException.class, () -> client.search("don quixote", 11));
    }

    @Test
    void shouldMap429ToRateLimitException() {
        stubFor(get(urlPathEqualTo("/search.json"))
                .willReturn(aResponse().withStatus(429)));

        assertThrows(OpenLibraryClientException.OpenLibraryRateLimitException.class,
                () -> client().search("don quixote"));
    }

    @Test
    void shouldMapServerErrorToServerException() {
        stubFor(get(urlPathEqualTo("/search.json"))
                .willReturn(aResponse().withStatus(503)));

        assertThrows(OpenLibraryClientException.OpenLibraryServerException.class,
                () -> client().search("don quixote"));
    }

    @Test
    void shouldMapMalformedJsonToMalformedResponseException() {
        stubFor(get(urlPathEqualTo("/search.json"))
                .willReturn(aResponse().withStatus(200).withBody("{not json")));

        assertThrows(OpenLibraryClientException.OpenLibraryMalformedResponseException.class,
                () -> client().search("don quixote"));
    }

    @Test
    void shouldMapEmptyBodyToMalformedResponseException() {
        stubFor(get(urlPathEqualTo("/search.json"))
                .willReturn(aResponse().withStatus(200)));

        assertThrows(OpenLibraryClientException.OpenLibraryMalformedResponseException.class,
                () -> client().search("don quixote"));
    }

    @Test
    void shouldFailWithTimeoutExceptionWhenProviderIsSlow() {
        stubFor(get(urlPathEqualTo("/search.json"))
                .willReturn(okJson(TWO_DOCS_JSON).withFixedDelay(2000)));

        OpenLibraryClient client = clientWithTimeout(Duration.ofMillis(400));

        assertThrows(OpenLibraryClientException.OpenLibraryTimeoutException.class,
                () -> client.search("don quixote"));
    }

    @Test
    void shouldFailWithClientExceptionWhenProviderUnreachable() {
        OpenLibraryClient client = new OpenLibraryClient(
                new OpenLibraryProperties("http://localhost:1", USER_AGENT, Duration.ofSeconds(2), 10),
                objectMapper);

        assertThrows(OpenLibraryClientException.class, () -> client.search("don quixote"));
    }
}
