package com.example.battleship.openapi;

import com.atlassian.oai.validator.model.Request;
import com.example.battleship.domain.User;
import com.example.battleship.dto.TokenResponse;
import com.example.battleship.repository.UserRepository;
import com.example.battleship.security.JwtService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import static com.example.battleship.openapi.OpenApiValidatorSupport.conformsToOpenApi;
import static com.example.battleship.openapi.OpenApiValidatorSupport.responseConformsToOpenApi;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class OpenApiConformanceIntegrationTest {

    private static final AtomicInteger UNIQUE = new AtomicInteger();
    private static final String PASSWORD = "contract-password";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void registerOperationConforms() throws Exception {
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(authJson(uniqueUsername(), PASSWORD)))
                .andExpect(status().isCreated())
                .andExpect(content().string("User registered"))
                .andExpect(conformsToOpenApi());
    }

    @Test
    void loginOperationConforms() throws Exception {
        String username = playerUsername();

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(authJson(username, PASSWORD)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").isString())
                .andExpect(jsonPath("$.refreshToken").isString())
                .andExpect(jsonPath("$.expiresIn").value(900000))
                .andExpect(conformsToOpenApi());
    }

    @Test
    void refreshOperationConforms() throws Exception {
        String username = playerUsername();

        mockMvc.perform(post("/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"refreshToken\":\"" + jwtService.generateRefreshToken(username) + "\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").isString())
                .andExpect(conformsToOpenApi());
    }

    @Test
    void createOperationConformsWithLoginIssuedTokenAndLocation() throws Exception {
        String accessToken = loginPlayer();

        mockMvc.perform(post("/api/games")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"boardSize\":10}"))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", org.hamcrest.Matchers.matchesPattern("/api/games/\\d+")))
                .andExpect(jsonPath("$.boardSize").value(10))
                .andExpect(conformsToOpenApi());
    }

    @Test
    void listOperationConformsWithPaginationFiltersAndOffsetlessDateTime() throws Exception {
        createGame(loginPlayer(), "{\"boardSize\":12}");
        String createdAfter = LocalDateTime.now().minusDays(1).withNano(0).toString();

        mockMvc.perform(get("/api/games")
                        .queryParam("page", "0")
                        .queryParam("size", "20")
                        .queryParam("sort", "createdAt,DESC")
                        .queryParam("status", "PENDING")
                        .queryParam("minBoardSize", "12")
                        .queryParam("createdAfter", createdAfter))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(20))
                .andExpect(jsonPath("$.content[0].createdAt").isString())
                .andExpect(conformsToOpenApi());
    }

    @Test
    void getOperationConforms() throws Exception {
        String location = createGame(loginPlayer(), "{\"boardSize\":10}");

        mockMvc.perform(get(location))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andExpect(conformsToOpenApi());
    }

    @Test
    void placeShipOperationConformsWithLocation() throws Exception {
        String accessToken = loginPlayer();
        String location = createGame(accessToken, "{\"boardSize\":10}");

        mockMvc.perform(post(location + "/ships")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(shipJson()))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", location))
                .andExpect(jsonPath("$.ships[0].shipName").value("Destroyer"))
                .andExpect(conformsToOpenApi());
    }

    @Test
    void attackOperationConformsWithLocation() throws Exception {
        String accessToken = loginPlayer();
        String location = createGame(accessToken, "{\"boardSize\":10}");
        placeShip(accessToken, location);

        mockMvc.perform(post(location + "/attacks")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"x\":0,\"y\":0}"))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", location))
                .andExpect(jsonPath("$.attacks[0].hit").value(true))
                .andExpect(conformsToOpenApi());
    }

    @Test
    void cancelOperationConformsWithEmptyBody() throws Exception {
        String location = createGame(loginPlayer(), "{\"boardSize\":10}");
        String adminToken = jwtService.generateAccessToken("contract-admin", java.util.List.of("ROLE_ADMIN"));

        mockMvc.perform(delete(location).header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""))
                .andExpect(conformsToOpenApi());
    }

    @Test
    void validUnknownRequestPropertyIsAcceptedAndConforms() throws Exception {
        mockMvc.perform(post("/api/games")
                        .header("Authorization", "Bearer " + loginPlayer())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"boardSize\":10,\"teachingNote\":\"accepted by Jackson\"}"))
                .andExpect(status().isCreated())
                .andExpect(conformsToOpenApi());
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("missingPrimitiveRequests")
    void missingPrimitiveRequestFieldsReturnDocumented400(
            String name, RequestFactory requestFactory, String contractPath) throws Exception {
        String accessToken = loginPlayer();
        String gameLocation = createGame(accessToken, "{\"boardSize\":10}");

        mockMvc.perform(requestFactory.build(gameLocation)
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("VALIDATION_ERROR"))
                .andExpect(responseConformsToOpenApi(contractPath, Request.Method.POST));
    }

    @Test
    void minimumBoardSizeViolationReturnsDocumented400() throws Exception {
        mockMvc.perform(get("/api/games").queryParam("minBoardSize", "0"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("VALIDATION_ERROR"))
                .andExpect(responseConformsToOpenApi("/api/games", Request.Method.GET));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("documentedBoundViolations")
    void documentedPathAndPageBoundsReturnCanonical400(
            String name, MockHttpServletRequestBuilder request, String contractPath) throws Exception {
        mockMvc.perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.message").isString())
                .andExpect(jsonPath("$.timestamp").isString())
                .andExpect(responseConformsToOpenApi(contractPath, Request.Method.GET));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("malformedClientInputRequests")
    void malformedClientInputsReturnCanonicalDocumented400(
            String name, RequestFactory requestFactory, String contractPath, Request.Method method) throws Exception {
        String accessToken = loginPlayer();
        String gameLocation = createGame(accessToken, "{\"boardSize\":10}");

        mockMvc.perform(requestFactory.build(gameLocation)
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").isString())
                .andExpect(jsonPath("$.timestamp").isString())
                .andExpect(responseConformsToOpenApi(contractPath, method));
    }

    @Test
    void representativeValidRequestConflictConformsAs400() throws Exception {
        String username = uniqueUsername();
        String body = authJson(username, PASSWORD);
        mockMvc.perform(post("/auth/register").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/auth/register").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("BAD_REQUEST"))
                .andExpect(conformsToOpenApi());
    }

    @Test
    void missingProtectedAuthenticationReturnsConforming401() throws Exception {
        mockMvc.perform(post("/api/games")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"boardSize\":10}"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("UNAUTHORIZED"))
                .andExpect(responseConformsToOpenApi("/api/games", Request.Method.POST));
    }

    @Test
    void malformedBearerTokenReturnsConforming401() throws Exception {
        mockMvc.perform(post("/api/games")
                        .header("Authorization", "Bearer invalid-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"boardSize\":10}"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("UNAUTHORIZED"))
                .andExpect(conformsToOpenApi());
    }

    @Test
    void malformedBearerOnPublicOperationReturnsConforming401() throws Exception {
        mockMvc.perform(get("/api/games").header("Authorization", "Bearer invalid-token"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("UNAUTHORIZED"))
                .andExpect(conformsToOpenApi());
    }

    @Test
    void invalidRefreshTokenReturnsConformingJson401() throws Exception {
        mockMvc.perform(post("/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"refreshToken\":\"not-a-jwt\"}"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("UNAUTHORIZED"))
                .andExpect(conformsToOpenApi());
    }

    @Test
    void accessTokenAtRefreshEndpointReturnsConformingJson401() throws Exception {
        String accessToken = jwtService.generateAccessToken("player", java.util.List.of("ROLE_PLAYER"));

        mockMvc.perform(post("/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"refreshToken\":\"" + accessToken + "\"}"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("UNAUTHORIZED"))
                .andExpect(conformsToOpenApi());
    }

    @Test
    void missingGameReturnsConforming404() throws Exception {
        mockMvc.perform(get("/api/games/999999999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("NOT_FOUND"))
                .andExpect(conformsToOpenApi());
    }

    @Test
    void attackPendingGameReturnsConforming409() throws Exception {
        String accessToken = loginPlayer();
        String location = createGame(accessToken, "{\"boardSize\":10}");

        mockMvc.perform(post(location + "/attacks")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"x\":1,\"y\":1}"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("CONFLICT"))
                .andExpect(conformsToOpenApi());
    }

    @Test
    void insufficientRoleReturnsConforming403() throws Exception {
        mockMvc.perform(post("/api/games")
                        .header("Authorization", "Bearer "
                                + jwtService.generateAccessToken("reader", java.util.List.of("ROLE_USER")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"boardSize\":10}"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error").value("FORBIDDEN"))
                .andExpect(conformsToOpenApi());
    }

    private static Stream<Arguments> missingPrimitiveRequests() {
        return Stream.of(
                Arguments.of("create boardSize", (RequestFactory) location -> post("/api/games").content("{}"),
                        "/api/games"),
                Arguments.of("place ship isHorizontal", (RequestFactory) location -> post(location + "/ships")
                        .content("{\"shipName\":\"Destroyer\",\"length\":2,\"startX\":0,\"startY\":0}"),
                        "/api/games/{id}/ships"),
                Arguments.of("attack x", (RequestFactory) location -> post(location + "/attacks").content("{\"y\":0}"),
                        "/api/games/{id}/attacks")
        );
    }

    private static Stream<Arguments> malformedClientInputRequests() {
        return Stream.of(
                Arguments.of("malformed JSON", (RequestFactory) location -> post("/api/games").content("{\"boardSize\":"),
                        "/api/games", Request.Method.POST),
                Arguments.of("wrong scalar type", (RequestFactory) location -> post("/api/games")
                                .content("{\"boardSize\":\"large\"}"),
                        "/api/games", Request.Method.POST),
                Arguments.of("missing body", (RequestFactory) location -> post("/api/games"),
                        "/api/games", Request.Method.POST),
                Arguments.of("query conversion", (RequestFactory) location -> get("/api/games")
                                .queryParam("minBoardSize", "small"),
                        "/api/games", Request.Method.GET),
                Arguments.of("path conversion", (RequestFactory) location -> get("/api/games/not-a-number"),
                        "/api/games/{id}", Request.Method.GET)
        );
    }

    private static Stream<Arguments> documentedBoundViolations() {
        return Stream.of(
                Arguments.of("game id minimum", get("/api/games/0"), "/api/games/{id}"),
                Arguments.of("negative page", get("/api/games").queryParam("page", "-1"), "/api/games"),
                Arguments.of("zero page size", get("/api/games").queryParam("size", "0"), "/api/games")
        );
    }

    private String playerUsername() {
        String username = uniqueUsername();
        userRepository.save(new User(username, passwordEncoder.encode(PASSWORD), Set.of("ROLE_PLAYER")));
        return username;
    }

    private String loginPlayer() throws Exception {
        String username = playerUsername();
        String body = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(authJson(username, PASSWORD)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readValue(body, TokenResponse.class).accessToken();
    }

    private String createGame(String accessToken, String body) throws Exception {
        String location = mockMvc.perform(post("/api/games")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getHeader("Location");
        assertThat(location).isNotBlank();
        return location;
    }

    private void placeShip(String accessToken, String location) throws Exception {
        mockMvc.perform(post(location + "/ships")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(shipJson()))
                .andExpect(status().isCreated());
    }

    private static String authJson(String username, String password) {
        return "{\"username\":\"" + username + "\",\"password\":\"" + password + "\"}";
    }

    private static String shipJson() {
        return "{\"shipName\":\"Destroyer\",\"length\":2,\"startX\":0,\"startY\":0,\"isHorizontal\":true}";
    }

    private static String uniqueUsername() {
        return "contract-user-" + UNIQUE.incrementAndGet();
    }

    @FunctionalInterface
    private interface RequestFactory {
        MockHttpServletRequestBuilder build(String gameLocation);
    }
}
