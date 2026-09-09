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

| Nivel | Diseño | Schema | Índices | Migración | Explain |
|---|---|---|---|---|---|
| Insuficiente | Arbitrario | Incompleto | Sin criterio | No funciona | Ausente |
| Mejorable | Parcial | Aceptable | Pocos justificados | Incompleta | Básico |
| Correcto | Bien razonado | Correcto | ESR aplicado | Idempotente | Bien interpretado |
| Excelente | Muy sólido | Completo | Optimizado | Rollback claro | Análisis técnico excelente |
