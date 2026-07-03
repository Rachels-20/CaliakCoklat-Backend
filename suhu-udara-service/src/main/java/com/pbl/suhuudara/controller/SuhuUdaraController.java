package com.pbl.suhuudara.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.pbl.suhuudara.dto.SuhuUdaraRequest;
import com.pbl.suhuudara.dto.SuhuUdaraResponse;
import com.pbl.suhuudara.service.SuhuUdaraService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/suhu-udara")
@RequiredArgsConstructor
public class SuhuUdaraController {

    private final SuhuUdaraService service;

    @PostMapping
    public SuhuUdaraResponse saveData(
            @Valid @RequestBody SuhuUdaraRequest request) {
        return service.saveData(request);
    }

    @GetMapping("/latest")
    public SuhuUdaraResponse getLatest(
            @RequestParam Long deviceId) {
        return service.getLatest(deviceId);
    }

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