package com.example.battleship.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public record CreateGameDTO(
        @Min(5) @Max(20) int boardSize
) {}
