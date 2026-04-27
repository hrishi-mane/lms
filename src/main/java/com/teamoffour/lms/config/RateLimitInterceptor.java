package com.teamoffour.lms.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
@Slf4j
public class RateLimitInterceptor implements HandlerInterceptor {

    private static final int MAX_REQUESTS_PER_MINUTE = 20;
    private static final long TIME_WINDOW_MS = 60_000L;
    private static final int MAX_TRACKED_CLIENTS = 500; // prevent unbounded growth

    private final ConcurrentHashMap<String, CopyOnWriteArrayList<Long>> requestTimestamps
            = new ConcurrentHashMap<>();

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws IOException {
        String clientId = request.getRemoteAddr();

        if (isRateLimitExceeded(clientId)) {
            response.setStatus(429);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"Rate limit exceeded. Try again later.\"}");
            return false;
        }

        recordRequest(clientId);
        return true;
    }

    private boolean isRateLimitExceeded(String clientId) {
        CopyOnWriteArrayList<Long> timestamps = requestTimestamps.get(clientId);
        if (timestamps == null) return false;

        long cutoff = System.currentTimeMillis() - TIME_WINDOW_MS;
        long recentCount = timestamps.stream().filter(t -> t >= cutoff).count();
        return recentCount >= MAX_REQUESTS_PER_MINUTE;
    }

    private void recordRequest(String clientId) {
        // Evict oldest client if map is getting too large
        if (!requestTimestamps.containsKey(clientId)
                && requestTimestamps.size() >= MAX_TRACKED_CLIENTS) {
            requestTimestamps.keys().nextElement(); // get any key
            String oldest = requestTimestamps.keys().nextElement();
            requestTimestamps.remove(oldest);
        }

        requestTimestamps.computeIfAbsent(clientId, k -> new CopyOnWriteArrayList<>())
                .add(System.currentTimeMillis());
    }

    @Scheduled(fixedRate = 300_000) // every 5 minutes
    public void cleanupOldEntries() {
        long cutoff = System.currentTimeMillis() - TIME_WINDOW_MS;
        requestTimestamps.forEach((clientId, timestamps) ->
                timestamps.removeIf(t -> t < cutoff));
        requestTimestamps.entrySet().removeIf(e -> e.getValue().isEmpty());
        log.debug("Rate limiter cleanup done. Active clients: {}", requestTimestamps.size());
    }
}