package com.example.battleship.persistenciaavanzada.jsonb;

public record ShipData(
    String shipName,
    int length,
    int startX,
    int startY,
    boolean isHorizontal,
    boolean sunk
) {}
