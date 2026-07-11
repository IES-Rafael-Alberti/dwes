package com.example.battleship.repository;

import com.example.battleship.domain.Game;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface GameRepository extends JpaRepository<Game, Long>, JpaSpecificationExecutor<Game> {

    // Sesión 4b — soft delete: solo juegos activos
    List<Game> findAllByActiveTrue();

    Optional<Game> findByIdAndActiveTrue(Long id);
}
