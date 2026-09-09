# Evaluación de UD1

Las adaptaciones de acceso y ritmo se aplican conforme al protocolo transversal
docente. No modifican RA/CE ni ponderaciones.

Para el curso 2026/2027, con un grupo de 30 alumnos, UD1 se evalúa mediante una práctica cooperativa pequeña y cuestionarios individuales. Los enunciados individuales actuales se conservan sin sobrescribir para recuperación, grupos pequeños o cursos futuros. Ninguna actividad aislada sustituye a las demás: observar HTTP, modificar código servidor y razonar conceptos son capacidades relacionadas, pero distintas.

## Ponderación

| Instrumento | Peso | Evidencia principal |
|---|---:|---|
| Práctica cooperativa HTTP + Hello Server | 65 % | Mensajes HTTP, endpoint, entorno reproducible, TDD y seguridad básica |
| Cuestionarios Moodle individuales | 35 % | Comprensión individual de RA1.a-g y semántica HTTP básica |

El hito individual 1 se realiza al final de UD2a y se registra como evidencia de
acreditación individual, no como una tercera calificación de UD1.

## Modalidad 2026/2027

El enunciado operativo está en
[`03-ejercicios/03-practica-cooperativa-2026/README.md`](03-ejercicios/03-practica-cooperativa-2026/README.md).

El laboratorio y la extensión se tratarán como una única práctica cooperativa de 3-4 alumnos. El grupo entregará un informe común y un repositorio con:

- una traza HTTP analizada;
- un endpoint ejecutable;
- una prueba escrita antes de la implementación;
- una comprobación de seguridad o error;
- responsabilidades identificables por integrante.

Cada integrante debe explicar su slice en la defensa breve. Si no se desarrolla cliente, la llamada se demostrará con Insomnia, Postman u otro cliente REST.

## Cobertura

| CE | Laboratorio | Extensión | Cuestionario |
|---|:---:|:---:|:---:|
| RA1.a | Principal | Apoyo | Comprobación |
| RA1.b | Principal | Apoyo | Comprobación |
| RA1.c | Principal | Apoyo | Comprobación |
| RA1.d | Principal | Apoyo | Comprobación |
| RA1.e | Principal | Contexto | Comprobación |
| RA1.f | Evidencia | Principal | Comprobación |
| RA1.g | Principal | Evidencia | Comprobación |

## Condiciones comunes

- Práctica cooperativa en grupos de 3-4 para 2026/2027; los cuestionarios y el hito individual son individuales.
- Sin IA generativa durante UD1.
- El alumnado debe poder reproducir y explicar su evidencia.
- Todos los integrantes han entregado el enlace al repositorio y al issue.
- La defensa registra una pregunta y una evidencia por integrante.
- No se entregan secretos, cookies, tokens, rutas personales ni capturas innecesarias.
- Una prueba verde sin comprensión no demuestra por sí sola el criterio.
- Las adaptaciones de acceso cambian el medio, no el resultado de aprendizaje evaluado.
- En cuestionarios o tareas cronometradas se respetará el tiempo individual
  acordado; el 50 % adicional es solo una referencia inicial revisable.

## Estado del cuestionario

El banco GIFT se conserva localmente en `05-cuestionarios/` y no se publica en Git ni MkDocs. Está revisado para cubrir RA1.a-g, pero solo se considerará validado después de importarlo y previsualizarlo en Moodle.
