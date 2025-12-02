package com.teamoffour.lms.service.observer.observers;

import com.teamoffour.lms.domain.Notification;
import com.teamoffour.lms.service.observer.IObserver;
import org.springframework.stereotype.Service;


@Service
public class EmailChannel implements IObserver {


    @Override
    public void update(Notification notification) {
        System.out.println("📧 EMAIL sent to: " + notification.getMember().getEmailId());
        System.out.println("   Subject: Library Notification");
        System.out.println("   Body: " + notification.getMessage());
    }
}
