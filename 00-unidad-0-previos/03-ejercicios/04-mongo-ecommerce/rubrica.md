# Rúbrica — Ejercicio MongoDB E-Commerce

## Criterios de evaluación

| Criterio | Excelente (4) | Notable (3) | Aprobado (2) | Insuficiente (1) | Peso |
|----------|---------------|-------------|--------------|-------------------|------|
| **Diseño e Integridad de Precios (RA6.a)** | Justificaciones excelentes sobre el embebido de variantes e ítems de órdenes en el momento exacto de la compra (inmutabilidad histórica de precios). | Decisiones bien tomadas pero justificación teórica genérica. | Fallos al duplicar precios de catálogo en órdenes de compra (vulnera inmutabilidad). | No hay justificación del diseño. | 25% |
| **Esquema de Validación (RA6.b)** | `$jsonSchema` impecable para productos u órdenes, validando de forma concisa variantes, precios, cantidades y expresiones regulares. | Esquema válido pero con omisiones menores en validaciones de variantes o tipos numéricos. | Esquema simple con fallos de sintaxis que no compila. | Sin esquema o inválido. | 25% |
| **Migración con Backfill (RA9.d)** | Script de migración con backfill e idempotencia que calcula correctamente `discountPercent` y recalcula precios asociados sin corromper el stock. | Script correcto pero no es idempotente o le faltan comprobaciones de seguridad. | Script básico incompleto o que falla en producción. | No funciona o ausente. | 20% |
| **Indexación y ESR Rule (RA6.g)** | Aplica de forma excelente la regla ESR (Equality, Sort, Range) para elegir y justificar la creación de índices compuestos de rendimiento. | Justifica índices con la regla ESR de forma genérica o incompleta. | Crea índices individuales arbitrarios sin seguir la regla ESR. | No indexa ni justifica. | 20% |
| **Declaración de IA (Transversal)** | Presente, detallada y totalmente verificable (explica prompts, qué se verificó y cómo). | Presente pero genérica o poco explicada. | Presente incompleta. | Ausente o se detecta uso de IA no declarado. | 10% |

## Notas

- Para obtener el 4 en cualquier criterio, el estudiante debe poder defenderlo oralmente si se le pregunta.
- El uso de IA sin declarar se considera falta de honestidad académica.
