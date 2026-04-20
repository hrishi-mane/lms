package com.teamoffour.lms.repository;

import com.teamoffour.lms.domain.Transaction;
import org.springframework.stereotype.Service;

import java.util.*;


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

    @Override
    public List<Transaction> findAll() {
        return new ArrayList<>(transactions.values());
    }
}
