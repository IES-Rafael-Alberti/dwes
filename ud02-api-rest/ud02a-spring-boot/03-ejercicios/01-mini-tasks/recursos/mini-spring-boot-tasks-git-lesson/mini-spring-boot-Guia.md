# Guion para ti (docente)

> Material docente. No forma parte del enunciado ni de la evaluación publicada
> para el alumnado.

## Objetivo

Guiar una sesión práctica donde el alumnado construye paso a paso una API REST en Spring Boot: **controlador simple → ResponseEntity → repositorio JPA → servicio → extras (ETag, paginación, búsqueda, PUT, HEAD)**, todo apoyado en **tests** y commits etiquetados.

## Preparación (10–15’)

1. Descarga y descomprime:

```bash
unzip mini-spring-boot-tasks-completo.zip
cd mini-spring-boot-tasks
```

2. Verifica Java/Maven:

```bash
java -version
mvn -v
```

3. (Opcional) Prepara el kit de historia Git:

```bash
unzip ../mini-spring-boot-tasks-git-lesson.zip -d ..
cd ../mini-spring-boot-tasks-git-lesson
bash setup-history.sh

Funciona
cd /ruta/mini-spring-boot-tasks-git-lesson
mkdir -p project
git -C project init
git -C project config user.name  "MiniTasks Teacher"
git -C project config user.email "teacher@example.com"

# v1
( cd snapshots/v1 && tar -cf - . ) | ( cd project && tar -xpf - )
git -C project add -A && git -C project commit -m "V1: Controlador sin ResponseEntity (memoria)" && git -C project tag v1

# v2
( cd snapshots/v2 && tar -cf - . ) | ( cd project && tar -xpf - )
git -C project add -A && git -C project commit -m "V2: Refactor a ResponseEntity (memoria)" && git -C project tag v2

# v3
( cd snapshots/v3 && tar -cf - . ) | ( cd project && tar -xpf - )
git -C project add -A && git -C project commit -m "V3: JPA + Repo + Controller V3 + ApiExceptionHandler" && git -C project tag v3

# v4
( cd snapshots/v4-basic && tar -cf - . ) | ( cd project && tar -xpf - )
git -C project add -A && git -C project commit -m "V4: Servicio y controlador V4 (CRUD)" && git -C project tag v4

# v4-extras
( cd snapshots/v5-final && tar -cf - . ) | ( cd project && tar -xpf - )
git -C project add -A && git -C project commit -m "V4 extras: ETag/Cache-Control, paginación, búsqueda, PUT, HEAD" && git -C project tag v4-extras
```

4. Planifica el recorrido: trabajarás **primero con los tags** (demo de evolución) y luego con el **proyecto completo** para ejecutar los tests por perfiles.

## Estructura de la sesión (90–120’)

**Bloque 1 (15’):** Contexto y objetivos

* Enseña `v1 → v2 → v3 → v4 → v4-extras`.
* Explica “por qué” de cada capa (códigos/headers, separación de responsabilidades, transacciones, testabilidad).

**Bloque 2 (20’):** Demo con el repo por etapas

```bash
cd mini-spring-boot-tasks-git-lesson/project
git checkout v1 && mvn spring-boot:run
# prueba /v1 endpoints
git checkout v2
git checkout v3
git checkout v4
git checkout v4-extras
```

Muestra cómo cambian endpoints, headers y responsabilidades.

**Bloque 3 (35–50’):** Trabajo guiado por tests (en el proyecto completo)

* Ejecuta agrupaciones:

```bash
cd ../../mini-spring-boot-tasks
mvn -q -Ptests-basic test
mvn -q -Ptests-addons test
mvn -q -Ptests-extras test
mvn -q -Ptests-paging-cache test
mvn -q -Ptests-final test
```

* Cuando un test falle, motiva a que lean el fallo y localicen la clase a cambiar.

**Bloque 4 (15’):** Cierre y extensiones

* Repaso de patrones, preguntas, próximos pasos (DTOs de lectura, paginación real, ETag por hash, especificaciones JPA).

## Qué remarcar (mensajes clave)

* **Sin ResponseEntity** también es válido (didáctico para empezar).
* **ResponseEntity** da control fino sobre **códigos y cabeceras**.
* **Repositorio** introduce persistencia real; **Servicio** concentra reglas y transacciones.
* **Tests** guían el diseño (TDD suave): primero expectativas, luego implementación.
* **Headers HTTP** son parte del contrato (ETag, Cache-Control, Link, X-Total-Count).

---

# Guion didáctico con checkpoints y ejercicios

## Objetivos de aprendizaje

* Modelar un CRUD REST en Spring Boot en pasos incrementales.
* Comprender cuándo y por qué usar `ResponseEntity`.
* Integrar JPA/H2 y separar capas (Controller/Service/Repository).
* Exponer paginación, búsqueda y cabeceras estándar (ETag, Cache-Control, Link).
* Escribir/leer tests (MockMvc, Mockito, @DataJpaTest) y depurar fallos.

## Requisitos

* Java 17+, Maven 3.9+.
* Editor/IDE (IntelliJ recomendado).
* Conocimientos básicos de REST, Spring MVC y JPA.

## Itinerario y checkpoints

### Checkpoint 1 — V1 (controlador sin ResponseEntity)

**Qué hacer:** Explorar `/v1` (memoria) y crear tareas.
**Comandos:**

```bash
mvn spring-boot:run
# en IntelliJ: abrir requests.http y ejecutar bloques V1
```

**Criterios de aceptación:**

* `POST /v1/tasks` devuelve **201** (via `@ResponseStatus`), y la tarea con `id`, `title`, `done=false`.

**Ejercicio 1 (Fácil):** Añade `GET /v1/ping` (si no existe) y comprueba que devuelva `"pong"`.

---

### Checkpoint 2 — V2 (ResponseEntity)

**Qué hacer:** Evaluar ventajas de `ResponseEntity`.
**Prueba rápida:** `POST /v2/tasks` debe devolver `201 + Location`.
**Criterios de aceptación:**

* Cabecera `Location` presente y con `/v2/tasks/{id}`.

**Ejercicio 2 (Fácil):** Añade `GET /v2/tasks` con `ResponseEntity<List<...>>` y verifica `200 OK`.

---

### Checkpoint 3 — V3 (Repositorio JPA + Validación + Handler)

**Qué hacer:** Probar `/v3/tasks` (carga `data.sql`), POST con DTO validado y 400 controlado.
**Tests a pasar (perfiles):**

```bash
mvn -q -Ptests-addons test        # DataJpa + 400
mvn -q -Ptests-extras test        # data.sql + 404
```

**Criterios de aceptación:**

* `@Valid` en `create` y `ApiExceptionHandler` devuelve JSON de error 400/404.
* `data.sql` cargó dos filas.

**Ejercicio 3 (Media):** Añade `GET /v3/tasks?done=true|false` y comprueba filtro.

---

### Checkpoint 4 — V4 (Servicio + CRUD)

**Qué hacer:** Introducir servicio y delegación desde el controlador.
**Tests a pasar:**

```bash
mvn -q -Ptests-basic test
```

**Criterios de aceptación:**

* Controlador delgado, lógica en `TaskService`, tests con `@MockBean`.

**Ejercicio 4 (Media):** Crea `PUT /v4/tasks/{id}` con `UpdateTaskDTO` para actualizar `title`/`done`.

---

### Checkpoint 5 — Extras (ETag, Cache-Control, paginación, Link, búsqueda, HEAD)

**Qué hacer:**

* `GET /v4/tasks` (sin paginación): **ETag**, **Cache-Control**, soporte `If-None-Match → 304`.
* `GET /v4/tasks?page=..&size=..&sort=..&q=..`: **X-Total-Count** + **Link**.
* `HEAD /v4/tasks` devuelve solo cabeceras.
  **Tests a pasar:**

```bash
mvn -q -Ptests-paging-cache test
mvn -q -Ptests-final test
```

**Criterios de aceptación:**

* ETag presente y coherente.
* Cabeceras `X-Total-Count` y `Link` correctas.
* Búsqueda `q` funcional.

**Ejercicio 5 (Avanzado):** Cambia el ETag por un **hash** de `(ids + done)` o `updatedAt` máximo.

---

## Ejercicios adicionales (para nota/extra)

1. **DTO de lectura (ViewModel)**

   * Devuelve `TaskView` con `id`, `title`, `done`, y un `links.self`.
   * **Objetivo:** separar entidad de contrato de salida.

2. **Validaciones enriquecidas**

   * Reglas: `title` longitud [3..100], sin solo espacios.
   * Testea 400 con mensajes claros.

3. **Paginación real con filtro `done` + `q`**

   * Añade métodos en repositorio: `findByDoneAndTitleContainingIgnoreCase(...)`.
   * Asegura que Link preserve `done` y `q`.

4. **Ordenación por múltiples campos**

   * Permite `sort=done,asc&sort=title,desc`.
   * Verifica que `Sort` múltiple se respete.

5. **Idempotencia de PUT y semántica de PATCH**

   * PUT debe soportar enviar el mismo contenido sin cambios → `200 OK` y mismo ETag.
   * PATCH `/v4/tasks/{id}/toggle` ya ilustra una operación parcial.

---

## Rubrica de evaluación (sugerencia)

* **Funcionalidad base (30%)**: CRUD operativo, validaciones y handler de errores.
* **Buenas prácticas (25%)**: separación Controller/Service/Repository, códigos/headers correctos.
* **Persistencia (15%)**: JPA/H2 funcionando, consultas correctas, tests `@DataJpaTest`.
* **HTTP avanzado (20%)**: ETag, Cache-Control, paginación y `Link` bien formados.
* **Calidad técnica (10%)**: claridad de código, DTOs, nombres, comentarios mínimos, pruebas verdes.

---

## Errores/pitfalls comunes

* Olvidar `@Valid` en el método del controlador (no solo en el DTO).
* No mapear 400/404 en el `@RestControllerAdvice`.
* ETag fijo o mal formateado (debe incluir comillas).
* `Link` sin preservar `q`/`done` o con `page` mal calculada.
* Confundir `HEAD` (sin cuerpo) con `GET`.

---

## Sugerencias de andamiaje

* Usar `requests.http` para disparar endpoints sin Postman.
* Ejecutar tests por perfiles para focalizar problemas.
* Leer el **mensaje del test** antes de tocar el código (TDD suave).
* Hacer commits pequeños y mensajes claros (en el kit Git ya están modelados).

---

## Apéndice — Comandos rápidos

```bash
# Arrancar app
mvn spring-boot:run

# Todos los tests
mvn -q -Pall-tests test

# Por bloques
mvn -q -Ptests-basic test
mvn -q -Ptests-addons test
mvn -q -Ptests-extras test
mvn -q -Ptests-paging-cache test
mvn -q -Ptests-final test
```
