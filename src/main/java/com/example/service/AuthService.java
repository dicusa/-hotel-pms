package com.example.service;

import com.example.model.User;
import com.example.repository.UserRepository;
import com.example.security.JwtUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.util.Optional;

public class AuthService {
    private final UserRepository userRepository;

    public AuthService(EntityManager em) {
        this.userRepository = new UserRepository(em);
    }

    /**
     * Validates credentials and returns a JWT if successful.
     */
    public Optional<String> login(String username, String password) {
        Optional<User> opt = userRepository.findByUsername(username);
        if (opt.isPresent()) {
            User user = opt.get();
            if (user.getPassword().equals(password)) { // plain‑text compare for demo
                String token = JwtUtil.generateToken(user.getUsername(), user.getRole());
                return Optional.of(token);
            }
        }
        return Optional.empty();
    }
}
