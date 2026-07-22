# API REST Recetas (Laravel 12 + Sail + PostgreSQL + Redis + Sanctum)

## 0. Objetivo y alcance
Construir una API REST para gestionar **Recetas**, con:

* **Docker/Sail** (entorno reproducible)
* **PostgreSQL** como BD principal
* **Redis** preparado (cache/colas; uso opcional)
* **Autenticación** con **Laravel Sanctum** (tokens)
* **Autorización** con **Policies** (`$this->authorize()` como en Laravel 10, común en empresa)
* **Regla de negocio** en un **Service** (`RecetaService`)
* **Errores de dominio** (`DomainException`) mapeados a **HTTP 409** con **código de error** (`RECETA_PUBLICADA`) usando la forma Laravel 11/12 en `bootstrap/app.php`
* **API Resources** (`RecetaResource`) para contrato consistente (incluye `meta/links` al paginar)
* **Tests** con **PHPUnit** (Feature tests de CRUD y autorización)

> Nota: En Laravel 11/12 cambian algunos puntos de configuración (Kernel/Handler). Aquí seguimos el enfoque actual del framework.
---

## 1. Crear el proyecto con Laravel Build + Sail

### 1.1 Preparación según el sistema operativo

| Sistema | Requisito |
|---|---|
| Linux | Docker Engine y Docker Compose. Configura el grupo `docker` para no usar `sudo` con Sail. |
| macOS | Docker Desktop en ejecución. |
| Windows | Docker Desktop con integración WSL2 y una distribución Linux. Trabaja desde esa terminal y guarda el proyecto en su sistema de archivos. |

No ejecutes Sail desde CMD, PowerShell ni Git Bash. Sail está soportado en Windows mediante WSL2; usar la terminal Linux evita problemas de permisos, montajes de volúmenes y rendimiento.

---

### 1.2 Crear el proyecto (Sail + pgsql + redis)

Ejecuta el siguiente comando en una terminal Linux. En Windows, usa la terminal de tu distribución WSL2:

```bash
curl -s "https://laravel.build/recetas-api?with=pgsql,redis" | bash
```

Si Docker creó archivos propiedad de `root` en Linux, corrige la propiedad antes de abrir el proyecto en el IDE:

```bash
sudo chown -R "$USER":"$USER" recetas-api
```

Entra en la carpeta:
```bash
cd recetas-api
```

### 1.3 Arrancar contenedores

Inicia el entorno en segundo plano:

```bash
./vendor/bin/sail up -d
```

Comprueba que Sail funciona correctamente:

```bash
./vendor/bin/sail php -v
./vendor/bin/sail artisan -V
```

---

## 2. Configurar `.env` (PostgreSQL y sesión)

### 2.1 Variables de base de datos

En `.env` (o `.env.example` y copiar a `.env`):

```env
DB_CONNECTION=pgsql
DB_HOST=pgsql
DB_PORT=5432
DB_DATABASE=laravel
DB_USERNAME=sail
DB_PASSWORD=password
```

### 2.2 Evitar error `sessions` (si aparece)

Si al abrir la app aparece algo como `relation "sessions" does not exist`, es porque el driver de sesión está en `database`.

Soluciones típicas:

**Opción A (simple para API):** usar `file`:

```env
SESSION_DRIVER=file
```

**Opción B:** migrar tabla de sesiones:

```bash
./vendor/bin/sail artisan session:table
./vendor/bin/sail artisan migrate
```

---

## 3. Activar soporte de rutas API (Laravel 11/12)

En Laravel 11/12 puede que no exista `routes/api.php` por defecto.

### 3.1 Crear archivo `routes/api.php`

Crea `routes/api.php` (si no existe).

Contenido mínimo:

```php
<?php

use Illuminate\Support\Facades\Route;

Route::get('/ping', fn () => response()->json(['pong' => true]));
```

### 3.2 Asegurar que `bootstrap/app.php` carga rutas API

En `bootstrap/app.php`, dentro de `->withRouting(...)`, asegúrate de que aparece `api:`:

```php
->withRouting(
    web: __DIR__.'/../routes/web.php',
    api: __DIR__.'/../routes/api.php',
    commands: __DIR__.'/../routes/console.php',
    health: '/up',
)
```

---

## 4. Instalar y configurar Sanctum (tokens)

### 4.1 Instalar Sanctum (si no estuviera)

```bash
./vendor/bin/sail composer require laravel/sanctum
./vendor/bin/sail artisan sanctum:install
./vendor/bin/sail artisan migrate
```

> En algunos proyectos Laravel 12 puede venir ya como dependencia o estar “casi listo”, pero lo instalamos explícitamente para evitar dudas.

### 4.2 Middleware de API / Sanctum

En Laravel 11/12 se configura middleware en `bootstrap/app.php`. Para una API con tokens tipo Bearer, normalmente basta con proteger rutas con `auth:sanctum`.

---

## 5. AuthController (login / me / logout)

### 5.1 Crear controlador

```bash
./vendor/bin/sail artisan make:controller Api/AuthController
```

Crea `app/Http/Controllers/Api/AuthController.php`:





```php
<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Hash;
use App\Models\User;

class AuthController extends Controller
{
    public function login(Request $request)
    {
        $data = $request->validate([
            'email' => ['required','email'],
            'password' => ['required','string'],
        ]);

        $user = User::where('email', $data['email'])->first();

        if (! $user || ! Hash::check($data['password'], $user->password)) {
            return response()->json([
                'error' => [
                    'code' => 'CREDENCIALES_INVALIDAS',
                    'message' => 'Credenciales inválidas',
                ],
            ], 401);
        }

        $token = $user->createToken('api-token')->plainTextToken;

        return response()->json([
            'token' => $token,
        ]);
    }

    public function me(Request $request)
    {
        return response()->json($request->user());
    }

    public function logout(Request $request)
    {
        // revoca el token actual
        $request->user()->currentAccessToken()?->delete();

        return response()->json(['message' => 'Logout correcto']);
    }
}
```

### 5.2 Rutas de auth

En `routes/api.php`:

```php
use Illuminate\Support\Facades\Route;
use App\Http\Controllers\Api\AuthController;

Route::post('/auth/login', [AuthController::class, 'login']);

Route::middleware('auth:sanctum')->group(function () {
    Route::get('/auth/me', [AuthController::class, 'me']);
    Route::post('/auth/logout', [AuthController::class, 'logout']);
});
```

---

## 6. Modelo Receta + migración + factory

### 6.1 Crear modelo, migración y factory

```bash
./vendor/bin/sail artisan make:model Receta -m -f
```

### 6.2 Migración de recetas

Edita la migración creada en `database/migrations/...create_recetas_table.php`:

```php
Schema::create('recetas', function (Blueprint $table) {
    $table->id();
    $table->foreignId('user_id')->constrained()->cascadeOnDelete();
    $table->string('titulo', 200);
    $table->text('descripcion');
    $table->text('instrucciones');
    $table->boolean('publicada')->default(false);
    $table->timestamps();
});
```

Ejecutar migraciones:

```bash
./vendor/bin/sail artisan migrate
```

### 6.3 Modelo `Receta`

En `app/Models/Receta.php`:

```php
protected $fillable = [
    'user_id', 'titulo', 'descripcion', 'instrucciones', 'publicada'
];
```

(Relación opcional)

```php
public function user()
{
    return $this->belongsTo(User::class);
}
```

### 6.4 Factory `RecetaFactory`

En `database/factories/RecetaFactory.php`:

```php
public function definition(): array
{
    return [
        'user_id' => \App\Models\User::factory(),
        'titulo' => $this->faker->sentence(3),
        'descripcion' => $this->faker->paragraph(),
        'instrucciones' => $this->faker->paragraph(),
        'publicada' => false,
    ];
}
```

---

## 7. API Resource para Receta

### 7.1 Crear Resource

```bash
./vendor/bin/sail artisan make:resource RecetaResource
```

En `app/Http/Resources/RecetaResource.php`:

```php
<?php

namespace App\Http\Resources;

use Illuminate\Http\Request;
use Illuminate\Http\Resources\Json\JsonResource;

class RecetaResource extends JsonResource
{
    public function toArray(Request $request): array
    {
        return [
            'id' => $this->id,
            'titulo' => $this->titulo,
            'descripcion' => $this->descripcion,
            'instrucciones' => $this->instrucciones,
            'publicada' => $this->publicada,
            'user_id' => $this->user_id,
            'created_at' => $this->created_at,
            'updated_at' => $this->updated_at,
        ];
    }
}
```

---

## 8. Policy de Receta (autorización)

### 8.1 Crear Policy

```bash
./vendor/bin/sail artisan make:policy RecetaPolicy --model=Receta
```

En `app/Policies/RecetaPolicy.php` (métodos relevantes):

```php
public function update(User $user, Receta $receta): bool
{
    return $user->id === $receta->user_id;
}

public function delete(User $user, Receta $receta): bool
{
    return $user->id === $receta->user_id;
}
```

### 8.2 Sobre `view`, `viewAny`, `create`

Si devuelven `false`, **solo afecta si llamas** a `authorize('view'...)`, `authorize('create'...)` o usas middleware `can:`. No bloquea nada “por sí solo”.

---

## 9. Service de negocio (regla “no modificar si publicada”)

### 9.1 Crear servicio

Crea `app/Services/RecetaService.php` (carpeta `Services` si no existe):

```php
<?php

namespace App\Services;

use App\Models\Receta;
use DomainException;

class RecetaService
{
    public function assertCanBeModified(Receta $receta): void
    {grep -n "~~~" 003-Laravel12-API_REST-Recetas.md
        if ($receta->publicada) {
            throw new DomainException('No se puede modificar una receta ya publicada');
        }
    }
}
```

---

## 10. Excepciones en Laravel 11/12 (bootstrap/app.php)

Objetivo: convertir `DomainException` a **409** y devolver **código de error**.

En `bootstrap/app.php`, dentro de `->withExceptions(...)`, añade handler **solo para DomainException** (muy importante: no capturar `Throwable` global o romperás 401/403/404).

Ejemplo:





```php
use DomainException;
use Symfony\Component\HttpFoundation\Response;
use Illuminate\Foundation\Configuration\Exceptions;

->withExceptions(function (Exceptions $exceptions) {
    $exceptions->render(function (DomainException $e) {
        $code = match ($e->getMessage()) {
            'No se puede modificar una receta ya publicada' => 'RECETA_PUBLICADA',
            default => 'ERROR_DOMINIO',
        };

        return response()->json([
            'error' => [
                'code' => $code,
                'message' => $e->getMessage(),
            ],
        ], Response::HTTP_CONFLICT);
    });
})
```

---

## 11. RecetaController (CRUD + filtros de index)

### 11.1 Crear controlador

```bash
./vendor/bin/sail artisan make:controller Api/RecetaController --api --model=Receta
```

### 11.2 Rutas CRUD

En `routes/api.php`:

```php
use App\Http\Controllers\Api\RecetaController;

Route::middleware('auth:sanctum')->group(function () {
    Route::apiResource('recetas', RecetaController::class);
});
```

### 11.3 Implementación del controlador (resumen)

En `app/Http/Controllers/Api/RecetaController.php`:

* `index()` con paginación configurable, ordenación y búsqueda (`q`, `sort`, `per_page`)
* `store()` crea receta asociada al usuario
* `show()` devuelve resource
* `update()`:

  1. `$this->authorize('update', $receta)` (403)
  2. `$recetaService->assertCanBeModified($receta)` (409 + code)
* `destroy()`:

  * solo `$this->authorize('delete', $receta)` y borrar

Ejemplo de `index()`:





```php
public function index(Request $request)
{
    $query = Receta::query();

    if ($search = $request->query('q')) {
        $query->where(function ($q) use ($search) {
            $q->where('titulo', 'ILIKE', "%{$search}%")
              ->orWhere('descripcion', 'ILIKE', "%{$search}%");
        });
    }

    $allowedSorts = ['titulo', 'created_at'];
    if ($sort = $request->query('sort')) {
        $direction = str_starts_with($sort, '-') ? 'desc' : 'asc';
        $field = ltrim($sort, '-');

        if (in_array($field, $allowedSorts, true)) {
            $query->orderBy($field, $direction);
        }
    }

    $perPage = min((int) $request->query('per_page', 10), 50);

    return RecetaResource::collection($query->paginate($perPage));
}
```

Ejemplo de `update()`:





```php
public function update(Request $request, Receta $receta, \App\Services\RecetaService $recetaService)
{
    $this->authorize('update', $receta);

    $recetaService->assertCanBeModified($receta);

    $data = $request->validate([
        'titulo' => 'sometimes|required|string|max:200',
        'descripcion' => 'sometimes|required|string',
        'instrucciones' => 'sometimes|required|string',
    ]);

    $receta->update($data);

    return response()->json(new RecetaResource($receta));
}
```



Ejemplo de `destroy()`:

```php
public function destroy(Receta $receta)
{
    $this->authorize('delete', $receta);

    $receta->delete();

    return response()->json(['message' => 'Receta eliminada']);
}
```

---

## 12. Seed de usuario para pruebas manuales

Crear seeder:

```bash
./vendor/bin/sail artisan make:seeder UserSeeder
```

En `database/seeders/UserSeeder.php`:

```php
public function run(): void
{
    \App\Models\User::updateOrCreate(
        ['email' => 'admin@demo.local'],
        ['name' => 'Admin Demo', 'password' => bcrypt('password')]
    );
}
```

En `DatabaseSeeder.php`:

```php
$this->call(UserSeeder::class);
```

Ejecutar:

```bash
./vendor/bin/sail artisan db:seed
```

---

## 13. Tests con PHPUnit

### 13.1 Crear tests

```bash
./vendor/bin/sail artisan make:test AuthTest
./vendor/bin/sail artisan make:test RecetaCrudTest
```

### 13.2 Pautas clave en tests

* Obtener token con Sanctum:

```php
$token = $user->createToken('api-token')->plainTextToken;
```

* Pasarlo como Bearer:

```php
$this->withHeader('Authorization', 'Bearer '.$token)
```

* Para códigos de error, testear con `assertJsonPath`:

```php
->assertJsonPath('error.code', 'RECETA_PUBLICADA')
```

### 13.3 Ejecutar tests

```bash
./vendor/bin/sail artisan test
./vendor/bin/sail artisan test --filter RecetaCrudTest
```

---

## 14. Pruebas manuales rápidas (curl)

Login:

```bash
curl -s -X POST http://localhost/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@demo.local","password":"password"}'
```

Crear receta:

```bash
curl -s -X POST http://localhost/api/recetas \
  -H "Authorization: Bearer TU_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"titulo":"Tortilla","descripcion":"Clasica","instrucciones":"..."}'
```

Listar (paginación/orden/búsqueda):

```bash
curl -s "http://localhost/api/recetas?per_page=5&sort=titulo&q=tort" \
  -H "Authorization: Bearer TU_TOKEN"
```

---

## 15. Errores comunes (muy importantes)

1. **Capturar `Throwable` en `withExceptions`**
   Rompe 401/403/404 y convierte todo en 409.
   Solución: capturar solo excepciones concretas (p.ej. `DomainException`).

2. Dejar `abort(418)` / `dd()` / `dump()` en controladores
   Rompe tests y contrato HTTP.

3. No usar `Resource::collection(paginate())`
   Puede faltar `meta/links` y romper tests de paginación.

---

# 16. Qué queda para la tarea del alumnado (C)

Sobre esta base, el alumnado deberá extender el proyecto con:

* Entidades relacionadas (p.ej. **Comentarios**, **Categorías**, etc.)
* Nuevas rutas y controladores
* Nuevas policies/reglas de negocio/tests
* Reutilizando el patrón:

  * Resource + Policy + Service + tests + manejo de excepciones

---

## 17. Unit Test vs Feature Test en Laravel

En Laravel (y en frameworks backend en general) existen **dos niveles principales de pruebas automáticas**: **Unit Tests** y **Feature Tests**. Aunque ambos usan PHPUnit, **su objetivo es distinto**.

---

### 17.1 Unit Tests (pruebas unitarias)

**Qué prueban**

* Una **unidad aislada de código**
* Normalmente:

  * una clase
  * un método
  * una regla de negocio

**Características**

* No arrancan el framework completo
* No usan base de datos real (o usan mocks)
* Son rápidas
* Detectan errores muy concretos

**Ejemplos típicos**

* Un método de un Service
* Una función de cálculo
* Una validación compleja

**Ejemplo conceptual**

```php
public function test_cannot_modify_published_receta()
{
    $receta = new Receta(['publicada' => true]);

    $service = new RecetaService();

    $this->expectException(DomainException::class);

    $service->assertCanBeModified($receta);
}
```

**Cuándo usar Unit Tests**

* Reglas de negocio puras
* Código que no depende de HTTP, rutas o middleware
* Lógica reutilizable

---

### 17.2 Feature Tests (pruebas funcionales)

**Qué prueban**

* El **comportamiento completo del sistema**
* Simulan peticiones HTTP reales

**Incluyen**

* Rutas
* Middleware
* Autenticación
* Autorización
* Controladores
* Resources
* Base de datos (normalmente SQLite en memoria o BD real de test)

**Ejemplos en este proyecto**

* `AuthTest`
* `RecetaCrudTest`
* `RecetaAuthorizationTest`

**Ejemplo real**

```php
$this->withHeader('Authorization', 'Bearer '.$token)
     ->putJson('/api/recetas/1', [...])
     ->assertStatus(409)
     ->assertJsonPath('error.code', 'RECETA_PUBLICADA');
```

Aquí se comprueba:

* autenticación
* autorización
* regla de negocio
* formato de respuesta
* código HTTP

---

### 17.3 Diferencias clave (tabla resumen)

| Aspecto    | Unit Test      | Feature Test        |
| ---------- | -------------- | ------------------- |
| Nivel      | Bajo           | Alto                |
| HTTP       | No             | Sí                  |
| BD         | No (o mock)    | Sí                  |
| Velocidad  | Muy rápida     | Más lenta           |
| Qué valida | Lógica aislada | Comportamiento real |
| Uso típico | Services       | APIs REST           |

---

### 17.4 ¿Por qué en este proyecto usamos Feature Tests?

Porque el objetivo es:

* Aprender **cómo se comporta una API real**
* Validar:

  * códigos HTTP
  * autorización
  * errores de dominio
  * contrato JSON

En empresa:

* **Unit Tests** validan reglas internas
* **Feature Tests** validan que *“el sistema funciona”*

En este proyecto:

> **Primero Feature Tests (visión global)**
> Luego, si el proyecto crece, se añaden Unit Tests.

---

## 18. Autorización por roles (roles y permisos)

Hasta ahora la autorización se ha basado en:

* **Propiedad del recurso**
* Policies (`update`, `delete`)
* `$this->authorize()`

Esto es correcto, pero **no suficiente** en aplicaciones reales.

---

### 18.1 Problema a resolver

Escenario típico:

* Usuario normal → crea y edita **sus** recetas
* Moderador → puede editar/borrar recetas de otros
* Administrador → acceso total

Con policies basadas solo en `user_id`, **esto no es posible**.

---

### 18.2 Solución habitual en Laravel

La solución más extendida en proyectos reales es:

**📦 `spatie/laravel-permission`**

Ventajas:

* Muy usada en empresa
* Roles y permisos desacoplados
* Integración directa con:

  * Policies
  * Middleware
  * Tests

---

### 18.3 Qué aporta el sistema de roles

Permite responder preguntas como:

* ¿Este usuario es `admin`?
* ¿Tiene permiso `recetas.delete.any`?
* ¿Puede saltarse la policy de propiedad?

---

### 18.4 Enfoque que vamos a usar

En este proyecto usaremos:

* **Roles**: `admin`, `user`
* **Permisos** (más adelante, opcional):

  * `recetas.manage.all`
  * `recetas.publish`

Y combinaremos:

* **Policies** (reglas finas)
* **Roles** (reglas globales)

Ejemplo conceptual en una Policy:

```php
public function delete(User $user, Receta $receta): bool
{
    if ($user->hasRole('admin')) {
        return true;
    }

    return $user->id === $receta->user_id;
}
```

---

### 18.5 Cuándo usar roles y cuándo policies

| Caso               | Solución         |
| ------------------ | ---------------- |
| Acceso global      | Roles            |
| Reglas por recurso | Policies         |
| Ambas              | Roles + Policies |

👉 **No son excluyentes**, se complementan.

---

### 18.6 Importante

Este punto **no se implementa automáticamente**:

* Hay que:

  * instalar el paquete
  * migrar tablas
  * asignar roles
  * adaptar policies
  * ajustar tests

Por eso **lo añadimos ahora**, cuando la API ya funciona y está testeada.

---

## 18.7 Siguiente paso (inmediato)

Tal como indicaste:

> *“lo añadimos justo cuando termines ese punto 18”*

👉 **El siguiente paso será implementar roles y permisos**:

1. Instalar `spatie/laravel-permission`
2. Crear roles (`admin`, `user`)
3. Asignarlos en seeders
4. Adaptar `RecetaPolicy`
5. Añadir tests específicos de rol
Perfecto. Implementamos **el Punto 18 (roles y permisos)** **paso a paso**, **sin romper nada de lo ya verde**, y manteniendo el enfoque **empresa + docente**.

> Objetivo final del punto 18
>
> * Rol `admin` puede **editar y borrar cualquier receta**
> * Rol `user` solo puede **editar/borrar sus recetas**
> * Se mantiene el uso de **Policies** (`$this->authorize()`)
> * Se añaden **tests** que prueban el comportamiento por rol

---

# 18. Implementación de autorización por roles (Spatie)

---

## 18.1 Instalar `spatie/laravel-permission`

Desde Sail:


```bash
./vendor/bin/sail composer require spatie/laravel-permission
```


Publicar configuración y migraciones:


```bash
./vendor/bin/sail artisan vendor:publish \
  --provider="Spatie\Permission\PermissionServiceProvider"
```


Ejecutar migraciones:


```bash
./vendor/bin/sail artisan migrate
```


Esto crea, entre otras, las tablas:

* `roles`
* `permissions`
* `model_has_roles`
* `model_has_permissions`

---

## 18.2 Configurar el modelo `User`

En `app/Models/User.php`, añade el trait:

```php
use Spatie\Permission\Traits\HasRoles;

class User extends Authenticatable
{
    use HasRoles;

    // ...
}
```

Con esto ya puedes usar:

* `$user->assignRole('admin')`
* `$user->hasRole('admin')`

---

## 18.3 Crear roles básicos (`admin`, `user`)

### 18.3.1 Seeder de roles

Crear seeder:

```bash
./vendor/bin/sail artisan make:seeder RoleSeeder
```

En `database/seeders/RoleSeeder.php`:

```php
<?php

namespace Database\Seeders;

use Illuminate\Database\Seeder;
use Spatie\Permission\Models\Role;

class RoleSeeder extends Seeder
{
    public function run(): void
    {
        Role::firstOrCreate(['name' => 'admin']);
        Role::firstOrCreate(['name' => 'user']);
    }
}
```

### 18.3.2 Registrar el seeder

En `database/seeders/DatabaseSeeder.php`:

```php
public function run(): void
{
    $this->call([
        RoleSeeder::class,
        UserSeeder::class,
    ]);
}
```

Ejecutar:

```bash
./vendor/bin/sail artisan db:seed
```

---

## 18.4 Asignar roles a usuarios

### 18.4.1 Ajustar `UserSeeder`

En `database/seeders/UserSeeder.php`:

```php
public function run(): void
{
    $admin = \App\Models\User::updateOrCreate(
        ['email' => 'admin@demo.local'],
        ['name' => 'Admin', 'password' => bcrypt('password')]
    );

    $admin->assignRole('admin');

    $user = \App\Models\User::updateOrCreate(
        ['email' => 'user@demo.local'],
        ['name' => 'User', 'password' => bcrypt('password')]
    );

    $user->assignRole('user');
}
```

Re-ejecutar seeders:

```bash
./vendor/bin/sail artisan db:seed
```

---

## 18.5 Adaptar `RecetaPolicy` para roles

Aquí está **el núcleo del punto 18**.

### Antes (solo propiedad)

```php
public function delete(User $user, Receta $receta): bool
{
    return $user->id === $receta->user_id;
}
```

### Después (roles + propiedad)




```php
public function update(User $user, Receta $receta): bool
{
    if ($user->hasRole('admin')) {
        return true;
    }

    return $user->id === $receta->user_id;
}

public function delete(User $user, Receta $receta): bool
{
    if ($user->hasRole('admin')) {
        return true;
    }

    return $user->id === $receta->user_id;
}
```

📌 **Muy importante (concepto clave para alumnos)**

* La **Policy sigue siendo la fuente de verdad**
* El rol **no sustituye** a la policy, la complementa

---

## 18.6 No tocar los controladores

Esto es intencionado.

En `RecetaController` seguimos usando:

```php
$this->authorize('update', $receta);
$this->authorize('delete', $receta);
```

👉 El cambio de comportamiento ocurre **solo** en la Policy.

Esto es exactamente lo que se busca en buen diseño.

---

## 18.7 Tests de autorización por rol

### 18.7.1 Test: admin puede borrar receta ajena

En `RecetaAuthorizationTest.php`:

```php
public function test_admin_can_delete_any_receta(): void
{
    $admin = User::factory()->create();
    $admin->assignRole('admin');

    $owner = User::factory()->create();
    $owner->assignRole('user');

    $receta = Receta::factory()->create([
        'user_id' => $owner->id,
    ]);

    $token = $admin->createToken('api-token')->plainTextToken;

    $this->withHeader('Authorization', 'Bearer ' . $token)
        ->deleteJson("/api/recetas/{$receta->id}")
        ->assertStatus(200);
}
```

---

### 18.7.2 Test: user normal NO puede borrar receta ajena

Este test **ya existe** y **debe seguir pasando**:

```php
->assertStatus(403);
```

Eso confirma que:

* roles no rompen reglas existentes
* admin es una excepción explícita

---

## 18.8 Ejecutar tests

```bash
./vendor/bin/sail artisan test
```

Resultado esperado:

* ✅ Todos en verde
* CRUD intacto
* Autorización extendida correctamente

---

## 18.9 Qué hemos conseguido con el punto 18

✔ Autorización realista (empresa)
✔ Policies como núcleo
✔ Roles como capa global
✔ Sin tocar controladores
✔ Tests claros y expresivos

Esto deja el proyecto **en estado profesional**.
Perfecto. Te propongo añadirlo como un **apéndice**, no como punto numerado del flujo principal, porque:

* no es obligatorio para que la API funcione
* es una **decisión de diseño avanzada**
* encaja muy bien como **mejora/refactor opcional** (ideal para alumnado)

Perfecto 👍
Te dejo **el texto listo para añadir al final del Punto 18**, integrado y redactado como **continuación natural**, para que lo pegues directamente en el documento donde corresponda.

---

### 18.10 Roles, guards y tests: consideraciones importantes en Laravel 11/12

Al implementar roles y permisos con `spatie/laravel-permission` en una API basada en **Laravel 11/12 + Sanctum**, es habitual encontrarse con errores relacionados con el **guard** y con la **ejecución de tests**. A continuación se recogen los puntos clave que es importante entender.

---

#### 18.10.1 El concepto de *guard* en Laravel

Un *guard* define **cómo se autentica un usuario**. En este proyecto:

* No se usan sesiones web
* Se usan **tokens de API** con Sanctum

Por tanto, el guard correcto es **`sanctum`**, no `web` ni `api`.

En Laravel 11/12 se recomienda **no hardcodear** el guard por defecto en `config/auth.php`, sino definirlo mediante variables de entorno.

En el archivo `.env`:

```env
AUTH_GUARD=sanctum
```

Y en `config/auth.php` (valor por defecto):

```php
'defaults' => [
    'guard' => env('AUTH_GUARD', 'web'),
    'passwords' => env('AUTH_PASSWORD_BROKER', 'users'),
],
```

Además, debe existir el guard `sanctum`:

```php
'guards' => [
    'web' => [
        'driver' => 'session',
        'provider' => 'users',
    ],
    'sanctum' => [
        'driver' => 'sanctum',
        'provider' => 'users',
    ],
],
```

---

#### 18.10.2 Roles y guard en Spatie

Cada rol y permiso en Spatie **pertenece a un guard**.
En una API con Sanctum:

* Los roles **deben crearse con `guard_name = sanctum`**
* Spatie utilizará el guard por defecto de Laravel (`auth.defaults.guard`)

Ejemplo correcto de creación de roles:

```php
Role::firstOrCreate([
    'name' => 'admin',
    'guard_name' => 'sanctum',
]);

Role::firstOrCreate([
    'name' => 'user',
    'guard_name' => 'sanctum',
]);
```

Si el rol no existe para ese guard, Spatie lanzará:

```
RoleDoesNotExist: There is no role named `admin` for guard `sanctum`
```

---

#### 18.10.3 Por qué fallan los tests aunque funcione en local

En **Feature Tests**:

* Laravel utiliza una **base de datos aislada**
* **No se ejecutan seeders automáticamente**
* El estado de la aplicación **no se comparte** con el entorno local

Por tanto, aunque los roles existan en la base de datos de desarrollo, **en los tests no existen** a menos que se creen explícitamente.

---

#### 18.10.4 Regla fundamental para tests con roles

> **Todo lo que un test necesita debe crearlo el propio test.**

Esto incluye:

* Usuarios
* Roles
* Permisos
* Datos de dominio

Los tests **no deben depender de seeders globales**.

---

#### 18.10.5 Creación de roles dentro de los tests (forma recomendada)

Ejemplo en un test de autorización:

```php
use Illuminate\Foundation\Testing\RefreshDatabase;
use Spatie\Permission\Models\Role;

class RecetaAuthorizationTest extends TestCase
{
    use RefreshDatabase;

    public function test_admin_can_delete_any_receta(): void
    {
        Role::create([
            'name' => 'admin',
            'guard_name' => 'sanctum',
        ]);

        Role::create([
            'name' => 'user',
            'guard_name' => 'sanctum',
        ]);

        $admin = User::factory()->create();
        $admin->assignRole('admin');

        $owner = User::factory()->create();
        $owner->assignRole('user');

        $receta = Receta::factory()->create([
            'user_id' => $owner->id,
        ]);

        $token = $admin->createToken('api-token')->plainTextToken;

        $this->withHeader('Authorization', 'Bearer ' . $token)
            ->deleteJson("/api/recetas/{$receta->id}")
            ->assertStatus(200);
    }
}
```

Puntos clave:

* Se usa `RefreshDatabase`
* Los roles se crean **antes de asignarlos**
* El test es completamente autónomo y reproducible

---

#### 18.10.6 Limpieza de caché (cuando se trabaja fuera de tests)

Spatie cachea roles y permisos.
Cuando se cambian guards, roles o configuración, es recomendable ejecutar:

```bash
php artisan optimize:clear
php artisan permission:cache-reset
php artisan config:clear
```

---

#### 18.10.7 Resumen del punto 18

* El guard correcto en APIs Laravel 11/12 con Sanctum es **`sanctum`**
* Los roles deben crearse para ese guard
* Spatie usa el guard por defecto de Laravel
* En tests, los roles **no existen si no se crean explícitamente**
* Los tests deben ser **autosuficientes**

Este enfoque refleja **cómo se trabaja realmente en proyectos profesionales**, especialmente en entornos de mantenimiento y evolución de APIs existentes.


---

# Apéndice A — Consideraciones sobre portabilidad de base de datos (`LIKE` vs `ILIKE`)

## A.1 Contexto

En la implementación de la búsqueda de recetas se ha utilizado, en algunos motores de base de datos, el operador `ILIKE`:

```sql
ILIKE '%texto%'
```

Este operador permite realizar búsquedas **case-insensitive** (sin distinguir mayúsculas/minúsculas), pero **no forma parte del estándar SQL**.

---

## A.2 Problema de portabilidad

`ILIKE` **solo está disponible en PostgreSQL**.

Otros motores habituales en proyectos Laravel:

| Motor           | Soporta `LIKE` | Soporta `ILIKE` |
| --------------- | -------------- | --------------- |
| PostgreSQL      | Sí             | Sí              |
| MySQL / MariaDB | Sí             | ❌               |
| SQLite          | Sí             | ❌               |

Esto implica que:

* el código funcionará correctamente en PostgreSQL
* **fallará** o no será válido si se migra a MySQL o SQLite
* el proyecto queda **acoplado a un motor concreto**

En un contexto docente o en proyectos que puedan evolucionar, esto **no es deseable**.

---

## A.3 Alternativa portable recomendada

Para mantener una búsqueda **case-insensitive** y **portable entre motores**, se recomienda:

1. Normalizar el texto a minúsculas
2. Usar `LIKE`, que sí es estándar SQL

Ejemplo:

```php
if ($search = $request->query('q')) {
    $search = mb_strtolower($search);

    $query->where(function ($q) use ($search) {
        $q->whereRaw('LOWER(titulo) LIKE ?', ["%{$search}%"])
          ->orWhereRaw('LOWER(descripcion) LIKE ?', ["%{$search}%"]);
    });
}
```

---

## A.4 Ventajas de esta solución

* ✅ Funciona en PostgreSQL, MySQL y SQLite
* ✅ Comportamiento coherente entre motores
* ✅ No depende de extensiones específicas
* ✅ Fácil de entender y mantener
* ✅ Adecuada para proyectos docentes y de tamaño medio

---

## A.5 Limitaciones (a tener en cuenta)

* El uso de `LOWER()` puede impedir el uso directo de índices
* En proyectos grandes se suelen usar:

  * índices funcionales
  * búsquedas de texto completo (Full-Text Search)
  * motores externos (Meilisearch, Elasticsearch, etc.)

Estas alternativas **quedan fuera del alcance de este proyecto**, pero son una evolución natural en sistemas reales.

---

## A.6 Nota final

> En este proyecto se ha priorizado la claridad, la portabilidad y el aprendizaje.
> La búsqueda puede implementarse con `ILIKE` (PostgreSQL) o con `LOWER(...) LIKE` (portable).
> **Modificar esta parte forma parte de una mejora/refactorización razonable del sistema.**


---

# Apéndice B — Documentación de la API con Swagger (OpenAPI)

## B.1 ¿Qué es Swagger / OpenAPI?

**OpenAPI** es un estándar para describir APIs REST:

* endpoints
* parámetros
* respuestas
* códigos de error
* esquemas de datos

**Swagger** es el conjunto de herramientas más conocido que implementa OpenAPI, permitiendo:

* Documentación **autogenerada**
* Interfaz web interactiva
* Pruebas de endpoints desde el navegador
* Mejor comunicación backend ↔ frontend

En proyectos reales:

* Swagger **no sustituye** a los tests
* Sirve como:

  * contrato
  * documentación viva
  * ayuda para frontend y QA

---

## B.2 Paquete usado en Laravel

En Laravel es habitual usar:

```
darkaonline/l5-swagger
```

Ventajas:

* Muy extendido
* Basado en anotaciones PHP
* No obliga a documentar todo de golpe

---

## B.3 Instalación de Swagger

Desde Sail:

```bash
./vendor/bin/sail composer require darkaonline/l5-swagger
```

Publicar configuración:

```bash
./vendor/bin/sail artisan vendor:publish \
  --provider="L5Swagger\L5SwaggerServiceProvider"
```

Generar documentación inicial:

```bash
./vendor/bin/sail artisan l5-swagger:generate
```

---

## B.4 Acceso a la UI de Swagger

Por defecto:

```
http://localhost/api/documentation
```

Desde ahí se puede:

* Ver endpoints
* Probar peticiones
* Enviar token Bearer

---

## B.5 Autenticación Bearer en Swagger

En `config/l5-swagger.php` ya viene preparado soporte para **Bearer Token**.

En la UI:

1. Pulsar **Authorize**
2. Introducir:

   ```
   Bearer TU_TOKEN
   ```

Swagger enviará el token en las peticiones.

---

## B.6 Documentar un endpoint (ejemplo real)

No es necesario documentar toda la API.
Se recomienda empezar por **uno o dos endpoints clave**.

Ejemplo: **crear receta**

---

### B.6.1 Endpoint `POST /api/recetas`

En `RecetaController.php`, sobre el método `store()`:

```php
/**
 * @OA\Post(
 *     path="/api/recetas",
 *     summary="Crear una nueva receta",
 *     tags={"Recetas"},
 *     security={{"bearerAuth":{}}},
 *
 *     @OA\RequestBody(
 *         required=true,
 *         @OA\JsonContent(
 *             required={"titulo","descripcion","instrucciones"},
 *             @OA\Property(property="titulo", type="string", example="Tortilla de patatas"),
 *             @OA\Property(property="descripcion", type="string", example="Receta clásica española"),
 *             @OA\Property(property="instrucciones", type="string", example="Batir huevos y freír patatas")
 *         )
 *     ),
 *
 *     @OA\Response(
 *         response=201,
 *         description="Receta creada correctamente"
 *     ),
 *     @OA\Response(
 *         response=401,
 *         description="No autenticado"
 *     ),
 *     @OA\Response(
 *         response=422,
 *         description="Datos de entrada inválidos"
 *     )
 * )
 */
public function store(Request $request)
{
    // ...
}
```

Tras añadir la anotación:

```bash
./vendor/bin/sail artisan l5-swagger:generate
```

Refrescar `http://localhost/api/documentation`.

---

## B.7 Buenas prácticas con Swagger

* Documentar **primero**:

  * endpoints públicos
  * endpoints más usados
* No obsesionarse con documentar todo
* Mantener Swagger **alineado con el código**
* No usar Swagger como sustituto de tests

---

## B.8 Limitaciones y advertencias

* Las anotaciones pueden resultar **verbosas**
* Mantener Swagger completo requiere disciplina
* En proyectos grandes:

  * a veces se combina con YAML
  * o se documenta solo lo esencial

Para este proyecto:

> Swagger es **un complemento**, no el núcleo.


---

# Apéndice D — Puesta en marcha del proyecto tras clonar el repositorio

Cuando se clona el repositorio del proyecto, **no todo el entorno está incluido**.
Esto es normal y ocurre en cualquier proyecto profesional.

Este apéndice explica **qué pasos hay que realizar para ejecutar la API por primera vez**.

---

## D.1 Qué NO se sube al repositorio (intencionadamente)

Por motivos de seguridad y buenas prácticas, **no se versionan**:

* `.env`
* `vendor/`
* `node_modules/`
* claves de aplicación (`APP_KEY`)
* datos de la base de datos

Cada desarrollador debe **reconstruir el entorno local**.

---

## D.2 Pasos comunes (siempre necesarios)

Independientemente de si se usa Sail o no, hay que:

1. Instalar dependencias (`composer install`)
2. Crear el archivo `.env`
3. Generar la `APP_KEY`
4. Configurar la base de datos
5. Ejecutar migraciones y seeders

---

## D.3 Opción A — Proyecto sin Sail (PHP + Composer)

### Requisitos

* PHP (versión compatible con Laravel 12)
* Composer
* Base de datos (PostgreSQL / MySQL / SQLite)

---

### Pasos

#### 1️⃣ Instalar dependencias

Desde la raíz del proyecto:

```bash
composer install
```

---

#### 2️⃣ Crear el archivo `.env`

Copiar el archivo de ejemplo:

```bash
cp .env.example .env
```

---

#### 3️⃣ Generar la clave de aplicación

```bash
php artisan key:generate
```

Esto genera la variable:

```env
APP_KEY=base64:...
```

---

#### 4️⃣ Configurar la base de datos

Editar `.env` según el motor usado, por ejemplo PostgreSQL:

```env
DB_CONNECTION=pgsql
DB_HOST=127.0.0.1
DB_PORT=5432
DB_DATABASE=recetas
DB_USERNAME=usuario
DB_PASSWORD=password
```

---

#### 5️⃣ Ejecutar migraciones y seeders

```bash
php artisan migrate --seed
```

---

#### 6️⃣ Lanzar el servidor

```bash
php artisan serve
```

La API estará disponible en:

```
http://localhost:8000
```

---

## D.4 Opción B — Proyecto con Sail (Docker)

Esta es la opción recomendada si el proyecto se ha trabajado con Docker.

---

### Requisitos

* Docker
* Docker Compose

---

### Pasos

#### 1️⃣ Instalar dependencias con Sail

Si no se dispone de PHP o Composer en local:

```bash
docker run --rm \
  -u "$(id -u):$(id -g)" \
  -v "$(pwd):/var/www/html" \
  -w /var/www/html \
  laravelsail/php84-composer:latest \
  composer install --ignore-platform-reqs
```

---

#### 2️⃣ Crear el archivo `.env`

```bash
cp .env.example .env
```

---

#### 3️⃣ Levantar los contenedores

```bash
./vendor/bin/sail up -d
```

---

#### 4️⃣ Generar la clave de aplicación

```bash
./vendor/bin/sail artisan key:generate
```

---

#### 5️⃣ Configurar la base de datos (si es necesario)

En proyectos con Sail, normalmente ya viene preparada:

```env
DB_CONNECTION=pgsql
DB_HOST=pgsql
DB_PORT=5432
DB_DATABASE=laravel
DB_USERNAME=sail
DB_PASSWORD=password
```

---

#### 6️⃣ Ejecutar migraciones y seeders

```bash
./vendor/bin/sail artisan migrate --seed
```

---

#### 7️⃣ Acceder a la aplicación

Por defecto:

```
http://localhost
```

---

## D.5 Variables importantes del archivo `.env`

Además de la base de datos y `APP_KEY`, hay que revisar:

```env
APP_NAME="API Recetas"
APP_ENV=local
APP_DEBUG=true
APP_URL=http://localhost

AUTH_GUARD=sanctum
```

Si se usa Swagger:

```env
L5_SWAGGER_GENERATE_ALWAYS=true
```

---

## D.6 Problemas habituales tras clonar

* ❌ Error de `APP_KEY` → ejecutar `key:generate`
* ❌ Error de base de datos → revisar `.env`
* ❌ Roles no existen → ejecutar seeders
* ❌ Cambios no reflejados → limpiar caché:

```bash
php artisan optimize:clear
php artisan permission:cache-reset
```

---

## D.7 Idea clave para el alumnado

> Clonar un repositorio **no significa que el proyecto esté listo para ejecutarse**.
> Reconstruir el entorno es parte del trabajo real de backend.

---
Muy buena observación.
Esto **siempre pasa en proyectos reales** y, si no se deja por escrito, genera muchísimo ruido al clonar el repo por primera vez.

Te dejo **un apéndice limpio, autocontenido y didáctico**, pensado **para no liar al alumnado**, con **dos caminos claros**:

* A) Proyecto sin Sail (PHP + Composer)
* B) Proyecto con Sail (Docker)

Está listo para **añadir como apéndice al documento**.

---

# Apéndice D — Puesta en marcha del proyecto tras clonar el repositorio

Cuando se clona el repositorio del proyecto, **no todo el entorno está incluido**.
Esto es normal y ocurre en cualquier proyecto profesional.

Este apéndice explica **qué pasos hay que realizar para ejecutar la API por primera vez**.

---

## D.1 Qué NO se sube al repositorio (intencionadamente)

Por motivos de seguridad y buenas prácticas, **no se versionan**:

* `.env`
* `vendor/`
* `node_modules/`
* claves de aplicación (`APP_KEY`)
* datos de la base de datos

Cada desarrollador debe **reconstruir el entorno local**.

---

## D.2 Pasos comunes (siempre necesarios)

Independientemente de si se usa Sail o no, hay que:

1. Instalar dependencias (`composer install`)
2. Crear el archivo `.env`
3. Generar la `APP_KEY`
4. Configurar la base de datos
5. Ejecutar migraciones y seeders

---

## D.3 Opción A — Proyecto sin Sail (PHP + Composer)

### Requisitos

* PHP (versión compatible con Laravel 12)
* Composer
* Base de datos (PostgreSQL / MySQL / SQLite)

---

### Pasos

#### 1️⃣ Instalar dependencias

Desde la raíz del proyecto:

```bash
composer install
```

---

#### 2️⃣ Crear el archivo `.env`

Copiar el archivo de ejemplo:

```bash
cp .env.example .env
```

---

#### 3️⃣ Generar la clave de aplicación

```bash
php artisan key:generate
```

Esto genera la variable:

```env
APP_KEY=base64:...
```

---

#### 4️⃣ Configurar la base de datos

Editar `.env` según el motor usado, por ejemplo PostgreSQL:

```env
DB_CONNECTION=pgsql
DB_HOST=127.0.0.1
DB_PORT=5432
DB_DATABASE=recetas
DB_USERNAME=usuario
DB_PASSWORD=password
```

---

#### 5️⃣ Ejecutar migraciones y seeders

```bash
php artisan migrate --seed
```

---

#### 6️⃣ Lanzar el servidor

```bash
php artisan serve
```

La API estará disponible en:

```
http://localhost:8000
```

---

## D.4 Opción B — Proyecto con Sail (Docker)

Esta es la opción recomendada si el proyecto se ha trabajado con Docker.

---

### Requisitos

* Docker
* Docker Compose

---

### Pasos

#### 1️⃣ Instalar dependencias con Sail

Si no se dispone de PHP o Composer en local:

```bash
docker run --rm \
  -u "$(id -u):$(id -g)" \
  -v "$(pwd):/var/www/html" \
  -w /var/www/html \
  laravelsail/php84-composer:latest \
  composer install --ignore-platform-reqs
```

---

#### 2️⃣ Crear el archivo `.env`

```bash
cp .env.example .env
```

---

#### 3️⃣ Levantar los contenedores

```bash
./vendor/bin/sail up -d
```

---

#### 4️⃣ Generar la clave de aplicación

```bash
./vendor/bin/sail artisan key:generate
```

---

#### 5️⃣ Configurar la base de datos (si es necesario)

En proyectos con Sail, normalmente ya viene preparada:

```env
DB_CONNECTION=pgsql
DB_HOST=pgsql
DB_PORT=5432
DB_DATABASE=laravel
DB_USERNAME=sail
DB_PASSWORD=password
```

---

#### 6️⃣ Ejecutar migraciones y seeders

```bash
./vendor/bin/sail artisan migrate --seed
```

---

#### 7️⃣ Acceder a la aplicación

Por defecto:

```
http://localhost
```

---

## D.5 Variables importantes del archivo `.env`

Además de la base de datos y `APP_KEY`, hay que revisar:

```env
APP_NAME="API Recetas"
APP_ENV=local
APP_DEBUG=true
APP_URL=http://localhost

AUTH_GUARD=sanctum
```

Si se usa Swagger:

```env
L5_SWAGGER_GENERATE_ALWAYS=true
```

---

## D.6 Problemas habituales tras clonar

* ❌ Error de `APP_KEY` → ejecutar `key:generate`
* ❌ Error de base de datos → revisar `.env`
* ❌ Roles no existen → ejecutar seeders
* ❌ Cambios no reflejados → limpiar caché:

```bash
php artisan optimize:clear
php artisan permission:cache-reset
```

---

## D.7 Idea clave para el alumnado

> Clonar un repositorio **no significa que el proyecto esté listo para ejecutarse**.
> Reconstruir el entorno es parte del trabajo real de backend.

---

Este apéndice suele ahorrar **mucho tiempo** y evita confusión innecesaria.
---
# Apéndice E — Despliegue mínimo de la API con Docker y Nginx

Este apéndice describe cómo realizar un **despliegue mínimo funcional** de la API de Recetas usando:

* Docker
* Docker Compose
* Nginx como reverse proxy
* PHP-FPM
* Base de datos (PostgreSQL)

El objetivo es **entender el proceso**, no cubrir todos los aspectos avanzados de producción.

---

## E.1 Arquitectura del despliegue

La arquitectura mínima será:

```
Cliente HTTP
   |
   v
Nginx (80)
   |
   v
PHP-FPM (Laravel)
   |
   v
PostgreSQL
```

Cada componente se ejecuta en **un contenedor independiente**.

---

## E.2 Estructura de directorios

En la raíz del proyecto:

```
.
├── docker/
│   ├── nginx/
│   │   └── default.conf
│   └── php/
│       └── Dockerfile
├── docker-compose.yml
├── .env.production
├── app/
├── public/
└── ...
```

---

## E.3 Dockerfile para PHP (Laravel)

📄 `docker/php/Dockerfile`

```dockerfile
FROM php:8.4-fpm

# Dependencias del sistema
RUN apt-get update && apt-get install -y \
    git \
    unzip \
    libpq-dev \
    && docker-php-ext-install pdo pdo_pgsql

# Composer
COPY --from=composer:2 /usr/bin/composer /usr/bin/composer

WORKDIR /var/www/html

# Copiar código
COPY . .

# Permisos
RUN chown -R www-data:www-data storage bootstrap/cache

# Instalar dependencias
RUN composer install --no-dev --optimize-autoloader

CMD ["php-fpm"]
```

---

## E.4 Configuración de Nginx

📄 `docker/nginx/default.conf`

```nginx
server {
    listen 80;
    server_name localhost;

    root /var/www/html/public;
    index index.php;

    location / {
        try_files $uri $uri/ /index.php?$query_string;
    }

    location ~ \.php$ {
        fastcgi_pass app:9000;
        fastcgi_index index.php;
        include fastcgi_params;
        fastcgi_param SCRIPT_FILENAME $document_root$fastcgi_script_name;
    }

    location ~ /\. {
        deny all;
    }
}
```

---

## E.5 Docker Compose

📄 `docker-compose.yml`

```yaml
version: "3.9"

services:
  app:
    build:
      context: .
      dockerfile: docker/php/Dockerfile
    container_name: recetas_app
    env_file:
      - .env.production
    volumes:
      - .:/var/www/html
    depends_on:
      - db

  nginx:
    image: nginx:alpine
    container_name: recetas_nginx
    ports:
      - "80:80"
    volumes:
      - .:/var/www/html
      - ./docker/nginx/default.conf:/etc/nginx/conf.d/default.conf
    depends_on:
      - app

  db:
    image: postgres:15
    container_name: recetas_db
    environment:
      POSTGRES_DB: recetas
      POSTGRES_USER: recetas
      POSTGRES_PASSWORD: secret
    volumes:
      - dbdata:/var/lib/postgresql/data

volumes:
  dbdata:
```

---

## E.6 Archivo de entorno para producción

📄 `.env.production`

```env
APP_NAME="API Recetas"
APP_ENV=production
APP_KEY=base64:GENERAR_EN_PRODUCCION
APP_DEBUG=false
APP_URL=http://localhost

LOG_CHANNEL=stderr

DB_CONNECTION=pgsql
DB_HOST=db
DB_PORT=5432
DB_DATABASE=recetas
DB_USERNAME=recetas
DB_PASSWORD=secret

AUTH_GUARD=sanctum
```

⚠️ **Este archivo no se sube al repositorio público**.

---

## E.7 Generar APP_KEY para producción

Antes de levantar el entorno por primera vez:

```bash
docker compose run --rm app php artisan key:generate --show
```

Copiar el valor generado en `.env.production`.

---

## E.8 Levantar el entorno

Desde la raíz del proyecto:

```bash
docker compose up -d --build
```

---

## E.9 Ejecutar migraciones y seeders

```bash
docker compose exec app php artisan migrate --seed
```

---

## E.10 Acceso a la API

La API estará disponible en:

```
http://localhost
```

Ejemplo:

```bash
http GET http://localhost/api/recetas
```

---

## E.11 Consideraciones importantes

Este despliegue:

✔ Es válido para pruebas y demos
✔ Refleja un entorno real simplificado
❌ No incluye HTTPS
❌ No incluye balanceo
❌ No incluye cache distribuida

---

## E.12 Qué sería necesario para producción real

(No obligatorio para esta tarea)

* HTTPS (Let's Encrypt)
* Variables de entorno seguras
* Backups
* Monitorización
* CI/CD

---

## E.13 Idea clave para el alumnado

> El despliegue **no es “subir el código”**.
> Es **reconstruir el entorno de ejecución** de forma controlada.

Este apéndice muestra el **mínimo profesional razonable**.

---

Si quieres, el siguiente paso natural podría ser:

* una **versión aún más reducida** (solo conceptos)
* o un **diagrama del despliegue**
* o convertir esto en una **tarea opcional avanzada** para alumnos fuertes
