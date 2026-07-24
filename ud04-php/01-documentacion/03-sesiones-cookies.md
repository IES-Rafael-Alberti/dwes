# Sesiones y cookies

## Objetivo

Mantener identidad y preferencias sin convertir el navegador en una fuente de autoridad.

## Sesión segura

Configurá la cookie antes de `session_start()`:

```php
session_set_cookie_params([
    'httponly' => true,
    'secure' => true,
    'samesite' => 'Lax',
    'path' => '/',
]);
session_start();
```

Tras autenticar correctamente, llamá a `session_regenerate_id(true)`. En la sesión se guarda un identificador mínimo; el rol y la propiedad deben verificarse en servidor para cada operación sensible.

Las contraseñas se crean con `password_hash($plain, PASSWORD_DEFAULT)` y se comprueban con `password_verify($candidate, $hash)`. No se cifran de forma reversible ni se resumen con MD5/SHA.

## Cookies

Una cookie de preferencias puede vivir más tiempo. Una contraseña, un rol confiable o información sensible no deben almacenarse ahí. En producción, `Secure` exige HTTPS; no lo desactives para acomodar un despliegue inseguro.

## Cierre de sesión

1. Vaciar `$_SESSION`.
2. Invalidar la cookie de sesión con los mismos parámetros.
3. Destruir la sesión.
4. Redirigir.

El logout que cambia estado se ejecuta mediante POST y lleva token CSRF.

## Pruebas mínimas

- el login regenera el identificador;
- una ruta privada rechaza una sesión ausente;
- logout invalida la sesión;
- cambiar un `user_id` recibido no permite acceder a datos ajenos.

## Siguiente paso

[PDO y persistencia](04-pdo-persistencia.md).
