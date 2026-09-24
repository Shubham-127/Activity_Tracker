package com.example.activity_tracker_monitor.dto;




import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.Instant;

@Data
public class ActivityEventDto {

    @NotBlank
    private String eventId;

    @NotBlank
    private String appName;

    private String windowTitle;

    private String domain;

    @NotNull
    private Instant startedAt;

    @NotNull
    private Instant endedAt;

    private boolean idle;
}