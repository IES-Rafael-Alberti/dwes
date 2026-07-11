# Informe de copia — DWES 2026/2027

## Resumen

Reorganización del material docente de DWES desde `Unidades/` (original) hacia `DWES_2026_2027_reorganizado/` (copia limpia). No se ha modificado ningún archivo original.

## Estructura final

```
DWES_2026_2027_reorganizado/
├── 00-planificacion/              ← Prompts, inventarios, estructura, decisiones
├── 00-recursos-comunes/           ← Plantillas, chuletas, anexos transversales
│   └── plantillas/                ← Declaración IA, rúbrica, RA/CE, README
├── 00-unidad-0-previos/           ← Java, MongoDB, PostgreSQL JSON (previos)
├── ud01-introduccion-entorno-servidor/
├── ud02-api-rest/
│   ├── ud02a-spring-boot/
│   ├── ud02b-dotnet/
│   └── ud02c-graphql/
├── ud03-mvc-spring-boot/
├── ud04-php/
├── ud05-laravel/
├── ud06-aplicaciones-hibridas/
├── ud07-proyecto-integrador/
├── evaluacion-global/
├── docs/                          ← Publicación mkdocs → GitHub Pages
├── hooks/                         ← nav_generator.py
└── mkdocs.yml
```

Cada unidad sigue la estructura:

```
udXX-nombre/
├── README.md
├── 01-documentacion/      → Apuntes, teoría
├── 02-ejemplos/           → Documentación en movimiento
├── 03-ejercicios/         → Cada uno con README + rúbrica + ra-ce
├── 04-proyectos/          → Individuales y grupales
├── 05-cuestionarios/      → GIFT semanales
├── 06-seguridad/          → Seguridad específica de la unidad
├── 90-archivo/            → Histórico (no publicado)
└── 99-profesor/           → Soluciones (no publicado)
```

## 1. Qué se ha copiado

| Origen | Destino | Contenido |
| ------ | ------- | --------- |
| `Unidades/U1/` | `ud01-introduccion-entorno-servidor/01-teoria/` | Apuntes HTTP, instalación (Rmd + PDF) + plan de sesión |
| `Unidades/U2a_ApiRest_SpBoot/Documentos/` | `ud02-api-rest/ud02a-spring-boot/01-teoria/` | Apuntes Spring Boot, controladores, servicios, tests, anotaciones, Hibernate, sesiones |
| `Unidades/U2a_ApiRest_SpBoot/03-bis-*.md` | `ud02-api-rest/ud02a-spring-boot/01-teoria/` | Vistas Thymeleaf |
| `Unidades/U2a_ApiRest_SpBoot/00-BattleShip.md` | `ud02-api-rest/ud02a-spring-boot/01-teoria/` | Plan de clase REST controllers (salvado de Battleship) |
| `Unidades/U2a_ApiRest_SpBoot/demo/` | `ud02-api-rest/ud02a-spring-boot/02-ejemplos/` | Demo Spring Boot |
| `Unidades/U2a_ApiRest_SpBoot/GestionEventos/` | `ud02-api-rest/ud02a-spring-boot/03-practicas/` | Proyecto gestión eventos |
| `Unidades/U2a_ApiRest_SpBoot/mini-spring-boot-tasks-*/` | `ud02-api-rest/ud02a-spring-boot/03-practicas/` | Proyectos tareas Spring Boot |
| `Unidades/U2a_ApiRest_SpBoot/tarea/` (sin correcciones) | `ud02-api-rest/ud02a-spring-boot/04-evaluacion/` | Tarea JWT, catálogo libros, tarea MVC |
| `Unidades/U2a_ApiRest_SpBoot/SpngBoot-4/` | `ud02-api-rest/ud02a-spring-boot/05-recursos/` | Spring Boot 4 + GraalVM (novedad, pendiente decisión) |
| `Unidades/U2a_ApiRest_SpBoot/Documentacion/SpringBoot/` | `ud02-api-rest/ud02a-spring-boot/05-recursos/` | Documentación adicional |
| `Unidades/U2a_ApiRest_SpBoot/Seguridad/` | `ud02-api-rest/ud02a-spring-boot/06-seguridad/` | Ejemplos seguridad Spring Boot |
| `Unidades/U2a_ApiRest_SpBoot/versiones/` | `ud02-api-rest/ud02a-spring-boot/90-archivo/` | Versiones históricas mini-spring-boot |
| `Unidades/U2a_ApiRest_SpBoot/ProyectoGuia/ProyectoGuia-Curso.md` | `ud02-api-rest/ud02a-spring-boot/99-profesor/` | Planificación docente Battleship (salvado) |
| `Unidades/U2a_ApiRest_SpBoot/ProyectoGuia/*.postman_collection.json` | `ud02-api-rest/ud02a-spring-boot/05-recursos/` | Colecciones Postman (salvadas) |
| `Unidades/U2b_DotNetApiREST/` | `ud02-api-rest/ud02b-dotnet/03-practicas/` | Proyectos .NET (MiApi, RecetasApi, Tareas, ToDo) |
| `Unidades/U2c-GraphQL-HotChocolate-dotNet/` | `ud02-api-rest/ud02c-graphql/01-teoria/` | Apunte GraphQL HotChocolate |
| `Unidades/U3_SpBootMVC/` | `ud03-mvc-spring-boot/` | Apuntes Spring MVC + tareas + ejemplos |
| `Unidades/U4_PHP/` (sin Correccion/ ni repos alumnos) | `ud04-php/` | PHP básico a OOP-CRUD (org/tex/pdf), actividades, tareas, PHP necesario Laravel |
| `Unidades/U5_Laravel/` (sin ProyectoCorreccion/) | `ud05-laravel/` | Apuntes Laravel, proyecto, sail (L10, L11, L12), backup 24/25 a 90-archivo |
| `Unidades/U6/` | `ud06-aplicaciones-hibridas/01-teoria/` | Apunte aplicaciones híbridas |
| `Unidades/Java/` (sin correcciones/) | `00-unidad-0-previos/` | Guión Java 17-21, GeoNotes (2 versiones), calc21, excepciones Java/Kotlin |
| `Unidades/Examenes/` | `evaluacion-global/` | Examen servidor + rúbrica |
| `Unidades/U2a.../ChuletaGIT.md` | `00-recursos-comunes/` | Recurso transversal |
| `Unidades/U2a.../JavaGenéricosEstrDat.md` | `00-recursos-comunes/` | Recurso transversal |

## 2. Qué se ha excluido deliberadamente

| Elemento | Motivo |
| -------- | ------ |
| Battleship: zips, código generado (battleship2/, dwes-battleship/, AutorLibro/, main/, dwes-battleship-*.zip, release-from-changelog.zip, repo-materiales-update.zip, changelog-tools.zip) | AI-generated que no funcionó |
| Battleship: guías AI (ArranqueProyecto.md, ComoTest*.md, uso_zips_battleship.md, FicheroDeTexto.txt) | AI-generated no fiable |
| `U2a_ApiRest_SpBoot/Recupera_y_Mejora/` | Contiene material de alumno (recuperación) |
| `U2a_ApiRest_SpBoot/tarea/correccion*/` y `nueva-copia/` | Artefactos de corrección |
| `U2a_ApiRest_SpBoot/GestionEventos.zip` | ZIP redundante (ya está la carpeta extraída) |
| `U2a_ApiRest_SpBoot/Documentacion/Laravel/` | Documentación Laravel fuera de sitio |
| `U4_PHP/TareaPHP/2dawa_24-25-quizz-app-dwes-php/` | Repositorio de alumnado |
| `U4_PHP/TareaPHP/Correccion/` | Artefactos de corrección |
| `U5_Laravel/ProyectoCorreccion/` (AcedoJavier, VillateAitana) | Repositorios de alumnado |
| `ProyectoConjunto/` (ambos zips + extracción) | Entregas de alumnado |
| `Java/correcciones/` | Artefactos de corrección |
| `Examenes/2574-Prueba Práctica...zip` | Posible entrega de alumno |
| `DWES/Unidades/UD2/` | Estructura huérfana, solo contenía GestionEventos (duplicado) |
| `prompt_inventario_material_modulo.md` (en Unidades/) | Plantilla genérica con placeholders, reemplazada por la versión personalizada en prompts_base_reforma/ |

## 3. Consolidaciones realizadas

| Elemento | Acción |
| -------- | ------ |
| GeoNotes (8 zips → 2) | Se conservan `geonotes-teaching-java21.zip` y `geonotes-java21-with-tests-and-examples.zip`. Las otras 6 variantes van a `90-archivo/geonotes-variants/` |
| Versiones Battleship | Eliminadas todas (15 zips en ProyectoGuia + 11 en versiones/ no relacionadas con Battleship se copiaron a 90-archivo) |
| laravel-24_25/ | Copiado a `ud05-laravel/90-archivo/` como backup histórico |

## 4. Decisiones pendientes que afectan al material copiado

(Ver `00-planificacion/DECISIONES_PENDIENTES.md` para detalle)

- **SpngBoot-4/ en 05-recursos**: Decidir si se integra como material principal (migración a SB4) o se mantiene como experimental.
- **ud05-laravel/**: Decidir si Laravel se mantiene como segundo framework, se sustituye por .NET, o se reduce a ejemplos.
- **Python**: Decidir si incorporar ejemplos/prácticas.
- **GraphQL (ud02c)**: Unidad infradotada (1 doc). Decidir si ampliar o fusionar.
- **ud06-aplicaciones-hibridas/**: Unidad infradotada (1 doc). Decidir futuro.
- **ud07-proyecto-integrador/**: Directorio vacío (ProyectoConjunto solo tenía entregas). Pendiente de crear material.
- **Battleship**: Reconstruir con TDD (simple) y más adelante aumentar con SDD.

## 5. Recomendaciones

1. Antes de usar el material en clase, resolver las decisiones de frameworks (Laravel/.NET/Python).
2. Migrar apuntes de Spring Boot 3 → 4 cuando se decida.
3. Reconstruir el ejemplo práctico de API REST con TDD (Battleship simplificado) para ud02a.
4. Revisar los apuntes en Rmd/org/tex y valorar migración a Markdown.
5. Las colecciones Postman salvadas de Battleship pueden servir como base para tests de la nueva versión TDD.
