import java.nio.file.Path;
import java.util.List;

public final class NoteFile {
    private NoteFile() {
    }

    public record Note(String title, String content) {
    }

    public static void save(Path path, List<Note> notes) {
        throw new UnsupportedOperationException("Completa J25-06");
    }

    public static List<Note> load(Path path) {
        throw new UnsupportedOperationException("Completa J25-06");
    }
}
