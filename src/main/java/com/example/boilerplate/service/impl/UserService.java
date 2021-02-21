package com.example.boilerplate.service.impl;

import com.example.boilerplate.dto.response.UserResponse;
import com.example.boilerplate.entity.User;
import com.example.boilerplate.exception.ResourceNotFoundException;
import com.example.boilerplate.model.CreateUserModel;
import com.example.boilerplate.model.UserModel;
import com.example.boilerplate.repository.IUserRepository;
import com.example.boilerplate.service.IUserService;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService implements IUserService {

    private final IUserRepository userRepository;

    private final ModelMapper modelMapper;

    private final PasswordEncoder passwordEncoder;

    public UserService(IUserRepository userRepository, ModelMapper modelMapper,
        PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserModel createUser(CreateUserModel createUserModel) {
        var user = modelMapper.map(createUserModel, User.class);
        user.setPassword(passwordEncoder.encode(createUserModel.getPassword()));
        var createdUser = userRepository.save(user);

        var userModel = modelMapper.map(createdUser, UserModel.class);
        return userModel;
    }

    @Override
    public List<UserResponse> getAllUsers() {
        var allUsers = userRepository.findAll();

        return allUsers.stream()
            .map(user -> modelMapper.map(user, UserResponse.class))
            .collect(Collectors.toList());
    }

    @Override
    public UserResponse getUserByEmail(String email) {
        var user = userRepository.findOneByEmail(email)
            .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));

        return modelMapper.map(user, UserResponse.class);
    }

    @Override
    public UserResponse getUserById(UUID id) {
        var user = userRepository.findOneById(id)
            .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));

        return modelMapper.map(user, UserResponse.class);
    }
}
