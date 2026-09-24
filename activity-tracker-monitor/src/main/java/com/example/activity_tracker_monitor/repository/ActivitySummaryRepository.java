package com.example.activity_tracker_monitor.repository;

import com.example.activity_tracker_monitor.model.ActivitySummary;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ActivitySummaryRepository extends JpaRepository<ActivitySummary, Long> {
    List<ActivitySummary> findByEmployeeIdAndDateBetween(Long employeeId, LocalDate from, LocalDate to);
    Optional<ActivitySummary> findByEmployeeIdAndDateAndAppName(Long employeeId, LocalDate date, String appName);
}
