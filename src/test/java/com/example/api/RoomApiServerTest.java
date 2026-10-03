package com.example.api;

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

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class RoomApiServerTest {
    private static EntityManagerFactory emf;
    private static EntityManager em;
    private static UserRepository userRepo;
    private static RoomApiServer server;
    private static String adminToken;
    private static String staffToken;

    @BeforeAll
    static void setUp() throws Exception {
        emf = Persistence.createEntityManagerFactory("hotelPU");
        em = emf.createEntityManager();
        userRepo = new UserRepository(em);
        // create admin and staff users
        User admin = new User("admin", "adminpass", Role.ADMIN);
        User staff = new User("staff", "staffpass", Role.STAFF);
        userRepo.save(admin);
        userRepo.save(staff);
        adminToken = com.example.security.JwtUtil.generateToken("admin", Role.ADMIN);
        staffToken = com.example.security.JwtUtil.generateToken("staff", Role.STAFF);
        server = new RoomApiServer();
        server.start();
    }

    @AfterAll
    static void tearDown() {
        if (server != null) server.stop();
        if (em != null) em.close();
        if (emf != null) emf.close();
    }

    private HttpURLConnection createConnection(String method, String path, String token) throws Exception {
        URL url = new URL("http://localhost:8080" + path);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod(method);
        if (token != null) {
            conn.setRequestProperty("Authorization", "Bearer " + token);
        }
        conn.setDoOutput(true);
        return conn;
    }

    private String readResponse(HttpURLConnection conn) throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) sb.append(line);
        return sb.toString();
    }

    @Test
    void crudOperationsWithAdmin() throws Exception {
        // Create
        HttpURLConnection conn = createConnection("POST", "/rooms", adminToken);
        String payload = "{\"number\":\"101\",\"type\":\"Deluxe\",\"price\":120.5}";
        conn.getOutputStream().write(payload.getBytes(StandardCharsets.UTF_8));
        assertEquals(201, conn.getResponseCode());
        String resp = readResponse(conn);
        assertTrue(resp.contains("101"));
        conn.disconnect();

        // Get all
        conn = createConnection("GET", "/rooms", adminToken);
        assertEquals(200, conn.getResponseCode());
        resp = readResponse(conn);
        assertTrue(resp.contains("101"));
        conn.disconnect();

        // Get by id
        // parse id from response
        int id = Integer.parseInt(resp.split("\"id\":")[1].split("[\",]")[0]);
        conn = createConnection("GET", "/rooms/" + id, adminToken);
        assertEquals(200, conn.getResponseCode());
        resp = readResponse(conn);
        assertTrue(resp.contains("101"));
        conn.disconnect();

        // Update
        conn = createConnection("PUT", "/rooms/" + id, adminToken);
        payload = "{\"price\":150.0}";
        conn.getOutputStream().write(payload.getBytes(StandardCharsets.UTF_8));
        assertEquals(200, conn.getResponseCode());
        resp = readResponse(conn);
        assertTrue(resp.contains("150.0"));
        conn.disconnect();

        // Delete
        conn = createConnection("DELETE", "/rooms/" + id, adminToken);
        assertEquals(204, conn.getResponseCode());
        conn.disconnect();

        // Verify deletion
        conn = createConnection("GET", "/rooms/" + id, adminToken);
        assertEquals(404, conn.getResponseCode());
        conn.disconnect();
    }

    @Test
    void unauthorizedAccess() throws Exception {
        // No token
        HttpURLConnection conn = createConnection("GET", "/rooms", null);
        assertEquals(401, conn.getResponseCode());
        conn.disconnect();

        // Staff token for POST
        conn = createConnection("POST", "/rooms", staffToken);
        String payload = "{\"number\":\"102\",\"type\":\"Standard\",\"price\":80.0}";
        conn.getOutputStream().write(payload.getBytes(StandardCharsets.UTF_8));
        assertEquals(403, conn.getResponseCode());
        conn.disconnect();
    }
}
