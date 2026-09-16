import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GeoQueriesTest {
    @Test
    void preservesOrderAndIncludesBorders() {
        var notes = List.of(
                new GeoQueries.Note("Cadiz", new GeoQueries.Point(36.5, -6.3)),
                new GeoQueries.Note("Sevilla", new GeoQueries.Point(37.4, -6.0)),
                new GeoQueries.Note("Madrid", new GeoQueries.Point(40.4, -3.7)));

        var result = GeoQueries.inside(notes,
                new GeoQueries.Point(36.0, -7.0),
                new GeoQueries.Point(37.4, -6.0));

        assertEquals(List.of("Cadiz", "Sevilla"), result);
    }
}
