package com.example.battleship.repository;

import com.example.battleship.domain.Game;
import com.example.battleship.domain.GameStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class GameRepositoryTest {

    @Autowired
    private GameRepository gameRepository;

    @BeforeEach
    void cleanUp() {
        gameRepository.deleteAll();
    }

    @Test
    void saveAndFindGame() {
        Game saved = gameRepository.save(new Game());

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getStatus()).isEqualTo(GameStatus.PENDING);
        assertThat(saved.isActive()).isTrue();

        var found = gameRepository.findById(saved.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getBoardSize()).isEqualTo(10);
    }

    @Test
    void findAllByActiveTrue_excludesCancelledGames() {
        Game active1 = gameRepository.save(new Game());
        Game active2 = gameRepository.save(Game.builder().boardSize(8).build());

        Game cancelled = gameRepository.save(new Game());
        cancelled.cancel();
        gameRepository.save(cancelled);

        assertThat(gameRepository.findAllByActiveTrue())
                .hasSize(2)
                .extracting(Game::getId)
                .containsExactlyInAnyOrder(active1.getId(), active2.getId());
    }

    @Test
    void findByIdAndActiveTrue_returnsOnlyActive() {
        Game active = gameRepository.save(new Game());

        Game cancelled = gameRepository.save(new Game());
        cancelled.cancel();
        gameRepository.save(cancelled);

        assertThat(gameRepository.findByIdAndActiveTrue(active.getId())).isPresent();
        assertThat(gameRepository.findByIdAndActiveTrue(cancelled.getId())).isEmpty();
    }
}
