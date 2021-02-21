package com.example.boilerplate.controller;

import com.example.boilerplate.service.IBookService;
import java.io.IOException;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/books")
public class BookController {

    private final IBookService bookService;

    public BookController(IBookService bookService) {
        this.bookService = bookService;
    }

    @ResponseStatus(code = HttpStatus.CREATED)
    @PostMapping
    public void createBook(@RequestParam("files") MultipartFile files, @RequestParam("title") String title,
        @RequestParam("keyWords") String keyWords, @RequestParam("writerId") String writerId,
        @RequestParam("genresIds") String genresIds) throws IOException {
        bookService.createBook(files, title, keyWords, writerId, genresIds);
    }
}
