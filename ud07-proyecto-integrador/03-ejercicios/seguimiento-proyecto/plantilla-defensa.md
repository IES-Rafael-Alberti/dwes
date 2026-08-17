# Plantilla — Preparación de defensa del proyecto DWES

Usa este guion para preparar una demostración breve, verificable y centrada en tu servidor. El docente decide las preguntas concretas y el cambio pequeño que se plantee. Cliente y despliegue pueden aparecer como contexto o evidencia de flujo, pero DWES no los califica de forma independiente.

## Ficha de la defensa

| Campo | Información |
|---|---|
| Proyecto y stack | `<nombre — Spring Boot 4/Java 25 o Laravel 12/PHP 8.4>` |
| Persona que defiende y aportación | `<nombre → responsabilidad real>` |
| Commit, etiqueta o release demostrada | `<referencia>` |
| Ruta de arranque local | `<enlace a README/runbook>` |

## Recorrido del servidor

### 1. Arranque local reproducible

- **Requisitos y configuración segura:** `<qué preparar, sin secretos>`
- **Pasos o comandos:** `<pasos>`
- **Comprobación de que el servidor está activo:** `<ruta, pantalla o salida esperada>`

### 2. Flujo principal

Explica y muestra este recorrido en el código y durante la ejecución:

`<entrada o acción → petición/formulario → endpoint/controlador → servicio/caso de uso → regla → persistencia/migración → respuesta>`

- **Regla de negocio demostrada:** `<regla y resultado>`
- **Modelo y datos implicados:** `<entidades, relación o restricción>`
- **Capa o archivo donde se localiza cada responsabilidad:** `<referencias>`

### 3. Caso de error

`<entrada inválida, recurso ausente o conflicto de regla → validación/manejo → respuesta o mensaje controlado>`

- **Por qué el servidor rechaza o controla el caso:** `<explicación>`
- **Prueba o evidencia relacionada:** `<comando, test o referencia>`

### 4. Integración con cliente, si existe

- **Escenario real:** `<acción visible → petición al servidor propio → respuesta o error visible>`
- **Cómo se observa el flujo de servidor:** `<endpoint, logs de desarrollo seguros, prueba o herramientas del navegador>`

Si no existe cliente separado, muestra la entrada MVC, una petición HTTP o una prueba que permita recorrer el mismo flujo. La interfaz, el diseño y el despliegue no sustituyen el servidor en la defensa de DWES.

## Decisiones que debes poder explicar

| Tema | Decisión y evidencia |
|---|---|
| Arquitectura | `<fronteras entre entrada, reglas/casos de uso y datos>` |
| Modelo y migraciones | `<por qué el modelo protege una regla o restricción>` |
| Seguridad | `<autenticación/autorización proporcional, o por qué no aplica>` |
| Pruebas | `<qué regla, error o flujo cubren y cómo se ejecutan>` |
| Trazabilidad | `<commits, issue/tablero y etiqueta/release relevantes>` |

## Cambio pequeño guiado

Prepara una modificación limitada sobre el código real; no anticipes una respuesta prefabricada. El docente decidirá el cambio concreto o las preguntas y puede pedir que razones las capas afectadas.

- **Área elegida para practicar:** `<regla, validación, respuesta o consulta pequeña>`
- **Capas posiblemente afectadas:** `<entrada → servicio/regla → datos/pruebas/documentación>`
- **Cómo comprobarías el resultado:** `<prueba, ejecución local y caso de error si corresponde>`
- **Límite conocido o riesgo:** `<qué habría que revisar antes de cambiar>`

## Cierre

- **Evidencia que mostrarás primero:** `<flujo o prueba más representativa>`
- **Pendiente conocido y plan:** `<si existe>`
- **Pregunta para el docente:** `<si necesitas confirmar una decisión>`
