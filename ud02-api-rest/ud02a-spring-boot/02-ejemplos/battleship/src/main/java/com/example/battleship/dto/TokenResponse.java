package com.example.battleship.dto;

public record TokenResponse(
    String accessToken,
    String refreshToken,
    long expiresIn
) {}
