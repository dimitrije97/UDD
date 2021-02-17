package com.example.boilerplate.service;

import com.example.boilerplate.dto.response.UserResponse;
import com.example.boilerplate.model.CreateUserModel;
import com.example.boilerplate.model.UserModel;
import java.util.List;
import java.util.UUID;

public interface IUserService {

    UserModel createUser(CreateUserModel createUserModel);

    UserResponse getUserByEmail(String email);

    UserResponse getUserById(UUID id);

    //TODO: Think about HashSet instead of List
    List<UserResponse> getAllUsers();
}
