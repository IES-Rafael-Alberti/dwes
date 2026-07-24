# GTask — proyecto conductor de UD4

GTask se convertirá en un proyecto incremental con PHP 8.4, TDD y seguridad. En este corte solo se fija su función dentro de la unidad; **la implementación existente aún no es una solución docente validada**.

## Secuencia prevista

1. Arranque reproducible y prueba de salud.
2. Enrutado y respuestas HTTP.
3. Validación y errores coherentes.
4. Persistencia con PDO preparado.
5. Autenticación y sesiones seguras.
6. CRUD de tareas limitado por propietario.
7. CSRF y endurecimiento de cookies.
8. Refactorización hacia casos de uso y repositorios.

## Restricción del siguiente corte

`GTask/` es un repositorio Git anidado y contiene cambios locales en `SEGUIMIENTO.md` y un `.env` no versionado. Deben preservarse. Antes de escribir código habrá que inventariar el diff interno y acordar checkpoints sin limpiar ni restaurar esos archivos.

Consultá también el [contrato de seguridad](../../../06-seguridad/README.md).
