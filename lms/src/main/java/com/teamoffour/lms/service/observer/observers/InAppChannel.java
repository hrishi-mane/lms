package com.teamoffour.lms.service.observer.observers;

import com.teamoffour.lms.domain.Notification;
import com.teamoffour.lms.service.observer.IObserver;
import org.springframework.stereotype.Service;


@Service
public class InAppChannel implements IObserver {
    @Override
    public void update(Notification notification) {
        System.out.println("   Message: " + notification.getMessage());

        // Store in member's notification list so that all the notifications can be viewed from the
        // app itself.
        notification.getMember().receiveNotification(notification);
    }
}
