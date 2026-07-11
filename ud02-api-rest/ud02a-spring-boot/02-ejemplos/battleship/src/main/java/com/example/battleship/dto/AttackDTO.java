package com.example.battleship.dto;

import jakarta.validation.constraints.Min;

public record AttackDTO(
        @Min(0) int x,
        @Min(0) int y
) {}
