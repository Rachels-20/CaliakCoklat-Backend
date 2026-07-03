package com.pbl.dashboard.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pbl.dashboard.dto.DashboardOverviewResponse;
import com.pbl.dashboard.dto.DashboardResponse;
import com.pbl.dashboard.dto.RecommendationResponse;
import com.pbl.dashboard.security.JwtUtil;
import com.pbl.dashboard.service.DashboardService;
import com.pbl.dashboard.service.RecommendationService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;
    private final JwtUtil jwtUtil;
    private final RecommendationService recommendationService;

    @GetMapping("/devices")
    public List<DashboardResponse> getDashboard(
            @RequestHeader("Authorization") String authHeader) {

        // Menghapus prefix "Bearer " dari header Authorization
        String token = authHeader.substring(7);

        // Mengambil userId dari JWT
        Long userId = jwtUtil.extractUserId(token);

        // Mengambil dashboard hanya untuk device milik user tersebut
        return dashboardService.getDashboardByUserId(userId, authHeader);
    }

    @GetMapping("/overview")
    public DashboardOverviewResponse getOverview(
            @RequestHeader("Authorization") String authHeader) {

        String token = authHeader.substring(7);

        Long userId = jwtUtil.extractUserId(token);

        return dashboardService.getOverviewByUserId(
                userId,
                authHeader);
    }

    @GetMapping("/recommendation")
    public ResponseEntity<RecommendationResponse> getRecommendation(
            @RequestHeader("Authorization") String authHeader) {

        String token = authHeader.substring(7);

        Long userId = jwtUtil.extractUserId(token);

        DashboardOverviewResponse overview = dashboardService.getOverviewByUserId(
                userId,
                authHeader);

        RecommendationResponse response = recommendationService.generate(overview);

        return ResponseEntity.ok(response);
    }
}