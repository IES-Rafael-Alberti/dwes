# Mini Spring Boot — Git Lesson Kit

Este kit crea un repositorio Git con **commits pedagógicos** por etapas:
- v1: Controlador sin ResponseEntity (memoria)
- v2: Refactor a ResponseEntity (memoria)
- v3: JPA + Repositorio + Controller V3 + ApiExceptionHandler
- v4: Servicio + Controller V4 (CRUD)
- v4-extras: ETag/Cache-Control, paginación (Link/X-Total-Count), búsqueda `q`, PUT y HEAD

## Uso
```bash
unzip mini-spring-boot-tasks-git-lesson.zip
cd mini-spring-boot-tasks-git-lesson
bash setup-history.sh
# ahora tienes commits y tags (v1, v2, v3, v4, v4-extras)
cd project
mvn spring-boot:run
```
