# Seguridad transversal en UD4

## Regla de trabajo

La seguridad forma parte de cada incremento y de sus pruebas. «Funciona» no significa «acepta cualquier entrada y ejecuta SQL».

| Control | Contrato mínimo | Estado en la reforma |
|---|---|---|
| Validación | esquema, longitud, tipo y regla de negocio en servidor | Notes y GTask |
| Escape/XSS | salida escapada según contexto | Notes y GTask |
| SQL injection | PDO preparado, sin concatenar valores | Notes y GTask |
| Sesiones | cookies `HttpOnly`, `Secure`, `SameSite`; regeneración al autenticar | Política y servicio GTask |
| CSRF | token en toda operación con cambio de estado | Notes y GTask |
| Subida de archivos | límites, tipo detectado en servidor, nombre generado y almacenamiento no ejecutable | Pendiente de práctica y GTask |
| Autorización | consulta y mutación limitadas por propietario/rol | Repositorio acotado por propietario y prueba GTask |
| Contraseñas | `password_hash`/`password_verify`, sin texto claro ni hash rápido | Repositorio y prueba GTask |

## Progresión de pruebas

1. Rechazar entradas ausentes, demasiado largas o con tipo incorrecto.
2. Demostrar que una carga XSS se representa como texto.
3. Demostrar que una carga SQL no altera la consulta.
4. Rechazar sesión ausente y regenerar el ID tras login.
5. Rechazar POST sin token CSRF o con token incorrecto.
6. Rechazar extensión, MIME o tamaño no permitidos y generar el nombre en servidor.
7. Impedir lectura, cambio y borrado de recursos de otro usuario.

El token CSRF se obtiene de `random_bytes`, se guarda en sesión y se compara con `hash_equals`; una igualdad débil o directa no es el contrato aceptado.

## Límites del núcleo

La [práctica procedural Notes](../03-ejercicios/notas-procedural-php84/README.md) demuestra primero validación, PDO preparado, CSRF, PRG y escape sin añadir autenticación. GTask incorpora después sesiones, contraseñas, propiedad y capas. La subida de archivos no forma parte de ninguno de los dos recorridos; búsqueda, filtros, roles y API JSON son ampliaciones, no requisitos del proyecto base.

El GTask heredado continúa como fuente histórica local y no certifica estos controles. El proyecto canónico es [`04-proyectos/gtask-php84`](../04-proyectos/gtask-php84/README.md).
