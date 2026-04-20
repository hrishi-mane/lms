package com.teamoffour.lms.service;

import com.teamoffour.lms.domain.Transaction;

import java.rmi.ServerException;
import java.util.List;

public interface TransactionInterface {
    String borrowBook(Long bookId, Long memberId) throws ServerException;

    String processReturn(Long transactionId);

    List<Transaction> getAllTransactions();

}
