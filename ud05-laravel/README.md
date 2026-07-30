# UD5 - Laravel 12 API-first

Esta unidad lleva el trabajo explícito de UD4 a las abstracciones de Laravel: rutas, middleware, migraciones, Eloquent, validación, autenticación con tokens y autorización por recurso. El recorrido obligatorio construye una API REST; Blade, el blog y el cliente Vue no forman parte de la ruta principal.

## Entorno oficial

**Laravel Sail** es el entorno de desarrollo oficial. Docker ejecuta PHP, Composer, extensiones, PostgreSQL y Redis dentro de contenedores. El sistema anfitrión solo necesita Docker y una terminal.

| Sistema | Requisito |
|---|---|
| Linux | Docker Engine y Compose; el usuario debe poder ejecutar `docker` sin `sudo`. |
| macOS | Docker Desktop iniciado. |
| Windows | Docker Desktop con integración WSL2; todo comando se ejecuta desde la distribución Linux de WSL2, no desde CMD, PowerShell ni Git Bash. |

No se instala PHP, Composer ni extensiones PHP globalmente. Los comandos de la documentación se ejecutan mediante `./vendor/bin/sail`.

## Recorrido recomendado

| Paso | Resultado | Material |
|---|---|---|
| 0 | Diagnosticar Sail y levantar el entorno aislado | [Entorno Sail](01-documentacion/00-entorno-sail.md) |
| 1 | Entender el arranque, rutas y middleware de una API Laravel 12 | [Ruta de Recetas](01-documentacion/01-ruta-api-recetas.md) |
| 2 | Persistir con migraciones, Eloquent, factories y seeders | [Base de datos](02-ejemplos/sail/Laravel12-api/recetas2-api-laravel12/docs/05_base_de_datos.md) |
| 3 | Diseñar endpoints con Form Requests, controladores y API Resources | [Rutas](02-ejemplos/sail/Laravel12-api/recetas2-api-laravel12/docs/02_rutas_api.md) y [controladores](02-ejemplos/sail/Laravel12-api/recetas2-api-laravel12/docs/03_controladores.md) |
| 4 | Autenticar con Sanctum y autorizar con Policies | [Roles y permisos](02-ejemplos/sail/Laravel12-api/recetas2-api-laravel12/docs/07_roles_permisos.md) |
| 5 | Probar el contrato HTTP y la regla de negocio | [Tests](02-ejemplos/sail/Laravel12-api/recetas2-api-laravel12/docs/06_tests.md) |
| 6 | Integrar el dominio en la API de recetas | [Guía de Recetas](02-ejemplos/sail/Laravel12-api/003-Laravel12-API_REST-Recetas.md) |

## Proyecto canónico

`02-ejemplos/sail/Laravel12-api/recetas2-api-laravel12/` es el ejemplo guiado de referencia. Se parte de su API de recetas para estudiar el flujo ruta -> validación -> controlador -> servicio -> policy -> resource -> prueba.

Los proyectos Laravel 10 y 11, el blog Blade y el cliente Vue se conservan como referencia histórica. Su mapa y límites están en [Ejemplos Sail](02-ejemplos/sail/README.md). No son alternativas para empezar la unidad ni material evaluable. Los archivos sensibles, soluciones y evaluaciones docentes permanecen fuera del repositorio público.

## Forma de trabajo

Cada incremento sigue RED -> GREEN -> REFACTOR. Una API no se considera terminada porque responda desde Postman: debe validar entrada en servidor, exponer un contrato JSON consistente, autenticar, autorizar por recurso y estar cubierta por pruebas feature o unitarias.

## Estado de la reforma

- Baseline decidido: Laravel 12 API-first y Sail.
- Ejemplo canónico identificado: API de Recetas Laravel 12.
- Laravel 10/11 y ejemplos Blade/Vue: referencia histórica, fuera de la ruta obligatoria.
- Ejemplo canónico validado con Sail: la suite cubre salud pública, autenticación, CRUD, validación, autorización y reglas de negocio.
- P3 completado: limpieza de derivados generados, movimiento de materiales L11 a su área histórica, actualización de `002-ConsideracionesProyecto.md` y eliminación de scaffold duplicado.
