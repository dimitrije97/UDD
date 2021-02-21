package com.example.boilerplate.dto.elasticSearch;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdvancedQuery {

    private String field1;

    private String value1;

    private String field2;

    private String value2;

    private String operation;
}
