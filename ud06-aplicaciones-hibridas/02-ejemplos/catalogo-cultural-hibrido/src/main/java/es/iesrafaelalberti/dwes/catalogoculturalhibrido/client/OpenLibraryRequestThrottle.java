package es.iesrafaelalberti.dwes.catalogoculturalhibrido.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Objects;

/**
 * {@link RequestThrottle} that enforces roughly one request per second, matching
 * the conservative Open Library limit for unauthenticated clients.
 *
 * <p><strong>Testable seams.</strong> Both the {@link Clock} and the actual
 * waiting behaviour ({@link ThrottleWait}) are injected. Production uses the
 * system clock and {@code Thread.sleep}; tests inject a mutable fake clock and a
 * recording wait so the spacing logic is proven deterministically without ever
 * sleeping.</p>
 */
@Component
public class OpenLibraryRequestThrottle implements RequestThrottle {

    /** Conservative Open Library limit for unauthenticated clients. */
    public static final Duration DEFAULT_MIN_INTERVAL = Duration.ofSeconds(1);

    private final Clock clock;
    private final Duration minInterval;
    private final ThrottleWait wait;

    private volatile Instant lastAcquire = Instant.EPOCH;

    @Autowired
    public OpenLibraryRequestThrottle() {
        this(Clock.systemUTC(), DEFAULT_MIN_INTERVAL, ThrottleWait::sleeping);
    }

    /** Test seam: package-private so tests build the throttle with fake time. */
    OpenLibraryRequestThrottle(Clock clock, Duration minInterval, ThrottleWait wait) {
        this.clock = Objects.requireNonNull(clock, "clock");
        this.minInterval = Objects.requireNonNull(minInterval, "minInterval");
        this.wait = Objects.requireNonNull(wait, "wait");
        if (minInterval.isZero() || minInterval.isNegative()) {
            throw new IllegalArgumentException("minInterval must be positive, got: " + minInterval);
        }
    }

    @Override
    public synchronized void acquire() {
        Instant now = clock.instant();
        Instant nextAllowed = lastAcquire.plus(minInterval);
        if (now.isBefore(nextAllowed)) {
            Duration remaining = Duration.between(now, nextAllowed);
            wait.waitFor(remaining);
            lastAcquire = clock.instant();
        } else {
            lastAcquire = now;
        }
    }
}
