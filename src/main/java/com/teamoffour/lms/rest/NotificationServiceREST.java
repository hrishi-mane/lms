package com.teamoffour.lms.rest;


import com.teamoffour.lms.service.dto.NotificationEventDTO;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
public class NotificationServiceREST {
    private final RestTemplate restTemplate;

    @Value("${notification.service.url}")
    private String notificationServiceUrl;

    public NotificationServiceREST(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }


    @Retry(name = "notificationService", fallbackMethod = "publishFallback")
    @Async
    public void publish(NotificationEventDTO event) {
        try {
            restTemplate.postForEntity(
                    notificationServiceUrl + "/notify",
                    event,
                    String.class
            );
        } catch (Exception e) {
            // log and swallow — notification failure should never break borrowing
            log.warn("Notification service unreachable: {}", e.getMessage());
        }
    }

    public void publishFallback(NotificationEventDTO event, Exception ex) {
        log.warn("Notification delivery failed after retries for member {} — reason: {}",
                event.getMemberId(), ex.getMessage());
    }
}
