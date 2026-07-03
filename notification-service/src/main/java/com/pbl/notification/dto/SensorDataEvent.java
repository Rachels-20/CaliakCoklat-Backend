package com.pbl.notification.dto;

import lombok.Data;

@Data
public class SensorDataEvent {
    private Long deviceId;
    private String sensorType;
    private Double value;
}