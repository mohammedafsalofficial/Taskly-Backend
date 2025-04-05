package com.taskly.apiTaskly.repository;

import com.taskly.apiTaskly.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByUsername(String username);
}
