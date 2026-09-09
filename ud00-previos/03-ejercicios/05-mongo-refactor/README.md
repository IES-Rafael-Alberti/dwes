# Refactor de modelo relacional a MongoDB

Dado el siguiente esquema SQL:

```sql
-- Tablas normalizadas al extremo
CREATE TABLE usuarios (id SERIAL PK, nombre, email, password_hash, created_at);
CREATE TABLE proyectos (id SERIAL PK, usuario_id FK, nombre, descripcion, created_at);
CREATE TABLE tareas (id SERIAL PK, proyecto_id FK, titulo, descripcion, estado, prioridad, asignado_a FK, created_at, updated_at);
CREATE TABLE comentarios (id SERIAL PK, tarea_id FK, usuario_id FK, texto, created_at);
CREATE TABLE etiquetas (id SERIAL PK, nombre, color);
CREATE TABLE tareas_etiquetas (tarea_id FK, etiqueta_id FK);
```

## Tareas

### 1. Rediseñar el modelo para MongoDB

Piensa en estos accesos típicos:
- **Dashboard**: proyectos del usuario con resumen de tareas por estado.
- **Kanban board**: tareas de un proyecto ordenadas por estado.
- **Detalle de tarea**: tarea + comentarios + etiquetas.

### 2. Justificar embedding vs referencing

Explica cada relación con criterio de acceso.

### 3. Indicar colecciones e índices

Lista colecciones, índices y motivo.

### 4. Escribir el `$jsonSchema`

Define la colección principal con validación real.

### 5. Reflexión

Responde:
- ¿Qué consultas son ahora más eficientes que en SQL?
- ¿Cuáles serían más difíciles?

## Criterios de evaluación

Ver `rubrica.md` y `ra-ce.md` en este directorio para el desglose detallado de criterios y alineación con los RA/CE del módulo.

## Entregables

- Documento de diseño `refactor.md` justificando las decisiones de modelado (embedding vs referencing) para cada acceso típico y la reflexión sobre SQL vs NoSQL.
- Script del validador `$jsonSchema` de la colección principal resultante.
- Declaración de uso de IA cumplimentada (ver plantilla en `00-recursos-comunes/plantillas/`).

## Política de IA

| Aspecto | |
|---------|-|
| Uso de IA permitido | Sí, para generar bocetos de diseño y esqueletos de código `$jsonSchema`. |
| Declaración obligatoria | Sí. |
| Herramientas permitidas | ChatGPT, Claude, Gemini, GitHub Copilot. |
| Qué NO está permitido | Generar las justificaciones de modelado o la reflexión final usando IA de forma automatizada sin analizar los pros y contras técnicos con criterio propio. |


