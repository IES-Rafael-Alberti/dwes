# Rúbrica — Ejercicio Benchmark MongoDB vs PostgreSQL

## Criterios de evaluación

| Criterio | Excelente (4) | Notable (3) | Aprobado (2) | Insuficiente (1) | Peso |
|----------|---------------|-------------|--------------|-------------------|------|
| **Generación del Dataset (RA6.a, RA9.g)** | Genera un dataset consistente y realista de 100k registros utilizando scripts optimizados (JS/python/SQL) con atributos variables y tipados idóneos. | Genera el dataset de 100k productos pero con poca variedad de atributos. | Genera un dataset incompleto o de menor tamaño (menos de 50k productos). | El dataset no se genera o tiene fallos graves. | 25% |
| **Metodología de Medición (RA6.g)** | Realiza mediciones de rendimiento sólidas (media de 10 ejecuciones por query), documentando tamaño en disco, explain plan e índices utilizados. | Realiza mediciones de rendimiento correctas pero sin promediar ejecuciones o ignorando tamaño en disco. | Mide los tiempos de forma imprecisa (por ejemplo, una sola ejecución superficial). | Mediciones ausentes o inválidas. | 25% |
| **Análisis e Interpretación (RA6.a, RA6.g)** | Presenta una tabla comparativa excelente e interpreta con gran rigurosidad técnica cuándo y por qué gana cada motor en función de la indexación (GIN vs B-Tree). | Tabla comparativa correcta pero con interpretación genérica sobre el rendimiento de ambos motores. | Presenta los datos numéricos pero no extrae conclusiones técnicas de rendimiento. | Sin interpretación de resultados o tabla incompleta. | 25% |
| **Reflexión sobre Flexibilidad/Integridad (RA6.a)** | Reflexiona de manera brillante sobre el peso de la flexibilidad de esquemas NoSQL vs la integridad relacional y transaccionalidad de PostgreSQL. | Reflexión correcta sobre flexibilidad e integridad pero con poca profundidad. | Respuestas vagas o superficiales que repiten enunciados teóricos. | Ausente o incorrecto. | 15% |
| **Declaración de IA (Transversal)** | Presente, detallada y totalmente verificable (explica prompts, qué se verificó y cómo). | Presente pero genérica o poco explicada. | Presente incompleta. | Ausente o se detecta uso de IA no declarado. | 10% |

## Notas

- Para obtener el 4 en cualquier criterio, el estudiante debe poder defenderlo oralmente si se le pregunta.
- El uso de IA sin declarar se considera falta de honestidad académica.
