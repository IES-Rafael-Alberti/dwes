import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class Main {
    public static void main(String[] args) throws IOException {
        Path path = Path.of("out", "notes.txt");
        Files.createDirectories(path.getParent());
        Files.writeString(path, "Java 25\nNIO.2\n", StandardCharsets.UTF_8);

        Files.readAllLines(path, StandardCharsets.UTF_8)
            .stream()
            .filter(line -> !line.isBlank())
            .forEach(System.out::println);
    }
}
