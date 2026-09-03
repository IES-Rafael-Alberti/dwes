# Matriz de transición de evaluación — DWES 2026/2027

## Estado del documento

**Inventario de la fase 1; actualizado como referencia tras ejecutar las fases 2
y 3.** El inventario original no se reescribe como si hubiera sido una decisión
definitiva: las transiciones siguen siendo propuestas y las ponderaciones quedan
pendientes de revisión docente. Los cambios operativos de fases 2-3 se describen
en los documentos enlazados desde cada unidad.

## Criterios usados

- `Formativa`: se conserva para practicar, pero no genera una corrección manual
  evaluable independiente.
- `Grupal`: práctica evaluable en grupos de 3-4, con responsabilidad individual,
  trazabilidad Git y defensa breve.
- `Hito individual 1`: evidencia individual al final del primer trimestre.
- `Hito individual 2`: trabajo final de módulo o prueba integradora individual.
- `Cuestionario`: evidencia Moodle individual y autocorregible.
- `Retirada`: no se mantiene como actividad del recorrido evaluable; se conserva
  como referencia histórica, ejemplo o material opcional si procede.

La columna `corrección actual` es una estimación cualitativa de carga, no una
medición de horas. Debe validarse con la experiencia real del curso anterior.

## Resumen de situación actual

| Área | Evidencias/prácticas localizadas | Situación observada | Problema para 30 alumnos |
|---|---|---|---|
| UD1 | Laboratorio HTTP, extensión Hello Server, cuestionario | 2 entregas y 1 cuestionario; la evaluación publicada asigna 40/30/30 | Dos correcciones manuales individuales tempranas |
| UD2a | Mini Tasks, Book Catalog, Gestión de eventos, cuestionarios | Tres entregas individuales publicadas; suman 70 % y queda 30 % sin instrumento publicado | Hasta tres colas de corrección por alumno antes de Navidad |
| UD3 | Productos incremental, Gestor seguro, cuestionario | Práctica incremental y ejemplo integrador; seguridad y MVC | Conviene una única práctica evaluable por slices |
| UD4 | Notes procedural, GTask OOP, ejercicios de fundamentos | Recorrido formativo e incremental con TDD y seguridad | Varias prácticas pueden confundirse con entregas independientes |
| UD5 | API Laravel de recetas, proyecto y referencias históricas | Proyecto canónico API-first; cuestionario no localizado en el inventario | Evitar evaluar duplicados Laravel/Blade/Vue |
| UD6 | Práctica P2A, proyecto P2B, banco privado RA9 | P2A guiada y P2B evaluable; GIFT privado de 14 preguntas | Calendario post-Navidad y corrección de transferencia |
| UD7 | Propuesta, seguimiento y proyecto integrador | Trabajo final individual por defecto, defensa y checklist | Debe alojar o enlazar el hito individual 1 sin duplicar el final |

## Matriz principal

| Unidad | Actividad actual | RA/CE documentados | Modalidad actual | Corrección actual | Decisión de transición propuesta | Archivos candidatos a cambiar |
|---|---|---|---|---|---|---|
| UD1 | Laboratorio HTTP | RA1.a-g; principal en evaluación UD1 | Individual | Alta | Grupal pequeña: análisis dividido por evidencias, informe común y defensa individual | `ud01-introduccion-entorno-servidor/EVALUACION.md`; `03-ejercicios/01-analisis-http/README.md`; `rubrica.md`; `ra-ce.md` |
| UD1 | Extensión TDD Hello Server | RA1.e-g; principal en evaluación UD1 | Individual | Media/alta | Integrar como slice de la práctica UD1 o convertir en formativa; candidato a hito individual 1 solo si no se integra | `ud01-introduccion-entorno-servidor/EVALUACION.md`; `03-ejercicios/02-extension-hello-server/README.md`; `rubrica.md`; `ra-ce.md` |
| UD1 | Repaso Moodle | RA1.a-g | Individual | Automática | Cuestionario | `ud01-introduccion-entorno-servidor/05-cuestionarios/ud01_repaso.gift`; `05-cuestionarios/README.md` |
| UD1 | Seguridad HTTP y entorno | RA1.e-g; relación con servidor | Guiada | Baja | Formativa y cubierta por cuestionario y práctica | `ud01-introduccion-entorno-servidor/06-seguridad/README.md`; materiales enlazados |
| UD2a | Mini Spring Boot Tasks | RA1.g, RA5.b/h, RA6.a-d, RA7.a-d | Individual | Muy alta | Primer tramo cooperativo: JPA/H2, endpoint, DTO y prueba; imita las sesiones 1-6 de Battleship | `ud02-api-rest/ud02a-spring-boot/EVALUACION.md`; `03-ejercicios/01-mini-tasks/README.md`; `rubrica.md`; `ra-ce.md` |
| UD2a | Book Catalog API | RA5.b/h, RA7.a-f | Individual | Muy alta | Segundo tramo cooperativo: API REST por capas, respuestas, errores y pruebas MockMvc; no mantener tres entregas completas separadas | `03-ejercicios/02-book-catalog/README.md`; `rubrica.md`; `ra-ce.md`; evaluación de entregas y starters |
| UD2a | Gestión de eventos | RA5.g/h, RA6.e-g, RA7.g-h | Individual | Alta | Tramo final cooperativo: JWT, Flyway, perfiles y OpenAPI, correspondiente a las sesiones 7, 8 y 10 de Battleship | `03-ejercicios/03-gestion-eventos/README.md`; `rubrica.md`; `ra-ce.md`; `EVALUACION.md` |
| UD2a | Battleship | Contenido trabajado para RA6.a-g y RA7.a-h; no constituye evidencia evaluable | Guiada/ejemplo | No aplicable | Mantener como hilo conductor de las explicaciones teórico-prácticas y referencia TDD/SDD; no crear ni inferir una entrega evaluable | `02-ejemplos/battleship/docs/README.md`; no cambiar en esta fase |
| UD2a | Cuestionarios semanas 1-4 | RA5.a-b y contenidos Spring/TDD/REST | Individual | Automática | Cuestionario modular; ampliar banco con código, emparejamiento y selección múltiple | `05-cuestionarios/ud02a_semanas_1_4.gift` |
| UD2a | Cuestionarios semanas 5-8 | RA5.b/h y errores/slicing | Individual | Automática | Cuestionario modular; revisar cobertura y variedad GIFT | `05-cuestionarios/ud02a_semanas_5_8.gift` |
| UD2a | Ponderación 2026/2027 | Mini Tasks 15 %, Book Catalog 20 %, Gestión de eventos 25 %, cuestionarios 25 %, defensa 15 % | Cooperativa e individual según instrumento | Definida | Aplicar la ruta incremental aprobada; Battleship no recibe ponderación | `ud02-api-rest/ud02a-spring-boot/EVALUACION.md` |
| UD2b | ToDo API .NET | Material localizado en inventario; .NET relegado a ejemplo | Individual/varía | Desconocida | Retirada como evidencia obligatoria DWES; conservar como referencia si no contradice la política de stacks | `ud02-api-rest/ud02b-dotnet/README.md`; actividades internas |
| UD2c | GraphQL | Material introductorio | Individual/varía | Baja | Formativa o retirada del recorrido evaluable; no crear práctica nueva | `ud02-api-rest/ud02c-graphql/README.md` |
| UD3 | Productos MVC incremental | RA4, RA5, RA6, RA8 según README de UD3 | Individual por checkpoints | Alta si se corrige cada checkpoint | Grupal incremental; checkpoints como hitos de seguimiento, una entrega evaluable y defensa individual | `ud03-mvc-spring-boot/README.md`; `03-ejercicios/Tareas/productosMVC/`; rúbricas/checkpoints |
| UD3 | Gestor de tareas seguro | RA4-RA6, RA8; ejemplo integrador | Ejemplo guiado | Media | Mantener como ejemplo; extraer retos formativos para preparar la práctica grupal | `02-ejemplos/SpringMVC/`; documentación de UD3 |
| UD3 | MVC, Thymeleaf y seguridad | RA4, RA5, RA6, RA8 | Cuestionario localizado | Automática | Cuestionario individual frecuente | `05-cuestionarios/ud03-mvc-thymeleaf-seguridad.gift` |
| UD4 | Ejercicios de fundamentos PHP | RA2, RA3 según README | Individual/formativa | Media si se entregan todos | Formativa; seleccionar ejercicios para clase y cubrir saberes con cuestionarios | `ud04-php/01-documentacion/11-ejercicios-fundamentos.md`; actividades relacionadas |
| UD4 | Notes procedural seguro | RA3, RA4, RA6 según README | Incremental | Alta si se evalúa completa | Grupal o formativa; preferencia: slice de práctica cooperativa procedural antes de GTask | `ud04-php/03-ejercicios/notas-procedural-php84/README.md`; rúbricas internas |
| UD4 | GTask PHP 8.4 OOP | RA5, RA6; TDD y capas | Proyecto/incremental | Muy alta | Grupal por casos de uso/endpoints; usar como práctica evaluable PHP principal | `ud04-php/04-proyectos/gtask-php84/README.md`; `SEGUIMIENTO.md`; rúbrica/checklist |
| UD4 | Concesionario histórico | No debe ser evidencia vigente | Histórico | No aplicable | Retirada del recorrido evaluable; mantener solo análisis de deuda y riesgos | `ud04-php/90-archivo/ejercicios-historicos/` |
| UD5 | API Laravel 12 de recetas | Rúbrica común; contenidos API-first | Ejemplo/proyecto | Alta | Grupal incremental si se imparte con tiempo; no duplicar con Laravel 10/11 | `ud05-laravel/README.md`; `03-ejercicios/Proyecto/`; docs del proyecto |
| UD5 | Laravel 10/11, Blade y cliente Vue | Referencia histórica/fuera de ruta principal | Ejemplo histórico | Baja | Retirada de evaluación; conservar como comparación o archivo | `ud05-laravel/02-ejemplos/sail/`; mapa de ejemplos |
| UD6 | Práctica incremental P2A | RA9.a-h, tabla de evidencias P2A | Guiada/evaluable según práctica | Alta | Grupal si se mantiene como práctica; reducir correcciones por checkpoints y exigir evidencia común | `ud06-aplicaciones-hibridas/03-ejercicios/practica-integracion/README.md`; tests/docs |
| UD6 | Proyecto de transferencia P2B | RA9 y rúbrica P2B | Proyecto evaluable | Muy alta | Grupal de 3-4 con responsabilidad individual y defensa; ajustar al calendario post-Navidad | `04-proyectos/proyecto-integracion-hibrida/README.md`; `ra-ce-evidencias.md`; `rubrica-ra9.md` |
| UD6 | Banco RA9 privado | RA9 | Individual | Automática | Cuestionario individual; ampliar de 14 preguntas si cubre el tiempo disponible | `05-cuestionarios/ud06_ra9.gift` o ubicación privada equivalente |
| UD6 | Chat Spring AI P3 | Ampliación opcional; no basta para RA9.g | Individual/grupal opcional | Alta | Formativa/opcional; no añadir carga evaluable común | `01-documentacion/03-integracion-chat-spring-ai.md`; proyecto |
| UD7 | Propuesta de proyecto | Diseño y delimitación de servidor | Individual o pareja aprobada | Media | Formativa con aprobación obligatoria; sirve para preparar el hito 2 | `ud07-proyecto-integrador/03-ejercicios/propuesta-proyecto/README.md` |
| UD7 | Seguimiento por hitos | Trazabilidad y autoría | Individual/grupo aprobado | Media | Evidencia de proceso para prácticas y proyectos; no nueva nota independiente sin decisión | `03-ejercicios/seguimiento-proyecto/README.md`; `02-seguimiento-hitos.md` |
| UD7 | Proyecto integrador final | Servidor propio, reglas, pruebas y defensa | Individual por defecto | Muy alta | Hito individual 2; conservar como evidencia final individual | `01-documentacion/01-proyecto-final-dwes.md`; `04-proyectos/proyecto-final-dwes/`; rúbrica |
| UD7 | Defensa/code review | Autoría y comprensión | Individual dentro del grupo | Alta pero acotable | Mantener como defensa de prácticas grupales y hito 2; 5-10 minutos por grupo con preguntas a cada miembro | `99-profesor/guion-defensa-y-cambio-guiado.md`; `99-profesor/checklist-integracion-cliente-servidor.md` |
| UD7 | Integración con cliente | Evidencia complementaria de servidor | Opcional | Variable | Si hay cliente, demostrar flujo completo; si no, Insomnia/Postman/REST. No exigir cliente completo | `99-profesor/checklist-integracion-cliente-servidor.md`; rúbrica UD7 |
| UD7 | Hito individual 1 | Pendiente de definir RA/CE y alcance | No existe todavía | Nula | Crear en fase posterior; final del primer trimestre, acotado y reproducible | Nuevo enunciado/rúbrica; ubicación por decidir |

## Inventario de cuestionarios GIFT

| Archivo localizado | Preguntas observadas o estado | Tipos observados | Transición propuesta |
|---|---|---|---|
| `ud01_repaso.gift` | Banco de repaso de UD1 | Revisar en fase 4 | Conservar y ampliar por temas HTTP, entorno, seguridad y TDD |
| `ud02a_semanas_1_4.gift` | 12 preguntas visibles | Principalmente elección única | Dividir o etiquetar por semana; añadir lectura de código, emparejamiento y selección múltiple |
| `ud02a_semanas_5_8.gift` | Banco localizado | Revisar contenido | Mantener como base de errores, slicing, persistencia y contrato |
| `ud03-mvc-thymeleaf-seguridad.gift` | 12 preguntas visibles | Elección única | Añadir código con huecos cerrados, escenarios CSRF/XSS/sesión y emparejamiento |
| `ud06_ra9.gift` | No inspeccionado aquí; la documentación indica banco privado de 14 preguntas | Pendiente de auditar | Mantener privacidad; comprobar cobertura RA9 y variedad |

### Cuestionarios que deben localizarse o confirmarse en la fase siguiente

- Bancos privados de UD4 y UD5, si existen fuera del árbol publicado.
- Otros bancos de UD1-UD6 ignorados por Git o almacenados en `99-profesor/`.
- Estado real de importación y previsualización en Moodle.
- Correspondencia entre cada banco y la matriz RA/CE.

No se concluye en esta fase cuántas preguntas tiene cada banco ni se cambia su
contenido. El objetivo numérico del plan general (10-14 cuestionarios y 200-250
preguntas) queda como propuesta de planificación, no como ponderación.

## Duplicados y posibles fusiones

1. **UD1 laboratorio HTTP + extensión Hello Server**: pueden formar una única
   práctica cooperativa con una demostración técnica común; confirmar que no se
   pierde RA1.f antes de fusionarlas.
2. **UD2a Mini Tasks + Book Catalog + Gestión de eventos**: comparten recorrido
   Spring Boot, capas, persistencia, validación, pruebas y API; deben convertirse
   en menos entregas incrementales, no en tres proyectos grupales completos.
3. **UD2a Battleship**: es ejemplo conductor TDD/SDD y no debe convertirse en
   cuarta entrega evaluable.
4. **UD3 Productos + Gestor seguro**: Productos puede ser práctica cooperativa y
   Gestor debe seguir como referencia integradora, salvo decisión contraria.
5. **UD4 Notes + GTask**: Notes debe ser formativa o un incremento acotado; GTask
   puede concentrar la evidencia PHP de capas y TDD.
6. **UD5 Laravel 10/11, Blade y Vue**: son referencias históricas; no crear
   entregas paralelas a la API Laravel 12.
7. **UD6 P2A + P2B**: comparten integración, pero P2B tiene contrato y rúbrica
   propios; decidir si P2A se integra como preparación formativa o como una sola
   práctica grupal con P2B.
8. **UD7 propuesta/seguimiento/proyecto**: propuesta y seguimiento son
   instrumentos de proceso, no tres notas nuevas; el proyecto final queda como
   hito individual 2.

## Propuesta de calendario, pendiente de aprobación

| Periodo | Evidencias principales | Evidencias automáticas |
|---|---|---|
| Inicio de curso | Práctica cooperativa UD1 | 1-2 cuestionarios HTTP/entorno/TDD |
| Antes de Navidad | Práctica(s) cooperativa(s) UD2a y, si procede, UD3 inicial | Cuestionario cada 1-2 semanas |
| Final del primer trimestre | Hito individual 1 | Cuestionario de consolidación |
| Después de Navidad | UD4-UD6 ajustadas al tiempo real; prácticas grupales cortas | Cuestionarios PHP/Laravel/integración |
| Cierre del módulo | Hito individual 2 y defensa | Repaso final opcional |

La tabla no asigna pesos. Solo ordena la carga para que las evidencias más
fundamentales se recojan antes de la salida a prácticas en empresa.

## Decisiones que quedan abiertas para revisión docente

1. Qué RA/CE y alcance exactos tendrá el hito individual 1.
2. Ponderación de prácticas grupales, defensas, cuestionarios y dos hitos
   individuales; no está fijada en esta fase.
3. Si cada grupo tendrá 3 o 4 integrantes según asistencia y número definitivo.
4. Número máximo de prácticas cooperativas evaluables por unidad.
5. Si se aceptará una práctica hecha con cliente propio como evidencia adicional
   sin convertir el cliente en requisito.
6. Dónde se alojará el hito individual 1 y qué plantilla de repositorio usará.
7. Si Moodle permite categorías, preguntas aleatorias, importación GIFT y
   retroalimentación sin plugins adicionales.
8. Qué repositorios plantilla y permisos GitHub puede mantener el docente.
9. Política de recuperación: individualización de una práctica grupal,
   cuestionario equivalente y evidencia de defensa.
10. Tratamiento de alumnado que no hace commits, no entrega el enlace Moodle o
    no puede defender su parte.

## Criterio de salida de la fase 1

- Todas las actividades evaluables localizadas en el árbol conocido aparecen en
  la matriz.
- Cada fila tiene una decisión de transición propuesta o queda explícitamente
  pendiente.
- Cada fila indica RA/CE o la ausencia que debe resolverse.
- Los duplicados y posibles fusiones están identificados.
- No se han cambiado enunciados, ponderaciones ni bancos GIFT durante esta fase.

## Ejecución posterior

- Fase 2 ejecutada: se crearon las plantillas cooperativas y la guía docente
  privada indicadas en el plan.
- Fase 3 ejecutada: se añadieron modalidades 2026/2027 en UD1-UD6 y el hito
  individual 1 de UD7. Se conservaron los enunciados individuales existentes y
  no se modificaron bancos GIFT.
