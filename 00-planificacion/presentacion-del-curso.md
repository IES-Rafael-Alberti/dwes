# Presentación del curso: Desarrollo Web en Entorno Servidor

## Qué vamos a aprender

En DWES desarrollarás aplicaciones web cuyo comportamiento depende del servidor: recibir peticiones, aplicar reglas de negocio, guardar y recuperar datos, proteger recursos, devolver respuestas correctas y comprobar que todo funciona. El objetivo no es solo conseguir una aplicación que parezca funcionar, sino construir un servidor que puedas ejecutar, probar, explicar, depurar y modificar de forma autónoma.

Trabajaremos principalmente con **Spring Boot 4 y Java 25**, y con **Laravel 12 y PHP 8.4**. Estas son las rutas válidas para las evidencias de servidor del módulo. Node.js, Express u otros frameworks no impartidos no constituyen una alternativa evaluable en DWES.

## Itinerario del módulo

La planificación concreta se ajustará al calendario del curso y a la formación en empresa. El recorrido previsto es este:

| Unidad | Trabajo principal | Resultados de aprendizaje |
| --- | --- | --- |
| UD00. Previos | Nivelación de Java, pruebas, herramientas y diseño de datos necesario para comenzar. No es una unidad evaluable independiente. | Apoyo a UD1 y UD2. |
| UD1. Entorno servidor | Arquitecturas, HTTP, Java y Spring Boot, primer servidor y seguridad básica. | RA1. |
| UD2. APIs REST | Spring Boot, TDD, contratos OpenAPI, persistencia, validación, seguridad y APIs REST. | RA1, RA4–RA7. |
| UD3. MVC | Aplicaciones MVC con Spring Boot y Thymeleaf. | RA4–RA6, RA8. |
| UD4. PHP | PHP 8.4, formularios, sesiones, PDO y arquitectura por capas. | RA1–RA6. |
| UD5. Laravel | Laravel 12, API-first, Eloquent, Sanctum y Policies. | RA2, RA4–RA7. |
| UD6. Aplicaciones híbridas | APIs externas y repositorios heterogéneos de información. | RA9. |
| UD7. Proyecto integrador | Proyecto de servidor, seguimiento por hitos, integración, documentación y defensa. | RA1–RA9. |

UD1, UD2 y UD3 forman el núcleo inicial y deben quedar bien consolidadas. Tras la incorporación a la formación en empresa se continuarán los bloques restantes y el proyecto con el ritmo que permita el calendario real.

## Cómo trabajaremos

- Práctica guiada y progresiva: ejemplos, ejercicios, retos y proyectos.
- **TDD** desde el inicio: escribir o comprender una prueba, implementar, ejecutar y corregir.
- **SDD** a partir de los bloques de aplicaciones: definir contratos, requisitos y decisiones antes de extender el código.
- Seguridad transversal: validación en servidor, autenticación, autorización, errores, secretos, sesiones, inyección, CSRF, XSS y configuración segura según la tecnología usada.
- Git y GitHub para conservar el historial, las decisiones, los cambios y la autoría del trabajo.
- Trabajo cooperativo cuando se indique, con responsabilidades y aportaciones localizables.

La IA generativa seguirá una progresión: inicialmente no sustituye el trabajo de fundamentos; después podrá utilizarse como asistencia supervisada para pruebas, depuración, comparación de alternativas o refactorización. Todo uso material debe declararse, indicar qué se aceptó, modificó o rechazó y demostrar su verificación. No se introducirán secretos, credenciales, datos personales ni información privada en las consultas a herramientas de IA.

## Qué se evaluará

La evaluación es continua, individual y criterial. Cada actividad indica los resultados de aprendizaje (RA) y criterios de evaluación (CE) que evidencia. Para aprobar el módulo se necesita una calificación mínima de 5 en cada RA.

| RA | Qué acredita | Peso orientativo |
| --- | --- | ---: |
| RA1 | Elección razonada de arquitecturas y tecnologías de servidor. | 10 % |
| RA2 | Integración de código ejecutable en páginas y documentos. | 10 % |
| RA3 | Estructuras y bloques embebidos en lenguajes de marcas. | 5 % |
| RA4 | Aplicaciones web con funcionalidades de servidor. | 10 % |
| RA5 | Separación entre presentación y lógica de negocio. | 10 % |
| RA6 | Datos, persistencia, seguridad e integridad. | 20 % |
| RA7 | Servicios web y protocolos. | 15 % |
| RA8 | Páginas dinámicas generadas desde el servidor. | 10 % |
| RA9 | Aplicaciones híbridas y fuentes de datos heterogéneas. | 10 % |

Las evidencias incluirán cuestionarios individuales, código ejecutable, pruebas automatizadas, documentación técnica, repositorios, demostraciones y defensas. Un cuestionario o una entrega de grupo no sustituyen la demostración práctica individual de las competencias de servidor.

Habrá dos hitos individuales de acreditación práctica:

1. **APIs y arquitectura de servidor**, al finalizar el trabajo principal de UD2: seguir una petición, identificar sus capas, modificar un endpoint, validar datos y comprobar el resultado.
2. **Proyecto final DWES**, durante UD7: ejecutar, explicar, depurar y modificar una parte del servidor del proyecto.

El profesorado podrá solicitar una auditoría individual cuando las evidencias no permitan acreditar la autoría o comprensión: explicar una decisión, seguir el recorrido de una petición, corregir un error, modificar código o una prueba, o resolver un caso límite. No es una sanción por usar IA; sirve para obtener evidencia de los RA y CE afectados.

## Entregas, equipos y plazos

Cada enunciado indicará la entrega, la rúbrica, los RA/CE asociados y el plazo. Entrega código que ejecute, instrucciones para reproducirlo, pruebas pertinentes y la documentación solicitada. Antes de enviar, comprueba que el repositorio es accesible al profesorado y que el proyecto puede arrancar siguiendo el `README` o runbook.

En las prácticas cooperativas cada integrante deberá aportar y documentar una parte localizable mediante commits, incidencias, tareas, documentación o evidencias equivalentes. La nota es individual: que el proyecto de un equipo funcione no acredita automáticamente a todas las personas.

La puntualidad forma parte del procedimiento de evaluación:

- Cada periodo iniciado de 24 horas tras el plazo reduce un 5 % la calificación académica de la actividad, hasta un máximo del 35 %.
- Salvo que el enunciado indique otro cierre, la entrega ordinaria termina siete días naturales después del plazo inicial.
- Tras ese cierre se aplicará el procedimiento de recuperación de los RA y CE pendientes.
- Una ampliación o excepción debe estar justificada y comunicarse conforme a las normas del aula.

## Proyecto integrador de DWES

En UD7 desarrollarás un proyecto con un servidor propio usando Spring Boot/Java o Laravel/PHP. Puede coordinarse con Proyecto Intermodular, Desarrollo web en entorno cliente, Diseño de interfaces o Despliegue, pero DWES evalúa exclusivamente las evidencias de servidor: petición, lógica de negocio, persistencia, seguridad, pruebas y respuesta.

La interfaz, el diseño visual, el despliegue o un frontend desarrollado en otro módulo no sustituyen el servidor. Si no hay cliente, podrás demostrar la API mediante Insomnia, Postman u otra herramienta REST.

Los hitos relativos del proyecto serán:

| Hito | Evidencia mínima |
| --- | --- |
| Propuesta y aprobación | Dominio, problema, usuarios, reglas, alcance, stack y persistencia razonados. |
| Fundación y modelo | Diseño, arquitectura, modelo de datos y migración inicial. |
| Corte vertical | Flujo completo con regla de negocio, validación, persistencia y respuesta o error. |
| Reglas y seguridad | Casos de uso relevantes, validación, errores, autenticación y autorización si corresponden. |
| Integración, pruebas y documentación | Pruebas, contrato OpenAPI cuando exista API, README/runbook y ejecución reproducible. |
| Defensa final | Repositorio y versión final, demostración, explicación del servidor y modificación o corrección dirigida. |

El proyecto debe incluir un dominio no trivial, separación de responsabilidades, persistencia íntegra y reproducible mediante migraciones, validación en servidor, tratamiento de errores, pruebas automatizadas y documentación de arranque. La defensa es individual: cada autor debe poder localizar, explicar y modificar su aportación real.

## Recuperación y asistencia

La recuperación se centrará en los RA y CE no superados y podrá requerir prácticas, pruebas o defensas específicas. No será necesario repetir las evidencias ya acreditadas.

La pérdida del derecho a evaluación continua se produce al no alcanzar el 80 % de asistencia. En ese caso se aplicará un Plan Personalizado de Evaluación con tareas equivalentes, defensa oral y prueba práctica sobre los RA y CE que corresponda.

## Dónde está el material

- Documentación pública del módulo: <https://ies-rafael-alberti.github.io/dwes/>.
- Repositorio del módulo: <https://github.com/IES-Rafael-Alberti/dwes>.
- Material por unidad: `udXX-*/`.
- Proyecto integrador: `ud07-proyecto-integrador/`.
- Rúbrica y checklist del proyecto: `ud07-proyecto-integrador/04-proyectos/proyecto-final-dwes/rubrica-y-checklist-dwes.md`.
- Programación didáctica: `../../../ProgramacionesDidacticas/PD-CFGS-DAW-2/src/mod5.md`.
