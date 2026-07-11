# Sesión 3: Comparativa Mongo vs PostgreSQL JSONB

## 1. Cuándo elegir cada uno

| Dimensión | MongoDB | PostgreSQL JSONB |
|-----------|---------|-----------------|
| Modelo nativo | Documentos | Relacional + documentos |
| Esquema | Flexible (schemaless) | Híbrido (relacional + JSON) |
| ACID transactions | Desde v4.0 (multi-doc) | Nativo, maduro |
| Joins | `$lookup` (lento) | JOIN nativo (rápido) |
| Integridad referencial | No nativa (en app) | FK constraints |
| Indexación JSONB | Índices nativos (múltiples tipos) | GIN, GIST, BTREE sobre generated columns |
| Replicación | Native replica sets | Streaming replication |
| Sharding | Nativo (shard key) | Declarative partitioning |
| Text search | Text indexes (bueno) | GIN + tsvector (muy bueno) |
| Geoespacial | Excelente (GeoJSON, 2dsphere) | PostGIS (el mejor, no nativo PG) |
| Herramientas | mongosh, Compass, Atlas | pgAdmin, DBeaver, psql |
| Madurez | 15+ años, probado | 30+ años, ultra probado |

## 2. Benchmark práctico

Comparar el mismo caso en ambos sistemas:

- 100k productos con atributos variados
- consultas equivalentes
- medición con `EXPLAIN ANALYZE` y `.explain()`

Variables a observar:

- latencia
- tamaño en disco
- complejidad de la consulta

## 3. El caso híbrido

```sql
CREATE TABLE events (
  id UUID PRIMARY KEY,
  event_type TEXT NOT NULL,
  user_id INTEGER REFERENCES users(id),
  data JSONB NOT NULL,
  created_at TIMESTAMPTZ DEFAULT NOW()
);
```

MongoDB solo para lo que realmente necesita ser documento: logs, sesiones o catálogos con atributos ultra variables.

## 4. Regla de decisión práctica

```text
¿Los datos tienen estructura fija y relaciones? → PostgreSQL relacional
¿Los datos son variables pero con algunos campos fijos y relaciones? → PostgreSQL híbrido (columnas + JSONB)
¿Los datos son completamente variables, sin relaciones, alta escalada escritura? → MongoDB
¿Necesito transacciones complejas + datos variables? → PostgreSQL
¿Necesito geoespacial complejo? → PostgreSQL + PostGIS
```

## 5. Ejercicio autónomo (mini-proyecto)

1. Elige un dominio: blog, e-commerce o incidencias.
2. Implementa el mismo modelo en MongoDB y en PostgreSQL (relacional + JSONB).
3. Inserta 10k registros de prueba en cada uno.
4. Ejecuta 3 queries equivalentes y mide con `EXPLAIN ANALYZE` / `.explain()`.
5. Redacta una comparativa de 1 página con tablas de tiempos, tamaño y reflexión.

### Rúbrica de evaluación
| Criterio | Insuficiente | Correcto | Excelente |
|----------|--------------|----------|-----------|
| Comparación técnica | Superficial | Compara los puntos clave | Argumenta con precisión y criterio |
| Benchmark | Incompleto | Mide lo solicitado | Presenta datos consistentes y claros |
| Conclusiones | Sin justificación | Conclusión válida | Conclusión bien fundamentada |
| Uso de SQL / Mongo | Incorrecto | Correcto | Correcto y optimizado |
