package com.example.boilerplate.service.impl;

import com.example.boilerplate.dto.request.LoginRequest;
import com.example.boilerplate.dto.request.RegistrationRequest;
import com.example.boilerplate.dto.response.LoginResponse;
import com.example.boilerplate.dto.response.UserResponse;
import com.example.boilerplate.entity.User;
import com.example.boilerplate.exception.BadRequestException;
import com.example.boilerplate.exception.ResourceNotFoundException;
import com.example.boilerplate.exception.UserAlreadyExistsException;
import com.example.boilerplate.model.CreateUserModel;
import com.example.boilerplate.model.UserModel;
import com.example.boilerplate.repository.IUserRepository;
import com.example.boilerplate.service.IAuthService;
import com.example.boilerplate.service.ITokenService;
import com.example.boilerplate.service.IUserService;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService implements IAuthService {

    private final IUserRepository userRepository;

    private final ModelMapper modelMapper;

    private final IUserService userService;

    private final PasswordEncoder passwordEncoder;

    private final ITokenService tokenService;

    public AuthService(IUserRepository userRepository, ModelMapper modelMapper,
        IUserService userService, PasswordEncoder passwordEncoder,
        ITokenService tokenService) {
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.tokenService = tokenService;
    }

    @Override
    public void registration(RegistrationRequest registrationRequest) {
        var user = userRepository.findOneByEmail(registrationRequest.getEmail());
        if (user.isPresent()) {
            throw new UserAlreadyExistsException(registrationRequest.getEmail());
        }

        var createUserModel = modelMapper.map(registrationRequest, CreateUserModel.class);
        var userModel = userService.createUser(createUserModel);
        //TODO: use userModel to create all other user types
    }

    @Override
    public LoginResponse login(LoginRequest loginRequest) {
        var user = userRepository.findOneByEmail(loginRequest.getEmail())
            .orElseThrow(() -> new ResourceNotFoundException("User", "email", loginRequest.getEmail()));
        //TODO: think about throwing badRequestException ("bad credentials") here
        // if user active

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new BadRequestException("Bad credentials.");
        }

        return getLoginResponse(user);
    }

    private LoginResponse getLoginResponse(User user) {
        var userModel = modelMapper.map(user, UserModel.class);
        var jwt = tokenService.generateAccessToken(userModel);

        var userResponse = modelMapper.map(user, UserResponse.class);

        return LoginResponse.builder()
            .userResponse(userResponse)
            .accessToken(jwt)
            .build();
    }
}
