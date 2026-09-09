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

- Dashboard: proyectos del usuario con resumen de tareas por estado.
- Kanban board: tareas de un proyecto ordenadas por estado.
- Detalle de tarea: tarea + comentarios + etiquetas.

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
