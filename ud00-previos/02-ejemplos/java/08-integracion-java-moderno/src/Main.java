import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) throws IOException {
        // JAVA: Path/Files leen un fichero real; UTF-8 hace explícita la codificación.
        var origen = Path.of("datos", "peticiones.csv");
        var texto = Files.readString(origen, StandardCharsets.UTF_8);

        // JAVA: Stream transforma las líneas en una lista de records; toList() no modifica la entrada.
        List<Peticion> peticiones = texto.lines()
                .skip(1) // La primera línea contiene los nombres de las columnas.
                .filter(linea -> !linea.isBlank())
                .map(Main::parsear)
                .toList();

        // JAVA: getFirst() forma parte de la API de colecciones secuenciadas (Java 21).
        System.out.println("Primera petición: " + peticiones.getFirst());

        // JAVA: sealed limita los resultados posibles; el switch con record patterns
        // descompone el record y es exhaustivo, sin if/else por tipos ni cast.
        peticiones.stream()
                .map(Main::clasificar)
                .map(Main::describir)
                .forEach(System.out::println);

        // JAVA: groupingBy + counting agrupan sin modificar la lista original.
        Map<Integer, Long> porFamilia = peticiones.stream().collect(Collectors.groupingBy(
                peticion -> peticion.estado() / 100,
                TreeMap::new,
                Collectors.counting()));

        // JAVA: max devuelve Optional; orElseThrow evita inventar una petición si faltan datos.
        var masLenta = peticiones.stream()
                .max(Comparator.comparingInt(Peticion::duracionMs))
                .orElseThrow(() -> new IllegalStateException("Fichero sin peticiones"));

        // JAVA: text block permite redactar texto multilínea; formatted inserta los resultados.
        var informe = """
                Resumen de peticiones (datos de ejemplo)
                Total: %d
                Por familia HTTP: %s
                Más lenta: %s %s (%d ms)
                """.formatted(peticiones.size(), porFamilia, masLenta.metodo(),
                masLenta.ruta(), masLenta.duracionMs());

        // JAVA: Files crea el directorio de salida y persiste el informe en UTF-8.
        var destino = Path.of("out", "resumen.txt");
        Files.createDirectories(destino.getParent());
        Files.writeString(destino, informe, StandardCharsets.UTF_8);
        System.out.println(informe);
        System.out.println("Informe guardado en " + destino.toAbsolutePath());
    }

    private static Peticion parsear(String linea) {
        var columnas = linea.split(";", -1);
        if (columnas.length != 4) {
            throw new IllegalArgumentException("Se esperaban cuatro columnas: " + linea);
        }
        return new Peticion(columnas[0], columnas[1],
                Integer.parseInt(columnas[2]), Integer.parseInt(columnas[3]));
    }

    private static Resultado clasificar(Peticion peticion) {
        return peticion.estado() < 400 ? new Correcta(peticion) : new Fallida(peticion);
    }

    private static String describir(Resultado resultado) {
        return switch (resultado) {
            // JAVA: pattern matching y guarda when (Java 21) sin perder exhaustividad.
            case Correcta(Peticion(var metodo, var ruta, var estado, var ms)) when ms > 100 ->
                    "Lenta: %s %s -> %d (%d ms)".formatted(metodo, ruta, estado, ms);
            case Correcta(Peticion(var metodo, var ruta, var estado, var ms)) ->
                    "Correcta: %s %s -> %d (%d ms)".formatted(metodo, ruta, estado, ms);
            case Fallida(Peticion p) ->
                    "Error: %s %s -> %d".formatted(p.metodo(), p.ruta(), p.estado());
        };
    }
}

// JAVA: record genera accesores, equals/hashCode y toString; el constructor
// compacto valida sus componentes antes de guardar el estado.
record Peticion(String metodo, String ruta, int estado, int duracionMs) {
    Peticion {
        Objects.requireNonNull(metodo);
        Objects.requireNonNull(ruta);
        if (metodo.isBlank() || ruta.isBlank() || estado < 100 || estado > 599 || duracionMs < 0) {
            throw new IllegalArgumentException("Petición inválida");
        }
    }
}

// JAVA: sealed define el conjunto cerrado de variantes del resultado.
sealed interface Resultado permits Correcta, Fallida { }
record Correcta(Peticion peticion) implements Resultado { }
record Fallida(Peticion peticion) implements Resultado { }
