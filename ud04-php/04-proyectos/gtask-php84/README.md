# GTask — proyecto conductor incremental

Construye un gestor de tareas seguro en PHP 8.4 sin framework. El repositorio público contiene el arranque, los contratos y los checkpoints, pero no la implementación final.

## Camino rápido

```bash
composer install
composer test
composer checkpoint -- --group=checkpoint-1
```

`composer test` verifica el núcleo entregado. Cada checkpoint empieza en **RED** y solo se activa cuando se aborda esa etapa.

## Incrementos

| Etapa | Demo docente | Trabajo del alumnado | RED / criterio observable |
|---|---|---|---|
| 0 | Composer, PSR-4 y SQLite temporal | levantar el entorno | `composer test` termina en verde |
| 1 | objeto de entrada y errores por campo | validar antes del caso de uso | `--group=checkpoint-1`: entrada inválida no llega a creación |
| 2 | registro, hash y sesión | definir el puerto de autenticación | `--group=checkpoint-2`: login no depende directamente de PDO |
| 3 | token generado en sesión | proteger todas las mutaciones | `--group=checkpoint-3`: token ausente es rechazado |
| 4 | repositorio PDO preparado y propietario | completar CRUD acotado | `--group=checkpoint-4`: lookup exige `id` y `ownerId` |
| 5 | front controller, PRG y vistas | conectar rutas y escapar salida | `--group=checkpoint-5`: falta el front controller público |

## Arquitectura objetivo

```text
public/index.php        front controller y rutas explícitas
src/Http/               entrada, sesión, CSRF y respuesta
src/Application/        casos de uso
src/Domain/             modelos y contratos de repositorio
src/Infrastructure/     PDO y configuración
templates/              vistas PHP sin decisiones de negocio
tests/                  unidad e integración
```

La entrega debe mantener esa dirección de dependencias: HTTP y PDO dependen del núcleo, no al revés.

## Contratos no negociables

- PHP 8.4, `declare(strict_types=1)` y autoload PSR-4.
- PDO preparado; sin interpolar entrada en SQL.
- validación en servidor y salida con `htmlspecialchars`.
- autenticación con `password_hash` y `password_verify`.
- sesión regenerada tras login y cookie configurable `HttpOnly`, `Secure`, `SameSite`.
- CSRF de sesión, `random_bytes` y `hash_equals` en cada mutación.
- toda operación de tarea incluye el identificador del propietario.
- el cliente nunca recibe trazas, SQL ni detalles internos.

## Entrega

Incluye tests propios, una breve decisión de arquitectura y evidencia RED → GREEN → REFACTOR por etapa. Búsqueda, filtros, roles, API JSON y subida de archivos quedan fuera del núcleo.
