package com.teamoffour.lms.service.observer;

import com.teamoffour.lms.domain.Notification;

public interface ISubject {
    void attach(IObserver observer);
    void detach(IObserver observer);
    void notifyObservers(Notification notification);
}
