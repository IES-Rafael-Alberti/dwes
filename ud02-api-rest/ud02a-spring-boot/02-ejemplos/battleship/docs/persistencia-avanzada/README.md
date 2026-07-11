# Persistencia avanzada

Más allá de H2 + Spring Data JPA. Contenido para ampliación o estudio autónomo.

## 1. JSON en PostgreSQL con JSONB

PostgreSQL ofrece el tipo `jsonb` que permite almacenar documentos JSON directamente en columnas de una tabla relacional. Es el punto medio entre SQL puro y NoSQL.

### ¿Por qué JSONB?

| Situación | Tabla normalizada | JSONB |
|-----------|-------------------|-------|
| Datos siempre iguales (mismos campos) | ✅ Esquema fijo, FK, índices | ❌ Sin restricciones de esquema |
| Datos semiestructurados (campos variables) | ❌ Muchas tablas, joins lentos | ✅ Un solo documento |
| Relaciones 1:N profundas | ❌ Múltiples joins | ✅ Arrays embebidos |
| Consultas ad-hoc sobre campos internos | ❌ SQL complejo | ✅ `@>`, `jsonb_path_query` |

### Ejemplo en Battleship

En el modelo relacional, `ship` y `attack` son tablas separadas con FK a `game`:

```sql
-- Relacional: 3 tablas, joins necesarios
SELECT * FROM game g
JOIN ship s ON s.game_id = g.id
JOIN attack a ON a.game_id = g.id;
```

Con JSONB, los barcos y ataques se guardan directamente en la fila de `game`:

```sql
-- Una tabla, datos embebidos
CREATE TABLE game_jsonb (
    id            BIGSERIAL PRIMARY KEY,
    board_size    INTEGER NOT NULL DEFAULT 10,
    status        VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    created_at    TIMESTAMP NOT NULL DEFAULT NOW(),
    active        BOOLEAN NOT NULL DEFAULT TRUE,
    cancelled_at  TIMESTAMP,
    ships_json    JSONB NOT NULL DEFAULT '[]'::jsonb,
    attacks_json  JSONB NOT NULL DEFAULT '[]'::jsonb
);

CREATE INDEX idx_game_jsonb_ships   ON game_jsonb USING GIN (ships_json);
CREATE INDEX idx_game_jsonb_attacks ON game_jsonb USING GIN (attacks_json);
```

### Código disponible

| Archivo | Descripción |
|---------|-------------|
| `persistenciaavanzada/jsonb/JsonbGameService.java` | Servicio con `JdbcTemplate` para operaciones JSONB |
| `persistenciaavanzada/jsonb/GameJsonbView.java` | Record con la proyección de la consulta |
| `persistenciaavanzada/jsonb/ShipData.java` | Record para datos de barco embebidos en JSON |
| `persistenciaavanzada/jsonb/AttackData.java` | Record para datos de ataque embebidos en JSON |
| `db/jsonb-migrations/V5__create_game_jsonb_table.sql` | Migración Flyway con tabla + índices GIN (solo PostgreSQL) |
| `application-jsonb.yml` | Perfil que activa las migraciones JSONB |

### Cómo probar

```bash
# Requiere PostgreSQL corriendo
SPRING_PROFILES_ACTIVE=prod,jsonb mvn spring-boot:run
```

### Operadores JSONB destacados

```sql
-- Contiene: ¿hay un barco con este nombre?
WHERE ships_json @> '[{"shipName": "Destroyer"}]'::jsonb

-- Extraer y filtrar elementos del array
SELECT * FROM game g
CROSS JOIN LATERAL jsonb_array_elements(g.ships_json) AS s
WHERE (s->>'isHorizontal')::boolean = true

-- Longitud del array
SELECT jsonb_array_length(ships_json) FROM game WHERE id = 1

-- Acceso a campo con path query (PG 12+)
SELECT jsonb_path_query(ships_json, '$[*].shipName') FROM game;
```

### Cuándo usar JSONB (y cuándo no)

| Usá JSONB cuando... | Usá tabla normalizada cuando... |
|---------------------|----------------------------------|
| Los datos son autocontenidos (se leen y escriben juntos) | Necesitás consultar campos individuales con frecuencia |
| La estructura puede variar (campos opcionales) | Necesitás integridad referencial con FK |
| Tenés relaciones 1:N poco profundas | Los datos se actualizan parcialmente |
| Querés evitar joins en lecturas frecuentes | Necesitás transacciones complejas con varias tablas |

> **Regla práctica**: si siempre cargás barcos y ataques junto con la partida, JSONB ahorra joins y simplifica el modelo. Si necesitás "todos los barcos de todas las partidas con length=5", la tabla normalizada es más rápida.

---

## 2. MongoDB con Spring Data

MongoDB es una base de datos NoSQL documental. En lugar de tablas con filas y columnas, usa colecciones de documentos JSON (BSON).

### Documento vs Tabla

| Concepto SQL | Concepto MongoDB |
|-------------|------------------|
| Base de datos | Base de datos |
| Tabla | Colección |
| Fila | Documento |
| Columna | Campo |
| FK / JOIN | Documentos embebidos o referencias |
| Schema migration | Migración de código (sin esquema fijo) |

### Ejemplo en Battleship

Mismo modelo, pero como documento MongoDB. La partida, los barcos y los ataques viven en un solo documento:

```json
{
  "_id": ObjectId("..."),
  "boardSize": 10,
  "status": "IN_PROGRESS",
  "createdAt": ISODate("..."),
  "active": true,
  "ships": [
    { "shipName": "Destroyer", "length": 2, "startX": 0, "startY": 0,
      "isHorizontal": true, "sunk": false }
  ],
  "attacks": [
    { "x": 5, "y": 5, "hit": false, "createdAt": ISODate("...") }
  ]
}
```

### Código disponible

| Archivo | Descripción |
|---------|-------------|
| `docs/persistencia-avanzada/mongo/GameDocument.java` | Documento MongoDB con `@Document("games")` (código de referencia) |
| `docs/persistencia-avanzada/mongo/ShipEmbedded.java` | Record embebido (subdocumento) |
| `docs/persistencia-avanzada/mongo/AttackEmbedded.java` | Record embebido (subdocumento) |
| `docs/persistencia-avanzada/mongo/GameMongoRepository.java` | `MongoRepository` con queries derivadas |

### Cómo probar

1. Descomentar la dependencia `spring-boot-starter-data-mongodb` en `pom.xml`
2. Tener MongoDB corriendo (localhost:27017)
3. Crear `application-mongo.yml`:

```yaml
spring:
  data:
    mongodb:
      uri: mongodb://localhost:27017/battleship
```

4. Ejecutar: `SPRING_PROFILES_ACTIVE=mongo mvn spring-boot:run`

### MongoDB vs JPA + JSONB

| Aspecto | JPA + JSONB (PostgreSQL) | MongoDB |
|---------|--------------------------|---------|
| Esquema | Híbrido: tabla con columna JSON | Sin esquema |
| Queries | SQL + operadores JSONB | API propia (Query DSL, aggregation pipeline) |
| Transacciones | ✅ ACID completas | ✅ A partir de 4.0 (multi-documento) |
| Joins | ✅ JOIN entre tablas | ❌ No hay joins (datos embebidos o $lookup) |
| Escalado | Vertical (más CPU/RAM) | Horizontal (sharding nativo) |
| Índices | GIN, B-tree, hash | B-tree, compound, text, 2dsphere, etc. |
| Madurez relacional | Máxima | Menor (no hay FK, no hay schema) |

> MongoDB brilla cuando el modelo es puramente documental y necesitás escalar horizontalmente. JSONB en PostgreSQL es ideal cuando ya estás usando PostgreSQL y querés flexibilidad documental sin salir del ecosistema SQL.

---

## 3. Material complementario

- [PostgreSQL JSON Functions and Operators](https://www.postgresql.org/docs/current/functions-json.html)
- [Spring Data MongoDB Reference](https://docs.spring.io/spring-data/mongodb/reference/)
- [MongoDB Aggregation Pipeline](https://www.mongodb.com/docs/manual/aggregation/)
