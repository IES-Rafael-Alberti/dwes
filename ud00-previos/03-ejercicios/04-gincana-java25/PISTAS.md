# Pistas de la gincana Java 25

Lee primero solo la pista 1 del reto en el que estas. Pide ayuda si persiste el
bloqueo; no hace falta acabar todos los retos.

## Reto 1

- Pista 1: recorre los numeros una sola vez y actualiza minimo, maximo, suma y
  contador de apariciones.
- Pista 2: calcula la media como `suma / (double) cantidad`; usa
  `String.format(Locale.ROOT, "%.3f", media)`.
  La ultima linea debe comenzar por `CLAVE=R1-`.

## Reto 2

- Pista 1: `equalsIgnoreCase` sirve para comparar nombres sin distinguir
  mayusculas. Un `HashSet<Student>` permite comprobar si `hashCode` concuerda
  con `equals`.
- Pista 2: si cambias `equals`, normaliza tambien el nombre en `hashCode` con
  `toLowerCase(Locale.ROOT)`.

## Reto 3

- Pista 1: `Figura` puede ser abstracta y declarar `area()`; usa una referencia
  `Figura` para llamar a cada implementacion.
- Pista 2: `Math.PI * radio * radio` da el area del circulo; presenta ambas
  areas con dos decimales usando `Locale.ROOT`.

## Reto 4

- Pista 1: ordena por anio descendente antes de quedarte con tres libros.
- Pista 2: `stream()`, `sorted(...)`, `limit(3)` y `map(Book::title)` te permiten
  construir la secuencia de titulos en ese orden.

## Reto 5

- Pista 1: escribe con `Files.writeString(Path.of("notas.txt"), texto, UTF_8)` y
  vuelve a leer con `Files.readString`.
- Pista 2: el numero de caracteres se obtiene del **texto leido**, no de un
  literal escrito en la clave.

## Reto 6

- Pista 1: una jerarquia `sealed` obliga a listar las implementaciones
  permitidas; el `switch` debe cubrir todos los casos.
- Pista 2: prueba el metodo con un exito, un aviso y un error; la ultima linea
  concatena los tres resultados en el orden indicado por el enunciado.
