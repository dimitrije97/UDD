package com.example.boilerplate.util.error;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiFieldErrorResponse {

    private String fieldName;

    private String fieldErrorMessage;
}
