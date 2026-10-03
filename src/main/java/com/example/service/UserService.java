package com.example.service;

import com.example.model.Role;
import com.example.model.User;
import com.example.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import jakarta.annotation.PostConstruct;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostConstruct
    public void initUsers() {
        // Create default users if they don't exist
        createUserIfNotExists("manager", "manager123", Role.MANAGER);
        createUserIfNotExists("reception", "reception123", Role.RECEPTIONIST);
        createUserIfNotExists("housekeeping", "housekeeping123", Role.HOUSEKEEPING);
        createUserIfNotExists("guest", "guest123", Role.GUEST);
    }

    private void createUserIfNotExists(String username, String rawPassword, Role role) {
        if (userRepository.findByUsername(username).isEmpty()) {
            User user = new User();
            user.setUsername(username);
            user.setPassword(passwordEncoder.encode(rawPassword));
            user.setRole(role);
            userRepository.save(user);
        }
    }
}