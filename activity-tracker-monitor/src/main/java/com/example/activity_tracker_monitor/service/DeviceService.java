package com.example.activity_tracker_monitor.service;


import com.example.activity_tracker_monitor.dto.DeviceRegisterRequest;
import com.example.activity_tracker_monitor.dto.DeviceRegisterResponse;

public interface DeviceService {
    DeviceRegisterResponse register( DeviceRegisterRequest request);
}
