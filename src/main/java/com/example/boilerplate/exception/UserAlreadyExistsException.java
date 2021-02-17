package com.example.boilerplate.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class UserAlreadyExistsException extends RuntimeException {

    private static final String USER_ALREADY_EXIST = "User with email %s already exists";

    public UserAlreadyExistsException(String email) {
        super(String.format(USER_ALREADY_EXIST, email));
    }
}
