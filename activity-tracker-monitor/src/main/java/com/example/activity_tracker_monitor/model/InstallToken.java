package com.example.activity_tracker_monitor.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table(name = "install_tokens")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InstallToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String token;

    @Column(nullable = false)
    private Long employeeId;

    @Column(nullable = false)
    private boolean used = false;

    @Column(nullable = false)
    private Instant expiresAt;}
