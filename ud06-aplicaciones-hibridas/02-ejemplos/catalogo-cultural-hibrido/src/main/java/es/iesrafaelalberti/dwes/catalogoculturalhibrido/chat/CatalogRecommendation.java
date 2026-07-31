package es.iesrafaelalberti.dwes.catalogoculturalhibrido.chat;

import java.util.List;

/**
 * Small structured result returned by the optional recommendation use case.
 */
public record CatalogRecommendation(
        String summary,
        List<String> recommendedIds,
        String sourceNote) {

    public CatalogRecommendation {
        recommendedIds = recommendedIds == null ? List.of() : List.copyOf(recommendedIds);
    }
}
