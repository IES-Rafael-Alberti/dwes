import java.util.List;

public final class Statistics {
    private Statistics() {
    }

    public record Summary(int count, double min, double max, double average) {
    }

    public static Summary summarize(List<Double> values) {
        throw new UnsupportedOperationException("Completa J25-01");
    }
}
