package com.teamoffour.lms.domain.bookstates;

import com.teamoffour.lms.domain.Book;
import com.teamoffour.lms.domain.Transaction;

public interface State {

    void incrementCopies(Book book);

    void decrementCopies(Book book);

    boolean isAvailable();

    String getStateName();

    void addTransaction(Transaction transaction, Book book);

}
