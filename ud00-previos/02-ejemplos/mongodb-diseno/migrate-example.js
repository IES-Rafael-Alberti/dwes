// Ejemplo de migraciones idempotentes

use('migration_demo');

print('== Inicio migraciones ==');
db.dropDatabase();

db.createCollection('products');
db.products.insertMany([
  { name: 'Teclado mecánico', price: 79.99 },
  { name: 'Ratón inalámbrico', price: 29.99 },
  { name: 'Monitor 24 pulgadas', price: 149.99 }
]);

print('Estado inicial:');
printjson(db.products.find().toArray());
print('Count inicial: ' + db.products.countDocuments());

function slugify(name) {
  return name.toLowerCase().trim().replace(/\s+/g, '-');
}

function migration1Up() {
  print('Migration 1 up: añadir slug');
  const docs = db.products.find({ slug: { $exists: false } }).toArray();
  docs.forEach(doc => {
    db.products.updateOne(
      { _id: doc._id, slug: { $exists: false } },
      { $set: { slug: slugify(doc.name) } }
    );
  });
  print('Count tras migration 1 up: ' + db.products.countDocuments());
  printjson(db.products.find().toArray());
}

function migration1Down() {
  print('Migration 1 down: eliminar slug');
  db.products.updateMany({ slug: { $exists: true } }, { $unset: { slug: '' } });
  print('Count tras migration 1 down: ' + db.products.countDocuments());
  printjson(db.products.find().toArray());
}

function migration2Up() {
  print('Migration 2 up: añadir discountPrice');
  db.products.updateMany({ discountPrice: { $exists: false } }, { $set: { discountPrice: null } });
  print('Count tras migration 2 up: ' + db.products.countDocuments());
  printjson(db.products.find().toArray());
}

function migration2Down() {
  print('Migration 2 down: eliminar discountPrice');
  db.products.updateMany({ discountPrice: { $exists: true } }, { $unset: { discountPrice: '' } });
  print('Count tras migration 2 down: ' + db.products.countDocuments());
  printjson(db.products.find().toArray());
}

migration1Up();
migration1Up();
migration1Down();
migration2Up();
migration2Up();
migration2Down();

print('== Fin migraciones ==');
