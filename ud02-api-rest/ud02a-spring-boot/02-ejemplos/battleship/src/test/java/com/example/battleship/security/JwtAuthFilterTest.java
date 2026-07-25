package com.example.battleship.security;

import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.util.List;
import tools.jackson.databind.json.JsonMapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class JwtAuthFilterTest {

    @Test
    void accessToken_withoutRoles_returns401() throws Exception {
        assertInvalidRolesReturn401(null);
    }

    @Test
    void accessToken_withInvalidRoles_returns401() throws Exception {
        assertInvalidRolesReturn401(List.of(42));
    }

    private void assertInvalidRolesReturn401(Object roles) throws Exception {
        JwtService jwtService = mock(JwtService.class);
        Claims claims = mock(Claims.class);
        when(jwtService.parseClaims("token")).thenReturn(claims);
        when(jwtService.isRefreshToken(claims)).thenReturn(false);
        when(claims.get("roles")).thenReturn(roles);

        var request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer token");
        var response = new MockHttpServletResponse();

        new JwtAuthFilter(jwtService, new SecurityErrorWriter(JsonMapper.builder().build()))
                .doFilter(request, response, new MockFilterChain());

        assertEquals(401, response.getStatus());
    }
}
