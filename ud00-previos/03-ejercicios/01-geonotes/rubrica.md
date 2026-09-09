# Rúbrica — Tarea GeoNotes (Java 25)

## Criterios de evaluación

| Criterio | Excelente (4) | Notable (3) | Aprobado (2) | Insuficiente (1) | Peso |
|----------|---------------|-------------|--------------|-------------------|------|
| **Modelado e Inmutabilidad (RA5.g, RA2.d)** | Define `record` de forma robusta con validaciones completas en el *compact constructor* (rango lat/lon, longitud de título, etc.). | Define `record` correcto pero falta alguna validación menor o no usa *compact constructor*. | Define `record` básico sin validaciones de negocio en constructor. | No modela con `record` o errores graves de sintaxis. | 25% |
| **Jerarquías Sealed y Switch (RA5.g, RA2.d)** | Jerarquía `sealed interface Attachment` perfectamente implementada con `permits` y el nuevo subtipo `Video`. Switch expressions exhaustivos con guardas `when`. | Implementa jerarquía sealed y switch pero faltan guardas o no es completamente exhaustivo. | Implementa clases normales (no sealed) o usa switch clásico con sentencias `break`. | No implementa jerarquía sealed ni switch moderno. | 25% |
| **Record Patterns y Colecciones (RA2.d, RA1.e)** | Utiliza *record patterns* para desestructurar puntos en el switch/instanceof y emplea `SequencedMap`/`SequencedCollection` para el Timeline de forma idónea. | Usa record patterns pero el Timeline usa colecciones clásicas de forma no óptima. | No usa record patterns pero implementa Timeline con colecciones básicas. | No implementa record patterns ni Timeline secuencial. | 25% |
| **Pruebas Unitarias (RA5.h)** | Escribe 3+ tests JUnit completos para validar el comportamiento del switch con guardas, la validación de records y el Timeline. | Escribe tests pero no cubren todos los casos críticos o son redundantes. | Escribe un único test básico o con aserciones triviales. | Sin tests o no compilan. | 15% |
| **Declaración de IA (Transversal)** | Presente, detallada y totalmente verificable (explica prompts, qué se verificó y cómo). | Presente pero genérica o poco explicada. | Presente incompleta. | Ausente o se detecta uso de IA no declarado. | 10% |

## Notas

- Para obtener el 4 en cualquier criterio, el estudiante debe poder defenderlo oralmente si se le pregunta.
- El uso de IA sin declarar se considera falta de honestidad académica.
