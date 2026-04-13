package com.teamoffour.lms.service;

import java.rmi.ServerException;

public interface TransactionInterface {
    String borrowBook(Long bookId, Long memberId) throws ServerException;

    String processReturn(Long transactionId);

}
