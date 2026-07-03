package com.pbl.weeklyreport.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class SensorResponse {
    private Long id;
    private Long deviceId;
    private Double nilai;
    private String satuan;
    private LocalDateTime createdAt;
}