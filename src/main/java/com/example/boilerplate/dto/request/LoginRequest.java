package com.example.boilerplate.dto.request;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import lombok.Data;

@Data
public class LoginRequest {

    @NotEmpty
    private String email;

    //TODO: Check if notEmpty annotation is necessary in this case (min = 3)
    @Size(min = 3, max = 50)
    private String password;
}
