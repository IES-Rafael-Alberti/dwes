package es.iesrafaelalberti.dwes.catalogoculturalhibrido.chat.ai;

import es.iesrafaelalberti.dwes.catalogoculturalhibrido.chat.CatalogChatException;
import es.iesrafaelalberti.dwes.catalogoculturalhibrido.chat.CatalogRecommendation;
import es.iesrafaelalberti.dwes.catalogoculturalhibrido.chat.CulturalChatCandidate;
import org.junit.jupiter.api.Test;
import org.springframework.ai.retry.TransientAiException;
import tools.jackson.core.JacksonException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SpringAiCulturalChatGatewayTest {

    private final List<CulturalChatCandidate> candidates = List.of(
            new CulturalChatCandidate("ID-1", "One", List.of("Author"), 2001, List.of("Art")),
            new CulturalChatCandidate("ID-2", "Two", List.of("Author"), 2002, List.of("History")));

    @Test
    void acceptsAValidStructuredResponseFromTheSpringAiBoundary() {
        SpringAiCulturalChatGateway gateway = gatewayReturning(
                new SpringAiCulturalChatGateway.AiRecommendationResponse(
                        "  Two useful entries  ", List.of("ID-2", "ID-1"), "  Supplied metadata only  "));

        CatalogRecommendation result = gateway.recommend(candidates);

        assertThat(result.summary()).isEqualTo("Two useful entries");
        assertThat(result.recommendedIds()).containsExactly("ID-2", "ID-1");
        assertThat(result.sourceNote()).isEqualTo("Supplied metadata only");
    }

    @Test
    void rejectsEmptyStructuredOutput() {
        SpringAiCulturalChatGateway gateway = gatewayReturning(null);

        assertInvalidResponse(() -> gateway.recommend(candidates));
    }

    @Test
    void mapsMalformedStructuredJsonToAControlledInvalidResponse() {
        SpringAiCulturalChatGateway gateway = new SpringAiCulturalChatGateway(
                new CulturalChatPromptBuilder(),
                (system, prompt) -> {
                    throw new MalformedOutputException("invalid JSON");
                });

        assertInvalidResponse(() -> gateway.recommend(candidates));
    }

    @Test
    void rejectsRecommendationIdsOutsideTheSuppliedCandidates() {
        SpringAiCulturalChatGateway gateway = gatewayReturning(
                new SpringAiCulturalChatGateway.AiRecommendationResponse(
                        "Summary", List.of("INVENTED-ID"), "Source note"));

        assertInvalidResponse(() -> gateway.recommend(candidates));
    }

    @Test
    void rejectsDuplicateAndBlankStructuredFields() {
        SpringAiCulturalChatGateway duplicateGateway = gatewayReturning(
                new SpringAiCulturalChatGateway.AiRecommendationResponse(
                        "Summary", List.of("ID-1", "ID-1"), "Source note"));
        SpringAiCulturalChatGateway blankGateway = gatewayReturning(
                new SpringAiCulturalChatGateway.AiRecommendationResponse(
                        " ", List.of("ID-1"), "Source note"));

        assertInvalidResponse(() -> duplicateGateway.recommend(candidates));
        assertInvalidResponse(() -> blankGateway.recommend(candidates));
    }

    @Test
    void mapsModelConnectivityFailuresToAControlledDomainException() {
        SpringAiCulturalChatGateway gateway = new SpringAiCulturalChatGateway(
                new CulturalChatPromptBuilder(),
                (system, prompt) -> {
                    throw new TransientAiException("Ollama unavailable");
                });

        assertThatThrownBy(() -> gateway.recommend(candidates))
                .isInstanceOfSatisfying(CatalogChatException.class, exception ->
                        assertThat(exception.getReason()).isEqualTo(CatalogChatException.Reason.MODEL_UNAVAILABLE))
                .hasMessage("The optional chat model is unavailable");
    }

    @Test
    void rejectsControlCharactersInModelText() {
        SpringAiCulturalChatGateway summaryGateway = gatewayReturning(
                new SpringAiCulturalChatGateway.AiRecommendationResponse(
                        "Unsafe\u0000summary", List.of("ID-1"), "Source note"));
        SpringAiCulturalChatGateway sourceGateway = gatewayReturning(
                new SpringAiCulturalChatGateway.AiRecommendationResponse(
                        "Summary", List.of("ID-1"), "Unsafe\u001bsource"));

        assertInvalidResponse(() -> summaryGateway.recommend(candidates));
        assertInvalidResponse(() -> sourceGateway.recommend(candidates));
    }

    @Test
    void rejectsModelTextBeyondDocumentedBounds() {
        SpringAiCulturalChatGateway summaryGateway = gatewayReturning(
                new SpringAiCulturalChatGateway.AiRecommendationResponse(
                        "s".repeat(501), List.of("ID-1"), "Source note"));
        SpringAiCulturalChatGateway sourceGateway = gatewayReturning(
                new SpringAiCulturalChatGateway.AiRecommendationResponse(
                        "Summary", List.of("ID-1"), "n".repeat(301)));

        assertInvalidResponse(() -> summaryGateway.recommend(candidates));
        assertInvalidResponse(() -> sourceGateway.recommend(candidates));
    }

    private SpringAiCulturalChatGateway gatewayReturning(
            SpringAiCulturalChatGateway.AiRecommendationResponse response) {
        return new SpringAiCulturalChatGateway(
                new CulturalChatPromptBuilder(),
                (system, prompt) -> response);
    }

    private void assertInvalidResponse(Runnable call) {
        assertThatThrownBy(call::run)
                .isInstanceOfSatisfying(CatalogChatException.class, exception ->
                        assertThat(exception.getReason()).isEqualTo(CatalogChatException.Reason.INVALID_RESPONSE))
                .hasMessage("The optional chat model returned an invalid recommendation");
    }

    private static final class MalformedOutputException extends JacksonException {
        private MalformedOutputException(String message) {
            super(message);
        }
    }
}
