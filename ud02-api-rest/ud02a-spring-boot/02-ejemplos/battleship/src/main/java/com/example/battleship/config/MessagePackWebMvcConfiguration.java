package com.example.battleship.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

@Configuration
public class MessagePackWebMvcConfiguration implements WebMvcConfigurer {

    private final ObjectMapper objectMapper;

    public MessagePackWebMvcConfiguration(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        // Keep JSON as the default representation when Accept is absent.
        converters.add(new MessagePackHttpMessageConverter(objectMapper));
    }
}
