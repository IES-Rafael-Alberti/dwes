import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TemperatureTest {
    @Test
    void convertsToFahrenheit() {
        assertEquals(212.0, new Temperature(100).fahrenheit());
    }

    @Test
    void rejectsBelowAbsoluteZero() {
        assertThrows(IllegalArgumentException.class,
                () -> new Temperature(-273.16));
    }
}
