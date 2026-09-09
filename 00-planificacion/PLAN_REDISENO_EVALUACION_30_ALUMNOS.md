# Plan de rediseño de tareas y cuestionarios para un grupo de 30 alumnos

## 1. Propósito y límite del cambio

Este plan adapta DWES a un grupo excepcional de 30 alumnos. El objetivo no es
reducir el nivel técnico ni convertir la evaluación en automática: es desplazar
la corrección repetitiva de muchas prácticas individuales hacia evidencias
individuales más verificables, prácticas cooperativas con autoría trazable,
defensas cortas y cuestionarios Moodle frecuentes.

No ejecutar cambios directamente sobre los enunciados hasta completar el
inventario indicado en la fase 1. No publicar GIFT, soluciones, rúbricas de uso
docente ni listas de respuestas en MkDocs o GitHub Pages.

La accesibilidad se aborda mediante el protocolo transversal de
`PROTOCOLO_ACCESIBILIDAD_NEAE_DWES_2026_2027.md`: se mantienen RA/CE y
ponderaciones, pero pueden adaptarse puesto, herramientas, pausas, tiempos,
formato de respuesta y entregas fraccionadas. No se publica ningún diagnóstico.

## 2. Decisiones de diseño que debe aplicar la ejecución

### 2.1 Tipos de evidencia

| Evidencia | Modalidad | Finalidad | Corrección principal |
|---|---|---|---|
| Prácticas de unidad | Grupos de 3 o 4 | Aplicar el contenido y colaborar en un repositorio real | Revisión por grupo, defensa individual y trazabilidad Git |
| Hito individual 1 | Individual, final del primer trimestre | Verificar fundamentos de servidor, TDD, HTTP y seguridad sin ayuda del grupo | Corrección completa individual |
| Hito individual 2 | Individual, final del módulo | Trabajo final de módulo o prueba integradora equivalente | Corrección completa individual y defensa |
| Cuestionarios Moodle | Individual | Comprobar conocimientos, sintaxis, lectura de código, seguridad y decisiones técnicas | Corrección automática |
| Defensa / code review | Individual dentro de cada grupo | Atribuir autoría y comprensión de la práctica cooperativa | Preguntas orales breves con checklist |

No crear más tareas individuales evaluables entre los dos hitos. Los ejercicios
guiados, retos breves y entregas de práctica pueden seguir existiendo, pero se
deben marcar como formativos o integrarse en una práctica cooperativa, nunca
como una nueva cola de 30 correcciones completas.

### 2.2 Prácticas cooperativas

- Tamaño ordinario: 3 o 4 personas. Con 30 alumnos, planificar 8-10 grupos.
- Mantener un registro de agrupamientos. No repetir pareja hasta que sea
  inevitable; priorizar que cada alumno coincida con compañeros distintos en
  prácticas sucesivas.
- Usar **vertical slices** como regla general: cada integrante implementa de
  principio a fin uno o varios endpoints/casos de uso propios, incluyendo
  controlador, servicio/caso de uso, repositorio/persistencia, validación,
  errores y pruebas. Esto es preferible a repartir una única capa por persona,
  porque permite evaluar el recorrido servidor completo de cada alumno.
- Permitir tareas compartidas (modelo, migraciones, seguridad, configuración,
  contrato OpenAPI) solo con una ficha de responsabilidad: propietario inicial,
  revisor y pruebas asociadas.
- Cada integrante revisa al menos un cambio de otra persona antes del cierre;
  registrar la revisión mediante pull request, comentario de issue o checklist
  en el README.

### 2.3 Entrega, trazabilidad y defensa

- Cada práctica partirá de un repositorio plantilla de GitHub. El grupo crea su
  copia al iniciar la práctica; no reutiliza una entrega de otro grupo.
- La entrega Moodle es siempre el enlace al repositorio y el identificador o URL
  del issue de entrega. **Todos** los integrantes hacen esa misma entrega en
  Moodle para que aparezcan en el libro de calificaciones.
- El `README.md` de cada entrega incluye: integrantes, responsabilidades o
  endpoints asignados, cómo ejecutar pruebas, cómo reproducir la demostración y
  enlace al issue de entrega.
- Al acabar cada hito el grupo abre un issue de entrega con checklist, enlace a
  la release/tag, resultado de pruebas y dudas pendientes. El issue no sustituye
  la entrega Moodle.
- La defensa dura 5-10 minutos por grupo. Preguntar a cada alumno una cuestión
  directa sobre su slice o una decisión compartida, por ejemplo transacciones,
  validación, autorización, errores o pruebas.
- Si una persona no explica su aportación, no acredita la parte individual de la
  práctica. Si nadie del grupo puede explicar una decisión compartida, esa parte
  no se acredita a ningún integrante. Registrar el resultado sin convertir la
  defensa en una exposición larga.

### 2.4 Integración cliente-servidor

- No exigir interfaz ni cliente completo.
- Si hay cliente, debe demostrar peticiones reales, respuestas correctas y
  errores de los endpoints propios.
- Si no hay cliente, una colección de Insomnia, Postman u otro cliente REST
  demuestra que el contrato se puede invocar. Esta vía vale como evidencia del
  servidor, pero no como integración cliente-servidor completa.

### 2.5 Cuestionarios Moodle

- Programar un cuestionario cada semana o, como máximo, cada dos semanas.
- Tamaño recomendado por intento: 15-20 preguntas. No superar 20 salvo una
  prueba final de recapitulación explícitamente planificada.
- Crear bancos más grandes que cada intento: al menos 30 preguntas válidas por
  tema, de las cuales Moodle selecciona 15-20 aleatoriamente cuando sea posible.
- Objetivo inicial: 12-14 cuestionarios y al menos 250 preguntas GIFT nuevas o
  revisadas para todo el módulo. Ajustar la cifra final al calendario real, sin
  bajar de 10 cuestionarios ni de 200 preguntas.
- Las preguntas cortas se usarán solo cuando la respuesta sea inequívoca. Para
  sintaxis con múltiples formas válidas, usar selección múltiple, emparejamiento
  o código con huecos y alternativas cerradas para evitar recorrección manual.

## 3. Fase 1: inventario y matriz de transición

### Objetivo

Construir una matriz de todas las actividades actuales antes de modificar sus
enunciados, ponderaciones o rutas.

### Ficheros de partida conocidos

- `ud01-introduccion-entorno-servidor/EVALUACION.md`
- `ud02-api-rest/ud02a-spring-boot/EVALUACION.md`
- `ud01-introduccion-entorno-servidor/05-cuestionarios/ud01_repaso.gift`
- `ud02-api-rest/ud02a-spring-boot/05-cuestionarios/ud02a_semanas_1_4.gift`
- `ud02-api-rest/ud02a-spring-boot/05-cuestionarios/ud02a_semanas_5_8.gift`
- `ud03-mvc-spring-boot/05-cuestionarios/ud03-mvc-thymeleaf-seguridad.gift`
- `ud06-aplicaciones-hibridas/05-cuestionarios/ud06_ra9.gift`
- `ud07-proyecto-integrador/` y su rúbrica/checklist.

### Trabajo

1. Localizar todos los `README.md`, `rubrica.md`, `ra-ce.md`, `EVALUACION.md`,
   prácticas de `03-ejercicios/`, proyectos de `04-proyectos/` y bancos GIFT.
2. Crear `00-planificacion/MATRIZ_TRANSICION_EVALUACION_2026_2027.md` con:
   unidad, actividad actual, RA/CE, modalidad actual, corrección estimada,
   decisión (`formativa`, `grupal`, `hito individual 1`, `hito individual 2`,
   `cuestionario`, `retirada`) y archivos que cambiarán.
3. Identificar duplicados o prácticas que puedan pasar a ser incrementos de una
   misma práctica cooperativa, evitando crear nuevos proyectos innecesarios.
4. No inventar ponderaciones oficiales: proponer una tabla de reparto para
   revisión docente y dejarla marcada como pendiente de aprobación.

### Criterio de salida

No queda ninguna entrega evaluable sin una decisión de transición ni RA/CE
asociado.

## 4. Fase 2: modelo común de prácticas cooperativas

### Artefactos nuevos comunes

Crear en `00-recursos-comunes/plantillas/`:

- `plantilla-practica-cooperativa.md`: objetivo, alcance, slices, criterios,
  pruebas, defensa, issue de entrega y entrega Moodle.
- `plantilla-readme-entrega-grupo.md`: miembros, responsabilidades, endpoints,
  instrucciones, pruebas, evidencia de integración y enlace al issue.
- `plantilla-issue-entrega.md`: checklist de hito para copiar en GitHub.
- `plantilla-registro-agrupamientos.csv`: actividad, alumno, grupo y compañeros
  previos; debe permitir comprobar rotación.
- `plantilla-defensa-code-review.md`: preguntas, respuesta observada, evidencia
  Git y resultado individual.

Crear también una guía docente privada en `00-recursos-comunes/99-profesor/` o
en el área privada ya usada por el proyecto. No versionar respuestas de defensa
ni criterios que faciliten su preparación mecánica.

### Mecanismo de agrupamiento

1. Mantener una tabla de co-participación alumno-a-alumno.
2. Para cada nueva práctica, crear grupos de 3-4 minimizando pares repetidos.
3. Equilibrar, cuando sea posible, experiencia previa y asistencia, pero no
   etiquetar públicamente al alumnado por nivel.
4. Publicar solo la composición del grupo y sus responsabilidades; conservar la
   matriz de rotación para el docente.
5. Si hay alta o baja, reajustar el grupo sin reiniciar la matriz histórica.

Puede implementarse después un script local que genere propuestas de grupos
desde CSV. No hacerlo antes de definir el formato y probarlo con una lista
ficticia de 30 alumnos.

## 5. Fase 3: rediseñar tareas y evaluación por unidad

### Regla transversal

Para cada unidad, conservar solo una práctica cooperativa evaluable o un
incremento cooperativo claramente ligado a la anterior. El resto de tareas
deben convertirse en práctica guiada formativa, reto autocorregible cuando sea
posible o material de ampliación.
### Paraa todas las unidades
Conservar en archivo las versiones actuales de las tareas individuales, antes de
convertirlas en grupales, para cursos posteriores o si se diera el caso de que se
den de baja muchos alumnos, tener prácticas individuales listas
### UD1: HTTP, entorno y primeros tests

- Transformar el laboratorio HTTP y la extensión Hello Server en una única
  práctica cooperativa pequeña, con slices de trazas HTTP, endpoint, prueba TDD
  y seguridad básica.
- Mantener una evidencia individual breve en cuestionario, no una segunda
  entrega individual completa.
- Revisar `ud01.../EVALUACION.md`: sustituir la ponderación de dos entregas
  individuales por práctica cooperativa + cuestionario, dejando el reparto como
  propuesta hasta aprobación.

### UD2: API REST y Spring Boot

- Convertir Mini Tasks, Book Catalog y Gestión de Eventos en menos entregas
  cooperativas, preferentemente incrementales, reutilizando starters y
  contratos existentes.
- Asignar vertical slices por integrante: recurso/endpoint, regla, consulta,
  validación/error y pruebas. Exigir que cada slice tenga pruebas.
- Preservar Battleship como ejemplo conductor, no como nueva entrega.
- Revisar `ud02a-spring-boot/EVALUACION.md`, los enunciados, rúbricas y matrices
  RA/CE de las tres prácticas para retirar la condición individual por defecto y
  añadir defensa individual y trazabilidad Git.

### UD3: MVC, sesiones y seguridad

- Mantener una práctica cooperativa sobre el proyecto canónico Productos o
  Gestor seguro, con responsabilidades verticales: flujo de formulario,
  validación, autorización por propietario, persistencia y pruebas.
- El cuestionario cubre Thymeleaf, PRG, CSRF, XSS, sesión y autorización.

### UD4 y UD5: PHP y Laravel

- Mantener como máximo una práctica cooperativa por bloque impartido, usando los
  proyectos canónicos Notes/GTask y la API Laravel como base, no copias nuevas.
- Repartir endpoints o casos de uso y exigir PDO/Eloquent seguro, validación,
  autorización cuando aplique y tests.
- El cuestionario cubre PHP 8.4, sesiones/cookies, validación, escape, PDO,
  inyección SQL, ORM/Eloquent, migraciones, rutas y políticas.

### UD6: aplicación híbrida

- Conservar el proyecto de integración como práctica cooperativa solo si el
  calendario lo permite. Si no, moverlo a ampliación formativa sin perjudicar la
  cobertura de RA9.
- Mantener el cuestionario RA9 y ampliarlo con contratos, cliente REST, errores,
  CORS, autenticación y límites de integraciones externas.

### UD7: proyecto integrador y los dos hitos individuales

- Mantener el proyecto final individual como **hito individual 2**.
- Crear el enunciado de **hito individual 1** para el final del primer trimestre:
  servidor pequeño desde plantilla, un flujo completo, TDD, validación, error,
  persistencia y explicación breve. Debe ser viable en una corrección acotada.
- Actualizar UD7 solo después de decidir si el hito 1 se aloja allí o en una
  unidad anterior; preferencia: en `ud07-proyecto-integrador/` como instrumento
  transversal con enlace desde las unidades previas.
- Ajustar la documentación existente de UD7 para que el trabajo final sea
  inequívocamente individual y no entre en conflicto con las nuevas prácticas
  cooperativas de unidades anteriores.

## 6. Fase 4: banco GIFT de Moodle

### Estructura de archivos

Mantener los bancos en `05-cuestionarios/`, ignorados por Git/MkDocs conforme a
la política actual. Usar nombres uniformes:

```text
ud01-semana-01-http.gift
ud01-semana-02-entorno-tdd.gift
ud02a-semana-03-rest-controladores.gift
...
ud06-semana-xx-integracion.gift
00-planificacion/banco-gift-cobertura.md
```

No mezclar preguntas de profesor con material público. Los ficheros pueden
existir localmente, pero deben permanecer ignorados; verificar `.gitignore`.

### Distribución mínima propuesta

| Bloque | Cuestionarios | Temas principales |
|---|---:|---|
| UD1 | 2 | HTTP, cabeceras, métodos, estado, entorno, TDD inicial |
| UD2a | 4 | Spring Boot, REST, servicios, DTO, errores, JPA, OpenAPI, seguridad, tests |
| UD3 | 2 | MVC, Thymeleaf, formularios, sesión, CSRF, XSS, autorización |
| UD4 | 2 | PHP, formularios, sesiones, PDO, seguridad, TDD |
| UD5 | 2 | Laravel, rutas, ORM, migraciones, validación, auth/policies |
| UD6/repaso | 1-2 | integración, CORS, contratos, síntesis transversal |

### Tipos de preguntas obligatorios por banco

Cada cuestionario de 15-20 preguntas debe combinar, cuando el tema lo permita:

- 35-45 % elección única o múltiple razonada;
- 20-30 % fragmento de código con hueco y opciones cerradas;
- 10-20 % emparejamiento (concepto-responsabilidad, estado-código HTTP,
  amenaza-mitigación, anotación-efecto);
- 10-20 % verdadero/falso con afirmaciones no triviales;
- 0-10 % respuesta corta solo con respuesta canónica inequívoca.

Incluir distractores plausibles basados en errores frecuentes. Evitar preguntar
memorística de nombres de clases cuando pueda evaluarse lectura de código,
decisión de diseño o seguridad.

### Ejemplos de cobertura

- HTTP: método, idempotencia, cabeceras, caché, sesión/cookies, estado y error.
- TDD: orden red-verde-refactor, test de límite, doble de prueba y aislamiento.
- Spring: controlador, servicio, repositorio, DTO, validación, `ResponseEntity`,
  `@RestControllerAdvice`, slicing de pruebas, JPA y migraciones.
- Seguridad API: validación, JWT, CORS, `401`/`403`, autorización y secretos.
- MVC: PRG, `BindingResult`, Thymeleaf escapado, CSRF y propietario.
- PHP: tipado, sesiones, escape HTML, `password_hash`, PDO preparado e inyección
  SQL.
- Laravel: rutas, request validation, Eloquent, migraciones, policies y errores.
- Integración: contrato, payload, error de cliente, timeout, CORS y trazabilidad.

### Validación GIFT

1. Validar sintaxis antes de importación mediante un parser o importación de
   prueba en curso Moodle de ensayo.
2. Previsualizar todas las preguntas y comprobar puntuación, escapes, código y
   retroalimentación.
3. Importar primero un banco piloto de 20 preguntas de UD1 y uno de UD2a.
4. Registrar incidencias de importación y convenciones en
   `00-planificacion/banco-gift-cobertura.md`.
5. Nunca declarar un banco validado solo porque el fichero parezca correcto.

## 7. Fase 5: automatización operativa GitHub

### Repositorios plantilla

- Decidir el propietario/organización y crear un template repository por stack
  o práctica, no uno por grupo.
- El template debe contener código inicial, tests iniciales si proceden, README
  de entrega de grupo, `.gitignore`, licencia/aviso de uso y configuración sin
  secretos.
- No incluir solución, respuestas de cuestionario ni artefactos evaluativos
  privados.

### Scripts docentes con `gh`

Crear scripts locales, documentados y con modo simulación, para:

1. leer un CSV de grupos;
2. crear o verificar repositorios desde plantilla;
3. asignar permisos a los integrantes;
4. crear issue inicial y etiquetas de hito;
5. listar enlaces de repositorios, último commit por autor, issues y releases;
6. generar una tabla para la revisión oral.

Los scripts no deben asumir que GitHub Classroom está disponible. Antes de usar
GitHub Insights, verificar sus permisos y límites; para la evaluación usar los
commits, PRs, issues y explicaciones como evidencia, no solo contadores de
actividad.

## 8. Fase 6: instrumentos de evaluación y despliegue gradual

1. Actualizar la rúbrica común y las rúbricas de prácticas para separar:
   comportamiento del grupo, responsabilidad individual, pruebas, trazabilidad,
   defensa y cuestionario.
2. Añadir a cada enunciado la sección fija de entrega, README, issue, defensa y
   evidencia de integración.
3. Mantener ponderaciones como borrador hasta la validación docente y comprobar
   que la suma de cada unidad sea 100 %.
4. Preparar un piloto completo en UD1: plantilla, grupos ficticios, issue,
   entrega Moodle, defensa de prueba y GIFT importado.
5. Tras el piloto, ajustar las plantillas antes de tocar masivamente UD2-UD6.
6. Aplicar los cambios unidad por unidad, ejecutando las pruebas y el build de
   MkDocs tras cada unidad.

## 9. Riesgos y mitigaciones

| Riesgo | Mitigación |
|---|---|
| Alumno sin contribución visible | Vertical slice, commits, revisión de pares, issue y defensa individual |
| Siempre trabajan los mismos compañeros | Matriz de co-participación y grupos rotatorios |
| Reparto por capas oculta falta de comprensión global | Vertical slices por defecto y preguntas cruzadas en defensa |
| Copia entre grupos | Templates separados, responsabilidades distintas, defensa y cuestionarios individuales |
| Corrección oral consume demasiado tiempo | 5-10 minutos, checklist y muestreo de una o dos preguntas por alumno |
| Cuestionarios demasiado memorísticos | Matriz RA/CE y mezcla obligatoria de lectura de código, seguridad y decisiones |
| Respuestas cortas generan recorrección | Usarlas solo cuando sean inequívocas; preferir formatos cerrados |
| Errores de GIFT al importar | Banco piloto, importación de ensayo y previsualización |
| GitHub no muestra autoría real por commits pobres | Exigir issue, PR/revisión, defensa y explicación del código |
| Calendario corto tras Navidad | Concentrar prácticas y cuestionarios de UD1-UD3 antes de Navidad; dejar UD6 como adaptable |

## 10. Orden de ejecución para el agente

1. Crear la matriz de transición y presentarla para revisión, sin alterar
   ponderaciones todavía.
2. Crear plantillas cooperativas y la guía de defensa.
3. Diseñar el piloto UD1 y dos bancos GIFT piloto.
4. Validar GIFT en Moodle y el flujo GitHub con grupos ficticios.
5. Rediseñar UD1 y revisar sus enlaces/RA/CE/rúbrica.
6. Rediseñar UD2a; no modificar a la vez UD3-UD7.
7. Continuar UD3, UD4, UD5, UD6 y UD7 en ese orden, respetando el calendario.
8. Tras cada unidad: `git diff --check`, comprobación de enlaces Markdown,
   pruebas de proyectos modificados y `mkdocs build --strict` si afecta a
   contenido publicado.
9. Actualizar `PRIORIDADES_CURSO_2026_2027.md`, `DECISIONES_PENDIENTES.md` y la
   memoria de cada unidad con el estado real. No marcar Moodle o GitHub como
   verificados sin evidencia de importación o ejecución.

## 11. Resultado esperado

Al terminar, el módulo tendrá una carga de corrección sostenible para 30
alumnos: 8-10 entregas cooperativas por ciclo, dos evidencias individuales
fuertes, defensa breve que acredita autoría, trazabilidad Git verificable y una
evaluación individual frecuente mediante cuestionarios Moodle variados y
autocorregibles.

## Registro de ejecución

- **Fase 1**: completada en `MATRIZ_TRANSICION_EVALUACION_2026_2027.md`.
- **Fase 2**: completada; plantillas cooperativas y guía docente creadas.
- **Fase 3**: completada; modalidad 2026/2027 aplicada en UD1-UD6 y hito
  individual 1 creado en UD7.
- **Fase 4**: completada en `banco-gift-cobertura.md`; se crearon 17 bancos
  nuevos con 272 preguntas. La importación y previsualización real en Moodle
  siguen pendientes.
- **Fase 5**: preparada; script local seguro y documentación de templates en
  `00-planificacion/github/`. No se han creado repositorios remotos ni añadido
  colaboradores.
- **Fase 6**: instrumentos, rúbricas, piloto UD1 y despliegue gradual preparados.
  La importación Moodle, la ejecución externa del piloto y las ponderaciones
  definitivas siguen pendientes de aprobación/verificación.
