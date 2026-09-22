import java.util.List;

public final class VarAndSwitchDemo {
    private VarAndSwitchDemo() {
    }

    public static void run() {
        var statuses = List.of(200, 404, 503);

        for (int status : statuses) {
            System.out.println(status + " -> " + labelFor(status));
        }
    }

    private static String labelFor(int status) {
        return switch (status) {
            case 200, 201 -> "correcto";
            case 400, 404 -> "error del cliente";
            default -> "otro estado";
        };
    }
}
