package es.iesrafaelalberti.dwes.catalogoculturalhibrido.chat;

import java.util.List;

/**
 * Application port for one stateless recommendation over supplied candidates.
 */
public interface CulturalChatGateway {

    CatalogRecommendation recommend(List<CulturalChatCandidate> candidates);
}
