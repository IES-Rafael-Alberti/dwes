# Rúbrica — Práctica Guiada Mini Spring Boot Tasks

## Criterios de evaluación

| Criterio | Excelente (4) | Notable (3) | Aprobado (2) | Insuficiente (1) | Peso |
|----------|---------------|-------------|--------------|-------------------|------|
| **Uso de Git y Recreación de Historia (Transversal)** | Recrea correctamente el histórico pedagógico con el script `setup-history.sh`, navega con éxito por las tags y explica los cambios de cada etapa. | Recrea el repositorio pero tiene dudas al explicar las diferencias entre etiquetas. | Recrea el repositorio pero no sabe realizar checkout ni entiende el histórico. | No logra recrear el repositorio ni el histórico. | 25% |
| **Separación por Capas (RA5.b)** | Implementa y comprende la separación estricta: Controlador V4 (DTOs) → Servicio → Repositorio JPA, sin fugas de responsabilidades. | Separación correcta pero con pequeñas mezclas de responsabilidades (ej.: lógica de negocio en controller). | Arquitectura básica V2 (controlador directo en memoria sin servicios). | No separa el código en capas o presenta errores de compilación graves. | 35% |
| **Paso de Tests y Verificación (RA5.h)** | Todos los perfiles de test (`mvn test`) pasan con éxito de forma local y sabe interpretar los resultados. | Todos los tests básicos pasan pero fallan los extras/addons o de rendimiento. | Pasan únicamente los tests de la versión V1 o V2. | Ningún test pasa con éxito. | 30% |
| **Declaración de IA (Transversal)** | Presente, detallada y totalmente de acuerdo con las plantillas del centro. | Presente pero genérica. | Presente incompleta. | Ausente. | 10% |

## Notas

- Para obtener el 4 en cualquier criterio, el estudiante debe poder defenderlo oralmente si se le pregunta.
- El uso de IA sin declarar se considera falta de honestidad académica.
