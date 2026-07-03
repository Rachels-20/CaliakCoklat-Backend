package com.pbl.device.dto;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeviceResponse {

    private Long id;

    private Long userId;

    private String phoneNumber;

    private String kodePerangkat;

    private String nama;

    private String lokasi;

    private Long intervalPengiriman;

    private LocalDateTime lastSeen;

    private Boolean aktif = true;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}