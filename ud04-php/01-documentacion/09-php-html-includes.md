# PHP, HTML y composición de páginas

## Flujo HTTP

GET presenta un recurso o formulario; POST envía una operación que cambia estado. El atributo `method` decide si PHP recibe valores en `$_GET` o `$_POST`; `action` indica la ruta receptora.

```php
<?php $title = 'Tasks'; ?>
<!doctype html>
<html lang="en">
<body>
  <h1><?= e($title) ?></h1>
  <?php if ($tasks === []): ?>
    <p>No tasks yet.</p>
  <?php else: ?>
    <ul>
      <?php foreach ($tasks as $task): ?>
        <li><?= e($task['title']) ?></li>
      <?php endforeach; ?>
    </ul>
  <?php endif; ?>
</body>
</html>
```

La lógica de presentación decide qué mostrar; las reglas de negocio y SQL no pertenecen a la plantilla.

## `include` y `require`

- `include` emite un warning si falta el archivo y continúa.
- `require` detiene la ejecución cuando falta una dependencia obligatoria.
- Las variantes `_once` impiden cargar dos veces definiciones del mismo archivo.

```php
require_once dirname(__DIR__) . '/src/bootstrap.php';
include dirname(__DIR__) . '/templates/header.php';
```

Construí rutas desde `__DIR__`, no desde el directorio de trabajo accidental. No formes nunca el nombre incluido a partir de entrada del usuario: abriría inclusión local/remota de archivos.

## Separación gradual

1. Extraer cabecera y pie repetidos.
2. Crear funciones de validación y escape.
3. Extraer casos de uso del script HTTP.
4. Extraer acceso PDO.
5. Sustituir includes ad hoc de clases por autoload de Composer.
