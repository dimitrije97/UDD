package com.example.boilerplate.dto.elasticSearch;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public final class RequiredHighlight {

    private String fieldName;

    private String value;
}
