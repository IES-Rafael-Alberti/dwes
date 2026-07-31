package es.iesrafaelalberti.dwes.catalogoculturalhibrido.service;

import es.iesrafaelalberti.dwes.catalogoculturalhibrido.model.CulturalItem;
import es.iesrafaelalberti.dwes.catalogoculturalhibrido.model.Source;
import es.iesrafaelalberti.dwes.catalogoculturalhibrido.repository.CulturalItemRepository;
import es.iesrafaelalberti.dwes.catalogoculturalhibrido.service.dto.CulturalRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class IngestionServiceTest {

    @Autowired
    private IngestionService ingestionService;

    @Autowired
    private CulturalItemRepository repository;

    @BeforeEach
    void cleanDatabase() {
        repository.deleteAll();
    }

    @Test
    void shouldParseFixtureAndPersistRecords() {
        List<CulturalItem> items = ingestionService.ingestFixture("dataset/wikidata-cultural-fixture.json");

        assertEquals(5, items.size());
        assertEquals(5, repository.count());

        assertTrue(repository.existsBySourceAndExternalId(Source.WIKIDATA, "Q480"));
        assertTrue(repository.existsBySourceAndExternalId(Source.WIKIDATA, "Q12418"));
    }

    @Test
    void shouldBeIdempotentOnReImport() {
        ingestionService.ingestFixture("dataset/wikidata-cultural-fixture.json");
        assertEquals(5, repository.count());

        List<CulturalItem> reImported = ingestionService.ingestFixture("dataset/wikidata-cultural-fixture.json");
        assertEquals(5, reImported.size());
        assertEquals(5, repository.count(), "Re-import must not create duplicates");
    }

    @Test
    void shouldUpdateExistingRecordsOnReImport() {
        ingestionService.ingestFixture("dataset/wikidata-cultural-fixture.json");

        var record = new CulturalRecord();
        record.setSource("WIKIDATA");
        record.setExternalId("Q480");
        record.setTitle("Don Quixote (Updated)");
        record.setCreators(List.of("Miguel de Cervantes", "Anonymous"));
        record.setYear(1615);
        record.setSubjects(List.of("novel", "Spanish Golden Age", "updated"));
        record.setSourceUrl("https://www.wikidata.org/wiki/Special:EntityData/Q480.json");
        record.setLicense("CC0-1.0");
        record.setRetrievedAt("2026-07-30T12:00:00");

        ingestionService.ingestRecords(List.of(record));

        var updated = repository.findBySourceAndExternalId(Source.WIKIDATA, "Q480");
        assertTrue(updated.isPresent());
        assertEquals("Don Quixote (Updated)", updated.get().getTitle());
        assertEquals(List.of("Miguel de Cervantes", "Anonymous"), updated.get().getCreators());
        assertEquals(1615, updated.get().getYear());
        assertEquals(List.of("novel", "Spanish Golden Age", "updated"), updated.get().getSubjects());
        assertEquals("https://www.wikidata.org/wiki/Special:EntityData/Q480.json", updated.get().getSourceUrl());
        assertEquals("CC0-1.0", updated.get().getLicense());
        assertEquals(LocalDateTime.parse("2026-07-30T12:00:00"), updated.get().getRetrievedAt());
    }

    @Test
    void shouldFailOnMalformedFixture() {
        assertThrows(IngestionService.IngestionException.class,
                () -> ingestionService.ingestFixture("dataset/malformed-fixture.json"));
    }

    @Test
    void shouldFailOnMissingFixture() {
        assertThrows(IngestionService.IngestionException.class,
                () -> ingestionService.ingestFixture("dataset/nonexistent.json"));
    }

    @Test
    void shouldNotPersistPartialStateOnMalformedJson() {
        assertThrows(IngestionService.IngestionException.class,
                () -> ingestionService.ingestFixture("dataset/malformed-fixture.json"));
        assertEquals(0, repository.count(),
                "No records should persist after a JSON parse failure");
    }

    @Test
    void shouldMixUpdateAndInsertInSameIngest() {
        ingestionService.ingestFixture("dataset/wikidata-cultural-fixture.json");
        assertEquals(5, repository.count());

        var existing = new CulturalRecord();
        existing.setSource("WIKIDATA");
        existing.setExternalId("Q480");
        existing.setTitle("Don Quixote (Updated)");
        existing.setCreators(List.of("Miguel de Cervantes", "Anonymous"));
        existing.setYear(1615);
        existing.setSubjects(List.of("novel", "Spanish Golden Age", "updated"));
        existing.setSourceUrl("https://www.wikidata.org/wiki/Q480");
        existing.setLicense("CC0-1.0");
        existing.setRetrievedAt("2026-07-30T12:00:00");

        var fresh = new CulturalRecord();
        fresh.setSource("WIKIDATA");
        fresh.setExternalId("Q45585");
        fresh.setTitle("The Starry Night");
        fresh.setCreators(List.of("Vincent van Gogh"));
        fresh.setYear(1889);
        fresh.setSubjects(List.of("painting", "Post-Impressionism"));
        fresh.setSourceUrl("https://www.wikidata.org/wiki/Q45585");
        fresh.setLicense("CC0-1.0");
        fresh.setRetrievedAt("2026-07-30T12:00:00");

        List<CulturalItem> items = ingestionService.ingestRecords(List.of(existing, fresh));

        assertEquals(2, items.size());
        assertEquals(6, repository.count(),
                "One update and one insert in the same call must not create duplicates");

        var updated = repository.findBySourceAndExternalId(Source.WIKIDATA, "Q480");
        assertTrue(updated.isPresent());
        assertEquals("Don Quixote (Updated)", updated.get().getTitle());

        assertTrue(repository.existsBySourceAndExternalId(Source.WIKIDATA, "Q45585"),
                "The new record must be inserted in the same ingestion call");
    }

    @Test
    void shouldTreatEmptyRecordListAsNoOp() {
        List<CulturalItem> items = ingestionService.ingestRecords(List.of());

        assertTrue(items.isEmpty());
        assertEquals(0, repository.count(), "An empty batch is a no-op, not an error");
    }

    @Test
    void shouldUseEachRecordsOwnSourceInMixedSourceBatch() {
        var wikidata = new CulturalRecord();
        wikidata.setSource("WIKIDATA");
        wikidata.setExternalId("SHARED-ID");
        wikidata.setTitle("From Wikidata");
        wikidata.setRetrievedAt("2026-07-30T12:00:00");

        var openLibrary = new CulturalRecord();
        openLibrary.setSource("OPEN_LIBRARY");
        openLibrary.setExternalId("SHARED-ID");
        openLibrary.setTitle("From Open Library");
        openLibrary.setRetrievedAt("2026-07-30T12:00:00");

        List<CulturalItem> items = ingestionService.ingestRecords(List.of(wikidata, openLibrary));

        assertEquals(2, items.size());
        assertEquals(2, repository.count(),
                "Same externalId under different sources must not collide");

        var storedWikidata = repository.findBySourceAndExternalId(Source.WIKIDATA, "SHARED-ID");
        assertTrue(storedWikidata.isPresent());
        assertEquals("From Wikidata", storedWikidata.get().getTitle());

        var storedOpenLibrary = repository.findBySourceAndExternalId(Source.OPEN_LIBRARY, "SHARED-ID");
        assertTrue(storedOpenLibrary.isPresent());
        assertEquals("From Open Library", storedOpenLibrary.get().getTitle());
    }

    @Test
    void shouldRollBackOnConstraintViolation() {
        var valid = new CulturalRecord();
        valid.setSource("WIKIDATA");
        valid.setExternalId("Q-ROLLBACK-1");
        valid.setTitle("Valid Record");
        valid.setRetrievedAt("2026-07-30T12:00:00");

        var invalid = new CulturalRecord();
        invalid.setSource("WIKIDATA");
        invalid.setExternalId("Q-ROLLBACK-2");
        invalid.setTitle(null);
        invalid.setRetrievedAt("2026-07-30T12:00:00");

        assertThrows(DataIntegrityViolationException.class,
                () -> ingestionService.ingestRecords(List.of(valid, invalid)));
        assertEquals(0, repository.count(),
                "Entire transaction must roll back when any record violates constraints");
        assertTrue(repository.findBySourceAndExternalId(Source.WIKIDATA, "Q-ROLLBACK-1").isEmpty(),
                "The valid insert must be rolled back together with the failing record");
    }
}
