package com.example.boilerplate.dto.elasticSearch;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UploadModel {

    private String title;

    private String keywords;

    private String genres;

    private String writer;

    private MultipartFile[] files;
}
