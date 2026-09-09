# Práctica cooperativa — {NOMBRE}

## Objetivo

{Competencia técnica y resultado observable.}

## Alcance

- Stack y versión: {Spring Boot/Laravel/...}
- Repositorio plantilla: `{URL}`
- Fecha/hito: `{fecha}`
- Grupo: 3-4 personas

## Reparto por vertical slices

Cada integrante debe completar un flujo de principio a fin: entrada HTTP/MVC,
validación, caso de uso, persistencia, respuesta/error y pruebas. El reparto por
capas solo se permite para una tarea compartida justificada.

| Integrante | Slice/endpoint | Regla de negocio | Pruebas | Revisor |
|---|---|---|---|---|
| | | | | |

## Entregables

- Repositorio derivado del template.
- `README.md` con integrantes, responsabilidades y ejecución.
- Issue de entrega con checklist y enlace a la release/tag.
- Tests automatizados y resultado reproducible.
- Contrato HTTP/OpenAPI o colección REST, si aplica.

Para una práctica menor, el README e issue son el dossier de autoría: incluyen
la tabla de slices, reglas, pruebas y evidencia. No se exige una especificación,
plan técnico o máquina de estados separados salvo que el enunciado la convierta
en un proyecto de alcance amplio. Consulta
`00-recursos-comunes/plantillas/guia-documentacion-autoria.md`.

## Criterios de aceptación

- [ ] Cada slice funciona y tiene una prueba de éxito.
- [ ] Cada slice tiene al menos un límite o error relevante.
- [ ] Las reglas se ejecutan en servidor, no solo en el cliente.
- [ ] Las pruebas y migraciones se ejecutan desde cero.
- [ ] No hay secretos versionados.

## Defensa individual

Duración total: 5-10 minutos por grupo. Cada integrante responderá una pregunta
directa sobre su slice y podrá recibir una pregunta cruzada sobre una decisión
compartida. La defensa acredita comprensión, no diseño visual.

Si un integrante tiene una adaptación de acceso, se aplicarán las medidas
individuales acordadas: pausas, tiempo de procesamiento, herramientas de acceso
y una forma de respuesta que no convierta la velocidad de tecleo o de ratón en
un criterio no previsto. La responsabilidad técnica y la acreditación individual
se mantienen.

## Entrega Moodle

Todos los integrantes entregan el enlace al mismo repositorio y al issue de
entrega. Si una persona no entrega en Moodle, su evidencia puede no aparecer en
el libro de calificaciones.
