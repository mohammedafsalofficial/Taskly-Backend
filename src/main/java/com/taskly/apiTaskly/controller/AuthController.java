package com.taskly.apiTaskly.controller;

import com.taskly.apiTaskly.dto.LoginRequest;
import com.taskly.apiTaskly.dto.LoginResponse;
import com.taskly.apiTaskly.model.User;
import com.taskly.apiTaskly.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody User user) {
        authService.register(user);
        return new ResponseEntity<>("User registered successfully!", HttpStatus.OK);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        LoginResponse response = authService.verifyUser(loginRequest);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
