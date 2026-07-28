
# Introducción a .NET y C# (para este proyecto)

## 1. ¿Qué es .NET?

**.NET** es una **plataforma de desarrollo** creada por Microsoft para construir aplicaciones:
- Web (APIs REST, webs)  
- Escritorio
- Servicios
- Consola
- Cloud / microservicios
    

Incluye:
- Un **runtime** (para ejecutar aplicaciones)
- Un **SDK** (herramientas para compilar, crear proyectos, ejecutar)
- Un conjunto de **librerías estándar**
- Varios **lenguajes** (principalmente C#)
    

En este proyecto usaremos:
- **.NET 10**
- **C#**
- **ASP.NET Core Web API**
    

---

## 2. ¿Qué es C#?
**C#** es el lenguaje principal de .NET.
Características clave (comparándolo con lo que ya sabéis):
- Tipado estático (como Java / Kotlin)
- Orientado a objetos
- Soporte moderno:
    - lambdas / funciones anónimas       
    - async / await       
    - LINQ (estilo streams)
- Sintaxis clara y muy expresiva
    

Ejemplo rápido:

```csharp
public int Sumar(int a, int b)
{
    return a + b;
}
```

Nada “extraño” si vienes de Java o Kotlin.

---

## 3. ¿Qué es ASP.NET Core Web API?

Es el **framework web** de .NET para crear **APIs REST**.

Equivalencias aproximadas:

|Lo que conoces|En .NET|
|---|---|
|Spring Boot|ASP.NET Core|
|Controller|Controller|
|@GetMapping|[HttpGet]|
|@PostMapping|[HttpPost]|
|DTO|DTO|
|Service|Service|
|Repository|Repository|

Conceptualmente **no hay nada nuevo**, solo cambia la sintaxis.

---

## 4. Cómo encaja con lo que ya sabéis

### Java / Kotlin
- Controllers → Controllers
- Anotaciones → Attributes
- Clases, métodos, paquetes → igual
- Dependency Injection → integrado en el framework
- ORM → Entity Framework Core (similar a JPA / Hibernate)

### JavaScript / Node
- API REST   
- JSON
- HTTP verbs
- Swagger
- Middleware (pipeline)

### Python
- Código claro y legible   
- Poco “boilerplate” en APIs sencillas
- Énfasis en la lógica, no en la configuración
    

👉 La **arquitectura mental** es la misma.

---

## 5. Vocabulario mínimo de .NET (imprescindible)

### SDK

Conjunto de herramientas para:

- crear proyectos
- compilar
- ejecutar
    

Comando principal:

```bash
dotnet
```

---

### Runtime

Entorno que ejecuta aplicaciones .NET ya compiladas.

(No lo tocas directamente.)

---

### Solution (`.sln`)
Archivo que agrupa uno o varios proyectos.
Equivalente conceptual:
- Maven multi-módulo
- Gradle multi-project

---

### Project (`.csproj`)
Proyecto individual.
Ejemplo:
- una API
- una librería
- un test

---

### Controller
Clase que expone endpoints HTTP.

```csharp
[ApiController]
[Route("api/[controller]")]
public class RecetasController : ControllerBase
```

---

### Attribute
Decorador entre corchetes `[]`.
Ejemplos:

```csharp
[HttpGet]
[HttpPost]
[Route("api/recetas")]
```

Equivalente a anotaciones en Java/Kotlin.

---

### IActionResult
Tipo de retorno de un endpoint HTTP.
Permite devolver:
- 200 OK
- 404 NotFound
- 400 BadRequest
- etc.

---

### Swagger / OpenAPI
Documentación automática de la API.
Permite:
- ver endpoints
- probarlos
- ver esquemas JSON

---

## 6. Qué **NO** se espera que aprendáis ahora
Este proyecto **NO pretende** que dominéis:
- C# avanzado
- .NET internals
- Razor / MVC
- Blazor
- Seguridad avanzada
- Configuración profunda del runtime
    

Solo debéis:
- **leer**
- **entender**
- **relacionar conceptos**
- **hacer preguntas**

---

## 7. Cómo leer este proyecto
Recomendación:
1. Entender **qué endpoint es**
2. Ver **qué método HTTP usa**
3. Ver **qué devuelve**
4. Ignorar detalles sintácticos secundarios
5. Compararlo mentalmente con Spring / Laravel / Node
6. Fijarse en la **lógica de negocio** y no en el lenguaje

---

## 8. Mensaje importante
> No se os evalúa de .NET ni de C#.  
> Se os evalúa de **arquitectura backend, APIs REST y razonamiento técnico**.

---

# Cómo entender el proyecto RecetasApi.Api

Esta sección es el mapa del proyecto real que vais a leer.

## 9. Estructura mínima del proyecto

Carpetas principales dentro de `RecetasApi.Api`:

- `Program.cs` → punto de arranque de la API (equivalente a `main`)
- `Controllers/` → endpoints HTTP
- `Domain/` → entidades del dominio (modelo de datos)
- `Contracts/` → DTOs de entrada/salida (lo que viaja por la API)
- `Infrastructure/` → acceso a datos (repositorio + EF Core + DB)
- `Mapping/` → conversión entre entidades y DTOs

Piensa en esto como: **Controller → Service/Repo → DB**, con DTOs en los bordes.

---

## 10. Flujo de una petición (idea general)

Ejemplo: `POST /api/recetas`

1. Llega un JSON (DTO `CreateRecetaRequest`).
2. El controller lo recibe y aplica reglas de negocio.
3. Se transforma a entidad `Receta` (mapping).
4. El repositorio guarda la entidad (EF Core + SQLite).
5. Se devuelve un DTO de respuesta (`RecetaDetailDto`).

---

## 11. Entidades del dominio (lo importante)

Las clases en `Domain/` representan el **modelo de datos**:

- `Receta`: título, autor, ingredientes, pasos, likes, fechas
- `IngredienteItem`: nombre + cantidad
- `Paso`: orden, descripción, duración
- `RecetaLike`: usuario que dio like
- `RecetaComentario`: usuario + texto

Detalles clave del modelo:

- Una receta puede tener **ingredientes** y **pasos** (listas).
- Una receta puede ser **copia** de otra (`OriginalRecetaId`).
- Una receta puede quedar **archivada** (`IsArchived`) y entonces no se edita.

---

## 12. DTOs (Contracts)

Los DTOs son **la forma del JSON** que entra/sale:

- `CreateRecetaRequest`, `UpdateRecetaRequest` → lo que envía el cliente
- `RecetaSummaryDto` → lo que se lista en colecciones
- `RecetaDetailDto` → detalle completo de una receta
- `ComentarioDto` → comentarios

Idea clave: **las entidades nunca salen directamente**, siempre se transforman a DTOs.

---

## 13. Mapeo (Mapping)

En `Mapping/` están los métodos que convierten:

- Entidad → DTO (`ToSummaryDto`, `ToDetailDto`)
- DTO → Entidad (`ToEntity`, `Apply`)

Esto separa la **lógica del dominio** del **formato de la API**.

---

## 14. Acceso a datos (Infrastructure)

El acceso a datos está en un **repositorio**:

- Interfaz común: `IRecetasRepository`
- Implementaciones:
  - `InMemoryRecetasRepository` (memoria, útil para pruebas)
  - `EfRecetasRepository` (persistente con SQLite)

En `Program.cs` se elige qué implementación usar (inyección de dependencias).

---

## 15. Base de datos (EF Core + SQLite)

Se usa **Entity Framework Core** como ORM y **SQLite** como base de datos.

- `RecetasDbContext` define las tablas y relaciones
- `Migrations/` guarda el histórico de cambios del esquema
- El archivo real está en `recetas.db`
- La cadena de conexión está en `appsettings.json`

---

## 16. Endpoints principales (RecetasController)

Endpoints más importantes:

- `GET /api/recetas` → listado (solo no archivadas)
- `GET /api/recetas/{id}` → detalle
- `POST /api/recetas` → crear
- `PUT /api/recetas/{id}` → actualizar
- `DELETE /api/recetas/{id}` → borrar o archivar
- `POST /api/recetas/{id}/likes` → dar like
- `DELETE /api/recetas/{id}/likes` → quitar like
- `GET /api/recetas/{id}/likes` → contador de likes
- `GET /api/recetas/{id}/comentarios` → ver comentarios
- `POST /api/recetas/{id}/comentarios` → comentar
- `DELETE /api/recetas/{id}/comentarios/{comentarioId}` → borrar comentario
- `POST /api/recetas/{id}/copiar` → crear copia
- `POST /api/recetas/{id}/desvincular-copia` → convertir copia en original
- `GET /api/recetas/historico` → ver archivadas

El usuario actual se simula con el header `X-User` (si no, usa "demo").

---

## 17. Reglas de negocio que conviene ver

En `RecetasController` y el repositorio hay reglas reales:

- No puedes crear dos recetas con el mismo título y autor.
- Si una receta tiene muchos likes, **se archiva** en vez de borrarse.
- Una receta archivada **no se edita ni acepta likes/comentarios**.
- Solo el autor de un comentario puede borrarlo.
- Se puede copiar una receta y luego desvincular la copia.

---

## 18. Qué mirar primero (orden recomendado)

1. `Program.cs` → cómo arranca la API
2. `Controllers/RecetasController.cs` → endpoints y reglas
3. `Domain/` → modelo
4. `Contracts/` → JSON de entrada/salida
5. `Infrastructure/` → acceso a datos
6. `Mapping/` → conversiones

Con esto ya podéis leer el proyecto con sentido, aunque no dominéis .NET.
