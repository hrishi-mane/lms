package com.teamoffour.lms.domain;


import com.teamoffour.lms.domain.enums.TransactionStatus;
import com.teamoffour.lms.service.strategy.IMembershipPlan;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


@Getter
@Setter
public class Member{
    private Long id;
    private String userName;
    private String password;
    private String emailId;
    private String phoneNumber;
    private LocalDate membershipStartDate;
    private LocalDate membershipEndDate;
    private IMembershipPlan membershipPlan;
    private List<Transaction> transactions;
    private List<Reservation> reservations;
    private List<Notification> notifications;

    public Member() {
        this.id = Long.valueOf(String.valueOf(new Random().nextLong() * 1_000_000_0000L));
        transactions = new ArrayList<>();
        reservations = new ArrayList<>();
        notifications = new ArrayList<>();
    }

    public void addTransaction(Transaction transaction) {
        transactions.add(transaction);
    }

    public void addReservation(Reservation reservation) {
        this.reservations.add(reservation);
    }

    public int getActiveReservationCount() {
        return (int) reservations.stream()
                .filter(Reservation::isActive)
                .count();
    }

    public int getQueuedReservationCount() {
        return (int) reservations.stream()
                .filter(Reservation::isQueued)
                .count();
    }

    public boolean canReserve() {
        // Limit based on membership plan
        int reservationLimit = membershipPlan.getMaxReservationsAllowed();
        return getActiveReservationCount() + getQueuedReservationCount() < reservationLimit;
    }

    public boolean hasReservationFor(Book book) {
        return reservations.stream()
                .anyMatch(r -> r.isActive() && r.getBook().getId().equals(book.getId())
                || r.isQueued() && r.getBook().getId().equals(book.getId()));
    }


    public int getActiveBorrowCount() {
        return getActiveTransaction().size();
    }

    public List<Transaction> getActiveTransaction() {
        return transactions.stream()
                .filter(t -> t.getTransactionStatus() == TransactionStatus.ACTIVE).toList();
    }

    public boolean canBorrow() {
        return getActiveBorrowCount() < membershipPlan.getBorrowingPolicy().getBorrowingLimit();
    }

    public void receiveNotification(Notification notification) {
        notifications.add(notification);
    }


    public void updateReservation(Reservation reservation) {
        for (int i = 0; i < reservations.size(); i++) {
            if (reservations.get(i).getId().equals(reservation.getId())) {
                reservations.set(i, reservation);
                return;
            }
        }
    }
}
