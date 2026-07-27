# Battleship API: recorrido incremental con TDD

Este caso práctico conecta la progresión de UD2a con una API real. La meta no es copiar el proyecto terminado, sino construir una decisión observable cada vez: escribimos una prueba pequeña, vemos por qué falla, implementamos lo mínimo y volvemos a comprobar.

El proyecto de referencia está en [`02-ejemplos/battleship/`](../02-ejemplos/battleship/docs/README.md) y usa **Spring Boot 4.0.5, Java 21, Maven, JPA, Flyway, H2 y Spring Security**. El `pom.xml` es la fuente de verdad de las versiones.

## Antes de empezar

Necesitas Java 21 o posterior. Comprueba el punto de partida:

```bash
cd ud02-api-rest/ud02a-spring-boot/02-ejemplos/battleship
./mvnw test
```

El proyecto del repositorio es una referencia final. Para practicar de verdad, reproduce las etapas en un proyecto nuevo o restaura tu propio commit al comenzar cada etapa.

## Mapa del recorrido

| Etapa | Pregunta que resolvemos | Evidencia principal |
|---|---|---|
| 1. Problema y contrato | ¿Qué comportamiento mínimo tendrá la API? | ejemplos HTTP y criterios acordados |
| 2. Primer rojo | ¿Puede crearse una partida pendiente? | test que falla por la razón esperada |
| 3. Dominio y DTO | ¿Qué pertenece al dominio y qué cruza HTTP? | `Game` y DTOs separados |
| 4. Servicio | ¿Dónde viven las reglas del juego? | tests del servicio |
| 5. Controlador | ¿Cómo traducimos el caso de uso a HTTP? | test MVC con estados y `Location` |
| 6. Persistencia | ¿Quién controla el esquema? | migración Flyway y test JPA |
| 7. Validación y errores | ¿Cómo falla la API de forma predecible? | respuestas 400/404 uniformes |
| 8. Seguridad y configuración | ¿Qué se puede leer o modificar? | tests de integración 401/403 y RBAC |
| 9. Flujo completo | ¿Funciona el recorrido fuera de una capa aislada? | test de integración y demo HTTP |

Cada etapa termina en un **checkpoint**. No avances si todavía no puedes explicar qué prueba protege el comportamiento anterior.

## 1. Del problema a un contrato mínimo

**Objetivo.** Definir una primera rebanada vertical: crear una partida y consultarla.

**Concepto.** Un contrato HTTP describe entradas, salidas y estados sin decidir aún clases o tablas.

**Cambio mínimo.** Escribe dos ejemplos antes de programar:

```http
POST /api/games
Content-Type: application/json

{"boardSize": 10}
```

Resultado acordado: `201 Created`, cabecera `Location: /api/games/{id}` y una respuesta con estado `PENDING`. La consulta a esa ubicación devolverá `200 OK`.

**Checkpoint.** Otra persona puede decir qué ocurre con un tablero de tamaño `0` sin leer el código: el contrato exige un error de validación `400`.

**Criterio observable.** El comportamiento cabe en pocos ejemplos HTTP y no menciona repositorios ni entidades.

## 2. Primer test rojo

**Objetivo.** Convertir el primer ejemplo en una prueba ejecutable.

**Concepto.** En TDD, rojo significa que la prueba falla por una capacidad que aún no existe; un error de compilación accidental o una configuración rota no demuestra el comportamiento.

**Cambio mínimo.** Empieza por `GameServiceTest`: expresa que al crear un tablero válido se obtiene un identificador, el tamaño solicitado y estado `PENDING`. Todavía no añadas barcos, ataques ni seguridad.

```java
@Test
void createGame_createsWithPendingStatus() {
    GameResponseDTO game = gameService.createGame(new CreateGameDTO(8));

    assertEquals(8, game.boardSize());
    assertEquals("PENDING", game.status());
    assertNotNull(game.id());
}
```

**Checkpoint.** Ejecuta solo la prueba y registra por qué falla:

```bash
./mvnw -Dtest=GameServiceTest#createGame_createsWithPendingStatus test
```

**Criterio observable.** Has visto un rojo relacionado con `createGame`, no con dependencias o sintaxis. Después implementas lo mínimo hasta verlo verde y refactorizas sin cambiar el contrato.

## 3. Separar dominio y DTO

**Objetivo.** Modelar una partida sin acoplar la tabla o la entidad al JSON público.

**Concepto.** `Game` representa estado persistente; `CreateGameDTO` valida la entrada; `GameResponseDTO` define la salida. Sus responsabilidades NO son intercambiables.

**Cambio mínimo.** Introduce solo:

- `Game` con `id`, `boardSize`, `status` y `createdAt`;
- `GameStatus` con `PENDING`, `IN_PROGRESS`, `WON` y `CANCELLED`;
- `CreateGameDTO` con el rango de tablero `5..20`;
- `GameResponseDTO` como proyección de salida.

**Checkpoint.** La prueba de creación queda verde y ninguna respuesta HTTP expone directamente una entidad JPA.

**Criterio observable.** Cambiar un nombre interno de la entidad no obliga a cambiar el contrato JSON.

## 4. Llevar las reglas al servicio

**Objetivo.** Hacer que el juego sea algo más que un CRUD.

**Concepto.** El controlador coordina HTTP; el servicio conserva las reglas: límites, solapamiento, disparos repetidos, hundimiento y transición de estados.

**Cambio mínimo.** Añade una regla por ciclo rojo-verde-refactor:

1. colocar el primer barco pasa a `IN_PROGRESS`;
2. un barco no puede salir del tablero;
3. dos barcos no pueden solaparse;
4. una coordenada no puede atacarse dos veces;
5. al alcanzar todas las posiciones, el barco se hunde y la última flota produce `WON`.

No implementes las cinco antes de ejecutar tests.

**Checkpoint.** Ejecuta `GameServiceTest` después de cada regla:

```bash
./mvnw -Dtest=GameServiceTest test
```

**Criterio observable.** Las reglas se prueban sin construir peticiones HTTP y el servicio devuelve DTOs, no respuestas web.

Amplía esta etapa con la guía de [servicios y DTOs](../02-ejemplos/battleship/docs/04-capa-servicios-dtos.md) y las [reglas de negocio](../02-ejemplos/battleship/docs/04b-reglas-de-negocio.md).

## 5. Exponer la API desde el controlador

**Objetivo.** Traducir los casos de uso a semántica HTTP.

**Concepto.** `@RestController`, `@RequestBody`, `@PathVariable` y `ResponseEntity` describen el borde web; no deben contener geometría del tablero.

**Cambio mínimo.** Implementa primero `POST /api/games` y `GET /api/games/{id}`. Comprueba `201 + Location` al crear y `200` al consultar. Después añade colocación, ataque, listado y cancelación.

**Checkpoint.** `GameControllerTest` usa un servicio simulado para observar solo el contrato MVC:

```bash
./mvnw -Dtest=GameControllerTest test
```

**Criterio observable.** Un cambio en el repositorio no rompe el test del controlador. Un cambio de estado HTTP sí lo rompe.

Consulta [controladores REST](03-controladores-rest.md) y la [práctica del controlador](../02-ejemplos/battleship/docs/03-controladores-rest.md).

## 6. Persistir con JPA y Flyway

**Objetivo.** Mantener partidas, barcos y ataques con un esquema reproducible.

**Concepto.** Flyway versiona el esquema; Hibernate lo valida. Con `ddl-auto: validate`, arrancar no debe modificar silenciosamente la base de datos.

**Cambio mínimo.** Crea `V1__create_game_tables.sql`, mapea las tres entidades y añade repositorios. Los cambios posteriores son nuevas migraciones (`V2`, `V3`…), nunca reescrituras de una migración ya compartida.

**Checkpoint.** El test JPA demuestra persistencia y filtrado de partidas activas:

```bash
./mvnw -Dtest=GameRepositoryTest test
```

**Criterio observable.** Una base vacía se crea mediante Flyway y el contexto falla si entidad y esquema no coinciden.

## 7. Validar y responder errores coherentes

**Objetivo.** Convertir fallos esperables en respuestas útiles y estables.

**Concepto.** Bean Validation protege la forma de entrada; el servicio protege reglas dependientes del estado; `@RestControllerAdvice` traduce excepciones a HTTP.

**Cambio mínimo.** Añade `@Valid`, restricciones en DTOs y un `ErrorPayload` común. Distingue como mínimo:

- `400 VALIDATION_ERROR` para datos estructuralmente inválidos;
- `400 BAD_REQUEST` para una regla de negocio incumplida;
- `404 NOT_FOUND` para una partida inexistente.

**Checkpoint.** Envía `{"boardSize":0}` y consulta un identificador inexistente en `GameControllerTest`.

**Criterio observable.** Ambos errores tienen `error`, `message` y `timestamp`; ningún stack trace forma parte del contrato.

Amplía con [manejo de errores](05-tdd-manejo-errores.md) y [test slicing](06-tdd-slicing.md).

## 8. Añadir seguridad y configuración

**Objetivo.** Separar lectura pública de mutaciones autorizadas y retirar secretos del código.

**Concepto.** La API es *stateless*: JWT autentica cada petición. Desactivar CSRF es coherente aquí porque no se autentica mediante cookies de sesión; no es una receta para aplicaciones MVC con formularios.

**Cambio mínimo.** Protege las mutaciones de jugador, reserva la cancelación para administración y permite las consultas públicas. Configura emisor, audiencia, caducidad y rutas de claves mediante propiedades/perfiles.

El proyecto de referencia también incluye CORS y limitación de peticiones. La lista de orígenes debe adaptarse al entorno real; `https://tufrontend.com` es deliberadamente un marcador didáctico.

La matriz mínima comprobada es:

| Operación | Acceso |
|---|---|
| `POST /auth/**` | público |
| `GET /api/games` y `GET /api/games/{id}` | público |
| crear partida, colocar barco y atacar | `PLAYER` |
| cancelar partida | `ADMIN` |
| cualquier otra ruta | usuario autenticado |

**Checkpoint.** `SecurityAuthorizationIntegrationTest` arranca el contexto completo con los filtros activos. Demuestra lectura pública, `401` sin autenticación, `403` con rol sintético insuficiente y acceso real con bearer tokens firmados por `JwtService` para `PLAYER` y `ADMIN`. También comprueba login incorrecto, refresh inválido o expirado y que un refresh token no sirve como access token. `JwtAuthFilterTest` focaliza claims de roles ausentes o inválidos.

Swagger/OpenAPI y Actuator requieren autenticación en la configuración base. CORS cubre API y autenticación con una lista de orígenes configurable; `http://localhost:5173` es solo el valor local predeterminado.

**Criterio observable.** Poder hacer `GET` no concede permiso para `POST` o `DELETE`, y ninguna clave privada se documenta como secreto reutilizable.

La implementación completa se estudia después del núcleo TDD en [seguridad JWT](../02-ejemplos/battleship/docs/07-seguridad-jwt.md).

## 9. Verificar el flujo completo

**Objetivo.** Demostrar que web, servicio, persistencia y configuración colaboran.

**Concepto.** Los tests aislados localizan fallos; el test de integración protege el ensamblaje. Ninguno sustituye al otro.

**Cambio mínimo.** Crea una partida, sigue `Location`, coloca un barco, dispara y consulta el resultado. Mantén el escenario corto: no repitas aquí todos los casos del servicio.

**Checkpoint.** Ejecuta la suite completa y después una demo manual:

```bash
./mvnw test
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
# En otra terminal:
./demo.sh
```

También puedes importar `insomnia-battleship.json`. El script y la colección son clientes de demostración; los tests automatizados siguen siendo la evidencia repetible.

**Criterio observable.** El flujo devuelve los estados y cuerpos acordados desde una aplicación arrancada, no solo desde mocks.

## Qué viene después: de TDD a contratos y SDD

TDD guía el diseño desde ejemplos ejecutables. Cuando varios equipos o clientes dependen de la API, conviene añadir una fuente contractual compartida: una especificación OpenAPI revisada, pruebas de contrato y un flujo SDD que conecte requisito, diseño, tareas y verificación.

El proyecto usa el contrato estático y versionado
[`battleship-v1.yaml`](../02-ejemplos/battleship/src/main/resources/static/api-docs/battleship-v1.yaml)
como fuente de verdad de sus nueve operaciones. Swagger UI es solo su visor y
Springdoc generado está deshabilitado para evitar una segunda especificación.
La traza SDD versionada enlaza requisitos, diseño, tareas y pruebas de
conformidad; los ejemplos OpenAPI representativos completos siguen siendo una
tarea pendiente separada.

## Lista de salida

- [ ] Puedo explicar el primer rojo y qué implementación mínima lo volvió verde.
- [ ] Distingo entidad, DTO, servicio, controlador y repositorio.
- [ ] Las reglas del tablero están probadas en el servicio.
- [ ] Los estados HTTP y el payload de error están probados en el borde web.
- [ ] Flyway controla el esquema y Hibernate solo lo valida.
- [ ] Puedo ejecutar los tests de autorización y explicar qué lecturas son públicas y qué mutaciones requieren rol.
- [ ] Puedo ejecutar la suite y demostrar un flujo completo.

## Material de profundización

La secuencia detallada por sesiones vive en el [índice del proyecto Battleship](../02-ejemplos/battleship/docs/README.md). Es material de ampliación del recorrido, no una segunda guía de inicio.
