package com.teamoffour.lms.domain;

import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;


@Getter
public class BorrowingPolicy {
    private final Integer borrowingLimit;
    private final Double fineRatePerDay;
    private final Integer borrowingDurationInDays;

    public BorrowingPolicy(Integer borrowingLimit, Double fineRatePerDay, Integer borrowingDurationInDays) {
        this.borrowingLimit = borrowingLimit;
        this.fineRatePerDay = fineRatePerDay;
        this.borrowingDurationInDays = borrowingDurationInDays;
    }

    public LocalDateTime calculateDueDate(LocalDateTime borrowedDate) {
        if (borrowedDate == null) {
            throw new IllegalArgumentException("Borrowed date cannot be null");
        }
        return borrowedDate.plusDays(borrowingDurationInDays);
    }


    public double calculateFine(LocalDateTime borrowedDate) {
        long daysOverdue = ChronoUnit.DAYS.between(borrowedDate, LocalDate.now());
        long chargeableDays = Math.max(0, daysOverdue);

        return chargeableDays * fineRatePerDay;
    }

}
