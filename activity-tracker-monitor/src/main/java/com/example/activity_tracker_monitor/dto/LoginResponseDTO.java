package com.example.activity_tracker_monitor.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data@AllArgsConstructor
public class LoginResponseDTO {
    private String token;
    private Long employeeId;
    private String name;
    private String role;
}
