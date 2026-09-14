# Sesión 1: JSONB vs relacional

> Duración estimada: 90-120 min
>
> Requiere PostgreSQL 15+ con `pg_stat_statements` habilitado para el benchmark.

## 1. Introducción
PostgreSQL ofrece tres tipos relacionados con JSON:

- `json`: almacena texto JSON tal cual, valida sintaxis, pero no optimiza consultas complejas.
- `jsonb`: almacena en formato binario, consulta más rápido, soporta índices GIN, normaliza espacios en blanco y claves duplicadas (la última clave gana).
- `jsonpath`: disponible desde PostgreSQL 12, permite consultas por rutas dentro del documento.

**Regla práctica:** usa siempre `jsonb` salvo que necesites preservar el orden de claves o el formato exacto del texto, algo poco habitual.

## 2. Cuándo usar JSONB vs columna relacional

| Criterio | Columna relacional | JSONB |
|----------|-------------------|-------|
| Los datos tienen estructura fija | ✅ | ❌ |
| Necesito integridad referencial (FK) | ✅ | ❌ (solo simulable con checks) |
| Necesito validar tipos estrictamente | ✅ | ❌ (schemaless) |
| Los campos varían por fila | ❌ | ✅ |
| Necesito índices sobre campos anidados | ❌ | ✅ (GIN) |
| Consultas tipo "contiene" | ❌ | ✅ (`@>`, `?`) |
| Datos que nunca consultaré individualmente | ❌ | ✅ (metadata) |

**La decisión correcta:** un modelo híbrido. Usa columnas relacionales para lo fijo y estructurado; usa JSONB para atributos variables, metadata o configuraciones.

## 3. Operaciones básicas con JSONB

```sql
SELECT '{"key": "value"}'::jsonb;

SELECT data->'customer' AS customer_json,
       data->>'customer' AS customer_text
FROM orders;

SELECT data#>'{shipping,address}' AS shipping_address_json,
       data#>>'{shipping,address}' AS shipping_address_text
FROM orders;

SELECT '{"a": 1, "b": 2}'::jsonb @> '{"a": 1}'::jsonb;
SELECT '{"a": 1}'::jsonb <@ '{"a": 1, "b": 2}'::jsonb;

SELECT '{"tags": ["sql", "json"]}'::jsonb ? 'tags';
SELECT '{"tags": ["sql", "json"]}'::jsonb ?| ARRAY['tags', 'price'];
SELECT '{"tags": ["sql", "json"]}'::jsonb ?& ARRAY['tags', 'price'];

SELECT * FROM jsonb_each('{"a": 1, "b": 2}'::jsonb);
SELECT * FROM jsonb_object_keys('{"a": 1, "b": 2}'::jsonb);
SELECT * FROM jsonb_array_elements('[1, 2, 3]'::jsonb);
```

## 4. GIN indexes

```sql
CREATE INDEX ON orders USING GIN (metadata);
CREATE INDEX ON orders USING GIN (metadata jsonb_path_ops);
```

GIN acelera especialmente:

- `@>`
- `?`
- `?|`
- `?&`

### `jsonb_path_ops` vs índice GIN por defecto
- **Por defecto**: más versátil, soporta más operadores.
- **`jsonb_path_ops`**: más compacto y eficiente para contención (`@>`), pero menos flexible.

Verifica siempre con `EXPLAIN ANALYZE`:

```sql
EXPLAIN ANALYZE
SELECT *
FROM orders
WHERE metadata @> '{"country": "ES"}'::jsonb;
```

## 5. Columnas generadas

```sql
ALTER TABLE orders
ADD COLUMN created_at_from_metadata TIMESTAMPTZ
GENERATED ALWAYS AS ((metadata->>'createdAt')::timestamptz) STORED;
```

Sirven para indexar campos internos de JSONB como si fueran columnas normales.

## 6. Ejemplo guiado (demo)

```sql
CREATE EXTENSION IF NOT EXISTS pgcrypto;

CREATE TABLE orders (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  customer_id INTEGER NOT NULL REFERENCES customers(id),
  status TEXT NOT NULL CHECK (status IN ('pending', 'shipped', 'delivered', 'cancelled')),
  total NUMERIC(10,2) NOT NULL,
  metadata JSONB NOT NULL DEFAULT '{}'::jsonb,
  created_at TIMESTAMPTZ DEFAULT NOW()
);

CREATE INDEX idx_orders_metadata ON orders USING GIN (metadata jsonb_path_ops);

ALTER TABLE orders
ADD COLUMN shipping_country TEXT
GENERATED ALWAYS AS (metadata->>'country') STORED;

CREATE INDEX idx_orders_country ON orders (shipping_country);
```

## 7. Ejercicio autónomo

Crear una tabla `products` con:

- `id` (serial PK)
- `name` (text)
- `price` (numeric)
- `category` (text FK)
- `created_at` (timestamptz)
- `attributes JSONB` para atributos variables

### Tareas
1. Insertar 5 productos con atributos variados.
2. Añadir índice GIN en `attributes`.
3. Crear una columna generada para `weight` (numérica).
4. Hacer queries para:
   - productos que tengan atributo `color`
   - productos con `color: "rojo"` y precio < 50
   - productos cuyo peso sea > 2kg
   - `EXPLAIN ANALYZE` de cada query

### Entregable
Script SQL completo con comentarios justificativos.

### Rúbrica de evaluación
| Criterio | Insuficiente | Correcto | Excelente |
|----------|--------------|----------|-----------|
| Diseño de tabla | Falta coherencia | Modelo válido | Modelo híbrido bien justificado |
| Uso de JSONB | No usa operadores adecuados | Usa operadores básicos | Usa operadores e índices correctamente |
| Índices | No crea índices | Crea GIN | Crea e interpreta `EXPLAIN ANALYZE` |
| Consultas | Incompletas | Resuelve lo pedido | Resuelve y optimiza |
