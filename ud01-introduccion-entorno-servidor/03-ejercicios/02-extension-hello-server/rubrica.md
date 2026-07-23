# Rúbrica de la extensión TDD

| Criterio | 4 - Excelente | 3 - Notable | 2 - Suficiente | 1 - Insuficiente | Peso |
|---|---|---|---|---|---:|
| Entorno reproducible | Wrapper y Java 25 verificados desde terminal; repo limpio de artefactos. | Entorno correcto con una omisión documental menor. | Ejecuta solo desde IDE o documenta versiones parcialmente. | No reproduce la línea base. | 20 % |
| Ciclo RED-GREEN | Dos commits claros; RED falla por contrato ausente y GREEN pasa con cambio mínimo. | Ciclo correcto con evidencia o separación mejorable. | Prueba e implementación existen, pero no demuestra el orden. | Implementa sin prueba válida o la suite falla. | 30 % |
| Contrato HTTP | Estado, tipo y JSON coinciden exactamente con la prueba y la evidencia real. | Contrato correcto con una imprecisión menor en la explicación. | Respuesta funcional pero prueba incompleta o contrato distinto. | No funciona o no prueba el contrato. | 25 % |
| Comprensión del recorrido | Explica Tomcat, despacho, controlador, conversión y representación sin confundir objetos con bytes. | Explicación correcta con una omisión. | Enumera componentes sin relacionarlos bien. | Confunde cliente, servidor o serialización. | 15 % |
| Entrega y defensa | Evidencia breve, reproducible, sanitizada y defendible. | Clara con un detalle menor. | Excesiva o poco ordenada, pero comprobable. | Incluye artefactos, datos sensibles o no puede explicarla. | 10 % |
