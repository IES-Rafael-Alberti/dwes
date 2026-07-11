package com.example.battleship.dto;

import java.time.LocalDateTime;
import java.util.List;

public record GameResponseDTO(
        Long id,
        int boardSize,
        String status,
        LocalDateTime createdAt,
        List<ShipDTO> ships,
        List<AttackDTO> attacks
) {
    public record ShipDTO(Long id, String shipName, int length,
                          int startX, int startY, boolean isHorizontal, boolean sunk) {}
    public record AttackDTO(Long id, int x, int y, boolean hit, LocalDateTime createdAt) {}
}
