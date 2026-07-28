# docs/08_INTEGRATION_TESTS_GUIDE.md

## Guia para alumnado - Tests de integracion

### Objetivo
Entender y ejecutar tests de integracion en la API para validar seguridad y comportamiento real por HTTP.

En esta guia:
- ejecutas los tests existentes,
- interpretas los resultados,
- y sabes como ampliar un test sin romper el proyecto.

---

## 1) Que es un test de integracion

Un test de integracion prueba varias capas juntas:
- Controller
- Service
- Repository
- Base de datos (SQLite de prueba)
- Middleware y autorizacion

Comparacion rapida con Spring Boot:
- En Spring se parece a `@SpringBootTest` + `MockMvc`/`TestRestTemplate`.
- Aqui usamos `WebApplicationFactory<Program>` y `HttpClient`.

---

## 2) Donde estan en este proyecto

Ruta:
- `TodoApi.Tests/Integration/TaskAuthorizationIntegrationTests.cs`

Estos tests validan principalmente:
- `401` sin autenticacion.
- permisos por rol (`User`, `Supervisor`, `Admin`).
- reglas de propiedad sobre tareas.

---

## 3) Como ejecutarlos

Desde `todo-dotnet/src`:

```bash
dotnet test TodoApi.Tests/TodoApi.Tests.csproj --filter "FullyQualifiedName~Integration"
```

Para ejecutar todos los tests:

```bash
dotnet test TodoApi.Tests/TodoApi.Tests.csproj
```

---

## 4) Como leer el resultado

Si todo va bien:
- veras tests en verde.
- no habra errores de compilacion.

Si falla un test de integracion:
- revisa primero el codigo HTTP esperado (`401`, `403`, `204`, etc.),
- revisa despues si la regla de negocio ha cambiado,
- y por ultimo revisa si los datos de prueba del test son coherentes.

---

## 5) Estructura basica del test actual

Piezas clave que debes reconocer:
- `TestWebAppFactory`:
- arranca una version de la API para pruebas.
- reemplaza autenticacion real por una de test.
- `TestAuthHandler`:
- genera un usuario autenticado leyendo cabeceras de prueba.
- `IntegrationContext`:
- prepara cliente HTTP + base de datos temporal.

Esto permite probar endpoints reales sin depender del login real en cada test.

---

## 6) Flujo recomendado al crear un nuevo test

1. Define escenario en una frase.
2. Prepara datos de prueba minimos.
3. Lanza request HTTP real con `HttpClient`.
4. Verifica status code y contenido importante.
5. Ejecuta solo integracion.
6. Ejecuta toda la suite.

Plantilla mental:
- Given: estado inicial.
- When: llamada HTTP.
- Then: codigo y comportamiento esperado.

---

## 7) Errores tipicos y solucion

- Falla por `401` inesperado:
- faltan cabeceras de autenticacion de test.
- Falla por `403` inesperado:
- el rol o `userId` usado no coincide con el escenario.
- Falla por datos:
- la tarea de prueba no se creo o no pertenece al usuario esperado.

---

## 8) Criterio de calidad minimo

Antes de dar por buena una entrega:
- tests de integracion en verde,
- tests unitarios en verde,
- y regla funcional claramente cubierta por al menos un test.

