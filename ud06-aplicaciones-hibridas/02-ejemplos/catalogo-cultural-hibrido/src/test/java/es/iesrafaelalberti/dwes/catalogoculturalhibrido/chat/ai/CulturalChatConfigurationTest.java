package es.iesrafaelalberti.dwes.catalogoculturalhibrido.chat.ai;

import es.iesrafaelalberti.dwes.catalogoculturalhibrido.chat.CatalogChatException;
import es.iesrafaelalberti.dwes.catalogoculturalhibrido.chat.CulturalChatCandidate;
import es.iesrafaelalberti.dwes.catalogoculturalhibrido.chat.CulturalChatGateway;
import es.iesrafaelalberti.dwes.catalogoculturalhibrido.service.CatalogRecommendationService;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.DefaultChatClient;
import org.springframework.ai.chat.client.advisor.ToolCallingAdvisor;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.model.chat.client.autoconfigure.ChatClientAutoConfiguration;
import org.springframework.ai.model.tool.autoconfigure.ToolCallingAutoConfiguration;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.core.retry.RetryTemplate;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

class CulturalChatConfigurationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withUserConfiguration(CulturalChatConfiguration.class);

    @Test
    void aiDisabledCoreContextStartsWithoutChatBuilder() {
        contextRunner
                .withPropertyValues("catalogo.ai.enabled=false")
                .run(context -> {
                    assertThat(context).hasNotFailed();
                    assertThat(context).doesNotHaveBean(ChatClient.Builder.class);
                    assertThat(context).doesNotHaveBean(CulturalChatGateway.class);
                    assertThat(context).doesNotHaveBean(CatalogRecommendationService.class);
                });
    }

    @Test
    void aiEnabledWithoutChatInfrastructureFailsClosedOnlyOnInvocation() {
        contextRunner
                .withPropertyValues(
                        "catalogo.ai.enabled=true",
                        "spring.ai.model.chat=ollama",
                        "spring.ai.model.embedding=none")
                .run(context -> {
                    assertThat(context).hasNotFailed();
                    assertThat(context).hasSingleBean(CulturalChatGateway.class);
                    assertThat(context).hasSingleBean(CatalogRecommendationService.class);

                    CulturalChatGateway gateway = context.getBean(CulturalChatGateway.class);
                    assertThatThrownBy(() -> gateway.recommend(List.of(
                            new CulturalChatCandidate("ID-1", "Title", List.of(), null, List.of()))))
                            .isInstanceOfSatisfying(CatalogChatException.class, exception ->
                                    assertThat(exception.getReason())
                                            .isEqualTo(CatalogChatException.Reason.MODEL_UNAVAILABLE));
                });
    }

    @Test
    void actualChatClientAutoConfigurationProvidesBuilderWithoutActiveTools() {
        contextRunner
                .withConfiguration(AutoConfigurations.of(
                        ToolCallingAutoConfiguration.class,
                        ChatClientAutoConfiguration.class))
                .withBean(ChatModel.class, () -> mock(ChatModel.class))
                .withPropertyValues(
                        "catalogo.ai.enabled=true",
                        "spring.ai.model.chat=ollama",
                        "spring.ai.model.embedding=none",
                        "spring.ai.chat.client.tool-calling.enabled=false")
                .run(context -> {
                    assertThat(context).hasNotFailed();
                    assertThat(context).hasSingleBean(ChatClient.Builder.class);
                    assertThat(context).hasSingleBean(CulturalChatGateway.class);
                    assertThat(context).doesNotHaveBean(ToolCallback.class);

                    ChatClient chatClient = context.getBean(ChatClient.Builder.class).build();
                    assertThat(chatClient.prompt()).isInstanceOfSatisfying(
                            DefaultChatClient.DefaultChatClientRequestSpec.class,
                            request -> {
                                assertThat(request.getToolCallbacks()).isEmpty();
                                assertThat(request.getToolCallbackProviders()).isEmpty();
                                assertThat(request.getAdvisors())
                                        .noneMatch(ToolCallingAdvisor.class::isInstance);
                            });
                });
    }

    @Test
    void unsupportedModelSelectionsLeaveAiSliceUnavailable() {
        assertAiSliceUnavailable(
                "spring.ai.model.chat=openai",
                "spring.ai.model.embedding=none");
        assertAiSliceUnavailable(
                "spring.ai.model.chat=ollama",
                "spring.ai.model.embedding=ollama");
    }

    private void assertAiSliceUnavailable(String chatSelection, String embeddingSelection) {
        contextRunner
                .withPropertyValues(
                        "catalogo.ai.enabled=true",
                        chatSelection,
                        embeddingSelection)
                .run(context -> {
                    assertThat(context).hasNotFailed();
                    assertThat(context).doesNotHaveBean(RetryTemplate.class);
                    assertThat(context).doesNotHaveBean(CulturalChatGateway.class);
                    assertThat(context).doesNotHaveBean(CatalogRecommendationService.class);
                });
    }
}
