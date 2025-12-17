package com.teamoffour.lms.domain;

import com.teamoffour.lms.domain.enums.TransactionStatus;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Random;

@Data
public class Transaction {
    private static final Logger log = LoggerFactory.getLogger(Transaction.class);

    private Long id;
    private LocalDateTime borrowDate;
    private LocalDateTime returnedDate;
    private TransactionStatus transactionStatus;
    private Member member;
    private Book book;

    public Transaction(Member member, Book book) {
        id = (new Random().nextLong() * 1_000_000_0000L);
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
            log.info("Fine calculated: ${} for transaction ID {}", fineAmount, id);
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