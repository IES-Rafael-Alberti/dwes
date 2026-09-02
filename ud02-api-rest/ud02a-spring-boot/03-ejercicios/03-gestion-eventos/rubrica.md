# Rúbrica — Seguridad JWT en API de Gestión de Eventos

## RA/CE evaluados

- RA5.g: Mecanismos de autenticación y autorización
- RA5.h: Pruebas y documentación del código

## Criterios de evaluación

| Criterio | Excelente (4) | Notable (3) | Aprobado (2) | Insuficiente (1) | Peso |
|----------|---------------|-------------|--------------|-------------------|------|
| **Autenticación JWT** | Login, register, me y logout funcionan con JWT firmado, validación completa (firma, exp, jti) | Login y register funcionan, falta logout o validación parcial | Solo login funciona, sin register ni logout | No implementa autenticación | 30% |
| **Autorización por roles** | Endpoints protegidos según rol (ORGANIZADOR vs PARTICIPANTE) con restricciones precisas | Roles implementados pero con alguna ruta mal protegida | Roles definidos pero sin restricción efectiva en endpoints | Sin autorización | 20% |
| **CORS** | Configuración completa de CORS para el frontend Angular (orígenes, métodos, headers) | CORS configurado pero demasiado permisivo | CORS configurado incorrectamente o no funcional | Sin configuración CORS | 15% |
| **DTOs y validación** | DTOs específicos para auth, `@Valid` en todos los endpoints, no se exponen entidades | DTOs presentes pero alguna entidad se expone | Uso mínimo de DTOs, validación insuficiente | Sin DTOs ni validación | 15% |
| **Tests (RA5.h)** | Tests existentes siguen pasando + nuevos tests para auth | Tests existentes pasan, sin tests nuevos | Tests existentes pasan parcialmente | Tests rotos | 10% |
| **Declaración IA** | Presente, detallada y verificable | Presente pero genérica | Presente incompleta | Ausente | 10% |

## Notas

- Para obtener el 4 en cualquier criterio, el estudiante debe poder defenderlo oralmente si se le pregunta.
- El uso de IA sin declarar se considera falta de honestidad académica.

## Aplicación 2026/2027

La entrega se organiza en grupos de 3-4 y slices verticales de dominio, API,
persistencia y pruebas. Se exige issue, README, revisión de pares y defensa
individual. El resultado común no sustituye la comprobación de autoría.

La ruta 2026/2027 exige además migraciones Flyway reproducibles, sin creación
automática de esquema en producción, y un contrato OpenAPI versionado con
pruebas MockMvc del slice. Estas evidencias cubren RA6.e-g y RA7.g-h; su peso
es el 25 % de UD2a para esta práctica en 2026/2027.

| Evidencia adicional 2026/2027 | Comprobación mínima |
| --- | --- |
| Migraciones y perfil de producción | Flyway aplica el esquema desde cero y el perfil productivo no usa `ddl-auto` para crearlo o actualizarlo. |
| Contrato OpenAPI | El YAML versionado declara las operaciones, JWT, esquemas y errores del slice. |
| Conformidad | Una prueba MockMvc contrasta al menos el flujo correcto y un error del slice contra el contrato. |
