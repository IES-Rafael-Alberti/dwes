package com.example.battleship.security;

import com.example.battleship.dto.ErrorPayload;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletResponse;
import tools.jackson.databind.json.JsonMapper;

import static org.assertj.core.api.Assertions.assertThat;

class SecurityErrorWriterTest {

    @Test
    void serializesQuotesBackslashesAndControlCharactersAsValidJson() throws Exception {
        var mapper = JsonMapper.builder().build();
        var response = new MockHttpServletResponse();
        String message = "quoted \"value\", slash \\ and newline\nnext";

        new SecurityErrorWriter(mapper).write(response, 401, "UNAUTHORIZED", message);

        ErrorPayload payload = mapper.readValue(response.getContentAsString(), ErrorPayload.class);
        assertThat(payload.error()).isEqualTo("UNAUTHORIZED");
        assertThat(payload.message()).isEqualTo(message);
        assertThat(payload.timestamp()).isNotBlank();
        assertThat(response.getContentType()).isEqualTo("application/json;charset=UTF-8");
    }
}
