import java.util.List;

public final class GeoQueries {
    private GeoQueries() {
    }

    public record Point(double lat, double lon) {
    }

    public record Note(String title, Point location) {
    }

    public static List<String> inside(List<Note> notes, Point cornerA, Point cornerB) {
        throw new UnsupportedOperationException("Completa J25-08");
    }
}
