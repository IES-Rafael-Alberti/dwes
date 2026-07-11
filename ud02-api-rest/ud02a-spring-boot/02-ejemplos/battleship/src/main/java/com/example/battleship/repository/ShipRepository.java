package com.example.battleship.repository;

import com.example.battleship.domain.Ship;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ShipRepository extends JpaRepository<Ship, Long> {
    List<Ship> findByGameId(Long gameId);
}
