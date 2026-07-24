# Fundamentos de PHP 8.4

## Objetivo

Escribir programas pequeños con tipos, control de flujo, arrays y funciones antes de mezclar PHP con HTTP o bases de datos.

## Punto de partida

```php
<?php
declare(strict_types=1);

$name = 'Ada';
$age = 28;
echo "Hello, {$name}. You are {$age}.";
```

Usaremos `declare(strict_types=1)` en el código propio. PHP sigue siendo dinámico, pero los tipos declarados convierten muchos errores silenciosos en fallos visibles.

## Decisiones y repetición

```php
$label = match (true) {
    $age < 18 => 'minor',
    $age < 65 => 'adult',
    default => 'senior',
};

$prices = [12.5, 8.0, 21.2];
foreach ($prices as $price) {
    echo $price . PHP_EOL;
}
```

Usá `match` cuando quieras obtener un valor con comparación estricta. Conservá `if` para reglas con varios pasos y `foreach` para recorrer colecciones.

## Arrays

```php
$product = ['id' => 7, 'name' => 'Keyboard', 'price' => 49.90];
$names = array_map(
    static fn (array $item): string => $item['name'],
    [$product],
);
```

Los arrays sirven como listas y mapas, pero no sustituyen indefinidamente a un objeto con contrato. Comprobá claves externas antes de usarlas.

## Funciones pequeñas y tipadas

```php
function priceWithVat(float $price, float $rate = 0.21): float
{
    if ($price < 0) {
        throw new InvalidArgumentException('Price cannot be negative');
    }
    return round($price * (1 + $rate), 2);
}
```

Una función debe expresar una responsabilidad, declarar entrada y salida y no depender de variables globales. Evitá referencias salvo que la mutación sea realmente parte del contrato.

## Práctica

1. Escribí primero una prueba para precio negativo.
2. Implementá la excepción mínima.
3. Añadí casos para IVA por defecto y personalizado.
4. Refactorizá nombres sin cambiar el comportamiento.

## Siguiente paso

[Formularios y validación](02-formularios-validacion.md).
