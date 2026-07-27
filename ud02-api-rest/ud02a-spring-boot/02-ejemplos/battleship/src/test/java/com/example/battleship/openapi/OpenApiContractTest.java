package com.example.battleship.openapi;

import java.math.BigDecimal;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.parser.OpenAPIV3Parser;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class OpenApiContractTest {

    private static final String CONTRACT_RESOURCE = "static/api-docs/battleship-v1.yaml";
    private static final String OFFSETLESS_LOCAL_DATE_TIME_PATTERN =
            "^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}(?::\\d{2}(?:\\.\\d{1,9})?)?$";

    private static OpenAPI contract;

    @BeforeAll
    static void parseCanonicalContract() {
        URL resource = OpenApiContractTest.class.getClassLoader().getResource(CONTRACT_RESOURCE);
        assertThat(resource).as("canonical OpenAPI resource").isNotNull();

        var result = new OpenAPIV3Parser().readLocation(resource.toExternalForm(), null, null);
        assertThat(result.getMessages()).as("OpenAPI parser messages").isEmpty();
        contract = result.getOpenAPI();
        assertThat(contract).isNotNull();
    }

    @Test
    void identifiesOpenApi31AndVersionOne() {
        assertThat(contract.getOpenapi()).startsWith("3.1.");
        assertThat(contract.getInfo().getVersion()).isEqualTo("1.0.0");
    }

    @Test
    void definesExactlyTheNineStableOperations() {
        Map<String, String> expected = new LinkedHashMap<>();
        expected.put("POST /auth/register", "registerUser");
        expected.put("POST /auth/login", "loginUser");
        expected.put("POST /auth/refresh", "refreshToken");
        expected.put("POST /api/games", "createGame");
        expected.put("GET /api/games", "listGames");
        expected.put("GET /api/games/{id}", "getGame");
        expected.put("POST /api/games/{id}/ships", "placeShip");
        expected.put("POST /api/games/{id}/attacks", "attackGame");
        expected.put("DELETE /api/games/{id}", "cancelGame");

        Map<String, String> actual = new LinkedHashMap<>();
        contract.getPaths().forEach((path, item) -> item.readOperationsMap()
                .forEach((method, operation) -> actual.put(method + " " + path, operation.getOperationId())));

        assertThat(actual).containsExactlyInAnyOrderEntriesOf(expected);
        assertThat(actual.values()).doesNotHaveDuplicates();
    }

    @Test
    void definesRequestAndResponseSchemaConstraints() {
        assertRequiredProperties("AuthRequest", "username", "password");
        assertNonBlank("AuthRequest", "username");
        assertNonBlank("AuthRequest", "password");
        assertRequiredProperties("TokenRefreshRequest", "refreshToken");
        assertNonBlank("TokenRefreshRequest", "refreshToken");
        assertRequiredProperties("TokenResponse", "accessToken", "refreshToken", "expiresIn");

        assertRequiredProperties("CreateGame", "boardSize");
        assertIntegerBounds("CreateGame", "boardSize", 5, 20);

        assertRequiredProperties("PlaceShip", "shipName", "length", "startX", "startY", "isHorizontal");
        assertNonBlank("PlaceShip", "shipName");
        assertIntegerMinimum("PlaceShip", "length", 1);
        assertIntegerMinimum("PlaceShip", "startX", 0);
        assertIntegerMinimum("PlaceShip", "startY", 0);

        assertRequiredProperties("Attack", "x", "y");
        assertIntegerMinimum("Attack", "x", 0);
        assertIntegerMinimum("Attack", "y", 0);

        assertRequiredProperties("Game", "id", "boardSize", "status", "createdAt", "ships", "attacks");
        assertRequiredProperties("Ship", "id", "shipName", "length", "startX", "startY", "isHorizontal", "sunk");
        assertRequiredProperties("GameAttack", "id", "x", "y", "hit", "createdAt");
        assertRequiredProperties("Error", "error", "message", "timestamp");
    }

    @Test
    void definesResponsesAndLocationHeaders() {
        assertResponse("/auth/register", PathItem.HttpMethod.POST, "201", "text/plain");
        assertResponse("/auth/login", PathItem.HttpMethod.POST, "200", "application/json");
        assertResponse("/auth/refresh", PathItem.HttpMethod.POST, "200", "application/json");
        assertCreatedGameResponse("/api/games");
        assertResponse("/api/games", PathItem.HttpMethod.GET, "200", "application/json");
        assertResponse("/api/games/{id}", PathItem.HttpMethod.GET, "200", "application/json");
        assertCreatedGameResponse("/api/games/{id}/ships");
        assertCreatedGameResponse("/api/games/{id}/attacks");

        var cancel = operation("/api/games/{id}", PathItem.HttpMethod.DELETE);
        assertThat(cancel.getResponses()).containsKey("204");
        assertThat(cancel.getResponses().get("204").getContent()).isNull();
    }

    @Test
    void declaresApplicableErrorStatuses() {
        assertResponseStatuses("/auth/register", PathItem.HttpMethod.POST, "201", "400", "401", "429", "500");
        assertResponseStatuses("/auth/login", PathItem.HttpMethod.POST, "200", "400", "401", "429", "500");
        assertResponseStatuses("/auth/refresh", PathItem.HttpMethod.POST, "200", "400", "401", "429", "500");
        assertResponseStatuses("/api/games", PathItem.HttpMethod.POST, "201", "400", "401", "403", "429", "500");
        assertResponseStatuses("/api/games", PathItem.HttpMethod.GET, "200", "400", "401", "429", "500");
        assertResponseStatuses("/api/games/{id}", PathItem.HttpMethod.GET, "200", "400", "401", "404", "429", "500");
        assertResponseStatuses("/api/games/{id}/ships", PathItem.HttpMethod.POST,
                "201", "400", "401", "403", "404", "409", "429", "500");
        assertResponseStatuses("/api/games/{id}/attacks", PathItem.HttpMethod.POST,
                "201", "400", "401", "403", "404", "409", "429", "500");
        assertResponseStatuses("/api/games/{id}", PathItem.HttpMethod.DELETE,
                "204", "400", "401", "403", "404", "409", "429", "500");
    }

    @Test
    void everyOperationReferencesTheReusableTooManyRequestsResponse() {
        contract.getPaths().values().stream()
                .flatMap(path -> path.readOperations().stream())
                .forEach(operation -> assertThat(operation.getResponses().get("429").get$ref())
                        .isEqualTo("#/components/responses/TooManyRequests"));
    }

    @Test
    void everyPublicOperationReferencesTheReusableUnauthorizedResponse() {
        Set<Operation> publicOperations = Set.of(
                operation("/auth/register", PathItem.HttpMethod.POST),
                operation("/auth/login", PathItem.HttpMethod.POST),
                operation("/auth/refresh", PathItem.HttpMethod.POST),
                operation("/api/games", PathItem.HttpMethod.GET),
                operation("/api/games/{id}", PathItem.HttpMethod.GET));

        publicOperations.forEach(operation -> assertThat(operation.getResponses().get("401").get$ref())
                .isEqualTo("#/components/responses/Unauthorized"));
    }

    @Test
    void definesPaginationParametersAndPageField() {
        Operation list = operation("/api/games", PathItem.HttpMethod.GET);

        assertParameter(list, "page", "integer", 0, BigDecimal.ZERO);
        assertParameter(list, "size", "integer", 20, BigDecimal.ONE);
        assertParameter(list, "sort", "string", "createdAt,DESC", null);
        assertParameter(list, "status", "string", null, null);
        assertParameter(list, "minBoardSize", "integer", null, BigDecimal.ONE);
        Parameter createdAfter = parameter(list, "createdAfter");
        assertSchemaType(createdAfter.getSchema(), "string");
        assertThat(createdAfter.getSchema().getFormat()).isNull();
        assertThat(createdAfter.getSchema().getPattern()).isEqualTo(OFFSETLESS_LOCAL_DATE_TIME_PATTERN);

        Schema<?> page = schema("PageResponse");
        assertThat(page.getRequired()).containsExactlyInAnyOrder(
                "content", "page", "size", "totalElements", "totalPages", "first", "last");
        assertThat(page.getProperties()).containsKey("page").doesNotContainKey("number");
    }

    @Test
    void modelsOffsetlessLocalDateTimes() {
        assertThat(property("Game", "createdAt").getFormat()).isNull();
        assertThat(property("GameAttack", "createdAt").getFormat()).isNull();
    }

    @Test
    void requestSchemasAcceptUnknownFieldsLikeJackson() {
        assertAllowsAdditionalProperties("AuthRequest");
        assertAllowsAdditionalProperties("TokenRefreshRequest");
        assertAllowsAdditionalProperties("CreateGame");
        assertAllowsAdditionalProperties("PlaceShip");
        assertAllowsAdditionalProperties("Attack");
    }

    @Test
    void refreshUnauthorizedResponseUsesTheErrorSchema() {
        var unauthorized = operation("/auth/refresh", PathItem.HttpMethod.POST).getResponses().get("401");
        if (unauthorized.get$ref() != null) {
            unauthorized = contract.getComponents().getResponses()
                    .get(unauthorized.get$ref().substring(unauthorized.get$ref().lastIndexOf('/') + 1));
        }
        assertThat(unauthorized.getContent()).containsKey("application/json");
    }

    @Test
    void definesPublicOverridesAndRoleDocumentedBearerSecurity() {
        var bearer = contract.getComponents().getSecuritySchemes().get("bearerAuth");
        assertThat(bearer.getType().toString()).isEqualTo("http");
        assertThat(bearer.getScheme()).isEqualTo("bearer");
        assertThat(bearer.getBearerFormat()).isEqualTo("JWT");

        assertPublic("/auth/register", PathItem.HttpMethod.POST);
        assertPublic("/auth/login", PathItem.HttpMethod.POST);
        assertPublic("/auth/refresh", PathItem.HttpMethod.POST);
        assertPublic("/api/games", PathItem.HttpMethod.GET);
        assertPublic("/api/games/{id}", PathItem.HttpMethod.GET);

        assertBearerRole("/api/games", PathItem.HttpMethod.POST, "PLAYER");
        assertBearerRole("/api/games/{id}/ships", PathItem.HttpMethod.POST, "PLAYER");
        assertBearerRole("/api/games/{id}/attacks", PathItem.HttpMethod.POST, "PLAYER");
        assertBearerRole("/api/games/{id}", PathItem.HttpMethod.DELETE, "ADMIN");
    }

    private static void assertRequiredProperties(String schemaName, String... names) {
        assertThat(schema(schemaName).getRequired()).containsExactlyInAnyOrder(names);
    }

    private static void assertNonBlank(String schemaName, String propertyName) {
        Schema<?> property = property(schemaName, propertyName);
        assertSchemaType(property, "string");
        assertThat(property.getMinLength()).isEqualTo(1);
        assertThat(property.getPattern()).isEqualTo(".*\\S.*");
    }

    private static void assertIntegerBounds(String schemaName, String propertyName, int minimum, int maximum) {
        Schema<?> property = property(schemaName, propertyName);
        assertSchemaType(property, "integer");
        assertThat(property.getMinimum()).isEqualByComparingTo(BigDecimal.valueOf(minimum));
        assertThat(property.getMaximum()).isEqualByComparingTo(BigDecimal.valueOf(maximum));
    }

    private static void assertIntegerMinimum(String schemaName, String propertyName, int minimum) {
        Schema<?> property = property(schemaName, propertyName);
        assertSchemaType(property, "integer");
        assertThat(property.getMinimum()).isEqualByComparingTo(BigDecimal.valueOf(minimum));
    }

    private static void assertResponse(String path, PathItem.HttpMethod method, String status, String mediaType) {
        var response = operation(path, method).getResponses().get(status);
        assertThat(response).isNotNull();
        assertThat(response.getContent()).containsKey(mediaType);
    }

    private static void assertCreatedGameResponse(String path) {
        var response = operation(path, PathItem.HttpMethod.POST).getResponses().get("201");
        assertThat(response.getContent()).containsKey("application/json");
        assertThat(response.getHeaders()).containsKey("Location");
        var location = response.getHeaders().get("Location");
        var locationSchema = location.getSchema() != null
                ? location.getSchema()
                : contract.getComponents().getHeaders().get(location.get$ref().substring(location.get$ref().lastIndexOf('/') + 1))
                        .getSchema();
        assertThat(locationSchema.getFormat()).isEqualTo("uri-reference");
    }

    private static void assertResponseStatuses(String path, PathItem.HttpMethod method, String... statuses) {
        assertThat(operation(path, method).getResponses().keySet()).containsExactlyInAnyOrder(statuses);
    }

    private static void assertParameter(Operation operation, String name, String type,
                                        Object defaultValue, BigDecimal minimum) {
        Schema<?> schema = parameter(operation, name).getSchema();
        assertSchemaType(schema, type);
        assertThat(schema.getDefault()).isEqualTo(defaultValue);
        assertThat(schema.getMinimum()).isEqualTo(minimum);
    }

    private static Parameter parameter(Operation operation, String name) {
        return operation.getParameters().stream()
                .filter(parameter -> name.equals(parameter.getName()))
                .findFirst()
                .orElseThrow();
    }

    private static void assertPublic(String path, PathItem.HttpMethod method) {
        assertThat(operation(path, method).getSecurity()).isEmpty();
    }

    private static void assertSchemaType(Schema<?> schema, String type) {
        assertThat(schema.getTypes()).containsExactly(type);
    }

    private static void assertAllowsAdditionalProperties(String schemaName) {
        assertThat(schema(schemaName).getAdditionalProperties()).isNotEqualTo(Boolean.FALSE);
    }

    private static void assertBearerRole(String path, PathItem.HttpMethod method, String role) {
        Operation operation = operation(path, method);
        assertThat(operation.getSecurity()).hasSize(1);
        assertThat(operation.getSecurity().getFirst()).containsEntry("bearerAuth", List.of());
        assertThat(operation.getDescription()).contains(role);
    }

    private static Operation operation(String path, PathItem.HttpMethod method) {
        return contract.getPaths().get(path).readOperationsMap().get(method);
    }

    private static Schema<?> schema(String name) {
        return contract.getComponents().getSchemas().get(name);
    }

    private static Schema<?> property(String schemaName, String propertyName) {
        return (Schema<?>) schema(schemaName).getProperties().get(propertyName);
    }
}
