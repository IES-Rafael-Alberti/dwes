# Caso blog

## Enunciado

Modela un sistema de blogs donde:

- Posts con título, contenido, slug, autor, tags, fecha de publicación, fecha de creación y estado (borrador/publicado).
- Comentarios anidados (autor, texto, fecha) con soporte para respuestas anidadas de 1 nivel.
- Cada post puede tener hasta 500 comentarios.
- Cada post tiene un contador de visitas.
- Se necesita buscar posts por contenido (text search).
- Los posts publicados expiran a los 2 años (archivo automático).

## Tareas

### 1. Diseñar esquema

Decide qué embebes y qué referencias, y justifica cada decisión.

### 2. Escribir validator `$jsonSchema`

Incluye campos obligatorios, tipos, límites y restricciones relevantes.

### 3. Crear índices

Justifica cada índice usando ESR rule.

### 4. Escribir migración

Añade el campo `readingTime` estimado a 200 palabras/minuto.

### 5. Hacer `explain()`

Documenta 3 consultas típicas:

- por autor,
- por tag,
- búsqueda textual.

## Criterios de evaluación

Ver `rubrica.md` y `ra-ce.md` en este directorio para el desglose detallado de criterios y alineación con los RA/CE del módulo.

## Entregables

- Documento de diseño `diseno.md` justificando las decisiones de modelado, la estrategia de índices y la interpretación de los planes de consulta con `explain()`.
- Script del validador `$jsonSchema`.
- Script de migración en JS para MongoDB Shell o `migrate-mongo`.
- Declaración de uso de IA cumplimentada (ver plantilla en `00-recursos-comunes/plantillas/`).

## Política de IA

| Aspecto | |
|---------|-|
| Uso de IA permitido | Sí, para generar esqueletos del `$jsonSchema` o del script de migración. |
| Declaración obligatoria | Sí. |
| Herramientas permitidas | ChatGPT, Claude, Gemini, GitHub Copilot. |
| Qué NO está permitido | Generar el documento de diseño o la interpretación del `explain()` de forma automatizada sin analizar los planes de ejecución ni justificar las decisiones técnicamente. |

