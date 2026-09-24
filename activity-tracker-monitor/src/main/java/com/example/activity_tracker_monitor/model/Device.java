package com.example.activity_tracker_monitor.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "device")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Device     {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long employeeId;

    @Column(nullable = false, unique = true)
    private String deviceHash;

    @Column(nullable = false)
    private Instant registeredAt;

    @Column(nullable = false)
    private boolean active = true;

    private String os;


}
