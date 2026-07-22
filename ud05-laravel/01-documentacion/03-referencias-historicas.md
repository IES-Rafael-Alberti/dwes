# Referencias históricas: Laravel 10 y 11

La ruta evaluable de UD5 es Laravel 12 con Sail. Los ejemplos de Laravel 10 y 11 se conservan solo para consultar diferencias de migración o mantener proyectos antiguos; no son una alternativa para iniciar una práctica nueva.

## Qué cambia al leer código anterior

| Tema | Lectura recomendada |
|---|---|
| Configuración de la aplicación | En Laravel 11/12, rutas, middleware y excepciones se concentran en `bootstrap/app.php`. |
| Providers | Revisa `bootstrap/providers.php` en lugar de asumir la estructura de versiones previas. |
| Autorización en controladores | Incluye el trait o usa `Gate::authorize()`/middleware `can:` de forma explícita. |
| Entorno | Mantén Sail; no instales PHP ni Composer globales solo para reproducir una versión antigua. |

La fuente de verdad para sintaxis o comportamiento de una versión concreta es su documentación oficial. No copies configuraciones, paquetes o fragmentos de ejemplos antiguos sin contrastarlos con Laravel 12 y cubrirlos con pruebas.

El cliente Vue histórico se publica como `IES-Rafael-Alberti/laravel-vue-client-historical` y conserva únicamente `.env.example` con su URL local. Si un `.env` anterior contenía tokens o credenciales, esos valores deben considerarse expuestos y rotarse antes de publicar el repositorio histórico. Los clones anteriores a la reescritura del historial deben volver a clonarse: no se reutilizan ni se fuerza su rama antigua sobre el remoto saneado.
