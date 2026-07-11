package com.example.battleship.dto;

import java.time.Instant;

public record ErrorPayload(
    String error,
    String message,
    String timestamp
) {
    public ErrorPayload(String error, String message) {
        this(error, message, Instant.now().toString());
    }
}
