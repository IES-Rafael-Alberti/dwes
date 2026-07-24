# Anexo — Herramientas del lenguaje

## Operadores y alcance

`===` y `!==` comparan valor y tipo; son la opción habitual frente a las coerciones de `==`. `??` permite proporcionar un valor cuando una clave no existe o es `null`.

```php
$page = max(1, (int) ($_GET['page'] ?? 1));
$status = $task['completed'] === true ? 'done' : 'pending';
```

Las variables declaradas fuera de una función no entran automáticamente en ella. Preferí parámetros y retornos a `global`. Una closure captura explícitamente con `use`:

```php
$minimum = 10;
$expensive = array_filter(
    $prices,
    static fn (float $price): bool => $price >= $minimum,
);
```

## Bucles y control

```php
$index = 0;
while ($index < count($items)) {
    if ($items[$index] === null) {
        $index++;
        continue;
    }
    if ($items[$index] === 'stop') {
        break;
    }
    $index++;
}

do {
    $attempts++;
} while ($attempts < 3 && !$accepted);
```

`while` puede no ejecutarse; `do-while` se ejecuta al menos una vez. `break` abandona el bucle y `continue` salta a la siguiente iteración.

En plantillas, la sintaxis alternativa conserva HTML legible:

```php
<?php foreach ($tasks as $task): ?>
    <li><?= e($task['title']) ?></li>
<?php endforeach; ?>
```

## Arrays multidimensionales

```php
$catalog = [
    ['id' => 1, 'name' => 'Mouse', 'tags' => ['usb', 'input']],
    ['id' => 2, 'name' => 'Monitor', 'tags' => ['display']],
];

$byId = array_column($catalog, null, 'id');
$names = array_column($catalog, 'name');
$visible = array_filter($catalog, static fn (array $p): bool => $p['id'] > 0);
```

Catálogo esencial: `count`, `array_keys`, `array_values`, `in_array` con tercer argumento `true`, `array_search`, `array_map`, `array_filter`, `array_reduce`, `array_column`, `array_merge`, `array_slice`, `sort` para listas y `asort`/`ksort` cuando hay que conservar claves.

## Closures, recursión y generadores

Una closure es adecuada para una operación local; la recursión solo cuando el problema tiene estructura recursiva y un caso base inequívoco:

```php
function factorial(int $n): int
{
    if ($n < 0) throw new InvalidArgumentException('n must be non-negative');
    return $n < 2 ? 1 : $n * factorial($n - 1);
}
```

Un generador procesa secuencias sin cargar todo en memoria:

```php
function lines(string $path): Generator
{
    $file = new SplFileObject($path);
    while (!$file->eof()) {
        yield rtrim($file->fgets());
    }
}
```

## Ejercicios recuperados

1. Clasificador por edad con `match` y límites probados.
2. Factorial iterativo y recursivo, incluidos 0 y negativos.
3. Detector de primos sin probar divisores innecesarios.
4. Filtrado y agrupación de un catálogo multidimensional.
5. Lectura perezosa de un archivo grande mediante generador.
