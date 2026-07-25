package com.example.battleship.openapi;

import java.util.List;

import com.example.battleship.security.JwtService;
import io.swagger.v3.parser.OpenAPIV3Parser;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class OpenApiPublicationIntegrationTest {

    private static final String CANONICAL_URL = "/api-docs/battleship-v1.yaml";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtService jwtService;

    @Test
    void publishesTheCanonicalContractAnonymously() throws Exception {
        String yaml = mockMvc.perform(get(CANONICAL_URL))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("application/yaml"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        var result = new OpenAPIV3Parser().readContents(yaml, null, null);
        assertThat(result.getMessages()).isEmpty();
        assertThat(result.getOpenAPI().getOpenapi()).startsWith("3.1.");
        assertThat(result.getOpenAPI().getInfo().getVersion()).isEqualTo("1.0.0");
    }

    @Test
    void configuresSwaggerUiWithOnlyTheCanonicalExternalUrl() throws Exception {
        mockMvc.perform(get("/api-docs/swagger-config"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.url").value(CANONICAL_URL))
                .andExpect(jsonPath("$.urls").doesNotExist());
    }

    @Test
    void doesNotPublishGeneratedDocumentation() throws Exception {
        String accessToken = jwtService.generateAccessToken("docs-check", List.of("ROLE_PLAYER"));

        mockMvc.perform(get("/api-docs")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
                .andExpect(status().isNotFound());
    }
}
