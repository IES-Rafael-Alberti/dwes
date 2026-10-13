# Introducción a JUnit 5: pruebas en Spring Boot

Desarrollo Web en Entorno Servidor (DWES) · Mini guía práctica

## 1. ¿Qué es JUnit?

JUnit es una herramienta para realizar pruebas automáticas en aplicaciones Java. Permite comprobar que nuestro código funciona correctamente sin tener que ejecutar manualmente todas las operaciones.

En Spring Boot, JUnit 5 se utiliza para verificar el comportamiento de servicios, controladores y otros componentes.

Por ejemplo, si tenemos un servidor que responde al acceder a `/hello`, podemos crear una prueba que compruebe que devuelve el mensaje esperado.

## 2. Nuestro servidor «Hello Server»

Partimos de una aplicación Spring Boot con un controlador REST:

```
@RestController
public class HelloController {

    @GetMapping("/hello")
    public String hello() {
        return "Hello World!";
    }
}
```

Al iniciar el servidor y acceder a `http://localhost:8080/hello`, obtenemos:

```
Hello World!
```

Queremos comprobar automáticamente que este endpoint funciona correctamente.

## 3. Dependencias necesarias

Si hemos creado el proyecto desde Spring Initializr incluyendo Spring Web y Spring Boot Starter Test, ya tendremos las dependencias necesarias.

En Maven, debemos disponer de la dependencia de pruebas correspondiente a nuestra versión de Spring Boot. Por ejemplo, en Spring Boot 3:

```
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <scope>test</scope>
</dependency>
```

Esta dependencia incluye JUnit 5 y herramientas de pruebas de Spring.

## 4. Nuestro primer test

Creamos la clase `HelloControllerTest.java` dentro de `src/test/java`, utilizando el paquete correspondiente al controlador.

```
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class HelloControllerTest {

    @Test
    void helloDevuelveMensajeCorrecto() {

        HelloController controller =
                new HelloController();

        String resultado = controller.hello();

        assertEquals("Hello World!", resultado);
    }
}
```

¿Qué hace el test?

1. `@Test` indica que el método es una prueba.
2. Creamos una instancia del controlador.
3. Ejecutamos el método `hello()`.
4. `assertEquals()` comprueba que el resultado coincide con el esperado.

Si ambos valores coinciden, el test pasa. Si son diferentes, falla.

Importante: este primer ejemplo es una prueba unitaria del método Java. Todavía no comprueba una petición HTTP real.

## 5. Ejecutar las pruebas

Desde la terminal, en la raíz del proyecto Maven:

```
./mvnw test
```

En Windows:

```
.\mvnw.cmd test
```

También podemos ejecutar las pruebas desde IntelliJ IDEA o VS Code.

Cuando una prueba falla, JUnit informa del resultado esperado y del obtenido.

## 6. Tarea práctica: añadir un test a «Hello Server»

Objetivo: incorporar una prueba automatizada a un servidor REST desarrollado con Spring Boot.

### Requisitos

1. Partir del proyecto `Hello Server` desarrollado en clase.
2. Comprobar que el endpoint `/hello` funciona correctamente.
3. Crear una clase `HelloControllerTest` en `src/test/java`.
4. Implementar al menos un test con `@Test` y `assertEquals()`.
5. Ejecutar las pruebas con Maven y verificar que pasan.
6. Modificar temporalmente el valor esperado para provocar un fallo, observar el resultado y restaurar el test correcto.

### Ampliación opcional: comprobar HTTP

La prueba anterior llama directamente al método del controlador. Como ampliación, se propone utilizar MockMvc para simular una petición HTTP `GET /hello` y comprobar que:

- El servidor responde con el código HTTP `200 OK`.
- El cuerpo de la respuesta contiene `Hello World!`.

MockMvc permite probar el comportamiento web de Spring MVC sin arrancar un servidor HTTP real.

--------
## JUnit: pruebas unitarias en Java

JUnit es un framework de Java que permite automatizar pruebas para comprobar que los métodos y las clases funcionan correctamente.

Se utiliza especialmente en proyectos con Java, Maven, Gradle y Spring Boot.

### 1. Ejemplo básico con JUnit 5

Supongamos que tenemos una clase `Calculadora`:

```
public class Calculadora {
    public int sumar(int a, int b) {
        return a + b;
    }
}
```

Podemos crear una prueba unitaria:

```
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CalculadoraTest {

    @Test
    void testSumar() {
        Calculadora calc = new Calculadora();

        int resultado = calc.sumar(3, 5);

        assertEquals(8, resultado);
    }
}
```

### 2. Anotaciones principales

| Anotación            | Función                                       |
| -------------------- | --------------------------------------------- |
| `@Test`              | Define un método de prueba                    |
| `@BeforeEach`        | Se ejecuta antes de cada prueba               |
| `@AfterEach`         | Se ejecuta después de cada prueba             |
| `@BeforeAll`         | Se ejecuta una vez antes de todas las pruebas |
| `@AfterAll`          | Se ejecuta una vez al finalizar               |
| `@ParameterizedTest` | Permite probar varios conjuntos de datos      |

### 3. Aserciones habituales

| Método              | Comprueba                    |
| ------------------- | ---------------------------- |
| `assertEquals()`    | Igualdad                     |
| `assertNotEquals()` | Desigualdad                  |
| `assertTrue()`      | Condición verdadera          |
| `assertFalse()`     | Condición falsa              |
| `assertNull()`      | Valor nulo                   |
| `assertNotNull()`   | Valor no nulo                |
| `assertThrows()`    | Lanzamiento de una excepción |

### 4. ¿Qué quieres trabajar con JUnit?

JUnit 5 desde cero, con ejemplos y ejercicios

JUnit en Spring Boot: servicios, controladores y repositorios

JUnit y Mockito: simulación de dependencias

Tema completo para impartir en Desarrollo Web en Entorno Servidor (DWES)

Práctica evaluable con soluciones y rúbrica

Continuar
