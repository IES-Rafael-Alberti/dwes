package com.example.battleship.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class RateLimitFilter extends OncePerRequestFilter {

    private final Map<String, List<Instant>> requests = new ConcurrentHashMap<>();
    private static final int DEFAULT_MAX_REQUESTS = 100;
    private static final long WINDOW_MS = 60_000;
    private final SecurityErrorWriter errorWriter;
    private final int maxRequests;

    public RateLimitFilter(SecurityErrorWriter errorWriter) {
        this(errorWriter, DEFAULT_MAX_REQUESTS);
    }

    RateLimitFilter(SecurityErrorWriter errorWriter, int maxRequests) {
        this.errorWriter = errorWriter;
        this.maxRequests = maxRequests;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                     HttpServletResponse response,
                                     FilterChain filterChain) throws ServletException, IOException {
        String ip = request.getRemoteAddr();
        Instant now = Instant.now();
        var timestamps = requests.computeIfAbsent(ip, k -> new CopyOnWriteArrayList<>());

        timestamps.removeIf(t -> t.isBefore(now.minusMillis(WINDOW_MS)));
        timestamps.add(now);

        if (timestamps.size() > maxRequests) {
            errorWriter.write(response, 429, "TOO_MANY_REQUESTS", "Rate limit exceeded");
            return;
        }

        filterChain.doFilter(request, response);
    }
}
