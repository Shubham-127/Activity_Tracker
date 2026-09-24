package com.example.activity_tracker_monitor.controller;

import com.example.activity_tracker_monitor.dto.DeviceRegisterRequest;
import com.example.activity_tracker_monitor.dto.DeviceRegisterResponse;
import com.example.activity_tracker_monitor.service.DeviceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/devices")
@RequiredArgsConstructor
public class DeviceRegistrationController {
    public final DeviceService deviceService;

@PostMapping("/register")
public ResponseEntity<DeviceRegisterResponse> register(
        @Valid @RequestBody DeviceRegisterRequest request) {
    return ResponseEntity.ok(deviceService.register(request));
}
}
