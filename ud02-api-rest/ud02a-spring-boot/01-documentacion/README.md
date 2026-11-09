# Documentación — Spring Boot

Los apuntes se agrupan por temas. Los nombres de los documentos conservan su
numeración anterior para reconocer las referencias de clase; el orden de las
carpetas no obliga a leer todos los anexos antes de empezar a programar.

## Primera parte — Construir y probar la API

### Fundamentos y arquitectura

- [Introducción a Spring Boot](01-fundamentos/01-introduccion-spring-boot.md).
- [Arquitectura en capas: por qué separamos responsabilidades](01-fundamentos/04a-arquitectura-en-capas.md).
- [Capa de servicios](01-fundamentos/04-capa-servicios.md).
- [Funcionamiento interno de una petición](01-fundamentos/07-funcionamiento-spring-boot.md).

### HTTP, controladores y errores

- [Controladores REST](02-api-rest/03-controladores-rest.md).
- [Manejo de errores](02-api-rest/05-tdd-manejo-errores.md).
- [Mapa del caso práctico Battleship](02-api-rest/08-battleship-caso-practico.md).
- [Guías de construcción por sesiones](../02-ejemplos/battleship/docs/README.md).

### Pruebas

- [Primeros pasos con TDD](03-pruebas/02-tdd-primeros-pasos.md).
- [Slicing de tests](03-pruebas/06-tdd-slicing.md).
- **[Diccionarios de consulta](03-pruebas/diccionarios/README.md):**
  [JUnit](03-pruebas/diccionarios/01-junit-jupiter.md),
  [aserciones JUnit/AssertJ/Hamcrest](03-pruebas/diccionarios/02-aserciones-junit-assertj-hamcrest.md),
  [Mockito](03-pruebas/diccionarios/03-mockito.md) y
  [MockMvc/Spring Test](03-pruebas/diccionarios/04-mockmvc-y-spring-test.md).

## Segunda parte — Persistencia y aplicaciones de servidor

### Bloque A — Modelo relacional e integridad

1. **Relaciones y ciclo de vida — dos versiones completas:**
   - [Versión didáctica](04-persistencia/bloque-a/01-entidades-relaciones-y-ciclo-de-vida-didactica.md):
     primera lectura con lenguaje progresivo, ejemplos y explicación de los términos.
   - [Versión de referencia técnica](04-persistencia/bloque-a/01-entidades-relaciones-y-ciclo-de-vida.md):
     detalle de 1:N, 1:1, N:M, estados de persistencia y alternativas para los ciclos.
2. [Integridad, migraciones y pruebas de persistencia](04-persistencia/bloque-a/02-integridad-migraciones-y-pruebas.md):
   reglas, restricciones, transacciones y evidencias contra la BD.
3. [Presentación de clase](transparencias/bloque-a-modelo-relacional-e-integridad.md):
   selección visual de decisiones y ejemplos, no sustituto de los apuntes.

### Consultas y representación de datos

- [Proyecciones JPA](04-persistencia/10-proyecciones-jpa.md).

Los bloques posteriores se incorporarán progresivamente. El trabajo de aplicación
continúa en los modelos de prácticas ya iniciados; Battleship es el ejemplo docente.

## Referencias para consultar cuando hagan falta

- [Anotaciones de Spring Boot](05-referencias/anexo-anotaciones-spring-boot.md).
- [Anotaciones de Lombok y código generado](05-referencias/anexo-lombok.md).
- [Hibernate y records](05-referencias/anexo-hibernate-records.md).
- [Genéricos de Java](05-referencias/anexo-java-genericos.md).
- [Git, Maven y Spring](05-referencias/anexo-git-maven-spring.md).
- [Migración de Spring Boot 3 a 4](05-referencias/09-migracion-sb3-sb4.md).

MVC y Thymeleaf tienen su recorrido en la unidad UD3.
