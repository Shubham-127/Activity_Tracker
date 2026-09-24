package com.example.activity_tracker_monitor.serviceImpl;

import com.example.activity_tracker_monitor.dto.ActivitySummaryResponse;
import com.example.activity_tracker_monitor.model.ActivitySummary;
import com.example.activity_tracker_monitor.repository.ActivitySummaryRepository;
import com.example.activity_tracker_monitor.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ActivitySummaryService {
    private final ActivitySummaryRepository summaryRepository;
    private final EmployeeRepository employeeRepository;

    public List<ActivitySummaryResponse> getSummary(Long employeeId, LocalDate from, LocalDate to){
        List<ActivitySummary> rows = summaryRepository.findByEmployeeIdAndDateBetween(employeeId, from, to);

        return rows.stream()
                .map(r -> new ActivitySummaryResponse(r.getDate(), r.getTotalActiveSeconds(), r.getTotalIdleSeconds(), r.getAppName()))
                .collect(Collectors.toList());
    }
    public List<ActivitySummaryResponse> getTeamSummary(Long managerId, LocalDate from, LocalDate to) {
        List<Long> teamIds = employeeRepository.findByManagerId(managerId)
                .stream().map(e -> e.getId()).collect(Collectors.toList());

        return teamIds.stream()
                .flatMap(id -> getSummary(id, from, to).stream())
                .collect(Collectors.toList());
    }
}