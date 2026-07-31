package es.iesrafaelalberti.dwes.catalogoculturalhibrido.client;

import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Unit tests for the fail-fast validation in {@link OpenLibraryProperties}.
 *
 * <p>No Spring context is started: the compact constructor is exercised
 * directly, so startup failures are proven offline.</p>
 */
class OpenLibraryPropertiesTest {

    private static final String USER_AGENT = "DWES-UD6-test/1.0";
    private static final Duration TIMEOUT = Duration.ofSeconds(5);

    @Test
    void shouldRejectSchemeRelativeBaseUrl() {
        assertThrows(IllegalArgumentException.class,
                () -> new OpenLibraryProperties("//openlibrary.org", USER_AGENT, TIMEOUT, 10),
                "scheme-relative base URL must be rejected with the intended validation exception");
    }

    @Test
    void shouldAcceptExactlyMaxAllowedResults() {
        OpenLibraryProperties properties = new OpenLibraryProperties(
                "https://openlibrary.org", USER_AGENT, TIMEOUT, 10);

        assertEquals(10, properties.maxResults());
    }

    @Test
    void shouldRejectResultsAboveMaxAllowed() {
        assertThrows(IllegalArgumentException.class,
                () -> new OpenLibraryProperties("https://openlibrary.org", USER_AGENT, TIMEOUT, 11),
                "a limit above the classroom-safe maximum (10) must be rejected");
    }

    @Test
    void shouldRejectZeroAndNegativeResults() {
        assertThrows(IllegalArgumentException.class,
                () -> new OpenLibraryProperties("https://openlibrary.org", USER_AGENT, TIMEOUT, 0));
        assertThrows(IllegalArgumentException.class,
                () -> new OpenLibraryProperties("https://openlibrary.org", USER_AGENT, TIMEOUT, -1));
    }

    @Test
    void shouldDefaultMaxResultsWhenAbsent() {
        OpenLibraryProperties properties = new OpenLibraryProperties(
                "https://openlibrary.org", USER_AGENT, TIMEOUT, null);

        assertEquals(OpenLibraryProperties.DEFAULT_MAX_RESULTS, properties.maxResults());
    }
}
