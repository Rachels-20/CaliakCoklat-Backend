package com.pbl.device.dto;

import lombok.Data;

@Data
public class DeviceRequest {

    private String kodePerangkat;

    private String kodeAktivasi;

    private String nama;

    private String lokasi;

    private Long intervalPengiriman;
}