package com.teamoffour.lms.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
public class RateLimitInterceptor implements HandlerInterceptor {

    private final Map<String, List<Long>> requestTimestamps = new ConcurrentHashMap<>();
    private static final int MAX_REQUESTS_PER_MINUTE = 20;
    private static final long TIME_WINDOW_MS = 60000; // 1 minute

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws IOException {
        String clientId = request.getRemoteAddr();

        if (isRateLimitExceeded(clientId)) {
            response.setStatus(429); // Too Many Requests
            response.setContentType("application/json");
            response.getWriter().write(
                    "{\"error\": \"Rate limit exceeded. Try again later.\"}"
            );
            return false; // Block request
        }

        recordRequest(clientId); // ✅ Record this request
        return true; // Allow request
    }

    private boolean isRateLimitExceeded(String clientId) {
        List<Long> timestamps = requestTimestamps.getOrDefault(
                clientId,
                new ArrayList<>()
        );

        // Remove timestamps older than 1 minute
        long oneMinuteAgo = System.currentTimeMillis() - TIME_WINDOW_MS;
        timestamps.removeIf(timestamp -> timestamp < oneMinuteAgo);

        // Check if limit exceeded
        return timestamps.size() >= MAX_REQUESTS_PER_MINUTE;
    }

    private void recordRequest(String clientId) {
        List<Long> timestamps = requestTimestamps.computeIfAbsent(
                clientId,
                k -> new ArrayList<>()
        );

        timestamps.add(System.currentTimeMillis());

        // Optional: Log rate limit usage
        log.debug("Client {} - Request count in last minute: {}",
                clientId, timestamps.size());
    }


    @Scheduled(fixedRate = 300000) // Run every 5 minutes
    public void cleanupOldEntries() {
        long fiveMinutesAgo = System.currentTimeMillis() - 300000;

        requestTimestamps.forEach((clientId, timestamps) -> {
            timestamps.removeIf(timestamp -> timestamp < fiveMinutesAgo);
        });

        // Remove empty entries
        requestTimestamps.entrySet().removeIf(
                entry -> entry.getValue().isEmpty()
        );

        log.info("Rate limiter cleanup completed. Active clients: {}",
                requestTimestamps.size());
    }
}