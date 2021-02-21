package com.example.boilerplate.dto.elasticSearch;

import lombok.Data;

@Data
public class SearchFilter {

    private String title;

    private String keywords;

    private String writer;

    private String genres;

    private String content;

    private String notTitle;

    private String notKeywords;

    private String notWriter;

    private String notGenres;

    private String notContent;
}
