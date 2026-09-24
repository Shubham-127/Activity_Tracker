package com.example.activity_tracker_monitor.controller;


import com.example.activity_tracker_monitor.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/test")
@RequiredArgsConstructor
public class TestTokenController {

    private final JwtUtil jwtUtil;

    @GetMapping("/admin-token")
    public String getAdminToken() {
        return jwtUtil.generateTestToken(999L, "ADMIN");
    }

    @GetMapping("/manager-token")
    public String getManagerToken(){
        return jwtUtil.generateTestToken(2L, "MANAGER");
    }
}
