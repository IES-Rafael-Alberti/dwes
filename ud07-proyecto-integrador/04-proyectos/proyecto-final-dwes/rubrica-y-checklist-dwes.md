# Rúbrica y checklist del proyecto final DWES

La revisión observa comportamiento, código, pruebas, trazabilidad y defensa. Coordinar cliente o despliegue con otros módulos es válido, pero el servidor se evalúa de forma independiente.

## Niveles de logro observables

| Nivel | Evidencia observable |
|---|---|
| Inicial | La evidencia falta, no ejecuta o no permite atribuir el comportamiento al servidor propio. |
| En progreso | Existe una implementación parcial; predominan caminos felices, acoplamiento o documentación insuficiente. |
| Consolidado | El comportamiento relevante funciona, se prueba y se explica con errores, datos y responsabilidades claros. |
| Sólido | Además, las decisiones están justificadas, los límites y casos relevantes son robustos y la trazabilidad permite una revisión autónoma. |

## Criterios y lista de comprobación

## Aplicación del modelo 2026/2027

Las prácticas previas pueden ser cooperativas, pero este proyecto final es el
**hito individual 2**. La evaluación separa resultado técnico, responsabilidad
individual, pruebas, trazabilidad, defensa y, si existe, evidencia de
integración. La ausencia de cliente no penaliza: se aceptan Insomnia, Postman u
otro cliente REST para demostrar llamadas al servidor.

### 1. Dominio y arquitectura servidor

- [ ] El dominio aprobado contiene al menos tres reglas, procesos o restricciones no triviales, independientes y comprobables en servidor.
- [ ] Existe un diseño versionado que explica las decisiones significativas y se actualiza cuando la implementación diverge materialmente.
- [ ] La entrada HTTP/MVC, los casos de uso/reglas y el acceso a datos tienen responsabilidades distinguibles.
- [ ] Los controladores no concentran reglas de negocio ni persistencia sin una razón documentada.
- [ ] **Spring Boot 4/Java 25:** la estructura aplica lo visto y pertinente: configuración, entidades/modelos, controladores, servicios/casos de uso, repositorios/JPA, DTO/formularios, validación, errores, seguridad si aplica y migraciones obligatorias.
- [ ] **Laravel 12/PHP 8.4:** la estructura separa responsabilidades de forma idiomática; los controladores no absorben reglas de negocio o persistencia sin justificación.

### 2. Datos, migraciones e integridad

- [ ] El modelo representa el dominio y sus restricciones, no solo las pantallas; para datos relacionales incluye un diagrama ER, relaciones y cardinalidades.
- [ ] El modelo relacional define y justifica el ciclo de vida/retención de históricos (conservación, archivo, anonimización, baja lógica o borrado); `ON DELETE CASCADE` no se usa por defecto.
- [ ] Si el dominio maneja existencias, las reglas de disponibilidad, reserva o ajuste, validación, concurrencia/integridad y auditoría/ciclo de vida están diseñadas y probadas.
- [ ] PostgreSQL es la opción aplicada por defecto; MySQL o MongoDB solo se usan con la autorización y justificación exigidas; para MongoDB se explica el diseño y la consistencia/integridad que gestiona la aplicación.
- [ ] Las migraciones o mecanismo equivalente permiten recrear los cambios sin editar manualmente la base de datos.
- [ ] Las relaciones, restricciones y transacciones necesarias protegen la integridad.
- [ ] Las consultas y transformaciones relevantes evitan errores previsibles y se justifican.

### 3. Validación, errores y seguridad aplicable

- [ ] Las entradas se validan en el servidor y los errores son comprensibles y coherentes.
- [ ] Los conflictos de reglas y recursos ausentes tienen un tratamiento explícito cuando aplica.
- [ ] Si el dominio requiere identidad o restricciones de acceso, autenticación y autorización protegen acciones y datos reales.
- [ ] No hay contraseñas en texto plano, tokens, claves ni archivos `.env` con valores reales versionados.

### 4. API o MVC y contrato

- [ ] La aplicación API, MVC o mixta demuestra flujos completos de servidor.
- [ ] Cuando hay API, recursos, métodos, estados HTTP y payloads son consistentes.
- [ ] Cuando hay API expuesta, OpenAPI documenta los flujos y la seguridad relevantes.
- [ ] Cuando hay MVC, formularios, estado y errores se procesan en el servidor de forma verificable.

### 5. Pruebas y ejecución local

- [ ] Las pruebas automatizadas cubren reglas, casos de error o flujos críticos del dominio.
- [ ] El proyecto arranca localmente con instrucciones reproducibles y sin intervención del autor.
- [ ] README/runbook indica requisitos, configuración segura, migraciones, arranque, pruebas y demostración mínima.

### 6. Integración cliente-servidor

- [ ] Si existe cliente, al menos un flujo visible prueba la cadena cliente → petición → endpoint → servicio/regla → persistencia → respuesta/error.
- [ ] Si no existe cliente, una colección o guion de Insomnia, Postman u otro cliente REST demuestra que los endpoints, respuestas y errores del servidor son invocables.
- [ ] El comportamiento del servidor puede revisarse aunque el cliente proceda de otro módulo.
- [ ] La ausencia de cliente no penaliza por sí misma; la demostración REST alternativa no se presenta como integración completa.
- [ ] La interfaz, diseño o despliegue no se presentan como sustitutos de la evidencia de servidor.

### 7. Git, trazabilidad, IA y defensa

- [ ] El alojamiento Git (GitHub, GitLab, Bitbucket o equivalente) es accesible para el docente, aunque el repositorio sea privado.
- [ ] Commits, issues/tablero y etiquetas/releases periódicas muestran evolución y responsabilidades.
- [ ] El trabajo en equipo permite identificar la autoría y aportación de cada integrante.
- [ ] La declaración de IA marca **No** si no se usó o registra cada uso material, qué se cambió/rechazó y la evidencia de la verificación.
- [ ] La defensa recorre el servidor en vivo y permite explicar o realizar un cambio pequeño dirigido, incluidas las decisiones o resultados de IA usados.
- [ ] CI/CD o despliegue, si se muestran, se registran solo como integración opcional: no sustituyen evidencias de servidor ni se califican en DWES.

## Restricciones de stack

Solo se admiten **Spring Boot 4 con Java 25** o **Laravel 12 con PHP 8.4** para el servidor DWES. **Node.js, Express y cualquier framework de servidor no impartido no son una ruta equivalente ni aceptada.**

Esta lista guía la revisión; no asigna pesos, porcentajes ni puntos. Debe leerse junto con el [enunciado](../../01-documentacion/01-proyecto-final-dwes.md) y la [matriz RA/CE](ra-ce-evidencias.md).
