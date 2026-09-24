package com.example.activity_tracker_monitor.serviceImpl;

import com.example.activity_tracker_monitor.dto.DeviceRegisterRequest;
import com.example.activity_tracker_monitor.dto.DeviceRegisterResponse;
import com.example.activity_tracker_monitor.model.Device;
import com.example.activity_tracker_monitor.model.InstallToken;
import com.example.activity_tracker_monitor.repository.DeviceRepository;
import com.example.activity_tracker_monitor.repository.InstallTokenRepository;
import com.example.activity_tracker_monitor.security.JwtUtil;
import com.example.activity_tracker_monitor.service.DeviceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class DeviceServiceImpl implements DeviceService {
    public final DeviceRepository deviceRepository;
    public final InstallTokenRepository installTokenRepository;
    public final JwtUtil jwtUtil;



    @Transactional
    public DeviceRegisterResponse register(DeviceRegisterRequest request) {
        InstallToken installToken = installTokenRepository
                .findByTokenAndUsedFalse(request.getInstallToken())
                .orElseThrow(() -> new IllegalStateException("Invalid or already-used install token"));

        if (installToken.getExpiresAt().isBefore(   Instant.now())) {
            throw new IllegalStateException("Install token expired");
        }

        deviceRepository.findByDeviceHash(request.getDeviceHash())
                .ifPresent(d -> { throw new IllegalStateException("Device already registered"); });

        Device device = Device.builder()
                .employeeId(installToken.getEmployeeId())
        .deviceHash(request.getDeviceHash())
        .os(request.getOs())
        .registeredAt(Instant.now())
                .active(true)
                .build();
        Device saved = deviceRepository.save(device);
        installToken.setUsed(true);
        installTokenRepository.save(installToken);

        String jwt = jwtUtil.generateToken(device.getId(), device.getEmployeeId());

        return new DeviceRegisterResponse(device.getId(), jwt);
    }
}
