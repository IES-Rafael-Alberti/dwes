# Guía de Comandos PyMongo

## Conexión Inicial
```python
from pymongo import MongoClient

# Conexión local
client = MongoClient('mongodb://localhost:27017/')

# Conexión con credenciales
client = MongoClient('mongodb://usuario:contraseña@localhost:27017/')

# Seleccionar base de datos
db = client['nombre_db']

# Seleccionar colección
collection = db['nombre_coleccion']
```

## Operaciones CRUD

### Create (Insertar)
```python
# Insertar un documento
resultado = collection.insert_one({
    "campo1": "valor1",
    "campo2": "valor2"
})
print(f"ID insertado: {resultado.inserted_id}")

# Insertar varios documentos
documentos = [
    {"campo1": "valor1"},
    {"campo1": "valor2"}
]
resultado = collection.insert_many(documentos)
print(f"IDs insertados: {resultado.inserted_ids}")
```

### Read (Consultar)
```python
# Encontrar todos los documentos
documentos = collection.find()
for doc in documentos:
    print(doc)

# Encontrar con condición
documentos = collection.find({"campo": "valor"})

# Encontrar uno
documento = collection.find_one({"campo": "valor"})

# Contar documentos
total = collection.count_documents({"campo": "valor"})

# Limitar resultados
documentos = collection.find().limit(5)

# Saltar resultados (paginación)
documentos = collection.find().skip(10)

# Ordenar resultados (1 ascendente, -1 descendente)
documentos = collection.find().sort("campo", 1)
```

### Update (Actualizar)
```python
# Actualizar un documento
resultado = collection.update_one(
    {"campo": "valor"},
    {"$set": {"campo2": "nuevo_valor"}}
)
print(f"Documentos modificados: {resultado.modified_count}")

# Actualizar varios documentos
resultado = collection.update_many(
    {"campo": "valor"},
    {"$set": {"campo2": "nuevo_valor"}}
)

# Incrementar valor
resultado = collection.update_one(
    {"campo": "valor"},
    {"$inc": {"contador": 1}}
)

# Actualizar arrays
resultado = collection.update_one(
    {"campo": "valor"},
    {"$push": {"array": "nuevo_elemento"}}
)
```

### Delete (Eliminar)
```python
# Eliminar un documento
resultado = collection.delete_one({"campo": "valor"})
print(f"Documentos eliminados: {resultado.deleted_count}")

# Eliminar varios documentos
resultado = collection.delete_many({"campo": "valor"})

# Eliminar todos los documentos
resultado = collection.delete_many({})
```

## Consultas Avanzadas

### Operadores de Comparación
```python
# Mayor que
documentos = collection.find({"campo": {"$gt": valor}})

# Mayor o igual que
documentos = collection.find({"campo": {"$gte": valor}})

# Menor que
documentos = collection.find({"campo": {"$lt": valor}})

# Menor o igual que
documentos = collection.find({"campo": {"$lte": valor}})

# No igual
documentos = collection.find({"campo": {"$ne": valor}})

# En array de valores
documentos = collection.find({"campo": {"$in": [valor1, valor2]}})

# No en array de valores
documentos = collection.find({"campo": {"$nin": [valor1, valor2]}})
```

### Operadores Lógicos
```python
# AND
documentos = collection.find({
    "$and": [
        {"campo1": "valor1"},
        {"campo2": "valor2"}
    ]
})

# OR
documentos = collection.find({
    "$or": [
        {"campo1": "valor1"},
        {"campo2": "valor2"}
    ]
})

# NOT
documentos = collection.find({
    "campo": {"$not": {"$eq": "valor"}}
})
```

## Índices
```python
# Crear índice
collection.create_index("campo")

# Crear índice único
collection.create_index("campo", unique=True)

# Crear índice compuesto
collection.create_index([("campo1", 1), ("campo2", -1)])

# Listar índices
for index in collection.list_indexes():
    print(index)

# Eliminar índice
collection.drop_index("nombre_indice")

# Eliminar todos los índices
collection.drop_indexes()
```

## Agregaciones
```python
# Pipeline de agregación básico
pipeline = [
    {"$match": {"campo": "valor"}},
    {"$group": {
        "_id": "$campo",
        "total": {"$sum": 1}
    }}
]
resultados = list(collection.aggregate(pipeline))

# Ejemplo más complejo
pipeline = [
    # Filtrar
    {"$match": {"campo": "valor"}},

    # Agrupar
    {"$group": {
        "_id": "$campo",
        "total": {"$sum": 1}
    }},

    # Proyectar
    {"$project": {
        "campo": 1,
        "_id": 0
    }},

    # Ordenar
    {"$sort": {"campo": 1}},

    # Limitar
    {"$limit": 5}
]
resultados = list(collection.aggregate(pipeline))
```

## Gestión de Bases de Datos y Colecciones
```python
# Listar bases de datos
databases = client.list_database_names()

# Listar colecciones
colecciones = db.list_collection_names()

# Crear colección
db.create_collection("nombre_coleccion")

# Eliminar colección
db.drop_collection("nombre_coleccion")

# Eliminar base de datos
client.drop_database("nombre_db")
```

## Tips y Buenas Prácticas

### Manejo de Conexiones
```python
# Usar with para manejar conexiones
with MongoClient('mongodb://localhost:27017/') as client:
    db = client.nombre_db
    # realizar operaciones

# Verificar conexión
try:
    client.admin.command('ping')
    print("Conexión exitosa")
except Exception as e:
    print(f"Error de conexión: {e}")
```

### Rendimiento
```python
# Explicar plan de consulta
explicacion = collection.find({"campo": "valor"}).explain()

# Usar hint para forzar índice
documentos = collection.find({"campo": "valor"}).hint([("campo", 1)])

# Proyección para traer solo campos necesarios
documentos = collection.find({}, {"campo1": 1, "campo2": 1, "_id": 0})
```
