package com.example.boilerplate.controller;

import com.example.boilerplate.dto.request.LoginRequest;
import com.example.boilerplate.dto.request.RegistrationRequest;
import com.example.boilerplate.dto.response.LoginResponse;
import com.example.boilerplate.service.IAuthService;
import javax.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final IAuthService authService;

    public AuthController(IAuthService authService) {
        this.authService = authService;
    }

    @ResponseStatus(code = HttpStatus.CREATED)
    @PostMapping("/registration")
    public void registration(@RequestBody @Valid RegistrationRequest registrationRequest) {
        authService.registration(registrationRequest);
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody @Valid LoginRequest loginRequest) {
        return authService.login(loginRequest);
    }
}
