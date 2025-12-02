package com.teamoffour.lms.domain;

import com.teamoffour.lms.domain.bookstates.Available;
import com.teamoffour.lms.domain.bookstates.State;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
public class Book {
    private Long id;
    private String isbn;
    private String title;
    private String author;
    private String category;
    private Integer publicationYear;
    private Integer copiesAvailable;
    private List<Reservation> reservations;
    private List<Transaction> transactions;
    private State currentState;


    public Book() {
        reservations = new ArrayList<>();
        transactions = new ArrayList<>();
        currentState = new Available();
    }

    public boolean isAvailable() {
        return currentState.isAvailable();
    }

    public void incrementCopies() {
        currentState.incrementCopies(this);
    }

    public void decrementCopies() {
        currentState.decrementCopies(this);
    }

    public void addTransaction(Transaction transaction) {
        transactions.add(transaction);
    }

    public void addReservation(Reservation reservation) {
        reservations.add(reservation);
    }

    public void updateState(State state) {
        currentState = state;
    }

    public void updateReservation(Reservation reservation) {
        for (int i = 0; i < reservations.size(); i++) {
            if (reservations.get(i).getId().equals(reservation.getId())) {
                reservations.set(i, reservation);
                return;
            }
        }
    }

    public List<Reservation> getActiveReservation() {
        return reservations.stream().filter(Reservation::isActive).toList();
    }

    public List<Reservation> getQueuedReservations() {
        return reservations.stream().filter(Reservation::isQueued).toList();
    }

    public boolean hasAnyOngoingReservation() {
        return getActiveReservation().size() + getQueuedReservations().size() > 0;
    }


}
