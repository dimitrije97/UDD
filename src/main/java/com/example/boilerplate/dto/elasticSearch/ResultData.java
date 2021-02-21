package com.example.boilerplate.dto.elasticSearch;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public final class ResultData {

    private String title;

    private String keywords;

    private String writer;

    private String genres;

    private String location;

    private String highlight;
}
