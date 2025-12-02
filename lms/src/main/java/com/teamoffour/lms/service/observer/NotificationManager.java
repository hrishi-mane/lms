package com.teamoffour.lms.service.observer;

import com.teamoffour.lms.domain.Notification;
import com.teamoffour.lms.service.observer.observers.EmailChannel;
import com.teamoffour.lms.service.observer.observers.InAppChannel;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
public class NotificationManager implements ISubject {
    private List<IObserver> observers;

    public NotificationManager() {
        this.observers = new ArrayList<>();
    }

    @PostConstruct
    public void init() {
        attach(new EmailChannel());
        attach(new InAppChannel());
    }

    @Override
    public void attach(IObserver observer) {
        if (!observers.contains(observer)) {
            observers.add(observer);
        }
    }

    @Override
    public void detach(IObserver observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers(Notification notification) {
        for (IObserver observer : observers) {
            observer.update(notification);
        }
    }
}
