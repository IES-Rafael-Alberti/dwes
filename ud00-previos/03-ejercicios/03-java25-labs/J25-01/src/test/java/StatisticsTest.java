import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class StatisticsTest {
    @Test
    void summarizesValues() {
        var result = Statistics.summarize(List.of(2.0, 4.0, 9.0));

        assertEquals(3, result.count());
        assertEquals(2.0, result.min());
        assertEquals(9.0, result.max());
        assertEquals(5.0, result.average());
    }

    @Test
    void rejectsEmptyInput() {
        assertThrows(IllegalArgumentException.class,
                () -> Statistics.summarize(List.of()));
    }

    @Test
    void rejectsNonFiniteValues() {
        assertThrows(IllegalArgumentException.class,
                () -> Statistics.summarize(List.of(1.0, Double.NaN)));
    }
}
