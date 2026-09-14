# Guía Completa de Comandos MongoDB

## Índice
1. [Gestión de Bases de Datos](#gestion-de-bases-de-datos)
2. [Gestión de Colecciones](#gestion-de-colecciones)
3. [Operaciones CRUD](#operaciones-crud)
4. [Consultas Avanzadas](#consultas-avanzadas)
5. [Índices](#indices)
6. [Agregaciones](#agregaciones)
7. [Administración](#administracion)
8. [Usuarios y Roles](#usuarios-y-roles)

## Gestión de Bases de Datos

### Bases de Datos
```javascript
// Mostrar bases de datos
show dbs

// Usar/crear base de datos
use nombreDB

// Eliminar base de datos actual
db.dropDatabase()

// Ver base de datos actual
db.getName()
```

## Gestión de Colecciones

### Operaciones Básicas
```javascript
// Crear colección
db.createCollection("nombreColeccion")

// Listar colecciones
show collections

// Eliminar colección
db.nombreColeccion.drop()

// Renombrar colección
db.nombreColeccion.renameCollection("nuevoNombre")
```

## Operaciones CRUD

### Create (Insertar)
```javascript
// Insertar un documento
db.collection.insertOne({
    campo1: "valor1",
    campo2: "valor2"
})

// Insertar varios documentos
db.collection.insertMany([
    { campo1: "valor1" },
    { campo1: "valor2" }
])

// Insertar o actualizar (upsert)
db.collection.update(
    { campo: "valor" },
    { $set: { campo: "nuevoValor" }},
    { upsert: true }
)
```

### Read (Consultar)
```javascript
// Encontrar todos los documentos
db.collection.find()

// Encontrar con condición
db.collection.find({ campo: "valor" })

// Encontrar uno
db.collection.findOne({ campo: "valor" })

// Contar documentos
db.collection.countDocuments({ campo: "valor" })

// Limitar resultados
db.collection.find().limit(5)

// Saltar resultados (paginación)
db.collection.find().skip(10)

// Ordenar resultados
db.collection.find().sort({ campo: 1 }) // 1 ascendente, -1 descendente
```

### Update (Actualizar)
```javascript
// Actualizar un documento
db.collection.updateOne(
    { campo: "valor" },
    { $set: { campo2: "nuevoValor" }}
)

// Actualizar varios documentos
db.collection.updateMany(
    { campo: "valor" },
    { $set: { campo2: "nuevoValor" }}
)

// Incrementar valor
db.collection.updateOne(
    { campo: "valor" },
    { $inc: { contador: 1 }}
)

// Actualizar arrays
db.collection.updateOne(
    { campo: "valor" },
    { $push: { array: "nuevoElemento" }}
)
```

### Delete (Eliminar)
```javascript
// Eliminar un documento
db.collection.deleteOne({ campo: "valor" })

// Eliminar varios documentos
db.collection.deleteMany({ campo: "valor" })

// Eliminar todos los documentos
db.collection.deleteMany({})
```

## Consultas Avanzadas

### Operadores de Comparación
```javascript
// Mayor que
db.collection.find({ campo: { $gt: valor }})

// Mayor o igual que
db.collection.find({ campo: { $gte: valor }})

// Menor que
db.collection.find({ campo: { $lt: valor }})

// Menor o igual que
db.collection.find({ campo: { $lte: valor }})

// No igual
db.collection.find({ campo: { $ne: valor }})

// En array de valores
db.collection.find({ campo: { $in: [valor1, valor2] }})

// No en array de valores
db.collection.find({ campo: { $nin: [valor1, valor2] }})
```

### Operadores Lógicos
```javascript
// AND
db.collection.find({
    $and: [
        { campo1: "valor1" },
        { campo2: "valor2" }
    ]
})

// OR
db.collection.find({
    $or: [
        { campo1: "valor1" },
        { campo2: "valor2" }
    ]
})

// NOT
db.collection.find({
    campo: { $not: { $eq: "valor" }}
})
```

### Operadores de Array
```javascript
// Array contiene todos los elementos
db.collection.find({ array: { $all: [elem1, elem2] }})

// Array tiene tamaño específico
db.collection.find({ array: { $size: 3 }})

// Elemento existe en array
db.collection.find({ array: elemento })

// Condición en elemento de array
db.collection.find({ "array.campo": valor })
```

## Índices

### Gestión de Índices
```javascript
// Crear índice
db.collection.createIndex({ campo: 1 })

// Crear índice único
db.collection.createIndex({ campo: 1 }, { unique: true })

// Listar índices
db.collection.getIndexes()

// Eliminar índice
db.collection.dropIndex("nombreIndice")

// Eliminar todos los índices
db.collection.dropIndexes()
```

## Agregaciones

### Pipeline de Agregación
```javascript
// Agregación básica
db.collection.aggregate([
    { $match: { campo: "valor" }},
    { $group: {
        _id: "$campo",
        total: { $sum: 1 }
    }}
])

// Etapas comunes
db.collection.aggregate([
    // Filtrar
    { $match: { campo: "valor" }},

    // Agrupar
    { $group: { _id: "$campo", total: { $sum: 1 }}},

    // Proyectar
    { $project: { campo: 1, _id: 0 }},

    // Ordenar
    { $sort: { campo: 1 }},

    // Limitar
    { $limit: 5 }
])
```

## Administración

### Información del Sistema
```javascript
// Estado del servidor
db.serverStatus()

// Estadísticas de la base de datos
db.stats()

// Estadísticas de una colección
db.collection.stats()

// Ver procesos en ejecución
db.currentOp()

// Matar un proceso
db.killOp(opId)
```

## Usuarios y Roles

### Gestión de Usuarios
```javascript
// Crear usuario
db.createUser({
    user: "nombre",
    pwd: "contraseña",
    roles: ["readWrite"]
})

// Listar usuarios
show users

// Eliminar usuario
db.dropUser("nombre")

// Cambiar contraseña
db.changeUserPassword("nombre", "nuevaContraseña")
```

### Tips de Rendimiento
```javascript
// Explicar plan de consulta
db.collection.find().explain()

// Crear índice en segundo plano
db.collection.createIndex({ campo: 1 }, { background: true })

// Hint para forzar uso de índice
db.collection.find().hint({ campo: 1 })
```
