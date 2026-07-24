# De PHP explícito a Laravel 12

## Idea central

Laravel no elimina HTTP, validación, persistencia o seguridad: les asigna abstracciones y convenciones. Primero hay que reconocer el mecanismo explícito.

| PHP sin framework | Laravel 12 | Responsabilidad que permanece |
|---|---|---|
| `public/index.php` y tabla de rutas | router y middleware | seleccionar el caso de uso |
| `$_GET` / `$_POST` | `Request` | leer entrada no confiable |
| función de validación | validator / `FormRequest` | rechazar datos inválidos |
| sesión y comprobaciones | sesión, guard y policies | autenticar y autorizar |
| repositorio PDO | query builder / Eloquent | persistir sin mezclar capas |
| `try/catch` y respuesta | exception handler | producir errores coherentes |
| construcción manual | service container | resolver dependencias |

## Actividad de cierre

Elegí un flujo de GTask —por ejemplo crear una tarea— y trazá:

1. entrada HTTP;
2. validación;
3. autenticación y autorización;
4. caso de uso;
5. persistencia;
6. respuesta;
7. prueba equivalente.

Después localizá cada responsabilidad en Laravel 12. La comparación se evalúa por lo que entendés, no por la cantidad de código que el framework oculta.

## Continuación

La implementación Laravel pertenece a UD5. Esta unidad termina cuando podés explicar el flujo completo sin atribuírselo a “magia del framework”.
