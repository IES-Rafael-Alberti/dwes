# Plantilla de equipo — práctica cooperativa UD1

Este repositorio es el punto de partida técnico para la práctica. La
[descripción completa, criterios y rúbrica](https://github.com/IES-Rafael-Alberti/dwes/blob/main/ud01-introduccion-entorno-servidor/03-ejercicios/03-practica-cooperativa-2026/README.md)
prevalece si alguna instrucción de este fichero queda desactualizada.

Este repositorio es una plantilla pública. El profesorado crea desde ella un
repositorio privado por equipo y añade a sus integrantes; el equipo trabaja y
entrega en ese repositorio privado, no en la plantilla pública. En GitHub, el
equipo no debe pulsar «Use this template» para trabajar directamente sobre la
plantilla común.

## 1. Completar al crear el repositorio

- **Equipo:** nombre o identificador acordado.
- **Integrantes y usuario GitHub:** completar la tabla.
- **Responsabilidades/slices:** identificar quién se responsabiliza de cada
  evidencia; el trabajo puede revisarse en equipo.

| Integrante | Usuario GitHub | Slice o responsabilidad |
|---|---|---|
| Integrante 1 | `@usuario` | HTTP / traza |
| Integrante 2 | `@usuario` | Endpoint y prueba TDD |
| Integrante 3 | `@usuario` | Seguridad o error controlado |
| Integrante 4 (si procede) | `@usuario` | Integración, documentación y apoyo |

No añadas contraseñas, tokens, cookies reales, datos personales ni rutas
locales del equipo.

## 2. Preparar y comprobar la línea base

Requisitos: JDK 25, Git y acceso inicial a Maven Central. Abre una terminal en
la raíz del repositorio:

```bash
java -version
./mvnw --version
./mvnw test
```

En Windows, ejecuta `mvnw.cmd --version` y `mvnw.cmd test`. La plantilla debe
pasar primero las tres pruebas existentes. En esta fase no se modifica aún la
aplicación: confirma que el entorno y las pruebas de partida funcionan.

## 3. Repartir y construir evidencias

### Slice HTTP

- Capturar una petición y su respuesta con HTTPie o Insomnia/Postman.
- Explicar método, ruta, estado, cabeceras y cuerpo.
- Redactar cualquier cookie, token o valor `Authorization` como
  `<REDACTED>`.

### Slice endpoint y TDD

- Añadir un test para `GET /api/info` **antes** de implementar la ruta.
- El contrato mínimo del endpoint es JSON con `application` igual a
  `hello-server` y `purpose` igual a `observe-server-execution`.
- Ejecutar el test nuevo: debe fallar porque la ruta aún no está implementada,
  no por un error de compilación.
- Añadir el cambio mínimo y volver a ejecutar `./mvnw test`.
- Conservar evidencia textual breve del paso rojo y del paso verde.

### Slice seguridad/error

- Probar una ruta inexistente o documentar un error/límite controlado.
- Justificar una medida HTTP relacionada con la respuesta o la traza.
- No afirmar que el starter implemente autenticación, roles o una API REST
  completa.

## 4. Trabajo en Git y revisión

1. Crear issues pequeños para los slices y asignar responsables.
2. Trabajar en ramas o commits que permitan identificar las contribuciones.
3. Revisar cambios de otros integrantes antes de integrar.
4. Usar el issue de entrega como checklist y cerrar solo lo que esté demostrado.

Se incluyen formularios iniciales de issue en `.github/ISSUE_TEMPLATE/`.
Etiquetas sugeridas para organizar el trabajo: `hito`, `pruebas`, `seguridad`
y `documentacion`.

## 5. Entrega y defensa

- README del equipo actualizado con integrantes, slices, comandos y resultado
  de las pruebas.
- Issue de entrega con enlaces y checklist completado.
- Traza HTTP y cualquier colección/instrucción de cliente necesaria.
- Cada integrante entrega **individualmente en Moodle el enlace al mismo
  repositorio** y puede explicar su slice en la defensa de grupo.
- Aplicar la política de IA indicada en la tarea y las normas de UD1; no usar
  IA generativa para resolver o redactar esta actividad.

## Alcance del starter

Incluye Spring Boot 4.0.5, JDK 25, Maven Wrapper, un controlador mínimo con
HTML, JSON y `/health`, y tres pruebas verdes. No incluye la solución de
`/api/info`, la práctica resuelta, respuestas de cuestionarios ni datos de
alumnado. CRUD, persistencia, OpenAPI y autenticación completa quedan fuera de
este ejercicio de UD1.
