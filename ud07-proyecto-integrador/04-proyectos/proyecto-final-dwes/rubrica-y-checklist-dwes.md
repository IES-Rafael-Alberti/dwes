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

### 1. Dominio y arquitectura servidor

- [ ] El dominio aprobado contiene reglas, procesos o restricciones no triviales.
- [ ] La entrada HTTP/MVC, los casos de uso/reglas y el acceso a datos tienen responsabilidades distinguibles.
- [ ] Los controladores no concentran reglas de negocio ni persistencia sin una razón documentada.
- [ ] **Spring Boot 4/Java 25:** la estructura usa controladores, servicios/casos de uso, DTO cuando evita exponer el modelo y repositorios/JPA de manera coherente.
- [ ] **Laravel 12/PHP 8.4:** la estructura usa controladores, servicios/acciones cuando aportan separación, Form Requests/Resources y Eloquent de manera coherente.

### 2. Datos, migraciones e integridad

- [ ] El modelo representa el dominio y sus restricciones, no solo las pantallas.
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
- [ ] El comportamiento del servidor puede revisarse aunque el cliente proceda de otro módulo.
- [ ] La interfaz, diseño o despliegue no se presentan como sustitutos de la evidencia de servidor.

### 7. Git, trazabilidad y defensa

- [ ] El alojamiento Git es accesible para el docente, aunque el repositorio sea privado.
- [ ] Commits, issues/tablero y etiquetas/releases periódicas muestran evolución y responsabilidades.
- [ ] El trabajo en equipo permite identificar la autoría y aportación de cada integrante.
- [ ] La defensa recorre el servidor en vivo y permite explicar o realizar un cambio pequeño dirigido.

## Restricciones de stack

Solo se admiten **Spring Boot 4 con Java 25** o **Laravel 12 con PHP 8.4** para el servidor DWES. **Node.js/Express no es una ruta equivalente ni aceptada.**

Esta lista guía la revisión; no asigna pesos, porcentajes ni puntos. Debe leerse junto con el [enunciado](../../01-documentacion/01-proyecto-final-dwes.md) y la [matriz RA/CE](ra-ce-evidencias.md).
