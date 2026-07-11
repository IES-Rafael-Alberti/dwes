# Caso e-commerce

## Enunciado

Modela una tienda online con:

- **Productos** con variantes (talla, color, precio, stock), categoría, descripción, imágenes y rating promedio.
- **Órdenes** con items (productoId, cantidad, precio en el momento de compra), dirección de envío, historial de estados (fecha, estado, quién), total e impuestos.
- **Usuarios** con email, passwordHash, direcciones (hasta 5), wishlist (máx. 50 productos) e historial de compras.
- **Reviews** con productoId, usuarioId, texto, rating 1-5 y fecha.

## Tareas

1. Diseñar el esquema completo con decisiones embed/reference justificadas.
2. Escribir el validator `$jsonSchema` de la colección principal.
3. Crear índices y justificarlos con ESR rule.
4. Escribir una migración que añada `discountPercent` y haga backfill con cálculo.
5. Hacer `explain()` de 3 consultas típicas.

## Criterios de evaluación

Ver `rubrica.md` y `ra-ce.md` en este directorio para el desglose detallado de criterios y alineación con los RA/CE del módulo.

## Entregables

- Documento de diseño `diseno.md` justificando el modelado, la estrategia de índices compuestos (ESR rule) y la interpretación de los planes con `explain()`.
- Script del validador `$jsonSchema`.
- Script de migración en JS para MongoDB Shell o `migrate-mongo`.
- Declaración de uso de IA cumplimentada (ver plantilla en `00-recursos-comunes/plantillas/`).

## Política de IA

| Aspecto | |
|---------|-|
| Uso de IA permitido | Sí, para generar esqueletos del `$jsonSchema` o del script de migración. |
| Declaración obligatoria | Sí. |
| Herramientas permitidas | ChatGPT, Claude, Gemini, GitHub Copilot. |
| Qué NO está permitido | Generar el documento de diseño o la interpretación de la regla ESR de forma automatizada sin analizar los planes de ejecución ni justificar las decisiones técnicamente. |

