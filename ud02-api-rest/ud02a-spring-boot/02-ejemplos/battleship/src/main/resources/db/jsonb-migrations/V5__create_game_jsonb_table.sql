-- Tabla de ejemplo con columnas JSONB (PostgreSQL only).
-- Activada por el perfil 'jsonb'.

CREATE TABLE IF NOT EXISTS game_jsonb (
    id            BIGSERIAL PRIMARY KEY,
    board_size    INTEGER NOT NULL DEFAULT 10,
    status        VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    created_at    TIMESTAMP NOT NULL DEFAULT NOW(),
    active        BOOLEAN NOT NULL DEFAULT TRUE,
    cancelled_at  TIMESTAMP,
    ships_json    JSONB NOT NULL DEFAULT '[]'::jsonb,
    attacks_json  JSONB NOT NULL DEFAULT '[]'::jsonb
);

CREATE INDEX IF NOT EXISTS idx_game_jsonb_ships   ON game_jsonb USING GIN (ships_json);
CREATE INDEX IF NOT EXISTS idx_game_jsonb_attacks ON game_jsonb USING GIN (attacks_json);
