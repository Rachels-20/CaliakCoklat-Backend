package com.pbl.dashboard.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DashboardResponse {

    private Long deviceId;
    private String nama;
    private String lokasi;
    private String status;

    private Double suhuTanah;
    private Double suhuUdara;
    private Double phTanah;
    private Double kelembapanTanah;
    private Double kelembapanUdara;
}