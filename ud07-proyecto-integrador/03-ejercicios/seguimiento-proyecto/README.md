# Plantilla — Seguimiento de hitos del proyecto DWES

Duplica esta plantilla para cada hito relativo acordado: propuesta, fundación, corte vertical, reglas y seguridad, integración y documentación, o defensa. El hito no necesita una fecha fija; debe dejar una evidencia revisable y una siguiente acción.

## Estado del hito

| Hito relativo | Estado | Responsable(s), según configuración docente | Enlace principal | Próxima acción |
|---|---|---|---|---|
| `<nombre del hito>` | `<pendiente / en curso / para revisión / cerrado>` | `<persona o personas>` | `<issue, tarjeta o documento>` | `<acción verificable>` |

## Contexto y entrega

- **Proyecto:** `<nombre>`
- **Stack de servidor:** `<Spring Boot 4 con Java 25 / Laravel 12 con PHP 8.4>`
- **Objetivo del hito:** `<resultado observable de servidor>`
- **Planificado:** `<regla, flujo, modelo, prueba o documentación que se esperaba completar>`
- **Entregado:** `<qué está realmente disponible y qué se ha dejado fuera>`
- **Diferencia y decisión:** `<desviación, motivo y ajuste de alcance; o “sin diferencias relevantes”>`

## Trazabilidad y enlaces

| Evidencia | Referencia |
|---|---|
| Commit(s) relevante(s) | `<hash o URL>` |
| Etiqueta, release o marca equivalente | `<referencia o “todavía no aplica”>` |
| Repositorio e incidencia/tarjeta | `<URL>` |
| Documentación o runbook | `<URL o ruta>` |

## Evidencia de servidor

### Demostración del flujo

`<acción o entrada → endpoint/controlador → servicio/regla → datos/migración → respuesta o error>`

- **Cómo se demuestra localmente:** `<pasos breves y resultado esperado>`
- **Regla, validación o error comprobado:** `<caso concreto>`
- **Pruebas ejecutadas:** `<comando, alcance y resultado>`

### Datos y migraciones

- **Cambio de modelo o migración:** `<qué cambia y cómo se reproduce>`
- **Datos de prueba o semilla:** `<origen seguro y cómo cargarlos, si aplica>`
- **Comprobación de integridad:** `<restricción, transición o caso de error revisado>`

### Integración con cliente, si existe

- **Escenario visible:** `<acción del cliente → petición → respuesta/error mostrado>`
- **Contrato o endpoint usado:** `<enlace OpenAPI, ruta o referencia>`
- **Cómo se comprueba que llega al servidor propio:** `<evidencia>`

Si no hay cliente separado, indica la alternativa usada para demostrar el flujo de servidor. El cliente, el diseño y el despliegue no sustituyen esta evidencia.

## Autoría y responsabilidades

Registra la aportación real de cada persona cuando el docente haya autorizado trabajo en equipo. En trabajo individual, registra tu propia responsabilidad. No presupone un tamaño de equipo.

| Persona | Área, caso de uso o decisión asumida | Evidencia localizable | Qué puede explicar o modificar |
|---|---|---|---|
| `<nombre>` | `<responsabilidad>` | `<commit, issue, prueba o ruta>` | `<aportación>` |

## Riesgos, feedback y continuidad

- **Riesgo o bloqueo actual:** `<uno concreto; si no existe, indicarlo>`
- **Ayuda o decisión necesaria:** `<pregunta para la revisión, si aplica>`
- **Feedback docente recibido:** `<observación y enlace, si existe>`
- **Acción acordada:** `<acción concreta y evidencia de cierre>`
- **Siguiente hito relativo:** `<nombre y resultado verificable>`
