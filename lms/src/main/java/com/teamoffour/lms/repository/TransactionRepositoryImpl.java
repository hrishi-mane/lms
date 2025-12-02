package com.teamoffour.lms.repository;

import com.teamoffour.lms.domain.Transaction;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


@Service
public class TransactionRepositoryImpl implements TransactionRepository {
    private final Map<Long, Transaction> transactions = new HashMap<>();

    @Override
    public void save(Transaction transaction) {
        transactions.put(transaction.getId(), transaction);
    }

    @Override
    public Optional<Transaction> findTransactionById(Long transactionId) {
        return Optional.ofNullable(transactions.get(transactionId));
    }
}
