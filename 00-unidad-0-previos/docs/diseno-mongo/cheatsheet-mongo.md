# Cheatsheet MongoDB — Modelado de datos

## Operadores de query

| Operador | Significado | Ejemplo |
|----------|-------------|---------|
| `$eq` | Igual a | `{ price: { $eq: 10 } }` |
| `$ne` | Distinto de | `{ status: { $ne: "deleted" } }` |
| `$gt` / `$gte` | Mayor que / Mayor o igual | `{ price: { $gt: 50 } }` |
| `$lt` / `$lte` | Menor que / Menor o igual | `{ price: { $lte: 100 } }` |
| `$in` / `$nin` | Dentro de / No dentro de | `{ status: { $in: ["a", "b"] } }` |
| `$exists` | Existe el campo | `{ slug: { $exists: false } }` |
| `$regex` | Expresión regular | `{ name: { $regex: /^Mongo/i } }` |
| `$elemMatch` | Condición en array | `{ tags: { $elemMatch: { $eq: "mongodb" } } }` |
| `$text` | Búsqueda de texto | `{ $text: { $search: "mongodb modelado" } }` |

## Operadores de array

| Operador | Uso |
|----------|-----|
| `$push` | Añadir al array |
| `$pull` | Eliminar del array |
| `$addToSet` | Añadir si no existe |
| `$each` | Con `$push` para añadir varios |
| `$slice` | Limitar tamaño del array |
| `$size` | Condición: longitud del array (`{ tags: { $size: 3 } }`) |

## Operadores de actualización

| Operador | Uso | Ejemplo |
|----------|-----|---------|
| `$set` | Establecer campo | `{ $set: { status: "published" } }` |
| `$unset` | Eliminar campo | `{ $unset: { legacyField: "" } }` |
| `$inc` | Incrementar numérico | `{ $inc: { views: 1 } }` |
| `$rename` | Renombrar campo | `{ $rename: { oldName: "newName" } }` |
| `$min` / `$max` | Actualizar si menor/mayor | `{ $min: { price: 10 } }` |
| `$mul` | Multiplicar | `{ $mul: { price: 1.1 } }` |

## Proyección (qué campos devolver)

| Sintaxis | Efecto |
|----------|--------|
| `{ title: 1, content: 1 }` | Solo esos campos |
| `{ comments: 0 }` | Excluir campo |
| `{ "comments.text": 1 }` | Campo anidado |
| `{ tags: { $slice: 3 } }` | Primeros 3 de un array |
| `{ tags: { $elemMatch: { $eq: "mongo" } } }` | Solo elemento que cumple |

## Etapas del aggregation pipeline

| Etapa | Uso |
|-------|-----|
| `$match` | Filtrar documentos (como find) |
| `$group` | Agrupar por campo, calcular agregados |
| `$sort` | Ordenar (`1` asc, `-1` desc) |
| `$project` | Proyectar campos, renombrar, calcular |
| `$lookup` | JOIN con otra colección |
| `$unwind` | Desanidar arrays (un elemento por doc) |
| `$bucket` | Agrupar en rangos |
| `$addFields` | Añadir campos calculados |
| `$out` | Escribir resultado en colección |

```javascript
// Ejemplo típico
db.orders.aggregate([
  { $match: { status: "shipped" } },
  { $group: { _id: "$customerId", total: { $sum: "$total" } } },
  { $sort: { total: -1 } },
  { $limit: 10 }
]);
```

## Índices

| Tipo | Creación | Uso |
|------|----------|-----|
| Simple | `{ name: 1 }` | Un solo campo |
| Compuesto | `{ authorId: 1, createdAt: -1 }` | Varios campos, orden importa |
| Text | `{ title: "text", content: "text" }` | Búsqueda de texto |
| TTL | `{ expireAt: 1 }, { expireAfterSeconds: 0 }` | Expiración automática |
| Partial | `{ status: 1 }, { partialFilterExpression: { status: "active" } }` | Solo indexa ciertos docs |
| Sparse | `{ slug: 1 }, { sparse: true }` | Solo indexa docs con ese campo |
| GIN (PG) | `USING GIN (metadata jsonb_path_ops)` | Índice para JSON en Postgres |

### ESR Rule (orden en índices compuestos)

```
E (Equality) → S (Sort) → R (Range)
```

Query: `db.posts.find({ authorId: X }).sort({ createdAt: -1 }).limit(10)`
Índice: `{ authorId: 1, createdAt: -1 }`  ← Equality en authorId, Sort en createdAt

## Validación con `$jsonSchema`

```javascript
db.runCommand({
  collMod: "posts",
  validator: {
    $jsonSchema: {
      bsonType: "object",
      required: ["title", "content", "authorId", "createdAt"],
      properties: {
        title: { bsonType: "string", minLength: 1 },
        content: { bsonType: "string" },
        authorId: { bsonType: "objectId" },
        status: { enum: ["draft", "published", "archived"] },
        tags: {
          bsonType: "array",
          maxItems: 10,
          uniqueItems: true,
          items: { bsonType: "string" }
        },
        comments: {
          bsonType: "array",
          items: {
            bsonType: "object",
            required: ["authorName", "text"],
            properties: {
              authorName: { bsonType: "string" },
              text: { bsonType: "string", maxLength: 2000 }
            }
          }
        }
      },
      additionalProperties: false
    }
  },
  validationAction: "error",  // o "warn" para migración gradual
  validationLevel: "strict"    // o "moderate" solo nuevos docs
});
```

| Propiedad | Uso |
|-----------|-----|
| `bsonType` | Tipo de datos: `object`, `string`, `int`, `double`, `date`, `array`, `objectId`, `bool` |
| `required` | Campos obligatorios |
| `enum` | Valores permitidos |
| `pattern` | Regex para strings |
| `minimum` / `maximum` | Límites numéricos |
| `minLength` / `maxLength` | Longitud de string |
| `minItems` / `maxItems` | Tamaño de array |
| `uniqueItems` | Array sin duplicados |
| `additionalProperties` | `false` = no permite campos extra |

## Comandos útiles

```javascript
// Diagnóstico
db.collection.stats()                      // Tamaño, documentos, índices
db.collection.getIndexes()                 // Listar índices
db.collection.explain("executionStats")    // Plan de ejecución
bsonsize(db.collection.findOne())          // Tamaño de un documento en bytes
db.collection.dataSize()                   // Tamaño total de datos
db.collection.totalIndexSize()             // Tamaño de índices

// Administración
db.createUser({ user: "...", pwd: "...", roles: [...] })
db.dropDatabase()                          // ¡Cuidado!
db.collection.dropIndex({ campo: 1 })      // Eliminar índice

// Migraciones (idempotentes)
db.collection.updateMany(
  { slug: { $exists: false } },
  [{ $set: { slug: { $toLower: { $replaceAll: { input: "$title", find: " ", replacement: "-" } } } } }]
);
```

## Preguntas guía para diseñar en MongoDB

```
1. ¿Qué datos leo SIEMPRE juntos?          → Embed
2. ¿Qué datos leo CASI SIEMPRE juntos?     → Embed (o extended reference)
3. ¿Este array puede CRECER SIN LÍMITE?    → Reference (colección separada)
4. ¿Necesito ACTUALIZACIÓN ATÓMICA?        → Embed
5. ¿Son datos VARIABLES por documento?     → Embed (atributo polimórfico)
6. ¿Los consulto POR SEPARADO a menudo?    → Reference
7. ¿Solo necesito UNA PARTE del doc?       → Patrón subconjunto
8. ¿MUCHAS LECTURAS, pocas escrituras?     → Patrón calculado
9. ¿SERIES TEMPORALES o IoT?               → Patrón bucket
10. ¿Solo el 1% de docs son GIGANTES?      → Patrón atípico (outlier)
```

## Recursos

- [M320 Data Modeling (MongoDB University)](https://learn.mongodb.com/courses/data-modeling)
- [Building with Patterns (blog oficial)](https://www.mongodb.com/blog/post/building-with-patterns-a-summary)
- [Schema Design Anti-Patterns (blog oficial)](https://www.mongodb.com/developer/products/mongodb/schema-design-anti-pattern-summary/)
- [Aitor Medrano — Modelado documental](https://aitor-medrano.github.io/iabd/sa/modelado.html)
