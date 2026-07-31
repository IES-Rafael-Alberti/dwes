package es.iesrafaelalberti.dwes.catalogoculturalhibrido.chat.ai;

import es.iesrafaelalberti.dwes.catalogoculturalhibrido.chat.CulturalChatGateway;
import es.iesrafaelalberti.dwes.catalogoculturalhibrido.service.CatalogRecommendationService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.retry.RetryPolicy;
import org.springframework.core.retry.RetryTemplate;

/** Wires the optional chat slice only when explicitly enabled. */
@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(name = "catalogo.ai.enabled", havingValue = "true")
@ConditionalOnProperty(name = "spring.ai.model.chat", havingValue = "ollama")
@ConditionalOnProperty(name = "spring.ai.model.embedding", havingValue = "none")
public class CulturalChatConfiguration {

    /**
     * Spring AI 2.0.0 maps max-attempts to Framework 7 maxRetries. Override the
     * transport template so the configured value of one remains one total call.
     */
    @Bean
    RetryTemplate catalogChatRetryTemplate() {
        return new RetryTemplate(RetryPolicy.withMaxRetries(0));
    }

    @Bean
    CulturalChatGateway culturalChatGateway(ObjectProvider<ChatClient.Builder> chatClientBuilder) {
        return new SpringAiCulturalChatGateway(chatClientBuilder);
    }

    @Bean
    CatalogRecommendationService catalogRecommendationService(CulturalChatGateway chatGateway) {
        return new CatalogRecommendationService(chatGateway);
    }
}
