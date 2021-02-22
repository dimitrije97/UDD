package com.example.boilerplate.service;

import java.io.IOException;
import org.springframework.web.multipart.MultipartFile;

public interface IBookService {

    void createBook(MultipartFile files, String title, String keyWords, String writerId, String genresIds)
        throws IOException;
}
