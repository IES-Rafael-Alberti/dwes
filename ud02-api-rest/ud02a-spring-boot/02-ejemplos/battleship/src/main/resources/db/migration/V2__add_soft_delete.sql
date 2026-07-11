-- Sesión 4b: soft delete + estado inicial PENDING
ALTER TABLE game ADD COLUMN active BOOLEAN NOT NULL DEFAULT TRUE;
ALTER TABLE game ADD COLUMN cancelled_at TIMESTAMP;

-- Actualizar partidas existentes a PENDING (antes era IN_PROGRESS por defecto)
UPDATE game SET status = 'PENDING' WHERE status = 'IN_PROGRESS';

-- Cambiar valor por defecto para nuevas partidas
ALTER TABLE game ALTER COLUMN status SET DEFAULT 'PENDING';
