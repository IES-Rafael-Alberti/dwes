package es.iesrafaelalberti.dwes.catalogoculturalhibrido.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CulturalItemTest {

    @Test
    void shouldCreateEntityWithAllFields() {
        LocalDateTime now = LocalDateTime.now();
        CulturalItem item = new CulturalItem(
                Source.WIKIDATA,
                "Q480",
                "Don Quixote",
                List.of("Miguel de Cervantes"),
                1605,
                List.of("novel", "Spanish Golden Age"),
                "https://www.wikidata.org/wiki/Q480",
                "CC0-1.0",
                now
        );

        assertEquals(Source.WIKIDATA, item.getSource());
        assertEquals("Q480", item.getExternalId());
        assertEquals("Don Quixote", item.getTitle());
        assertEquals(1, item.getCreators().size());
        assertTrue(item.getCreators().contains("Miguel de Cervantes"));
        assertEquals(1605, item.getYear());
        assertEquals(2, item.getSubjects().size());
        assertTrue(item.getSubjects().contains("novel"));
        assertEquals("https://www.wikidata.org/wiki/Q480", item.getSourceUrl());
        assertEquals("CC0-1.0", item.getLicense());
        assertEquals(now, item.getRetrievedAt());
    }

    @Test
    void shouldAllowNullOptionalFields() {
        CulturalItem item = new CulturalItem(
                Source.OPEN_LIBRARY,
                "OL123",
                "Test",
                null,
                null,
                null,
                "http://example.com",
                "Unknown",
                LocalDateTime.now()
        );

        assertNull(item.getCreators());
        assertNull(item.getYear());
        assertNull(item.getSubjects());
    }

    @Test
    void shouldUpdateFieldsViaSetters() {
        CulturalItem item = new CulturalItem(
                Source.WIKIDATA,
                "Q1",
                "Original",
                null,
                null, null,
                "http://example.com",
                "CC0",
                LocalDateTime.now()
        );

        item.setTitle("Updated");
        item.setYear(2024);
        item.setCreators(List.of("Author"));
        item.setSubjects(List.of("updated", "test"));

        assertEquals("Updated", item.getTitle());
        assertEquals(2024, item.getYear());
        assertEquals(1, item.getCreators().size());
        assertEquals(2, item.getSubjects().size());
    }
}
