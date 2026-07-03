package com.pbl.dashboard.dto;

import lombok.Data;

@Data
public class DeviceResponse {
    private Long id;
    private Long userId;
    private String kodePerangkat;
    private String nama;
    private String lokasi;
    private String status;
}