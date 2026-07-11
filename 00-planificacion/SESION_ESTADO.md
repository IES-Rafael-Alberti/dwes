# Estado del proyecto — DWES 2026/2027

*Última actualización: 7 julio 2026*

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
- [x] **18 tests verdes** (6 controller MockMvc + 11 service integración + 1 context)
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

---

## Pendiente

### 1. Migración SB4 ✅ COMPLETADA

No queda ningún proyecto en SB3 en todo el módulo.

### 2. Documentación ✅ COMPLETADA

Marcas IA, typos, monolitos, V1/V2 y PorVer/ resueltos.

### 3. Ejercicios — completar plantillas ✅ COMPLETADA
- [x] `02-book-catalog/`: README, rúbrica, RA/CE creados
- [x] `03-gestion-eventos/`: README, rúbrica, RA/CE creados

### Notas de diseño
- [ ] Battleship: crear docs paso a paso alineados con la progresión de documentos (01-introducción → 02-tdd → 03-controladores → ...)

### 4. Seguridad — documentación de arquitectura ✅ COMPLETADA
- [x] Creado `06-seguridad/README.md` con: arquitectura general, componentes clave, CORS, perfiles/propiedades y referencias a docs existentes

### 5. Cuestionarios GIFT ✅ COMPLETADA
- [x] `ud02a_semanas_1_4.gift`: 12 preguntas (Spring Boot, TDD, REST, servicios, errores, slicing)
- [x] `ud02a_semanas_5_8.gift`: 8 preguntas (seguridad JWT, Battleship, migración SB3→SB4)

### 6. U6 — Aplicaciones híbridas (notas de diseño)
- [ ] Incluir Spring Boot para conectar a modelos de lenguaje (Spring AI, LLM integration) — anotado 6 julio 2026

### 7. Decisiones marco (de DECISIONES_PENDIENTES.md)
- [ ] Segundo framework (.NET / Laravel / Python)
- [ ] Unidad Proyecto Integrador
- [ ] Tratamiento de FastAPI / PIA
- [ ] Definir tamaño máximo de grupos y criterios de formación
- [ ] Decidir publicación de enunciados de ejercicios/proyectos en GitHub Pages
- [ ] Crear rúbrica común del módulo por RA/CE
- [ ] Decidir futuro de U2c (GraphQL) y U6 (Híbridas)
