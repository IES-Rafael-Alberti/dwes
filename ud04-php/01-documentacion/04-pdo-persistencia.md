# PDO y persistencia

## Conexión

Las credenciales proceden del entorno, no del repositorio.

```php
$pdo = new PDO(
    getenv('DATABASE_DSN'),
    getenv('DATABASE_USER'),
    getenv('DATABASE_PASSWORD'),
    [
        PDO::ATTR_ERRMODE => PDO::ERRMODE_EXCEPTION,
        PDO::ATTR_DEFAULT_FETCH_MODE => PDO::FETCH_ASSOC,
        PDO::ATTR_EMULATE_PREPARES => false,
    ],
);
```

## Consulta preparada

```php
$statement = $pdo->prepare(
    'SELECT id, title, completed FROM tasks WHERE id = :id AND owner_id = :owner_id'
);
$statement->execute(['id' => $taskId, 'owner_id' => $currentUserId]);
$task = $statement->fetch();
```

Los valores nunca se concatenan en SQL. Los nombres de tabla, columna u ordenación no aceptan parámetros: si deben variar, se seleccionan desde una lista permitida en el servidor.

## Transacciones

Una operación con varios cambios que deben ocurrir juntos usa `beginTransaction`, `commit` y `rollBack`. No muestres al usuario mensajes internos de PDO; registralos en servidor y devolvé un error estable.

## Diseño comprobable

Encapsulá PDO en un repositorio con métodos que expresen intención (`findOwnedBy`, `save`, `deleteOwnedBy`). Así las reglas HTTP no quedan mezcladas con SQL y pueden probarse por separado.

## Siguiente paso

[CRUD procedural seguro](05-crud-procedural-seguro.md).
