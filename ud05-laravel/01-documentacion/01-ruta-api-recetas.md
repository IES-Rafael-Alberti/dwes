# Ruta de construcción: API de Recetas

La API de Recetas es el ejemplo guiado de UD5. No se lee como una aplicación terminada: se reconstruye por incrementos, comprobando cada contrato con Sail antes de continuar.

## Secuencia

| Incremento | Pregunta que responde | Código y guía |
|---|---|---|
| 1. Arranque | ¿Qué registra Laravel antes de atender una petición? | [Bootstrap](../02-ejemplos/sail/Laravel12-api/recetas2-api-laravel12/docs/01_bootstrap_app.md) |
| 2. Contrato HTTP | ¿Qué ruta existe, qué middleware la protege y qué estado devuelve? | [Rutas API](../02-ejemplos/sail/Laravel12-api/recetas2-api-laravel12/docs/02_rutas_api.md) |
| 3. Entrada y salida | ¿Cómo se validan datos y cómo se mantiene estable el JSON? | [Controladores](../02-ejemplos/sail/Laravel12-api/recetas2-api-laravel12/docs/03_controladores.md) |
| 4. Dominio | ¿Dónde viven el modelo, las reglas de propietario y la lógica de negocio? | [Modelo, Policies y servicios](../02-ejemplos/sail/Laravel12-api/recetas2-api-laravel12/docs/04_modelos_policies_servicios.md) |
| 5. Datos | ¿Cómo evoluciona el esquema y cómo se crean datos reproducibles? | [Migraciones y factories](../02-ejemplos/sail/Laravel12-api/recetas2-api-laravel12/docs/05_base_de_datos.md) |
| 6. Evidencia | ¿Qué demuestra que el contrato, la seguridad y las reglas no se rompen? | [Tests](../02-ejemplos/sail/Laravel12-api/recetas2-api-laravel12/docs/06_tests.md) |
| 7. Acceso | ¿Cómo se combinan tokens Sanctum, roles y Policies? | [Roles y permisos](../02-ejemplos/sail/Laravel12-api/recetas2-api-laravel12/docs/07_roles_permisos.md) |

## Regla de progreso

Para cada incremento: escribe o ejecuta primero una prueba que exprese el comportamiento, impleméntalo hasta verde y refactoriza solo si la suite permanece verde. Una respuesta JSON manualmente correcta no sustituye una prueba Feature.

## Material de consulta

La [guía extensa de Recetas](../02-ejemplos/sail/Laravel12-api/003-Laravel12-API_REST-Recetas.md) se conserva como ampliación y resolución de problemas. No es el orden obligatorio de lectura: su contenido se consulta después de completar cada incremento de esta ruta.
