package com.example.battleship.config;

import java.util.Optional;

import org.springdoc.core.properties.SwaggerUiConfigProperties;
import org.springdoc.core.properties.SwaggerUiOAuthProperties;
import org.springdoc.core.properties.SpringDocConfigProperties;
import org.springdoc.core.providers.ObjectMapperProvider;
import org.springdoc.core.providers.SpringWebProvider;
import org.springdoc.webmvc.core.providers.SpringWebMvcProvider;
import org.springdoc.webmvc.ui.SwaggerConfigResource;
import org.springdoc.webmvc.ui.SwaggerIndexPageTransformer;
import org.springdoc.webmvc.ui.SwaggerIndexTransformer;
import org.springdoc.webmvc.ui.SwaggerResourceResolver;
import org.springdoc.webmvc.ui.SwaggerWebMvcConfigurer;
import org.springdoc.webmvc.ui.SwaggerWelcomeCommon;
import org.springdoc.webmvc.ui.SwaggerWelcomeWebMvc;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.webmvc.autoconfigure.WebMvcProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.accept.ApiVersionStrategy;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Configuration
@EnableConfigurationProperties({
        SpringDocConfigProperties.class,
        SwaggerUiConfigProperties.class,
        SwaggerUiOAuthProperties.class
})
public class OpenApiConfig {

    @Bean
    ObjectMapperProvider springdocObjectMapperProvider(SpringDocConfigProperties properties) {
        return new ObjectMapperProvider(properties);
    }

    @Bean
    SpringWebProvider springWebProvider(Optional<ApiVersionStrategy> apiVersionStrategy) {
        return new SpringWebMvcProvider(apiVersionStrategy);
    }

    @Bean
    SwaggerWelcomeCommon swaggerWelcome(SwaggerUiConfigProperties swaggerUi,
                                         SpringDocConfigProperties springDoc,
                                         ObjectProvider<SpringWebProvider> springWebProvider) {
        return new SwaggerWelcomeWebMvc(swaggerUi, springDoc, springWebProvider);
    }

    @Bean
    SwaggerConfigResource swaggerConfigResource(SwaggerWelcomeCommon swaggerWelcome) {
        return new SwaggerConfigResource(swaggerWelcome);
    }

    @Bean
    SwaggerIndexTransformer swaggerIndexTransformer(SwaggerUiConfigProperties swaggerUi,
                                                     SwaggerUiOAuthProperties swaggerUiOAuth,
                                                     SwaggerWelcomeCommon swaggerWelcome,
                                                     ObjectMapperProvider objectMapperProvider) {
        return new SwaggerIndexPageTransformer(
                swaggerUi, swaggerUiOAuth, swaggerWelcome, objectMapperProvider);
    }

    @Bean
    SwaggerResourceResolver swaggerResourceResolver(SwaggerUiConfigProperties swaggerUi) {
        return new SwaggerResourceResolver(swaggerUi);
    }

    @Bean
    SwaggerWebMvcConfigurer swaggerWebMvcConfigurer(
            SwaggerUiConfigProperties swaggerUi,
            WebProperties webProperties,
            WebMvcProperties webMvcProperties,
            SwaggerIndexTransformer swaggerIndexTransformer,
            SwaggerResourceResolver swaggerResourceResolver,
            SwaggerWelcomeCommon swaggerWelcome) {
        return new SwaggerWebMvcConfigurer(
                swaggerUi,
                webProperties,
                webMvcProperties,
                swaggerIndexTransformer,
                swaggerResourceResolver,
                swaggerWelcome);
    }

    @RestController
    static class CanonicalOpenApiController {

        private static final String CANONICAL_RESOURCE = "static/api-docs/battleship-v1.yaml";
        private static final MediaType YAML = MediaType.parseMediaType("application/yaml");

        @GetMapping("/api-docs/battleship-v1.yaml")
        ResponseEntity<Resource> canonicalContract() {
            return ResponseEntity.ok()
                    .contentType(YAML)
                    .body(new ClassPathResource(CANONICAL_RESOURCE));
        }

        @GetMapping("/api-docs")
        ResponseEntity<Void> generatedDocsAreDisabled() {
            return ResponseEntity.notFound().build();
        }
    }
}
