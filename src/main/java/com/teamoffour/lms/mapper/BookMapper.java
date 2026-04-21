package com.teamoffour.lms.mapper;


import com.teamoffour.lms.domain.Book;
import com.teamoffour.lms.service.dto.AddBookRequest;
import com.teamoffour.lms.service.dto.BookDTO;
import org.springframework.stereotype.Service;

@Service
public class BookMapper {

    public BookDTO toBookDTO(Book book) {
        BookDTO dto = new BookDTO();
        dto.setId(book.getId());
        dto.setIsbn(book.getIsbn());
        dto.setTitle(book.getTitle());
        dto.setAuthor(book.getAuthor());
        dto.setCategory(book.getCategory());
        dto.setPublicationYear(book.getPublicationYear());
        dto.setCopiesAvailable(book.getCopiesAvailable());
        dto.setAvailable(book.isAvailable());
        return dto;
    }

    public Book convertAddBookRequestToBook(AddBookRequest addBookRequest) {
        Book book = new Book();

        book.setTitle(addBookRequest.getTitle());
        book.setAuthor(addBookRequest.getAuthor());
        book.setCategory(addBookRequest.getCategory());
        book.setIsbn(addBookRequest.getIsbn());
        book.setPublicationYear(addBookRequest.getPublicationYear());
        book.setCopiesAvailable(addBookRequest.getCopiesAvailable());

        return book;
    }
}
