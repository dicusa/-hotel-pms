package com.example.service;

import com.example.model.Role;
import com.example.model.User;
import com.example.repository.UserRepository;
import com.example.security.JwtUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class AuthServiceTest {
    private static EntityManagerFactory emf;
    private static EntityManager em;
    private static AuthService authService;
    private static UserRepository userRepo;

    @BeforeAll
    public static void setUp() {
        emf = Persistence.createEntityManagerFactory("hotelPU");
        em = emf.createEntityManager();
        authService = new AuthService(em);
        userRepo = new UserRepository(em);
        // create a test user
        User admin = new User("admin", "adminpass", Role.ADMIN);
        userRepo.save(admin);
    }

    @AfterAll
    public static void tearDown() {
        if (em != null) em.close();
        if (emf != null) emf.close();
    }

    @Test
    public void testSuccessfulLoginReturnsJwt() {
        Optional<String> tokenOpt = authService.login("admin", "adminpass");
        assertTrue(tokenOpt.isPresent(), "Token should be present for valid credentials");
        String token = tokenOpt.get();
        assertNotNull(JwtUtil.getUsername(token), "Username should be extractable from token");
        assertEquals("admin", JwtUtil.getUsername(token));
        assertEquals(Role.ADMIN, JwtUtil.getRole(token));
    }

    @Test
    public void testLoginFailsWithWrongPassword() {
        Optional<String> tokenOpt = authService.login("admin", "wrongpass");
        assertTrue(tokenOpt.isEmpty(), "Token should be empty for invalid credentials");
    }

    @Test
    public void testRoleCheckFilter() {
        String token = authService.login("admin", "adminpass").orElseThrow();
        assertTrue(com.example.security.RoleCheckFilter.hasRole(token, Role.ADMIN));
        assertFalse(com.example.security.RoleCheckFilter.hasRole(token, Role.STAFF));
    }
}
