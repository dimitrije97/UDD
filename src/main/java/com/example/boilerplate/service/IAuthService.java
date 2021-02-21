package com.example.boilerplate.service;

import com.example.boilerplate.dto.request.LoginRequest;
import com.example.boilerplate.dto.request.RegistrationRequest;
import com.example.boilerplate.dto.response.LoginResponse;

public interface IAuthService {

    void registration(RegistrationRequest registrationRequest);

    LoginResponse login(LoginRequest loginRequest);
}
