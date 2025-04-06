package com.taskly.apiTaskly.service;

import com.taskly.apiTaskly.dto.LoginRequest;
import com.taskly.apiTaskly.dto.LoginResponse;
import com.taskly.apiTaskly.exception.InvalidRequestException;
import com.taskly.apiTaskly.model.User;
import com.taskly.apiTaskly.model.UserPrincipal;
import com.taskly.apiTaskly.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager,
                       JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    public void register(User user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new InvalidRequestException("Username already taken!");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
    }

    public LoginResponse verifyUser(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        UserPrincipal user = (UserPrincipal) authentication.getPrincipal();
        String token = jwtService.generateToken(user.getUsername());
        return new LoginResponse(token);
    }
}
