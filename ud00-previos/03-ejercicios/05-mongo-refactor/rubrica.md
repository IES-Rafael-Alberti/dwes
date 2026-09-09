# Rúbrica — Ejercicio MongoDB Refactor

## Criterios de evaluación

| Criterio | Excelente (4) | Notable (3) | Aprobado (2) | Insuficiente (1) | Peso |
|----------|---------------|-------------|--------------|-------------------|------|
| **Rediseño NoSQL (RA6.a)** | Rediseño NoSQL excelente, adaptado a los patrones de acceso (Dashboard, Kanban, Detalle Tarea) con equilibrio perfecto entre embebido y referencia. | Rediseño coherente con pocos errores menores en la elección de referencias. | Copia directa de la estructura SQL (un ID por tabla) en NoSQL (antipatrión del ID-itis). | No rediseña el modelo. | 35% |
| **Esquema de Validación (RA6.b)** | `$jsonSchema` impecable, reflejando el anidamiento de comentarios o etiquetas e integrando validaciones robustas. | Esquema válido pero con omisiones menores en validaciones de subtipos o de estructuras anidadas. | Esquema simple con fallos de sintaxis que no compila. | Sin esquema o ausente. | 25% |
| **Reflexión Técnica (RA6.g)** | Análisis técnico excelente sobre las fortalezas y debilidades de SQL vs NoSQL, identificando de forma crítica problemas de transaccionalidad o denormalización. | Comparativa correcta pero con argumentos teóricos genéricos. | Comparativa superficial sin analizar la naturaleza del acceso a datos. | Sin reflexión o ausente. | 30% |
| **Declaración de IA (Transversal)** | Presente, detallada y totalmente verificable (explica prompts, qué se verificó y cómo). | Presente pero genérica o poco explicada. | Presente incompleta. | Ausente o se detecta uso de IA no declarado. | 10% |

## Notas

- Para obtener el 4 en cualquier criterio, el estudiante debe poder defenderlo oralmente si se le pregunta.
- El uso de IA sin declarar se considera falta de honestidad académica.
