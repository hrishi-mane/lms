package com.teamoffour.lms.controller;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * Exposes circuit breaker state for demonstration and monitoring.
 * In production this would be replaced by /actuator/circuitbreakers.
 */
@RestController
public class ResilienceController {

    private final CircuitBreakerRegistry circuitBreakerRegistry;

    @Autowired
    public ResilienceController(CircuitBreakerRegistry circuitBreakerRegistry) {
        this.circuitBreakerRegistry = circuitBreakerRegistry;
    }

    /**
     * GET /lms/circuitBreakers
     * Returns the current state of all circuit breakers.
     * States: CLOSED (normal), OPEN (failing — fallback active), HALF_OPEN (recovering)
     */
    @GetMapping("/lms/circuitBreakers")
    public Map<String, Object> getCircuitBreakerStatus() {
        Map<String, Object> status = new HashMap<>();

        circuitBreakerRegistry.getAllCircuitBreakers().forEach(cb -> {
            Map<String, Object> cbInfo = new HashMap<>();
            CircuitBreaker.State state = cb.getState();
            CircuitBreaker.Metrics metrics = cb.getMetrics();

            cbInfo.put("state", state.name());
            cbInfo.put("failureRate", metrics.getFailureRate() + "%");
            cbInfo.put("numberOfBufferedCalls", metrics.getNumberOfBufferedCalls());
            cbInfo.put("numberOfFailedCalls", metrics.getNumberOfFailedCalls());
            cbInfo.put("numberOfSuccessfulCalls", metrics.getNumberOfSuccessfulCalls());
            cbInfo.put("description", getStateDescription(state));

            status.put(cb.getName(), cbInfo);
        });

        if (status.isEmpty()) {
            status.put("message", "No circuit breakers have been invoked yet. " +
                    "Call /lms/borrowBook/ or /lms/processReturn/ to activate them.");
        }

        return status;
    }

    private String getStateDescription(CircuitBreaker.State state) {
        return switch (state) {
            case CLOSED -> "Normal operation — requests passing through";
            case OPEN -> "Circuit OPEN — fallback responses active, requests blocked";
            case HALF_OPEN -> "Recovering — trial requests allowed to test recovery";
            default -> state.name();
        };
    }
}
