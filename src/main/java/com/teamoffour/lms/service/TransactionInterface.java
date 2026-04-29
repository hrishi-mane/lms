package com.teamoffour.lms.service;

import com.teamoffour.lms.service.dto.TransactionDTO;

import java.util.List;

public interface TransactionInterface {
    String borrowBook(Long bookId, Long memberId);

    String processReturn(Long transactionId);

    List<TransactionDTO> getAllTransactions();

}
