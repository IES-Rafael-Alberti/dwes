# Cómo diseñar un esquema MongoDB

Esta guía explica un procedimiento que se puede aplicar a cualquier aplicación.
No consiste en empezar creando colecciones: primero se estudia cómo se usará la
información y después se decide cómo guardarla.

## 1. Identificar la carga de trabajo

Escribe las operaciones que la aplicación tendrá que realizar. Para cada una
indica si es lectura o escritura, con qué frecuencia se ejecuta y qué datos debe
devolver.

| Operación | Tipo | Frecuencia | Datos necesarios |
|---|---|---:|---|
| Consultar un libro por ISBN | Lectura | Muy alta | Libro y disponibilidad |
| Ver la ficha de un libro | Lectura | Alta | Libro, autor y categorías |
| Consultar préstamos de un usuario | Lectura | Media | Usuario y préstamos |
| Registrar un préstamo | Escritura | Media | Usuario, libro y fechas |
| Cerrar un préstamo | Escritura | Media | Préstamo y fecha de devolución |

No diseñes para entidades abstractas que nunca aparecen en una consulta. Diseña
para estas operaciones concretas.

## 2. Mapear las relaciones

Para cada relación decide si los datos se leen siempre juntos, cuánto pueden
crecer y si necesitan existir de forma independiente.

### Embebido

Guarda un documento dentro de otro cuando:

- se lee normalmente junto;
- tiene un tamaño limitado y predecible;
- pertenece al ciclo de vida del documento principal;
- se beneficia de una actualización atómica.

### Referencia

Guarda el identificador de otro documento cuando:

- la información se consulta por separado;
- puede crecer sin límite;
- se comparte entre muchos documentos;
- necesita un ciclo de vida independiente.

Una referencia manual no es una clave foránea. MongoDB no comprueba por sí solo
que el identificador apuntado exista ni elimina automáticamente las referencias
huérfanas.

## 3. Aplicar patrones

Solo después de conocer las consultas se aplican patrones como:

- subconjunto: guardar en el documento principal solo los datos más consultados;
- referencia extendida: copiar algunos datos de lectura frecuente;
- documento calculado: guardar un total que costaría calcular continuamente;
- bucket: agrupar muchos eventos en documentos de tamaño controlado;
- versionado de esquema: permitir durante una migración documentos antiguos y
  nuevos.

Un patrón no se elige por moda. Se justifica con una consulta, una escritura,
un tamaño o una restricción de rendimiento.

## 4. Crear índices

Cada consulta frecuente debe tener un índice razonado. Comprueba la consulta
con `explain("executionStats")` y revisa que el orden de un índice compuesto
coincida con sus filtros y ordenaciones.

```javascript
db.loans.createIndex({ userId: 1, returnedAt: 1 });
db.books.createIndex({ isbn: 1 }, { unique: true });

db.loans
  .find({ userId: ObjectId("000000000000000000000001"), returnedAt: null })
  .explain("executionStats");
```

Los índices también forman parte del diseño: mejoran lecturas, pero ocupan
espacio y hacen más costosas las escrituras.

## 5. Especificar las reglas que no cubre el esquema

Antes de programar, redacta las invariantes:

- un ISBN no puede repetirse;
- un préstamo debe apuntar a un usuario y a un libro existentes;
- la fecha de devolución no puede ser anterior a la de préstamo;
- un libro no puede prestarse dos veces simultáneamente;
- un usuario no puede superar el límite de préstamos activos.

Después asigna cada regla al lugar adecuado:

| Regla | Índice/validador | Aplicación | Transacción |
|---|---|---|---|
| ISBN único | Índice `unique` | También validar entrada | No siempre |
| Tipos y campos obligatorios | `$jsonSchema` | DTO y servicio | No |
| Usuario y libro existentes | No hay FK | Servicio/repositorio | Cuando varias escrituras deban ser una unidad |
| Límite de préstamos | No basta un índice | Servicio | Puede ser necesaria |

Esta tabla evita confundir un documento flexible con un documento sin reglas.

## 6. Ejemplo completo: biblioteca

El ejemplo se puede ejecutar con `mongosh` usando el script
[`ejemplo-biblioteca.js`](../../02-ejemplos/mongodb-diseno/ejemplo-biblioteca.js). El script reinicia la
base de datos de ejemplo, crea los índices, inserta documentos y muestra las
consultas principales. No lo ejecutes contra una base de datos real: contiene
`dropDatabase()`.

### Decisión de modelado

- `books` contiene la ficha del libro. El ISBN es único.
- `users` contiene los datos de cada usuario.
- `loans` es una colección independiente porque crece con el tiempo y se
  consulta por usuario, libro y estado.
- No se embeben todos los préstamos dentro de `users`: el array crecería sin
  límite.
- No se copian todos los datos del libro en cada préstamo; se guarda una
  referencia y, si hace falta para el histórico, una copia explícita del título
  en el momento del préstamo.

### Documentos resultantes

```javascript
db.books.insertOne({
  isbn: "978-0000000001",
  title: "Diseño de APIs",
  authors: ["Ada Ejemplo"],
  categories: ["web", "backend"],
  available: true
});

db.users.insertOne({
  username: "ana",
  name: "Ana Alumna",
  active: true
});

db.loans.insertOne({
  userId: ObjectId("000000000000000000000002"),
  bookId: ObjectId("000000000000000000000003"),
  titleAtLoan: "Diseño de APIs",
  loanedAt: ISODate("2026-09-10T09:00:00Z"),
  dueAt: ISODate("2026-09-24T09:00:00Z"),
  returnedAt: null
});
```

### Índices y consultas principales

```javascript
db.books.createIndex({ isbn: 1 }, { unique: true });
db.loans.createIndex({ userId: 1, returnedAt: 1 });
db.loans.createIndex({ bookId: 1, returnedAt: 1 });

// Préstamos activos de una persona.
db.loans.find({ userId: userId, returnedAt: null });

// Comprobar si un libro está prestado.
db.loans.findOne({ bookId: bookId, returnedAt: null });
```

### Conclusión del ejemplo

El diseño no se ha obtenido traduciendo tablas automáticamente. Se ha obtenido
de las consultas, la cardinalidad y el crecimiento esperado. La aplicación
deberá comprobar las referencias, los límites y las transiciones del préstamo.

## Lista de comprobación

- ¿He escrito las consultas y escrituras principales?
- ¿Sé qué datos se leen juntos?
- ¿He justificado cada documento embebido?
- ¿He controlado el crecimiento de arrays y documentos?
- ¿He identificado las referencias que no tienen FK?
- ¿Cada consulta importante tiene un índice razonado?
- ¿Sé qué reglas comprobará MongoDB y cuáles la aplicación?
- ¿Tengo ejemplos de documentos válidos y casos inválidos?
