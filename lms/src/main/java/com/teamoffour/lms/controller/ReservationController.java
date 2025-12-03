package com.teamoffour.lms.controller;

import com.teamoffour.lms.service.ReservationInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.rmi.ServerException;


@RestController
public class ReservationController {

    ReservationInterface reservationInterface;

    @Autowired
    public ReservationController(ReservationInterface reservationInterface) {
        this.reservationInterface = reservationInterface;
    }

    @PostMapping("/lms/reserveBook")
    public String reserveBook(
            @RequestParam("book_id") Long bookId, @RequestParam("member_id") Long memberId) {
        return reservationInterface.reserveBook(bookId, memberId);
    }

    @PostMapping("/lms/processReservationPickup/{reservationId}")
    public String processReservationPickup(@PathVariable Long reservationId) throws ServerException {
        return reservationInterface.processReservationPickup(reservationId);
    }

    @PostMapping("/lms/expireActiveReservations")
    public void expireActiveReservations() {
        reservationInterface.expireActiveReservations();
    }
}
