package com.example.battleship.dto;

import jakarta.validation.constraints.*;

public record PlaceShipDTO(
        @NotBlank String shipName,
        @Min(1) int length,
        @Min(0) int startX,
        @Min(0) int startY,
        boolean isHorizontal
) {}
