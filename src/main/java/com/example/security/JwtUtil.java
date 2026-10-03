package com.example.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.model.Role;

import java.util.Date;

public class JwtUtil {
    private static final String SECRET = "replace_this_with_a_secure_secret";
    private static final long EXPIRATION_MS = 3600_000; // 1 hour
    private static final Algorithm ALGORITHM = Algorithm.HMAC256(SECRET);

    public static String generateToken(String username, Role role) {
        return JWT.create()
                .withSubject(username)
                .withClaim("role", role.name())
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + EXPIRATION_MS))
                .sign(ALGORITHM);
    }

    public static DecodedJWT verifyToken(String token) {
        return JWT.require(ALGORITHM)
                .build()
                .verify(token);
    }

    public static String getUsername(String token) {
        return verifyToken(token).getSubject();
    }

    public static Role getRole(String token) {
        String roleStr = verifyToken(token).getClaim("role").asString();
        return Role.valueOf(roleStr);
    }
}
