# Ejercicio: benchmark Mongo vs PostgreSQL

## Objetivo
Comparar el rendimiento real de MongoDB frente a PostgreSQL JSONB en un caso de uso con datos semiestructurados.

## Dataset
- 100k productos
- 5-15 atributos variables por producto

## Queries a medir
1. Buscar productos con un atributo específico (existencia de clave).
2. Filtrar por valor de atributo (por ejemplo, `color = rojo`).
3. Rango numérico en atributo (por ejemplo, precio entre X e Y, usando columna generada en PostgreSQL).
4. Búsqueda de texto en el nombre del producto.
5. Productos que cumplan 2 condiciones en atributos diferentes.

## Métricas
- Tiempo medio de 10 ejecuciones
- Tamaño en disco
- Número de documentos examinados vs devueltos

## Plantilla de informe

| Query | MongoDB (ms) | PG JSONB (ms) | Ganador |
|-------|-------------|---------------|---------|
| 1 | ... | ... | ... |
| 2 | ... | ... | ... |
| 3 | ... | ... | ... |
| 4 | ... | ... | ... |
| 5 | ... | ... | ... |

## Ejemplo de base en PostgreSQL

```sql
CREATE TABLE products (
  id BIGSERIAL PRIMARY KEY,
  name TEXT NOT NULL,
  price NUMERIC(10,2) NOT NULL,
  attributes JSONB NOT NULL DEFAULT '{}'::jsonb,
  color TEXT GENERATED ALWAYS AS (attributes->>'color') STORED,
  created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_products_attributes_gin ON products USING GIN (attributes);
CREATE INDEX idx_products_color ON products (color);
CREATE INDEX idx_products_name ON products (name);
```

## Reflexión
Responder:

- ¿En qué casos gana cada uno?
- ¿Cuándo usarías cada tecnología?
- ¿Qué peso tienen los joins, la validación y la flexibilidad del esquema?

## Criterios de evaluación

Ver `rubrica.md` y `ra-ce.md` en este directorio para el desglose detallado de criterios y alineación con los RA/CE del módulo.

## Entregables

- Script SQL / JS `benchmark_setup` para generar los 100k registros y definir los índices en ambos motores.
- Informe de benchmark `benchmark.md` con la tabla comparativa rellena, el análisis técnico de explain, tamaño en disco y la reflexión crítica.
- Declaración de uso de IA cumplimentada (ver plantilla en `00-recursos-comunes/plantillas/`).

## Política de IA

| Aspecto | |
|---------|-|
| Uso de IA permitido | Sí, para generar los scripts generadores de datos aleatorios o estructurar la tabla del benchmark. |
| Declaración obligatoria | Sí. |
| Herramientas permitidas | ChatGPT, Claude, Gemini, GitHub Copilot. |
| Qué NO está permitido | Delegar el análisis de rendimiento de los planes de consulta (explain) o las conclusiones científicas a la IA sin un análisis manual y fundamentación propia. |

