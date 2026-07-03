package com.pbl.dashboard.dto;

import lombok.Data;

@Data
public class SensorResponse {
    private Long id;
    private Long deviceId;
    private Double nilai;
    private String satuan;
    private String waktu;
}