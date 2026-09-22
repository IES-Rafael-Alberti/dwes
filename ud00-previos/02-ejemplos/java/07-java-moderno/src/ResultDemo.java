import java.util.List;

public final class ResultDemo {
    private ResultDemo() {
    }

    public static void run() {
        List<Result> results = List.of(
            new Success("Pedido guardado"),
            new Success(""),
            new Failure("El correo ya existe")
        );

        for (Result result : results) {
            System.out.println(describe(result));
        }
    }

    private static String describe(Result result) {
        return switch (result) {
            case Success(String value) when value.isBlank() -> "Correcto, sin mensaje";
            case Success(String value) -> "Correcto: " + value;
            case Failure(String message) -> "Error: " + message;
        };
    }
}
