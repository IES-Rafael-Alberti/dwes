package es.iesrafaelalberti.dwes.catalogoculturalhibrido.service;

import es.iesrafaelalberti.dwes.catalogoculturalhibrido.chat.CatalogRecommendation;
import es.iesrafaelalberti.dwes.catalogoculturalhibrido.chat.CulturalChatCandidate;
import es.iesrafaelalberti.dwes.catalogoculturalhibrido.chat.CulturalChatGateway;
import es.iesrafaelalberti.dwes.catalogoculturalhibrido.service.dto.CulturalRecord;

import java.util.List;
import java.util.Objects;

/**
 * Pure application orchestration for the optional, stateless chat enrichment.
 */
public class CatalogRecommendationService {

    public static final int MAX_CANDIDATES = 10;

    private final CulturalChatGateway chatGateway;

    public CatalogRecommendationService(CulturalChatGateway chatGateway) {
        this.chatGateway = Objects.requireNonNull(chatGateway, "chatGateway must not be null");
    }

    /**
     * Makes exactly one chat request over at most ten already-normalized records.
     */
    public CatalogRecommendation recommend(List<CulturalRecord> normalizedRecords) {
        Objects.requireNonNull(normalizedRecords, "normalizedRecords must not be null");
        if (normalizedRecords.isEmpty()) {
            throw new IllegalArgumentException("At least one normalized record is required");
        }

        List<CulturalChatCandidate> candidates = normalizedRecords.stream()
                .limit(MAX_CANDIDATES)
                .map(this::toCandidate)
                .toList();
        return chatGateway.recommend(candidates);
    }

    private CulturalChatCandidate toCandidate(CulturalRecord record) {
        Objects.requireNonNull(record, "normalized record must not be null");
        String source = required(record.getSource(), "source");
        String externalId = required(record.getExternalId(), "externalId");
        String candidateId = source + ":" + externalId;
        if (candidateId.length() > 120) {
            throw new IllegalArgumentException("Catalog candidate ID exceeds 120 characters");
        }
        return new CulturalChatCandidate(
                candidateId,
                record.getTitle(),
                record.getCreators(),
                record.getYear(),
                record.getSubjects());
    }

    private String required(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Normalized record " + field + " must not be blank");
        }
        return value.strip();
    }
}
