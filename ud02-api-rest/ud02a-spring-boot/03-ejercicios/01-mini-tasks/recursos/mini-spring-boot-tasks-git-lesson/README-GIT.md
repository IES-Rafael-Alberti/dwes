# Mini Spring Boot — Git Lesson Kit

Este es el kit requerido por el ejercicio. `setup-history.sh` usa el contenido
de `snapshots/` para reconstruir el repositorio pedagógico; los snapshots no son
entregables ni deben modificarse.

Este kit crea un repositorio Git con **commits pedagógicos** por etapas:
- v1: Controlador sin ResponseEntity (memoria)
- v2: Refactor a ResponseEntity (memoria)
- v3: JPA + Repositorio + Controller V3 + ApiExceptionHandler
- v4: Servicio + Controller V4 (CRUD)
- v4-extras: ETag/Cache-Control, paginación (Link/X-Total-Count), búsqueda `q`, PUT y HEAD

## Uso
```bash
bash setup-history.sh
# ahora tienes commits y tags (v1, v2, v3, v4, v4-extras)
cd project
mvn spring-boot:run
```
