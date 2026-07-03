package com.pbl.suhuudara.dto;

import lombok.Data;

@Data
public class SensorStatusEvent {

    private Long deviceId;

    private String sensorType;

    private boolean sensorOK;

}
