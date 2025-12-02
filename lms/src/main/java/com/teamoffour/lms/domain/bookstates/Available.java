package com.teamoffour.lms.domain.bookstates;

import com.teamoffour.lms.domain.Book;
import com.teamoffour.lms.domain.Transaction;

public class Available implements State {

    @Override
    public boolean isAvailable() {
        return true;
    }

    @Override
    public String getStateName() {
        return "Available";
    }

    @Override
    public void incrementCopies(Book book) {
        book.setCopiesAvailable(book.getCopiesAvailable() + 1);
    }

    @Override
    public void decrementCopies(Book book) {
        int availableCopies =  book.getCopiesAvailable() - 1;
        if (availableCopies <= 0) {
            book.setCurrentState(new Unavailable());
        }

        book.setCopiesAvailable(availableCopies);
    }

    @Override
    public void addTransaction(Transaction transaction, Book book) {
        book.addTransaction(transaction);
    }
}
