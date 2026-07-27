package com.example.battleship.security;

import com.example.battleship.dto.ErrorPayload;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import tools.jackson.databind.json.JsonMapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

class RateLimitFilterTest {

    @Test
    void rejectsNonPositiveConfiguredMaximumAndReturnsCanonicalJsonAtTheLimit() throws Exception {
        var mapper = JsonMapper.builder().build();

        assertThatIllegalArgumentException()
                .isThrownBy(() -> new RateLimitFilter(new SecurityErrorWriter(mapper), 0))
                .withMessage("maxRequests must be positive");
        var filter = new RateLimitFilter(new SecurityErrorWriter(mapper), 1);
        var request = new MockHttpServletRequest();
        request.setRemoteAddr("192.0.2.1");

        filter.doFilter(request, new MockHttpServletResponse(), new MockFilterChain());
        var rejected = new MockHttpServletResponse();
        filter.doFilter(request, rejected, new MockFilterChain());

        ErrorPayload payload = mapper.readValue(rejected.getContentAsString(), ErrorPayload.class);
        assertThat(rejected.getStatus()).isEqualTo(429);
        assertThat(rejected.getContentType()).isEqualTo("application/json;charset=UTF-8");
        assertThat(payload.error()).isEqualTo("TOO_MANY_REQUESTS");
        assertThat(payload.message()).isEqualTo("Rate limit exceeded");
        assertThat(payload.timestamp()).isNotBlank();
    }
}
