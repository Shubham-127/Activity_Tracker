package com.example.activity_tracker_monitor.service;




import com.example.activity_tracker_monitor.dto.ActivityEventDto;
import java.util.List;

public interface ActivityIngestService {
    int ingestBatch(List<ActivityEventDto> events, Long deviceId);
}