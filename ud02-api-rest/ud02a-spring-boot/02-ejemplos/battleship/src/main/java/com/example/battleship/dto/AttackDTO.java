package com.example.battleship.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record AttackDTO(
        @NotNull @Min(0) Integer x,
        @NotNull @Min(0) Integer y
) {}
