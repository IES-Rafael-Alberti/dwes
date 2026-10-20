# Ejercicio 01 — Mini Spring Boot Tasks

## Objetivo

Desarrollar individualmente una API REST pequeña con Spring Boot y JPA, seguir su
historial Git y dejar el código, las pruebas y las evidencias en un repositorio
propio. Mini Tasks es la tarea individual que acompaña al code-along Battleship;
Book Catalog y Gestión de eventos se realizarán después y en grupo.

## Repositorio individual

Cada estudiante usará el
[repositorio plantilla Mini Spring Boot Tasks](https://github.com/IES-Rafael-Alberti/dwes-template-ud2a-mini-spring-boot-tasks):

1. Haz **Fork** del repositorio en tu cuenta de GitHub.
   Usa **Fork**, no «Use this template», para conservar commits y etiquetas.
2. Clona tu fork individual. Si tenías una copia de grupo, conserva el historial
   y trabaja ahora en una copia propia; identifica desde qué punto empiezan tus
   aportaciones individuales.
3. Trabaja en la rama `main`, que parte de la versión `v4`; las etiquetas `v1`,
   `v2`, `v3`, `v4` y `v4-extras` permiten consultar la evolución.
4. Realiza y sube tus commits con tu propia cuenta.

No ejecutes `setup-history.sh`: el historial está ya en el repositorio que has
forkeado. El proyecto se trabaja desde su raíz; no hay un repositorio Git
anidado.

## Trabajo individual y avances por sesión

El profesor indicará al final de cada sesión de Battleship el avance de Mini
Tasks que corresponde. No hay que terminar toda la práctica de una vez.

En la **sesión 3**, lee los controladores V1/V2, prueba sus contratos HTTP y
añade con TDD `GET /v1/tasks/{id}` (existente y `404`). Las rutas de este starter
son `/v1/tasks` y `/v2/tasks`, y el campo se llama `done`. El detalle está al final
de la [sesión 3 de Battleship](../../02-ejemplos/battleship/docs/03-controladores-rest.md).
Este primer avance en memoria no sustituye al slice JPA de cierre descrito abajo.

Antes de programar, explora los commits y las etiquetas para explicar la
evolución:

```bash
git log --oneline --decorate --graph --all
git tag --list
```

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
- `autoria.md`: nombre, usuario GitHub y punto de partida de tu trabajo; si se
  conservó código de una copia anterior de grupo, identifica su procedencia.
- `declaracion-uso-ia.md`: cumplimentada tanto si se utilizó IA como si no.
- Historial de commits y pruebas reproducibles del slice.

Entrega en la tarea de Moodle **solo la URL de tu fork individual**. Moodle sirve
para localizarlo; no se entregan capturas ni documentos evaluables por separado.

## Evaluación

La práctica representa el **15 % de UD2a**. Los criterios y la relación con RA/CE
están disponibles en `rubrica.md` y `ra-ce.md` de este directorio (modalidad
individual de esta edición). La plantilla remota puede conservar indicaciones
de trabajo en grupo de una versión anterior: para esta entrega prevalece este
enunciado. Se valoran tu historial y tus aportaciones, la
separación por capas, las pruebas del slice y la declaración de uso de IA.

## Política de IA

Se permite utilizar IA como asistente para comprender errores o tests, pero no
limitarse a copiar etapas del historial. Registra el uso material en
`declaracion-uso-ia.md`; debes comprender tus cambios y commits.

## Relación con Battleship

Mini Tasks aplica en un contexto pequeño los contenidos de las primeras sesiones
de Battleship: H2/JPA, controladores, capas, DTOs y pruebas. Battleship sigue
siendo el proyecto conductor y no se entrega como parte de esta práctica.
