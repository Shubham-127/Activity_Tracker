package com.example.activity_tracker_monitor.model;




import jakarta.persistence.*;
import lombok.Data;
import java.time.Instant;

@Entity
@Table(
        name = "activity_event",
        uniqueConstraints = @UniqueConstraint(columnNames = "eventId")
)
@Data
public class ActivityEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String eventId;

    @Column(nullable = false)
    private Long employeeId;

    @Column(nullable = false)
    private Long deviceId;

    private String appName;

    @Column(length = 500)
    private String windowTitle;

    private String domain;

    @Column(nullable = false)
    private Instant startedAt;

    @Column(nullable = false)
    private Instant endedAt;

    @Column(nullable = false)
    private boolean idle = false;
}
