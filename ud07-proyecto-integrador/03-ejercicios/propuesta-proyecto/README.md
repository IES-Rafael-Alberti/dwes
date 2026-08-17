# Plantilla — Propuesta y aprobación del proyecto DWES

Completa esta plantilla antes de consolidar el desarrollo. Describe decisiones reales de tu proyecto: no incluyas una solución ni datos sensibles. Entrega el documento o enlázalo desde la incidencia/tarjeta inicial del repositorio, según indique el docente.

## Identificación

| Campo | Información |
|---|---|
| Nombre del proyecto | `<nombre>` |
| Alumno/a o integrantes | `<nombres, según la configuración indicada por el docente>` |
| Reparto inicial de responsabilidad, si procede | `<persona → área, caso de uso o responsabilidad de servidor>` |
| Repositorio Git y acceso docente | `<URL y forma de acceso; puede ser privado>` |
| Issue, tablero o registro inicial | `<URL o referencia>` |

## Problema y alcance de servidor

### Dominio, problema y usuarios

- **Dominio:** `<ámbito real del proyecto>`
- **Problema que se quiere resolver:** `<situación concreta>`
- **Usuarios o roles previstos:** `<quién usa el sistema y para qué>`
- **Límite inicial:** `<qué queda dentro y qué queda fuera de este primer alcance>`

### Reglas no triviales del servidor

Escribe reglas, estados, restricciones o procesos que el servidor deba decidir y validar. Un listado de pantallas o de operaciones CRUD no basta.

| Regla, estado o restricción | Cuándo se aplica | Resultado esperado o error |
|---|---|---|
| `<regla 1>` | `<situación>` | `<respuesta o cambio de estado>` |
| `<regla 2>` | `<situación>` | `<respuesta o cambio de estado>` |

### Modelo de datos inicial

Incluye un boceto sencillo: entidades o agregados relevantes, relaciones y restricciones que afecten a las reglas. Puede ser un diagrama enlazado o texto estructurado.

`<enlace, imagen versionada sin datos privados o descripción del modelo>`

### Flujo cliente-servidor previsto

Describe un flujo que pueda comprobarse de extremo a extremo. Si no habrá cliente separado, explica la entrada MVC o HTTP que lo activa.

`<acción visible o entrada → petición/formulario → endpoint/controlador → servicio o regla → datos → respuesta o error>`

## Decisiones técnicas iniciales

### Stack de servidor

Marca una ruta y justifica brevemente por qué encaja con el alcance:

- [ ] Spring Boot 4 con Java 25 — `<justificación>`
- [ ] Laravel 12 con PHP 8.4 — `<justificación>`

**Express y Node.js no se aceptan como servidor para la evidencia de DWES.** No los propongas como backend alternativo de este proyecto.

### Seguridad y autenticación

- **¿Hay identidades, datos o acciones que proteger?** `<sí/no y por qué>`
- **Necesidad de autenticación o autorización:** `<mecanismo proporcional al riesgo, o por qué no aplica>`
- **Validación y errores relevantes:** `<entradas, conflictos o ausencias que el servidor debe controlar>`

### Integraciones externas

Solo completa esta sección si una integración externa responde a una necesidad real del dominio. No es un requisito de UD7.

- **Servicio, dataset o API externa:** `<nombre y enlace>`
- **Justificación:** `<qué necesidad resuelve>`
- **Fallo, límite o alternativa local prevista:** `<cómo se comportará el servidor>`

### Coordinación opcional con otros módulos

`<nota opcional de calendario o ritmo con Cliente, DIW o DAW>`

Esta nota solo sirve para coordinar tiempos. No crea entregables compartidos ni sustituye las evidencias de servidor de DWES.

## Puesta en marcha y primer hito

### Plan de arranque local

- **Requisitos locales conocidos:** `<runtime, base de datos u otras dependencias>`
- **Configuración segura:** `<variables necesarias documentadas sin valores secretos>`
- **Migraciones o creación de datos:** `<plan inicial>`
- **Comando o pasos de arranque previstos:** `<pasos reproducibles>`
- **Comprobación mínima tras arrancar:** `<ruta, caso de uso o prueba>`

### Riesgos iniciales

| Riesgo o incertidumbre | Impacto en el servidor | Primera acción de reducción |
|---|---|---|
| `<riesgo>` | `<impacto>` | `<acción verificable>` |

### Primer hito relativo

- **Hito:** `<por ejemplo, Fundación y modelo>`
- **Resultado verificable:** `<qué se podrá ejecutar, revisar o probar>`
- **Evidencia Git prevista:** `<commit, etiqueta/release e issue/tarjeta>`
- **Siguiente revisión solicitada:** `<qué necesitas confirmar con el docente>`

## Registro de aprobación docente

| Campo | Registro |
|---|---|
| Estado | `<pendiente / aprobado / aprobado con condiciones / cambios solicitados>` |
| Persona que aprueba o revisa | `<nombre>` |
| Fecha de la revisión | `<fecha>` |
| Condiciones de aprobación | `<condiciones, si las hay>` |
| Cambios solicitados | `<cambios concretos, si los hay>` |
| Próxima acción del alumnado | `<acción y evidencia esperada>` |

## Si el tema no se aprueba

Registra el motivo y propone una alternativa más acotada o un tema de reserva. Pide una nueva revisión antes de consolidar el desarrollo. Conserva el enlace a esta propuesta y al feedback para que la decisión sea trazable.
