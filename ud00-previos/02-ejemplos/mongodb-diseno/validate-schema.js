// Demo de validación: warn, error, anidados y additionalProperties

use('validation_demo');

print('== Inicio validación ==');
db.dropDatabase();

print('1) Modo warn');
db.createCollection('warn_users', {
  validator: {
    $jsonSchema: {
      bsonType: 'object',
      required: ['name', 'email'],
      properties: {
        name: { bsonType: 'string' },
        email: { bsonType: 'string' }
      }
    }
  },
  validationAction: 'warn',
  validationLevel: 'strict'
});

const badWarnDoc = { name: 123, email: 'demo@example.com' };
print('Insertando documento incorrecto en warn_users (debe entrar con advertencia):');
printjson(db.warn_users.insertOne(badWarnDoc));
print('Documento almacenado:');
printjson(db.warn_users.findOne({ email: 'demo@example.com' }));

print('2) Cambio a modo error');
db.runCommand({
  collMod: 'warn_users',
  validator: {
    $jsonSchema: {
      bsonType: 'object',
      required: ['name', 'email'],
      properties: {
        name: { bsonType: 'string' },
        email: { bsonType: 'string' }
      }
    }
  },
  validationAction: 'error',
  validationLevel: 'strict'
});

print('Intentando insertar un documento incorrecto con error:');
try {
  printjson(db.warn_users.insertOne({ name: 999, email: 'fail@example.com' }));
} catch (e) {
  print('Error esperado: ' + e.message);
}

print('3) Esquema anidado');
db.createCollection('orders_nested', {
  validator: {
    $jsonSchema: {
      bsonType: 'object',
      required: ['customer', 'items'],
      properties: {
        customer: {
          bsonType: 'object',
          required: ['name', 'address'],
          properties: {
            name: { bsonType: 'string' },
            address: {
              bsonType: 'object',
              required: ['city', 'zip'],
              properties: {
                city: { bsonType: 'string' },
                zip: { bsonType: 'string' }
              }
            }
          }
        },
        items: {
          bsonType: 'array',
          minItems: 1,
          items: {
            bsonType: 'object',
            required: ['sku', 'qty'],
            properties: {
              sku: { bsonType: 'string' },
              qty: { bsonType: 'int', minimum: 1 }
            }
          }
        }
      }
    }
  }
});

print('Insertando documento válido anidado:');
printjson(db.orders_nested.insertOne({
  customer: { name: 'Laura', address: { city: 'Madrid', zip: '28001' } },
  items: [{ sku: 'A-1', qty: 2 }]
}));

print('4) additionalProperties: false');
db.createCollection('strict_profiles', {
  validator: {
    $jsonSchema: {
      bsonType: 'object',
      required: ['username', 'role'],
      additionalProperties: false,
      properties: {
        username: { bsonType: 'string' },
        role: { bsonType: 'string' }
      }
    }
  },
  validationAction: 'error'
});

try {
  printjson(db.strict_profiles.insertOne({ username: 'maria', role: 'admin', extra: true }));
} catch (e) {
  print('Bloqueado por additionalProperties: ' + e.message);
}

print('== Fin validación ==');
