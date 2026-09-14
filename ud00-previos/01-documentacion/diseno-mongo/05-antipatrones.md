# Anti-patrones en MongoDB y cómo evitarlos

Los anti-patrones son prácticas de diseño que parecen funcionar al principio pero
acaban generando problemas de rendimiento, mantenimiento o integridad.

| # | Anti-patrón | Descripción | Síntoma | Solución |
|---|-------------|-------------|---------|----------|
| 1 | **Array masivo** | Arrays embebidos sin límite que crecen sin control (comentarios, logs, eventos) | Documentos que se acercan al límite de 16 MB; writes lentos por realojamiento | Colección separada o patrón subconjunto |
| 2 | **Documento inflado** | Documentos con 100+ campos, incluyendo datos que nunca se consultan juntos | `$project` en TODAS las queries para excluir campos | Dividir en colecciones con sentido |
| 3 | **El SQL que no fue** | Usar `$lookup` como JOIN obligatorio; colecciones normalizadas al extremo | Múltiples `$lookup` por consulta, rendimiento pobre | Embeber cuando el acceso lo pida |
| 4 | **Número masivo de colecciones** | Crear una colección por entidad (como en SQL) sin considerar acceso | Decenas de colecciones con pocos documentos cada una; joins constantes | Consolidar esquemas afines |
| 5 | **Índices innecesarios** | Índices que rara vez se usan o son redundantes | Consumen memoria RAM; ralentizan escrituras | Revisar y eliminar periódicamente con `$indexStats` |
| 6 | **Schema de silencio** | Sin validación ni índices porque "MongoDB es schemaless" | Datos inconsistentes, queries lentísimas, errores en producción | `$jsonSchema` + índices estratégicos |
| 7 | **Documento multiuso** | Un mismo documento intenta servir para lectura, escritura, reportes y búsqueda | 30-40 campos de los cuales 20 son opcionales y rara vez usados | Diseñar por patrón de acceso |
| 8 | **Sin relaciones explícitas** | No usar ObjectId de referencia, solo strings/nombres embebidos | Cambiar el nombre de un autor requiere actualizar miles de posts | ObjectId + denormalización controlada |
| 9 | **Falta de versionado de esquema** | No incluir `schemaVersion` ni migraciones; el esquema cambia sin control | Código que rompe al encontrar documentos con estructura antigua | Campo `schemaVersion` + migraciones idempotentes |

## Tabla resumen

| Anti-patrón | ¿Lo tienes? | Solución rápida |
|-------------|-------------|-----------------|
| Array masivo | `db.collection.find({ $expr: { $gt: [{ $size: "$array" }, 500] } })` | Pasar a colección separada |
| Documento inflado | `Object.bsonsize(doc)` > 4 MB | Dividir en colecciones |
| SQL mental | `$lookup` en > 30% de consultas | Revisar embebidos |
| Muchas colecciones | > 20 colecciones para 5 entidades lógicas | Consolidar |
| Índices innecesarios | `db.collection.aggregate([ { $indexStats: {} } ])` con 0 usos | Drop |
| Schema silencio | Sin `validator` en `db.getCollectionInfos()` | Añadir `$jsonSchema` |
| Sin versionado | Sin `schemaVersion` en documentos | Añadir campo + migración |

## Ejercicio

Refactoriza este esquema que acumula **múltiples anti-patrones**:

```javascript
{
  _id: ObjectId(),
  nombre: "Usuario 1",
  email: "u1@demo.com",
  direccionCompleta: "Calle Falsa 123, Ciudad, País, 28001, (junto al parque)", // TODO: separar
  pedidos: [/* cientos de items con datos completos de producto */],
  comentarios: [/* miles, cada uno con texto enorme */],
  productosFavoritos: [/* sin límite, algunos ya no existen */],
  logsActividad: [/* array infinito */],
  reporteAnual: { /* datos agregados embebidos aquí contra todo sentido */ },
  lastLogin: ISODate("2023-01-01"),
  createdAt: ISODate("2020-01-01"),
  updatedAt: ISODate("2023-01-01")
}
```

### Tareas
1. Identifica qué anti-patrones tiene (pon el número de la tabla)
2. Propón un diseño refactorizado con decisiones justificadas
3. ¿Qué patrón(es) positivo(s) aplicarías? (subconjunto, bucket, calculado...)
