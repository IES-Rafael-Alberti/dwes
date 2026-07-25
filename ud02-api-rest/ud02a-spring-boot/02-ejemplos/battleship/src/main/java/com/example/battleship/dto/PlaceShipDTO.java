package com.example.battleship.dto;

import jakarta.validation.constraints.*;

public record PlaceShipDTO(
        @NotBlank String shipName,
        @NotNull @Min(1) Integer length,
        @NotNull @Min(0) Integer startX,
        @NotNull @Min(0) Integer startY,
        @NotNull Boolean isHorizontal
) {}
