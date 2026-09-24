package com.example.activity_tracker_monitor.controller;

import com.example.activity_tracker_monitor.dto.LoginRequestDTO;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.example.activity_tracker_monitor.dto.LoginResponseDTO;
import com.example.activity_tracker_monitor.model.Employee;
import com.example.activity_tracker_monitor.repository.EmployeeRepository;
import com.example.activity_tracker_monitor.security.JwtUtil;
import com.example.activity_tracker_monitor.security.PasswordConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    public final EmployeeRepository employeeRepository;
    public final PasswordEncoder passwordEncoder;
    public final JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO request){
        Employee employee = employeeRepository.findByEmail(request.getEmail())
                .orElse(null);

        if (employee == null || !passwordEncoder.matches(request.getPassword(), employee.getPassword())){
            return ResponseEntity.status(401).body("Invalid email or password");

        }

        String token = jwtUtil.generateTestToken(employee.getId(), employee.getRole());
        return ResponseEntity.ok(new LoginResponseDTO(token, employee.getId(), employee.getName(), employee.getRole()));
    }
}
