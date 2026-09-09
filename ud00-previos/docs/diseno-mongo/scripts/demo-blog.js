// Demo de blog: validación, índices y consultas

use('blog_db');

print('== Inicio demo blog ==');
db.dropDatabase();

const postsValidator = {
  $jsonSchema: {
    bsonType: 'object',
    required: ['title', 'content', 'authorId', 'tags', 'createdAt', 'comments'],
    additionalProperties: false,
    properties: {
      title: { bsonType: 'string', minLength: 3 },
      content: { bsonType: 'string', minLength: 10 },
      authorId: { bsonType: 'objectId' },
      tags: { bsonType: 'array', maxItems: 10, items: { bsonType: 'string' } },
      createdAt: { bsonType: 'date' },
      publishedAt: { bsonType: ['date', 'null'] },
      expireAt: { bsonType: ['date', 'null'] },
      comments: {
        bsonType: 'array',
        items: {
          bsonType: 'object',
          required: ['authorName', 'text', 'createdAt'],
          additionalProperties: false,
          properties: {
            authorName: { bsonType: 'string', minLength: 2 },
            text: { bsonType: 'string', minLength: 1 },
            createdAt: { bsonType: 'date' }
          }
        }
      }
    }
  }
};

db.createCollection('posts', { validator: postsValidator });
db.posts.createIndex({ authorId: 1, createdAt: -1 });
db.posts.createIndex({ title: 'text', content: 'text' });
db.posts.createIndex({ expireAt: 1 }, { expireAfterSeconds: 0 });

print('Validador e índices creados:');
printjson(db.getCollectionInfos({ name: 'posts' }));

const authorId = ObjectId();
const otherAuthorId = ObjectId();

db.posts.insertMany([
  {
    title: 'Introducción a MongoDB',
    content: 'MongoDB permite modelar datos con flexibilidad y buen rendimiento para ciertos patrones de acceso.',
    authorId,
    tags: ['mongodb', 'modelado', 'bases-de-datos'],
    createdAt: new Date('2026-01-10T10:00:00Z'),
    publishedAt: new Date('2026-01-10T11:00:00Z'),
    expireAt: new Date('2028-01-10T11:00:00Z'),
    comments: [
      { authorName: 'Ana', text: 'Muy claro.', createdAt: new Date('2026-01-10T12:00:00Z') },
      { authorName: 'Luis', text: 'Buen ejemplo.', createdAt: new Date('2026-01-10T13:00:00Z') }
    ]
  },
  {
    title: 'Índices en MongoDB',
    content: 'Los índices ayudan a acelerar consultas, pero deben elegirse según el patrón real de acceso.',
    authorId: otherAuthorId,
    tags: ['mongodb', 'indices'],
    createdAt: new Date('2026-02-05T09:30:00Z'),
    publishedAt: new Date('2026-02-05T10:00:00Z'),
    expireAt: new Date('2028-02-05T10:00:00Z'),
    comments: [
      { authorName: 'Marta', text: 'Me sirve para el examen.', createdAt: new Date('2026-02-05T11:00:00Z') }
    ]
  }
]);

print('Posts insertados:');
printjson(db.posts.find().toArray());

print('Consulta por authorId:');
const postsByAuthor = db.posts.find({ authorId }).sort({ createdAt: -1 }).toArray();
printjson(postsByAuthor);

print('Consulta por tag:');
const postsByTag = db.posts.find({ tags: 'mongodb' }).toArray();
printjson(postsByTag);

print('Explain de consulta por autor:');
const explain = db.posts.find({ authorId }).sort({ createdAt: -1 }).explain('executionStats');
printjson({
  totalDocsExamined: explain.executionStats.totalDocsExamined,
  totalKeysExamined: explain.executionStats.totalKeysExamined,
  executionTimeMillis: explain.executionStats.executionTimeMillis
});

print('== Fin demo blog ==');
