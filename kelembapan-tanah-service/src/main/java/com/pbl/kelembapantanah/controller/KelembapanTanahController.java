package com.pbl.kelembapantanah.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.pbl.kelembapantanah.dto.KelembapanTanahRequest;
import com.pbl.kelembapantanah.dto.KelembapanTanahResponse;
import com.pbl.kelembapantanah.service.KelembapanTanahService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/kelembapan-tanah")
@RequiredArgsConstructor
public class KelembapanTanahController {

    private final KelembapanTanahService service;

    /**
     * Simpan data baru.
     */
    @PostMapping
    public KelembapanTanahResponse saveData(
            @Valid @RequestBody KelembapanTanahRequest request) {

        return service.saveData(request);
    }

    /**
     * Ambil data terbaru.
     * Example:
     * GET /kelembapan-tanah/latest?deviceId=1
     */
    @GetMapping("/latest")
    public KelembapanTanahResponse getLatest(
            @RequestParam Long deviceId) {

        return service.getLatest(deviceId);
    }

    /**
     * Ambil rata-rata data mingguan.
     * Example:
     * GET /kelembapan-tanah/weekly?deviceId=1&date=2026-05-14
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