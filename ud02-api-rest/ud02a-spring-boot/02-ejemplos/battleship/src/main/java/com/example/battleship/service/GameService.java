package com.example.battleship.service;

import com.example.battleship.domain.Attack;
import com.example.battleship.domain.Game;
import com.example.battleship.domain.GameStatus;
import com.example.battleship.domain.Ship;
import com.example.battleship.domain.exceptions.GameNotFoundException;
import com.example.battleship.dto.AttackDTO;
import com.example.battleship.dto.CreateGameDTO;
import com.example.battleship.dto.GameResponseDTO;
import com.example.battleship.dto.PlaceShipDTO;
import com.example.battleship.repository.AttackRepository;
import com.example.battleship.repository.GameRepository;
import com.example.battleship.repository.ShipRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class GameService {

    private final GameRepository gameRepo;
    private final ShipRepository shipRepo;
    private final AttackRepository attackRepo;

    public GameService(GameRepository gameRepo, ShipRepository shipRepo, AttackRepository attackRepo) {
        this.gameRepo = gameRepo;
        this.shipRepo = shipRepo;
        this.attackRepo = attackRepo;
    }

    public GameResponseDTO createGame(CreateGameDTO dto) {
        Game game = Game.builder().boardSize(dto.boardSize()).status(GameStatus.PENDING).build();
        game = gameRepo.save(game);
        return toResponse(game, List.of(), List.of());
    }

    @Transactional(readOnly = true)
    public List<GameResponseDTO> listGames() {
        return gameRepo.findAllByActiveTrue().stream()
                .map(g -> toResponse(g, shipRepo.findByGameId(g.getId()), attackRepo.findByGameId(g.getId())))
                .toList();
    }

    @Transactional(readOnly = true)
    public Page<GameResponseDTO> listGames(Specification<Game> spec, Pageable pageable) {
        return gameRepo.findAll(spec, pageable)
                .map(g -> toResponse(g, shipRepo.findByGameId(g.getId()), attackRepo.findByGameId(g.getId())));
    }

    @Transactional(readOnly = true)
    public GameResponseDTO getGame(Long id) {
        Game game = gameRepo.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new GameNotFoundException(id));
        return toResponse(game, shipRepo.findByGameId(id), attackRepo.findByGameId(id));
    }

    public GameResponseDTO placeShip(Long gameId, PlaceShipDTO dto) {
        Game game = gameRepo.findByIdAndActiveTrue(gameId)
                .orElseThrow(() -> new GameNotFoundException(gameId));
        if (game.getStatus() == GameStatus.WON || game.getStatus() == GameStatus.CANCELLED) {
            throw new IllegalStateException("Cannot place ships on a finished or cancelled game");
        }
        validateShipPlacement(game, dto);
        validateNoOverlap(gameId, dto);
        Ship ship = Ship.builder()
                .gameId(gameId)
                .shipName(dto.shipName())
                .length(dto.length())
                .startX(dto.startX())
                .startY(dto.startY())
                .isHorizontal(dto.isHorizontal())
                .sunk(false)
                .build();
        ship = shipRepo.save(ship);

        // Sesión 4b — si es el primer barco, pasar a IN_PROGRESS
        List<Ship> ships = shipRepo.findByGameId(gameId);
        if (game.getStatus() == GameStatus.PENDING) {
            game.setStatus(GameStatus.IN_PROGRESS);
            gameRepo.save(game);
        }
        List<Attack> attacks = attackRepo.findByGameId(gameId);
        return toResponse(game, ships, attacks);
    }

    public GameResponseDTO attack(Long gameId, AttackDTO dto) {
        Game game = gameRepo.findByIdAndActiveTrue(gameId)
                .orElseThrow(() -> new GameNotFoundException(gameId));
        if (game.getStatus() != GameStatus.IN_PROGRESS) {
            throw new IllegalStateException("Game is not in progress");
        }
        if (attackRepo.existsByGameIdAndXAndY(gameId, dto.x(), dto.y())) {
            throw new IllegalArgumentException("Position already attacked");
        }
        List<Ship> ships = shipRepo.findByGameId(gameId);
        if (ships.isEmpty()) {
            throw new IllegalStateException("No ships placed. Place ships before attacking.");
        }
        boolean hit = ships.stream().anyMatch(s -> occupies(s, dto.x(), dto.y()));
        Attack attack = Attack.builder().gameId(gameId).x(dto.x()).y(dto.y()).hit(hit).build();
        attack = attackRepo.save(attack);
        checkSunkShips(game, ships);
        checkGameOver(game, ships);
        List<Attack> attacks = attackRepo.findByGameId(gameId);
        return toResponse(game, ships, attacks);
    }

    public void cancelGame(Long gameId) {
        Game game = gameRepo.findByIdAndActiveTrue(gameId)
                .orElseThrow(() -> new GameNotFoundException(gameId));
        game.cancel();
        gameRepo.save(game);
    }

    private void validateShipPlacement(Game game, PlaceShipDTO dto) {
        int size = game.getBoardSize();
        if (dto.startX() < 0 || dto.startY() < 0) {
            throw new IllegalArgumentException("Position out of bounds");
        }
        if (dto.isHorizontal() && dto.startX() + dto.length() > size) {
            throw new IllegalArgumentException("Ship does not fit horizontally");
        }
        if (!dto.isHorizontal() && dto.startY() + dto.length() > size) {
            throw new IllegalArgumentException("Ship does not fit vertically");
        }
        List<Ship> existing = shipRepo.findByGameId(game.getId());
        for (Ship s : existing) {
            if (s.getShipName().equals(dto.shipName())) {
                throw new IllegalArgumentException("Ship name already used");
            }
        }
    }

    // Sesión 4b — los barcos no se solapan
    private void validateNoOverlap(Long gameId, PlaceShipDTO newShip) {
        List<Ship> existing = shipRepo.findByGameId(gameId);
        for (Ship s : existing) {
            if (rectanglesOverlap(
                    s.getStartX(), s.getStartY(),
                    s.isHorizontal() ? s.getStartX() + s.getLength() - 1 : s.getStartX(),
                    s.isHorizontal() ? s.getStartY() : s.getStartY() + s.getLength() - 1,
                    newShip.startX(), newShip.startY(),
                    newShip.isHorizontal() ? newShip.startX() + newShip.length() - 1 : newShip.startX(),
                    newShip.isHorizontal() ? newShip.startY() : newShip.startY() + newShip.length() - 1
            )) {
                throw new IllegalArgumentException("Ships cannot overlap");
            }
        }
    }

    private boolean rectanglesOverlap(int ax1, int ay1, int ax2, int ay2,
                                       int bx1, int by1, int bx2, int by2) {
        return ax1 <= bx2 && ax2 >= bx1 && ay1 <= by2 && ay2 >= by1;
    }

    private boolean occupies(Ship ship, int x, int y) {
        if (ship.isHorizontal()) {
            return y == ship.getStartY() && x >= ship.getStartX() && x < ship.getStartX() + ship.getLength();
        } else {
            return x == ship.getStartX() && y >= ship.getStartY() && y < ship.getStartY() + ship.getLength();
        }
    }

    private void checkSunkShips(Game game, List<Ship> ships) {
        List<Attack> attacks = attackRepo.findByGameId(game.getId());
        for (Ship ship : ships) {
            if (ship.isSunk()) continue;
            if (allPositionsHit(ship, attacks)) {
                ship.setSunk(true);
                shipRepo.save(ship);
            }
        }
    }

    private boolean allPositionsHit(Ship ship, List<Attack> attacks) {
        for (int i = 0; i < ship.getLength(); i++) {
            int x = ship.isHorizontal() ? ship.getStartX() + i : ship.getStartX();
            int y = ship.isHorizontal() ? ship.getStartY() : ship.getStartY() + i;
            boolean hit = attacks.stream().anyMatch(a -> a.getX() == x && a.getY() == y && a.isHit());
            if (!hit) return false;
        }
        return true;
    }

    private void checkGameOver(Game game, List<Ship> ships) {
        boolean allSunk = ships.stream().allMatch(Ship::isSunk);
        if (allSunk && !ships.isEmpty()) {
            game.setStatus(GameStatus.WON);
            gameRepo.save(game);
        }
    }

    private GameResponseDTO toResponse(Game game, List<Ship> ships, List<Attack> attacks) {
        List<GameResponseDTO.ShipDTO> shipDTOs = ships.stream()
                .map(s -> new GameResponseDTO.ShipDTO(s.getId(), s.getShipName(), s.getLength(),
                        s.getStartX(), s.getStartY(), s.isHorizontal(), s.isSunk()))
                .toList();
        List<GameResponseDTO.AttackDTO> attackDTOs = attacks.stream()
                .map(a -> new GameResponseDTO.AttackDTO(a.getId(), a.getX(), a.getY(), a.isHit(), a.getCreatedAt()))
                .toList();
        return new GameResponseDTO(game.getId(), game.getBoardSize(), game.getStatus().name(),
                game.getCreatedAt(), shipDTOs, attackDTOs);
    }
}
