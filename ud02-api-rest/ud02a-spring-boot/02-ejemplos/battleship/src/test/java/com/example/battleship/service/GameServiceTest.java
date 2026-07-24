package com.example.battleship.service;

import com.example.battleship.domain.Attack;
import com.example.battleship.domain.Game;
import com.example.battleship.domain.Ship;
import com.example.battleship.domain.exceptions.GameNotFoundException;
import com.example.battleship.dto.AttackDTO;
import com.example.battleship.dto.CreateGameDTO;
import com.example.battleship.dto.GameResponseDTO;
import com.example.battleship.dto.PlaceShipDTO;
import com.example.battleship.repository.AttackRepository;
import com.example.battleship.repository.GameRepository;
import com.example.battleship.repository.ShipRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class GameServiceTest {

    @Autowired
    private GameService gameService;

    @Autowired
    private GameRepository gameRepo;

    @Autowired
    private ShipRepository shipRepo;

    @Autowired
    private AttackRepository attackRepo;

    private Long gameId;

    @BeforeEach
    void setUp() {
        GameResponseDTO game = gameService.createGame(new CreateGameDTO(10));
        gameId = game.id();
    }

    @Test
    void createGame_createsWithPendingStatus() {
        GameResponseDTO game = gameService.createGame(new CreateGameDTO(8));
        assertEquals(8, game.boardSize());
        assertEquals("PENDING", game.status());
        assertNotNull(game.id());
    }

    @Test
    void cancelGame_removesFromList() {
        gameService.cancelGame(gameId);
        var games = gameService.listGames();
        assertTrue(games.stream().noneMatch(g -> g.id().equals(gameId)));
    }

    @Test
    void listGames_returnsCreatedGames() {
        var games = gameService.listGames();
        assertFalse(games.isEmpty());
        assertTrue(games.stream().anyMatch(g -> g.id().equals(gameId)));
    }

    @Test
    void getGame_returnsGameDetails() {
        GameResponseDTO game = gameService.getGame(gameId);
        assertEquals(gameId, game.id());
        assertEquals(10, game.boardSize());
    }

    @Test
    void getGame_throwsForNonExistent() {
        assertThrows(GameNotFoundException.class, () -> gameService.getGame(999L));
    }

    @Test
    void placeShip_addsShipToGame() {
        GameResponseDTO result = gameService.placeShip(gameId,
                new PlaceShipDTO("Destroyer", 2, 0, 0, true));
        assertEquals(1, result.ships().size());
        assertEquals("Destroyer", result.ships().getFirst().shipName());
    }

    @Test
    void placeShip_duplicateName_throws() {
        gameService.placeShip(gameId, new PlaceShipDTO("Destroyer", 2, 0, 0, true));
        assertThrows(IllegalArgumentException.class,
                () -> gameService.placeShip(gameId, new PlaceShipDTO("Destroyer", 2, 3, 0, true)));
    }

    @Test
    void placeShip_outOfBounds_throws() {
        assertThrows(IllegalArgumentException.class,
                () -> gameService.placeShip(gameId, new PlaceShipDTO("Carrier", 5, 8, 0, true)));
    }

    @Test
    void placeShip_overlappingExistingShip_throws() {
        gameService.placeShip(gameId, new PlaceShipDTO("Destroyer", 3, 1, 2, true));

        assertThrows(IllegalArgumentException.class,
                () -> gameService.placeShip(gameId, new PlaceShipDTO("Submarine", 3, 2, 1, false)));
    }

    @Test
    void attack_miss_recordsMiss() {
        gameService.placeShip(gameId, new PlaceShipDTO("Destroyer", 2, 0, 0, true));
        GameResponseDTO result = gameService.attack(gameId, new AttackDTO(9, 9));
        assertFalse(result.attacks().getFirst().hit());
    }

    @Test
    void attack_hit_recordsHit() {
        gameService.placeShip(gameId, new PlaceShipDTO("Destroyer", 2, 0, 0, true));
        GameResponseDTO result = gameService.attack(gameId, new AttackDTO(0, 0));
        assertTrue(result.attacks().getFirst().hit());
    }

    @Test
    void attack_duplicatePosition_throws() {
        gameService.placeShip(gameId, new PlaceShipDTO("Destroyer", 2, 0, 0, true));
        gameService.attack(gameId, new AttackDTO(0, 0));
        assertThrows(IllegalArgumentException.class,
                () -> gameService.attack(gameId, new AttackDTO(0, 0)));
    }

    @Test
    void sinkAllShips_winsGame() {
        gameService.placeShip(gameId, new PlaceShipDTO("Patrol", 2, 0, 0, true));
        GameResponseDTO r1 = gameService.attack(gameId, new AttackDTO(0, 0));
        assertEquals("IN_PROGRESS", r1.status());
        GameResponseDTO r2 = gameService.attack(gameId, new AttackDTO(1, 0));
        assertEquals("WON", r2.status());
    }
}
