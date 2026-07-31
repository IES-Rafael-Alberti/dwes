package es.iesrafaelalberti.dwes.catalogoculturalhibrido.service;

import es.iesrafaelalberti.dwes.catalogoculturalhibrido.client.OpenLibraryClient;
import es.iesrafaelalberti.dwes.catalogoculturalhibrido.model.CulturalItem;
import es.iesrafaelalberti.dwes.catalogoculturalhibrido.service.dto.CulturalRecord;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Orchestrates a remote search against Open Library and the idempotent
 * persistence of the mapped results.
 *
 * <p><strong>Transaction boundary.</strong> This service is intentionally not
 * transactional. The remote call in {@link OpenLibraryClient} must complete
 * <em>before</em> a DB transaction is opened: holding a JPA transaction open
 * across network I/O locks database resources while waiting for a third party.
 * {@link IngestionService#ingestRecords} starts its own transaction, so either
 * the whole batch is persisted idempotently or none of it is.</p>
 *
 * <p>Provider failures propagate as {@link es.iesrafaelalberti.dwes.catalogoculturalhibrido.client.OpenLibraryClientException}
 * without any state being persisted. Programming and JPA failures are never
 * swallowed.</p>
 */
@Service
public class CatalogSearchService {

    private final OpenLibraryClient openLibraryClient;
    private final IngestionService ingestionService;

    public CatalogSearchService(OpenLibraryClient openLibraryClient, IngestionService ingestionService) {
        this.openLibraryClient = openLibraryClient;
        this.ingestionService = ingestionService;
    }

    /**
     * Searches Open Library and upserts the results by {@code (OPEN_LIBRARY, externalId)}.
     *
     * @return the persisted {@link CulturalItem}s, one per mapped record
     */
    public List<CulturalItem> searchAndIngest(String query, int limit) {
        List<CulturalRecord> records = openLibraryClient.search(query, limit);
        return ingestionService.ingestRecords(records);
    }
}
