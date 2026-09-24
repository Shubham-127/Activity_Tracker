package com.example.activity_tracker_monitor.security;


import com.example.activity_tracker_monitor.repository.EmployeeRepository;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
@Component("teamGuard")
@RequiredArgsConstructor

public class TeamGuard {
    private final EmployeeRepository employeeRepository;

    public boolean isManagerOf(Authentication auth, Long targetEmployeeId) {
        Claims claims = (Claims) auth.getDetails();
        if (claims == null) return false;

        Long managerId = claims.get("employeeId", Long.class);
        if (managerId == null) return false;

        return employeeRepository.existsByIdAndManagerId(targetEmployeeId, managerId);
    }
}
