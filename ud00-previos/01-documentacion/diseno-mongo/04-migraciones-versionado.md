# Migraciones y versionado

## 1. El problema del schema en MongoDB

MongoDB no ofrece migraciones automáticas como Rails o Laravel. El schema es implícito, así que cualquier cambio estructural requiere scripts explícitos.

## 2. `mongosh` scripts

Las migraciones pueden escribirse como scripts JavaScript ejecutables con `mongosh`.

```javascript
// migrate-add-slug.js
db.posts.updateMany(
  { slug: { $exists: false } },
  [
    { $set: { slug: { $replaceAll: { input: "$title", find: " ", replacement: "-" } } } }
  ]
);
```

## 3. Herramientas de migración

### `migrate-mongo`

Herramienta habitual en ecosistema Node.js.

```bash
npm install -g migrate-mongo
migrate-mongo init
migrate-mongo create add-slug-to-posts
migrate-mongo up
migrate-mongo down
```

### `mongock`

Alternativa similar para Java/Spring Boot.

## 4. Migraciones idempotentes

Una migración bien hecha se puede ejecutar más de una vez sin romper nada.

```javascript
db.posts.updateMany(
  { slug: { $exists: false } },
  { $set: { slug: "pendiente" } }
);
```

## 5. Versionado de esquemas

Añadir `schemaVersion` permite saber qué versión tiene cada documento.

```javascript
db.posts.updateMany(
  { schemaVersion: { $exists: false } },
  { $set: { schemaVersion: 1 } }
);
```

## 6. Rollback

Toda migración debe tener `up()` y `down()`.

```javascript
// down: eliminar campo añadido
db.posts.updateMany({}, { $unset: { slug: "" } });
```

## 7. Ejemplo guiado

### Crear migración con `migrate-mongo`

```bash
migrate-mongo create add-slug-to-posts
```

### `up`

```javascript
module.exports = {
  async up(db) {
    await db.collection("posts").updateMany(
      { slug: { $exists: false } },
      [{ $set: { slug: { $toLower: { $replaceAll: { input: "$title", find: " ", replacement: "-" } } } } }]
    );
  },

  async down(db) {
    await db.collection("posts").updateMany({}, { $unset: { slug: "" } });
  }
};
```

### Índice único y sparse

```javascript
db.posts.createIndex({ slug: 1 }, { unique: true, sparse: true });
```

### Migrar `legacy_status` a `status`

```javascript
db.posts.updateMany(
  { legacy_status: { $exists: true } },
  [{ $set: { status: "$legacy_status" } }, { $unset: "legacy_status" }]
);
```

## 8. Ejercicio autónomo

Escribe 3 migraciones para e-commerce:

a. Añadir `discountPrice` a productos con valor por defecto `null`.
b. Añadir índice compuesto `{ category: 1, price: 1 }`.
c. Renombrar `shippingAddress` a `deliveryAddress` en órdenes.

Incluye rollback para cada una y documenta la prueba de `up` y `down`.
