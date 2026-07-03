package com.pbl.notification.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeviceOfflineEvent {

    private Long deviceId;

    private Long userId;

    private String kodePerangkat;

    private String nama;

    private String lokasi;
}