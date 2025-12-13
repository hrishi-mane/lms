package com.teamoffour.lms.service.command;

import com.teamoffour.lms.domain.Book;
import com.teamoffour.lms.repository.BookRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RemoveBookCommand implements ICommand {
    private static final Logger log = LoggerFactory.getLogger(RemoveBookCommand.class);

    private final BookRepository bookRepository;
    private final Long bookId;
    private Book removedBook;
    private Integer originalCopiesAvailable;

    public RemoveBookCommand(BookRepository bookRepository, Long bookId) {
        this.bookRepository = bookRepository;
        this.bookId = bookId;
    }

    @Override
    public String execute() {
        Book book = bookRepository.findBookById(bookId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Book not found with ID: " + bookId));

        this.removedBook = book;
        this.originalCopiesAvailable = book.getCopiesAvailable();

        book.markAsLost();
        book.setCopiesAvailable(0);
        bookRepository.save(book);

        log.info("✓ COMMAND EXECUTED: Book '{}' (ID: {}) marked as unavailable",
                book.getTitle(), bookId);

        return "Book marked as unavailable: " + book.getTitle();
    }

    @Override
    public String undo() {
        if (removedBook == null) {
            log.warn("✗ UNDO FAILED: Command was never executed");
            return "Cannot undo: Command was never executed";
        }

        removedBook.markAsFound();
        removedBook.setCopiesAvailable(originalCopiesAvailable);
        bookRepository.save(removedBook);

        log.info("↶ COMMAND UNDONE: Book '{}' (ID: {}) restored with {} copies",
                removedBook.getTitle(), bookId, originalCopiesAvailable);

        return String.format("Book '%s' restored with %d copies available",
                removedBook.getTitle(),
                originalCopiesAvailable);
    }

    @Override
    public String getDescription() {
        String title = (removedBook != null) ? removedBook.getTitle() : "Unknown";
        return String.format("Remove Book: ID %d (%s)", bookId, title);
    }
}