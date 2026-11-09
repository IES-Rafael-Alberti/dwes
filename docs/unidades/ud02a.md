# UD2a - API REST con Spring Boot

La ruta de UD2a construye APIs REST con Spring Boot 4 y Java 25. La primera
parte introduce el recorrido mediante TDD; la segunda desarrolla persistencia
y capacidades de servidor con bloques de código explicados y comprobados.
Battleship es el ejemplo conductor; las prácticas vigentes y los cuestionarios
aportan las evidencias del alumnado.

## Ruta rápida

1. Lee la [teoría](../ud02a-spring-boot/01-documentacion/README.md).
2. Recorre el [mapa incremental de Battleship](../ud02a-spring-boot/01-documentacion/02-api-rest/08-battleship-caso-practico.md).
3. Sigue las [sesiones prácticas](../ud02a-spring-boot/02-ejemplos/battleship/docs/README.md).
4. Resuelve [mini-tasks](../ud02a-spring-boot/03-ejercicios/01-mini-tasks/ra-ce.md), [book-catalog](../ud02a-spring-boot/03-ejercicios/02-book-catalog/ra-ce.md) y [gestión de eventos](../ud02a-spring-boot/03-ejercicios/03-gestion-eventos/ra-ce.md).

## Referencias para leer y escribir los tests

- [Índice de diccionarios: JUnit, aserciones, Mockito y MockMvc](../ud02a-spring-boot/01-documentacion/03-pruebas/diccionarios/README.md).
- [Anotaciones de Spring, Boot, JPA y validación](../ud02a-spring-boot/01-documentacion/05-referencias/anexo-anotaciones-spring-boot.md).
- [Lombok: qué genera cada anotación](../ud02a-spring-boot/01-documentacion/05-referencias/anexo-lombok.md).

## Segunda parte — Bloque A

- [Relaciones — versión didáctica para estudiar](../ud02a-spring-boot/01-documentacion/04-persistencia/bloque-a/01-entidades-relaciones-y-ciclo-de-vida-didactica.md).
- [Entidades y relaciones — referencia técnica ampliada](../ud02a-spring-boot/01-documentacion/04-persistencia/bloque-a/01-entidades-relaciones-y-ciclo-de-vida.md).
- [Integridad, migraciones y pruebas](../ud02a-spring-boot/01-documentacion/04-persistencia/bloque-a/02-integridad-migraciones-y-pruebas.md).
- [Presentación para clase](../ud02a-spring-boot/01-documentacion/transparencias/bloque-a-modelo-relacional-e-integridad.md).

## Resultados

| Bloque | Evidencia |
| --- | --- |
| Arquitectura REST | Controladores, DTOs, servicios y repositorios |
| Persistencia | JPA, Flyway y pruebas de integración |
| Calidad | TDD, test slicing y contratos HTTP |
| Seguridad | JWT, roles, CORS y errores consistentes |
| Contrato | OpenAPI 3.1 canónico de Battleship |

La [guía de evaluación](../ud02a-spring-boot/EVALUACION.md) reúne las
evidencias con ponderación publicada. La [matriz RA/CE](../ud02a-spring-boot/ra-ce.md)
identifica qué actividad demuestra cada criterio; cada ejercicio conserva su
propia rúbrica.

## Límites

.NET y GraphQL se conservan como referencias opcionales. No son una segunda
ruta obligatoria ni sustituyen Spring Boot/Battleship.

## Trabajo con agentes de IA

No es evaluable, pero a partir de esta unidad conviene apoyarse en la
[guía de desarrollo asistido por agentes de IA](../guias/desarrollo-con-agentes-ia.md):
un solo agente, spec corta y verificación con pruebas propias antes de aceptar nada.
