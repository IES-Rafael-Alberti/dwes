# 📊 Rúbrica de evaluación — Extensión de la API de Recetas

> **Nota importante**
> La evaluación no es binaria. Cada apartado tiene **niveles intermedios**.
> El objetivo es reflejar **grado de consecución**, no solo presencia o ausencia.

---

## 1️⃣ Ingredientes en las recetas (OBLIGATORIO)

| Nivel           | Descripción                                                                              |
| --------------- | ---------------------------------------------------------------------------------------- |
| 🔴 Insuficiente | No existe modelo de ingredientes o no está relacionado correctamente con recetas         |
| 🟠 Básico       | Modelo y relación creados, pero con operaciones incompletas o sin validación             |
| 🟡 Adecuado     | CRUD funcional de ingredientes, relaciones correctas y validaciones básicas              |
| 🟢 Avanzado     | Diseño claro, relaciones bien justificadas, endpoints coherentes y protegidos por policy |

**Checklist orientativo**

* Modelo y migración de ingredientes
* Relación correctamente definida
* Endpoints claros
* Restricción de modificación por propietario/admin

---

## 2️⃣ Likes de recetas (OBLIGATORIO)

| Nivel           | Descripción                                                     |
| --------------- | --------------------------------------------------------------- |
| 🔴 Insuficiente | Likes no implementados o con duplicados                         |
| 🟠 Básico       | Likes funcionales pero sin control de duplicados o sin eliminar |
| 🟡 Adecuado     | Control correcto de duplicados, endpoints claros                |
| 🟢 Avanzado     | Diseño limpio, control de errores y conteo eficiente            |

**Checklist**

* Tabla intermedia correcta
* Un usuario no puede dar dos likes
* Posibilidad de quitar like
* Conteo accesible desde la API

---

## 3️⃣ Comentarios en recetas (OBLIGATORIO)

| Nivel           | Descripción                                                  |
| --------------- | ------------------------------------------------------------ |
| 🔴 Insuficiente | Comentarios inexistentes o sin relación correcta             |
| 🟠 Básico       | Comentarios creados pero sin control de autor                |
| 🟡 Adecuado     | CRUD funcional, políticas de autorización aplicadas          |
| 🟢 Avanzado     | Buen diseño de endpoints, uso de resources y policies claras |

**Checklist**

* Comentarios asociados a receta y usuario
* Solo el autor/admin puede borrar
* Uso de policies
* Respuestas coherentes

---

## 4️⃣ Seguridad y autorización (OBLIGATORIO)

| Nivel           | Descripción                                                  |
| --------------- | ------------------------------------------------------------ |
| 🔴 Insuficiente | Endpoints sin protección o reglas inconsistentes             |
| 🟠 Básico       | Autenticación aplicada pero con lagunas                      |
| 🟡 Adecuado     | Policies bien usadas, control de propietario                 |
| 🟢 Avanzado     | Uso claro de roles (admin / user) y autorización consistente |

**Checklist**

* Sanctum correctamente usado
* Policies aplicadas
* Admin con privilegios ampliados
* Respuestas 401 / 403 correctas

---

## 5️⃣ Tests automáticos (OBLIGATORIO)

| Nivel           | Descripción                                  |
| --------------- | -------------------------------------------- |
| 🔴 Insuficiente | Tests inexistentes o rotos                   |
| 🟠 Básico       | Algunos tests, cobertura muy parcial         |
| 🟡 Adecuado     | Tests de las funcionalidades principales     |
| 🟢 Avanzado     | Tests claros, autónomos y bien estructurados |

**Checklist**

* Tests feature funcionales
* Tests pasan en entorno limpio
* Tests nuevos para funcionalidades añadidas

---

## 6️⃣ Imagen del plato final (SEMI-OBLIGATORIO)

| Nivel             | Descripción                                          |
| ----------------- | ---------------------------------------------------- |
| ⚪ No implementado | No afecta negativamente                              |
| 🟠 Básico         | Subida de imagen sin validaciones                    |
| 🟡 Adecuado       | Validación de tipo y almacenamiento correcto         |
| 🟢 Avanzado       | Integración limpia, URL accesible y bien documentada |

---

## 7️⃣ Swagger / Documentación (SEMI-OPCIONAL)

| Nivel             | Descripción                      |
| ----------------- | -------------------------------- |
| ⚪ No implementado | Sin penalización                 |
| 🟠 Básico         | Swagger instalado                |
| 🟡 Adecuado       | Al menos un endpoint documentado |
| 🟢 Avanzado       | Documentación clara y usable     |

---

## 8️⃣ Diseño y calidad del código

| Nivel           | Descripción                            |
| --------------- | -------------------------------------- |
| 🔴 Insuficiente | Código desordenado o difícil de seguir |
| 🟠 Básico       | Funciona, pero con inconsistencias     |
| 🟡 Adecuado     | Código claro y coherente               |
| 🟢 Avanzado     | Buen diseño, fácil de mantener         |

**Checklist**

* Controladores claros
* Uso coherente de Resources
* Código legible

---

## 9️⃣ Documentación y entrega

| Nivel           | Descripción                             |
| --------------- | --------------------------------------- |
| 🔴 Insuficiente | Entrega incompleta o confusa            |
| 🟠 Básico       | Documentación mínima                    |
| 🟡 Adecuado     | Documento claro y completo              |
| 🟢 Avanzado     | Entrega profesional y bien estructurada |

**Checklist**

* Enlace al repositorio
* Qué se ha implementado
* Cómo probar la API (HTTPie)
* Dificultades y mejoras

---

## 🎯 Interpretación global

* **Apto**: todos los obligatorios en nivel 🟡 o superior
* **Notable**: obligatorios sólidos + algún opcional
* **Sobresaliente**: diseño cuidado, tests, opcionales bien hechos y buena documentación
# Anexo C.1 — Verificación de funcionalidades mediante HTTPie

> En todos los ejemplos se asume que el token de autenticación ya está disponible en la variable de entorno `TOKEN`.

```bash
export TOKEN=eyJ0eXAiOiJKV1Qi...
```

---

## 1️⃣ Likes — Un usuario no puede dar dos likes a la misma receta

### 1.1 Dar like por primera vez (debe funcionar)

```bash
http POST :8000/api/recetas/1/like \
  "Authorization:Bearer $TOKEN"
```

**Resultado esperado**

* Código: `201` o `200`
* Mensaje indicando que el like se ha registrado

---

### 1.2 Dar like por segunda vez (debe fallar)

```bash
http POST :8000/api/recetas/1/like \
  "Authorization:Bearer $TOKEN"
```

**Resultado esperado**

* Código: `409`
* Respuesta JSON similar a:

```json
{
  "error": {
    "code": "LIKE_DUPLICADO",
    "message": "El usuario ya ha dado like a esta receta"
  }
}
```

✔ Este comportamiento confirma que **no se permiten likes duplicados**

---

### 1.3 Quitar like

```bash
http DELETE :8000/api/recetas/1/like \
  "Authorization:Bearer $TOKEN"
```

**Resultado esperado**

* Código: `200`
* Like eliminado correctamente

---

## 2️⃣ Comentarios — Solo el autor puede borrar su comentario

### 2.1 Crear comentario

```bash
http POST :8000/api/recetas/1/comentarios \
  "Authorization:Bearer $TOKEN" \
  texto="Muy buena receta"
```

**Resultado esperado**

* Código: `201`
* Devuelve el comentario creado con su `id`

---

### 2.2 Intentar borrar comentario con otro usuario (debe fallar)

Cambiar el token por el de otro usuario:

```bash
export TOKEN_INTRUSO=eyJ0eXAiOiJKV1Qi...
```

```bash
http DELETE :8000/api/comentarios/5 \
  "Authorization:Bearer $TOKEN_INTRUSO"
```

**Resultado esperado**

* Código: `403`
* Error de autorización

✔ Confirma uso correcto de **Policy**

---

### 2.3 Borrar comentario como autor

```bash
http DELETE :8000/api/comentarios/5 \
  "Authorization:Bearer $TOKEN"
```

**Resultado esperado**

* Código: `200`
* Comentario eliminado

---

## 3️⃣ Ingredientes — Solo el propietario puede modificar ingredientes

### 3.1 Añadir ingrediente a una receta propia

```bash
http POST :8000/api/recetas/1/ingredientes \
  "Authorization:Bearer $TOKEN" \
  nombre="Huevo" cantidad=3 unidad="ud"
```

**Resultado esperado**

* Código: `201`
* Ingrediente creado

---

### 3.2 Modificar ingrediente como otro usuario (debe fallar)

```bash
http PUT :8000/api/ingredientes/10 \
  "Authorization:Bearer $TOKEN_INTRUSO" \
  cantidad=5
```

**Resultado esperado**

* Código: `403`

---

## 4️⃣ Recetas — Un usuario no puede modificar una receta publicada

### 4.1 Intentar modificar receta publicada

```bash
http PUT :8000/api/recetas/1 \
  "Authorization:Bearer $TOKEN" \
  titulo="Cambio ilegal"
```

**Resultado esperado**

* Código: `409`
* Error:

```json
{
  "error": {
    "code": "RECETA_PUBLICADA",
    "message": "No se puede modificar una receta ya publicada"
  }
}
```

---

## 5️⃣ Roles — Admin puede borrar cualquier receta

### 5.1 Borrar receta como admin

```bash
http DELETE :8000/api/recetas/1 \
  "Authorization:Bearer $TOKEN_ADMIN"
```

**Resultado esperado**

* Código: `200`
* Receta eliminada aunque no sea suya

---

## 6️⃣ Autenticación — Endpoint protegido sin token

```bash
http GET :8000/api/recetas
```

**Resultado esperado**

* Código: `401`
* Error de autenticación

---

## 7️⃣ Swagger (si se implementa)

Acceder a:

```
http://localhost/api/documentation
```

Comprobar:

* La API carga
* Se puede autorizar con Bearer Token
* Al menos un endpoint propio está documentado

---

# Nota para la evaluación

Durante la corrección, el profesor podrá:

* Ejecutar directamente estos comandos
* Verificar el comportamiento esperado
* Asignar el nivel correspondiente en la rúbrica

Esto **reduce ambigüedad**, **agiliza la corrección** y **acerca la tarea a un entorno profesional real**.
