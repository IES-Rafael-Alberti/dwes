package es.iesrafaelalberti.dwes.catalogoculturalhibrido.repository;

import es.iesrafaelalberti.dwes.catalogoculturalhibrido.model.CulturalItem;
import es.iesrafaelalberti.dwes.catalogoculturalhibrido.model.Source;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class CulturalItemRepositoryTest {

    @Autowired
    private CulturalItemRepository repository;

    @Test
    void shouldSaveAndFindBySourceAndExternalId() {
        CulturalItem item = new CulturalItem(
                Source.WIKIDATA,
                "Q480",
                "Don Quixote",
                null, 1605, null,
                "https://www.wikidata.org/wiki/Q480",
                "CC0-1.0",
                LocalDateTime.now()
        );
        repository.save(item);

        var found = repository.findBySourceAndExternalId(Source.WIKIDATA, "Q480");
        assertTrue(found.isPresent());
        assertEquals("Don Quixote", found.get().getTitle());
    }

    @Test
    void shouldReturnEmptyForUnknownPair() {
        var found = repository.findBySourceAndExternalId(Source.WIKIDATA, "Q-NONEXISTENT");
        assertTrue(found.isEmpty());
    }

    @Test
    void shouldEnforceUniqueConstraint() {
        CulturalItem item1 = new CulturalItem(
                Source.OPEN_LIBRARY,
                "OL123",
                "First",
                null, null, null,
                "http://example.com",
                "CC0",
                LocalDateTime.now()
        );
        repository.save(item1);

        CulturalItem item2 = new CulturalItem(
                Source.OPEN_LIBRARY,
                "OL123",
                "Duplicate",
                null, null, null,
                "http://example.com",
                "CC0",
                LocalDateTime.now()
        );
        assertThrows(DataIntegrityViolationException.class, () -> repository.saveAndFlush(item2));
    }

    @Test
    void shouldAllowSameExternalIdFromDifferentSource() {
        CulturalItem wd = new CulturalItem(
                Source.WIKIDATA,
                "OL123",
                "From Wikidata",
                null, null, null,
                "http://example.com",
                "CC0",
                LocalDateTime.now()
        );
        CulturalItem ol = new CulturalItem(
                Source.OPEN_LIBRARY,
                "OL123",
                "From Open Library",
                null, null, null,
                "http://example.com",
                "CC0",
                LocalDateTime.now()
        );
        repository.save(wd);
        repository.save(ol);

        assertEquals(2, repository.count());
    }

    @Test
    void shouldCheckExistenceBySourceAndExternalId() {
        CulturalItem item = new CulturalItem(
                Source.WIKIDATA,
                "Q42",
                "Test",
                null, null, null,
                "http://example.com",
                "CC0",
                LocalDateTime.now()
        );
        repository.save(item);

        assertTrue(repository.existsBySourceAndExternalId(Source.WIKIDATA, "Q42"));
        assertFalse(repository.existsBySourceAndExternalId(Source.WIKIDATA, "Q99"));
    }
}
