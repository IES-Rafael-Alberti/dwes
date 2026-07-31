package es.iesrafaelalberti.dwes.catalogoculturalhibrido.client;

import java.time.Duration;

/**
 * Abstraction over the act of waiting for the throttle interval to elapse.
 *
 * <p>Production code uses {@link #sleeping}, which parks the calling thread.
 * Tests inject a recording or no-op implementation so that throttle spacing is
 * verified with fake time and never actually sleeps.</p>
 */
@FunctionalInterface
public interface ThrottleWait {

    /** Waits for the given duration before a request is released. */
    void waitFor(Duration duration);

    /**
     * Production strategy: {@code Thread.sleep} on the current thread.
     *
     * @throws IllegalStateException if the thread is interrupted while waiting
     */
    static void sleeping(Duration duration) {
        try {
            Thread.sleep(duration);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Throttle wait interrupted", e);
        }
    }
}
