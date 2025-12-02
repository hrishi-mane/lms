package com.teamoffour.lms.service;

import java.rmi.ServerException;

public interface ReservationInterface {
    String reserveBook(Long bookId, Long memberId);
    String processReservationPickup(Long reservationId) throws ServerException;
    void expireActiveReservations();
}
