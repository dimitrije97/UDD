package com.example.boilerplate.dto.request;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import lombok.Data;

@Data
public class RegistrationRequest {

    @NotEmpty
    @Email
    private String email;

    @Size(min = 3, max = 50)
    private String password;

//    @Match('password')
    private String rePassword;
}
