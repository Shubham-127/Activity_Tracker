package com.example.activity_tracker_monitor.dto;


import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class DeviceRegisterRequest {

    @NotBlank
    private String deviceHash;
    @NotBlank
    private String os;
    @NotBlank
    private String installToken;
}
