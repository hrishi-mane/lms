package com.teamoffour.lms.service.observer;

import com.teamoffour.lms.domain.Notification;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class NotificationManager implements ISubject {
    private final List<IObserver> observers;

    public NotificationManager(List<IObserver> observers) {
        this.observers = observers;
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
