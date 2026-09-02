# Práctica cooperativa UD1 — HTTP y Hello Server

## Objetivo

Construir y explicar un flujo mínimo de servidor relacionando mensaje HTTP,
endpoint, prueba TDD, entorno reproducible y una medida de seguridad.

## Organización

Grupos de 3-4 alumnos, con un repositorio creado desde el template docente. El
reparto recomendado es:

| Slice | Resultado mínimo |
|---|---|
| HTTP | Traza de petición/respuesta, método, cabeceras y estados explicados |
| Endpoint y TDD | Endpoint Hello Server, test escrito antes del cambio y ejecución reproducible |
| Seguridad/error | Validación o error controlado y medida de seguridad HTTP justificada |

Cada integrante debe poder seguir su slice desde la petición hasta la respuesta
o error. Las tareas compartidas se registran en el README del repositorio.

## Entrega

- Repositorio derivado del template.
- README con integrantes, slices, comandos y resultado de pruebas.
- Issue de entrega con checklist y tag/release.
- Informe o evidencias de la traza HTTP.
- Colección REST o instrucciones de Insomnia/Postman si no hay cliente propio.
- Declaración de uso de IA según las normas de UD1.

Todos los integrantes deben entregar en Moodle el enlace al mismo repositorio.

## Defensa

En 5-10 minutos por grupo, cada integrante responderá una pregunta sobre su
slice. Se podrá pedir que localice el test, el endpoint, la cabecera o el
tratamiento del error. La defensa acredita comprensión individual.

## Criterios de aceptación

- [ ] La traza diferencia cliente, servidor, petición y respuesta.
- [ ] El endpoint funciona desde un cliente REST.
- [ ] Existe evidencia del ciclo rojo-verde-refactor.
- [ ] Se demuestra un error o límite y su respuesta.
- [ ] No se versionan cookies, tokens ni secretos.
- [ ] El proyecto arranca siguiendo el README.
