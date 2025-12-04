package com.teamoffour.lms.mapper;


import com.teamoffour.lms.domain.Book;
import com.teamoffour.lms.service.dto.AddBookRequest;
import org.springframework.stereotype.Service;

@Service
public class BookMapper {

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
