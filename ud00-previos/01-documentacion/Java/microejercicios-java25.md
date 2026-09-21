# Microejercicios de Java 25

Estos ejercicios son breves y opcionales. Sirven para reconocer las tres
novedades permanentes de Java 25 sin convertirlas en el centro del módulo. Se
ejecutan con JDK 25 y no requieren `--enable-preview`.

## 1. Importar un módulo

**Novedad:** declaraciones `import module` (JEP 511).

Crea un programa que use `List`, `Map` y `LocalDate` utilizando una sola
declaración:

```java
import module java.base;

public class ModuleImports {
    public static void main(String[] args) {
        var names = List.of("Ana", "Pedro", "Lucía");
        var today = LocalDate.now();
        System.out.println(today + ": " + names);
    }
}
```

Después sustituye la declaración por imports normales y explica qué aporta cada
forma. La conclusión importante es que importar un módulo no elimina la
necesidad de conocer paquetes ni debe usarse para ocultar dependencias.

## 2. Fuente compacta

**Novedad:** archivos fuente compactos y `main` de instancia (JEP 512).

Crea `Greeting.java` sin declarar una clase ni escribir `public static void
main`:

```java
void main() {
    IO.println("Hola desde Java 25");
}
```

Ejecuta el archivo directamente con `java Greeting.java`. Después explica por
qué una aplicación Gradle o Spring Boot sigue necesitando clases, paquetes y
una estructura explícita.

## 3. Cuerpo flexible de constructor

**Novedad:** sentencias antes de la invocación a `super` (JEP 513).

Completa este ejemplo para normalizar el texto antes de construir la clase
base:

```java
class Label {
    private final String value;

    Label(String value) {
        this.value = value;
    }
}

class NormalizedLabel extends Label {
    NormalizedLabel(String rawValue) {
        // Normaliza aquí y llama después a super(...).
        super(rawValue);
    }
}
```

Modifica el constructor para eliminar espacios exteriores y rechazar una
cadena vacía. No accedas a `this` ni a miembros de la instancia antes de
`super(...)`: el objetivo es reconocer la novedad y sus límites, no sustituir
una validación sencilla en un constructor normal.

## Criterio de uso

- Hacer los tres solo si el grupo ya domina records, `switch`, colecciones y
  tests.
- No evaluar estas novedades por separado ni exigirlas en Spring Boot.
- Mantener la sintaxis tradicional como referencia principal cuando facilite la
  lectura entre versiones de Java.

Referencias: [JEP 511](https://openjdk.org/jeps/511), [JEP
512](https://openjdk.org/jeps/512) y [JEP 513](https://openjdk.org/jeps/513).
