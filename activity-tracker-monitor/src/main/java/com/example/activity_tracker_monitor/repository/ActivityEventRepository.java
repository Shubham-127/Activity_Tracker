package com.example.activity_tracker_monitor.repository;




import com.example.activity_tracker_monitor.model.ActivityEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Set;

public interface ActivityEventRepository extends JpaRepository<ActivityEvent, Long> {

    @Query("SELECT e.eventId FROM ActivityEvent e WHERE e.eventId IN :eventIds")
    Set<String> findExistingEventIds(Collection<String> eventIds);
    List<ActivityEvent> findByStartedAtBetween(Instant from, Instant to);
}
