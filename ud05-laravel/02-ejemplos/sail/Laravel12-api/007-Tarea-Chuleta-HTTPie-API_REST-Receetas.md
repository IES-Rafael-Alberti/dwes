# 🧾 CHULETA HTTPie — API Recetas (A4)

> Pensada para imprimir o tener abierta durante el desarrollo.

---

## 0️⃣ Variables de entorno

```bash
export TOKEN=token_usuario
export TOKEN_ADMIN=token_admin
export TOKEN_INTRUSO=token_otro_usuario
```

---

## 🔐 Autenticación

Login:

```bash
http POST :8000/api/auth/login \
  email=usuario@demo.local password=password
```

---

## 🍽️ Recetas

Crear receta:

```bash
http POST :8000/api/recetas \
  "Authorization:Bearer $TOKEN" \
  titulo="Tortilla" \
  descripcion="Clásica" \
  instrucciones="..."
```

Listar recetas:

```bash
http GET :8000/api/recetas \
  "Authorization:Bearer $TOKEN"
```

---

## 🧂 Ingredientes

Añadir ingrediente:

```bash
http POST :8000/api/recetas/1/ingredientes \
  "Authorization:Bearer $TOKEN" \
  nombre="Huevo" cantidad=3 unidad="ud"
```

---

## ❤️ Likes

Dar like:

```bash
http POST :8000/api/recetas/1/like \
  "Authorization:Bearer $TOKEN"
```

Quitar like:

```bash
http DELETE :8000/api/recetas/1/like \
  "Authorization:Bearer $TOKEN"
```

---

## 💬 Comentarios

Crear comentario:

```bash
http POST :8000/api/recetas/1/comentarios \
  "Authorization:Bearer $TOKEN" \
  texto="Muy buena receta"
```

Borrar comentario:

```bash
http DELETE :8000/api/comentarios/5 \
  "Authorization:Bearer $TOKEN"
```

---

## 🛡️ Admin

Borrar cualquier receta:

```bash
http DELETE :8000/api/recetas/1 \
  "Authorization:Bearer $TOKEN_ADMIN"
```

---

## 📘 Swagger (si se implementa)

```text
http://localhost/api/documentation
```

---

### Nota final para el alumnado

> Si un endpoint **no se puede comprobar con HTTPie**, probablemente **no está bien definido como API REST**.

---

Con esto tienes:

* rúbrica **objetiva y comprobable**
* comandos reutilizables para corrección
* una chuleta clara y realista

Cuando quieras, puedo ayudarte a **compactar esto en PDF A4**, o adaptarlo exactamente al **formato de rúbrica de Séneca / Moodle**.
