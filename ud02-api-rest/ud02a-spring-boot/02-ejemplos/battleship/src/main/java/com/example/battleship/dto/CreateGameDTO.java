package com.example.battleship.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record CreateGameDTO(
        @NotNull @Min(5) @Max(20) Integer boardSize
) {}
