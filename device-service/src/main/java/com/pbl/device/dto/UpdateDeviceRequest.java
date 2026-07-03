package com.pbl.device.dto;

import lombok.Data;

@Data
public class UpdateDeviceRequest {

    private String nama;
    private String lokasi;
    private Long intervalPengiriman;
}