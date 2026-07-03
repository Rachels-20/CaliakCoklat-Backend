package com.pbl.notification.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeviceResponse {
    private Long id;
    private String nama;
    private Long userId;
    private String lokasi;
    private String phoneNumber;
    private String kodePerangkat;
}