# Validación e índices en MongoDB

## 1. Por qué validar en BD

Confiar solo en el driver o en la aplicación es un error. El driver no valida por sí mismo, la aplicación puede cambiar, y los datos malos entran igual si alguien escribe directamente en la base de datos. La base de datos es la última línea de defensa.

## 2. `$jsonSchema` validator

La validación nativa de MongoDB se define con `collMod` y `validator: { $jsonSchema: { ... } }`.

### Sintaxis básica

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
        createdAt: { bsonType: "date" }
      },
      additionalProperties: false
    }
  },
  validationAction: "error",
  validationLevel: "strict"
});
```

### Claves de validación

- `bsonType`: tipo BSON esperado.
- `properties`: definición de campos.
- `required`: campos obligatorios.
- `additionalProperties: false`: rechaza campos no definidos.
- `enum`: valores permitidos.
- `pattern`: expresión regular para strings.
- `minimum` / `maximum`: límites numéricos.
- `minLength` / `maxLength`: límites de longitud.

### Anidación

```javascript
db.runCommand({
  collMod: "orders",
  validator: {
    $jsonSchema: {
      bsonType: "object",
      required: ["items", "shippingAddress"],
      properties: {
        shippingAddress: {
          bsonType: "object",
          required: ["street", "city", "zip"],
          properties: {
            street: { bsonType: "string" },
            city: { bsonType: "string" },
            zip: { bsonType: "string", pattern: "^[0-9]{5}$" }
          }
        }
      }
    }
  }
});
```

### Modo de validación

- **`validationAction`**: `"error"` (rechaza el documento, opción segura) | `"warn"` (permite el documento pero lo loguea como advertencia)
- **`validationLevel`**: `"strict"` (aplica a todos los documentos) | `"moderate"` (solo a documentos nuevos o modificados)

#### Cuándo usar `warn`

`validationAction: "warn"` es útil cuando ya tienes datos en una colección y quieres migrar el esquema sin romper la aplicación. Los documentos que no cumplan se registrarán en los logs pero no se bloquearán, permitiendo una migración gradual:

```javascript
// Fase 1: validar sin bloquear para ver cuántos docs incumplen
db.runCommand({
  collMod: "posts",
  validator: { $jsonSchema: { ... } },
  validationAction: "warn",
  validationLevel: "moderate"  // solo nuevos/modificados
});

// Revisar logs de mongod para ver qué documentos fallan
// ...corregir datos manualmente o con script...

// Fase 2: cuando los datos estén limpios, pasar a error
db.runCommand({
  collMod: "posts",
  validationAction: "error",
  validationLevel: "strict"
});
```

Esto evita el *downtime*: pones el validador en `warn`, limpias los datos gradualmente, y solo cuando estás seguro pasas a `error`.

## 3. Índices en MongoDB

Los índices aceleran consultas, pero también ocupan espacio y penalizan escrituras. Hay que elegirlos con criterio.

### Tipos principales

- **Simple**: un campo.
- **Compuesto**: varios campos.
- **TTL**: expiración automática.
- **Partial index**: solo indexa documentos que cumplen condición.
- **Sparse**: omite documentos sin el campo.
- **Text index**: búsqueda textual.

### Orden en índices compuestos: ESR

**E**quality → **S**ort → **R**ange.

Primero los campos usados con igualdad, luego los de ordenación, y al final los de rango.

```javascript
db.posts.createIndex({ authorId: 1, createdAt: -1 });
```

### TTL

```javascript
db.posts.createIndex(
  { createdAt: 1 },
  { expireAfterSeconds: 60 * 60 * 24 * 30 }
);
```

### Partial index

```javascript
db.posts.createIndex(
  { authorId: 1, createdAt: -1 },
  { partialFilterExpression: { status: "published" } }
);
```

### Sparse

```javascript
db.users.createIndex({ phone: 1 }, { sparse: true });
```

### Text

```javascript
db.posts.createIndex({ title: "text", content: "text" });
```

## 4. `explain()`

`explain()` sirve para comprobar si MongoDB usa índice o hace colección completa.

- `IXSCAN`: usa índice.
- `COLLSCAN`: recorre toda la colección.
- `docs examined` debe acercarse a `docs returned`.

```javascript
db.posts.find({ authorId: ObjectId("64f1a1a1a1a1a1a1a1a1a1a1") })
  .sort({ createdAt: -1 })
  .explain("executionStats");
```

## 5. Ejemplo guiado: colección `posts`

### Validator

```javascript
db.runCommand({
  collMod: "posts",
  validator: {
    $jsonSchema: {
      bsonType: "object",
      required: ["title", "content", "authorId", "tags", "createdAt"],
      properties: {
        title: { bsonType: "string", minLength: 1 },
        content: { bsonType: "string" },
        authorId: { bsonType: "objectId" },
        tags: {
          bsonType: "array",
          maxItems: 10,
          items: { bsonType: "string" }
        },
        createdAt: { bsonType: "date" }
      },
      additionalProperties: false
    }
  },
  validationAction: "error",
  validationLevel: "strict"
});
```

### Índices

```javascript
db.posts.createIndex({ authorId: 1, createdAt: -1 });
db.posts.createIndex({ createdAt: 1 }, { expireAfterSeconds: 60 * 60 * 24 * 30, partialFilterExpression: { status: "draft" } });
```

### `explain()`

```javascript
db.posts.find({ authorId: ObjectId("64f1a1a1a1a1a1a1a1a1a1a1") })
  .sort({ createdAt: -1 })
  .explain("executionStats");
```

## 6. Ejercicio autónomo

Sobre el diseño de e-commerce de la sesión anterior:

1. Escribe el validator completo para `products` u `orders`.
2. Añade al menos 3 índices bien justificados.
3. Incluye un índice TTL y un partial index.
4. Ejecuta `explain()` en 2 consultas y documenta el resultado.
