// Código de referencia para MongoDB.
// Descomentar la dependencia spring-boot-starter-data-mongodb en pom.xml
// y copiar a src/main/java/.../mongo/ para compilar.

package com.example.battleship.persistenciaavanzada.mongo;

/*
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
*/

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

// @Document("games")
public class GameDocument {

    // @Id
    private String id;

    private int boardSize;
    private String status;
    private LocalDateTime createdAt;
    private boolean active = true;
    private LocalDateTime cancelledAt;
    private List<ShipEmbedded> ships = new ArrayList<>();
    private List<AttackEmbedded> attacks = new ArrayList<>();

    public GameDocument() {}

    public GameDocument(int boardSize) {
        this.boardSize = boardSize;
        this.status = "PENDING";
        this.createdAt = LocalDateTime.now();
    }

    public String getId() { return id; }
    public int getBoardSize() { return boardSize; }
    public String getStatus() { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public boolean isActive() { return active; }
    public LocalDateTime getCancelledAt() { return cancelledAt; }
    public List<ShipEmbedded> getShips() { return ships; }
    public List<AttackEmbedded> getAttacks() { return attacks; }

    public void setStatus(String status) { this.status = status; }
    public void setShips(List<ShipEmbedded> ships) { this.ships = ships; }
    public void setAttacks(List<AttackEmbedded> attacks) { this.attacks = attacks; }

    public void cancel() {
        this.status = "CANCELLED";
        this.active = false;
        this.cancelledAt = LocalDateTime.now();
    }
}
