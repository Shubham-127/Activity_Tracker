package com.example.activity_tracker_monitor.serviceImpl;


//
//import com.yourcompany.activitytrackermonitor.dto.ActivityEventDto;
//import com.yourcompany.activitytrackermonitor.entity.ActivityEvent;
//import com.yourcompany.activitytrackermonitor.entity.Device;
//import com.yourcompany.activitytrackermonitor.repository.ActivityEventRepository;
//import com.yourcompany.activitytrackermonitor.repository.DeviceRepository;
import com.example.activity_tracker_monitor.dto.ActivityEventDto;
import com.example.activity_tracker_monitor.model.ActivityEvent;
import com.example.activity_tracker_monitor.model.Device;
import com.example.activity_tracker_monitor.repository.ActivityEventRepository;
import com.example.activity_tracker_monitor.repository.DeviceRepository;
import com.example.activity_tracker_monitor.service.ActivityIngestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ActivityIngestServiceImpl implements ActivityIngestService {

    private final ActivityEventRepository eventRepository;
    private final DeviceRepository deviceRepository;

    @Override
    @Transactional
    public int ingestBatch(List<ActivityEventDto> events, Long deviceId) {

        Device device = deviceRepository.findById(deviceId)
                .orElseThrow(() -> new IllegalStateException("Unknown device: " + deviceId));

        if (!device.isActive()) {
            throw new IllegalStateException("Device is deactivated: " + deviceId);
        }

        List<String> incomingIds = events.stream()
                .map(ActivityEventDto::getEventId)
                .collect(Collectors.toList());

        Set<String> existingIds = eventRepository.findExistingEventIds(incomingIds);

        List<ActivityEvent> toSave = events.stream()
                .filter(dto -> !existingIds.contains(dto.getEventId()))
                .map(dto -> toEntity(dto, device))
                .collect(Collectors.toList());

        eventRepository.saveAll(toSave);

        log.info("Device {} (employee {}): ingested {} new, {} duplicates skipped",
                deviceId, device.getEmployeeId(), toSave.size(), events.size() - toSave.size());

        return toSave.size();
    }

    private ActivityEvent toEntity(ActivityEventDto dto, Device device) {
        ActivityEvent event = new ActivityEvent();
        event.setEventId(dto.getEventId());
        event.setEmployeeId(device.getEmployeeId());
        event.setDeviceId(device.getId());
        event.setAppName(dto.getAppName());
        event.setWindowTitle(dto.getWindowTitle());
        event.setDomain(dto.getDomain());
        event.setStartedAt(dto.getStartedAt());
        event.setEndedAt(dto.getEndedAt());
        event.setIdle(dto.isIdle());
        return event;
    }
}