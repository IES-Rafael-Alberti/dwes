# CRUD procedural seguro

## Resultado esperado

Construir un CRUD pequeño sin ocultar el ciclo HTTP, pero sin repetir las prácticas inseguras del concesionario de 2022.

## Incrementos TDD

1. Listar recursos con salida escapada.
2. Crear mediante validación, PDO preparado y PRG.
3. Consultar por identificador válido y devolver 404 cuando no existe.
4. Editar solo un recurso del usuario autenticado.
5. Borrar mediante POST y token CSRF.

Cada incremento empieza por una prueba observable. La comprobación manual en el navegador complementa, no sustituye, la suite.

## Contrato completo de operaciones

| Operación | Método | Validación y resultado |
|---|---|---|
| Listar | GET | paginación acotada, salida escapada, `200` |
| Ver | GET | ID entero positivo, recurso propio o `404` |
| Crear | POST | campos + regla de negocio, transacción, `303` |
| Editar | POST | propietario + CSRF + versión esperada, `303` o conflicto |
| Borrar | POST | propietario + CSRF, `303`; nunca GET |

```php
function validCsrf(string $submitted): bool
{
    $expected = (string) ($_SESSION['csrf_token'] ?? '');
    return $expected !== '' && hash_equals($expected, $submitted);
}
```

El token se genera con `bin2hex(random_bytes(32))` y se compara con `hash_equals` para evitar comparaciones dependientes del tiempo. La operación PDO combina identificador y propietario:

```php
$stmt = $pdo->prepare(
    'DELETE FROM tasks WHERE id = :id AND owner_id = :owner_id'
);
$stmt->execute(['id' => $id, 'owner_id' => $currentUserId]);
```

## Manejo de errores

- entrada inválida: `422`, sin ejecutar SQL;
- recurso ausente o no propio: `404` sin revelar su existencia;
- conflicto de integridad: mensaje estable, detalles en log;
- fallo inesperado: `500`, sin credenciales, consulta ni traza en la respuesta.

## Fronteras mínimas

```text
public/index.php        entrada HTTP y enrutado
src/validation.php     normalización y reglas
src/tasks.php          casos de uso
src/task_repository.php SQL mediante PDO
templates/             HTML con escape
tests/                 contratos y regresiones
```

Separar funciones no convierte el ejercicio en OOP: permite ver responsabilidades antes de introducir clases.

## Caso histórico: concesionario 2022

El proyecto anterior **no es una solución ejecutable de referencia**. Se conserva fuera del recorrido únicamente para localizar deuda:

- entradas interpoladas o concatenadas en SQL;
- credenciales y conexión mezcladas con casos de uso;
- operaciones de cambio sin defensa CSRF sistemática;
- escape, validación y autorización inconsistentes;
- múltiples endpoints PHP acoplados a formularios concretos.

La actividad correcta es identificar el riesgo y definir el test que lo demostraría; el starter, harness y comando de pruebas aún están pendientes del corte GTask. No hay todavía una práctica ejecutable oficial que permita afirmar RED/GREEN.

## Siguiente paso

[OOP y capas](06-oop-capas.md).
