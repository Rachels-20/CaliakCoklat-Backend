package com.pbl.weeklyreport.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WeeklyReportResponse {

    private String weekStart;
    private String weekEnd;

    private SensorWeeklyReport suhuTanah;
    private SensorWeeklyReport suhuUdara;
    private SensorWeeklyReport kelembapanTanah;
    private SensorWeeklyReport kelembapanUdara;
    private SensorWeeklyReport phTanah;
}