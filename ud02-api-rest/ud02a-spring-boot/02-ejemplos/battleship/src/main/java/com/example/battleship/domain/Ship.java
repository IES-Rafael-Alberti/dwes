package com.example.battleship.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class Ship {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "game_id", nullable = false)
    private Long gameId;

    @Column(name = "ship_name", nullable = false, length = 50)
    private String shipName;

    @Column(nullable = false)
    private int length;

    @Column(name = "start_x", nullable = false)
    private int startX;

    @Column(name = "start_y", nullable = false)
    private int startY;

    @Column(name = "is_horizontal", nullable = false)
    private boolean isHorizontal;

    @Column(nullable = false)
    private boolean sunk;
}
