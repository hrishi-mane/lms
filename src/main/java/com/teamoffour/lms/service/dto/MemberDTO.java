package com.teamoffour.lms.service.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class MemberDTO {
    private Long id;
    private String userName;
    private String emailId;
    private String phoneNumber;
    private LocalDate membershipStartDate;
    private LocalDate membershipEndDate;
    private String planName;
    private int activeBorrowCount;
    private int activeReservationCount;
    private int queuedReservationCount;
}
