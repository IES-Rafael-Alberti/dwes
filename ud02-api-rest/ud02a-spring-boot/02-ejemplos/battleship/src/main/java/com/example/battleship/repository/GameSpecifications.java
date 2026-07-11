package com.example.battleship.repository;

import com.example.battleship.domain.Game;
import org.springframework.data.jpa.domain.Specification;

public class GameSpecifications {

    public static Specification<Game> hasStatus(String status) {
        return (root, query, cb) ->
            status == null ? null : cb.equal(root.get("status"), status);
    }

    public static Specification<Game> boardSizeAtLeast(int minSize) {
        return (root, query, cb) ->
            cb.greaterThanOrEqualTo(root.get("boardSize"), minSize);
    }

    public static Specification<Game> createdAfter(java.time.LocalDateTime date) {
        return (root, query, cb) ->
            date == null ? null : cb.greaterThanOrEqualTo(root.get("createdAt"), date);
    }
}
