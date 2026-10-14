# Ejercicio 01 — Mini Spring Boot Tasks

## Objetivo

Desarrollar en grupo una API REST pequeña con Spring Boot y JPA, seguir su
historial Git y dejar todo el trabajo y las evidencias de esta entrega dentro de
un único repositorio de grupo.

## Repositorio de grupo

Con 27 alumnos, la clase se organizará en **seis grupos de cuatro y uno de tres**.
El profesorado comunicará la composición de los equipos. Cada grupo usará el
[repositorio plantilla Mini Spring Boot Tasks](https://github.com/IES-Rafael-Alberti/dwes-template-ud2a-mini-spring-boot-tasks):

1. Una persona del grupo hace **Fork** del repositorio en su cuenta de GitHub.
   Usa **Fork**, no «Use this template», para conservar commits y etiquetas.
2. Añade al resto del grupo como colaboradores y clona el fork del grupo.
3. Trabaja en la rama `main`, que parte de la versión `v4`; las etiquetas `v1`,
   `v2`, `v3`, `v4` y `v4-extras` permiten consultar la evolución.
4. Cada integrante realiza y sube sus commits con su propia cuenta. No compartáis
   credenciales.

No ejecutes `setup-history.sh`: el historial está ya en el repositorio que has
forkeado. El proyecto se trabaja desde su raíz; no hay un repositorio Git
anidado.

## Trabajo del grupo

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
- `equipo.md`: integrantes, usuarios GitHub y contribuciones.
- `declaracion-uso-ia.md`: cumplimentada tanto si se utilizó IA como si no.
- Historial de commits y pruebas reproducibles del slice.

Entrega en la tarea de Moodle **solo la URL del fork del grupo**. Moodle sirve
para localizarlo; no se entregan capturas ni documentos evaluables por separado.

## Evaluación

La práctica representa el **15 % de UD2a**. Los criterios y la relación con RA/CE
están disponibles también dentro del repositorio plantilla, en `rubrica.md` y
`ra-ce.md`. Se valoran el historial y la contribución de cada integrante, la
separación por capas, las pruebas del slice y la declaración de uso de IA.

## Política de IA

Se permite utilizar IA como asistente para comprender errores o tests, pero no
limitarse a copiar etapas del historial. Registra el uso material en
`declaracion-uso-ia.md`; cada integrante debe comprender sus cambios y commits.

## Relación con Battleship

Mini Tasks aplica en un contexto pequeño los contenidos de las primeras sesiones
de Battleship: H2/JPA, controladores, capas, DTOs y pruebas. Battleship sigue
siendo el proyecto conductor y no se entrega como parte de esta práctica.
