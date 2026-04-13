package com.teamoffour.lms.domain;

import com.teamoffour.lms.domain.enums.NotificationType;
import lombok.Getter;

import java.util.Date;


@Getter
public class Notification {
    private Long id;
    private String message;
    private Date sentDate;
    private Member member;
    private NotificationType type;

    public Notification(Member member, String message, NotificationType type) {
        this.member = member;
        this.message = message;
        this.type = type;
    }
}
