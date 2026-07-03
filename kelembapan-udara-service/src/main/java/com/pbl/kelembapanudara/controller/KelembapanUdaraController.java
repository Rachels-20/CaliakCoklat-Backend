package com.pbl.kelembapanudara.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import com.pbl.kelembapanudara.dto.KelembapanUdaraRequest;
import com.pbl.kelembapanudara.dto.KelembapanUdaraResponse;
import com.pbl.kelembapanudara.service.KelembapanUdaraService;

@RestController
@RequestMapping("/kelembapan-udara")
@RequiredArgsConstructor
public class KelembapanUdaraController {
    private final KelembapanUdaraService service;

    /**
     * Simpan data baru.
     */
    @PostMapping
    public KelembapanUdaraResponse saveData(
            @Valid @RequestBody KelembapanUdaraRequest request) {

        return service.saveData(request);
    }

    /**
     * Ambil data terbaru.
     * Example:
     * GET /kelembapan-Udara/latest?deviceId=1
     */
    @GetMapping("/latest")
    public KelembapanUdaraResponse getLatest(
            @RequestParam Long deviceId) {

        return service.getLatest(deviceId);
    }

    /**
     * Ambil rata-rata data mingguan.
     * Example:
     * GET /kelembapan-Udara/weekly?deviceId=1&date=2026-05-14
     */
    @GetMapping("/weekly")
    public List<Double> getWeekly(
            @RequestParam("deviceId") Long deviceId,
            @RequestParam(required = false) String date) {

        if (date == null || date.isBlank()) {
            date = LocalDate.now().toString();
        }

        return service.getWeekly(deviceId, date);
    }
}
