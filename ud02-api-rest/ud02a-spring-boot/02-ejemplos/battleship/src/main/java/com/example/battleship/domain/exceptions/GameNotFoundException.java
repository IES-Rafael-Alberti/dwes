package com.example.battleship.domain.exceptions;

public class GameNotFoundException extends RuntimeException {
    public GameNotFoundException(Long id) {
        super("Game not found: " + id);
    }
}
