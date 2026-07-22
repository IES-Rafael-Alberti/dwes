# Inventario de reforma de UD5

## Decisión

UD5 se reforma como **Laravel 12 API-first**. Laravel Sail es el entorno oficial para aislar PHP, Composer, extensiones y servicios. El ejemplo conductor es la API de recetas en `02-ejemplos/sail/Laravel12-api/recetas2-api-laravel12/`.

## Clasificación del material recibido

| Grupo | Diagnóstico | Destino |
|---|---|---|
| API de Recetas Laravel 12 | Contiene Sail, PostgreSQL, Redis, Sanctum, Policies, Resources, servicios y pruebas | Ejemplo canónico validado con Sail |
| Guía larga de Recetas | Contenido útil pero monolítico y con repeticiones | Fuente de consolidación para el recorrido canónico |
| Laravel 10 y 11 | Ejemplos de versiones anteriores, algunos duplicados | Referencia histórica, fuera del itinerario obligatorio |
| Blog Blade y cliente Vue | Tecnologías válidas pero no alineadas con la ruta API-first | Complemento histórico, no evaluable en UD5 |
| `90-archivo/laravel-24_25` | Copia histórica con derivados y dependencias instaladas | Archivo local ignorado, fuera del recorrido y de GitHub Pages |
| Proyecto heredado | Enunciado Laravel 10, contenido ajeno y repositorio anidado | Reescrito como proyecto Laravel 12 API-first |
| `.env`, logs y exportaciones locales | Riesgo de secretos, rutas personales y datos de ejecución | Nunca publicar; sustituir por `.env.example` y documentación reproducible |

## Riesgos comprobados

- La unidad no tenía README, secuencia, contrato de entorno ni entrada Laravel 12 inequívoca.
- Los repositorios anidados no forman una topología de submódulos reproducible; no se actualizan automáticamente durante esta reforma.
- Laravel 10/11 y Laravel 12 coexistían como rutas aparentemente equivalentes.
- Un `.env` o un log dentro de un repositorio anidado no se vuelve seguro por estar excluido de la navegación: se revisa y sanea en su propio repositorio antes de publicarlo.

## Plan por prioridad

### P0 - recorrido y contención

- [x] Fijar Laravel 12 API-first y Sail como baseline.
- [x] Declarar la API de Recetas como ejemplo canónico.
- [x] Retirar el `.env` versionado del cliente Laravel 10, sustituirlo por `.env.example` y reescribir su historial remoto en `laravel-vue-client-historical`; cualquier secreto que hubiera estado expuesto debe rotarse antes de reutilizarlo.
- [x] Retirar de la ruta pública ZIP y `.Rhistory`; Sail ignora logs, bases SQLite, dependencias y artefactos generados. Los logs locales existentes no se publican ni se eliminan automáticamente para no destruir diagnósticos docentes.
- [x] Eliminar del historial de `laravel10-api` las colecciones Insomnia con rutas personales y añadir reglas de ignorado para impedir su reintroducción.
- [x] Registrar los commits y cambios locales de cada repositorio anidado antes de cualquier consolidación.

### Estado de repositorios anidados (22 julio 2026)

| Repositorio | HEAD remoto/local | Cambios locales preservados | Decisión |
|---|---|---|---|
| `Laravel10-api/laravel-api10` | `c5443a4` | controladores, rutas, seeders y reglas de ignorado | Histórico saneado; no consolidar ni migrar. |
| `Laravel10-api/laravel-client` | `ff63290` | `002-ClienteVue_Laravel10-api.org` | Histórico saneado; no consolidar ni migrar. |
| `Laravel12-api/recetas2-api-laravel12` | `5fb533a` | README, controladores, requests, tests y documentación | Ejemplo canónico; estos cambios se validan y se versionan en su propio repositorio. |
| `03-ejercicios/Proyecto` | `7e9933a` | enunciado Markdown, README y HTML/PDF heredados | No publicar HTML/PDF; el enunciado Markdown es la fuente pública. |

### P1 - ejemplo canónico reproducible

- [x] Ejecutar la suite y corregir sus contratos hasta obtener una línea base verde: 26 pruebas y 58 aserciones con Sail/PHP 8.4.
- [x] Sustituir el README genérico del proyecto por puesta en marcha, reset, pruebas, contrato y diagnóstico con Sail.
- [x] Unificar las respuestas de la API mediante Resources y documentar el contrato de recursos individuales y listas.
- [x] Enlazar la guía monolítica desde una ruta de siete incrementos docentes verificables.

### P2 - evaluación y limpieza curricular

- [x] Reescribir el proyecto de UD5 para Laravel 12, Sanctum, Policies, Form Requests, Resources y pruebas.
- [x] Crear seguridad transversal y definir qué evidencias se evalúan.
- [x] Mover Laravel 10/11, Blade y Vue a una referencia histórica explícita.
- [x] Mantener rúbrica y verificación docente de la extensión de Recetas en `99-profesor/` local e ignorado.
