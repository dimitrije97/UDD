package com.example.boilerplate.service;

import com.example.boilerplate.dto.plagiator.PaperDTO;
import com.example.boilerplate.dto.plagiator.ReturnDTO;
import java.io.IOException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

public interface IBookService {

    void createBook(MultipartFile files, String title, String keyWords, String writerId, String genresIds)
        throws IOException;

    ResponseEntity<ReturnDTO> checkPlagiarism(PaperDTO paperDTO) throws IOException;
}
