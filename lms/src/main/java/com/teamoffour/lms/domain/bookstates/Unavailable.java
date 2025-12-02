package com.teamoffour.lms.domain.bookstates;

import com.teamoffour.lms.domain.Book;
import com.teamoffour.lms.domain.Transaction;

public class Unavailable implements State {


    @Override
    public boolean isAvailable() {
        return false;
    }

    @Override
    public String getStateName() {
        return "Unavailable";
    }

    @Override
    public void addTransaction(Transaction transaction, Book book) {
        throw new UnsupportedOperationException("Cannot process transaction for unavailable book.");
    }

    @Override
    public void incrementCopies(Book book) {
        int availableCopies =  book.getCopiesAvailable() + 1;
        book.setCopiesAvailable(availableCopies);
    }

    @Override
    public void decrementCopies(Book book) {
        throw new  UnsupportedOperationException("Cannot decrement copies of book that is not available");
    }
}
