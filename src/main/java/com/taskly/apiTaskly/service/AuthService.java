package com.taskly.apiTaskly.service;

import com.taskly.apiTaskly.exception.UserAlreadyExists;
import com.taskly.apiTaskly.model.User;
import com.taskly.apiTaskly.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User register(User user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new UserAlreadyExists("User Already Exists!");
        }

        return userRepository.save(user);
    }
}
