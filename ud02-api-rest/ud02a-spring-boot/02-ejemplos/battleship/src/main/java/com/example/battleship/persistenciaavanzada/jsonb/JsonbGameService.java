package com.example.battleship.persistenciaavanzada.jsonb;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class JsonbGameService {

    private final JdbcTemplate jdbc;
    private final ObjectMapper mapper;

    public JsonbGameService(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
        this.mapper = new ObjectMapper().registerModule(new JavaTimeModule());
    }

    public void createGame(int boardSize) {
        jdbc.update("""
            INSERT INTO game_jsonb (board_size, status, created_at, active, ships_json, attacks_json)
            VALUES (?, 'PENDING', NOW(), TRUE, '[]'::jsonb, '[]'::jsonb)
            """, boardSize);
    }

    public List<GameJsonbView> findByShipName(String shipName) {
        return jdbc.query("""
            SELECT id, board_size, status, created_at, active, cancelled_at, ships_json, attacks_json
            FROM game_jsonb
            WHERE ships_json @> jsonb_build_array(jsonb_build_object('shipName', ?))
            """, rowMapper(), shipName);
    }

    public List<GameJsonbView> findWithHits() {
        return jdbc.query("""
            SELECT id, board_size, status, created_at, active, cancelled_at, ships_json, attacks_json
            FROM game_jsonb
            WHERE attacks_json @> jsonb_build_array(jsonb_build_object('hit', true))
            """, rowMapper());
    }

    public List<GameJsonbView> findByShipLength(int length) {
        return jdbc.query("""
            SELECT g.* FROM game_jsonb g
            CROSS JOIN LATERAL jsonb_array_elements(g.ships_json) AS s
            WHERE (s->>'isHorizontal')::boolean = true
              AND (s->>'length')::int = ?
            """, rowMapper(), length);
    }

    public int countShips(long gameId) {
        return jdbc.queryForObject(
            "SELECT jsonb_array_length(ships_json) FROM game_jsonb WHERE id = ?",
            Integer.class, gameId);
    }

    public void addShip(long gameId, ShipData ship) {
        try {
            String shipJson = mapper.writeValueAsString(ship);
            jdbc.update("""
                UPDATE game_jsonb
                SET ships_json = ships_json || ?::jsonb
                WHERE id = ?
                """, shipJson, gameId);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize ship", e);
        }
    }

    public void addAttack(long gameId, AttackData attack) {
        try {
            String attackJson = mapper.writeValueAsString(attack);
            jdbc.update("""
                UPDATE game_jsonb
                SET attacks_json = attacks_json || ?::jsonb
                WHERE id = ?
                """, attackJson, gameId);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize attack", e);
        }
    }

    private RowMapper<GameJsonbView> rowMapper() {
        return (rs, rowNum) -> {
            try {
                List<ShipData> ships = mapper.readValue(
                    rs.getString("ships_json"),
                    new TypeReference<List<ShipData>>() {});
                List<AttackData> attacks = mapper.readValue(
                    rs.getString("attacks_json"),
                    new TypeReference<List<AttackData>>() {});
                return new GameJsonbView(
                    rs.getLong("id"),
                    rs.getInt("board_size"),
                    rs.getString("status"),
                    rs.getObject("created_at", LocalDateTime.class),
                    rs.getBoolean("active"),
                    rs.getObject("cancelled_at", LocalDateTime.class),
                    ships, attacks);
            } catch (JsonProcessingException e) {
                throw new SQLException("Failed to parse JSON", e);
            }
        };
    }
}
