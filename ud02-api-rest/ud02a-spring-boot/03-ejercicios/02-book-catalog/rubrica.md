# Rúbrica — Book Catalog API

## RA/CE evaluados

- RA5.b: Separación por capas (Controller → Service → DTO)
- RA5.h: Pruebas y documentación del código

## Criterios de evaluación

| Criterio | Excelente (4) | Notable (3) | Aprobado (2) | Insuficiente (1) | Peso |
|----------|---------------|-------------|--------------|-------------------|------|
| **Entrega progresiva** | Complete las 6 entregas con commits correctamente formateados | Complete 5 entregas | Complete 3-4 entregas | Menos de 3 entregas | 30% |
| **Uso de ResponseEntity** | Usa ResponseEntity con códigos precisos (201, 204, Location) y headers adecuados | Usa ResponseEntity pero con errores menores en códigos | Usa ResponseEntity de forma básica | No usa ResponseEntity | 20% |
| **Separación por capas (RA5.b)** | Capas Service y Controller claramente separadas, sin fugas de responsabilidad | Separación correcta pero con algún acoplamiento menor | Lógica de negocio parcialmente en el controlador | Sin capa de servicio | 20% |
| **Manejo de errores** | Excepciones propias + `@RestControllerAdvice` global con códigos HTTP precisos | Manejador global pero sin excepciones propias | Manejo básico con try-catch en controlador | Sin manejo de errores | 15% |
| **Tests (RA5.h)** | Todos los tests semilla pasan | Pasan 4-5 entregas | Pasan 2-3 entregas | Pasan 0-1 entregas | 15% |

## Notas

- Para obtener el 4 en cualquier criterio, el estudiante debe poder defenderlo oralmente si se le pregunta.
- El uso de IA sin declarar se considera falta de honestidad académica.
