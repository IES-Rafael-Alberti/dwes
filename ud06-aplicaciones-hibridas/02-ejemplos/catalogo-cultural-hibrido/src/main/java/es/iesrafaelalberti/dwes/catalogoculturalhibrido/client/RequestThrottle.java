package es.iesrafaelalberti.dwes.catalogoculturalhibrido.client;

/**
 * Throttles outbound provider calls to a conservative minimum interval.
 *
 * <p>The throttle is deliberately conservative: it never lets two requests
 * through closer than the configured interval, whatever the concurrency. It is
 * meant to be acquired <em>before</em> each uncached provider call, and never on
 * cache hits (a cache hit must not consume a request slot).</p>
 */
public interface RequestThrottle {

    /**
     * Blocks until the minimum interval since the previous release has elapsed.
     *
     * <p>The first call never blocks. Subsequent calls block for exactly the
     * remaining part of the interval when it has not fully elapsed.</p>
     */
    void acquire();
}
