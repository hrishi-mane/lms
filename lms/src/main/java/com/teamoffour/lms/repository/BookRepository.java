package com.teamoffour.lms.repository;


import com.teamoffour.lms.domain.Book;

import java.util.Optional;

public interface BookRepository {
    Optional<Book> findBookById(Long bookId);

    Long save(Book book);
}
