// src/main/java/com/teamoffour/lms/config/NotificationServiceHealthIndicator.java
package com.teamoffour.lms.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class NotificationServiceHealthIndicator implements HealthIndicator {

    private final RestTemplate restTemplate;

    @Value("${notification.service.url}")
    private String notificationServiceUrl;

    public NotificationServiceHealthIndicator(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public Health health() {
        try {
            restTemplate.getForEntity(notificationServiceUrl + "/actuator/health", String.class);
            return Health.up()
                    .withDetail("notificationService", "reachable")
                    .build();
        } catch (Exception e) {
            return Health.down()
                    .withDetail("notificationService", "unreachable")
                    .withDetail("reason", e.getMessage())
                    .build();
        }
    }
}