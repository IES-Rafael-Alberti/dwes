package com.example.battleship.web;

import com.example.battleship.dto.AttackDTO;
import com.example.battleship.dto.CreateGameDTO;
import com.example.battleship.dto.GameResponseDTO;
import com.example.battleship.dto.PlaceShipDTO;
import com.example.battleship.repository.GameSpecifications;
import com.example.battleship.service.GameService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/games")
public class GameController {

    private final GameService gameService;

    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @PostMapping
    @PreAuthorize("hasRole('PLAYER')")
    public ResponseEntity<GameResponseDTO> create(@Valid @RequestBody CreateGameDTO dto) {
        GameResponseDTO game = gameService.createGame(dto);
        return ResponseEntity.created(URI.create("/api/games/" + game.id())).body(game);
    }

    @GetMapping
    public Page<GameResponseDTO> list(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) @Min(1) Integer minBoardSize,
            @RequestParam(required = false) LocalDateTime createdAfter,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        var spec = Specification
                .where(GameSpecifications.hasStatus(status))
                .and(GameSpecifications.boardSizeAtLeast(minBoardSize != null ? minBoardSize : 0))
                .and(GameSpecifications.createdAfter(createdAfter));

        return gameService.listGames(spec, pageable);
    }

    @GetMapping("/{id}")
    public ResponseEntity<GameResponseDTO> get(@PathVariable Long id) {
        return ResponseEntity.ok(gameService.getGame(id));
    }

    @PostMapping("/{id}/ships")
    @PreAuthorize("hasRole('PLAYER')")
    public ResponseEntity<GameResponseDTO> placeShip(@PathVariable Long id, @Valid @RequestBody PlaceShipDTO dto) {
        GameResponseDTO game = gameService.placeShip(id, dto);
        return ResponseEntity.created(URI.create("/api/games/" + game.id())).body(game);
    }

    @PostMapping("/{id}/attacks")
    @PreAuthorize("hasRole('PLAYER')")
    public ResponseEntity<GameResponseDTO> attack(@PathVariable Long id, @Valid @RequestBody AttackDTO dto) {
        GameResponseDTO game = gameService.attack(id, dto);
        return ResponseEntity.created(URI.create("/api/games/" + game.id())).body(game);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> cancel(@PathVariable Long id) {
        gameService.cancelGame(id);
        return ResponseEntity.noContent().build();
    }
}
