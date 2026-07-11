# Guion de clase — Java (hasta 21) para estudiantes de Kotlin

**Perfil del alumnado:** Programadores con base en Kotlin que necesitan trasladar sus conocimientos a Java moderno (Java 17–21).

**Producto final del módulo:** Mini‑proyecto **GeoNotes** (CLI + librería) que usa *records*, *sealed types*, *pattern matching* (incl. *record patterns*), *switch expressions*, *text blocks*, *sequenced collections* y una pincelada de *virtual threads*.

**Prerequisitos:** JDK 21, Gradle 8+, editor (IntelliJ IDEA recomendado), Java habilitado en PATH.

---

## 🚀 Contexto de la Industria: Java en Spring Boot 3 vs Spring Boot 4

Al incorporarse al mercado laboral, el alumnado se encontrará con proyectos en diferentes fases de madurez tecnológica. Es vital entender qué características de Java están disponibles según la versión de Spring Boot del proyecto:

### 1. Spring Boot 3 (Legado vigente / Producción mayoritaria)
- **Línea base**: Requiere **Java 17** como mínimo.
- **Características de Java 17 disponibles**: 
  - `record` para inmutabilidad (Java 16+).
  - `sealed` classes e interfaces (Java 17).
  - Expresiones `switch` con flechas `->` (Java 14+).
  - *Text Blocks* `"""` para plantillas multilínea (Java 15).
  - *Pattern Matching* básico para `instanceof` (Java 16).
- **Limitaciones**: No se pueden usar de forma nativa las novedades de Java 21+ (como *Record Patterns*, *Sequenced Collections* o *Virtual Threads*).

### 2. Spring Boot 4 (Greenfield / Futuro inmediato)
- **Línea base**: Requiere **Java 21** como mínimo (con soporte completo para Java 25 LTS).
- **Características habilitadas**:
  - *Sequenced Collections* (`putFirst`, `reversed`, etc.).
  - *Record Patterns* (desestructuración de records en switch/instanceof).
  - *Pattern Matching for switch* completo (con guardas `when`).
  - *Virtual Threads* nativos (para alta concurrencia síncrona sin bloquear hilos del SO).
  - Variables y patrones anónimos `_` (Java 22, estándar en Java 25).

### ¿Por qué conocer ambos?
Un desarrollador senior debe saber escribir código robusto en Java 17 para mantener sistemas heredados en Spring Boot 3, pero también debe conocer las APIs modernas de Java 21/25 para proponer refactorizaciones eficientes al migrar proyectos a Spring Boot 4 (por ejemplo, simplificar el acceso a colecciones o habilitar Virtual Threads para mejorar el rendimiento de APIs de bloqueos de red).

---

## Vista general por sesiones

### Sesión 1 — Calentamiento Java vs Kotlin + Setup
**Objetivos**
- Diferencias clave de sintaxis y filosofía (nullability, tipado, `final` vs `val`, `record` vs `data class`).
- Crear proyecto Gradle y estructura básica.

**Actividades**
1) **Mini‑repaso rápido** (15′):
   - Kotlin `val`/`var` → Java `final`/tipo explícito.
   - `data class` (Kotlin) → `record` (Java 16+).
   - `sealed class/interface` existe también en Java 17.
   - `when` (Kotlin) ~ `switch expression` (Java 14+).
2) **Proyecto** (20′):
   - Crear carpeta `geonotes/` y proyecto Gradle (aplicación CLI):

```bash
mkdir geonotes && cd geonotes
gradle init --type java-application --dsl groovy --test-framework junit --project-name geonotes --package com.example.geonotes
```

3) **Estructura inicial** (10′):
   - `src/main/java/com/example/geonotes/Main.java`
   - `src/main/java/com/example/geonotes/model/` (modelos)
   - `src/main/java/com/example/geonotes/core/` (lógica)

**Tarea**
- Leer el código base y compilar/ejecutar con `./gradlew run`.

---

### Sesión 2 — Records + Validación + Text Blocks
**Objetivos**
- Definir datos inmutables con `record`.
- Validar datos con *compact constructor*.
- Producir salidas multilínea con *Text Blocks* (Java 15).

**Código**
`src/main/java/com/example/geonotes/model/GeoPoint.java`
```java
package com.example.geonotes.model;

// Equivalente aproximado a: data class GeoPoint(val lat: Double, val lon: Double)
public record GeoPoint(double lat, double lon) {
  // Compact constructor para validar de forma concisa
  public GeoPoint {
    if (Double.isNaN(lat) || lat < -90 || lat > 90) {
      throw new IllegalArgumentException("lat fuera de rango [-90,90]");
    }
    if (Double.isNaN(lon) || lon < -180 || lon > 180) {
      throw new IllegalArgumentException("lon fuera de rango [-180,180]");
    }
  }
}
```

`src/main/java/com/example/geonotes/model/Note.java`
```java
package com.example.geonotes.model;

import java.time.Instant;

// Record para una nota geolocalizada (immutable por diseño)
public record Note(long id, String title, String content, GeoPoint location, Instant createdAt) {
  public Note {
    if (title == null || title.isBlank()) throw new IllegalArgumentException("title requerido");
    if (content == null) content = ""; // normalizamos a ""
    if (location == null) throw new IllegalArgumentException("location requerido");
    if (createdAt == null) createdAt = Instant.now();
  }
}
```

`src/main/java/com/example/geonotes/core/Render.java`
```java
package com.example.geonotes.core;

import com.example.geonotes.model.Note;

// Uso de Text Blocks """ para generar plantillas multilínea (como Kotlin raw strings)
public final class Render {
  private Render() {}

  public static String toJson(Note n) {
    String json = """
      {
        "id": %d,
        "title": "%s",
        "content": "%s",
        "location": { "lat": %.6f, "lon": %.6f },
        "createdAt": "%s"
      }
      """.formatted(
        n.id(), n.title(), escape(n.content()),
        n.location().lat(), n.location().lon(), n.createdAt()
      );
    return json;
  }

  private static String escape(String s) { return s.replace("\"", "\\\""); }
}
```

**Actividad guiada**
- Crear 2–3 `Note` y mostrarlas con `Render.toJson`.

---

### Sesión 3 — Jerarquías con *sealed* + Switch Expression + Pattern Matching
**Objetivos**
- Diseñar una jerarquía restringida con `sealed interface` (Java 17).
- Usar `switch` como **expresión** con flechas `->` y `yield`.
- *Pattern matching* para `instanceof` y en `switch`.

**Código**
`src/main/java/com/example/geonotes/model/Attachment.java`
```java
package com.example.geonotes.model;

// Jerarquía cerrada como en Kotlin sealed
public sealed interface Attachment permits Photo, Audio, Link {}

public record Photo(String url, int width, int height) implements Attachment {}
public record Audio(String url, int seconds) implements Attachment {}
public record Link(String url, String label) implements Attachment {}
```

`src/main/java/com/example/geonotes/core/Describe.java`
```java
package com.example.geonotes.core;

import com.example.geonotes.model.*;

public final class Describe {
  private Describe() {}

  // switch como EXPRESIÓN + pattern matching de tipos + guardas `when`
  public static String attachmentLabel(Attachment a) {
    return switch (a) {
      case Photo p when p.width() >= 1920 -> "📷 Photo (HD)";
      case Photo p                        -> "📷 Photo";
      case Audio a1 when a1.seconds() > 300 -> "🎵 Audio (largo)";
      case Audio a1                          -> "🎵 Audio";
      case Link l                            -> "🔗 " + (l.label() == null ? l.url() : l.label());
    };
  }

  // Pattern matching para instanceof
  public static int mediaSize(Object o) {
    if (o instanceof Photo p) {
      return p.width() * p.height();
    } else if (o instanceof Audio a) {
      return a.seconds();
    } else {
      return 0;
    }
  }
}
```

**Actividad guiada**
- Instanciar varios `Attachment` y filtrar/etiquetar con `attachmentLabel`.

---

### Sesión 4 — Record Patterns + Desestructuración + Parciales
**Objetivos**
- Emplear **record patterns** (Java 21) en `switch` e `if`.
- Hacer *matching* anidado y con guardas.

**Código**
`src/main/java/com/example/geonotes/model/GeoArea.java`
```java
package com.example.geonotes.model;

public record GeoArea(GeoPoint topLeft, GeoPoint bottomRight) {}
```

`src/main/java/com/example/geonotes/core/Match.java`
```java
package com.example.geonotes.core;

import com.example.geonotes.model.*;

public final class Match {
  private Match() {}

  public static String region(GeoPoint p) {
    // Ejemplo simple con guardas
    return switch (p) {
      case GeoPoint(double lat, double lon) when lat == 0 && lon == 0 -> "ORIGIN";
      case GeoPoint(double lat, double lon) when lat == 0 -> "Equator";
      case GeoPoint(double lat, double lon) when lon == 0 -> "Greenwich";
      case GeoPoint(double lat, double lon) -> "(" + lat + "," + lon + ")";
    };
  }

  public static boolean contains(GeoArea r, GeoPoint p) {
    // Record patterns anidados en instanceof
    if (r instanceof GeoArea(GeoPoint(double x1, double y1), GeoPoint(double x2, double y2))) {
      double lat = p.lat();
      double lon = p.lon();
      return lat >= Math.min(x1, x2) && lat <= Math.max(x1, x2)
          && lon >= Math.min(y1, y2) && lon <= Math.max(y1, y2);
    }
    return false;
  }
}
```

**Actividad guiada**
- Probar `region` y `contains` con varios puntos y áreas.

---

### Sesión 5 — Sequenced Collections + Diseño de API
**Objetivos**
- Introducir `SequencedCollection`, `SequencedSet`, `SequencedMap` (Java 21).
- Diseñar una API simple de timeline de notas.

**Código**
`src/main/java/com/example/geonotes/core/Timeline.java`
```java
package com.example.geonotes.core;

import com.example.geonotes.model.Note;
import java.util.*;

// Usamos LinkedHashMap que ahora cumple SequencedMap (orden de inserción)
public final class Timeline {
  private final SequencedMap<Long, Note> notes = new LinkedHashMap<>();

  public void addFirst(Note n) { notes.putFirst(n.id(), n); }
  public void addLast(Note n)  { notes.putLast(n.id(), n); }

  public Note first()  { return notes.firstEntry().getValue(); }
  public Note last()   { return notes.lastEntry().getValue(); }

  public SequencedMap<Long, Note> reversedView() { return notes.reversed(); }
  public Collection<Note> values() { return notes.values(); }
}
```

**Actividad guiada**
- Insertar notas con `addFirst`/`addLast` y comprobar `reversedView`.

---

### Sesión 6 — Integración y CLI con Text Blocks + Switch + Render
**Objetivos**
- Montar un CLI simple para listar/filtrar/añadir notas.
- Consolidar *text blocks*, *switch expression* y colecciones.

**Código**
`src/main/java/com/example/geonotes/Main.java`
```java
package com.example.geonotes;

import com.example.geonotes.core.*;
import com.example.geonotes.model.*;
import java.time.Instant;
import java.util.*;

public class Main {
  private static final Timeline timeline = new Timeline();
  private static final Scanner sc = new Scanner(System.in);

  public static void main(String[] args) {
    seed();
    loop();
  }

  private static void loop() {
    while (true) {
      String menu = """
        === GeoNotes ===
        1) Añadir nota al final
        2) Añadir nota al inicio
        3) Listar
        4) Listar (reversed)
        5) Region de punto
        0) Salir
        Opción:
        """;
      System.out.print(menu);
      String opt = sc.nextLine().trim();
      switch (opt) {
        case "1" -> addNote(false);
        case "2" -> addNote(true);
        case "3" -> list(false);
        case "4" -> list(true);
        case "5" -> regionQuery();
        case "0" -> { System.out.println("Adiós!"); return; }
        default -> System.out.println("Opción inválida");
      }
    }
  }

  private static void addNote(boolean first) {
    try {
      System.out.print("Título: ");
      String t = sc.nextLine();
      System.out.print("Contenido: ");
      String c = sc.nextLine();
      System.out.print("lat: "); double lat = Double.parseDouble(sc.nextLine());
      System.out.print("lon: "); double lon = Double.parseDouble(sc.nextLine());
      long id = System.currentTimeMillis();
      Note n = new Note(id, t, c, new GeoPoint(lat, lon), Instant.now());
      if (first) timeline.addFirst(n); else timeline.addLast(n);
      System.out.println("OK\n" + Render.toJson(n));
    } catch (Exception e) {
      System.out.println("Error: " + e.getMessage());
    }
  }

  private static void list(boolean reversed) {
    var view = reversed ? timeline.reversedView().values() : timeline.values();
    for (Note n : view) {
      System.out.println(Render.toJson(n));
    }
  }

  private static void regionQuery() {
    try {
      System.out.print("lat: "); double lat = Double.parseDouble(sc.nextLine());
      System.out.print("lon: "); double lon = Double.parseDouble(sc.nextLine());
      System.out.println(Match.region(new GeoPoint(lat, lon)));
    } catch (Exception e) {
      System.out.println("Error: " + e.getMessage());
    }
  }

  private static void seed() {
    timeline.addLast(new Note(1, "Cádiz", "Playita", new GeoPoint(36.5297, -6.2927), Instant.now()));
    timeline.addLast(new Note(2, "Sevilla", "Triana", new GeoPoint(37.3826, -5.9963), Instant.now()));
  }
}
```

**Actividad guiada**
- Usar todas las opciones. Observar vistas invertidas y JSON multilínea.

---

### Sesión 7 — Virtual Threads (breve) + Pruebas
**Objetivos**
- Comprender cuándo tendría sentido usar *virtual threads* para tareas IO.
- Añadir una demo mínima (no imprescindible en el proyecto final).

**Código (demostración opcional)**
`src/main/java/com/example/geonotes/core/AsyncDemo.java`
```java
package com.example.geonotes.core;

import java.util.concurrent.*;

public final class AsyncDemo {
  private AsyncDemo() {}

  public static void runManyIO() throws Exception {
    try (var exec = Executors.newVirtualThreadPerTaskExecutor()) {
      Future<?> f1 = exec.submit(() -> slowIO(1));
      Future<?> f2 = exec.submit(() -> slowIO(2));
      f1.get(); f2.get();
    }
  }

  private static void slowIO(int id) {
    try {
      Thread.sleep(500); // simulación de IO bloqueante
      System.out.println("IO " + id + " OK en " + Thread.currentThread());
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
  }
}
```

**Actividad**
- Llamar a `AsyncDemo.runManyIO()` desde `Main` tras un comando oculto `vt` y observar que los hilos son *virtual*.

---

## Sugerencia de evaluación continua
- **Checkpoints en clase** (micro‑retos de 5′–10′): crear un `record`, añadir una guarda en `switch`, aplicar `record patterns` anidados.
- **Mini‑entrega**: CLI funcional que cree y liste notas con formatos JSON (*text blocks*) y jerarquía de adjuntos sellada.
- **Rubrica** (10 ptos):
  - Modelado con `record` + validación (2)
  - Jerarquía `sealed` + `switch`/pattern matching (3)
  - `record patterns` (2)
  - `sequenced collections` (2)
  - (Opcional) virtual threads demo (1)

---

## Equivalencias rápidas Java ↔ Kotlin
- `data class` ↔ `record` (inmutables, miembros canónicos; en Java los campos son finales y hay *accessors* `name()` en lugar de propiedades).
- `sealed class/interface` ~ `sealed` Java (con `permits`).
- `when` ↔ `switch expression` + *pattern matching* (`case T t when cond -> ...`).
- Raw strings de Kotlin ↔ *Text Blocks* `"""` de Java.
- Estructuras inmutables: usar `List.of`, `Map.of` y para orden secuencial, `Sequenced*`.

---

## Extensiones y deberes
- Añade tipo `Attachment` nuevo (`Video`) y ajusta el `switch` exhaustivo.
- Exporta `Timeline` a un archivo usando `Files.writeString` con *text blocks*.
- Escribe 3 tests JUnit para `Match.contains` y `Describe.attachmentLabel`.

---

## Apéndice — Comandos útiles
```bash
# Compilar y ejecutar
./gradlew run

# Empaquetar (jar)
./gradlew jar

# Ejecutar jar
java -jar build/libs/geonotes.jar
```

