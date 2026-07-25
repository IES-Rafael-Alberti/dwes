package com.example.battleship.openapi;

import com.atlassian.oai.validator.OpenApiInteractionValidator;
import com.atlassian.oai.validator.mockmvc.MockMvcResponse;
import com.atlassian.oai.validator.model.Request;
import com.atlassian.oai.validator.report.JsonValidationReportFormat;
import org.springframework.test.web.servlet.ResultMatcher;

import java.net.URL;

import static com.atlassian.oai.validator.mockmvc.OpenApiValidationMatchers.openApi;

final class OpenApiValidatorSupport {

    private static final String CONTRACT_RESOURCE = "static/api-docs/battleship-v1.yaml";
    private static final OpenApiInteractionValidator VALIDATOR = createValidator();

    private OpenApiValidatorSupport() {
    }

    static OpenApiInteractionValidator createValidator() {
        URL resource = OpenApiValidatorSupport.class.getClassLoader().getResource(CONTRACT_RESOURCE);
        if (resource == null) {
            throw new IllegalStateException("Missing classpath resource: " + CONTRACT_RESOURCE);
        }
        return OpenApiInteractionValidator.createForSpecificationUrl(resource.toExternalForm()).build();
    }

    static ResultMatcher conformsToOpenApi() {
        return openApi().isValid(VALIDATOR);
    }

    static ResultMatcher responseConformsToOpenApi(String path, Request.Method method) {
        return result -> {
            var report = VALIDATOR.validateResponse(path, method, MockMvcResponse.of(result.getResponse()));
            if (report.hasErrors()) {
                throw new AssertionError(JsonValidationReportFormat.getInstance().apply(report));
            }
        };
    }
}
