# Rúbrica — Ejercicio MongoDB Blog

## Criterios de evaluación

| Criterio | Excelente (4) | Notable (3) | Aprobado (2) | Insuficiente (1) | Peso |
|----------|---------------|-------------|--------------|-------------------|------|
| **Justificación de Diseño (RA6.a)** | Justificaciones excelentes basadas en patrones de modelado NoSQL (embebido de comentarios, expiración de documentos, etc.). | Decisiones bien tomadas pero justificaciones genéricas. | Mezcla confusa de patrones relacionales y NoSQL. | No hay justificación del diseño. | 25% |
| **Esquema de Validación (RA6.b)** | `$jsonSchema` impecable, con tipado fuerte, restricciones de tamaño, obligatoriedad y formato del slug. | Esquema válido pero con omisiones en validaciones secundarias o restricciones de tamaño. | Esquema simple con fallos de sintaxis menores que no compila. | Sin esquema o inválido. | 25% |
| **Migración de Datos (RA9.d)** | Script de migración idempotente y robusto con lógica matemática para `readingTime` en palabras por minuto. | Script correcto pero no es idempotente o tiene fallos menores. | Script básico incompleto o que falla en producción. | No funciona o ausente. | 20% |
| **Optimización con Explain (RA6.g)** | Documenta y analiza correctamente 3 consultas con `explain()`, interpretando índices, ganancia de performance y el plan de ejecución. | Documenta con explain pero no profundiza en la interpretación del plan de ejecución. | Muestra el log del explain sin ninguna interpretación. | No documenta o ausente. | 20% |
| **Declaración de IA (Transversal)** | Presente, detallada y totalmente verificable (explica prompts, qué se verificó y cómo). | Presente pero genérica o poco explicada. | Presente incompleta. | Ausente o se detecta uso de IA no declarado. | 10% |

## Notas

- Para obtener el 4 en cualquier criterio, el estudiante debe poder defenderlo oralmente si se le pregunta.
- El uso de IA sin declarar se considera falta de honestidad académica.
