package com.teamoffour.lms.service.observer;

import com.teamoffour.lms.domain.Notification;

public interface IObserver {
    void update (Notification notification);

}
