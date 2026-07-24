# Estado del proyecto — DWES 2026/2027

*Última actualización: 24 julio 2026*

---

## Hecho (anteriores sesiones)

- [x] Inventario bruto y consolidado de `Unidades/` original
- [x] Estructura común de unidades definida y documentada
- [x] Copia reorganizada de TODO el material a la nueva estructura
- [x] Battleship depurado (solo pedagogía, código IA descartado)
- [x] GeoNotes consolidado de 8 a 2 versiones
- [x] Plantillas compartidas (IA, rúbrica, RA/CE, README) en `00-recursos-comunes/`
- [x] Infraestructura mkdocs + GitHub Pages (mkdocs.yml, hooks, symlinks)
- [x] README.md raíz del módulo
- [x] Prompts base de reforma para DWES (00–04)
- [x] Decisiones marco pendientes documentadas
- [x] HTTP Ejercicio 01: añadido `httpie` como alternativa didáctica a `curl`
- [x] Reorganizados los 3 ejercicios de `ud02a-spring-boot/03-ejercicios/`
- [x] Movido `tareaMVC` (cuestionario Thymeleaf) a `ud03-mvc-spring-boot/05-cuestionarios/`

## Hecho (5 julio 2026)

### Fase 1 — Documentación ud02a-spring-boot (limpieza + reestructuración)
- [x] Eliminado duplicado `UD2_Cap3-Sesion1.md` (idéntico a `00-BattleShip.md`)
- [x] Thymeleaf movido a UD3 (controladores+vistas y vistas-thymeleaf)
- [x] Creado README/index.md con orden TDD-first
- [x] Renumeración secuencial 01–12 de todos los documentos
- [x] Limpiados artefactos IA de `07-funcionamiento-spring-boot.md`
- [x] HTMLs movidos a subcarpeta `_html/`
- [x] Anexos renombrados (`anexo-*`)

### Fase 2 — Demo migrada a Spring Boot 4
- [x] `pom.xml`: SB 3.5.6 → 4.0.5
- [x] `spring-boot-starter-web` → `spring-boot-starter-webmvc`
- [x] `springdoc` 2.2.0 → 3.0.3
- [x] Eliminadas dependencias y archivos de Thymeleaf
- [x] Compilación y 2 tests verdes

### Fase 3 — Battleship con TDD + Flyway
- [x] Proyecto `battleship/` con SB 4.0.5 + Flyway + H2 + JPA + Lombok + SpringDoc
- [x] V1 Flyway migration (game, ship, attack)
- [x] Domain entities (Game, Ship, Attack) con `@Column(name = "...")` explícito
- [x] DTOs (CreateGameDTO, PlaceShipDTO, AttackDTO, GameResponseDTO)
- [x] Repositories (GameRepository, ShipRepository, AttackRepository)
- [x] GameService con lógica completa (crear, colocar, atacar, hundir, ganar)
- [x] GameController con API REST completa
- [x] GlobalExceptionHandler
- [x] **Corte histórico del 5 julio: 18 tests verdes** (6 controller MockMvc + 11 service integración + 1 context); no representa el recuento vigente.
- [x] Solucionado Flyway en SB4 (`spring-boot-starter-flyway`)
- [x] Solucionado Hibernate 7 naming strategy (`@Column` explícito)
- [x] Script `demo.sh` (curl)
- [x] Colección Insomnia `insomnia-battleship.json`

## Hecho (6-7 julio 2026)

### Fase 4 — Migración resto proyectos SB4
- [x] Verificado: book-catalog-template y mini-tasks ya estaban en SB 4.0.5
- [x] `demojpa` retirado de pendientes (nunca llegó a existir como proyecto)
- [x] GestionEventos migrado a SB 4.0.5:
  - `build.gradle`: plugin `3.5.7` → `4.0.5`, `starter-web` → `starter-webmvc`, añadido `webmvc-test`
  - 3 controlador tests: `@WebMvcTest` → `org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest`
  - 3 controlador tests: `@MockBean` → `@MockitoBean` (`org.springframework.test.context.bean.override.mockito.MockitoBean`)

### Fase 5 — Migración SB4 proyectos 06-seguridad/
- [x] `Ejemplo1` (SB 3.3.3 → 4.0.5, starter-web → webmvc)
- [x] `GestionBiblioteca` (SB 3.3.4 → 4.0.5, starter-web → webmvc, Java 17→21, jjwt 0.11.5→0.12.5)

### Fase 6 — Limpieza documentación ud02a
- [x] Marcas IA: limpiadas 2 ("¡Buena pregunta!", "¡Perfecto!")
- [x] Typos corregidos: 3 archivos renombrados
- [x] Monolitos revisados y clasificados (guías de solución se mantienen)
- [x] V1/V2 documentadas y mantenidas ambas
- [x] PorVer/ reorganizado en 3 niveles pedagógicos (fundamentos/intermedio/avanzado) con 23 carpetas temáticas
- [x] Eliminados .tex, .html y auxiliares de compilación (mantenidos .org)

### Fase 7 — Plantillas ejercicios
- [x] `02-book-catalog/`: README, rúbrica, RA/CE creados
- [x] `03-gestion-eventos/`: README, rúbrica, RA/CE creados

### Fase 8 — Documentación de seguridad
- [x] Creado `06-seguridad/README.md` con arquitectura general, CORS, perfiles y propiedades

### Fase 9 — Cuestionarios GIFT semanales
- [x] `ud02a_semanas_1_4.gift`: 12 preguntas (docs 01-06)
- [x] `ud02a_semanas_5_8.gift`: 8 preguntas (seguridad, Battleship, migración)

## Hecho (14 julio 2026)

### Fase 10 — Decisiones estratégicas e instalación de Laravel 12
- [x] Seleccionado definitivamente **Laravel (PHP)** como segundo framework del módulo, dejando .NET (C#) como ejemplo introductorio.
- [x] Documentados los pasos de instalación multiplataforma para Laravel 12 con Sail en `ud05-laravel/02-ejemplos/sail/Laravel12-api/003-Laravel12-API_REST-Recetas.md`, resolviendo el gotcha de permisos en Linux (`chown`) y detallando el requisito de WSL2 en Windows.
- [x] Decidido el futuro de **GraphQL (U2c)**: se mantendrá solo como demostración introductoria de GraphQL en lugar de unidad completa, basándonos en sus desventajas arquitectónicas y el auge del verbo HTTP QUERY.
- [x] Decidido el tratamiento de **FastAPI / PIA**: Python/FastAPI se ofrecerá únicamente como ejemplo sencillo opcional de autoestudio (ejemplo de servicio de modelos de IA) para su uso en proyectos finales, aclarando que el desarrollo profundo de IA pertenece a la especialización post-DAW.
- [x] Creada **rúbrica común del módulo por RA/CE** (versión inicial para refinar): `00-planificacion/rubrica_comun_DWES_por_RA_CE.md`. Incluye los 9 RA con 4 niveles de logro (IN/SU/NT/SB) orientados a arquitectura limpia, TDD y seguridad.

## Hecho (16 julio 2026)

### Fase 11 — Inventario de UD3 MVC

- [x] Inventariadas documentación, ejemplo, ejercicio, cuestionario, seguridad, proyecto y material docente.
- [x] Creado `ud03-mvc-spring-boot/INVENTARIO_REFORMA.md` con clasificación, riesgos, huecos y plan P0–P3.
- [x] Se detectaron dos proyectos heredados en Spring Boot 3.4.x / Java 17; ambos están ya migrados a Spring Boot 4.0.5 / Java 25.
- [x] Identificada la mezcla entre starter, implementación y solución del ejercicio Productos.
- [x] Identificados como privados `05-cuestionarios/`, `99-profesor/` y soluciones ignoradas.
- [x] Actualizadas las decisiones pendientes: se retiraron afirmaciones obsoletas sobre repositorio y rúbrica.

---

## Estado actual y pendientes

### 1. Migración SB4 — ✅ CERRADA EN UD3

Migración UD3 cerrada: Productos y Gestor de tareas usan Spring Boot 4.0.5 y Java 25. No se mantiene todavía un proyecto comparativo SB3/SB4.

### 2. Documentación ✅ COMPLETADA

Marcas IA, typos, monolitos, V1/V2 y PorVer/ resueltos.

### 3. Ejercicios — completar plantillas ✅ COMPLETADA
- [x] `02-book-catalog/`: README, rúbrica, RA/CE creados
- [x] `03-gestion-eventos/`: README, rúbrica, RA/CE creados

### Notas de diseño
- [x] Battleship: creado recorrido canónico paso a paso alineado con la progresión de UD2a (contrato → TDD → dominio/DTO → servicio → controlador → Flyway/JPA → errores → seguridad → integración).
- [x] Battleship: corregidos los enlaces relativos de sus ampliaciones y diferenciada la documentación generada por SpringDoc de un futuro contrato OpenAPI/SDD.
- [x] Battleship: Mockito configurado explícitamente como agente Java mediante `maven-dependency-plugin` y Surefire; la suite vigente ejecuta **51/51 tests verdes en Java 25**.
- [x] Battleship: añadido contrato de solapamiento cruzado con evidencia RED contra una mutación sin validación y GREEN con la implementación existente.
- [x] Battleship: matriz mínima de acceso demostrada con filtros reales: consultas públicas, `401` sin autenticación, `403` con rol insuficiente, mutaciones de juego para `PLAYER` y cancelación para `ADMIN`.
- [x] Battleship: endurecidos login, refresh y filtro JWT; hay pruebas bearer reales para `PLAYER`/`ADMIN`, claims inválidos, token malformed/expirado y refresh usado como access. Swagger/API docs y Actuator requieren autenticación; CORS comparte configuración externalizada para `/api/**` y `/auth/**`.

### 4. Seguridad — documentación de arquitectura ✅ COMPLETADA
- [x] Creado `06-seguridad/README.md` con: arquitectura general, componentes clave, CORS, perfiles/propiedades y referencias a docs existentes

### 5. Cuestionarios GIFT ✅ COMPLETADA
- [x] `ud02a_semanas_1_4.gift`: 12 preguntas (Spring Boot, TDD, REST, servicios, errores, slicing)
- [x] `ud02a_semanas_5_8.gift`: 8 preguntas (seguridad JWT, Battleship, migración SB3→SB4)

### 6. U6 — Aplicaciones híbridas (notas de diseño)
- [ ] Incluir Spring Boot para conectar a modelos de lenguaje (Spring AI, LLM integration) — anotado 6 julio 2026

### 7. Decisiones marco (de DECISIONES_PENDIENTES.md)
- [x] Segundo framework (Laravel/PHP seleccionado como secundario, .NET como anexo)
- [ ] Unidad Proyecto Integrador
- [x] Tratamiento de FastAPI / PIA (relegado a ejemplo opcional de lectura/autoestudio)
- [ ] Definir tamaño máximo de grupos y criterios de formación
- [ ] Decidir publicación de enunciados de ejercicios/proyectos en GitHub Pages
- [x] Crear rúbrica común del módulo por RA/CE (v1 creada — pendiente de refinar)
- [x] Decidir futuro de U2c (GraphQL) (relegado a demostración introductoria)
- [ ] Decidir futuro de U6 (Híbridas)

### 8. Reforma de UD3 — ✅ NÚCLEO COMPLETADO

- [x] Thymeleaf elegido como motor principal del recorrido obligatorio.
- [x] Gestor de tareas definido y reconstruido como ejemplo integrador guiado posterior a Productos.
- [x] Productos reformado: starter público incremental, itinerario único y solución final privada.
- [x] Primer corte de seguridad delimitado: sesión, CSRF, validación y autorización por propietario; registro y roles quedan fuera.
- [x] Documentación consolidada en seis guías canónicas y un índice de unidad con objetivos, prerrequisitos, RA/CE y secuencia completa.
- [x] `06-seguridad/README.md` versionado con CSRF, XSS, validación, sesión y autorización por propietario.
- [x] GIFT y rúbrica privada corregidos; `99-profesor/` permanece local, ignorado y respaldado en unidad externa.
- [x] Gestor reconstruido con progresión segura, tests y SB4/Java 25; Productos mantiene su progresión incremental.

### Fase 12 — Reconstrucción del Gestor de tareas UD3 (16 julio 2026)

- [x] Reconstruido como ejemplo integrador posterior a Productos con SB 4.0.5, Java 25, Gradle 9.1 y Thymeleaf.
- [x] CRUD limitado al propietario con DTO/form object, validación Jakarta y servicio de casos de uso.
- [x] CSRF activo y logout/mutaciones mediante POST; sin credenciales precargadas ni consola H2.
- [x] 10 pruebas verdes: aislamiento y flujo propietario, acceso y mutaciones ajenas, alta y validación, edición inválida sin mutación, autenticación BCrypt correcta/incorrecta, formularios CSRF, logout con/sin token y perfil demo.
- [x] Tutorial acumulativo sustituido por guía canónica; ideas clasificadas como backlog docente; HTML derivado eliminado.
- [ ] Siguiente corte opcional: registro seguro y después búsqueda/filtros por propietario. No forman parte del núcleo actual ni bloquean el cierre de UD3.

### Fase 13 — Consolidación documental y evaluativa de UD3 (16 julio 2026)

- [x] Sustituidos cinco documentos heredados solapados por una progresión Markdown numerada: MVC, controladores/vistas, Thymeleaf, formularios/PRG y persistencia.
- [x] Mustache reducido a comparación opcional; retiradas referencias activas a UD2, `web.xml`, Spring Boot 3 y Java 17.
- [x] Índices público y de unidad alineados con la secuencia hasta el Gestor seguro.
- [x] HTML derivados eliminados; Markdown queda como fuente canónica.
- [x] Backlog del Gestor trasladado al material docente privado.
- [x] Decisión operativa de `99-profesor/`: navegación local dentro de cada unidad, exclusión de Git y MkDocs, respaldo externo.

### Fase 14 — Publicación MkDocs estricta (16 julio 2026)

- [x] `mkdocs build --strict` termina correctamente: el `README.md` del cliente Vue de Laravel 10 queda como fuente documental canónica y su `index.html` de Vite se excluye únicamente del sitio generado para evitar la colisión de destino.

### Fase 15 — Primer corte de reforma de UD4 PHP (16 julio 2026)

- [x] PHP 8.4 fijado como baseline de la unidad.
- [x] Creado `ud04-php/INVENTARIO_REFORMA.md` y un recorrido Markdown canónico: fundamentos → formularios → sesiones → PDO → CRUD procedural → OOP/capas → GTask → Laravel 12.
- [x] Retirados del recorrido los PDF, TeX, HTML, ZIP, temporales y copias derivados; las fuentes históricas quedan en `90-archivo/`, ignorado.
- [x] El concesionario de 2022 deja de presentarse como referencia ejecutable y se usa únicamente como caso de análisis de deuda.
- [x] Material `-profe` y GIFT trasladado a `99-profesor/`, excluido de Git y MkDocs.
- [x] Publicado el contrato transversal de seguridad de UD4.
- [x] Reconstruido GTask en `ud04-php/04-proyectos/gtask-php84`: starter incremental público, contratos RED activables y solución final local en `99-profesor/`.
- [x] Cubiertos en la solución validación, autenticación con hash, sesión segura, CSRF, PDO preparado y aislamiento por propietario.
- [x] Validado GTask con PHP 8.4.17 y PHPUnit 11.5.56: **20 pruebas y 86 aserciones verdes**; starter base con 2 pruebas verdes y cinco checkpoints RED ejecutables.
- [x] Preservado sin cambios el gitlink heredado (`356c434`), incluido su `SEGUIMIENTO.md` modificado y `.env` local.
- [x] Añadida la práctica procedural segura Notes: primera alta guiada por el docente, cuatro checkpoints RED y solución final privada; cubre CRUD PDO/SQLite, validación, CSRF, PRG, rutas, escape XSS y errores genéricos sin añadir autenticación.
- [x] Validada la práctica Notes con PHP 8.4.17 y PHPUnit 11.5.56: **19 pruebas y 53 aserciones verdes**, starter con 2 pruebas verdes y cuatro checkpoints RED ejecutables sin warnings.
- [x] UD4 cerrada para impartición. La importación real del GIFT en Moodle continúa como verificación operativa no bloqueante y no se presenta como realizada.

### Fase 16 — Cierre de sesión (17 julio 2026)

- [x] UD2, UD3 y UD4 quedan con sus núcleos técnicos y documentales reformados; MkDocs estricto termina correctamente.
- [x] Verificado que SDD ya estaba previsto para unidades intermedias y para la futura evolución contractual de Battleship.
- [x] Detectado que Clean Architecture solo aparece en la rúbrica: falta diseñar una práctica guiada SDD → TDD → CA, preferiblemente con Spring Boot.
- [x] Reforma de UD5 Laravel 12 iniciada y cerrada para impartición en sesiones posteriores.
- [ ] Diseñar después la práctica guiada de Clean Architecture sin sobrecargar las unidades iniciales.
- [ ] Importar el GIFT de UD4 en Moodle para validar el banco en la plataforma real.
- [x] UD1 auditada y cerrada formalmente para impartición.

### Fase 17 — Consolidación y publicación incremental (24 julio 2026)

- [x] UD1 cerrada para impartición y confirmada mediante cortes funcionales.
- [x] UD2 auditada, migrada a Java 25 y endurecida: paginación estable, claves JWT externas, perfiles seguros, H2/Flyway alineados y 51 pruebas verdes.
- [x] UD3 consolidada en tres cortes: Gestor seguro con Thymeleaf, starter Productos y recorrido documental canónico.
- [x] UD4 consolidada en dos cortes: starters PHP 8.4 verificables y recorrido documental canónico.
- [x] UD0 migrada a Java 25: GeoNotes con 15 pruebas, Calc25 con 4 pruebas y wrappers reproducibles.
- [x] Todos los cortes anteriores se publicaron en `origin/main`; `mkdocs build --strict` continúa terminando correctamente.
- [x] Incorporada la regla operativa de verificar, confirmar y subir cada unidad funcional relevante antes de cambiar de contexto.
- [ ] Prioridad siguiente: completar el contrato OpenAPI/SDD de UD2 y cerrar después los pendientes restantes de UD3.
