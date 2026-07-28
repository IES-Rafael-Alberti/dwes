Perfecto. Te dejo un **prompt limpio, específico y “a prueba de Codex”**, pensado **para usar directamente en Rider**, que:

* **parte explícitamente de lo que ya existe**
* **NO reimplementa Iteración 1** si ya está completa
* guía **Iteración 2 en adelante** una vez Iteración 1 esté cerrada
* fuerza a Codex a **leer el código actual antes de escribir nada**
* evita refactors innecesarios

Puedes copiarlo tal cual.

---

# Prompt para Codex (JetBrains Rider/VSCode)

## Proyecto: TO-DO API REST con .NET 8 / 9 / 10 + Frontend puro (HTML/JS/CSS)

### Contexto general

Estás trabajando sobre un proyecto **YA EXISTENTE** de una aplicación **TO-DO API REST** en **.NET (C#)** con frontend mínimo en **HTML + JavaScript + CSS puro**, servido desde `wwwroot`.

El proyecto sigue una arquitectura clara:

```
Controller → Service → Repository → EF Core (SQLite)
```

y **la Iteración 1 está INCOMPLETA** y hay que **terminarla primero**.

⚠️ **MUY IMPORTANTE**
Antes de escribir código nuevo:

1. **Explora el código actual**
2. Identifica:

    * `TasksController`
    * DTOs (`TaskCreateDto`, `TaskReadDto`, `TaskUpdateDto`)
    * `ITasksService` y `TasksService`
    * `ITasksRepository` y `TasksEfRepository`
    * `TodoDbContext`
    * `Program.cs`
3. **NO refactorices ni renombres nada existente**
4. **NO cambies la arquitectura**

Todo lo nuevo debe **encajar** en lo que ya existe.

---

## Estado actual (Iteración 1 – CASI COMPLETA)

### Modelo Task (objetivo Iteración 1)

El objetivo es completar el modelo y la API con estos campos:

* `Id`
* `Title`
* `Description` (opcional)
* `CreationDate` (auto)
* `DueDate` (obligatorio)
* `Status` (`Pending | InProgress | Completed`)

### Estado real actual (lo que hay en el código)

* Modelo completo con `Description`, `CreationDate`, `DueDate`, `Status`
* DTOs completos en `TodoApi/DTOs`
* Mapping en `TodoApi/Mapping/TasksMapping.cs`
* Frontend conectado a la API con panel debug
* Falta aplicar migracion en la base de datos local si no se ha ejecutado

✅ **Cerrar Iteración 1**
✅ **Luego se arranca Iteración 2**

---

## Iteración 2 — Usuarios + Autenticación JWT

### Objetivo

Añadir **usuarios y autenticación JWT** con el **mínimo impacto posible** sobre el código existente.

### Requisitos funcionales

1. **Entidad User**

    * `Id`
    * `Username` (único) //¿necesario?
    * `Email` (único)
    * `PasswordHash`
    * `AvatarUrl (imagen del usuario, puede ser null, lo haremos al final de todo)`
    * `Role` (`User`, `Supervisor`, `Admin`)

2. **Persistencia**

    * Tabla `Users` en SQLite
    * Migración EF Core
    * No usar ASP.NET Identity ¿Por qué? ¿Demasiado complejo?

3. **Autenticación**

    * JWT **sencillo y docente**
    * Endpoints:

        * `POST /api/auth/register`→  devuelve JWT
        * `POST /api/auth/login` → devuelve JWT
        * `POST /api/auth/logout`
    * Usuarios de prueba permitidos (seed o hardcoded si se indica)

4. **Seguridad**

    * Proteger endpoints de `/api/tasks` con `[Authorize]`
    * Activar HTTPS en desarrollo (`dotnet dev-certs https --trust`)

5. **Arquitectura**

    * Crear:

        * `AuthController`
        * `IAuthService` / `AuthService`
    * NO mezclar lógica de autenticación con `TasksService`

6. **Integración mínima**

    * Añadir `UserId` a `TodoTask`
    * Al crear tareas, asociarlas al usuario autenticado
    * No implementar todavía control de propiedad (eso es Iteración 3)

---

## Iteración 3 — Propiedad de tareas

### Objetivo

Restringir el acceso a las tareas según el usuario autenticado.

### Requisitos

* Cada tarea pertenece a un usuario (`Task.UserId`)
* Cambios:

    * `GET /api/tasks` → devuelve solo tareas del usuario actual
    * `PUT /api/tasks/{id}` y `DELETE /api/tasks/{id}`

        * comprobar propiedad
        * devolver **403 Forbidden** si no es suya
* La lógica de propiedad debe ir en el **Service**, no en el Controller

---

## Iteración 4 — Roles (User / Supervisor / Admin)

### Objetivo

Añadir autorización por roles usando JWT + policies.

### Reglas

* **User**

    * Solo sus tareas
* **Supervisor**

    * Ver todas las tareas
    * Editar tareas ajenas
    * ❌ No puede:

        * borrar tareas ajenas
        * marcar tareas ajenas como `Completed`
* **Admin**

    * Control total (CRUD de tareas y usuarios)

### Requisitos técnicos

* Usar `[Authorize(Roles = "...")]` o policies
* No duplicar lógica entre controller y service
* Mantener código claro y docente

---

## Reglas estrictas para Codex

* ❌ NO refactorizar código existente

* ❌ NO renombrar carpetas ni namespaces

* ❌ NO introducir frameworks externos

* ❌ NO usar Identity

* ❌ NO mover lógica al controller

* ✅ Todo el código nuevo debe:

    * seguir el estilo existente
    * ser incremental
    * ser comprensible para FP

Si algo no está claro en el código existente:
👉 **detente y analízalo antes de escribir**

---

## Tareas inmediatas (Iteración 1)

1. Aplicar migracion de base de datos si no se ha hecho.
2. Ajustar o ampliar tests si es necesario.
3. Verificar endpoints desde `wwwroot` y curl/httpie.

---

## Documentación requerida (Markdown)

Crear o completar estos documentos conforme avances:

* `docs/05_ITERATION_2_JWT.md`
* `docs/06_ITERATION_3_OWNERSHIP.md`
* `docs/07_ITERATION_4_ROLES.md`

Explica:

* decisiones de diseño
* fragmentos clave de código
* ejemplos de errores comunes

---

### Resultado esperado

Un proyecto:

* coherente
* incremental
* sin refactors innecesarios
* con seguridad progresiva
* entendible para alumnos de FP

---

Si quieres, en el siguiente mensaje puedo:

* **adaptar este prompt aún más a Codex (modo ultra estricto)**
* o **hacer una versión “resumida” para alumnado**
