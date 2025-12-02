package com.teamoffour.lms.controller;


import com.teamoffour.lms.domain.Book;
import com.teamoffour.lms.mapper.BookMapper;
import com.teamoffour.lms.repository.BookRepository;
import com.teamoffour.lms.service.dto.AddBookRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BookController {
    private final BookMapper bookMapper;
    private final BookRepository bookRepository;

    @Autowired
    public BookController(BookMapper bookMapper, BookRepository bookRepository) {
        this.bookMapper = bookMapper;
        this.bookRepository = bookRepository;
    }


    @PostMapping(value = "/lms/addBook")
    public String addBook(@RequestBody AddBookRequest addBookRequest) {
        Book book = bookMapper.convertAddBookRequestToBook(addBookRequest);
        return "Book added with Id : " + bookRepository.save(book);
    }

}
