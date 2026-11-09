# Pruebas REST y Tests automáticos

Este documento contiene:
- instrucciones y ejemplos de peticiones para Insomnia (colección incluida en el repo),
- ejemplos curl para usar en terminal/Insomnia,
- scripts de comprobación (Postman/Insomnia) simples,
- dos tests Java listos para ejecutar y un test esqueleto con comentarios para que los alumnos lo completen.

Variables de entorno recomendadas para Insomnia / Postman
- baseUrl = http://localhost:8080
- apiPrefix = /api/v1

Cabecera común
- Content-Type: application/json
- Accept: application/json

---------------------------

ENDPOINTS (resumen)
- Evento: {{baseUrl}}{{apiPrefix}}/eventos
- Participante: {{baseUrl}}{{apiPrefix}}/participantes
- Organizador: {{baseUrl}}{{apiPrefix}}/organizadores

---------------------------

Ejemplos curl (crear recursos)

Crear Evento

```bash
curl -X POST "{{baseUrl}}{{apiPrefix}}/eventos" \
  -H "Content-Type: application/json" \
  -d '{"nombre":"Jornada Java","descripcion":"Conferencia sobre Java","tipo":"CONGRESO"}'
```

Crear Organizador

```bash
curl -X POST "{{baseUrl}}{{apiPrefix}}/organizadores" \
  -H "Content-Type: application/json" \
  -d '{"nombre":"Uni Eventos"}'
```

Crear Participante (registro)

```bash
curl -X POST "{{baseUrl}}{{apiPrefix}}/participantes" \
  -H "Content-Type: application/json" \
  -d '{"nombre":"Ana Pérez","email":"ana@example.com","usuario":"anap","contrasenia":"secreto123","evento":{"id":1}}'
```

---------------------------

Tests simples para Insomnia / Postman (copiar en la pestaña Tests)

Ver status 200 y que la respuesta es un array (GET lista):

```javascript
pm.test("Status is 200", function () {
    pm.response.to.have.status(200);
});
pm.test("Body is an array", function () {
    var json = pm.response.json();
    pm.expect(json).to.be.an("array");
});
```

Tras crear, comprobar 201 y que devuelve id:

```javascript
pm.test("Created", function () {
    pm.response.to.have.status(201);
    var json = pm.response.json();
    pm.expect(json).to.have.property("id");
});
```

Comprobar que la contraseña no aparece en la respuesta (si se oculta):

```javascript
pm.test("No password exposed", function () {
    var json = pm.response.json();
    pm.expect(json).to.not.have.property("contrasenia");
});
```

---------------------------

IMPORTAR LA COLECCIÓN INSOMNIA
- Fichero: `Insomnia_GestionEventos.json` en esta carpeta (`Documentacion/04-cliente-rest/`).
- En Insomnia: File -> Import -> From File -> seleccionar `Insomnia_GestionEventos.json`.
- Crear environment con variables `baseUrl` y `apiPrefix` según indicación.

---------------------------

TESTS AUTOMATIZADOS JAVA (resumen)
- Dos tests listos incluidos en `src/test/java`:
    - `EventoControladorTest` (WebMvcTest, MockMvc)
    - `EventoRepoTest` (DataJpaTest)
- Un archivo `ParticipanteServiceTest` con instrucciones/TODOs para que los alumnos lo completen.

Cómo ejecutar los tests (desde la raíz del proyecto):
- ./gradlew test

---------------------------

NOTAS PARA EL PROFESOR
- No se han modificado entidades ni controladores: los tests asumen que implementaréis los endpoints y repos necesarios durante la clase.
- He dejado el test de participante como esqueleto con comentarios para que los alumnos lo completen como parte de la práctica.

## Nota sobre `@MockitoBean` en Spring Boot 4

En los tests de controlador (`EventoControladorTest`, `OrganizadorControladorTest`, `ParticipanteControladorTest`)
se debe usar en Spring Boot 4:

```java
import org.springframework.test.context.bean.override.mockito.MockitoBean;
```

La anotación vigente es `@MockitoBean`:

```java
@MockitoBean
private EventoServicio eventoServicio;
```

En código histórico de Spring Boot 3 puede aparecer `@MockBean`; no debe usarse en código
nuevo de Spring Boot 4. En los tests de slice (`@WebMvcTest`, etc.), `@MockitoBean`
registra el mock como bean en el contexto de pruebas.


---

Archivo generado automáticamente: PruebasREST_y_Test.md
