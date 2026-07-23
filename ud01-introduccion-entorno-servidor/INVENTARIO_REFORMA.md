# Inventario de reforma de UD1

## Estado

UD1 está **en reforma y no puede declararse cerrada**. Es una unidad prioritaria anterior a Navidad y debe cumplir la definición de cierre de `00-planificacion/PRIORIDADES_CURSO_2026_2027.md`.

## Diagnóstico

- No existe un README ni un recorrido canónico de la unidad.
- La documentación principal está en R Markdown y PDF, no en Markdown navegable.
- Los PDF están desincronizados respecto a sus fuentes y contienen residuos de conversación.
- La guía de instalación usa Java 17/21 y versiones indeterminadas, frente al baseline Java 25 y Spring Boot 4.
- El plan de la sesión 3 menciona un proyecto, scripts, endpoints, Swagger, login y Docker que no existen en UD1.
- No hay ejemplo ejecutable ni pruebas pese a la metodología TDD del módulo.
- `06-seguridad/` está vacío, aunque UD1 debe cubrir HTTPS/TLS, cookies y cabeceras de seguridad.
- El ejercicio HTTP tiene valor didáctico, pero no es reproducible ni protege adecuadamente las trazas sensibles.
- La política de IA del ejercicio contradice la decisión de no usar IA en las primeras unidades.
- La publicación copiaba fuentes `.Rmd` y PDF desincronizados porque la exclusión `*.rmd` no coincide con la extensión real `.Rmd` en sistemas sensibles a mayúsculas.

## Cobertura actual de RA1

| CE | Estado | Problema principal |
|---|---|---|
| a | Parcial | La comparación entre ejecución cliente y servidor necesita precisión y evidencia práctica. |
| b | Parcial | Se explican ventajas, pero se mezclan simplificaciones y duplicados. |
| c | Insuficiente | Inferir tecnología por cabeceras no demuestra mecanismos de ejecución en servidor. |
| d | Insuficiente | Servidor web, contenedor servlet, servidor embebido y servidor de aplicaciones no se comparan correctamente. |
| e | Insuficiente | Se enumeran lenguajes y tecnologías, pero no se caracterizan ni seleccionan. |
| f | No evidenciado | Falta una integración mínima entre HTML y código servidor. |
| g | No evidenciado | Se citan herramientas y frameworks sin analizarlos ni evaluarlos. |

## Material existente y destino

| Material | Diagnóstico | Destino |
|---|---|---|
| `UD1_Cap1-Introduccion.Rmd` | Materia prima aprovechable con simplificaciones técnicas | Reescribir como Markdown canónico |
| `UD1_Cap1-protocoloHTTP.Rmd` | Extenso, desproporcionado y con errores de semántica HTTP | Extraer un núcleo moderno y verificable |
| `UD1_Cap2-InstalacionPrimerContacto.Rmd` | Baseline obsoleto y residuo conversacional | Sustituir por entorno Java 25/Spring Boot 4 reproducible |
| Tres PDF | Derivados desincronizados; algunos contienen conversación | Excluir de publicación y retirar tras consolidar Markdown |
| `UD1_Plan_Sesion3.md` | Plan docente efímero con recursos inexistentes | Excluir de publicación y trasladar solo ideas válidas |
| `03-ejercicios/01-analisis-http` | Buena base, pero RA/CE, privacidad, QUERY e IA no están alineados | Reformar como laboratorio reproducible |
| `ud01_repaso.gift` | Cinco preguntas y cobertura incompleta | Ampliar y validar mediante importación real en Moodle |

## Recorrido canónico previsto

1. Cliente, servidor y contenido estático o dinámico.
2. Mecanismos de ejecución: CGI/FastCGI, procesos persistentes y contenedores.
3. Servidor web, proxy inverso, contenedor servlet, servidor embebido y servidor de aplicaciones.
4. Lenguajes, runtimes, librerías y frameworks de servidor.
5. Integración de HTML con plantillas y respuestas JSON.
6. HTTP actual: HTTP/1.1, HTTP/2, HTTP/3, métodos, estados, cabeceras, contenido, caché y QUERY.
7. HTTP seguro: TLS, certificados, cookies, cabeceras y sanitización de trazas.
8. Entorno reproducible con Java 25, Spring Boot 4 y Maven Wrapper.
9. Hello Server mínimo con HTML, JSON, `/health` y pruebas.
10. Laboratorio HTTP y evaluación completa de RA1.a-g.

## Alcance

### Obligatorio

- Un único recorrido Markdown enlazado desde el README y la portada de UD1.
- Java 25 y Spring Boot 4 con versiones reproducibles.
- Ejemplo mínimo ejecutable y probado.
- Evidencias observables para RA1.a-g.
- Seguridad HTTP básica y tratamiento seguro de trazas.
- Laboratorio con endpoints controlados o simulados, no sitios públicos arbitrarios.
- Evaluación coherente y política de IA alineada con el inicio del módulo.

### Fuera de alcance

- Desarrollar una API REST completa, JWT, OpenAPI, Battleship o despliegue avanzado; pertenecen a UD2.
- Repetir fundamentos de Java, Git o bases de datos de la Unidad 0.
- Mantener PDF como segunda fuente canónica.
- Catálogos enciclopédicos de tipos MIME o frameworks.

## Plan por prioridad

### P0 - recorrido y contención

- [x] Auditar todos los materiales y mapear la cobertura actual de RA1.a-g.
- [x] Excluir de MkDocs las fuentes Rmd, PDF y el plan docente desincronizados.
- [x] Crear el README con propósito, baseline, RA/CE, secuencia, evaluación y frontera con UD2.
- [x] Consolidar cliente/servidor, mecanismos de ejecución, infraestructura y selección de tecnologías en cuatro lecciones Markdown.
- [x] Consolidar HTTP moderno y seguridad en lecciones Markdown canónicas verificadas contra los RFC vigentes.
- [x] Consolidar el entorno reproducible en una lección Markdown canónica.
- [x] Corregir la portada `docs/unidades/ud01.md` y enlazar el recorrido.

### P1 - entorno y ejemplo

- [x] Documentar un entorno reproducible con Java 25, Spring Boot 4 y Maven Wrapper 3.9.11.
- [x] Crear Hello Server con HTML, JSON, `/health` y tres pruebas MockMvc.
- [x] Validar con OpenJDK 25.0.3 y Spring Boot 4.0.5: 3 pruebas verdes y respuestas HTTP `200` en los tres endpoints.

### P2 - práctica, seguridad y evaluación

- [x] Reformar el laboratorio HTTP con Hello Server local, evidencias sanitizadas y objetivos reproducibles.
- [x] Corregir la rúbrica y el mapa RA/CE del ejercicio para cubrir RA1.a-g.
- [x] Crear la guía de HTTPS/TLS, cookies y cabeceras de seguridad.
- [x] Completar el modelo de evaluación de RA1.a-g, crear la extensión TDD y revisar el banco GIFT.
- [ ] Importar el GIFT en Moodle antes de marcar su validación como realizada.

### P3 - limpieza

- [x] Eliminar Rmd y PDF tras consolidar todo el contenido útil en Markdown.
- [x] Retirar definitivamente la planificación efímera.
- [x] Actualizar los inventarios generales que calificaban como vigentes los materiales heredados.

## Criterio de cierre

- El recorrido es único, navegable y técnicamente actual.
- Java 25/Spring Boot 4 se reproducen con comandos documentados.
- El ejemplo mínimo y sus pruebas funcionan.
- RA1.a-g tienen contenido, actividad y evidencia de evaluación.
- Seguridad y privacidad aparecen desde el primer laboratorio.
- No se publican conversaciones, fuentes o derivados contradictorios.
- El inventario y la planificación reflejan el estado real.
- `mkdocs build --strict` termina sin errores nuevos.
