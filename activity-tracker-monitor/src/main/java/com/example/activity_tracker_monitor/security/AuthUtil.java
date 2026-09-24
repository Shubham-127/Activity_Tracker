package com.example.activity_tracker_monitor.security;

import io.jsonwebtoken.Claims;
import org.springframework.security.core.Authentication;

public class AuthUtil {
    public static Long currentDeviceId(Authentication auth) {
        return Long.parseLong((String) auth.getPrincipal());
    }

    public static Long currentEmployeeId(Authentication auth) {
        Claims claims = (Claims) auth.getDetails();
        if (claims == null) return null;
        return claims.get("employeeId", Long.class);
    }
}
