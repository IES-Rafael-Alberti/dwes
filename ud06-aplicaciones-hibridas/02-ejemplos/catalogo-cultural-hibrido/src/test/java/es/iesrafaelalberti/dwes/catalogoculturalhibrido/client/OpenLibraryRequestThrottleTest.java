package es.iesrafaelalberti.dwes.catalogoculturalhibrido.client;

import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Deterministic tests for {@link OpenLibraryRequestThrottle} spacing.
 *
 * <p>Both seams are fakes: a mutable {@link Clock} and a wait that records the
 * requested durations instead of sleeping. The interruption test enters the
 * production sleeping strategy but interrupts it immediately; no test waits for
 * a real throttle interval or depends on the wall clock.</p>
 */
class OpenLibraryRequestThrottleTest {

    private static final Duration INTERVAL = Duration.ofSeconds(1);

    @Test
    void shouldNotWaitForTheFirstRequest() {
        MutableClock clock = new MutableClock(Instant.parse("2026-07-31T12:00:00Z"));
        List<Duration> waits = new ArrayList<>();

        OpenLibraryRequestThrottle throttle = new OpenLibraryRequestThrottle(clock, INTERVAL, waits::add);

        throttle.acquire();

        assertTrue(waits.isEmpty(), "the first request must never be delayed");
    }

    @Test
    void shouldWaitTheFullIntervalForAnImmediateSecondRequest() {
        MutableClock clock = new MutableClock(Instant.parse("2026-07-31T12:00:00Z"));
        List<Duration> waits = new ArrayList<>();

        OpenLibraryRequestThrottle throttle = new OpenLibraryRequestThrottle(clock, INTERVAL, waits::add);

        throttle.acquire();
        throttle.acquire();

        assertEquals(List.of(Duration.ofSeconds(1)), waits,
                "two back-to-back requests must be spaced exactly the minimum interval");
    }

    @Test
    void shouldWaitOnlyTheRemainingTimeWhenTheIntervalHasPartiallyElapsed() {
        MutableClock clock = new MutableClock(Instant.parse("2026-07-31T12:00:00Z"));
        List<Duration> waits = new ArrayList<>();

        OpenLibraryRequestThrottle throttle = new OpenLibraryRequestThrottle(clock, INTERVAL, waits::add);

        throttle.acquire();
        clock.advance(Duration.ofMillis(400));
        throttle.acquire();

        assertEquals(List.of(Duration.ofMillis(600)), waits,
                "only the remaining 600 ms must be waited after 400 ms have elapsed");
    }

    @Test
    void shouldSpaceTheNextCallerFromTheActualReleaseAfterWaitOversleep() {
        MutableClock clock = new MutableClock(Instant.parse("2026-07-31T12:00:00Z"));
        List<Duration> waits = new ArrayList<>();
        OpenLibraryRequestThrottle throttle = new OpenLibraryRequestThrottle(clock, INTERVAL, duration -> {
            waits.add(duration);
            clock.advance(waits.size() == 1 ? duration.plusMillis(500) : duration);
        });

        throttle.acquire();
        throttle.acquire();
        throttle.acquire();

        assertEquals(List.of(INTERVAL, INTERVAL), waits,
                "the next caller must wait a full interval from the overslept permit release");
    }

    @Test
    void shouldNotWaitOnceTheIntervalHasFullyElapsed() {
        MutableClock clock = new MutableClock(Instant.parse("2026-07-31T12:00:00Z"));
        List<Duration> waits = new ArrayList<>();

        OpenLibraryRequestThrottle throttle = new OpenLibraryRequestThrottle(clock, INTERVAL, waits::add);

        throttle.acquire();
        clock.advance(Duration.ofMillis(1500));
        throttle.acquire();

        assertTrue(waits.isEmpty(), "a request after 1.5 s must not be throttled");
    }

    @Test
    void shouldSpaceConcurrentCallersUsingVirtualTime() throws Exception {
        Instant initialTime = Instant.parse("2026-07-31T12:00:00Z");
        MutableClock clock = new MutableClock(initialTime);
        List<Duration> waits = new ArrayList<>();
        Map<Thread, Instant> permitTimesByCaller = new ConcurrentHashMap<>();
        OpenLibraryRequestThrottle throttle = new OpenLibraryRequestThrottle(clock, INTERVAL, duration -> {
            waits.add(duration);
            clock.advance(duration);
            permitTimesByCaller.put(Thread.currentThread(), clock.instant());
        });
        CountDownLatch start = new CountDownLatch(1);
        ExecutorService callers = Executors.newFixedThreadPool(3);

        try {
            List<Future<Instant>> permits = new ArrayList<>();
            for (int i = 0; i < 3; i++) {
                permits.add(callers.submit(() -> {
                    start.await();
                    throttle.acquire();
                    return permitTimesByCaller.getOrDefault(Thread.currentThread(), initialTime);
                }));
            }

            start.countDown();
            List<Instant> permitTimes = new ArrayList<>();
            for (Future<Instant> permit : permits) {
                permitTimes.add(permit.get(1, TimeUnit.SECONDS));
            }
            permitTimes.sort(Comparator.naturalOrder());

            assertEquals(List.of(INTERVAL, INTERVAL), waits);
            assertTrue(Duration.between(permitTimes.get(0), permitTimes.get(1)).compareTo(INTERVAL) >= 0);
            assertTrue(Duration.between(permitTimes.get(1), permitTimes.get(2)).compareTo(INTERVAL) >= 0);
        } finally {
            callers.shutdownNow();
        }
    }

    @Test
    void shouldPreserveInterruptionAndReleaseTheMonitorForTheNextCaller() throws Exception {
        Duration longInterval = Duration.ofSeconds(30);
        MutableClock clock = new MutableClock(Instant.parse("2026-07-31T12:00:00Z"));
        OpenLibraryRequestThrottle throttle = new OpenLibraryRequestThrottle(
                clock, longInterval, ThrottleWait::sleeping);
        AtomicReference<RuntimeException> failure = new AtomicReference<>();
        AtomicBoolean interruptPreserved = new AtomicBoolean();

        throttle.acquire();
        Thread waitingCaller = new Thread(() -> {
            try {
                throttle.acquire();
            } catch (RuntimeException exception) {
                failure.set(exception);
                interruptPreserved.set(Thread.currentThread().isInterrupted());
            }
        }, "throttle-interruption-test");

        waitingCaller.start();
        long waitingDeadline = System.nanoTime() + TimeUnit.SECONDS.toNanos(1);
        while (waitingCaller.getState() != Thread.State.TIMED_WAITING
                && System.nanoTime() < waitingDeadline) {
            Thread.onSpinWait();
        }
        boolean reachedSleepingWait = waitingCaller.getState() == Thread.State.TIMED_WAITING;

        waitingCaller.interrupt();
        waitingCaller.join(TimeUnit.SECONDS.toMillis(1));

        assertTrue(reachedSleepingWait,
                "the caller must reach ThrottleWait.sleeping before interruption");
        assertFalse(waitingCaller.isAlive(), "interruption must terminate the waiting path promptly");
        assertNotNull(failure.get());
        assertInstanceOf(IllegalStateException.class, failure.get());
        assertInstanceOf(InterruptedException.class, failure.get().getCause());
        assertTrue(interruptPreserved.get(), "ThrottleWait.sleeping must restore the interrupt flag");

        clock.advance(longInterval);
        ExecutorService subsequentCaller = Executors.newSingleThreadExecutor();
        try {
            subsequentCaller.submit(throttle::acquire).get(1, TimeUnit.SECONDS);
        } finally {
            subsequentCaller.shutdownNow();
        }
    }

    /** Test double: a {@link Clock} whose reading the test controls. */
    static final class MutableClock extends Clock {

        private Instant now;

        MutableClock(Instant now) {
            this.now = now;
        }

        synchronized void advance(Duration duration) {
            now = now.plus(duration);
        }

        @Override
        public ZoneId getZone() {
            return ZoneOffset.UTC;
        }

        @Override
        public Clock withZone(ZoneId zone) {
            return new MutableClock(now);
        }

        @Override
        public synchronized Instant instant() {
            return now;
        }
    }
}
