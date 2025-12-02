package com.teamoffour.lms.repository;

import com.teamoffour.lms.domain.Book;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


@Service
public class BookRepositoryImpl implements BookRepository {
    private final Map<Long, Book> books = new HashMap<>();

    @Override
    public Optional<Book> findBookById(Long bookId) {
        return Optional.ofNullable(books.get(bookId));
    }

    @Override
    public Long save(Book book) {
        books.put(book.getId(), book);
        return book.getId();
    }
}
