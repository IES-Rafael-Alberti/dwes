import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class OutcomeTextTest {
    @Test
    void describesSuccess() {
        assertEquals("OK: catalog", OutcomeText.describe(new Success("catalog")));
    }

    @Test
    void describesFailure() {
        assertEquals("ERROR DB-001: database unavailable",
                OutcomeText.describe(new Failure("DB-001", "database unavailable")));
    }
}
