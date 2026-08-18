# Seguimiento por hitos del proyecto DWES

Los hitos ordenan el aprendizaje y el feedback. Se adaptan al calendario escolar; el objetivo es que el proyecto esté casi terminado a finales de enero, sin convertir esa referencia en una fecha fija.

## Hitos relativos

| Hito | Entregable | Evidencia Git | Feedback docente | Punto de control | Respuesta al riesgo |
|---|---|---|---|---|---|
| Propuesta y aprobación | Dominio, problema, usuarios, al menos tres reglas, alcance, stack, persistencia y diseño inicial razonado. | Issue, tarjeta o documento inicial enlazado al repositorio. | Validar viabilidad, no trivialidad, límites y tecnología permitida. | Tema aprobado antes de consolidar desarrollo. | Reducir alcance, aclarar responsabilidad individual/equipo o corregir una elección técnica no permitida. |
| Fundación y modelo | Diseño razonado, diagrama ER si procede, arquitectura inicial, modelo y migración inicial; arranque local. | Commit/release o etiqueta de hito y tarea enlazada. | Revisar decisiones, separación de responsabilidades y modelo. | El esquema se reproduce y una ruta mínima funciona. | Corregir diseño, modelo o migraciones antes de acumular funcionalidades. |
| Corte vertical | Un flujo completo con regla, validación, persistencia y respuesta/error. | Commits y prueba(s) que permitan revisar el flujo. | Revisar que no sea solo CRUD ni interfaz simulada. | Se puede demostrar extremo a extremo. | Recortar pantallas y reforzar una regla de negocio verificable. |
| Reglas y seguridad | Casos de uso relevantes, validación, errores y seguridad cuando aplique. | Etiqueta/release y evidencia de pruebas. | Comprobar amenazas y autorizaciones pertinentes al dominio. | Rutas y datos sensibles se comportan según su política. | Eliminar seguridad ornamental; proteger primero las acciones y datos reales. |
| Integración, pruebas y documentación | Cliente integrado si existe, pruebas, OpenAPI si hay API, README/runbook y actualización del diseño si divergió. | Etiqueta/release, enlaces a documentación y resultado de pruebas. | Revisar reproducibilidad, trazabilidad y coherencia diseño-implementación. | Otra persona puede arrancar y probar el servidor siguiendo el repositorio. | Simplificar integración, reparar contratos, actualizar el diseño y documentar el camino mínimo. |
| Defensa final | Guion de recorrido, evidencias y preparación de cambio pequeño. | Release final y tablero/issue cerrado o equivalente. | Ensayo de preguntas y de localización de capas. | Cada autor puede explicar y modificar su parte. | Reasignar explicación, limpiar deuda crítica y practicar sobre el código real. |

## Bucle de feedback

En cada hito: presenta el entregable y su enlace Git, registra el feedback como issue/tarea o comentario equivalente, decide una acción concreta y muestra su cierre en el siguiente punto de control. Las etiquetas o releases son marcas de estado; no sustituyen las pruebas ni la revisión.

## Plantilla de estado para revisión docente

```markdown
## Estado — <nombre del proyecto> — <hito>

- Stack: Spring Boot 4 con Java 25 / Laravel 12 con PHP 8.4
- Responsables y aportación actual: <persona → área o caso de uso>
- Enlace al repositorio y etiqueta/release: <URL>
- Flujo demostrable: <acción → endpoint/MVC → servicio/regla → datos → respuesta/error>
- Diseño o decisión actualizada: <enlace + motivo, si cambió respecto a lo aprobado>
- IA material usada: <No / enlace al registro y evidencia de verificación>
- Pruebas ejecutadas y resultado: <comando + resultado>
- Documentación de arranque: <enlace>
- Riesgo o bloqueo: <uno, concreto>
- Feedback recibido y siguiente acción: <decisión verificable>
```

No se asignan pesos ni calificaciones artificiales a los hitos. La revisión final usa evidencias observables conforme a la [rúbrica/checklist](../04-proyectos/proyecto-final-dwes/rubrica-y-checklist-dwes.md).
