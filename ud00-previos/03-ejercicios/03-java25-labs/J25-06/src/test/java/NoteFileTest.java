import org.junit.jupiter.api.Test;

import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.assertEquals;

class NoteFileTest {
    @Test
    void savesAndLoadsUtf8Notes() throws Exception {
        var path = Files.createTempDirectory("j25-06").resolve("data/notes.tsv");
        var notes = java.util.List.of(new NoteFile.Note("Cadiz", "playa"),
                new NoteFile.Note("Sevilla", "Triana"));

        NoteFile.save(path, notes);

        assertEquals(notes, NoteFile.load(path));
    }
}
