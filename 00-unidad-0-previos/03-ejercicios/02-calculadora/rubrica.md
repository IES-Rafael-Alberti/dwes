# Rúbrica — Tarea Calculadora (Java 25)

## Criterios de evaluación

| Criterio | Excelente (4) | Notable (3) | Aprobado (2) | Insuficiente (1) | Peso |
|----------|---------------|-------------|--------------|-------------------|------|
| **Resolución de Retos (RA2.d, RA5.g)** | Implementa con éxito 3+ retos sugeridos (reiniciar, reutilizar resultado, comando reset/clear, funciones adicionales). | Implementa 2 retos con éxito o con pequeños fallos de integración. | Implementa 1 reto o la integración con el REPL es inestable. | No implementa ningún reto. | 40% |
| **Arquitectura de Intérprete (RA5.g)** | Comprende la separación Lexer → Parser → AST → Evaluator. Modifica el AST utilizando interfaces selladas y records de nodos de forma limpia. | Modifica el AST pero introduce acoplamiento o vulnera el patrón Composite de forma menor. | Modifica la lógica directamente en el REPL sin respetar la arquitectura del AST. | No respeta en absoluto la arquitectura por capas. | 30% |
| **Pruebas de Regresión (RA5.h)** | Añade y ejecuta tests JUnit que cubren específicamente las modificaciones/retos introducidos, asegurando que no se rompe la lógica existente. | Añade tests para los retos pero con cobertura parcial o aserciones incompletas. | Añade tests genéricos o triviales sin aserciones útiles. | No añade tests de los retos. | 20% |
| **Declaración de IA (Transversal)** | Presente, detallada y totalmente verificable (explica prompts, qué se verificó y cómo). | Presente pero genérica o poco explicada. | Presente incompleta. | Ausente o se detecta uso de IA no declarado. | 10% |

## Notas

- Para obtener el 4 en cualquier criterio, el estudiante debe poder defenderlo oralmente si se le pregunta.
- El uso de IA sin declarar se considera falta de honestidad académica.
