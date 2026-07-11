// Código de referencia para MongoDB.
// Copiar a src/main/java/.../mongo/ cuando esté activo spring-boot-starter-data-mongodb.

package com.example.battleship.persistenciaavanzada.mongo;

import java.time.LocalDateTime;

public record AttackEmbedded(
    int x,
    int y,
    boolean hit,
    LocalDateTime createdAt
) {}
