# Rúbrica — Ejercicio PostgreSQL JSONB Modelo Híbrido

## Criterios de evaluación

| Criterio | Excelente (4) | Notable (3) | Aprobado (2) | Insuficiente (1) | Peso |
|----------|---------------|-------------|--------------|-------------------|------|
| **Modelo Híbrido y DDL (RA6.a, RA6.b)** | DDL impecable, con tipado relacional correcto, columna JSONB indexada con GIN, y columna generada virtual para indexación B-Tree de alta velocidad. | DDL correcto pero sin usar columnas generadas o índices optimizados. | DDL básico, crea la tabla pero sin índices GIN sobre el JSONB. | DDL ausente o incorrecto. | 25% |
| **Validación con Triggers (RA6.b)** | Trigger PL/pgSQL completo y robusto, que valida de forma precisa la presencia de atributos requeridos según el tipo de envío, con control de excepciones excelente. | Trigger funcional pero con fallos menores en validaciones complejas (como internacional). | Trigger básico que valida tipos de envío de forma incompleta. | Sin trigger o no compila. | 25% |
| **Consultas JSONB (RA6.c)** | Escribe con éxito las consultas solicitadas utilizando de manera óptima operadores JSONB (`->`, `->>`, casteos numéricos, etc.). | Consultas correctas pero con sintaxis ineficiente o casteos dudosos. | Consultas con errores que no devuelven los resultados deseados. | Consultas ausentes o con errores graves de sintaxis. | 20% |
| **Pruebas con Explain (RA6.g)** | Documenta con `EXPLAIN ANALYZE` ambas consultas, interpretando con precisión si se usa el índice GIN o B-Tree virtual, coste temporal y scans. | Ejecuta explain pero la interpretación es genérica o superficial. | Adjunta el explain sin interpretar la salida ni el plan de ejecución. | No utiliza explain. | 20% |
| **Declaración de IA (Transversal)** | Presente, detallada y totalmente verificable (explica prompts, qué se verificó y cómo). | Presente pero genérica o poco explicada. | Presente incompleta. | Ausente o se detecta uso de IA no declarado. | 10% |

## Notas

- Para obtener el 4 en cualquier criterio, el estudiante debe poder defenderlo oralmente si se le pregunta.
- El uso de IA sin declarar se considera falta de honestidad académica.
