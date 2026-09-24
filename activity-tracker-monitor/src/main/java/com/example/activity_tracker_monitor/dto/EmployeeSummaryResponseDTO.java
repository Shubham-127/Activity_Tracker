package com.example.activity_tracker_monitor.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EmployeeSummaryResponseDTO {
    private Long id;
    private String name;
    private String role;
}