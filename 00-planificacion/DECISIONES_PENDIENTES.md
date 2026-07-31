# Decisiones pendientes — DWES 2026/2027

Decisiones identificadas durante el inventario y la planificación que deben resolverse antes o durante la reorganización.

## 1. Migración a Spring Boot 4 (✅ CERRADA EN UD3)

- **Decisión**: ✅ Migrar todo el material de Spring Boot a SB4.
- **Ejecutado**: Demo de ud02a migrada a SB 4.0.5 (`spring-boot-starter-webmvc`, Jackson 3, `@MockitoBean`, Flyway starter). Battleship creado directamente en SB4.
- **Lecciones aprendidas**:
  - SB4 eliminó `FlywayAutoConfiguration` de `spring-boot-autoconfigure`. Requiere `spring-boot-starter-flyway` explícito.
  - Hibernate 7 cambió la naming strategy por defecto (ya no convierte camelCase a snake_case). Usar `@Column(name = "...")` explícito.
  - `@WebMvcTest` se movió a `org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest`.
  - `@MockitoBean` está en `org.springframework.test.context.bean.override.mockito.MockitoBean`.
- **Lección adicional**: El compilador debe incluir `-parameters` (`<parameters>true</parameters>` en `maven-compiler-plugin`) para que SB4 resuelva nombres de parámetros en `@RequestParam`. Sin esto, devuelve 404 silencioso.
- **Completado el 6 julio de 2026**: book-catalog-template, mini-tasks y GestionEventos quedaron en Spring Boot 4.0.5.
- **Estado del 16 julio de 2026**: Productos y Gestor de tareas usan Spring Boot 4.0.5 y Java 25. El comparativo SB3/SB4 se decidirá más adelante y no bloquea UD3.

## 2. Estrategia de frameworks

Actualmente DWES tiene: Spring Boot (Java), .NET (C#), GraphQL (.NET), PHP, Laravel.

A decidir:

### 2.1 Framework principal (backend pesado)
- ✅ **Spring Boot** — se mantiene como principal.
- Migrar a Spring Boot 4 (decisión 1).

### 2.2 Segundo framework (✅ DECIDIDO)
- **Decisión**: Elegir **Laravel (PHP)** como segundo framework oficial del módulo.
- **Motivación**:
  - Se alinea de forma rigurosa con los Resultados de Aprendizaje (RA) y Criterios de Evaluación (CE) del Real Decreto de la asignatura.
  - Ofrece variedad conceptual didáctica a los alumnos (lenguaje de scripting y tipado dinámico vs Java, patrón ActiveRecord/Eloquent vs DataMapper/JPA, y configuración explícita en Laravel 12 vs autoconfiguración implícita/anotaciones en Spring Boot).
  - Evita la redundancia conceptual que supondría impartir .NET (C#) como framework completo, dado que su arquitectura y estructura (tipado estático, compilación, anotaciones/atributos y DI) son muy similares a las de Spring Boot.
  - **Plan de acción**: .NET queda relegado a un ejemplo introductorio opcional ("para que vean la sintaxis"), y Laravel 12 se imparte y evalúa de forma completa.

### 2.3 Python y FastAPI (✅ DECIDIDO)
- **Decisión**: Python/FastAPI no tendrá unidad propia ni será obligatorio. Se incluirá únicamente como un **ejemplo sencillo de lectura/autoestudio**.
- **Motivación**:
  - Aunque FastAPI es el estándar en la industria para servir modelos de Machine Learning (PIA), ese contenido corresponde propiamente al Curso de Especialización en Inteligencia Artificial y Big Data (post-DAW).
  - Dejar un ejemplo básico sirve para que los alumnos entiendan la sintaxis y lo puedan usar de forma opcional en su Proyecto Integrador si lo desean, sin sobrecargar las horas lectivas obligatorias del módulo.

### 2.4 Node / Express

Hay un módulo exclusivo del stack MERN, por lo que **no duplicar**. Node/Express queda fuera del alcance de DWES.

## 3. TDD como metodología base (✅ EN MARCHA)

- **Desde la primera práctica** de la primera unidad: test rojo → implementar → test verde.
- Empezar con tests unitarios simples, avanzar a tests de integración y API.
- Más adelante introducir **spec-driven development** (contratos OpenAPI, guardrails).
- El alumno debe interiorizar que el test es parte del código, no un añadido.
- **Avance histórico (5 julio)**: Battleship alcanzó entonces 18 tests (6 controller + 11 service + 1 context). El recuento vigente se mantiene en la sección 9. Demo de SB4 migrada manteniendo tests existentes (2 test suites).

## 4. Uso de IA en el módulo

Progresión:
1. **Primeras unidades**: 0 uso de IA. El alumno escribe código y tests manualmente.
2. **Unidades intermedias**: Se introduce IA como herramienta supervisada (generar tests, explicar errores, refactorizar).
3. **Unidades finales**: IA como asistente integrado en el flujo de trabajo, pero el alumno debe validar, criticar y justificar cada sugerencia.

Objetivo: que aprendan a usar IA **con criterio**, no a delegar ciegamente.

## 5. Seguridad transversal (especialización del centro)

- Cada unidad incorpora contenidos de seguridad específicos en `06-seguridad/`.
- No relegar la seguridad a una unidad final.
- Áreas a cubrir por unidad (propuesta inicial):

| Unidad | Contenidos de seguridad |
| ------ | ----------------------- |
| U1 (HTTP) | Seguridad en cabeceras HTTP (CSP, HSTS, X-Frame-Options), HTTPS, TLS |
| U2a (API REST, Spring) | JWT, OAuth2, CORS, validación de entrada, OWASP Top 10 API |
| U2b (.NET) | Mismos conceptos pero en .NET: Identity, JWT, CORS |
| U2c (GraphQL) | Seguridad específica de GraphQL (query depth, rate limiting, auth) |
| U3 (MVC) | CSRF, XSS, seguridad en formularios, autenticación basada en sesión |
| U4 (PHP) | SQL injection, XSS, validación server-side, password hashing |
| U5 (Laravel) | Seguridad integrada de Laravel (Elixir, policies, gates) |
| U6 (Híbridas) | Seguridad en APIs públicas, rate limiting, API keys |

## 6. Nuevo verbo HTTP QUERY

- **RFC publicado**: Junio 2026.
- **Propósito**: Pasar parámetros de consulta en el BODY de la petición en vez de la URL, evitando URLs enormes con GET o el mal uso de POST para consultas.
- **Decisión**: ✅ Incluirlo al enseñar los verbos HTTP en U1, haciendo énfasis en que es una novedad (RFC junio 2026). A medida que los frameworks lo adopten (Spring Boot ya tiene soporte experimental con custom verbs), se irá incorporando en las unidades correspondientes. Por ahora, enfoque conceptual + seguimiento de adopción en cada framework.

## 7. Ajuste de servidor embebido (Tomcat) (✅ UBICACIÓN RESUELTA)

- **Contexto**: Todos los proyectos Spring Boot del módulo arrancan con Tomcat embebido por defecto (salvo que se cambie a Jetty/Undertow). Nunca se han modificado parámetros de rendimiento ni seguridad del contenedor.
- **Propuesta**: Incluir un bloque específico al final de la unidad U2a (o en el proyecto Battleship) que cubra:
  - **Rendimiento**: `server.tomcat.max-threads`, `server.tomcat.max-connections`, `server.tomcat.connection-timeout`, compresión (`server.compression.enabled`).
  - **Seguridad**: `server.tomcat.max-http-header-size`, `server.tomcat.max-http-post-size`, `server.tomcat.max-swallow-size`, HTTP-only cookies, cabeceras de seguridad (HSTS, CSP, X-Frame-Options via Tomcat valves o filtros Spring Security).
  - **Configuración programática**: `TomcatServletWebServerFactory` para personalizar el conector (HTTPS, puertos, compresión).
- **Ubicación adoptada**: ampliación final de Battleship en `02-ejemplos/battleship/docs/09-docker-tomcat.md`, enlazada desde su índice por sesiones.
- **Estado**: la ubicación deja de ser una decisión pendiente. Su profundidad y actualización son mantenimiento didáctico ordinario.

## 8. Depuración de versionados anteriores (✅ RESUELTA)

Del inventario:

| Elemento | Resolución |
| -------- | ------ |
| Battleship | Depurado y reconstruido; los históricos dejaron de ser una decisión abierta |
| GeoNotes | Consolidado de ocho a dos versiones |
| Material heredado | Reorganizado en la nueva estructura; cualquier limpieza restante se gestiona por unidad |
| Formatos fuente | Markdown es el formato publicable; los fuentes únicos se conservan solo cuando aportan valor |

## 9. Battleship: reconstrucción con TDD y SDD (✅ IMPLEMENTADO; VERIFICACIÓN SDD PENDIENTE)

- El proyecto Battleship original era un experimento con IA que no funcionó.
- **Reconstruido** con TDD + Flyway + SB4 desde cero en `ud02a-spring-boot/02-ejemplos/battleship/`.
- **Stack**: SB 4.0.5, Flyway, H2, JPA, Lombok, SpringDoc 3.0.3.
- **Suite vigente**: 51/51 tests verdes en Java 25, distribuidos entre controlador, servicio, repositorio, integración, seguridad y contexto. Mockito se carga explícitamente como `-javaagent` desde Surefire; el contrato de servicio incluye solapamiento cruzado de barcos.
- **API REST**: `POST /api/games`, `POST /api/games/{id}/ships`, `POST /api/games/{id}/attacks`, `GET /api/games`, `GET /api/games/{id}`.
- **Validaciones**: fuera de límites, posiciones repetidas, nombres duplicados, barcos solapados.
- **Demo**: script `demo.sh`, colección Insomnia `insomnia-battleship.json`.
- **Documentación**: recorrido incremental canónico creado en `01-documentacion/08-battleship-caso-practico.md`; las guías extensas del proyecto quedan como ampliaciones por sesión y sus enlaces MkDocs están corregidos.
- **Seguridad demostrada**: tests de integración con filtros activos cubren consultas públicas, `401` sin autenticación, `403` con rol sintético insuficiente y bearer tokens reales firmados por `JwtService` para acceso `PLAYER` y `ADMIN`. También cubren credenciales incorrectas, refresh inválido/expirado, refresh usado como access y claims de roles inválidos. CSRF se deshabilita porque la API es stateless y usa bearer tokens, no cookies de sesión. Swagger/API docs y Actuator requieren token; CORS cubre API y autenticación con orígenes externalizados.
- **SDD/OpenAPI**: contrato estático OpenAPI 3.1 versionado como fuente de verdad, pruebas de conformidad y traza versionada implementados. Springdoc generado está deshabilitado y Swagger UI carga únicamente el YAML canónico. Quedan pendientes la repetición formal de la verificación SDD y los ejemplos representativos completos.

## 10. Proyectos grupales

- Se trabajará con proyectos individuales y grupales.
- **Pendiente**: Definir tamaño máximo de grupo. Propuesta inicial: 4 personas.
- Definir criterios de formación de grupos (docente asigna, alumnos se agrupan libremente, mixto).
- Establecer mecanismo de evaluación individual dentro del grupo (para evitar free-riders).

## 11. Publicación de ejercicios y proyectos

- La `01-documentacion/` y `02-ejemplos/` se publican en GitHub Pages vía mkdocs.
- **Pendiente**: Decidir si los README.md de `03-ejercicios/` y `04-proyectos/` también se publican (sin soluciones, claro). La mayoría de centros publican los enunciados para que los alumnos los consulten.
- Los cuestionarios GIFT (`05-cuestionarios/`) NO se publican (son privados del profesor/Moodle).

## 12. Rúbrica común del módulo (✅ RESUELTA EN V1)

- Se creó `00-planificacion/rubrica_comun_DWES_por_RA_CE.md` con los nueve RA y cuatro niveles de logro.
- Su refinamiento es trabajo editorial continuo, no una decisión marco pendiente.

## 13. Unidades infradotadas (✅ DECIDIDO)

- **U2c (GraphQL)**: Se mantendrá únicamente como **ejemplo/demostración introductoria**, sin cargarlo como unidad completa con prácticas evaluables.
  - **Motivación**: Los inconvenientes de GraphQL (complejidad en la resolución del N+1, pérdida de caché HTTP estándar a nivel de proxies/CDNs, problemas de seguridad por profundidad de consultas y la falta de códigos de estado HTTP nativos) suelen superar sus ventajas en proyectos estándar. Además, con la especificación del nuevo verbo **HTTP QUERY** (RFC junio 2026), es posible pasar filtros avanzados en el body de peticiones de lectura sin comprometer la semántica REST, quitándole a GraphQL uno de sus principales argumentos de venta.
- **U6 (Híbridas)**: ✅ Núcleo completado: P0, P1 y P2 incluyen ejemplo, práctica guiada y proyecto evaluable con instrumentos RA9. Spring AI permanece como ampliación opcional P3 (una llamada de chat; no RAG, vectores, MCP ni agentes).

## 14. Repositorio Git y publicación GitHub Pages (PARCIALMENTE RESUELTA)

- **Estado**: El repositorio y la infraestructura MkDocs/GitHub Pages ya existen. Solo permanece abierta la frontera exacta de publicación descrita en la decisión 11.
- **Decisión operativa sobre material docente — ✅ CERRADA**: `99-profesor/` permanece dentro de cada unidad para facilitar su uso en clase. Git lo ignora, MkDocs lo excluye y el respaldo se realiza en una unidad externa. No se creará un repositorio privado ni se usarán ramas como frontera de lectura.
- **Público** (GitHub Pages vía MkDocs): `01-documentacion/`, `02-ejemplos/` y los índices de unidad.
- **Privado/no publicado**: `99-profesor/`, GIFT, soluciones, correcciones y material de evaluación docente.
- **Duda transversal restante**: decidir si los enunciados y starters de `03-ejercicios/` y `04-proyectos/` se incorporan a Pages. Antes de hacerlo se comprobará que no contengan soluciones.
- **Publicación integrada**: La documentación de DWES se publicará en GitHub Pages junto con la de PIA y SBD (misma org, mismo site o sites separados). Decidir si un repo único con subdominios o repos separados.
- **Acción pendiente**: cerrar únicamente la política de publicación de enunciados y starters. La ubicación y el respaldo de `99-profesor/` ya no son decisiones pendientes.

## 15. Reforma de UD3 MVC (✅ NÚCLEO CERRADO)

El inventario completo está en `ud03-mvc-spring-boot/INVENTARIO_REFORMA.md`. Antes de modificar contenido deben resolverse estas decisiones:

1. **Motor principal de plantillas — ✅ DECIDIDO**: Thymeleaf será obligatorio. Mustache solo podrá aparecer como comparación puntual, sin duplicar el recorrido.
2. **Papel del Gestor de tareas — ✅ DECIDIDO Y EJECUTADO**: es el ejemplo integrador guiado posterior a Productos, reconstruido con SB4/Java 25/Thymeleaf y seguridad por propietario.
3. **Práctica Productos — ✅ DECIDIDO Y EJECUTADO**: ejercicio incremental, starter público deliberadamente incompleto, itinerario público único y una sola solución final privada.
4. **Alcance de seguridad — ✅ PRIMER CORTE CERRADO**: el núcleo obligatorio cubre autenticación de sesión, CSRF, validación y autorización por propietario. El registro público queda fuera hasta abordarlo con DTO, codificación de contraseña y tests propios.

5. **Documentación y evaluación — ✅ EJECUTADO**: seis guías Markdown canónicas, índice con RA/CE, guía de seguridad, GIFT corregido y rúbrica privada. Mustache queda como comparación opcional.
6. **Ampliaciones — FUERA DE ALCANCE**: registro, búsqueda, filtros, paginación, roles, AJAX y API REST no bloquean el cierre de UD3.

No quedan decisiones específicas de UD3 abiertas. El comparativo SB3/SB4 se abordará más adelante en un único proyecto y la publicación de enunciados sigue siendo transversal.

## 16. Reforma de UD4 PHP (✅ BASELINE Y RECORRIDO DECIDIDOS)

- **Baseline — ✅ DECIDIDA**: toda la unidad usa PHP 8.4.
- **Metodología — ✅ DECIDIDA**: TDD desde cada incremento y seguridad como contrato transversal.
- **Documentación — ✅ PRIMER CORTE EJECUTADO**: Markdown es la única fuente pública canónica; derivados y fuentes históricas quedan fuera del recorrido.
- **Concesionario 2022 — ✅ DECIDIDO**: no es una solución vigente. Solo se utiliza como caso de análisis de deuda y riesgo.
- **Proyecto conductor — ✅ IMPLEMENTADO**: el nuevo GTask canónico articula persistencia, sesiones, autorización y capas con un starter público incremental y solución final local.
- **Restricción operativa — ✅ RESPETADA**: el gitlink heredado conserva el commit `356c434`, `SEGUIMIENTO.md` modificado y un `.env` no versionado; no se usa como destino de escritura.
- **Material docente — ✅ DECIDIDO**: soluciones y GIFT permanecen en `99-profesor/`, local, ignorado y respaldado externamente.

No queda una decisión conceptual que bloquee UD4. La práctica procedural segura
P2 está implementada y validada como Notes, antes de GTask y sin duplicar su autenticación
o arquitectura por capas. Solo queda validar el banco GIFT mediante
una importación real en Moodle; es una comprobación operativa no bloqueante y no
debe declararse realizada antes de esa prueba.
