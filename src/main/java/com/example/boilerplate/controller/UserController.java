package com.example.boilerplate.controller;

import com.example.boilerplate.dto.response.UserResponse;
import com.example.boilerplate.service.IUserService;
import java.util.List;
import java.util.UUID;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final IUserService userService;

    public UserController(IUserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<UserResponse> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/search")
    public UserResponse getUserByEmail(@RequestParam String email) {
        return userService.getUserByEmail(email);
    }

    @GetMapping("/{id}")
    public UserResponse getUserById(@PathVariable UUID id) {
        return userService.getUserById(id);
    }
}
