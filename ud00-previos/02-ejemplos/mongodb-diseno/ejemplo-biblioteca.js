// Ejemplo guiado: diseño de una biblioteca con mongosh.
// Ejecutar con: mongosh ejemplo-biblioteca.js

use('biblioteca_db');
db.dropDatabase();

// La unicidad del ISBN sí puede garantizarla MongoDB con un índice.
db.books.createIndex({ isbn: 1 }, { unique: true });

// Los préstamos viven en una colección independiente porque crecen con el tiempo.
db.loans.createIndex({ userId: 1, returnedAt: 1 });
db.loans.createIndex({ bookId: 1, returnedAt: 1 });

const bookId = ObjectId();
const userId = ObjectId();

db.books.insertOne({
  _id: bookId,
  isbn: '978-0000000001',
  title: 'Diseño de APIs',
  authors: ['Ada Ejemplo'],
  categories: ['web', 'backend'],
  available: true
});

db.users.insertOne({
  _id: userId,
  username: 'ana',
  name: 'Ana Alumna',
  active: true
});

db.loans.insertOne({
  userId,
  bookId,
  titleAtLoan: 'Diseño de APIs',
  loanedAt: ISODate('2026-09-10T09:00:00Z'),
  dueAt: ISODate('2026-09-24T09:00:00Z'),
  returnedAt: null
});

print('Préstamos activos de la usuaria:');
printjson(db.loans.find({ userId, returnedAt: null }).toArray());

print('¿Está prestado el libro?');
printjson(db.loans.findOne({ bookId, returnedAt: null }));

print('Consulta e índice:');
printjson(db.loans
  .find({ userId, returnedAt: null })
  .explain('executionStats'));

// MongoDB no comprueba que userId y bookId existan en sus colecciones.
// Esa regla corresponde al servicio de la aplicación (y a sus pruebas).
