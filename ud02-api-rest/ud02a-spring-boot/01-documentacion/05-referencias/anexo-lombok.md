# Lombok — qué código genera cada anotación

Lombok genera código durante la compilación mediante su integración con el
compilador. **No es el contenedor de Spring ni el proveedor de persistencia**:
un constructor generado no registra un bean y un setter no guarda una fila.

Esta referencia amplía la tabla resumida del
[anexo de anotaciones de Spring](anexo-anotaciones-spring-boot.md#lombok).
Se consulta al leer una clase, no obliga a añadir todas estas anotaciones.

## 1. Accesores y constructores

| Anotación | Código que genera | Matiz de uso |
|---|---|---|
| `@Getter` | Getter para el campo o campos de la clase | No añade una regla de negocio al leer |
| `@Setter` | Setter para los campos aplicables | No valida por sí solo ni hace persistencia; no se genera para un campo `final` |
| `@Getter(AccessLevel.NONE)` / `@Setter(AccessLevel.NONE)` | Desactiva esa generación en un campo concreto | Útil si el acceso debe pasar por una operación del dominio |
| `@NoArgsConstructor` | Constructor sin argumentos | Puede necesitarse en entidades JPA; elegir visibilidad con intención |
| `@RequiredArgsConstructor` | Constructor para campos `final` sin inicializar y campos `@NonNull` sin inicializar | Facilita inyección por constructor; Spring es quien realiza la inyección |
| `@AllArgsConstructor` | Constructor con los campos de instancia correspondientes | El orden de argumentos sigue la declaración de campos; cuidado con muchos argumentos del mismo tipo |
| `@NonNull` | Añade comprobaciones de nulo en el código generado/aplicable | Habitualmente lanza `NullPointerException`; no es Bean Validation |

Ejemplo de constructor útil en un servicio:

```java
@Service
@RequiredArgsConstructor
public class GameService {
    private final GameRepository repository;
}
```

Lombok genera el constructor que recibe `repository`. Al existir un único
constructor, Spring puede utilizarlo sin `@Autowired`. **Hacer `new GameService`
en un test corriente no activa transacciones ni otras interceptaciones de Spring.**

### 1.1. `boolean` y `Boolean` no producen siempre el mismo getter

```java
private boolean active; // normalmente: isActive(), setActive(...)
private Boolean archived; // normalmente: getArchived(), setArchived(...)
```

Para `private boolean isHorizontal`, Lombok genera normalmente `isHorizontal()`
y `setHorizontal(...)`. No adivinar los nombres mirando solo el texto del campo:
comprobar el autocompletado o el código generado cuando haya dudas.

### 1.2. El constructor forzado no define un estado válido

`@NoArgsConstructor(force = true)` permite inicializar campos `final` con valores
predeterminados como cero, `false` o `null`. No significa que los invariantes del
dominio o las condiciones `NonNull` hayan quedado satisfechos. No utilizarlo
simplemente para silenciar un error de diseño de construcción.

## 2. Construcción con `@Builder`

| Anotación | Lectura | Cuidado |
|---|---|---|
| `@Builder` | Genera un builder con métodos de preparación y `build()` | No valida automáticamente las reglas del negocio ni sustituye a JPA |
| `@Builder.Default` | Conserva un valor por defecto cuando el builder no lo proporciona | Especialmente útil con colecciones inicializadas o flags |
| `@Singular` | Genera métodos para añadir elementos a una colección del builder | La colección construida tiene el comportamiento que documenta Lombok; no es automáticamente una colección persistente gestionada |

```java
@Getter
@Builder
public class GameSetup {
    private String name;

    @Builder.Default
    private boolean active = true;

    @Builder.Default
    private List<String> shipNames = new ArrayList<>();
}
```

Sin el tratamiento adecuado, un inicializador de campo no tiene por qué ser
el valor utilizado al construir con el builder. Por ejemplo, la lista podría
quedar nula. Asignar explícitamente `null` en el builder tampoco equivale a
«no haber proporcionado un valor».

`@Builder` aplicado a una clase y las anotaciones de constructor pueden interactuar.
Si la clase necesita varios constructores, se puede aplicar el builder al
constructor concreto que se quiere utilizar. No mezclar anotaciones hasta que
compile sin entender qué firmas se están generando.

## 3. Métodos de objeto y anotaciones compuestas

| Anotación | Qué genera |
|---|---|
| `@ToString` | Representación textual de los campos incluidos |
| `@ToString.Exclude` | Excluye ese campo del método generado |
| `@ToString(onlyExplicitlyIncluded = true)` + `@ToString.Include` | Opta por una selección explícita de campos |
| `@EqualsAndHashCode` | Igualdad y hash basados en los campos configurados |
| `@EqualsAndHashCode.Exclude` | Omite un campo de esa comparación/hash |
| `@EqualsAndHashCode(onlyExplicitlyIncluded = true)` + `Include` | Selecciona explícitamente los campos que intervienen |
| `@Data` | Combina getters, setters aplicables, `toString`, igualdad/hash y constructor requerido |
| `@Value` de Lombok | Modelo de valor con clase/campos y accesos orientados a inmutabilidad, constructor e igualdad/hash/texto |

La inmutabilidad de `@Value` es **superficial**: si un campo contiene una lista
mutable, su contenido no se vuelve inmutable automáticamente. Un record tampoco
hace inmutables por sí solos los objetos de sus componentes.

### 3.1. Entidades y `@Data`

Aplicar `@Data` a una entidad bidireccional puede generar métodos que recorren
`Game → ships → game`, activan carga diferida o calculan igualdad/hash usando
estado mutable.

- Excluir una relación de `toString` puede evitar ese recorrido al imprimir.
- Excluirla de `equals/hashCode` no define por sí solo una identidad persistente correcta.
- Usar solo un id generado también exige tratar qué pasa antes de que exista.
- Un campo mutable que cambie el hash dentro de un `HashSet` puede romper la búsqueda.

En entidades suele ser más claro elegir `@Getter` y los setters realmente
necesarios y diseñar por separado igualdad, texto y operaciones de negocio.
El [bloque A](../04-persistencia/bloque-a/01-entidades-relaciones-y-ciclo-de-vida.md)
explica el motivo con las relaciones del juego.

## 4. Otras anotaciones que puedes encontrar

| Anotación | Función y límite |
|---|---|
| `@Slf4j` | Genera un logger llamado `log` para esa clase; no registra todo automáticamente |
| `@Cleanup` | Genera cierre de un recurso según sus reglas; en Java moderno, valorar `try-with-resources` por claridad |
| `@SneakyThrows` | Permite propagar excepciones comprobadas sin declararlas de la forma ordinaria; no las resuelve ni las convierte en una respuesta HTTP |

Un `toString` generado puede incluir datos que no queremos registrar. La
selección de campos del texto y de los logs debe ser deliberada.

## 5. Anotaciones que se llaman igual

| Nombre | Paquete | Responsabilidad |
|---|---|---|
| `@Value` | `lombok.Value` | Generación de una clase de valor |
| `@Value` | `org.springframework.beans.factory.annotation.Value` | Inyección de un valor/configuración de Spring |
| `@NonNull` | `lombok.NonNull` | Comprobación en código generado/aplicable |
| `@NotNull` | `jakarta.validation.constraints.NotNull` | Restricción de Bean Validation al ejecutar la validación correspondiente |
| `@Column(nullable=false)` | `jakarta.persistence.Column` | Información de mapeo; con migraciones, la columna real debe tener su restricción |

Estas tres formas de expresar «no nulo» operan en momentos y niveles distintos.
No se sustituyen simplemente por tener un nombre parecido.

## 6. Ejemplo completo de comprobación del código generado

No se pide escribir pruebas de cada getter generado en el proyecto. Este ejemplo
solo hace observable lo que significan las anotaciones de la referencia.

<!-- ejemplo-java: LombokDictionaryTest.java -->
```java
package es.daw2a.referencias;

import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.Value;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class LombokDictionaryTest {
    @Getter @Setter @NoArgsConstructor
    static class Flags {
        private boolean active;
        private Boolean archived;
    }

    @Getter @Builder
    static class GameSetup {
        private String name;
        @Builder.Default private boolean active = true;
        @Builder.Default private List<String> shipNames = new ArrayList<>();
    }

    @Value
    static class DisplayName { String value; }

    @RequiredArgsConstructor
    static class RequiredName { @NonNull private final String name; }

    @AllArgsConstructor @ToString
    static class Credentials {
        private String username;
        @ToString.Exclude private String password;
    }

    @Test void generatesPrimitiveAndWrapperAccessors() {
        var flags = new Flags();
        flags.setActive(true);
        flags.setArchived(Boolean.FALSE);
        assertThat(flags.isActive()).isTrue();
        assertThat(flags.getArchived()).isFalse();
    }

    @Test void builderKeepsDefaultsAndIndependentLists() {
        var first = GameSetup.builder().name("first").build();
        var second = GameSetup.builder().name("second").build();
        first.getShipNames().add("A");
        assertThat(first.isActive()).isTrue();
        assertThat(first.getShipNames()).containsExactly("A");
        assertThat(second.getShipNames()).isEmpty();
    }

    @Test void valueGeneratesValueEquality() {
        assertThat(new DisplayName("Ana")).isEqualTo(new DisplayName("Ana"));
        assertThat(new DisplayName("Ana").getValue()).isEqualTo("Ana");
    }

    @Test void requiredNonNullIsCheckedByGeneratedConstructor() {
        assertThrows(NullPointerException.class, () -> new RequiredName(null));
    }

    @Test void excludedFieldIsNotPrinted() {
        assertThat(new Credentials("ana", "secret").toString())
                .contains("ana").doesNotContain("secret");
    }
}
```

## 7. Comprobar qué se ha generado

- Autocompletado e inspección de firmas en el IDE.
- Compilación mediante el wrapper del proyecto, con su procesador de anotaciones.
- Herramienta `delombok` de Lombok cuando se necesite examinar el Java equivalente.

El proyecto de aula ya configura Lombok como procesador. Que el editor no
reconozca un método y que el compilador no lo genere pueden ser problemas
distintos: comprobar el build real antes de cambiar las clases.

Fuentes: [Lombok — características estables](https://projectlombok.org/features/),
[constructores](https://projectlombok.org/features/constructor),
[builder](https://projectlombok.org/features/Builder),
[igualdad](https://projectlombok.org/features/EqualsAndHashCode)
y [toString](https://projectlombok.org/features/ToString).
