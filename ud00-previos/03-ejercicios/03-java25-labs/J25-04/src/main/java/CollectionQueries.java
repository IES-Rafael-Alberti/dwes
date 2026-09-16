import java.time.Instant;
import java.util.List;

public final class CollectionQueries {
    private CollectionQueries() {
    }

    public record Note(String title, Instant createdAt) {
    }

    public static List<String> latestTitles(List<Note> notes, int limit) {
        throw new UnsupportedOperationException("Completa J25-04");
    }
}
