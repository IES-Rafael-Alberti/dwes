package com.example.battleship.openapi;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatCode;

class OpenApiValidatorSupportTest {

    @Test
    void createsValidatorWhenWorkingDirectoryDoesNotContainTheProject() {
        String original = System.getProperty("user.dir");
        try {
            System.setProperty("user.dir", System.getProperty("java.io.tmpdir"));
            assertThatCode(OpenApiValidatorSupport::createValidator).doesNotThrowAnyException();
        } finally {
            System.setProperty("user.dir", original);
        }
    }
}
