package com.pbl.kelembapanudara.dto;

import lombok.Data;

@Data
public class SensorDataEvent {
    private Long deviceId;
    private String sensorType;
    private Double value;
}