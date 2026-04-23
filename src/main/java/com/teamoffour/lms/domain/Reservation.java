package com.teamoffour.lms.domain;

import com.teamoffour.lms.domain.enums.ReservationStatus;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Random;

@Getter
@Setter
@ToString(exclude = {"member", "book"})   // prevent infinite recursion
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Reservation {
    private static final Logger log = LoggerFactory.getLogger(Reservation.class);
    private static final int RESERVATION_VALIDITY_DAYS = 2;

    @EqualsAndHashCode.Include
    private Long id;
    private Member member;
    private Book book;
    private LocalDate reservationDate;
    private LocalDate expiryDate;
    private ReservationStatus status;

    public Reservation(Member member, Book book) {
        this.id = Math.abs(new Random().nextLong() % 900_000_000_000_000L);
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
        return expiryDate != null && LocalDate.now().isAfter(expiryDate);
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
        log.info("Reservation #{} expired", id);
    }

    public long getDaysUntilExpiry() {
        if (expiryDate == null) {
            return 0;
        }
        if (isExpired()) {
            return 0;
        }
        return ChronoUnit.DAYS.between(LocalDate.now(), expiryDate);
    }
}