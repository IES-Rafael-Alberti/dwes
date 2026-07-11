# 🧪 EXAMEN DWES

**Desarrollo Web en Entorno Servidor – Spring Boot**

---

## 1. Contexto del examen

Durante el curso has desarrollado un proyecto backend con **Spring Boot**, partiendo de un diseño de base de datos y evolucionando hacia una arquitectura con controladores, servicios y repositorios, incorporando **seguridad con JWT**, **autorización por roles**, documentación y pruebas de endpoints.

En este examen se evaluará tu **comprensión real de tu propio proyecto**, no la complejidad del mismo ni que todos los proyectos hagan lo mismo.

Cada alumno trabaja sobre **su propio proyecto**, que es de **tema libre**.

---

## 2. Estructura del examen

El examen consta de **dos partes**:

### 🧩 Parte A – Modificación práctica del proyecto (día 1)

Añadir un **endpoint nuevo** al proyecto, cumpliendo una serie de requisitos comunes.

### 📝 Parte B – Documento de explicación técnica

Completar el documento `EXAMEN.md`, explicando **qué se ha hecho, por qué y dónde**.

📌 En función de la claridad del documento entregado, **algunos alumnos realizarán una defensa oral corta** en la siguiente sesión para aclarar dudas.

⏱️ **Duración orientativa del trabajo práctico:** 1 hora y 30 minutos.

---

## 3. PARTE A – Modificación práctica (opción única)

Debes **añadir un endpoint nuevo** a tu proyecto Spring Boot que **no existiera previamente**.

### 3.1. Funcionalidad del endpoint

El endpoint debe:

* Tener un propósito claro dentro de tu dominio.
* Ser coherente con el proyecto.

Ejemplos (orientativos):

* Listado filtrado.
* Recurso asociado al usuario autenticado.
* Resumen o estado de datos.
* Cualquier funcionalidad razonable dentro de tu aplicación.

📌 **No se evalúa la complejidad**, sino la coherencia y la correcta integración.

---

### 3.2. Arquitectura (obligatorio)

El endpoint debe respetar la arquitectura del proyecto:

* **Controlador**

  * Recibe la petición HTTP.
  * No contiene lógica de negocio compleja.
* **Servicio**

  * Contiene la lógica de negocio.
* **Repositorio**

  * Accede a los datos si es necesario.

❌ No se aceptan endpoints con toda la lógica en el controlador.

---

### 3.3. Seguridad (obligatorio)

El endpoint debe estar **protegido**, al menos, de una de las siguientes formas:

* Requiere autenticación mediante JWT.
* Requiere un rol concreto.
* Limita el acceso a recursos propios del usuario autenticado.

No se exige una lógica compleja, pero sí **correcta y justificada**.

---

### 3.4. Prueba del endpoint (obligatorio)

El endpoint debe ser **probado manualmente**, utilizando al menos una de estas herramientas:

* Postman
* Insomnia
* httpie
* curl

Debes ser capaz de explicar:

* Qué petición se envía.
* Qué headers son necesarios (por ejemplo, el token).
* Qué respuesta se espera.

📌 El uso de Swagger es complementario, pero **no es el medio principal de evaluación en este apartado**.

---

### 3.5. Tests automatizados (opcional – nota alta)

De forma opcional, puedes añadir:

* Un test de controlador **o**
* Un test de servicio.

No es obligatorio.
Se valorará como **mejora de nota**, no como requisito imprescindible.

---

## 4. Trabajo en rama (obligatorio)

Para realizar el examen debes trabajar **en una rama nueva de tu repositorio**, distinta de la rama principal (`main` o `master`).

**Normas:**

* Crea una rama específica para el examen, por ejemplo:

  * `examen-endpoint`
  * `examen-servidor`
* Todos los cambios del examen deben hacerse **en esa rama**.
* **No trabajes directamente sobre la rama principal**.
* No es necesario hacer *merge* de la rama del examen a la rama principal.

📌 **Motivo**:
Trabajar en una rama evita romper el proyecto principal y es una práctica habitual en entornos profesionales.

---

### Comandos Git mínimos (orientativos)

```bash
git checkout -b examen-endpoint
# realizar aquí los cambios del examen
git add .
git commit -m "Examen: nuevo endpoint"
```

---

## 5. PARTE B – Documento `EXAMEN.md`

Debes completar el documento `EXAMEN.md`, donde explicarás:

* Qué endpoint has añadido.
* Por qué has elegido ese endpoint.
* Qué partes del proyecto has tocado y por qué.
* Cómo has aplicado la seguridad.
* Cómo has probado el endpoint.
* Qué dificultades has encontrado.

El objetivo es que el profesor pueda **entender tu trabajo sin necesidad de revisar todo el código**.

---

## 6. Entrega

1. Crea una rama nueva en tu repositorio.
2. Realiza en esa rama la modificación del examen.
3. Completa el documento `EXAMEN.md`.
4. Indica en el documento:

   * Enlace al repositorio del proyecto.
   * Nombre de la rama del examen.
   * Commit final del examen.
5. Asegúrate de que el proyecto **compila y arranca correctamente**.

No se aceptarán cambios fuera del tiempo establecido.

---

## 7. Defensa oral (día 2)

En función de la claridad del documento entregado, algunos alumnos realizarán una **defensa oral corta** (5–7 minutos), centrada en:

* Explicar el endpoint añadido.
* Justificar decisiones de diseño.
* Aclarar dudas sobre seguridad o arquitectura.

La defensa oral **no es un segundo examen**, sino un mecanismo para verificar comprensión.

---

# 📊 RÚBRICA DE EVALUACIÓN

### 🧪 Examen Proyecto Spring Boot – Servidor

### 1. Endpoint añadido y arquitectura (40 %)

* Endpoint nuevo funcional y coherente: **15 %**
* Uso correcto de controlador / servicio / repositorio: **15 %**
* Integración limpia en el proyecto: **10 %**

---

### 2. Seguridad y control de acceso (20 %)

* Autenticación JWT correctamente aplicada: **10 %**
* Autorización coherente (roles y/o usuario propietario): **10 %**

---

### 3. Pruebas del endpoint (20 %)

**Pruebas manuales del endpoint (15 %)**
Se valora especialmente:

* Uso de Postman / Insomnia / httpie / curl.
* Comprensión de la petición (método, headers, token).
* Comprensión de la respuesta (códigos HTTP y datos).

**Documentación en Swagger (5 %)**

* El endpoint aparece documentado.
* No se penaliza que la documentación sea básica.

---

### 4. Documento explicativo `EXAMEN.md` (15 %)

* Explica claramente qué ha hecho y por qué.
* Identifica correctamente dónde ha tocado el proyecto.
* Lenguaje técnico correcto y estructura clara.

---

### 5. Tests automatizados (hasta +5 % – nota alta)

* Test funcional de controlador o servicio.
* O explicación razonada de por qué no hay tests y qué cambiaría del diseño.

---

## 📌 Observaciones finales

* Se valora más **la comprensión y el razonamiento** que la complejidad.
* La simplicidad no penaliza si el diseño es correcto.
* Copiar sin entender impactará negativamente en la calificación.

---

## ⚠️ Avisos importantes

### 🚫 Uso de Inteligencia Artificial

Durante la realización de este examen **no está permitido el uso de herramientas de Inteligencia Artificial**, incluyendo (entre otras):

* ChatGPT u otros asistentes conversacionales
* Copilot u otras ayudas automáticas de generación de código
* Herramientas de autocompletado inteligente más allá del IDE

📌 El objetivo del examen es evaluar **tu comprensión real del proyecto y del código**, no la capacidad de delegar el razonamiento en herramientas externas.

El uso de IA supondrá la **invalidación del examen**.

---

### 📚 Documentación permitida

Puedes consultar **documentación oficial**, especialmente:

* Documentación oficial de Spring Boot
  [https://docs.spring.io/spring-boot/docs/current/reference/html/](https://docs.spring.io/spring-boot/docs/current/reference/html/)

* Documentación de Spring Security
  [https://docs.spring.io/spring-security/reference/](https://docs.spring.io/spring-security/reference/)

* Documentación de Spring Data JPA
  [https://docs.spring.io/spring-data/jpa/docs/current/reference/html/](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/)

* Documentación de HTTP y códigos de estado (MDN)
  [https://developer.mozilla.org/es/docs/Web/HTTP/Status](https://developer.mozilla.org/es/docs/Web/HTTP/Status)

📌 Se permite consultar documentación, **no soluciones cerradas ni ejemplos completos copiados**.

---

### 🔄 Commits frecuentes (muy recomendable)

Se recomienda **hacer commits frecuentes durante el examen**, y no dejar todo para el final.

Buenas prácticas:

* Un commit tras crear el endpoint.
* Un commit tras integrar seguridad.
* Un commit tras probar el endpoint.
* Un commit final con el documento `EXAMEN.md`.

📌 Esto:

* reduce errores,
* evita pérdidas de trabajo,
* y refleja una forma de trabajar profesional.

No es necesario hacer *merge* con la rama principal.

---

## 📌 Nota final

Este examen no busca rapidez extrema ni soluciones complejas, sino:

* **comprensión**
* **orden**
* **capacidad de explicación**
* **buenas prácticas básicas**

Trabaja con calma, explica lo que haces y por qué, y céntrate en que tu proyecto siga siendo **correcto y mantenible**.

Respuesta corta y clara: **sí, pero no esta tal cual**.
Respuesta profesional: **una versión reducida y “limpia”, orientada a expectativas, no a corrección**.

Te explico el porqué y luego te dejo **la versión exacta que sí puedes pasarles**.

---

## 1️⃣ Por qué NO pasarles la checklist del profesor

Esta checklist:

* contiene **red flags** explícitos
* muestra **criterios de sospecha**
* revela cómo decides defensa oral o bajada de nota
* está pensada para **detectar carencias**, no para guiar

Si se la das tal cual:

* algunos “jugarán” a cubrir casillas sin entender
* otros se bloquearán
* pierdes margen pedagógico en la defensa oral

---

## 2️⃣ Qué SÍ es buena idea pasarles

👉 Una **lista de expectativas**, no de corrección.
👉 Redactada en positivo: *“se espera que…”*
👉 Sin pesos ni penalizaciones ocultas.

Eso:

* reduce ansiedad
* mejora calidad de entregas
* evita reclamaciones tipo *“no sabía que…”*

---

## 3️⃣ VERSIÓN PARA EL ALUMNADO (recomendada)

Puedes entregarla:

* en Moodle
* como último apartado del examen
* o como aviso separado

---

### ✅ Qué se espera en este examen

#### Endpoint

* Se espera que el endpoint añadido:

  * tenga un propósito claro,
  * no existiera previamente,
  * esté bien integrado en el proyecto.

---

#### Arquitectura

* Se espera que:

  * el controlador gestione la petición HTTP,
  * la lógica esté en el servicio,
  * el acceso a datos esté en el repositorio.
* No se espera lógica de negocio compleja en el controlador.

---

#### Seguridad

* Se espera que el endpoint:

  * no sea público,
  * requiera autenticación,
  * y/o tenga control de acceso coherente.
* Se espera que el alumno sepa explicar cómo funciona esa seguridad.

---

#### Pruebas del endpoint

* Se espera que el endpoint haya sido probado manualmente.
* Se valora especialmente:

  * saber lanzar la petición,
  * enviar correctamente el token,
  * interpretar la respuesta.
* Swagger es una ayuda, no el medio principal de evaluación.

---

#### Documento `EXAMEN.md`

* Se espera un documento claro que explique:

  * qué se ha hecho,
  * por qué se ha hecho,
  * qué partes del proyecto se han tocado.
* El documento debe poder entenderse sin revisar todo el código.

---

#### Tests automatizados

* No son obligatorios.
* Se valora:

  * añadir alguno si se sabe hacer,
  * o explicar honestamente por qué no se han añadido y qué cambiarías del diseño.

---

#### Trabajo con Git

* Se espera que el trabajo se realice en una rama nueva.
* Se recomienda hacer commits frecuentes.
* No es necesario hacer merge con la rama principal.

---

#### Defensa oral

* Algunos alumnos realizarán una defensa oral corta.
* Servirá para aclarar dudas, no para repetir el examen.




