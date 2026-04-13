package com.teamoffour.lms.controller;

import com.teamoffour.lms.service.TransactionInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.rmi.ServerException;

@RestController
public class TransactionController {
    TransactionInterface transactionInterface;


    @Autowired
    public TransactionController(TransactionInterface transactionInterface) {
        this.transactionInterface = transactionInterface;
    }

    @GetMapping(value = "/lms/borrowBook/")
    public String borrowBook(@RequestParam("book_id") Long bookId, @RequestParam("member_id") Long memberId) throws ServerException {
        return transactionInterface.borrowBook(bookId, memberId);
    }

    @GetMapping(value = "/lms/processReturn/{transactionId}")
    public String returnBook(@PathVariable Long transactionId) {
        return transactionInterface.processReturn(transactionId);
    }


}
