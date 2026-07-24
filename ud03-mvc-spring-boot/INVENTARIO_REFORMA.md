# Inventario y plan de reforma de UD3

**Estado a 16 de julio de 2026:** reforma del núcleo completada. Documentación consolidada, Productos reformado, Gestor reconstruido y guía de seguridad publicada. Las ampliaciones declaradas permanecen fuera de alcance.

Este documento conserva la evidencia del inventario y las decisiones aplicadas. La guía docente de la unidad es `README.md`.

## Resumen ejecutivo

UD3 dispone de una secuencia documental canónica de siete pasos. Sus dos proyectos activos usan Spring Boot 4.0.5 y Java 25. Productos ofrece el recorrido incremental y el Gestor funciona como ejemplo integrador seguro con Thymeleaf.

## Inventario por área

| Área | Contenido actual | Clasificación | Acción prevista |
| --- | --- | --- | --- |
| `01-documentacion/` | 6 guías Markdown numeradas | Canónico y publicado | Mantener la secuencia y evitar monolitos duplicados |
| `02-ejemplos/SpringMVC/` | Gestor seguro con Thymeleaf, JPA, sesión y Spring Security | Ejemplo integrador canónico | Mantener el núcleo seguro; ampliar solo mediante tests |
| `03-ejercicios/` | Itinerario incremental y starter de Productos | Reformado | Mantener sincronizados los contratos públicos y la solución docente privada |
| `04-proyectos/` | Ruta local vacía, no versionada | Fuera del primer corte | El Gestor permanece como ejemplo integrador; no duplicarlo como proyecto |
| `05-cuestionarios/` | 1 GIFT de 12 preguntas, ignorado por Git | Privado y corregido | Mantener fuera de publicación |
| `06-seguridad/` | Guía canónica de seguridad MVC | Canónico y publicado | Mantener enlazada al Gestor sin duplicarlo |
| `90-archivo/` | Ruta local vacía, no versionada | Disponible | Crear la ruta cuando haya fuentes sustituidas que archivar |
| `99-profesor/` | Solución de Productos, rúbrica, backlog y guía docente | Privado local | Mantener ignorado y respaldado en la unidad externa acordada |

## Hallazgos del estado heredado y resolución

Los puntos de esta sección describen el material encontrado **antes de la reforma**. No representan la configuración actual.

### 1. Documentación heredada

- Los tres documentos heredados de Thymeleaf se fusionaron en `03-thymeleaf.md` y `04-formularios-validacion-prg.md`; se conservaron modelo, iteraciones, URL, formularios y errores.
- `SpringMVC.md` se sustituyó por las guías `01`, `02` y `05`; se retiraron `web.xml`, configuración redundante y el CRUD completo que anticipaba Productos.
- Los dos recorridos Mustache repetían dependencias y sintaxis, incluida una versión obsoleta. Se retiraron; solo queda una comparación opcional breve en `03-thymeleaf.md`.
- La antigua `SpringMVC-GestorTareas.md` superaba 3.000 líneas; fue sustituida por una guía canónica breve por etapas.
- `SpringMV-Tareas_IdeasAtrabajar.md` contenía ampliaciones únicas, no un duplicado. Se conservó como `99-profesor/backlog-gestor.md` porque orienta al docente y no pertenece al recorrido público.
- Los HTML derivados verificados fueron eliminados; Markdown es la fuente canónica.
- `README.md` es el índice completo con objetivos, prerrequisitos, RA/CE y secuencia didáctica.

### 2. Gestor heredado — problemas ya resueltos

- Usaba Spring Boot 3.4.0, Java 17, Mustache y el sufijo `.mustache.html`; ahora usa Spring Boot 4.0.5, Java 25 y Thymeleaf.
- Tenía un único test de contexto; ahora dispone de 10 pruebas verdes de flujo, autenticación, autorización, validación, CSRF y perfil demo.
- Deshabilitaba CSRF, habilitaba la consola H2 y precargaba credenciales fijas; la reconstrucción mantiene CSRF activo, consola H2 deshabilitada y credenciales externas solo bajo el perfil explícito `demo`.
- Exponía consultas y mutaciones globales; ahora todo recurso se busca junto con su propietario y los recursos ajenos e inexistentes responden igual.
- Los outputs de Gradle y metadatos de IDE no forman parte del proyecto versionado ni del paquete docente.

### 3. Ejercicio Productos — reformado

- Thymeleaf es el motor principal; se retiraron Mustache y su configuración accidental.
- El starter público usa Spring Boot 4.0.5 y Java 25, arranca con una portada y no entrega el CRUD resuelto.
- El Markdown público es el itinerario canónico: listado, alta, validación, edición, eliminación y persistencia, cada etapa con demo breve, trabajo, RED y criterio observable.
- `checkpoints/` conserva contratos deliberadamente desactivados (`.java.disabled`), incluidos binding con `ProductForm`, validación y mapeo formulario–entidad; el alumnado activa solo la etapa actual. El test de contexto mantiene verde el starter base.
- Existe una única solución final local bajo `99-profesor/productosMVC-solucion/`, ignorada por Git y respaldada en la unidad externa acordada.
- Se eliminaron el HTML Pandoc, los fragmentos `sol_productosMVC/`, plantillas Mustache, código CRUD antes incluido en el starter y configuración Lombok innecesaria.
- Validación final completada con Java 25: starter (1 test) y solución docente (10 tests) en verde con Spring Boot 4.0.5.

### 4. Cuestionario, seguridad y publicación

- El GIFT se corrigió y amplió a 12 preguntas canónicas sobre MVC, Thymeleaf, PRG y seguridad; continúa ignorado y fuera de Pages.
- `06-seguridad/README.md` cubre CSRF, XSS, validación, sesión y autorización por propietario sin duplicar la guía del Gestor.
- `04-proyectos/` no se versiona: UD3 usa Productos como práctica y el Gestor como ejemplo integrador, sin duplicarlos como proyecto.
- `99-profesor/`, `05-cuestionarios/`, soluciones locales, salidas de compilación y metadatos de IDE no deben incorporarse al sitio público.
- Las credenciales de demostración se aportan mediante variables de entorno bajo el perfil explícito `demo`; el perfil normal no precarga cuentas.

## Huecos didácticos

Los huecos del núcleo quedaron cubiertos: objetivos y RA/CE, ciclo MVC, progresión verificable, TDD, form objects, PRG, persistencia, seguridad y rúbrica privada. Las ampliaciones del Gestor siguen fuera de alcance deliberadamente.

## Decisiones del núcleo cerradas

- Thymeleaf es el motor obligatorio; Mustache queda como comparación puntual.
- Productos es la práctica incremental con una única solución final privada.
- El Gestor es el ejemplo integrador guiado posterior a Productos, no un segundo proyecto.
- El primer corte de seguridad incluye sesión, CSRF, validación y autorización por propietario. Registro, roles y recuperación de credenciales quedan fuera de alcance.

La publicación de enunciados en GitHub Pages sigue siendo una decisión transversal del módulo, no una decisión exclusiva de UD3.

## Plan priorizado

### P0 — Saneamiento y frontera pública — completado

- Establecer fuentes Markdown canónicas y retirar de publicación los HTML derivados.
- Separar starter, solución y material del profesor.
- Eliminar exclusiones de `.gitignore` que oculten archivos necesarios; mantener ignorados builds, IDE y material privado.
- Crear un índice de UD3 con orden y objetivos.

### P1 — Arquitectura didáctica — núcleo completado

- Decisiones del núcleo cerradas.
- Productos organizado como recorrido incremental TDD.
- Gestor reconstruido como ejemplo integrador seguro y enlazado desde el mapa documental general.

### P2 — Actualización técnica — completada

- Los dos proyectos canónicos usan Spring Boot 4.0.5 y Java 25.
- El Gestor usa el starter MVC de Spring Boot 4 y Thymeleaf.
- La suite del Gestor cubre flujo, autenticación, autorización, validación, CSRF y perfil demo antes de futuras ampliaciones.

### P3 — Seguridad y evaluación — completado

- Creada y versionada la guía `06-seguridad/README.md`.
- Revisados el GIFT y la rúbrica privada; se mantiene una fuente por propósito.
- Material privado ignorado y respaldado externamente; no se añade otro repositorio.

## Criterio de salida del inventario

- [x] Material clasificado por función y visibilidad.
- [x] Duplicados, obsolescencia y riesgos registrados.
- [x] Huecos didácticos identificados.
- [x] Decisiones de UD3 separadas de las decisiones transversales.
- [x] Decisiones del núcleo de UD3 cerradas: Thymeleaf obligatorio, Productos incremental y Gestor seguro como ejemplo integrador.
- [x] Productos, Gestor, documentación, seguridad y evaluación privada reformados y consolidados.

## Resultado de la reconstrucción del Gestor (16 de julio de 2026)

- `02-ejemplos/SpringMVC/` es ya el ejemplo integrador canónico: Spring Boot 4.0.5, Java 25, Gradle 9.1 y Thymeleaf.
- El núcleo implementa CRUD de tareas por propietario mediante `TaskForm`, controlador fino, servicio de casos de uso y repositorios acotados por usuario.
- CSRF permanece activo; mutaciones y logout usan POST. Recurso inexistente y ajeno responden igual (`404`).
- Se retiraron Mustache, roles y credenciales precargadas, consola H2, búsquedas globales y acciones en lote inseguras.
- El tutorial conversacional `SpringMVC-GestorTareas.md` fue sustituido por `06-gestor-tareas-seguro.md`; el documento de ideas pasó al backlog docente privado.
- Se eliminaron el HTML derivado `03-UD2-Controladores-Vistas-Thymeleaf.html` y su Markdown heredado después de integrar el contenido útil en las guías `02`, `03` y `04`.
- Fuera del primer corte: registro, búsqueda/filtros, paginación, roles, AJAX, API REST y Flyway.
- Evidencia TDD: el primer intento RED no pudo ejecutar por Gradle 8.11.1 incompatible con Java 25 (`Unsupported class file major version 69`); tras migrar la infraestructura y desarrollar el contrato, las 10 pruebas de seguridad, flujo y perfil demo quedan verdes.
