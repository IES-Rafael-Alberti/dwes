# docs/05_ITERATION_2_JWT.md

## Iteracion 2 — Usuarios + Autenticacion JWT (sin Identity)

### Objetivo
Incorporar usuarios y autenticacion **JWT** con impacto minimo en el codigo existente.

### Alcance funcional
- Entidad `User` persistida en SQLite.
- Endpoints:
  - `POST /api/auth/register` → devuelve JWT
  - `POST /api/auth/login` → devuelve JWT
  - `POST /api/auth/logout` → invalida el token (en este proyecto: logout "cliente")
- Proteger `/api/tasks` con `[Authorize]`.
- Activar HTTPS en desarrollo.

### Decisiones de diseno
- **Sin ASP.NET Identity**: se implementa autenticacion sencilla para entender el flujo.
- **JWT simple**: claims basicos (`sub`, `email`, `role`).
- **Sin refresh tokens** en Iteracion 2.
- **Logout**: se resuelve en cliente (borrando token). No hay revocacion en servidor.

---

## Modelo `User`

Campos:
- `Id` (int, autoincrement)
- `Username` (string, unico)
- `Email` (string, unico)
- `PasswordHash` (string)
- `Role` (`User | Supervisor | Admin`)
- `AvatarUrl` (string, opcional) **no se usa en Iteracion 2**

Notas:
- `PasswordHash` se calcula en servidor (no se guarda password en claro).
- La validacion de `Email` y `Username` es minima (formato basico y unicidad).

---

## API de autenticacion

### `POST /api/auth/register`
Entrada:
```json
{
  "username": "ana",
  "email": "ana@example.com",
  "password": "123456"
}
```

Salida:
```json
{
  "token": "<jwt>",
  "expiresIn": 3600
}
```

Errores:
- `400` datos invalidos
- `409` email/username ya existe

### `POST /api/auth/login`
Entrada:
```json
{
  "email": "ana@example.com",
  "password": "123456"
}
```

Salida:
```json
{
  "token": "<jwt>",
  "expiresIn": 3600
}
```

Errores:
- `400` datos invalidos
- `401` credenciales incorrectas

### `POST /api/auth/logout`
- En Iteracion 2 es **logout cliente** (borrar token en frontend).
- El endpoint puede devolver `204 No Content` para dejar claro el flujo.

---

## JWT (contenido minimo)

Claims recomendadas:
- `sub`: id del usuario
- `email`: email del usuario
- `role`: rol del usuario

Configuracion:
- `Issuer`, `Audience`, `SigningKey` en `appsettings.json`.
- Duracion recomendada: 1h (`expiresIn = 3600`).

Ejemplo de `appsettings.json`:
```json
{
  "Jwt": {
    "Issuer": "TodoApi",
    "Audience": "TodoApiClient",
    "SigningKey": "REEMPLAZAR_POR_UN_SECRETO_LARGO",
    "ExpiresInSeconds": 3600
  }
}
```

Ejemplo de registro en `Program.cs` (resumen):
```csharp
builder.Services.AddAuthentication("Bearer")
    .AddJwtBearer("Bearer", options =>
    {
        options.TokenValidationParameters = new TokenValidationParameters
        {
            ValidIssuer = jwt.Issuer,
            ValidAudience = jwt.Audience,
            IssuerSigningKey = new SymmetricSecurityKey(
                Encoding.UTF8.GetBytes(jwt.SigningKey)),
            ValidateIssuer = true,
            ValidateAudience = true,
            ValidateIssuerSigningKey = true
        };
    });

app.UseAuthentication();
app.UseAuthorization();
```

---

## Integracion con Tasks

- Se protege `TasksController` con `[Authorize]`.
- En Iteracion 2 se añade `UserId` a `TodoTask` **pero no se restringe** el acceso por usuario.
- Al crear tareas, se asocia `UserId` con el usuario autenticado.

---

## Migraciones

- Nueva tabla `Users`.
- Columna `UserId` en `Tasks`.
- Nueva migracion EF Core.

---

## Errores comunes

- Olvidar activar `app.UseAuthentication()` antes de `UseAuthorization()`.
- Configurar mal `Issuer`/`Audience` y romper validacion de tokens.
- Guardar password en claro (siempre hash).
- No proteger `/api/tasks` y dejar la API abierta.
- Usar un `SigningKey` corto o facil de adivinar.

---

## Estructura prevista (carpetas/archivos)

- `TodoApi/Models/User.cs`
- `TodoApi/DTOs/Auth/RegisterDto.cs`
- `TodoApi/DTOs/Auth/LoginDto.cs`
- `TodoApi/DTOs/Auth/AuthResponseDto.cs`
- `TodoApi/Controllers/AuthController.cs`
- `TodoApi/Services/IAuthService.cs`
- `TodoApi/Services/AuthService.cs`
- `TodoApi/Repositories/IUsersRepository.cs`
- `TodoApi/Repositories/UsersEfRepository.cs`
- `TodoApi/Mapping/AuthMapping.cs` (opcional, si se decide separar mapeos)

> Nota: esta estructura puede ajustarse al implementar. Se mantiene la arquitectura Controller → Service → Repository → EF.

---

## DTOs de autenticacion (propuesta)

### RegisterDto
- `Username` (min 3)
- `Email` (formato email)
- `Password` (min 6)

### LoginDto
- `Email`
- `Password`

### AuthResponseDto
- `Token`
- `ExpiresIn`

---

## Validaciones minimas (propuesta)

- `Username` requerido (min 3, max 50).
- `Email` requerido (formato valido).
- `Password` requerido (min 6).
- Unicidad de `Email` y `Username` en base de datos.

---

## Hashing de password (decidir en implementacion)

Opciones posibles:
- `PasswordHasher<T>` de `Microsoft.AspNetCore.Identity` (sin Identity completo).
- `Rfc2898DeriveBytes` (PBKDF2) con salt.

Se elegira una unica opcion al implementar para mantenerlo simple.

---

## HTTPS en desarrollo

Comando recomendado:
```bash
dotnet dev-certs https --trust
```

---

## Frontend (plan minimo, si se hace en Iteracion 2)

- Formulario de login/registro.
- Guardar token en `localStorage`.
- Enviar `Authorization: Bearer <jwt>` en `fetch()`.
- Boton logout → borrar token.
- Toggle para activar/desactivar envio de token (demostracion de 401).
- Mostrar `exp` decodificado del JWT para ver la caducidad.

---

## Flujo completo (paso a paso)

1. **Registro**
   - `POST /api/auth/register` con `username`, `email`, `password`.
   - La API valida datos, guarda usuario y devuelve `token`.

2. **Login**
   - `POST /api/auth/login` con `email`, `password`.
   - Si es correcto, devuelve `token` y `expiresIn`.

3. **Guardar token**
   - Frontend guarda el token en `localStorage` (o memoria).

4. **Acceso protegido**
   - Cada llamada a `/api/tasks` incluye header:
     `Authorization: Bearer <jwt>`.
   - Si no hay token o es invalido → `401 Unauthorized`.

5. **Logout**
   - Se borra el token en el cliente.
   - No se invalida en servidor (Iteracion 2).

---

## Errores tipicos por endpoint (con ejemplos)

### `POST /api/auth/register`
- `400 Bad Request` si falta `email` o `password`.
- `409 Conflict` si `email` o `username` ya existe.

Ejemplo de respuesta:
```json
{
  "error": "USER_ALREADY_EXISTS",
  "message": "El email ya esta registrado."
}
```

### `POST /api/auth/login`
- `400 Bad Request` si faltan campos.
- `401 Unauthorized` si credenciales incorrectas.

Ejemplo:
```json
{
  "error": "INVALID_CREDENTIALS",
  "message": "Email o password incorrectos."
}
```

### `GET /api/tasks` (protegido)
- `401 Unauthorized` si no hay token o es invalido.

Ejemplo:
```json
{
  "error": "Unauthorized"
}
```

---

## Mini glosario JWT (para alumnado)

- **JWT (JSON Web Token)**: un string firmado que prueba que el usuario se autentico.
- **Header**: parte del token con el algoritmo usado (ej. HS256).
- **Payload**: datos del usuario (claims).
- **Signature**: firma generada con la clave secreta.
- **Claim**: dato dentro del token (`sub`, `email`, `role`).
- **Bearer token**: token enviado en el header `Authorization`.

Ejemplo de header:
```
Authorization: Bearer <jwt>
```

---

## Equivalencias rapidas con Spring Boot

Estas equivalencias sirven para orientarse si ya conoces Spring:

- `Controller` en .NET → `@RestController` en Spring.
- `Service` en .NET → `@Service` en Spring.
- `Repository` en .NET → `@Repository` en Spring.
- `DTO` en .NET → `DTO`/`Request`/`Response` en Spring.
- `[Authorize]` en .NET → `@PreAuthorize` o seguridad por filtros en Spring.
- `Program.cs` (configuracion) → `@Configuration` + `SecurityConfig` en Spring.
- `appsettings.json` → `application.yml` / `application.properties`.
- Middleware → Filters/Interceptors.

JWT:
- `AddAuthentication().AddJwtBearer()` → `SecurityFilterChain` + `JwtAuthenticationFilter`.
- Claims (`sub`, `role`) → claims/authorities en Spring Security.

Nota:
- En .NET se usa mucho inyeccion por constructor igual que en Spring.

---

## Checklist (Iteracion 2)

### Hecho
- [x] Documento base de Iteracion 2 (objetivos, endpoints, modelo, JWT, errores).
- [x] Casos de prueba de auth añadidos en `docs/04_API_TESTING.md`.
- [x] Repositorio de usuarios + servicio de JWT base.
- [x] Configuracion JWT en `Program.cs` y `appsettings.json`.
- [x] `AuthController` y proteccion de `/api/tasks`.
- [x] Asociacion de `UserId` al crear tareas.
- [x] Migracion EF (Users + UserId).
- [x] Tests de auth y proteccion de `/api/tasks`.
- [x] Pruebas manuales documentadas (curl/httpie).
- [x] Frontend minimo de auth integrado.

### Estado
- Iteracion 2 cerrada (backend + pruebas manuales + frontend minimo).

### Hecho (pendiente de posible ajuste)
- [x] Ejemplo de configuracion JWT en `appsettings.json` y `Program.cs` (puede ajustarse al implementar).

### Por hacer (codigo)
- [x] Crear entidad `User` y relacion con `TodoTask` (`UserId`).
- [x] Crear DTOs de auth (`RegisterDto`, `LoginDto`, `AuthResponseDto`).
- [x] Crear `IAuthService` y `AuthService`.
- [x] Implementar hashing de password.
- [x] Configurar JWT en `Program.cs` y `appsettings.json`.
- [x] Proteger `/api/tasks` con `[Authorize]`.
- [x] Asociar `UserId` al crear tareas.
- [x] Crear migracion y aplicar en SQLite.

### Por hacer (tests)
- [x] Tests de registro/login.
- [x] Tests de acceso protegido a `/api/tasks`.

### Por hacer (frontend)
- [x] UI minima para login/registro (panel integrado).
- [x] Guardar y enviar token JWT en `fetch()`.

### Por hacer (documentacion)
- [ ] Revisar y ajustar ejemplos de config JWT si cambian nombres finales.

---

## Pruebas minimas (manuales)

1. Registrar usuario → recibe JWT.
2. Acceder a `/api/tasks` sin token → `401`.
3. Acceder a `/api/tasks` con token → `200`.
4. Login con password incorrecto → `401`.
