# 1. Introducción a MongoDB #

## ¿Qué es MongoDB? ##

**MongoDB** es una base de datos NoSQL (No Relational) orientada a documentos, diseñada para manejar grandes volúmenes de datos estructurados o no estructurados de manera flexible y eficiente. A diferencia de las bases de datos relacionales, MongoDB almacena los datos en formato **BSON** (una representación binaria de JSON), lo que permite una mayor flexibilidad en la estructura de los datos y una escalabilidad horizontal óptima.

MongoDB es ampliamente utilizado en aplicaciones modernas debido a su capacidad para gestionar datos no estructurados o semi-estructurados, su escalabilidad y su enfoque en alta disponibilidad. Es especialmente útil en entornos donde los requisitos de escalado y rendimiento cambian dinámicamente, como en aplicaciones web, sistemas de análisis de datos y microservicios.

## Características principales ##

1. **Modelo basado en documentos**: MongoDB organiza los datos en documentos, que son objetos JSON (JavaScript Object Notation), lo que permite una estructura flexible y jerárquica. Esto facilita el almacenamiento de datos complejos en un solo documento, lo que mejora la eficiencia en ciertos casos de uso.

2. **NoSQL (No Relacional)**: MongoDB no sigue el esquema rígido de las bases de datos relacionales, lo que significa que los datos pueden variar en estructura y no requieren tablas predefinidas. Esto hace que sea ideal para proyectos que necesitan escalabilidad y flexibilidad.

3. **Escalabilidad horizontal**: MongoDB permite dividir los datos en múltiples servidores (sharding), lo que facilita el escalado de manera eficiente. Esto es fundamental en aplicaciones distribuidas donde el volumen de datos puede crecer rápidamente.

4. **Alta disponibilidad mediante replicación**: MongoDB soporta la replicación, permitiendo que los datos se copien automáticamente en varios servidores. Esto asegura alta disponibilidad y permite recuperación en caso de fallos.

5. **Consultas flexibles**: MongoDB soporta consultas complejas con filtros, proyecciones y operadores avanzados. También permite realizar agregaciones para análisis de datos.

6. **Soporte de transacciones**: A partir de MongoDB 4.0, se introdujo el soporte para transacciones multi-documento, lo que mejora la consistencia de los datos en escenarios críticos.

7. **Agregaciones y MapReduce**: MongoDB incluye potentes capacidades de agregación, que permiten procesar y transformar grandes conjuntos de datos, similar a lo que se puede hacer en bases de datos relacionales con SQL.

8. **Compatibilidad con múltiples lenguajes**: MongoDB ofrece drivers para muchos lenguajes de programación (Python, Java, Node.js, etc.), lo que lo convierte en una opción versátil para desarrolladores de diferentes plataformas.

## Comparación con bases de datos relacionales ##

| Característica         | MongoDB                                     | Base de Datos Relacional                   |
|------------------------|---------------------------------------------|--------------------------------------------|
| **Modelo de Datos**     | Basado en documentos (JSON/BSON)            | Basado en tablas (filas y columnas)        |
| **Esquema**             | Flexible (sin necesidad de un esquema fijo) | Esquema rígido (definido previamente)      |
| **Escalabilidad**       | Horizontal (Sharding)                      | Vertical (aumento de capacidad del servidor) |
| **Consultas**           | JSON (find(), agregaciones)                 | SQL (SELECT, JOIN, etc.)                   |
| **Transacciones**       | Transacciones multi-documento               | Transacciones completas                    |
| **Relaciones**          | Embebidas o mediante referencias            | Relaciones explícitas (JOINs)              |
| **Rendimiento**         | Optimizado para lectura y escritura rápida  | Optimizado para integridad y consistencia  |

MongoDB ofrece una gran flexibilidad y es ideal para aplicaciones donde los datos cambian frecuentemente o son heterogéneos. A diferencia de las bases de datos relacionales que requieren una estructura de datos fija, MongoDB permite modificar la estructura de los documentos sobre la marcha, lo que hace que la adaptación sea más ágil en entornos dinámicos.

---

## 2. Documentos y Colecciones ##

### Definición de Documento ###

En **MongoDB**, un **documento** es una estructura de datos compuesta por pares de campo-valor. Los documentos son similares a los objetos JSON (JavaScript Object Notation) pero se almacenan en formato **BSON** (Binary JSON), lo que permite optimizar el rendimiento de MongoDB. Un documento puede contener diversos tipos de datos, incluyendo cadenas de texto, números, booleanos, arreglos, objetos anidados y más.

**Ejemplo de Documento en MongoDB (Formato JSON)**:
```json
{
    "nombre": "Laptop",
    "precio": 999.99,
    "cantidad": 15,
    "categoria": "Electrónica",
    "especificaciones": {
        "procesador": "Intel i7",
        "ram": "16GB",
        "almacenamiento": "512GB SSD"
    }
}
```

Este documento contiene un campo llamado `especificaciones` que a su vez es un subdocumento con detalles específicos sobre el producto. MongoDB permite este tipo de estructuras anidadas sin ninguna limitación específica, proporcionando gran flexibilidad en la forma en que se modelan los datos.

### Estructura de los Documentos (JSON/BSON) ###

Los documentos de MongoDB se almacenan en formato **BSON**, que es una versión binaria de JSON. BSON permite que MongoDB realice un almacenamiento más eficiente y un acceso rápido a los datos. Aunque los datos se ingresan y consultan en formato JSON, se convierten a BSON cuando se almacenan en la base de datos.

**Tipos de datos admitidos en BSON**:
- **String**: Cadenas de texto.
- **Number**: Números enteros y flotantes.
- **Boolean**: Verdadero (`true`) o Falso (`false`).
- **Array**: Listas de valores.
- **Document**: Subdocumentos anidados.
- **Null**: Valor nulo.
- **Date**: Fechas y horas.
- **ObjectId**: Identificador único generado automáticamente.

### Definición de Colección ###

Una **colección** en MongoDB es un conjunto de documentos que comparten una estructura lógica. A diferencia de las tablas en bases de datos relacionales, las colecciones no requieren que todos los documentos tengan el mismo conjunto de campos o estructura, lo que proporciona flexibilidad en la organización de los datos.

**Ejemplo de Colección:**
Supongamos que tienes una tienda online y deseas almacenar información sobre los productos disponibles. La colección llamada `productos` podría tener varios documentos que representan diferentes artículos. Ejemplo:

```json
[
    {
        "nombre": "Laptop",
        "precio": 999.99,
        "cantidad": 10,
        "categoria": "Electrónica"
    },
    {
        "nombre": "Mouse",
        "precio": 19.99,
        "cantidad": 50,
        "categoria": "Accesorios"
    },
    {
        "nombre": "Teclado",
        "precio": 49.99,
        "cantidad": 30,
        "categoria": "Accesorios"
    }
]
```

Cada documento en la colección puede tener campos diferentes o adicionales, y no es obligatorio que todos sigan un esquema rígido. Esto hace que MongoDB sea altamente flexible y escalable para diferentes tipos de aplicaciones.

### Diferencias entre Documentos y Filas en SQL ###

- **Documentos en MongoDB**: Los documentos en MongoDB son estructuras de datos flexibles que pueden tener diferentes campos y tipos de datos. Cada documento es una unidad autónoma que puede contener información compleja y anidada.

- **Filas en SQL**: En una base de datos relacional, los datos se almacenan en filas y columnas con una estructura fija (esquema). Todos los registros en una tabla tienen los mismos campos, y los valores deben ajustarse al esquema predefinido.

| Característica         | Documento en MongoDB                    | Fila en SQL                              |
|------------------------|-----------------------------------------|------------------------------------------|
| **Esquema**            | Flexible (sin estructura fija)          | Fijo (requiere predefinir columnas)      |
| **Datos anidados**     | Permite documentos anidados             | No admite anidaciones complejas          |
| **Almacenamiento**     | Almacenado como BSON (Binary JSON)       | Almacenado en tablas con formato binario |
| **Modificación**       | Se puede modificar la estructura de un documento sin afectar a otros | Se requiere ajustar la estructura de la tabla para cambios |

MongoDB permite almacenar datos más complejos y dinámicos que las bases de datos relacionales tradicionales, lo que lo hace especialmente útil para aplicaciones que requieren flexibilidad en su modelo de datos.

---

## 3. Bases de Datos en MongoDB ##

En MongoDB, las **bases de datos** son contenedores lógicos que agrupan colecciones de documentos. MongoDB permite crear múltiples bases de datos, y cada una puede contener diferentes colecciones. Esta sección cubre cómo crear, seleccionar y gestionar bases de datos en MongoDB.

### **Creación y Uso de Bases de Datos** ###

A diferencia de las bases de datos relacionales, en MongoDB no es necesario definir previamente un esquema o estructura para crear una base de datos. MongoDB crea automáticamente una base de datos cuando se inserta el primer documento en una colección de esa base de datos.

#### **1. Crear o Seleccionar una Base de Datos** ####

Para trabajar con una base de datos en MongoDB, se utiliza el comando `use`. Este comando selecciona una base de datos, y si la base de datos no existe, MongoDB la creará automáticamente en el momento en que se agreguen documentos.

**Sintaxis:**
```javascript
use nombre_de_la_base_de_datos
```

**Ejemplo:**
```javascript
use tienda_online
```
Esto selecciona la base de datos `tienda_online`. Si no existe, se creará cuando se inserte el primer documento.

Con esto cubrimos la introducción a MongoDB

#### **2. Listar Bases de Datos Existentes** ####

Para ver todas las bases de datos disponibles en un servidor MongoDB, se utiliza el comando `show dbs`.

**Sintaxis:**
```javascript
show dbs
```

**Resultado:**
```bash
admin      0.000GB
config     0.000GB
local      0.000GB
tienda_online 0.002GB
```
Las bases de datos `admin`, `config` y `local` son bases de datos internas de MongoDB, y la base de datos `tienda_online` es la que hemos creado.

Con esto concluimos la sección sobre Bases de Datos en MongoDB.

#### **3. Eliminar una Base de Datos** ####

MongoDB permite eliminar una base de datos mediante el comando `db.dropDatabase()`. Este comando eliminará la base de datos seleccionada y todo su contenido.

**Sintaxis:**
```javascript
db.dropDatabase()
```

**Ejemplo:**
Si estás usando la base de datos `tienda_online`, ejecutar este comando eliminará la base de datos por completo:
```javascript
use tienda_online
db.dropDatabase()
```

**Resultado:**
```bash
{ "dropped" : "tienda_online", "ok" : 1 }
```

### **Manejo de Bases de Datos en MongoDB** ###

MongoDB organiza las bases de datos y las colecciones de manera flexible, lo que facilita la gestión de datos en diferentes niveles. A continuación se describen algunos aspectos clave del manejo de bases de datos.

#### **1. Cambiar entre Bases de Datos** ####

Para cambiar entre bases de datos, simplemente usa el comando `use`. MongoDB cambiará el contexto a la base de datos especificada sin necesidad de una reconexión. Esto es útil cuando necesitas trabajar con múltiples bases de datos en una misma sesión.

**Ejemplo:**
```javascript
use inventario
```
Esto cambiará el contexto a la base de datos `inventario`, aunque no contenga datos todavía.

#### **2. Información sobre la Base de Datos Actual** ####

El comando `db.stats()` proporciona estadísticas sobre la base de datos actual, como el tamaño de las colecciones, el número de índices y otros detalles útiles.

**Sintaxis:**
```javascript
db.stats()
```

**Ejemplo de resultado:**
```json
{
  "db": "tienda_online",
  "collections": 5,
  "objects": 100,
  "avgObjSize": 50.12,
  "dataSize": 5012,
  "storageSize": 8192,
  "indexes": 2,
  "ok": 1
}
```
Este comando proporciona información valiosa sobre el estado de la base de datos y su uso de recursos.

#### **3. Copiar una Base de Datos** ####

MongoDB permite clonar bases de datos para crear copias exactas de sus documentos. Aunque no existe un comando directo para clonar una base de datos completa, se puede realizar utilizando scripts que copien las colecciones y sus documentos a una nueva base de datos.

Un enfoque común es crear un respaldo y restaurarlo en una nueva base de datos utilizando `mongodump` y `mongorestore` (herramientas de la línea de comandos de MongoDB).

### **Buenas Prácticas al Trabajar con Bases de Datos** ###

1. **Nombres de Bases de Datos**:
   - Utiliza nombres descriptivos y fáciles de entender para tus bases de datos. Los nombres pueden incluir letras, números y guiones bajos (_), pero no deben contener espacios ni comenzar con números.
   - Ejemplo: `clientes`, `ventas_2024`.

2. **Optimización de la Estructura**:
   - Aunque MongoDB es flexible con respecto al esquema, intenta mantener una estructura lógica coherente entre documentos. Esto facilitará las consultas y el mantenimiento a largo plazo.

3. **Tamaño de la Base de Datos**:
   - Monitoriza regularmente el tamaño de tus bases de datos y colecciones utilizando `db.stats()`. Si notas un crecimiento inesperado, investiga qué colecciones están ocupando más espacio.

Con esto concluimos la sección sobre Bases de Datos en MongoDB.
---
### 4. CRUD en MongoDB

En MongoDB, el concepto de **CRUD** se refiere a las operaciones básicas que puedes realizar sobre los datos: **Create (Crear)**, **Read (Leer)**, **Update (Actualizar)** y **Delete (Borrar)**. En esta sección, exploraremos cómo realizar cada una de estas operaciones utilizando los comandos de MongoDB y cómo gestionarlas eficazmente dentro de una colección.

#### **1. Crear (Create)**

La operación de **Crear** en MongoDB se realiza cuando insertamos documentos en una colección. MongoDB ofrece dos métodos principales para insertar datos: `insertOne()` para un solo documento y `insertMany()` para varios documentos.

##### **Insertar un Documento: `insertOne()`**

Este método permite insertar un solo documento en una colección. MongoDB automáticamente añadirá un campo `_id` único a cada documento si no se proporciona uno.

**Sintaxis:**
```javascript
db.nombre_de_la_coleccion.insertOne({ documento })
```

**Ejemplo:**
```javascript
db.productos.insertOne({
    "nombre": "Smartphone",
    "precio": 599.99,
    "cantidad": 25,
    "categoria": "Electrónica"
})
```

**Resultado:**
```bash
{
    "acknowledged": true,
    "insertedId": ObjectId("63461f0f4f9d460fdf65d97a")
}
```

##### **Insertar Múltiples Documentos: `insertMany()`**

Este método permite insertar varios documentos a la vez en una colección.

**Sintaxis:**
```javascript
db.nombre_de_la_coleccion.insertMany([{ documento1 }, { documento2 }, ...])
```

**Ejemplo:**
```javascript
db.productos.insertMany([
    {
        "nombre": "Tablet",
        "precio": 299.99,
        "cantidad": 50,
        "categoria": "Electrónica"
    },
    {
        "nombre": "Cámara",
        "precio": 799.99,
        "cantidad": 15,
        "categoria": "Fotografía"
    }
])
```

**Resultado:**
```bash
{
    "acknowledged": true,
    "insertedIds": [
        ObjectId("63461f174f9d460fdf65d97b"),
        ObjectId("63461f174f9d460fdf65d97c")
    ]
}
```

---

#### **2. Leer (Read)**

La operación de **Leer** o consultar documentos es fundamental para obtener datos almacenados en MongoDB. El método más común para realizar esta operación es `find()`, el cual permite realizar consultas con diferentes filtros y proyecciones.

##### **Consultar Todos los Documentos: `find()`**

Si no se especifican filtros, el método `find()` devuelve todos los documentos de la colección.

**Sintaxis:**
```javascript
db.nombre_de_la_coleccion.find()
```

**Ejemplo:**
```javascript
db.productos.find()
```

**Resultado:**
```bash
[
    {
        "_id": ObjectId("63461f0f4f9d460fdf65d97a"),
        "nombre": "Smartphone",
        "precio": 599.99,
        "cantidad": 25,
        "categoria": "Electrónica"
    },
    {
        "_id": ObjectId("63461f174f9d460fdf65d97b"),
        "nombre": "Tablet",
        "precio": 299.99,
        "cantidad": 50,
        "categoria": "Electrónica"
    }
]
```

##### **Consultar con Filtros: `find({ filtro })`**

Puedes aplicar filtros para seleccionar solo aquellos documentos que cumplen con ciertos criterios.

**Sintaxis:**
```javascript
db.nombre_de_la_coleccion.find({ campo: valor })
```

**Ejemplo:**
Consultar productos que pertenezcan a la categoría "Electrónica":

```javascript
db.productos.find({ "categoria": "Electrónica" })
```

##### **Proyección de Campos:**

Puedes especificar qué campos deseas incluir o excluir en el resultado de la consulta usando la proyección.

**Sintaxis:**
```javascript
db.nombre_de_la_coleccion.find({ filtro }, { campo1: 1, campo2: 0 })
```

**Ejemplo:**
Consultar productos y mostrar solo los nombres y precios:

```javascript
db.productos.find({}, { "nombre": 1, "precio": 1, "_id": 0 })
```

**Resultado:**
```bash
[
    { "nombre": "Smartphone", "precio": 599.99 },
    { "nombre": "Tablet", "precio": 299.99 }
]
```

---

#### **3. Actualizar (Update)**

La operación de **Actualizar** permite modificar los documentos existentes. MongoDB ofrece los métodos `updateOne()` para actualizar un solo documento y `updateMany()` para actualizar múltiples documentos.

##### **Actualizar un Documento: `updateOne()`**

Este método actualiza el primer documento que coincida con el filtro especificado.

**Sintaxis:**
```javascript
db.nombre_de_la_coleccion.updateOne({ filtro }, { $set: { campo: valor } })
```

**Ejemplo:**
Aumentar el precio del "Smartphone" a 649.99:

```javascript
db.productos.updateOne(
    { "nombre": "Smartphone" },
    { $set: { "precio": 649.99 } }
)
```

##### **Actualizar Múltiples Documentos: `updateMany()`**

Este método actualiza todos los documentos que coincidan con el filtro.

**Sintaxis:**
```javascript
db.nombre_de_la_coleccion.updateMany({ filtro }, { $set: { campo: valor } })
```

**Ejemplo:**
Cambiar la categoría de todos los productos con precio menor a 400 a "Accesorios":

```javascript
db.productos.updateMany(
    { "precio": { $lt: 400 } },
    { $set: { "categoria": "Accesorios" } }
)
```

---

#### **4. Borrar (Delete)**

La operación de **Borrar** elimina uno o más documentos de una colección. MongoDB ofrece los métodos `deleteOne()` y `deleteMany()`.

##### **Borrar un Documento: `deleteOne()`**

Elimina el primer documento que coincida con el filtro especificado.

**Sintaxis:**
```javascript
db.nombre_de_la_coleccion.deleteOne({ filtro })
```

**Ejemplo:**
Eliminar el producto con nombre "Tablet":

```javascript
db.productos.deleteOne({ "nombre": "Tablet" })
```

##### **Borrar Múltiples Documentos: `deleteMany()`**

Elimina todos los documentos que coincidan con el filtro.

**Sintaxis:**
```javascript
db.nombre_de_la_coleccion.deleteMany({ filtro })
```

**Ejemplo:**
Eliminar todos los productos que pertenezcan a la categoría "Accesorios":

```javascript
db.productos.deleteMany({ "categoria": "Accesorios" })
```

---

### 5. Consultas en MongoDB

En MongoDB, las consultas permiten recuperar información almacenada en las colecciones, aplicando diferentes criterios para filtrar y estructurar los resultados. MongoDB ofrece un lenguaje de consultas poderoso que permite realizar búsquedas básicas, filtradas, y también aplicar proyecciones para devolver solo los campos relevantes.

#### **1. Consultas Básicas con `find()`**

El método `find()` es el más utilizado en MongoDB para realizar consultas. Sin argumentos, `find()` devolverá todos los documentos de una colección. Sin embargo, es común aplicar filtros para buscar documentos específicos.

##### **Sintaxis General:**
```javascript
db.nombre_de_la_coleccion.find({ filtro }, { proyección })
```
- **Filtro**: Especifica los criterios de búsqueda. Si se deja vacío (`{}`), devolverá todos los documentos.
- **Proyección**: Define qué campos serán devueltos en el resultado. Si se omite, se devuelven todos los campos.

##### **Ejemplo Básico**:
```javascript
db.productos.find()
```
Este comando devolverá todos los documentos en la colección `productos`.

---

#### **2. Consultas con Filtros**

Los **filtros** en MongoDB permiten buscar documentos que coincidan con criterios específicos. Los filtros se pasan como el primer argumento de `find()`.

##### **Operadores de Comparación Comunes**:
- `$eq`: Igual a.
- `$ne`: Diferente de.
- `$gt`: Mayor que.
- `$gte`: Mayor o igual que.
- `$lt`: Menor que.
- `$lte`: Menor o igual que.
- `$in`: En un conjunto de valores.
- `$nin`: No en un conjunto de valores.

##### **Ejemplos de Consultas con Filtros**:

1. **Consultar documentos con un valor específico (`$eq`)**:
   ```javascript
   db.productos.find({ "categoria": "Electrónica" })
   ```

2. **Consultar documentos con valores mayores a un valor (`$gt`)**:
   ```javascript
   db.productos.find({ "precio": { $gt: 500 } })
   ```

3. **Consultar documentos en un rango de valores (`$gte` y `$lte`)**:
   ```javascript
   db.productos.find({ "precio": { $gte: 200, $lte: 800 } })
   ```

4. **Consultar documentos que contengan valores de una lista (`$in`)**:
   ```javascript
   db.productos.find({ "categoria": { $in: ["Electrónica", "Accesorios"] } })
   ```
Con esto, hemos cubierto las operaciones básicas de CRUD en MongoDB. Estas operaciones son esenciales para interactuar con las bases de datos de MongoDB y gestionar los datos de manera efectiva.
---

#### **3. Consultas con Proyecciones**

La **proyección** en MongoDB se utiliza para definir qué campos se incluirán o excluirán en los resultados. Es el segundo argumento del método `find()`.

##### **Incluir o Excluir Campos**:

1. **Incluir campos específicos** (1 significa incluir):
   ```javascript
   db.productos.find({}, { "nombre": 1, "precio": 1 })
   ```
   Este comando devuelve solo los campos `nombre` y `precio`, excluyendo todos los demás.

2. **Excluir campos específicos** (0 significa excluir):
   ```javascript
   db.productos.find({}, { "categoria": 0 })
   ```
   Este comando devuelve todos los campos excepto `categoria`.

3. **Excluir el campo `_id`** (que se devuelve por defecto):
   ```javascript
   db.productos.find({}, { "_id": 0, "nombre": 1, "precio": 1 })
   ```
   Esto excluye el campo `_id` y devuelve solo `nombre` y `precio`.

---

#### **4. Consultas con Operadores Lógicos**

MongoDB también permite combinar filtros con operadores lógicos como `$and`, `$or`, `$not`, y `$nor`.

##### **Operadores Lógicos Comunes**:
- `$and`: Coincidencia de todas las condiciones.
- `$or`: Coincidencia de alguna condición.
- `$not`: Negar una condición.
- `$nor`: Coincidencia de ninguna condición.

##### **Ejemplos de Consultas con Operadores Lógicos**:

1. **Consultar documentos que cumplan dos condiciones (`$and`)**:
   ```javascript
   db.productos.find({
       $and: [
           { "categoria": "Electrónica" },
           { "precio": { $gt: 500 } }
       ]
   })
   ```

2. **Consultar documentos que cumplan al menos una condición (`$or`)**:
   ```javascript
   db.productos.find({
       $or: [
           { "precio": { $lt: 100 } },
           { "categoria": "Accesorios" }
       ]
   })
   ```

3. **Consultar documentos que no cumplan una condición (`$not`)**:
   ```javascript
   db.productos.find({
       "precio": { $not: { $gt: 500 } }
   })
   ```

---

#### **5. Consultas con Operadores de Arreglos**

MongoDB permite realizar búsquedas sobre campos que contienen arreglos utilizando operadores específicos como `$all`, `$size`, `$elemMatch`, entre otros.

##### **Operadores Comunes para Arreglos**:
- `$all`: Coincidencia de todos los valores en un arreglo.
- `$size`: Coincidencia por el tamaño del arreglo.
- `$elemMatch`: Coincidencia de al menos un elemento que cumpla con múltiples condiciones.

##### **Ejemplos de Consultas con Arreglos**:

1. **Consultar documentos con un campo que contenga un arreglo y tenga un tamaño específico (`$size`)**:
   Supongamos que un documento tiene un campo `etiquetas` que es un arreglo de palabras clave. Podemos buscar documentos que tengan exactamente tres etiquetas.

   ```javascript
   db.productos.find({ "etiquetas": { $size: 3 } })
   ```

2. **Consultar documentos donde un arreglo contenga todos los valores especificados (`$all`)**:
   ```javascript
   db.productos.find({ "etiquetas": { $all: ["nuevo", "popular"] } })
   ```

3. **Consultar documentos donde al menos un elemento del arreglo cumpla con una condición (`$elemMatch`)**:
   ```javascript
   db.productos.find({
       "especificaciones": {
           $elemMatch: { "procesador": "Intel i7", "ram": { $gte: "16GB" } }
       }
   })
   ```

---

#### **6. Ordenar y Limitar Resultados**

MongoDB permite ordenar los resultados de una consulta y limitar el número de documentos devueltos.

##### **Ordenar Resultados: `sort()`**

El método `sort()` ordena los resultados de la consulta según el campo especificado. Se usa `1` para orden ascendente y `-1` para descendente.

**Ejemplo**: Ordenar los productos por precio de forma descendente.
```javascript
db.productos.find().sort({ "precio": -1 })
```

##### **Limitar el Número de Resultados: `limit()`**

El método `limit()` se utiliza para devolver solo un número específico de documentos.

**Ejemplo**: Obtener solo los primeros 5 productos más caros.
```javascript
db.productos.find().sort({ "precio": -1 }).limit(5)
```

---

### Conclusión

Las consultas en MongoDB ofrecen una enorme flexibilidad, permitiendo filtrar, proyectar, ordenar y limitar los resultados. MongoDB también admite una amplia gama de operadores lógicos, de comparación y de arreglos para realizar búsquedas complejas y específicas. El uso adecuado de estos operadores y métodos te permitirá gestionar grandes volúmenes de datos de manera eficiente y precisa.

---

### 6. Índices en MongoDB

Los **índices** en MongoDB son estructuras de datos especiales que almacenan una parte de los datos de una colección de una manera que permite que las consultas sean mucho más rápidas. Sin índices, MongoDB debe realizar un **escaneo completo de la colección** (examinar cada documento) para seleccionar los documentos que coincidan con la consulta. Los índices mejoran el rendimiento de las consultas, pero también tienen algunos costos asociados, como el uso de espacio adicional y tiempo de procesamiento para mantenerlos actualizados.

#### **1. ¿Qué son los índices?**

Un **índice** en MongoDB es similar a un índice en un libro: permite acceder a la información de manera rápida sin tener que revisar todos los documentos de una colección. MongoDB crea automáticamente un índice en el campo `_id` de cada colección, que es el identificador único de cada documento.

##### **Ventajas de usar índices:**
- **Aceleración de consultas**: Las consultas que utilizan índices se ejecutan más rápido porque MongoDB puede buscar en una estructura de índice en lugar de revisar todos los documentos.
- **Optimización de operaciones de lectura**: Las consultas que requieren ordenar o filtrar datos pueden beneficiarse significativamente de los índices.

##### **Desventajas o costos:**
- **Sobrecarga en inserciones**: Mantener los índices actualizados agrega una pequeña sobrecarga en las operaciones de escritura (insertar, actualizar, eliminar).
- **Uso de almacenamiento adicional**: Los índices ocupan espacio adicional en el disco.

---

#### **2. Tipos de Índices en MongoDB**

MongoDB soporta varios tipos de índices, que son útiles en diferentes situaciones. Los más comunes son los siguientes:

##### **Índice Único**
Este es el tipo de índice predeterminado en el campo `_id`. Asegura que no haya valores duplicados en el campo indexado.

**Ejemplo:**
```javascript
db.productos.createIndex({ "nombre": 1 }, { unique: true })
```
Este índice garantiza que no puede haber dos productos con el mismo nombre.

##### **Índice Simple**
Un índice simple se crea en un solo campo. Acelera las búsquedas sobre ese campo en particular.

**Ejemplo:**
```javascript
db.productos.createIndex({ "precio": 1 })
```
Este comando crea un índice en el campo `precio` en orden ascendente (`1`), lo que acelerará las consultas que busquen productos por su precio.

##### **Índice Compuesto**
Un índice compuesto se crea en más de un campo. Acelera consultas que filtran por múltiples campos simultáneamente.

**Ejemplo:**
```javascript
db.productos.createIndex({ "categoria": 1, "precio": -1 })
```
Este índice compuesto acelera las consultas que buscan productos por `categoria` y ordenan los resultados por `precio` en orden descendente.

##### **Índice de Texto**
Los índices de texto permiten realizar búsquedas de texto completo dentro de los campos indexados.

**Ejemplo:**
```javascript
db.productos.createIndex({ "descripcion": "text" })
```
Este índice permite realizar búsquedas de texto en el campo `descripcion`, como buscar productos por palabras clave en su descripción.

##### **Índice Geoespacial**
Los índices geoespaciales son utilizados para almacenar y consultar datos que representan ubicaciones geográficas.

**Ejemplo:**
```javascript
db.tiendas.createIndex({ "ubicacion": "2dsphere" })
```
Este índice es útil para realizar consultas geoespaciales, como buscar tiendas cercanas a una ubicación específica.

---

#### **3. Crear, Ver y Eliminar Índices**

##### **Crear un Índice: `createIndex()`**

El método `createIndex()` permite crear un índice en un campo específico. Se pueden crear índices en uno o varios campos, dependiendo de las necesidades de las consultas.

**Sintaxis:**
```javascript
db.nombre_de_la_coleccion.createIndex({ campo: 1 })
```
- `campo: 1` indica que el índice se crea en el campo especificado en orden ascendente (`1`), o descendente (`-1`).

**Ejemplo:**
```javascript
db.productos.createIndex({ "precio": 1 })
```
Esto crea un índice en el campo `precio` en orden ascendente, lo que optimiza las consultas que busquen productos por su precio.

##### **Ver Índices: `getIndexes()`**

El método `getIndexes()` devuelve una lista de todos los índices que existen en una colección.

**Sintaxis:**
```javascript
db.nombre_de_la_coleccion.getIndexes()
```

**Ejemplo:**
```javascript
db.productos.getIndexes()
```
Esto devolverá una lista de los índices existentes en la colección `productos`, incluyendo el índice predeterminado en `_id`.

##### **Eliminar un Índice: `dropIndex()`**

El método `dropIndex()` se utiliza para eliminar un índice específico de una colección. Esto es útil cuando un índice ya no es necesario o si está afectando negativamente el rendimiento de las escrituras.

**Sintaxis:**
```javascript
db.nombre_de_la_coleccion.dropIndex({ campo: 1 })
```

**Ejemplo:**
```javascript
db.productos.dropIndex({ "precio": 1 })
```
Esto eliminará el índice en el campo `precio`.

---

#### **4. Casos de Uso y Buenas Prácticas para Índices**

##### **¿Cuándo crear índices?**
1. **Consultas frecuentes**: Si realizas consultas repetidas en un campo o combinación de campos, deberías crear índices en esos campos para mejorar el rendimiento.
2. **Consultas de ordenación**: Si una consulta incluye la ordenación por un campo, un índice en ese campo mejorará significativamente el tiempo de respuesta.
3. **Filtros complejos**: Consultas que combinan múltiples condiciones (`AND`, `OR`, rangos) se beneficiarán de índices compuestos.

##### **Buenas Prácticas al Crear Índices**:
1. **No indexar todos los campos**: Aunque los índices mejoran las consultas, crean sobrecarga en las operaciones de escritura. Indexa solo los campos que realmente son utilizados en consultas frecuentes.
2. **Monitorear el rendimiento**: Utiliza `explain()` para entender cómo MongoDB está utilizando los índices en tus consultas.
3. **Optimizar índices compuestos**: Los índices compuestos son útiles cuando las consultas filtran por múltiples campos. Asegúrate de crear índices compuestos si filtras frecuentemente por más de un campo.
4. **Evitar duplicidad de índices**: No crees índices redundantes en campos que ya están cubiertos por otros índices compuestos.

---

#### **5. Uso de `explain()` para Optimización**

El método `explain()` en MongoDB permite analizar cómo se ejecuta una consulta y qué índices están siendo utilizados. Esto es esencial para la optimización de consultas y la identificación de problemas de rendimiento.

##### **Sintaxis de `explain()`**:
```javascript
db.nombre_de_la_coleccion.find({ filtro }).explain("executionStats")
```

##### **Ejemplo**:
```javascript
db.productos.find({ "precio": { $gt: 500 } }).explain("executionStats")
```
El resultado mostrará si la consulta utilizó un índice o si hizo un escaneo completo de la colección.

---

### Conclusión

Los índices son una herramienta poderosa en MongoDB para mejorar el rendimiento de las consultas, especialmente cuando trabajas con grandes volúmenes de datos. Sin embargo, el uso de índices debe ser planificado cuidadosamente, ya que conlleva costos asociados en las operaciones de escritura y el uso de almacenamiento. Es importante monitorear las consultas usando `explain()` y crear los índices necesarios para mantener el sistema optimizado.

---
### 7. Agregaciones en MongoDB

En MongoDB, la **agregación** es un proceso que permite realizar operaciones avanzadas en los datos, como calcular sumas, promedios, agrupaciones, filtrado y transformación de datos. Las agregaciones son particularmente útiles para realizar análisis de datos complejos directamente en la base de datos, sin necesidad de procesar los resultados en el código de la aplicación.

El sistema de agregación de MongoDB utiliza **pipelines** (tuberías), que son secuencias de etapas (stages) que transforman los documentos de entrada en resultados de salida. Cada etapa aplica una operación, como filtrar, agrupar, ordenar, o modificar documentos.

#### **1. ¿Qué es el Aggregation Framework?**

El **Aggregation Framework** en MongoDB es el principal sistema para realizar consultas complejas y análisis de datos. Similar a la cláusula `GROUP BY` de SQL, pero con mucha más flexibilidad, este framework permite transformar y analizar datos en múltiples etapas.

##### **Sintaxis Básica**:
```javascript
db.nombre_de_la_coleccion.aggregate([ { etapa1 }, { etapa2 }, ... ])
```
- Cada **etapa** recibe documentos de la etapa anterior, los transforma y pasa los resultados a la siguiente etapa.

---

#### **2. Etapas Comunes en el Aggregation Framework**

Las siguientes son algunas de las etapas más comunes que se usan en las agregaciones:

##### **$match: Filtrar Documentos**

La etapa `$match` se utiliza para filtrar documentos, similar a `find()`. Solo pasa a la siguiente etapa los documentos que cumplen con el filtro.

**Ejemplo**:
Filtrar productos cuyo precio sea mayor a 500:
```javascript
db.productos.aggregate([
    { $match: { "precio": { $gt: 500 } } }
])
```

##### **$group: Agrupar Documentos**

La etapa `$group` permite agrupar documentos por un campo específico y realizar operaciones de agregación, como sumas o promedios, en cada grupo.

**Ejemplo**:
Agrupar productos por categoría y calcular el precio promedio de cada categoría:
```javascript
db.productos.aggregate([
    { $group: {
        _id: "$categoria",
        precioPromedio: { $avg: "$precio" }
    } }
])
```
- `_id` define el campo por el que se agrupa (en este caso, `categoria`).
- `$avg` calcula el promedio del campo `precio` en cada grupo.

##### **$project: Seleccionar y Transformar Campos**

La etapa `$project` permite seleccionar los campos que se incluirán en la salida, además de crear nuevos campos o modificar los existentes.

**Ejemplo**:
Proyectar el nombre del producto y su precio con un campo adicional que muestre el precio con IVA (21%):
```javascript
db.productos.aggregate([
    { $project: {
        "nombre": 1,
        "precio": 1,
        "precioConIVA": { $multiply: ["$precio", 1.21] }
    } }
])
```

##### **$sort: Ordenar los Resultados**

La etapa `$sort` ordena los documentos en el pipeline en función de uno o más campos.

**Ejemplo**:
Ordenar los productos por precio en orden descendente:
```javascript
db.productos.aggregate([
    { $sort: { "precio": -1 } }
])
```

##### **$limit: Limitar el Número de Resultados**

La etapa `$limit` restringe el número de documentos devueltos.

**Ejemplo**:
Limitar los resultados a los 3 productos más caros:
```javascript
db.productos.aggregate([
    { $sort: { "precio": -1 } },
    { $limit: 3 }
])
```

##### **$unwind: Descomponer Arreglos**

La etapa `$unwind` descompone un arreglo dentro de un documento en múltiples documentos, uno por cada elemento del arreglo.

**Ejemplo**:
Supongamos que un documento tiene un campo `etiquetas` que es un arreglo. `$unwind` crea un documento separado por cada etiqueta.
```javascript
db.productos.aggregate([
    { $unwind: "$etiquetas" }
])
```

---

#### **3. Combinando Etapas en un Pipeline**

La verdadera potencia del **Aggregation Framework** proviene de la capacidad de combinar múltiples etapas en un pipeline para realizar transformaciones complejas en los datos.

**Ejemplo Completo**:
Obtener el precio promedio de los productos por categoría, solo para aquellos cuyo precio sea mayor a 100, y ordenar los resultados por precio promedio en orden descendente:

```javascript
db.productos.aggregate([
    { $match: { "precio": { $gt: 100 } } },
    { $group: { _id: "$categoria", precioPromedio: { $avg: "$precio" } } },
    { $sort: { precioPromedio: -1 } }
])
```

**Explicación**:
1. **$match** filtra los productos con un precio mayor a 100.
2. **$group** agrupa los productos por `categoria` y calcula el precio promedio.
3. **$sort** ordena las categorías en orden descendente por su precio promedio.

---

#### **4. Expresiones de Agregación Comunes**

MongoDB ofrece una serie de expresiones que permiten realizar cálculos dentro del Aggregation Framework. Estas expresiones se pueden usar en etapas como `$group` y `$project`.

##### **Operadores Matemáticos:**
- `$sum`: Suma de valores.
- `$avg`: Promedio de valores.
- `$min`/`$max`: Mínimo/Máximo valor.
- `$multiply`: Multiplica dos o más valores.
- `$divide`: Divide un valor por otro.

##### **Operadores de Comparación:**
- `$eq`: Igualdad.
- `$ne`: Desigualdad.
- `$gt`/`$gte`: Mayor/Mayor o igual.
- `$lt`/`$lte`: Menor/Menor o igual.

##### **Operadores de Arreglos:**
- `$push`: Inserta valores en un arreglo.
- `$addToSet`: Inserta valores únicos en un arreglo.
- `$size`: Devuelve el tamaño de un arreglo.

**Ejemplo de Expresión**:
Calcular el total de productos por categoría y sumarlos:
```javascript
db.productos.aggregate([
    { $group: {
        _id: "$categoria",
        totalProductos: { $sum: 1 }
    } }
])
```

---

#### **5. Uso de `explain()` para Optimización**

Al igual que las consultas normales, puedes usar `explain()` para analizar cómo MongoDB ejecuta una agregación y verificar si los índices son utilizados adecuadamente.

**Sintaxis**:
```javascript
db.nombre_de_la_coleccion.aggregate([ { pipeline } ]).explain("executionStats")
```

**Ejemplo**:
```javascript
db.productos.aggregate([
    { $match: { "precio": { $gt: 100 } } }
]).explain("executionStats")
```
Esto devolverá un informe detallado sobre cómo se ejecuta la agregación, incluyendo el uso de índices y el número de documentos examinados.

---

#### **6. Agregaciones con MapReduce**

Además del Aggregation Framework, MongoDB también soporta un sistema llamado **MapReduce**, que es una técnica más avanzada para realizar agregaciones complejas y personalizadas, especialmente útil en grandes volúmenes de datos distribuidos. Sin embargo, en la mayoría de los casos, el Aggregation Framework es más eficiente y fácil de usar que MapReduce.

**Ejemplo de MapReduce**:
Supongamos que queremos contar el número total de productos por categoría utilizando MapReduce:

```javascript
db.productos.mapReduce(
   function() { emit(this.categoria, 1); },  // Map function
   function(key, values) { return Array.sum(values); },  // Reduce function
   { out: "productos_totales_por_categoria" }  // Output collection
)
```

---

### Conclusión

El **Aggregation Framework** de MongoDB es una herramienta poderosa para realizar análisis complejos y transformar datos dentro de la base de datos. La capacidad de combinar múltiples etapas de agregación en un solo pipeline proporciona una gran flexibilidad y eficiencia. Las expresiones de agregación y las etapas avanzadas, como `$group`, `$project`, y `$unwind`, permiten realizar operaciones sofisticadas con los datos. Además, `explain()` es una herramienta clave para optimizar las agregaciones.

---
### 8. Replicación y Alta Disponibilidad en MongoDB

La **replicación** en MongoDB es un mecanismo que permite mantener múltiples copias de los datos en diferentes servidores para garantizar la **alta disponibilidad** y la **tolerancia a fallos**. Este sistema asegura que, en caso de que un servidor falle, los datos permanezcan accesibles desde otros servidores. La replicación se logra mediante un conjunto de réplicas o **Replica Set**, donde una copia actúa como el servidor principal y las otras como réplicas secundarias.

#### **1. ¿Qué es la Replicación en MongoDB?**

La **replicación** es el proceso de sincronizar datos en múltiples servidores, de modo que si un servidor falla, los datos aún estén disponibles en otros servidores. En MongoDB, esto se logra mediante **Replica Sets**.

Un **Replica Set** es un grupo de instancias de MongoDB que mantienen el mismo conjunto de datos. Un Replica Set incluye:
- **Un nodo primario** (Primary): Recibe todas las operaciones de escritura.
- **Uno o más nodos secundarios** (Secondary): Mantienen una copia de los datos replicados del primario.
- **Opcionalmente, un nodo árbitro** (Arbiter): No almacena datos, pero ayuda a decidir qué nodo se convierte en el primario en caso de una elección.

**Ventajas de la Replicación:**
- **Alta disponibilidad**: Si el nodo primario falla, los nodos secundarios pueden ser promovidos a primarios, manteniendo la disponibilidad de la base de datos.
- **Escalabilidad de lectura**: Los clientes pueden leer datos de los nodos secundarios, distribuyendo la carga de lectura.
- **Tolerancia a fallos**: Si un servidor deja de estar disponible, los otros servidores pueden continuar operando sin interrupción.

---

#### **2. Componentes de un Replica Set**
##### **1. Nodo Primario (Primary Node)**

El **nodo primario** es el único nodo que puede recibir operaciones de escritura en un Replica Set. Todas las operaciones de escritura se replican desde el nodo primario a los nodos secundarios. Si el nodo primario falla, uno de los secundarios será promovido a primario mediante un proceso de elección.

##### **2. Nodo Secundario (Secondary Node)**

Los **nodos secundarios** replican los datos del nodo primario y pueden configurarse para recibir operaciones de lectura, lo que distribuye la carga de lectura entre varios nodos. Si el primario falla, un nodo secundario es elegido automáticamente como el nuevo primario.

##### **3. Árbitro (Arbiter)**

Un **árbitro** es un nodo especial en un Replica Set que no almacena datos, pero participa en las elecciones de nodos. Los árbitros se utilizan para romper empates en caso de fallos del primario, pero no afectan al rendimiento de lectura o escritura.

---

#### **3. Configuración de un Replica Set**

Para configurar un Replica Set en MongoDB, necesitas iniciar múltiples instancias de MongoDB, preferiblemente en diferentes servidores (o en el mismo servidor en diferentes puertos para propósitos de prueba), y configurar los nodos para que trabajen juntos como un Replica Set.

##### **1. Iniciar MongoDB en Modo Replica Set**

Debes iniciar cada instancia de MongoDB con la opción `--replSet` para indicar que formará parte de un Replica Set.

**Comandos para iniciar tres instancias de MongoDB (en diferentes puertos):**
```bash
mongod --replSet rs0 --port 27017 --dbpath /data/db1 --bind_ip localhost
mongod --replSet rs0 --port 27018 --dbpath /data/db2 --bind_ip localhost
mongod --replSet rs0 --port 27019 --dbpath /data/db3 --bind_ip localhost
```

##### **2. Iniciar el Replica Set**

Conéctate a una de las instancias (normalmente la primera) usando `mongo` y ejecuta los comandos para iniciar el Replica Set.

**Iniciar el Replica Set:**
```javascript
rs.initiate({
  _id: "rs0",
  members: [
    { _id: 0, host: "localhost:27017" },
    { _id: 1, host: "localhost:27018" },
    { _id: 2, host: "localhost:27019" }
  ]
})
```

Este comando configura el Replica Set con tres miembros en los puertos `27017`, `27018` y `27019`.

##### **3. Ver el Estado del Replica Set**

Puedes verificar el estado del Replica Set con el comando `rs.status()`, que te proporcionará información sobre el estado de cada nodo (primario o secundario).

**Ver el estado:**
```javascript
rs.status()
```

**Resultado esperado:**
```bash
{
  "set" : "rs0",
  "members" : [
    {
      "_id" : 0,
      "name" : "localhost:27017",
      "stateStr" : "PRIMARY"
    },
    {
      "_id" : 1,
      "name" : "localhost:27018",
      "stateStr" : "SECONDARY"
    },
    {
      "_id" : 2,
      "name" : "localhost:27019",
      "stateStr" : "SECONDARY"
    }
  ]
}
```

---

#### **4. Proceso de Elección**

Cuando el **nodo primario** falla, los nodos secundarios votan para elegir un nuevo nodo primario. Este proceso se denomina **elección** y generalmente toma unos segundos.

##### **Causas para una Elección:**
- El nodo primario falla o se desconecta.
- El nodo primario es degradado manualmente.
- Se añade un nuevo nodo al Replica Set.

El Replica Set requiere una mayoría (más de la mitad de los nodos) para votar y elegir un nuevo nodo primario. Si no se puede alcanzar una mayoría, el Replica Set entra en un estado de "sin primario" hasta que se restaure el quorum.

---

#### **5. Escalabilidad de Lectura con Nodos Secundarios**

Por defecto, MongoDB solo permite que las lecturas ocurran en el **nodo primario**, para asegurar consistencia en los datos. Sin embargo, puedes configurar lecturas en los nodos secundarios para mejorar la escalabilidad de lectura. Esto puede ser útil en sistemas donde se prioriza la disponibilidad y la velocidad sobre la consistencia fuerte.

##### **Permitir Lecturas en los Secundarios**:

Puedes configurar un cliente para que lea desde los nodos secundarios utilizando la opción **readPreference**.

**Ejemplo de Configuración de Lectura en Secundarios:**
```javascript
db.getMongo().setReadPref("secondaryPreferred")
```

Este comando permite que las lecturas ocurran preferentemente en los nodos secundarios, aunque también puede leer del primario si no hay secundarios disponibles.

---

#### **6. Ventajas y Desventajas de la Replicación en MongoDB**

##### **Ventajas:**
- **Alta disponibilidad**: Si el primario falla, los secundarios pueden tomar su lugar automáticamente.
- **Escalabilidad de lectura**: Puedes distribuir la carga de lectura entre los nodos secundarios.
- **Recuperación ante desastres**: La replicación asegura que, en caso de fallo del hardware o software, los datos no se pierdan.

##### **Desventajas:**
- **Sobrecarga de escritura**: Mantener los datos replicados en múltiples nodos introduce una sobrecarga en las operaciones de escritura.
- **Consistencia eventual**: Las lecturas desde nodos secundarios pueden devolver datos que aún no están sincronizados con el primario, lo que puede ser un problema si se necesita consistencia fuerte.

---

#### **7. Consideraciones sobre la Consistencia**

MongoDB ofrece diferentes **niveles de consistencia** dependiendo de dónde se realicen las lecturas (primario o secundarios) y cómo se configuran las réplicas. La **consistencia eventual** significa que los nodos secundarios pueden tardar un breve período en ponerse al día con el nodo primario, lo que podría resultar en datos desactualizados cuando se leen desde los secundarios.

- **Consistencia Fuerte**: Si las lecturas se hacen solo desde el nodo primario, los datos siempre estarán actualizados y consistentes.
- **Consistencia Eventual**: Las lecturas en los nodos secundarios pueden estar desactualizadas, ya que los datos tardan un tiempo en replicarse.

---

### Conclusión

La replicación en MongoDB es una técnica esencial para garantizar la **alta disponibilidad** y la **tolerancia a fallos** en entornos distribuidos. Los **Replica Sets** permiten que múltiples servidores mantengan copias idénticas de los datos, lo que asegura que, incluso si un servidor falla, los datos siguen estando accesibles. La **escabilidad de lectura** es otra ventaja importante, ya que las lecturas pueden distribuirse entre los nodos secundarios. Sin embargo, la replicación introduce algunos desafíos relacionados con la consistencia de los datos y la sobrecarga de las operaciones de escritura.

---
### 9. Sharding en MongoDB

El **sharding** es una técnica de escalabilidad horizontal que MongoDB utiliza para distribuir grandes conjuntos de datos y cargas de trabajo a través de múltiples servidores. Esto permite manejar bases de datos de gran tamaño y mejorar el rendimiento, repartiendo los datos en fragmentos o "shards". Cada shard almacena una parte de los datos, y MongoDB se encarga de distribuir las consultas y operaciones de escritura entre ellos de manera eficiente.

#### **1. ¿Qué es el Sharding?**

**Sharding** es el proceso de dividir datos en partes más pequeñas llamadas **shards**. Cada shard es una base de datos MongoDB completa que contiene un subconjunto de los datos, y juntos forman una sola base de datos lógica. MongoDB decide automáticamente en qué shard se almacenan los documentos en función de una clave de fragmentación.

##### **Ventajas del Sharding:**
- **Escalabilidad horizontal**: Distribuye datos y operaciones entre varios servidores, lo que permite manejar grandes volúmenes de datos y mejorar el rendimiento.
- **Carga equilibrada**: El sharding permite distribuir la carga de trabajo entre diferentes shards, evitando que un solo servidor tenga que procesar todas las consultas y operaciones.
- **Alta disponibilidad**: Los shards pueden replicarse usando Replica Sets, lo que garantiza que los datos estarán disponibles incluso en caso de fallo de uno o más servidores.

---

#### **2. Componentes del Sharding en MongoDB**

El sharding en MongoDB involucra varios componentes clave:

##### **1. Shards**
Cada shard contiene una parte del conjunto de datos. Los shards son bases de datos MongoDB completas y normalmente se configuran como **Replica Sets** para garantizar la alta disponibilidad y la redundancia de datos.

##### **2. Router de Consultas (mongos)**
El **router de consultas** (o `mongos`) es el componente que recibe las solicitudes de los clientes y las distribuye a los shards correspondientes. El router se encarga de determinar qué shard contiene los datos solicitados y dirige la consulta o la operación de escritura a ese shard específico.

##### **3. Config Servers**
Los **Config Servers** son instancias de MongoDB que almacenan metadatos sobre la estructura del clúster, como la asignación de fragmentos y la configuración del sharding. Estos servidores contienen la información sobre qué shard contiene qué datos y ayudan a coordinar las operaciones de sharding.

---

#### **3. Proceso de Sharding**

El proceso de sharding comienza cuando se habilita el sharding en una colección, especificando una clave de fragmentación (shard key). MongoDB divide los datos de la colección en **rangos** basados en esa clave y asigna esos rangos a los diferentes shards.

##### **Shard Key (Clave de Fragmentación)**
La **Shard Key** es un campo o conjunto de campos dentro de los documentos de una colección que MongoDB usa para distribuir los datos entre los shards. Elegir una buena shard key es crucial para el rendimiento del clúster, ya que determina cómo los datos se distribuyen y balancean entre los shards.

- **Características de una buena Shard Key**:
  - **Cardinalidad Alta**: Debe tener muchos valores únicos para que los datos se distribuyan uniformemente entre los shards.
  - **Distribución Uniforme**: Debe permitir que los documentos se distribuyan equitativamente entre los shards, evitando que uno o pocos shards manejen la mayoría de los datos o consultas.
  - **Consultas Comunes**: Debe estar relacionada con los campos más utilizados en las consultas para maximizar la eficiencia.

**Ejemplo** de Shard Key: El campo `usuario_id` en una colección de usuarios puede ser una buena shard key, ya que probablemente tiene muchos valores únicos y distribuye bien los datos.

---

#### **4. Configuración del Sharding**

##### **1. Habilitar el Sharding en una Base de Datos**
Antes de shardear una colección, debes habilitar el sharding en la base de datos.

**Ejemplo:**
```javascript
sh.enableSharding("mi_base_de_datos")
```

##### **2. Shardear una Colección**
Para shardear una colección, debes definir una shard key. Aquí se usa el campo `usuario_id` como la shard key.

**Ejemplo:**
```javascript
sh.shardCollection("mi_base_de_datos.usuarios", { "usuario_id": 1 })
```

Este comando indica a MongoDB que comience a dividir la colección `usuarios` en fragmentos basados en el valor de `usuario_id`, distribuyendo los documentos entre los shards disponibles.

##### **3. Ver el Estado del Sharding**
Puedes ver el estado del clúster sharded y verificar qué datos están en cada shard con el comando:

**Ejemplo:**
```javascript
sh.status()
```

Este comando muestra la configuración de los shards, las colecciones shardeadas y la distribución de los datos.

---

#### **5. Tipos de Sharding en MongoDB**

Existen dos tipos principales de sharding en MongoDB:

##### **Sharding por Rango (Range Sharding)**
En el **sharding por rango**, MongoDB distribuye los documentos en fragmentos basándose en un rango de valores de la shard key. Por ejemplo, los documentos con `usuario_id` entre 1 y 1000 pueden ir al Shard 1, mientras que los que tienen `usuario_id` entre 1001 y 2000 pueden ir al Shard 2.

Este tipo de sharding es útil cuando los valores de la shard key son numéricos o secuenciales.

##### **Sharding por Hash (Hashed Sharding)**
En el **sharding por hash**, MongoDB aplica una función hash a la shard key para distribuir los documentos entre los shards de manera más uniforme, incluso si los valores de la clave no están distribuidos equitativamente.

Este tipo de sharding es útil cuando no hay una distribución natural de los datos (por ejemplo, si los valores de la shard key están muy concentrados en un rango específico).

---

#### **6. Balanceo de Fragmentos**

MongoDB incluye un **balanceador automático** que redistribuye los fragmentos de datos entre los shards cuando algunos de ellos están desbalanceados (por ejemplo, si un shard contiene significativamente más datos que los otros). El balanceador se ejecuta en segundo plano y migra fragmentos de un shard a otro para equilibrar la carga.

El balanceador asegura que:
- Los datos se distribuyan equitativamente entre los shards.
- Las cargas de lectura y escritura se mantengan equilibradas.

El balanceo no interrumpe las operaciones de lectura o escritura, ya que MongoDB maneja el proceso de manera transparente.

---

#### **7. Ventajas y Desventajas del Sharding**

##### **Ventajas del Sharding**:
- **Escalabilidad Horizontal**: Al distribuir los datos entre múltiples servidores, MongoDB puede manejar grandes volúmenes de datos y altas tasas de operaciones.
- **Distribución de Carga**: El sharding permite que los datos y las consultas se distribuyan entre varios servidores, lo que ayuda a evitar que un solo servidor se sobrecargue.
- **Alta Disponibilidad**: Los shards pueden configurarse como Replica Sets, lo que asegura que los datos sean redundantes y estén disponibles incluso si uno o más shards fallan.

##### **Desventajas del Sharding**:
- **Complejidad de Configuración**: Configurar y administrar un clúster sharded requiere un conocimiento avanzado de MongoDB y puede ser complejo de gestionar.
- **Impacto en las Consultas**: Las consultas que no utilizan la shard key correctamente pueden ser menos eficientes, ya que podrían requerir que MongoDB consulte todos los shards.
- **Reparto de Carga no Óptimo**: Si la shard key no se elige correctamente, algunos shards pueden recibir más datos o consultas que otros, resultando en un desequilibrio de carga.

---

#### **8. Escenarios para Usar Sharding**

##### **Cuándo Usar Sharding**:
- **Grandes volúmenes de datos**: Si tu base de datos crece rápidamente y supera la capacidad de un solo servidor, el sharding es una solución adecuada.
- **Altas tasas de lectura y escritura**: Si tu aplicación tiene muchas operaciones de lectura y escritura, el sharding permite distribuir esas operaciones entre varios servidores, mejorando el rendimiento.
- **Carga desigual**: Si ciertos conjuntos de datos o consultas consumen una cantidad desproporcionada de recursos, el sharding permite dividir la carga entre varios servidores.

##### **Cuándo Evitar el Sharding**:
- **Bases de datos pequeñas**: Si tus datos y tu tráfico son lo suficientemente pequeños como para ser manejados por un solo servidor, no necesitas sharding.
- **Consultas simples**: Si tus consultas son simples y no requieren una distribución compleja, el sharding puede agregar complejidad innecesaria.

---

### Conclusión

El **sharding** en MongoDB es una técnica poderosa para **escalar horizontalmente** bases de datos que manejan grandes volúmenes de datos o tráfico. Al distribuir los datos entre múltiples shards, MongoDB permite que la base de datos crezca y maneje más operaciones de manera eficiente. Sin embargo, configurar un clúster sharded requiere planificación cuidadosa, especialmente en la elección de la shard key, para garantizar un rendimiento óptimo y una carga equilibrada entre los servidores.

---

### 10. Seguridad en MongoDB

La **seguridad** en MongoDB es fundamental para proteger los datos de acceso no autorizado, ataques y otras amenazas potenciales. MongoDB ofrece varias características de seguridad, que incluyen autenticación, autorización, cifrado de datos, auditoría y medidas de seguridad en red. Estas características permiten asegurar tanto el acceso a los datos como su integridad.

#### **1. Autenticación en MongoDB**

La **autenticación** es el proceso de verificar la identidad de un usuario antes de que pueda acceder a la base de datos. MongoDB proporciona varios métodos de autenticación para garantizar que solo los usuarios autorizados puedan acceder al sistema.

##### **Tipos de Autenticación en MongoDB:**

##### **1. Autenticación basada en roles (SCRAM - Salted Challenge Response Authentication Mechanism)**

Este es el método de autenticación más común en MongoDB y está habilitado por defecto. MongoDB utiliza SCRAM-SHA-1 o SCRAM-SHA-256 para autenticar usuarios.

**Pasos para habilitar la autenticación:**

1. **Crear un usuario administrador:**
   Antes de habilitar la autenticación, es necesario crear un usuario administrador en la base de datos `admin` con el rol `userAdminAnyDatabase`.

   **Ejemplo:**
   ```javascript
   use admin
   db.createUser({
      user: "admin",
      pwd: "password123",
      roles: [ { role: "userAdminAnyDatabase", db: "admin" } ]
   })
   ```

2. **Reiniciar MongoDB con la autenticación habilitada:**
   Para habilitar la autenticación, agrega la siguiente opción al archivo de configuración (`mongod.conf`) o inicia MongoDB con el parámetro `--auth`.

   **Ejemplo (archivo de configuración `mongod.conf`):**
   ```yaml
   security:
     authorization: "enabled"
   ```

   Luego, reinicia el servicio MongoDB.

3. **Iniciar sesión como usuario autenticado:**
   A partir de este momento, cualquier operación requerirá que los usuarios se autentiquen. Puedes iniciar sesión usando el comando `mongo` e indicando el usuario y la contraseña:

   **Ejemplo:**
   ```bash
   mongo --authenticationDatabase "admin" -u "admin" -p "password123"
   ```

##### **2. Autenticación LDAP**

MongoDB puede integrarse con un sistema de **LDAP** (Lightweight Directory Access Protocol) para gestionar la autenticación y el control de acceso desde un directorio centralizado. Esta opción es útil en entornos empresariales donde los usuarios y sus roles se gestionan a través de un sistema LDAP externo.

##### **3. Autenticación Kerberos**

**Kerberos** es otro protocolo de autenticación que MongoDB soporta. Es comúnmente utilizado en redes empresariales y proporciona un mecanismo seguro para la autenticación mutua entre los servidores MongoDB y los clientes.

##### **4. Certificados X.509**

MongoDB también permite la autenticación mediante **certificados X.509**, que son utilizados para la autenticación de clientes y servidores. Este método se utiliza a menudo en entornos donde la seguridad basada en certificados es requerida para garantizar la integridad y autenticación mutua.

---

#### **2. Autorización en MongoDB**

La **autorización** en MongoDB se refiere al proceso de verificar qué acciones puede realizar un usuario autenticado. MongoDB usa un sistema basado en **roles** para controlar los permisos de los usuarios, lo que significa que cada usuario tiene uno o más roles que definen las operaciones que puede realizar en la base de datos.

##### **Tipos de Roles en MongoDB:**

1. **Roles predefinidos:**
   MongoDB incluye varios roles predefinidos para diferentes niveles de acceso:

   - **userAdmin**: Permite crear y gestionar usuarios.
   - **dbAdmin**: Permite realizar tareas administrativas, como crear índices, obtener estadísticas de la base de datos y manejar las configuraciones.
   - **read**: Permite solo operaciones de lectura en una base de datos.
   - **readWrite**: Permite operaciones de lectura y escritura en una base de datos.

2. **Roles por bases de datos específicas:**
   Los roles pueden aplicarse a bases de datos específicas. Por ejemplo, puedes tener un usuario con acceso de solo lectura a una base de datos y acceso completo a otra.

**Ejemplo de creación de un usuario con permisos de lectura y escritura:**
```javascript
use mi_base_de_datos
db.createUser({
   user: "usuario1",
   pwd: "password123",
   roles: [ { role: "readWrite", db: "mi_base_de_datos" } ]
})
```

3. **Roles personalizados:**
   Además de los roles predefinidos, MongoDB permite la creación de **roles personalizados**, que proporcionan un control granular sobre las operaciones que un usuario puede realizar.

**Ejemplo de creación de un rol personalizado:**
```javascript
use admin
db.createRole({
   role: "analista",
   privileges: [
      { resource: { db: "mi_base_de_datos", collection: "" }, actions: [ "find", "listCollections" ] }
   ],
   roles: []
})
```

Este rol personalizado llamado `analista` permite realizar operaciones de lectura y listar colecciones en la base de datos `mi_base_de_datos`.

---

#### **3. Cifrado de Datos en MongoDB**

MongoDB ofrece varias opciones de **cifrado** para garantizar que los datos estén protegidos tanto en tránsito como en reposo.

##### **1. Cifrado en tránsito (TLS/SSL)**

El **cifrado en tránsito** asegura que los datos estén protegidos mientras se transfieren entre los clientes y servidores MongoDB. Esto se logra mediante la configuración de **TLS/SSL**, que cifra todas las comunicaciones.

**Habilitar TLS/SSL en MongoDB:**
- Genera o adquiere certificados TLS/SSL.
- Configura MongoDB para que utilice estos certificados.

**Ejemplo de configuración (`mongod.conf`):**
```yaml
net:
  ssl:
    mode: requireSSL
    PEMKeyFile: /etc/ssl/mongodb.pem
```

##### **2. Cifrado en reposo**

El **cifrado en reposo** asegura que los datos estén cifrados mientras se almacenan en disco. MongoDB ofrece **cifrado en reposo** a nivel de archivo utilizando el **MongoDB Enterprise Edition**.

**Ejemplo de configuración (`mongod.conf`):**
```yaml
security:
  enableEncryption: true
  encryptionKeyFile: /path/to/encryption-keyfile
```

---

#### **4. Seguridad en la Red**

Es importante proteger el acceso a los servidores MongoDB mediante medidas de seguridad en la red. Aquí algunas buenas prácticas:

##### **1. Uso de Firewalls**

Configura **firewalls** para restringir el acceso a los puertos de MongoDB. Solo los clientes y servidores autorizados deben poder acceder a los servidores MongoDB. El puerto por defecto de MongoDB es el **27017**, pero puedes cambiarlo en la configuración.

**Ejemplo de configuración en `mongod.conf`:**
```yaml
net:
  bindIp: 127.0.0.1,192.168.0.100
  port: 27017
```

Este ejemplo limita el acceso a MongoDB solo a conexiones locales (`127.0.0.1`) y a una dirección IP específica (`192.168.0.100`).

##### **2. Deshabilitar el Acceso Remoto a la Consola de Administración**

Es recomendable deshabilitar el acceso remoto a la consola de administración de MongoDB, ya que el acceso no autorizado a esta puede comprometer el sistema.

##### **3. Aislamiento de Redes (VPC)**

En entornos en la nube, es recomendable alojar los servidores MongoDB dentro de una **VPC (Virtual Private Cloud)** y usar **VPN** o **túneles seguros** para el acceso externo. Esto limita el acceso público y protege las bases de datos de ataques en red.

---

#### **5. Auditoría en MongoDB**

MongoDB Enterprise incluye características de **auditoría** que permiten rastrear todas las acciones de los usuarios en la base de datos, lo cual es crucial para cumplir con regulaciones como **GDPR** o **HIPAA**.

La auditoría permite registrar eventos como:
- Operaciones de lectura y escritura en documentos.
- Creación y eliminación de bases de datos.
- Cambios en la configuración de usuarios y roles.

**Configuración básica de auditoría:**
```yaml
auditLog:
  destination: file
  format: JSON
  path: /var/log/mongodb/auditLog.json
```

---

#### **6. Buenas Prácticas de Seguridad en MongoDB**

1. **Habilitar autenticación y autorización**: Nunca dejes una instancia de MongoDB sin autenticación habilitada.
2. **Usar TLS/SSL para comunicaciones seguras**: Asegúrate de que todas las conexiones estén cifradas para evitar la intercepción de datos.
3. **Aislar la red**: Utiliza firewalls y VPC para proteger los servidores MongoDB de accesos no autorizados.
4. **Configurar roles de usuario limitados**: Asigna a los usuarios solo los permisos que necesitan.
5. **Realizar auditorías periódicas**: Revisa regularmente los logs de auditoría para detectar posibles intentos de acceso no autorizados.

---

### Conclusión

La **seguridad en MongoDB** es esencial para proteger los datos y garantizar que solo los usuarios autorizados puedan acceder a ellos. Con una

 combinación de autenticación robusta, control de acceso basado en roles, cifrado de datos y medidas de seguridad en la red, MongoDB ofrece un entorno seguro para manejar bases de datos críticas. Además, el monitoreo mediante auditorías permite mantener el control sobre las operaciones y detectar actividades sospechosas.

---
### Anexos: Comparación de MongoDB con SQL

En este anexo, ampliamos las secciones 2, 3, 4, 5, 6 y 7, estableciendo comparaciones con SQL, dado que los estudiantes ya tienen conocimientos previos en bases de datos relacionales.

---

### Anexo 1: Documentos y Colecciones vs. Tablas y Filas (Ampliación de la Sección 2)

En bases de datos relacionales, los datos se almacenan en **tablas**, y cada fila en una tabla es un registro único. En MongoDB, los datos se almacenan en **colecciones**, y cada documento en una colección es similar a una fila en una tabla.

| Característica              | MongoDB (Documentos y Colecciones)      | SQL (Tablas y Filas)                      |
|-----------------------------|-----------------------------------------|-------------------------------------------|
| **Esquema**                 | No es necesario definir un esquema      | Esquema rígido definido por tablas        |
| **Documentos/Filas**        | Documentos pueden tener diferentes campos | Todas las filas tienen la misma estructura |
| **Datos anidados**          | Soporta subdocumentos y arreglos        | Se utiliza normalización o relaciones     |
| **Consultas**               | Filtrado con `find()` y operadores JSON | Consultas con `SELECT`, `WHERE`, `JOIN`   |

#### Comparación de estructuras:

##### SQL:
```sql
CREATE TABLE productos (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(255),
    precio DECIMAL,
    cantidad INT,
    categoria VARCHAR(255)
);
```

##### MongoDB:
```json
{
    "nombre": "Laptop",
    "precio": 999.99,
    "cantidad": 10,
    "categoria": "Electrónica"
}
```

En SQL, todas las filas deben ajustarse a un esquema predefinido, mientras que en MongoDB cada documento puede tener campos adicionales o diferentes.

---

### Anexo 2: Bases de Datos en MongoDB vs. SQL (Ampliación de la Sección 3)

En MongoDB, las bases de datos son contenedores lógicos que agrupan colecciones, al igual que en SQL, donde las bases de datos agrupan tablas. Sin embargo, la principal diferencia está en la flexibilidad de MongoDB en cuanto a la estructura de los datos.

| Operación                        | MongoDB                              | SQL                                      |
|-----------------------------------|---------------------------------------|------------------------------------------|
| **Crear base de datos**           | `use nombre_base_datos`               | `CREATE DATABASE nombre_base_datos`      |
| **Crear tabla/colección**         | Colección creada automáticamente      | `CREATE TABLE nombre_tabla (...)`        |
| **Eliminar base de datos**        | `db.dropDatabase()`                   | `DROP DATABASE nombre_base_datos`        |
| **Eliminar tabla/colección**      | `db.nombre_coleccion.drop()`          | `DROP TABLE nombre_tabla`                |

En MongoDB, no es necesario predefinir una colección antes de insertar datos. La base de datos y la colección se crean automáticamente cuando se insertan documentos.

---

### Anexo 3: Operaciones CRUD en MongoDB vs. SQL (Ampliación de la Sección 4)

Las operaciones CRUD (Crear, Leer, Actualizar, Borrar) son fundamentales tanto en MongoDB como en SQL, pero la forma en que se ejecutan difiere debido a las diferencias en el modelo de datos.

#### Crear (Insertar Datos)

##### SQL:
```sql
INSERT INTO productos (nombre, precio, cantidad, categoria)
VALUES ('Laptop', 999.99, 10, 'Electrónica');
```

##### MongoDB:
```javascript
db.productos.insertOne({
    "nombre": "Laptop",
    "precio": 999.99,
    "cantidad": 10,
    "categoria": "Electrónica"
});
```

En SQL, se utiliza el comando `INSERT INTO` y los nombres de las columnas deben coincidir con el esquema de la tabla. En MongoDB, no es necesario predefinir las columnas.

#### Leer (Consultar Datos)

##### SQL:
```sql
SELECT * FROM productos WHERE precio > 500;
```

##### MongoDB:
```javascript
db.productos.find({ "precio": { $gt: 500 } });
```

En MongoDB, se utilizan operadores JSON para filtrar los documentos, como `$gt` (mayor que). SQL utiliza cláusulas como `WHERE` para filtrar las filas de una tabla.

#### Actualizar

##### SQL:
```sql
UPDATE productos SET precio = 899.99 WHERE nombre = 'Laptop';
```

##### MongoDB:
```javascript
db.productos.updateOne(
    { "nombre": "Laptop" },
    { $set: { "precio": 899.99 } }
);
```

Tanto en SQL como en MongoDB, se pueden actualizar registros específicos, pero en MongoDB se utiliza `$set` para modificar campos específicos.

#### Borrar

##### SQL:
```sql
DELETE FROM productos WHERE nombre = 'Laptop';
```

##### MongoDB:
```javascript
db.productos.deleteOne({ "nombre": "Laptop" });
```

Las operaciones de eliminación son similares, con la diferencia de que MongoDB usa métodos específicos como `deleteOne()` o `deleteMany()`.

---

### Anexo 4: Consultas en MongoDB vs. SQL (Ampliación de la Sección 5)

Las consultas en MongoDB se basan en operadores JSON, mientras que en SQL se utilizan cláusulas como `SELECT`, `WHERE` y `JOIN`.

| Operación                           | MongoDB                                         | SQL                                      |
|--------------------------------------|-------------------------------------------------|------------------------------------------|
| **Seleccionar todos los documentos/filas** | `db.productos.find()`                          | `SELECT * FROM productos`                |
| **Filtrar por un valor**             | `db.productos.find({ "precio": 999.99 })`       | `SELECT * FROM productos WHERE precio = 999.99` |
| **Filtrar por rango**                | `db.productos.find({ "precio": { $gt: 500 } })` | `SELECT * FROM productos WHERE precio > 500` |
| **Proyección de campos**             | `db.productos.find({}, { "nombre": 1, "_id": 0 })` | `SELECT nombre FROM productos`           |

En MongoDB, los operadores de comparación, como `$gt` (mayor que) o `$lt` (menor que), son equivalentes a las cláusulas `>` y `<` de SQL.

#### Consultas avanzadas con operadores lógicos

##### SQL:
```sql
SELECT * FROM productos WHERE precio > 500 AND categoria = 'Electrónica';
```

##### MongoDB:
```javascript
db.productos.find({ $and: [ { "precio": { $gt: 500 } }, { "categoria": "Electrónica" } ] });
```

En MongoDB, se pueden combinar condiciones usando operadores como `$and` o `$or`, similar a las cláusulas `AND` y `OR` en SQL.

---

### Anexo 5: Índices en MongoDB vs. SQL (Ampliación de la Sección 6)

Los índices mejoran el rendimiento de las consultas tanto en SQL como en MongoDB, aunque su implementación y sintaxis son diferentes.

| Operación                  | MongoDB                                     | SQL                                      |
|----------------------------|---------------------------------------------|------------------------------------------|
| **Crear índice**            | `db.productos.createIndex({ "precio": 1 })` | `CREATE INDEX idx_precio ON productos(precio)` |
| **Ver índices**             | `db.productos.getIndexes()`                 | `SHOW INDEXES FROM productos`            |
| **Eliminar índice**         | `db.productos.dropIndex("nombre_del_indice")` | `DROP INDEX idx_precio ON productos`     |

En SQL, los índices se crean explícitamente para mejorar el rendimiento de las consultas en columnas específicas. En MongoDB, se pueden crear índices en cualquier campo, y se pueden utilizar en consultas con operadores JSON.

#### Índices compuestos

##### SQL:
```sql
CREATE INDEX idx_precio_categoria ON productos(precio, categoria);
```

##### MongoDB:
```javascript
db.productos.createIndex({ "precio": 1, "categoria": 1 });
```

Tanto en MongoDB como en SQL, los índices compuestos permiten mejorar el rendimiento de las consultas que filtran por múltiples campos.

---

### Anexo 6: Agregaciones en MongoDB vs. SQL (Ampliación de la Sección 7)

MongoDB y SQL tienen mecanismos para realizar operaciones de agregación, como **sumar**, **promediar**, **contar** y **agrupar** datos. En SQL, esto se hace con `GROUP BY`, mientras que en MongoDB se utiliza el **Aggregation Framework**.

#### Agrupaciones (GROUP BY en SQL)

##### SQL:
```sql
SELECT categoria, AVG(precio) FROM productos GROUP BY categoria;
```

##### MongoDB:
```javascript
db.productos.aggregate([
    { $group: { _id: "$categoria", precioPromedio: { $avg: "$precio" } } }
])
```

En SQL, `GROUP BY` agrupa los datos por una o más columnas y luego realiza operaciones de agregación. En MongoDB, `$group` agrupa los documentos y realiza cálculos como `$avg` (promedio) o `$sum` (suma).

#### Filtrar y agrupar (HAVING en SQL)

##### SQL:
```sql
SELECT categoria, AVG(precio) FROM productos GROUP BY categoria HAVING AVG(precio) > 500;
```

##### MongoDB:


```javascript
db.productos.aggregate([
    { $group: { _id: "$categoria", precioPromedio: { $avg: "$precio" } } },
    { $match: { precioPromedio: { $gt: 500 } } }
])
```

En SQL, `HAVING` se utiliza para filtrar los resultados de una agrupación, mientras que en MongoDB se usa `$match` después de `$group` para filtrar los resultados agregados.

---

### Conclusión del Anexo

Este anexo ha proporcionado una comparación detallada de las operaciones comunes entre **MongoDB** y **SQL**, facilitando la transición de los estudiantes que ya están familiarizados con las bases de datos relacionales. MongoDB ofrece una mayor flexibilidad con su modelo de documentos y su capacidad para trabajar con datos no estructurados, mientras que SQL mantiene un esquema rígido pero altamente optimizado para transacciones estructuradas. Sin embargo, los conceptos fundamentales, como el manejo de índices, consultas y agregaciones, se pueden trasladar fácilmente entre ambos sistemas.
