# Rúbrica — Proyecto de Diseño MongoDB Grupal

## Criterios de evaluación

| Criterio | Excelente (4) | Notable (3) | Aprobado (2) | Insuficiente (1) | Peso |
|----------|---------------|-------------|--------------|-------------------|------|
| **Justificación de Decisiones (RA6.a)** | Justificaciones técnicas brillantes, fundamentadas en patrones de acceso reales, tasas de lectura/escritura y volumen proyectado. | Decisiones de modelado bien argumentadas pero con un enfoque más genérico. | Modelado razonablemente correcto pero explicaciones superficiales o copy-paste. | No justifica las decisiones o duplica antipatrones relacionales. | 25% |
| **Validación y Esquemas (RA6.b)** | `$jsonSchema` completos, válidos, robustos, con validaciones estrictas y tipado fuerte para todas las colecciones. | Esquemas válidos pero omiten validaciones secundarias o restricciones de tamaño. | Esquemas con fallas sintácticas o muy incompletos que no compilan. | Esquemas ausentes o inválidos. | 25% |
| **Indexación y ESR Rule (RA6.g)** | Aplica y justifica con brillantez la regla ESR (Equality, Sort, Range) para todas las consultas críticas y crea índices compuestos de alto rendimiento. | Índices correctos pero justificación de la regla ESR algo incompleta. | Crea índices individuales arbitrarios sin aplicar la regla ESR. | Índices ausentes o incorrectos. | 20% |
| **Migraciones up/down (RA9.d)** | Scripts de migración impecables, totalmente idempotentes, con lógica de reversión (`down` / rollback) probada y sin pérdida de datos. | Scripts correctos y funcionales pero la reversión tiene fallos menores. | Scripts de migración básicos sin lógica de reversión. | No funcionan o ausentes. | 20% |
| **Declaración de IA individual (Transversal)** | Cada miembro del grupo entrega su declaración individualizada, honesta y detallando su rol en la supervisión de la IA. | Entregada pero genérica o poco explicada. | Entregada incompleta o copia de otro miembro. | Ausente o se detecta uso no declarado. | 10% |

## Notas

- Para obtener el 4 en cualquier criterio, el estudiante debe poder defenderlo oralmente si se le pregunta.
- El uso de IA sin declarar se considera falta de honestidad académica.
