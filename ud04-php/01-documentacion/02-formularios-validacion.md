# Formularios y validación

## Regla principal

`$_GET`, `$_POST`, cabeceras y cookies son entrada no confiable. Validar decide si el dato puede entrar al sistema; escapar decide cómo puede salir a HTML.

## Lectura explícita

```php
$email = filter_input(INPUT_POST, 'email', FILTER_VALIDATE_EMAIL);
$name = trim((string) ($_POST['name'] ?? ''));

$errors = [];
if ($email === false || $email === null) {
    $errors['email'] = 'Enter a valid email address';
}
if ($name === '' || mb_strlen($name) > 80) {
    $errors['name'] = 'Name is required and must not exceed 80 characters';
}
```

No uses `filter_input` como sustituto de las reglas de negocio. Un correo válido sintácticamente puede seguir estando duplicado.

## Salida HTML

```php
function e(string $value): string
{
    return htmlspecialchars($value, ENT_QUOTES | ENT_SUBSTITUTE, 'UTF-8');
}
```

```php
<input name="name" value="<?= e($name) ?>">
```

Escapá en el límite de salida según el contexto. `htmlspecialchars` protege HTML; no vuelve seguro un valor para SQL, JavaScript o una URL.

## Flujo POST/Redirect/GET

Después de una operación válida, redirigí con `303 See Other`. Así refrescar la página no repite el POST. Si hay errores, devolvé el formulario con estado `422` y mensajes asociados a cada campo.

## Contrato de prueba

- dato ausente o mal formado → rechazo;
- dato válido → normalización conocida;
- texto con etiquetas → se muestra escapado;
- POST válido → redirección y una sola operación.

## Siguiente paso

[Sesiones y cookies](03-sesiones-cookies.md).
