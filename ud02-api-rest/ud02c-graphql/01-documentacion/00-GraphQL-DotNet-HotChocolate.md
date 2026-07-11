¡Perfecto! Empecemos con .NET y Hot Chocolate, entonces. Te guiaré paso a paso.

**1. Entorno de Desarrollo:**

*   **IDE:** Visual Studio 2022 (o VS Code con la extensión C#)
*   **.NET SDK:** Asegúrate de tener instalado el SDK .NET 8.0 o superior.
*   **Hot Chocolate:** Lo instalaremos con NuGet.

**2. Creación del Proyecto:**

Abre Visual Studio y crea un nuevo proyecto:

*   Tipo de proyecto: ASP.NET Core Web API
*   Nombre: `MiApiGraphQL`
*   Framework: .NET 8.0 (o superior)

**3. Instalación de Hot Chocolate:**

Abre la consola del proyecto (View -> Terminal) y ejecuta:

```bash
dotnet add package HotChocolate
dotnet add package HotChocolate.AspNetCore
```

**4. Configuración de Hot Chocolate:**

Abre `Program.cs` y agrega lo siguiente:

```csharp
builder.Services.AddGraphQL(options =>
{
    options.EnableFeedback = true;
});

builder.Services.AddCors(options =>
{
    options.AddBackgroundTasks(true);
});
```

**5. Definición de Esquema GraphQL:**

Crea un archivo llamado `schema.cs` en la raíz del proyecto.  Este archivo definirá tus tipos, queries y mutations.

```csharp
using HotChocolate;

namespace MiApiGraphQL;

public class Query
{
    [GraphQLQuery]
    public string GetHello()
    {
        return "Hello, GraphQL!";
    }
}
```

**6. Ejecución de la API:**

Ejecuta la aplicación.  Debería aparecer un mensaje en la consola indicando que la API está corriendo en `https://localhost:5001`.

**7. Prueba con un Cliente GraphQL:**

*   **GraphiQL:**  Hot Chocolate incluye un endpoint GraphiQL para probar la API.  Visita `https://localhost:5001/graphql` en tu navegador.
*   **Postman:**  Puedes usar Postman para enviar peticiones GraphQL.

**8. Ejemplo con un Tipo y Datos:**

Modifica `schema.cs`:

```csharp
using HotChocolate;

namespace MiApiGraphQL;

public class Query
{
    [GraphQLQuery]
    public string GetHello()
    {
        return "Hello, GraphQL!";
    }

    [GraphQLQuery]
    public Todo GetTodo()
    {
        return new Todo
        {
            Id = 1,
            Title = "Learn GraphQL",
            Completed = false
        };
    }
}

public record Todo(int Id, string Title, bool Completed);
```

Ahora, al ejecutar la query `query { todo { id title completed } }` en GraphiQL, deberías obtener los datos del todo.

**Próximos pasos:**

*   **Mutations:** Aprende a crear mutations para crear, actualizar y eliminar datos.
*   **Resolvers:** Explora cómo usar resolvers para obtener datos de fuentes externas (bases de datos, APIs, etc.).
*   **Directives:**  Aprende a usar directives para agregar lógica a tus queries y mutations.


## **Mutations en GraphQL con Hot Chocolate**

Las mutations son operaciones que **modifican datos** (crear, actualizar, eliminar), a diferencia de las queries que solo consultan.

### **Paso 1: Crear una clase Mutation**

Crea un archivo `Mutation.cs` en tu proyecto:

```csharp
namespace MiApiGraphQL;

public class Mutation
{
    private static List<Todo> _todos = new()
    {
        new Todo(1, "Learn GraphQL", false),
        new Todo(2, "Build an API", false)
    };

    public Todo AddTodo(string title)
    {
        var newTodo = new Todo(_todos.Count + 1, title, false);
        _todos.Add(newTodo);
        return newTodo;
    }

    public Todo? UpdateTodo(int id, string? title, bool? completed)
    {
        var todo = _todos.FirstOrDefault(t => t.Id == id);
        if (todo == null) return null;

        var updatedTodo = todo with
        {
            Title = title ?? todo.Title,
            Completed = completed ?? todo.Completed
        };

        _todos[_todos.FindIndex(t => t.Id == id)] = updatedTodo;
        return updatedTodo;
    }

    public bool DeleteTodo(int id)
    {
        var todo = _todos.FirstOrDefault(t => t.Id == id);
        if (todo == null) return false;

        _todos.Remove(todo);
        return true;
    }
}
```

### **Paso 2: Registrar Mutation en Program.cs**

Modifica `Program.cs` para incluir las mutations:

```csharp
builder.Services
    .AddGraphQLServer()
    .AddQueryType<Query>()
    .AddMutationType<Mutation>();  // ← Añade esta línea
```

### **Paso 3: Actualizar Query para ver todos los Todos**

Modifica `Query.cs` para que use la misma lista:

```csharp
namespace MiApiGraphQL;

public class Query
{
    private static List<Todo> _todos = new()
    {
        new Todo(1, "Learn GraphQL", false),
        new Todo(2, "Build an API", false)
    };

    public string GetHello() => "Hello, GraphQL!";

    public List<Todo> GetTodos() => _todos;

    public Todo? GetTodo(int id) => _todos.FirstOrDefault(t => t.Id == id);
}
```

### **Paso 4: Probar las Mutations en GraphiQL**

Ejecuta tu aplicación y ve a `https://localhost:5001/graphql`

**Crear un Todo:**
```graphql
mutation {
  addTodo(title: "Aprender Mutations") {
    id
    title
    completed
  }
}
```

**Actualizar un Todo:**
```graphql
mutation {
  updateTodo(id: 1, completed: true) {
    id
    title
    completed
  }
}
```

**Eliminar un Todo:**
```graphql
mutation {
  deleteTodo(id: 2)
}
```

**Consultar todos los Todos:**
```graphql
query {
  todos {
    id
    title
    completed
  }
}
```

### **Conceptos clave:**

✅ **Mutations modifican datos** (POST/PUT/DELETE en REST)
✅ **Devuelven el objeto modificado** para confirmación
✅ **Se ejecutan secuencialmente** (a diferencia de queries que pueden ejecutarse en paralelo)



## **Resolvers en GraphQL con Hot Chocolate**

Los resolvers son funciones que **obtienen datos** para un campo específico. Hot Chocolate los crea automáticamente, pero puedes personalizarlos para:

- Obtener datos de bases de datos
- Llamar a APIs externas
- Aplicar lógica de negocio
- Resolver relaciones entre tipos

### **Paso 1: Crear un escenario con relaciones**

Vamos a añadir un tipo `User` y relacionarlo con `Todo`:

Crea un archivo `Models.cs`:

```csharp
namespace MiApiGraphQL;

public record Todo(int Id, string Title, bool Completed, int UserId);

public record User(int Id, string Name, string Email);
```

### **Paso 2: Crear datos de ejemplo**

Crea un archivo `Data.cs` para simular una base de datos:

```csharp
namespace MiApiGraphQL;

public static class Data
{
    public static List<User> Users = new()
    {
        new User(1, "Ana García", "ana@example.com"),
        new User(2, "Carlos López", "carlos@example.com")
    };

    public static List<Todo> Todos = new()
    {
        new Todo(1, "Learn GraphQL", false, 1),
        new Todo(2, "Build an API", false, 1),
        new Todo(3, "Deploy to production", true, 2)
    };
}
```

### **Paso 3: Crear un Resolver personalizado**

Ahora vamos a extender `Todo` para que pueda resolver su `User` asociado.

Crea un archivo `TodoExtensions.cs`:

```csharp
namespace MiApiGraphQL;

[ExtendObjectType(typeof(Todo))]
public class TodoExtensions
{
    // Resolver que obtiene el usuario asociado a un Todo
    public User? GetUser([Parent] Todo todo)
    {
        return Data.Users.FirstOrDefault(u => u.Id == todo.UserId);
    }
}
```

**Explicación:**
- `[ExtendObjectType(typeof(Todo))]` → Extiende el tipo `Todo` con campos adicionales
- `[Parent]` → Inyecta el objeto `Todo` actual
- `GetUser` → Se convierte en un campo `user` en GraphQL

### **Paso 4: Crear resolver inverso (User → Todos)**

En `UserExtensions.cs`:

```csharp
namespace MiApiGraphQL;

[ExtendObjectType(typeof(User))]
public class UserExtensions
{
    // Resolver que obtiene todos los Todos de un usuario
    public List<Todo> GetTodos([Parent] User user)
    {
        return Data.Todos.Where(t => t.UserId == user.Id).ToList();
    }
}
```

### **Paso 5: Actualizar Query.cs**

```csharp
namespace MiApiGraphQL;

public class Query
{
    public string GetHello() => "Hello, GraphQL!";

    public List<Todo> GetTodos() => Data.Todos;

    public Todo? GetTodo(int id) => Data.Todos.FirstOrDefault(t => t.Id == id);

    public List<User> GetUsers() => Data.Users;

    public User? GetUser(int id) => Data.Users.FirstOrDefault(u => u.Id == id);
}
```

### **Paso 6: Registrar los resolvers en Program.cs**

```csharp
builder.Services
    .AddGraphQLServer()
    .AddQueryType<Query>()
    .AddMutationType<Mutation>()
    .AddTypeExtension<TodoExtensions>()    // ← Añade esto
    .AddTypeExtension<UserExtensions>();   // ← Y esto
```

### **Paso 7: Probar en GraphiQL**

**Consultar Todos con su Usuario:**
```graphql
query {
  todos {
    id
    title
    completed
    user {
      id
      name
      email
    }
  }
}
```

**Consultar Usuario con sus Todos:**
```graphql
query {
  users {
    id
    name
    email
    todos {
      id
      title
      completed
    }
  }
}
```

**Consultar solo lo que necesitas:**
```graphql
query {
  user(id: 1) {
    name
    todos {
      title
    }
  }
}
```

### **Conceptos clave:**

✅ **Resolvers personalizados** permiten lógica compleja
✅ **`[Parent]`** inyecta el objeto padre en el resolver
✅ **Lazy loading** - Solo se ejecutan si el cliente solicita ese campo
✅ **N+1 Problem** - Cuidado con consultas repetidas (lo veremos con DataLoaders más adelante)


Perfecto, vamos con **Directives**.

## **Directives en GraphQL con Hot Chocolate**

Las directives son **anotaciones** que modifican el comportamiento de queries, mutations o campos. Se usan con `@` en GraphQL.

### **Tipos de Directives:**

1. **Built-in** (incluidas en GraphQL): `@skip`, `@include`, `@deprecated`
2. **Custom** (las que tú creas): autenticación, autorización, validación, transformación de datos

---

## **Parte 1: Directives Built-in**

### **@skip y @include**

Permiten incluir/excluir campos condicionalmente.

**Prueba en GraphiQL:**

```graphql
query GetTodos($includeUser: Boolean!) {
  todos {
    id
    title
    user @include(if: $includeUser) {
      name
    }
  }
}
```

**Variables:**
```json
{
  "includeUser": true
}
```

Cambia `includeUser` a `false` y verás que el campo `user` desaparece.

### **@deprecated**

Marca campos como obsoletos.

En `Query.cs`:

```csharp
namespace MiApiGraphQL;

public class Query
{
    public string GetHello() => "Hello, GraphQL!";

    [GraphQLDeprecated("Use 'todos' instead")]
    public List<Todo> GetAllTodos() => Data.Todos;

    public List<Todo> GetTodos() => Data.Todos;

    public Todo? GetTodo(int id) => Data.Todos.FirstOrDefault(t => t.Id == id);

    public List<User> GetUsers() => Data.Users;

    public User? GetUser(int id) => Data.Users.FirstOrDefault(u => u.Id == id);
}
```

En GraphiQL verás una advertencia cuando uses `allTodos`.

---

## **Parte 2: Custom Directives**

Vamos a crear directives personalizadas para casos reales.

### **Ejemplo 1: @uppercase - Transformar texto**

Crea `UpperCaseDirective.cs`:

```csharp
using HotChocolate.Types;

namespace MiApiGraphQL;

public class UpperCaseDirectiveType : DirectiveType
{
    protected override void Configure(IDirectiveTypeDescriptor descriptor)
    {
        descriptor
            .Name("uppercase")
            .Location(DirectiveLocation.FieldDefinition);
    }
}

public class UpperCaseDirective
{
    public string Apply(string value)
    {
        return value.ToUpper();
    }
}
```

Registra en `Program.cs`:

```csharp
builder.Services
    .AddGraphQLServer()
    .AddQueryType<Query>()
    .AddMutationType<Mutation>()
    .AddTypeExtension<TodoExtensions>()
    .AddTypeExtension<UserExtensions>()
    .AddDirectiveType<UpperCaseDirectiveType>();  // ← Añade esto
```

Úsala en `UserExtensions.cs`:

```csharp
namespace MiApiGraphQL;

[ExtendObjectType(typeof(User))]
public class UserExtensions
{
    public List<Todo> GetTodos([Parent] User user)
    {
        return Data.Todos.Where(t => t.UserId == user.Id).ToList();
    }

    [GraphQLName("nameUpper")]
    [UseUpperCase]
    public string GetNameUpperCase([Parent] User user)
    {
        return user.Name;
    }
}

public class UseUpperCaseAttribute : ObjectFieldDescriptorAttribute
{
    protected override void OnConfigure(
        IDescriptorContext context,
        IObjectFieldDescriptor descriptor,
        System.Reflection.MemberInfo member)
    {
        descriptor.Use(next => async context =>
        {
            await next(context);

            if (context.Result is string value)
            {
                context.Result = value.ToUpper();
            }
        });
    }
}
```

**Prueba en GraphiQL:**

```graphql
query {
  users {
    name
    nameUpper
  }
}
```

---

### **Ejemplo 2: @auth - Autorización**

Crea `AuthDirective.cs`:

```csharp
using HotChocolate.Resolvers;
using HotChocolate.Types;

namespace MiApiGraphQL;

public class AuthDirectiveType : DirectiveType
{
    protected override void Configure(IDirectiveTypeDescriptor descriptor)
    {
        descriptor
            .Name("auth")
            .Location(DirectiveLocation.FieldDefinition)
            .Argument("role")
            .Type<StringType>();
    }
}

public class AuthAttribute : ObjectFieldDescriptorAttribute
{
    public string? Role { get; set; }

    protected override void OnConfigure(
        IDescriptorContext context,
        IObjectFieldDescriptor descriptor,
        System.Reflection.MemberInfo member)
    {
        descriptor.Use(next => async context =>
        {
            // Simular verificación de autenticación
            var isAuthenticated = context.ContextData.TryGetValue("isAuthenticated", out var auth)
                                  && auth is true;

            if (!isAuthenticated)
            {
                throw new GraphQLException("No autenticado");
            }

            // Verificar rol si se especifica
            if (Role != null)
            {
                var userRole = context.ContextData.TryGetValue("userRole", out var role)
                               ? role as string
                               : null;

                if (userRole != Role)
                {
                    throw new GraphQLException($"Requiere rol: {Role}");
                }
            }

            await next(context);
        });
    }
}
```

Úsala en `Mutation.cs`:

```csharp
namespace MiApiGraphQL;

public class Mutation
{
    [Auth(Role = "admin")]
    public Todo AddTodo(string title)
    {
        var newTodo = new Todo(Data.Todos.Count + 1, title, false, 1);
        Data.Todos.Add(newTodo);
        return newTodo;
    }

    [Auth(Role = "admin")]
    public Todo? UpdateTodo(int id, string? title, bool? completed)
    {
        var todo = Data.Todos.FirstOrDefault(t => t.Id == id);
        if (todo == null) return null;

        var updatedTodo = todo with
        {
            Title = title ?? todo.Title,
            Completed = completed ?? todo.Completed
        };

        Data.Todos[Data.Todos.FindIndex(t => t.Id == id)] = updatedTodo;
        return updatedTodo;
    }

    [Auth]
    public bool DeleteTodo(int id)
    {
        var todo = Data.Todos.FirstOrDefault(t => t.Id == id);
        if (todo == null) return false;

        Data.Todos.Remove(todo);
        return true;
    }
}
```

Configura el contexto en `Program.cs`:

```csharp
builder.Services
    .AddGraphQLServer()
    .AddQueryType<Query>()
    .AddMutationType<Mutation>()
    .AddTypeExtension<TodoExtensions>()
    .AddTypeExtension<UserExtensions>()
    .AddDirectiveType<AuthDirectiveType>()
    .AddHttpRequestInterceptor(async (context, executor, builder, ct) =>
    {
        // Simular autenticación (en producción, verificarías JWT)
        var isAuth = context.Request.Headers.ContainsKey("Authorization");
        builder.SetProperty("isAuthenticated", isAuth);

        // Simular rol de usuario
        if (isAuth)
        {
            var authHeader = context.Request.Headers["Authorization"].ToString();
            var role = authHeader.Contains("admin") ? "admin" : "user";
            builder.SetProperty("userRole", role);
        }

        await Task.CompletedTask;
    });
```

**Prueba en GraphiQL:**

Sin autenticación (fallará):
```graphql
mutation {
  addTodo(title: "Test") {
    id
    title
  }
}
```

Con autenticación (en HTTP Headers):
```
Authorization: Bearer admin-token
```

---

### **Ejemplo 3: @validate - Validación de datos**

Crea `ValidateDirective.cs`:

```csharp
using HotChocolate.Types;
using System.ComponentModel.DataAnnotations;

namespace MiApiGraphQL;

public class ValidateAttribute : ObjectFieldDescriptorAttribute
{
    public int MinLength { get; set; }
    public int MaxLength { get; set; }

    protected override void OnConfigure(
        IDescriptorContext context,
        IObjectFieldDescriptor descriptor,
        System.Reflection.MemberInfo member)
    {
        descriptor.Use(next => async context =>
        {
            var arguments = context.Selection.Field.Arguments;

            foreach (var arg in arguments)
            {
                if (context.ArgumentValue<string>(arg.Name) is string value)
                {
                    if (MinLength > 0 && value.Length < MinLength)
                    {
                        throw new GraphQLException(
                            $"{arg.Name} debe tener al menos {MinLength} caracteres");
                    }

                    if (MaxLength > 0 && value.Length > MaxLength)
                    {
                        throw new GraphQLException(
                            $"{arg.Name} no puede tener más de {MaxLength} caracteres");
                    }
                }
            }

            await next(context);
        });
    }
}
```

Úsala en `Mutation.cs`:

```csharp
[Auth(Role = "admin")]
[Validate(MinLength = 3, MaxLength = 100)]
public Todo AddTodo(string title)
{
    var newTodo = new Todo(Data.Todos.Count + 1, title, false, 1);
    Data.Todos.Add(newTodo);
    return newTodo;
}
```

**Prueba en GraphiQL:**

```graphql
mutation {
  addTodo(title: "ab") {
    id
    title
  }
}
```

Debería fallar con: "title debe tener al menos 3 caracteres"

---

### **Conceptos clave:**

✅ **Directives built-in** - `@skip`, `@include`, `@deprecated`
✅ **Custom directives** - Para lógica transversal (auth, validación, transformación)
✅ **Middleware pattern** - Las directives envuelven resolvers
✅ **Reutilización** - Una directive se puede aplicar a múltiples campos

---

## **Resumen completo:**

1. ✅ **Mutations** - Modificar datos (CRUD)
2. ✅ **Resolvers** - Obtener datos relacionados, lógica personalizada
3. ✅ **Directives** - Lógica transversal (auth, validación, transformación)

Perfecto, vamos con **DataLoaders**.

## **DataLoaders en GraphQL con Hot Chocolate**

Los DataLoaders resuelven el **problema N+1**: cuando consultas una lista de objetos y cada uno necesita resolver una relación, se ejecuta una query por cada elemento.

### **El Problema N+1**

**Sin DataLoader:**
```graphql
query {
  todos {      # 1 query
    title
    user {     # N queries (una por cada todo)
      name
    }
  }
}
```

Si tienes 100 todos, ejecutarías **101 queries** (1 para todos + 100 para usuarios).

**Con DataLoader:**
Solo ejecutarías **2 queries** (1 para todos + 1 batch para todos los usuarios).

---

## **Paso 1: Instalar el paquete de DataLoader**

```bash
dotnet add package HotChocolate.Data
```

---

## **Paso 2: Crear un DataLoader para Users**

Crea `UserDataLoader.cs`:

```csharp
using GreenDonut;

namespace MiApiGraphQL;

public class UserDataLoader : BatchDataLoader<int, User>
{
    public UserDataLoader(
        IBatchScheduler batchScheduler,
        DataLoaderOptions? options = null)
        : base(batchScheduler, options)
    {
    }

    protected override async Task<IReadOnlyDictionary<int, User>> LoadBatchAsync(
        IReadOnlyList<int> keys,
        CancellationToken cancellationToken)
    {
        // Simular consulta a base de datos
        Console.WriteLine($"🔍 DataLoader: Cargando {keys.Count} usuarios en batch");

        var users = Data.Users
            .Where(u => keys.Contains(u.Id))
            .ToDictionary(u => u.Id);

        await Task.Delay(100); // Simular latencia de BD

        return users;
    }
}
```

**Explicación:**
- `BatchDataLoader<TKey, TValue>` → Agrupa múltiples peticiones en una sola
- `LoadBatchAsync` → Se ejecuta UNA vez con todas las keys solicitadas
- Devuelve un diccionario `Key → Value`

---

## **Paso 3: Crear un DataLoader para Todos**

Crea `TodoDataLoader.cs`:

```csharp
using GreenDonut;

namespace MiApiGraphQL;

public class TodoDataLoader : GroupedDataLoader<int, Todo>
{
    public TodoDataLoader(
        IBatchScheduler batchScheduler,
        DataLoaderOptions? options = null)
        : base(batchScheduler, options)
    {
    }

    protected override async Task<ILookup<int, Todo>> LoadGroupedBatchAsync(
        IReadOnlyList<int> keys,
        CancellationToken cancellationToken)
    {
        Console.WriteLine($"🔍 DataLoader: Cargando todos para {keys.Count} usuarios en batch");

        var todos = Data.Todos
            .Where(t => keys.Contains(t.UserId))
            .ToLookup(t => t.UserId);

        await Task.Delay(100); // Simular latencia de BD

        return todos;
    }
}
```

**Diferencia:**
- `GroupedDataLoader` → Para relaciones **uno a muchos** (un usuario tiene muchos todos)
- `BatchDataLoader` → Para relaciones **uno a uno** (un todo tiene un usuario)

---

## **Paso 4: Actualizar los Resolvers para usar DataLoaders**

Modifica `TodoExtensions.cs`:

```csharp
namespace MiApiGraphQL;

[ExtendObjectType(typeof(Todo))]
public class TodoExtensions
{
    // ANTES (sin DataLoader):
    // public User? GetUser([Parent] Todo todo)
    // {
    //     return Data.Users.FirstOrDefault(u => u.Id == todo.UserId);
    // }

    // DESPUÉS (con DataLoader):
    public async Task<User?> GetUser(
        [Parent] Todo todo,
        UserDataLoader userDataLoader,
        CancellationToken cancellationToken)
    {
        return await userDataLoader.LoadAsync(todo.UserId, cancellationToken);
    }
}
```

Modifica `UserExtensions.cs`:

```csharp
namespace MiApiGraphQL;

[ExtendObjectType(typeof(User))]
public class UserExtensions
{
    // ANTES (sin DataLoader):
    // public List<Todo> GetTodos([Parent] User user)
    // {
    //     return Data.Todos.Where(t => t.UserId == user.Id).ToList();
    // }

    // DESPUÉS (con DataLoader):
    public async Task<IEnumerable<Todo>> GetTodos(
        [Parent] User user,
        TodoDataLoader todoDataLoader,
        CancellationToken cancellationToken)
    {
        return await todoDataLoader.LoadAsync(user.Id, cancellationToken);
    }

    [GraphQLName("nameUpper")]
    [UseUpperCase]
    public string GetNameUpperCase([Parent] User user)
    {
        return user.Name;
    }
}
```

---

## **Paso 5: Registrar DataLoaders en Program.cs**

```csharp
builder.Services
    .AddGraphQLServer()
    .AddQueryType<Query>()
    .AddMutationType<Mutation>()
    .AddTypeExtension<TodoExtensions>()
    .AddTypeExtension<UserExtensions>()
    .AddDirectiveType<AuthDirectiveType>()
    .RegisterService<UserDataLoader>()     // ← Añade esto
    .RegisterService<TodoDataLoader>()     // ← Y esto
    .AddHttpRequestInterceptor(async (context, executor, builder, ct) =>
    {
        var isAuth = context.Request.Headers.ContainsKey("Authorization");
        builder.SetProperty("isAuthenticated", isAuth);

        if (isAuth)
        {
            var authHeader = context.Request.Headers["Authorization"].ToString();
            var role = authHeader.Contains("admin") ? "admin" : "user";
            builder.SetProperty("userRole", role);
        }

        await Task.CompletedTask;
    });

// Registrar los DataLoaders como servicios
builder.Services.AddScoped<UserDataLoader>();
builder.Services.AddScoped<TodoDataLoader>();
```

---

## **Paso 6: Probar en GraphiQL**

Ejecuta esta query:

```graphql
query {
  todos {
    id
    title
    user {
      id
      name
      email
    }
  }
}
```

**Observa la consola de Visual Studio:**

```
🔍 DataLoader: Cargando 2 usuarios en batch
```

En lugar de ejecutar una query por cada todo, **agrupa todas las peticiones** y ejecuta una sola query.

---

## **Paso 7: Comparación con/sin DataLoader**

**Sin DataLoader (problema N+1):**
```
Query 1: SELECT * FROM todos
Query 2: SELECT * FROM users WHERE id = 1
Query 3: SELECT * FROM users WHERE id = 1
Query 4: SELECT * FROM users WHERE id = 2
Total: 4 queries
```

**Con DataLoader:**
```
Query 1: SELECT * FROM todos
Query 2: SELECT * FROM users WHERE id IN (1, 2)
Total: 2 queries
```

---

## **Paso 8: DataLoader con caché automático**

Los DataLoaders **cachean resultados** durante la misma request:

```graphql
query {
  todos {
    user { name }
  }
  users {
    name
  }
}
```

Aunque pides `users` dos veces (en `todos.user` y en `users`), el DataLoader **solo ejecuta una query** y reutiliza el resultado.

---

## **Paso 9: DataLoader avanzado - con base de datos real**

Si usas Entity Framework Core:

```csharp
using GreenDonut;
using Microsoft.EntityFrameworkCore;

namespace MiApiGraphQL;

public class UserDataLoader : BatchDataLoader<int, User>
{
    private readonly IDbContextFactory<AppDbContext> _dbContextFactory;

    public UserDataLoader(
        IBatchScheduler batchScheduler,
        IDbContextFactory<AppDbContext> dbContextFactory,
        DataLoaderOptions? options = null)
        : base(batchScheduler, options)
    {
        _dbContextFactory = dbContextFactory;
    }

    protected override async Task<IReadOnlyDictionary<int, User>> LoadBatchAsync(
        IReadOnlyList<int> keys,
        CancellationToken cancellationToken)
    {
        await using var dbContext = await _dbContextFactory.CreateDbContextAsync(cancellationToken);

        return await dbContext.Users
            .Where(u => keys.Contains(u.Id))
            .ToDictionaryAsync(u => u.Id, cancellationToken);
    }
}
```

---

## **Conceptos clave:**

✅ **BatchDataLoader** → Para relaciones uno a uno (todo → user)
✅ **GroupedDataLoader** → Para relaciones uno a muchos (user → todos)
✅ **Batching automático** → Agrupa múltiples peticiones en una sola query
✅ **Caché por request** → Evita queries duplicadas en la misma petición
✅ **Resuelve N+1** → Reduce drásticamente el número de queries a la BD

---

## **Comparación de rendimiento:**

| Escenario | Sin DataLoader | Con DataLoader |
|-----------|----------------|----------------|
| 10 todos | 11 queries | 2 queries |
| 100 todos | 101 queries | 2 queries |
| 1000 todos | 1001 queries | 2 queries |

---
Perfecto, vamos con **Subscriptions**.

## **Subscriptions en GraphQL con Hot Chocolate**

Las subscriptions permiten **comunicación en tiempo real** entre servidor y cliente mediante WebSockets. El cliente se "suscribe" a eventos y recibe actualizaciones automáticamente.

### **Casos de uso:**
- Notificaciones en tiempo real
- Chat en vivo
- Actualizaciones de estado
- Feeds de actividad

---

## **Paso 1: Instalar paquetes necesarios**

```bash
dotnet add package HotChocolate.Subscriptions
dotnet add package HotChocolate.AspNetCore
```

---

## **Paso 2: Configurar WebSockets en Program.cs**

Modifica `Program.cs`:

```csharp
var builder = WebApplication.CreateBuilder(args);

// Añadir servicios
builder.Services.AddScoped<UserDataLoader>();
builder.Services.AddScoped<TodoDataLoader>();

builder.Services
    .AddGraphQLServer()
    .AddQueryType<Query>()
    .AddMutationType<Mutation>()
    .AddSubscriptionType<Subscription>()      // ← Añade esto
    .AddTypeExtension<TodoExtensions>()
    .AddTypeExtension<UserExtensions>()
    .AddDirectiveType<AuthDirectiveType>()
    .RegisterService<UserDataLoader>()
    .RegisterService<TodoDataLoader>()
    .AddInMemorySubscriptions()               // ← Y esto (para desarrollo)
    .AddHttpRequestInterceptor(async (context, executor, builder, ct) =>
    {
        var isAuth = context.Request.Headers.ContainsKey("Authorization");
        builder.SetProperty("isAuthenticated", isAuth);

        if (isAuth)
        {
            var authHeader = context.Request.Headers["Authorization"].ToString();
            var role = authHeader.Contains("admin") ? "admin" : "user";
            builder.SetProperty("userRole", role);
        }

        await Task.CompletedTask;
    });

var app = builder.Build();

app.UseWebSockets();        // ← Habilitar WebSockets
app.MapGraphQL();

app.Run();
```

---

## **Paso 3: Crear la clase Subscription**

Crea `Subscription.cs`:

```csharp
using HotChocolate.Execution;
using HotChocolate.Subscriptions;

namespace MiApiGraphQL;

public class Subscription
{
    // Subscription básica: escuchar cuando se crea un Todo
    [Subscribe]
    public Todo OnTodoCreated([EventMessage] Todo todo) => todo;

    // Subscription: escuchar cuando se actualiza un Todo
    [Subscribe]
    public Todo OnTodoUpdated([EventMessage] Todo todo) => todo;

    // Subscription: escuchar cuando se elimina un Todo
    [Subscribe]
    public int OnTodoDeleted([EventMessage] int todoId) => todoId;

    // Subscription con filtro: solo todos de un usuario específico
    [Subscribe]
    public Todo OnTodoCreatedByUser(
        [EventMessage] Todo todo,
        int userId)
    {
        return todo;
    }
}
```

**Explicación:**
- `[Subscribe]` → Marca el método como subscription
- `[EventMessage]` → Recibe el payload del evento
- Los parámetros adicionales actúan como **filtros**

---

## **Paso 4: Emitir eventos desde Mutations**

Modifica `Mutation.cs` para emitir eventos:

```csharp
using HotChocolate.Subscriptions;

namespace MiApiGraphQL;

public class Mutation
{
    [Auth(Role = "admin")]
    [Validate(MinLength = 3, MaxLength = 100)]
    public async Task<Todo> AddTodo(
        string title,
        int userId,
        [Service] ITopicEventSender eventSender)
    {
        var newTodo = new Todo(Data.Todos.Count + 1, title, false, userId);
        Data.Todos.Add(newTodo);

        // Emitir evento general
        await eventSender.SendAsync(
            nameof(Subscription.OnTodoCreated),
            newTodo);

        // Emitir evento filtrado por usuario
        await eventSender.SendAsync(
            $"{nameof(Subscription.OnTodoCreatedByUser)}_{userId}",
            newTodo);

        return newTodo;
    }

    [Auth(Role = "admin")]
    public async Task<Todo?> UpdateTodo(
        int id,
        string? title,
        bool? completed,
        [Service] ITopicEventSender eventSender)
    {
        var todo = Data.Todos.FirstOrDefault(t => t.Id == id);
        if (todo == null) return null;

        var updatedTodo = todo with
        {
            Title = title ?? todo.Title,
            Completed = completed ?? todo.Completed
        };

        Data.Todos[Data.Todos.FindIndex(t => t.Id == id)] = updatedTodo;

        // Emitir evento de actualización
        await eventSender.SendAsync(
            nameof(Subscription.OnTodoUpdated),
            updatedTodo);

        return updatedTodo;
    }

    [Auth]
    public async Task<bool> DeleteTodo(
        int id,
        [Service] ITopicEventSender eventSender)
    {
        var todo = Data.Todos.FirstOrDefault(t => t.Id == id);
        if (todo == null) return false;

        Data.Todos.Remove(todo);

        // Emitir evento de eliminación
        await eventSender.SendAsync(
            nameof(Subscription.OnTodoDeleted),
            id);

        return true;
    }
}
```

**Explicación:**
- `[Service] ITopicEventSender` → Inyecta el servicio para emitir eventos
- `SendAsync(topic, payload)` → Envía el evento a todos los suscriptores

---

## **Paso 5: Actualizar Subscription con filtros avanzados**

Modifica `Subscription.cs` para soportar filtros:

```csharp
using HotChocolate.Execution;
using HotChocolate.Subscriptions;

namespace MiApiGraphQL;

public class Subscription
{
    [Subscribe]
    [Topic]
    public Todo OnTodoCreated([EventMessage] Todo todo) => todo;

    [Subscribe]
    [Topic]
    public Todo OnTodoUpdated([EventMessage] Todo todo) => todo;

    [Subscribe]
    [Topic]
    public int OnTodoDeleted([EventMessage] int todoId) => todoId;

    // Subscription con filtro por usuario
    [Subscribe(With = nameof(SubscribeToTodosByUser))]
    public Todo OnTodoCreatedByUser([EventMessage] Todo todo) => todo;

    public ValueTask<ISourceStream<Todo>> SubscribeToTodosByUser(
        int userId,
        [Service] ITopicEventReceiver eventReceiver)
    {
        return eventReceiver.SubscribeAsync<Todo>(
            $"{nameof(OnTodoCreatedByUser)}_{userId}");
    }
}
```

---

## **Paso 6: Probar Subscriptions en GraphiQL**

### **6.1 Abrir dos pestañas de GraphiQL**

Ve a `https://localhost:5001/graphql`

### **6.2 En la Pestaña 1: Suscribirse a eventos**

```graphql
subscription {
  onTodoCreated {
    id
    title
    completed
    userId
  }
}
```

Haz clic en **Execute** (el botón de play). Verás que la query queda "esperando".

### **6.3 En la Pestaña 2: Crear un Todo**

Añade el header de autenticación:
```
Authorization: Bearer admin-token
```

Ejecuta esta mutation:

```graphql
mutation {
  addTodo(title: "Test en tiempo real", userId: 1) {
    id
    title
  }
}
```

### **6.4 Observar la Pestaña 1**

¡Deberías ver aparecer el nuevo todo automáticamente! 🎉

---

## **Paso 7: Subscription con filtro por usuario**

### **Pestaña 1: Suscribirse solo a todos del usuario 1**

```graphql
subscription {
  onTodoCreatedByUser(userId: 1) {
    id
    title
    userId
  }
}
```

### **Pestaña 2: Crear todos de diferentes usuarios**

```graphql
# Este SÍ aparecerá en la subscription
mutation {
  addTodo(title: "Para usuario 1", userId: 1) {
    id
  }
}

# Este NO aparecerá en la subscription
mutation {
  addTodo(title: "Para usuario 2", userId: 2) {
    id
  }
}
```

---

## **Paso 8: Subscription para actualizaciones**

### **Pestaña 1: Suscribirse a actualizaciones**

```graphql
subscription {
  onTodoUpdated {
    id
    title
    completed
  }
}
```

### **Pestaña 2: Actualizar un todo**

```graphql
mutation {
  updateTodo(id: 1, completed: true) {
    id
    title
    completed
  }
}
```

Verás la actualización en tiempo real en la Pestaña 1.

---

## **Paso 9: Subscription para eliminaciones**

### **Pestaña 1: Suscribirse a eliminaciones**

```graphql
subscription {
  onTodoDeleted
}
```

### **Pestaña 2: Eliminar un todo**

```graphql
mutation {
  deleteTodo(id: 1)
}
```

Recibirás el ID del todo eliminado.

---

## **Paso 10: Subscription avanzada - con transformación**

Puedes transformar los datos antes de enviarlos:

```csharp
public class Subscription
{
    [Subscribe]
    [Topic]
    public TodoNotification OnTodoCreated([EventMessage] Todo todo)
    {
        return new TodoNotification(
            $"Nuevo todo creado: {todo.Title}",
            todo,
            DateTime.UtcNow
        );
    }
}

public record TodoNotification(
    string Message,
    Todo Todo,
    DateTime Timestamp
);
```

Query:
```graphql
subscription {
  onTodoCreated {
    message
    timestamp
    todo {
      id
      title
    }
  }
}
```

---

## **Paso 11: Cliente JavaScript (ejemplo con Apollo Client)**

Para consumir desde una aplicación web:

```javascript
import { ApolloClient, InMemoryCache, split, HttpLink } from '@apollo/client';
import { GraphQLWsLink } from '@apollo/client/link/subscriptions';
import { createClient } from 'graphql-ws';
import { getMainDefinition } from '@apollo/client/utilities';

const httpLink = new HttpLink({
  uri: 'https://localhost:5001/graphql'
});

const wsLink = new GraphQLWsLink(createClient({
  url: 'wss://localhost:5001/graphql',
}));

const splitLink = split(
  ({ query }) => {
    const definition = getMainDefinition(query);
    return (
      definition.kind === 'OperationDefinition' &&
      definition.operation === 'subscription'
    );
  },
  wsLink,
  httpLink,
);

const client = new ApolloClient({
  link: splitLink,
  cache: new InMemoryCache()
});

// Suscribirse
client.subscribe({
  query: gql`
    subscription {
      onTodoCreated {
        id
        title
      }
    }
  `
}).subscribe({
  next: (data) => console.log('Nuevo todo:', data),
  error: (error) => console.error('Error:', error)
});
```

---

## **Paso 12: Producción - Redis Pub/Sub**

Para producción con múltiples servidores, usa Redis:

```bash
dotnet add package HotChocolate.Subscriptions.Redis
```

En `Program.cs`:

```csharp
builder.Services
    .AddGraphQLServer()
    // ... otras configuraciones
    .AddRedisSubscriptions((sp) =>
        ConnectionMultiplexer.Connect("localhost:6379"));
```

---

## **Conceptos clave:**

✅ **WebSockets** → Comunicación bidireccional en tiempo real
✅ **[Subscribe]** → Marca métodos como subscriptions
✅ **ITopicEventSender** → Emite eventos desde mutations
✅ **Filtros** → Suscribirse solo a eventos específicos
✅ **InMemory vs Redis** → InMemory para desarrollo, Redis para producción

---

## **Comparación: Polling vs Subscriptions**

| Aspecto | Polling (REST) | Subscriptions (GraphQL) |
|---------|----------------|-------------------------|
| Latencia | Alta (cada X segundos) | Baja (instantáneo) |
| Tráfico | Alto (requests constantes) | Bajo (solo cuando hay cambios) |
| Complejidad | Simple | Moderada |
| Escalabilidad | Limitada | Alta (con Redis) |

---

## **Resumen completo del curso:**

1. ✅ **Mutations** - Modificar datos (CRUD)
2. ✅ **Resolvers** - Obtener datos relacionados
3. ✅ **Directives** - Lógica transversal (auth, validación)
4. ✅ **DataLoaders** - Optimización (resolver N+1)
5. ✅ **Subscriptions** - Tiempo real (WebSockets)

---

¿Quieres que exploremos algo más como:
- **Paginación avanzada** (cursor-based pagination)?
- **File uploads** (subir archivos)?
- **Integración con Entity Framework Core**?
- **Testing de GraphQL** con xUnit?
- **Deployment** en producción?
