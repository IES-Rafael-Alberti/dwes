# Rúbrica del laboratorio HTTP

## Criterios

| Criterio | 4 - Excelente | 3 - Notable | 2 - Suficiente | 1 - Insuficiente | Peso |
|---|---|---|---|---|---:|
| Mensajes HTTP y representaciones | Interpreta con precisión método, destino, estado, campos, contenido y `Content-Type`; diferencia HTML, JSON, HEAD, OPTIONS y error usando evidencia. | Interpreta correctamente casi toda la traza con alguna imprecisión menor. | Identifica elementos básicos, pero confunde parte de su función. | No aporta trazas válidas o confunde solicitud y respuesta. | 25 % |
| Ejecución cliente-servidor | Delimita responsabilidades, ventajas, costes y validaciones con ejemplos obtenidos del laboratorio. | Distingue cliente y servidor, pero justifica parcialmente decisiones o costes. | Reconoce la diferencia sin conectarla bien con la evidencia. | Confunde dónde se ejecuta el código o confía decisiones de seguridad al cliente. | 20 % |
| Mecanismos e infraestructura | Explica el recorrido hasta el controlador y compara proceso persistente, CGI y FastCGI sin inferir arquitectura desde banners. | Explica el recorrido y dos mecanismos con alguna omisión. | Enumera componentes sin explicar sus relaciones. | Atribuye mecanismos erróneos o usa cabeceras como prueba concluyente. | 20 % |
| Selección de tecnologías | Compara Spring Boot y Laravel con criterios técnicos, operativos y didácticos y concluye según contexto. | Usa varios criterios, aunque la conclusión es poco matizada. | Presenta una comparación genérica con pocos criterios. | Emite preferencias sin justificación técnica. | 15 % |
| Semántica de QUERY | Explica RFC 10008, seguridad, idempotencia, contenido tipado y límites de adopción sin fingir soporte ejecutado. | Describe correctamente QUERY con una omisión menor. | Distingue QUERY de GET/POST de forma superficial. | Lo clasifica como cabecera, mutación o simple alias de POST. | 10 % |
| Seguridad y comunicación | Entrega concisa, reproducible, sanitizada y defendible oralmente. | Entrega clara con un detalle menor de formato o sanitización. | Evidencia excesiva o poco clara, sin secretos reales. | Expone datos sensibles, no permite reproducir el trabajo o no puede explicarlo. | 10 % |

## Condiciones

- Exponer credenciales, tokens o cookies reales obliga a revocarlos y corregir la entrega; nunca mejora la evidencia.
- Una captura no sustituye una explicación técnica.
- El alumnado debe poder reproducir y defender oralmente cualquier apartado.
- El uso de IA generativa incumple la política de esta actividad y se gestiona mediante las normas de integridad académica, no como un criterio que otorgue puntos.
