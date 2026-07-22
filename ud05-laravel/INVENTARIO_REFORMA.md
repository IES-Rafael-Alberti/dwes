# Inventario de reforma de UD5

## Decisión

UD5 se reforma como **Laravel 12 API-first**. Laravel Sail es el entorno oficial para aislar PHP, Composer, extensiones y servicios. El ejemplo conductor es la API de recetas en `02-ejemplos/sail/Laravel12-api/recetas2-api-laravel12/`.

## Clasificación del material recibido

| Grupo | Diagnóstico | Destino |
|---|---|---|
| API de Recetas Laravel 12 | Contiene Sail, PostgreSQL, Redis, Sanctum, Policies, Resources, servicios y pruebas | Ejemplo canónico validado con Sail |
| Guía larga de Recetas | Contenido útil pero monolítico y con repeticiones | Fuente de consolidación para el recorrido canónico |
| Laravel 10 | Permite comparar la estructura anterior a los cambios introducidos en Laravel 11 y consolidados en Laravel 12 | Conservar un único proyecto comparativo, fuera del itinerario evaluable |
| Laravel 11 | Ejemplos intermedios y parcialmente duplicados por Laravel 12 | No conservar como versión docente; eliminar o migrar a Laravel 12 solo contenido único |
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
| `Laravel12-api/recetas2-api-laravel12` | `747f030` | Ninguno | Ejemplo canónico validado y versionado en su propio repositorio. |
| `03-ejercicios/Proyecto` | `origin/main: 7e9933a`; local: `dba131f` | HTML/PDF heredados | El enunciado Markdown y README están listos; no publicar HTML/PDF. |

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

### P3 - revisión de material local e histórico

Esta revisión no bloquea la impartición de UD5, pero debe resolverse antes de considerar terminada su limpieza de repositorios:

- [ ] **Laravel 10 comparativo**: revisar `Laravel10-api/laravel-api10` y conservar un único proyecto funcional que muestre cómo se estructuraba Laravel antes de la simplificación iniciada en Laravel 11 y acentuada en Laravel 12. Evaluar los cambios locales de controladores, rutas, seeder y reglas de ignorado; no migrar este proyecto a Laravel 12 porque su valor es precisamente el contraste.
- [ ] **Cliente Laravel 10/Vue**: revisar el cambio local de `Laravel10-api/laravel-client/002-ClienteVue_Laravel10-api.org`. Conservarlo solo si aporta valor al contraste histórico o al consumo de la API; no forma parte del recorrido evaluable.
- [ ] **Ejemplos Laravel 11**: comparar `Laravel11-api/laravel11-api-old` y `Laravel11-api/laravel11-api` con los ejemplos canónicos de Laravel 12. Eliminar los duplicados. Si contienen una técnica necesaria que no esté cubierta, trasladarla a un ejemplo Laravel 12 y retirar la etiqueta y el proyecto Laravel 11.
- [ ] **Plantillas de entorno**: revisar los `.env.example` no versionados del Blog Laravel 10, los dos ejemplos Laravel 11 y `recetas-api-laravel12`. Solo versionar plantillas reproducibles, sin secretos, rutas personales ni valores locales; descartar las de proyectos que se eliminen.
- [ ] **Derivados del proyecto**: decidir si eliminar `001-Proyecto_DWES_Cliente-Servidor.html` y `001-Proyecto_DWES_Cliente-Servidor.pdf`. El Markdown publicado continúa siendo la única fuente canónica.

#### Criterio de aceptación

- Laravel 12 sigue siendo la única ruta obligatoria y evaluable.
- Laravel 10 conserva exactamente un caso comparativo intencional y claramente rotulado como histórico.
- No queda ningún ejemplo presentado como Laravel 11.
- Cualquier contenido rescatado de Laravel 11 se valida y publica como Laravel 12 únicamente si cubre algo ausente en los ejemplos actuales.
- No se publican derivados ni plantillas de entorno sin revisar.
