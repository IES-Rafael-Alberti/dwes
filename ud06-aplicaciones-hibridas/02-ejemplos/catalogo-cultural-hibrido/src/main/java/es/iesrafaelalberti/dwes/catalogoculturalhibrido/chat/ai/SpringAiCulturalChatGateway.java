package es.iesrafaelalberti.dwes.catalogoculturalhibrido.chat.ai;

import es.iesrafaelalberti.dwes.catalogoculturalhibrido.chat.CatalogChatException;
import es.iesrafaelalberti.dwes.catalogoculturalhibrido.chat.CatalogRecommendation;
import es.iesrafaelalberti.dwes.catalogoculturalhibrido.chat.CulturalChatCandidate;
import es.iesrafaelalberti.dwes.catalogoculturalhibrido.chat.CulturalChatGateway;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.retry.NonTransientAiException;
import org.springframework.ai.retry.TransientAiException;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.web.client.ResourceAccessException;
import tools.jackson.core.JacksonException;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Optional outbound adapter backed by one synchronous Spring AI chat call.
 */
public final class SpringAiCulturalChatGateway implements CulturalChatGateway {

    private static final int MAX_RECOMMENDATIONS = 3;
    private static final int MAX_SUMMARY_LENGTH = 500;
    private static final int MAX_SOURCE_NOTE_LENGTH = 300;

    private final CulturalChatPromptBuilder promptBuilder;
    private final StructuredChatCall structuredChatCall;

    public SpringAiCulturalChatGateway(ChatClient.Builder chatClientBuilder) {
        this(new CulturalChatPromptBuilder(), springAiCall(chatClientBuilder));
    }

    public SpringAiCulturalChatGateway(ObjectProvider<ChatClient.Builder> chatClientBuilder) {
        this(new CulturalChatPromptBuilder(), springAiCall(chatClientBuilder));
    }

    SpringAiCulturalChatGateway(
            CulturalChatPromptBuilder promptBuilder,
            StructuredChatCall structuredChatCall) {
        this.promptBuilder = Objects.requireNonNull(promptBuilder);
        this.structuredChatCall = Objects.requireNonNull(structuredChatCall);
    }

    @Override
    public CatalogRecommendation recommend(List<CulturalChatCandidate> candidates) {
        Objects.requireNonNull(candidates, "candidates must not be null");
        List<CulturalChatCandidate> boundedCandidates = candidates.stream().limit(10).toList();
        String prompt = promptBuilder.build(boundedCandidates);

        AiRecommendationResponse response;
        try {
            response = structuredChatCall.call(promptBuilder.systemInstructions(), prompt);
        } catch (JacksonException exception) {
            throw CatalogChatException.invalidResponse();
        } catch (TransientAiException | NonTransientAiException | ResourceAccessException exception) {
            throw CatalogChatException.modelUnavailable(exception);
        }
        return validated(response, boundedCandidates);
    }

    private CatalogRecommendation validated(
            AiRecommendationResponse response,
            List<CulturalChatCandidate> candidates) {
        if (response == null
                || blank(response.summary())
                || response.summary().length() > MAX_SUMMARY_LENGTH
                || hasControlCharacter(response.summary())
                || blank(response.sourceNote())
                || response.sourceNote().length() > MAX_SOURCE_NOTE_LENGTH
                || hasControlCharacter(response.sourceNote())
                || response.recommendedIds() == null
                || response.recommendedIds().isEmpty()
                || response.recommendedIds().size() > MAX_RECOMMENDATIONS) {
            throw CatalogChatException.invalidResponse();
        }

        Set<String> allowedIds = candidates.stream()
                .map(CulturalChatCandidate::id)
                .collect(java.util.stream.Collectors.toSet());
        Set<String> uniqueIds = new HashSet<>(response.recommendedIds());
        if (uniqueIds.size() != response.recommendedIds().size()
                || response.recommendedIds().stream().anyMatch(id -> id == null
                        || hasControlCharacter(id)
                        || !allowedIds.contains(id))) {
            throw CatalogChatException.invalidResponse();
        }

        return new CatalogRecommendation(
                response.summary().strip(),
                response.recommendedIds(),
                response.sourceNote().strip());
    }

    private boolean blank(String value) {
        return value == null || value.isBlank();
    }

    private static boolean hasControlCharacter(String value) {
        return value.codePoints().anyMatch(Character::isISOControl);
    }

    private static StructuredChatCall springAiCall(ChatClient.Builder chatClientBuilder) {
        ChatClient chatClient = Objects.requireNonNull(chatClientBuilder).build();
        return springAiCall(chatClient);
    }

    private static StructuredChatCall springAiCall(ObjectProvider<ChatClient.Builder> chatClientBuilder) {
        Objects.requireNonNull(chatClientBuilder);
        return (systemInstructions, userPrompt) -> {
            ChatClient.Builder builder = chatClientBuilder.getIfAvailable();
            if (builder == null) {
                throw CatalogChatException.modelUnavailable(
                        new IllegalStateException("ChatClient.Builder is unavailable"));
            }
            return springAiCall(builder.build()).call(systemInstructions, userPrompt);
        };
    }

    private static StructuredChatCall springAiCall(ChatClient chatClient) {
        return (systemInstructions, userPrompt) -> chatClient.prompt()
                .system(systemInstructions)
                .user(userPrompt)
                .call()
                .entity(AiRecommendationResponse.class);
    }

    @FunctionalInterface
    interface StructuredChatCall {
        AiRecommendationResponse call(String systemInstructions, String userPrompt);
    }

    /** Spring AI structured-output projection; validated before crossing the port. */
    public record AiRecommendationResponse(
            String summary,
            List<String> recommendedIds,
            String sourceNote) {
    }
}
