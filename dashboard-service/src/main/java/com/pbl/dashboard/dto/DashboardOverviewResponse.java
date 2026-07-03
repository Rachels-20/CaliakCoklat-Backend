package com.pbl.dashboard.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DashboardOverviewResponse {
    private Double suhuTanah;
    private String suhuTanahStatus;
    private String suhuTanahMessage;

    private Double suhuUdara;
    private String suhuUdaraStatus;
    private String suhuUdaraMessage;

    private Double phTanah;
    private String phTanahStatus;
    private String phTanahMessage;

    private Double kelembapanTanah;
    private String kelembapanTanahStatus;
    private String kelembapanTanahMessage;

    private Double kelembapanUdara;
    private String kelembapanUdaraStatus;
    private String kelembapanUdaraMessage;

}