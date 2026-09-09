# Ejercicio: modelo híbrido orders + metadata

## Enunciado
Se va a diseñar el modelo de datos para la gestión de pedidos de una tienda online.

Cada pedido tiene:

- **Campos fijos**: `order_id`, `customer_id` (FK), `status`, `subtotal`, `shipping_cost`, `tax`, `total`, `created_at`
- **Campos variables por tipo de envío**:
  - **Envío normal**: dirección, tracking_number, estimated_days
  - **Envío express**: dirección, tracking_number, guaranteed_hours, contact_phone, insurance_amount
  - **Envío internacional**: dirección, tracking_number, customs_declaration (array de items con codigo y valor), origin_country, destination_country, estimated_days
  - **Recogida en tienda**: store_id, pickup_code, estimated_date

## Tareas
1. Diseñar el schema con JSONB para `shipping_details`.
2. Escribir el DDL completo (tipos relacionales + JSONB + GIN index + columna generada para país).
3. Escribir un trigger que valide `shipping_details` según el tipo de envío.
4. Insertar ejemplos de cada tipo.
5. Consultar pedidos internacionales con aduana pendiente.
6. Consultar pedidos express con garantía < 4h que no se han entregado.
7. Ejecutar `EXPLAIN ANALYZE` de cada query.
8. Comparar con un equivalente en MongoDB (dibujar el schema Mongo).

## Propuesta de DDL

```sql
CREATE TABLE orders (
  order_id UUID PRIMARY KEY,
  customer_id BIGINT NOT NULL REFERENCES customers(id),
  status TEXT NOT NULL CHECK (status IN ('pending', 'paid', 'shipped', 'delivered', 'cancelled')),
  subtotal NUMERIC(10,2) NOT NULL,
  shipping_cost NUMERIC(10,2) NOT NULL,
  tax NUMERIC(10,2) NOT NULL,
  total NUMERIC(10,2) NOT NULL,
  shipping_type TEXT NOT NULL CHECK (shipping_type IN ('normal', 'express', 'international', 'pickup')),
  shipping_details JSONB NOT NULL,
  created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_orders_shipping_details ON orders USING GIN (shipping_details);

ALTER TABLE orders
ADD COLUMN destination_country TEXT
GENERATED ALWAYS AS (shipping_details->>'destination_country') STORED;

CREATE INDEX idx_orders_destination_country ON orders (destination_country);
```

## Trigger de validación

```sql
CREATE OR REPLACE FUNCTION validate_shipping_details()
RETURNS TRIGGER AS $$
BEGIN
  IF NEW.shipping_type = 'normal' THEN
    IF NOT (NEW.shipping_details ? 'address'
         AND NEW.shipping_details ? 'tracking_number'
         AND NEW.shipping_details ? 'estimated_days') THEN
      RAISE EXCEPTION 'shipping_details inválido para envío normal';
    END IF;
  ELSIF NEW.shipping_type = 'express' THEN
    IF NOT (NEW.shipping_details ? 'address'
         AND NEW.shipping_details ? 'tracking_number'
         AND NEW.shipping_details ? 'guaranteed_hours'
         AND NEW.shipping_details ? 'contact_phone'
         AND NEW.shipping_details ? 'insurance_amount') THEN
      RAISE EXCEPTION 'shipping_details inválido para envío express';
    END IF;
  ELSIF NEW.shipping_type = 'international' THEN
    IF NOT (NEW.shipping_details ? 'customs_declaration'
         AND NEW.shipping_details ? 'origin_country'
         AND NEW.shipping_details ? 'destination_country') THEN
      RAISE EXCEPTION 'shipping_details inválido para envío internacional';
    END IF;
  ELSIF NEW.shipping_type = 'pickup' THEN
    IF NOT (NEW.shipping_details ? 'store_id'
         AND NEW.shipping_details ? 'pickup_code'
         AND NEW.shipping_details ? 'estimated_date') THEN
      RAISE EXCEPTION 'shipping_details inválido para recogida en tienda';
    END IF;
  END IF;

  RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_validate_shipping_details
BEFORE INSERT OR UPDATE ON orders
FOR EACH ROW
EXECUTE FUNCTION validate_shipping_details();
```

## Queries solicitadas

```sql
-- Pedidos internacionales con aduana pendiente
SELECT *
FROM orders
WHERE shipping_type = 'international'
  AND shipping_details->>'customs_status' = 'pending';

-- Pedidos express con garantía < 4h que no se han entregado
SELECT *
FROM orders
WHERE shipping_type = 'express'
  AND (shipping_details->>'guaranteed_hours')::int < 4
  AND status <> 'delivered';
```

## EXPLAIN ANALYZE

```sql
EXPLAIN ANALYZE
SELECT *
FROM orders
WHERE shipping_type = 'international'
  AND shipping_details->>'customs_status' = 'pending';

EXPLAIN ANALYZE
SELECT *
FROM orders
WHERE shipping_type = 'express'
  AND (shipping_details->>'guaranteed_hours')::int < 4
  AND status <> 'delivered';
```

## Rúbrica de evaluación
| Criterio | Insuficiente | Mejorable | Correcto | Excelente |
|----------|--------------|-----------|----------|-----------|
| Diseño del modelo | Incompleto | Aceptable | Coherente | Muy bien justificado |
| Validación | No valida | Valida parcialmente | Valida según tipo | Valida con buen criterio y mensajes claros |
| Consultas | Incorrectas | Parciales | Correctas | Correctas y optimizadas |
| Comparativa MongoDB | Ausente | Básica | Clara | Clara y bien argumentada |
