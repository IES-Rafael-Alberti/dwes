package com.example.battleship.openapi;

import java.math.BigDecimal;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.atlassian.oai.validator.report.MessageResolver;
import com.atlassian.oai.validator.schema.SchemaValidator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.examples.Example;
import io.swagger.v3.oas.models.headers.Header;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.parameters.RequestBody;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.parser.OpenAPIV3Parser;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class OpenApiContractTest {

    private static final String CONTRACT_RESOURCE = "static/api-docs/battleship-v1.yaml";
    private static final String OFFSETLESS_LOCAL_DATE_TIME_PATTERN =
            "^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}(?::\\d{2}(?:\\.\\d{1,9})?)?$";

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static OpenAPI contract;
    private static SchemaValidator schemaValidator;

    @BeforeAll
    static void parseCanonicalContract() {
        URL resource = OpenApiContractTest.class.getClassLoader().getResource(CONTRACT_RESOURCE);
        assertThat(resource).as("canonical OpenAPI resource").isNotNull();

        var result = new OpenAPIV3Parser().readLocation(resource.toExternalForm(), null, null);
        assertThat(result.getMessages()).as("OpenAPI parser messages").isEmpty();
        contract = result.getOpenAPI();
        assertThat(contract).isNotNull();
        schemaValidator = new SchemaValidator(contract, new MessageResolver());
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
    void providesRepresentativeExamplesForRequestsAndResponses() {
        List.of("AuthRequest", "TokenRefreshRequest", "CreateGame", "PlaceShip", "Attack")
                .forEach(name -> assertThat(schema(name).getExample()).as(name + " schema example").isNotNull());
        List.of("TokenResponse", "Game", "Ship", "GameAttack", "PageResponse", "Error")
                .forEach(name -> assertThat(schema(name).getExample()).as(name + " schema example").isNotNull());

        assertRequestExample("/auth/register", PathItem.HttpMethod.POST);
        assertRequestExample("/auth/login", PathItem.HttpMethod.POST);
        assertRequestExample("/auth/refresh", PathItem.HttpMethod.POST);
        assertRequestExample("/api/games", PathItem.HttpMethod.POST);
        assertRequestExample("/api/games/{id}/ships", PathItem.HttpMethod.POST);
        assertRequestExample("/api/games/{id}/attacks", PathItem.HttpMethod.POST);

        assertResponseExample("/auth/login", PathItem.HttpMethod.POST, "200");
        assertResponseExample("/auth/refresh", PathItem.HttpMethod.POST, "200");
        assertResponseExample("/api/games", PathItem.HttpMethod.POST, "201");
        assertResponseExample("/api/games", PathItem.HttpMethod.GET, "200");
        assertResponseExample("/api/games/{id}", PathItem.HttpMethod.GET, "200");
        assertResponseExample("/api/games/{id}/ships", PathItem.HttpMethod.POST, "201");
        assertResponseExample("/api/games/{id}/attacks", PathItem.HttpMethod.POST, "201");

        contract.getComponents().getResponses().forEach((name, response) ->
                assertThat(response.getContent().get("application/json").getExamples())
                        .as(name + " error example").isNotEmpty());
        Header location = contract.getComponents().getHeaders().get("Location");
        assertThat(location.getExample())
                .isEqualTo("/api/games/42");
        validateHeaderExamples("component header Location", location);
    }

    @Test
    void createGameResponseExampleReflectsTheCreatedGameState() {
        Example example = contract.getComponents().getExamples().get("CreatedGame");
        JsonNode value = OBJECT_MAPPER.valueToTree(example.getValue());

        assertThat(value.path("boardSize").asInt()).isEqualTo(10);
        assertThat(value.path("status").asText()).isEqualTo("PENDING");
        assertThat(value.path("createdAt").asText()).matches(OFFSETLESS_LOCAL_DATE_TIME_PATTERN);
        assertThat(value.path("ships").isEmpty()).isTrue();
        assertThat(value.path("attacks").isEmpty()).isTrue();
    }

    @Test
    void validatesEveryDocumentedExampleAgainstItsResolvedSchema() {
        contract.getComponents().getSchemas().forEach((name, schema) ->
                validateExample("component schema " + name, schema.getExample(), schema));
        contract.getComponents().getParameters().forEach((name, parameter) -> {
            Parameter resolved = resolveParameter(parameter);
            validateParameterExamples("component parameter " + name, resolved);
        });
        contract.getComponents().getHeaders().forEach((name, header) ->
                validateHeaderExamples("component header " + name, header));
        contract.getComponents().getResponses().forEach((name, response) ->
                validateResponseExamples("component response " + name, resolveResponse(response)));

        contract.getPaths().forEach((path, pathItem) -> {
            if (pathItem.getParameters() != null) {
                pathItem.getParameters().forEach(parameter -> {
                    Parameter resolved = resolveParameter(parameter);
                    validateParameterExamples(path + " path parameter " + resolved.getName(), resolved);
                });
            }
            pathItem.readOperationsMap().forEach((method, operation) -> {
                String operationName = method + " " + path;
                if (operation.getParameters() != null) {
                    operation.getParameters().forEach(parameter -> {
                        Parameter resolved = resolveParameter(parameter);
                        validateParameterExamples(operationName + " parameter " + resolved.getName(), resolved);
                    });
                }
                if (operation.getRequestBody() != null) {
                    validateContentExamples(operationName + " request", resolveRequestBody(operation.getRequestBody()).getContent());
                }
                operation.getResponses().forEach((status, response) ->
                        validateResponseExamples(operationName + " " + status, resolveResponse(response)));
            });
        });
    }

    @Test
    void validatesSchemaLevelPaginationParameterExamples() {
        Operation list = operation("/api/games", PathItem.HttpMethod.GET);

        List.of("page", "size", "sort", "status", "minBoardSize", "createdAfter").forEach(name -> {
            Parameter parameter = parameter(list, name);
            assertThat(parameter.getSchema().getExample()).as(name + " schema example").isNotNull();
            validateExample("GET /api/games parameter " + name + " schema example",
                    parameter.getSchema().getExample(), parameter.getSchema());
        });
    }

    @Test
    void validatesRegisterTextPlainResponseExampleAgainstItsSchema() {
        ApiResponse response = resolveResponse(operation("/auth/register", PathItem.HttpMethod.POST)
                .getResponses().get("201"));
        var mediaType = response.getContent().get("text/plain");

        validateExample("POST /auth/register 201 text/plain response", mediaType.getExample(), mediaType.getSchema());
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

    private static void assertRequestExample(String path, PathItem.HttpMethod method) {
        assertThat(operation(path, method).getRequestBody().getContent().get("application/json").getExamples())
                .as(method + " " + path + " request example").isNotEmpty();
    }

    private static void assertResponseExample(String path, PathItem.HttpMethod method, String status) {
        assertThat(operation(path, method).getResponses().get(status).getContent().get("application/json").getExamples())
                .as(method + " " + path + " " + status + " response example").isNotEmpty();
    }

    private static void validateResponseExamples(String location, ApiResponse response) {
        validateContentExamples(location + " response", response.getContent());
    }

    private static void validateParameterExamples(String location, Parameter parameter) {
        Schema<?> schema = parameter.getSchema();
        validateExample(location, parameter.getExample(), schema);
        if (schema != null) {
            validateExample(location + " schema example", schema.getExample(), schema);
        }
    }

    private static void validateHeaderExamples(String location, Header header) {
        validateExample(location, header.getExample(), header.getSchema());
    }

    private static void validateContentExamples(String location, io.swagger.v3.oas.models.media.Content content) {
        if (content == null) {
            return;
        }
        content.forEach((mediaTypeName, mediaType) -> {
            validateExample(location + " " + mediaTypeName, mediaType.getExample(), mediaType.getSchema());
            if (mediaType.getExamples() != null) {
                mediaType.getExamples().forEach((name, example) ->
                        validateExample(location + " " + mediaTypeName + " example " + name,
                                resolveExample(example).getValue(), mediaType.getSchema()));
            }
        });
    }

    private static void validateExample(String location, Object example, Schema<?> schema) {
        if (example == null || schema == null) {
            return;
        }
        try {
            String json = example instanceof String string ? string : OBJECT_MAPPER.writeValueAsString(example);
            var report = schemaValidator.validate(json, schema, location);
            assertThat(report.hasErrors())
                    .as(location + " validates against its schema: " + report.getMessages())
                    .isFalse();
        } catch (Exception exception) {
            throw new AssertionError("Could not validate " + location, exception);
        }
    }

    private static Example resolveExample(Example example) {
        return example.get$ref() == null ? example
                : contract.getComponents().getExamples().get(componentName(example.get$ref()));
    }

    private static Parameter resolveParameter(Parameter parameter) {
        return parameter.get$ref() == null ? parameter
                : contract.getComponents().getParameters().get(componentName(parameter.get$ref()));
    }

    private static RequestBody resolveRequestBody(RequestBody requestBody) {
        return requestBody.get$ref() == null ? requestBody
                : contract.getComponents().getRequestBodies().get(componentName(requestBody.get$ref()));
    }

    private static ApiResponse resolveResponse(ApiResponse response) {
        return response.get$ref() == null ? response
                : contract.getComponents().getResponses().get(componentName(response.get$ref()));
    }

    private static String componentName(String reference) {
        return reference.substring(reference.lastIndexOf('/') + 1);
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
