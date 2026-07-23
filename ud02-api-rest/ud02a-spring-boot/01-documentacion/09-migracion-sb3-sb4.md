# Migración de Spring Boot 3 a Spring Boot 4

Los proyectos Spring Boot 3 seguirán presentes durante años. Esta guía resume cambios que afectan al material del módulo; no sustituye las notas oficiales de cada versión.

## Baseline

Spring Boot 4 requiere Java 17 como mínimo. DWES adopta **Java 25 LTS** como baseline curricular, por lo que todos los proyectos de la ruta se compilan y verifican con Java 25.

## Starters web y módulos de prueba

Spring Boot 4 modulariza el soporte MVC y sus tests:

| Spring Boot 3 | Spring Boot 4 |
|---|---|
| `spring-boot-starter-web` | `spring-boot-starter-webmvc` |
| slices incluidos por el starter de test clásico | módulos de test explícitos según la tecnología |

```xml
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-webmvc</artifactId>
</dependency>
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-test</artifactId>
  <scope>test</scope>
</dependency>
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-webmvc-test</artifactId>
  <scope>test</scope>
</dependency>
```

Para slices JPA añade `spring-boot-data-jpa-test` con alcance `test`.

## Imports de slices

### Web MVC

```java
// Spring Boot 3
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

// Spring Boot 4
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
```

### Repositorios JPA

`@DataJpaTest` **no ha desaparecido**. Se trasladó al módulo y paquete de prueba JPA:

```java
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

@DataJpaTest
class GameRepositoryTest {
}
```

No sustituyas automáticamente un slice por `@SpringBootTest`: cargar todo el contexto cambia el aislamiento, el tiempo de ejecución y la semántica transaccional de la prueba.

### Beans Mockito

```java
// Spring Boot 3
import org.springframework.boot.test.mock.mockito.MockBean;

// Spring Boot 4 / Spring Framework 7
import org.springframework.test.context.bean.override.mockito.MockitoBean;
```

## Nombres de parámetros

Spring MVC puede resolver nombres implícitos de parámetros cuando el compilador conserva metadatos con `-parameters`. El parent de Spring Boot configura esta opción; si el proyecto importa solo el BOM o personaliza el compilador, verifícala explícitamente:

```xml
<plugin>
  <groupId>org.apache.maven.plugins</groupId>
  <artifactId>maven-compiler-plugin</artifactId>
  <configuration>
    <parameters>true</parameters>
  </configuration>
</plugin>
```

También puedes declarar `name` explícitamente en `@RequestParam` y `@PathVariable` cuando forme parte del contrato.

## JPA e Hibernate 7

Spring Boot mantiene por defecto una estrategia física que convierte camelCase a nombres con guiones bajos. Si seleccionas la estrategia estándar de Hibernate, los identificadores se mantienen sin esa conversión:

```yaml
spring:
  jpa:
    hibernate:
      naming:
        physical-strategy: org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl
```

Para esquemas duraderos, migraciones Flyway o nombres que formen parte de un contrato, usa `@Column(name = "...")` de forma explícita en lugar de depender de una estrategia implícita.

## Flyway

Spring Boot 4 separa Flyway en un módulo propio de auto-configuración. Declara el starter y el módulo de base de datos necesario:

```xml
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-flyway</artifactId>
</dependency>
```

La presencia del starter web nunca sustituye la dependencia de Flyway. Verifica las migraciones desde una base vacía.

## Jackson 3

Spring Boot 4 utiliza Jackson 3. La serialización habitual de DTOs requiere pocos cambios, pero módulos personalizados, tipos avanzados e imports de excepciones deben revisarse contra la versión administrada por Spring Boot.

## Checklist

- [ ] Fijar Spring Boot y Java 25 en el build.
- [ ] Cambiar a `spring-boot-starter-webmvc`.
- [ ] Añadir los módulos de test MVC/JPA utilizados.
- [ ] Actualizar imports de `@WebMvcTest` y `@DataJpaTest`.
- [ ] Sustituir `@MockBean` por `@MockitoBean`.
- [ ] Conservar slices; usar `@SpringBootTest` solo cuando se necesite el contexto completo.
- [ ] Verificar `-parameters` o declarar nombres explícitos.
- [ ] Añadir el starter Flyway y probar una base vacía.
- [ ] Revisar naming JPA e imports personalizados de Jackson.
- [ ] Ejecutar toda la suite con Java 25.

## Referencias

- [Spring Boot system requirements](https://docs.spring.io/spring-boot/system-requirements.html)
- [Spring Boot testing](https://docs.spring.io/spring-boot/reference/testing/index.html)
- [Spring Boot data access](https://docs.spring.io/spring-boot/how-to/data-access.html)
