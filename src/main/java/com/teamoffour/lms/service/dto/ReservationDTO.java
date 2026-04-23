package com.teamoffour.lms.service.dto;

import com.teamoffour.lms.domain.enums.ReservationStatus;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ReservationDTO {
    private Long id;
    private Long bookId;
    private String bookTitle;
    private String bookIsbn;
    private Long memberId;
    private String memberUserName;
    private LocalDate reservationDate;
    private LocalDate expiryDate;
    private ReservationStatus status;
    private long daysUntilExpiry;
}
