package com.example.activity_tracker_monitor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ActivityTrackerMonitorApplication {

	public static void main(String[] args) {
		SpringApplication.run(ActivityTrackerMonitorApplication.class, args);
	}

}
