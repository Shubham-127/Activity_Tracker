package com.example.activity_tracker_monitor.repository;

import com.example.activity_tracker_monitor.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    List<Employee> findByManagerId(Long managerId);
    boolean existsByIdAndManagerId(Long employeeId, Long managerId);
    Optional<Employee> findByEmail(String email);
}
