package com.pbl.suhutanah.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.pbl.suhutanah.dto.SuhuTanahRequest;
import com.pbl.suhutanah.dto.SuhuTanahResponse;
import com.pbl.suhutanah.service.SuhuTanahService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/suhu-tanah")
@RequiredArgsConstructor
public class SuhuTanahController {

    private final SuhuTanahService service;

    @PostMapping
    public SuhuTanahResponse saveData(
            @Valid @RequestBody SuhuTanahRequest request) {
        return service.saveData(request);
    }

    @GetMapping("/latest")
    public SuhuTanahResponse getLatest(
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