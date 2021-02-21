package com.example.boilerplate.dto.elasticSearch;

import lombok.Data;

@Data
public class SimpleQuery {

    private String field;

    private String value;
}
