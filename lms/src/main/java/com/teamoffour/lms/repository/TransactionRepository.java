package com.teamoffour.lms.repository;

import com.teamoffour.lms.domain.Transaction;

import java.util.Optional;

public interface TransactionRepository {
    void save(Transaction transaction);
    Optional<Transaction> findTransactionById(Long transactionId);
}
