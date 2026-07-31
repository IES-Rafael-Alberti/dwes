package es.iesrafaelalberti.dwes.catalogoculturalhibrido.chat.ai;

import org.junit.jupiter.api.Test;
import org.springframework.ai.model.chat.client.autoconfigure.ChatClientBuilderProperties;
import org.springframework.ai.retry.TransientAiException;
import org.springframework.ai.retry.autoconfigure.SpringAiRetryAutoConfiguration;
import org.springframework.ai.retry.autoconfigure.SpringAiRetryProperties;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.test.context.ConfigDataApplicationContextInitializer;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.core.retry.RetryTemplate;

import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AiSafetyConfigurationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withInitializer(new ConfigDataApplicationContextInitializer())
            .withUserConfiguration(CulturalChatConfiguration.class)
            .withConfiguration(AutoConfigurations.of(SpringAiRetryAutoConfiguration.class));

    @Test
    void mainConfigurationBindsOneAttemptAndDisablesToolCalling() {
        contextRunner.run(context -> {
            Binder binder = Binder.get(context.getEnvironment());
            SpringAiRetryProperties retry = binder.bind(
                    SpringAiRetryProperties.CONFIG_PREFIX, SpringAiRetryProperties.class)
                    .orElseThrow(() -> new AssertionError("retry properties were not bound"));
            ChatClientBuilderProperties chatClient = binder.bind(
                    ChatClientBuilderProperties.CONFIG_PREFIX, ChatClientBuilderProperties.class)
                    .orElseThrow(() -> new AssertionError("chat client properties were not bound"));

            assertThat(retry.getMaxAttempts()).isEqualTo(1);
            assertThat(chatClient.getToolCalling().isEnabled()).isFalse();
        });
    }

    @Test
    void transientFailureMakesOnlyOneAttemptWithoutOllama() {
        contextRunner.withPropertyValues(
                "catalogo.ai.enabled=true",
                "spring.ai.model.chat=ollama",
                "spring.ai.model.embedding=none").run(context -> {
            AtomicInteger attempts = new AtomicInteger();
            RetryTemplate retryTemplate = context.getBean(RetryTemplate.class);

            assertThatThrownBy(() -> retryTemplate.invoke(() -> {
                attempts.incrementAndGet();
                throw new TransientAiException("simulated transport failure");
            })).isInstanceOf(TransientAiException.class);
            assertThat(attempts).hasValue(1);
        });
    }
}
