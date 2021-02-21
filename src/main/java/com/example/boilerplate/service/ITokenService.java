package com.example.boilerplate.service;

import com.example.boilerplate.model.UserModel;

public interface ITokenService {

    String generateAccessToken(UserModel user);
}
