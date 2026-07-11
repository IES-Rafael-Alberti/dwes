package com.example.battleship.persistenciaavanzada.jsonb;

import java.time.LocalDateTime;
import java.util.List;

public record GameJsonbView(
    long id,
    int boardSize,
    String status,
    LocalDateTime createdAt,
    boolean active,
    LocalDateTime cancelledAt,
    List<ShipData> ships,
    List<AttackData> attacks
) {}
