package com.example.battleship.repository;

import com.example.battleship.domain.Attack;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AttackRepository extends JpaRepository<Attack, Long> {
    List<Attack> findByGameId(Long gameId);
    boolean existsByGameIdAndXAndY(Long gameId, int x, int y);
}
