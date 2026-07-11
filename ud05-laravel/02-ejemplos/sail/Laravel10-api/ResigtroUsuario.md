## Registro de usuarios

## **🔹 1. Crear la Ruta para el Registro**
Agrega en **`routes/api.php`** la ruta para el registro:

```php
Route::post('/auth/register', [\App\Http\Controllers\Api\V1\AuthController::class, 'register']);
```

---

## **🔹 2. Modificar `AuthController.php` para Incluir el Registro**
Dentro de `AuthController.php`, agrega el método `register()`:

```php
/**
 * @OA\Post(
 *     path="/api/v1/auth/register",
 *     summary="Register a new user",
 *     @OA\RequestBody(
 *         required=true,
 *         @OA\JsonContent(
 *             required={"name", "email", "password"},
 *             @OA\Property(property="name", type="string", example="John Doe"),
 *             @OA\Property(property="email", type="string", format="email", example="john@example.com"),
 *             @OA\Property(property="password", type="string", format="password", example="securepassword123")
 *         ),
 *     ),
 *     @OA\Response(response=201, description="User registered successfully"),
 *     @OA\Response(response=400, description="Validation error"),
 *     @OA\Response(response=500, description="Server error")
 * )
 */
public function register(Request $request)
{
    // Validar los datos de entrada
    $request->validate([
        'name' => 'required|string|max:255',
        'email' => 'required|string|email|max:255|unique:users',
        'password' => 'required|string|min:6',
    ]);

    // Crear el usuario con la contraseña encriptada
    $user = User::create([
        'name' => $request->name,
        'email' => $request->email,
        'password' => bcrypt($request->password),
    ]);

    // Asignar el rol por defecto "user"
    $user->assignRole('user');

    // Generar token JWT
    $token = JWTAuth::fromUser($user);

    return response()->json([
        'message' => 'User registered successfully',
        'access_token' => $token,
        'token_type' => 'bearer',
        'expires_in' => auth()->factory()->getTTL() * 60
    ], 201);
}
```

---

## **🔹 3. Asegurar que el Rol `user` Existe**
Ya tienes el rol `user` en `DatabaseSeeder.php`, pero verifica que se está creando correctamente en la base de datos.

Si aún no has ejecutado las migraciones, hazlo con:

```bash
sail artisan migrate --seed
```

Si ya las ejecutaste pero necesitas regenerarlas, usa:

```bash
sail artisan migrate:fresh --seed
```

---

## **🔹 4. Probar el Registro**
Prueba el **registro** con Insomnia, Postman o curl:

### ** Petición `POST /api/v1/auth/register`**
```json
{
  "name": "Nuevo Usuario",
  "email": "nuevo@example.com",
  "password": "contraseñaSegura123"
}
```

### ** Respuesta Esperada (201)**
```json
{
    "message": "User registered successfully",
    "access_token": "eyJ0eXAiOiJKV1QiLCJh...",
    "token_type": "bearer",
    "expires_in": 3600
}
```

---

✅Ahora los nuevos usuarios se registrarán con el rol `"user"` por defecto y recibirán un token JWT para autenticarse inmediatamente.** 🚀