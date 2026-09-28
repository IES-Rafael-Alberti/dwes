# De Java moderno a un registro de peticiones

Demostración integradora para cerrar el repaso de Java y abrir UD01. La entrada
es un **fichero de datos de ejemplo**, no peticiones HTTP reales: más adelante
observaremos peticiones y respuestas con HTTPie y un servidor auténtico.

Desde esta carpeta, con **JDK 25**:

```bash
javac -d out src/Main.java
java -cp out Main
```

Abrir la carpeta como proyecto sencillo en IntelliJ y ejecutar `Main` con
esta carpeta como **directorio de trabajo**, para que encuentre
`datos/peticiones.csv`. El programa deja `out/resumen.txt` para comparar la
salida de pantalla con la que ha quedado guardada en disco.

| Paso del recorrido | Característica Java que se muestra |
|---|---|
| Leer y validar líneas | `Path`, `Files`, UTF-8 y `record` con constructor compacto |
| Identificar éxito o error | Jerarquía `sealed` y `switch` con patrones de `record` |
| Resumir el conjunto | Streams, `map`, `groupingBy`, `Comparator` y `Optional` |
| Mostrar primer elemento | `getFirst()` de colecciones secuenciadas |
| Elaborar el informe | `var`, text block y `formatted` |
| Guardar el resultado | `Files.writeString` y directorio `out/` ignorado |

El código fuente tiene comentarios `JAVA: ...` junto a cada decisión. En clase
basta con seguir tres preguntas: **¿de dónde proceden los datos?, ¿dónde se
clasifican y transforman?, ¿qué cambia cuando quien proporciona los datos es un
navegador y el programa espera peticiones en un servidor?**

Los hilos virtuales tienen su demostración separada en
[`07-java-moderno`](../07-java-moderno/README.md); no intervienen en este
ejemplo secuencial y no hacen falta para entender el comienzo de UD01.
