# DWES — Desarrollo Web en Entorno Servidor

Módulo de 2º curso del Ciclo Formativo de Grado Superior en **Desarrollo de Aplicaciones Web**.

Curso 2026/2027 — IES Rafael Alberti.

## Estructura

```
00-planificacion/          → Planificación, inventarios, prompts, decisiones
00-recursos-comunes/       → Plantillas compartidas, chuletas, anexos
00-unidad-0-previos/       → Java, MongoDB, PostgreSQL JSON
ud01-introduccion-entorno-servidor/
ud02-api-rest/
  ud02a-spring-boot/       → API REST con Spring Boot 4
  ud02b-dotnet/            → API REST con .NET
  ud02c-graphql/           → GraphQL con HotChocolate
ud03-mvc-spring-boot/      → Spring Boot MVC
ud04-php/                  → PHP básico a OOP CRUD
ud05-laravel/              → Laravel 12
ud06-aplicaciones-hibridas/
ud07-proyecto-integrador/  → Proyecto intermodular
evaluacion-global/         → Exámenes y rúbricas globales
docs/                      → Publicación mkdocs → GitHub Pages
hooks/                     → Hook Python para navegación automática
```

## Estructura interna de cada unidad

```
udXX-nombre/
├── README.md
├── 01-documentacion/      → Apuntes, teoría (se publica)
├── 02-ejemplos/           → Documentación en movimiento (se publica)
├── 03-ejercicios/         → Cada uno con README + rubrica + ra-ce
├── 04-proyectos/          → Proyectos individuales y grupales
├── 05-cuestionarios/      → Cuestionarios semanales GIFT (Moodle)
├── 06-seguridad/          → Seguridad específica de la unidad
├── 90-archivo/            → Material histórico (no publicado)
└── 99-profesor/           → Soluciones, correcciones (no publicado)
```

## Metodología

- **TDD** desde el día 1
- **SDD** (spec-driven development) a partir de UD3-4
- **Seguridad** transversal con carpeta en cada unidad
- **IA**: 0 uso → supervisada → crítica. Declaración obligatoria

## Trazabilidad del trabajo

- Cada unidad mantiene su inventario o lista de pendientes como documento vivo.
- Al completar una tarea, el mismo corte debe actualizar su estado y registrar la evidencia relevante: archivos, pruebas o commit.
- Una tarea no se considera cerrada mientras continúe marcada como pendiente, aunque su implementación ya exista.
- Si el trabajo descubre nuevos pendientes, se anotan antes de cerrar el corte para evitar decisiones implícitas o trabajo invisible.

### Confirmación y publicación de cambios

- Toda incorporación con entidad propia —una funcionalidad, proyecto, recorrido documental, migración o decisión estructural— se verifica y se confirma en Git antes de cambiar de unidad de trabajo.
- Las correcciones pequeñas y relacionadas pueden agruparse, pero se confirman como máximo al cerrar el corte o la sesión; no se acumulan reformas completas sin commit.
- Cada commit representa una unidad funcional revisable e incluye sus pruebas, documentación e inventario cuando correspondan.
- Antes de confirmar se revisan el diff, los archivos nuevos y la ausencia de secretos, soluciones privadas o artefactos generados.
- Tras superar la verificación, los commits se suben al repositorio remoto en lotes pequeños y coherentes, sin esperar a que se acumulen cambios de otras unidades.
- Si un cambio no está listo para publicarse, se registra explícitamente qué falta y se mantiene fuera del commit sin bloquear unidades independientes ya verificadas.

## Publicación

```bash
mkdocs build    # Genera site/
mkdocs serve    # Vista previa local
```

## Enlaces

- [Documentación publicada](https://ies-rafael-alberti.github.io/dwes/)
- [Repositorio](https://github.com/IES-Rafael-Alberti/dwes)
