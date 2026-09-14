# Sesión 2: Operaciones avanzadas

## 1. Modificación de documentos JSONB

```sql
SELECT jsonb_set('{"profile": {"name": "Ana"}}'::jsonb,
                 '{profile,role}',
                 '"admin"'::jsonb,
                 true);

SELECT jsonb_insert('[1,2,3]'::jsonb, '{1}', '99'::jsonb, false);

SELECT '{"a": 1, "b": 2}'::jsonb - 'b';

SELECT '{"a": 1}'::jsonb || '{"b": 2}'::jsonb;

SELECT jsonb_build_object('name', 'Laptop', 'price', 1200);
SELECT jsonb_build_array('sql', 'jsonb', 'postgres');
SELECT jsonb_agg(name) FROM products;
SELECT jsonb_object_agg(category, count(*)) FROM products GROUP BY category;
```

## 2. JsonPath queries (PG12+)

```sql
SELECT jsonb_path_exists(data, '$.store.book[*].author')
FROM catalog;

SELECT jsonb_path_query(data, '$.book[*].price ? (@ < 10)')
FROM catalog;

SELECT jsonb_path_query_array(data, '$.store.book[*]')
FROM catalog;

SELECT jsonb_path_match(data, '$.store.book[*].price < 10')
FROM catalog;
```

Ventajas frente a `@>` y `->>`: permiten filtros más complejos, wildcards y condiciones dentro del propio path.

## 3. Triggers para validación de JSONB

```sql
CREATE OR REPLACE FUNCTION validate_metadata()
RETURNS TRIGGER AS $$
BEGIN
  IF NOT (NEW.metadata ? 'version') OR
     NOT (NEW.metadata->>'version' ~ '^\d+\.\d+\.\d+$') THEN
    RAISE EXCEPTION 'metadata.version debe ser semver (X.Y.Z)';
  END IF;

  IF (NEW.metadata ? 'priority') AND
     NOT (jsonb_typeof(NEW.metadata->'priority') = 'number') THEN
    RAISE EXCEPTION 'metadata.priority debe ser número';
  END IF;

  RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_validate_metadata
  BEFORE INSERT OR UPDATE ON orders
  FOR EACH ROW
  WHEN (NEW.metadata IS DISTINCT FROM OLD.metadata OR OLD IS NULL)
  EXECUTE FUNCTION validate_metadata();
```

## 4. Vistas materializadas con JSONB

Cuando necesitas extraer datos JSONB frecuentemente, una vista materializada puede reducir coste repetido.

```sql
CREATE MATERIALIZED VIEW product_search_view AS
SELECT id,
       name,
       price,
       attributes->>'color' AS color,
       (attributes->>'weight')::numeric AS weight
FROM products;

CREATE INDEX ON product_search_view (color);
```

Refresco:

```sql
REFRESH MATERIALIZED VIEW CONCURRENTLY product_search_view;
```

## 5. JSONB vs tablas de atributos (EAV anti-patrón)

EAV (entity-attribute-value) es un anti-patrón clásico cuando se usa para evitar modelar bien.

| Enfoque | Ventajas | Problemas |
|--------|----------|-----------|
| EAV | Muy flexible | Consultas complejas, joins costosos, validación pobre |
| JSONB | Flexible y más simple | Menos tipado nativo por campo |
| Columnas separadas | Mejor rendimiento e integridad | Menos flexible |

## 6. Ejercicio autónomo

Partiendo del esquema `products` de la sesión 1:

1. Escribe un trigger que valide que los productos de categoría `Electrónica` tengan `voltaje` (string) y `consumo_watts` (number).
2. Usa `jsonb_set` para añadir `updated_at` en `attributes` de un producto existente.
3. Crea una vista materializada `product_search_view` con los campos más consultados.
4. Escribe una query con `jsonb_path_query` para encontrar productos con cualquier atributo cuyo valor numérico sea > 100.

### Entregable
Script SQL con triggers, vistas y path queries.

### Rúbrica de evaluación
| Criterio | Insuficiente | Correcto | Excelente |
|----------|--------------|----------|-----------|
| Trigger | No valida o falla | Valida parcialmente | Valida con mensajes claros |
| JSONB updates | No usa funciones correctas | Actualiza correctamente | Usa funciones con criterio |
| Vista materializada | No creada | Creada y funcional | Bien indexada y justificada |
| JsonPath | No resuelve | Resuelve lo básico | Usa filtros complejos correctamente |
