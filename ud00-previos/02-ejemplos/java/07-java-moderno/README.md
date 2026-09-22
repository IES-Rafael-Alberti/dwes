# Java moderno en ejecución

Proyecto único para explicar en IntelliJ las características de Java moderno
que aparecen en la guía. `Main` llama a cada demostración por separado; se
puede ejecutar todo el recorrido o comentar temporalmente llamadas para
centrarse en un bloque.

| Clase | Características |
|---|---|
| `VarAndSwitchDemo` | `var` y `switch` como expresión |
| `TextBlocksDemo` | Text blocks y `formatted` |
| `Result` y `ResultDemo` | `record`, `sealed`, patrones de records y guardas `when` |
| `VirtualThreadsDemo` | Ejecutores y tareas con virtual threads |
| `SequencedCollectionsDemo` | `getFirst`, `getLast` y `reversed` |

Requiere **JDK 25**. No necesita Gradle ni dependencias externas:

```bash
javac -d out src/*.java
java -cp out Main
```

En IntelliJ, abrir esta carpeta como proyecto sencillo, configurar el SDK 25 y
ejecutar `Main`. Para el bloque de virtual threads, la salida muestra el hilo
virtual real que ha ejecutado cada tarea; el orden de finalización puede variar.
