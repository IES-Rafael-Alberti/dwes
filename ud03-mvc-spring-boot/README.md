# UD3 — Aplicaciones MVC con Spring Boot

Esta unidad enseña a construir aplicaciones web renderizadas en el servidor con Spring Boot 4, Spring MVC y Thymeleaf. El recorrido avanza desde el ciclo petición-respuesta hasta un Gestor de tareas con sesión y autorización por propietario.

## Objetivos

Al terminar la unidad, el alumnado podrá:

- explicar las responsabilidades de modelo, vista y controlador;
- mapear peticiones HTTP y preparar datos para una vista;
- crear plantillas Thymeleaf con formularios y salida escapada;
- validar la entrada del usuario y aplicar Post/Redirect/Get;
- separar formulario, caso de uso y entidad persistente;
- proteger operaciones mediante CSRF, sesión y autorización por propietario;
- probar y documentar el comportamiento MVC.

## Prerrequisitos

- Java 25 y fundamentos de programación orientada a objetos.
- HTTP: métodos, rutas, parámetros, estados y ciclo petición-respuesta.
- Spring Boot: inyección de dependencias, capas y pruebas básicas.
- Persistencia JPA básica para la segunda mitad del recorrido.

## Resultados de aprendizaje y criterios de evaluación

| RA | Criterios trabajados | Evidencia principal |
| --- | --- | --- |
| RA4 | a, b, d, e, f | Sesión, autenticación y pruebas del Gestor |
| RA5 | a–h | Separación MVC, formularios, configuración, estado, diseño, pruebas y documentación |
| RA6 | a–g | Consulta, alta, actualización y borrado con JPA manteniendo integridad |
| RA8 | a, c–g | Vistas dinámicas, interacción y validación de formularios con Thymeleaf |

La redacción completa de los RA y CE se conserva en el documento normativo de planificación `00-planificacion/DAW2o-RA_CE-2025-2026.md`, que no forma parte del sitio público.

## Secuencia didáctica

| Paso | Contenido | Evidencia |
| --- | --- | --- |
| 1 | [Fundamentos de MVC](01-documentacion/01-fundamentos-mvc.md) | Explicar el recorrido de una petición |
| 2 | [Controladores y vistas](01-documentacion/02-controladores-y-vistas.md) | Renderizar una vista con datos del modelo |
| 3 | [Thymeleaf](01-documentacion/03-thymeleaf.md) | Mostrar, iterar y enlazar sin desactivar el escape |
| 4 | [Formularios, validación y PRG](01-documentacion/04-formularios-validacion-prg.md) | Rechazar entrada inválida y evitar reenvíos |
| 5 | [Productos incremental](03-ejercicios/TareaSpringMVC-Productos/TareaSpringMVC-Productos.md) | Completar los checkpoints con TDD |
| 6 | [Persistencia](01-documentacion/05-persistencia.md) | Separar form object, servicio, repositorio y entidad |
| 7 | [Gestor de tareas seguro](01-documentacion/06-gestor-tareas-seguro.md) | Verificar sesión, CSRF y propiedad del recurso |

## Mapa de materiales

- `01-documentacion/`: fuentes Markdown canónicas en el orden del recorrido.
- `02-ejemplos/SpringMVC/`: Gestor de tareas integrador con Gradle Wrapper.
- `03-ejercicios/`: enunciado incremental y starter público de Productos.
- `06-seguridad/`: guía transversal de seguridad de la unidad.

Los cuestionarios, soluciones y guías docentes permanecen fuera de la publicación. Mustache aparece únicamente como comparación sintáctica opcional en la guía de Thymeleaf.

## Fuera de alcance

Registro público, recuperación de contraseña, roles, búsqueda, filtros, paginación, AJAX y API REST no forman parte del núcleo de UD3.
