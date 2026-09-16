import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CollectionQueriesTest {
    @Test
    void returnsNewestNonBlankTitlesWithoutChangingInput() {
        var old = new CollectionQueries.Note("antigua", Instant.parse("2026-01-01T00:00:00Z"));
        var newest = new CollectionQueries.Note(" nueva ", Instant.parse("2026-03-01T00:00:00Z"));
        var blank = new CollectionQueries.Note("  ", Instant.parse("2026-04-01T00:00:00Z"));
        var input = List.of(old, newest, blank);

        assertEquals(List.of("nueva", "antigua"),
                CollectionQueries.latestTitles(input, 2));
        assertEquals(List.of(old, newest, blank), input);
    }
}
