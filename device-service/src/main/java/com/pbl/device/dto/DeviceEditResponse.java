package com.pbl.device.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeviceEditResponse {

    private Long id;

    private Long userId;

    private String kodePerangkat;

    private String nama;

    private String lokasi;

    private LocalDateTime updatedAt;
}