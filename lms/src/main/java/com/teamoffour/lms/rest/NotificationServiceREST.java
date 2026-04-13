package com.teamoffour.lms.rest;


import com.teamoffour.lms.service.dto.NotificationEventDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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
}
