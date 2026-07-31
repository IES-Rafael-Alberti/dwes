package es.iesrafaelalberti.dwes.catalogoculturalhibrido.client;

import es.iesrafaelalberti.dwes.catalogoculturalhibrido.client.OpenLibraryClientException.OpenLibraryMalformedResponseException;
import es.iesrafaelalberti.dwes.catalogoculturalhibrido.client.OpenLibraryClientException.OpenLibraryRateLimitException;
import es.iesrafaelalberti.dwes.catalogoculturalhibrido.client.OpenLibraryClientException.OpenLibraryServerException;
import es.iesrafaelalberti.dwes.catalogoculturalhibrido.client.OpenLibraryClientException.OpenLibraryTimeoutException;
import es.iesrafaelalberti.dwes.catalogoculturalhibrido.model.Source;
import es.iesrafaelalberti.dwes.catalogoculturalhibrido.service.dto.CulturalRecord;
import io.netty.channel.ChannelOption;
import io.netty.channel.ConnectTimeoutException;
import io.netty.handler.timeout.ReadTimeoutException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.netty.http.client.HttpClient;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeoutException;

/**
 * Imperative teaching-facing client for the Open Library Search API
 * ({@code /search.json}).
 *
 * <p><strong>Blocking boundary.</strong> This client deliberately calls
 * {@code block()} on the WebClient/Reactor pipeline. The async machinery stays
 * contained inside the adapter: services above (and the DB transaction opened
 * by {@code IngestionService}) never see reactive types, which keeps the
 * teaching flow imperative and simple. The cost is one blocked thread per
 * request — acceptable for the low-volume, human-facing usage Open Library
 * permits, but a real high-traffic system should keep WebClient end-to-end
 * or use a dedicated HTTP client.</p>
 *
 * <p>Only a minimal field set is requested: {@code key}, {@code title},
 * {@code author_name}, {@code first_publish_year} and {@code subject}. The
 * Phase 0 contract also lists {@code cover_edition_key}; it is deliberately
 * omitted because cover art is not part of the normalized model. Results map
 * into the source-neutral {@link CulturalRecord} used by the idempotent
 * ingestion service.</p>
 *
 * <p><strong>Failure semantics.</strong> Transport timeouts, HTTP 429, HTTP 5xx,
 * malformed JSON and invalid/empty queries map to controlled exceptions (see
 * {@link OpenLibraryClientException}). No automatic retry is performed in this
 * slice: retrying a 429 or 5xx blindly can amplify provider load, so retries are
 * deferred to a dedicated resilience layer with backoff, jitter, budgets and
 * caching (P1C).</p>
 */
@Component
public class OpenLibraryClient {

    /** Attribution/licensing caveat stored with every mapped record. */
    public static final String OPEN_LIBRARY_LICENSE_NOTICE =
            "Open Library / Internet Archive licensing — see https://openlibrary.org/developers/licensing";

    /**
     * Canonical host of the source, used for {@code sourceUrl} provenance.
     * This is deliberately independent of the configured endpoint: provenance
     * data must always point to the real Open Library resource, never to a
     * test double or mirror.
     */
    private static final String CANONICAL_BASE_URL = "https://openlibrary.org";

    private static final String SEARCH_PATH = "/search.json";
    private static final String REQUESTED_FIELDS = "key,title,author_name,first_publish_year,subject";

    private final OpenLibraryProperties properties;
    private final ObjectMapper objectMapper;
    private final WebClient webClient;

    @Autowired
    public OpenLibraryClient(OpenLibraryProperties properties, ObjectMapper objectMapper) {
        this(properties, objectMapper, buildWebClient(properties));
    }

    /** Test seam: allows wiring the client against any HTTP endpoint (e.g. WireMock). */
    OpenLibraryClient(OpenLibraryProperties properties, ObjectMapper objectMapper, WebClient webClient) {
        this.properties = properties;
        this.objectMapper = objectMapper;
        this.webClient = webClient;
    }

    static WebClient buildWebClient(OpenLibraryProperties properties) {
        return WebClient.builder()
                .baseUrl(properties.baseUrl())
                .defaultHeader(HttpHeaders.USER_AGENT, properties.userAgent())
                .clientConnector(new ReactorClientHttpConnector(
                        HttpClient.create()
                                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS,
                                        (int) properties.requestTimeout().toMillis())
                                .responseTimeout(properties.requestTimeout())))
                .build();
    }

    /** Searches with the configured default result limit. */
    public List<CulturalRecord> search(String query) {
        return search(query, properties.maxResults());
    }

    /**
     * Searches Open Library and maps the result set into normalized
     * {@link CulturalRecord}s.
     *
     * @throws IllegalArgumentException if the query is blank or the limit is
     *                                  outside {@code 1..maxResults}
     * @throws OpenLibraryClientException on any controlled provider failure
     */
    public List<CulturalRecord> search(String query, int limit) {
        if (query == null || query.isBlank()) {
            throw new IllegalArgumentException("Open Library search query must not be null or blank");
        }
        if (limit < 1 || limit > properties.maxResults()) {
            throw new IllegalArgumentException(
                    "Open Library result limit must be between 1 and " + properties.maxResults()
                            + ", got: " + limit);
        }
        return doSearch(query.trim(), limit);
    }

    private List<CulturalRecord> doSearch(String query, int limit) {
        try {
            String body = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path(SEARCH_PATH)
                            .queryParam("q", query)
                            .queryParam("limit", limit)
                            .queryParam("fields", REQUESTED_FIELDS)
                            .build())
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            if (body == null || body.isBlank()) {
                throw new OpenLibraryMalformedResponseException(
                        "Open Library returned an empty body for " + SEARCH_PATH, null);
            }
            OpenLibrarySearchResponse response =
                    objectMapper.readValue(body, OpenLibrarySearchResponse.class);
            return mapToRecords(response);
        } catch (WebClientResponseException e) {
            throw mapHttpError(e);
        } catch (WebClientRequestException e) {
            if (isTimeout(e)) {
                throw new OpenLibraryTimeoutException(
                        "Open Library request timed out after " + properties.requestTimeout(), e);
            }
            throw new OpenLibraryClientException(
                    "Open Library unreachable: " + e.getMessage(), e);
        } catch (JacksonException e) {
            throw new OpenLibraryMalformedResponseException(
                    "Open Library returned malformed JSON for " + SEARCH_PATH, e);
        }
    }

    private OpenLibraryClientException mapHttpError(WebClientResponseException e) {
        int status = e.getStatusCode().value();
        String message = "Open Library answered HTTP " + status + " for " + SEARCH_PATH
                + " (" + querySnippet(e) + ")";
        if (status == 429) {
            return new OpenLibraryRateLimitException(message);
        }
        if (status >= 500) {
            return new OpenLibraryServerException(message);
        }
        return new OpenLibraryClientException(message, e);
    }

    private static String querySnippet(WebClientResponseException e) {
        try {
            String body = e.getResponseBodyAsString();
            if (body == null || body.isBlank()) {
                return "no response body";
            }
            return body.length() <= 200 ? body : body.substring(0, 200) + "...";
        } catch (RuntimeException ex) {
            return "response body not readable";
        }
    }

    private static boolean isTimeout(Throwable error) {
        for (Throwable cause = error; cause != null; cause = cause.getCause()) {
            if (cause instanceof TimeoutException
                    || cause instanceof ReadTimeoutException
                    || cause instanceof ConnectTimeoutException) {
                return true;
            }
        }
        return false;
    }

    private List<CulturalRecord> mapToRecords(OpenLibrarySearchResponse response) {
        if (response.getDocs() == null) {
            return List.of();
        }
        return response.getDocs().stream()
                .filter(doc -> doc.getKey() != null && !doc.getKey().isBlank()
                        && doc.getTitle() != null && !doc.getTitle().isBlank())
                .map(this::toRecord)
                .toList();
    }

    private CulturalRecord toRecord(OpenLibrarySearchResponse.OpenLibraryDoc doc) {
        CulturalRecord record = new CulturalRecord();
        record.setSource(Source.OPEN_LIBRARY.name());
        record.setExternalId(workKey(doc.getKey()));
        record.setTitle(doc.getTitle());
        record.setCreators(nullToEmpty(doc.getAuthorName()));
        record.setYear(doc.getFirstPublishYear());
        record.setSubjects(nullToEmpty(doc.getSubject()));
        record.setSourceUrl(canonicalWorkUrl(doc.getKey()));
        record.setLicense(OPEN_LIBRARY_LICENSE_NOTICE);
        record.setRetrievedAt(LocalDateTime.now().toString());
        return record;
    }

    /** {@code /works/OL12345W} → {@code OL12345W}. */
    private static String workKey(String path) {
        return path.substring(path.lastIndexOf('/') + 1);
    }

    private static String canonicalWorkUrl(String path) {
        return CANONICAL_BASE_URL + path;
    }

    private static List<String> nullToEmpty(List<String> values) {
        return values == null ? List.of() : values;
    }
}
