package com.teamoffour.lms.domain;

import lombok.Getter;

import java.time.LocalDate;

@Getter

public class Fine {
    private final Long id;
    private final Transaction transaction;
    private final Double amount;
    private final LocalDate generatedDate;
    private boolean isPaid;

    public Fine(Transaction transaction,  Double amount) {
        this.id = (long) (Math.random() * 1_000_000_0000L);
        this.transaction = transaction;
        this.amount = amount;
        this.generatedDate = LocalDate.now();
    }

    private void markPaid() {
        this.isPaid = true;
    }
}
