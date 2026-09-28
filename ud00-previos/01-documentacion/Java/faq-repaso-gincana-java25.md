# Preguntas para repasar Java tras la gincana

La gincana ya ha terminado. Usa estas preguntas para entender **por qué**
funcionaron los programas, no para memorizar las claves. Intenta responder cada
una antes de leer la respuesta. Los ejemplos son independientes de las
soluciones de los retos.

## 1. Compilar, ejecutar y conectar programas

### ¿Qué hace cada parte de `javac -d out src/Main.java` y `java -cp out Main`?

`javac` compila el código fuente y `-d out` deja los `.class` bajo `out/`.
`java -cp out Main` ejecuta la clase `Main` y le indica a Java dónde buscarla.
`-cp` recibe una **ruta de clases compiladas**, no el nombre de un fichero
`.java`. Si la clase declara un paquete, también hay que indicar su nombre
completo al ejecutarla.

**Prueba mental:** si compilas en `out/` y ejecutas `java Main` sin `-cp out`,
¿por qué podría no encontrar la clase?

### ¿Qué hizo la tubería `|`? ¿El descifrador corrigió el programa?

La tubería envió la salida estándar del primer programa como entrada estándar
del segundo. `SiguienteReto` tomó la última línea `CLAVE=...` e intentó
descifrar el siguiente enunciado. **No verificó el código:** una clave correcta
puede haberse obtenido por otros medios. Poder explicar el cálculo y ejecutar
el programa son comprobaciones distintas.

## 2. Números, formatos y condiciones

### ¿Por qué `suma / cantidad` no siempre sirve para obtener una media decimal?

Si ambos operandos son `int`, Java realiza división entera y descarta la parte
decimal. `suma / (double) cantidad` calcula un `double`. Para presentar tres
decimales con punto independientemente del idioma del ordenador, puede usarse
`String.format(Locale.ROOT, "%.3f", media)`.

**Prueba mental:** ¿qué imprimirían `5 / 2` y `5 / 2.0`?

## 3. `record`, igualdad y colecciones

### ¿Cuándo usar un `record` en lugar de una clase tradicional?

Cuando queremos agrupar datos cuyo estado no se reasigna tras construirlos y
tener automáticamente constructor, accesores (`nombre()` en vez de
`getNombre()`), `toString`, `equals` y `hashCode`. Un constructor compacto
permite validar los valores antes de guardar el estado. Un `record` no vuelve
inmutables por arte de magia los objetos mutables que reciba como componentes.

### ¿Por qué cambiar `equals` obliga a revisar también `hashCode`?

El contrato exige que dos objetos iguales tengan el mismo hash. Un `HashSet`
usa ese hash para buscar duplicados: si `equals` ignora mayúsculas, pero
`hashCode` no, podría conservar dos estudiantes considerados iguales. Hay que
normalizar el nombre de forma coherente en ambos métodos. Que dos objetos
tengan el mismo hash **no** implica que sean iguales.

**Prueba mental:** ¿qué debería ocurrir al añadir «Ada» y «ada» de la misma edad
a un `HashSet` que usa esa regla de igualdad?

## 4. Polimorfismo

### ¿Qué ganamos al declarar `Figura figura = new Circulo(1)`?

Podemos trabajar con cualquier subtipo de `Figura` y llamar a `figura.area()`
sin escribir una condición para cada figura. Java ejecuta la implementación
del objeto real (`Circulo`). Una clase abstracta sirve para definir el contrato
común; las subclases aportan la fórmula concreta.

**Prueba mental:** si añadieras un `Triangulo`, ¿qué parte del código debería
poder reutilizarse sin cambios?

## 5. Colecciones y Streams

### ¿Por qué importa el orden de `sorted(...).limit(3)`?

Para encontrar los tres libros más recientes, primero hay que ordenar **todos**
por año descendente y después quedarse con tres. Si haces `limit(3)` antes,
solo ordenas los tres primeros elementos originales y puedes excluir libros
más nuevos. `map(Book::title)` transforma los libros seleccionados en títulos.

### ¿Es obligatorio usar Streams para este problema?

No. Un bucle y una lista también sirven si expresan correctamente la
ordenación y el límite. Streams resultan útiles cuando una cadena de
transformaciones (`sorted`, `limit`, `map`) hace el proceso más legible; su uso
no sustituye la comprobación del resultado.

## 6. Ficheros y rutas

### ¿Dónde se crea `Path.of("notas.txt")`?

En el **directorio de trabajo del proceso**, que depende de cómo lo ejecutes
desde terminal o IntelliJ; no necesariamente junto al `.java`. Se puede
comprobar con `Path.of("").toAbsolutePath()`. Si solo miras en la carpeta del
código fuente, podrías pensar que el fichero no se creó.

### ¿Por qué leer de nuevo después de `Files.writeString`?

Porque así compruebas lo que quedó guardado, no solo el valor que tenías en
memoria. Especificar `StandardCharsets.UTF_8` hace explícita la codificación
del ejercicio. Si añades un salto de línea al escribir, el texto leído ya no
será exactamente igual al original y cambia su número de caracteres.

## 7. `sealed` y `switch`

### ¿Qué aporta `sealed` frente a una interfaz normal?

Limita los tipos que pueden implementarla mediante `permits`. Así sabemos qué
variantes existen y un `switch` que devuelve un valor puede tratarlas todas.
Si se añade una nueva variante permitida, el compilador puede señalar que
falta actualizar ese `switch` exhaustivo.

### ¿Por qué usar `switch` con patrones en vez de comparar cadenas como `"OK"`?

El tipo representa el estado (`Success`, `Warning`, `Failure`) y cada caso
extrae su contenido con seguridad de tipos. Comparar cadenas dispersa las
reglas, admite errores tipográficos y no avisa al añadir un nuevo tipo de
resultado.

## Para cerrar

Elige **una** de estas frases y justifícala con un ejemplo de tu código:

1. «La clave correcta no demuestra por sí sola que mi solución sea correcta».
2. «El directorio desde el que ejecuto afecta a dónde aparece mi fichero».
3. «Al cambiar una regla de igualdad también cambian mis colecciones».
