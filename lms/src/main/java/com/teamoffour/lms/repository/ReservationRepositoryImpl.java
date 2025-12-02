package com.teamoffour.lms.repository;

import com.teamoffour.lms.domain.Reservation;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ReservationRepositoryImpl implements ReservationRepository {
    private final Map<Long, Reservation> reservations = new HashMap<>();

    @Override
    public List<Reservation> findAll() {
        return new ArrayList<>(reservations.values());
    }

    @Override
    public Long save(Reservation reservation) {
        reservations.put(reservation.getId(), reservation);
        return reservation.getId();
    }

    @Override
    public Optional<Reservation> findReservationById(Long id) {
        return Optional.ofNullable(reservations.get(id));
    }
}
