package com.example.activity_tracker_monitor.controller;

import com.example.activity_tracker_monitor.dto.ActivitySummaryResponse;
import com.example.activity_tracker_monitor.dto.EmployeeSummaryResponseDTO;
import com.example.activity_tracker_monitor.model.Employee;
import com.example.activity_tracker_monitor.repository.EmployeeRepository;
import com.example.activity_tracker_monitor.security.AuthUtil;
import com.example.activity_tracker_monitor.serviceImpl.ActivitySummaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/admin/activity")
@RequiredArgsConstructor
public class AdminActivityController {

    private final ActivitySummaryService summaryService;
    private final EmployeeRepository employeeRepository;

    @GetMapping("/employee/{id}")
    @PreAuthorize("hasRole('ADMIN') or @teamGuard.isManagerOf(authentication, #id)")
    public ResponseEntity<List<ActivitySummaryResponse>> getEmployeeActivity(
            @PathVariable Long id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return ResponseEntity.ok(summaryService.getSummary(id, from, to));
    }

    @GetMapping("/team")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<List<ActivitySummaryResponse>> getTeamActivity(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            Authentication auth) {
        Long managerId = AuthUtil.currentEmployeeId(auth);
        if (managerId == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(summaryService.getTeamSummary(managerId, from, to));
    }

    @GetMapping("/employees")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<List<EmployeeSummaryResponseDTO>> getEmployees(Authentication auth) {
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        List<Employee> employees;
        if (isAdmin) {
            employees = employeeRepository.findAll();
        } else {
            Long managerId = AuthUtil.currentEmployeeId(auth);
            employees = employeeRepository.findByManagerId(managerId);
        }

        List<EmployeeSummaryResponseDTO> response = employees.stream()
                .map(e -> new EmployeeSummaryResponseDTO(e.getId(), e.getName(), e.getRole()))
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }
}
