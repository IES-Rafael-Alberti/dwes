package com.example.battleship.openapi;

import com.atlassian.oai.validator.model.Request;
import com.example.battleship.domain.User;
import com.example.battleship.domain.Game;
import com.example.battleship.domain.GameStatus;
import com.example.battleship.dto.TokenResponse;
import com.example.battleship.repository.GameRepository;
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
import java.util.List;
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

@SpringBootTest(properties = "app.rate-limit.max-requests=1000")
@AutoConfigureMockMvc
class OpenApiConformanceIntegrationTest {

    private static final AtomicInteger UNIQUE = new AtomicInteger();
    private static final String PASSWORD = "contract-password";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GameRepository gameRepository;

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

        for (String validCreatedAfter : List.of(
                "2026-07-25T20:00",
                "2026-07-25T20:00:00",
                "2026-07-25T20:00:00.123456789")) {
            mockMvc.perform(get("/api/games").queryParam("createdAfter", validCreatedAfter))
                    .andExpect(status().isOk())
                    .andExpect(responseConformsToOpenApi("/api/games", Request.Method.GET));
        }

        for (String invalidCreatedAfter : List.of(
                "2026-07-25T20:00:00Z",
                "2026-07-25T20:00:00+02:00",
                "not-a-timestamp",
                "2026-13-40T20:00:00")) {
            mockMvc.perform(get("/api/games").queryParam("createdAfter", invalidCreatedAfter))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.error").value(org.hamcrest.Matchers.anyOf(
                            org.hamcrest.Matchers.is("VALIDATION_ERROR"), org.hamcrest.Matchers.is("BAD_REQUEST"))))
                    .andExpect(responseConformsToOpenApi("/api/games", Request.Method.GET));
        }
    }

    @Test
    void listOperationUsesCanonicalDefaultsAndCreatedAtDescendingOrder() throws Exception {
        LocalDateTime firstCreatedAt = LocalDateTime.of(2099, 1, 1, 0, 0);
        for (int day = 0; day < 21; day++) {
            gameRepository.save(Game.builder()
                    .boardSize(5)
                    .status(GameStatus.PENDING)
                    .createdAt(firstCreatedAt.plusDays(day))
                    .build());
        }

        mockMvc.perform(get("/api/games"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(20))
                .andExpect(jsonPath("$.totalElements").value(org.hamcrest.Matchers.greaterThanOrEqualTo(21)))
                .andExpect(jsonPath("$.content.length()").value(20))
                .andExpect(jsonPath("$.content[0].createdAt").value("2099-01-21T00:00:00"))
                .andExpect(jsonPath("$.content[19].createdAt").value("2099-01-02T00:00:00"))
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
    @MethodSource("dtoBoundaryViolations")
    void dtoBoundaryViolationsReturnCanonicalDocumented400(
            String name, RequestFactory requestFactory, String contractPath) throws Exception {
        String accessToken = jwtService.generateAccessToken("boundary-player", java.util.List.of("ROLE_PLAYER"));
        String gameLocation = createPendingGame(20);

        mockMvc.perform(requestFactory.build(gameLocation)
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.message").isString())
                .andExpect(jsonPath("$.timestamp").isString())
                .andExpect(responseConformsToOpenApi(contractPath, Request.Method.POST));
    }

    @Test
    void validCreateGameBoardSizeBoundariesConform() throws Exception {
        String accessToken = loginPlayer();

        for (int boardSize : new int[]{5, 20}) {
            mockMvc.perform(post("/api/games")
                            .header("Authorization", "Bearer " + accessToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"boardSize\":" + boardSize + "}"))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.boardSize").value(boardSize))
                    .andExpect(conformsToOpenApi());
        }
    }

    @Test
    void validAuthAndRefreshRequestsConform() throws Exception {
        String username = uniqueUsername();
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(authJson(username, PASSWORD)))
                .andExpect(status().isCreated())
                .andExpect(conformsToOpenApi());

        String loginBody = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(authJson(username, PASSWORD)))
                .andExpect(status().isOk())
                .andExpect(conformsToOpenApi())
                .andReturn().getResponse().getContentAsString();
        TokenResponse tokenResponse = objectMapper.readValue(loginBody, TokenResponse.class);

        mockMvc.perform(post("/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"refreshToken\":\"" + tokenResponse.refreshToken() + "\"}"))
                .andExpect(status().isOk())
                .andExpect(conformsToOpenApi());
    }

    @Test
    void validShipAndAttackCoordinateMinimumsConform() throws Exception {
        String accessToken = loginPlayer();
        String gameLocation = createGame(accessToken, "{\"boardSize\":5}");

        mockMvc.perform(post(gameLocation + "/ships")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"shipName\":\"Scout\",\"length\":1,\"startX\":0,\"startY\":0,\"isHorizontal\":true}"))
                .andExpect(status().isCreated())
                .andExpect(conformsToOpenApi());

        mockMvc.perform(post(gameLocation + "/attacks")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"x\":0,\"y\":0}"))
                .andExpect(status().isCreated())
                .andExpect(conformsToOpenApi());
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

    private static Stream<Arguments> dtoBoundaryViolations() {
        return Stream.of(
                Arguments.of("register missing username", (RequestFactory) location -> post("/auth/register").content("{\"password\":\"valid-password\"}"), "/auth/register"),
                Arguments.of("register blank username", (RequestFactory) location -> post("/auth/register").content("{\"username\":\" \",\"password\":\"valid-password\"}"), "/auth/register"),
                Arguments.of("register missing password", (RequestFactory) location -> post("/auth/register").content("{\"username\":\"valid-user\"}"), "/auth/register"),
                Arguments.of("register blank password", (RequestFactory) location -> post("/auth/register").content("{\"username\":\"valid-user\",\"password\":\" \"}"), "/auth/register"),
                Arguments.of("login missing username", (RequestFactory) location -> post("/auth/login").content("{\"password\":\"valid-password\"}"), "/auth/login"),
                Arguments.of("login blank username", (RequestFactory) location -> post("/auth/login").content("{\"username\":\" \",\"password\":\"valid-password\"}"), "/auth/login"),
                Arguments.of("login missing password", (RequestFactory) location -> post("/auth/login").content("{\"username\":\"valid-user\"}"), "/auth/login"),
                Arguments.of("login blank password", (RequestFactory) location -> post("/auth/login").content("{\"username\":\"valid-user\",\"password\":\" \"}"), "/auth/login"),
                Arguments.of("refresh missing token", (RequestFactory) location -> post("/auth/refresh").content("{}"), "/auth/refresh"),
                Arguments.of("refresh blank token", (RequestFactory) location -> post("/auth/refresh").content("{\"refreshToken\":\" \"}"), "/auth/refresh"),
                Arguments.of("create missing board size", (RequestFactory) location -> post("/api/games").content("{}"), "/api/games"),
                Arguments.of("create board size below minimum", (RequestFactory) location -> post("/api/games").content("{\"boardSize\":4}"), "/api/games"),
                Arguments.of("create board size above maximum", (RequestFactory) location -> post("/api/games").content("{\"boardSize\":21}"), "/api/games"),
                Arguments.of("ship missing name", (RequestFactory) location -> post(location + "/ships").content("{\"length\":1,\"startX\":0,\"startY\":0,\"isHorizontal\":true}"), "/api/games/{id}/ships"),
                Arguments.of("ship blank name", (RequestFactory) location -> post(location + "/ships").content("{\"shipName\":\" \",\"length\":1,\"startX\":0,\"startY\":0,\"isHorizontal\":true}"), "/api/games/{id}/ships"),
                Arguments.of("ship missing length", (RequestFactory) location -> post(location + "/ships").content("{\"shipName\":\"Scout\",\"startX\":0,\"startY\":0,\"isHorizontal\":true}"), "/api/games/{id}/ships"),
                Arguments.of("ship length below minimum", (RequestFactory) location -> post(location + "/ships").content("{\"shipName\":\"Scout\",\"length\":0,\"startX\":0,\"startY\":0,\"isHorizontal\":true}"), "/api/games/{id}/ships"),
                Arguments.of("ship missing startX", (RequestFactory) location -> post(location + "/ships").content("{\"shipName\":\"Scout\",\"length\":1,\"startY\":0,\"isHorizontal\":true}"), "/api/games/{id}/ships"),
                Arguments.of("ship startX below minimum", (RequestFactory) location -> post(location + "/ships").content("{\"shipName\":\"Scout\",\"length\":1,\"startX\":-1,\"startY\":0,\"isHorizontal\":true}"), "/api/games/{id}/ships"),
                Arguments.of("ship missing startY", (RequestFactory) location -> post(location + "/ships").content("{\"shipName\":\"Scout\",\"length\":1,\"startX\":0,\"isHorizontal\":true}"), "/api/games/{id}/ships"),
                Arguments.of("ship startY below minimum", (RequestFactory) location -> post(location + "/ships").content("{\"shipName\":\"Scout\",\"length\":1,\"startX\":0,\"startY\":-1,\"isHorizontal\":true}"), "/api/games/{id}/ships"),
                Arguments.of("ship missing orientation", (RequestFactory) location -> post(location + "/ships").content("{\"shipName\":\"Scout\",\"length\":1,\"startX\":0,\"startY\":0}"), "/api/games/{id}/ships"),
                Arguments.of("attack missing x", (RequestFactory) location -> post(location + "/attacks").content("{\"y\":0}"), "/api/games/{id}/attacks"),
                Arguments.of("attack x below minimum", (RequestFactory) location -> post(location + "/attacks").content("{\"x\":-1,\"y\":0}"), "/api/games/{id}/attacks"),
                Arguments.of("attack missing y", (RequestFactory) location -> post(location + "/attacks").content("{\"x\":0}"), "/api/games/{id}/attacks"),
                Arguments.of("attack y below minimum", (RequestFactory) location -> post(location + "/attacks").content("{\"x\":0,\"y\":-1}"), "/api/games/{id}/attacks")
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

    private String createPendingGame(int boardSize) {
        Game game = gameRepository.save(Game.builder()
                .boardSize(boardSize)
                .status(GameStatus.PENDING)
                .build());
        return "/api/games/" + game.getId();
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
