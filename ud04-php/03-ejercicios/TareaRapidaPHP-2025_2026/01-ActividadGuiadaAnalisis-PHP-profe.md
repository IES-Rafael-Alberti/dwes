Perfecto. A continuación tienes la **actividad guiada de análisis**, pensada para trabajar **sobre el CRUD PHP clásico y el antes/después con Laravel 12**, **sin programar nada nuevo**, centrada en **comprensión, arquitectura y pensamiento backend**.

Está diseñada para:

* hacerles **leer código**
* obligarles a **razonar**
* conectar **PHP clásico ↔ framework**
* preparar la **tarea de ampliación (C)**

Antes de frameworks, **análisis**.

---

# Actividad guiada de análisis

## PHP clásico vs Laravel 12 (qué hace el framework por ti)

### Contexto de la actividad

Se proporciona al alumnado:

* Un CRUD funcional en PHP clásico (concesionario)
* Fragmentos equivalentes implementados en Laravel 12

El objetivo **NO** es mejorar el CRUD clásico ni reescribirlo, sino **entender qué problemas resuelve un framework backend moderno**.

---

## 🧩 Bloque 1 — Comprensión del flujo

### Pregunta 1

En el CRUD PHP clásico, describe el **flujo completo** que se produce cuando un usuario envía un formulario para crear un nuevo registro.

👉 Indica, al menos:

* qué envía el navegador
* cómo lo recibe el servidor
* qué validaciones se realizan
* cuándo se ejecuta el SQL
* qué respuesta vuelve al usuario

---

### Pregunta 2

En Laravel 12, ese mismo flujo se reparte en varias capas (Request, Controller, Model, etc.).

* Enumera esas capas.
* Explica **qué responsabilidad tiene cada una**.
* Indica qué parte del flujo **no aparece explícitamente** en el código Laravel, pero sigue existiendo.

---

## 🧩 Bloque 2 — Acceso a datos del cliente

### Pregunta 3

En PHP clásico se accede a los datos del formulario mediante `$_POST` y `$_GET`.

* ¿Qué problemas puede tener este enfoque?
* ¿Qué ocurre si un campo no existe?
* ¿Por qué este acceso dificulta el mantenimiento?

---

### Pregunta 4

En Laravel se utiliza el objeto `Request`.

* ¿Qué ventajas aporta frente a `$_POST`?
* ¿Qué problema de diseño está resolviendo?
* ¿Por qué es importante que el controlador **no dependa del HTML del formulario**?

---

## 🧩 Bloque 3 — Validación

### Pregunta 5

Observa el código de validación manual del CRUD PHP clásico.

* ¿Qué ocurre cuando aumentan los campos del formulario?
* ¿Qué pasa si hay varios formularios similares?
* ¿Qué problemas aparecen si se reutiliza esa validación?

---

### Pregunta 6

En Laravel, la validación se mueve a un `FormRequest`.

* ¿Qué ventajas tiene separar la validación del controlador?
* ¿Qué ocurre si la validación falla?
* Relaciona este mecanismo con `@Valid` en Spring Boot.

---

## 🧩 Bloque 4 — Persistencia y SQL

### Pregunta 7

En el CRUD clásico, el SQL está escrito a mano.

* Enumera **dos riesgos** de este enfoque.
* ¿Qué parte del sistema queda más expuesta a errores?
* ¿Por qué este código es difícil de probar automáticamente?

---

### Pregunta 8

Laravel utiliza Eloquent en lugar de SQL directo.

* ¿Elimina Laravel el uso de SQL?
* ¿Dónde está el SQL ahora?
* ¿Qué gana el desarrollador con este cambio?

---

## 🧩 Bloque 5 — Autorización y reglas de negocio

### Pregunta 9

En PHP clásico, la autorización suele resolverse con condicionales incrustados en el código.

* ¿Por qué este enfoque no escala bien?
* ¿Qué ocurre si la misma regla se necesita en varios sitios?

---

### Pregunta 10

Laravel utiliza Policies para la autorización.

* ¿Qué ventaja aporta centralizar la autorización?
* ¿Por qué las policies facilitan los tests?
* Relaciona este enfoque con anotaciones de seguridad en Spring Boot.

---

## 🧩 Bloque 6 — Arquitectura y mantenimiento

### Pregunta 11

El CRUD PHP clásico mezcla HTML, lógica y acceso a datos.

* ¿Qué problemas aparecen cuando el proyecto crece?
* ¿Qué tipo de cambios son más costosos?
* ¿Por qué este tipo de código es frecuente en proyectos antiguos?

---

### Pregunta 12

Laravel fuerza una separación de capas.

* ¿Qué capas identifica claramente?
* ¿Qué tipo de errores se reducen gracias a esta separación?
* ¿Por qué esto es especialmente importante en proyectos de empresa?

---

## 🧩 Bloque 7 — Reflexión final (clave)

### Pregunta 13

Explica con tus propias palabras la siguiente frase:

> “Laravel no inventa el backend, lo organiza.”

Apóyate en **ejemplos concretos** del antes/después.

---

### Pregunta 14

Después de analizar el CRUD PHP clásico y su equivalente en Laravel:

* ¿Qué parte del trabajo te gustaría **no tener que hacer nunca a mano**?
* ¿Qué parte crees que **sí deberías saber hacer** aunque el framework la oculte?

Justifica tu respuesta.

---

## 🧩 Bloque 8 — Conexión con la tarea de ampliación

### Pregunta 15

Relaciona esta actividad con la tarea de ampliación de la API de recetas.

* ¿Qué errores del PHP clásico debes evitar?
* ¿Qué mecanismos de Laravel debes utilizar correctamente?
* ¿Por qué entender el “antes” te ayuda a hacer mejor el “después”?

---

## 📌 Criterios de corrección (orientativos)

* **Excelente**: respuestas razonadas, con conexiones claras entre PHP clásico y Laravel.
* **Adecuado**: identifica problemas y ventajas, aunque de forma superficial.
* **Insuficiente**: se limita a describir código sin reflexión arquitectónica.

---

## 🎯 Objetivo real de la actividad

> No aprender PHP antiguo.
> Aprender **por qué hoy se hace backend de otra forma**.

Esta actividad **prepara directamente** la tarea C de ampliación.

---
## 📄 Entrega

El alumno entregará un documento escrito (PDF / Markdown / DOCX) que contenga:

Respuestas razonadas a las preguntas planteadas

Uso de vocabulario técnico adecuado

Ejemplos claros cuando sea necesario

❌ No se pide código nuevo
❌ No se evalúa estilo de programación