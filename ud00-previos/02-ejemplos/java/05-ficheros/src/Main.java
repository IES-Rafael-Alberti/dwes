import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class Main {
    public static void main(String[] args) {
        Path path = Path.of("out", "notes.txt");

        try {
            Files.createDirectories(path.getParent());

            System.out.println("Primera forma: lista de lineas y Stream");
            Files.writeString(path, "Java 25\nNIO.2\n", StandardCharsets.UTF_8);
            Files.readAllLines(path, StandardCharsets.UTF_8)
                .stream()
                .filter(line -> !line.isBlank())
                .forEach(System.out::println);

            System.out.println("\nSegunda forma: texto completo");
            Files.writeString(path, "Primera línea\nSegunda línea\n", StandardCharsets.UTF_8);
            String contenido = Files.readString(path, StandardCharsets.UTF_8);
            System.out.println(contenido);
        } catch (IOException exception) {
            System.out.println("No se pudo leer o escribir: " + exception.getMessage());
        }
    }
}
