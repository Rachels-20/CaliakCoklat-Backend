package com.pbl.device.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.pbl.device.dto.ClaimDeviceRequest;
import com.pbl.device.dto.DeviceRequest;
import com.pbl.device.dto.DeviceResponse;
import com.pbl.device.dto.IntervalResponse;
import com.pbl.device.dto.RegisterDeviceRequest;
import com.pbl.device.dto.UpdateDeviceRequest;
import com.pbl.device.service.DeviceService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/devices")
@RequiredArgsConstructor
public class DeviceController {

    private final DeviceService deviceService;

    /**
     * Menambahkan perangkat baru milik user yang sedang login.
     */
    @PostMapping
    public DeviceResponse createDevice(
            @RequestHeader("Authorization") String authHeader,
            @Valid @RequestBody DeviceRequest request) {

        return deviceService.createDevice(authHeader, request);
    }

    /**
     * Mengambil semua perangkat milik user yang sedang login.
     */
    @GetMapping("/me")
    public List<DeviceResponse> getMyDevices(
            @RequestHeader("Authorization") String authHeader) {

        return deviceService.getMyDevices(authHeader);
    }

    @GetMapping("/{id}")
    public DeviceResponse getById(@PathVariable Long id) {
        return deviceService.getById(id);
    }

    @PostMapping("/register")
    public DeviceResponse registerDevice(
            @RequestBody RegisterDeviceRequest request) {

        return deviceService.registerDevice(request);
    }

    @PostMapping("/claim")
    public DeviceResponse claimDevice(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody ClaimDeviceRequest request) {

        return deviceService.claimDevice(authHeader, request);
    }

    @DeleteMapping("/{id}/unclaim")
    public DeviceResponse unclaimDevice(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long id) {

        return deviceService.unclaimDevice(
                authHeader,
                id);
    }

    @GetMapping("/kode/{kodePerangkat}")
    public DeviceResponse getByKodePerangkat(
            @PathVariable String kodePerangkat) {

        return deviceService.getByKodePerangkat(
                kodePerangkat);
    }

    @PutMapping("/heartbeat/{kodePerangkat}")
    public void heartbeat(
            @PathVariable String kodePerangkat) {

        deviceService.heartbeat(kodePerangkat);
    }

    @GetMapping("/test")
    public String test() {
        return "OK";
    }

    @PutMapping("/{id}")
    public DeviceResponse updateDevice(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long id,
            @RequestBody UpdateDeviceRequest request) {

        return deviceService.updateDevice(
                authHeader,
                id,
                request);
    }

    @GetMapping("/kode/{kodePerangkat}/interval")
    public ResponseEntity<IntervalResponse> getIntervalPengiriman(
            @PathVariable String kodePerangkat) {

        return ResponseEntity.ok(
                deviceService.getIntervalPengiriman(kodePerangkat));
    }

    @PatchMapping("/kode/{kodePerangkat}/last-seen")
    public void updateLastSeen(
            @PathVariable String kodePerangkat) {

        deviceService.updateLastSeen(kodePerangkat);
    }
}