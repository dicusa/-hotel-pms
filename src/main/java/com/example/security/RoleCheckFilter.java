package com.example.security;

import com.example.model.Role;

public class RoleCheckFilter {
    /**
     * Returns true if the JWT token contains at least the required role.
     * ADMIN > STAFF. For simplicity we treat equality only.
     */
    public static boolean hasRole(String token, Role required) {
        try {
            Role tokenRole = JwtUtil.getRole(token);
            return tokenRole == required;
        } catch (Exception e) {
            return false;
        }
    }
}
