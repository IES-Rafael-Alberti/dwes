// Código de referencia para MongoDB.
// Copiar a src/main/java/.../mongo/ cuando esté activo spring-boot-starter-data-mongodb.

package com.example.battleship.persistenciaavanzada.mongo;

/*
import org.springframework.data.mongodb.repository.MongoRepository;
*/

import java.util.List;

// public interface GameMongoRepository extends MongoRepository<GameDocument, String> {
public interface GameMongoRepository {

    List<GameDocument> findByStatus(String status);

    List<GameDocument> findByBoardSizeGreaterThanEqual(int minSize);

    List<GameDocument> findByActiveTrue();
}
