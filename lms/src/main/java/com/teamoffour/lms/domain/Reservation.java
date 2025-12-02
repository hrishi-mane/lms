package com.teamoffour.lms.domain;

import com.teamoffour.lms.domain.enums.ReservationStatus;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
public class Reservation {
    private final Long id;
    private final Member member;
    private final Book book;
    private final LocalDate reservationDate;
    private LocalDate expiryDate;
    private ReservationStatus status;

    // Configuration: Reservation valid for 7 days
    private static final int RESERVATION_VALIDITY_DAYS = 2;

    public Reservation(Member member, Book book) {
        this.id = Long.valueOf(String.valueOf((long)(Math.random() * 1_000_000_0000L)));
        this.member = member;
        this.book = book;
        this.reservationDate = LocalDate.now();
        this.status = ReservationStatus.QUEUED;
    }

    public void activate() {
        if (status != ReservationStatus.QUEUED) {
            throw new IllegalStateException(
                    "Can only activate QUEUED reservations. Current status: " + status);
        }

        this.status = ReservationStatus.ACTIVE;
        this.expiryDate = LocalDateTime.now().plusDays(RESERVATION_VALIDITY_DAYS).toLocalDate();

    }

    public boolean isExpired() {
        return LocalDate.now().isAfter(expiryDate);
    }

    public boolean isActive() {
        return status == ReservationStatus.ACTIVE;
    }

    public boolean isQueued() {
        return status == ReservationStatus.QUEUED;
    }


    public void fulfill() {
        if (status != ReservationStatus.ACTIVE) {
            throw new IllegalStateException(
                    "Can only fulfill ACTIVE reservations. Current status: " + status);
        }

        this.status = ReservationStatus.FULFILLED;
    }

    public void expire() {
        if (status != ReservationStatus.ACTIVE) {
            throw new IllegalStateException(
                    "Can only expire ACTIVE reservations. Current status: " + status);
        }

        this.status = ReservationStatus.EXPIRED;

        System.out.println("⏰ Reservation #" + id + " expired");
    }


    public long getDaysUntilExpiry() {
        if (isExpired()) {
            return 0;
        }
        return java.time.temporal.ChronoUnit.DAYS.between(LocalDate.now(), expiryDate);
    }
}