# Ejemplos Java del guion

Ejemplos pequeños y ejecutables para acompañar el guion de Java para alumnado
con base en Kotlin. No son tareas evaluables ni sustituyen a los proyectos de
`03-ejercicios/`.

Cada carpeta es independiente y se puede compilar desde su propia raíz:

```bash
javac -d out src/*.java
java -cp out Main
```

| Ejemplo | Idea principal |
|---|---|
| [01-polimorfismo](01-polimorfismo/README.md) | Clase abstracta, herencia y `@Override` |
| [02-records](02-records/README.md) | `record`, constructor compacto y accesores |
| [03-sealed-switch](03-sealed-switch/README.md) | `sealed`, records y `switch` exhaustivo |
| [04-streams](04-streams/README.md) | `filter`, `map`, `groupingBy` y `Optional` |
| [05-ficheros](05-ficheros/README.md) | `Path`, `Files`, UTF-8 y errores |
| [06-genericos](06-genericos/README.md) | Invariancia, comodines y PECS |
| [07-java-moderno](07-java-moderno/README.md) | Java 10-21: `var`, patrones, `sealed`, virtual threads y colecciones |

Los microejercicios de las novedades propias de Java 25 están separados en
`01-documentacion/Java/microejercicios-java25.md`: son actividades guiadas, no
otra aplicación que mantener.

GeoNotes, Calc25 y los laboratorios con tests permanecen en
`03-ejercicios/` porque ya son proyectos o retos con un recorrido propio.
