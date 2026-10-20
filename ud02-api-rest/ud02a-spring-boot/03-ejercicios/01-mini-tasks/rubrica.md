# Rúbrica — Práctica Guiada Mini Spring Boot Tasks

## Criterios de evaluación

| Criterio | Excelente (4) | Notable (3) | Aprobado (2) | Insuficiente (1) | Peso |
|----------|---------------|-------------|--------------|-------------------|------|
| **Uso de Git e historial (Transversal)** | Mantiene su fork individual, explora y explica las etiquetas, trabaja sobre `main` y registra cambios pequeños de autoría propia. | El fork y los commits están, pero la evolución o la autoría de algunos cambios no queda clara. | Hay pocos commits significativos, no se usan las etiquetas o no se evidencia trabajo individual suficiente. | No hay un fork individual utilizable ni historial de trabajo verificable. | 25% |
| **Separación por Capas (RA5.b)** | Implementa y comprende la separación estricta: Controlador V4 (DTOs) → Servicio → Repositorio JPA, sin fugas de responsabilidades. | Separación correcta pero con pequeñas mezclas de responsabilidades (ej.: lógica de negocio en controller). | Arquitectura básica V2 (controlador directo en memoria sin servicios). | No separa el código en capas o presenta errores de compilación graves. | 35% |
| **Tests y verificación (RA5.h)** | `mvn test` termina correctamente e incluye pruebas pertinentes para su slice individual, incluidos sus casos representativos. | Las pruebas principales pasan; falta cubrir algún caso relevante del slice. | Hay pruebas, pero no verifican el comportamiento nuevo o no se ejecutan de forma reproducible. | No hay pruebas para el slice o no se pueden ejecutar. | 30% |
| **Declaración de IA (Transversal)** | Presente, detallada y totalmente de acuerdo con las plantillas del centro. | Presente pero genérica. | Presente incompleta. | Ausente. | 10% |

## Notas

- El uso de IA sin declarar se considera falta de honestidad académica.

## Aplicación 2026/2027

La práctica es **individual**. Cada estudiante desarrolla una funcionalidad
vertical en su propio fork, que es la fuente de evidencias de esta entrega:
código, tests, commits, decisiones y
declaración de IA. La tarea de Moodle solo recoge el enlace al repositorio; no se
califican capturas ni documentos separados.

La funcionalidad vertical adicional es obligatoria para la ruta 2026/2027: un
cambio pequeño de extremo a extremo que evidencie conexión JPA/H2, servicio,
repositorio, DTO cuando corresponda, respuesta HTTP y prueba. Corresponde a
RA6.a-d y RA7.a-d; la práctica representa el 15 % de la calificación de UD2a en
2026/2027.
