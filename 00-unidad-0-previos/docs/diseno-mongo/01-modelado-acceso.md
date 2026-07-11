# Modelado por acceso a datos

> **Notas didácticas**
> - Tiempo estimado: 90-120 min teoría + ejercicio autónomo
> - Método: Explicación → demo en vivo → ejercicio autónomo → día de dudas posterior

## 1. Introducción

Uno de los errores más comunes al empezar con MongoDB es diseñar la base de datos como si fuera una base relacional. Ese enfoque suele producir colecciones excesivamente normalizadas, consultas con demasiados `$lookup` y estructuras que ignoran la ventaja principal de MongoDB: **guardar juntos los datos que se consultan juntos**.

En SQL modelamos por **entidad**: normalizamos para evitar redundancia y luego reconstruimos con JOINs. En MongoDB el diseño debe hacerse por **acceso a datos**: primero se analiza cómo se leen y escriben los datos, y después se decide qué embeker y qué referenciar.

La pregunta correcta no es "¿qué entidad existe?", sino **"¿qué necesito leer junto en una consulta real?"**.

## 2. Metodología de modelado (3 fases)

MongoDB University propone esta metodología estructurada:

```
FASE 1: Definir la carga (workload)
├── Comprender para qué operaciones modelamos
├── Cuantificar y calificar lecturas/escrituras
└── Listar las operaciones más importantes

FASE 2: Modelar las relaciones
├── 1:1 → normalmente embebido
├── 1:F (pocos) → array embebido
├── 1:N (muchos) → referencia en el lado "muchos"
├── 1:S (muchísimos) → referencia inversa (el "muchos" apunta al "uno")
└── N:M → array de referencias en uno de los lados

FASE 3: Aplicar patrones de diseño
└── Transformaciones sobre el esquema (rendimiento, mantenimiento, simplificación)
```

### Simplicidad vs Rendimiento

| Objetivo | Simplicidad | Mixto | Rendimiento |
|----------|-------------|-------|-------------|
| Fase 1 | Operaciones más frecuentes | Mayoría de operaciones + tamaño | Todas las operaciones + tamaño + criticidad |
| Fase 2 | Embeber siempre que se pueda | Embeber y referenciar | Embeber y referenciar |
| Fase 3 | Patrón A | Patrones A y B | Patrones A, B y C |

> **Regla práctica:** es más fácil optimizar código para mejorar rendimiento que simplificar un esquema complejo _a posteriori_. **Siempre empieza por simplicidad.**

## 3. Embedding vs Referencing

MongoDB ofrece dos formas de relacionar datos:

- **Embedding**: documentos anidados dentro de otro documento.
- **Referencing**: guardar el ID de otro documento y consultar aparte.

### Cuándo embeker

- La relación es **1:1** o **1:F (pocos)** — p. ej., posts y comentarios (~500 máx)
- Los datos se leen **siempre juntos**
- El subdocumento **no crece sin límite** (vigilar límite de 16 MB BSON)
- Necesitas **actualización atómica** del conjunto

### Cuándo referenciar

- La relación es **1:N (muchos)** o **1:S (muchísimos)** — p. ej., editorial y libros
- Los datos se leen **por separado** con frecuencia
- El tamaño puede crecer sin control (logs, eventos)
- Arrays de alta cardinalidad (> unos pocos miles)

### Tabla comparativa

| Criterio | Embedding | Referencing |
|----------|-----------|-------------|
| Cardinalidad | 1:1, 1:F (few) | 1:N, 1:S (squillions) |
| Frecuencia de acceso | Se lee siempre junto | Se lee separado a menudo |
| Tamaño | Controlado, predecible | Puede crecer sin límite |
| Atomicidad | Útil si se actualiza junto | Mejor si son entidades independientes |
| Consulta | Una sola lectura | Requiere `$lookup` o múltiples queries |

### Regla práctica

> **Si siempre lees X con Y, y X no crece sin límite → embebe X en Y.**

## 4. Tipos de relación en detalle

### 1:1 — Embeber siempre que se pueda

```javascript
// Persona + dirección: siempre se leen juntos
db.people.insertOne({
  name: "Aitor",
  age: 48,
  address: {
    street: "Secreta",
    city: "Elx"
  }
});
```

Excepción: si una parte se accede raramente o es mucho más grande, valorar separar.

### 1:F (one-to-few) — Array embebido

```javascript
// Post + comentarios (~500 máx, se leen juntos)
db.posts.insertOne({
  title: "Modelado en MongoDB",
  comments: [
    { author: "Ana", text: "Muy útil", createdAt: ISODate() },
    { author: "Luis", text: "Coincido", createdAt: ISODate() }
  ]
});
```

**Cuidado con el tamaño:** un documento BSON tiene un límite de 16 MB. Usa `bsonsize()` para comprobarlo:

```javascript
var p = db.posts.findOne()
bsonsize(p)
```

### 1:N (one-to-many) — Referencia en el lado "muchos"

```javascript
// Editorial + libros
db.editorial.insertOne({ _id: 1, name: "O'Reilly", country: "USA" });

db.book.insertOne({
  title: "MongoDB: The Definitive Guide",
  editorial_id: 1,  // ← referencia manual
  pages: 216
});
```

### 1:S (one-to-squillions) — Referencia inversa

Cuando la cardinalidad es enorme (logs, eventos, métricas), **no** pongas un array en el lado "uno". Pon el ID del "uno" en cada documento del lado "muchos":

```javascript
// App + logs (millones de entradas)
db.app.insertOne({ _id: 1, name: "Mi App" });

db.log.insertOne({
  app_id: 1,  // ← el "muchos" apunta al "uno"
  activity: "User login",
  message: "Login successful",
  timestamp: ISODate()
});
```

### N:M — Array de referencias

Dependiendo de la cardinalidad real de N y M:

| Si N ≤ 3 y M ≤ 5 | Array en ambos lados (two-way embedding) |
| Si N ≤ 3 y M grande | Embed N dentro de M (one-way) |
| Otros casos | Array de referencias en un lado |

```javascript
// Libro + Autores (N:M, con array de referencias en libros)
db.book.insertOne({
  _id: 1,
  title: "La historia interminable",
  authors: [1, 2]  // ObjectIds de la colección authors
});
```

> ⚠️ **DBRef está deprecado.** No uses `{ $ref, $id, $db }`. Usa referencias manuales simples y `$lookup` si necesitas el JOIN.

## 5. Patrones de modelado (visión general)

Se organizan en tres categorías. Los veremos en detalle en sesiones posteriores.

### Representación (cómo se representa el esquema)

| Patrón | Cuándo usarlo |
|--------|---------------|
| **Atributo** | Un campo cambia de significado según el tipo (ej: precios por país) |
| **Versionado de documento** | Necesitas histórico de cambios en los documentos |
| **Versionado de esquema** | El esquema evoluciona y convive con docs antiguos (campo `schemaVersion`) |
| **Polimórfico** | Documentos con más similitudes que diferencias (ej: actores y directores en una misma colección con `type`) |

### Frecuencia de acceso (optimizar lecturas intensivas)

| Patrón | Cuándo usarlo |
|--------|---------------|
| **Subconjunto** | Documentos grandes de los que solo necesitas una parte (ej: portada de película vs ficha completa) |
| **Aproximación** | Muchísimas escrituras y el valor exacto no es crítico (ej: contador de visitas) |
| **Referencia extendida** | Duplicas datos de un documento referenciado para evitar `$lookup` (ej: datos del cliente embebidos en el pedido) |

### Agrupación (escalar lecturas)

| Patrón | Cuándo usarlo |
|--------|---------------|
| **Calculado** | Más lecturas que escrituras; precalculas en escritura para leer rápido (ej: total de pedido) |
| **Cubo (bucket)** | Series temporales o IoT; agrupas medidas en bloques por hora/día |
| **Atípico (outlier)** | Casos que se salen de la norma (ej: usuario con 1000 favoritos vs media de 50) |

## 6. Ejemplo guiado: sistema de blogs

### Análisis de carga (Fase 1)

| Operación | Tipo | Frecuencia | Datos implicados |
|-----------|------|------------|------------------|
| Ver post + comentarios | Lectura | Muy alta | title, content, author, comments |
| Listar posts del autor | Lectura | Alta | title, slug, publishedAt |
| Buscar por tag | Lectura | Media | title, tags |
| Publicar post | Escritura | Media | post completo |
| Añadir comentario | Escritura | Alta | postId + comment |

### Decisiones embed vs reference (Fase 2)

| Relación | Cardinalidad | Decisión | Justificación |
|----------|-------------|----------|---------------|
| Post → comentarios | 1:F (~500 máx) | ✅ Embed | Se leen siempre juntos, tamaño acotado |
| Post → autor | N:1 | ❌ Reference | Autor se consulta independientemente |
| Post → tags | N:F (pocos) | ✅ Embed | Siempre con el post, no crecen |
| Post → comments.replies | 1:F (1 nivel) | ✅ Embed | Contexto del comentario, tamaño acotado |

```javascript
// Esquema final
db.posts.insertOne({
  title: "Modelado en MongoDB",
  slug: "modelado-mongodb",
  content: "Contenido completo del post...",
  authorId: ObjectId("64f1a1a1a1a1a1a1a1a1a1a1"),
  tags: ["mongodb", "modelado"],
  status: "published",
  createdAt: ISODate("2026-06-16T10:00:00Z"),
  comments: [
    {
      authorName: "Ana",
      text: "Muy útil",
      createdAt: ISODate("2026-06-16T11:00:00Z"),
      replies: [
        { authorName: "Luis", text: "Coincido", createdAt: ISODate("2026-06-16T11:10:00Z") }
      ]
    }
  ]
});
```

### Modelado con MongoDB Compass

Desde 2025, MongoDB Compass permite [representar y modelar esquemas gráficamente](https://www.mongodb.com/docs/compass/current/data-modeling). Selecciona una conexión, BD y colecciones, y Compass infiere el esquema a partir de una muestra de datos. También permite crear esquemas manuales definiendo colecciones, atributos, tipos, índices y relaciones.

## 7. Ejercicio autónomo: sistema de e-commerce

Realiza el proceso completo de las 3 fases para este dominio:

### Entidades
- **Productos**: variantes (talla, color, precio, stock), categoría, atributos variables
- **Órdenes**: items (producto, cantidad, precio del momento), dirección envío, historial estados
- **Usuarios**: email, passwordHash, direcciones (hasta 5), wishlist (máx 50), historial compras
- **Reviews**: productoId, usuarioId, texto, rating (1-5), fecha

### Fase 1 — Define la carga
- ¿Qué operaciones son las más frecuentes?
- ¿Qué operaciones son críticas en rendimiento?

### Fase 2 — Decide embed vs reference
Para cada relación, usa la cardinalidad adecuada (1:1, 1:F, 1:N, 1:S, N:M)

### Fase 3 — Patrones
- ¿Aplicarías algún patrón de los vistos? ¿Cuál y por qué?

### Pistas
- **Producto + variantes**: cardinalidad 1:F (tallas/colores acotados) → embed
- **Órdenes + items**: embed (el precio debe congelarse en el momento de compra)
- **Reviews**: colección separada (1:S — un producto puede tener miles)
- **Usuario + wishlist**: embedding controlado si hay límite (50); referencia si es ilimitado

### Entregable
Documento de 1-2 páginas con:
1. Tabla de análisis de carga (5-6 operaciones)
2. Tabla de decisiones embed vs reference justificadas
3. Esquema JSON de ejemplo para una colección clave
4. Patrón identificado y justificación

## 8. Cierre

MongoDB no se diseña pensando primero en entidades, sino en **consultas reales**. Si el modelo acompaña al acceso, tendrás menos `$lookup`, menos complejidad y mejor rendimiento. Si no, acabarás usando MongoDB como una SQL mal disimulada.

**Referencia complementaria:** [Aitor Medrano — Modelado de datos documentales](https://aitor-medrano.github.io/iabd/sa/modelado.html) (base de este material).

## 🧩 Ejercicio autónomo

Entrega el documento de 1-2 páginas descrito en la sección 7.
