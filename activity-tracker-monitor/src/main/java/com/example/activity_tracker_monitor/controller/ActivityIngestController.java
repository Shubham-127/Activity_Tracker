package com.example.activity_tracker_monitor.controller;

//package com.yourcompany.activitytrackermonitor.controller;
//
//import com.yourcompany.activitytrackermonitor.dto.ActivityEventDto;
//import com.yourcompany.activitytrackermonitor.security.AuthUtil;
//import com.yourcompany.activitytrackermonitor.service.ActivityIngestService;
import com.example.activity_tracker_monitor.dto.ActivityEventDto;
import com.example.activity_tracker_monitor.security.AuthUtil;
import com.example.activity_tracker_monitor.service.ActivityIngestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/activity")
@RequiredArgsConstructor
public class ActivityIngestController {

    private final ActivityIngestService ingestService;

    @PostMapping("/batch")
    public ResponseEntity<Map<String, Object>> ingest(
            @Valid @RequestBody List<ActivityEventDto> events,
            Authentication auth) {

        Long deviceId = AuthUtil.currentDeviceId(auth);
        int saved = ingestService.ingestBatch(events, deviceId);

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(Map.of(
                "received", events.size(),
                "saved", saved,
                "duplicatesSkipped", events.size() - saved
        ));
    }
}