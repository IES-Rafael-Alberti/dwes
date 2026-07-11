// Código de referencia para MongoDB.
// Copiar a src/main/java/.../mongo/ cuando esté activo spring-boot-starter-data-mongodb.

package com.example.battleship.persistenciaavanzada.mongo;

public record ShipEmbedded(
    String shipName,
    int length,
    int startX,
    int startY,
    boolean isHorizontal,
    boolean sunk
) {}
