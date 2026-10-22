# Ejercicio 01 — Mini Spring Boot Tasks

## Objetivo

Desarrollar individualmente una API REST pequeña con Spring Boot y JPA, seguir su
historial Git y dejar el código, las pruebas y las evidencias en un repositorio
propio. Mini Tasks es la tarea individual que acompaña al code-along Battleship;
Book Catalog y Gestión de eventos se realizarán después y en grupo.

## Repositorio individual

Cada estudiante creará desde cero un repositorio propio en GitHub para Mini
Tasks, sin fork, plantilla, historial ni etiquetas heredadas. Inicializa el
proyecto y el repositorio Git local, trabaja en `main` y publica tus commits en
tu repositorio propio. El proyecto se trabaja desde su raíz; no hay un
repositorio Git anidado.

## Trabajo individual y avances por sesión

El profesor indicará al final de cada sesión de Battleship el avance de Mini
Tasks que corresponde. No hay que terminar toda la práctica de una vez.

En la **sesión 3**, lee los controladores V1/V2, prueba sus contratos HTTP y
añade con TDD `GET /v1/tasks/{id}` (existente y `404`). Las rutas de la actividad
son `/v1/tasks` y `/v2/tasks`, y el campo se llama `done`. El detalle está al final
de la [sesión 3 de Battleship](../../02-ejemplos/battleship/docs/03-controladores-rest.md).
Este primer avance en memoria no sustituye al slice JPA de cierre descrito abajo.

Sobre `main`, implementa una **funcionalidad vertical pequeña** (*slice*): un
cambio funcional completo de extremo a extremo, no una capa aislada. Por ejemplo,
un endpoint para filtrar tareas debe conectar petición HTTP, controlador,
servicio, repositorio y respuesta, con una prueba que compruebe el comportamiento.

El slice adicional debe incluir una operación acotada de consulta o modificación
con controlador, servicio, repositorio JPA, DTO cuando corresponda y prueba
automatizada. Devuelve respuestas HTTP coherentes e incluye el caso de recurso
inexistente. No se pide todavía una migración versionada ni OpenAPI.

Ejecuta las pruebas con:

```bash
mvn test
```

## Evidencias dentro del repositorio

Completa y sube estos ficheros junto con el código y los tests:

- `aprendizaje.md`: cambios entre versiones, decisiones y responsabilidades de
  las capas.
- `autoria.md`: nombre, usuario GitHub y punto de partida de tu trabajo.
- `declaracion-uso-ia.md`: cumplimentada tanto si se utilizó IA como si no.
- Historial de commits y pruebas reproducibles del slice.

Entrega en la tarea de Moodle **solo la URL de tu repositorio propio**. Moodle
sirve para localizarlo; no se entregan capturas ni documentos evaluables por
separado.

## Evaluación

La práctica representa el **15 % de UD2a**. Los criterios y la relación con RA/CE
están disponibles en `rubrica.md` y `ra-ce.md` de este directorio (modalidad
individual de esta edición). Se valoran tu historial y tus aportaciones, la
separación por capas, las pruebas del slice y la declaración de uso de IA.

## Política de IA

Se permite utilizar IA como asistente para comprender errores o tests, pero no
limitarse a copiar etapas del historial. Registra el uso material en
`declaracion-uso-ia.md`; debes comprender tus cambios y commits.

## Relación con Battleship

Mini Tasks aplica en un contexto pequeño los contenidos de las primeras sesiones
de Battleship: H2/JPA, controladores, capas, DTOs y pruebas. Battleship sigue
siendo el proyecto conductor y no se entrega como parte de esta práctica.
