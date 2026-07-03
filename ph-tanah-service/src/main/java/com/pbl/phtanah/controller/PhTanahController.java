package com.pbl.phtanah.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.pbl.phtanah.dto.PhTanahRequest;
import com.pbl.phtanah.dto.PhTanahResponse;
import com.pbl.phtanah.service.PhTanahService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/ph-tanah")
@RequiredArgsConstructor
public class PhTanahController {

    private final PhTanahService service;

    @PostMapping
    public PhTanahResponse saveData(
            @Valid @RequestBody PhTanahRequest request) {
        return service.saveData(request);
    }

    @GetMapping("/latest")
    public PhTanahResponse getLatest(
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