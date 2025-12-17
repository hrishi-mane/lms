package com.teamoffour.lms.controller;

import com.teamoffour.lms.domain.Book;
import com.teamoffour.lms.mapper.BookMapper;
import com.teamoffour.lms.repository.BookRepository;
import com.teamoffour.lms.service.command.AddBookCommand;
import com.teamoffour.lms.service.command.CommandManager;
import com.teamoffour.lms.service.command.RemoveBookCommand;
import com.teamoffour.lms.service.dto.AddBookRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class BookController {
    private final BookMapper bookMapper;
    private final BookRepository bookRepository;
    private final CommandManager commandManager;

    @Autowired
    public BookController(BookMapper bookMapper,
                          BookRepository bookRepository,
                          CommandManager commandManager) {
        this.bookMapper = bookMapper;
        this.bookRepository = bookRepository;
        this.commandManager = commandManager;
    }

    @GetMapping(value="/lms/getAllBooks")
    public List<Book> getAllBooks() {
        return bookRepository.getAllBooks();
    }

    @PostMapping(value = "/lms/addBook")
    public String addBook(@RequestBody AddBookRequest addBookRequest) {
        AddBookCommand command = new AddBookCommand(
                bookRepository,
                bookMapper,
                addBookRequest
        );

        return commandManager.executeCommand(command);
    }

    @PostMapping(value = "/lms/removeBook/{bookId}")
    public String removeBook(@PathVariable Long bookId) {
        RemoveBookCommand command = new RemoveBookCommand(
                bookRepository,
                bookId
        );

        return commandManager.executeCommand(command);
    }

    @PostMapping(value = "/lms/undoLastBookOperation")
    public String undoLastBookOperation() {
        return commandManager.undoLastCommand();
    }

    @GetMapping(value = "/lms/commandHistory")
    public Map<String, Object> getCommandHistory() {
        Map<String, Object> response = new HashMap<>();

        if (!commandManager.canUndo()) {
            response.put("message", "No commands in history");
            response.put("historySize", 0);
            response.put("canUndo", false);
            return response;
        }

        response.put("historySize", commandManager.getHistorySize());
        response.put("canUndo", true);
        response.put("lastCommand", commandManager.getLastCommandDescription());
        response.put("allCommands", commandManager.getCommandHistory());

        return response;
    }

    @DeleteMapping(value = "/lms/commandHistory")
    public String clearCommandHistory() {
        commandManager.clearHistory();
        return "Command history cleared successfully";
    }
}