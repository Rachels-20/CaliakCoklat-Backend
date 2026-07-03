package com.pbl.weeklyreport.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SensorWeeklyReport {
    private Double averageWeekly;
    private List<DailyAverageResponse> dailyAverages;
}