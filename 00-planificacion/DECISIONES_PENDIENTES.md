# Decisiones pendientes — DWES 2026/2027

Decisiones identificadas durante el inventario y la planificación que deben resolverse antes o durante la reorganización.

## 1. Migración a Spring Boot 4 (✅ COMPLETADA)

- **Decisión**: ✅ Migrar todo el material de Spring Boot a SB4.
- **Ejecutado**: Demo de ud02a migrada a SB 4.0.5 (`spring-boot-starter-webmvc`, Jackson 3, `@MockitoBean`, Flyway starter). Battleship creado directamente en SB4.
- **Lecciones aprendidas**:
  - SB4 eliminó `FlywayAutoConfiguration` de `spring-boot-autoconfigure`. Requiere `spring-boot-starter-flyway` explícito.
  - Hibernate 7 cambió la naming strategy por defecto (ya no convierte camelCase a snake_case). Usar `@Column(name = "...")` explícito.
  - `@WebMvcTest` se movió a `org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest`.
  - `@MockitoBean` está en `org.springframework.test.context.bean.override.mockito.MockitoBean`.
- **Lección adicional**: El compilador debe incluir `-parameters` (`<parameters>true</parameters>` en `maven-compiler-plugin`) para que SB4 resuelva nombres de parámetros en `@RequestParam`. Sin esto, devuelve 404 silencioso.
- **Pendiente (✅ COMPLETADO 6 julio 2026)**: Se verificó que book-catalog-template y mini-tasks ya estaban en SB4. GestionEventos migrado de SB 3.5.7 Gradle a SB 4.0.5. No queda ningún proyecto en SB3.

## 2. Estrategia de frameworks

Actualmente DWES tiene: Spring Boot (Java), .NET (C#), GraphQL (.NET), PHP, Laravel.

A decidir:

### 2.1 Framework principal (backend pesado)
- ✅ **Spring Boot** — se mantiene como principal.
- Migrar a Spring Boot 4 (decisión 1).

### 2.2 Segundo framework
Opción A: **Laravel** (se mantiene como está, actualizar a Laravel 12).
Opción B: **.NET Core Web API** como segundo framework con entidad (sustituyendo a Laravel).
Opción C: Ambos, Laravel como principal de PHP y .NET como ejemplos/tercera vía.

**Consideraciones**: ¿Cuánto tiempo lectivo dedicar a cada uno? ¿Evaluar ambos o solo el principal?

### 2.3 Python como opción

Python está muy extendido y aparece en varios módulos del ciclo. Decidir si:
- Incluir ejemplos/prácticas con Python (FastAPI, Flask) como demostración.
- Dedicar una unidad completa.
- Dejarlo fuera por solapamiento con otros módulos.

### 2.4 Node / Express

Hay un módulo exclusivo del stack MERN, por lo que **no duplicar**. Node/Express queda fuera del alcance de DWES.

## 3. TDD como metodología base (✅ EN MARCHA)

- **Desde la primera práctica** de la primera unidad: test rojo → implementar → test verde.
- Empezar con tests unitarios simples, avanzar a tests de integración y API.
- Más adelante introducir **spec-driven development** (contratos OpenAPI, guardrails).
- El alumno debe interiorizar que el test es parte del código, no un añadido.
- **Avance**: Battleship implementado con TDD (18 tests: 6 controller + 11 service + 1 context). Demo de SB4 migrada manteniendo tests existentes (2 test suites).

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

## 7. Ajuste de servidor embebido (Tomcat)

- **Contexto**: Todos los proyectos Spring Boot del módulo arrancan con Tomcat embebido por defecto (salvo que se cambie a Jetty/Undertow). Nunca se han modificado parámetros de rendimiento ni seguridad del contenedor.
- **Propuesta**: Incluir un bloque específico al final de la unidad U2a (o en el proyecto Battleship) que cubra:
  - **Rendimiento**: `server.tomcat.max-threads`, `server.tomcat.max-connections`, `server.tomcat.connection-timeout`, compresión (`server.compression.enabled`).
  - **Seguridad**: `server.tomcat.max-http-header-size`, `server.tomcat.max-http-post-size`, `server.tomcat.max-swallow-size`, HTTP-only cookies, cabeceras de seguridad (HSTS, CSP, X-Frame-Options via Tomcat valves o filtros Spring Security).
  - **Configuración programática**: `TomcatServletWebServerFactory` para personalizar el conector (HTTPS, puertos, compresión).
- **Dónde ubicarlo**: Propuesta: al final de `02-ejemplos/battleship/` como apéndice de despliegue/configuración, o en `01-documentacion/` como "09-tuning-despliegue.md". Decidir tras ver la carga de la unidad.
- **Estado**: 🔲 Pendiente de decidir ubicación y contenidos concretos.

## 8. Depuración de versionados pendientes

Del inventario:

| Elemento | Acción |
| -------- | ------ |
| Battleship (15 zips + 11 históricos) | Quedarse con 2-3 versiones canónicas. Resto a 90-archivo/ |
| GeoNotes (8 variantes) | Quedarse con 1-2. El resto archivar |
| laravel-24_25/ | Revisar si tiene contenido único; si no, archivar |
| DWES/Unidades/UD2/ | Revisar contenido, decidir integración o archivo |
| Rmd/org/tex → md | Decidir si se migran los apuntes a Markdown como formato único |

## 9. Battleship: reconstrucción con TDD y SDD (✅ COMPLETADO, fase TDD)

- El proyecto Battleship original era un experimento con IA que no funcionó.
- **Reconstruido** con TDD + Flyway + SB4 desde cero en `ud02a-spring-boot/02-ejemplos/battleship/`.
- **Stack**: SB 4.0.5, Flyway, H2, JPA, Lombok, SpringDoc 3.0.3.
- **18 tests** (todos verdes): 6 controller (MockMvc), 11 service (integración), 1 context.
- **API REST**: `POST /api/games`, `POST /api/games/{id}/ships`, `POST /api/games/{id}/attacks`, `GET /api/games`, `GET /api/games/{id}`.
- **Validaciones**: fuera de límites, posiciones repetidas, nombres duplicados, barcos solapados.
- **Demo**: script `demo.sh`, colección Insomnia `insomnia-battleship.json`.
- **Pendiente**: Fase SDD (especificaciones OpenAPI, contratos formales). Por ahora queda con la estructura TDD.

## 10. Proyectos grupales

- Se trabajará con proyectos individuales y grupales.
- **Pendiente**: Definir tamaño máximo de grupo. Propuesta inicial: 4 personas.
- Definir criterios de formación de grupos (docente asigna, alumnos se agrupan libremente, mixto).
- Establecer mecanismo de evaluación individual dentro del grupo (para evitar free-riders).

## 11. Publicación de ejercicios y proyectos

- La `01-documentacion/` y `02-ejemplos/` se publican en GitHub Pages vía mkdocs.
- **Pendiente**: Decidir si los README.md de `03-ejercicios/` y `04-proyectos/` también se publican (sin soluciones, claro). La mayoría de centros publican los enunciados para que los alumnos los consulten.
- Los cuestionarios GIFT (`05-cuestionarios/`) NO se publican (son privados del profesor/Moodle).

## 12. Rúbrica común del módulo

- Existe `plantilla-rubrica-ejercicio.md` para rúbricas por ejercicio.
- **Pendiente**: Crear una rúbrica común del módulo por RA/CE (como la de SBD: `rubrica_comun_SBD_por_RA_CE.md`) que sirva como referencia transversal para todas las evaluaciones.

## 13. Unidades infradotadas

- **U2c (GraphQL)**: Solo 1 documento. Decidir si se amplía o se fusiona con U2b.
- **U6 (Híbridas)**: Solo 1 documento conceptual. Decidir si se expande o elimina.

## 14. Repositorio Git y publicación GitHub Pages

- **Estado**: 🔲 Pendiente de crear el repo Git y decidir qué incluir.
- **Propuesta**: Dividir en dos repos o usar un repo con ramas/subdirectorios para separar lo público de lo privado:
  - **Público** (GitHub Pages vía mkdocs): `01-documentacion/`, `02-ejemplos/`, enunciados de `03-ejercicios/` (los README.md sin soluciones), `00-recursos-comunes/`.
  - **Privado** (no se publica): `99-profesor/` (soluciones, guías docentes con respuestas), `90-archivo/`, exámenes, rúbricas completas.
  - **Dudas**: Los `recursos/` dentro de `03-ejercicios/` contienen proyectos completos (con tests y código). Publicarlos o no depende de si queremos que los alumnos los descarguen como base. Si se publican, las soluciones dentro de esos proyectos quedarían expuestas.
- **Publicación integrada**: La documentación de DWES se publicará en GitHub Pages junto con la de PIA y SBD (misma org, mismo site o sites separados). Decidir si un repo único con subdominios o repos separados.
- **Acción**: Antes de crear el repo, definir la estructura de exclusión (`.gitignore`, subdirectorios no publicados, etc.).
