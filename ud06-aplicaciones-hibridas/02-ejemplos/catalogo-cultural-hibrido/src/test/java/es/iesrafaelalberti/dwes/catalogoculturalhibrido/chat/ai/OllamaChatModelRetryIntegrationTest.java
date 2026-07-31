package es.iesrafaelalberti.dwes.catalogoculturalhibrido.chat.ai;

import es.iesrafaelalberti.dwes.catalogoculturalhibrido.chat.CulturalChatGateway;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.model.chat.client.autoconfigure.ChatClientAutoConfiguration;
import org.springframework.ai.model.ollama.autoconfigure.OllamaApiAutoConfiguration;
import org.springframework.ai.model.ollama.autoconfigure.OllamaChatAutoConfiguration;
import org.springframework.ai.model.tool.autoconfigure.ToolCallingAutoConfiguration;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.ollama.api.OllamaApi;
import org.springframework.ai.retry.TransientAiException;
import org.springframework.ai.retry.autoconfigure.SpringAiRetryAutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.ConfigDataApplicationContextInitializer;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.core.retry.RetryTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class OllamaChatModelRetryIntegrationTest {

    private final OllamaApi ollamaApi = mock(OllamaApi.class);

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withInitializer(new ConfigDataApplicationContextInitializer())
            .withConfiguration(AutoConfigurations.of(
                    SpringAiRetryAutoConfiguration.class,
                    ToolCallingAutoConfiguration.class,
                    OllamaApiAutoConfiguration.class,
                    OllamaChatAutoConfiguration.class,
                    ChatClientAutoConfiguration.class))
            .withUserConfiguration(CulturalChatConfiguration.class)
            .withBean(OllamaApi.class, () -> ollamaApi)
            .withPropertyValues(
                    "catalogo.ai.enabled=true",
                    "spring.ai.model.chat=ollama",
                    "spring.ai.model.embedding=none",
                    "spring.ai.chat.client.tool-calling.enabled=false",
                    "spring.ai.ollama.chat.model=offline-test-model",
                    "spring.ai.ollama.init.pull-model-strategy=never");

    @Test
    void ollamaModelUsesScopedZeroRetryTemplateForOneTransportAttempt() {
        when(ollamaApi.chat(any(OllamaApi.ChatRequest.class)))
                .thenThrow(new TransientAiException("simulated Ollama transport failure"));

        contextRunner.run(context -> {
            assertThat(context).hasNotFailed();
            assertThat(context.getBeansOfType(ChatModel.class))
                    .containsOnlyKeys("ollamaChatModel");
            assertThat(context).hasSingleBean(OllamaChatModel.class);
            assertThat(context).hasSingleBean(ChatClient.Builder.class);
            assertThat(context).hasSingleBean(CulturalChatGateway.class);
            assertThat(context).doesNotHaveBean(EmbeddingModel.class);

            OllamaChatModel chatModel = context.getBean(OllamaChatModel.class);
            RetryTemplate retryTemplate = context.getBean(
                    "catalogChatRetryTemplate", RetryTemplate.class);
            assertThat(ReflectionTestUtils.getField(chatModel, "retryTemplate"))
                    .isSameAs(retryTemplate);

            assertThatThrownBy(() -> chatModel.call(new Prompt("offline retry test")))
                    .isInstanceOf(TransientAiException.class)
                    .hasMessageContaining("simulated Ollama transport failure");

            verify(ollamaApi, times(1)).chat(any(OllamaApi.ChatRequest.class));
        });
    }
}
