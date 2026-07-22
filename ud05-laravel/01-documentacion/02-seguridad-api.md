# Seguridad transversal en una API Laravel

La seguridad no aparece al final de la API. Cada endpoint se revisa con esta secuencia: identificar al cliente, validar la entrada, autorizar la acción, limitar la salida y probar el rechazo.

## 1. Autenticación con Sanctum

Los endpoints privados usan tokens Bearer de Sanctum y el middleware `auth:sanctum`. Un token identifica a la persona que hace la petición; no autoriza automáticamente todas sus acciones.

```http
Authorization: Bearer <token>
```

Un endpoint sin token debe devolver `401`. Los tokens se crean tras un inicio de sesión correcto y se revocan al cerrar sesión. No se guardan tokens ni credenciales en el repositorio, colecciones HTTP públicas o capturas.

## 2. Validación en servidor

Cada entrada de creación o actualización se valida con un Form Request. Las reglas están fuera del controlador para que sean reutilizables, auditables y comprobables mediante pruebas.

No se debe usar `$request->all()` para persistir datos. El controlador recibe únicamente `$request->validated()` y el modelo solo permite campos explícitos.

Un cuerpo inválido debe devolver `422` con errores por campo. La validación de una interfaz cliente puede mejorar la experiencia, pero nunca reemplaza la validación HTTP del servidor.

## 3. Autorización por recurso

Una Policy responde si una persona autenticada puede actuar sobre una instancia concreta. En Recetas, el propietario puede modificar o borrar su recurso; el administrador tiene únicamente las excepciones declaradas.

La autenticación responde a «quién es». La Policy responde a «puede hacer esto sobre este recurso». Confundir ambos conceptos produce APIs que permiten modificar datos ajenos.

Un acceso autenticado pero no autorizado debe devolver `403`. Cada mutación debe tener una prueba de propietario permitido y de usuario ajeno rechazado.

## 4. Contrato JSON y errores

Los API Resources controlan los campos de salida. No se devuelve un modelo Eloquent directamente: puede filtrar atributos internos hoy y exponerlos por accidente mañana.

Las respuestas deben conservar una forma estable:

| Situación | Estado esperado |
|---|---:|
| Entrada inválida | 422 |
| Sin token o token inválido | 401 |
| Token válido sin permiso | 403 |
| Recurso inexistente | 404 |
| Conflicto de regla de negocio | 409 |

No se envían trazas, claves, consultas SQL ni mensajes internos al cliente. El detalle técnico se conserva únicamente en logs locales protegidos.

## 5. Sail, secretos y dependencias

`.env` es local. El repositorio solo aporta `.env.example`; nunca se suben `APP_KEY`, tokens, contraseñas o exportaciones de base de datos. Sail aísla PHP, Composer y las extensiones, pero no convierte un secreto versionado en seguro.

Antes de entregar o publicar:

```bash
./vendor/bin/sail artisan test
git status --short
```

Revisa que no aparezcan `.env`, `storage/logs`, `vendor/`, `node_modules/` ni archivos generados. Actualiza dependencias mediante Composer dentro de Sail y valida la suite antes de aceptar el cambio.

## 6. CSRF y CORS

La API didáctica usa tokens Bearer y no cookies de sesión para autenticar mutaciones; por ello CSRF no protege este flujo. Si una API pasa a usar autenticación basada en cookies con Sanctum SPA, se debe activar y probar la protección CSRF.

CORS no autoriza usuarios: solo controla qué orígenes del navegador pueden llamar a la API. Se configura con el origen real del cliente, métodos y cabeceras mínimos; `*` no se usa junto con credenciales.
