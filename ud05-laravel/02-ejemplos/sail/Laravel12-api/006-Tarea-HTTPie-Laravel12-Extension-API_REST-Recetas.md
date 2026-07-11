Perfecto. Te dejo **dos bloques listos para pegar**:

1. **Rúbrica con verificación integrada mediante HTTPie** (los comandos aparecen justo donde se evalúa cada criterio).
2. **Chuleta HTTPie A4** (compacta, pensada para imprimir o tener al lado en terminal).

---

# 📊 RÚBRICA DE EVALUACIÓN — con verificación mediante HTTPie integrada

> En los criterios funcionales se incluyen **comandos HTTPie concretos** que permiten comprobar objetivamente el comportamiento de la API.

---

## 1️⃣ Ingredientes en las recetas (OBLIGATORIO)

**Qué se evalúa**

* Relación receta–ingredientes
* Autorización por propietario/admin
* Endpoints claros

**Verificación con HTTPie**

Añadir ingrediente:

```bash
http POST :8000/api/recetas/1/ingredientes \
  "Authorization:Bearer $TOKEN" \
  nombre="Huevo" cantidad=3 unidad="ud"
```

Modificar ingrediente como intruso (debe fallar):

```bash
http PUT :8000/api/ingredientes/10 \
  "Authorization:Bearer $TOKEN_INTRUSO" \
  cantidad=5
```

**Resultado esperado**

* 201 al crear
* 403 si no es propietario/admin

---

## 2️⃣ Likes de recetas (OBLIGATORIO)

**Qué se evalúa**

* No duplicar likes
* Posibilidad de quitar like
* Control de errores

**Verificación con HTTPie**

Primer like:

```bash
http POST :8000/api/recetas/1/like \
  "Authorization:Bearer $TOKEN"
```

Segundo like (debe fallar):

```bash
http POST :8000/api/recetas/1/like \
  "Authorization:Bearer $TOKEN"
```

Respuesta esperada:

```json
{
  "error": {
    "code": "LIKE_DUPLICADO"
  }
}
```

Quitar like:

```bash
http DELETE :8000/api/recetas/1/like \
  "Authorization:Bearer $TOKEN"
```

---

## 3️⃣ Comentarios en recetas (OBLIGATORIO)

**Qué se evalúa**

* Asociación usuario–receta
* Solo el autor puede borrar

**Verificación con HTTPie**

Crear comentario:

```bash
http POST :8000/api/recetas/1/comentarios \
  "Authorization:Bearer $TOKEN" \
  texto="Muy buena receta"
```

Borrar como intruso (debe fallar):

```bash
http DELETE :8000/api/comentarios/5 \
  "Authorization:Bearer $TOKEN_INTRUSO"
```

Borrar como autor:

```bash
http DELETE :8000/api/comentarios/5 \
  "Authorization:Bearer $TOKEN"
```

---

## 4️⃣ Seguridad y autorización (OBLIGATORIO)

**Qué se evalúa**

* Uso correcto de Sanctum
* Policies aplicadas
* Códigos 401 / 403 / 409

**Verificación con HTTPie**

Acceso sin token:

```bash
http GET :8000/api/recetas
```

Resultado esperado:

* `401 Unauthorized`

---

## 5️⃣ Reglas de negocio (OBLIGATORIO)

**Ejemplo: receta publicada no modificable**

```bash
http PUT :8000/api/recetas/1 \
  "Authorization:Bearer $TOKEN" \
  titulo="Cambio ilegal"
```

Resultado esperado:

```json
{
  "error": {
    "code": "RECETA_PUBLICADA"
  }
}
```

Código HTTP: `409`

---

## 6️⃣ Roles — Admin (OBLIGATORIO)

**Qué se evalúa**

* El admin puede realizar acciones globales

**Verificación con HTTPie**

```bash
http DELETE :8000/api/recetas/1 \
  "Authorization:Bearer $TOKEN_ADMIN"
```

Resultado esperado:

* `200 OK` aunque no sea el propietario

---

## 7️⃣ Swagger (SEMI-OPCIONAL)

**Verificación**

* Acceder a `http://localhost/api/documentation`
* Autorizar con Bearer Token
* Al menos un endpoint propio documentado

---
