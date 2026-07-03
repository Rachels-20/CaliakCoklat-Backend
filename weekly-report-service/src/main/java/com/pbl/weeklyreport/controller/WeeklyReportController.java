package com.pbl.weeklyreport.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pbl.weeklyreport.dto.WeeklyReportResponse;
import com.pbl.weeklyreport.security.JwtUtil;
import com.pbl.weeklyreport.service.WeeklyReportService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/reports/weekly-report")
@RequiredArgsConstructor
public class WeeklyReportController {

    private final WeeklyReportService weeklyReportService;
    private final JwtUtil jwtUtil;

    @GetMapping
    public WeeklyReportResponse getWeeklyReport(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam(required = false) String date) {

        String token = authHeader.substring(7);
        Long userId = jwtUtil.extractUserId(token);

        return weeklyReportService.getWeeklyReport(
                authHeader,
                date);
    }
}