package com.example.activity_tracker_monitor.serviceImpl;

import com.example.activity_tracker_monitor.model.ActivityEvent;
import com.example.activity_tracker_monitor.model.ActivitySummary;
import com.example.activity_tracker_monitor.repository.ActivityEventRepository;
import com.example.activity_tracker_monitor.repository.ActivitySummaryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class ActivityRollUpJobImple {

    private static final String IDLE_BUCKET = "Idle";

    private final ActivityEventRepository eventRepository;
    private final ActivitySummaryRepository summaryRepository;

    @Scheduled(cron = "0 */5 * * * *")
    @Transactional
    public void rollup() {
        Instant windowStart = Instant.now().minus(Duration.ofMinutes(10));
        Instant windowEnd = Instant.now();

        List<ActivityEvent> events = eventRepository.findByStartedAtBetween(windowStart, windowEnd);
        if (events.isEmpty()) {
            return;
        }

        Map<String, List<ActivityEvent>> grouped = events.stream()
                .collect(Collectors.groupingBy(this::groupKey));

        for (Map.Entry<String, List<ActivityEvent>> entry : grouped.entrySet()) {
            String[] parts = entry.getKey().split("\\|", 3);
            Long employeeId = Long.parseLong(parts[0]);
            LocalDate date = LocalDate.parse(parts[1]);
            String appName = parts[2];

            long seconds = entry.getValue().stream()
                    .mapToLong(this::durationSeconds).sum();

            boolean isIdleBucket = IDLE_BUCKET.equals(appName);
            upsertSummary(employeeId, date, appName,
                    isIdleBucket ? 0 : seconds,
                    isIdleBucket ? seconds : 0);
        }

        log.info("Rollup: processed {} events into {} summary buckets", events.size(), grouped.size());
    }

    private String groupKey(ActivityEvent e) {
        LocalDate date = e.getStartedAt().atZone(ZoneOffset.UTC).toLocalDate();
        String bucket = e.isIdle() ? IDLE_BUCKET : e.getAppName();
        return e.getEmployeeId() + "|" + date + "|" + bucket;
    }

    private long durationSeconds(ActivityEvent e) {
        return Duration.between(e.getStartedAt(), e.getEndedAt()).getSeconds();
    }

    private void upsertSummary(Long employeeId, LocalDate date, String appName, long activeToAdd, long idleToAdd) {
        ActivitySummary summary = summaryRepository.findByEmployeeIdAndDateAndAppName(employeeId, date, appName)
                .orElseGet(() -> {
                    ActivitySummary s = new ActivitySummary();
                    s.setEmployeeId(employeeId);
                    s.setDate(date);
                    s.setAppName(appName);
                    s.setTotalActiveSeconds(0);
                    s.setTotalIdleSeconds(0);
                    return s;
                });

        summary.setTotalActiveSeconds(summary.getTotalActiveSeconds() + activeToAdd);
        summary.setTotalIdleSeconds(summary.getTotalIdleSeconds() + idleToAdd);
        summaryRepository.save(summary);
    }
}