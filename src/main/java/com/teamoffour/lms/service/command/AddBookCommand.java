package com.teamoffour.lms.service.command;

import com.teamoffour.lms.domain.Book;
import com.teamoffour.lms.mapper.BookMapper;
import com.teamoffour.lms.repository.BookRepository;
import com.teamoffour.lms.service.dto.AddBookRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AddBookCommand implements ICommand {
    private static final Logger log = LoggerFactory.getLogger(AddBookCommand.class);

    private final BookRepository bookRepository;
    private final BookMapper bookMapper;
    private final AddBookRequest addBookRequest;
    private Long bookId;
    private Book book;

    public AddBookCommand(BookRepository bookRepository,
                          BookMapper bookMapper,
                          AddBookRequest addBookRequest) {
        this.bookRepository = bookRepository;
        this.bookMapper = bookMapper;
        this.addBookRequest = addBookRequest;
    }

    @Override
    public String execute() {
        book = bookMapper.convertAddBookRequestToBook(addBookRequest);
        bookId = bookRepository.save(book);

        log.info("✓ COMMAND EXECUTED: Book '{}' added with ID: {}",
                book.getTitle(), bookId);

        return "Book added with Id: " + bookId;
    }

    @Override
    public String undo() {
        if (bookId == null) {
            log.warn("✗ UNDO FAILED: Command was never executed");
            return "Cannot undo: Command was never executed";
        }

        bookRepository.findBookById(bookId).ifPresentOrElse(
                foundBook -> {
                    foundBook.setCopiesAvailable(0);
                    foundBook.markAsLost();
                    bookRepository.save(foundBook);

                    log.info("↶ COMMAND UNDONE: Book '{}' (ID: {}) marked as unavailable",
                            foundBook.getTitle(), bookId);
                },
                () -> log.warn("✗ UNDO FAILED: Book not found with ID: {}", bookId)
        );

        return "Book addition undone: ID " + bookId + " marked as unavailable";
    }

    @Override
    public String getDescription() {
        if (book != null) {
            return String.format("Add Book: '%s' by %s (ISBN: %s)",
                    book.getTitle(),
                    book.getAuthor(),
                    book.getIsbn());
        }
        return String.format("Add Book: '%s' by %s",
                addBookRequest.getTitle(),
                addBookRequest.getAuthor());
    }
}