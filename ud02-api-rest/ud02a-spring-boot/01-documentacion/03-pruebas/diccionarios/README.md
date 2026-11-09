# Diccionarios de testing — localizar, leer y utilizar

Estas referencias sirven para consultar lo que aparece en los tests de clase.
No son otra práctica ni una lista de instrucciones que haya que memorizar antes
de programar. Busca el nombre, lee su significado y comprueba qué está haciendo
en el caso concreto.

## ¿Qué herramienta estoy leyendo?

| Si ves… | Pertenece a… | Consulta |
|---|---|---|
| `@Test`, `@BeforeEach`, `@ParameterizedTest` | JUnit Jupiter | [Ejecución, anotaciones y parametrizados](01-junit-jupiter.md) |
| `assertEquals`, `assertThrows`, `assertAll` | Aserciones de JUnit | [Diccionario de aserciones](02-aserciones-junit-assertj-hamcrest.md) |
| `assertThat`, `isEqualTo`, `extracting`, `assertThatThrownBy` | AssertJ, si ese es el import | [Diccionario de aserciones](02-aserciones-junit-assertj-hamcrest.md) |
| `when`, `thenReturn`, `thenThrow`, `doThrow`, `verify` | Mockito | [Dobles, respuestas y llamadas](03-mockito.md) |
| `@Mock`, `@InjectMocks`, `@Captor`, `@Spy` | Mockito | [Anotaciones y extensión](03-mockito.md#2-crear-e-inicializar-dobles) |
| `@MockitoBean` | Spring Test, que integra un mock de Mockito en el contexto | [Mockito frente a Spring](03-mockito.md#3-mock-no-es-mockitobean) |
| `perform`, `andExpect`, `status`, `jsonPath` | Spring Test / MockMvc | [Peticiones y expectativas](04-mockmvc-y-spring-test.md) |
| `hasSize`, `containsString`, `is` en una expectativa | Hamcrest | [Matchers de Hamcrest](02-aserciones-junit-assertj-hamcrest.md#6-hamcrest-en-las-expectativas-mvc) |
| `@WebMvcTest`, `@SpringBootTest`, `@Transactional` | Spring y Spring Boot | [Niveles de prueba](04-mockmvc-y-spring-test.md#1-que-se-ejecuta-en-cada-prueba) y [anexo de anotaciones](../../05-referencias/anexo-anotaciones-spring-boot.md) |

**El import decide qué API estás usando.** Existen varios métodos llamados
`assertThat`, `any` o `isNull` que no son intercambiables.

## Leer un test en tres pasos

```java
when(service.getGame(999L)).thenThrow(new GameNotFoundException(999L));

mvc.perform(get("/api/games/999"))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.error").value("NOT_FOUND"));

verify(service).getGame(999L);
```

1. **Preparación:** el mock queda configurado para lanzar una excepción cuando
   reciba esa llamada. `when` no implementa ni ejecuta el servicio real.
2. **Acción y resultado:** MockMvc tramita la petición y las expectativas comprueban
   la respuesta de la capa web, incluido el advice que esté cargado.
3. **Interacción:** Mockito comprueba que se llamó al colaborador con ese id.

Este caso no demuestra que el repositorio real encuentre o no una partida.
Eso necesita otra prueba al nivel adecuado.

## Atajos según la duda

- «¿Cuándo se ejecuta este método?» → [JUnit y ciclo de vida](01-junit-jupiter.md#2-ciclo-de-vida).
- «¿Por qué `doThrow` y no `when`?» → [Métodos void](03-mockito.md#5-metodos-void-dothrow-donothing-y-doanswer).
- «¿La lista contiene solo esto y en este orden?» → [Colecciones](02-aserciones-junit-assertj-hamcrest.md#3-colecciones-arrays-y-extraccion).
- «¿Esto provoca una excepción o comprueba una excepción?» → [Mockito frente a aserciones](03-mockito.md#1-tres-operaciones-distintas).
- «¿Hay una BD o un servicio real en este test?» → [Niveles de prueba](04-mockmvc-y-spring-test.md#1-que-se-ejecuta-en-cada-prueba).
- «¿Qué genera Lombok?» → [Anotaciones de Lombok](../../05-referencias/anexo-lombok.md).
- «¿Quién interpreta esta anotación?» → [Spring, Boot, JPA y validación](../../05-referencias/anexo-anotaciones-spring-boot.md).

## Versiones y ejemplos

Los ejemplos se comprueban con las dependencias gestionadas por el POM de
`bshipAlong` —Spring Boot 4.1.1 y Java 25—. No se fijan versiones independientes
de JUnit, Mockito o AssertJ para copiar una referencia. Jupiter mantiene sus
paquetes `org.junit.jupiter` en las generaciones actuales de JUnit.

Las clases completas incluidas al final de los diccionarios son ejemplos de
consulta verificables, no funcionalidades que haya que añadir a la aplicación.
Los fragmentos cortos suponen las variables y tipos indicados en su contexto.

Para el recorrido inicial: [primeros pasos con TDD](../02-tdd-primeros-pasos.md).
Para el modelo de datos: [bloque A de persistencia](../../04-persistencia/bloque-a/01-entidades-relaciones-y-ciclo-de-vida.md).
