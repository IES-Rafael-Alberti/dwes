# C — Tarea del alumnado

## Extensión de la API REST de Recetas

### Contexto

Partimos de una **API REST funcional** desarrollada en clase con:

* Laravel 12 + Sail
* PostgreSQL
* Autenticación con Sanctum
* Autorización con Policies y roles (Spatie)
* Manejo de errores con códigos
* Tests funcionales (Feature Tests)

El objetivo de esta tarea es **extender la API existente**, aplicando los mismos patrones de diseño, calidad y pruebas que ya se han trabajado.

---

## 1. Objetivo general de la tarea

Extender la API de recetas incorporando **nuevas funcionalidades habituales en aplicaciones reales**, manteniendo:

* coherencia en el diseño
* seguridad
* pruebas automáticas
* documentación mínima de uso

---

## 2. Extensiones OBLIGATORIAS (mínimo para aprobar)

Estas extensiones **deben implementarse todas**.

---

### 2.1 Ingredientes de una receta (OBLIGATORIO)

Cada receta debe poder tener **ingredientes asociados**.

#### Requisitos mínimos

* Modelo `Ingrediente`
* Relación **Receta → Ingredientes** (1:N o N:M, decisión justificada)
* Cada ingrediente debe tener, al menos:

  * nombre
  * cantidad
  * unidad (g, ml, cucharadas, etc.)

#### API esperada (ejemplo orientativo)

* Añadir ingredientes a una receta
* Listar ingredientes de una receta
* Modificar ingredientes
* Eliminar ingredientes

#### Reglas

* Solo el **propietario** (o admin) puede modificar ingredientes de su receta
* Uso de **Policy**
* Uso de **Resource**

---

### 2.2 Likes de recetas (OBLIGATORIO)

Los usuarios pueden marcar recetas como favoritas / dar “like”.

#### Requisitos mínimos

* Un usuario puede dar **like** a una receta
* Un usuario **no puede dar más de un like** a la misma receta
* Se puede:

  * dar like
  * quitar like
  * consultar número de likes de una receta

#### Decisiones técnicas esperadas

* Tabla intermedia (`likes`)
* Control de duplicados
* Endpoints claros y RESTful

---

### 2.3 Comentarios en recetas (OBLIGATORIO)

Los usuarios pueden comentar recetas.

#### Requisitos mínimos

* Modelo `Comentario`
* Cada comentario pertenece a:

  * una receta
  * un usuario
* Campos mínimos:

  * texto
  * fecha

#### Reglas

* Cualquier usuario autenticado puede comentar
* Solo el autor (o admin) puede borrar su comentario
* Uso de **Policy**
* Uso de **Resource**

---

## 3. Extensiones SEMI-OBLIGATORIAS (para subir nota)

Estas extensiones **no son obligatorias**, pero **suman nota**.

---

### 3.1 Imagen del plato final (SEMI-OBLIGATORIO)

Permitir subir una imagen asociada a la receta.

#### Requisitos mínimos

* Subida de imagen (JPEG/PNG)
* Asociación de la imagen a la receta
* Guardado en `storage`
* URL accesible desde la API

#### Consideraciones

* Se valorará:

  * validación del tipo de archivo
  * tamaño máximo
* No es obligatorio:

  * redimensionado
  * múltiples imágenes

> Si esta parte se complica técnicamente, se puede dejar documentada como **intento**.

---

### 3.2 Búsquedas avanzadas / filtros (OPCIONAL)

Ejemplos:

* filtrar por número de likes
* buscar por ingrediente
* ordenar por popularidad

---

### 3.3 Tests adicionales (OPCIONAL)

* Tests específicos de:

  * likes
  * comentarios
  * ingredientes
* Tests de autorización por rol


### Documentación con Swagger (OPCIONAL)

Podéis:

* Instalar Swagger
* Documentar **al menos un endpoint propio**
  (por ejemplo: ingredientes, likes o comentarios)
* Mostrar la UI funcionando

Se valorará:

* claridad
* coherencia con la API
* que la documentación sea usable

No se penaliza **no hacerlo**.

---

## Nota

> En proyectos reales, Swagger suele introducirse cuando la API ya está estable.
> No es prioritario al inicio, pero sí muy útil cuando el proyecto crece.

---

## 4. Requisitos técnicos OBLIGATORIOS

Independientemente de las extensiones:

* Usar **API Resources**
* Usar **Policies**
* Mantener el uso de **códigos de error**
* No romper endpoints existentes
* Código legible y organizado

---

## 5. Pruebas de la API con HTTPie (OBLIGATORIO)

En lugar de Postman o Insomnia, se utilizará **HTTPie** desde terminal.

### 5.1 Autenticación

```bash
http POST :8000/api/auth/login \
  email=admin@demo.local password=password
```

Guardar el token:

```bash
export TOKEN=eyJ0eXAiOiJKV1Qi...
```

---

### 5.2 Crear receta

```bash
http POST :8000/api/recetas \
  "Authorization:Bearer $TOKEN" \
  titulo="Tortilla" \
  descripcion="Clásica" \
  instrucciones="..."
```

---

### 5.3 Añadir ingrediente (ejemplo)

```bash
http POST :8000/api/recetas/1/ingredientes \
  "Authorization:Bearer $TOKEN" \
  nombre="Huevo" cantidad=3 unidad="ud"
```

---

### 5.4 Dar like

```bash
http POST :8000/api/recetas/1/like \
  "Authorization:Bearer $TOKEN"
```

---

### 5.5 Comentar receta

```bash
http POST :8000/api/recetas/1/comentarios \
  "Authorization:Bearer $TOKEN" \
  texto="Muy buena receta"
```

> Cada grupo debe incluir en la entrega **una pequeña colección de comandos HTTPie** para probar su API.

---

## 6. (Opcional) Prueba desde UI

Si algún alumno lo desea, puede:

* crear una UI mínima (HTML + fetch)
* o usar una herramienta tipo Swagger / Redoc

No es obligatorio.

---

## 7. Entregables (MUY IMPORTANTE)

Cada alumno/grupo debe entregar:

---

### 7.1 Repositorio Git

* URL del repositorio (GitHub / GitLab)
* Código fuente completo
* Docker/Sail funcional

---

### 7.2 Documento de entrega (OBLIGATORIO)

Un **documento breve** (Markdown o PDF) que incluya:

1. **Qué se ha implementado**

   * partes obligatorias
   * partes opcionales
2. **Cómo probar la API**

   * comandos HTTPie
3. **Decisiones técnicas**

   * relaciones
   * diseño de endpoints
4. **Dificultades encontradas**
5. **Mejoras pendientes** (si hubiera más tiempo)

---

### 7.3 Tests

* Deben existir tests funcionales
* No se exige cobertura completa
* Sí se exige:

  * que los tests existentes sigan pasando
  * al menos algunos tests nuevos de lo añadido

---

## 8. Criterios de evaluación (resumen)

* Extensiones obligatorias completas → **Apto**
* Uso correcto de policies y roles → **Muy importante**
* Código claro y coherente → **Muy importante**
* Opcionales bien implementados → **Sube nota**
* Documentación clara → **Sube nota**

---

## 9. Importante

> No se evalúa solo “que funcione”,
> se evalúa **cómo está hecho** y **si es mantenible**.

Esta tarea simula **una ampliación real de un proyecto existente**, no un ejercicio aislado.

---
