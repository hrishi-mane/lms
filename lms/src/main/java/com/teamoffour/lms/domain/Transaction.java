package com.teamoffour.lms.domain;

import com.teamoffour.lms.domain.enums.TransactionStatus;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Getter
public class Transaction {
    private final Long id;
    private final LocalDateTime borrowDate;
    private LocalDateTime returnedDate;
    private TransactionStatus transactionStatus;
    private final Member member;
    private final Book book;


    public Transaction(Member member, Book book) {
        id = (long) (Math.random() * 1_000_000_0000L);
        this.borrowDate = LocalDateTime.now();
        this.transactionStatus = TransactionStatus.ACTIVE;
        this.member = member;
        this.book = book;
    }

    public LocalDateTime calculateDueDate() {
        return member.getMembershipPlan().getBorrowingPolicy().calculateDueDate(borrowDate);
    }

    public boolean isOverdue() {
        return calculateDueDate().isBefore(LocalDateTime.now());
    }

    public long getDaysOverdue() {
        if (isOverdue()) {
            return ChronoUnit.DAYS.between(calculateDueDate(), LocalDate.now());
        }

        return 0;
    }

    public Fine calculateFine() {
        if (!isOverdue()) {
            return null;
        }

        double fineAmount = member.getMembershipPlan().getBorrowingPolicy().calculateFine(borrowDate);

        if (fineAmount > 0) {
            Fine fine = new Fine(this, fineAmount);

            System.out.println("Fine calculated: $" + fineAmount + " for transaction ID " + id);
            transactionStatus = TransactionStatus.FINE_PENDING;
            return fine;
        }

        return null;

    }

    public void markReturned() {
        this.transactionStatus = TransactionStatus.RETURNED;
    }

    public void updateReturnedDate(LocalDateTime returnedDate) {
        this.returnedDate = returnedDate;
    }

    public boolean isActive() {
        return TransactionStatus.ACTIVE.equals(transactionStatus);
    }

}
