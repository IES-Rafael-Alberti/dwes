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

## Entregables

- Repositorio Git con el código completo de la API segura.
- Declaración de uso de IA cumplimentada (ver plantilla en `00-recursos-comunes/plantillas/`).

## Requisitos técnicos

- Spring Boot 4.0.5+, Java 25, Gradle
- JWT (jjwt 0.12.5), Spring Security, BCrypt
- Tests existentes deben seguir pasando tras añadir seguridad
- CORS configurado para el frontend Angular

## Política de IA

| Aspecto | |
|---------|-|
| Uso de IA permitido | Sí, para comprender el flujo JWT o depurar errores de seguridad |
| Declaración obligatoria | Sí |
| Herramientas permitidas | ChatGPT, Claude, Gemini, GitHub Copilot |
| Qué NO está permitido | Copiar configuraciones de seguridad sin comprender el flujo de autenticación |

## Evaluación

Ver `rubrica.md` y `ra-ce.md` en este directorio.
