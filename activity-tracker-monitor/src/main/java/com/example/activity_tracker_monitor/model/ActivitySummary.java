package com.example.activity_tracker_monitor.model;


import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Entity
@Table(
        name = "activity_summary",
        uniqueConstraints = @UniqueConstraint(columnNames = {"employeeId", "date","appName"})
)
@Data
public class ActivitySummary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long employeeId;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private String appName;

    @Column(nullable = false)
    private long totalActiveSeconds;

    @Column(nullable = false)
    private long totalIdleSeconds;
}
