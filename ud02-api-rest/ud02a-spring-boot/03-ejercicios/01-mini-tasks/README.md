# Ejercicio 01 — Mini Spring Boot Tasks (Kit de Aprendizaje con Git)

## Objetivo

Familiarizarse de forma guiada y progresiva con el diseño y refactorización de APIs REST en Spring Boot, utilizando la historia de Git como hilo conductor (commits y tags pedagógicas).

## Contenidos y Recursos

Este ejercicio cuenta con dos recursos en `recursos/`:
- `mini-spring-boot-tasks-completo/`: El proyecto final con todas las etapas ya integradas y perfiles de test listos.
- `mini-spring-boot-tasks-git-lesson/`: Un kit con un script (`setup-history.sh`) para recrear un repositorio local paso a paso.

## Tareas

### 1. Recrear el histórico pedagógico (Git Lesson)

Abre tu terminal, entra en la carpeta `recursos/mini-spring-boot-tasks-git-lesson` y ejecuta el script de arranque:

```bash
bash setup-history.sh
```

Esto creará una subcarpeta `project/` con un repositorio Git listo. Navega por las diferentes versiones e inspecciona cómo evoluciona la arquitectura:

1. **Tag `v1`**: Un controlador básico en memoria.
2. **Tag `v2`**: Introducción de `ResponseEntity` para un control fino de las respuestas.
3. **Tag `v3`**: Integración de base de datos H2 con JPA + Repositorio y manejo de errores con `ApiExceptionHandler`.
4. **Tag `v4`**: Introducción de la capa de servicios (`TaskService`) para encapsular la lógica de negocio y uso de DTOs (`CreateTaskDTO`, `UpdateTaskDTO`).
5. **Tag `v4-extras`**: Paginación, caché (ETag/Cache-Control), parámetros de búsqueda y métodos HEAD.

### 2. Ejecutar y testear cada etapa

Utiliza Maven para verificar las etapas:
- Corre el servidor en local: `mvn spring-boot:run`
- Utiliza el archivo `requests.http` provisto en IntelliJ o VS Code para probar las llamadas de forma manual.
- Lanza los tests asociados a cada fase utilizando los perfiles Maven del `pom.xml`:
  ```bash
  mvn -q -Ptests-basic test
  mvn -q -Ptests-extras test
  ```

## Entregables

- Documento `aprendizaje.md` donde expliques en tus propias palabras qué mejoras se introducen en la arquitectura al pasar de `v1` → `v2`, `v2` → `v3` y `v3` → `v4`.
- Captura de pantalla de la ejecución exitosa de los tests (`mvn test -Pall-tests`).
- Declaración de uso de IA cumplimentada (ver plantilla en `00-recursos-comunes/plantillas/`).

## Política de IA

| Aspecto | |
|---------|-|
| Uso de IA permitido | Sí, como asistente para explicar errores de compilación o comprender la lógica de los tests unitarios. |
| Declaración obligatoria | Sí. |
| Herramientas permitidas | ChatGPT, Claude, Gemini, GitHub Copilot. |
| Qué NO está permitido | El código ya está implementado; no está permitido limitarse a copiar el código de las tags sin realizar las reflexiones arquitectónicas requeridas. |

## Evaluación

Ver `rubrica.md` y `ra-ce.md` en este directorio.

## Relación con Battleship

Este ejercicio complementa las sesiones de Battleship sobre TDD,
controladores, capas y test slicing. Consulta el
[recorrido canónico de Battleship](../../02-ejemplos/battleship/docs/README.md)
antes de analizar las etapas. Battleship es el proyecto conductor; aquí
practicas la lectura de esa evolución en un contexto más pequeño.
