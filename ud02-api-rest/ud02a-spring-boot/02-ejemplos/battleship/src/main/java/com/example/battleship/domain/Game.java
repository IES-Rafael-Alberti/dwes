package com.example.battleship.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "board_size", nullable = false)
    private int boardSize;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private GameStatus status;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    // Sesión 4b — soft delete
    @Builder.Default
    @Column(nullable = false)
    private boolean active = true;

    @Column(name = "cancelled_at")
    private LocalDateTime cancelledAt;

    @PrePersist
    void onCreate() {
        if (createdAt == null) createdAt = LocalDateTime.now();
        if (status == null) status = GameStatus.PENDING;
        if (boardSize == 0) boardSize = 10;
        active = true;
    }

    public void cancel() {
        if (status == GameStatus.WON) {
            throw new IllegalStateException("Cannot cancel a finished game");
        }
        this.status = GameStatus.CANCELLED;
        this.active = false;
        this.cancelledAt = LocalDateTime.now();
    }
}
