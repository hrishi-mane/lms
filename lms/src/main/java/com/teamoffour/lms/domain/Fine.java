package com.teamoffour.lms.domain;

import lombok.Getter;

import java.time.LocalDate;
import java.util.Random;

@Getter

public class Fine {
    private final Long id;
    private final Transaction transaction;
    private final Double amount;
    private final LocalDate generatedDate;
    private boolean isPaid;

    public Fine(Transaction transaction,  Double amount) {
        this.id = (new Random().nextLong() * 1_000_000_0000L);
        this.transaction = transaction;
        this.amount = amount;
        this.generatedDate = LocalDate.now();
    }


}
