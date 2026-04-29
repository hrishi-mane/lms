package com.teamoffour.lms.service;

import com.teamoffour.lms.service.dto.ReservationDTO;

import java.util.List;

public interface ReservationInterface {
    String reserveBook(Long bookId, Long memberId);

    String processReservationPickup(Long reservationId);

    void expireActiveReservations();

    List<ReservationDTO> getAllReservations();
}
