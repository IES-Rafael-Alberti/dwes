# Inventario de reforma de UD2

## Estado

UD2 está **en revisión de cierre y no puede declararse cerrada**. La prioridad activa es consolidar la ruta Spring Boot; .NET y GraphQL quedan como demostraciones opcionales y no evaluables.

## Decisión

- Spring Boot 4 con Java 25 será la única ruta principal y evaluable.
- Battleship será el proyecto conductor canónico.
- Mini Tasks, Book Catalog y Gestión de Eventos serán ejercicios con papeles delimitados, no rutas paralelas.
- .NET conservará como máximo una demostración mínima verificada.
- GraphQL conservará como máximo una demostración mínima verificada.
- OpenAPI versionado y el flujo SDD deben convertirse en fuentes verificables, no limitarse a documentación generada desde código.

## Hallazgos verificados

### Seguridad y publicación

- `V4__seed_admin.sql` crea `admin/admin123` como migración normal. Es una credencial privilegiada conocida y bloquea cualquier presentación del perfil como desplegable.
- Las claves RSA y el keystore de Battleship existen localmente bajo `src/main/resources/keys/`, pero Git los ignora y **no están versionados**. Aun así, MkDocs podía copiarlos desde el árbol local porque Git ignore no controla la publicación.
- `.dockerignore` usa `keys/*.pem` y `keys/*.p12`, pero los archivos reales viven en `src/main/resources/keys/`; esas reglas no protegen el contexto real.
- Battleship genera un par RSA efímero fuera de `prod`/`docker` y exige PEM externos en esos perfiles; Maven y Docker excluyen `src/main/resources/keys/` de los artefactos.
- `06-seguridad/Seguridad/` contiene soluciones completas, APIs antiguas, secretos de demostración, `.Rhistory`, ZIP, PDF, HTML, Org y residuos conversacionales.
- Se excluyen temporalmente de MkDocs las claves, el árbol heredado de seguridad y las rutas opcionales .NET/GraphQL hasta clasificarlos.

### Reproducibilidad

- Existen gitlinks para `demo`, Mini Tasks, Book Catalog, Gestión de Eventos, Gestión Biblioteca, Recetas .NET y ToDo .NET.
- No existe `.gitmodules`; un clon limpio no puede reconstruir esos repositorios anidados.
- Hay cambios locales preservados en Battleship y varios repositorios anidados; no se consolidarán sin revisar cada unidad de trabajo.
- Las afirmaciones de tests verdes deben volver a verificarse desde los wrappers y revisiones exactas que se publiquen.

### Documentación principal

- `06-tdd-slicing.md` usa imports de Spring Boot 3 y `@MockBean`.
- `09-migracion-sb3-sb4.md` afirma erróneamente que `@DataJpaTest` desapareció; Spring Boot 4 lo mantiene en un paquete y módulo nuevos.
- La introducción mezcla starters, Java 21 y dependencias anteriores con el baseline Spring Boot 4/Java 25.
- La secuencia duplica introducción y controladores, y sitúa funcionamiento interno demasiado tarde.
- La página pública de UD2a no enlaza recorrido, RA/CE, seguridad, proyecto canónico ni criterio de cierre.
- Battleship dispone de un contrato OpenAPI 3.1 versionado y canónico; Springdoc generado está deshabilitado y Swagger UI carga únicamente ese recurso.

### Evidencia de ejecución

- El 23 de julio de 2026, `./mvnw clean test` ejecutó 49 pruebas verdes con OpenJDK 25.0.3 tras retirar la migración del administrador conocido.
- Tras migrar el build y añadir `PageResponse`, `./mvnw clean test` ejecuta 50 pruebas verdes con `release 25` y ya no aparece la advertencia por serialización directa de `PageImpl`.
- El 24 de julio de 2026, `./mvnw clean verify` y el build Docker con Java 25 ejecutaron 50 pruebas verdes. La inspección del JAR no encontró `keys/`, PEM ni almacenes de claves, y la exportación de la imagen no encontró material de claves bajo `/app`.
- H2 queda fijado en 2.3.232, última versión que la versión de Flyway utilizada declara verificada; las migraciones pasan sin advertencia de compatibilidad.
- `Game.active` usa `@Builder.Default` y una prueba directa confirma que el builder conserva `true` antes de persistir.
- En ese corte del 24 de julio, `./mvnw clean verify` ejecutó 51 pruebas verdes con Java 25, sin las advertencias de H2/Flyway ni Lombok.
- El 25 de julio de 2026, la suite vigente `./mvnw clean verify` ejecutó **136 pruebas verdes**: estructura/publicación del contrato, conformidad MockMvc de las nueve operaciones y errores representativos, y **27 casos** de seguridad/autorización, incluida la lista CORS exacta. El JAR contiene `BOOT-INF/classes/static/api-docs/battleship-v1.yaml` y no contiene entradas de claves ni marcadores PEM. `mkdocs build --strict` terminó correctamente.
- La regresión de token emitido por login se integra deliberadamente en `SecurityAuthorizationIntegrationTest`: prueba el recorrido real login → token → solicitud protegida dentro de la matriz de roles, evitando un fixture duplicado en un `LoginTokenIntegrationTest` aislado.

### Rutas opcionales

- .NET ocupa cientos de MB locales por `bin/`, `obj/`, bases SQLite y metadatos; mezcla .NET 8 y 10 y contiene tareas obligatorias incompatibles con su papel opcional.
- Los proyectos .NET útiles son gitlinks irreproducibles y mantienen cambios locales; no deben publicarse desde el repositorio principal en este estado.
- GraphQL es un único documento extenso sin proyecto, versión fijada ni pruebas, con APIs Hot Chocolate incompatibles entre secciones.

## Alcance de cierre

### Obligatorio

- Ruta canónica Spring Boot 4/Java 25 coherente y enlazada.
- Battleship reproducible, sin credenciales conocidas ni claves empaquetadas.
- Tests de dominio, HTTP, persistencia y seguridad verificados.
- OpenAPI versionado con autenticación y errores; los ejemplos representativos
  de requests/responses permanecen como una tarea separada hasta completarlos.
- Flujo SDD trazable desde requisitos hasta pruebas de conformidad.
- Seguridad moderna con 401/403, ownership/BOLA, CORS, CSRF, JWT y gestión de claves justificadas.
- Matriz RA/CE a-g/evidencias para la unidad y sus ejercicios.
- Publicación limitada a material intencional y revisado.

### Fuera del núcleo

- Mantener varias APIs completas como proyectos conductores.
- Evaluar .NET o GraphQL como recorridos equivalentes a Spring Boot.
- GraalVM, Kubernetes, BSON y persistencias alternativas como requisitos de cierre.
- Publicar soluciones, archivos generados, secretos de demostración o bases locales.

## Plan por prioridad

### P0 - contención y exactitud

- [x] Auditar documentación, proyectos, seguridad y rutas opcionales.
- [x] Excluir temporalmente de MkDocs claves y material no verificado de seguridad, .NET y GraphQL.
- [x] Eliminar la credencial administrativa conocida de las migraciones normales.
- [x] Externalizar las claves JWT y verificar que claves/keystores no entren en JAR ni imagen.
- [x] Corregir slicing y migración SB3/SB4 según los módulos, paquetes y requisitos oficiales de Spring Boot 4.
- [x] Consolidar los doce gitlinks huérfanos como directorios versionados: los proyectos Spring Boot, .NET, PHP y Laravel ya no requieren `.gitmodules`, con exclusión de registros, artefactos, ejecutables locales y credenciales.

### P1 - ruta canónica

- [x] Fijar Java 25 en el proyecto canónico Battleship; queda revisar ejemplos y documentación secundaria.
- [x] Estabilizar el contrato JSON paginado de Battleship mediante `PageResponse` y una prueba HTTP dedicada.
- [x] Alinear H2/Flyway con H2 2.3.232, combinación declarada verificada y probada por la suite.
- [x] Resolver la inicialización ignorada por `@Builder` en `Game` y probar el valor por defecto.
- [x] Reordenar y consolidar la documentación en una única ruta canónica teoría -> Battleship -> ejercicios; los apuntes paralelos, experimentos y demo mínima están clasificados como material histórico o sandbox no evaluable.
- [x] Completar README, página pública, RA/CE y guía de evaluación específica de UD2; las evidencias publicadas suman el 70 % y el instrumento del 30 % restante queda pendiente de comunicación docente, sin asignarlo artificialmente.
- [x] Verificar Battleship desde wrapper, perfiles y base limpia: desde un clon nuevo, `./mvnw clean verify` pasó con 141 pruebas, `dev` respondió en HTTP y `prod,docker` arrancó por HTTPS con PostgreSQL 16, Flyway y claves externas efímeras con permisos restringidos.
- [x] Convertir la seguridad HTTP en un contrato canónico y probado; la autorización por claims conserva su suite específica de roles.

### P2 - SDD y contrato

- [x] Diseñar y versionar OpenAPI 3.1 como fuente de verdad en `static/api-docs/battleship-v1.yaml`.
- [x] Añadir autenticación, roles esperados, errores y cabeceras al contrato.
- [x] Completar y validar ejemplos representativos de requests/responses en todos los esquemas, parámetros, cabeceras y respuestas del contrato canónico.
- [x] Verificar conformidad entre contrato e implementación con parser y Atlassian MockMvc para las nueve operaciones.
- [x] Documentar requisitos, decisiones, tareas, trazabilidad TDD y comandos de evidencia SDD en la [traza versionada para el alumnado](ud02a-spring-boot/02-ejemplos/battleship/docs/10-sdd-openapi.md).

### P3 - opcionales y limpieza

- [x] Reducir .NET a una demostración limpia, fijada y no evaluable: TodoApi sobre .NET 10 es la única referencia verificable; MiApi, RecetasApi y la guía de despliegue quedan clasificados como históricos.
- [ ] Sustituir GraphQL por una demostración mínima ejecutable o retirarla.
- [ ] Eliminar material generado, duplicado, conversacional y obsoleto.
- [ ] Clasificar ejercicios y previews sin competir con Battleship.

## Criterio de cierre

- Un clon limpio reproduce todo el material obligatorio.
- No hay secretos ni credenciales conocidas en artefactos desplegables.
- Java 25/Spring Boot 4 y los tests están verificados.
- OpenAPI y SDD tienen artefactos versionados y evidencia automática.
- Seguridad y RA/CE están trazadas a pruebas y actividades.
- .NET/GraphQL están claramente aislados como opcionales.
- MkDocs no publica material interno, soluciones ni archivos sensibles.
