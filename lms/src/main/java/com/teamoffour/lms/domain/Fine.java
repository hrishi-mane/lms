package com.teamoffour.lms.domain;

import lombok.Data;

import java.time.LocalDate;
import java.util.Random;

@Data
public class Fine {
    private Long id;
    private Transaction transaction;
    private Double amount;
    private LocalDate generatedDate;
    private boolean isPaid;

    public Fine(Transaction transaction, Double amount) {
        this.id = (new Random().nextLong() * 1_000_000_0000L);
        this.transaction = transaction;
        this.amount = amount;
        this.generatedDate = LocalDate.now();
    }
}