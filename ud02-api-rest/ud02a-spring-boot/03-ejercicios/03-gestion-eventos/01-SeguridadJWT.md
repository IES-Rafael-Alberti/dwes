# Tarea 3 – Seguridad y JWT (API Gestión de Eventos)

Objetivo: añadir autenticación/autorización a la API refactorizada, para poder consumirla desde el cliente Angular. Mantén la estructura de servicios/DTOs y añade seguridad sin romper los endpoints existentes.

## Requisitos mínimos (fundamentales)
- **CORS**: Configura `SecurityFilterChain` con CORS (orígenes/métodos/headers permitidos) incluyendo `Authorization` y `Content-Type` para el cliente Angular.
- **Usuarios y roles**: Entidad `Usuario` (username/email único, password hasheada con BCrypt, rol). Enum de roles al menos `PARTICIPANTE` y `ORGANIZADOR`.
- **Autenticación JWT**:
  - Endpoint `POST /auth/login` que recibe credenciales y devuelve un JWT firmado (incluye `sub`, `roles`, `exp`, `jti`).
  - Endpoint `POST /auth/register` para alta de usuario (rol por defecto `PARTICIPANTE`; permitir `ORGANIZADOR` si procede).
  - Endpoint `GET /auth/me` que devuelve el usuario autenticado (DTO sin password).
  - `POST /auth/logout`: invalidar en cliente; como base, mantén lista de tokens inválidos en memoria (jti) hasta que expiren.
  - Filtro JWT que valide firma/exp/jti en cada request y construya el `Authentication` a partir de `UserDetailsService`.
- **Autorización por roles**:
  - Endpoints públicos: `login`, `register` (y, si quieres, documentación futura).
  - Endpoints protegidos: resto de la API.
  - Restricciones: creación/edición/borrado de eventos/organizadores solo `ORGANIZADOR`; consultas generales accesibles a usuarios autenticados.
- **DTOs/errores**: No exponer entidades de usuario; usa DTOs y `@Valid`. Maneja 401/403 con el `@ControllerAdvice` existente o añade handlers para seguridad.

## Recomendado (si hay tiempo)
- **Blacklist persistente**: guardar los `jti` de tokens revocados en BD (tabla `revoked_tokens` con `expires_at`) y limpiar caducados con un job.
- **Refrescar tokens**: opcional, tokens de acceso cortos + refresh token con rotación/allowlist.
- **Mejoras de filtro**: aplicar CORS solo a las rutas necesarias; permitir `OPTIONS` preflight sin autenticación.

## Extra (bonus, optativo)
- **HTTPS local**: perfil con keystore auto-generado (`server.ssl.*`) para servir en HTTPS.
- **Swagger / Actuator**: lo dejaremos para la siguiente entrega, pero reserva las rutas en la configuración de seguridad si lo añades.

## Entrega
- Código en el mismo repositorio con commits claros.
- README con instrucciones breves de seguridad (login, token, roles, rutas públicas/protegidas).
- Evidencias: colección/capturas de login, me, CRUD con token de `ORGANIZADOR`, y un 403 cuando un `PARTICIPANTE` intenta acceder a una ruta restringida.

### Formato mínimo del README de seguridad
- Breve descripción de la seguridad aplicada (JWT, roles).
- Cómo arrancar la API y dónde se configura CORS.
- Endpoints de auth (`/auth/register`, `/auth/login`, `/auth/me`, `/auth/logout`) con ejemplos de payload/respuesta (sin incluir passwords reales).
- Qué roles existen y qué rutas son públicas/protegidas (ej.: creación de eventos solo ORGANIZADOR).
- Cómo usar el token en las peticiones (`Authorization: Bearer <token>`).
- Notas si usas extras: blacklist persistente, perfil HTTPS, refresh tokens o rutas abiertas para Swagger/Actuator.
