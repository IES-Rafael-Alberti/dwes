package com.example.battleship.persistenciaavanzada.jsonb;

import java.time.LocalDateTime;

public record AttackData(
    int x,
    int y,
    boolean hit,
    LocalDateTime createdAt
) {}
