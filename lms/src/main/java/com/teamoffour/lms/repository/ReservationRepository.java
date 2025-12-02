package com.teamoffour.lms.repository;

import com.teamoffour.lms.domain.Reservation;

import java.util.List;
import java.util.Optional;

public interface ReservationRepository {
    List<Reservation> findAll();
    Long save(Reservation reservation);
    Optional<Reservation> findReservationById(Long reservationId);
}
