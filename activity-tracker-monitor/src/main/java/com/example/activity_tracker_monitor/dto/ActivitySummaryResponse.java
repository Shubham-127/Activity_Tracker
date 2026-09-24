package com.example.activity_tracker_monitor.dto;


import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class ActivitySummaryResponse {
    private LocalDate date;
    private long totalActiveSeconds;
    private long totalIdleSeconds;
    private String appName;
}
