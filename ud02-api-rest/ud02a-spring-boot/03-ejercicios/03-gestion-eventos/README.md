# Ejercicio 03 — Seguridad JWT en API de Gestión de Eventos

## Objetivo

Añadir autenticación y autorización JWT a una API REST de gestión de eventos existente (Spring Boot), preparándola para ser consumida desde un cliente Angular. Implementar registro de usuarios, login JWT, control de acceso por roles y CORS.

## Contenidos y Recursos

- `01-SeguridadJWT.md`: Enunciado detallado con requisitos mínimos y recomendaciones.
- `recursos/GestionEventos/`: Proyecto base (Spring Boot 4.0.5, Gradle, JPA, H2) con la API de eventos ya implementada (controladores, servicios, repositorios, DTOs y tests).

## Tareas

1. **Autenticación JWT**: Endpoints `POST /auth/login`, `POST /auth/register`, `GET /auth/me`, `POST /auth/logout`.
2. **Autorización por roles**: Restringir endpoints según rol (`PARTICIPANTE`, `ORGANIZADOR`).
3. **CORS**: Configurar CORS para el cliente Angular (orígenes, métodos, headers permitidos).
4. **Seguridad en endpoints**: Proteger creación/edición/borrado para ORGANIZADOR; consultas para autenticados.
5. **DTOs y manejo de errores**: No exponer entidades; usar DTOs, `@Valid`, y el `@ControllerAdvice` existente para 401/403.
6. **Persistencia reproducible**: Añadir Flyway, una migración inicial del
   esquema de eventos y una migración propia del slice; en producción no se
   permite crear ni actualizar el esquema con Hibernate.
7. **Contrato OpenAPI**: Versionar un documento OpenAPI de la API, con los
   endpoints de autenticación y del slice, esquemas, respuestas de error y
   seguridad JWT; contrastarlo con pruebas MockMvc.

Para la persistencia, añade `org.flywaydb:flyway-core` y el módulo Flyway del
motor elegido. Las migraciones se guardan en `src/main/resources/db/migration`
con nombres como `V1__esquema_inicial.sql` y `V2__slice_grupo.sql`; los datos
exclusivos de prueba van en `src/test/resources/db/migration`. El perfil de
producción debe validar el esquema migrado o no modificarlo, nunca crear tablas
automáticamente. El contrato se versiona junto al código, no se sustituye por
capturas de Swagger UI.

## Entregables

- Repositorio Git con el código completo de la API segura.
- Declaración de uso de IA cumplimentada (ver plantilla en `00-recursos-comunes/plantillas/`).
- Migraciones Flyway, contrato OpenAPI y pruebas de conformidad del slice.

## Requisitos técnicos

- Spring Boot 4.0.5+, Java 25, Gradle
- JWT (jjwt 0.12.5), Spring Security, BCrypt
- Tests existentes deben seguir pasando tras añadir seguridad
- CORS configurado para el frontend Angular

### Ejecutar fuera de IntelliJ

Desde la raíz del proyecto `GestionEventos`, usa siempre el wrapper incluido:

```bash
bash gradlew test
bash gradlew bootRun --args='--spring.profiles.active=dev'
```

`bash gradlew` no depende de que el fichero tenga permiso ejecutable. El wrapper
se actualizó a Gradle 9.1 porque Gradle 8.14 fallaba al arrancar con Java 25
(`Unsupported class file major version 69`). No instales ni dependas de una
versión global de Gradle.

## Política de IA

| Aspecto | |
|---------|-|
| Uso de IA permitido | Sí, para comprender el flujo JWT o depurar errores de seguridad |
| Declaración obligatoria | Sí |
| Herramientas permitidas | ChatGPT, Claude, Gemini, GitHub Copilot |
| Qué NO está permitido | Copiar configuraciones de seguridad sin comprender el flujo de autenticación |

## Evaluación

Ver `rubrica.md` y `ra-ce.md` en este directorio.

## Relación con Battleship

Este ejercicio culmina las sesiones 7, 8 y 10 de Battleship: seguridad JWT,
perfiles, persistencia preparada para producción y contrato OpenAPI. Consulta
la [sesión de seguridad](../../02-ejemplos/battleship/docs/07-seguridad-jwt.md),
la de [producción](../../02-ejemplos/battleship/docs/08-produccion-perfiles.md)
y la [trazabilidad SDD](../../02-ejemplos/battleship/docs/10-sdd-openapi.md).
Battleship muestra la referencia completa; aquí se reproducen esos conceptos
en la API de eventos, sin entregar Battleship.
