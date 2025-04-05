package com.taskly.apiTaskly.service;

import com.taskly.apiTaskly.exception.InvalidRequestException;
import com.taskly.apiTaskly.model.User;
import com.taskly.apiTaskly.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void register(User user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new InvalidRequestException("Username already taken!");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
    }
}
