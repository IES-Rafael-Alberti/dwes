---
title: "Tests para la Mini Task: TDD y MockMvc"
author: "José MSA"
output: pdf_document
---

---

# Capítulo 2 bis — Tests para la Mini Task

**Objetivo:** leer y probar la aplicación real de la Mini Task con TDD,
`MockMvc` y una prueba de integración. El código actual es deliberadamente
pequeño: guarda objetos en memoria y todavía no usa servicio, repositorio ni
JPA.

## Dependencia de tests

El `pom.xml` real ya incluye esta dependencia con alcance exclusivo de tests:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <scope>test</scope>
</dependency>
```

`spring-boot-starter-test` proporciona JUnit Jupiter, AssertJ, Hamcrest y MockMvc,
entre otras utilidades. No hay que añadir una dependencia separada de
`junit-jupiter`: el starter ya gestiona las dependencias de testing necesarias
para este proyecto. Después de comprobar o modificar el `pom.xml`, recarga el
proyecto Maven en IntelliJ (ventana **Maven** → **Reload All Maven Projects**)
para que el IDE reconozca las clases y anotaciones de test.

En Spring Boot 4, `@WebMvcTest` se importa desde
`org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest`; el paquete
antiguo de Spring Boot 3 no funciona en este proyecto.

El stack de pruebas es `spring-boot-starter-test` (JUnit Jupiter, AssertJ, Hamcrest,
MockMvc y Jackson test). En un proyecto con Maven Wrapper se ejecuta con
`./mvnw test`. El recurso antiguo
`03-ejercicios/01-mini-tasks/recursos/mini-spring-boot-tasks-git-lesson/project/`
no incluye wrapper: en esa carpeta se usa `mvn test`, con Maven instalado.

### Mockito como agente de la JVM: cambio motivado por un aviso

Al ejecutar los tests de **`02-ejemplos/bshipAlong`**, con Java 25, apareció este
aviso. El cambio descrito aquí se aplicó a ese proyecto; no significa que los
recursos antiguos de Mini Tasks ya tengan modificado su POM:

> Mockito is currently self-attaching to enable the inline-mock-maker. This will
> no longer work in future releases of the JDK.

Iba seguido de otro del JDK, indicando que se había cargado dinámicamente el
agente de Byte Buddy. **Qué significa:** desde Mockito 5 el *mock maker* por
defecto es el inline, que se auto-adjunta como agente para poder simular
métodos y clases finales. Desde JDK 21 se avisa de esta carga dinámica; JEP 451
prepara su desactivación **por defecto** en una versión futura. El aviso no rompe
estos tests, pero motiva eliminar la dependencia de la auto-adjunción.

**Solución localizada:** la [documentación de Mockito, §0.3](https://javadoc.io/doc/org.mockito/mockito-core/latest/org.mockito/org/mockito/Mockito.html#0.3)
recomienda declararlo explícitamente como agente de la JVM en lugar de dejar
que se auto-adjunte. En la búsqueda se probó por error `mockito-subclass` como
contenido del archivo `mockito-extensions/org.mockito.plugins.MockMaker`: es el
nombre de un artefacto, no el identificador que espera ese archivo. El
identificador correcto es `mock-maker-subclass`. Ese modo tiene otras
limitaciones (no simula clases ni métodos finales); aquí conservamos el modo
inline y configuramos su agente.

`-XX:+EnableDynamicAgentLoading` permite explícitamente la carga dinámica y evita
su aviso; no es solo un filtro de mensajes. Se descartó porque seguiría
dependiendo de la auto-adjunción y de una opción específica de la JVM.

**Cambio realizado en `02-ejemplos/bshipAlong/pom.xml`:** dentro de
`<build><plugins>` se añaden `maven-dependency-plugin` con el
goal `properties`, que deja disponible la ruta del jar de Mockito, y ese jar se
pasa a surefire como `-javaagent`:

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-dependency-plugin</artifactId>
    <executions>
        <execution>
            <goals><goal>properties</goal></goals>
        </execution>
    </executions>
</plugin>
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-surefire-plugin</artifactId>
    <configuration>
        <argLine>-javaagent:"${org.mockito:mockito-core:jar}"</argLine>
    </configuration>
</plugin>
```

Con este cambio la copia de comprobación terminó con **21 tests verdes sin el
aviso de auto-adjunción**: el agente se carga al arrancar cada JVM de pruebas.
La ruta se obtiene de las dependencias resueltas, sin fijar una versión de Mockito
ni una ruta particular a `.m2`. Las comillas permiten rutas con espacios.

Esta configuración se aplica a **`./mvnw test`**. Ejecutar un test con el runner
JUnit propio de IntelliJ no implica usar Surefire: para compartirla, ejecuta
el goal Maven `test` desde la ventana Maven del IDE. Si se usa Failsafe para
pruebas de integración, necesita también su configuración de agente.

Si se añade JaCoCo y configura la propiedad `argLine`, hay que conservarla mediante
`@{argLine} -javaagent:...`. No añadas ese marcador sin definir la propiedad:
si puede quedar sin valor, declara `<argLine/>` dentro de `<properties>` del POM.

## 0) El código real de partida

### Modelo y DTO

El modelo real es `com.example.minitasks.model.SimpleTask`. Es una clase con
los campos públicos `Long id`, `String title` y `boolean done`, además de
constructores y getters/setters. El cuerpo del `POST` se deserializa en
`com.example.minitasks.dto.CreateTaskDTO`, un record que solo contiene
`title`:

```java
package com.example.minitasks.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateTaskDTO(@NotBlank String title) {}
```

El `@NotBlank` está declarado en el DTO. V1 no usa `@Valid`, por lo que no
activa esa validación. V2 sí recibe el DTO con `@Valid` y rechaza un `title`
en blanco con `400 Bad Request`.

### V1: lista en memoria y `AtomicLong`

`com.example.minitasks.web.TaskControllerV1` tiene `@RequestMapping("/v1")`,
una `List<SimpleTask> inMemory` y un `AtomicLong seq`. Sus rutas reales son:

| Método y ruta | Resultado actual |
|---|---|
| `GET /v1/ping` | texto `pong`, `200 OK` |
| `GET /v1/tasks` | lista JSON, `200 OK` |
| `POST /v1/tasks` | crea una tarea y devuelve `201 Created` |

El `POST` de V1 lleva `@ResponseStatus(HttpStatus.CREATED)`, pero no devuelve
cabecera `Location` y no existe `GET /v1/tasks/{id}`.

### V2: mismo almacenamiento, respuesta HTTP explícita

`com.example.minitasks.web.TaskControllerV2` usa otra lista independiente y
`@RequestMapping("/v2")`. Tiene `GET /v2/tasks` y `POST /v2/tasks`. Ambos
métodos devuelven `ResponseEntity`; el `POST` responde `201 Created`, incluye
`Location: /v2/tasks/{id}` y devuelve la tarea creada. También ofrece
`GET /v2/tasks/{id}`: devuelve `200 OK` si existe y `404 Not Found` si no
existe. Su `POST` usa `@Valid`, por lo que un título en blanco produce `400`.

El método de lectura declara `@PathVariable("id") Long id`: indicar el nombre
evita depender de que el compilador conserve los nombres de los parámetros.

La lista es local a cada controlador y se reinicia al recrearse el contexto.
Además, V1 y V2 no comparten los datos.

## 1) TDD: especificar lo que existe

TDD sigue el ciclo **rojo–verde–refactor**: escribimos una prueba que falla,
implementamos lo mínimo y refactorizamos sin romper el comportamiento.

Como primer sanity check podemos comprobar que la aplicación
`com.example.minitasks.MiniTasksApplication` carga correctamente:

```java
package com.example.minitasks;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ContextLoadTest {
  @Test void contextLoads() {}
}
```

## 2) Tests web reales con MockMvc

El test real de V1 usa `MockMvcBuilders.standaloneSetup`: crea el controlador
directamente y prueba la capa MVC sin abrir un puerto. No necesita
`@WebMvcTest`, ni inventa `TaskController` ni el paquete `com.example.tasks`:

```java
package com.example.minitasks.web;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class TaskControllerV1Test {
  private MockMvc mvc;

  @BeforeEach
  void setUp() {
    mvc = MockMvcBuilders.standaloneSetup(new TaskControllerV1()).build();
  }

  @Test
  void ping_shouldReturnPong() throws Exception {
    mvc.perform(get("/v1/ping"))
       .andExpect(status().isOk())
       .andExpect(content().string("pong"));
  }

  @Test
  void list_shouldInitiallyBeEmpty() throws Exception {
    mvc.perform(get("/v1/tasks"))
       .andExpect(status().isOk())
       .andExpect(jsonPath("$", hasSize(0)));
  }

  @Test
  void create_shouldReturn201AndTask() throws Exception {
    mvc.perform(post("/v1/tasks")
            .contentType("application/json")
            .content("{\"title\":\"Aprender Spring Boot\"}"))
       .andExpect(status().isCreated())
       .andExpect(jsonPath("$.id").value(1))
       .andExpect(jsonPath("$.title").value("Aprender Spring Boot"))
       .andExpect(jsonPath("$.done").value(false));
  }
}
```

El test real de V2 usa `MockMvcBuilders.standaloneSetup(new
TaskControllerV2()).setValidator(validator)`. Es decir, no depende de
`@WebMvcTest`: crea el controlador directamente y conecta un
`LocalValidatorFactoryBean` para que `@Valid` se ejecute. Sus casos principales
son:

```java
@Test
void create_shouldReturn201AndLocation() throws Exception {
  mvc.perform(post("/v2/tasks")
          .contentType("application/json")
          .content("{\"title\":\"Tarea V2\"}"))
     .andExpect(status().isCreated())
     .andExpect(header().string("Location", "/v2/tasks/1"));
}
```

En la misma clase de test, con el `mvc` standalone configurado y un controlador
nuevo por cada `setUp`, se cubren también la lectura, el 404 y la validación:

```java
@Test
void getsExistingTask() throws Exception {
  mvc.perform(post("/v2/tasks").contentType(MediaType.APPLICATION_JSON)
          .content("{\"title\":\"Read\"}"));

  mvc.perform(get("/v2/tasks/1"))
     .andExpect(status().isOk())
     .andExpect(jsonPath("$.title").value("Read"));
}

@Test
void returnsNotFoundForMissingTask() throws Exception {
  mvc.perform(get("/v2/tasks/99"))
     .andExpect(status().isNotFound());
}

@Test
void rejectsBlankTitle() throws Exception {
  mvc.perform(post("/v2/tasks").contentType(MediaType.APPLICATION_JSON)
          .content("{\"title\":\" \"}"))
     .andExpect(status().isBadRequest());
}
```

## 3) Flujo y estado en memoria

Si queremos comprobar que una tarea creada aparece después en la lista, las
dos peticiones deben estar en el mismo test. El estado no se debe compartir
entre métodos:

```java
@Test
void createThenList_shouldContainTheTask() throws Exception {
  mvc.perform(post("/v1/tasks")
          .contentType("application/json")
          .content("{\"title\":\"Tarea 1\"}"))
     .andExpect(status().isCreated());

  mvc.perform(get("/v1/tasks"))
     .andExpect(status().isOk())
     .andExpect(jsonPath("$", hasSize(1)))
     .andExpect(jsonPath("$[0].title").value("Tarea 1"));
}
```

Los ejemplos anteriores describen regresión del contrato actual: V1 todavía no
ofrece `GET /v1/tasks/{id}`, `404`, `Location` ni `400` por `@NotBlank`.
Cuando se decida añadir cada capacidad, se escribirá primero su test y se
implementará después; no se presentan aquí como funcionalidades ya disponibles.

## 4) Integración con servidor real

`MockMvc` prueba la capa MVC sin red. Para probar el recorrido HTTP completo
se puede arrancar la aplicación en un puerto aleatorio. Este test corresponde
al contrato V2 y usa un DTO local únicamente para deserializar la respuesta:

En Spring Boot 4, este ejemplo requiere además `spring-boot-resttestclient` y
`spring-boot-restclient`, ambas con alcance `test`; sus versiones las gestiona
Spring Boot. `spring-boot-starter-test` por sí solo no aporta todo lo necesario
para `TestRestTemplate`. Se configura expresamente con
`@AutoConfigureTestRestTemplate`:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-resttestclient</artifactId>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-restclient</artifactId>
    <scope>test</scope>
</dependency>
```

```java
package com.example.minitasks;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestRestTemplate
class TaskIntegrationTest {
  @LocalServerPort int port;
  @Autowired TestRestTemplate rest;

  String url(String path) { return "http://localhost:" + port + path; }

  @Test
  void createV2_shouldReturnCreatedAndLocation() {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    HttpEntity<String> request = new HttpEntity<>(
        "{\"title\":\"Integración\"}", headers);

    ResponseEntity<TaskResponse> response = rest.postForEntity(
        url("/v2/tasks"), request, TaskResponse.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    assertThat(response.getHeaders().getLocation()).hasToString("/v2/tasks/1");
    assertThat(response.getBody().title).isEqualTo("Integración");
  }

  static class TaskResponse {
    public Long id;
    public String title;
    public boolean done;
  }
}
```

También se puede completar la integración V2 con el GET por id, usando el id
obtenido de la respuesta de creación:

```java
ResponseEntity<TaskResponse> got = rest.getForEntity(
    url("/v2/tasks/" + response.getBody().id), TaskResponse.class);
assertThat(got.getStatusCode()).isEqualTo(HttpStatus.OK);
assertThat(got.getBody().title).isEqualTo("Integración");
```

Si el proyecto ejecuta V1 y V2 simultáneamente, el resultado de cada
controlador debe probarse con su ruta correspondiente.

## 5) Evolución TDD: siguiente ejercicio opcional

La evolución natural es añadir capacidades de forma incremental: trasladar a
V1 las mejoras que ya tiene V2 (`@Valid`, `400`, búsqueda por id con `404` y
`Location`) y, más adelante, separar controlador, servicio y repositorio JPA.

Esos cambios son **siguientes ejercicios**, no una descripción del Mini Task
actual. Para cada capacidad nueva, escribe primero su expectativa, prepara solo
lo necesario para que compile, observa el rojo de comportamiento e implementa
lo mínimo para obtener verde. No añadas de golpe pruebas de todas las capacidades
futuras. Cuando llegue JPA, se distinguirán los tests de controlador
(`@WebMvcTest`), repositorio (`@DataJpaTest`) e integración
(`@SpringBootTest`); los tests de este capítulo siguen probando listas en
memoria.

## 6) Resumen

1. El código real usa `SimpleTask`, listas en memoria y `AtomicLong`.
2. V1 expone `/v1/ping` y `/v1/tasks`; V2 expone `/v2/tasks`.
3. V2 usa `ResponseEntity` y `Location`; V1 usa `@ResponseStatus`.
4. V1 aún no tiene GET por id ni activa `@Valid`; V2 sí ofrece ambas capacidades.
5. MockMvc y la integración deben probar únicamente ese contrato real.
6. Para V1, la validación y los errores por id son iteraciones posteriores; JPA
   también queda para una fase posterior.

---
