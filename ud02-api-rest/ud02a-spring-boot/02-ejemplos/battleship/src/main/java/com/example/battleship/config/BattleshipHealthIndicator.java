package com.example.battleship.config;

import com.example.battleship.repository.GameRepository;
import org.springframework.boot.health.contributor.Health;
import org.springframework.boot.health.contributor.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
public class BattleshipHealthIndicator implements HealthIndicator {

    private final GameRepository gameRepository;

    public BattleshipHealthIndicator(GameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }

    @Override
    public Health health() {
        try {
            long count = gameRepository.count();
            return Health.up()
                    .withDetail("totalGames", count)
                    .build();
        } catch (Exception e) {
            return Health.down()
                    .withDetail("error", e.getMessage())
                    .build();
        }
    }
}
