####retos más abajo, hay que pasarlo a otro fichero
# Cheat-sheet (Git + Maven + Spring)

## Moverse entre etapas (tags)

```bash
# ver tags
git tag -n
# moverte (HEAD detached)
git checkout v3
# crear rama desde un tag (recomendado p/ejercicios)
git checkout -b ejercicios/alumno-01 v3
```

## Ver diferencias entre etapas

```bash
git diff v1..v2 -- src/main/java
git diff v3..v4 -- src/main/java
```

## Trabajar con ramas

```bash
# crear y cambiar
git checkout -b feature/paginacion
# volver a main (si existiera)
git checkout main
```

## Guardar cambios temporales (stash)

```bash
# guardar trabajo sucio
git stash push -m "WIP: validaciones"
# ver stashes
git stash list
# aplicar el último
git stash pop
```

## Reset (cuidado, puede perder cambios)

```bash
# descartar cambios locales en el directorio de trabajo
git restore --source=HEAD -- .
# reset suave (mantiene cambios en staging)
git reset --soft HEAD~1
# reset duro (retrocede y borra cambios locales)
git reset --hard HEAD~1
```

## Cherry-pick (traer un commit concreto)

```bash
# identifica el SHA (git log --oneline)
git cherry-pick <sha>
```

## Revert (deshacer con commit inverso)

```bash
git revert <sha>
```

## Restaurar un fichero concreto

```bash
git restore --source=v4-extras -- src/main/java/com/example/minitasks/web/TaskControllerV4.java
```

## Rebase rápido (opcional, avanzado)

```bash
# rebasar tu rama sobre v4-extras
git checkout ejercicios/alumno-01
git rebase v4-extras
```

## Maven (perfiles de tests)

```bash
# todos
mvn -q -Pall-tests test
# básicos
mvn -q -Ptests-basic test
# addons (DataJpa + 400)
mvn -q -Ptests-addons test
# extras (data.sql + 404)
mvn -q -Ptests-extras test
# paging + cache
mvn -q -Ptests-paging-cache test
# final (pageable headers, búsqueda, PUT, HEAD)
mvn -q -Ptests-final test
```

## Arrancar la app

```bash
mvn spring-boot:run
# o desde IntelliJ: requests.http (play ▶️ en cada bloque)
```

## Problemas típicos y fixes rápidos

* **“Detached HEAD”**: crea una rama desde el tag antes de tocar código:

  ```bash
  git checkout -b ejercicios/alumno-01 v3
  ```
* **Conflictos al rebase**: resuelve, `git add .`, continúa:

  ```bash
  git rebase --continue
  ```
* **H2 no arranca**: revisa `application.properties` (URL y `ddl-auto`).
* **Tests @WebMvcTest fallan por beans faltantes**: añade `@MockitoBean` del service/repo en el test.

---

# Retos rápidos con checkpoints

## Reto 1 — `updatedAt` + ETag “real”

**Objetivo:** Añadir `updatedAt` a `Task` y generar ETag con hash de `(ids, done, max(updatedAt))`.

* **Criterios de aceptación:**

  * La entidad `Task` tiene `@Column` `updatedAt` y se actualiza en `@PrePersist/@PreUpdate`.
  * `GET /v4/tasks` devuelve un **ETag** que cambia cuando modificas una tarea.
  * Test manual: dos `GET` seguidos → segundo con `If-None-Match` devuelve **304**.
* **Pistas:**

  * ETag: `DigestUtils.md5DigestAsHex(string.getBytes())` (añade `spring-core`) o simple `String.valueOf(maxUpdatedAt.toEpochMilli())`.
  * No te olvides de las **comillas** en el ETag.

## Reto 2 — Filtro combinado `done` + `q` (paginado)

**Objetivo:** Soportar `GET /v4/tasks?page&size&done&q`.

* **Criterios de aceptación:**

  * Repositorio con `Page<Task> findByDoneAndTitleContainingIgnoreCase(boolean, String, Pageable)`.
  * `TaskService.listPage(done,q,pageable)` combina filtros cuando ambos están presentes.
  * Cabecera `Link` preserva `done` y `q`.
* **Pistas:**

  * Revisa `buildExtra(q,done)` en el controlador.
  * Añade tests tipo `tests-final` duplicando uno de búsqueda y ajustando parámetros.

## Reto 3 — Sort múltiple

**Objetivo:** Permitir `sort=done,asc&sort=title,desc`.

* **Criterios de aceptación:**

  * `GET /v4/tasks?page=0&size=5&sort=done,asc&sort=title,desc` devuelve orden correcto.
* **Pistas:**

  * Spring ya parsea múltiples `sort` en `Pageable`.
  * Solo asegúrate de **propagar** el `Pageable` hasta el repo.

## Reto 4 — DTO de salida (ViewModel)

**Objetivo:** No exponer entidad; devuelve `TaskView` con `id,title,done` y `links.self`.

* **Criterios de aceptación:**

  * `GET /v4/tasks` (paginado o no) devuelve **lista de DTOs**, no entidades JPA.
  * `links.self` correcto por cada recurso (`/v4/tasks/{id}`).
* **Pistas:**

  * Crea `TaskView` (record).
  * Mapea en el controlador: `tasks.stream().map(TaskView::from)`.

## Reto 5 — Validación enriquecida y mensajes

**Objetivo:** Reglas `title` [3..100], trim no vacío. Mensajes claros.

* **Criterios de aceptación:**

  * `POST /v4/tasks` con `"  "` => 400 y mensaje comprensible.
  * `PUT /v4/tasks/{id}` aplica la misma validación si `title` viene presente.
* **Pistas:**

  * `@NotBlank @Size(min=3,max=100)` en DTO.
  * En `UpdateTaskDTO`, valida solo si `title != null`.

## Reto 6 — HEAD y `X-Total-Count` “real”

**Objetivo:** `HEAD /v4/tasks?page=size=` debe retornarte totales correctos con filtros.

* **Criterios de aceptación:**

  * Con `done=true` y `q=spring`, el `X-Total-Count` cambia respecto a sin filtros.
* **Pistas:**

  * Reutiliza `service.listPage(done,q,pageable)` y **solo headers**.

---

## Mini-checklist para evaluar (por reto)

* [ ] Tests verdes.
* [ ] Código del controlador **delgado** (sin lógica de negocio).
* [ ] Servicio con lógica y transacciones.
* [ ] Repositorio solo con consultas.
* [ ] Headers correctos (ETag/Link/X-Total-Count).
* [ ] Validaciones y errores JSON coherentes.

¿Quieres que te empaquete estos **retos en un PDF** (para proyectar) y un set de **tests semilla** por cada reto (fallando primero) para que el alumnado haga TDD?
