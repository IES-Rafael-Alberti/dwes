package com.example.battleship.web;

import com.example.battleship.domain.User;
import com.example.battleship.dto.TokenResponse;
import com.example.battleship.repository.UserRepository;
import com.example.battleship.security.JwtService;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.security.PrivateKey;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class SecurityAuthorizationIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private PrivateKey privateKey;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void listGames_isPublic() throws Exception {
        mockMvc.perform(get("/api/games"))
                .andExpect(status().isOk());
    }

    @Test
    void getGame_isPublic() throws Exception {
        mockMvc.perform(get("/api/games/999999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void login_withWrongCredentials_returns401() throws Exception {
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"admin\",\"password\":\"wrong\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void refresh_withInvalidToken_returns401() throws Exception {
        mockMvc.perform(post("/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"refreshToken\":\"not-a-jwt\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void refresh_withExpiredToken_returns401() throws Exception {
        mockMvc.perform(post("/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"refreshToken\":\"" + expiredRefreshToken() + "\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void refreshToken_cannotBeUsedAsAccessToken() throws Exception {
        mockMvc.perform(post("/api/games")
                        .header("Authorization", "Bearer " + jwtService.generateRefreshToken("player"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"boardSize\":10}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void createGame_withoutAuthentication_returns401() throws Exception {
        mockMvc.perform(post("/api/games")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"boardSize\":10}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void createGame_withoutPlayerRole_returns403() throws Exception {
        mockMvc.perform(post("/api/games")
                        .with(user("reader").roles("USER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"boardSize\":10}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void createGame_asPlayer_returns201() throws Exception {
        mockMvc.perform(post("/api/games")
                        .header("Authorization", "Bearer " +
                                jwtService.generateAccessToken("player", java.util.List.of("ROLE_PLAYER")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"boardSize\":10}"))
                .andExpect(status().isCreated());
    }

    @Test
    void loginIssuedToken_withLegacyPlayerRole_authenticatesCreateGame() throws Exception {
        String username = "legacy-player-login";
        String password = "secure-password";
        userRepository.findByUsername(username).ifPresent(userRepository::delete);
        userRepository.save(new User(username, passwordEncoder.encode(password), Set.of("PLAYER")));

        String responseBody = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"" + username + "\",\"password\":\"" + password + "\"}"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        TokenResponse tokens = objectMapper.readValue(responseBody, TokenResponse.class);
        assertThat(jwtService.parseClaims(tokens.accessToken()).get("roles", List.class))
                .containsExactly("ROLE_PLAYER");

        mockMvc.perform(post("/api/games")
                        .header("Authorization", "Bearer " + tokens.accessToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"boardSize\":10}"))
                .andExpect(status().isCreated());
    }

    @Test
    void placeShip_asPlayerBearerToken_reachesEndpoint() throws Exception {
        mockMvc.perform(post("/api/games/999999/ships")
                        .header("Authorization", "Bearer " +
                                jwtService.generateAccessToken("player", java.util.List.of("ROLE_PLAYER")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"shipName\":\"Destroyer\",\"startX\":1,\"startY\":1,\"length\":2,\"isHorizontal\":true}"))
                .andExpect(status().isNotFound());
    }

    @Test
    void placeShip_withoutPlayerRole_returns403() throws Exception {
        mockMvc.perform(post("/api/games/999999/ships")
                        .with(user("reader").roles("USER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"shipName\":\"Destroyer\",\"startX\":1,\"startY\":1,\"length\":2,\"isHorizontal\":true}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void attack_withoutPlayerRole_returns403() throws Exception {
        mockMvc.perform(post("/api/games/999999/attacks")
                        .with(user("reader").roles("USER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"x\":1,\"y\":1}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void attack_asPlayerBearerToken_reachesEndpoint() throws Exception {
        mockMvc.perform(post("/api/games/999999/attacks")
                        .header("Authorization", "Bearer " +
                                jwtService.generateAccessToken("player", java.util.List.of("ROLE_PLAYER")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"x\":1,\"y\":1}"))
                .andExpect(status().isNotFound());
    }

    @ParameterizedTest
    @ValueSource(strings = {"/api/games/999999/ships", "/api/games/999999/attacks"})
    void playerMutations_withoutAuthentication_return401(String path) throws Exception {
        mockMvc.perform(post(path)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void cancelGame_withoutAuthentication_returns401() throws Exception {
        mockMvc.perform(delete("/api/games/999999"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void cancelGame_asPlayer_returns403() throws Exception {
        mockMvc.perform(delete("/api/games/999999")
                        .with(user("player").roles("PLAYER")))
                .andExpect(status().isForbidden());
    }

    @Test
    void cancelGame_asAdmin_reachesTheEndpoint() throws Exception {
        mockMvc.perform(delete("/api/games/999999")
                        .header("Authorization", "Bearer " +
                                jwtService.generateAccessToken("admin", java.util.List.of("ROLE_ADMIN"))))
                .andExpect(status().isNotFound());
    }

    @Test
    void apiDocs_requireAuthentication() throws Exception {
        mockMvc.perform(get("/api-docs"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void actuatorHealth_requiresAuthentication() throws Exception {
        mockMvc.perform(get("/actuator/health"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void authCorsPreflight_acceptsConfiguredFrontend() throws Exception {
        mockMvc.perform(request(org.springframework.http.HttpMethod.OPTIONS, "/auth/login")
                        .header("Origin", "http://localhost:5173")
                        .header("Access-Control-Request-Method", "POST"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:5173"));
    }

    private String expiredRefreshToken() throws Exception {
        Instant past = Instant.now().minusSeconds(120);
        return Jwts.builder()
                .subject("player")
                .issuer("battleship-api")
                .audience().add("battleship-client").and()
                .claim("type", "refresh")
                .issuedAt(Date.from(past.minusSeconds(60)))
                .expiration(Date.from(past))
                .signWith(privateKey, Jwts.SIG.RS256)
                .compact();
    }
}
