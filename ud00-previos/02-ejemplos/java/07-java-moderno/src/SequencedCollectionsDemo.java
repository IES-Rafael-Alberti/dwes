import java.util.ArrayList;
import java.util.List;

public final class SequencedCollectionsDemo {
    private SequencedCollectionsDemo() {
    }

    public static void run() {
        var steps = new ArrayList<>(List.of("validar", "guardar", "responder"));
        var reverseView = steps.reversed();
        var independentCopy = new ArrayList<>(reverseView);

        System.out.println("Primero: " + steps.getFirst());
        System.out.println("Ultimo: " + steps.getLast());
        System.out.println("Vista inversa: " + reverseView);
        System.out.println("Copia independiente: " + independentCopy);
    }
}
